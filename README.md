# College Management in Java

A console application for managing college lecturers, departments and academic committees. The project demonstrates object-oriented modelling, inheritance, collection management, custom exceptions and file persistence in Java.

**Authors:** [Asaf Ziv](https://github.com/AsafZiv1908) and Iness Goldes. Developed as an academic project.

## Features

- Add lecturers, doctors and professors, and assign lecturers to departments.
- Create committees, manage membership and update committee chairpersons.
- Compare research output and committees using dedicated comparators.
- Calculate average salaries for a college or department.
- Save and restore application data using Java object serialization.

## Technology and design

The application uses Java and the standard library, with no external dependencies. `Lecturer`, `Doctor` and `Professor` model the academic staff hierarchy. `Department` and `Committee` model group membership, while `MainManagement` coordinates the console operations. Domain-specific exceptions report invalid input and unsupported operations.

```text
CollegeManagementApp  →  MainManagement
                             ├── Lecturer
                             │     ├── Doctor
                             │     └── Professor
                             ├── Department
                             └── Committee
```

## Run locally

Requirements: **JDK 17 or newer**. The included scripts use Windows PowerShell.

```powershell
git clone https://github.com/AsafZiv1908/college-management-java.git
cd college-management-java
.\run.ps1
```

The script compiles the source and launches `college.management.CollegeManagementApp`. Enter a college name, then select a numbered menu operation. Choose `0` to save and exit.

To compile without launching:

```powershell
.\build.ps1
```

Data is stored in `<college-name>.ser` in the working directory. Saved data and compiled classes are excluded from Git. Use only data files created by your own copy of the application.

## Source map

| Location | Purpose |
| --- | --- |
| [CollegeManagementApp.java](src/college/management/CollegeManagementApp.java) | Entry point and numbered console menu |
| [MainManagement.java](src/college/management/MainManagement.java) | College operations, lookups and persistence |
| [Domain classes](src/college/management/) | Staff hierarchy, departments, committees and comparators |
| [Exceptions](src/college/management/exceptions/) | Domain-specific error types |

## Verification and scope

The project was compiled with JDK 23 during portfolio preparation. Startup, save, reload and exit were checked. These checks do not cover every interactive menu path.

This is an academic console application. The package was renamed to `college.management`, so serialized files from the original package are not compatible with this version.

The companion [PostgreSQL version](https://github.com/AsafZiv1908/college-management-database) extends the same domain with relational storage and JDBC.
