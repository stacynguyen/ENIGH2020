import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

/*
	Caliz para establecer diferencias en ingresos durante meses pandemia
	
	ENIGH: entrevistas 21 ago - 28 nov, mes anterior jul-oct, 6 meses = [feb jul] - [may-oct]
	Los meses mas representados son mayo-julio 2022
	
C:\java>java ENIGH2020_3 P001 (salario subordinado)

mes		masa total salarios		salario/	recibos*				wrt feb
real	reportados en mes		recibos*		
			
2       8.041473193E9           6,347		1266923.0     			1.0000
3       3.1565416417E10         5,995		5265475.0     			0.9445
4       5.5175762153E10         5,909		9337060.0     			0.9310
5       7.9982252637E10         6,244		1.2809527E7 * meses		0.9837
6       7.8781721007E10         6,150		1.2809527E7 * más		0.9690
7       8.3395529452E10         6,510		1.2809527E7 * represent	1.0257 -- de regreso
8       7.8731971784E10         6,821		1.1542604E7   			1.0746
9       5.1418565768E10         6,816		7544052.0     			1.0738
10      2.5532909448E10         7,353		3472467.0     			1.1584	

* (recibos = poblacion empleada, basicamente)

Conclusion: "efecto pandemia". Los meses mas representados se parecen bastante en ingreso a febrero
0.9837/3+0.969/3+1.0257/3=0.9928 y las colas maomeno se compensan. TL DR Nada que ver
	
No entiendo bien. Esperaba más. De las cifras, se ve un maximo de 12.8M de personas consideradas
en el universo muestral. Es bajismimo. Cifras IMSS jun 2020 19.5M registrados, 16.9 permanentes.
P001 debe de estar capurando esa rajita, y ni siquiera toda.

La conclusion sería que en esta esquinita del mercado laboral la pandemia no pegó tan duro esos
meses. ¿Será? ¿Restaurantes p ej? Sería contraejemplo. Sólo que no los tengan registrados...

El resto ha de ser gente que formalmento cotiza pero solo tiene ingresos extraordinarios o algo asi
Y los subempleados, que han de estar en otra columna. Como sea. Tal vez si haya caido mucho el ingreso
pero de aqui no va a salir. El unico dato con ventana de 6 meses es este


*/

public class ENIGH2020_3 {
	
	static DecimalFormat df0 = new DecimalFormat("#,##0,000");
	static DecimalFormat df1 = new DecimalFormat("#0.00");
	static DecimalFormat df2 = new DecimalFormat("#0.0000");


	static String path = "C:/Users/arrio/Downloads/ENIGH 2020/";
	
	static double smm_2020 = 123.22d * 30;	
	static double smm_2021 = 141.70d * 30;
	static double smm_2022 = 172.87d * 30;
	
	static double smm_2020_ZN = 185.56d * 30;	// zona norte
	static double smm_2021_ZN = 213.39d * 30;
	static double smm_2022_ZN = 260.34d * 30;

	public static void main(String[] args) throws Exception {
					
		String data = null;

		// Recupera factores de expansion
		int ih = 1; // hogar
		int ifact = 7;	// factor
		HashMap<String,String> m = new HashMap<String,String>();	
		String fileName = "concentradohogar.csv";
		BufferedReader in = new BufferedReader(new InputStreamReader (new FileInputStream(path+fileName)));
		in.readLine(); // skip header
		while ( (data=in.readLine())!=null ) {
			String[] ds = data.split(",");
			String key = ds[ih];						// folio hogar
			String factor = ds[ifact];					// 1 hogar = ? representa
			String ubica = ds[2];						// ubicacion geo
			String value = ubica+"/"+factor;
			if (ubica.length()==4) ubica = "0" + ubica;	// dont know,guessing
			m.put(key,value);
		}
		in.close();

		// Jovenes construyendo futuro va aparte
		 fileName = "ingresos.csv";
		 in = new BufferedReader (new InputStreamReader (new FileInputStream(path+fileName)));
		in.readLine(); // skip header

		double[] suma = new double[12]; // mes x tipo 
		double[] n = new double[12];
		double nr = 0;
		double smm = 0;	// pobl con salario minimo
		double tot = 0; // tot poblacion representada (personas con un salario)
		
		while ( (data=in.readLine())!=null ) {
			
			String[] ds = data.split(",");
			String clave = ds[3].trim();
			String key = ds[1].trim();		// folio hogar
			
			String ds_hogar = m.get(key);	// datos de concentrado hogar
			if (ds_hogar==null) { System.out.println("*** " + key); System.exit(0); }

			String ubica = ds_hogar.split("/")[0];
			int edo = Integer.parseInt(ubica.substring(0,2));
			String mpo = ubica.substring(2,5);

			String factor_s = ds_hogar.split("/")[1];
			Double factor = Double.parseDouble(factor_s);
			
			// if (!salario_subordinado(clave)) continue;
			if (!clave.equals(args[0])) continue;			// run it 7 times and be done
			
			int i0 = Integer.parseInt(ds[4]);		// ind mes 1 etc
			int i1 = Integer.parseInt(ds[5]);
			int i2 = Integer.parseInt(ds[6]);
			int i3 = Integer.parseInt(ds[7]);
			int i4 = Integer.parseInt(ds[8]);
			int i5 = Integer.parseInt(ds[9]);
			
			double s0 = Double.parseDouble(ds[10]);		// importe mes 1 etc
			double s1 = Double.parseDouble(ds[11]);
			double s2 = Double.parseDouble(ds[12]);
			double s3 = Double.parseDouble(ds[13]);
			double s4 = Double.parseDouble(ds[14]);
			double s5 = Double.parseDouble(ds[15]);

			suma[i0] = suma[i0] + s0*factor;	n[i0] = n[i0]+ factor;
			suma[i1] = suma[i1] + s1*factor;	n[i1] = n[i1]+ factor;
			suma[i2] = suma[i2] + s2*factor;	n[i2] = n[i2]+ factor;
			suma[i3] = suma[i3] + s3*factor;	n[i3] = n[i3]+ factor;
			suma[i4] = suma[i4] + s4*factor;	n[i4] = n[i4]+ factor;
			suma[i5] = suma[i5] + s5*factor;	n[i5] = n[i5]+ factor;
			
			// Extra: cáliz de salario minimo. Correr seleccionando solo P001
			
			// opcion 1: promedio de ingreso x salario es menor o igual al minimo		4 172 924
			double sal_men = (s0+s1+s2+s3+s4+s5)/6;
			double umbral = smm_2020;
			if (ZonaNorte.in(edo,mpo)) umbral = smm_2020_ZN;
			if (sal_men<=umbral) smm = smm + factor; 

			// opcion 2: promedio de ingreso x salario esta en torno al minimo		 = 375 960 !!
			//if ( (0.95d * umbral<= sal_men) && ( sal_men <= umbral * 1.05d) ) smm = smm + factor; // solo absolutos

			
			nr++;
			tot = tot + factor;
			
		}
		in.close();

		// ojo son meses reales; 2 es feb etc
		for (int i=2; i<11; i++) {
			double c1 = suma[i]/n[i] ; // masa percepcion concepto x / no recibos expandido por factor
			double c2 = c1/(suma[2]/n[2]); // wrt febrero
			
		//	System.out.println(i+"\t"+suma[i]+"\t\t"+ n[i]+ "\t\t"+(suma[i]/n[i]));
		//	System.out.println(i+"\t"+suma[i]+"\t\t"+(suma[i]/nr));		
			System.out.println(i+"\t"+suma[i]+"\t\t"+n[i]+"\t\t"+df0.format(c1)+"\t"+df2.format(c2));
		}
		
		// SMM
		System.out.println();
		System.out.println();
		System.out.println("Total de poblacion c/salario (factor ya expandido) "+tot);
		System.out.println("Con SMM = "+smm+", que es "+smm/tot+" de poblacion c/salario");
		
		//" 13.3% de las personas trabajadoras remuneradas y asalariadas que laboran jornada 
		// completa[1] ganan hasta un salario mínimo (1 SM)[2].
		// https://www.gob.mx/conasami/prensa/el-13-3-de-las-personas-remuneradas-y-asalariadas-que-laboran-una-jornada-completa-ganan-hasta-un-salario-minimo-segun-datos-del-censo-2020
		
		// En el tableau de IMSS, oct 2022, bajo "hasta 1 salario minimo" trae 47284! 
		// Debo de estar haciendo algo mal
		// ENOE SEGUNDO TRIMESTRE DE 2021 dice 8.45M, pero no estan distribuidos c/contrato s/contrato ni jornada completa/no
	
	
	} // main
	
	static boolean salario_subordinado(String k) { return (salario_subordinado_i(k)!=-1); }

	static int salario_subordinado_i(String k) {
		if (k.equals("P001")) return 0 ;
		if (k.equals("P002")) return 1 ;
		if (k.equals("P003")) return 2 ;
		if (k.equals("P004")) return 3 ;
		if (k.equals("P005")) return 4 ;
		if (k.equals("P006")) return 5 ;
		if (k.equals("P007")) return 6 ;
		return -1;
	}
	
} // class


