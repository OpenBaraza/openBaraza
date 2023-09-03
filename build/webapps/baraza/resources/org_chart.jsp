<html>
  <head>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script src="../assets/global/plugins/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript">
      google.charts.load('current', {packages:["orgchart"]});
      google.charts.setOnLoadCallback(drawChart);

      function drawChart() {
      	
      	var data = new google.visualization.DataTable();
        data.addColumn('string', 'Name');
        data.addColumn('string', 'Manager');
      
      	$.ajax({
			method: "GET",
			url: "../org_chart?tag=org_chart",
			dataType: "json",
			
			success: function (rData) {
				console.log("SUCCESS : ", rData);
				
				if(rData.org_data) {
					var org_data = rData.org_data;
					console.log(org_data);
					
					// For each orgchart box, provide the name, manager, and tooltip to show.
					data.addRows(org_data);

					// Create the chart.
					var chart = new google.visualization.OrgChart(document.getElementById('chart_div'));
					// Draw the chart, setting the allowHtml option to true for the tooltips.
					chart.draw(data, {'allowHtml':true});   

				} else {
					console.log("Data error", "error");
				}
			},
			error: function (err) {
				console.log("ERROR : ", err);
			}
		});
      
      }
   </script>
    </head>
  <body>
    <div id="chart_div"></div>
  </body>
</html>

