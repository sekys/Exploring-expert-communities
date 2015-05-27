package parser;

public class Dvojica {
	public Dvojica(String meno, double ohodnotenie) {
		this.meno = meno;
		this.ohodnotenie = ohodnotenie;
	}
	
	public String meno;
	public double ohodnotenie;
	
	@Override
	public String toString() {
		return meno + " (" + ohodnotenie + ")";
	}
	
	
}
