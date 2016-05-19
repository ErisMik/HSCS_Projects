/* 
Eric Mikulin, Computer Science 302
Javascript Frontend
*/

// Main Function, designates the tasks. Is called when button is pressed
function do_search(){
    read_form();  // Read the inputs of the website
    send_to_back();
    $('#searchResults div').remove();  // Clean any previous search results (Remove all elements of tag <div> in the search results div)
    add_paper("M14/1/AYJPN/HP2/JPN/TZ0/XX", [1,3,6,7]);  // Print the new search results to the div
}

// Function reads the data from the HTML form (not a form per say)
function read_form(){
    // window.alert("You pressed the search button!?");  // Debug Window

    var subject = $('#subjectSelect').val();  // Get the value of the selection box (This one is for the subject)
    console.log(subject);  // Print the results to console for debug
    var parts = $('#partsSelect input[name="searchType"]:checked').val();  // Get the value of the checked boxes (This one is for the search type)
    console.log(parts);  // Print the results to console for debug
    var years = $('#yearSelect input[name="searchType"]:checked').val();  // Get the value of the checked boxes (This one is for the years to search in)
    console.log(years);  // Print the results to console for debug
    var search = $('#inputSelect').val();  // Get the value of the text they entered in to search
    console.log(search);  // Print the results to console for debug

}

// Send to backend
function send_to_back(){
    // NOTHING WORKS ARGAGGTGARG GGRAGR GGR GAGARGRGAGGARGARG
}

// Adds the paper as an element to the search results div
function add_paper(name, numbers){
    // Format the input as facy HTML stuffs
    var txt =   "<div class=\"resultDiv\">" +
                    "<p> Paper Name: "+ name +" </p>" +
                    "<p> Question Number(s): "+ numbers.join(", ") +"</p>" +
                    "<a href=\"index.html\"> Link to Paper</a>" +
                "</div>"

    $("#searchResults").append(txt)  // Append the HTML code generated above to the div that shows the search results
}