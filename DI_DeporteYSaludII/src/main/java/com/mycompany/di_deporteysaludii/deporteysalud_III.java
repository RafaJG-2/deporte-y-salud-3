/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.di_deporteysaludii;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.jfree.data.general.DefaultPieDataset;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;







/**
 *
 * @author Rafa
 */
public class deporteysalud_III extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(deporteysalud_III.class.getName());

    /**
     * Creates new form deporteysalud_II
     */
    public deporteysalud_III() {
        initComponents();
        
       try {
            ImageIcon im1 = new ImageIcon(getClass().getResource("/imagenes/heart.png"));
            Image img1 = im1.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            jLabel1.setIcon(new ImageIcon(img1));

            URL url = new URL("https://www.pngplay.com/wp-content/uploads/6/Exercise-Vector-Background-PNG-Image.png");
            ImageIcon im2 = new ImageIcon(url);
            Image img2 = im2.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            jLabel6.setIcon(new ImageIcon(img2));
        } catch (Exception e) {
            e.printStackTrace();
        }
       
           leerarchivoexcel();
    }
    
private void escribirarchivoexcel(String tipo, String fecha, String tiempo, String pulsaciones, String rutaImagen) {
    String archivo = "Entrenamientos.xlsx";

    try {
        XSSFWorkbook workbook;
        XSSFSheet sheet;

        File file = new File(archivo);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            }
            sheet = workbook.getSheetAt(0);
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Entrenamientos");


            Row header = sheet.createRow(0);
            String[] headers = {"Tipo de entrenamiento", "Fecha entrenada", "Tiempo", "Pulsaciones", "Imagen"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
                sheet.setColumnWidth(i, 5000);
            }
        }

        int fila = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(fila);
        row.createCell(0).setCellValue(tipo);
        row.createCell(1).setCellValue(fecha);
        double tiempoNum = 0, pulsacionesNum = 0;
        try { tiempoNum = Double.parseDouble(tiempo); } catch (Exception ignored) {}
        try { pulsacionesNum = Double.parseDouble(pulsaciones); } catch (Exception ignored) {}
        row.createCell(2).setCellValue(tiempoNum);
        row.createCell(3).setCellValue(pulsacionesNum);

        if (rutaImagen != null && !rutaImagen.isEmpty()) {
            try (FileInputStream is = new FileInputStream(rutaImagen)) {
                int pictureIdx = workbook.addPicture(is.readAllBytes(), Workbook.PICTURE_TYPE_PNG);
                XSSFDrawing drawing = sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor();
                anchor.setCol1(4);
                anchor.setRow1(fila);
                XSSFPicture picture = drawing.createPicture(anchor, pictureIdx);
                picture.resize(0.05, 0.05);
            }
        }

        graficobarraexcel(sheet);
        graficopieexcel(sheet);


        try (FileOutputStream out = new FileOutputStream(archivo)) {
            workbook.write(out);
        }

        workbook.close();
        leerarchivoexcel();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void crearGraficoBarrasJPG() {
    try {
        FileInputStream fis = new FileInputStream("Entrenamientos.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                String tipo = row.getCell(0).getStringCellValue();
                double tiempo = row.getCell(2).getNumericCellValue();
                dataset.addValue(tiempo, "Tiempo", tipo);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Tiempo por Tipo de Entrenamiento",
                "Tipo de Entrenamiento",
                "Minutos",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartUtils.saveChartAsJPEG(new File("grafico_barras.jpg"), chart, 1920, 1080);

        workbook.close();
        fis.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void crearGraficoCircularJPG() {
    try {
        FileInputStream fis = new FileInputStream("Entrenamientos.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        DefaultPieDataset dataset = new DefaultPieDataset();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                String tipo = row.getCell(0).getStringCellValue();
                double tiempo = row.getCell(2).getNumericCellValue();
                dataset.setValue(tipo, tiempo);
            }
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Tiempo por tipo de entrenamiento",
                dataset,
                true, true, false);

        ChartUtils.saveChartAsJPEG(new File("grafico_circular.jpg"), chart, 1920, 1080);

        workbook.close();
        fis.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}



private void graficobarraexcel(XSSFSheet sheet) {
    try {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        int ultimaFila = sheet.getLastRowNum();

        XSSFClientAnchor chartAnchor = drawing.createAnchor(0, 0, 0, 0, 6, 1, 16, 20);
        XSSFChart chart = drawing.createChart(chartAnchor);
        chart.setTitleText("Tiempo por tipos de entrenamientos");
        chart.setTitleOverlay(false);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Tipos de entrenamientos");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Tiempo");

        XDDFDataSource<String> tipos = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, ultimaFila, 0, 0));
        XDDFNumericalDataSource<Double> tiempos = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, ultimaFila, 2, 2));

        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series = data.addSeries(tipos, tiempos);
        series.setTitle("Tiempo", null);
        chart.plot(data);
    } catch (Exception e) {
        e.printStackTrace();
    }
}


private void graficopieexcel(XSSFSheet sheet) {
    try {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        int ultimaFila = sheet.getLastRowNum();

        XSSFClientAnchor chartAnchorPie = drawing.createAnchor(0, 0, 0, 0, 6, 22, 16, 42);
        XSSFChart pieChart = drawing.createChart(chartAnchorPie);
        pieChart.setTitleText("Tipos de Entrenamientos");
        pieChart.setTitleOverlay(false);

        XDDFDataSource<String> categorias = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, ultimaFila, 0, 0));
        XDDFNumericalDataSource<Double> valores = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, ultimaFila, 2, 2));

        XDDFChartData pieData = pieChart.createData(ChartTypes.PIE, null, null);
        XDDFChartData.Series pieSeries = pieData.addSeries(categorias, valores);
        pieSeries.setTitle("Tiempo", null);
        pieChart.plot(pieData);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void leerarchivoexcel() {
    String archivo = "Entrenamientos.xlsx";

    try (FileInputStream fis = new FileInputStream(archivo);
         XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

        XSSFSheet sheet = workbook.getSheetAt(0);
        list1.removeAll(); 

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { 
            Row row = sheet.getRow(i);
            if (row != null) {
                String tipo = row.getCell(0).getStringCellValue();
                String fecha = row.getCell(1).getStringCellValue();

                Cell tiempoCell = row.getCell(2);
                String tiempo = (tiempoCell.getCellType() == CellType.NUMERIC) 
                                ? String.valueOf(tiempoCell.getNumericCellValue()) 
                                : tiempoCell.getStringCellValue();

                Cell pulsacionesCell = row.getCell(3);
                String pulsaciones = (pulsacionesCell.getCellType() == CellType.NUMERIC) 
                                     ? String.valueOf(pulsacionesCell.getNumericCellValue()) 
                                     : pulsacionesCell.getStringCellValue();

                String datos = "Tipo: " + tipo + ", Fecha: " + fecha
                        + ", Tiempo: " + tiempo + ", Pulsaciones: " + pulsaciones;
                list1.add(datos);
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}




    
    // PARTE DEL GRIDBAG COMENTADA
    /*
    setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            add(jLabel4, gbc);

            gbc.gridy++;
            add(jComboBox1, gbc);

            gbc.gridy++;
            add(jLabel6, gbc);

            gbc.gridy++;
            add(jTextField1, gbc);

            gbc.gridy++;
            add(jLabel8, gbc);

            gbc.gridy++;
            add(jTextField5, gbc);

            gbc.gridy++;
            add(jLabel7, gbc);

            gbc.gridy++;
            add(jTextField4, gbc);

            gbc.gridy++;
            add(jLabel1, gbc);


        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.add(jButton1);
        panelBotones.add(jButton2);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(panelBotones, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridheight = 10;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(list1, gbc);

        gbc.gridy = 10;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(jLabel2, gbc);
        
        */
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        list1 = new java.awt.List();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONAR", "Cardio", "Pierna", "Running", "Yoga", "Pilates", "Escalada", "Otro" }));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/heart.png"))); // NOI18N

        jLabel2.setText("TIPO DE ENTRENAMIENTO");

        jLabel3.setText("FECHA ENTRENADA");

        jLabel4.setText("TIEMPO ENTRENADO");

        jLabel5.setText("PULSACIONES");

        jButton1.setText("ACEPTAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Guardar en PDF");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addGap(33, 33, 33))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(52, 52, 52))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String tipo = jComboBox1.getSelectedItem().toString();

        if (!tipo.equals("SELECCIONAR")) {
            String datos = "Tipo: " + tipo
                    + ", Fecha: " + jTextField1.getText()
                    + ", Tiempo: " + jTextField2.getText()
                    + ", Pulsaciones: " + jTextField3.getText();
            list1.add(datos);
            
escribirarchivoexcel(tipo, jTextField1.getText(), jTextField2.getText(), jTextField3.getText(), "src/main/resources/imagenes/heart.png");

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
 try {
     
        crearGraficoBarrasJPG();
        crearGraficoCircularJPG();

        String archivoExcel = "Entrenamientos.xlsx";
        String archivoPDF = "InformeEntrenamientos.pdf";

        FileInputStream fis = new FileInputStream(archivoExcel);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
        document.open();


        PdfPTable tabla = new PdfPTable(4);
        tabla.addCell("Tipo");
        tabla.addCell("Fecha");
        tabla.addCell("Tiempo");
        tabla.addCell("Pulsaciones");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                tabla.addCell(row.getCell(0).getStringCellValue());
                tabla.addCell(row.getCell(1).getStringCellValue());
                tabla.addCell(String.valueOf(row.getCell(2).getNumericCellValue()));
                tabla.addCell(String.valueOf(row.getCell(3).getNumericCellValue()));
            }
        }

        document.add(tabla);

        File graficoBarras = new File("grafico_barras.jpg");
        if (graficoBarras.exists()) {
            com.itextpdf.text.Image barras = com.itextpdf.text.Image.getInstance("grafico_barras.jpg");
            barras.scaleToFit(500, 350);
            barras.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(barras);
        }

        File graficoCircular = new File("grafico_circular.jpg");
        if (graficoCircular.exists()) {
            com.itextpdf.text.Image circular = com.itextpdf.text.Image.getInstance("grafico_circular.jpg");
            circular.scaleToFit(500, 350);
            circular.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(circular);
        }

        document.close();
        workbook.close();
        fis.close();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new deporteysalud_III().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private java.awt.List list1;
    // End of variables declaration//GEN-END:variables
}
