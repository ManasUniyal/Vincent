package solesurvivors.vincent;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CSVToMatrix {
    private ArrayList<ArrayList<Double>> f1,f2,f3,f4,b1,b2,b3,b4;
    Context context;
    InputStream inputStream;

    CSVToMatrix(Context context){
       this.context=context;
    }

    public ArrayList<ArrayList<Double>> getMatrix(InputStream inputStream) throws IOException {
        ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
        String str;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while((str=br.readLine())!=null){
            String individualLine[]=str.split(",");
            ArrayList<Double> arr = new ArrayList<>();
            for(int i=0;i<individualLine.length;i++){
                arr.add(Double.parseDouble(individualLine[i]));
            }
            matrix.add(arr);
        }
        return matrix;
    }

    public ArrayList<ArrayList<Double>> multiply(ArrayList<ArrayList<Double>> m1, ArrayList<ArrayList<Double>> m2){
        int r1=(int)m1.size();
        int c1=(int)m1.get(0).size();
        int r2=(int)m2.size();
        int c2=(int)m2.get(0).size();
        double ans;
        ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
        for(int i=0;i<r1;i++){
            ArrayList<Double> v = new ArrayList<>();
            for(int j=0;j<c2;j++) {
                ans = 0;
                for (int k = 0; k < r2; k++) {
                    ans += m1.get(i).get(k) * m2.get(k).get(j);
                }
                v.add(ans);
            }
            matrix.add(v);
        }
        return matrix;
    }

    public ArrayList<ArrayList<Double>> addition(ArrayList<ArrayList<Double>> m1, ArrayList<ArrayList<Double>> m2){
        for(int i=0;i<m1.size();i++){
            for(int j=0;j<m1.get(i).size();j++){
                m1.get(i).set(j, m1.get(i).get(j) + m2.get(i).get(j));
            }
        }
        return m1;
    }

    public ArrayList<ArrayList<Double>> sigmoid(ArrayList<ArrayList<Double>> m){
        for(int i=0;i<(int)m.size();i++){
            for(int j=0;j<(int)m.get(i).size();j++){
                m.get(i).set(j, m.get(i).get(j) / (1 + Math.exp(-m.get(i).get(j))));
            }
        }
        return m;
    }

    private void printMatrix(ArrayList<ArrayList<Double>> m){
        for(int i=0;i<m.size();i++){
            for(int j=0;j<m.get(i).size();j++){
                System.out.println(i+" "+j+" "+m.get(i).get(j)+" ");
            }
        }
    }

    public void getMatrices() throws IOException {

        inputStream = context.getResources().openRawResource(R.raw.f1);
        f1=getMatrix(inputStream);
        inputStream = context.getResources().openRawResource(R.raw.b1);
        b1=getMatrix(inputStream);
        inputStream = context.getResources().openRawResource(R.raw.f2);
        f2=getMatrix(inputStream);
        inputStream = context.getResources().openRawResource(R.raw.b2);
        b2=getMatrix(inputStream);
        inputStream = context.getResources().openRawResource(R.raw.f3);
        f3=getMatrix(inputStream);
        inputStream = context.getResources().openRawResource(R.raw.b3);
        b3=getMatrix(inputStream);
        inputStream = context.getResources().openRawResource(R.raw.f4);
        f4=getMatrix(inputStream);
        inputStream = context.getResources().openRawResource(R.raw.b4);
        b4=getMatrix(inputStream);

    }

    double[] getFinalMatrix() throws IOException {

        //Assignment of the rated 1 D matrix of dimensions 2990*1 into an ArrayList named inputToBeModified

        getMatrices();

        ArrayList<ArrayList<Double>> inputToBeModified = new ArrayList<ArrayList<Double>>();
        for(int i = 0; i<2990; i++){
            ArrayList<Double> v = new ArrayList<>();
            v.add(MainActivity.userRating[i]);
            inputToBeModified.add(v);
        }

        inputToBeModified=multiply(f1,inputToBeModified);
        inputToBeModified=addition(inputToBeModified,b1);
        inputToBeModified=sigmoid(inputToBeModified);
        inputToBeModified=multiply(f2,inputToBeModified);
        inputToBeModified=addition(inputToBeModified,b2);
        inputToBeModified=sigmoid(inputToBeModified);
        inputToBeModified=multiply(f3,inputToBeModified);
        inputToBeModified=addition(inputToBeModified,b3);
        inputToBeModified=sigmoid(inputToBeModified);
        inputToBeModified=multiply(f4,inputToBeModified);
        inputToBeModified=addition(inputToBeModified,b4);

        double[] output=new double[2990];
        for(int i = 0; i< inputToBeModified.size(); i++){
            output[i]= inputToBeModified.get(i).get(0);
        }

        return output;

    }

    public static void main(String[] args) throws IOException {

    }



}

