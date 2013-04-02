package org.bee.compress;



public class ToolTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		YUICompressor c = new YUICompressor();
		String input = "E:/lijz/workspace/zpwebcompress/resource/js/test.js";
		String output = "E:/lijz/workspace/zpwebcompress/resource/js/test-min.js";
		String[] paras = new String[]{"-o",output,input};
		c.main(paras);
		*/
		/*
		Main c = new Main();
		String input = "E:/lijz/workspace/zpwebcompress/resource/js/test.js";
		String output = "E:/lijz/workspace/zpwebcompress/resource/js/test-min.js";
		String[] paras = new String[]{"-o",output,input};
		c.compress(paras);
		*/
		/*
		ZPCompressor c = new ZPCompressor();
		String input = "E:/lijz/workspace/zpwebcompress/resource/jsp/index.jsp";
		String output = "E:/lijz/workspace/zpwebcompress/resource/jsp/index-min.jsp";
		String[] paras = new String[]{"-o",output,input};
		c.compress(paras);
		*/
		
		BeeCompressor c = new BeeCompressor();
		String input = "E:/lijz/javamonkey/compressor/zpwebcompress_trunk/test/main.css";		
		
		String[] paras = new String[]{"--line-break","200","--nomunge",input};
		c.compress(paras);
		
	}

}
