# WarehouseSystem
This is a system for handling the selection and loading of fascia for bumpers
on minivans.</br>
It tracks the status of the orders, provides computer support for warehousing workers, and keeps track of inventory levels in the warehouse.</br>
## Command-line Usage </br>
 First, cd into the **project** folder. Make sure that **project/16orders.txt**, **project/initial.csv**, **project/translation.csv** and **project/traversal_table.csv** are presented. Otherwise,
our code **won't** run successfully and will throw **IOException**. </br>
___
Cd into **src/simulation** and compile the code using the following command:</br>
(We didn't use \*.java because there are JUnit test classes in the same
directory)
```
$javac Loader.java Order.java Pallet.java Picker.java Sequencer.java Loader.java Worker.java Simulation.java Warehouse.java WarehouseSystem.java Request.java WarehousePicking.java
```
___
Cd into the **project** (i.e. ../../) folder and run the simulation:
```
$ java -classpath ./src simulation.Simulation 16orders.txt
```
Then, you should see print statements in command-line logging the events
happening in the system cause by 16orders.txt. It will produce **project/orders.csv** which records the orders loaded on the truck and **project/final.csv** recording the racks that *do not* have
30 fascia.
