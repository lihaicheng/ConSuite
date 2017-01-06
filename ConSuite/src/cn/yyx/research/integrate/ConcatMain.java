package cn.yyx.research.integrate;

import java.io.InputStream;

import cn.yyx.research.slice.Slicer;
import cn.yyx.research.util.CommandUtil;
import cn.yyx.research.util.SystemStreamUtil;

public class ConcatMain {
	
	public void RunOneProcess(String cmd)
	{
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmd);
			InputStream es = process.getErrorStream();
			InputStream is = process.getInputStream();
			Thread t1 = new Thread(new DisplayInfo(is, System.out));
			t1.start();
			Thread t2 = new Thread(new DisplayInfo(es, System.err));
			t2.start();
			process.waitFor();
			t1.join();
			t2.join();
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ConcatMain cm = new ConcatMain();
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			sb.append(" " + args[i]);
		}
		String cmd = "java -jar evosuite-master-1.0.4-SNAPSHOT.jar -Dassertions=false" + sb.toString();
		cm.RunOneProcess(cmd);
		
		Slicer s = new Slicer("evosuite-tests");
		s.SliceSuffixedTestInDirectory("_ESTest");
		SystemStreamUtil.Flush();
		
		String projectcp = CommandUtil.FindProjectClassPath(args);
		String pathsep = System.getProperty("path.separator");
		cmd = "javac " + Slicer.consuitedir + "/*.java" + " -cp ." + (projectcp == null ? "" : (pathsep + projectcp)) + pathsep + "evosuite-standalone-runtime-1.0.4-SNAPSHOT";
		cm.RunOneProcess(cmd);
		
		
	}

}
