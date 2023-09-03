let setUrl = "";
const PREVIOUS = 'Previous';
const CURRENT = 'Current';
const NEXT = 'Next';
const NEXT2 = 'Next-2';
const NEXT3 = 'Next-3';
const NEXTN = 'Next-';
const INCREMENT = 'INCREMENT';
const DECREMENT = 'DECREMENT';
const currentWeekShifts = "Current Week Shifts";
const previousWeekShifts = "Previous Week Shifts";
const followingWeekShifts = "Following Week 1 Shifts";
const followingWeek2Shifts = "Following Week 2 Shifts";
const followingWeek3Shifts = "Following Week 3 Shifts";
const historyLookUp = [PREVIOUS, CURRENT, NEXT, NEXT2, NEXT3, NEXTN];

// let getOfficesUrl = "./jsondata?xml=attendance.xml&view=30:0";
let getOfficesUrl = "./jsondata?xml=attendance.xml&view=30:5";
let getEmployeesUrl = "./jsondata?xml=attendance.xml&view=55:0";
let getAssignedEmployeesUrl = "./jsondata?xml=attendance.xml&view=30:4";
var cardStatus = "";
let getHolidays = './jsondata?xml=attendance.xml&view=20:4';
let appendElem = '.employees-listing';
let appendElemOffices = '.offices';
let colors = ['', 'shf-purple', 'shf-orange', 'shf-green', 'shf-yellow', 'shf-red', 'shf-blue', 'shf-violet'];

// Error Msg
let ajaxMsg = [];
// ajaxMsg[1] = "Something Went Wrong! Contact Us as soon as possible.";
ajaxMsg[1] = "Please login to <a href='./' target='_blank'>HCM Demo</a> Then <a href='#' onclick='window.location.reload(true);'>refresh</a> to view the shift.";
var validFailMsg = " can't be blank";

// Add Modal
var AddModalID = $('#addCardModal');
var AddModalTitle = $('#addModalTitle');

// Delete Modal
var DeleteModalID = $('#deleteCardModal');
var DeleteModalTitle = $('#deleteModalTitle');

// Week Description
let weekDescription = $("#week_description");
const CURRENT_WEEK = 'current week';
const NEXT_WEEK = 'next week';
const PREVIOUS_WEEK = 'previous week';

function allowDrop(ev) {
    'use strict';
    if ($(ev.target).hasClass("task-list")) {
        ev.preventDefault();
    }
}

function drag(ev, segId) {
    let segmentId = segId.id;
    ev.dataTransfer.setData("text", segmentId);

    // Get parent div where the card is dragged from
    let parent_id = document.getElementById(segmentId).parentElement;
    let fromParentElem = parent_id.id;

    // console.log("Drag Parent -> " + fromParentElem);
    // console.log(fromParentElem);

    // Ifparent is from container 
    if (fromParentElem == "employee_not_assigned") {
        setUrl = 'ajaxinsert?app_xml=attendance.xml&app_key=55:1';
        cardStatus = "employee_not_assigned";
    } else {
        cardStatus = "";
        setUrl = 'ajaxupdate?app_xml=attendance.xml&oper=form&app_key=55:2';
        // alert('Trial');
    }
}

/**
 * Resolves the proper date for the selected week
 * @param {*} sheduleDate 
 */
function dateWeekResolver(sheduleDate) {
    // console.log("sheduleDate", sheduleDate);

    // The new Date() function can process strings in the 
    // format YYYY-MM-DD or YYYY/MM/DD
    // hence dateWeekResolverPreprocessor
    let weekDesc = dateWeekResolverPreprocessor(sheduleDate);
    let weekState = weekDescription.val();
    let defaultSheduleDate = new Date(weekDesc);
    let processedDate;
    let weekNumber = getPageNumber();

    // console.log("weekDesc", weekDesc);
    // console.log("defaultSheduleDate", defaultSheduleDate);
    // console.log("defaultSheduleDate.getDate()", defaultSheduleDate.getDate());
    console.log("Current Week State", weekState);

    if (weekNumber == 0) {
        processedDate = defaultSheduleDate;
    } else if (weekNumber > 0) {
        processedDate = defaultSheduleDate.addDays(7 * weekNumber);
    } else {
        processedDate = defaultSheduleDate.minusDays(7 * Math.abs(weekNumber));
    }

    console.log("processedDate", processedDate);

    var month = iformat(processedDate.getMonth() + 1); //months (0-11)
    var day = iformat(processedDate.getDate()); //day (1-31)
    var year = processedDate.getFullYear();

    // processedDate = date_formatter(year, month, day);
    processedDate = day + '-' + month + '-' + year;
    // console.log("new processedDate", processedDate);
    return processedDate;

}

/**
 * Convert into 
 */
 function dateWeekResolverPreprocessor(sheduleDate) {

    let splitDateArr = sheduleDate.split('-');
    // console.log(splitDateArr);
    // console.log(splitDateArr[0], splitDateArr[1], splitDateArr[2]); // [ "29", "11", "2021" ]
    // YYYY/MM/DD
    let parseAbleDate = splitDateArr[2] + '-' + splitDateArr[1] + '-' + splitDateArr[0];
    return parseAbleDate;
}

/**
 * convert date 28-04-2021 into 28/04/2021 for firefox compatibility with Date() Function
 * @param {*} sheduleDate 
 * @returns 
 */
 function datePreprocessorFireFoxCompatibility(sheduleDate) {

    let splitDateArr = sheduleDate.split('-');
    let year = splitDateArr[2];
    let month = splitDateArr[1];
    let day = splitDateArr[0];
    
    let parseAbleDate = day + '/' + month + '/' + year;
    return parseAbleDate;
}

function drop(ev, segementId, office, day) {
    ev.preventDefault();
    var draggedElemId = ev.dataTransfer.getData("text");
    let sheduleDate = generateCurrentDateFromDay(day);
    let sData = {};

    if (cardStatus == "employee_not_assigned") {
        // clone here
        droppedId = document.getElementById(draggedElemId).cloneNode(true);
        ev.target.appendChild(droppedId);

        sData = [{
            entity_id: draggedElemId,
            location_id: office,
            schedule_date: sheduleDate,
            time_in: '8:00',
            time_out: '17:00'
        }];
    } else {
        droppedId = document.getElementById(draggedElemId);
        // var droppedIdVal = droppedId.innerText; // get text between
        ev.target.appendChild(droppedId);
        let assignmentArr = draggedElemId.split("_");

        sData = [{
            KF: assignmentArr[1],
            location_id: office,
            schedule_date: sheduleDate,
            time_in: '8:00',
            time_out: '17:00'
        }];
    }

    let first = date.getDate() - date.getDay(); // First day is the day of the week - the day of the week
    let sheduleDay = first + day; // last day is the first day + 6
    // console.log("sheduleDay + > " + date);
    // let sheduleDate = new Date(date.setDate(sheduleDay));

    var jsonData = JSON.stringify(sData);

    console.log(jsonData);

    //Logic to delete the item
    $.ajax({
        type: 'POST',
        url: setUrl,
        data: { form_data: jsonData },
        dataType: 'json',
        beforeSend: function() {},
        success: function(data) {
            // console.log(data);

            // reload after dragging
            window.location.reload();
        },
        error: function(data) {}
    });

}

/**
 * Gets the date from dragged day
 * @param {*} day 
 * @returns 
 */
function generateCurrentDateFromDay(day) {
    // Get the current 1st week
    let date = new Date();
    let dragdDate;
    let first = (date.getDate() - date.getDay()) + 1; // First day is the day of the month - the day of the week
    let firstday = new Date(date.setDate(first)); // First day of the week(Sunday Date) 1st sunday date of current wek on the table


    // Add the date to passed day (args)
    let firstCurrentWeekSundayDate = firstday.getDate();
    let currentDraggedDay = firstCurrentWeekSundayDate + day; // this will be +1
    let newCurrentDraggedDay = currentDraggedDay - 1; // get the exact current date

    // validate the date to the start date 
    let firstMonth = date.getMonth() + 1;
    let firstYear = date.getFullYear();
    let daysOfMonth = getDaysOfMonth(0, firstMonth, firstYear);

    // if day is within the max days of the month, then return date
    if (newCurrentDraggedDay <= daysOfMonth) {

        // dragdDate = new Date(firstYear, firstMonth, newCurrentDraggedDay);
        // yyyy-mm-dd
        dragdDate = date_formatter(firstYear, firstMonth, newCurrentDraggedDay);

    } else {
        // else add the month and get the difference of the max month days to get the current date return date
        currentDraggedDay = newCurrentDraggedDay - daysOfMonth;
        firstMonth = firstMonth + 1; // Add month

        dragdDate = date_formatter(firstYear, firstMonth, currentDraggedDay);
    }

    return dragdDate;
}

/**
 * Convert 2 digit number if 0-9
 * @param {*} dateVal 
 * @returns 
 */
function two_digit_date_convertor(dateVal) {
    if (dateVal <= 9) {
        dateVal = '0' + dateVal;
        return dateVal;
    }
    return dateVal;
}

/**
 * Date Formatter 
 * @param {*} firstYear 
 * @param {*} firstMonth 
 * @param {*} newCurrentDraggedDay 
 * @returns 
 * dd-MM-yyyy
 */
 function date_formatter(firstYear, firstMonth, newCurrentDraggedDay, reverse = false) {
    let dragdDate, dragdDateReversed, formattedDate;
    let firstMonthCount = firstMonth.length;
    let newCurrentDraggedDayCount = newCurrentDraggedDay.length;

    // if string
    if(firstMonthCount && newCurrentDraggedDayCount){
        // if date has single digit eg 1 then convert to 01
        firstMonth = firstMonthCount < 2 ? two_digit_date_convertor(firstMonth) : firstMonth;
        newCurrentDraggedDay = newCurrentDraggedDayCount < 2 ? two_digit_date_convertor(newCurrentDraggedDay) : newCurrentDraggedDay;
    } else {
        // if integer then 
        firstMonth = iformat(firstMonth);
        newCurrentDraggedDay = iformat(newCurrentDraggedDay);
    }

    // yyyy-mm-dd
    dragdDateReversed = firstYear + "-" + firstMonth + "-" + newCurrentDraggedDay;

    // dd-MM-yyyy
    dragdDate = newCurrentDraggedDay + "-" + firstMonth + "-" + firstYear;

    formattedDate = reverse ? dragdDateReversed : dragdDate;

    return formattedDate;
}


// ajaxCall('GET', getOfficesUrl, '', ajaxMsg, '#errorAlert');
// ajaxCall('GET', getEmployeesUrl, '', ajaxMsg, '#errorAlert');
// ajaxCall('GET', getAssignedEmployeesUrl, '', ajaxMsg, '#errorAlert');

// Load Current Week
// current();
const setPage = (page) => {
    // console.log("Passed ", page);
    // Update local storage
    localStorage.page = JSON.stringify({page: page});

    let currentPage = JSON.parse(localStorage.page);
    // console.log("Storage currentPage ",  currentPage.page );
}

const getPage = () => {
    if (!localStorage.hasOwnProperty("page")) {
        // console.log("Get currentPage:  undefined" );
        return undefined; // skip keys like "setItem", "getItem" etc
    }
    let currentPage = JSON.parse(localStorage.page);
    // console.log("Get currentPage ",  currentPage.page );
    return currentPage.page;
}

const getPageNumber = () => {
    if (!localStorage.hasOwnProperty("page_number")) return 0;
    let pageNumber = localStorage.page_number;
    return pageNumber;
}

// Load Current Week
let currentWeek = getPage();
let renderCurrentWeekHtml = "";
switch(currentWeek) {
    case PREVIOUS:
        // console.log("switch PREVIOUS");
        back();
        renderCurrentWeekHtml = "<span> Showing Previous Week Shifts </span>";
        break;

    case CURRENT:
        // console.log("switch CURRENT");
        renderCurrentWeekHtml = "<span> Showing Current Week Shifts </span>";
        current();
        break;

    case NEXT:
        // console.log("switch NEXT");
        renderCurrentWeekHtml = "<span> Showing Following Week Shifts </span>";
        next();
        break;

    default:
        current();
        renderCurrentWeekHtml = "<span> Showing Current Week Shifts </span>";
        // console.log("default then current");
        break;
}
$("#currentWk").html(renderCurrentWeekHtml);


/**
 * General Ajax
 * @param {*} method 
 * @param {*} url 
 * @param {*} jsonData 
 * @param {*} ajaxMsg 
 * @param {*} elemID 
 * @param {*} htmlMsg 
 */
function ajaxCall(method, url, jsonData, ajaxMsg, elemID) {

    var msgHTML = "";
    if (method = 'POST') {
        $.ajax({
            type: method,
            url: url,
            data: jsonData,
            dataType: 'json',
            success: function(data) {

                getAjaxData(data, url, elemID);

                // window.location.href = '/dashboard';
            },
            error: function(data) {

                msgHTML = '<div class="alert alert-danger" role="alert">' +
                    ajaxMsg[1] +
                    '</div>';

                $(elemID).html(msgHTML);
                $('.shift-tb').hide();
            }
        });
    } else {
        $.ajax({
            type: method,
            url: url,
            dataType: 'json',
            success: function(data) {

                getAjaxData(data, url, elemID);

                // window.location.href = '/dashboard';
            },
            error: function(data) {

                msgHTML = '<div class="alert alert-danger" role="alert">' +
                    ajaxMsg[1] +
                    '</div>';

                $(elemID).html(msgHTML);
                $('.shift-tb').hide();
            }
        });
    }


}

/**
 * Filters Data
 * @param {*} data 
 */
function getAjaxData(data, url, elemID) {
    // console.log("getAjaxData");

    let msgHTML = "";

    switch (url) {

        case getOfficesUrl:
            weeklyOfficeTemplateGenerator(appendElemOffices, data);
            break;

        case getEmployeesUrl:
            employeeCardTemplateGenerator(appendElem, data);
            break;

        case getAssignedEmployeesUrl:
            locateEmployeeShift(data);
            break;

        default:
            text = "No value found";
    }
}

function changeColor(userCardID, colorToChange, elemID) {

    // console.log(userCardID);
    // console.log(colorToChange);
    let elemId = '#' + userCardID;

    // $(elemId).addClass(colorToChange);

    let element = document.getElementById(userCardID);
    element.classList.remove("shf-purple");
    element.classList.remove("shf-orange");
    element.classList.remove("shf-green");
    element.classList.remove("shf-yellow");
    element.classList.add(colorToChange);

}

function employeeCardTemplateGenerator(appendElem, data) {
    let employeeListingTemplateCard = '';

    for (let i = 0; i < data.length; i++) {

        employeeListingTemplateCard += '<div class="card task-box mb-10" id="' + data[i].entity_id + '" >' +
            '<div class="card-body">' +
            '</div>' +
            '<div>' +
            '<a href="javascript: void(0);" class="text-muted">' +
            '<ul style="padding-bottom: 10px;">' +
            '<li>' + data[i].first_name + ' ' + data[i].surname + '</li>' +
            '<li>(08:00 - 17:00)</li>' +
            ' </ul>' +
            '</a>' +
            '</div>' +
            '</div>' +
            '</div>';

        // console.log(data[i].name);
    }

    $(appendElem).html(employeeListingTemplateCard);
}

function weeklyOfficeTemplateGenerator(appendElem, data) {
    let weekListingTemplateCard = '';

    for (let i = 0; i < data.length; i++) {
        weekListingTemplateCard += '<tr class="" id="office_' + data[i].location_id + '">';
        // Loop for the whole 7 days
        for (let day = 0; day <= 7; day++) {
            if (day == 0) {
                weekListingTemplateCard += '<th scope="row" > ' +
                    '<center>' +
                    '<span id="header_' + data[i].location_id + '_' + day + '">' + data[i].location_name + '</span>' +
                    '</center>' +
                    '</th>';
            } else {
                // removed drag from table only view
                // weekListingTemplateCard += '<td id="office_' + data[i].location_id + '_day_' + day + '" class="task-list" ondrop="drop(event, this ,' + data[i].location_id + ', ' + day + ')" ondragover="allowDrop(event)">' +
                weekListingTemplateCard += '<td id="office_' + data[i].location_id + '_day_' + day + '" class="task-list" >' +

                    '</td>';
            }

        }
        weekListingTemplateCard += '</tr>';
    }
    $(appendElem).html(weekListingTemplateCard);

    return true;
}

function locateEmployeeShift(data) {
    // console.log("Function 1 : locateEmployeeShift");
    // console.log(data);
    let office_location = 0;
    let day = 0;
    let appendElem = '';
    let idElem = '';
    let schedule_date = '';
    let KF = '';

    for (let i = 0; i < data.length; i++) {
        office_location = data[i].location_id;
        // day = getDay(data[i].end_day);
        schedule_date = data[i].schedule_date;
        KF = data[i].KF;

        // convert date 28-04-2021 into 28/04/2021 for firefox compatibitility with Date() Function
        schedule_date = schedule_date.replace(/-/g, '/');

        day = getDay(schedule_date);
        // console.log("day");
        // console.log(day);
        idElem = "assignment_" + KF;
        if (day !== -1) {
            day = day + 1;
            // idElem = "employee_" + office_location + "_day_" + day;
            appendElem = "#office_" + office_location + "_day_" + day;

            appendEmployeeCard(appendElem, idElem, data[i]);
        }
    }
}

/**
 * Get the num for the day of the week
 *  
 * @param {*} data 
 * @returns 
 */
function getDay(data) {
    // console.log("========================");
    // console.log("Function 2 : getDay");
    // console.log(data);
    let date = new Date(data);
    let dayWeeek = date.getDay();


    // first date
    let todayDate = new Date();
    let first = todayDate.getDate() - todayDate.getDay(); // First day is the day of the month - the day of the week
    let firstday = new Date(todayDate.setDate(first));

    // if (validateCurrentWeek(date, firstday)) {
    //     return dayWeeek;
    // }
    // return -1;

    return dayWeeek;

}

/**
 * Check if the date is within the current week
 */
function validateCurrentWeek(date, firstday) {
    // console.log("Function 3 : validateCurrentWeek");
    // console.log(date);

    if (checkCurrentWeek(date, firstday)) {
        return true;
    }

    return false;
}

function checkCurrentWeek(employee_date, firstday) {
    // console.log("-----------checkCurrentWeek----------");
    // console.log(employee_date);


    let firstWeekSundayDate = firstday.getDate();
    let lastWeekSundayDate = firstWeekSundayDate + 6;
    let firstMonthDate = firstday.getMonth() + 1;
    let firstYear = firstday.getFullYear();

    let lastDay = lastWeekSundayDate - firstWeekSundayDate;
    let followingMonth;
    let followingYear;

    // if its december make the last month as January
    // else increment add 1 and the result will be valid month
    if (firstMonthDate == 12) {
        followingMonth = 01;
        followingYear = firstday.getFullYear() + 1;
    } else {
        followingMonth = firstMonthDate + 1;
        followingYear = firstday.getFullYear();
    }

    // Minus 1 as by default month starts from 0
    let finalDate = new Date(followingYear, followingMonth - 1, lastDay);
    let firstDate = new Date(firstYear, firstMonthDate - 1, firstWeekSundayDate);


    if (employee_date >= firstDate && employee_date <= finalDate) {
        return true;
    }

    return false;

}

function appendEmployeeCard(appendElem, idElem, data) {
    // console.log("================= appendEmployeeCard ============");
    // console.log("Function 5 : getDay");
    // console.log(data);
    // console.log("appendElem " + appendElem);
    // console.log("data.schedule_label " + data.schedule_label);
    // console.log("=================");
    let employeeListingTemplateCard = '';


    employeeListingTemplateCard = '<div class="card task-box mb-10 ' + data.schedule_label + '" id="' + idElem + '">' +
        '<div class="card-body">' +
        '</div>' +
        '<div>' +
        '<a href="javascript: void(0);" class="text-muted">' +
        '<ul style="padding-bottom: 10px;">' +
        '<li>' + data.employee_short_name + '</li>' +
        '<li>(' + data.time_in + ' - ' + data.time_out + ')</li>' +
        ' </ul>' +
        '</a>' +
        '</div>' +
        '</div>' +
        '</div>';
    $(appendElem).append(employeeListingTemplateCard);
    // $("#office_" + office_location + "_day_" + day).append(employeeListingTemplateCard);
}


/**
 * Validates Field
 * @param {*} data 
 */
function validateField(data) {
    return answer = data == "" ? true : false;
}

// Add Comment 
$('.btn-time-update').click(function(event) {
    let from = $("#from").val();
    let to = $("#to").val();
    let note_content = $("#note_content").val();
    let user_id = $("#user_id").val();
    let keyfield = $("#keyfield").val();
    let schedule_label = $("#schedule_label").val();
    let msgHTML = '';
    var error = [];

    if (validateField(from)) {
        error.push("From Time : " + validFailMsg)
    }
    if (validateField(to)) {
        error.push("From To : " + validFailMsg)
    }

    if (error.length > 0) {

        msgHTML = '<div class="alert alert-danger" role="alert">' +
            '<ul style="margin-bottom: 0;">';

        for (var i = 0; i < error.length; i++) {
            console.log(error[i]);
            msgHTML += '<li>' + error[i] + '</li>'
        }

        msgHTML += '</ul>' +
            '</div>';

        $('#msgAlert').html(msgHTML);

    } else {

        // hide Modal
        AddModalID.modal('hide');
        // window.location.reload();

        event.preventDefault();


        let sData = [{
            KF: keyfield,
            location_id: null,
            schedule_date: null,
            details: note_content,
            schedule_label: schedule_label,
            user_id: user_id,
            time_in: from,
            time_out: to,
        }];

        let jsonData = JSON.stringify(sData);

        console.log(jsonData);

        updateUrl = 'ajaxupdate?app_xml=attendance.xml&oper=form&app_key=55:3';

        $.post(updateUrl, {
            form_data: jsonData
        }, function(data) {

            AddModalID.modal('hide');
            window.location.reload();

            $('.signin-popup-box').fadeOut('fast');
        }, "JSON");
    }
});

// Delete Comment 
$('.deletebtn').click(function(event) {

    // hide Modal
    DeleteModalID.modal('hide');
    // window.location.reload();

    let keyfield = $("#keyfield_delete").val();

    console.log("Clicked deleteCardModal .delete" + keyfield);

    event.preventDefault();

    let delData = [{
        KF: keyfield
    }];

    let jsonData = JSON.stringify(delData);


    $.post("ajaxupdate?app_xml=attendance.xml&app_key=55:2&oper=delete", {
        form_data: jsonData
    }, function(data) {

        DeleteModalID.modal('hide');
        // console.log('AJAX Auth');
        // console.log(data);
        // console.log("Roles Count => " + data.roles.length);

    }, "JSON");

});


/**
 * ===================== TABLE SCRIPTS ========================
 */

//Grab day of the week from local computer
let date = new Date();
let dayOfWeekNumber = date.getDay();
let todayDate = date.getDate();
let todayMonthDate = date.getMonth() + 1; // Jan = 0; Dec = 11;
let formattedDay = new Date(date.setDate(todayDate)).toLocaleDateString();
let nameOfDay;
let quote;
let displayDayMonth;
let idDateName;
let first = date.getDate() - date.getDay(); // First day is the day of the month - the day of the week
let last = first + 6; // last day is the first day + 6
let firstday = new Date(date.setDate(first)); // First day of the week(Sunday Date) 1st sunday date of current wek on the table
let lastday = new Date(date.setDate(last));

/**
 * Generate Table Header Name, Day and Today Label
 * @param {*} dayOfWeekNumber 
 * @param {*} todayMonthDate 
 * @param {*} firstday 
 * @param {*} dayOfWeekNumber 
 */
function showDate(dayOfWeekNumber, todayMonthDate, firstday) {
    let currentState;
    let displayDayMonthElem;
    let displayDayMonthElemFooter;
    let displayDayMonth;
    let idName;
    let idNameFooter;
    let todayMsg;
    let todayMsgFooter;
    let currentDay = -1;

    // Jan starts 0 hence +1
    todayMonthDate = firstday.getMonth() + 1;

    for (let count = 0; count < 7; count++) {
        idName = "date_" + count;
        idNameFooter = "tfoot_date_" + count;

        currentState = firstday.addDays(count);
        currentDay = currentState.getDate();
        todayMonthDate = currentState.getMonth() + 1;
        displayDayMonth = currentDay + "/" + todayMonthDate;

        // console.log("currentState",currentState);
        // console.log("currentDay",currentDay);
        // console.log("displayDayMonth",displayDayMonth);

        displayDayMonthElem = document.getElementById(idName);
        displayDayMonthElem.innerHTML = `${displayDayMonth}`;

        // Display Footer Table Days
        displayDayMonthElemFooter = document.getElementById(idNameFooter);
        displayDayMonthElemFooter.innerHTML = `${displayDayMonth}`;

        // Show Today
        todayMsg = "#current_" + count;
        todayMsgFooter = "#tfoot_current_" + count;
        if (dayOfWeekNumber == count) {
            $(todayMsg).show();
            $(todayMsgFooter).show();
        } else {
            $(todayMsg).hide();
            $(todayMsgFooter).hide();
        }
    }
}

// How many days that month has
function getDaysOfMonth(date, month, year) {
    // If date is not used by adding 0 at the date args
    if (date != 0) {
        month = date.getMonth() + 1; // month number
        year = date.getFullYear(); // year
    }

    let daysInMonth = new Date(year, month, 0).getDate();

    return daysInMonth;

}

/**
 * ===================== WEEK GENERATION PIPELINE BEGINE HERE ========================
 */

/**
 * Generate the offices again
 * to clear the board
 * @returns 
 */
function generateWeekShiftPipeline(firstDay, lastDay) {
    // console.log("=================== generateWeekShiftPipeline ===========================");

    $.ajax({
        type: "GET",
        url: getOfficesUrl,
        dataType: 'json',
        success: function(data) {
            // once the offices have been generated, apppend shift employees
            if (weeklyOfficeTemplateGenerator(appendElemOffices, data)) {

                // Populate Employees once office week Generation has been done
                weekAjax(firstDay, lastDay);

                // show holiday and disable holiday boxes
                holidayAjax(firstDay, lastDay);
            }

        },
        error: function(data) {

            msgHTML = '<div class="alert alert-danger" role="alert">' +
                ajaxMsg[1] +
                '</div>';

            $(elemID).html(msgHTML);
            $('.shift-tb').hide();

            // return false;
        }
    });



}

/**
 * Ajax Call 
 * @param {*} firstDay 
 * @param {*} lastDay 
 */
function weekAjax(firstDay, lastDay) {
    let firstDayFormatted = date_formatter(firstDay.getFullYear(), firstDay.getMonth() + 1, firstDay.getDate(), true);
    let lastDayFormatted = date_formatter(lastDay.getFullYear(), lastDay.getMonth() + 1, lastDay.getDate(), true);
    // console.log("weekAjax firstDay ", firstDayFormatted);
    // console.log("weekAjax lastDay ", lastDayFormatted);
    // &data=&where=schedule_date%20BETWEEN%20%272021-01-08%27%20AND%20%272022-01-16%27
    let filterUrl = getAssignedEmployeesUrl + "&data=&where=schedule_date BETWEEN \'" + firstDayFormatted.trim() + "\' AND \'" + lastDayFormatted.trim() + "\'";
    // console.log("weekAjax filterUrl ", filterUrl);
    $.ajax({
        type: "GET",
        url: filterUrl,
        dataType: 'json',
        success: function(dataAssignedEmployees) {
            dynamicEmployeeWeeklyAssignment(dataAssignedEmployees, firstDay, lastDay);
        },
        error: function(data) {

            msgHTML = '<div class="alert alert-danger" role="alert">' +
                ajaxMsg[1] +
                '</div>';

            $(elemID).html(msgHTML);
            $('.shift-tb').hide();
        }
    });
}

/**
 * For safari browsers
 * @param {*} datestring 
 * @returns 
 */
 function parseDate(datestring) {
    // console.log("datestring", datestring);
    let d = datestring.split(/\D+/g).map(function(v) { return parseInt(v, 10); });
    // console.log("datestring d", d);
    // console.log("datestring d.length", d.length);

    // should have 3 elements if 4 means firefox appended - in element 0 thus shift function
    d.length === 4 ? d.shift() : d;

    let parsedDate = new Date(d[0], d[1] - 1, d[2]);
    // console.log("datestring d parsedDate ", parsedDate);

    return parsedDate;
}

/**
 * Assign the Employees Dynamically
 * Based on the week range provided
 * @param {*} data 
 * @param {*} firstDay 
 * @param {*} endDay 
 */
function dynamicEmployeeWeeklyAssignment(data, firstDay, endDay) {
    // console.log("firstDay", firstDay);
    // console.log("endDay", endDay);
    // console.log("data", data); 

    let schedule_date = '';
    let office_location = 0;
    let dayWeek = '';
    let schedule_date_day = '';
    let appendElem = '';
    let KF = '';
    let day = 0;

    for (let i = 0; i < data.length; i++) {
        office_location = data[i].location_id;
        KF = data[i].KF;
        schedule_date = data[i].schedule_date;
        
        // convert date 28-04-2021 into 28/04/2021 for firefox compatibitility with Date() Function
        // schedule_date = schedule_date.replace(/-/g, '/');

        //convert for parsedate
        schedule_date =  new Date(schedule_date);
        // console.log("schedule_date before date_formatter ", schedule_date);

        // in firefox, the date has hyphen, we remove it
        // schedule_date = schedule_date.replace(/-/g, '');

        // console.log("schedule_date before date_formatter ", schedule_date);
        schedule_date_day =  schedule_date.getTime();
        schedule_date = date_formatter(schedule_date.getFullYear(), schedule_date.getMonth() + 1, schedule_date.getDate(), true);
        // console.log("schedule_date after date_formatter ", schedule_date);

        // schedule_date_day = new Date(schedule_date);
        // dayWeek = schedule_date_day.getDay();
        // day = parseInt(schedule_date_day.getDay());
        // day = day + 1;
        dayWeek = parseDate(schedule_date).getDay();
        day = parseInt(parseDate(schedule_date).getDay());
        // since  sunday starts 0 then in this case make it 7
        // that is the id Elements in the dome start from 1 to 7
        // there is no zero
        // console.log("---------------- Changed");
        day = day === 0 ? 7 : day; 
        // console.log("dayWeek", dayWeek);
        // console.log("day", day);

        // let daysOfWeek = [ 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday' ];
        // // not used
        // let whichDay = daysOfWeek[parseDate(schedule_date).getDay()];
        // console.log("whichDay", whichDay);

        idElem = "assignment_" + KF;
        appendElem = "#office_" + office_location + "_day_" + day;

        // console.log("firstDay.getTime()", firstDay.getTime());
        // console.log("endDay.getTime()", endDay.getTime());
        // console.log("schedule_date_day.getTime() => schedule_date_day", schedule_date_day); // 1646784000000
        // console.log("isDateCurrentRange(firstDay.getTime(), endDay.getTime(), schedule_date_day.getTime())", isDateCurrentRange(firstDay.getTime(), endDay.getTime(), schedule_date_day));

        // We use time to compare dates 
        // The Api COvers the range so really does not matter
        // filtering dates
        // @deprecated
        // if (isDateCurrentRange(firstDay.getTime(), endDay.getTime(), schedule_date_day)) {
            // if the scheduled date is within the date range week [@deprecated]
            // then assign employees
            appendEmployeeCard(appendElem, idElem, data[i]);

        //     console.log(schedule_date_day," Within Range Between ",firstDay,endDay);
        // } else {
        //     // appendEmployeeCard(appendElem, idElem, null);

        //     // console.log(schedule_date_day," Not Within Between ",firstDay,endDay);
        // }
    }


}

/**
 * 
 * @param {*} n 
 * @returns 
 * Date Formatter for values less than 10 
 * to append 0 before eg 09
 */
function iformat(n) {
    return (n < 10 ? '0' : '') + n;
}

/**
 * Get Today Date from Midnight 
 */
function getCurrentDateFromMidnight() {
    var date = new Date();
    var month = iformat(date.getMonth() + 1); //months (0-11)
    var day = iformat(date.getDate()); //day (1-31)
    var year = date.getFullYear();

    return year + "/" + month + "/" + day;
}

/**
 * Compare dates
 * @param {*} startTime 
 * @param {*} endTime 
 * @param {*} val 
 * @returns 
 */
function isDateCurrentRange(startTime, endTime, val) {
    return val >= startTime && val <= endTime;
}

/**
 * Function that populates 
 * the hidden inputs that will be used to send copy request
 */
function setCopyInputs(day, elemID) {
	 // check if day is set or to null
	day = day ? convertStrToDateFormat(day) : day;
	$("#"+elemID).val(day);
}

function convertStrToDateFormat(str) {
    let date = new Date(str),
      month = ("0" + (date.getMonth() + 1)).slice(-2),
      day = ("0" + date.getDate()).slice(-2);
    // return [date.getFullYear(), mnth, day].join("-");
    return date_formatter(date.getFullYear(), month, day, true);
}

function next(isPageSet = false) {
    // console.log("next()");

    let START_WEEK = 7;
    let LAST_WEEK = 13;
    let weekName = "";
    let weekTableTitle = "";
    let pageNumber = getPageNumber();

    // Update if button Localstorage Track next page
    if(isPageSet){
        setPage(INCREMENT);
        
        // Add the page number by 1 and save
		pageNumber++;
		localStorage.page_number = pageNumber;
    }

    // read the current page state
   let currentPage = getPage();

   console.log("currentPage next()", currentPage);

    if(pageNumber == 0) {
        START_WEEK = 0;
       	LAST_WEEK = 6;
       	isCurrentWk = true;
       	
		weekName = currentWeekShifts;
		weekTableTitle = CURRENT_WEEK;
    } else if(pageNumber > 0) {
       	START_WEEK = 7 * pageNumber;
       	LAST_WEEK = 6 + (7 * pageNumber);
       
		weekName = 'Next ' + pageNumber + ' week';
		weekTableTitle = 'Next Week ' + pageNumber + ' Shifts';
	} else {
		START_WEEK = 7 * pageNumber;
       	LAST_WEEK = 6 + (7 * pageNumber);
       
		weekName = 'Previous ' + Math.abs(pageNumber) + ' week';
		weekTableTitle = 'Previous Week ' + Math.abs(pageNumber) + ' Shifts';
	}

    // set Value of week
    weekDescription.val(weekName);

    console.log('next() currentPage ',currentPage, 'weekName ', weekName, ' START_WEEK ', START_WEEK, ' LAST_WEEK ', LAST_WEEK);

    let formattedDate = getCurrentDateFromMidnight();
    let date = new Date(formattedDate);
    // console.log("---------------change here (dayOfWeekNumber minus 1)------------------")
    let dayOfWeekNumber = date.getDay() - 1;
    let todayMonthDate = date.getMonth() + 1;
    // console.log("---------------change here (first add 1)------------------")
    let first = (date.getDate() - date.getDay()) + 1;
    let current_firstday = new Date(date.setDate(first));
    let next_firstday = current_firstday.addDays(START_WEEK);
    let last_firstday = current_firstday.addDays(LAST_WEEK);
    renderCurrentWeekHtml = "<span> Showing "+ weekTableTitle +" </span>";

    // Set hidden inputs as null
    setCopyInputs(next_firstday, "first_day");
    setCopyInputs(last_firstday, "last_day");

    $("#currentWk").html(renderCurrentWeekHtml);
    $(".currentWk").html(weekTableTitle);

    showDate(dayOfWeekNumber, todayMonthDate, next_firstday);

    // Remove Today Label
    let todayMsg = "#current_" + dayOfWeekNumber;
    let todayMsgFooter = "#tfoot_current_" + dayOfWeekNumber;
    $(todayMsg).hide();
    $(todayMsgFooter).hide();

    generateWeekShiftPipeline(next_firstday, last_firstday);

}

function current(isPageSet = false) {
    // console.log("current()");

    // Localstorage Track current page
    if(isPageSet) {
        setPage(CURRENT);
        
        // Save the page number by week count
		localStorage.page_number = 0;
    }

    let formattedDate = getCurrentDateFromMidnight();
    // console.log("start formattedDate", formattedDate);
    let date = new Date(formattedDate);
    // console.log("date", date);
    // console.log("start ---------------change here (dayOfWeekNumber minus 1)------------------")
    let dayOfWeekNumber = date.getDay() - 1;
    // console.log("start dayOfWeekNumber", dayOfWeekNumber);
    let todayMonthDate = date.getMonth() + 1;
    // console.log("start todayMonthDate", todayMonthDate);
    // console.log("start ---------------change here (first add 1)------------------")
    let first = (date.getDate() - date.getDay()) + 1;
    // console.log("start first", first);
    let current_firstday = new Date(date.setDate(first));
    // console.log("start current_firstday", current_firstday);
    let current_lastday = current_firstday.addDays(6);
    // console.log("start current_lastday", current_lastday);
    renderCurrentWeekHtml = "<span> Showing "+ currentWeekShifts +" </span>";

    // Set hidden inputs
    setCopyInputs(current_firstday, "first_day");
    setCopyInputs(current_lastday, "last_day");

    // set Value of week
    weekDescription.val(CURRENT_WEEK);

    $("#currentWk").html(renderCurrentWeekHtml);
    $(".currentWk").html(currentWeekShifts);

    showDate(dayOfWeekNumber, todayMonthDate, current_firstday);

    generateWeekShiftPipeline(current_firstday, current_lastday);
}

function back(isPageSet = false) {
    // console.log("back()");

    let START_WEEK = 7;
    let LAST_WEEK = 1;
    let weekName = "";
    let weekTableTitle = "";
    let pageNumber = getPageNumber();

    // Localstorage Track next page
    if(isPageSet) {
        setPage(DECREMENT);
        
        // Reduce the page number by 1 and save
		pageNumber--;
		localStorage.page_number = pageNumber;
    }

    let currentPage = getPage();
    console.log("currentPage back()", currentPage);

    let formattedDate = getCurrentDateFromMidnight();
    let date = new Date(formattedDate);
    let dayOfWeekNumber = date.getDay() - 1;
    let todayMonthDate = date.getMonth() + 1;
    let first = (date.getDate() - date.getDay()) + 1;
    let current_firstday = new Date(date.setDate(first));
    let previous_firstday;
    let previous_last;
    let isCurrentWk = false;
    console.log('back() current_firstday ', current_firstday);
        
    if(pageNumber == 0) {
        START_WEEK = 0;
       	LAST_WEEK = 6;
       	isCurrentWk = true;
       	
		weekName = currentWeekShifts;
		weekTableTitle = CURRENT_WEEK;
    } else if(pageNumber > 0) {
       	START_WEEK = 7 * pageNumber;
       	LAST_WEEK = 6 + (7 * pageNumber);
       	isCurrentWk = false;
       	
		weekName = 'next ' + pageNumber + ' week';
		weekTableTitle = 'Next Week ' + pageNumber + ' Shifts';
	} else {
		START_WEEK = 7 * pageNumber;
       	LAST_WEEK = 6 + (7 * pageNumber);
       	isCurrentWk = false;
       	
		weekName = 'Previous ' + Math.abs(pageNumber) + ' week';
		weekTableTitle = 'Previous Week ' + Math.abs(pageNumber) + ' Shifts';
	}

    if(isCurrentWk) {
        previous_firstday = current_firstday.minusDays(START_WEEK);
        previous_last = current_firstday.minusDays(LAST_WEEK);
    } else {
        // for future week's previous week will still be in future from current week
        // hence add days regardless
        previous_firstday = current_firstday.addDays(START_WEEK);
        previous_last = current_firstday.addDays(LAST_WEEK);
    }

    renderCurrentWeekHtml = "<span> Showing "+ weekTableTitle +" </span>";

    // Set hidden inputs
    setCopyInputs(previous_firstday, "first_day");
    setCopyInputs(previous_last, "last_day");

    // set Value of week
    weekDescription.val(weekName);

    console.log('back() currentPage ', currentPage, 'weekName ', weekName, ' START_WEEK ', START_WEEK, ' LAST_WEEK ', LAST_WEEK, 'Page Number', pageNumber);

    $("#currentWk").html(renderCurrentWeekHtml);
    $(".currentWk").html(weekTableTitle);

    showDate(dayOfWeekNumber, todayMonthDate, previous_firstday);

    generateWeekShiftPipeline(previous_firstday, previous_last);

    // Remove Today Label
    let todayMsg = "#current_" + dayOfWeekNumber;
    let todayMsgFooter = "#tfoot_current_" + dayOfWeekNumber;
        
    $(todayMsg).hide();
    $(todayMsgFooter).hide();
}

function holidayAjax(firstDay, lastDay) {
    // console.log(firstDay, lastDay);
    $.ajax({
        type: "GET",
        url: getHolidays,
        dataType: 'json',
        success: function(holidays) {
            showHoliday(holidays, firstDay, lastDay);
        },
        error: function(data) {

            msgHTML = '<div class="alert alert-danger" role="alert">' +
                ajaxMsg[1] +
                '</div>';

            $(elemID).html(msgHTML);
            $('.shift-tb').hide();
        }
    });
}

/**
 * Shows the holiday
 */
function showHoliday(data, firstDay, lastDay){

    let formattedDate, parsedDate;
    let endsWith;
    let disableColumn, officeDay;
    let startsWithHeaderTbl = "current_holiday_";
    let startsWithFooterTbl = "tfoot_holiday_";

    // Get all future holiday dates from yesterday midnight
    let incomingHolidayDates = data.filter((datum) => {
        // string date 27-Jan-2022 into parsable date [2022-Jan-21]
        parsedDate = datePreprocessorFireFoxCompatibility(datum.holiday_date);

        formattedDate = new Date(parsedDate);

        // we use time to compare dates
        return formattedDate.getTime() >= firstDay.getTime() && formattedDate.getTime() <= lastDay.getTime();
    });

    // console.log("filter", incomingHolidayDates);

    // Hide all holiday Label on Table before showing the desired label
    // previous/next action to update 
    hideHolidayLabels(startsWithHeaderTbl);
    hideHolidayLabels(startsWithFooterTbl);

    // append the holidays
    incomingHolidayDates.map((holiday) => {
        parsedDate = datePreprocessorFireFoxCompatibility(holiday.holiday_date);
        formattedDate = new Date(parsedDate);

        // get all holiday Id office boxes on the table
        officeDay = formattedDate.getDay() + 1; // office day starts from 1 but getDay starts from 0
        endsWith = "_day_"+ officeDay;
        
        let endsWithNodeList = document.querySelectorAll('[id$="'+ endsWith +'"]'); // disable all boxes ending with _day_

        // Shed office holiday grey
        endsWithNodeList.forEach(node => {
            
            disableColumn = node.getAttribute("id");
            // console.log("disableColumn", disableColumn);
            // $(`#${disableColumn}`).css({'background': '#efefef'});
            $(`#${disableColumn}`).css({'background': 'rgb(250, 243, 192)'});
            
            // Delete the attributes to disable 
            // $(`#${disableColumn}`).removeAttr("ondrop");
            // $(`#${disableColumn}`).removeAttr("ondragover");
        });

        // Tbl Header Display holiday
        showHolidayLabel(formattedDate.getDay(), holiday.holiday_name, startsWithHeaderTbl);

        // Tbl Footer Display holiday
        showHolidayLabel(formattedDate.getDay(), holiday.holiday_name, startsWithFooterTbl);
    });
}

/**
 * 
 * @param {*} formattedDate 
 * @param {*} holidayName 
 * @param {*} startWithElem 
 */
function showHolidayLabel(formattedDate, holidayName, startWithElem) {
    let displayDayMonthElem, idHoliday;

    idHoliday = startWithElem + formattedDate;
    displayDayMonthElem = document.getElementById(idHoliday);
    displayDayMonthElem.innerHTML = `${holidayName}`;

    $(`#${idHoliday}`).show();

    // console.log("show ID Holiday: ", idHoliday);
}

/**
 * Hide all Holidays Label
 * @param {*} startsWithTbl 
 */
function hideHolidayLabels(startsWithTbl) {
    let hideAllHoliday;      
    let startWithNodeList = document.querySelectorAll('[id^="'+ startsWithTbl +'"]'); // hide all holiday label start with current_holiday_
    // console.log("startWithNodeList: ", startWithNodeList);
    // console.log("startWithNodeList Length: ", startWithNodeList.length);

    startWithNodeList.forEach(node => { 
        
        hideAllHoliday = node.getAttribute("id");
        // console.log("hideAllHoliday: ", hideAllHoliday);

        $(`#${hideAllHoliday}`).hide();
    });
}
