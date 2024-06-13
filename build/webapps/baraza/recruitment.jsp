<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="mainPage" value="recruitment.jsp" scope="page" />

<%@ page import="org.baraza.web.BWebData" %>
<%@ page import="org.baraza.DB.BDB" %>

<%
	ServletContext context = getServletContext();
	String dbConfig = "java:/comp/env/jdbc/database";
	String xmlcnf = "application.xml";
	
	String applicantModal = "";
	String applicantModalView = "";
	String loginXml = "application.xml";
	if(loginXml != null) {
		BWebData webData = new BWebData(context, request, loginXml);
		applicantModal = webData.getModalForm(request, "1:0");
		applicantModalView = webData.getModalFormView(request, "1:0:0");
		webData.close();
	}
	
	boolean noApplications = false;
	BDB db = new BDB(dbConfig);
	String sql = "SELECT intake_id FROM intake WHERE (hcm_post = true) AND (closing_date >= current_date) AND (org_id = 0)";
	String intakeId = db.executeFunction(sql);
	sql = "SELECT internship_id FROM internships WHERE (hcm_post = true) AND (closing_date >= current_date) AND (org_id = 0)";
	String internshipId = db.executeFunction(sql);
	if((intakeId == null) && (internshipId == null)) noApplications = true;
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
	<link href="./assets/css/app.css?101" rel="stylesheet" type="text/css"/>
	<link href="./assets/css/recruitment.css?110" rel="stylesheet" type="text/css"/>

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
<div class="page-header navbar navbar-fixed-top" style="background-color: #fafafa;border-bottom:none;">
	<!-- BEGIN HEADER INNER -->
	<div class="page-header-inner">
		<!-- BEGIN LOGO -->
		<div class="page-logo">
			<a href="index.jsp">
				<img src="./assets/logos/logo_header.png" alt="logo" style="margin: 0px 10px 0 10px; width: 107px;" class="logo-default"/>
			</a>
			
		</div>
		<!-- END LOGO -->
		<!-- BEGIN RESPONSIVE MENU TOGGLER -->
		

		<h2 class="ptopheader" style="margin-left: 45%;font-weight: bold;color: #0072b8;">VACANCIES</h2>
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
<div class="page-container" style="margin-top: 50px;">
    
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
			<div class="row" id="recruitment_actions">
				<select id="cmb_job_category" class="cmb_category form-control" style="display: inline;width:350px;height:33px;padding:1px 1px 1px 10px">
				</select>										
		
				<button type='button' id="btn_job_filter" class='btn btn-secondary' style="display: inline;margin-left: 10px;padding: 8px 14px 8px 14px;
				background-color: #0072b8;
				color: white;
				font-weight: bold;">Filter</button>
				
<% if(noApplications) { %>
				<button type='button' id="btn_register" class='btn btn-secondary' style="display: inline;margin-left: 10px;padding: 8px 14px 8px 14px;
				background-color: #0072b8;
				color: white;
				font-weight: bold;">Register</button>
<% } %>
			</div>
			<div class="row" style="max-width:100%;margin-top:10px" id="div_msg">
			</div>
			<div class="row" style="max-width:100%;margin-top:20px;">
				<!-- BEGIN Portlet PORTLET-->
				<div class="portlet light bordered" style="border: none !important;box-shadow: none;">
					<div class="portlet-title">
						<center><div class="job-listing__category-line"></div>
						<p class="job-listing__category-title">Jobs Listing</p></center>
					</div>
					<div class="portlet-body">

						<!-- RESULTS SECTION -->
						<section class="results">
							<div class="row" style="max-width: 100% ;">
								<div class="small-12 columns" id="job_list">
								    <i class="fas fa-sync fa-spin"> </i>
								</div>
								<div class="small-12 columns" id="client_job_list">
								    <i class="fas fa-sync fa-spin"> </i>
								</div>
							</div>
						</section>
						
					</div>
				</div>
			</div>
			<div class="row" style="max-width: 100%;">
				<!-- BEGIN Portlet PORTLET-->
				<div class="portlet light bordered" style="border: none !important;box-shadow: none;">
					<div class="portlet-title">
						<center><div class="job-listing__category-line"></div>
						<p class="job-listing__category-title">Internships Listing</p></center>
					   
					</div>
					<div class="portlet-body">

						<!-- RESULTS SECTION -->
						<section class="results">
							<div class="row" style="max-width: 100%;">
								<div class="small-12 columns" id="internship_list">
								    <i class="fas fa-sync fa-spin"> </i>
								</div>
							</div>
						</section>
						
					</div>
				</div>
			</div>
		
			<!-- APPLICANT MODAL -->
			<%=applicantModal%>
			
			<%=applicantModalView%>
			<!-- END APPLICANT MODAL -->
		</div>
	</div>
	
	<!-- END CONTENT -->
</div>
<!-- END CONTAINER -->
<!-- BEGIN FOOTER -->
<div class="page-footer">
	<div class="page-footer-inner">
		2024 &copy; openBaraza <a href="http://dewcis.com">Dew Cis Solutions Ltd</a> All Rights Reserved
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

<script type="text/javascript" src="./assets/js/recruitment_api.js?1012"></script>

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

<script type="text/javascript">

	var app_xml = '<%=loginXml%>';

    listAllJobs();
    
    $('#btn_job_filter').click(function(event){
    	console.log($('#cmb_job_category').val());
    
		listFilteredJobs($('#cmb_job_category').val());
	});
	
	$("#btn_register").click(function(e) {
		$('#modal_application').modal('show');
	});
	
	$("#btn_application").click(function(e) {
		if(!validate($("#frm_application").serializeArray())) {
			let applyMsg = "<div style='color:red'><i class='glyphicon glyphicon-remove'></i>Form not filled appropriately</div>";
			$('#div_msg').html(applyMsg);
				
			return;
		}
		
		var bForm = $('#frm_application')[0];
		var bData = new FormData(bForm);
		var cv_file = document.getElementById("cv_file");
		var applicant_email = $('#applicant_email').val();
		applicant_email = applicant_email.trim();
		applicant_email = applicant_email.toLowerCase();
		var valid_email = validateEmail(applicant_email);
		
		if(!valid_email) {
			let checkEmailError = "<div style='color:red'><i class='glyphicon glyphicon-remove'></i> Supplied Email is not valid</div>";
			$('#error_msg_application').html(checkEmailError);
		} else if(cv_file.value.length < 4) {
			let applyErrMsg = "<div style='color:red'><i class='glyphicon glyphicon-remove'></i>You need to attach your CV in word or pdf</div>";
			$('#error_msg_application').html(applyErrMsg);
		} else {
		
			document.querySelector('#btn_application').innerText = 'Processing ...';
			$("#btn_application").prop('disabled', true);
		
			$.ajax({
				type: "POST",
				url: "job_application?view=1:0",
				dataType: "json",
				data: bData,
				cache: false,
				contentType: false,
				processData: false,
				timeout: 600000,

				success: function (rData) {
					console.log(rData);
					
					for (var key of Object.keys(rData.data)) {
						$("#td_" + key).html(rData.data[key]);
					}
					
					$('.modal').modal('hide');
								
					$('#modal_application_view').modal('show');
				}
			});
		}
	});

	
</script>

<!-- END JAVASCRIPTS -->

</body>
<!-- END BODY -->
</html>

