$(function() {

 
$("#inputPassword").keyup(function(event){
if(event.keyCode == 13){
$("#loginButton").click();
}
})
 
$("#loginButton").click(function(e){

	$.ajax({type: "POST",
    url: $('#url').val(),
    data: 'username='+$("#userId").val()+'&password='+$("#inputPassword").val(),
    success:function(data,textStatus,jqXHR ){
    	window.location.href="/content/etap/en.html";
        alert("hi "+$("#userId").val());
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {

    	alert("Invalid User Name or Password");

    }}); 

})


})
