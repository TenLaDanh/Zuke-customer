package vn.edu.tdc.zuke_customer.data_models;

public class CartDetail {
    private String cartID;
    private String productID;
    private int amount;
    private int totalPrice;
    private String key;

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CartDetail() {
    }

    public CartDetail(String cartID, String productID, int amount, int totalPrice) {
        this.cartID = cartID;
        this.productID = productID;
        this.amount = amount;
        this.totalPrice = totalPrice;
    }
}
