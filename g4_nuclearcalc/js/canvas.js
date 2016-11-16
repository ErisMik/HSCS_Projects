/*Created By Eric Mikulin*/
//Declare Variables - Constants
var DEFAULT_SIZE = 10;
var DEFAULT_RANGE = 100;
var DEFAULT_WIND = 10;

//Declare Variables - Recieved
var size;
var range;
var wind;

var gammaModifier;
var neutronModifier;
var thermalModifier;
var pressureModifier;
var falloutModifier;

//Declare Variables - Calculated
var gammaSource;
var gammaRadiationSafe;

var neutronSource;
var neutronRadiationSafe;

var thermalSource;
var thermalRadiationSafe;

var pressureSource;
var pressureRadiationSafe;

var falloutSource;
var falloutMap = {
        thirty: {width: 0, length: 0, dose: 30},
        ten: {width: 0, length: 0, dose: 10},
        three: {width: 0, length: 0, dose: 3},
        one: {width: 0, length: 0, dose: 1},
        half: {width: 0, length: 0, dose: 0.5}
};
var falloutArea = {width: 0, length: 0};

var minSafeDistance;


//This is the default function that calls the other functions (This function is called from the compute button)
function draw(){
        calculate(); //Do all the calculations

        clearOne(); //Clear canvas one
        animateOne(); //Draw canvas one

        clearTwo(); //Clear Canvas two
        animateTwo(); //Draw canvas two

        sendData(); //Update HTML data elements
}

//This function holds the text for tracking and updating the lines and text data for the first canvas
function animateOne(){
        //Connect this script to the canvas
        var canvas = document.getElementById('nukeCanvas');
        var context = canvas.getContext('2d');

        //This function updates the canvas with the background and lines at the point where the mouse is
        function writeMessage(canvas, message) {
                //link canvas to its context
                var context = canvas.getContext('2d');
                context.clearRect(0, 0, canvas.width, canvas.height);
                
                //Update the background of the canvas
                canvasOne();

                //Draw vertical line
                context.beginPath();
                context.moveTo(message, 0);
                context.lineTo(message, canvas.height);
                context.strokeStyle = 'black';
                context.lineWidth = 2;
                context.stroke();

                var neg = 5; //Set offset

                //If mouse crosses over 80% of the field, flip the side the text is on so it doesnt get removed
                if (message > canvas.width * 0.8){
                        neg = -225; //new offset from message
                }

                //Display Current Range
                context.font = '18pt Calibri';
                context.fillStyle = 'black';
                context.fillText("Range: " + distance(message) + "km", message + neg, 25);

                //Display gamma radiation amount at distance
                context.font = '18pt Calibri';
                context.fillStyle = 'black';
                context.fillText( "Gamma: " + gammaSprd(distance(message)) + "Gy", message + neg, canvas.height/8);

                //Display neutron radiation amount at distance
                context.font = '18pt Calibri';
                context.fillStyle = 'black';
                context.fillText( "Neutron: " + neutronSprd(distance(message)) + "Gy", message + neg, 3*canvas.height/8);

                //Display thermal radiation amount at distance
                context.font = '18pt Calibri';
                context.fillStyle = 'black';
                context.fillText( "Temp: " + thermalSprd(distance(message)) + "K", message + neg, 5*canvas.height/8);

                //Display overpressure amount at distance
                context.font = '18pt Calibri';
                context.fillStyle = 'black';
                context.fillText( "Pressure: " + pressureSprd(distance(message)) + "kPa", message + neg, 7*canvas.height/8);
      }

      //Grabs mouse position, returns array
      function getMousePos(canvas, evt) {
                var rect = canvas.getBoundingClientRect();
                return {
                        x: evt.clientX - rect.left,
                        y: evt.clientY - rect.top
                };
      }

      //adds an event listner to the canvas that waits for the mouse to move, then updates the canvas
      canvas.addEventListener('mousemove', function(evt) {
                var mousePos = getMousePos(canvas, evt);
                var message = mousePos.x;
                //Updates canvas
                writeMessage(canvas, message);
      }, false);
}

function canvasOne(){
        //Connect this script to the canvas
        var canvas = document.getElementById('nukeCanvas');
        var context = canvas.getContext('2d');

        //Gamma radiation
        drawSpread(canvas, context, canvas.height/8, gammaRadiationSafe);
        //Nuetron Radiation
        drawSpread(canvas, context, 3*canvas.height/8, neutronRadiationSafe);
        //Thermal
        drawSpread(canvas, context, 5*canvas.height/8, thermalRadiationSafe);
        //Pressure
        drawSpread(canvas, context, 7*canvas.height/8, pressureRadiationSafe);
}

function drawSpread(canvas, context, order, matrix){
        //Draw first line 0% Survive
        context.beginPath();
        context.moveTo(0, order);
        context.lineTo(canvas.width, order);
        context.strokeStyle = 'red';
        context.lineWidth = canvas.height/4;
        context.stroke();

        //Draw second line 25% Survive
        context.beginPath();
        context.moveTo(matrix.quarter, order);
        context.lineTo(canvas.width, order);
        context.strokeStyle = 'orange';
        context.lineWidth = canvas.height/4;
        context.stroke();

        //Draw second line 50% Survive
        context.beginPath();
        context.moveTo(matrix.half, order);
        context.lineTo(canvas.width, order);
        context.strokeStyle = 'yellow';
        context.lineWidth = canvas.height/4;
        context.stroke();

        //Draw second line 75% Survive
        context.beginPath();
        context.moveTo(matrix.threeQuart, order);
        context.lineTo(canvas.width, order);
        context.strokeStyle = 'green';
        context.lineWidth = canvas.height/4;
        context.stroke();

        //Draw second line 100% Survive
        context.beginPath();
        context.moveTo(matrix.all, order);
        context.lineTo(canvas.width, order);
        context.strokeStyle = "#0066AA";
        context.lineWidth = canvas.height/4;
        context.stroke();
}

//This function holds the text for tracking and updating the lines and text data for the second canvas
function animateTwo(){
        //Connect this script to the canvas
        var canvas = document.getElementById('falloutCanvas');
        var context = canvas.getContext('2d');

        //Updates the canvas every mouse click
        function writeMessage(canvas, mousePos) {
                //Link Context
                var context = canvas.getContext('2d');

                //Clear the canvas
                clearTwo();
                
                //Draw Background
                canvasTwo();

                //Draw Vertical Line at x-position of mouse
                context.beginPath();
                context.moveTo(mousePos.x, 0);
                context.lineTo(mousePos.x, canvas.height);
                context.strokeStyle = 'black';
                context.lineWidth = 2;
                context.stroke();

                //Draw Horizontal Line at y-position of mouse
                context.beginPath();
                context.moveTo(0, mousePos.y);
                context.lineTo(canvas.width, mousePos.y);
                context.strokeStyle = 'black';
                context.lineWidth = 2;
                context.stroke();

                var neg = 5; //Set offset

                //If mouse crosses over 80% of the field, flip the side the text is on so it doesnt get removed
                if (mousePos.x > canvas.width * 0.85){
                        neg = -150; //new offset from message
                }

                //Draw Range (text) on the vertical line
                context.font = '18pt Calibri';
                context.fillStyle = 'black';
                context.fillText("Range: " + distance(mousePos.x) + "km", mousePos.x + neg, 25);

                //Draw Grays (text) on horizontal line
                context.font = '18pt Calibri';
                context.fillStyle = 'black';
                context.fillText("Fallout: " + falloutDoseRate(mousePos.x, mousePos.y) + "Gy/hour", mousePos.x + (3 * neg / 2), mousePos.y + 25);
      }

      //Grabs mouse position, returns array
      function getMousePos(canvas, evt) {
                var rect = canvas.getBoundingClientRect();
                return {
                        x: evt.clientX - rect.left,
                        y: evt.clientY - rect.top
                };
      }

      //adds an event listner to the canvas that waits for the mouse to move, then updates the canvas
      canvas.addEventListener('mousemove', function(evt) {
                var mousePos = getMousePos(canvas, evt);
                var message = mousePos;
                //Updates canvas
                writeMessage(canvas, message);
      }, false);

      function falloutDoseRate(x, y){
        return Math.max(inRange(x,y,falloutMap.thirty), inRange(x,y,falloutMap.ten), inRange(x,y,falloutMap.three), inRange(x,y,falloutMap.one), inRange(x,y,falloutMap.half));
        function inRange(x, y, cloud){
                //Connect this script to the canvas
                var canvas = document.getElementById('falloutCanvas');
                var context = canvas.getContext('2d');

                var dose = 0;
                if ((y >= ((canvas.height/2) - (cloud.width*9))) && (y <= ((canvas.height/2) + (cloud.width*8))) && (x <= cloud.length*9)){
                        dose = cloud.dose;
                }
                return dose;
        }
      }
}

//Updates background of canvasTwo
function canvasTwo(){
        //Connect this script to the canvas
        var canvas = document.getElementById('falloutCanvas');
        var context = canvas.getContext('2d');

        //Draw each of the plumes
        curve(0.5, falloutMap.thirty);
        curve(1, falloutMap.ten);
        curve(2, falloutMap.three);
        curve(4, falloutMap.one);
        curve(5, falloutMap.half);

        //Draw cloud (bezier curves)
        function curve(i, dosage) {
                //Find cloud size
                var cloud = fallout(i);
                //Call the function to draw
                drawCloud(canvas, context, cloud.length, cloud.width, cloud.shape);
                //Calculate area for mouse hover
                dosage.width = distance(cloud.width);
                dosage.length = distance(cloud.length);
        }
}

//Draws the curves (fallout), these are horizontal bezier curves
function drawCloud(canvas, context, length, width, shape){
        //Show the points here! Just looks cleaner this way m'kay
        //The first ones are half width from center of canvas, the second ones are the smae in the opposite direction
        //the control points are four times the distance to form a better shape
        var y1 = (canvas.height/2) - (width/2);
        var cpy1 = (canvas.height/2) - (shape*width);
        var cpy2 = (canvas.height/2) + (shape*width);
        var y2 = (canvas.height/2) + (width/2);

        //draw elipse (quadratic technically)
        context.beginPath();
        context.moveTo(0, y1);
        context.bezierCurveTo(length, cpy1, length, cpy2, 0, y2); //context.bezierCurveTo(cp1x,cp1y,cp2x,cp2y,x,y);

        //Define the stroke style
        context.strokeStyle = 'Black';
        context.lineWidth = 1;
        context.stroke();
}

//Clears canvas one
function clearOne(){
        //Link to the canvas
        var canvas = document.getElementById('nukeCanvas');
        var context = canvas.getContext('2d');

        //clear a rectangle over the whole canvas
        context.clearRect (0 , 0 , canvas.width, canvas.height);
}

//Clears canvas two
function clearTwo(){
        //Link to the canvas
        var canvas = document.getElementById('falloutCanvas');
        var context = canvas.getContext('2d');

        //clear a rectangle over the whole canvas
        context.clearRect (0 , 0 , canvas.width, canvas.height);
}

//Calculate all the data that we need thats not called by the canvas or is global
function calculate(){
        //Grab the values from the html form elements, if they did not enter in a piece of data (i.e. length = 0) revert to the default
        size = (document.getElementById('size').value.length>0) ? document.getElementById('size').value : DEFAULT_SIZE;
        range = (document.getElementById('range').value.length>0) ? document.getElementById('range').value : DEFAULT_RANGE;
        wind = (document.getElementById('wind').value.length>0) ? document.getElementById('wind').value : DEFAULT_WIND;

        //Figures out what warhead type is picked (all temporary values) by checking wich radio button is selected, else revert to default
        if (document.getElementById('typeFission').checked){
                gammaModifier = 1;
                neutronModifier = 1;
                thermalModifier = 1;
                pressureModifier = 1;
                falloutModifier = 1;
        }
        else if (document.getElementById('typeFusion').checked){
                gammaModifier = 1;
                neutronModifier = 1;
                thermalModifier = 1;
                pressureModifier = 1;
                falloutModifier = 1;
        }
        else if (document.getElementById('typeNeutron').checked){
                gammaModifier = 1;
                neutronModifier = 1;
                thermalModifier = 1;
                pressureModifier = 1;
                falloutModifier = 1;
        }
        else {
                gammaModifier = 1;
                neutronModifier = 1;
                thermalModifier = 1;
                pressureModifier = 1;
                falloutModifier = 1;
        }

        //calculation for sources 
        gammaSource = size * 135; //Source for gamma (megatons * 135 (Not sure if correct)
        neutronSource = size * 135; //Source for neutron radiation (not sure if correct)
        thermalSource = size*0.35*1000000000000000; //Source for thermal spread (In joules). Converts to percent of TNT equiv, then to joules. (equation not working)
        pressureSource = size * 3000; //Source for pressure spread (Not sure if correct)
        falloutSource = size; //source for fallout (temporary)

        //Distances where certain percentages survive for gamma radiation (Sorta related to Michelle's Data), grays
        //The numbers are the number of ______ of the particular unit to survive that percentage
        gammaRadiationSafe = {
                zero: gammaSprdInverse(20),
                quarter: gammaSprdInverse(7), 
                half: gammaSprdInverse(5), 
                threeQuart: gammaSprdInverse(3),
                all: gammaSprdInverse(0.5),
        };

        //Distances where certain percentages survive for neutron radiation (Based off gamma radiation), grays
        neutronRadiationSafe = {
                zero: neutronSprdInverse(20),
                quarter: neutronSprdInverse(7), 
                half: neutronSprdInverse(5), 
                threeQuart: neutronSprdInverse(3),
                all: neutronSprdInverse(0.5),
        };

        //Distances where certain percentages survive for thermal radiation (Figured this out myself), kelvin
        thermalRadiationSafe = {
                zero: thermalSprdInverse(400),
                quarter: thermalSprdInverse(350), 
                half: thermalSprdInverse(336), 
                threeQuart: thermalSprdInverse(323),
                all: thermalSprdInverse(310),
        };

        //Distances where certain percentages survive for overpressure, kpa (based off of book)
        pressureRadiationSafe = {
                zero: pressureSprdInverse(50),
                quarter: pressureSprdInverse(20), 
                half: pressureSprdInverse(15), 
                threeQuart: pressureSprdInverse(10),
                all: pressureSprdInverse(7.5),
        };

        minSafeDistance = Math.max(gammaRadiationSafe.all, neutronRadiationSafe.all, thermalRadiationSafe.all, pressureRadiationSafe.all); //find out who will survive longest

        //Calculate the area effected by the fallout
        var cloud = fallout(5);
        falloutArea.width = distance(cloud.width);
        falloutArea.length = distance(cloud.length);
}

//Calculates the actual distance based on how far along the canvas the mouse is, mapped and stuff
function distance(canvasWidth){
        //Link to the canvas (They are both the same width, though this should be fixed)
        var canvas = document.getElementById('falloutCanvas');

        //Find the ratio between them, then divide by ratio
        var ratio = canvas.width / range;
        var x = canvasWidth / ratio;
        x = Math.floor(x * 10) / 10; //Truncate to make it look nicer
        return x;
}

//Calculates the actual distance based on how far along the canvas the mouse is, mapped and stuff
function distanceInverse(distance){
        //Link to the canvas (They are both the same width, though this should be fixed)
        var canvas = document.getElementById('falloutCanvas');

        //Find the ratio between them, then divide by ratio
        var ratio = canvas.width / range;
        var x = distance * ratio;
        return x;
}

//Calculates the intensity of the gamma radiation at a distance
function gammaSprd(distance){
        var grays = (gammaSource/(distance * distance));
        grays = grays.toExponential(3); //Make it scientific Notation
        return grays;
}

//Calculates the distance a certain amount of gamma Radiation is at (Inverse of the above function gammaSprd(distance))
function gammaSprdInverse(intens){
        var distance = Math.sqrt(gammaSource/intens);
        distance = distanceInverse(distance);
        return distance;
}

//Calculates the intensity of the neutron radiation at a distance
function neutronSprd(distance){
        var grays = (neutronSource/(distance * distance * distance));
        grays = grays.toExponential(3); //Make it scientific Notation
        return grays;
}

//Calculates the distance a certain amount of neutron Radiation is at (Inverse of the above function neutronSprd(distance))
function neutronSprdInverse(intens){
        var distance = Math.pow(neutronSource/intens, 1/3);
        distance = distanceInverse(distance);
        return distance;
}

//Calculates the intensity of the thermal radiation at a distance
function thermalSprd(distance){
        var kelvin = (thermalSource)/(Math.pow(distance*100000,2));
        if (kelvin < 300){kelvin = 300;}
        kelvin = kelvin.toExponential(3); //Make it scientific Notation
        return kelvin;
}

//Calculates the distance a certain amount of thermal Radiation is at (Inverse of the above function thermalSprd(distance))
function thermalSprdInverse(intens){
        var distance = Math.sqrt(thermalSource/intens) / 100000;
        distance = distanceInverse(distance);
        return distance;
}

//Calculates the intensity of the overpressure at a distance
function pressureSprd(distance){
        var kPa = pressureSource / (6 * distance * distance) ;
        if (kPa < 101.325){kPa = 101.325;}
        kPa = kPa.toExponential(3); //Make it scientific Notation
        return kPa;
}

//Calculates the distance a certain amount of overpressure is at (Inverse of the above function pressureSprd(distance))
function pressureSprdInverse(intens){
        var distance = Math.sqrt(pressureSource / (6*intens));
        distance = distanceInverse(distance);
        return distance;
}

//Calculates the fallout at a time
function fallout(time){
        //Make the plume at a time
        var plume = {
                width: falloutSource * time * size / 4, //Placeholder
                length: falloutSource * time * (wind +1) * size / 4, //Placeholder
                shape: 2 //distance the control points are away
        };

        if (wind == 0){plume.shape = 1;} //Gotta get that nice shape

        return plume;
}

//Updates the HTML in the data section to accuretly show all the calculated data
function sendData(){
        document.getElementById('dRange').innerHTML = range;
        document.getElementById('dSize').innerHTML = size;
        document.getElementById('dWind').innerHTML = wind;
        document.getElementById('dPeakGammaRad').innerHTML = gammaSource;
        document.getElementById('dPeakNeuRad').innerHTML = neutronSource;
        document.getElementById('dPeakTemp').innerHTML = thermalSource.toExponential(1);
        document.getElementById('dPeakPressure').innerHTML = pressureSource;
        document.getElementById('dFalloutArea').innerHTML = Math.floor(falloutArea.width * falloutArea.length * 10) / 10; //Bugged, requires a calc, then a render, then a calc to get the correct information :(
        document.getElementById('dSafeDistance').innerHTML = distance(Math.floor(minSafeDistance * 10) / 10); //Truncate for swag
}
