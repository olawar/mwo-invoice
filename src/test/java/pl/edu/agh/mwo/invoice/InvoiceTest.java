package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
	private Invoice invoice;

	@Before
	public void createEmptyInvoiceForTheTest() {
		invoice = new Invoice();
	}

	@Test
	public void testEmptyInvoiceHasEmptySubtotal() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testEmptyInvoiceHasEmptyTaxAmount() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
	}

	@Test
	public void testEmptyInvoiceHasEmptyTotal() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
		Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
		invoice.addProduct(taxFreeProduct);
		Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasProperSubtotalForManyProducts() {
		invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
		invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
		invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testInvoiceHasProperTaxValueForManyProduct() {
		// tax: 0
		invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
		// tax: 8
		invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
		// tax: 2.30
		invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
	}

	@Test
	public void testInvoiceHasProperTotalValueForManyProduct() {
		// price with tax: 200
		invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
		// price with tax: 108
		invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
		// price with tax: 12.30
		invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
		// 2x kubek - price: 10
		invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
		// 3x kozi serek - price: 30
		invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
		// 1000x pinezka - price: 10
		invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
		Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
		// 2x chleb - price with tax: 10
		invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
		// 3x chedar - price with tax: 32.40
		invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
		// 1000x pinezka - price with tax: 12.30
		invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
		Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithZeroQuantity() {
		invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithNegativeQuantity() {
		invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
	}
	
	@Test
	public void testInvoiceId() {
		Invoice.clearNextCounter();
		int number = new Invoice().getNumber();
		int number2 = new Invoice().getNumber();
		Assert.assertNotEquals(number, number2);
	}
	
	@Test
	public void testTheSameInvoiceIdIsFixed() {
		Invoice.clearNextCounter();
		Assert.assertEquals(invoice.getNumber(), invoice.getNumber());
	}
	
	@Test
	public void testInvoiceIdGetsBigger() {
		Invoice.clearNextCounter();
		int number1 = new Invoice().getNumber();
		int number2 = new Invoice().getNumber();
		Assert.assertThat(number1, Matchers.lessThan(number2));
	}
	
	@Test
	public void testInvoiceIdGetsBiggerByOne() {
		Invoice.clearNextCounter();
		int number1 = new Invoice().getNumber();
		int number2 = new Invoice().getNumber();
		Assert.assertEquals(number1+1, number2);
	}
	
	@Test
	public void testInvoiceIdGreaterThanZero() {
		Invoice.clearNextCounter();
		int number1 = new Invoice().getNumber();
		Assert.assertThat(number1, Matchers.greaterThan(0));
	}
	
	@Test
	public void testPrintedInvoiceHasNumber() {
		String printed = invoice.printInvoice();
		String number = String.valueOf(invoice.getNumber());
		Assert.assertThat(printed, Matchers.containsString(number));
	}
	
	@Test
	public void testPrintedInvoiceHasProductInNewLine() {
		invoice.addProduct(new OtherProduct("Oscypek", new BigDecimal("2.50")));
		String printed = invoice.printInvoice();
		Assert.assertThat(printed, Matchers.containsString("\nOscypek"));
	}
	
	@Test
	public void testPrintedInvoiceHasPrice() {
		Invoice inv3 = new Invoice();
		inv3.addProduct(new OtherProduct("Kawa", new BigDecimal("7.50")));
		String printed = inv3.printInvoice();
		Assert.assertThat(printed, Matchers.containsString("7.50"));
	}
	
	@Test
	public void testPrintedInvoiceHasType() {
		Invoice inv4 = new Invoice();
		inv4.addProduct(new DairyProduct("Jogurt", new BigDecimal("17.50")));
		String printed = inv4.printInvoice();
		Assert.assertThat(printed, Matchers.containsString("DairyProduct"));
	}
	
	@Test
	public void testPrintedInvoiceHasQuantity() {
		Invoice inv6 = new Invoice();
		inv6.addProduct(new DairyProduct("Jogurt", new BigDecimal("19.50")), 3);
		String printed = inv6.printInvoice();
		Assert.assertThat(printed, Matchers.containsString("Jogurt, ilosc: 3"));
	}
	
	@Test
	public void testPrintedInvoiceHasSummary() {
		Invoice inv5 = new Invoice();
		inv5.addProduct(new DairyProduct("Jogurt", new BigDecimal("17.50")));
		inv5.addProduct(new OtherProduct("Kawa", new BigDecimal("7.50")));
		inv5.addProduct(new OtherProduct("Oscypek", new BigDecimal("2.50")));
		String printed = inv5.printInvoice();
		Assert.assertThat(printed, Matchers.containsString("Liczba pozycji: 3"));
	}
	
	

}
