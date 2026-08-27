package com.agroo.agroo.dto.request;

import com.agroo.agroo.model.enums.LikeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {
    private LikeType likeType = LikeType.LIKE;
}