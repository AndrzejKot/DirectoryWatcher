var stompClient = null;
var initialRoot = '\\home';

function createNewFile() {
$.ajax({
    url: "/addFile",
    data: "name=" + $("#fileName").val(),
    }).then(function(data) {
        console.log(data);
});
}

function initialize() {
$.ajax({
    url: "/init",
    data: "root=" + initialRoot,
    }).then(function(data) {
    data.forEach(function(entry) {
        $("#dirs").val($("#dirs").val() + entry + "\n");
    });

});
}

function subscribeToTopic() {
    stompClient.subscribe('/topic/broadcast', function (message) {
        $("#dirs").val($("#dirs").val() + message.body + "\n");
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
});