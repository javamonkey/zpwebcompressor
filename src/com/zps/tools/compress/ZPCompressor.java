package com.zps.tools.compress;

import jargs.gnu.CmdLineParser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * 增加压缩js,jsp,html,htm,css的选项
 * 压缩<script> </script>里的脚本
 * 压缩<script noCompress> </script>里的脚本
 * @author lijiazhi
 *
 */
public class ZPCompressor {

	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option typeOpt = null;
	CmdLineParser.Option verboseOpt = null;
	CmdLineParser.Option nomungeOpt = null;
	CmdLineParser.Option linebreakOpt = null;
	CmdLineParser.Option preserveSemiOpt = null;
	CmdLineParser.Option disableOptimizationsOpt = null;
	CmdLineParser.Option helpOpt = null;
	CmdLineParser.Option charsetOpt = null;
	CmdLineParser.Option outputFilenameOpt = null;
	CmdLineParser.Option defaultOutputFilenameOpt = null;
	
	int linebreakpos = -1;
	boolean verbose = false;
	String scriptStartTag = "<script";
	String scriptEndTag = "</script>";
	
	public static void main(String[] args){
		ZPCompressor zp = new ZPCompressor();
		zp.compress(args);
	}
	

	public void compress(String args[]) {

		typeOpt = parser.addStringOption("type");
		verboseOpt = parser.addBooleanOption('v', "verbose");
		nomungeOpt = parser.addBooleanOption("nomunge");
		linebreakOpt = parser.addStringOption("line-break");
		preserveSemiOpt = parser.addBooleanOption("preserve-semi");
		disableOptimizationsOpt = parser
				.addBooleanOption("disable-optimizations");
		helpOpt = parser.addBooleanOption('h', "help");
		charsetOpt = parser.addStringOption("charset");
		outputFilenameOpt = parser.addStringOption('o', "output");
		defaultOutputFilenameOpt = parser.addStringOption('a', "append");

		Reader in = null;
		Writer out = null;
		

		try {

			parser.parse(args);

			Boolean help = (Boolean) parser.getOptionValue(helpOpt);
			if (help != null && help.booleanValue()) {
				usage();
				System.exit(0);
			}

			verbose = parser.getOptionValue(verboseOpt) != null;

			String charset = (String) parser.getOptionValue(charsetOpt);
			if (charset == null || !Charset.isSupported(charset)) {
				charset = System.getProperty("file.encoding");
				if (charset == null) {
					charset = "UTF-8";
				}
				if (verbose) {
					System.err.println("\n[INFO] Using charset " + charset);
				}
			}
			
		

			String[] fileArgs = parser.getRemainingArgs();
			String type = (String) parser.getOptionValue(typeOpt);

			if (fileArgs.length == 0) {

				if (type == null || !type.equalsIgnoreCase("js")
						&& !type.equalsIgnoreCase("css")&&!type.equalsIgnoreCase("jsp")&&!type.equalsIgnoreCase("html")&&!type.equalsIgnoreCase("htm")) {
					usage();
					System.exit(1);
				}

				in = new InputStreamReader(System.in, charset);

			} else {

				if (type != null && !type.equalsIgnoreCase("js")
						&& !type.equalsIgnoreCase("css")&&!type.equalsIgnoreCase("jsp")&&!type.equalsIgnoreCase("html")&&!type.equalsIgnoreCase("htm")) {
					usage();
					System.exit(1);
				}

				String inputFilename = fileArgs[0];
			
			
				
				if (type == null) {
					int idx = inputFilename.lastIndexOf('.');
					if (idx >= 0 && idx < inputFilename.length() - 1) {
						type = inputFilename.substring(idx + 1);
						
					}
				}

				if (type == null || !type.equalsIgnoreCase("js")
						&& !type.equalsIgnoreCase("css")&&!type.equalsIgnoreCase("jsp")&&!type.equalsIgnoreCase("html")&&!type.equalsIgnoreCase("htm")) {
					usage();
					System.exit(1);
				}

				in = new InputStreamReader(new FileInputStream(inputFilename),
						charset);
			}

			int linebreakpos = -1;
			String linebreakstr = (String) parser.getOptionValue(linebreakOpt);
			if (linebreakstr != null) {
				try {
					linebreakpos = Integer.parseInt(linebreakstr, 10);
				} catch (NumberFormatException e) {
					usage();
					System.exit(1);
				}
			}

			String outputFilename = (String) parser
					.getOptionValue(outputFilenameOpt);
			
			if(outputFilename==null){
				String suffix = (String) parser
						.getOptionValue(defaultOutputFilenameOpt);
				
			}

			if (type.equalsIgnoreCase("jsp")||type.equalsIgnoreCase("html")||type.equalsIgnoreCase("htm")) {
				// add by 李家智
				if (outputFilename == null) {
					out = new OutputStreamWriter(System.out, charset);
				} else {
					out = new OutputStreamWriter(new FileOutputStream(
							outputFilename), charset);
				}
				
				StringBuilder jspContent = new StringBuilder();
				StringBuilder output = new StringBuilder();
				char[] array  = new char[1024];
				int readCount = -1;
				while((readCount=in.read(array))!=-1){
					jspContent.append(array,0,readCount);
				}
				int start = 0;
				int index = 0;
				while((index = jspContent.indexOf("<script",start))!=-1){					
					int end = jspContent.indexOf(">", index)+1;
					output.append(jspContent.substring(start,end));
					if(end==-1) throw new RuntimeException("wrong jsp");					
					String scriptDesc = jspContent.substring(index, end);
					if(scriptDesc.indexOf("src=")!=-1){	
						start = end;
						continue ;
					}
					
					if(scriptDesc.indexOf("noCompress")!=-1){
						int jsEnd=jspContent.indexOf("</script>",end);
						output.append(jspContent.substring(end,jsEnd+9));
						start =  jsEnd+9;
						continue;
						
					}
					
					//如果是js
					
					int jsEnd=jspContent.indexOf("</script>",end);
					String jsContent = jspContent.substring(end,jsEnd);		
					jsContent = new String(jsContent.getBytes(),"UTF-8");
					StringReader sr = new StringReader(jsContent);
					//StringReader sr = new StringReader("var aaaa = 123;\n var bbbb= 'dfdfdf';");
					
					StringWriter sw = new StringWriter();					
					this.compressJS(sr, sw);					
					output.append(sw.toString());
					output.append("</script>");
					start = jsEnd+"</script>".length();
					
					
				}
				
				output.append(jspContent.substring(start));
				out.write(output.toString());
				
				

			}

			else if (type.equalsIgnoreCase("js")) {
				if (outputFilename == null) {
					out = new OutputStreamWriter(System.out, charset);
				} else {
					out = new OutputStreamWriter(new FileOutputStream(
							outputFilename), charset);
				}
				compressJS(in, out);

			} else if (type.equalsIgnoreCase("css")) {

				CssCompressor compressor = new CssCompressor(in);

				// Close the input stream first, and then open the output
				// stream,
				// in case the output file should override the input file.
				in.close();
				in = null;

				if (outputFilename == null) {
					out = new OutputStreamWriter(System.out, charset);
				} else {
					out = new OutputStreamWriter(new FileOutputStream(
							outputFilename), charset);
				}

				compressor.compress(out, linebreakpos);
			}

		} catch (CmdLineParser.OptionException e) {

			usage();
			System.exit(1);

		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);

		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void saveTemp(String f) throws IOException{
		FileWriter fileWriter = new FileWriter("temp__out.text");
		fileWriter.write(f);
		fileWriter.close();
		
	}

	private void compressJS(Reader in, Writer out) throws IOException {

		try {

			JavaScriptCompressor compressor = new JavaScriptCompressor(in,
					new ErrorReporter() {

						public void warning(String message, String sourceName,
								int line, String lineSource, int lineOffset) {
							if (line < 0) {
								System.err.println("\n[WARNING] " + message);
							} else {
								System.err.println("\n[WARNING] " + line + ':'
										+ lineOffset + ':' + message);
							}
						}

						public void error(String message, String sourceName,
								int line, String lineSource, int lineOffset) {
							if (line < 0) {
								System.err.println("\n[ERROR] " + message);
							} else {
								System.err.println("\n[ERROR] " + line + ':'
										+ lineOffset + ':' + message);
							}
						}

						public EvaluatorException runtimeError(String message,
								String sourceName, int line, String lineSource,
								int lineOffset) {
							error(message, sourceName, line, lineSource,
									lineOffset);
							return new EvaluatorException(message);
						}
					});

			// Close the input stream first, and then open the output stream,
			// in case the output file should override the input file.
			in.close();
			in = null;

			boolean munge = parser.getOptionValue(nomungeOpt) == null;
			boolean preserveAllSemiColons = parser
					.getOptionValue(preserveSemiOpt) != null;
			boolean disableOptimizations = parser
					.getOptionValue(disableOptimizationsOpt) != null;

			compressor.compress(out, linebreakpos, munge, verbose,
					preserveAllSemiColons, disableOptimizations);

		} catch (EvaluatorException e) {

			throw e;
		}

	}

	private static void usage() {
		System.out
				.println("\nUsage: java -jar yuicompressor-x.y.z.jar [options] [input file]\n\n"

						+ "Global Options\n"
						+ "  -h, --help                Displays this information\n"
						+ "  --type <js|css>           Specifies the type of the input file\n"
						+ "  --charset <charset>       Read the input file using <charset>\n"
						+ "  --line-break <column>     Insert a line break after the specified column number\n"
						+ "  -v, --verbose             Display informational messages and warnings\n"
						+ "  -o <file>                 Place the output into <file>. Defaults to stdout.\n\n"

						+ "JavaScript Options\n"
						+ "  --nomunge                 Minify only, do not obfuscate\n"
						+ "  --preserve-semi           Preserve all semicolons\n"
						+ "  --disable-optimizations   Disable all micro optimizations\n\n"

						+ "If no input file is specified, it defaults to stdin. In this case, the 'type'\n"
						+ "option is required. Otherwise, the 'type' option is required only if the input\n"
						+ "file extension is neither 'js' nor 'css'.");
	}
}