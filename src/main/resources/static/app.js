var stompClient = null;
var pathsSubscription = null;
var endSessionSubscription = null;

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
    url: "/init"
    }).then(function(data) {
    data.forEach(function(entry) {
        $("#dirs").val($("#dirs").val() + entry + "\n");
    });
});
}

function disableUI() {
    $("#root").prop("disabled", true);
    $("#fileName").prop("disabled", true);
    $("#sendFileName").prop("disabled", true);
    $("#dirs").prop("disabled", true);
}

function unsubscribeWebsocket() {
    pathsSubscription.unsubscribe();
    endSessionSubscription.unsubscribe();
}

function showPopup() {
    var r = confirm("Your session has expired! Press Ok to reload page.");
    if (r == true) {
        location.reload();
    } else {
        console.log('Unsubscribing!');
        unsubscribeWebsocket();
    }
}

function subscribeToEndSessionTopic() {
    endSessionSubscription = stompClient.subscribe('/topic/endSession', function (message) {
        console.log('EndSessionTopic: ' + message.body);
        disableUI();
        showPopup();
    });
}

function subscribeToPathsTopic() {
    pathsSubscription = stompClient.subscribe('/topic/paths', function (message) {
        $("#dirs").val($("#dirs").val() + message.body + "\n");
    });
}

function prepareWebsocket() {
    var socket = new SockJS('/watcherWebsocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        subscribeToPathsTopic();
        subscribeToEndSessionTopic();
    });
}

$(function () {
    initialize();
    prepareWebsocket();
    $( "#sendFileName" ).click(function() { createNewFile(); });

    window.onbeforeunload = function () {
        unsubscribeWebsocket();
    };
});