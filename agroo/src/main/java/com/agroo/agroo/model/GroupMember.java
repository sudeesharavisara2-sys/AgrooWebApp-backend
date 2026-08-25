package com.agroo.agroo.model;

import com.agroo.agroo.model.enums.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.MEMBER;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ChatGroup chatGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}