package com.recipeapp.recipemanager.config;

import com.recipeapp.recipemanager.model.Category;
import com.recipeapp.recipemanager.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner loadCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) { // Only add if empty
                categoryRepository.saveAll(List.of(
                        new Category(null, "Greek"),
                        new Category(null, "Italian"),
                        new Category(null, "Mexican"),
                        new Category(null, "Indian"),
                        new Category(null, "Chinese"),
                        new Category(null, "American"),
                        new Category(null, "French"),
                        new Category(null, "Japanese"),
                        new Category(null, "Thai"),
                        new Category(null, "Spanish"),
                        new Category(null, "Mediterranean"),
                        new Category(null, "Korean"),
                        new Category(null, "Vietnamese"),
                        new Category(null, "Turkish"),
                        new Category(null, "Lebanese"),
                        new Category(null, "Brazilian"),
                        new Category(null, "Caribbean"),
                        new Category(null, "African"),
                        new Category(null, "German"),
                        new Category(null, "Russian"),
                        new Category(null, "British")
                ));
                System.out.println("Preloaded categories into the database!");
            }
        };
    }
}
