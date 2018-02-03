package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
	private Collection<Product> products = new ArrayList<Product>();

	public void addProduct(Product product) {
		this.products.add(product);
	}

	public void addProduct(Product product, Integer quantity) {
		if(quantity < 1) {
			throw new IllegalArgumentException();
		}
		for(int i = 0; i < quantity; i++) {
			this.products.add(product);
		}
	}

	public BigDecimal getNetPrice() {
		BigDecimal subtotal = BigDecimal.ZERO;
		for (Product product : this.products) {
			subtotal = subtotal.add(product.getPrice());
		}
		return subtotal;
	}

	public BigDecimal getTax() {
		BigDecimal tax = BigDecimal.ZERO;
		for (Product product : this.products) {
			tax = tax.add(product.getTaxPercent().multiply(product.getPrice()));
		}
		return tax;
	}

	public BigDecimal getGrosPrice() {
		BigDecimal total = getNetPrice().add(getTax());
		return total;
	}
}
