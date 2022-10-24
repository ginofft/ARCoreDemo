package com.google.arcore.java.common.MVBB;
import java.util.ArrayList;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;


public class MVBB {
    static class BoundingBox{
        public double rot_angle1;
        public double rot_angle2;
        public double volume;
        public double width;
        public double height;
        public double depth;
        public double min_x;
        public double max_x;
        public double min_y;
        public double max_y;
        public double min_z;
        public double max_z;
        public BoundingBox()
        {
            rot_angle1 =0; rot_angle2=0;
            volume = Double.MAX_VALUE;
            width=0;height=0;depth=0;
            min_x=0;min_y=0;min_z=0;
            max_x=0;max_y=0;max_z=0;
        }
    }
    public float[][] pnts;

    public BoundingBox min_bbox;
    public MVBB(float[][] inputPnts)
    {
        pnts = inputPnts;
        min_bbox = new BoundingBox();
    }
    public MVBB(){
        pnts =  new float[][]
                {{110.0f, 120.0f, 100.0f},
                {100.0f, 100.0f, 120.0f},
                {100.0f, 120.0f, 110.0f},
                {100.0f, 100.0f, 100.0f}};
        min_bbox = new BoundingBox();
    }
    public void minBoundingRect(Double epsilon)
    {
        DMatrixRMaj pnts = convert2DMatrixRMaj();
        if (epsilon==null)
        {
            epsilon = 0.2;
        }
        ArrayList<Double> boxes = new ArrayList<Double>();
        ArrayList<DMatrixRMaj> rotatedPnts = new ArrayList<DMatrixRMaj>();
        ArrayList<Double> angi = arrange(0.0, 90.0, epsilon);
        ArrayList<Double> angj = arrange(0.0, 180.0, epsilon);
        for (Double ideg:angi)
        {
            for (Double jdeg:angj)
            {
                Double i = Math.toRadians(ideg);
                Double j = Math.toRadians(jdeg);
                DMatrixRMaj R = calculateRotationalMatrix(i,j);
                DMatrixRMaj rotPnts = new DMatrixRMaj();
                CommonOps_DDRM.mult(pnts, R, rotPnts);
                rotatedPnts.add(rotPnts);
                double min_x = findMinimumAlongAxis(rotPnts, 0);
                double max_x = findMaximumAlongAxis(rotPnts, 0);
                double min_y = findMinimumAlongAxis(rotPnts, 1);
                double max_y = findMaximumAlongAxis(rotPnts, 1);
                double min_z = findMinimumAlongAxis(rotPnts, 2);
                double max_z = findMaximumAlongAxis(rotPnts, 2);
                double width = max_x - min_x;
                double height = max_y - min_y;
                double depth = max_z - min_z;
                double volume = width * height * depth;
                boxes.add(volume);
                if (volume < min_bbox.volume)
                {
                    min_bbox.rot_angle1 = i; min_bbox.rot_angle2 = j;
                    min_bbox.volume = volume;
                    min_bbox.width = width; min_bbox.height = height; min_bbox.depth = depth;
                    min_bbox.min_x = min_x; min_bbox.min_y = min_y; min_bbox.min_z = min_z;
                    min_bbox.max_x = max_x; min_bbox.max_y = max_y; min_bbox.max_z = max_z;
                }
            }
        }
    }
    public void setPnts(float[][] inputPnts)
    {
        pnts = inputPnts;
    }
    public DMatrixRMaj calculateRotationalMatrix(double i, double j)
    {
        double [][] RotationData =
                {{Math.cos(i), -Math.cos(i-(Math.PI/2)), 0},
                {Math.cos(j)*Math.cos(i-(Math.PI/2)), Math.cos(j)*Math.cos(i), -Math.cos(j-(Math.PI/2))},
                {Math.cos(i-(Math.PI/2))*Math.cos(j-(Math.PI/2)), Math.cos(j-(Math.PI/2))*Math.cos(i), Math.cos(j)}};
        return new DMatrixRMaj(RotationData);
    }
    public DMatrixRMaj calculateCornerPoint()
    {
        DMatrixRMaj R = calculateRotationalMatrix(min_bbox.rot_angle1, min_bbox.rot_angle2);
        double[][] pnts =
                {
                        {min_bbox.min_x, min_bbox.min_y, min_bbox.min_z},
                        {min_bbox.max_x, min_bbox.min_y, min_bbox.min_z},
                        {min_bbox.min_x, min_bbox.max_y, min_bbox.min_z},
                        {min_bbox.max_x, min_bbox.max_y, min_bbox.min_z},

                        {min_bbox.min_x, min_bbox.min_y, min_bbox.max_z},
                        {min_bbox.max_x, min_bbox.min_y, min_bbox.max_z},
                        {min_bbox.min_x, min_bbox.max_y, min_bbox.max_z},
                        {min_bbox.max_x, min_bbox.max_y, min_bbox.max_z}
                };
        DMatrixRMaj originalPnts = new DMatrixRMaj(pnts);
        DMatrixRMaj cornerPnts = new DMatrixRMaj();
        CommonOps_DDRM.mult(originalPnts, R, cornerPnts);
        return cornerPnts;
    }
    private DMatrixRMaj convert2DMatrixRMaj()
    {
        return new DMatrixRMaj(convertFloatMatrix2DoubleMatrix(pnts));
    }
    private static ArrayList<Double> arrange(Double start, Double end, Double step)
    {
        ArrayList<Double> result = new ArrayList<Double>();
        while (start<end)
        {
            result.add(start);
            start+=step;
        }
        return result;
    }

    private static Double findMinimumAlongAxis(DMatrixRMaj M, int Axis) {
        Double result = Double.MAX_VALUE;
        for (int i=0;i<M.numRows;i++)
        {
            result = Math.min(M.get(i,Axis), result);
        }
        return result;
    }

    private static Double findMaximumAlongAxis(DMatrixRMaj M, int Axis)
    {
        Double result = Double.MIN_VALUE;
        for (int i=0; i<M.numRows; i++)
        {
            result = Math.max(M.get(i,Axis), result);
        }
        return result;
    }
    private static double[][] convertFloatMatrix2DoubleMatrix(float[][] input)
    {

        if (input == null)
        {
            return null;
        }

        double[][] output = new double[input.length][input[0].length];
        for (int i=0; i<input.length; i++)
            for (int j=0; j<input[0].length; j++)
                {
                    output[i][j] = input[i][j];
                }
        return output;
    }
}

