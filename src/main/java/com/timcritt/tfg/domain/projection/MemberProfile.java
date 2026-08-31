package com.timcritt.tfg.domain.projection;

public class MemberProfile {
    private final Long userId;
    private Long version;
    private String firstName;
    private String lastName;

    public MemberProfile(Builder builder) {
        if(builder.userId == null)
            throw new NullPointerException("builder.userId cannot be null");

        this.userId = builder.userId;
        this.version = builder.version == null ? 0L : builder.version;

        updateProfile(
                builder.firstName,
                builder.lastName
        );
    }
    public static Builder builder() {
        return new Builder();
    }

    public void updateProfile(String firstname, String lastname) {

        if (firstName != null) {
            if(firstname.isBlank()) {
                throw new IllegalArgumentException("firstname cannot be blank");
            }
            this.firstName = firstname;
        }

        if (lastname != null) {
            if(lastname.isBlank()) {
                throw new IllegalArgumentException("lastname cannot be blank");
            }
            this.lastName = lastname;
        }
    }

    public void updateVersion(Long version) {
        if (version == null) {
            throw new IllegalArgumentException("version is required");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version cannot be negative");
        }
        this.version = version;
    }

    public static class Builder {
        private Long userId;
        private Long version;
        private String firstName;
        private String lastName;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public Builder version(Long version) {
            this.version = version;
            return this;
        }
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        public MemberProfile build() {
            return new MemberProfile(this);
        }
    }

    public Long getMemberId() { return userId; }
    public Long getVersion() {return version;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}


}
