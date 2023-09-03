

<?php

$client = new SoapClient("http://umis.babcock.edu.ng:8080/babcock/webservice?wsdl");

echo "API Test";

var_dump($client->__getFunctions()); 
var_dump($client->__getTypes()); 
?>
