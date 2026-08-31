package com.timcritt.tfg.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaterialDetailsTest {

    @Test
    void trimsFieldsWhenBuilt() {
        MaterialDetails details = MaterialDetails.builder()
                .materialId(26L)
                .name("  TOEFL Practice Test 2  ")
                .description("  Updated description  ")
                .part1Title("  Part 1  ")
                .part2Title("  Part 2  ")
                .build();

        assertEquals(26L, details.getMaterialId());
        assertEquals("TOEFL Practice Test 2", details.getName());
        assertEquals("Updated description", details.getDescription());
        assertEquals("Part 1", details.getPart1Title());
        assertEquals("Part 2", details.getPart2Title());
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> MaterialDetails.builder()
                .materialId(26L)
                .name(" ")
                .build());
    }

    @Test
    void rejectsBlankPart1Title() {
        assertThrows(IllegalArgumentException.class, () -> MaterialDetails.builder()
                .materialId(26L)
                .name("TOEFL Practice Test 2")
                .part1Title(" ")
                .build());
    }

    @Test
    void rejectsBlankPart2Title() {
        assertThrows(IllegalArgumentException.class, () -> MaterialDetails.builder()
                .materialId(26L)
                .name("TOEFL Practice Test 2")
                .part2Title(" ")
                .build());
    }
}

