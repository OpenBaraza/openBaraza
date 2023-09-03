<%@ page import="java.util.Vector" %>
<%@ page import="org.baraza.DB.BDB" %>
<%

BDB db = new BDB("java:/comp/env/jdbc/database");

String code = request.getParameter("code");

if(code != null) {
String mySql = "UPDATE applications SET is_confirmed = true WHERE (confirmation_code = '"
	+ code + "') AND (is_confirmed = false)";

db.executeUpdate(mySql);
}

db.close();

%>
<!DOCTYPE html>
<!-- saved from url=(0034)https://eaglehr.co.ke/recruitment/ -->
<!-- https://get.foundation/sites/docs/reveal.html -->
<!-- https://get.foundation/sites/docs/forms.html -->
<html class="whatinput-types-initial whatinput-types-initial whatinput-types-mouse whatinput-types-mouse whatinput-types-keyboard whatinput-types-keyboard" lang="en-US" data-whatinput="keyboard" data-whatintent="mouse">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


<!-- Force IE to use the latest rendering engine available -->
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<!-- Mobile Meta -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta class="foundation-mq">

<!-- foundation css  -->

<!-- <link href="./assets/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css" rel="stylesheet" type="text/css" /> -->
<script type="text/javascript" src="./assets/eagle-hr/assets/bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="./assets/eagle-hr/assets/bower_components/moment/min/moment.min.js"></script>
<!-- <script type="text/javascript" src="./assets/eagle-hr/assets/bower_components/bootstrap/dist/js/bootstrap.min.js"></script> -->
<script type="text/javascript" src="./assets/eagle-hr/assets/bower_components/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"></script>
<!-- <link rel="stylesheet" href="./assets/eagle-hr/assets/bower_components/bootstrap/dist/css/bootstrap.min.css" /> -->
<link rel="stylesheet" href="./assets/eagle-hr/assets/bower_components/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.min.css" />

<link rel="stylesheet" href="./assets/eagle-hr/assets/foundation.css">
<link rel="stylesheet" href="./assets/eagle-hr/assets/app.css">
<link rel="stylesheet" href="./assets/eagle-hr/assets/font-awesome.min.css">
<link rel="stylesheet" href="./assets/eagle-hr/assets/all.min.css">

<!-- If Site Icon isn't set in customizer -->

<link rel="pingback" href="https://eaglehr.co.ke/xmlrpc.php">

<title>Finding jobs in Kenya and Africa?</title>
<meta name="robots" content="max-image-preview:large">
<link rel="dns-prefetch" href="https://maps.googleapis.com/">
<link rel="dns-prefetch" href="https://fonts.googleapis.com/">
<!-- <link rel='stylesheet' id='mec-custom-google-font-css' href='https://fonts.googleapis.com/css?family=%7CDefault+Font%3A%2C+&#038;subset=latin%2Clatin-ext' type='text/css' media='all' /> -->
<link rel='stylesheet' id='mec-google-fonts-css' href='https://fonts.googleapis.com/css?family=Montserrat%3A400%2C700%7CRoboto%3A100%2C300%2C400%2C700&#038;ver=5.8' type='text/css' media='all' />
<link rel="dns-prefetch" href="https://s.w.org/">
<link rel="alternate" type="application/rss+xml" title="EAGLE HR CONSULTANTS » Feed" href="https://eaglehr.co.ke/feed/">
<link rel="alternate" type="application/rss+xml" title="EAGLE HR CONSULTANTS » Comments Feed" href="https://eaglehr.co.ke/comments/feed/">
<script type="text/javascript">
    window._wpemojiSettings = {
        "baseUrl": "https:\/\/s.w.org\/images\/core\/emoji\/13.1.0\/72x72\/",
        "ext": ".png",
        "svgUrl": "https:\/\/s.w.org\/images\/core\/emoji\/13.1.0\/svg\/",
        "svgExt": ".svg",
        "source": {
            "concatemoji": "https:\/\/eaglehr.co.ke\/wp-includes\/js\/wp-emoji-release.min.js?ver=5.8"
        }
    };
    ! function(e, a, t) {
        var n, r, o, i = a.createElement("canvas"),
            p = i.getContext && i.getContext("2d");

        function s(e, t) {
            var a = String.fromCharCode;
            p.clearRect(0, 0, i.width, i.height), p.fillText(a.apply(this, e), 0, 0);
            e = i.toDataURL();
            return p.clearRect(0, 0, i.width, i.height), p.fillText(a.apply(this, t), 0, 0), e === i.toDataURL()
        }

        function c(e) {
            var t = a.createElement("script");
            t.src = e, t.defer = t.type = "text/javascript", a.getElementsByTagName("head")[0].appendChild(t)
        }
        for (o = Array("flag", "emoji"), t.supports = {
                everything: !0,
                everythingExceptFlag: !0
            }, r = 0; r < o.length; r++) t.supports[o[r]] = function(e) {
            if (!p || !p.fillText) return !1;
            switch (p.textBaseline = "top", p.font = "600 32px Arial", e) {
                case "flag":
                    return s([127987, 65039, 8205, 9895, 65039], [127987, 65039, 8203, 9895, 65039]) ? !1 : !s([55356, 56826, 55356, 56819], [55356, 56826, 8203, 55356, 56819]) && !s([55356, 57332, 56128, 56423, 56128, 56418, 56128, 56421, 56128, 56430, 56128, 56423, 56128, 56447], [55356, 57332, 8203, 56128, 56423, 8203, 56128, 56418, 8203, 56128, 56421, 8203, 56128, 56430, 8203, 56128, 56423, 8203, 56128, 56447]);
                case "emoji":
                    return !s([10084, 65039, 8205, 55357, 56613], [10084, 65039, 8203, 55357, 56613])
            }
            return !1
        }(o[r]), t.supports.everything = t.supports.everything && t.supports[o[r]], "flag" !== o[r] && (t.supports.everythingExceptFlag = t.supports.everythingExceptFlag && t.supports[o[r]]);
        t.supports.everythingExceptFlag = t.supports.everythingExceptFlag && !t.supports.flag, t.DOMReady = !1, t.readyCallback = function() {
            t.DOMReady = !0
        }, t.supports.everything || (n = function() {
            t.readyCallback()
        }, a.addEventListener ? (a.addEventListener("DOMContentLoaded", n, !1), e.addEventListener("load", n, !1)) : (e.attachEvent("onload", n), a.attachEvent("onreadystatechange", function() {
            "complete" === a.readyState && t.readyCallback()
        })), (n = t.source || {}).concatemoji ? c(n.concatemoji) : n.wpemoji && n.twemoji && (c(n.twemoji), c(n.wpemoji)))
    }(window, document, window._wpemojiSettings);
</script>
<script data-dapp-detection="">
    ! function() {
        let e = !1;

        function n() {
            if (!e) {
                const n = document.createElement("meta");
                n.name = "dapp-detected", document.head.appendChild(n), e = !0
            }
        }
        if (window.hasOwnProperty("ethereum")) {
            if (window.__disableDappDetectionInsertion = !0, void 0 === window.ethereum) return;
            n()
        } else {
            var t = window.ethereum;
            Object.defineProperty(window, "ethereum", {
                configurable: !0,
                enumerable: !1,
                set: function(e) {
                    window.__disableDappDetectionInsertion || n(), t = e
                },
                get: function() {
                    if (!window.__disableDappDetectionInsertion) {
                        const e = arguments.callee;
                        e && e.caller && e.caller.toString && -1 !== e.caller.toString().indexOf("getOwnPropertyNames") || n()
                    }
                    return t
                }
            })
        }
    }();
</script>
<script src="./assets/eagle-hr/assets/wp-emoji-release.min.js.download" type="text/javascript" defer=""></script>
<style type="text/css">
    img.wp-smiley,
    img.emoji {
        display: inline !important;
        border: none !important;
        box-shadow: none !important;
        height: 1em !important;
        width: 1em !important;
        margin: 0 .07em !important;
        vertical-align: -0.1em !important;
        background: none !important;
        padding: 0 !important;
    }
</style>
<link rel="stylesheet" id="wp-color-picker-css" href="./assets/eagle-hr/assets/color-picker.min.css" type="text/css" media="all">
<link rel="stylesheet" id="mec-owl-carousel-style-css" href="./assets/eagle-hr/assets/owl.carousel.css" type="text/css" media="all">
<link rel="stylesheet" id="mec-owl-carousel-theme-style-css" href="./assets/eagle-hr/assets/owl.theme.css" type="text/css" media="all">
<link rel="stylesheet" id="mec-font-icons-css" href="./assets/eagle-hr/assets/iconfonts.css" type="text/css" media="all">
<link rel="stylesheet" id="mec-frontend-style-css" href="./assets/eagle-hr/assets/frontend.css" type="text/css" media="all">
<link rel="stylesheet" id="mec-google-fonts-css" href="./assets/eagle-hr/assets/css" type="text/css" media="all">
<link rel="stylesheet" id="mec-dynamic-styles-css" href="./assets/eagle-hr/assets/dyncss.css" type="text/css" media="all">
<style id="mec-dynamic-styles-inline-css" type="text/css">
    .mec-event-content p {
        font-family: 'Default Font', sans-serif;
        font-weight: 300;
    }
    
    .mec-wrap.colorskin-custom .mec-color,
    .mec-wrap.colorskin-custom .mec-event-sharing-wrap .mec-event-sharing>li:hover a,
    .mec-wrap.colorskin-custom .mec-color-hover:hover,
    .mec-wrap.colorskin-custom .mec-color-before *:before,
    .mec-wrap.colorskin-custom .mec-widget .mec-event-grid-classic.owl-carousel .owl-controls .owl-buttons i,
    .mec-wrap.colorskin-custom .mec-event-list-classic a.magicmore:hover,
    .mec-wrap.colorskin-custom .mec-event-grid-simple:hover .mec-event-title,
    .mec-wrap.colorskin-custom .mec-single-event .mec-event-meta dd.mec-events-event-categories:before,
    .mec-wrap.colorskin-custom .mec-single-event-date:before,
    .mec-wrap.colorskin-custom .mec-single-event-time:before,
    .mec-wrap.colorskin-custom .mec-events-meta-group.mec-events-meta-group-venue:before,
    .mec-wrap.colorskin-custom .mec-calendar .mec-calendar-side .mec-previous-month i,
    .mec-wrap.colorskin-custom .mec-calendar .mec-calendar-side .mec-next-month,
    .mec-wrap.colorskin-custom .mec-calendar .mec-calendar-side .mec-previous-month:hover,
    .mec-wrap.colorskin-custom .mec-calendar .mec-calendar-side .mec-next-month:hover,
    .mec-wrap.colorskin-custom .mec-calendar.mec-event-calendar-classic dt.mec-selected-day:hover,
    .mec-wrap.colorskin-custom .mec-infowindow-wp h5 a:hover,
    .mec-events-meta-group-countdown .mec-end-counts h3 {
        color: #f7941d
    }
    
    .mec-wrap.colorskin-custom .mec-event-sharing .mec-event-share:hover .event-sharing-icon,
    .mec-wrap.colorskin-custom .mec-event-grid-clean .mec-event-date,
    .mec-wrap.colorskin-custom .mec-event-list-modern .mec-event-sharing>li:hover a i,
    .mec-wrap.colorskin-custom .mec-event-list-modern .mec-event-sharing .mec-event-share:hover .mec-event-sharing-icon,
    .mec-wrap.colorskin-custom .mec-event-list-modern .mec-event-sharing li:hover a i,
    .mec-wrap.colorskin-custom .mec-calendar .mec-selected-day,
    .mec-wrap.colorskin-custom .mec-calendar .mec-selected-day:hover,
    .mec-wrap.colorskin-custom .mec-calendar .mec-calendar-row dt.mec-has-event:hover,
    .mec-wrap.colorskin-custom .mec-calendar .mec-has-event:after,
    .mec-wrap.colorskin-custom .mec-bg-color,
    .mec-wrap.colorskin-custom .mec-bg-color-hover:hover,
    .colorskin-custom .mec-event-sharing-wrap:hover>li {
        background-color: #f7941d;
    }
    
    .mec-wrap.colorskin-custom .mec-event-list-modern .mec-event-sharing>li:hover a i,
    .mec-wrap.colorskin-custom .mec-event-list-modern .mec-event-sharing .mec-event-share:hover .mec-event-sharing-icon,
    .mec-wrap.colorskin-custom .mec-event-list-standard .mec-month-divider span:before,
    .mec-wrap.colorskin-custom .mec-single-event .mec-social-single:before,
    .mec-wrap.colorskin-custom .mec-single-event .mec-frontbox-title:before,
    .mec-wrap.colorskin-custom .mec-calendar .mec-calendar-events-side .mec-table-side-day,
    .mec-wrap.colorskin-custom .mec-border-color,
    .mec-wrap.colorskin-custom .mec-border-color-hover:hover {
        border-color: #f7941d;
    }
</style>
<!-- <link rel="stylesheet" id="mec-custom-google-font-css" href="./assets/eagle-hr/assets/css(1)" type="text/css" media="all"> -->
<link rel="stylesheet" id="wp-block-library-css" href="./assets/eagle-hr/assets/style.min.css" type="text/css" media="all">
<link rel="stylesheet" id="contact-form-7-css" href="./assets/eagle-hr/assets/styles.css" type="text/css" media="all">
<link rel="stylesheet" id="email-subscribers-css" href="./assets/eagle-hr/assets/email-subscribers-public.css" type="text/css" media="all">
<link rel="stylesheet" id="shiftnav-css" href="./assets/eagle-hr/assets/shiftnav.min.css" type="text/css" media="all">
<link rel="stylesheet" id="shiftnav-font-awesome-css" href="./assets/eagle-hr/assets/font-awesome.min(1).css" type="text/css" media="all">
<link rel="stylesheet" id="shiftnav-light-css" href="./assets/eagle-hr/assets/light.css" type="text/css" media="all">
<link rel="stylesheet" id="motion-ui-css-css" href="./assets/eagle-hr/assets/motion-ui.min.css" type="text/css" media="all">
<!-- <link rel="stylesheet" id="site-css-css" href="./assets/eagle-hr/assets/style.css" type="text/css" media="all"> -->
<script type="text/javascript" src="./assets/eagle-hr/assets/jquery.min.js.download" id="jquery-core-js"></script>
<script type="text/javascript" src="./assets/eagle-hr/assets/jquery-migrate.min.js.download" id="jquery-migrate-js"></script>
<script type="text/javascript" src="./assets/eagle-hr/assets/owl.carousel.min.js.download" id="mec-owl-carousel-script-js"></script>
<script type="text/javascript" src="./assets/eagle-hr/assets/jquery.validate.min.js.download" id="mec-form-validate-script-js"></script>
<script type="text/javascript" id="mec-frontend-script-js-extra">
    /* <![CDATA[ */
    var mecdata = {
        "day": "day",
        "days": "days",
        "hour": "hour",
        "hours": "hours",
        "minute": "minute",
        "minutes": "minutes",
        "second": "second",
        "seconds": "seconds"
    };
    /* ]]> */
</script>
<script type="text/javascript" src="./assets/eagle-hr/assets/frontend.js.download" id="mec-frontend-script-js"></script>
<!-- <script type="text/javascript" src="./assets/eagle-hr/assets/events.js.download" id="mec-events-script-js"></script> -->
<script type="text/javascript" src="./assets/eagle-hr/assets/js" id="googlemap-js"></script>
<script type="text/javascript" id="email-subscribers-js-extra">
    /* <![CDATA[ */
    var es_data = {
        "messages": {
            "es_empty_email_notice": "Please enter email address",
            "es_rate_limit_notice": "You need to wait for sometime before subscribing again",
            "es_single_optin_success_message": "Successfully Subscribed.",
            "es_email_exists_notice": "Email Address already exists!",
            "es_unexpected_error_notice": "Oops.. Unexpected error occurred.",
            "es_invalid_email_notice": "Invalid email address",
            "es_try_later_notice": "Please try after some time"
        },
        "es_ajax_url": "https:\/\/eaglehr.co.ke\/wp-admin\/admin-ajax.php"
    };
    /* ]]> */
</script>
<script type="text/javascript" src="./assets/eagle-hr/assets/email-subscribers-public.js.download" id="email-subscribers-js"></script>
<link rel="https://api.w.org/" href="https://eaglehr.co.ke/wp-json/">
<link rel="alternate" type="application/json" href="https://eaglehr.co.ke/wp-json/wp/v2/pages/9">
<link rel="shortlink" href="https://eaglehr.co.ke/?p=9">
<link rel="alternate" type="application/json+oembed" href="https://eaglehr.co.ke/wp-json/oembed/1.0/embed?url=https%3A%2F%2Feaglehr.co.ke%2Frecruitment%2F">
<link rel="alternate" type="text/xml+oembed" href="https://eaglehr.co.ke/wp-json/oembed/1.0/embed?url=https%3A%2F%2Feaglehr.co.ke%2Frecruitment%2F&amp;format=xml">

<!-- ShiftNav CSS
================================================================ -->
<style type="text/css" id="shiftnav-dynamic-css">
    @media only screen and (min-width:800px) {
        #shiftnav-toggle-main,
        .shiftnav-toggle-mobile {
            display: none;
        }
        .shiftnav-wrap {
            padding-top: 0 !important;
        }
    }
    /* Status: Loaded from Transient */
    
    .tabs-title>a:focus,
    .tabs-title>a[aria-selected='true'] {
        background: #022a30;
        color: #fff;
    }
    
    .tabs-title>a:hover {
        background: #022a30;
        color: #fff;
    }
    
    .tabs-title>a {
        color: #fff;
    }
    
    .tabs {
        background: #db8b00;
    }
    
    .tabs-content {
        /* border-bottom: 0px; */
        padding-bottom: 40px;
        background-color: #f5f6fa;
    }
    
    [type='text'],
    [type='date'],
    select {
        outline: none !important;
        /* border-color: #db8b00;
        border: 2px solid #db8b00; */
        border-color: #e3e3e3;
        border: 2px solid #e3e3e3;
    }
    
    [type='text']:focus,
    [type='date']:focus,
    select:focus {
        outline: none !important;
        /* border-color: #022a30;
        border: 2px solid #022a30; */
        border-color: #db8b00;
        border: 2px solid #db8b00;
    }
    
    label {
        font-weight: 800;
        line-height: 1.8;
        color: #022a30;
    }
    
    .single_job {
        padding-top: 80px;
        padding-bottom: 80px;
    }
</style>
<!-- end ShiftNav CSS -->

<link rel="canonical" href="https://eaglehr.co.ke/recruitment/">
<meta name="twitter:card" content="summary">
<meta name="twitter:domain" content="EAGLE HR CONSULTANTS">
<meta name="twitter:description" content="At Eagle HR Consultants, we will help you find the right job that best fits your qualifications and experience, we work closely with companies that when hiring need to get the right personnel to fill the positions offered, we are the best HR Consultants in Kenya and Africa!">
<meta name="twitter:title" content="Finding jobs in Kenya and Africa?">
<meta property="og:site_name" content="EAGLE HR CONSULTANTS">
<meta property="og:description" content="At Eagle HR Consultants, we will help you find the right job that best fits your qualifications and experience, we work closely with companies that when hiring need to get the right personnel to fill the positions offered, we are the best HR Consultants in Kenya and Africa!">
<meta property="og:url" content="https://eaglehr.co.ke/recruitment/">
<meta property="og:type" content="article">
<meta property="og:title" content="Finding jobs in Kenya and Africa?">
<meta name="description" content="At Eagle HR Consultants, we will help you find the right job that best fits your qualifications and experience, we work closely with companies that when hiring need to get the right personnel to fill the positions offered, we are the best HR Consultants in Kenya and Africa!">
<meta name="title" content="Finding jobs in Kenya and Africa?">
<link rel="icon" href="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-32x32.png" sizes="32x32">
<link rel="icon" href="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-192x192.png" sizes="192x192">
<link rel="apple-touch-icon" href="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-180x180.png">
<meta name="msapplication-TileImage" content="https://eaglehr.co.ke/wp-content/uploads/2021/02/cropped-logo_dark-3-270x270.png">

<!-- Drop Google Analytics here -->
<!-- end analytics -->
<!-- Global site tag (gtag.js) - Google Analytics -->
<script async="" src="./assets/eagle-hr/assets/js(1)"></script>
<script>
    window.dataLayer = window.dataLayer || [];

    function gtag() {
        dataLayer.push(arguments);
    }
    gtag('js', new Date());

    gtag('config', 'UA-72134826-23');
</script>

<script type="text/javascript" charset="UTF-8" src="./assets/eagle-hr/assets/common.js.download"></script>
<script type="text/javascript" charset="UTF-8" src="./assets/eagle-hr/assets/util.js.download"></script>
</head>

<!-- Uncomment this line if using the Off-Canvas Menu -->

<!-- LOADER -->

<body class="shiftnav-enabled shiftnav-lock shiftnav-disable-shift-body">
<div class="loader"></div>




<div class="off-canvas-wrapper">

    <div class="off-canvas position-right is-transition-push" id="off-canvas" data-off-canvas="bnbcdg-off-canvas" aria-hidden="true">
        <ul id="menu-main_menu" class="vertical medium-horizontal menu dropdown" data-responsive-menu="accordion medium-dropdown" role="menubar" data-dropdown-menu="4avyus-dropdown-menu" data-mutate="fik3hj-responsive-menu">
            <li id="menu-item-18" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-18" role="menuitem"><a href="https://eaglehr.co.ke/about-us/">About Us</a></li>
            <li id="menu-item-16" class="menu-item menu-item-type-post_type menu-item-object-page current-menu-item page_item page-item-9 current_page_item menu-item-16 active" role="menuitem"><a href="https://eagle.hcm.co.ke/hcmke/recruitment.jsp" aria-current="page">Recruitment</a></li>
            <li id="menu-item-99" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-99" role="menuitem"><a href="https://eaglehr.co.ke/hr-outsourcing/">HR Outsourcing</a></li>
            <li id="menu-item-15" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-15" role="menuitem"><a href="https://eaglehr.co.ke/training/">Training</a></li>
            <li id="menu-item-17" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-17" role="menuitem"><a href="https://eaglehr.co.ke/contact-us/">Contact Us</a></li>
        </ul>
    </div>
    <div class="off-canvas-content" data-off-canvas-content="">

        <!-- START OF HEADER SECTION -->
        <header class="nude_header">

            <!-- This navs will be applied to the topbar, above all content 
      To see additional nav styles, visit the /parts directory -->
            <!-- By default, this menu will use off-canvas for small
    and a topbar for medium-up -->
            <div class="row">
                <div class="small-12 columns">
                    <div class="top-bar">

                        <div class="top-bar-left small-4 columns">
                            <a href="https://eaglehr.co.ke">
                                <img src="https://eaglehr.co.ke/wp-content/themes/eagle-dezari/assets/imgs/logo_dark.png" alt="EAGLE HR CONSULTANTS" class="logo">
                            </a>
                        </div>
                        <div class="top-bar-right hide-for-small-only">
                            <ul id="menu-main_menu-1" class="vertical medium-horizontal menu dropdown" data-responsive-menu="accordion medium-dropdown" role="menubar" data-dropdown-menu="zbj1og-dropdown-menu" data-mutate="6jm4ky-responsive-menu">
                                <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-18" role="menuitem"><a href="https://eaglehr.co.ke/about-us/">About Us</a></li>
                                <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-16" role="menuitem"><a href="https://eagle.hcm.co.ke/hcmke/recruitment.jsp">Recruitment</a></li>
                                <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-99" role="menuitem"><a href="https://eaglehr.co.ke/hr-outsourcing/">HR Outsourcing</a></li>
                                <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-15" role="menuitem"><a href="https://eaglehr.co.ke/training/">Training</a></li>
                                <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-17" role="menuitem"><a href="https://eaglehr.co.ke/contact-us/">Contact Us</a></li>
                            </ul>

                        </div>


                    </div>
                </div>
            </div>

        </header>
        <!-- end .header -->
        <div id="content">

            <div id="inner-content">

                <main id="main" role="main">


                    <!-- START OF SINGLE JOB  SECTION -->
                    <section class="recruitment">
                        <div class="row">
                            <div class="small-12 columns">
                                <div class="recruitment_message">
                                    <p>Congratulations for taking your first step to the right path.</p>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section class="single_job">
                        <div class="row">
                            <div class="small-12 columns">
                                <div class="callout small-12 warning" role="alert">
                                    Your application has been successful!
                                </div>
                            </div>
                        </div>
                    </section>


                    <!--END OF SINGLE JOB SECTION -->


                </main>
                <!-- end #main -->


            </div>
            <!-- end #inner-content -->

        </div>
        <!-- end #content -->

        <!-- START OF FOOTER SECTION -->
        <footer>
            <div class="row">
                <div class="small-12 columns">
                    <div class="row">
                        <!-- Footer brand messaging -->
                        <div class="medium-4 columns hide-for-small-only">
                            <h5><img src="https://eaglehr.co.ke/wp-content/themes/eagle-dezari/assets/imgs/logo_white.png" class="footer_logo"></h5>
                        </div>
                        <!-- Footer navigation -->
                        <div class="medium-8 small-12 columns">
                            <div class="row">
                                <div class="medium-4 small-6 columns">
                                    <ul id="menu-footer_nav" class="fnav">
                                        <li id="menu-item-89" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-89"><a href="https://eaglehr.co.ke/about-us/">About Us</a></li>
                                        <li id="menu-item-86" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-86"><a href="https://eaglehr.co.ke/training/">Training</a></li>
                                        <li id="menu-item-87" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-87"><a href="https://eagle.hcm.co.ke/hcmke/recruitment.jsp">Recruitment</a></li>
                                        <li id="menu-item-88" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-88"><a href="https://eaglehr.co.ke/contact-us/">Contact Us</a></li>
                                    </ul>
                                </div>
                                <div class="medium-4 small-6 columns">
                                    <ul class="fnav">
                                        <li><a href="#">Blog</a></li>
                                        <li><a href="#">Press Release</a></li>
                                    </ul>
                                </div>
                                <div class="medium-4 small-12 columns">
                                    <h6>Follow us:</h6>
                                    <ul class="socials">
                                        <li><a href="https://www.linkedin.com/in/eagle-hr-consultants-ltd-bb150416a/" target="_blank"><i class="fa fa-linkedin" aria-hidden="true"></i></a></li>
                                        <li><a href="https://twitter.com/eaglehrkenya" target="_blank"><i class="fa fa-twitter" aria-hidden="true"></i></a></li>
                                        <li><a href="https://www.facebook.com/EAGLE-HR-Consultants-Limited-180768482559263" target="_blank"><i class="fa fa-facebook" aria-hidden="true"></i></a></li>

                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- sub footer -->
                <div class="sub_footer">
                    <div class="row">
                        <div class="small-12 columns">
                            <p>
                                © 2021 EAGLE HR CONSULTANTS.
                            </p>

                        </div>
                    </div>
                </div>
            </div>
        </footer>
        <!-- END OF FOOTER SECTION -->
        <script src="./assets/eagle-hr/assets/jquery.js.download"></script>
        <script src="./assets/eagle-hr/assets/what-input.js.download"></script>
        <script src="./assets/eagle-hr/assets/foundation.js.download"></script>
        <script src="./assets/eagle-hr/assets/app.js.download"></script>

        <!-- ShiftNav Main Toggle -->

        <!-- /#shiftnav-toggle-main -->
        <!-- ShiftNav #shiftnav-main -->
        <!-- /.shiftnav #shiftnav-main -->
        <script src="./assets/global/plugins/jquery.min.js" type="text/javascript"></script>
        <!-- <script src="./assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js" type="text/javascript"></script> -->

    </div>
    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script src="https://dhbhdrzi4tiry.cloudfront.net/cdn/sites/foundation.js"></script>
    <script>
        $(document).foundation();
    </script>

    <script type="text/javascript" src="./assets/eagle-hr/eagleapi.js"></script>
    <script type="text/javascript">
        let queryString = window.location.search;
        let urlParams = new URLSearchParams(queryString);
        let job_id = urlParams.get('link');

        // set job_id input
        $("#job_id").val(job_id);

        // console.log(job_id);
        // eagleApi.init([job_id]);
    </script>
    <script type="text/javascript">
    </script>


    </script>
</body>

</html>
