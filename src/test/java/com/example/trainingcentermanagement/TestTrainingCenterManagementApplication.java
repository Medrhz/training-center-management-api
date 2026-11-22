package com.example.trainingcentermanagement;

import org.springframework.boot.SpringApplication;

public class TestTrainingCenterManagementApplication {

   public static void main (String[] args) {
      SpringApplication
          .from(TrainingCenterManagementApplication::main)
          .with(TestcontainersConfiguration.class)
          .run(args);
   }

}
