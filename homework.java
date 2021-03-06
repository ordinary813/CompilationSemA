import java.util.HashMap;
import java.util.Scanner;

public class homework{


        static int ADDRESS = 5; 
        static int numLabels = 0;
        static int wantedLabel = 0; 
        static int actualLabel = 0; 

            static final class AST {

                public final String value;
                public final AST left;
                public final AST right;


                private AST(String val,AST left, AST right) {
                    value = val;
                    this.left = left;
                    this.right = right;
                }

                public static AST createAST(Scanner input) {
                    if (!input.hasNext())
                        return null;

                    String value = input.nextLine();
                    if (value.equals("~"))
                        return null;

                    return new AST(value, createAST(input), createAST(input));
                }
            }

            static final class Variable{
            	
                int address;
                String type;

                public Variable(String Type, int Address) {
                    type = Type;
                    address = Address;

                }

            }

            static final class SymbolTable{

                public static HashMap<String,Variable> myHashtable;

                public SymbolTable(HashMap<String,Variable> hashTable) {
                    myHashtable = hashTable;
                }

                public SymbolTable() {
                	myHashtable = new HashMap<String,Variable>();
                }
                
                public static SymbolTable generateSymbolTable(AST tree){
                		
                	SymbolTable st = new SymbolTable();
                	AST treeTemp = tree;	
                	coded(treeTemp, st);
                	return st;
                }
                
                public static void coded(AST tree, SymbolTable symbolTable){
                    
                	
                	if(tree == null) {
                        return;
                    }

                    
  
                    if(tree.value.equals("program")) {

                    	coded(tree.right, symbolTable);
                    	
                    }

                    if(tree.value.equals("content")) {

                    	coded(tree.left, symbolTable);
                    	
                    }

                    if(tree.value.equals("scope")) {

                    	coded(tree.left, symbolTable);
                    }

                    if(tree.value.equals("declarationsList")) {
                    	
                    	if(tree.left != null)
                    		coded(tree.left, symbolTable);
                    	
                    	if(tree.right != null)
                    		coded(tree.right, symbolTable);
                    }

                    if(tree.value.equals("var")) {

                        Variable variable = new Variable(getType(tree),ADDRESS);                        	//creating a new variable with the string getPointerType(tree), and gets assigned an address
                        myHashtable.put(tree.left.left.value,variable);                                 	//new entry in the hashtable, name of the variable , and it's values
                        int toAdd = 1;
                        
                        if(tree.right.value.equals("array"))
                        	toAdd = getTotalRange(tree.right);                                         		//this will count the amount of cells we need to allocate

                        ADDRESS = ADDRESS + toAdd;                                                      	//increasing the size of static ADDRESS
                    }
                  return;	
             }


            private static String getType(AST tree)
            {
            	if(tree == null)
            		return "";
            	else if(tree.left.value.equals("pointer"))
            		return tree.value + " " +getPointerType(tree.left);
            	else if(tree.left.value.equals("identifier"))
            		return myHashtable.get((tree.left.value).type) + " " +getPointerType(tree.left);
            	else
            		return myHashtable.get((tree.left.value).type);
            }

            private static int getTotalRange(AST tree)                                                      //this function returns the total amount of memory slots needed for the array
            {
                if(tree == null)
                    return;

                if(tree.right.equals("range"))
                    return Integer.parseInt(tree.right.left.value) - Integer.parseInt(tree.left.left.value);

                return getTotalRange(tree.left) + getTotalRange(tree.right);
            }

            private static void generatePCode(AST ast, SymbolTable symbolTable) {

            	ast = ast.right;
                code(ast,symbolTable);

            }

            private static void code(AST tree,SymbolTable symbolTable){

                if(tree == null){
                    return;
                }

                if(tree.value.equals("assignment")){

                    codel(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("sto");
                }


                if(tree.value.equals("content")){

                    if(tree.right != null){

                        code(tree.right,symbolTable);
                    }

                }

                if(tree.value.equals("print")) {

                    coder(tree.left,symbolTable);
                    System.out.println("print");
                }

                if(tree.value.equals("if") && !(tree.right.value.equals("else"))){

                    int localWanted=numLabels++;
        
                    coder(tree.left,symbolTable);
                    System.out.println("fjp L" + localWanted);
                    
                    code(tree.right,symbolTable);
                    System.out.println("L" + localWanted + ":");
                        
                    return;

                }

                if(tree.value.equals("if") ){

                    int localWanted=numLabels++;
                    int localActual=numLabels++;
                    wantedLabel=localWanted;
                    actualLabel=localActual;
                    
                    coder(tree.left,symbolTable);
                    System.out.println("fjp L" + localWanted);
                    
                    code(tree.right,symbolTable);
                    System.out.println("L" + localActual+ ":");
                }

                if(tree.value.equals("else") ){

                    int localWanted=wantedLabel++;
                    int localActual=actualLabel++;
                    
                    code(tree.left,symbolTable);
                    System.out.println("ujp L" + localActual);
                    System.out.println("L" + localWanted + ":");
                    code(tree.right,symbolTable);
                }

                if(tree.value.equals("while") ){

                    int localWanted=numLabels++;
                    int localActual=numLabels++;
                    
                    System.out.println("L" + localWanted+ ":");
                    coder(tree.left,symbolTable);
                    
                    System.out.println("fjp L" + localActual);
                    
                    code(tree.right,symbolTable);
                    System.out.println("ujp L" + localWanted);
                    System.out.println("L" + localActual+ ":");
                }
                

                if(tree.value.equals("statementsList")){

                    if(tree.left != null) {

                        code(tree.left,symbolTable);
                    }

                    code(tree.right,symbolTable);


                }

                return;
            }

            private static void coder(AST tree,SymbolTable symbolTable){

                if(tree.value.equals("plus")){

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("add");
                }

                if(tree.value.equals("minus")){

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("sub");
                }

                if(tree.value.equals("multiply")){

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("mul");
                }

                if(tree.value.equals("divide")){

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("div");
                }

                if(tree.value.equals("negative") && tree.right == null ){

                    coder(tree.left,symbolTable);
                    
                    System.out.println("neg");
                }

                if(tree.value.equals("equals")){
                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("equ");
                }

                if(tree.value.equals("notEquals"))
                {
                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("neq");
                }

                if(tree.value.equals("and")){

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("and");
                }

                if(tree.value.equals("or")){

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("or");
                }

                if(tree.value.equals("false")) {

                    System.out.println("ldc 0");
                }

                if(tree.value.equals("true")) {

                    System.out.println("ldc 1");
                }

                if(tree.value.equals("lessThan")) {

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("les");
                }

                if(tree.value.equals("lessOrEquals")) {

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("leq");
                }

                if(tree.value.equals("greaterOrEquals")) {

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("geq");
                }

                if(tree.value.equals("greaterThan")) {

                    coder(tree.left,symbolTable);
                    coder(tree.right,symbolTable);
                    
                    System.out.println("grt");
                }

                if(tree.value.equals("constReal" )){

                    System.out.printf("ldc %f\n" , (float)Float.valueOf(tree.left.value));
                }

                if(tree.value.equals("constBool")) {

                    if(tree.left.value .equals( "true"))
                        System.out.println("ldc 1");

                    if(tree.left.value .equals( "false"))
                        System.out.println("ldc 0");
                }


                if(tree.value.equals("not" )){

                    coder(tree.left,symbolTable);
                    System.out.println("not ");
                }

                if(tree.value.equals("constInt")) {

                    System.out.println("ldc "+(int) Integer.parseInt(tree.left.value));
                }

                if(tree.value.equals("identifier")){

                    codel(tree,symbolTable);
                    System.out.println("ind");
                }

            }

            private static void codel(AST tree,SymbolTable symbolTable){

                if(tree.value.equals("identifier" )){

                    System.out.println("ldc " + SymbolTable.myHashtable.get(tree.left.value).address);
                }
            }


            public static void main(String[] args) {

                Scanner scanner = new Scanner(System.in);

                AST tree = AST.createAST(scanner);
                if(tree != null){

                    SymbolTable symbolTable = new SymbolTable();
                    SymbolTable.generateSymbolTable(tree);

                    generatePCode(tree, symbolTable);

                }

            }

    }

