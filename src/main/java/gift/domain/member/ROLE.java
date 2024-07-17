package gift.domain.member;

public enum ROLE {
    ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER");

    private final String description;

    ROLE(String description) {
        this.description = description;
    }
}
