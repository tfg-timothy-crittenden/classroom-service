package com.timcritt.tfg.domain.projection;

public class MaterialDetails {

    private final Long materialId;
    private Long version;
    private String name;
    private String description;
    private String part1Title;
    private String part2Title;

    private MaterialDetails(Builder builder) {
        if (builder.materialId == null) {
            throw new IllegalArgumentException("materialId cannot be null");
        }

        this.materialId = builder.materialId;
        this.version = builder.version == null ? 0L : builder.version;

        updateDetails(
                builder.name,
                builder.description,
                builder.part1Title,
                builder.part2Title
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public void updateDetails(
            String name,
            String description,
            String part1Title,
            String part2Title
    ) {
        if (name != null) {
            if (name.isBlank()) {
                throw new IllegalArgumentException("name cannot be blank");
            }
            this.name = name.trim();
        }

        if (description != null) {
            this.description = description.trim();
        }

        if (part1Title != null) {
            if (part1Title.isBlank()) {
                throw new IllegalArgumentException("part1Title cannot be blank");
            }
            this.part1Title = part1Title.trim();
        }

        if (part2Title != null) {
            if (part2Title.isBlank()) {
                throw new IllegalArgumentException("part2Title cannot be blank");
            }
            this.part2Title = part2Title.trim();
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

        private Long materialId;
        private Long version;
        private String name;
        private String description;
        private String part1Title;
        private String part2Title;

        public Builder materialId(Long materialId) {
            this.materialId = materialId;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder part1Title(String part1Title) {
            this.part1Title = part1Title;
            return this;
        }

        public Builder part2Title(String part2Title) {
            this.part2Title = part2Title;
            return this;
        }

        public MaterialDetails build() {
            return new MaterialDetails(this);
        }
    }

    public Long getMaterialId() {
        return materialId;
    }
    public Long getVersion() {
        return version;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getPart1Title() {
        return part1Title;
    }
    public String getPart2Title() {
        return part2Title;
    }
    
}