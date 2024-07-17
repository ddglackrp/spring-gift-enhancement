package gift.domain.member;

import gift.domain.Wish;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @OneToMany(mappedBy = "member")
    private List<Wish> wishList = new ArrayList<>();

    public Member() {
    }

    private Member(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.role = ROLE.ROLE_USER;
    }

    public static class Builder {
        private String email;
        private String password;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Member build() {
            return new Member(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public ROLE getRole() {
        return role;
    }

    public List<Wish> getWishList() {
        return wishList;
    }
}
