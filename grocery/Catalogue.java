package grocery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/*
 * This is a class to hold all available products for purchase
 * A product is differentiated from another product by the product name (case sensitive)
 * 
 */

public class Catalogue
{
	
	private ArrayList<Product> productList;
	
	Catalogue()
	{
		this.productList = new ArrayList<Product>();
	}

	/*
	 * Returns a boolean if a product exists in the catalogue.  Use the second
	 * argument to set whether the comparsion should take case-sensitivity into accoun
	 * 
	 */
	public boolean productIsInCatalogue( String inProdName, Boolean compareIgnoreCase )
	{

		HashSet<String> currentProductSet = new HashSet<String>();
		
		/*
		 * Get list of pricing product names
		 */
		Iterator ite = this.productList.iterator();
		while( ite.hasNext() )
		{
			if( true == compareIgnoreCase )
			{
				currentProductSet.add( ( (Product)ite.next() ).getProductName().toUpperCase() );
			}
			else
			{
				currentProductSet.add( ( (Product)ite.next() ).getProductName() );
			}
		}
		
		if( true == compareIgnoreCase )
		{
			return currentProductSet.contains( inProdName.toUpperCase() );
		}
		else
		{
			return currentProductSet.contains( inProdName );
		}

	}
	
	/*
	 * Add a product to the catalogue
	 */
	public void addProduct( Product inProduct )
	{
		this.productList.add( inProduct );
	}
	
	/*
	 * Add a rate to an existing product already in the catalogue
	 */
	public void addRateToExistingProduct( String inExistingProdName, Rate inRate )
	{
		Iterator ite = this.productList.iterator();
		
		while( ite.hasNext() )
		{
			Product currentProd = (Product)ite.next();
			String currentProdName = currentProd.getProductName();
			
			if( 0 == currentProdName.compareTo( inExistingProdName ) )
			{
				currentProd.addRate( inRate );
				break;
			}
			
		}
		
	}
	
	/*
	 * Retrieve a product based on its name
	 */
	public Product getProduct( String inExistingProdName )
	{
		Product foundProduct = null;
		Iterator ite = this.productList.iterator();
		
		while( ite.hasNext() )
		{
			Product aProduct = (Product)ite.next();
			if( true == aProduct.getProductName().equals( inExistingProdName ) )
			{
				foundProduct = aProduct;
				break;
			}
		}
		
		return foundProduct;
		
	}
	
	/*
	 * DEBUG
	 */
	public void printCatalogue()
	{
		Iterator ite = this.productList.iterator();
		while( ite.hasNext() )
		{
			( (Product)ite.next() ).printProduct();
		}
	}
	
}
