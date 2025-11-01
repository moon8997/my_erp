package com.myproject.caseNara.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
    private Integer menuCode;
    private String menuName;
    private Integer displayOrder;
    private String endpoint;
    private Integer parentCode;
}