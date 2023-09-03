var newRow = false;
var newRowCount = 0;

var dateEditor = function(cell, onRendered, success, cancel){
	//cell - the cell component for the editable cell
	//onRendered - function to call when the editor has been rendered
	//success - function to call to pass the successfuly updated value to Tabulator
	//cancel - function to call to abort the edit and return to a normal cell

	//create and style input
	var cellValue = moment(cell.getValue(), "DD/MM/YYYY").format("YYYY-MM-DD");
	input = document.createElement("input");

	input.setAttribute("type", "date");

	input.style.padding = "4px";
	input.style.width = "100%";
	input.style.boxSizing = "border-box";

	input.value = cellValue;

	onRendered(function(){
	    input.focus();
	    input.style.height = "100%";
	});

	function onChange(){
	    if(input.value != cellValue){
	        //success(moment(input.value, "YYYY-MM-DD").format("DD/MM/YYYY"));
			success(input.value);
	    }else{
	        cancel();
	    }
	}

	//submit new value on blur or change
	input.addEventListener("blur", onChange);

	//submit new value on enter
	input.addEventListener("keydown", function(e){
	    if(e.keyCode == 13){
	        onChange();
	    }

	    if(e.keyCode == 27){
	        cancel();
	    }
	});

	return input;
};

var timeEditor = function(cell, onRendered, success, cancel){
	//cell - the cell component for the editable cell
	//onRendered - function to call when the editor has been rendered
	//success - function to call to pass the successfuly updated value to Tabulator
	//cancel - function to call to abort the edit and return to a normal cell

	//create and style input
	var cellValue = cell.getValue();
console.log(cellValue);
	input = document.createElement("input");

	input.setAttribute("type", "time");
	input.setAttribute("step", "600");

	input.style.padding = "4px";
	input.style.width = "100%";
	input.style.boxSizing = "border-box";

	input.value = cellValue;

	onRendered(function(){
	    input.focus();
	    input.style.height = "100%";
	});

	function onChange(){
	    if(input.value != cellValue){
	        success(input.value);
	    }else{
	        cancel();
	    }
	}

	//submit new value on blur or change
	input.addEventListener("blur", onChange);

	//submit new value on enter
	input.addEventListener("keydown", function(e){
	    if(e.keyCode == 13){
	        onChange();
	    }

	    if(e.keyCode == 27){
	        cancel();
	    }
	});

	return input;
};

var menuTitleFormatter = function(cell, formatterParams, onRendered){
	if(newRow === true) {
		return "<i class='fa fa-stop' style='color:blue;'></i>";
	} else {
		return "<i class='fa fa-plus' style='color:blue;'></i>";
	}
}

var cellFnctIcon = function(cell, formatterParams, onRendered){ 
	var cellIcon = "<i class='fa fa-trash' style='color:blue;'></i>";
	if(newRow === true) cellIcon = "<i class='fa fa-floppy-o' style='color:blue;'></i>";
	return cellIcon;
};

function headerClick(e, column, row_default) {
	if(newRow === false) {
		newRow = true;

		let rDefault = JSON.parse(JSON.stringify(row_default));

		newRowCount++;
		rDefault.id = newRowCount;
		column.getTable().addRow(rDefault);
	} else {
		var myTable = column.getTable();
		var rowCount = myTable.getRows().length;

		myTable.deleteRow(newRowCount)
		.then(function(){ newRow = false; })
		.catch(function(error){
			console.log('Not deleted');
		});
	}
}

function controlCellClick(e, cell, viewno) {
	console.log("BASE 2030");

	if(newRow === true) {
		var rowData = JSON.stringify(cell.getData());
		$.post('ajax?fnct=jsinsert', {viewno:viewno, jsrowdata:rowData}, function(mData) {

			if(mData.error == true) {
				toastr['error'](mData.error_msg, "Error");
				cell.getRow().delete();
		    } else if(mData.error == false) {
				cell.getRow().update(mData);
			}
			
			newRow = false;
			cell.getRow().reformat();
		});
	} else {
		var dc = confirm("Are you sure you want to delete?");
		if (dc == true) {
			var myData = cell.getRow().getData();
			$.post('ajax?fnct=jsdelete', {viewno:viewno, keyfield:myData.keyfield}, function(mData) {

		        if(mData.error == true) {
		            toastr['error'](mData.error_msg, "Error");
		        } else if(mData.error == false) {
					cell.getRow().delete();
		        }
			});
		}
	}
}

function editCell(cell, viewno) {
	console.log("BASE 4030 :  edit cell");
	var currVal = cell.getValue();
	var oldVal = cell.getOldValue();
	var myRow = cell.getRow();
	var myData = myRow.getData();

	if(newRow === false) {
		if(currVal !== oldVal) {
			$.post('ajax?fnct=jsfieldupdate', {viewno:viewno, fieldname:cell.getField(), fieldvalue:currVal, keyfield:myData.keyfield}, function(mData) {
console.log(mData);
		        if(mData.error == true) {
		            toastr['error'](mData.error_msg, "Error");
		        }
				cell.getRow().update(mData);
			});
		} 
	}
}

function editRow(data) {
	console.log("BASE 3030");
	console.log(data);
}

function updatePrice(cell) {
	console.log("BASE 5020");
	if(newRow === true) {
		var currVal = cell.getValue();
		var myData = {amount:0};
		if (db_e1_item_id.hasOwnProperty(currVal)) { 
			myData.amount = db_e1_item_id[currVal];
		}

		if(myData.amount > 0) {
			cell.getRow().update(myData);
		}
	}
}

function updatePosPrice(cell) {
	console.log("BASE 5020");
	if(newRow === true) {
		var currVal = cell.getValue();
		var myData = {item_price:0};
		if (db_e1_item_id.hasOwnProperty(currVal)) { 
			myData.item_price = db_e1_item_id[currVal];
		}

		if(myData.item_price > 0) {
			cell.getRow().update(myData);
		}
	}
}

function addPOSItem(val) {
	
	if(newRow === false) {
		newRow = true;

		let rDefault = JSON.parse(JSON.stringify(db_1_default));

		newRowCount++;
		rDefault.id = newRowCount;
		rDefault.item_id = val;
		rDefault.item_price = db_e1_item_id[val];
		
		tablo1.addRow(rDefault);
	}
	
}

function addTransItem(val) {
	
	if(newRow === false) {
		newRow = true;

		let rDefault = JSON.parse(JSON.stringify(db_1_default));

		newRowCount++;
		rDefault.id = newRowCount;
		rDefault.item_id = val;
		rDefault.amount = db_e1_item_id[val];
		
		tablo1.addRow(rDefault);
	}
	
}






