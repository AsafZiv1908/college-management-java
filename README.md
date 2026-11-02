# College Management — Java

A Java console application for managing a college's lecturers, departments and committees. Academic project by Asaf Ziv and Iness Goldes.

## Features

- Lecturer, doctor and professor models with inheritance.
- Departments, committee membership and chairman assignment.
- Salary averages and comparisons of lecturers and committees.
- Custom exceptions and local serialization for saving college data.

## Run

Install a JDK (17 or newer), then open PowerShell in this repository:

```powershell
.\run.ps1
```

The script compiles the source and launches `college.management.CollegeManagementApp`. Enter a college name and use the numbered menu; choose `0` to save and exit. Data is saved as `<college-name>.ser` in the current working directory and is excluded from Git.

To compile only, run `.\build.ps1`. No external Java libraries are required.

## Repository structure

- `src/college/management/`: application and domain classes.
- `src/college/management/exceptions/`: domain exceptions.
- `build.ps1` and `run.ps1`: Windows build and launch scripts.

## Preparation notes

The original personal package and entry-point names were replaced with descriptive names. Author credits are retained; personal identity numbers were removed from source comments. Existing serialized data from the original package is not compatible with the renamed Java classes; start with a fresh data file. This repository contains the original file-based version; the PostgreSQL extension is maintained separately in `college-management-database`.
