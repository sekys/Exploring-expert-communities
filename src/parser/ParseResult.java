package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/*
 * Autor ak spolupracuje s niekym tak ma k nemu blizsie
 * 
 * n x n autorov
 * 
 * pre kazdeho autora mame N vektor V matici sprvit jedno ohodnotenie
 * Vacsina vektorov bol 0.
 */

public class ParseResult extends TreeMap<String, List<Dvojica>> {
	private static final long serialVersionUID = 1L;

	public ParseResult() {
		super();
	}

	private void _addResult(String rodic, String dieta, double ohodnotenie) {
		List<Dvojica> zoznam = super.get(rodic);
		if (zoznam == null) {
			zoznam = new ArrayList<Dvojica>();
			super.put(rodic, zoznam);
		}
		
		for (Dvojica dvojica : zoznam) {
			if (dvojica.meno.equals(dieta)) {
				dvojica.ohodnotenie += ohodnotenie;
				return;
			}
		}

		zoznam.add(new Dvojica(dieta, ohodnotenie));
	}

	public void addResult(String a, String b, double ohodnotenie) {
		_addResult(a, b, ohodnotenie);
		_addResult(b, a, ohodnotenie);
	}

	public ArrayList<String> zoznamMien;

	public void vybudujZoznamUnikatnychMien() {
		// Zorad zoznam mien
		zoznamMien = new ArrayList<String>(super.keySet());
		Collections.sort(zoznamMien);
	}
	
	public int getIndex(String meno) {
		return Collections.binarySearch(zoznamMien, meno);
	}
	
};
