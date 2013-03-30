package com.zps.tools.compress;

import com.yahoo.platform.yui.compressor.YUICompressor;

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
		
		ZPCompressor c = new ZPCompressor();
		String input = "E:\\lijz\\workspace\\testArt\\index.html";		
		
		String[] paras = new String[]{"-a","-min",input};
		c.compress(paras);
		
	}

}
