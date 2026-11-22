package at.ac.fhtw.swen3.paperless.ocrworker.service;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    public String extractText(File pdf) throws Exception {
        File image = File.createTempFile("ocr-", ".tif");

        try {
            Process process = new ProcessBuilder("gs", "-sDEVICE=tiffgray", "-r300", "-dUseCropBox", "-o", image.getPath(), pdf.getPath()).start();
            //-r300=300pixel/zoll

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("Could not convert PDF for OCR");
            }

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
            tesseract.setLanguage("deu+eng");
            tesseract.setVariable("user_defined_dpi", "300");   //otherwise: invalid resolution 1dpi, using 70 instead
            return tesseract.doOCR(image);
        } finally {
            image.delete();
        }
    }
}
