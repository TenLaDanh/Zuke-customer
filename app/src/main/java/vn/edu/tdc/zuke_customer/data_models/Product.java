package vn.edu.tdc.zuke_customer.data_models;

public class Product {
    //properties
    private String key;
    private String id;
    private String name;
    private String category_id;
    private String image;
    private String manu_id;
    private int quantity;
    private String description;
    private int import_price;
    private int price;

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    private int sold;
    private String  created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getManu_id() {
        return manu_id;
    }

    public void setManu_id(String manu_id) {
        this.manu_id = manu_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImport_price() {
        return import_price;
    }

    public void setImport_price(int import_price) {
        this.import_price = import_price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Product( String id, String name, String category_id, String image, String manu_id, int quantity, String description, int import_price, int price, int sold, String created_at) {
        this.id = id;
        this.name = name;
        this.category_id = category_id;
        this.image = image;
        this.manu_id = manu_id;
        this.quantity = quantity;
        this.description = description;
        this.import_price = import_price;
        this.price = price;
        this.sold = sold;
        this.created_at = created_at;
    }

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Product() {
    }

    @Override
    public String toString() {
        return name;
    }
}
