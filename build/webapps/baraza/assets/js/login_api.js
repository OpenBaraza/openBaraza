
// Error Msg
let ajaxMsg = [];
ajaxMsg[1] = "Something Went Wrong! Contact Us as soon as possible.";

let fetchOpportunityCountUrl = "./recruitment?tag=opportunity_count";

/**
 * General Ajax
 * @param {*} url 
 * @param {*} elemID 
 * @param {*} htmlMsg 
 */
function ajaxCall(url, elemID, fnct) {

    var msgHTML = "";
    $.ajax({
        type: 'GET',
        url: url,
        dataType: 'json',
        success: function(data) {
			console.log(data);
			
			if (fnct == 1) {
            	getJobListing(data, elemID);
            }

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


function getJobListing(data, elemID) {
    let jLHtml = "";
	
    if(data.internships > 0) {
		jLHtml += '<a class="btn blue" href="recruitment.jsp"><i class="icon-key"></i>&nbsp;Internships</a></br>';
  	}
  	
  	if(data.jobs > 0) {
		jLHtml += '<a class="btn blue" href="recruitment.jsp"><i class="icon-key"></i>&nbsp;Vacancies</a></br>';
  	}
  	
  	if(data.consultancies > 0) {
		jLHtml += '<a class="btn blue" href="resourcing.jsp?rc_type=1"><i class="icon-key"></i>&nbsp;Consultancies</a></br>';
  	}
  	
  	if(data.part_time_jobs > 0) {
		jLHtml += '<a class="btn blue" href="resourcing.jsp?rc_type=1"><i class="icon-key"></i>&nbsp;Part Time Jobs</a></br>';
  	}
  	
  	if(data.volunteer_posts > 0) {
		jLHtml += '<a class="btn blue" href="resourcing.jsp?rc_type=2"><i class="icon-key"></i>&nbsp;Volunteer Posts</a></br>';
  	}

    $(elemID).html(jLHtml);
}



function listOpportunityCounts(categoryId) {
    let app_xml = 'application.xml';
    
    ajaxCall(fetchOpportunityCountUrl, '#list_opportunities', 1);
}






