package com.webgis.admin.dto.user;

public record UserSummaryDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email
) {}