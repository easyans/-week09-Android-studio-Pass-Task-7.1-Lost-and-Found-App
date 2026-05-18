package com.aakash.lostandfoundapp;

public class LostFoundItem {
    private int id;
    private String type;
    private String name;
    private String phone;
    private String description;
    private String date;
    private String location;
    private String category;
    private String imagePath;
    private String timestamp;
    private double lat;
    private double lng;

    public LostFoundItem() {}
    public int getId()            { return id; }
    public String getType()       { return type; }
    public String getName()       { return name; }
    public String getPhone()      { return phone; }
    public String getDescription(){ return description; }
    public String getDate()       { return date; }
    public String getLocation()   { return location; }
    public String getCategory()   { return category; }
    public String getImagePath()  { return imagePath; }
    public String getTimestamp()  { return timestamp; }
    public double getLat()        { return lat; }
    public double getLng()        { return lng; }

    public void setId(int id)                   { this.id = id; }
    public void setType(String type)            { this.type = type; }
    public void setName(String name)            { this.name = name; }
    public void setPhone(String phone)          { this.phone = phone; }
    public void setDescription(String d)        { this.description = d; }
    public void setDate(String date)            { this.date = date; }
    public void setLocation(String location)    { this.location = location; }
    public void setCategory(String category)    { this.category = category; }
    public void setImagePath(String imagePath)  { this.imagePath = imagePath; }
    public void setTimestamp(String timestamp)  { this.timestamp = timestamp; }
    public void setLat(double lat)              { this.lat = lat; }
    public void setLng(double lng)              { this.lng = lng; }

    public String getSummary() {
        return type + " " + category + " – " + description;
    }
}