package com.zerobase.lms.admin.repository;


import com.zerobase.lms.admin.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
