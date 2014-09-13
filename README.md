groceryApp
==========

A Java console program to simulate a grocery check out system

-------------------------------------------------------------------------------
| Applicaton purpose                                                          |
-------------------------------------------------------------------------------

1. Read and loads a pricing catalogue specified on the command line
2. Prompt user to enter items to purchase
3. Find and rate user-entered items with optimal deals
4. Generate a bill

-------------------------------------------------------------------------------
| Applicaton data structure                                                   |
-------------------------------------------------------------------------------

The application uses the following class:

Catalogue	: 1 to many relationshio to Product class
Product  	: 1 to many relationship to Rate class
Rate     	: store pricing information for a specific product
PurchasedProduct: product name and 1 rate

The purpose of each class is described in the source code

-------------------------------------------------------------------------------
| How to compile and run                                                      |
-------------------------------------------------------------------------------

1. Copy the directory 'grocery' to a folder, say C:\test\grocery
2. Open command prompt and navigate to C:\test
3. >> javac grocery\Grocery.java

   This will generate the following .class files
   - C:\test\grocery\Catalogue.class
   - C:\test\grocery\Grocery.class
   - C:\test\grocery\Product.class
   - C:\test\grocery\PurchasdProduct.class
   - C:\test\grocery\Rate.class

4. Create a pricing catalogue file and note the path to the file
   For example

apple|retail|Retail|1|0.50
Apple|retail|Retail|1|0.50
orange|retail|Retail|1|0.95
avoCado|retail|Retail|1|2.99
orange|tier|Buy 2 get 1 50% off|-1|1-2,0.95,1;3-3,0.95,0.50
apple|bulk|Buy 3 for $1.30|3|1.30
apple|bulk|Buy 2 get 1 free|3|1.00
apple|tier|Buy 1 get 1 50% off|-1|1-1,0.50,1;2-2,0.50,0.50
avoCado|bulk|buy 2 get 2 free|4|5.98

5. >> java grocery/Grocery PATH_TO_FILE
