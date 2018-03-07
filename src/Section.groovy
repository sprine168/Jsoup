/*
    Author      -   Kielan Sullivan
    Program     -   Section (CSC 346 Midterm)
    Description -   Creates a Section object to hold all the data parsed from the website
 */
class Section {
    def department
    def crn
    def courseID
    def discipline
    def courseNumber
    def secNum
    def classType
    def term
    def room
    def message
    def additionalMessage
    def fees
    def days
    def time
    def instructor
    def beginDate
    def endDate
    def perWhat
    def hours
    def availableSeats
    def maximumEnrollment
    def webPage

    public Section() {
        message = ""
    }

    def String toString() {
        return "Section{department='${department}', " +
                "CRN='${crn}', " +
                "courseID='${courseID}', " +
                "discipline='${discipline}', " +
                "courseNumber='${courseNumber}', " +
                "section='${secNum}', " +
                "term='${term}', " +
                "classType='${classType}', " +
                "days='${days}', " +
                "time='${time}', " +
                "room='${room}', " +
                "instructor='${instructor}', " +
                "beginDate='${beginDate}', " +
                "endDate='${endDate}', " +
                "hours='${hours}', " +
                "availableSeats='${availableSeats}', " +
                "maximumEnrollment='${maximumEnrollment}', " +
                "message='${message}', " +
                "additionalMessage='${additionalMessage}', " +
                "fees='\$${fees}', " +
                "perWhat='${perWhat}', " +
                "url='${webPage}'}"

    }
}