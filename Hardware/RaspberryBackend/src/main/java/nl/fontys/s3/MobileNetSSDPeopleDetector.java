package nl.fontys.s3;

import org.opencv.core.*;
import org.opencv.dnn.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MobileNetSSDPeopleDetector {

    private final Net net;
    private static final int PERSON_CLASS_ID = 15;
    private static final float CONFIDENCE_THRESHOLD = 0.5f;

    public MobileNetSSDPeopleDetector(String prototxtPath, String modelPath) {
        net = Dnn.readNetFromCaffe(prototxtPath, modelPath);
    }

    public int detectPeople(Mat frame, Mat[] blobOutput) {
        List<Rect> detections = new ArrayList<>();

        Mat blob = Dnn.blobFromImage(frame, 0.007843, new Size(300, 300),
                new Scalar(127.5, 127.5, 127.5), false, false);
        net.setInput(blob);
        Mat detectionsMat = net.forward();

        detectionsMat = detectionsMat.reshape(1, (int) detectionsMat.total() / 7);
        int peopleCounter = 0;

        for (int i = 0; i < detectionsMat.rows(); i++) {
            float confidence = (float) detectionsMat.get(i, 2)[0];
            int classId = (int) detectionsMat.get(i, 1)[0];

            if (confidence > CONFIDENCE_THRESHOLD && classId == PERSON_CLASS_ID) {
                int left = (int) (detectionsMat.get(i, 3)[0] * frame.cols());
                int top = (int) (detectionsMat.get(i, 4)[0] * frame.rows());
                int right = (int) (detectionsMat.get(i, 5)[0] * frame.cols());
                int bottom = (int) (detectionsMat.get(i, 6)[0] * frame.rows());

                left = Math.max(left, 0);
                top = Math.max(top, 0);
                right = Math.min(right, frame.cols() - 1);
                bottom = Math.min(bottom, frame.rows() - 1);

                Rect rect = new Rect(left, top, right - left, bottom - top);
                detections.add(rect);
                peopleCounter++;
            }
        }

        for (Rect rect : detections) {
            Mat roi = new Mat(frame, rect);

            // 1. Convert to grayscale (existing)
            Mat grayRoi = new Mat();
            Imgproc.cvtColor(roi, grayRoi, Imgproc.COLOR_BGR2GRAY);

            // 2. Apply Gaussian blur (existing)
            Mat blurredGray = new Mat();
            Imgproc.GaussianBlur(grayRoi, blurredGray, new Size(15, 15), 0);

            // 3. Convert back to BGR (existing)
            Mat processedRoi = new Mat();
            Imgproc.cvtColor(blurredGray, processedRoi, Imgproc.COLOR_GRAY2BGR);

            // 4. Pixelate the ROI
            Mat small = new Mat();
            Size pixelSize = new Size(Math.max(rect.width / 10, 1), Math.max(rect.height / 10, 1));
            Imgproc.resize(processedRoi, small, pixelSize, 0, 0, Imgproc.INTER_LINEAR);
            Imgproc.resize(small, processedRoi, processedRoi.size(), 0, 0, Imgproc.INTER_NEAREST);

            // 5. Heavy Gaussian blur on pixelated ROI
            Imgproc.GaussianBlur(processedRoi, processedRoi, new Size(45, 45), 0);



            // 7. Add random noise
            Mat noise = new Mat(processedRoi.size(), processedRoi.type());
            Core.randn(noise, 0, 50);  // mean=0, stddev=50
            Core.add(processedRoi, noise, processedRoi);

            // 8. Copy processed ROI back to original frame
            processedRoi.copyTo(roi);

            // 9. Draw detection rectangle
            Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
        }


        HighGui.imshow("Frame with Privacy-Protected People", frame);

        // Output the processed frame in blobOutput[0]
        blobOutput[0] = frame;

        return peopleCounter;
    }
}
