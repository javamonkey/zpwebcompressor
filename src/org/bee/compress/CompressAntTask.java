package org.bee.compress;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;


public class CompressAntTask extends Task {

        private List<FileSet> filesets = new ArrayList<FileSet>();
        private Mapper mapper;
        
        private int linebreak = 200;
        private boolean nomunge = false;
//	    private boolean preserveAllSemiColons = false;
//	    private boolean disableOptimizations = false;
//	    private boolean verbose = false;
        private String todir;
        private String charSet = Charset.defaultCharset().name();
        //如果压缩有错，，不压缩，直接采用原来内容，然后报告
        private boolean keepRunning = true;
       
        private List allErrorFiles = new ArrayList();
        
        
        public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }
        
        public void addMapper(Mapper mapper) {
                this.mapper = mapper;
        }
        
//        public void setDisableOptimizations(boolean disableOptimizations) {
//                this.disableOptimizations = disableOptimizations;
//        }
        
        public void setLinebreak(int linebreak) {
                this.linebreak = linebreak;
        }

        public boolean isKeepRunning() {
			return keepRunning;
		}

		public void setKeepRunning(boolean keepRunning) {
			this.keepRunning = keepRunning;
		}

	
//        public void setPreserveAllSemiColons(boolean preserveAllSemiColons) {
//                this.preserveAllSemiColons = preserveAllSemiColons;
//        }

        public String getCharSet() {
			return charSet;
		}

		public void setCharSet(String charSet) {
			this.charSet = charSet;
		}

		public void setToDir(String todir) {
                this.todir = todir;
        }
        
//        public void setVerbose(boolean verbose) {
//                this.verbose = verbose;
//        }
        
        private void validateRequired() throws BuildException {
                StringBuilder errorString = new StringBuilder();
                
                if (mapper == null)
                        errorString.append("Mapper property is required\n");
                if (todir == null || "".equals(todir))
                        errorString.append("Output directory is not specified\n");
                
                if (errorString.length()>0) {
                        throw new BuildException(errorString.toString());
                }
        }
        
        public void execute() throws BuildException {
        	//	log("made by ibeetl.com");
                validateRequired();
                int total = 0;
                Iterator<FileSet> iter = filesets.listIterator();
                
                while (iter.hasNext()) {
                        FileSet fileset = iter.next();
                        DirectoryScanner scanner = fileset.getDirectoryScanner(getProject());
                        File dir = scanner.getBasedir();                        
                        String[] files = scanner.getIncludedFiles();
                        for (int i = 0; i < files.length; i++) {
                            String[] output = mapper.getImplementation().mapFileName(files[i]);
                            if (output != null) {
                                try {
                                    total++;    
                                	compress(new File(dir, files[i]), new File(todir, output[0]));
                                        
                                } catch (IOException io) {
                                        log("Failed to compress file: " + files[i]);
                                }
                            }
                        }
                }
                log("Compress total:"+total+" Failed:"+allErrorFiles.size());
                if(allErrorFiles.size()!=0){                	
                       for(Object msg:allErrorFiles){
                       	log("Failed to compress file: " + msg);
                       }
                }
                
             
        }
        
        private void compress(File source, File dest) throws IOException {
        	File f = new File(dest.getParent());
        	if(!f.exists()){
        		f.mkdirs();
        	}
        	BeeCompressor zp = new BeeCompressor();
        	String input = source.toString();	
        	String output = dest.toString();
        	List<String> list = new ArrayList();
        	list.add("--charset");
        	list.add(this.charSet);
        	
        	list.add("-o");
        	list.add(output);        
        	
        	list.add("--line-break");
        	list.add(String.valueOf(this.linebreak));
        	if(this.nomunge){
        		list.add("--nomunge");
        	}
        	list.add(input);
        	
     	
    		try{
    			
    			zp.compress((String[])list.toArray(new String[0]));
    		}catch(Throwable ex){
    			log("Failed to compress file: " +input);    		
    			if(this.keepRunning){
    				allErrorFiles.add(input);
    				simpleCopy(source,dest);
    			}else{
    				
        			throw new RuntimeException(ex.getMessage()+"  "+input,ex);
    			}
    			
    		}
    		
    		
        	
        }
        
        private void simpleCopy(File source, File dest) throws IOException{
        	char[] array  = new char[1024];
			int readCount = -1;
			InputStreamReader in = new InputStreamReader(new FileInputStream(source),
					charSet);
			StringWriter w = new StringWriter();
			while((readCount=in.read(array))!=-1){
				w.write(array, 0, readCount);
				
			}
			//閲嶅悕
			in.close();
			FileWriter fw = new FileWriter(dest);
			fw.write(w.toString());
			fw.close();
			
		
        }
}