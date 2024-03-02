package routesearch.data.javafile;

public class Company{
	private String name;
	public Company(String name){
		if(name==null) {
			this.name = "";
		}
		this.name = name;
	}
}