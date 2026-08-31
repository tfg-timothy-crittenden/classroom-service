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

    public void updateProfile(String firstName, String lastName) {
        if (firstName != null) {
            if (firstName.isBlank()) {
                throw new IllegalArgumentException("firstName cannot be blank");
            }
            this.firstName = firstName.trim();
        }

        if (lastName != null) {
            if (lastName.isBlank()) {
                throw new IllegalArgumentException("lastName cannot be blank");
            }
            this.lastName = lastName.trim();
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

    public Long getUserId() { return userId; }
    public Long getVersion() {return version;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}


}
