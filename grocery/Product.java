package grocery;

import java.util.HashSet;
import java.util.Iterator;

/*
 * A pricing product class that holds all the rates for the product
 *
 */
public class Product
{

	private String productName;
	private HashSet <Rate> productRate;
	
	Product() 
	{
		
		this( "N/A" );
		
	}
	
	Product( String inProductName ) 
	{
		
		this.productName = inProductName;
		this.productRate = new HashSet <Rate>();
		
	}
	
	/*
	 * Add a rate to this product
	 */
	public void addRate( Rate inRate ) 
	{
		
		this.productRate.add( inRate );
		
	}
	
	/*
	 * DEBUG
	 */
	public void printProduct() 
	{
		
		System.out.println( "*** PRODUCT NAME [" + this.productName + "] ***\n" );
		
		if( this.productRate.size() > 0 ) 
		{
			Iterator ite = this.productRate.iterator();
			while( ite.hasNext() )
			{
				((Rate)ite.next()).printRate();
			}
		}
		else 
		{
			System.out.println( "This product does not have rates defined");
		}
		
		System.out.println( "" );
		
	}
	
	public String getProductName()
	{
		return this.productName;
	}
	
	/*
	 * A rate is considered optimal (for the customer) if it meets the following
	 * criteria in order:
	 * 
	 * 1. The effective quantity of the rate applies
	 *    e.g. a promotion like 'buy 3 apple at $1.20' is not applicable if we
	 *         only wish to purchase 1 apple
	 *         
	 * 2. The average cost per unit is the lowest
	 * 
	 */
	public Rate getBestRate( Double inQuantity )
	{
		/*
		 * Find list of rates that are applicable based on quantity
		 * That is, inQuantiy >= rate's effective quantity
		 */
		Rate lowestRate = null;
		HashSet <Rate> applicableRate = new HashSet <Rate>();
		
		Iterator ite = this.productRate.iterator();
		while( ite.hasNext() )
		{
			Rate aRate = (Rate)ite.next();
			if( inQuantity >= aRate.getEffectiveQuantity() )
			{
				applicableRate.add( aRate );
			}
		}
		
		/*
		 * Amongst the available rates, pick the rate with
		 * the lowest cost per unit
		 */
		
		ite = applicableRate.iterator();
		while( ite.hasNext() )
		{
			Rate appRate = (Rate)ite.next();
			
			if( null == lowestRate )
			{
				/*
				 * Handle first time entering the loop
				 */
				lowestRate = appRate;
			}
			
			if( lowestRate.getCostPerUnit() > appRate.getCostPerUnit() )
			{
				lowestRate = appRate;
			}
		}
		
		return lowestRate;
		
	}
	
}
