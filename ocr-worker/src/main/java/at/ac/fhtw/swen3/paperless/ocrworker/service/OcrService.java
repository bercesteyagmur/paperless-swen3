package at.ac.fhtw.swen3.paperless.ocrworker.service;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    public String extractText(File pdf) throws Exception {
        File image = new File("page.tif");

        new ProcessBuilder("gs", "-sDEVICE=tiffgray", "-r300", "-o", image.getPath(), pdf.getPath()).start().waitFor();

        Tesseract tesseract = new Tesseract();

        tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        return tesseract.doOCR(image);
    }
}
