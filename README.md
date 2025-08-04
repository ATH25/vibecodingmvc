

# VibeCodingMVC 🎵🚀

This is a Spring Boot project built for experimenting with "vibe coding" — a fun, focused, and creative approach to building modern Java applications. It uses Java 21 and Maven.

## 🔧 Tech Stack

- Java 21
- Spring Boot
- Maven
- Git + GitHub

## 📁 Project Structure

```
vibecodingmvc/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── .gitignore
├── application.properties
├── pom.xml
└── README.md
```

## 🚀 Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/ATH25/vibecodingmvc.git
   cd vibecodingmvc
   ```

2. Build and run the project:
   ```bash
   mvn spring-boot:run
   ```

3. Start vibin' 🎧

## ✨ Project Goals

- Explore Spring Boot's MVC capabilities
- Learn advanced Java 21 features
- Improve productivity with a vibe-first mindset
- Build something fun, meaningful, and technically solid

## 📌 Notes

- This project is part of a learning journey and will evolve over time.
- Contributions welcome once the basics are in place!

## 🤖 Junie Prompts

### beer-controller-branch (branch name: 3-create-service-layer-and-controller)

```
In the package controllers, create a new Spring MVC controller for the Beer entity. Add operations for create, get by id, and list all. In the package services, create a service interface and implementation. Add methods as needed to support the controller operations using the Spring Data Repository. The controller should only use the service and the service will use the Spring Data JPA repository for persistence operations. 
Create Spring MockMVC tests for the controller operations. Verify tests are passing. 

Finally, add a section in readme.md file for the Junie prompts I provided. Have a sub section for the branch and add the prompt under the sub section
```

