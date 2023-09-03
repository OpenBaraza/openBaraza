const productBacklog = '31';
const sprintBacklog = '32';
const toDo = '33';
const inProgress = '34';
const review = '35';
const done = '36';
const impediments = '37';

const productBacklogSegmentID = $("#product_backlog");
const sprintBacklogSegmentID = $("#sprint_backlog");
const toDoSegmentID = $("#to_do");
const inProgressSegmentID = $("#in_progress");
const reviewSegmentID = $("#review");
const doneSegmentID = $("#done");
const impedimentsSegmentID = $("#impediments");

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
var postDeleteNoteURL = 'canvas?fnct=delScrumNote';

var getNotesURL = 'canvas?fnct=getScrumNotes';

var postAssignCard = 'canvas?fnct=assignScrumNote';
var postUnassignCard = 'canvas?fnct=unassignScrumNote';
var postArchiveCard = 'canvas?fnct=archiveScrumNote';
var getBaseNoteURL = './canvas?fnct=getScrumNote&note_id=';

// READ MORE
// http://jsfiddle.net/zA23k/215/

/**
 * 
 * @param {*} segment 
 */
function openModal(segment) {
    // console.log("Clicked openModal");
    // console.log("Column Segment " + segment);
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
    // // console.log("Clicked openEditModal changed");
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
    // console.log("Clicked deleteNote");
    // console.log("Column deleteNote " + noteID);

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
                // console.log("Success +++> ");
                // console.log(data);

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
 * 
 * @param {*} noteID 
 */
function assignCard(noteID) {
    // console.log("Clicked assignCard");
    // console.log("Column assignCard " + noteID);

    var jsonData = {
        note_id: noteID
    };

    var result = confirm("Are you sure you want to assign this card to yourself?");
    if (result) {
        //Logic to delete the item
        $.ajax({
            type: 'POST',
            url: postAssignCard,
            data: jsonData,
            dataType: 'json',
            beforeSend: function() { //calls the loader id tag
                // $("#frmEditNote .close").click();
                // $("#loader").show();
            },
            success: function(data) {
                // console.log("Success +++> ");
                // console.log(data);

                // assignNotes(data.notes);

                msgHTML = '<div class="alert alert-primary" role="alert">' +
                    'Note Deleted Successfuly ' +
                    '</div>';

                window.location.reload();
            },
            error: function(data) {

                msgHTML = '<div class="alert alert-danger" role="alert">' +
                    'Oops! An Error Occured' +
                    '</div>';

                // $('#msgAlert').html(msgHTML);
                // Show modal to display error showed
                // editCardModal.modal('show');
            }
        });
    }



    // EditModalTitle.text('Edit Card');
    // EditModalID.modal('show');
}

/**
 * 
 * @param {*} noteID 
 */
function unassignCard(noteID) {
    // console.log("Clicked unassignCard");
    // console.log("Column unassignCard " + noteID);

    var jsonData = {
        note_id: noteID
    };

    var result = confirm("Are you sure you want to remove yourself from this note?");
    if (result) {
        //Logic to delete the item
        $.ajax({
            type: 'POST',
            url: postUnassignCard,
            data: jsonData,
            dataType: 'json',
            beforeSend: function() { //calls the loader id tag
                // $("#frmEditNote .close").click();
                // $("#loader").show();
            },
            success: function(data) {
                // console.log("Success +++> ");
                // console.log(data);

                // assignNotes(data.notes);

                msgHTML = '<div class="alert alert-primary" role="alert">' +
                    'Note Deleted Successfuly ' +
                    '</div>';

                window.location.reload();
            },
            error: function(data) {

                msgHTML = '<div class="alert alert-danger" role="alert">' +
                    'Oops! An Error Occured' +
                    '</div>';

                // $('#msgAlert').html(msgHTML);
                // Show modal to display error showed
                // editCardModal.modal('show');
            }
        });
    }



    // EditModalTitle.text('Edit Card');
    // EditModalID.modal('show');
}

/**
 * 
 * @param {*} noteID 
 */
function archive(noteID) {
    // console.log("Clicked archive");
    // console.log("Column archive " + noteID);

    var jsonData = {
        note_id: noteID
    };

    var result = confirm("Are you sure you want to archive this note?");
    if (result) {
        // Logic to delete the item
        $.ajax({
            type: 'POST',
            url: postArchiveCard,
            data: jsonData,
            dataType: 'json',
            beforeSend: function() { //calls the loader id tag
                // $("#frmEditNote .close").click();
                // $("#loader").show();
            },
            success: function(data) {
                // console.log("Success +++> ");
                // console.log(data);

                // assignNotes(data.notes);

                msgHTML = '<div class="alert alert-primary" role="alert">' +
                    'Note Deleted Successfuly ' +
                    '</div>';

                window.location.reload();
            },
            error: function(data) {

                msgHTML = '<div class="alert alert-danger" role="alert">' +
                    'Oops! An Error Occured' +
                    '</div>';

                // $('#msgAlert').html(msgHTML);
                // Show modal to display error showed
                // editCardModal.modal('show');
            }
        });
    }



    // EditModalTitle.text('Edit Card');
    // EditModalID.modal('show');
}

/**
 * 
 * @param {*} noteID 
 */
function unarchive(noteID) {
    alert("Coming Soon!");
}

/**
 * Check the right segment
 * @param {*} segment 
 */
function checkSegment(segment) {
    if (segment == "product_backlog") {
        return productBacklog;
    } else if (segment == "sprint_backlog") {
        return sprintBacklog;
    } else if (segment == "to_do") {
        return toDo;
    } else if (segment == "in_progress") {
        return inProgress;
    } else if (segment == "review") {
        return review;
    } else if (segment == "done") {
        return done;
    } else if (segment == "impediments") {
        return impediments;
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

    // console.log("Color Passed-> " + colorVal);
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

    var appendProductBacklogHtml = '';
    var appendSprintBacklogHtml = '';
    var appendToDoHtml = '';
    var appendInProgressHtml = '';
    var appendReviewHtml = '';
    var appendDoneHtml = '';
    var appendImpedimentsHtml = '';

    for (var i = 0; i < data.length; i++) {
        // console.log(data[i]);
        // If Customer Relationship
        if (data[i].note_segment == productBacklog) {
            appendProductBacklogHtml += appendNote(data[i], 'No');
            productBacklogSegmentID.html(appendProductBacklogHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == sprintBacklog) {
            appendSprintBacklogHtml += appendNote(data[i], 'No');
            sprintBacklogSegmentID.html(appendSprintBacklogHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == toDo) {
            appendToDoHtml += appendNote(data[i], 'No');
            toDoSegmentID.html(appendToDoHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == inProgress) {
            appendInProgressHtml += appendNote(data[i], 'No');
            inProgressSegmentID.html(appendInProgressHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == review) {
            appendReviewHtml += appendNote(data[i], 'No');
            reviewSegmentID.html(appendReviewHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == done) {
            appendDoneHtml += appendNote(data[i], 'No');
            doneSegmentID.html(appendDoneHtml);
            checkSelectedColor(data[i]);
        } else if (data[i].note_segment == impediments) {
            appendImpedimentsHtml += appendNote(data[i], 'No');
            impedimentsSegmentID.html(appendImpedimentsHtml);
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
function appendNote(data, isArchived) {

    var noteHtml = '';
    var noteId = data.note_id;
    var noteSegment = data.note_segment;
    var noteContent = data.note_content;
    var noteLabel = data.note_label;

    noteHtml += '<!-- start task card -->' +
        '<div class="card task-box ' + noteLabel + '" id="task_' + noteId + '">' +
        '<div class="card-body">' +
        '<!-- Dropdown options -->' +
        '<div class="dropdown float-right">' +
        '<a href="#" class="dropdown-toggle arrow-none" data-toggle="dropdown" aria-expanded="false">' +
        '<i class="mdi mdi-dots-vertical m-0 text-muted h5"></i>' +
        '</a>' +
        '<div class="dropdown-menu dropdown-menu-right">';
    if (isArchived != 'Yes') {
        if (data.hasOwnProperty('note_additional_details') && data.note_additional_details != "") {
            noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();openEditModal(\'' + noteId + '\');"><i class="bx bx-edit"></i> Edit</a>';
        } else {
            noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();openEditModal(\'' + noteId + '\');"><i class="bx bx-edit"></i> Edit</a>';
        }

        noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();viewNote(\'' + noteId + '\');"><i class="bx bx-folder-open"></i> View Details</a>';
        if (data.hasOwnProperty('assigned') && data.assigned == true) {
            noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();unassignCard(\'' + noteId + '\');"><i class="bx bx-street-view"></i> Remove Assignment </a>';
        } else {
            noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();assignCard(\'' + noteId + '\');"><i class="bx bx-street-view"></i> Self Assign</a>';
        }

        noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();archive(\'' + noteId + '\');"><i class="bx bx-archive-in"></i> Archive</a>';
    } else {
        noteHtml += '<a class="dropdown-item" href="#" onclick="event.preventDefault();unarchive(\'' + noteId + '\');"><i class="bx bx-archive-out"></i> Restore To Board</a>';
    }
    noteHtml += '<a class="dropdown-item delete" href="#" onclick="event.preventDefault();deleteNote(\'' + noteId + '\');"><i class="bx bx-trash"></i> Delete</a>' +
        '</div>' +
        '</div>' +
        '<!--End Dropdown options-->';
    if (data.hasOwnProperty('assigned') && data.assigned == true) {
        noteHtml += '<div class="float-right ml-2" style="display:block;">' +
            '<span class="badge badge-pill badge-soft-white font-size-13" style="padding: .2em;"><i class="bx bxs-user-check"></i></span>' +
            '</div>';
    }
    noteHtml += '<div>' +
        '<a href="javascript: void(0);" class="text-muted">' +
        noteContent +
        '</a>' +
        '</div>';
    if (data.hasOwnProperty('assign_list')) {
        if (data.assign_list.length > 0) {
            for (var i = 0; i < data.assign_list.length; i++) {
                noteHtml += '<!-- Start Abbreviation -->' +
                    '<div class="team float-left" style="margin-right: 0.2rem;">' +
                    '<a href="javascript: void(0);" class="team-member d-inline-block">' +
                    '<div class="avatar-xs">' +
                    '<span class="avatar-title rounded-circle bg-soft-white text-primary">' +
                    data.assign_list[i].entity_name.charAt(0) +
                    '</span>' +
                    '</div>' +
                    '</a>' +
                    '</div>' +
                    '<!-- End Abbreviation -->';
            }
        }
    }
    noteHtml += '</div>' +
        '</div>' +
        '<!-- end task card -->';


    return noteHtml;
}