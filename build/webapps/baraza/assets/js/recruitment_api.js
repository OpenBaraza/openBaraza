
// Error Msg
let ajaxMsg = [];
ajaxMsg[1] = "Something Went Wrong! Contact Us as soon as possible.";


let fetchAllVacanciesUrl = "./jsongeneral?xml=application.xml&view=20:0";
let fetchClientVacanciesUrl = "./jsongeneral?xml=application.xml&view=22:0";
let fetchJobCategoriesUrl = "./jsongeneral?xml=application.xml&view=20:2";
let fetchAllInternshipsUrl = "./jsongeneral?xml=application.xml&view=25:0";

let fetchWorkCategorysUrl = "./jsongeneral?xml=application.xml&view=35:2";
let fetchAllResourcingUrl = "./jsongeneral?xml=application.xml&view=35:0";
let fetchAllVolunteerUrl = "./jsongeneral?xml=application.xml&view=35:1";


/**
 * General Ajax
 * @param {*} method 
 * @param {*} url 
 * @param {*} jsonData 
 * @param {*} ajaxMsg 
 * @param {*} elemID 
 * @param {*} htmlMsg 
 */
function ajaxCall(url, jsonData, ajaxMsg, elemID, fnct) {

    var msgHTML = "";
    $.ajax({
        type: 'GET',
        url: url,
        dataType: 'json',
        success: function(data) {
			console.log(data);
			
            getAjaxData(data, url, elemID, fnct);

            // window.location.href = '/dashboard';
        },
        error: function(data) {

            msgHTML = '<div class="callout small-12 alert" role="alert">' +
                ajaxMsg[1] +
                '</div>';

            $(elemID).html(msgHTML);
            $('.shift-tb').hide();
        }
    });

}

/**
 * Filters Data
 * @param {*} data 
 */
function getAjaxData(data, url, elemID, fnct) {
    // console.log("getAjaxData url+> " + url);

    let msgHTML = "";

    if (fnct == 1) {
        fetchJobCategories(data, elemID);
    } else if (fnct == 2) {
        getJobListing(data, elemID);
    } else if (fnct == 3) {
        getInternshipListing(data, elemID);
    } else if (fnct == 4) {
    	fetchWorkCategorys(data, elemID);
    } else if (fnct == 5) {
    	getOpportunityListing(data, elemID);
    } else if (fnct == 6) {
    	getVolunteerListing(data, elemID);
    } else if (fnct == 7) {
        getClientJobListing(data, elemID);
    }
}

function fetchJobCategories(data, elemID) {
    for (let i = 0; i < data.length; i++) {
       $(elemID).append($('<option></option>').attr("value", data[i].job_category_id).text(data[i].job_category_name));
    }
}

function fetchWorkCategorys(data, elemID) {
    for (let i = 0; i < data.length; i++) {
       $(elemID).append($('<option></option>').attr("value", data[i].work_category_id).text(data[i].work_category_name));
    }
}

function getJobListing(data, elemID) {
    let jLHtml = "";

    for (let i = 0; i < data.length; i++) {

        jLHtml += '<div class="result_job">' +
            '<div class="job_title">' + data[i].department_role_name + '</div>' +
            '<div class="job_category_name">' + '<img src="./assets/images/workers.png">'+ '&ensp;' + data[i].job_category_name + '</div>' +
            '<div class="job_category">'  + '<img src="./assets/images/navigation.png">' + '&ensp;'+ data[i].location_name + '</div>' +
            '<div class="job_ref">' + '<img src="./assets/images/canceled.png">' + '&ensp;Closing Date : ' + data[i].closing_date + '</div>' +
            '<div class="job_action">' +
            '<a class="button hollow primary" href="./recruitment_detail.jsp?work_type=1&link=' + data[i].KF + '">' +
            'VIEW JOB' +
            '</a>' +
            '</div>' +
            '</div>';
    }

    $(elemID).html(jLHtml);
}

function getClientJobListing(data, elemID) {
    let jLHtml = "";

    for (let i = 0; i < data.length; i++) {

        jLHtml += '<div class="result_job">' +
            '<div class="job_title">' + data[i].job_title + '</div>' +
            '<div class="job_category_name">' + '<img src="./assets/images/workers.png">'+ '&ensp;' + data[i].job_category_name + '</div>' +
            '<div class="job_category">'  + '<img src="./assets/images/navigation.png">' + '&ensp;'+ data[i].location_name + '</div>' +
            '<div class="job_ref">' + '<img src="./assets/images/canceled.png">' + '&ensp;Closing Date : ' + data[i].closing_date + '</div>' +
            '<div class="job_action">' +
            '<a class="button hollow primary" href="./recruitment_detail.jsp?work_type=3&link=' + data[i].KF + '">' +
            'VIEW JOB' +
            '</a>' +
            '</div>' +
            '</div>';
    }

    $(elemID).html(jLHtml);
}

function getInternshipListing(data, elemID) {

    let jLHtml = "";

    for (let i = 0; i < data.length; i++) {

        jLHtml += '<div class="result_job">' +
            '<div class="job_title">' + data[i].department_name + '</div>' +
            '<div class="job_category_name">' + '<img src="./assets/images/workers.png">'+ '&ensp;' + data[i].job_category_name + '</div>' +
            '<div class="job_category">'  + '<img src="./assets/images/navigation.png">' + '&ensp;' + data[i].location + '</div>' +
            '<div class="job_ref">'  + '<img src="./assets/images/canceled.png">' + '&ensp;Closing Date : ' + data[i].closing_date + '</div>' +
            '<div class="job_action">' +
		    '<a class="button hollow primary" href="./recruitment_detail.jsp?work_type=2&link=' + data[i].KF + '">' +
		    'VIEW JOB' +
		    '</a>' +
		    '</div>' +
		    '</div>';
    }


    $(elemID).html(jLHtml);
}

function getOpportunityListing(data, elemID) {

    let cnHtml = "";
    let ptHtml = "";
    let vlHtml = "";

    for (let i = 0; i < data.length; i++) {
        let oHtml = '<div class="result_job">' +
        	'<div class="job_category_name">' + data[i].industry_name + '</div>' +
			'<div class="job_category_name">' + data[i].work_category_name + '</div>' +
		    '<div class="job_title">' + data[i].opportunity_name + '</div>' +
		    '<div class="job_category">' + data[i].location + '</div>' +
		    '<div class="job_ref">' + 'Closing Date : ' + data[i].closing_date + '</div>' +
		    '<div class="job_action">' +
		    '<a class="button hollow primary" href="./resourcing_detail.jsp?work_type=' + data[i].opportunity_type_id
		    + '&link=' + data[i].KF + '">VIEW</a>' +
		    '</div>' +
		    '</div>';
		    
		if(data[i].opportunity_type_id == '1') {
			cnHtml += oHtml;
		} else if(data[i].opportunity_type_id == '2') {
			ptHtml += oHtml;
		} else if(data[i].opportunity_type_id == '3') {
			vlHtml += oHtml;
		}

    }


    $('#consolting_list').html(cnHtml);
    $('#part_time_list').html(ptHtml);
}

function getVolunteerListing(data, elemID) {

    let cnHtml = "";
    let ptHtml = "";
    let vlHtml = "";

    for (let i = 0; i < data.length; i++) {
        let oHtml = '<div class="result_job">' +
        	'<div class="job_category_name">' + data[i].industry_name + '</div>' +
			'<div class="job_category_name">' + data[i].work_category_name + '</div>' +
		    '<div class="job_title">' + data[i].opportunity_name + '</div>' +
		    '<div class="job_category">' + data[i].location + '</div>' +
		    '<div class="job_ref">' + 'Closing Date : ' + data[i].closing_date + '</div>' +
		    '<div class="job_action">' +
		    '<a class="button hollow primary" href="./resourcing_detail.jsp?work_type=' + data[i].opportunity_type_id
		    + '&link=' + data[i].KF + '">VIEW</a>' +
		    '</div>' +
		    '</div>';
		    
		if(data[i].opportunity_type_id == '1') {
			cnHtml += oHtml;
		} else if(data[i].opportunity_type_id == '2') {
			ptHtml += oHtml;
		} else if(data[i].opportunity_type_id == '3') {
			vlHtml += oHtml;
		}

    }

    $('#volunteer_list').html(vlHtml);
}

function getCurrentUrlParameter() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    let linkId = urlParams.get('link');

    return linkId;
}

function listAllJobs() {
    ajaxCall(fetchJobCategoriesUrl, '', ajaxMsg, '#cmb_job_category', 1);
    
    ajaxCall(fetchAllVacanciesUrl, '', ajaxMsg, '#job_list', 2);
    
    ajaxCall(fetchAllInternshipsUrl, '', ajaxMsg, '#internship_list', 3);

    ajaxCall(fetchClientVacanciesUrl, '', ajaxMsg, '#client_job_list', 7);
}

function listFilteredJobs(categoryId) {
    let fetchFilteredVacanciesUrl = fetchAllVacanciesUrl + "&where=job_category_id=" + categoryId;
    
    ajaxCall(fetchFilteredVacanciesUrl, '', ajaxMsg, '#job_list', 2);

    let fetchFilteredClientVacanciesUrl = fetchClientVacanciesUrl + "&where=job_category_id=" + categoryId;
    
    ajaxCall(fetchFilteredClientVacanciesUrl, '', ajaxMsg, '#client_job_list', 7);

    let fetchFilteredInternshipsUrl = fetchAllInternshipsUrl + "&where=job_category_id=" + categoryId;
    
    ajaxCall(fetchFilteredInternshipsUrl, '', ajaxMsg, '#internship_list', 3);
}

function listAllResourcing(resourceType) {
    ajaxCall(fetchWorkCategorysUrl, '', ajaxMsg, '#cmb_work_category', 4);
    
    if(resourceType == 1) {
    	ajaxCall(fetchAllResourcingUrl, '', ajaxMsg, '#consolting_list', 5);
    } else if(resourceType == 2) {
    	ajaxCall(fetchAllVolunteerUrl, '', ajaxMsg, '#consolting_list', 6);
    }
}

function listFilteredResourcing(categoryId, resourceType) {
    if(resourceType == 1) {
		let fetchFilteredResourcingUrl = fetchAllResourcingUrl + "&where=work_category_id=" + categoryId;
		
		ajaxCall(fetchFilteredResourcingUrl, '', ajaxMsg, '#consolting_list', 5);
    } else if(resourceType == 2) {
    	let fetchFilteredVolunteerUrl = fetchAllVolunteerUrl + "&where=work_category_id=" + categoryId;
		
		ajaxCall(Volunteer, '', ajaxMsg, '#consolting_list', 6);
	}
}






