package Training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class LR {
	private static double e = 2.71828182846;
	private static double[] weight = new double [65];
	private static ArrayList<double[]> set = new ArrayList<double[]>();
	private static ArrayList<String> set2 = new ArrayList<String>();
	private static BufferedReader BufferedReader;
	
	private static void readfile(String colour) throws IOException
	{
		File file = new File(System.getProperty("user.dir")+ File.separator 
				+colour+ "TrainingData.dat");
		if(!file.exists())file.createNewFile();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
		
			while(reader.ready())
			{
				double[] temp = new double [64];
//				System.out.print(colour + " ");
				for (int i =0;i<64;i++)
					{
							temp[i]= (reader.read()-2000);
							temp[i]/= 2000;
//							System.out.print(temp[i] + ",");
					}
//				System.out.println();
				set.add(temp);
				set2.add(colour);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static double output(double[] ds)
	{
		int i;
		double b=weight[64];
		double z=0;
		double a;
		z+=b;
		for(i=0;i<64;i++)
		{
			z+= weight[i]*ds[i];
		}
		a = 1.0/(1.0+Math.exp(-z));
		return a;
	}
	public static void xuatw()
	{
		for(int i=0;i<64;i++)System.out.println(weight[i]);
	}
	private static void ANN()
	{	
		Random rd = new Random();
		double[] dw = new double[65];
		double[] dz = new double[set.size()];
		double[] A = new double[set.size()];
		File file = new File(System.getProperty("user.dir")+ File.separator 
				+ "Weight.dat");
		// tren white duoi black
//		double test[] = {-0.975,-0.985,-0.985,-0.955,-0.55,0.0,0.0,-0.975,-0.995,0.0,0.0,0.0,0.0,-0.995,-0.985,-0.995,0.0,0.0,-0.995,0.0,-0.995,-0.985,-0.995,0.0,0.0,0.0,0.0,-0.995,0.0,0.0,0.0,0.0,0.005,0.0,0.0,0.005,0.0,0.0,0.0,0.0,0.0,0.0,0.005,0.0,0.005,0.0,0.0,0.0,0.0,0.005,0.0,0.0,0.015,0.005,0.005,0.005,0.025,0.015,0.015,0.045,0.45,0.0,0.0,0.025,-0.975,-0.985,-0.985,-0.955,-0.55,0.0,0.0,-0.975,-0.995,0.0,0.0,0.0,0.0,-0.995,-0.985,-0.995,0.0,0.0,-0.995,0.0,-0.995,-0.985,-0.995,0.0,0.0,0.0,0.0,-0.995,0.0,0.0,0.0,0.0,0.005,0.0,0.0,0.005,0.0,0.0,0.0,0.0,0.0,0.0,0.005,0.0,0.005,0.0,0.0,0.0,0.0,0.005,0.0,0.0,0.015,0.005,0.005,0.005,0.025,0.015,0.015,0.045,0.45,0.0,0.0,0.025};
//		double test2[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,-0.55,0.0,0.015,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.005,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,-0.995,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,-0.955,0.0,0.0,0.0,0.45,0.0,0.0,0.0,0.0,-0.975,0.0,0.0};
		for(int i =0;i<weight.length;i++)
		{
			weight[i] = rd.nextDouble()-0.5;
		}
		if(!file.exists())
		{
			try {
				file.createNewFile();
				BufferedReader reader = new BufferedReader(new FileReader (file));
				for(int i=0;i<dw.length;i++) {
					dw[i]=Double.parseDouble(reader.readLine());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int step = 4000;double alpha = 0.02;
		for(;step>0;step--)
		{
			for(int i=0;i<set.size();i++)
				{A[i] = output(set.get(i));
				}
			for(int i=0;i<set.size();i++) {
				if(set2.get(i) == "Black")dz[i] = A[i]-1;
				else dz[i] = A[i]+1;
			}
			for(int i=0;i<64;i++)
			{
				double E=0;
				for(int j = 0; j <set.size();j++)E+= set.get(j)[i]*dz[j];
				dw[i] = E/set.size();
			} 
			for (int i =0;i < weight.length;i++)weight[i] -=dw[i] *alpha;
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter (file));
			for(int i=0;i<dw.length;i++) {
				writer.write(String.valueOf(dw[i]));
				writer.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(output(test));
//		System.out.println(output(test2));
//		for(int i =0 ;i< 64;i++) System.out.print(dw[i] + " ");
	}
	
	public static void Train() throws FileNotFoundException {
		try {
			readfile("White");
			readfile("Black");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ANN();
	}
}
