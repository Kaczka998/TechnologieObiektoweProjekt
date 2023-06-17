# TechnologieObiektoweProjekt

This is a simple Java console application that demonstrates JSON-to-Object and Object-to-JSON mapping using a 3-choice menu. It does not use any 3rd party libraries for mapping, instead it uses custom logic based on reflection mainly. Project is built with Java 17.

## How to Run

1. Open IntelliJ and load your Maven project.
2. Make sure your project is set up correctly with the necessary dependencies. You can check the pom.xml file to verify that the required dependencies are correctly specified.
3. Go to CustomODM > src > main > java > org.example and find Main class.
4. Right-click on the main class file in the project explorer or editor window, then select "Run (..)" from the context menu.

Alternatively, you can run this application in cmd, but it requires to have Maven installed on your PC (as Intellij has Maven built in).


## How to Use

This application has 2 classes predefined for testing purposes. You can find them in Examples.java. Both of them have inner classes - Car has field engine type Engine and Person has field addresses type List<Address>.

While application is running you can see simple menu:

        === Object Mapper Menu ===
        1. Map JSON to Object
        2. Map Object to JSON
        3. Quit
        Enter your choice: 

   Scenario 1: Enter "1".
1. You are asked for json String, f.e. for Car class it will look like: {"make":"Toyota","model":"Yaris","year":2014,"features":["AC","electric mirror"],"engine":{"type":"diesel","displacement":1.2}}
2. Then you are asked for a name of class you want to parse the JSON to. If you provide uncompatible class name then you get error message and are return to the menu. If you provided JSON like above and class name
  as "Car" you should get output looking like:

            Object mapped successfully:
            
            make : Toyota
            model : Yaris
            year : 2014
            features : 
            AC
            electric mirror
            engine : 
            type : diesel
            displacement : 1.2
   
3. You are returned to the menu.
   
   Scenario 2: Enter "2".
1. You are asked for name of class wchich type object you want to map. F.e. you enter "Person".
2. You are asked to enter value for every field expected on Person object to dynamically create Person object. It has information what data type is expected, if you provide uncompatible data type you get error message and   return to the menu.
3. If you provide proper values you get JSON string in response, example:

          Enter your choice: 2
          Enter the class name: Person
          Provide value for name field (expected data type: String): Jan Kowalski
          Provide value for age field (expected data type: int): 49
          addresses: 
          Provide values for street field (separately, expected data type: String): Sienkiewicza 78
          Provide values for city field (separately, expected data type: String): Kielce
          Provide values for state field (separately, expected data type: String): TK
          Provide values for postalCode field (separately, expected data type: String): 25-501
          Are there any more values? (yes/no): no
          JSON mapped successfully:
          {"name":"Jan Kowalski","age":49,"addresses":[{"street":"Sienkiewicza 78","city":"Kielce","state":"TK","postalCode":"25-501"}]}

You probably noticed the question "Are there any more values? (yes/no): no", it asks if you want to add more items to list and it works similar for list of string where you enter every item of list separately and every
another item by typing "yes" and entering this item.

  Scenario 3: Enter "3"
The application stops.

In any other scenario there will show information "Invalid choice. Please try again." and you will return to menu.

## Customization

Most important information about the code is written in code itself with explanation comments. If you would like to use this ODM for mapping your custom objects you have to either put it into Examples.java or mofidy Helper.findClass() method to look for classes also outside of Examples.java.
