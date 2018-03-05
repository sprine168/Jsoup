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
            def insert = "Insert into Subjects(ABBRV, Subject) VALUES(? , ?)"
            def params = [abbrvCells, fullName]
            sql.executeInsert(insert, params)
        }
    }
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

    sql.eachRow('SELECT * FROM Departments'){
        print it
    }
    sql.close()
}

//a in menu is to create departments table
//b in menu is to create subjects table
println "A: Erase and build Subjects Table\tB:Erase and Build Departments Table\n " +
        "C: Print Departments Tablet\tPrint the report of disciplines by Department\n" +
        "D: Print Departments Table\tPrint the report of disciplines by Department\n" +
        "Erase and build sections data"

def menu

printSubjects()
printDepartments()
