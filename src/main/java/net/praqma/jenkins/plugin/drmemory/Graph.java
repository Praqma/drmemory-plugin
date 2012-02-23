package net.praqma.jenkins.plugin.drmemory;

public class Graph {
	private String name;
	
	public Graph() {
		
	}
	
	public Graph( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
}
