package mx.edu.itcelaya.ecommerceproducts;

/**
 * Created by niluxer on 5/16/16.
 */
public class Products {
    private int id;
    private String title;
    private String type;
    private double price;
    private String imageUrl;

    public Products(int _id, String _title, String _type, double _price, String _imageUrl)
    {
        id = _id;
        title = _title;
        type = _type;
        price = _price;
        imageUrl = _imageUrl;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
