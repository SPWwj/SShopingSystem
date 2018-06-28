package com.example.weiwenjie.sassistbot;
import java.util.List;

public class TextAnalysis {
	private int quantity=0;
	private int quantitylocate;
	String txtBeforeQuantity="";
	String txtAfterQuantity="";
	private String item ="";
	private String nameArray[]= {"Apple","Pear","Orange","Grape","Watermelon"};

    public int getNamePosition() {
        return namePosition;
    }

    private int namePosition=0;
	private List<String> list;
	private Boolean valid=false;

	public int getQuantity() {
		return quantity;
	}

	public String getItem() {
		return item;
	}

	public String[] getNameArray() {
		return nameArray;
	}

	public List<String> getList() {
		return list;
	}

	public Boolean getValid() {
		return valid;
	}

	public TextAnalysis(List<String> list) {
		this.list=list;
		if(Check_itemInDatabase()&&Check_Quantity()) {
			valid=true;
		}else valid = false;

		txt ();
	}


	public void txt (){
        for(int i =0 ; i <list.size() ;i++){
            if(quantitylocate>i){
                txtBeforeQuantity+=list.get(i)+ " ";
            }
            else if (quantitylocate<i) txtAfterQuantity+=list.get(i)+ " ";
        }

	    return;
    }


	
	private Boolean Check_Quantity() {
		Boolean x = false;
		Boolean first=true;
		for (int i=0;i<list.size();i++) {
			if(list.get(i).matches(".*\\d+.*")){
				if(first) { 
					x=true;
					first=false;
					quantity=Integer.parseInt(list.get(i));
					quantitylocate=i;
				}
				else {
					 x = false;
				}
			}
			
		}
		return x;					
	}
	
	private Boolean Check_itemInDatabase() {
		Boolean findItem=false;
		for(int i =0;i<nameArray.length;i++)
		{
			for(int j = 0 ; j<list.size();j++) {
				if(list.get(j).toLowerCase().equals(nameArray[i].toLowerCase()))
				{	
					findItem=true;
					namePosition=i;
					item=nameArray[i];
				}
			}
		}
		return findItem;
	}
	
	public Boolean RemoveQuantity(int i) {
		Boolean pass = false;
		if(i<=quantity && i >0) {
			list.set(quantitylocate, Integer.toString(quantity-i));
			if(Check_itemInDatabase()&&Check_Quantity()) {
				valid=true;
			}else valid = false;
			pass=true;
		}
		else pass =false;

		return pass;



	}
	
	
}
