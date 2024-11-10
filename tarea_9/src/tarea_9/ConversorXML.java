package tarea_9;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConversorXML {

	public static Scanner sc = new Scanner(System.in);

	public File solicitarRutaCompleta() {

		File fichero = null;

		while (fichero == null || !fichero.exists() || !fichero.canRead()) {

			System.out.println("=====================================================================");
			System.out.println(" Por favor, introduzca la ruta completa de su fichero de subtítulos: ");
			System.out.println(" (EJEMPLO: D:\\AD\\tarea_9\\src\\tarea_9\\prueba.srt");
			System.out.println("=====================================================================");

			String rutaCompleta = sc.nextLine();

			fichero = new File(rutaCompleta);

			if (!fichero.exists() || !fichero.canRead()) {

				System.out.println("=================================================================");
				System.out.println(" El fichero que ha elegido no existe o no se puede acceder a él.");
				System.out.println("=================================================================");

			} else {

				System.out.println("=====================================================================");
				System.out.println(" Ha elegido el fichero: " + fichero.getName());
				System.out.println("=====================================================================");
			}
		}

		return fichero;
	}

	public void transformaSRTaXML(File ficheroSRT) {

		// Necesitaremos un BufferedReader para leer el documento:
		BufferedReader lector = null;

		try {
			lector = new BufferedReader(new FileReader(ficheroSRT));

			// Creamos la factoría para crear nuevos XML:
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation implementation = builder.getDOMImplementation();

			// Creamos el documento XML vacio:
			Document document = implementation.createDocument(null, "subtitulos", null);
			document.setXmlVersion("1.0");

			// Obtenemos el elemento raiz:
			Element elementoRaiz = document.getDocumentElement();

			// Patrón para detectar las líneas de tiempo:
			Pattern patronTiempo = Pattern.compile("(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})");

			String line;

			// Creamos el nodo "subtitulo":
			Element elementoSubtitulo = null;

			// Variable temporal para almacenar el texto del subtítulo
			StringBuilder texto = new StringBuilder();

			// Leemos el fichero línea a línea
			while ((line = lector.readLine()) != null) {
				line = line.trim();

				// Para ignorar las líneas vacias:
				if (line.isEmpty()) {

					// Si estamos al final de un bloque, añadir el teexto acumulado:
					if (elementoSubtitulo != null) {
						Element elementoTexto = document.createElement("texto");
						elementoTexto.appendChild(document.createTextNode(texto.toString().trim()));
						elementoSubtitulo.appendChild(elementoTexto);

						// Reiniciar para el siguiente subtítulo
						texto.setLength(0);
						elementoSubtitulo = null;
					}

					continue;
				}

				// Capturar el número del subtítulo:
				// Si es el número de subtítulo, iniciar un nuevo bloque

				if (elementoSubtitulo == null) {
					elementoSubtitulo = document.createElement("subtitulo");
					elementoSubtitulo.setAttribute("numero", line); // Atributo "número" primero
					elementoRaiz.appendChild(elementoSubtitulo);
					continue;
				}

				// Detectar las líneas de tiempo:
				Matcher matcher = patronTiempo.matcher(line);
				if (matcher.find()) {
					elementoSubtitulo.setAttribute("inicio", matcher.group(1)); // Atributo "inicio" en segundo lugar
					elementoSubtitulo.setAttribute("fin", matcher.group(2)); // Atributo "fin" en tercer lugar
					continue;
				}

				// Acumular el texto del subtítulo conservando saltos de línea naturales
				texto.append(line).append("\n");

			}

			// Cerramos el BufferedReader:
			lector.close();

			// Generamos el fichero XML a partir del documento creado:
			Source source = new DOMSource(document);
			Result result = new StreamResult(new File(ficheroSRT.getPath() + ".xml"));
			Transformer transformer = TransformerFactory.newDefaultInstance().newTransformer();
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generaHTML() {

		String ficheroEstilosXSL = "Alumnos2.xsl";
		String ficheroAlumnosXML = "Alumnos.xml";
		File ficheroHTML = new File("TablaHTML.html");

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(ficheroHTML);

			Source estilos = new StreamSource(ficheroEstilosXSL);
			Source datos = new StreamSource(ficheroAlumnosXML);

			Result result = new StreamResult(fos);

			Transformer transformer = TransformerFactory.newInstance().newTransformer(estilos);
			transformer.transform(datos, result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}