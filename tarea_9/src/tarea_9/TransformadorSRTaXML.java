package tarea_9;

public class TransformadorSRTaXML {
	public static void main(String[] args) {
		/*
		 * Ejercicio 1:
		 * 
		 * Realiza un programa en Java, que solicite al usuario la ruta completa de un
		 * fichero de subtítulos, y, llamando a un método no estático, transforme ese
		 * fichero de subtítulos en un fichero XML.
		 * 
		 * Se adjunta un ejemplo de un fichero de subtítulos, y como debe quedar el XML
		 * tras su generación.
		 * 
		 * La clase que contiene el main se llamará “TransformadorSRTaXML”.
		 */

		new ConversorXML().transformaSRTaXML(new ConversorXML().solicitarRutaCompleta());

	}
}