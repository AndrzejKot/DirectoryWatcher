var stompClient = null;
var initialRoot = 'C:\\Users\\ankt\\Desktop\\challenge';

function createNewFile() {
$.ajax({
    url: "/addFile",
    data: "name=" + $("#fileName").val(),
    }).then(function(data) {
        console.log(data);
});
}

function selectFileClick() {
//    console.log('Selected file: ' + $('#fileSelector').get(0).files[0].mozFullPath);
//    initialize();
}

function initialize() {
$.ajax({
    url: "/init",
    data: "root=" + initialRoot,
    }).then(function(data) {
    data.forEach(function(entry) {
        $("#dirs").append(entry + "\n");
    });

});
}

function subscribeToTopic() {
    stompClient.subscribe('/topic/broadcast', function (message) {
        $("#dirs").append(message.body + "\n");
    });
}

function prepareWebsocket() {
    var socket = new SockJS('/watcherWebsocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        subscribeToTopic();
    });
}

$(function () {
    initialize();
    prepareWebsocket();
    $( "#sendFileName" ).click(function() { createNewFile(); });
//    $("#fileSelector").change(function() { selectFileClick(); });
});