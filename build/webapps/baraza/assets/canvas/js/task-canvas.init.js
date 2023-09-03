
function assignIDSegment(segment){

    console.log("To assignIDSegment : " + segment);

    if (segment == "key-partners") {
        console.log("To KeyPartners : " + KeyPartners);
        return KeyPartners;

    } else if (segment == "key-activities") {

        return KeyActivities;

    } else if (segment == "key-resources") {

        return KeyResources;

    } else if (segment == "value-propositions") {

        return ValuePrepositions;

    } else if (segment == "customer-relationships") {

        return customerRelationships;

    } else if (segment == "channels") {

        return channels;

    } else if (segment == "customer-segments") {

        return customerSegments;

    } else if (segment == "cost-structure") {

        return costStructure;

    } else if (segment == "revenue-streams") {

        return revenueStreams;

    } else if (segment == "brainstorm") {

        return brainStorm;

    } else {

        return "None";
    }
}

function getNoteID(droppedElId){
    var noteID = droppedElId.split("_");

    return noteID[1];
}

dragula([
    document.getElementById("key-partners"),
    document.getElementById("key-activities"),
    document.getElementById("key-resources"),
    document.getElementById("value-propositions"),
    document.getElementById("customer-relationships"),
    document.getElementById("channels"),
    document.getElementById("customer-segments"),
    document.getElementById("cost-structure"),
    document.getElementById("revenue-streams"),
    document.getElementById("brainstorm")
]).on('drop', function (el) {
    var parentElId = $(el).parent().attr('id');
    var droppedElIndex = $(el).index();
    var droppedElId = $(el).attr('id');
    var noteID = getNoteID(droppedElId);
    //get the id with the related canvas segment
    var droppedElIDSegment = assignIDSegment(parentElId);

    // console.log("parentElId : " + parentElId + " droppedElIndex : " + droppedElIndex + " droppedElId : " + droppedElId);
    
    var jsonData = { 
        note_segment: droppedElIDSegment,
        note_id:  noteID  
    };

    // console.log("JSON SENT : " + JSON.stringify(jsonData));

    var postDragNoteURL = 'canvas?fnct=moveNote';

     $.ajax({
        type: 'POST',
        url: postDragNoteURL,
        data: jsonData,
        dataType: 'json',
        beforeSend: function () {
    
        },
        success: function (data) {

        },
        error: function (data) {

        }
    });

});

