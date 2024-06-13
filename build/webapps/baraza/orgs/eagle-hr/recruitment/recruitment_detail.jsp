<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="mainPage" value="application.jsp" scope="page" />
<%@ page import="org.baraza.web.BWebData" %>

<%
	ServletContext context = getServletContext();
	String dbConfig = "java:/comp/env/jdbc/database";
	String xmlcnf = "application.xml";
	
	String applicantModal = "";
	//String loginXml = context.getInitParameter("login_xml");
	String loginXml = "application.xml";
	if(loginXml != null) {
		BWebData webData = new BWebData(context, request, loginXml);
		applicantModal = webData.getModalForm(request, "1:0");
		webData.close();
	}

%>

<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
	<meta charset="utf-8"/>
	<title>Eagle HR Consultants | Recruitment</title>
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
	<link rel="icon" href="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-32x32.png" sizes="32x32">
	<link rel="icon" href="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-192x192.png" sizes="192x192">
	<link rel="apple-touch-icon" href="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-180x180.png">
	<meta name="msapplication-TileImage" content="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-270x270.png">

	<link href="./assets/global/plugins/jquery-ui/jquery-ui-1.10.3.custom.min.css" rel="stylesheet" type="text/css" media="screen" />
    <link href="./assets/jqgrid/css/ui.jqgrid.css" rel="stylesheet" type="text/css" media="screen" />

	<!-- jsgrid css -->
    <link type="text/css" rel="stylesheet" href="./assets/jsgrid/jsgrid.min.css" />
    <link type="text/css" rel="stylesheet" href="./assets/jsgrid/jsgrid-theme.min.css" />

    <link href="./assets/admin/layout4/css/custom.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/css/app.css?1030" rel="stylesheet" type="text/css"/>
	<link href="./assets/css/email-subscribers.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/css/eagle/recruitment_detail.css?13" rel="stylesheet" type="text/css"/>
	

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
	<div class="page-header navbar navbar-fixed-top" style="border-bottom: none;background-color: #022a30;padding: 16px 0px 16px 0px;height: 116px;">
		<!-- BEGIN HEADER INNER -->
		<div class="page-header-inner">
			<!-- BEGIN LOGO -->
			<div class="page-logo">
				<a href="index.jsp">
					<img src="https://eaglehr.co.ke/wp-content/themes/eagle-dezari/assets/imgs/logo_white.png" alt="logo" style="margin: 0px 10px 0 50px; width: 80px;" class="logo-default"/>
				</a>
				
			</div>
			<!-- END LOGO -->
			<!-- BEGIN RESPONSIVE MENU TOGGLER -->
			
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
	<div class="page-container" style="margin-top: 115px;background-color: white;">
		<div class="page-sidebar-wrapper">
			<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
			<!-- DOC: Change data-auto-speed="200" to adjust the sub menu slide up/down speed -->
			<div class="page-sidebar navbar-collapse collapse">
				<div class="row">
					<div class="col-md-12">
						<!-- BEGIN Portlet PORTLET-->
						<div class="portlet light bordered"  style="background-color: #f5f7f9;padding-top: 30px;">
							<div class="portlet-title">
								<h3 style="font-size: 20px;font-weight: bold;">GUIDELINES</h3>
							</div>
							<div class="portlet-body">
								<div class="scroller" style="height:460px" data-rail-visible="1" data-rail-color="yellow" data-handle-color="#a1b2bd">
									<h4></h4>
												
									<ol class="list">
										<li>Enter your email address to apply for the vacancy.</li>
										<li>If we do not have your details a form will pop up for you to enter your details.</li>
										<li>Fill in all the required fields, correctly. Ensure you provide a valid email address.All fields with * are mandatory.</li>
										<li>Click on Apply. This sends login credentials to the email provided.</li>
										<li>Check your email for the login credentials and further details.</li>
									</ol>
		
	
									<!-- <ol>
										<li>Enter your email address to apply for the vacancy</li>
										<li>If we do not have your details a form will pop up for you to enter your details</li>
										<li>Fill in all the required fields, correctly. Ensure you provide a valid email address.All fields with * are mandatory</li>
										<li>Click on Apply. This sends login credentials to the email provided.</li>
										<li>Check your email for the login credentials and further details</li>
									</ol> -->
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
			
			<div class="row"  style="max-width: 100%;margin-right: 0;margin-left: 0;">
				<div class="col-md-12">
					<!-- BEGIN Portlet PORTLET-->
					<div class="portlet light bordered">
						<div class="portlet-title">
							<!-- <div class="caption font-green-sharp">
								<i class="icon-info font-green-sharp"></i>
								<c:if test="${param.work_type eq '1'}">
									<img src="./assets/images/back.png">
								<span style="color: #757e8a;">Back to Offers</span>
								</c:if>
								<c:if test="${param.work_type eq '2'}">
									<span class="caption-helper">Internship Listings</span>
								</c:if>
							</div>
							<div class="actions">
								<a href="javascript:;" class="btn btn-circle btn-default btn-icon-only fullscreen"></a>
							</div> -->
							<a href="./recruitment.jsp" style="text-decoration: none;"><img src="./assets/images/back.png">
								<span style="color: #757e8a;font-size: 12px;font-weight: bold;">&ensp;Back to offers</span></a>
						</div>
						<div class="portlet-body">
						   <!-- START OF SINGLE JOB  SECTION -->
							<section class="single_job">
								<c:if test="${param.work_type eq '1'}">
									<div class="row">
										<div class="small-12 columns">
											<div id="single_job_title" class="recruitment_title">
												<i class="fas fa-sync fa-spin"> </i>
												
											</div>
											<!-- <div class="single_job_divider"> -->
											</div>
										</div>
									</div>
									<div class="row">
										<div class="small-12 columns">
											<div id="single_job_ref" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_job_location" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_job_category" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_job_positions" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_job_closing" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 20px;margin-bottom: 50px;">
										<h3 style="font-size: 20px;font-weight: bold;">ABOUT THE ROLE</h3>
										<div class="small-12 columns">
											<div id="single_job_details" class="recruitment_details">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<h3 style="font-size: 20px;font-weight: bold;">REQUIREMENTS</h3>
											<div id="single_job_requirements" class="recruitment_details">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
										</div>
									</div>
								</c:if>
								
								<c:if test="${param.work_type eq '2'}">
									<div class="row">
										<div class="small-12 columns">
											<div id="single_internship_title" class="recruitment_title">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<!-- <div class="single_job_divider"> -->
											</div>
										</div>
									</div>
									<div class="row">
										<div class="small-12 columns">
											<div id="single_internship_ref" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_internship_location" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_internship_category" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_internship_positions" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="single_internship_closing" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 20px;margin-bottom: 50px;">
										<h3 style="font-size: 20px;font-weight: bold;">ABOUT THE ROLE</h3>
										<div class="small-12 columns">
											<div id="single_internship_details" class="recruitment_details">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
										</div>
									</div>
								</c:if>

								<c:if test="${param.work_type eq '3'}">
									<div class="row">
										<div class="small-12 columns">
											<div id="client_job_title" class="recruitment_title">
												<i class="fas fa-sync fa-spin"> </i>
												
											</div>
											<!-- <div class="single_job_divider"> -->
											</div>
										</div>
									</div>
									<div class="row">
										<div class="small-12 columns">
											<div id="client_job_ref" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="client_job_location" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="client_job_category" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<div id="client_job_closing" class="recruitment_narration">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 20px;margin-bottom: 50px;">
										<h3 style="font-size: 20px;font-weight: bold;">ABOUT THE ROLE</h3>
										<div class="small-12 columns">
											<div id="client_job_details" class="recruitment_details">
												<i class="fas fa-sync fa-spin"> </i>
											</div>
											<!-- <h3 style="font-size: 20px;font-weight: bold;">REQUIREMENTS</h3>
											<div id="client_job_requirements" class="recruitment_details">
												<i class="fas fa-sync fa-spin"> </i>
											</div> -->
										</div>
									</div>
								</c:if>
								
								<div class="row">
									<!-- <div class="single_job_divider"> -->
									</div>
									<div class="small-12 large-6 columns">
										<label style="font-size: 15px;"> <span class="entrar_email" style="font-size: 14px;font-weight: bold;">What's your email address?*</span><br><br>
											<input type="text" name="application_email" id="application_email" placeholder="Email *" required="required" style="background-color: #d5e0fe;
											border: none;
											max-width: 330px;
											width: 330px;
											padding: 8px;">
											<input id="nf-field-4" onclick="job_application()" class="ninja-forms-field nf-element" type="button" value="APPLY NOW" style="background-color: #022a30;
											color: white;
											border: none;
											padding: 8px;">
										</label>
									</div>
									<!-- <div class="small-12 large-6 columns">
										<div class="nf-field-element">
											<input id="nf-field-4" 
											 onclick="job_application()"
											 class="ninja-forms-field nf-element" type="button" value="APPLY NOW">
										</div>
									</div> -->
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
<div class="page-footer" style="background-color: #022a30;">
	<div class="page-footer-inner" style="color: #fff;">
		2022 &copy; openBaraza <a href="http://dewcis.com" style="color: #db8b00;">Dew Cis Solutions Ltd</a> All Rights Reserved
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

<!-- openbaraza js-->
<script type="text/javascript" src="./assets/js/attendance-task.js"></script>

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

<script type="text/javascript" src="./assets/js/eagle-hr/recruitment_detail.js?1015"></script>

<script type="text/javascript">
    let queryString = window.location.search;
    let urlParams = new URLSearchParams(queryString);
    let job_id = urlParams.get('link');

    // set job_id input
    $("#job_id").val(job_id);

    console.log(job_id);
    <c:if test="${param.work_type eq '1'}">
    	handleSingleJobListing();
    </c:if>
    <c:if test="${param.work_type eq '2'}">
    	handleSingleInternshipListing();
    </c:if>
	<c:if test="${param.work_type eq '3'}">
    	handleClientJobListing();
    </c:if>
</script>

<script type="text/javascript">

	var app_xml = '<%=loginXml%>';

	function job_application() {
		var isValid = false;

		console.log("Check validation");
		
		let applicant_email = $('#application_email').val();
		applicant_email = applicant_email.trim();
		applicant_email = applicant_email.toLowerCase();
		let job_id = urlParams.get('link');
		console.log(applicant_email);
		console.log(job_id);
		
		let valid_email = validateEmail(applicant_email);
		if(valid_email) {
		
			<c:if test="${param.work_type eq '1'}">
    			let emailUrl = "./recruitment?tag=search_email&email=" + applicant_email + "&intake_id=" + job_id;
    		</c:if>
    		<c:if test="${param.work_type eq '2'}">
    			let emailUrl = "./recruitment?tag=search_intern_email&email=" 
    				+ applicant_email + "&internship_id=" + job_id;
    		</c:if>
			<c:if test="${param.work_type eq '3'}">
    			let emailUrl = "./recruitment?tag=search_email&email=" + applicant_email + "&intake_id=" + job_id;
    		</c:if>
			
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
						let checkEmailMsg = "<div style='color:blue'><i class='glyphicon glyphicon-ok'></i>" + data.message + "</div>";
						$('#job_apply_msg').html(checkEmailMsg);
					} else if(data.success == 2) {
						<c:if test="${param.work_type eq '1'}">
    						let applyUrl = "./recruitment?tag=apply_job&email=" + applicant_email
								+ "&entity_id=" + data.entityId + "&intake_id=" + job_id;
						</c:if>
						<c:if test="${param.work_type eq '2'}">
    						let applyUrl = "./recruitment?tag=apply_internship&email=" + applicant_email
								+ "&entity_id=" + data.entityId + "&internship_id=" + job_id;
						</c:if>
						<c:if test="${param.work_type eq '3'}">
    						let applyUrl = "./recruitment?tag=apply_job&email=" + applicant_email
								+ "&entity_id=" + data.entityId + "&intake_id=" + job_id;
						</c:if>
						
						$.ajax({
							type: 'GET',
							url: applyUrl,
							dataType: 'json',
							success: function(adata) {
								console.log(adata);
								
								let checkApplyMsg = "<div style='color:blue'><i class='glyphicon glyphicon-ok'></i>" + adata.message + "</div>";
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
				let job_id = urlParams.get('link');
				<c:if test="${param.work_type eq '1'}">
					let applyUrl = "./recruitment?tag=apply_job&email=" + data.data.applicant_email
						+ "&entity_id=" + data.data.keyfield + "&intake_id=" + job_id;
				</c:if>
				<c:if test="${param.work_type eq '2'}">
					let applyUrl = "./recruitment?tag=apply_internship&email=" + data.data.applicant_email
						+ "&entity_id=" + data.data.keyfield + "&internship_id=" + job_id;
				</c:if>
				<c:if test="${param.work_type eq '3'}">
					let applyUrl = "./recruitment?tag=apply_job&email=" + data.data.applicant_email
						+ "&entity_id=" + data.data.keyfield + "&intake_id=" + job_id;
				</c:if>
				console.log(applyUrl);
				
							
				$.ajax({
					type: 'GET',
					url: applyUrl,
					dataType: 'json',
					success: function(adata) {
						console.log(adata);
						
						let checkApplyMsg = "<div style='color:blue'><i class='glyphicon glyphicon-ok'></i>" + adata.message + "</div>";
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

