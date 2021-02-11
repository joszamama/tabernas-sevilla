$(document).ready(function() {
    $('#reg-key').focusout(
        function(event) {
            var key = $('#reg-key').val();         
            var data = '?key='
                    + encodeURIComponent(key);
            $.ajax({
                url : '/register/check-key'+data,
                type : "GET",
 
                success : function(response) {
                	if(response=='true'){
                		$('#reg-key-msg').text('Welcome to the crew!');
                	}else{
                		$('#reg-key-msg').text('Invalid key!');
                	}
					
                },
                error : function(xhr, status, error) {
                    $('#reg-key-msg').text('Error checking key');
                }
            });
            return false;
        });
    });

function showMe (box) {

    var chbox = document.getElementById("show-emp");
	var vis="none";
    if(chbox.checked){
        vis = "block";
    }
    document.getElementById(box).style.display = vis; 
}



