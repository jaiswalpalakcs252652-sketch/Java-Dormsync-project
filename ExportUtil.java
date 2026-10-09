package com.dormsync.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;

public class ExportUtil {

    // ==================== EXCEL EXPORT (TEMP STUB) ====================
    public static void exportTableToExcel(JTable table, String filePath) {
        JOptionPane.showMessageDialog(null,
            "Excel export is temporarily disabled.\n\n" +
            "PDF export works! Try that instead.",
            "Excel Not Available",
            JOptionPane.WARNING_MESSAGE);
    }

    // ==================== PDF EXPORT (TABLE) ====================
    public static void exportTableToPDF(JTable table, String filePath) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            com.lowagie.text.Font titleFont =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                                              com.lowagie.text.Font.BOLD);
            Paragraph title = new Paragraph("DormSync Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            TableModel model = table.getModel();
            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
            pdfTable.setWidthPercentage(100);

            com.lowagie.text.Font headFont =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12,
                                              com.lowagie.text.Font.BOLD);
            for (int i = 0; i < model.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(i), headFont));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            com.lowagie.text.Font dataFont =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10);
            for (int rows = 0; rows < model.getRowCount(); rows++) {
                for (int cols = 0; cols < model.getColumnCount(); cols++) {
                    Object val = model.getValueAt(rows, cols);
                    pdfTable.addCell(new Phrase(val != null ? val.toString() : "", dataFont));
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(null,
                "PDF Exported Successfully!\n" + filePath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "PDF Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== PAYMENT RECEIPT PDF ====================
    public static void generateReceiptPDF(String receiptId, String tenantName, String amount,
                                          String date, String method, String status) {
        Document document = new Document();
        try {
            String fileName = "Receipt_" + receiptId + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            com.lowagie.text.Font titleFont =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 22,
                                              com.lowagie.text.Font.BOLD,
                                              java.awt.Color.BLUE);
            Paragraph title = new Paragraph("DORMSYNC PAYMENT RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("--------------------------------------------------"));

            com.lowagie.text.Font bodyFont =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14);
            document.add(new Paragraph("Receipt ID  : " + receiptId, bodyFont));
            document.add(new Paragraph("Tenant Name : " + tenantName, bodyFont));
            document.add(new Paragraph("Amount Paid : Rs. " + amount, bodyFont));
            document.add(new Paragraph("Paid On     : " + date, bodyFont));
            document.add(new Paragraph("Method      : " + method, bodyFont));
            document.add(new Paragraph("Status      : " + status, bodyFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("--------------------------------------------------"));

            com.lowagie.text.Font footer =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12,
                                              com.lowagie.text.Font.ITALIC);
            document.add(new Paragraph("Thank you for your payment!", footer));

            document.close();
            JOptionPane.showMessageDialog(null, "Receipt Generated: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}