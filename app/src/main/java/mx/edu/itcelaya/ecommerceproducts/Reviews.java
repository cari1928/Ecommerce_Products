package mx.edu.itcelaya.ecommerceproducts;

/**
 * Created by Radogan on 2016-11-23.
 */

public class Reviews {
    private int id;
    private String created_at;
    private String review;
    private String reviewer_name;
    private String reviewer_email;

    public Reviews(int id, String created_at, String review, String reviewer_name, String reviewer_email) {
        this.id = id;
        this.created_at = created_at;
        this.review = review;
        this.reviewer_name = reviewer_name;
        this.reviewer_email = reviewer_email;
    }

    public int getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getReview() {
        return review;
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public String getReviewer_email() {
        return reviewer_email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setReviewer_name(String reviewer_name) {
        this.reviewer_name = reviewer_name;
    }

    public void setReviewer_email(String reviewer_email) {
        this.reviewer_email = reviewer_email;
    }
}
