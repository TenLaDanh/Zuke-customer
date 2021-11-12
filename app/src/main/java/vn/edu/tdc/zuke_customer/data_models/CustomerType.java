package vn.edu.tdc.zuke_customer.data_models;

public class CustomerType {
    private String key;
    private int consume;
    private int discount;
    private String name;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomerType() {
    }

    public CustomerType(int consume, int discount, String name) {
        this.consume = consume;
        this.discount = discount;
        this.name = name;
    }
}
