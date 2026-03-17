package com.billpoint.backend.controller;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.billpoint.backend.model.Bill;
import com.billpoint.backend.model.Product;
import com.billpoint.backend.model.Shop;
import com.billpoint.backend.repository.BillRepository;
import com.billpoint.backend.repository.ProductRepository;
import com.billpoint.backend.repository.ShopRepository;
import com.billpoint.backend.security.UserDetailsImpl;
import com.billpoint.backend.dto.MessageResponse;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('SHOP_OWNER')")
public class ReportController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long getAuthShopId(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Shop shop = shopRepository.findByOwner_Id(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Shop not found"));
        return shop.getId();
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(Authentication authentication) {
        Long shopId = getAuthShopId(authentication);
        Iterable<Bill> bills = billRepository.findByShop_Id(shopId);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Bill bill : bills) {
            totalRevenue = totalRevenue.add(bill.getFinalAmount());
        }

        return ResponseEntity.ok(new MessageResponse("Total Revenue: " + totalRevenue.toString()));
    }

    @GetMapping("/sales")
    public ResponseEntity<List<Bill>> getSalesData(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        
        Long shopId = getAuthShopId(authentication);
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        List<Bill> bills = billRepository.findByShop_IdAndCreatedAtBetween(shopId, start, end);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<Product>> getInventoryReport(Authentication authentication) {
        Long shopId = getAuthShopId(authentication);
        List<Product> products = productRepository.findByShop_Id(shopId);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/download/sales", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadSalesDateReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        
        Long shopId = getAuthShopId(authentication);
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        List<Bill> bills = billRepository.findByShop_IdAndCreatedAtBetween(shopId, start, end);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Sales Report (" + startDate + " to " + endDate + ")").setFontSize(20).setBold());
            document.add(new Paragraph("Shop ID: " + shopId).setMarginBottom(10));

            Table table = new Table(new float[]{3, 3, 3, 3});
            table.addHeaderCell("Bill ID");
            table.addHeaderCell("Date");
            table.addHeaderCell("Payment Mode");
            table.addHeaderCell("Final Amount");

            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (Bill bill : bills) {
                table.addCell(String.valueOf(bill.getId()));
                table.addCell(bill.getCreatedAt().toString());
                table.addCell(bill.getPaymentMode());
                table.addCell(bill.getFinalAmount().toString());
                totalRevenue = totalRevenue.add(bill.getFinalAmount());
            }

            document.add(table);
            document.add(new Paragraph("Total Revenue: Rs. " + totalRevenue).setBold().setMarginTop(10));

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report_" + startDate + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
