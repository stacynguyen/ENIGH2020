/*

	Calcula si  una combinacion estado/municipio esta en la zona norte
	Para propositos de salario minimo
	
	BM 
	actualizado sept 2022
	
	

	Banxico: salarios minimos (CL289)
	
	El área geográfica de la Zona Libre de la Frontera Norte se integra por los municipios que hacen frontera con Estados Unidos de Norteamérica: 
	
	2 Baja California: Ensenada, Playas de Rosarito, Mexicali, Tecate y Tijuana
		
	8 Chihuahua: Janos, Ascensión, Juárez, Práxedis G. Guerrero, Guadalupe, Coyame del Sotol, Ojinaga y Manuel Benavides
				
	5 Coahuila: Ocampo, Acuña, Zaragoza, Jiménez, Piedras Negras, Nava, Guerrero e Hidalgo 
	
	19 Nuevo León: Anáhuac 
	
	26 Sonora: San Luis Río Colorado, Puerto Peñasco, General Plutarco Elías Calles, Caborca, Altar, Sáric, Nogales, Santa Cruz, Cananea, Naco y Agua Prieta

	28 Tamaulipas: Nuevo Laredo, Guerrero, Mier, Miguel Alemán, Camargo, Gustavo Díaz Ordaz, Reynosa, Río Bravo, Valle Hermoso y Matamoros 
*/
public class ZonaNorte {

	// Municipios de estados relevantes
	
	static String[] m2 = {	"001", "005", "002", "003", "004" };
	static String[] m8 = { "035","005","037","053","028","015","052","042"} ;
	static String[] m5 = { "023","002","038","014","025","022","012","013" };
	static String[] m19 = { "005" };
	static String[] m26 = { "055","048","070","070","017","004","060","043","059","019","039","002"};
	static String[] m28 = { "027","014","024","025","007","015","032","033","040","022" };

	public static void main(String[] args) throws Exception { 
		System.out.println( in(Integer.parseInt(args[0]),args[1]) );
	}
	
	static boolean in(int e, String m) {
		if (e==2)		return in_mun(m, m2);  
		else if (e==5)	return in_mun(m, m5); 
		else if (e==8)	return in_mun(m, m8); 
		else if (e==19)	return in_mun(m, m19); 
		else if (e==26)	return in_mun(m, m26); 
		else if (e==28)	return in_mun(m, m28); 
		else			return false;
	}
	
	static boolean in_mun(String m, String[] ms) {
		for (int i=0; i<ms.length; i++) if (ms[i].equals(m)) return true; 
		return false;
	}

}

