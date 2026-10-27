package com.example.hppmw.model;

public record AccessTokenResponse(
        String token,
        String type,
        String app_id,
        String app_name,
        String time_created,
        Long seconds_to_expire,
        String email
) {
}
