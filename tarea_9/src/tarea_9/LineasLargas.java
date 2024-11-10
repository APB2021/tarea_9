package tarea_9;

public class LineasLargas {
	public static void main(String[] args) {
		/*
		 * Ejercicio 3:
		 * 
		 * Realiza un programa en Java, que solicite al usuario una carpeta del sistema,
		 * y, para todos los ficheros que tengan la extensión “.srt” genere un fichero
		 * de texto con los subtítulos que tienen una línea (de subtítulo) con más de X
		 * caracteres (aparecerá todo el subtítulo).
		 * 
		 * X será solicitado al usuario en este programa.
		 * 
		 * La clase que contiene el main se llamará “LineasLargas”.
		 */

		new ConversorXML().transformaSRTenCarpetaaTXT(new ConversorXML().solicitarDirectorio());
	}
}