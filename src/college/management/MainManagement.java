package college.management;

import college.management.exceptions.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainManagement {
    private final String dataFile;
    private String collegeName;
    private final List<Lecturer> lecturers = new ArrayList<>();
    private final List<Committee> committees = new ArrayList<>();
    private final List<Department> departments = new ArrayList<>();
    private final Scanner scan = new Scanner(System.in);

    public MainManagement(String collegeName) {
        this.collegeName = collegeName;
        this.dataFile = collegeName + ".ser";
    }

    public void addLecturer() throws InvalidInputException {
        System.out.println("Enter Lecturer's full name:");
        String name = scan.nextLine();

        System.out.println("Enter Lecturer's ID:");
        String id = scan.nextLine();
        if (isLecturerIdExist(id))
            throw new InvalidInputException("Lecturer ID already exists.");

        Lecturer.Degree degree = null;
        while (degree == null) {
            System.out.println("Enter Lecturer's Degree (FIRST, SECOND, DR, PROFESSOR):");
            String inp = scan.nextLine().toUpperCase();
            try { degree = Lecturer.Degree.valueOf(inp); }
            catch (IllegalArgumentException e) { throw new InvalidInputException("Invalid degree value."); }
        }

        System.out.println("Enter Lecturer's Degree Name:");
        String degreeName = scan.nextLine();

        System.out.println("Enter Lecturer's wage:");
        int wage = scan.nextInt();
        if (wage <= 0) throw new InvalidInputException("Wage must be positive.");
        scan.nextLine();

        System.out.println("Enter Lecturer's Department (or leave empty):");
        String department = scan.nextLine();
        if (department.isEmpty()) department = null;

        Lecturer lec;
        if (degree == Lecturer.Degree.PROFESSOR) {
            System.out.println("Enter Professorship Institute:");
            String inst = scan.nextLine();
            Professor p = new Professor(id, name, inst, new ArrayList<>());
            addArticlesLoop(p);
            p.setWage(wage);
            p.setDepartment(department);
            p.setDegree(degree);
            p.setDegreeName(degreeName);
            lec = p;
        } else if (degree == Lecturer.Degree.DR) {
            Doctor d = new Doctor(name, id, degree, degreeName, wage, department);
            addArticlesLoop(d);
            lec = d;
        } else {
            lec = new Lecturer(id, name, department, new ArrayList<>());
            lec.setWage(wage);
            lec.setDegree(degree);
            lec.setDegreeName(degreeName);
        }

        lecturers.add(lec);
        System.out.println("Lecturer added successfully!");
    }


    private void addArticlesLoop(Lecturer l) throws InvalidInputException {
        List<String> arts = (l instanceof Doctor) ? ((Doctor) l).getArticles() :
                (l instanceof Professor) ? ((Professor) l).getArticles() : null;
        if (arts == null) throw new InvalidInputException("Only Doctors/Professors can have articles.");
        String a;
        do {
            System.out.println("Add article? (yes/no)");
            a = scan.nextLine().toLowerCase();
            if (a.equals("yes")) {
                System.out.println("Enter title:");
                arts.add(scan.nextLine());
            } else if (!a.equals("no")) {
                throw new InvalidInputException("Enter yes/no");
            }
        } while (!a.equals("no"));
    }

    public void addDepartment() throws DuplicateCommitteeException {
        System.out.println("Enter Department Name:");
        String name = scan.nextLine();
        if (departments.stream().anyMatch(d -> d.getName().equals(name)))
            throw new DuplicateCommitteeException("Department exists.");
        System.out.println("Enter number of students:");
        int n = scan.nextInt();
        scan.nextLine();
        departments.add(new Department(name, n, new ArrayList<>()));
        System.out.println("Department added.");
    }

    public void addCommittee() throws NoLecturerFoundException, InvalidDegreeException, DuplicateCommitteeException, InvalidInputException {
        System.out.println("Enter Committee Name:");
        String name = scan.nextLine();
        if (isCommitteeExist(name)) throw new DuplicateCommitteeException("Committee exists.");

        System.out.println("Enter Chairman ID:");
        String cid = scan.nextLine();
        Lecturer chair = findLecturerById(cid);
        if (chair == null) throw new NoLecturerFoundException("Lecturer not found.");
        if (!isDoctorOrProfessor(chair)) throw new InvalidDegreeException("Chairman must be DR/PROFESSOR.");

        System.out.println("Enter required degree (FIRST, SECOND, DR, PROFESSOR):");
        Lecturer.Degree req;
        try {
            req = Lecturer.Degree.valueOf(scan.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid degree.");
        }

        committees.add(new Committee(name, new ArrayList<>(), chair.getName(), req));
        System.out.println("Committee added.");
    }

    public void addMemberToCommittee() throws NoLecturerFoundException, NoCommitteesFoundException, NoChairmanFoundException, InvalidDegreeException {
        System.out.println("Enter Lecturer ID:");
        Lecturer lec = findLecturerById(scan.nextLine());
        if (lec == null) throw new NoLecturerFoundException("Lecturer not found.");

        System.out.println("Enter Committee Name:");
        Committee com = findCommitteeByName(scan.nextLine());
        if (com == null) throw new NoCommitteesFoundException("Committee not found.");

        if (com.getChairman().equals(lec.getName()))
            throw new NoChairmanFoundException("Chairman already member.");

        if (lec.getDegree() != com.getRequiredDegree())
            throw new InvalidDegreeException("Degree mismatch.");

        if (!com.getLecturers().contains(lec.getName())) com.getLecturers().add(lec.getName());
        if (!lec.getCommittee().contains(com.getName())) lec.getCommittee().add(com.getName());
        System.out.println("Member added.");
    }

    public void updateCommitteeChairman() throws NoCommitteesFoundException, NoLecturerFoundException, InvalidDegreeException, NoChairmanFoundException {
        System.out.println("Enter Committee Name:");
        Committee com = findCommitteeByName(scan.nextLine());
        if (com == null) throw new NoCommitteesFoundException("Committee not found.");

        System.out.println("Enter new Chairman ID:");
        Lecturer lec = findLecturerById(scan.nextLine());
        if (lec == null) throw new NoLecturerFoundException("Lecturer not found.");
        if (!isDoctorOrProfessor(lec)) throw new InvalidDegreeException("Chairman must be DR/PROFESSOR.");
        if (!com.getLecturers().contains(lec.getName()))
            throw new NoChairmanFoundException("New chairman must be member.");

        com.setChairman(lec.getName());
        System.out.println("Chairman updated.");
    }

    public void removeCommitteeMember() throws NoCommitteesFoundException, NoLecturerFoundException, NoChairmanFoundException {
        System.out.println("Enter Committee Name:");
        Committee com = findCommitteeByName(scan.nextLine());
        if (com == null) throw new NoCommitteesFoundException("Committee not found.");

        System.out.println("Enter Lecturer ID:");
        Lecturer lec = findLecturerById(scan.nextLine());
        if (lec == null) throw new NoLecturerFoundException("Lecturer not found.");

        if (com.getChairman().equals(lec.getName()))
            throw new NoChairmanFoundException("Cannot remove chairman.");

        if (!com.getLecturers().remove(lec.getName()))
            throw new NoLecturerFoundException("Lecturer not in committee.");

        lec.getCommittee().remove(com.getName());
        System.out.println("Member removed.");
    }

    public void assignLecturerDepartment() throws NoLecturerFoundException, NoDepartmentFoundException, LecturerAlreadyInCommitteeException {
        System.out.println("Enter Lecturer ID:");
        Lecturer lec = findLecturerById(scan.nextLine());
        if (lec == null) throw new NoLecturerFoundException("Lecturer not found.");

        System.out.println("Enter Department Name:");
        String dname = scan.nextLine();
        Department dep = departments.stream().filter(d -> d.getName().equals(dname)).findFirst().orElse(null);
        if (dep == null) throw new NoDepartmentFoundException("Department not found.");

        if (dep.getLecturers().contains(lec.getName()))
            throw new LecturerAlreadyInCommitteeException("Lecturer already in department.");

        dep.getLecturers().add(lec.getName());
        lec.setDepartment(dname);
        System.out.println("Lecturer assigned to department.");
    }

    public void compareCommittees() throws NoCommitteesFoundException {
        System.out.println("Enter the name of the first committee:");
        String c1Name = scan.nextLine();
        System.out.println("Enter the name of the second committee:");
        String c2Name = scan.nextLine();

        Committee c1 = findCommitteeByName(c1Name);
        Committee c2 = findCommitteeByName(c2Name);

        if (c1 == null || c2 == null) {
            throw new NoCommitteesFoundException("One or both committees not found!");
        }

        System.out.println("Compare by: (1) Number of Members OR (2) Number of Articles by Members?");
        int choice = scan.nextInt();
        scan.nextLine();

        CommitteeComparator comparator = new CommitteeComparator(choice, this);
        int result = comparator.compare(c1, c2);

        if (result > 0) {
            System.out.println(c1.getName() + " is larger by the chosen criteria.");
        } else if (result < 0) {
            System.out.println(c2.getName() + " is larger by the chosen criteria.");
        } else {
            System.out.println("The committees are equal by the chosen criteria.");
        }
    }

    public void compareDoctorsByArticles() throws NoDoctorsFoundException {
        DrProfessorCompare comparator = new DrProfessorCompare();
        Doctor topDoctor = null;

        for (Lecturer l : lecturers) {
            if (l instanceof Doctor d) {
                if (topDoctor == null || comparator.compare(d, topDoctor) > 0) {
                    topDoctor = d;
                }
            }
        }

        if (topDoctor == null) {
            throw new NoDoctorsFoundException("No Doctors in the system!");
        }

        int count = topDoctor.getArticles().size();
        System.out.println("Top lecturer is: " + topDoctor.getName() + " with " + count + " articles.");
    }

    public void duplicateCommittee() throws NoCommitteesFoundException {
        System.out.println("Enter committee to duplicate:");
        Committee orig = findCommitteeByName(scan.nextLine());
        if (orig == null) throw new NoCommitteesFoundException("Not found.");
        Committee nc = new Committee("new-" + orig.getName(),
                new ArrayList<>(orig.getLecturers()),
                orig.getChairman(),
                orig.getRequiredDegree());
        committees.add(nc);
        System.out.println("Duplicated: " + nc.getName());
    }

    public void lecturerAvgWage() throws NoLecturerFoundException {
        if (lecturers.isEmpty()) throw new NoLecturerFoundException("No lecturers.");
        double avg = lecturers.stream().mapToInt(Lecturer::getWage).average().orElse(0);
        System.out.println("Average wage: " + avg + "₪");
    }

    public void departmentAvgWage() throws NoLecturerFoundException {
        System.out.println("Enter Department Name:");
        String dn = scan.nextLine();
        List<Lecturer> list = lecturers.stream().filter(l -> dn.equals(l.getDepartment())).collect(Collectors.toList());
        if (list.isEmpty()) throw new NoLecturerFoundException("No lecturers.");
        double avg = list.stream().mapToInt(Lecturer::getWage).average().orElse(0);
        System.out.println("Average wage in " + dn + ": " + avg + "₪");
    }

    public void lecturerInfo() throws NoLecturerFoundException {
        if (lecturers.isEmpty()) throw new NoLecturerFoundException("No lecturers.");
        lecturers.forEach(System.out::println);
    }

    public void committeeInfo() throws NoCommitteesFoundException {
        if (committees.isEmpty()) throw new NoCommitteesFoundException("No committees.");
        committees.forEach(System.out::println);
    }

    public void saveData() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            out.writeObject(lecturers);
            out.writeObject(committees);
            out.writeObject(departments);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData() throws IOException, ClassNotFoundException {
        File f = new File(dataFile);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            lecturers.clear();
            committees.clear();
            departments.clear();
            lecturers.addAll((List<Lecturer>) in.readObject());
            committees.addAll((List<Committee>) in.readObject());
            departments.addAll((List<Department>) in.readObject());
        }
    }

    private boolean isDoctorOrProfessor(Lecturer l) {
        return l instanceof Doctor || l instanceof Professor;
    }

    private boolean isCommitteeExist(String n) {
        return committees.stream().anyMatch(c -> c.getName().equals(n));
    }

    private boolean isLecturerIdExist(String id) {
        return lecturers.stream().anyMatch(l -> l.getId().equals(id));
    }

    private Lecturer findLecturerById(String id) {
        return lecturers.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
    }

    private Committee findCommitteeByName(String n) {
        return committees.stream().filter(c -> c.getName().equals(n)).findFirst().orElse(null);
    }

    public Lecturer findLecturerByName(String name) {
        return lecturers.stream()
                .filter(l -> l.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Doctor findDoctorByName(String name) {
        Lecturer l = findLecturerByName(name);
        return (l instanceof Doctor) ? (Doctor) l : null;
    }

}
