请参考build-compress.xml：可以压缩扩展名为js,css,jsp,html,htm文件
有如下参数可以使用： 
keepRunning:true表示如果压缩失败，则对源文件不压缩，直接使用非压缩的文件。如果是false，则在压缩失败的时候退出任务
压缩失败主要包括不合法的js，css文件
linebreak：如果不指定，每行容纳200列
charset：源文件的字符集，默认为Local
nomunge：如果为true，这不混淆，默认为false，采用混淆压缩
