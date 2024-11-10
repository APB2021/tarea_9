package tarea_9;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConversorXML {

	public static Scanner sc = new Scanner(System.in);

	/**
	 * @author Alberto Polo
	 * @return Devuelve la ruta completa de un fichero de subtítulos
	 */
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

	/**
	 * @author Alberto Polo
	 * @param ficheroSRT recibe un fichero de subtítulos con extensión .srt y lo
	 *                   transforma en .XML
	 */
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

	/**
	 * @author Alberto Polo
	 * @return Devuelve un directorio o carpeta del sistema solicitado al usuario.
	 */
	public File solicitarDirectorio() {

		File directorio = null;

		while (directorio == null || !directorio.exists() || !directorio.canRead() || !directorio.isDirectory()) {

			System.out.println("=============================================================================");
			System.out.println(" Por favor, introduzca la ruta completa de su carpeta de subtítulos: ");
			System.out.println(" (EJEMPLO: C:\\Users\\beton\\git\\tarea_9\\tarea_9\\src\\tarea_9\\ejercicio_2");
			System.out.println("=============================================================================");

			String rutaCarpeta = sc.nextLine();

			directorio = new File(rutaCarpeta);

			if (!directorio.exists() || !directorio.canRead() || !directorio.isDirectory()) {

				System.out.println("=======================================================");
				System.out.println(" La carpeta que ha elegido no existe o no es accesible.");
				System.out.println("=======================================================");

			} else {

				System.out.println("=====================================================================");
				System.out.println(" Ha elegido la carpeta: " + directorio.getAbsolutePath());
				System.out.println("=====================================================================");
			}
		}

		return directorio;
	}

	/**
	 * @author Alberto Polo
	 * @param directorio recibe un directorio o carpeta, filtra los archivos con
	 *                   extensión .srt que contiene y transforma cada uno de ellos
	 *                   en .XML recurriendo al método transformaSRTaXML(File
	 *                   ficheroSRT).
	 */
	public void transformaSRTenCarpetaaXML(File directorio) {

		// Utilizamos listFiles con FilenameFilter para filtrar solo los archivos .srt
		File[] archivosSRT = directorio.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String nombre) {
				// Devuelve true solo si el archivo tiene extensión .srt
				return nombre.toLowerCase().endsWith(".srt");
			}
		});

		// Verifica si se han encontrado archivos .srt y los imprime
		if (archivosSRT != null && archivosSRT.length > 0) {
			System.out.println("Archivos .srt encontrados:");
			for (File archivo : archivosSRT) {
				// Mostramos el nombre del archivo con extensión .srt
				System.out.println(archivo.getName());
				// Lo tratamos con el método creado para el ejercicio 1:
				new ConversorXML().transformaSRTaXML(archivo);
			}
		} else {
			System.out.println("No se encontraron archivos .srt en el directorio especificado.");
		}
	}

	/**
	 * @author Alberto Polo
	 * @param directorio recibe un directorio o carpeta, filtra los archivos con
	 *                   extensión .srt que contiene y transforma cada uno de ellos
	 *                   en .TXT recurriendo al método transformaSRTaTXT(File
	 *                   ficheroSRT, int caracteresMinimos).
	 */
	public void transformaSRTenCarpetaaTXT(File directorio) {

		// Utilizamos listFiles con FilenameFilter para filtrar solo los archivos .srt
		File[] archivosSRT = directorio.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String nombre) {
				// Devuelve true solo si el archivo tiene extensión .srt
				return nombre.toLowerCase().endsWith(".srt");
			}
		});

		// Verifica si se han encontrado archivos .srt y los trata:
		if (archivosSRT != null && archivosSRT.length > 0) {

			// Solicitamos al usuario los caracteres mínimos que ha de tener la línea a
			// incluir:
			System.out.print("Introduce el número mínimo de caracteres que debe tener una línea para ser incluida: ");
			int caracteresMinimos = sc.nextInt();

			// Mostramos un listado con los archivos con extensión ".srt" encontrados en la
			// carpeta elegida:
			System.out.println("Archivos .srt encontrados:");

			for (File archivo : archivosSRT) {
				// Mostramos el nombre del archivo con extensión .srt
				System.out.println(archivo.getName());
				// Lo tratamos con el método creado para convertir .srt en .txt:
				new ConversorXML().transformaSRTaTXT(archivo, caracteresMinimos);
			}
		} else {
			System.out.println("No se encontraron archivos .srt en el directorio especificado.");
		}
	}

	private void transformaSRTaTXT(File ficheroSRT, int caracteresMinimos) {

		System.out.println("ARCHIVO: " + ficheroSRT);
		System.out.println("CARACTERES: " + caracteresMinimos);

		// Necesitaremos un BufferedReader para leer el documento y un BufferedWriter
		// para escribir:
		BufferedReader lector = null;
		BufferedWriter escritor = null;

		try {
			lector = new BufferedReader(new FileReader(ficheroSRT));
			// Tomo como referencia la ruta absoluta y le añado la extensión ".txt":
			escritor = new BufferedWriter(new FileWriter(ficheroSRT.getAbsolutePath() + ".txt"));

			String linea;
			// Variable temporal para almacenar el texto del subtítulo
			StringBuilder subtitulo = new StringBuilder();
			// Patrón para detectar las líneas de tiempo:
			Pattern patronTiempo = Pattern.compile("(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})");
			// boolean para definir si hemos de agregar o no el subtitulo:
			boolean agregarSubtitulo = false;

			// Leemos el fichero línea a línea
			while ((linea = lector.readLine()) != null) {
				linea = linea.trim();

				// Si encontramos una línea vacía, esta marca el fin de un bloque de subtítulos
				if (linea.isEmpty()) {
					// Si alguna línea del subtítulo es demasiado larga, la guardamos
					if (agregarSubtitulo) {
						escritor.write(subtitulo.toString() + "\n\n");
					}
					// Reiniciamos el StringBuilder para el siguiente subtítulo
					subtitulo.setLength(0);
					agregarSubtitulo = false;
					continue;
				}

				// Si es una línea de tiempo, la omitimos:
				Matcher matcher = patronTiempo.matcher(linea);
				if (matcher.matches()) {
					continue; // Omitimos la línea de tiempo.
				}

				// Si no es la primera línea del subtítulo (que es el número), agregamos el
				// texto:
				if (subtitulo.length() > 0) {
					subtitulo.append(linea).append("\n");
				}

				// Si alguna línea supera el mínimo de caracteres, cambiamos "agregarSubtitulo"
				// a true
				if (linea.length() > caracteresMinimos) {
					agregarSubtitulo = true;
				}
			}

			// Procesar el último subtítulo, si es necesario

			if (agregarSubtitulo) {
				escritor.write(subtitulo.toString() + "\n\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Cerramos el BufferedReader y el BufferedWriter:
			try {
				if (lector != null)
					lector.close();
				if (escritor != null)
					escritor.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void subtituloLineaMasLarga(File ficheroSRT) {
	    BufferedReader lector = null;
	    String linea;
	    StringBuilder subtitulo = new StringBuilder();
	    int maxLongitud = 0;
	    String subtituloMasLargo = "";
	    String lineaMasLarga = "";

	    // Patrón para detectar las líneas de tiempo
	    Pattern patronTiempo = Pattern.compile("(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})");

	    try {
	        lector = new BufferedReader(new FileReader(ficheroSRT));

	        while ((linea = lector.readLine()) != null) {
	            linea = linea.trim();

	            // Si encontramos una línea vacía, esta marca el fin de un bloque de subtítulos
	            if (linea.isEmpty()) {
	                // Verificamos si el subtítulo actual tiene la línea más larga
	                if (subtitulo.length() > 0 && lineaMasLarga.length() > maxLongitud) {
	                    maxLongitud = lineaMasLarga.length();
	                    subtituloMasLargo = subtitulo.toString();
	                }
	                // Reiniciamos el StringBuilder para el siguiente subtítulo
	                subtitulo.setLength(0);
	                lineaMasLarga = ""; // Reiniciamos la línea más larga
	                continue;
	            }

	            // Si es una línea de tiempo, la omitimos
	            Matcher matcher = patronTiempo.matcher(linea);
	            if (matcher.matches()) {
	                continue;
	            }

	            // Agregamos la línea al subtítulo
	            subtitulo.append(linea).append("\n");

	            // Si la línea actual es más larga que la anterior, la actualizamos
	            if (linea.length() > maxLongitud) {
	                maxLongitud = linea.length();
	                lineaMasLarga = linea; // Guardamos la línea más larga
	            }
	        }

	        // Procesamos el último subtítulo si es necesario
	        if (subtitulo.length() > 0 && lineaMasLarga.length() > maxLongitud) {
	            maxLongitud = lineaMasLarga.length();
	            subtituloMasLargo = subtitulo.toString();
	        }

	        // Mostrar el subtítulo con la línea más larga y la longitud de la línea más larga
	        if (!subtituloMasLargo.isEmpty()) {
	            System.out.println("Subtítulo con la línea más larga:");
	            System.out.println(subtituloMasLargo);
	            System.out.println("Longitud de la línea más larga: " + maxLongitud);
	        } else {
	            System.out.println("No se encontraron subtítulos válidos.");
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (lector != null) lector.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}


}