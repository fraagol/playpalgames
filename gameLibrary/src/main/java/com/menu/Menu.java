package com.menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu {
    private static final String INVALID_OPTION = "*** Invalid Option ***";
    private List<MenuItem> itens;
	private String title;
	private String prompt;
    
    /**
     * 
     * @param maxItens
     */
    public Menu(int maxItens) {
        itens = new ArrayList<>();
//        for(int i = 0; i < maxItens; i++) {
//            itens.add(null);
//        }
    }
    
    /**
     * 
     * @param index
     * @param item
     */
    public void addItem(int index, MenuItem item) {
        itens.add(index, item.setOption(index));
    }

    public void addItem( MenuItem item) {
        itens.add( item);
    }

    public void addItem( MenuItem[] item) {

        itens.addAll(Arrays.asList(item));
    }


    /**
     * 
     * @param index
     * @param label
     * @param action
     */
    public void addItem(int index, String label, Action action) {
        itens.add(index, new MenuItem( label, action));
    }
    
    /**
     * 
     */
    public void run() {
        int option = drawAndGet();
        if (option==0){
            run();
        }else {
            try {
                MenuItem item = itens.get(option - 1);
                if (item != null) {
                    item.run();
                } else {
                    System.out.println(INVALID_OPTION);
                    run();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(INVALID_OPTION);
                run();
            }
        }
        
    }

    public void setTitle(String title) {
    	this.title = title;
    }


    
    
    /**
     * 
     */
    public String toString() {
        StringBuilder builder = new StringBuilder("\n");
        for(MenuItem item : itens) {
            if(item != null) {
                builder.append((itens.indexOf(item)+1)+ item.toString()).append("\n");
            }
        }
        
        return builder.toString();
    }
   
    
   
    
    /**
     * 
     * @return
     */
    private Integer drawAndGet() {
        String option = null;
        try {
            draw();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            option = br.readLine();
            System.out.println();
        } catch(Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return null;
        }
        
         try {
            return Integer.parseInt(option);

         }catch (NumberFormatException e){
          return 0;
         }
    }
    
    /**
     * 
     */
    private void draw() {
    	//clearScreen();
    	//banner();
        System.out.println(this);
        if(prompt != null) {
        	System.out.print(prompt);
        }
        else {
        	System.out.print("Option: ");
        }
    }
    

}
