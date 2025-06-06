package ui;

public enum View {
    SIGN_IN("sign-in.fxml", "Sign In"),
    SIGN_UP("sign-up.fxml", "Sign Up"),
    CLIENT_MAIN("client-main-page.fxml", "Main"),
    CLIENT_BUY("buy-page.fxml", "Buy"),
    CLIENT_REVIEW("review-page.fxml", "Review"),
    STOCK_OPERATOR_MAIN("stock-operator-main-page.fxml", "Main"),
    ADMIN_MAIN("admin-main-page.fxml", "Main");

    public final String path;
    public final String title;

    View(String path, String title) {
        this.path = path;
        this.title = title;
    }
}