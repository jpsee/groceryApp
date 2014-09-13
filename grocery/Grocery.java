package grocery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * A console program that does the following:
 * 
 * 1. Read and loads a pricing catalogue specified on the command line
 * 2. Prompt user to enter items to purchase
 * 3. Rate user entered items for optimal deal
 * 4. Generate a bill
 * 
 */

public class Grocery {
	
	private static Catalogue pricingCatalogue;
	private static HashMap<String, Double> inputItemList = new HashMap<String, Double>();
	private static ArrayList<PurchasedProduct> purchasedItemList;

	public static void main(String[] args) {
		
		if( args.length != 1 )
		{
			System.err.println( "Please specify a path to a pricing catalogue file" );
			System.exit( 1 );
		}
		
		generateCatalogue( args[0] );
		gatherUserInput();
		rateItem();
		printItemizedBill();
	}
	
	/*
	 * Parse a text dlimited file that holds pricing information
	 * A sample looks like the following:
	 * 
	 * apple|retail|Retail|1|0.50
	 * Apple|retail|Retail|1|0.50
	 * orange|retail|Retail|1|0.95
	 * avoCado|retail|Retail|1|2.99
	 * orange|tier|Buy 2 get 1 50% off|-1|1-2,0.95,1;3-3,0.95,0.50
	 * apple|bulk|Buy 3 for $1.30|3|1.30
	 * apple|bulk|Buy 2 get 1 free|3|1.00
	 * apple|tier|Buy 1 get 1 50% off|-1|1-1,0.50,1;2-2,0.50,0.50
	 * avoCado|bulk|buy 2 get 2 free|4|5.98
	 *
	 * Column 01 - Product name
	 * Column 02 - Rate name
	 * Column 03 - Rate description (goes on bill)
	 * Column 04 - Total quantity in order for this rate to be effective
	 *           - -1 if this is a tier model
	 * Column 05 - Total cost of the rate
	 *           - For a tiered model, specify different tiers separated by ';'
	 *           - information within a tier is separated by ','
	 *           - e.g 1-5,0.50,0.8 reads The first 5 unit is charged at 0.50 each with 20% off
	 */
	private static void generateCatalogue( String inputFile ) {
		
		try {
			
            List<String> lines = Files.readAllLines( 
            		Paths.get(inputFile),
                    Charset.defaultCharset() );
            
            pricingCatalogue = new Catalogue();
            
            Boolean compareIgnoreCase = false;
            
            /*
             * Step through each line
             */
            for (String line : lines) {
                
            	StringTokenizer token = new StringTokenizer( line, "|" );
            	
            	String productName       = (String)token.nextElement();
            	String rateName          = (String)token.nextElement();
            	String rateDescr         = (String)token.nextElement();
            	Double effectiveQuantity = new Double( (String)token.nextElement() );
            	
            	Double effectivePrice;
            	String tierPrice;
            	Rate newRate;
            	Product newProduct;
            	
            	if( effectiveQuantity == -1 )
            	{
            		/*
            		 * A tiered rate
            		 */
            		tierPrice = (String)token.nextElement();
            		newRate = new Rate( rateName, rateDescr, tierPrice );
            		
            	}
            	else
            	{
            		/*
            		 * Non-tiered rate
            		 */
            		effectivePrice = new Double( (String)token.nextElement() );
            		newRate = new Rate( rateName, rateDescr, effectiveQuantity, effectivePrice );
            	}

            	if( true == pricingCatalogue.productIsInCatalogue( productName, compareIgnoreCase ) )
            	{
            		/*
            		 * Add the rate to the existing product
            		 */
            		pricingCatalogue.addRateToExistingProduct( productName, newRate );
            	}
            	else
            	{
            		/*
            		 * Create a product and associate the rate to the product
            		 * Add the product to the catalogue
            		 */
            		newProduct = new Product( productName );
                	newProduct.addRate( newRate );
                	pricingCatalogue.addProduct( newProduct );
            	}

            	
            } // end reading input file
            
        } 
		catch (IOException e) {
            e.printStackTrace();
        }
		
		//pricingCatalogue.printCatalogue();
		System.out.println( "Price catalogue loaded sucessfully from [" + inputFile + "]\n" );
		
	}

	/*
	 * Prompt and interact with user via console to gather the list of items wishing to purchase
	 * The expected input is in a format like '2 apple'
	 */
	private static void gatherUserInput()
	{

        BufferedReader br 			= new BufferedReader(new InputStreamReader( System.in ) );
        String inStr 				= new String();
        String magicWord			= "CHECK OUT";
        Boolean readyToCheckOut 	= false;
        Boolean compareIgnoreCase	= false;
        StringTokenizer item_tok	= null;
        String tok					= null;
        String itemName				= new String();
        Double itemQuantity			= new Double( 0 );
        
        inputItemList.clear();
        System.out.println( "Please enter an item with quantity in a format like '2 apple'" );
        System.out.println( "When you are done entering item(s), type 'CHECK OUT' to get an itemized bill" );
        
        while( false == readyToCheckOut )
        {
        	
        	System.out.print( ">> ");
        	
        	try {
        		inStr = br.readLine();
        	}catch( IOException ioe ) {
        		System.err.println("Failed to read line item");
        	}
        	
        	item_tok = new StringTokenizer( inStr );
        	
        	while( 	false == inStr.equals( magicWord )
        			&&
        			true == item_tok.hasMoreTokens() )
        	{
        		
        		/*
        		 * ASSUMPTION: User is going to enter something like "5 apple"
        		 * That is, quantity, space then item name
        		 */
        		
        		try
        		{
        			tok = item_tok.nextElement().toString();
        			itemQuantity = new Double( tok );
        			tok = item_tok.nextElement().toString();
        		}
        		catch( NumberFormatException nfe )
        		{
        			System.err.println( "[" + tok + "] is not something I recognize.  Try something like '2 apple'" );
        			break;
        		}
        		catch( Exception e )
        		{
        			System.err.println( "Oops I did not understand that.  Try something like '2 apple'" );
        			break;
        		}
      
        		itemName = tok;
        		//System.out.println( "--- ITEM [" + itemName + "] QUANTITY [" + ItemQuantity + "]" );
        		
        		/*
        		 * Validate the entered product name matches a product from the catalogue
        		 * Case sensitive
        		 */
        		if( false == pricingCatalogue.productIsInCatalogue( itemName, compareIgnoreCase ) )
        		{
        			System.err.println( "Item [" + itemName + "] does not exist in the catalogue" );
        			continue;
        		}
        		
        		if( true == inputItemList.containsKey( itemName ) ) {
        			/*
        			 * Update the quantity
        			 */
        			itemQuantity = itemQuantity + inputItemList.get( itemName );
        			inputItemList.remove( itemName );
        			inputItemList.put( itemName, itemQuantity );
        		}
        		else {
        			/*
        			 * Add the product and the quantity
        			 */
        			inputItemList.put( itemName, itemQuantity );
        		}
        	}
        	
        	if( true == inStr.equals( magicWord ) ) {
        		readyToCheckOut = true;
        	}
        }
        
        //System.out.println( "inputItemList [" + inputItemList + "]" );

	}
	
	/*
	 * Loop through the custom input items and find the best deal(s)
	 */
	private static void rateItem()
	{
		purchasedItemList = new ArrayList<PurchasedProduct>();
		Product aProduct;
		Rate bestRate;
		PurchasedProduct pp;
		double purchasedQuantity = 0;
		
		/*
		 * Step through each input item with quantity
		 */
		for( Map.Entry<String, Double> entry : inputItemList.entrySet() )
		{
		    String prodName 		= entry.getKey();
		    Double prodQuantity 	= entry.getValue();
		    
		    /*
		     * Find the product from the catalogue
		     */
		    aProduct = pricingCatalogue.getProduct( prodName );
		    
		    /*
		     * Get the best rate from the product
		     */
		    bestRate = aProduct.getBestRate( prodQuantity );
		    purchasedQuantity = bestRate.getEffectiveQuantity();
		    
		    pp = new PurchasedProduct( prodName, purchasedQuantity, bestRate );
		    purchasedItemList.add( pp );
		    
		    prodQuantity = prodQuantity - purchasedQuantity;
		    
		    /*
		     * Keep finding the best rate for the same product until we
		     * have filled the quantity
		     */
		    while( prodQuantity > 0 )
		    {
			    bestRate = aProduct.getBestRate( prodQuantity );
			    purchasedQuantity = bestRate.getEffectiveQuantity();
			    
			    pp = new PurchasedProduct( prodName, purchasedQuantity, bestRate );
			    purchasedItemList.add( pp );
			    
			    prodQuantity = prodQuantity - purchasedQuantity;
		    }
		    
		}
	}
	
	/*
	 * Generate a bill for display
	 */
	private static void printItemizedBill()
	{
		PurchasedProduct pp = null;
		Double totalDue = new Double( 0 );
		Double lineTotal = new Double( 0 );
		
		System.out.println( "\nHere is your invoice:" );
		
		Iterator ite = purchasedItemList.iterator();
		while( ite.hasNext() )
		{
			pp = (PurchasedProduct)ite.next();
			lineTotal = pp.getPurhcasedCost();
			System.out.format( "%10s%20s%10.2f\n", pp.getPurchasedProductName(), pp.getPurchasedRateDescr(), lineTotal );
			totalDue += lineTotal;
		}
		
		System.out.format( "\n%10s%20s$%9.2f\n", "TOTAL DUE", "", totalDue );
		
	}
	
}
