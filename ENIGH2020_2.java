 import java.io.*;
import java.util.*;
import java.text.DecimalFormat;


/*

	Segunda version esta trabaja directamente sobre cuenta concentradora concentradohogar
	Aqui, un hogar por registro

	Ingreso total                 = 1.7985407896451033E12
	Numero hogares                = 3.5749659E7
	Numero integrantes/hogares    = 3.5457920311911226
	Ingreso promedio por hogar    = 50309.313150234615	CHECK
	
	CONEVAL Lineas de pobreza pesos corrientes por persona al mes
	
	linea pobreza extrema por ingresos (LPEI canasta alimentaria)
	jun 2020 rural 1279.09 urbana 1679.08
	
	Línea de pobreza por Ingresos (LPI), canasta alimentaria + no alimentaria 
	jun 2020 rural 2481.77 urbana 3504.34

*/

public class ENIGH2020_2 {
	
	static double smm_2020 = 123.22d * 30;		// salario minimo mensual
	static double smm_2021 = 141.70d * 30;
	static double smm_2022 = 172.87d * 30;
	static double smm_2020_ZN = 185.56d * 30;	// zona norte
	static double smm_2021_ZN = 213.39d * 30;
	static double smm_2022_ZN = 260.34d * 30;

	static double LPEI_urbana 	= 1679.08d;	// x persona, mensual
	static double LPEI_rural 	= 1279.09d;
	static double LPI_urbana 	= 3504.34d;
	static double LPI_rural 	= 2481.77d;
	
	static DecimalFormat df0 = new DecimalFormat("##0,000");
	static DecimalFormat df1 = new DecimalFormat("#0.00");
	static DecimalFormat df2 = new DecimalFormat("#0.0000");

	static double pobl = 126760856d ;	// NB Esta es la pobl de ENIGH, no censo censo 2020 126014024d	
	
	static String path = "C:/Users/arrio/Downloads/ENIGH 2020/";
	static int iv = 0;		// id vivienda, 9 campos estados 1-9, 10 en adelante
	static int ih = 1;		// hogar, siempre 1 en este archivo
	static int ifact = 7;	// factor
	static int iih = 12;	// integrantes hogar
	static int ii3 = 22;	// ing_cor ingreso acumulado hogar
	static int ibg = 51; 	// beneficios gobierno
	
	public static void main(String[] args) throws Exception {
		
				
		double nr = 0;	// num reg
		double s = 0;	// ingreso hogar
		double n = 0;	// suma poblacion
		double n_urb = 0;	// suma poblacion urbana
		double n_rur = 0;	// suma poblacion rural
		
		double sh = 0;	// suma ingresos hogares TODA la poblacion, una vez usando factor
		double nh = 0;	// no. hogares
		int edo = 0; 
		boolean urbano = false;
		double[][] importes = new double[32][3];	// edo x {tots,urbano,rural}
		double[][] hogares = new double[32][3];		// edo x {tots,urbano,rural}
		double[][] poble = new double[32][3];	// poblacion estado x {tots,urbano,rural}
		double sf =0;
		double t = 0;
		
		double[][] LPEI = new double[32][2];	// urbana x rural
		double[][] LPI = new double[32][2];  

		double[][] LPEI_c_ayuda = new double[32][2];
		double[][] LPEI_s_ayuda = new double[32][2];
		double[][] LPI_c_ayuda = new double[32][2];
		double[][] LPI_s_ayuda = new double[32][2];
		
		// Ingresos
		
		String fileName = "concentradohogar.csv";
		String data = null;
		BufferedReader in = new BufferedReader (new InputStreamReader (new FileInputStream(path+fileName)));
		in.readLine(); // skip header

		while ( (data=in.readLine())!=null ) {
			String[] ds = data.split(",");
			String folioviv = ds[iv];
			if (folioviv.length()==9) folioviv = "0" + folioviv;
			edo = Integer.parseInt(folioviv.substring(0,2));
			urbano = (Integer.parseInt(folioviv.substring(2,3))!=6);
			
			long v = Long.parseLong(ds[iv]);			// vivienda
			int h = Integer.parseInt(ds[ih]);			// hogar
			double fact = Double.parseDouble(ds[ifact]);// trimestral
			int integ_hogar = Integer.parseInt(ds[iih]);
			double i3 = Double.parseDouble(ds[ii3]);	// trimestral	
			double bg = Double.parseDouble(ds[ibg]);	// beneficios gobierno	
			
			s = i3*fact;
			importes[edo-1][0] = importes[edo-1][0] + s;
			if (urbano) importes[edo-1][1] = importes[edo-1][1] + s;
			else		importes[edo-1][2] = importes[edo-1][2] + s;
			sh = sh+s;

			hogares[edo-1][0] = hogares[edo-1][0] + fact;
			if (urbano) hogares[edo-1][1] = hogares[edo-1][1] + fact;
			else		hogares[edo-1][2] = hogares[edo-1][2] + fact;
			sf = sf + fact;	// suma factores es no hogares en el pais
			
			t = integ_hogar * fact;
			
			poble[edo-1][0] = poble[edo-1][0] + fact;
			if (urbano) poble[edo-1][1] = poble[edo-1][1] + t;
			else		poble[edo-1][2] = poble[edo-1][2] + t;
			n = n + t;			// esto debe sumar pobl total
			if (urbano) n_urb = n_urb + t; else n_rur = n_rur + t;
			
			
			// Umbrales de pobreza
			
			// LPEI
			int j = 0; double umbral = LPEI_urbana; 
			if (!urbano) { j = 1; umbral = LPEI_rural; }
			if ( ((i3/integ_hogar)/3d) < umbral) {
				LPEI[edo-1][j] = LPEI[edo-1][j] + t; //integ_hogar*fact;
				if (bg>0) LPEI_c_ayuda[edo-1][j] = LPEI_c_ayuda[edo-1][j] + t;
				else 	  LPEI_s_ayuda[edo-1][j] = LPEI_s_ayuda[edo-1][j] + t;
			}
				
			// LPI
			umbral = LPI_urbana; if (!urbano) umbral = LPI_rural; 
			if ( ((i3/integ_hogar)/3d) < umbral) {
				LPI[edo-1][j] = LPI[edo-1][j] + t; //integ_hogar*fact;
				if (bg>0) LPI_c_ayuda[edo-1][j] = LPI_c_ayuda[edo-1][j] + t;
				else 	  LPI_s_ayuda[edo-1][j] = LPI_s_ayuda[edo-1][j] + t;
			}
				
			nr++;
		}
			
		in.close();
		
		// Ingresos trimestrales por hogar, nacional
		double perhogar = sh / sf;
		
		System.out.println("Poblacion  total              = "+n);
		System.out.println("Poblacion urbana/rural        = "+n_urb+" / "+n_rur);
		System.out.println("Ingreso total                 = "+sh);
		System.out.println("Numero hogares                = "+sf);
		System.out.println("Numero integrantes/hogares    = "+n/sf);
		System.out.println("Ingreso promedio por hogar    = "+perhogar);
		//System.out.println("Ingreso promedio por hogar + est alquiler = "+(sh/nh+6568d) );
		
		// Ingresos trimestrales por hogar, por estado

		double LPEI_u =	0;
		double LPEI_r =	0;	
		double LPI_u =	0;
		double LPI_r =	0;	
		double LPEI_sa_u =	0 ;
		double LPEI_sa_r =	0 ;
		double LPI_sa_u = 	0 ;
		double LPI_sa_r = 	0 ;

		System.out.println();
		System.out.println();
		
	System.out.println("Estado\tTotal\tUrbano\tRural\t\tLPEI u\tLPEI r\tLPI u\tLPI r\t\tLPEI/su\tLPEI/sr\tLPI/su\tLPI/sr");

		for (int e=0; e<32; e++) {
			System.out.print( (e+1) + "\t" );
			
			// Ingreso
			System.out.print( df0.format(importes[e][0]/hogares[e][0])  + "\t" );
			System.out.print( df0.format(importes[e][1]/hogares[e][1])  + "\t" );
			System.out.print( df0.format(importes[e][2]/hogares[e][2])  + "\t\t" );
			
			// Umbrales pobreza
			System.out.print( df1.format(LPEI[e][0]/poble[e][1])  + "\t" );
			System.out.print( df1.format(LPEI[e][1]/poble[e][2])  + "\t" );
			System.out.print( df1.format(LPI[e][0]/poble[e][1])  + "\t" );
			System.out.print( df1.format(LPI[e][1]/poble[e][2])  + "\t\t" );
			
			// Pobreza c/s ayuda
			System.out.print( df1.format(LPEI_s_ayuda[e][0]/LPEI[e][0])  + "\t" );
			System.out.print( df1.format(LPEI_s_ayuda[e][1]/LPEI[e][1])  + "\t" );
			System.out.print( df1.format(LPI_s_ayuda[e][0]/LPI[e][0])  + "\t" );
			System.out.print( df1.format(LPI_s_ayuda[e][1]/LPI[e][1])  + "\t" );
			
			LPEI_u = LPEI_u + LPEI[e][0] ;
			LPEI_r = LPEI_r + LPEI[e][1] ;
			LPI_u = LPI_u + LPI[e][0] ;
			LPI_r = LPI_r + LPI[e][1] ;
								
			LPEI_sa_u = LPEI_sa_u + LPEI_s_ayuda[e][0] ;
			LPEI_sa_r = LPEI_sa_r + LPEI_s_ayuda[e][1] ;
			LPI_sa_u = LPI_sa_u + LPI_s_ayuda[e][0] ;
			LPI_sa_r = LPI_sa_r + LPI_s_ayuda[e][1] ;

			System.out.println();
		}

		double n_LPEI = LPEI_u + LPEI_r;
		double n_LPI = LPI_u + LPI_r;
		double n_LPEI_sa = LPEI_sa_u + LPEI_sa_r;
		double n_LPI_sa = LPI_sa_u + LPI_sa_r;

		System.out.println();
		System.out.println();
		System.out.println("LPEI = linea pobreza extrema por ingresos (LPEI canasta alimentaria)" );
		System.out.println("rural = "+LPEI_rural+"\turbana = "+LPEI_urbana + "\tjun 2020, persona/mes");
		System.out.println();	
		System.out.println("LPI = linea de pobreza por Ingresos (LPI), canasta alimentaria + no alimentaria ");
		System.out.println("rural = "+LPI_rural+"\turbana = "+LPI_urbana  + "\tjun 2020, persona/mes");

		System.out.println();
		System.out.println();
		System.out.println("Nacional urbana, abajo de LPEI = "+ 	df2.format( (LPEI_u/n_urb)	) );
		System.out.println("Nacional rural , abajo de LPEI = "+ 	df2.format( (LPEI_r/n_rur)	) );
		System.out.println("Nacional urbana, abajo de LPI  = "+ 	df2.format( (LPI_u/n_urb)	) );
		System.out.println("Nacional rural , abajo de LPI  = "+ 	df2.format( (LPI_r/n_rur)	) );
		System.out.println();
		System.out.println("Nacional, abajo de LPEI        = "+ 	df2.format((n_LPEI/n)	) );
		System.out.println("Nacional, abajo de LPI         = "+ 	df2.format((n_LPI/n)	) );

		System.out.println();

		System.out.println("Nacional urbana, abajo de LPEI s/ayuda = "+  df2.format( (LPEI_sa_u/LPEI_u)	) );
		System.out.println("Nacional rural , abajo de LPEI s/ayuda = "+  df2.format( (LPEI_sa_r/LPEI_r)	) );
		System.out.println("Nacional urbana, abajo de LPI  s/ayuda = "+  df2.format( (LPI_sa_u/LPI_u)	) );
		System.out.println("Nacional rural , abajo de LPI  s/syuda = "+  df2.format( (LPI_sa_r/LPI_r)	) );
		System.out.println();
		System.out.println("Nacional, abajo de LPEI s/ayuda  = "+ df2.format(n_LPEI_sa/n_LPEI	) );
		System.out.println("Nacional, abajo de LPI  s/ayuda  = "+ df2.format(n_LPI_sa/n_LPI 	) );

		
	} // main
	
	public String estado_alfa(int e) { return ids_estados[e-1]; } // 1..32
	
	static int id_estado(String name) { // 1..32
		for (int i=0; i<ids_estados.length; i++) if (ids_estados[i].equals(name)) return (i+1); 
		return -1;
	}

	static String[] ids_estados = {
		"Ags", "BC", "BCS", "Camp", "Coah", "Col", "Chis", "Chih","CDMX", "Dur", "Gto",	"Gue",	"Hgo", "Jal", 
		"Mex",	"Mich",	"Mor",	"Nay", "NL", "Oax",	"Pue",	"Que",	"QR", "SLP","Sin",	"Son",	"Tab",	"Tamp",	
		"Tla", "Ver", "Yuc", "Zac",		
	};


} // class

