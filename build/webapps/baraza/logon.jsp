<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<%@ page import="org.baraza.web.BWebData" %>
<%

	String applicantModal = "";
	String subscriptionModal = "";
	String resetPasswordModal = "";
	
	ServletContext context = getServletContext();
	//String loginXml = "application.xml";
	String loginXml = context.getInitParameter("login_xml");
System.out.println("BASE LOGIN : " + loginXml);

	if(loginXml != null) {
		BWebData webData = new BWebData(context, request, loginXml);
		applicantModal = webData.getModalForm(request, "1:0");
		subscriptionModal = webData.getModalForm(request, "10:0");
		resetPasswordModal = webData.getModalForm(request, "5:0");
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

	<title><%= pageContext.getServletContext().getInitParameter("web_title") %></title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
	<meta http-equiv="Content-type" content="text/html; charset=utf-8">
	<meta content="" name="description"/>
	<meta content="" name="author"/>

	<!-- BEGIN GLOBAL MANDATORY STYLES -->
	<!-- <link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/> -->
	<link href="${contextPath}/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
	<link href="${contextPath}/assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
	<link href="${contextPath}/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="${contextPath}/assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css"/>
	<!-- END GLOBAL MANDATORY STYLES -->

	<!-- BEGIN PAGE LEVEL STYLES -->
	<link href="${contextPath}/assets/global/plugins/select2/select2.css" rel="stylesheet" type="text/css"/>
	<link href="${contextPath}/assets/admin/pages/css/login-soft.css" rel="stylesheet" type="text/css"/>
	<!-- END PAGE LEVEL SCRIPTS -->

	<!-- BEGIN PAGE LEVEL PLUGIN STYLES -->
	<link href="./assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/fullcalendar/fullcalendar.min.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/jqvmap/jqvmap/jqvmap.css" rel="stylesheet" type="text/css"/>
	<link href="./assets/global/plugins/morris/morris.css" rel="stylesheet" type="text/css">
	<link href="./assets/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-colorpicker/css/colorpicker.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css" />
	<!-- END PAGE LEVEL PLUGIN STYLES -->

	<!-- BEGIN THEME STYLES -->
	<link href="${contextPath}/assets/global/css/components-md.css" id="style_components" rel="stylesheet" type="text/css"/>
	<link href="${contextPath}/assets/global/css/plugins-md.css" rel="stylesheet" type="text/css"/>
	<link href="${contextPath}/assets/admin/layout4/css/layout.css" rel="stylesheet" type="text/css"/>
	<link id="style_color" href="${contextPath}/assets/admin/layout4/css/themes/default.css" rel="stylesheet" type="text/css"/>
		
	<link href="${contextPath}/assets/admin/layout4/css/custom.css" rel="stylesheet" type="text/css"/>
	<!-- END THEME STYLES -->

	<style type="text/css">
		.form-input {
			color: #495057;
			height: 34px;
			border: 1px solid #495057;
			padding: .75em;
		}
		.form-input::placeholder {
			color:  #495057;
		}
	</style>    

	<link rel="shortcut icon" href="./assets/logos/favicon.png"/>
</head>

<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="page-md login" id="login">
<!-- BEGIN LOGO -->
<div class="logo">
	<a href="http://openbaraza.org/">
    <img src="${contextPath}/assets/logos/logo.png" alt=""/>
	</a>
</div>
<!-- END LOGO -->
<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
<div class="menu-toggler sidebar-toggler">
</div>
<!-- END SIDEBAR TOGGLER BUTTON -->
<!-- BEGIN LOGIN -->
<div class="content">
	<!-- BEGIN LOGIN FORM -->
	<form class="login-form" method="POST" action="j_security_check" method="post">
		<h3 class="form-title"><%= pageContext.getServletContext().getInitParameter("login_title") %></h3>
		<div class="alert alert-danger display-hide">
			<button class="close" data-close="alert"></button>
			<span>Enter  username and password. </span>
		</div>
		<div class="form-group">
			<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
			<label class="control-label visible-ie8 visible-ie9">Username</label>
			<div class="input-icon">
				<i class="fa fa-user"></i>
				<input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="Username" 
				id="j_username" name="j_username" autofocus required
				/>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label visible-ie8 visible-ie9">Password</label>
			<div class="input-icon">
				<i class="fa fa-lock"></i>
				<input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="Password" 
				id="j_password" name="j_password" required/>
			</div>
		</div>
		<div class="form-actions">
			<label class="checkbox">
			<input type="checkbox" name="remember" value="1"/> Remember me </label>
			<button type="submit" class="btn blue pull-right">
			Login <i class="m-icon-swapright m-icon-white"></i>
			</button>
		</div>
		
<% if(loginXml != null) { %>
		<a class="" href="" data-toggle="modal" data-target="#modal_subscription" style="color:#429539;font-size:18px">Company Subscription</a><br>
		<a class="" href="" data-toggle="modal" data-target="#modal_application" style="color:#0088cc; font-size:14px">Register New Account</a> 

		<div class="forget-password">
			<h4>Forgot your password ?</h4>
			<p>
				 no worries, click <a class="" href="" data-toggle="modal" data-target="#modal_passwordReset" style="color:#0088cc;">Recover Lost Password</a>
				to reset your password.
			</p>
		</div>
<% } %>   
		
	</form>
	<!-- END LOGIN FORM -->

	<!-- BEGIN FORGOT PASSWORD FORM -->
		<%=resetPasswordModal%>
	<!-- END FORGOT PASSWORD FORM -->

	<!-- APPLICANT MODAL -->
		<%=applicantModal%>
	<!-- END APPLICANT MODAL -->

	<!-- SUBSCRIPTION POPUP -->
		<%=subscriptionModal%>
	<!-- END SUBSCRIPTION -->

	<!-- MESSAGE MODAL -->
	<div class="modal fade" id="messageModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" data-backdrop="static">
	  <div class="modal-dialog modal-dialog-centered modal-sm" style="padding-top:7%;" role="document">
	    <div class="modal-content">
	      <div class="modal-body">
	      	<div id="alert_message"></div>
	      </div>
	      <div class="modal-footer" style="text-align:center;">
	        <button type="button" class="btn btn-primary" data-dismiss="modal" id="dismiss_msg">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	<!-- END MESSAGE MODAL -->

</div>
<!-- END LOGIN -->

<div class="content">
	<div class="form-group">
		<div class="form-group">
        	<div id="list_opportunities">
        	</div>
        </div>
	</div>	
</div>

<!-- BEGIN COPYRIGHT -->
<div class="copyright">
	 2020 &copy; openBaraza
</div>
<!-- END COPYRIGHT -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<!-- BEGIN CORE PLUGINS -->
<!--[if lt IE 9]>
<script src="${contextPath}/assets/global/plugins/respond.min.js"></script>
<script src="${contextPath}/assets/global/plugins/excanvas.min.js"></script> 
<![endif]-->
<script src="${contextPath}/assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/jquery-migrate.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="${contextPath}/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/backstretch/jquery.backstretch.min.js" type="text/javascript"></script>
<script type="text/javascript" src="${contextPath}/assets/global/plugins/select2/select2.min.js"></script>
<script src="${contextPath}/assets/global/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js" type="text/javascript" ></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${contextPath}/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${contextPath}/assets/admin/pages/scripts/login-soft.js" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->

<!-- calendar-->
<!-- IMPORTANT! fullcalendar depends on jquery-ui.min.js for drag & drop support -->
<script type="text/javascript" src="./assets/global/plugins/moment.min.js"></script>
<script type="text/javascript" src="./assets/global/plugins/fullcalendar/fullcalendar.min.js"></script>
<script type="text/javascript" src="./assets/global/plugins/fullcalendar/lang-all.js"></script>


<script>
	jQuery(document).ready(function() {     
		Metronic.init(); // init metronic core components
		Metronic.setAssetsPath('assets/'); // Set the assets folder path
		Calendar.init();
		Login.init();

		// init background slide images
		$.backstretch([
			"${contextPath}/assets/admin/pages/media/bg/1.jpg",
			"${contextPath}/assets/admin/pages/media/bg/2.jpg",
			"${contextPath}/assets/admin/pages/media/bg/3.jpg",
			"${contextPath}/assets/admin/pages/media/bg/4.jpg"
			], {
				fade: 1000,
				duration: 8000
			}
		);
	});

	var app_xml = '<%=loginXml%>';

	$("#btn_application").click(function(e) {
		if( !validate($("#frm_application").serializeArray()) ) {return;}	
		var form_data = JSON.stringify($("#frm_application").serializeArray());
		$.post('ajaxinsert', {app_xml:app_xml, app_key:'1:0', form_data:form_data}, function(data) {
			console.log(data);
			responseMessage(data);
		});
	});

	$("#btn_subscription").click(function(e) {
		if( !validate($("#frm_subscription").serializeArray()) ) {return;}
		if( !confirmEmail() ){return;}
		var form_data = JSON.stringify($("#frm_subscription").serializeArray());
		$.post('ajaxinsert', {app_xml:app_xml, app_key:'10:0', form_data:form_data}, function(data) {
			console.log(data);
			responseMessage(data);
		});
	});

	$("#btn_passwordReset").click(function(e) {
		if( !validate($("#frm_passwordReset").serializeArray()) ) {return;}
		if( !confirmEmail() ){return;}
		var form_data = JSON.stringify($("#frm_passwordReset").serializeArray());
		$.post('ajaxinsert', {app_xml:app_xml, app_key:'5:0', form_data:form_data}, function(data) {
			console.log(data);
			responseMessage(data);
		});
	});

	function responseMessage(data) {
		$('#alert_message').html('');
		if (data.error==0) {
			$('#alert_message').html('<div class="alert alert-success" style="margin-bottom: 0;" role="alert">' + data.msg + '</div>');
		} else {
			$('#alert_message').html('<div class="alert alert-danger" style="margin-bottom: 0;" role="alert">' + data.error_msg + '</div>');
		}
		$('#dismiss_msg').on('click', function() {
			$('.modal').modal('hide');
			$(".modal form").trigger("reset");
		});
		$('#messageModal').modal('show');
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

	function confirmEmail() {
		$("#confirm_email").prev('.text-danger').remove();
		if( $("#primary_email").val() != $("#confirm_email").val() ) {
			$("#confirm_email").css({"border":"1px solid #ff000087"});
			$("#confirm_email").before('<span class="text-danger">Emails do not match</span>');
			return false;
		} else return true;
	}

	$("div.modal-body input").on('change', function(){
		if($(this).attr('name').includes('email')) {
		    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		    if (!re.test(String($(this).val()).toLowerCase())) {
		    	$(this).css({"border":"1px solid #ff000087"});
				$(this).before('<span class="text-danger">Enter a valid email address</span>');
		    } else {
		    	$(this).prev('.text-danger').remove();
				$(this).css({"border":"1px solid #e5e5e5"});
		    }
		}else if ( $(this).val()=="" && $(this).attr("placeholder").includes("*") ) {
			$(this).css({"border":"1px solid #ff000087"});
			$(this).before('<span class="text-danger">This field is required *</span>');
		} else {
			$(this).prev('.text-danger').remove();
			$(this).css({"border":"1px solid #e5e5e5"});
		}
	});

	$(function(){    
	  $('.date-picker').datepicker({
	        format: 'dd-mm-yyyy',
	        autoclose: true
	    });
	    $(".date-picker").click(function(){
	    	$('.date-picker').datepicker("show");
	    })
	});

</script>

<% if(loginXml != null) { %>

<script type="text/javascript" src="./assets/js/login_api.js?1026"></script>
<script type="text/javascript">
    listOpportunityCounts();
</script>

<% } %>
<!-- END JAVASCRIPTS -->

<%@ include file="./assets/include/calendar.jsp" %>

</body>
<!-- END BODY -->
</html>
