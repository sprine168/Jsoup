import groovy.json.internal.IO
import groovy.sql.Sql
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException;

/*
* Steven Prine
* csc-346
* Prof. Noynaert
*/

class Subject {

    def subject, abbrv = ""

    Subject(abbrv, subject) {
        this.subject = subject
        this.abbrv = abbrv
    }

    @Override
    String toString() {
        return "$abbrv $subject"
    }
}

class Department {

    def abbrv, department

    public Department(abbrv, department) {
        this.abbrv = abbrv
        this.department = department
    }

    @Override
    String toString() {
        return "$abbrv $department"
    }
}

//Scraping Subjects
def subjectSearch() throws IO {
    def website = "https://aps2.missouriwestern.edu/schedule/Default.asp?tck=201910"
    Document document = Jsoup.connect(website).get()

    Elements rows = document.select("#subject > option[value]")
    rows.remove(0)
    def abbrv
    def subject
    Subject subjects

    def data = []

    rows.each { row ->
        def subjectCells = row.select("option[value]")
        subject = subjectCells.text()

        def abbrvCells = row.attr("value")
        abbrv = abbrvCells

        subjects = new Subject(abbrv, subject)
        data.add(subjects)
    }

    //Will eventually be entering data here programatically to subject table
    data.each {
        println it
    }
}

//Scraping Departments
def departmentSearch() throws IO {
    def website = "https://aps2.missouriwestern.edu/schedule/Default.asp?tck=201910"
    Document documentDep = Jsoup.connect(website).get()

    def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")

    Elements rows = documentDep.select("#department option")
    Department departments

    def dataDep = []

    rows.each { row ->
        println row
        def abbrvCells = row.attr("value")
        def fullName = row.text()

        if (!abbrvCells.equals("ALL")) {
            def insert = "Insert into Departments(ABBRV, Department) VALUES(? , ?)"
            def params = [abbrvCells, fullName]
            sql.executeInsert(insert, params)
        }
    }
    sql.close()
}

subjectSearch()
departmentSearch()


