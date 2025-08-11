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

## 🤖 Vibe Coding Prompts

### beer-controller-branch (branch name: 3-create-service-layer-and-controller)

```
In the package controllers, create a new Spring MVC controller for the Beer entity. Add operations for create, get by id, and list all. In the package services, create a service interface and implementation. Add methods as needed to support the controller operations using the Spring Data Repository. The controller should only use the service and the service will use the Spring Data JPA repository for persistence operations. 
Create Spring MockMVC tests for the controller operations. Verify tests are passing. 

Finally, add a section in readme.md file for the Junie prompts I provided. Have a sub section for the branch and add the prompt under the sub section
```

### controller-update-delete (branch name: 4-add-controller-endpoints)

```
Inspect the BeerController. Add API endpints for update and delete. Create new service methods. Create additional MockMVC tests for the new API operations. 
Create a new unit test to test all service operations. 
```

#### ✅ Files Added
- `src/test/java/tom/springframework/vibecodingmvc/services/BeerServiceTest.java`

#### ✅ Files Modified
- `README.md`
- `BeerController.java`
- `BeerService.java`
- `BeerServiceImpl.java`
- `BeerControllerTest.java`


## Using Junie

- New contributors: start with the project guidelines at [.junie/guidelines.md](.junie/guidelines.md) for structure, commands, and best practices.
- To use Junie in IntelliJ IDEA, read the official guide: https://www.jetbrains.com/guide/ai/article/junie/intellij-idea/
- When writing prompts for Junie:
  - Be explicit about files, packages, and tests to create/modify.
  - Keep changes minimal and scoped; follow Controller → Service → Repository layering.
  - After changes, run `mvn test` and review diffs before committing.
