package com.example.domain.user.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode
public class Operator {
    private String userId;
    private User.Role role;
}
