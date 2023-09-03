// Error Msg
let ajaxMsg = [];
ajaxMsg[1] = "Something Went Wrong! Contact Us as soon as possible.";

let fetchSingleJobUrl = "./jsongeneral?xml=application.xml&view=20:0:0&linkdata=";
let fetchClientJobUrl = "./jsongeneral?xml=application.xml&view=22:0:0&linkdata=";
let fetchSingleInternshipUrl = "./jsongeneral?xml=application.xml&view=25:0:0&linkdata=";
let fetchSingleResourcingUrl = "./jsongeneral?xml=application.xml&view=35:0:0&linkdata=";

const EmailNotFound = "Email found";
const EmailNotFoundUserDisplay = "Complete security check. Kindly log in to your email to complete verification";
const ApplicationAdded = "Application already added";
const ApplicationAddedUserDisplay = "You have already applied for this position";


/**
 * General Ajax
 * @param {*} url 
 * @param {*} ajaxMsg 
 * @param {*} fnct 
 */
function ajaxCall(url, ajaxMsg, fnct) {

    var msgHTML = "";
    $.ajax({
        type: 'GET',
        url: url,
        dataType: 'json',
        success: function(data) {

            getAjaxData(data, url, fnct);

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
function getAjaxData(data, url, fnct) {
    // console.log("getAjaxData url+> " + url);

    let msgHTML = "";

	if (fnct == 1) {
        singleJobDetails(data);
    } else if (fnct == 2) {
    	singleInternshipDetails(data);
    } else if (fnct == 3) {
    	clientJobDetails(data);
    } else if (fnct == 4) {
    	singleResourcingDetails(data);
    }
}

function singleJobDetails(data) {
    console.log(data);

    for (let i = 0; i < data.length; i++) {
        $('#single_job_title').html(data[i].department_role_name);
        $('#single_job_title').css({ 'text-transform': 'uppercase' });
        
        $('#single_job_ref').html("<img src='./assets/images/audi.png'>" + "&ensp;" + data[i].job_intake_ref);
        $('#single_job_positions').html("<img src='./assets/images/position.png'>" + "&ensp;" + "Positions : " + data[i].positions);
        $('#single_job_closing').html("<img src='./assets/images/canceled.png'>" + "&ensp;" + "Closing Date : " + data[i].closing_date);
        $('#single_job_location').html("<img src='./assets/images/navigation.png'>" + "&ensp;" + data[i].location_name);
        $('#single_job_category').html("<img src='./assets/images/workers.png'>" + "&ensp;" + data[i].job_category_name);
        $('#single_job_details').html(data[i].job_description);
        $('#single_job_requirements').html(data[i].job_requirements);
    }
}


function singleInternshipDetails(data) {
    console.log(data);

    for (let i = 0; i < data.length; i++) {
        $('#single_internship_title').html(data[i].internship_title);
        $('#single_internship_title').css({ 'text-transform': 'uppercase' });
        
        $('#single_internship_ref').html("<img src='./assets/images/audi.png'>" + "&ensp;" + data[i].request_ref);
        $('#single_internship_positions').html("<img src='./assets/images/position.png'>" + "&ensp;" + "Positions : " + data[i].positions);
        $('#single_internship_closing').html("<img src='./assets/images/canceled.png'>" + "&ensp;" + "Closing Date : " + data[i].closing_date);
        $('#single_internship_location').html("<img src='./assets/images/navigation.png'>" + "&ensp;" + data[i].location);
        $('#single_internship_duration').html("Duration : " + data[i].duration + " Months");
        $('#single_internship_category').html("<img src='./assets/images/workers.png'>" + "&ensp;" + data[i].job_category_name);
        $('#single_internship_details').html(data[i].details);
        $('#single_internship_requirements').html(data[i].job_requirements);
    }
}

function clientJobDetails(data) {
    console.log(data);

    for (let i = 0; i < data.length; i++) {
        $('#client_job_title').html(data[i].job_title);
        $('#client_job_title').css({ 'text-transform': 'uppercase' });
        
        $('#client_job_ref').html("<img src='./assets/images/calendar.png'>" + "&ensp;" + "Contract Period : " + data[i].contract_period + "&ensp;" + "months");
        $('#client_job_positions').html("<img src='./assets/images/position.png'>" + "&ensp;" + "Positions : " + data[i].positions);
        $('#client_job_closing').html("<img src='./assets/images/canceled.png'>" + "&ensp;" + "Closing Date : " + data[i].closing_date);
        $('#client_job_location').html("<img src='./assets/images/navigation.png'>" + "&ensp;" + data[i].location_name);
        $('#client_job_category').html("<img src='./assets/images/workers.png'>" + "&ensp;" + data[i].job_category_name);
        $('#client_job_details').html(data[i].job_description);
        // $('#client_job_requirements').html(data[i].job_requirements);
    }
}

function singleResourcingDetails(data) {
    console.log(data);

    for (let i = 0; i < data.length; i++) {
		$('#single_opportunity_title').html(data[i].opportunity_name);
		$('#single_opportunity_title').css({ 'text-transform': 'uppercase' });
		$('#single_opportunity_ref').html(data[i].request_ref);
		$('#single_opportunity_positions').html("Positions : " + data[i].positions);
		$('#single_opportunity_closing').html("Closing Date : " + data[i].closing_date);
		$('#single_opportunity_location').html(data[i].location);
		$('#single_opportunity_duration').html("Duration : " + data[i].duration);
		$('#single_opportunity_industry').html(data[i].industry_name);
		$('#single_opportunity_category').html(data[i].work_category_name);
		$('#single_opportunity_summary').html(data[i].opportunity_summary);
		$('#single_opportunity_requirements').html(data[i].job_requirements);
    }

}

function getCurrentUrlParameter() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    let linkId = urlParams.get('link');

    return linkId;
}

function validateEmail(email) {
	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return re.test(email);
}

function validate(form) {
	let valid = true;
	$.each(form, function (i, field) {
		$("[name='"+field.name+"']").prev('.text-danger').remove();
		if (field.value=="" && $("[name='"+field.name+"']").prop('required')) {
			valid = false;
			$("[name='"+field.name+"']").css({"border":"1px solid #ff000087"});
			$("[name='"+field.name+"']").before('<span class="text-danger">This field is required *</span>');
		}
    });
    return valid;
}

function handleSendApplicantLink(job_id) {
    console.log('============= handleSendApplicantLink =============');
    // Send Link 
    $('.btn_sign_in').click(function(event) {
        event.preventDefault();

        // set the 
        let username = $("#email").val();
        // let entityId = $('#entityId').val();
        let entityId = '';
        let search_url = "/hcmke/recruitment?tag=search_email&email=" + username;

        let msgHTML = '';
        let userMessage = '';
        if (validateEmail(username)) {
            
            $.post(search_url, {
                form_data: username
            }, function(data) {
                console.log(data);
    
                if (data.success == 1) {
                    // set entity ID
                    entityId = data.entityId;
                    let sendUrl = "/hcmke/recruitment?tag=apply_job&email=" + username + "&entity_id=" + entityId + "&intake_id=" + job_id;
                  

                    let form_data = {
                        email: username,
                        entity_id: entityId,
                        intake_id: job_id
                    };
    
                    $.post(sendUrl, { apply_job: form_data }, function(data) {

                        let respMessage = data.message;
    
                        if (data.success == 1) {

                            if( respMessage == ApplicationAdded){
                                userMessage = ApplicationAddedUserDisplay;
                            }else if( respMessage == EmailNotFound){
                                userMessage = EmailNotFoundUserDisplay;
                            }else{
                                userMessage = respMessage;
                            }
                            msgHTML = '<div data-alert class="callout small-12 primary">';
                            msgHTML += userMessage;
                            msgHTML += '</div>';
    
                        } else {
                            msgHTML = '<div class="callout small-12 primary" role="alert">';
                            msgHTML += data.message;
                            msgHTML += '</div>';
                        }

    
                        // disable buttons
                        $(".btn_sign_in").attr("disabled", true);
                        $(".btn_application").attr("disabled", true);

                        $('#frmSignAlert').html(msgHTML);
    
                    });
                }else{
                    msgHTML = '<div class="callout small-12 primary" role="alert">';
                    msgHTML += data.message += '. Please Click on <b>Registration & Apply</b> Tab to complete the application.';
                    msgHTML += '</div>';
                
                    $('#frmSignAlert').html(msgHTML);
                }
    
                $('#frmSignAlert').html(msgHTML);

            }).fail(function(jqXHR, textStatus, errorThrown) {
    
               
            });


        } else {
            msgHTML = '<div class="callout small-12 alert" role="alert">';
            msgHTML += 'Invalid Email Address';
            msgHTML += '</div>';

            $('#frmSignAlert').html(msgHTML);
        }

    });
}

/**
 * 
 */
function handleSingleJobListing() {
    console.log('============= handleSingleJobListing =============');
    let app_xml = 'application.xml';
    let linkId = getCurrentUrlParameter();
    let fetchUrl = fetchSingleJobUrl + linkId

    // GET Method
    ajaxCall(fetchUrl, ajaxMsg, 1);
};

/**
 * 
 */
function handleSingleInternshipListing() {
    console.log('============= handleSingleInternshipListing =============');
    let app_xml = 'application.xml';
    let linkId = getCurrentUrlParameter();
    let fetchUrl = fetchSingleInternshipUrl + linkId

    // GET Method
    ajaxCall(fetchUrl, ajaxMsg, 2);
};

/**
 * 
 */
function handleClientJobListing() {
    console.log('============= handleClientJobListing =============');
    let app_xml = 'application.xml';
    let linkId = getCurrentUrlParameter();
    let fetchUrl = fetchClientJobUrl + linkId

    // GET Method
    ajaxCall(fetchUrl, ajaxMsg, 3);
};

/**
 * 
 */
function handleSingleResourcing() {
    console.log('============= handleSingleRecruitment =============');
    let app_xml = 'application.xml';
    let linkId = getCurrentUrlParameter();
    let fetchUrl = fetchSingleResourcingUrl + linkId

    // GET Method
    ajaxCall(fetchUrl, ajaxMsg, 4);
};






