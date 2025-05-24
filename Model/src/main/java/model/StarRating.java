package model;

public enum StarRating {
    OneStar(1),
    TwoStar(2),
    ThreeStar(3),
    FourStar(4),
    FiveStar(5);

    private final int value;

    private StarRating(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
