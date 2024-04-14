package gui.nhanvien;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JPanel;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class QRScanner extends JPanel implements WebcamListener {

    private static final long serialVersionUID = 1L;

    private Webcam webcam = null;
    private WebcamPanel panel = null;
    private String lastScannedQR = null;
    private boolean cameraOpened = false;

    public QRScanner() {
        super();

        Dimension size = WebcamResolution.QVGA.getSize();

        webcam = Webcam.getDefault();
        webcam.setViewSize(size);
        webcam.addWebcamListener(this);

        panel = new WebcamPanel(webcam);
        panel.setPreferredSize(size);
        panel.setFPSDisplayed(true);

//        JButton toggleButton = new JButton("Mở / tắt");
//        toggleButton.addActionListener(e -> toggleCamera());

        add(panel);
//        add(toggleButton);
    }

    public void toggleCamera() {
        if (webcam.isOpen()) {
            webcam.close();
            panel.stop();
        } else {
            if (isAnyWebcamOpen()) {
                Webcam.getWebcams().forEach(w -> {
                    if (w.isOpen()) {
                        w.close();
                    }
                });
            }
            webcam.open();
            panel.start();
        }
    }

    private boolean isAnyWebcamOpen() {
        for (Webcam w : Webcam.getWebcams()) {
            if (w.isOpen()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void webcamOpen(WebcamEvent we) {
        System.out.println("Webcam open");
    }

    @Override
    public void webcamClosed(WebcamEvent we) {
        System.out.println("Webcam closed");
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {
        System.out.println("Webcam disposed");
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {
        BufferedImage image = we.getImage();
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            if (result != null) {
                String scannedQR = result.getText();
                lastScannedQR = scannedQR;
                System.out.println("QR Code detected: " + scannedQR);
            }
        } catch (NotFoundException e) {
            // QR Code not found in the image
        }
    }

    public WebcamPanel getWebcamPanel() {
        return panel;
    }

    public void closeCamera() {
        if (webcam.isOpen()) {
            webcam.close();
            panel.stop();
        }
    }

    public Webcam getWebcam() {
        return webcam;
    }
    
    public boolean isCameraOpened() {
        return cameraOpened;
    }
}