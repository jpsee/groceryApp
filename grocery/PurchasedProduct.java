package grocery;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * A class to represent the actual product and rate selected.  A customer wishing to
 * purchase 5 apples may end up with 3 purchased product objects all of which
 * are of 'apple' product each with a different rate 
 */

public class PurchasedProduct
{
	
	private String 	purchasedProdName;
	private double 	purchasedQuantity;
	private Rate 	purchasedRate;
	private double	purchasedCost;
	
	PurchasedProduct( String inProdName, double inQuantity, Rate inRate )
	{
		//this.purchasedProdName 	= Character.toUpperCase( inProdName.charAt(0) ) + inProdName.substring( 1 );
		this.purchasedProdName	= inProdName;
		this.purchasedQuantity	= inQuantity;
		this.purchasedRate 		= inRate;
		this.purchasedCost		= this.getCost();
	}
	
	public String getPurchasedProductName()
	{
		return this.purchasedProdName;
	}
	
	public String getPurchasedRateDescr()
	{
		return this.purchasedRate.getRateDescr();
	}
	
	public double getPurhcasedCost()
	{
		return this.purchasedCost;
	}

	private double getCost()
	{
		double lineCost = 0;
		
		if( false == this.purchasedRate.isTiered() )
		{
			lineCost = this.purchasedRate.getEffectivePrice();
		}
		else
		{
			lineCost = this.purchasedRate.getTierPrice( new Double( this.purchasedQuantity ) );
		}
		
		return round( lineCost, 2 );
	}
	
	/*
	 * Reference: http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	 */
	private double round( double value, int places ) 
	{
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}
