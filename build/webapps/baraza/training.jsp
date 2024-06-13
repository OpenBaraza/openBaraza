<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="mainPage" value="application.jsp" scope="page" />
<%@ page import="org.baraza.web.BWebData" %>
<%@ page import="org.baraza.DB.BDB" %>

<%
	ServletContext context = getServletContext();
	String dbConfig = "java:/comp/env/jdbc/database";
	String xmlcnf = "application.xml";
	
	String applicantModal = "";
	String loginXml = "application.xml";
	if(loginXml != null) {
		BWebData webData = new BWebData(context, request, loginXml);
		applicantModal = webData.getModalForm(request, "1:1");
		webData.close();
	}
	
	BDB db = new BDB(dbConfig);
	String tdSql = "SELECT min(training_id) FROM trainings "
		+ "WHERE (end_date >= current_date) AND (public_training = true) AND (completed = false)";
	String trId = db.executeFunction(tdSql);
	if(trId == null) trId = "0";
 
	db.close();
%>

<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
	<meta charset="utf-8"/>
	<title><%= pageContext.getServletContext().getInitParameter("web_title") %></title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta content="width=device-width, initial-scale=1" name="viewport"/>
	<meta content="Open Baraza Framework" name="description"/>
	<meta content="Open Baraza" name="author"/>

	<!-- BEGIN GLOBAL MANDATORY STYLES -->
	<link href="https://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/font-awesome/css/font-awesome.min.css"  rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
	<!-- END GLOBAL MANDATORY STYLES -->
	<!-- BEGIN PAGE LEVEL PLUGIN STYLES -->
	<link href="./assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/fullcalendar/fullcalendar.min.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/jqvmap/jqvmap/jqvmap.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/morris/morris.css" rel="stylesheet" type="text/css">
	<!-- END PAGE LEVEL PLUGIN STYLES -->
	<!-- BEGIN PAGE STYLES -->
	<link href="./assets/admin/pages/css/tasks.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/clockface/css/clockface.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-colorpicker/css/colorpicker.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/jquery-tags-input/jquery.tagsinput.css" rel="stylesheet" type="text/css"/>
    <link href="./assets/global/plugins/select2/select2.css" rel="stylesheet" type="text/css" />
    <link href="./assets/global/plugins/jquery-multi-select/css/multi-select.css" rel="stylesheet" type="text/css" />
    <link href="./assets/global/plugins/fullcalendar/fullcalendar.min.css" rel="stylesheet"/>
    <link href="./assets/global/plugins/bootstrap-toastr/toastr.min.css" rel="stylesheet" type="text/css"/>
    <link href="./assets/global/plugins/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
    <link href="./assets/admin/pages/css/profile.css" rel="stylesheet" type="text/css"/>

	<link href="./assets/global/plugins/jstree/dist/themes/default/style.min.css" rel="stylesheet" type="text/css"/>

    <!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
    <link href="./assets/global/plugins/jquery-file-upload/css/jquery.fileupload.css" rel="stylesheet">

	<!-- END PAGE STYLES -->
	<!-- BEGIN THEME STYLES -->
	<!-- DOC: To use 'rounded corners' style just load 'components-rounded.css' stylesheet instead of 'components.css' in the below style tag -->

    <link href="./assets/global/css/components-md.css" id="style_components" rel="stylesheet" type="text/css"/>
    <link href="./assets/global/css/plugins-md.css" rel="stylesheet" type="text/css"/>

	<link href="./assets/admin/layout4/css/layout.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/admin/layout4/css/themes/light.css" rel="stylesheet" type="text/css" id="style_color"/>

	<!-- END THEME STYLES -->
	<link rel="shortcut icon" href="./assets/logos/favicon.png"/>

	<link href="./assets/global/plugins/jquery-ui/jquery-ui-1.10.3.custom.min.css" rel="stylesheet" type="text/css" media="screen" />
    <link href="./assets/jqgrid/css/ui.jqgrid.css" rel="stylesheet" type="text/css" media="screen" />

	<!-- jsgrid css -->
    <link type="text/css" rel="stylesheet" href="./assets/jsgrid/jsgrid.min.css" />
    <link type="text/css" rel="stylesheet" href="./assets/jsgrid/jsgrid-theme.min.css" />

    <link href="./assets/admin/layout4/css/custom.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/css/app.css?1040" rel="stylesheet" type="text/css"/>
	<link href="./assets/css/email-subscribers.css" rel="stylesheet" type="text/css"/>
	

</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<!-- DOC: Apply "page-header-fixed-mobile" and "page-footer-fixed-mobile" class to body element to force fixed header or footer in mobile devices -->
<!-- DOC: Apply "page-sidebar-closed" class to the body and "page-sidebar-menu-closed" class to the sidebar menu element to hide the sidebar by default -->
<!-- DOC: Apply "page-sidebar-hide" class to the body to make the sidebar completely hidden on toggle -->
<!-- DOC: Apply "page-sidebar-closed-hide-logo" class to the body element to make the logo hidden on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-hide" class to body element to completely hide the sidebar on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-fixed" class to have fixed sidebar -->
<!-- DOC: Apply "page-footer-fixed" class to the body element to have fixed footer -->
<!-- DOC: Apply "page-sidebar-reversed" class to put the sidebar on the right side -->
<!-- DOC: Apply "page-full-width" class to the body element to have full width page without the sidebar menu -->
<body class="page-header-fixed page-sidebar-closed-hide-logo page-sidebar-closed-hide-logo page-footer-fixed">

<!-- BEGIN HEADER -->
<div class="page-header navbar navbar-fixed-top">
	<!-- BEGIN HEADER INNER -->
	<div class="page-header-inner">
		<!-- BEGIN LOGO -->
		<div class="page-logo">
			<a href="index.jsp">
			<img src="./assets/logos/logo_header.png" alt="logo" style="margin: 20px 10px 0 10px; width: 107px;" class="logo-default"/>
			</a>
			<div class="menu-toggler sidebar-toggler">
				<!-- DOC: Remove the above "hide" to enable the sidebar toggler button on header -->
			</div>
		</div>
		<!-- END LOGO -->
		<!-- BEGIN RESPONSIVE MENU TOGGLER -->
		<a href="javascript:;" class="menu-toggler responsive-toggler" data-toggle="collapse" data-target=".navbar-collapse">
		</a>
		<!-- END RESPONSIVE MENU TOGGLER -->

		<!-- BEGIN PAGE TOP -->
		<div class="page-top">

		</div>
		<!-- END PAGE TOP -->
	</div>
	<!-- END HEADER INNER -->
</div>

<!-- END HEADER -->

<div class="clearfix"></div>

<!-- BEGIN CONTAINER -->
<div class="page-container">
    <div class="page-sidebar-wrapper">
		<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
		<!-- DOC: Change data-auto-speed="200" to adjust the sub menu slide up/down speed -->
		<div class="page-sidebar navbar-collapse collapse">
            <div class="row">
				<div class="col-md-12">
					<!-- BEGIN Portlet PORTLET-->
					<div class="portlet light bordered">
						<div class="portlet-title">
						    <div class="caption font-green-sharp">
						        <i class="icon-info font-green-sharp"></i>
						        <!--<span class="caption-subject bold uppercase"> Portlet</span>-->
						        <span class="caption-helper">Guidelines</span>
						    </div>
						    <div class="actions">
						        <a href="javascript:;" class="btn btn-circle btn-default btn-icon-only fullscreen"></a>
						    </div>
						</div>
						<div class="portlet-body">
						    <div class="scroller" style="height:460px" data-rail-visible="1" data-rail-color="yellow" data-handle-color="#a1b2bd">
						        <h4></h4>
						        <ol>
						        	<li>Enter your email address to register for the training</li>
						        	<li>If we do not have your details a form will pop up for you to enter your details</li>
						            <li>Fill in all the required fields, correctly. Ensure you provide a valid email address. All fields with * are mandatory</li>
						            <li>Click on Apply. This sends login credentials to the email provided.</li>
						            <li>Check your email for the login credentials and further details</li>
						        </ol>
						    </div>
						</div>
					</div>
					<!-- END Portlet PORTLET-->
				</div>
			</div>
		</div>
	</div>

	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
		
		<div class="row">
			<div class="col-md-12">
				<!-- BEGIN Portlet PORTLET-->
				<div class="portlet light bordered">
				    <div class="portlet-title">
				        <div class="caption font-green-sharp">
				            <i class="icon-info font-green-sharp"></i>
				            <span class="caption-helper">Training Details</span>
				        </div>
				        <div class="actions">
				            <a href="javascript:;" class="btn btn-circle btn-default btn-icon-only fullscreen"></a>
				        </div>
				    </div>
				    <div class="portlet-body">
                       <!-- START OF SINGLE JOB  SECTION -->
                        <section class="single_training">
	                        <div class="row">
	                            <div class="small-12 columns">
	                                <div id="course_title" class="training_title">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                                <div class="single_job_divider">
	                                </div>
	                            </div>
	                        </div>
	                        <div class="row">
	                            <div class="small-12 columns">
	                                <div id="training_name" class="training_topic">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                                <div id="training_by" class="training_narration">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                                <div id="start_date" class="training_narration">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                                <div id="end_date" class="training_narration">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                                <div id="start_time" class="training_narration">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                                <div id="end_time" class="training_narration">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                            </div>
	                        </div>
	                        <div class="row">
	                            <div class="small-12 columns">
	                                <div id="training_details" class="training_details">
	                                    <i class="fas fa-sync fa-spin"> </i>
	                                </div>
	                            </div>
	                        </div>
                            
                            <div class="row">
                                <div class="single_training_divider">
		                        </div>
                            	<div class="small-12 large-6 columns">
                                    <label> Email
                                        <input type="text" name="application_email" id="application_email" placeholder="Email *" required="required">
                                    </label>
                                </div>
                                
                                <c:if test="${param.work_type eq '1'}">
                                	<div class="small-12 large-6 columns">
						        		<label> Pricing
		                                    <input type="text" cols="50" name="application_pricing" id="application_pricing" placeholder="Pricing *" required="required">
		                                </label>
                                    </div>
                                    <div class="small-12 large-6 columns">
						        		<label> Summary Offer
		                                    <textarea type="text" rows="4" cols="50" name="applicant_comments" id="applicant_comments" placeholder="Summary Offer *" required="required"></textarea>
		                                </label>
                                    </div>
				           	 	</c:if>
				           	 	
                                <c:if test="${param.work_type eq '2'}">
                                	<div class="small-12 large-6 columns">
						        		<label> Pricing
		                                    <input type="text" name="application_pricing" id="application_pricing" placeholder="Pricing *" required="required">
		                                </label>
                                    </div>
                                    <div class="small-12 large-6 columns">
						        		<label> Summary Offer
		                                    <textarea type="text" name="applicant_comments" id="applicant_comments" placeholder="Summary Offer *" required="required"></textarea>
		                                </label>
                                    </div>
				           	 	</c:if>
				           	 	
                                <div class="small-12 large-6 columns">
                                    <div class="nf-field-element">
                                        <input id="nf-field-4" 
                                         onclick="training_application()"
                                         class="ninja-forms-field nf-element" type="button" value="APPLY NOW">
                                    </div>
                                </div>
                                <div class="small-12 large-6 columns">
                                    <div class="nf-field-element">
                                		<div id="job_apply_msg"></div>
                                	</div>
                                </div>
                            </div>
                        </section>
                        
                        <!-- APPLICANT MODAL -->
							<%=applicantModal%>
						<!-- END APPLICANT MODAL -->

                        <!--END OF SINGLE JOB SECTION -->

                    </div>
                    <!-- end #inner-content -->

                </div>
                <!-- end #content -->

                <!-- START OF FOOTER SECTION -->
						
				    </div>
        		</div>
			</div>
		</div>
			
		</div>
	</div>
	<!-- END CONTENT -->
</div>
<!-- END CONTAINER -->
<!-- BEGIN FOOTER -->
<div class="page-footer">
	<div class="page-footer-inner">
		2022 &copy; openBaraza <a href="http://dewcis.com">Dew Cis Solutions Ltd</a> All Rights Reserved
	</div>
	<div class="scroll-to-top">
		<i class="icon-arrow-up"></i>
	</div>
</div>

<!-- END FOOTER -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<!-- BEGIN CORE PLUGINS -->
<!--[if lt IE 9]>
<script src="./assets/global/plugins/respond.min.js"></script>
<script src="./assets/global/plugins/excanvas.min.js"></script>
<![endif]-->
<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery-migrate.min.js" type="text/javascript"></script>
<!-- IMPORTANT! Load jquery-ui.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
<script src="./assets/global/plugins/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
<!--<script src="./jquery-ui-1.11.4.custom/jquery-ui.min.js"  type="text/javascript"></script>-->
<script src="./assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="./assets/global/plugins/jqvmap/jqvmap/jquery.vmap.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.russia.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.world.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.europe.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.germany.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.usa.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jqvmap/jqvmap/data/jquery.vmap.sampledata.js" type="text/javascript"></script>
<script src="./assets/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js" type="text/javascript" ></script>
<script src="./assets/global/plugins/ckeditor/ckeditor.js" type="text/javascript" ></script>

<script src="./assets/global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/select2/select2.min.js" type="text/javascript"></script>



<!-- IMPORTANT! fullcalendar depends on jquery-ui.min.js for drag & drop support -->
<script src="./assets/global/plugins/morris/morris.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/morris/raphael-min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery.sparkline.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<script src="./assets/global/plugins/jquery-file-upload/js/vendor/jquery.ui.widget.js"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<!--<script src="//blueimp.github.io/JavaScript-Load-Image/js/load-image.all.min.js"></script>-->
<script src="./assets/global/plugins/jquery-file-upload/js/vendor/load-image.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script src="./assets/global/plugins/jquery-file-upload/js/vendor/canvas-to-blob.min.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="./assets/global/plugins/jquery-file-upload/js/jquery.iframe-transport.js"></script>
<!-- The basic File Upload plugin -->
<script src="./assets/global/plugins/jquery-file-upload/js/jquery.fileupload.js"></script>
<!-- The File Upload processing plugin -->
<script src="./assets/global/plugins/jquery-file-upload/js/jquery.fileupload-process.js"></script>
<!-- The File Upload image preview & resize plugin -->
<script src="./assets/global/plugins/jquery-file-upload/js/jquery.fileupload-image.js"></script>
<!-- The File Upload audio preview plugin -->
<script src="./assets/global/plugins/jquery-file-upload/js/jquery.fileupload-audio.js"></script>
<!-- The File Upload video preview plugin -->
<script src="./assets/global/plugins/jquery-file-upload/js/jquery.fileupload-video.js"></script>
<!-- The File Upload validation plugin -->
<script src="./assets/global/plugins/jquery-file-upload/js/jquery.fileupload-validate.js"></script>

<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="./assets/global/plugins/bootstrap-fileinput/bootstrap-fileinput.js" type="text/javascript"></script>
<script src="./assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="./assets/admin/layout4/scripts/layout.js" type="text/javascript"></script>
<script src="./assets/admin/layout4/scripts/demo.js" type="text/javascript"></script>
<script src="./assets/admin/pages/scripts/index3.js" type="text/javascript"></script>
<script src="./assets/admin/pages/scripts/tasks.js" type="text/javascript"></script>
<script src="./assets/admin/pages/scripts/components-pickers.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery-multi-select/js/jquery.multi-select.js" type="text/javascript" ></script>
<script src="./assets/global/plugins/jquery-multi-select/js/jquery.quicksearch.js" type="text/javascript"></script>
<script src="./assets/global/plugins/clockface/js/clockface.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jstree/dist/jstree.min.js" type="text/javascript"></script>
<script src="./assets/admin/pages/scripts/ui-tree.js" type="text/javascript"></script>
<script src="./assets/global/plugins/bootstrap-toastr/toastr.min.js"></script>

<!-- END PAGE LEVEL SCRIPTS -->

<script type="text/javascript" src="./assets/jqgrid/js/i18n/grid.locale-en.js"></script>
<script type="text/javascript" src="./assets/jqgrid/js/jquery.jqGrid.min.js"></script>

<!-- jsgrid for sub form editing-->
<script type="text/javascript" src="./assets/jsgrid/jsgrid.min.js"></script>

<!-- calendar-->
<!-- IMPORTANT! fullcalendar depends on jquery-ui.min.js for drag & drop support -->
<script type="text/javascript" src="./assets/global/plugins/moment.min.js"></script>
<script type="text/javascript" src="./assets/global/plugins/fullcalendar/fullcalendar.min.js"></script>


<script src="https://dhbhdrzi4tiry.cloudfront.net/cdn/sites/foundation.js"></script>
<script>
    $(document).foundation();
</script>

<script type="text/javascript" src="./assets/js/grid_date.js"></script>
<script type="text/javascript" src="./assets/js/grid_time.js"></script>

<script>
    jQuery(document).ready(function() {
        Metronic.init(); // init metronic core componets
        Layout.init(); // init layout
        
        $('.date-picker').datepicker({
            autoclose: true
        });

		var date = new Date();
		$('.date-picker2').datepicker({
            autoclose: true,
			startDate:date
        });

		UITree.init();

		$('.clockface').clockface({
            format: 'hh:mm a',
            trigger: 'manual'
        });

        $('.clockface-toggle').click(function (e) {
            e.stopPropagation();
            var target = $(this).attr('data-target');
            $('#' + target ).clockface('toggle');
        });

        $('.timepicker-no-seconds').timepicker({
            autoclose: true,
            minuteStep: 5
        });

        $('.timepicker-24').timepicker({
            autoclose: true,
            minuteStep: 5,
            showSeconds: false,
            showMeridian: false
        });

        // handle input group button click
        $('.timepicker').parent('.input-group').on('click', '.input-group-btn', function(e){
            e.preventDefault();
            $(this).parent('.input-group').find('.timepicker').timepicker('showWidget');
        });

    });
</script>

<script type="text/javascript" src="./assets/js/training.js?1070"></script>

<script type="text/javascript">
    
    let training_id = <%= trId %>;

    // set job_id input
    $("#training_id").val(training_id);

    console.log(training_id);
	getTrainingDetails(training_id);
</script>

<script type="text/javascript">

	var app_xml = '<%=loginXml%>';

	function training_application() {
		var isValid = false;

		console.log("Check validation");
		
		let training_id = <%= trId %>;
		let applicant_email = $('#application_email').val();
		applicant_email = applicant_email.trim();
		applicant_email = applicant_email.toLowerCase();
				
		console.log(applicant_email);
		console.log(training_id);
		
		let valid_email = validateEmail(applicant_email);
		if(valid_email) {
		
			let emailUrl = "./training?tag=search_training_email&email=" + applicant_email + "&training_id=" + training_id;
			
			$.ajax({
				type: 'GET',
				url: emailUrl,
				dataType: 'json',
				success: function(data) {
					console.log(data);
					if(data.success == 0) {
						$('#applicant_email').val(applicant_email);
						$('#modal_application').modal('show');
					} else if(data.success == 1) {
						let checkEmailMsg = "<div style='color:blue'><i class='glyphicon glyphicon-remove'></i>" + data.message + "</div>";
						$('#job_apply_msg').html(checkEmailMsg);
					} else if(data.success == 2) {
						let applyUrl = "./training?tag=apply_training";
							
						var formData = {email:applicant_email, entity_id:data.entityId, training_id:training_id}; 
 
						$.ajax({
							type: 'POST',
							url: applyUrl,
							data : formData,
							dataType: 'json',
							success: function(adata) {
								console.log(adata);
								
								let checkApplyMsg = "<div style='color:blue'><i class='glyphicon glyphicon-remove'></i>" + adata.message + "</div>";
								$('#job_apply_msg').html(checkApplyMsg);
							}
						});
					}
				}
			});
		} else {
			let checkEmailError = "<div style='color:red'><i class='glyphicon glyphicon-remove'></i> Supplied Email is not valid</div>";
			$('#job_apply_msg').html(checkEmailError);
		}

		return isValid;
	}


	$("#btn_application").click(function(e) {
		if( !validate($("#frm_application").serializeArray()) ) {return;}	
		var form_data = JSON.stringify($("#frm_application").serializeArray());
		$.post('ajaxinsert', {app_xml:app_xml, app_key:'1:0', form_data:form_data}, function(data) {
			console.log(data);
			
			if(data.error == 0) {
				let training_id = <%= trId %>;

				
				var formData = {email:data.data.applicant_email, entity_id:data.data.keyfield, training_id:training_id}; 
 
				
				let applyUrl = "./training?tag=apply_training";
				console.log(applyUrl);
				
							
				$.ajax({
					type: 'POST',
					url: applyUrl,
					data : formData,
					dataType: 'json',
					success: function(adata) {
						console.log(adata);
						
						let checkApplyMsg = "<div style='color:blue'><i class='glyphicon glyphicon-remove'></i>" + adata.message + "</div>";
						$('#job_apply_msg').html(checkApplyMsg);
						
						$('.modal').modal('hide');
					}
				});
			} else {
				$('.modal').modal('hide');
				
				let applyErrMsg = "<div style='color:red'><i class='glyphicon glyphicon-remove'></i>" + data.error_msg + "</div>";
				$('#job_apply_msg').html(applyErrMsg);
			}
		});
	});

</script>

<!-- END JAVASCRIPTS -->


</body>
<!-- END BODY -->
</html>

