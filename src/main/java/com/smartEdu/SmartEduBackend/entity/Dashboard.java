package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dashboard {
    private int studentsCount;
    private int teachersCount;
    private int parentsCount;
    private int principalsCount;
    private int staffUsersCount;
    private int schoolsCount;
    private int zonalCount;
    private int provinceCount;
}
