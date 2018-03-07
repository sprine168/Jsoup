/*
    Author      -  Kielan Sullivan
    Program     -  CSC 346 Midterm(Jsoup Scrapping feat. Groovy)
    Description -  This program takes in an arguement from the driver class to scrape a web page and create Sections
                   Based on the inforation gathered.
                   Class Section creates a section from the schedule website taking the Department, CourseID, CRN, and
                   other pertinent information.
                   Class Sections is a groovy class that does all the work.  It divides the web page into cells based
                   on the <tr> and <td> of the html code.  It checks class attribute and builds the section based on
                   a class name "list_row" or a class name "detail_row"
 */


import groovy.sql.Sql
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class GroovyWebScrape{

    static def getSections(dept) {
        println "Department $dept"
        Section sec = null
        def baseURL = "https://aps2.missouriwestern.edu/schedule/?tck=201910"
        Connection.Response res = Jsoup.connect(baseURL)
                .timeout(70 * 1000) //bumped timeout to 70 because 60 seconds was timing out at times
                .method(Connection.Method.POST)
                .data("course_number", "")
                .data("subject", "ALL")
                .data("department", dept)
                .data("display_closed", "yes")
                .data("course_type", "ALL")
                .followRedirects(true)
                .execute()

        Document doc = res.parse()
//        Document doc = Jsoup.parse(new File("CSMP.html"), "UTF-8", baseURL)
        def sql = Sql.newInstance("jdbc:sqlite:jsoup.db", "org.sqlite.JDBC")




        Elements rows = doc.select("tr")
        println "Found ${rows.size()} rows"
        def rC = 0

        if (rows.size() > 4) {
            rows.each { row ->

                def className = row.attr("class")

                Elements cells = row.select("td")

                if (className == "list_row") {
                    //this handles the cases where there is multiple class times without printing two sections
                    if (sec != null && sec.term != null) {
                        def insert = "Insert into Section(dept, crn, courseID, discipline, courseNumber, sectionNumber," +
                                " term, " +
                                "classType, days, classTime, room, instructor, beginDate, endDate, hours, availableSeats," +
                                "maximumEnrollment, message, additionalMessage, fees, perWhat, url) VALUES(? , ? , ? , ?" +
                                ", ? , ? , ? , ?, ? , ? , ? , ?, ? , ? , ? , ?, ? , ? , ? , ? , ? ,?)"
                        def params = [sec.department, sec.crn, sec.courseID, sec.discipline, sec.courseNumber,
                                      sec.secNum, sec.term, sec.classType, sec.days, sec.time, sec.room, sec.instructor,
                                        sec.beginDate, sec.endDate, sec.hours, sec.availableSeats, sec.maximumEnrollment,
                        sec.message, sec.additionalMessage, sec.fees, sec.perWhat, sec.webPage]
                        sql.executeInsert(insert, params)
//                        println "Section: $sec"
//                        rC++
                    }

                    def cellCount = cells.size()

                    switch (cellCount) {
                        case 10:

                            sec = new Section()
                            Elements links = cells.select("a[href]")
                            Element link = links.get(0)
                            def absLink = link.attr("href")
                            def toUse = "https://aps2.missouriwestern.edu/schedule/"+absLink
                            sec.webPage = toUse.toString()
                            sec.department = dept
                            sec.crn = cells.get(0).text().trim()
                            sec.courseID = cells.get(1).text().trim()
                            def cN = cells.get(1).text().trim()
                            sec.secNum = cells.get(2).text().trim()
                            sec.discipline = cN.take(3)
                            sec.courseNumber = cN.drop(3)
                            def type = cells.get(3).text().trim().split("\\,")

//                            sec.classType = cells.get(3).text().trim()
                            sec.classType = type[0]
                            sec.hours = cells.get(5).text().trim().toInteger()
                            sec.days = cells.get(6).text().trim()
                            sec.time = cells.get(7).text().trim()
                            sec.room = cells.get(8).text().trim()
                            sec.instructor = cells[9].text().trim()
                            break
                        case 5:
                            sec.room += " | " + cells.get(3).text().trim()
                            sec.time += " | " + cells.get(2).text().trim()
                            sec.days += " | " + cells.get(1).text().trim()
                            break
                        default:
                            println "No way to handle rows with ${cellCount} cells"
                            System.exit(1)
                    }//end of switch
                }//end of list_row

                else if (className == "detail_row") {
                    sec.term = ""
                    Elements tags = row.select("*")
                    tags.each { tag ->
                        className = tag.attr("class").toString()
                        switch (className) {
                            case "course_messages":
                                /*
                                This area gets a little rough.  Definitely can go over this in the future to make better
                                First take the text in the tage and clean it up. Then I get the information on what
                                drives the additional fees. That is the per variable. per can hold flat fee or something
                                 like Credit Hour fee.  I then make an array of the mes variable split on ".". This
                                either gives us a size for mes1 as 2 or 3.  if the size is 2, the message is the first
                                item in the array.  The second message is usually the Additional Fees informtation.
                                Last, the actual fee that will be charged is taken from the array.
                                If the size is 2, the everything else is mostly the same but the additional message is
                                set to "None".
                                 */
                                def mes = tag.text().trim()
                                def per = mes.substring(mes.indexOf("(") + 1, mes.indexOf(")"))
                                def mes1 = mes.split("\\.")
                                if (mes1.size() > 2) {
                                    sec.message = mes1[0]
                                    sec.additionalMessage = mes1[1].substring(0,mes1[1].size()-3)
                                    def fee = mes1[1].drop(mes1[1].size()-2) +"."+ (mes1[2].take(2))
                                    sec.fees = Double.parseDouble(fee)
                                } else {
                                    sec.message = mes1[0].take(mes1[0].size()-3)
                                    def fee = mes1[0].drop(mes1[0].size()-2) +"."+ (mes1[1].take(2))
                                    sec.fees = Double.parseDouble(fee)
                                    sec.additionalMessage = "None"
                                }
                                if (per.length()>8) {
                                    per = per.drop(4)
                                }
                                sec.perWhat = per
                                break
                            case "course_ends":
                                def eD = tag.text()
                                sec.endDate = eD.drop("Course Ends: ".length())
                                break
                            case "course_begins":
                                def bD = tag.text()
                                sec.beginDate = bD.drop("Course Begins: ".length())
                                break
                            case "course_seats":
                                def seats = tag.text()
                                seats.replace("<br />", " ")
                                def parts = seats.split(/\s+/)
                                if (parts.length > 6) {
                                    def max = parts[2]
                                    def av = parts[6]
                                    sec.maximumEnrollment = Integer.parseInt(max)
                                    sec.availableSeats = Integer.parseInt(av)
                                }
                                break
                            case "course_term":
                                sec.term = tag.text().trim()
                                break

                        }
                    }
                }//end of detail_row

            }//end of iterator

        }
        //write to db
//        println "Section: ${sec}"
//        println rC
        sql.close()
    }//end of getSections

}//end of class Sections