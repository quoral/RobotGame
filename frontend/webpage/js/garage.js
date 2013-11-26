/*
	*Author: Mikael Pettersson & Christoffer Wern
	*
	*
	*This file is intended to cover all functions needed for the garage.html-page aswell as some communication
	*with its' child page(the 3d-scene). It loads some needed variables as soon as the page is loaded, to keep
	*track of the users' robot parts.
	*
*/

$(document).ready(function() {

	addComponents();
	
	//TODO - fix so this works with correct variable names, maybe make a function with an array 
	//that contains {'chassi', 'wheels' ......} etc.
	$("#weapon").droppable({
		accept: ".weapon",
		drop: function (event, ui) {
            if (ui.draggable.is('.dropped')) return false;
			$("#weapon").text("");
			parent.components[2] = ui.draggable[0].id;
			parent.actionNodes.push("weaponButton");
            $("#weapon").append("<img src='Style/"+ui.draggable[0].id+".jpg' height='100px' width='100px'>");
			document.getElementById("robot3D").contentWindow.hasChanged(ui.draggable[0].id);
		}
	});
	
	$("#chassi").droppable({
		accept: ".chassi",
		drop: function (event, ui) {
            if (ui.draggable.is('.dropped')) return false;
			$("#chassi").text("");
			parent.components[0] = ui.draggable[0].id;
			parent.maxNrOfSensorNodes = 3;
			//to show continuation with other chassis.
			//if(ui.draggable[0].id == "chassi2") parent.maxnrOfSensorNodes = 5;
            $("#chassi").append("<img src='Style/"+ui.draggable[0].id+".jpg' height='100px' width='100px'>");
			document.getElementById("robot3D").contentWindow.hasChanged(ui.draggable[0].id);
        }
	});
	
	$("#wheels").droppable({
		accept: ".wheels",
		drop: function (event, ui) {
            if (ui.draggable.is('.dropped')) return false;
			$("#wheels").text("");
			parent.actionNodes.push("wheelButton");
			parent.components[1] = ui.draggable[0].id;
            $("#wheels").append("<img src='Style/"+ui.draggable[0].id+".jpg' height='100px' width='100px'>");
        }
	});
	
	$( ".chassi").draggable({
        appendTo: "body",
        helper: "clone"
    });
	
	$( ".weapon").draggable({
        appendTo: "body",
        helper: "clone"
    });
	
	$( ".wheels").draggable({
        appendTo: "body",
        helper: "clone"
    });
	
	//Hover Functionality for part information
	/*******WEAPONS*******/
	$('#Laser2_2').hover(
		function(){
			$("#robotView").text("");
			$("#robotView").append("<p>This is your standard futuristic awesome lazer. It shoots huge holes in the enemy armor and is awesome. </p>");
	});
	$('#weapon2').hover(
		function(){
			$("#robotView").text("");
			$("#robotView").append("<p>PLACEHOLDER</p>");
	});
	
	/*******CHASSIS*******/
	$('#TankGroup2').hover(
		function(){
			$("#robotView").text("");
			$("#robotView").append("<p>This is your standard robot chassi.</p>");
	});
	$('#chassi2').hover(
		function(){
			$("#robotView").text("");
			$("#robotView").append("<p>PLACEHOLDER</p>");
	});
	
	/*******MOBILITY*******/
	$('#wheels1').hover(
		function(){
			$("#robotView").text("");
			$("#robotView").append("<p>PLACEHOLDER</p>");
	});
	
});


function addComponents(){
	if(parent.components[0] != null){
		parent.maxNrOfSensorNodes = 3;
		//to show continuation with other chassis.
		//if(ui.draggable[0].id == "chassi2") parent.maxnrOfSensorNodes = 5;
		$("#chassi").text("");
		$("#chassi").append("<img src='Style/"+parent.components[0]+".jpg' height='100px' width='100px'>");
	}
	if(parent.components[1] != null){
		parent.actionNodes.push("wheelButton");
		$("#wheels").text("");	
		$("#wheels").append("<img src='Style/"+parent.components[1]+".jpg' height='100px' width='100px'>");
	}
	if(parent.components[2] != null){
		parent.actionNodes.push("weaponButton");
		$("#weapon").text("");
		$("#weapon").append("<img src='Style/"+parent.components[2]+".jpg' height='100px' width='100px'>");
	}
	if(parent.components[3] != null){
		//parent.actionNodes.push("weaponButton2"); tills vidare
		$("#weapon").text("");
		$("#weapon").append("<img src='Style/"+parent.components[3]+".jpg' height='100px' width='100px'>");
	}
	if(parent.components[4] != null){
		//parent.actionNodes.push("weaponButton3"); tills vidare
		$("#weapon").text("");
		$("#weapon").append("<img src='Style/"+parent.components[3]+".jpg' height='100px' width='100px'>");
	}
}


