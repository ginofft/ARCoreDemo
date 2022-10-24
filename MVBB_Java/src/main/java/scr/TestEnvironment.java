package scr;
import org.ejml.data.DMatrixRMaj;

public class TestEnvironment {
    public static void main(String[] args) {
        double[][] temp = {
                {110.0f, 120.0f, 100.0f},
                {100.0f, 100.0f, 120.0f},
                {100.0f, 120.0f, 110.0f},
                {100.0f, 100.0f, 100.0f}
        };
        MVBB exampleBox =  new MVBB();
        exampleBox.setPnts(temp);
        exampleBox.minBoundingRect(0.05);
        DMatrixRMaj cornerPnts = exampleBox.calculateCornerPoint();
        System.out.println("Minimum Volume Bounding Box");
        System.out.println("Rotation Angles");
        System.out.printf("Along z axis: %.2f rad (%.2f deg) %n", exampleBox.min_bbox.rot_angle1, Math.toDegrees(exampleBox.min_bbox.rot_angle1));
        System.out.printf("Along x axis: %.2f rad (%.2f deg) %n", exampleBox.min_bbox.rot_angle2, Math.toDegrees(exampleBox.min_bbox.rot_angle2));
        System.out.printf("Width: %.1f Height: %.1f Depth: %.1f Volume: %.2f%n%n", exampleBox.min_bbox.width, exampleBox.min_bbox.height, exampleBox.min_bbox.depth, exampleBox.min_bbox.volume);
        System.out.println("The corner points are:");
        System.out.println(cornerPnts);
    }
}