package com.ban.appeal.dto;

public record DiscordUser(
    String id,
    String username,
    String avatar,
    String discriminator,
    int public_flags,
    int flags,
    String locale,
    Boolean mfa_enabled,
    int premium_type
) {}