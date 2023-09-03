let setUrl = "";
const PREVIOUS = 'Previous';
const CURRENT = 'Current';
const NEXT = 'Next';
const currentWeekShifts = "Current Week Shifts";
const previousWeekShifts = "Previous Week Shifts";
const followingWeekShifts = "Following Week Shifts";
let getOfficesUrl = "./jsondata?xml=attendance.xml&view=30:5";
let getEmployeesUrl = "./jsondata?xml=attendance.xml&view=55:0";
let getAssignedEmployeesUrl = "./jsondata?xml=attendance.xml&view=30:3";
let getEmployeesOnLeave = "./jsondata?xml=attendance.xml&view=20:5";
let getHolidays = './jsondata?xml=attendance.xml&view=20:4';
let postCopy = './ajax?ajaxfunction=copy_work_schedule&ajaxparams=';
let postDelete = './ajax?ajaxfunction=delete_work_schedule&ajaxparams=';
let cardStatus = "";
let defaultTime = "10:00 - 19:00";
let timeIn = "10:00";
let timeOut = "19:00";

let appendElem = '.employees-listing';
let appendEmployeeLeaveElem = '.employees-on-leave-listing';
let appendElemOffices = '.offices';
let colors = ['', 'shf-purple', 'shf-orange', 'shf-green', 'shf-yellow', 'shf-red', 'shf-blue', 'shf-violet'];
const searchEmployeeField = document.querySelector(".search-employee");

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

//Declare Variables
var active = false;
var currentX;
var currentY;
var initialX;
var initialY;
var xOffset = 0;
var yOffset = 0;
var globalSegementId = document.querySelector("#employee_not_assigned");
var box1 = document.querySelector("#employee_not_assigned");
// var box2 = document.querySelector(".task-list");
var box2 = document.querySelector(".offices");

function dragStart(e) { //when the drag starts
    ev.preventDefault();
    if (e.type === "touchstart") { //if its a touchscreen
      initialX = e.touches[0].clientX - xOffset; //set initial x-cordinate to where it was before drag started
      initialY = e.touches[0].clientY - yOffset; //set initial y-cordinate to where it was before drag started
    } else { //if its not a touchscreen (mouse)
      initialX = e.clientX - xOffset; //set initial x-cordinate to where it was before drag started
      initialY = e.clientY - yOffset; //set initial y-cordinate to where it was before drag started
    }
    if (e.target === globalSegementId) { //if user is dragging circle
      active = true; //the drag is active
    }
}

function setTranslate(xPos, yPos, el) { //Im not sure what this does but it dosnt work without it
    el.style.transform = "translate3d(" + xPos + "px, " + yPos + "px, 0)";
}

function allowDrop(ev) {
    'use strict';
    if ($(ev.target).hasClass("task-list")) {
        ev.preventDefault();
    }
}

function drag(ev, segId) {
    let segmentId = segId?.id;
    globalSegementId = segId;
    ev.dataTransfer.setData("text", segmentId);

    // Get parent div where the card is dragged from
    let parent_id = document.getElementById(segmentId).parentElement;
    let fromParentElem = parent_id.id;

    console.log("Drag Parent -> " + fromParentElem);
    console.log("SegmentId Current Card Being dragged -> " + segmentId);

    // Ifparent is from container 
    if (fromParentElem == "employee_not_assigned") {
        setUrl = 'ajaxinsert?app_xml=attendance.xml&app_key=55:1';
        cardStatus = "employee_not_assigned";
    } else {
        cardStatus = "";
        setUrl = 'ajaxupdate?app_xml=attendance.xml&oper=form&app_key=55:2';
        // alert('Trial');
    }

    if (active) { //if the drag is active
        e.preventDefault(); //the user cant do anything else but drag
      
        if (e.type === "touchmove") { //if its a touchscreen
          currentX = e.touches[0].clientX - initialX; //set current x-cordinate to where it is now
          currentY = e.touches[0].clientY - initialY; //set current y-cordinate to where it is now
        } else { //if its not a touchscreen (mouse)
          currentX = e.clientX - initialX; //set current x-cordinate to where it is now
          currentY = e.clientY - initialY; //set current y-cordinate to where it is now
        }
        
        //Im not sure what this does but it dosnt work without it
        xOffset = currentX;
        yOffset = currentY;
        setTranslate(currentX, currentY, segId);
      }
}

function drop(ev, segementId, office, day) {
    ev.preventDefault();
    var draggedElemId = ev.dataTransfer.getData("text");
    let sheduleDate = generateCurrentDateFromDay(day);
    let sData = {};
    let resovedDate;

    resovedDate = dateWeekResolver(sheduleDate);

    if (cardStatus == "employee_not_assigned") {
        // clone here
        droppedId = document.getElementById(draggedElemId).cloneNode(true);
        ev.target.appendChild(droppedId);

        sData = [{
            entity_id: draggedElemId,
            location_id: office,
            schedule_date: resovedDate,
            time_in: timeIn,
            time_out: timeOut
        }];
    } else {
        droppedId = document.getElementById(draggedElemId);
        // var droppedIdVal = droppedId.innerText; // get text between
        ev.target.appendChild(droppedId);
        let assignmentArr = draggedElemId.split("_");

        sData = [{
            KF: assignmentArr[1],
            location_id: office,
            schedule_date: resovedDate,
            time_in: timeIn,
            time_out: timeOut
        }];
    }

    let first = date.getDate() - date.getDay(); // First day is the day of the week - the day of the week
    let sheduleDay = first + day; // last day is the first day + 6
    // console.log("sheduleDay + > " + date);
    // let sheduleDate = new Date(date.setDate(sheduleDay));

    var jsonData = JSON.stringify(sData);

    // console.log(jsonData);

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
            // window.location.reload();
            pageRefresh();
        },
        error: function(data) {}
    });


    const box1Size = box1.getBoundingClientRect(); //the size of box1
    const box2Size = box2.getBoundingClientRect(); //the size of box2
    const elementSize = globalSegementId.getBoundingClientRect(); //the size of the circle
    if (elementSize.left >= box1Size.left && elementSize.right <= box1Size.right && elementSize.top >= box1Size.top && elementSize.bottom <= box1Size.bottom) { //if the circle is in box1
      initialX = currentX; //set the initial x-cordinate to where it is now
      initialY = currentY; //set the initial y-cordinate to where it is now
    } else if (elementSize.left >= box2Size.left && elementSize.right <= box2Size.right && elementSize.top >= box2Size.top && elementSize.bottom <= box2Size.bottom) { //if the circle is in box2
      initialX = currentX; //set the initial x-cordinate to where it is now
      initialY = currentY; //set the initial y-cordinate to where it is now
    }
    else { //if the circle is in neither box1 nor box2
         currentX = 0;
      currentY = 0;
        initialX = 0;
      initialY = 0;
      xOffset = 0;
      yOffset = 0;
        setTranslate(0, 0, globalSegementId);
    }
    
    active = false; //the drag is no longer active

}


//Add Event Listeners for Touchscreens
globalSegementId.addEventListener("touchstart", dragStart, false);
globalSegementId.addEventListener("touchend", drop, false);
globalSegementId.addEventListener("touchmove", drag, false);

//Add Event Listeners for Mice
globalSegementId.addEventListener("mousedown", dragStart, false);
globalSegementId.addEventListener("mouseup", drop, false);
globalSegementId.addEventListener("mousemove", drag, false);

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

    // console.log("weekDesc", weekDesc);
    // console.log("defaultSheduleDate", defaultSheduleDate);
    // console.log("defaultSheduleDate.getDate()", defaultSheduleDate.getDate());
    // console.log("Current Week State", weekState);

    if (weekState == CURRENT_WEEK) {
        processedDate = defaultSheduleDate;
    } else if (weekState == NEXT_WEEK) {
        processedDate = defaultSheduleDate.addDays(7);
    } else if (weekState == PREVIOUS_WEEK) {
        processedDate = defaultSheduleDate.minusDays(7);
    }

    // console.log("processedDate", processedDate);

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

/**
 * Gets the date from dragged day
 * @param {*} day 
 * @returns 
 */
function generateCurrentDateFromDay(day) {
    // Get the current 1st week
    let date = new Date();
    let dragdDate;
    let first = date.getDate() - date.getDay(); // First day is the day of the month - the day of the week
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
ajaxCall('GET', getEmployeesUrl, '', ajaxMsg, '#errorAlert');
ajaxCall('GET', getEmployeesOnLeave, '', ajaxMsg, '#errorAlert');
// ajaxCall('GET', getHolidays, '', ajaxMsg, '#errorAlert');
// ajaxCall('GET', getAssignedEmployeesUrl, '', ajaxMsg, '#errorAlert');


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

// Load Current Week
let currentWeek = getPage();

let renderCurrentWeekHtml = "";
let renderCurrentWeekCopyHtml = "";
switch(currentWeek) {
    case PREVIOUS:
        // console.log("switch PREVIOUS");
        back();
        renderCurrentWeekHtml = "<span> Showing "+ previousWeekShifts +" </span>";
        renderCurrentWeekCopyHtml = previousWeekShifts;
        break;

    case CURRENT:
        // console.log("switch CURRENT");
        renderCurrentWeekHtml = "<span> Showing "+ currentWeekShifts +" </span>";
        renderCurrentWeekCopyHtml = currentWeekShifts;
        current();
        break;

    case NEXT:
        // console.log("switch NEXT");
        renderCurrentWeekHtml = "<span> Showing "+ followingWeekShifts +" </span>";
        renderCurrentWeekCopyHtml = followingWeekShifts;
        next();
        break;

    default:
        current();
        renderCurrentWeekHtml = "<span> Showing "+ currentWeekShifts +" </span>";
        renderCurrentWeekCopyHtml = currentWeekShifts;
        // console.log("default then current");
        break;
}
$("#currentWk").html(renderCurrentWeekHtml);
$(".currentWk").html(renderCurrentWeekCopyHtml);


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
 * React like state
 * @returns 
 */
function useState() {
    let _state = null;
  
    function getState() {
      return _state;
    }
  
    function setState(data) {
      _state = [...data];
    }
  
    return [getState, setState];
  }

  const [getState, setState] = useState();

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
            // save employee data
            setState(data);
            break;

        case getAssignedEmployeesUrl:
            locateEmployeeShift(data);
            break;

        case getEmployeesOnLeave:
            // console.log("getEmployeesOnLeave ",data);
            employeeCardTemplateGenerator(appendEmployeeLeaveElem, data);
            break;
        // case getHolidays:
        //     // console.log("showHoliday ",data);
        //     showHoliday(data);
        //     break;

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
        if (appendElem == appendEmployeeLeaveElem)
            employeeListingTemplateCard += '<div class="card task-box mb-10" id="' + data[i].entity_id + '">';
        else
            employeeListingTemplateCard += '<div class="card task-box mb-10" id="' + data[i].entity_id + '"  ondragstart="drag(event, this)">';

        // employeeListingTemplateCard += '<div class="card task-box mb-10" id="' + data[i].entity_id + '" draggable="true" ondragstart="drag(event, this)">' +
        employeeListingTemplateCard += '<div class="card-body">' +
            '<div class="dropdown float-right">' +
            '<a href="#" class="dropdown-toggle arrow-none" data-toggle="dropdown" aria-expanded="false"><i class="mdi mdi-dots-vertical m-0 text-muted h5"></i></a><div class="dropdown-menu dropdown-menu-right">' +
            // '<textarea placeholder="Add Comment"> </textarea>' +
            '<a class="dropdown-item" href="#" onclick="event.preventDefault();openModal(' + data[i].entity_id + ');"><i class="bx bx-edit"></i> Add Comment</a>' +
            // '<a class="dropdown-item" href="#" onclick="event.preventDefault();changeColor(' + data[i].entity_id + ', \'' + colors[1] + '\', this);"><span class="color-square ' + colors[1] + '"></span> Option 1</a>' +
            '</div>' +
            '</div>' +
            '<div>' +
            '<a href="javascript: void(0);" class="text-muted">' +
            '<ul>';
        if (appendElem == appendEmployeeLeaveElem)
            employeeListingTemplateCard += '<li>' + data[i].entity_name + '</li>' +
            '<li>From: ' + data[i].leave_from + ' <br/>To: ' + data[i].leave_to + '</li>';
        else
            employeeListingTemplateCard += '<li>' + data[i].first_name + ' ' + data[i].surname + '</li>' +
            '<li>' + defaultTime + '</li>';

        employeeListingTemplateCard += ' </ul>' +
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
                weekListingTemplateCard += '<td id="office_' + data[i].location_id + '_day_' + day + '" class="task-list" ondrop="drop(event, this ,' + data[i].location_id + ', ' + day + ')" ondragover="allowDrop(event)">' +

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

    if (validateCurrentWeek(date, firstday)) {
        return dayWeeek;
    }
    return -1;

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
    let time = firstday.getTime();

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

    employeeListingTemplateCard = '<div class="card task-box mb-10 ' + data.schedule_label + '" id="' + idElem + '" draggable="true" ondragstart="drag(event, this)">' +
        '<div class="card-body">' +
        '<div class="dropdown float-right">' +
        '<a href="#" class="dropdown-toggle arrow-none" data-toggle="dropdown" aria-expanded="false"><i class="mdi mdi-dots-vertical m-0 text-muted h5"></i></a><div class="dropdown-menu dropdown-menu-right">' +
        '<a class="dropdown-item" href="#" onclick="event.preventDefault();openModal(\'' + data.KF + '\',\'' + data.employee_id + '\',\'' + data.time_in + '\',\'' + data.time_out + '\',\'' + data.schedule_label + '\',\'' + data.details + '\');"><i class="bx bx-edit"></i> Add Comment</a>' +
        '<a class="dropdown-item" style="color: #f46a6a" href="#" onclick="event.preventDefault();deleteCardModal(\'' + data.KF + '\');"><i class="bx bx-trash"></i> Unassign User </a>' +
        '</div>' +
        '</div>' +
        '<div>' +
        '<a href="javascript: void(0);" class="text-muted">' +
        '<ul>' +
        '<li>' + data.entity_name + '</li>' +
        '<li>(' + data.time_in + ' - ' + data.time_out + ')</li>';
        if(data.details != "") {
            employeeListingTemplateCard += '<li><br>Comment: <br>' + data.details + '</li>';
        }
        employeeListingTemplateCard += ' </ul>' +
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
        // window.location.reload();
        pageRefresh();
        // console.log('AJAX Auth');
        // console.log(data);
        // console.log("Roles Count => " + data.roles.length);

    }, "JSON");

});

/**
 * ===================== TABLE SCRIPTS ========================
 */

/**
 * Table JS Below
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
// console.log('Sunday', firstday);

// console.log('Saturday', firstday.addDays(6));
// console.log('Sunday', firstday);
// console.log('-1: Saturday', firstday.minusDays(1));

/**
 * Generate Table Header Name, Day and Today Label
 * @param {*} dayOfWeekNumber 
 * @param {*} todayMonthDate 
 * @param {*} firstday 
 * @param {*} dayOfWeekNumber 
 */
function showDate(dayOfWeekNumber, todayMonthDate, firstday, showCurrent = true) {

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

        // Display Header Table Days
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
 * ===================== WEEK GENERATION PIPELINE BEGIN HERE ========================
 */


/**
 * Generate the offices again
 * to clear the board
 * @returns 
 */
function generateWeekShiftPipeline(firstDay, lastDay) {
    // console.log("=================== generateWeekShiftPipeline ===========================");
    // console.log("generateWeekShiftPipeline(firstDay, lastDay)", firstDay, lastDay);

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

    // console.log("weekAjax", firstDay, lastDay);
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
        day = day + 1;
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
 * to append 0 in a single digit number eg 09
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
 * General 
 * @param {*} response 
 * @param {*} responseType 
 * @param {*} viewPortLocation 
 */
function makeAlert(response, responseType, viewPortLocation){
    new Noty({
        text: response,
        type: responseType,
        timeout: 5000,
        layout: viewPortLocation
    }).show();
}

/**
 * 
 * @param {*} response 
 * @param {*} responseType 
 * @param {*} viewPortLocation 
 */
function confirmAlert(response, shiftActionType){

    let confirm = new Noty({
        text: response,
        buttons: [
          Noty.button('YES', 'btn btn-success', function () {
            //   console.log('button 1 clicked and sent request.');
              shiftAjax(shiftActionType);
              confirm.close();
          }, {id: 'button1', 'data-status': 'ok'}),
      
          Noty.button('NO', 'btn btn-error', function () {
            //   console.log('button 2 clicked');
              confirm.close();
          })
        ]
      });

      confirm.show();
}

/**
 * 
 * @param {*} postCopyUrl 
 * @param {*} jsonData 
 */
function shiftAjax(shiftActionType){

    let firstDate = $("#first_day").val();
    let lastDate = $("#last_day").val();
    let jsonData = {
        firstDate,
        lastDate
    }

    jsonData = JSON.stringify(jsonData);

    // make url
    let requestUrl = shiftActionType == 'copy' ? postCopy + firstDate : postDelete + firstDate; 

    $.post(requestUrl, {
        form_data: jsonData
    }, function(data) {

        let response = data?.success ? data.msg : "Something went wrong";
        let type = data?.success ? 'success' : "warning";

        makeAlert(response, type, 'topRight');

        // console.log("shiftAjax currentActiveWeek", currentActiveWeek);
        // fetch again for update
        pageRefresh();
   
    }, "JSON");
}

/**
 * Page Refresh 
 */
function pageRefresh(){

    let currentActiveWeek = getPage();

    switch(currentActiveWeek) {
        case PREVIOUS:
            back();
            break;
    
        case CURRENT:
            current();
            break;
    
        case NEXT:
            next();
            break;
    
        default:
            current();
            break;
    } 
}

/**
 * Send request to copy shifts
 */
function copyShift() {

    confirmAlert('Are you sure you want to copy?', 'copy');
}

/**
 * Send request to delete shifts
 */
function deleteShift() {

    confirmAlert('Are you sure you want to clear board?', 'delete');
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

/**
 * converts
 * Sat Mar 05 2022 00:00:00 GMT+0300 (East Africa Time)
 * to 2022-03-05
 * @param {*} str 
 * @returns 
 */
function convertStrToDateFormat(str) {
    let date = new Date(str),
      month = ("0" + (date.getMonth() + 1)).slice(-2),
      day = ("0" + date.getDate()).slice(-2);
    // return [date.getFullYear(), mnth, day].join("-");
    return date_formatter(date.getFullYear(), month, day, true);
}

function next() {
    // console.log("next()");

    // Localstorage Track next page
    setPage(NEXT);

    const START_WEEK = 7;
    const LAST_WEEK = 13;
    let formattedDate = getCurrentDateFromMidnight();
    let date = new Date(formattedDate);
    let dayOfWeekNumber = date.getDay();
    let todayMonthDate = date.getMonth() + 1;
    let first = date.getDate() - date.getDay();
    let current_firstday = new Date(date.setDate(first));
    let next_firstday = current_firstday.addDays(START_WEEK);
    let last_firstday = current_firstday.addDays(LAST_WEEK);
    renderCurrentWeekHtml = "<span> Showing "+ followingWeekShifts +" </span>";

    // set Value of week
    weekDescription.val(NEXT_WEEK);

    // Set hidden inputs as null
    setCopyInputs(next_firstday, "first_day");
    setCopyInputs(last_firstday, "last_day");

    $("#currentWk").html(renderCurrentWeekHtml);
    $(".currentWk").html(followingWeekShifts);

    showDate(dayOfWeekNumber, todayMonthDate, next_firstday);

    // Remove Today Label
    let todayMsg = "#current_" + dayOfWeekNumber;
    let todayMsgFooter = "#tfoot_current_" + dayOfWeekNumber;
    $(todayMsg).hide();
    $(todayMsgFooter).hide();

    generateWeekShiftPipeline(next_firstday, last_firstday);

}

function current() {
    // console.log("current()");

    // Localstorage Track current page
    setPage(CURRENT);

    let formattedDate = getCurrentDateFromMidnight();
    let date = new Date(formattedDate);
    let dayOfWeekNumber = date.getDay();
    let todayMonthDate = date.getMonth() + 1;
    let first = date.getDate() - date.getDay();
    let current_firstday = new Date(date.setDate(first));
    let current_lastday = current_firstday.addDays(6);
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

function back() {
    // console.log("back()");

    // Localstorage Track next page
    setPage(PREVIOUS);
    
    const START_WEEK = 7;
    const LAST_WEEK = 1;
    let formattedDate = getCurrentDateFromMidnight();
    let date = new Date(formattedDate);
    let dayOfWeekNumber = date.getDay();
    let todayMonthDate = date.getMonth() + 1;
    let first = date.getDate() - date.getDay();

    let current_firstday = new Date(date.setDate(first));
    let previous_firstday = current_firstday.minusDays(START_WEEK);
    let previous_last = current_firstday.minusDays(LAST_WEEK);
    renderCurrentWeekHtml = "<span> Showing "+ previousWeekShifts +" </span>";

    // Set hidden inputs
    setCopyInputs(previous_firstday, "first_day");
    setCopyInputs(previous_last, "last_day");

    // set Value of week
    weekDescription.val(PREVIOUS_WEEK);

    $("#currentWk").html(renderCurrentWeekHtml);
    $(".currentWk").html(previousWeekShifts);

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
    // console.log("showHoliday", data, firstDay, lastDay);
    let formattedDate, parsedDate;
    let endsWith;
    let disableColumn, officeDay;
    let startsWithHeaderTbl = "current_holiday_";
    let startsWithFooterTbl = "tfoot_holiday_";

    // Get all future holiday dates from yesterday midnight
    let incomingHolidayDates = data.filter((datum) => {
        // string date 27-Jan-2022 into parsable date [2022-Jan-21]
        // parsedDate = dateWeekResolverPreprocessor(datum.holiday_date);
        parsedDate = datePreprocessorFireFoxCompatibility(datum.holiday_date);
        // console.log("parsedDate: ", parsedDate);

        formattedDate = new Date(parsedDate);
        // console.log("formattedDate", formattedDate, firstDay, lastDay);

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
            
            // Delete the attributes to disable dragging
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
        // console.log("show ID Holiday: ", idHoliday);
    displayDayMonthElem = document.getElementById(idHoliday);
    displayDayMonthElem.innerHTML = `${holidayName}`;

    $(`#${idHoliday}`).show();
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


function handleSearchInputChange(e) {
    const searchValue = e.target.value.toLowerCase();

    const data = getState();

    const filteredItems = data.filter((item) =>
      (item.first_name.toLowerCase().includes(searchValue) 
      || item.surname.toLowerCase().includes(searchValue))
    );
    employeeCardTemplateGenerator(appendElem, filteredItems);
}

// bind the search with keyup event
searchEmployeeField.addEventListener("keyup", handleSearchInputChange);