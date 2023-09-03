const KeyPartners = '17';
const KeyActivities = '15';
const KeyResources = '16';
const ValuePrepositions = '10';
const customerRelationships = '13';
const channels = '11';
const customerSegments = '12';
const costStructure = '18';
const revenueStreams = '14';
const brainStorm = '19';

const KeyPartnersSegmentID = $("#key-partners");
const KeyActivitiesSegmentID = $("#key-activities");
const KeyResourcesSegmentID = $("#key-resources");
const ValuePrepositionsSegmentID = $("#value-propositions");
const customerRelationshipsSegmentID = $("#customer-relationships");
const channelsSegmentID = $("#channels");
const customerSegmentsSegmentID = $("#customer-segments");
const costStructureSegmentID = $("#cost-structure");
const revenueStreamsSegmentID = $("#revenue-streams");
const brainStormSegmentID = $("#brainstorm");

// Color Constants
const anColor = "an";
const gnColor = "gn";
const ynColor = "yn";
const rnColor = "rn";
const enColor = "en";

var frmNote = $('#frmNote');
// Add Modal
var AddModalID = $('#addCardModal');
var AddModalTitle = $('#addModalTitle');
// Edit Modal
var EditModalID = $('#editCardModal');
var EditModalTitle = $('#editModalTitle');

// View Modal
var viewModalID = $('#viewCardModal');
var viewModalTitle = $('#viewModalTitle');

// delete URL
var postDeleteNoteURL = 'canvas?fnct=delBmcNote';

var getNotesURL = 'canvas?fnct=getBmcNotes';

var getBaseNoteURL = './canvas?fnct=getBmcNote&note_id=';

// READ MORE
// http://jsfiddle.net/zA23k/215/

/**
 * 
 * @param {*} segment 
 */
function openModal(segment) {
    console.log("Clicked openModal");
    console.log("Column Segment " + segment);
    // set value of card col
    $("#note_segment").val(segment);

    AddModalTitle.text('New Card');
    AddModalID.modal('show');
}

/**
 * 
 * @param {*} noteID 
 */
function openEditModal(noteID) {

    var getNoteURL = getBaseNoteURL + noteID;

    $.ajax({
        type: 'GET',
        url: getNoteURL,
        success: function(data) {
            var arraySize = data.note.length;
            var dataNote = data.note;

            var noteContent = dataNote[0].note_content;
            var noteSegment = dataNote[0].note_segment;
            var noteLabel = dataNote[0].note_label;

            // set value of the form
            $("#frmEditNote input[name=note_segment]").val(noteSegment);
            $("#frmEditNote input[name=note_label]").val(noteLabel);
            $("#frmEditNote input[name=note_id]").val(noteID);
            $('#note_content_edit').summernote('code', noteContent);
            if (dataNote[0].hasOwnProperty('note_additional_details') && dataNote[0].note_additional_details != "") {
                $('#note_additional_details_edit').summernote('code', dataNote[0].note_additional_details);
            }

            EditModalTitle.text('Edit Card');
            EditModalID.modal('show');

        },
        error: function(data) {

            msgHTML = '<div class="alert alert-danger" role="alert">' +
                'Oops! An Error Occured' +
                '</div>';

            $('#msgAlert').html(msgHTML);
            // Show modal to display error showed
            EditModalID.modal('show');
        }
    });
}

/**
 * 
 * @param {*} noteID 
 */
function deleteNote(noteID) {
    console.log("Clicked deleteNote");
    console.log("Column deleteNote " + noteID);

    var jsonData = {
        note_id: noteID
    };

    var result = confirm("Are you sure you want to delete?");
    if (result) {
        //Logic to delete the item
        $.ajax({
            type: 'POST',
            url: postDeleteNoteURL,
            data: jsonData,
            dataType: 'json',
            beforeSend: function() { //calls the loader id tag
                // $("#frmEditNote .close").click();
                // $("#loader").show();
            },
            success: function(data) {
                console.log("Success +++> ");
                console.log(data);

                assignNotes(data.notes);

                msgHTML = '<div class="alert alert-primary" role="alert">' +
                    'Note Deleted Successfuly ' +
                    '</div>';

                window.location.reload();
            },
            error: function(data) {

                msgHTML = '<div class="alert alert-danger" role="alert">' +
                    'Oops! An Error Occured' +
                    '</div>';

                $('#msgAlert').html(msgHTML);
                // Show modal to display error showed
                // editCardModal.modal('show');
            }
        });
    }



    // EditModalTitle.text('Edit Card');
    // EditModalID.modal('show');
}


/**
 * Check the right segment
 * @param {*} segment 
 */
function checkSegment(segment) {

    if (segment == KeyPartners) {

        return KeyPartners;

    } else if (segment == KeyActivities) {

        return KeyActivities;

    } else if (segment == KeyResources) {

        return KeyResources;

    } else if (segment == ValuePrepositions) {

        return ValuePrepositions;

    } else if (segment == customerRelationships) {

        return customerRelationships;

    } else if (segment == channels) {

        return channels;

    } else if (segment == customerSegments) {

        return customerSegments;

    } else if (segment == costStructure) {

        return costStructure;

    } else if (segment == revenueStreams) {

        return revenueStreams;

    } else if (segment == brainStorm) {

        return brainStorm;

    } else {

        return "None";
    }
}

/**
 * 
 * @param {*} noteID 
 * @param {*} colorToChange 
 */
function changeColorChange(noteID, colorToChange) {

    var elemID = "#task_" + noteID;

    if (colorToChange == anColor) {

        $(elemID).addClass(colorToChange);

        $(elemID).removeClass(gnColor);
        $(elemID).removeClass(ynColor);
        $(elemID).removeClass(rnColor);
        $(elemID).removeClass(enColor);

    } else if (colorToChange == gnColor) {

        $(elemID).addClass(colorToChange);

        $(elemID).removeClass(anColor);
        $(elemID).removeClass(ynColor);
        $(elemID).removeClass(rnColor);
        $(elemID).removeClass(enColor);

    } else if (colorToChange == ynColor) {

        $(elemID).addClass(colorToChange);

        $(elemID).removeClass(anColor);
        $(elemID).removeClass(gnColor);
        $(elemID).removeClass(rnColor);
        $(elemID).removeClass(enColor);

    } else if (colorToChange == rnColor) {

        $(elemID).addClass(colorToChange);

        $(elemID).removeClass(anColor);
        $(elemID).removeClass(gnColor);
        $(elemID).removeClass(ynColor);
        $(elemID).removeClass(enColor);

    } else if (colorToChange == enColor) {

        $(elemID).addClass(colorToChange);

        $(elemID).removeClass(anColor);
        $(elemID).removeClass(gnColor);
        $(elemID).removeClass(ynColor);
        $(elemID).removeClass(rnColor);

    }

}

function selectChange(colorVal) {

    console.log("Color Passed-> " + colorVal);
    // In Add Form
    $("#note_label").val(colorVal);
    // In Edit Form
    $("#frmEditNote input[name=note_label]").val(colorVal);
}

function validateCreateFields() {
    var msgHTML = "";
    if (validateNoteContent() != "") {
        msgHTML = '<div class="alert alert-danger" role="alert">' +
            validateNoteContent() +
            '</div>';
    }
}

function validateNoteContent() {
    if ($("#note_content").val() == "") {
        return "Note Content Field can't be empty"
    }
    return "";
}

function validateNoteLabel() {
    if ($("#note_label").val() == "") {
        return "Color can't be unselected"
    }
    return "";
}

/**
 * Ensures the notes are divided in proper segment
 * @param {*} data 
 */
function assignNotes(data) {

    var appendKeyPartnersHtml = '';
    var appendKeyActivitiesHtml = '';
    var appendKeyResourcesHtml = '';
    var appendValuePrepositionsHtml = '';
    var appendcustomerRelationshipsHtml = '';
    var appendchannelsHtml = '';
    var appendcustomerSegmentsHtml = '';
    var appendcostStructureHtml = '';
    var appendrevenueStreamsHtml = '';
    var appendbrainStormHtml = '';

    for (var i = 0; i < data.length; i++) {
        // If Customer Relationship
        if (data[i].note_segment == KeyPartners) {
            appendKeyPartnersHtml += appendNote(data[i]);
            KeyPartnersSegmentID.html(appendKeyPartnersHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == KeyActivities) {
            appendKeyActivitiesHtml += appendNote(data[i]);
            KeyActivitiesSegmentID.html(appendKeyActivitiesHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == KeyResources) {
            appendKeyResourcesHtml += appendNote(data[i]);
            KeyResourcesSegmentID.html(appendKeyResourcesHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == ValuePrepositions) {
            appendValuePrepositionsHtml += appendNote(data[i]);
            ValuePrepositionsSegmentID.html(appendValuePrepositionsHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == customerRelationships) {
            appendcustomerRelationshipsHtml += appendNote(data[i]);
            customerRelationshipsSegmentID.html(appendcustomerRelationshipsHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == channels) {
            appendchannelsHtml += appendNote(data[i]);
            channelsSegmentID.html(appendchannelsHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == customerSegments) {
            appendcustomerSegmentsHtml += appendNote(data[i]);
            customerSegmentsSegmentID.html(appendcustomerSegmentsHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == costStructure) {
            appendcostStructureHtml += appendNote(data[i]);
            costStructureSegmentID.html(appendcostStructureHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == revenueStreams) {
            appendrevenueStreamsHtml += appendNote(data[i]);
            revenueStreamsSegmentID.html(appendrevenueStreamsHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == brainStorm) {
            appendbrainStormHtml += appendNote(data[i]);
            brainStormSegmentID.html(appendbrainStormHtml);
            checkSelectedColor(data[i]);
        }
    }

}

/**
 * 
 * @param {*} data 
 */
function checkSelectedColor(data) {
    var noteLabel = data.note_label;
    var noteId = data.note_id;

    var colorSelectedAnColor = $("#an_" + noteId);
    var colorSelectedGnColor = "#gn_" + noteId;
    var colorSelectedYnColor = "#yn_" + noteId;
    var colorSelectedRnColor = "#rn_" + noteId;
    var colorSelectedEnColor = "#en_" + noteId;

    if (noteLabel == anColor) {
        colorSelectedAnColor.html('<i class="fa fa-check"></i>');
    } else if (noteLabel == gnColor) {
        $(colorSelectedGnColor).html('<i class="fa fa-check"></i>');
    } else if (noteLabel == ynColor) {
        $(colorSelectedYnColor).html('<i class="fa fa-check"></i>');
    } else if (noteLabel == rnColor) {
        $(colorSelectedRnColor).html('<i class="fa fa-check"></i>');
    } else if (noteLabel == enColor) {
        $(colorSelectedEnColor).html('<i class="fa fa-check"></i>');
    }
}

/**
 * 
 * @param {*} noteID 
 */
function viewNote(noteID) {
    var getNoteURL = getBaseNoteURL + noteID;

    $.ajax({
        type: 'GET',
        url: getNoteURL,
        success: function(data) {
            var arraySize = data.note.length;
            var dataNotes = data.note;
            var noteId = '';
            var noteContent = '';
            var noteAddDet = '';
            var ppleHtml = '';

            if (dataNotes[0].note_id == noteID) {

                noteContent = '<div class="note_content_view">' + dataNotes[0].note_content + '</div>';
                noteId = dataNotes[0].note_id;

                $("#note_content_view").html(noteContent);

                // check if extra  details exists and not null
                if (dataNotes[0].hasOwnProperty('note_additional_details') && dataNotes[0].note_additional_details != "") {
                    $("#note_add_det_view").html('<div class="note_add_det_view">' + dataNotes[0].note_additional_details + '</div>');
                }

                // Check if assigned exists in the obj
                if (dataNotes[0].hasOwnProperty('assign_list')) {
                    // and has data
                    if (dataNotes[0].assign_list.length > 0) {

                        for (var j = 0; j < dataNotes[0].assign_list.length; j++) {
                            // if last element dont add comma
                            if (dataNotes[0].assign_list.length - 1 == j) {
                                ppleHtml += '<span class="badge badge-pill badge-soft-primary font-size-10">' + dataNotes[0].assign_list[j].entity_name + '</span>';
                            } else {
                                ppleHtml += '<span class="badge badge-pill badge-soft-primary font-size-10">' + dataNotes[0].assign_list[j].entity_name + '</span>, ';
                            }
                        }
                        $("#note_assignee_view").html(ppleHtml);
                    }
                }

                viewModalTitle.text('View Card');
                viewModalID.modal('show');
            }

            // } // end for

        },
        error: function(data) {

            msgHTML = '<div class="alert alert-danger" role="alert">' +
                'Oops! An Error Occured' +
                '</div>';

            $('#msgAlert').html(msgHTML);
            // Show modal to display error showed
            viewModalID.modal('show');
        }
    });
}

/**
 * Note Body
 * @param {*} data 
 */
function appendNote(data) {

    var noteHtml = '';
    var noteId = data.note_id;
    var noteSegment = data.note_segment;
    var noteContent = data.note_content;
    var noteLabel = data.note_label;

    noteHtml += '<!-- start task card -->' +
        '<div class="card task-box ' + noteLabel + '" id="task_' + noteId + '">' +
        '<div class="card-body">' +
        '<!--Dropdown options-->' +
        '<div class="dropdown float-right">' +
        '<a href="#" class="dropdown-toggle arrow-none" data-toggle="dropdown" aria-expanded="false">' +
        '<i class="mdi mdi-dots-vertical m-0 text-muted h5"></i>' +
        '</a>' +
        '<div class="dropdown-menu dropdown-menu-right">';
    if (data.hasOwnProperty('note_additional_details') && data.note_additional_details != "") {
        noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();openEditModal(\'' + noteId + '\');"><i class="bx bx-edit"></i> Edit</a>';
    } else {
        noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();openEditModal(\'' + noteId + '\');"><i class="bx bx-edit"></i> Edit</a>';
    }
    noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();viewNote(\'' + noteId + '\');"><i class="bx bx-folder-open"></i> View Details</a>' +
        '<a class="dropdown-item delete" href="#" onclick="event.preventDefault();deleteNote(\'' + noteId + '\');"><i class="bx bx-trash"></i> Delete</a>' +
        '</div>' +
        '</div>' +
        '<!--End Dropdown options-->' +
        '<div class="float-right ml-2" style="display:none;">' +
        '<span class="badge badge-pill badge-soft-warning font-size-10">Assigned</span>' +
        '</div>' +
        '<div>' +
        '<a href="javascript: void(0);" class="text-muted">' + noteContent + '</a>' +
        '</div>' +
        '<div class="team float-left" style="display:none;">' +
        '<a href="javascript: void(0);" class="team-member d-inline-block">' +
        '<div class="avatar-xs">' +
        '<span class="avatar-title rounded-circle bg-soft-primary text-primary">' +
        'R' +
        '</span>' +
        '</div>' +
        '</a>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<!-- end task card -->';


    return noteHtml;
}