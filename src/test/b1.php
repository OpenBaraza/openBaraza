<?php
$params["arg0"] = getStudentSemester("2016/2017.1");
$params["arg1"] = "test123";

$client = new SoapClient("http://umis.babcock.edu.ng/babcock/webservice?wsdl");
try{
	if ($client->_soap_version == 1){
		//echo "version 1";
		$params = array($params);
	}
	$response = $client->__soapCall('getWsData',$params);
	print_r($response);

	
} catch(SoapFault $exception) {
	echo 'ERROR ::: ' . $exception->getMessage();
} catch(Exception $ex) {
	
	echo 'PHP ERROR ::: ' . $ex->getMessage();
	
}

function getStudentSemester($quarterId) {	
		$xml = "<QUERY>\n";
		$xml .= "<GRID name=\"Semester Student List\" keyfield=\"qstudentid\" table=\"ws_qstudents\" where=\"quarterid = '" . $quarterId . "'\">\n";
		$xml .= "	<TEXTFIELD>quarterid</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>studentid</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>studentname</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>quarterid</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>schoolid</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>departmentid</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>studylevel</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>majorid</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>majorname</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>residenceid</TEXTFIELD>\n";
		$xml .= "	<TEXTFIELD>finaceapproval</TEXTFIELD>\n";
		$xml .= "</GRID>\n";
		$xml .= "</QUERY>\n";
		
		return $xml;
	}

?>
