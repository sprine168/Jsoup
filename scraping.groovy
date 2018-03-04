import groovy.json.internal.IO
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/*
* Steven Prine
* csc-346
* Prof. Noynaert
*/

class Subject {

    def subject, abbrv = ""

    Subject(subject, abbrv) {
        this.subject = subject
        this.abbrv = abbrv
    }

    def getSubject() {
        return subject
    }

    def getAbbrv() {
        return abbrv
    }

   @Override
    String toString() {
        return "Subject: $subject  Abbrv: $abbrv"
    }
}

class Department {

    def abbrv, department

    public Department(abbrv, department) {
        this.abbrv = abbrv
        this.department = department
    }

    def getAbbrv() {
        return abbrv
    }

    void setAbbrv(abbrv) {
        this.abbrv = abbrv
    }

    def getDepartment() {
        return department
    }

    void setDepartment(department) {
        this.department = department
    }

    @Override
    String toString() {
        return "Abbreviation: $abbrv  Department: $department"
    }
}

def subjectSearch() throws IO {
    def website = "https://aps2.missouriwestern.edu/schedule/?tck=201910"
    Document document = Jsoup.connect(website).get()

    title = document.title()
    println "The tite is: $title \n"

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
//        println "$subject  $abbrv"

        subjects = new Subject(subject, abbrv)
        data.add(subjects)
    }

    data.each {
        println it
    }

}

subjectSearch()