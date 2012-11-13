function genHelp() {
    var el = $("#dialog_new");    
    var loc = window.location;
    var url = "http://" + getUrl(loc) + "public/help/help.txt";
    $.ajax({
        type: "get",
        url: url,
        success: function(data){
            var d = el.dialog({
                width: 800,
                height: 600,
                resizable:false,
                draggable:false
            });
            el.html(data);
        }
    });
}