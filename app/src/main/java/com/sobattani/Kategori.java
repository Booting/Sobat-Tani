package com.sobattani;

public class Kategori {
	private int Id      = 0;
	private String Name = "";

	// constructor tanpa parameter
    public Kategori() {
    
    }
    
    public Kategori(int Id, String Name){
        this.Id = Id;
        this.Name = Name;
    }

	// constructor dengan parameter
    public Kategori(String Name){
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}