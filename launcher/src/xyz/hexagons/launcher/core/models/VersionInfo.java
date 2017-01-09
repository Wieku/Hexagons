package xyz.hexagons.launcher.core.models;

public class VersionInfo {
	public String program;
	public String[] classpath;
	public String[] args;
	public String branch;
	public String version;


	public static final VersionInfo defaultInfo;

	static {
		defaultInfo = new VersionInfo();
		defaultInfo.branch = "stable";
	}
}
