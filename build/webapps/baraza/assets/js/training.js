// Error Msg
let ajaxMsg = [];
ajaxMsg[1] = "Something Went Wrong! Contact Us as soon as possible.";

let fetchTrainingUrl = "./jsongeneral?xml=application.xml&view=40:0:0&linkdata=";

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
        singleTrainingDetails(data);
    }
}

function singleTrainingDetails(data) {
    console.log(data);

    for (let i = 0; i < data.length; i++) {
        $('#course_title').html(data[i].training_course_name);
        $('#course_title').css({ 'text-transform': 'uppercase' });
        
        $('#training_name').html("Topic : " + data[i].training_name);
        $('#training_by').html("By : " + data[i].entity_name);
        $('#start_date').html("From : " + data[i].start_date);
        $('#end_date').html("To : " + data[i].end_date);
        $('#start_time').html("Time : " + data[i].start_time);
        $('#end_time').html("To : " + data[i].end_time);
        $('#training_details').html(data[i].details);
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
function getTrainingDetails(linkId) {
    console.log('============= handleSingleTraining =============');
    let app_xml = 'application.xml';
    let fetchUrl = fetchTrainingUrl + linkId

    // GET Method
    ajaxCall(fetchUrl, ajaxMsg, 1);
};








