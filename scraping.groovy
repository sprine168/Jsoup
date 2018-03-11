import groovy.json.internal.IO
import groovy.sql.Sql
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/*
* Steven Prine
*Kielan Sullivan
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

def disciplinesReport(def s) throws IO{
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")
    sql.eachRow("Select Abbrv, Subject From Subjects WHERE Abbrv = ${s}"){
        println it
    }
}

def breakDepartments(def s) {
    def data = Sql.newInstance("jdbc:sqlite:schedule.db", "org.sqlite.JDBC")
    data.eachRow("SELECT disc AS Department, name AS Title FROM departments WHERE disc LIKE ${s}") {
        println it
    }
    data.close()

    def sql = Sql.newInstance("jdbc:sqlite:schedule.db", "org.sqlite.JDBC")
    sql.eachRow("SELECT disciplines.disc, disciplines.name FROM departments, sections, disciplines WHERE sections.department = departments.disc AND disciplines.disc = sections.discipline AND departments.disc = ${s} GROUP BY disciplines.name") { list ->
        println "\t $list"
    }
    sql.close()


}

//------------------------------Kielan start methods----------------------------
def sectionCreate(String s) {
    GroovyWebScrape.getSections(s)
    println "Section Table Created Successfully for Department ${s}!"

}

def sectionDelete(String s) throws IO {
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")
    def delete = "DELETE FROM Section Where dept like ${s}" // EXISTS(Select * from Section)"
    sql.execute(delete)
    sql.close()
}


def printSections(String s) {
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")

    sql.eachRow("SELECT * FROM Section WHERE dept LIKE ${s} OR discipline LIKE ${s}") {
        println it
    }
    sql.close()
}

//Takes in arguements from the SELECT query in the printInstructors method.
def instructorSchedule(String ins, String days, String hours, ArrayList emp) {


    if (days == "") {
        days = "Online"
    }
    if (hours == "-") {
        hours == "Online"
    }

    def toAdd = 0
    //if the emp list is empty than the first instructor passed is added
    def i = new Instructor(ins, days, hours)
    if (emp.size() == 0) {
        emp.add(i)
    } else {
        //iterates through the list and if the instructor already exists on the list, their days and times are added
        for (Instructor e : emp) {
            if (e.instructor == i.instructor) {
                e.addHours(i.hours)
                if (e.days != i.days) {
                    e.addDays(i.days)
                }
                break
            }
            //if an instructor is not on the list yet, a counter increases as the list is iterated through
            else {
                toAdd++
            }
        }
        //if the instructor did not appear in the list, it is added to the list.
        if (toAdd >= emp.size()) {
            emp.add(i)
        }
    }
}

def printFaculty(String s) {
    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")
    def ins = []
    sql.eachRow("SELECT instructor, days, classTime FROM Section WHERE dept LIKE ${s}") {
        instructorSchedule(it.getAt('instructor'), it.getAt('days'), it.getAt('classTime'), ins)
    }
    ins.each {
        println it
    }

    sql.close()
}
//----------------------------------------End of Kielan's methods

println "A: Erase and Build Subjects table"
println "B: Erase and Build Departments table"
println "C: Print Subjects table"
println "D: Print Departments table"
println "E: Print the report of disciplines by Department"
println "G: Erase and Build Sections data"
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
        println "Enter a Discipline"
        def i = input.next().toUpperCase().trim()
        disciplinesReport(i)
        break

//Kielan
    case "g":
        println "Enter Department for sections"
        i = input.next().toUpperCase().trim()
        sectionDelete(i)
        sectionCreate(i)
        break
    case "h":
        println "Enter Department or Discipline"
        i = input.next().toUpperCase().trim()
        printSections(i)
        break
    case "i":
        println "Enter Department for sections"
        i = input.next().toUpperCase().trim()
        printFaculty(i)
        break
//end of Kielans add

    case "j":
        println "Enter Department for Break Sections"
        def i = input.next().toUpperCase().trim()
        breakDepartments(i)
        break
    case "k":
        def checker = true
        while (checker) {
            println "Enter Department for Break Sections or Type Quit or Q to exit"
            def i = input.next().toUpperCase().trim()
            if (i.trim().toLowerCase() == "quit" || i.toLowerCase().trim() == "q") {
                println "System is exiting"
                System.exit(1)
            }else{
                breakDepartments(i)
            }
        }
    case "quit":
        println "System is now exiting"
        break
//Added this because I would always use q instead of quit.
    case "q":
        println "System is now exiting"
        break
    default:
        println "Entry Not Accepted Now Exiting"
}
