package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

	private Map<Product, Integer> products = new HashMap<Product, Integer>();		
	private static int counter = 0;
	private final int id = ++counter;
	
	static void clearNextCounter() {
		counter = 0;
	}
	
	public void addProduct(Product product) {
		addProduct(product, 1);
	}

	public void addProduct(Product product, Integer quantity) {
		if (product == null || quantity <= 0) {
			throw new IllegalArgumentException();
		}
		products.put(product, quantity);
	}

	public BigDecimal getNetTotal() {
		BigDecimal totalNet = BigDecimal.ZERO;
		for (Product product : products.keySet()) {
			BigDecimal quantity = new BigDecimal(products.get(product));
			totalNet = totalNet.add(product.getPrice().multiply(quantity));
		}
		return totalNet;
	}

	public BigDecimal getTaxTotal() {
		return getGrossTotal().subtract(getNetTotal());
	}

	public BigDecimal getGrossTotal() {
		BigDecimal totalGross = BigDecimal.ZERO;
		for (Product product : products.keySet()) {
			BigDecimal quantity = new BigDecimal(products.get(product));
			totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
		}
		return totalGross;
	}

	public int getNumber() {
		return this.id;
	}

	public String printInvoice() {
		ArrayList<String> linesToPrint = new ArrayList<String>();
		linesToPrint.add(String.valueOf(getNumber()));		
		for (Product p : products.keySet()) {
			linesToPrint.add(p.getName() + ", ilosc: " + products.get(p) + ", typ: " + p.getClass().getSimpleName() + ", cena: " + p.getPrice());
		}		
		linesToPrint.add("Liczba pozycji: " + products.size());
		
		StringBuilder sb = new StringBuilder();
		for (String s : linesToPrint) {
		    sb.append("\n");
		    sb.append(s);
		}		
//		System.out.println(sb.toString());
		return sb.toString();
	}
}
