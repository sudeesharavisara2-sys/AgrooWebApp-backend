package com.agroo.agroo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberRequest {
    private Long userId;
    private List<Long> userIds;  // For adding multiple members
}