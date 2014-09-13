package grocery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * Rate class represents the detail on how to rate a specific product
 * A product may have many rates
 */
public class Rate
{
	
	private String rateName;			
	private String rateDescr;			// bill invoice display
	private double effectiveQuantity;	// the total quantity applicable to take advantage of this rate
	private double effectivePrice;		// the total price applicable
	private double costPerUnit;			// average cost per unit
	private ArrayList<Map> tiers;		// ONLY applicable if this is tierd pricing
	
	Rate()
	{
		this( "N/A", "N/A", 0, 0 );
	}
	
	/*
	 * Non-tier rate constructor
	 */
	Rate( String inRateName, String inRateDesc, double inEffQ, double inEffPr )
	{
		this.rateName = inRateName;
		this.rateDescr = inRateDesc;
		this.effectiveQuantity = inEffQ;
		this.effectivePrice = inEffPr;
		this.tiers = new ArrayList<Map>();
		
		this.costPerUnit = this.getCostPerUnit();			
	}
	
	/*
	 * Tier rate constructor
	 */
	Rate( String inRateName, String inRateDesc, String tier_val )
	{
		this.rateName = inRateName;
		this.rateDescr = inRateDesc;
		this.effectiveQuantity = -1; // this is calculated later in getCostPerUnit
		this.effectivePrice = -1;
		this.tiers = new ArrayList<Map>();
		
		/*
		 * Example tier_val "1-1,0.50,1;2-2,0.50,0.50"
		 * Each tier is separated by ';'
		 * A token within a tier is operated by ','
		 */
		StringTokenizer more_tiers = new StringTokenizer( tier_val, ";" );
		
		while( more_tiers.hasMoreTokens() )
		{
			StringTokenizer tier_detail = new StringTokenizer( (String)more_tiers.nextElement(), "," );
			Map<String, Double> tier_map = new HashMap<String, Double>();
			
			/*
			 * First token is the min to max quantity e.g. 1-1 or 1-5
			 */
			String[] minMaxVal = tier_detail.nextElement().toString().split("-");
			
			tier_map.put( "MIN", new Double( minMaxVal[0] ) );
			tier_map.put( "MAX", new Double( minMaxVal[1] ) );
			
			/*
			 * Second token is the quantity price per unit so 1.50 mean charge each unit for 1.50
			 */
			tier_map.put( "PRICE", new Double( (String)tier_detail.nextElement() ) );
			
			/*
			 * Third token is the discount scale, e.g 1 means 100% no discount and 0.5 means 50% discount
			 */
			tier_map.put( "SCALE", new Double( (String)tier_detail.nextElement() ) );
			
			tiers.add( tier_map );
		}
		
		this.costPerUnit = this.getCostPerUnit();
		
	}
	
	public String getRateDescr()
	{
		return this.rateDescr;
	}
	
	public double getEffectiveQuantity()
	{
		return this.effectiveQuantity;
	}
	
	public double getEffectivePrice()
	{
		return this.effectivePrice;
	}
	
	public Boolean isTiered()
	{
		return ( this.effectivePrice == -1 );
	}
	
	/*
	 * Calculate the total cost with the input quantity
	 */
	public double getTierPrice( double inQuantity )
	{
		Iterator ite 		= this.tiers.iterator();
		
		Double min 			= new Double( 0 );
		Double max			= new Double( 0 );
		Double price		= new Double( 0 );
		Double scale		= new Double( 0 );
		
		Double total_cost 	= new Double( 0 );
		Double total_quan 	= new Double( 0 );
		Double tierMaxQ		= new Double( 0 );
		
		Double toRateQuan  = new Double( inQuantity );

		/*
		 * Step through each tier
		 */
		while( ite.hasNext() )
		{
			
			Map tier_map = (Map)ite.next();
			
			min 	= (Double)tier_map.get( "MIN" );
			max 	= (Double)tier_map.get( "MAX" );
			price 	= (Double)tier_map.get( "PRICE" );
			scale 	= (Double)tier_map.get( "SCALE" );
			
			/*
			 * Get the tier applicable units
			 */
			tierMaxQ = max - min + 1;
			
			if( 0 >= toRateQuan )
			{
				break;
			}
			else if( toRateQuan >= tierMaxQ )
			{
				/*
				 * The incoming to-to-rated quantity is greater than
				 * the tier total units.  Rate it with the
				 * maximum units in this tier
				 */
				total_cost = total_cost + ( tierMaxQ * price * scale );
				toRateQuan = toRateQuan - tierMaxQ;
				continue;
			}
			else
			{
				/*
				 * The incoming to-be-rated quantity is less than
				 * the tier total units.  Rate it with the to-be-rated
				 * quantity
				 */
				total_cost = total_cost + ( toRateQuan * price * scale );
				break;
			}
			
		}
		
		return total_cost;
		
	}
	
	/*
	 * Calculate the 'average' cost per unit
	 * 
	 * For a non-tiered rate, the average cost is price over quantity
	 * 
	 * For a tiered rate, we calculate each tier cost, add them up and
	 * divide by the total quantity to get the average cost
	 * 
	 */
	public double getCostPerUnit()
	{
		
		if( false == this.isTiered() )
		{
			/*
			 * Simple pricing; individual or bulk
			 */
			return ( this.effectivePrice / this.effectiveQuantity );
		}
		else
		{
			/*
			 * Tier pricing.  Calculate the total cost then divide by the quantity to
			 * get the average cost
			 */
			Iterator ite 		= this.tiers.iterator();
			
			Double min 			= new Double( 0 );
			Double max			= new Double( 0 );
			Double price		= new Double( 0 );
			Double scale		= new Double( 0 );
			
			Double total_cost 	= new Double( 0 );
			Double total_quan 	= new Double( 0 );
			Double costPerUnit	= new Double( 0 );
			
			while( ite.hasNext() )
			{
				
				Map tier_map = (Map)ite.next();
				
				min 	= (Double)tier_map.get( "MIN" );
				max 	= (Double)tier_map.get( "MAX" );
				price 	= (Double)tier_map.get( "PRICE" );
				scale 	= (Double)tier_map.get( "SCALE" );
				
				if( 0 >= ( max - min + 1 ) )
				{
					break;
				}
				
				total_quan = total_quan + ( max - min + 1 );
				total_cost = total_cost + ( ( max - min + 1 ) * price * scale );
				
			}
			
			this.effectiveQuantity = total_quan;
			costPerUnit = total_cost / total_quan;
			
			return costPerUnit;
		}
	}
	
	/*
	 * DEBUG
	 */
	public void printRate()
	{
		System.out.println( "\tRATE NAME [" + this.rateName + "]" );
		System.out.println( "\tRATE DESC [" + this.rateDescr + "]" );
		System.out.println( "\tQUANTITY [" + this.effectiveQuantity + "]" );
		System.out.println( "\tCOST PER UNIT [" + this.costPerUnit + "]" );
		
		if( false == this.isTiered() )
		{
			System.out.println( "\tPRICE [" + this.effectivePrice + "]" );
		}
		else
		{
			int num_tiers = this.tiers.size();
			for( int i = 0; i < num_tiers; ++i )
			{
				System.out.println( "\t--- TIER [" + ( i + 1 ) + "]" );
				System.out.println( "\t\t    --- MIN      [" + this.tiers.get( i ).get( "MIN") + "]" );
				System.out.println( "\t\t    --- MAX      [" + this.tiers.get( i ).get( "MAX") + "]" );
				System.out.println( "\t\t    --- PRICE    [" + this.tiers.get( i ).get( "PRICE") + "]" );
				System.out.println( "\t\t    --- SCALE    [" + this.tiers.get( i ).get( "SCALE") + "]" );
			}
		}
		
		System.out.println( "\n\n");
	}

}
