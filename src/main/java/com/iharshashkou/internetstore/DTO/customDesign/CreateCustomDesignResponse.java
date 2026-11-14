package com.iharshashkou.internetstore.DTO.customDesign;

public record CreateCustomDesignResponse(
        CustomDesignResponse design,
        String guestAccessToken
) {}
