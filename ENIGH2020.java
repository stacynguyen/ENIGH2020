import java.io.*;
import java.util.*;

/*
	Discrepancias:
	
	1- Ingreso promedio trim oficial = 50,309
		Corriendo este programa = 48594.75 (no deberia dar exactamente igual, falta estimado alquiler a detalle)
		Sacando promedio a campo  ing_cor en concentradohogar = 47838.49
		
	2 - No hogares aqui = 88,927
		No hogares concentradohogar = 89,006
		
	3- No viviendas informe final = 105,483
		No viviendas aqui = 87,674

*/

public class ENIGH2020 {
	static String path = "C:/Users/arrio/Downloads/ENIGH 2020/";
	static int iv = 0;
	static int ih = 1;
	static int ii3 = 16;
	
	public static void main(String[] args) throws Exception {
		
		/* ¨PEND estimado alquiler
		
		variable 55 estim_alqu (ie ieh = 54) en concentradohogar
		folio es 1a col
		
		*/
		
		
		String data = null;

		// Jovenes construyendo futuro va aparte
		String fileName1 = "ingresos_jcf.csv";
		data = null;
		BufferedReader in = new BufferedReader
			(new InputStreamReader (new FileInputStream(path+fileName1)));
		in.readLine(); // skip header
		int nr = 0;		// num reg
		double sr = 0;	// suma ingreso registros
		long vant = 0;
		int hant = 0;
		double s = 0;	// suma ingreso hogar
		double n = 1;	// suma no. ingresos hogar
		double sh = 0;	// suma ingresos hogares
		double nh = 1;	// no. hogares
		double nv = 1;	// no. viviendas
		HashMap<String,Double> m = new HashMap<String,Double>();
		while ( (data=in.readLine())!=null ) {
			String[] ds = data.split(",");
			long v = Long.parseLong(ds[iv]);			// vivienda
														// 2 campos edo, siempre
			int h = Integer.parseInt(ds[ih]);			// hogar
			double i3 = Double.parseDouble(ds[ii3]);	// trimestral

			if (nr>0 && (v!=vant || (v==vant && h!=hant))) {			// corte hogar
				sh = sh + s;
				// System.out.println(s+"\t,"+sh+","+nh);
				String key = ds[iv]+"+"+ds[ih] ;
				m.put(key,s);
				nh++;
				s = 0;
				n = 1;
			}
			s = s + i3;		
			nr++;
			vant = v;
			hant = h;
		}
		in.close();
		sh = sh + s;
		nh++;
		
		// Ingresos
		
		String fileName = "ingresos.csv";
		data = null;
		in = new BufferedReader
			(new InputStreamReader (new FileInputStream(path+fileName)));
		in.readLine(); // skip header
		
		nr = 0;		// num reg
		sr = 0;	// suma ingreso registros
		vant = 0;
		hant = 0;
		s = 0;	// suma ingreso hogar
		n = 1;	// suma no. ingresos hogar
		sh = 0;	// suma ingresos hogares
		nh = 1;	// no. hogares
		nv = 1;	// no. viviendas
		String vkey = null;
		String hkey = null;
		while ( (data=in.readLine())!=null ) {
			String[] ds = data.split(",");
			long v = Long.parseLong(ds[iv]);			// vivienda
														// con edo<10 1 campo, else 2
			int h = Integer.parseInt(ds[ih]);			// hogar
			double i3 = Double.parseDouble(ds[ii3]);	// trimestral
			sr = sr + i3;	
	
			if (nr>0 && v!=vant) nv++;
			if (nr>0 && (v!=vant || (v==vant && h!=hant))) {			// corte hogar
				String keyant = vkey + "+" + hkey;
				Double jcf = m.get(keyant);
				if (jcf!=null) { 
					s = s + jcf;			// cuantos?
					m.remove(keyant);		// see orphans below
				}
				sh = sh + s; 
				//System.out.println(s+"\t,"+sh+","+nh);
				nh++;
				s = 0;
				n = 1;
			}
			s = s + i3;		
			nr++;
			vant = v;
			hant = h;
			vkey = ds[iv]; hkey = ds[ih] ;
		}
		in.close();
		Double jcf = m.get(vkey + "+" + hkey);
		if (jcf!=null) { 
			s = s + jcf;
			m.remove(vkey + "+" + hkey);
		}
		sh = sh + s;
		nh++;
		
		// Adjust orphan JCF w/out entry in income
		double jcf_tot = 0;
		for (Double d:m.values()) jcf_tot = jcf_tot + d;
		
		
		// System.out.println("Numero registros = "+nr);		// check
		// System.out.println("Importe suma registros = "+sr);	// debe ser ==sh check
		System.out.println("Numero viviendas = "+nv);	// 105,483 oficial, no da
		System.out.println("Ingreso total    = "+sh);
		System.out.println("Numero hogares   = "+nh);
		System.out.println("Ingreso promedio por hogar = "+sh/nh);
		System.out.println("Ingreso promedio por hogar + est alquiler = "+(sh/nh+6568d) );
		System.out.println("Remanente JCF = "+jcf_tot);
		System.out.println("Ingreso promedio por hogar + est alquiler + JCF = "+(sh/nh+6568d+jcf_tot/nh) );		//  me da 48594.75, oficial 50,309
		// Sumando campo W ing_cor de concentradohogar, da 47838.49 por hogar
		// Manual: "Suma de los ingresos por trabajo, los provenientes de rentas, de transferencias, de estimación del alquiler y de otros ingresos"
		
		
		
	} // main
	

} // class