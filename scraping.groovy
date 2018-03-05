import groovy.json.internal.IO
import groovy.sql.Sql
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/*
* Steven Prine
* csc-346
* Prof. Noynaert
*/

def subjectCreate() throws IO {
    def website = "https://aps2.missouriwestern.edu/schedule/Default.asp?tck=201910"
    Document document = Jsoup.connect(website).get()

    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")

    Elements rows = document.select("#subject > option")
    rows.each { row ->
        def abbrvCells = row.attr("value")
        def fullName = row.text()

        if (!abbrvCells.equals("ALL")) {
            def insert = "Insert into Subjects(abbrv, Subject) VALUES(? , ?)"
            def params = [abbrvCells, fullName]
            sql.executeInsert(insert, params)
        }
    }
    println "Subject Table Created Successfully!"
    sql.close()
}

def departmentCreate() throws IO {
    def website = "https://aps2.missouriwestern.edu/schedule/Default.asp?tck=201910"
    Document documentDep = Jsoup.connect(website).get()

    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")

    Elements rows = documentDep.select("#department option")
    rows.each { row ->
        def abbrvCells = row.attr("value")
        def fullName = row.text()

        if (!abbrvCells.equals("ALL")) {
            def insert = "Insert into Departments(ABBRV, Department) VALUES(? , ?)"
            def params = [abbrvCells, fullName]
            sql.executeInsert(insert, params)
        }
    }
    println "Department Table Created Successfully!"
    sql.close()
}

def departmentDelete() throws IO {
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")
    def delete = "DELETE FROM Departments Where EXISTS(Select * from Departments)"
    sql.execute(delete)
    sql.close()
}

def subjectsDelete() throws IO {
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")
    def delete = "DELETE FROM Subjects Where EXISTS(Select * from Subjects)"
    sql.execute(delete)
    sql.close()
}

def printSubjects() throws IO {
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")
    sql.eachRow('SELECT * FROM Subjects') {
        println it
    }
    sql.close()
}

def printDepartments() throws IO {
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")

    sql.eachRow('SELECT * FROM Departments') {
        println it
    }
    sql.close()
}

println "A: Erase and Build Subjects table"
println "B: Erase and Build Departments table"
println "C: Print Subjects table"
println "D: Print Departments table"
println "E: Print the report of disciplines by Department"
println "G: Erase and build sections data"
println "H: Print a simple listing of all sections by department or by discipline"
println "I: Print faculty and faculty schedules  by department"
println "J: Print control-break section report for a department"
println "K: Produce the control-break output"
println "Q: Quit"
println "\t\tPlease Select A Character"

//User input
Scanner input = new Scanner(System.in)
String s = input.next().toLowerCase().trim()
println s

//Menu Controller
switch (s) {
    case "a":
        subjectsDelete()
        subjectCreate(); break
    case "b":
        departmentDelete()
        departmentCreate(); break
    case "c":
        printSubjects();
        break
    case "d":
        printDepartments();
        break
    case "e":
        println "future disciplines print"
        break
    case "quit":
        println "System is now exiting"
        break
    default:
        println "Entry Not Accepted Now Exiting"
}
