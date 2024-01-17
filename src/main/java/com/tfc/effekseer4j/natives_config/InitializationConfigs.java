package com.tfc.effekseer4j.natives_config;

public class InitializationConfigs {
	public static String binPath = "bin";
	public static String libsPath = "libs";
	public static String version = "153e";
	public static String os = getOs();
	public static String downloadURL = "https://effekseer.sakura.ne.jp/Releases/Effekseer%version%%os%.zip";
	
	public static String getOs() {
		String os = System.getProperty("os.name");
		os = os.substring(0, 1).toUpperCase() + os.substring(1, 3).toLowerCase();
		return os;
	}
	
	//I hate sourceforge, let it be known
	//It doesn't allow for gradle implementations afaik
	public static String jni4netDownload = "https://downloads.sourceforge.net/project/jni4net/0.8.8/jni4net-0.8.8.0-bin.zip?ts=gAAAAABgjIerN1mfYh6GVHCxq_S9QVEOG2xmf05GXdDgG5IGxbgjOOBmZMf82st426Tcb75Y0Yzz2SnIfoDVVbq28JsCq8FAyQ%3D%3D&amp;use_mirror=versaweb&amp;r=https%3A%2F%2Fsourceforge.net%2Fprojects%2Fjni4net%2Ffiles%2F0.8.8%2F";
	
	public static String versionFile() {
		return "effekseer_" + version + "_" + os;
	}
	
	public static String getDownloadPath() {
		return downloadURL.replace("%version%", version).replace("%os%", os);
	}
}
