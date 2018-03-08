/**
    Author      -   Kielan Sullivan
    Program     -   Instructor(CSC 346 MidTerm)
    Description -   Class to hold instructors to better print out the report
 */
class Instructor{
    def instructor
    def days
    def hours

    public Instructor(){
        instructor = ""
        days = ""
        hours = ""
    }

    public Instructor(String i, String d, String h) {
        instructor = i
        days = d
        hours = h
    }

    def addHours(String h) {
        hours += " | " + h
    }

    def addDays(String d) {
        days += " | " +d
    }

    def String toString(){
        return "Instructor: ${instructor} in class on '${days}' AT '${hours}'"
    }

}