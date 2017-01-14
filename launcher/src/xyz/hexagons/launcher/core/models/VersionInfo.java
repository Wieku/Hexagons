package xyz.hexagons.launcher.core.models;

public class VersionInfo {
	public File program;
	public File[] classpath;
	public String[] args;
	public String branch;
	public String version;


	public static final VersionInfo defaultInfo;

	static {
		defaultInfo = new VersionInfo();
		defaultInfo.branch = "test";
	}

	public static class File {
		public String obj;
		public String sha;
	}
}
