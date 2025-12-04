package com.example.enviexpress.util;

import freemarker.template.Template;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import jakarta.servlet.http.HttpServletResponse;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.OutputStream;
import java.util.*;

/**
 * Utilidad para generar archivos PDF a partir de plantillas FreeMarker.
 */
@Component
public class PdfGenerator {

    private final FreeMarkerConfigurer configurer;

    public PdfGenerator(FreeMarkerConfigurer configurer) {
        this.configurer = configurer;
    }

    /**
     * Genera un PDF a partir de una plantilla FreeMarker
     * 
     * @param templateName nombre de la plantilla (sin extensión)
     * @param model mapa con los datos para la plantilla
     * @param filename nombre del archivo PDF a generar
     * @param response HttpServletResponse para enviar el PDF
     * @throws Exception si hay error en la generación
     */
    public void generarPdf(String templateName, Map<String, Object> model, 
                          String filename, HttpServletResponse response) throws Exception {
        
        // Cargar plantilla
        Template template = configurer.getConfiguration().getTemplate(templateName + ".html");
        
        // Procesar plantilla con los datos
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        // Configurar respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".pdf");

        // Generar PDF
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }
}