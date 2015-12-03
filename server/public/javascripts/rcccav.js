if (window.console) {
  console.log("Welcome to RCCCAV's JavaScript!");
}

function turnSystem(resultDiv, cmd) {

  var ret = confirm("Are you sure you want to turn entire system " + cmd + "?");
  if (ret != true) {
    return;
  }

  $.ajax({
      url: '/rcccav/system/' + cmd.toUpperCase(),
      type: 'get',
      headers: {},
      success: function( data ) {
        console.log(data);
        $('#' + resultDiv).html(data);
      },
      error: function(data) {
        console.log(data);
        $('#' + resultDiv).html(data);
      }
  });

}

function switchInput(resultDiv, group, source) {

    /*
    This function actually switch an input to a group of outlet
    Currently the projector and rccc cctv are grouped like this:

    Group1 - Front right, left and back projectors
    Group2 - Front center project
    Group3 - RCCC CCTV Channel #1
    Group4 - RCCC CCTV Channel #2
    Here is the layout of VGA Switch:
    Inputs: 
        1 - mac
        2 - podium
        3 - camcorder
        4 - laptop
    Outputs:
        1 - Kramer 1:3 RGBHV Distributor which connects Group1 projectors via RGBHV
        2 - Connects to group2 project via VGA over CAT6
        3 - Connects to VGA to HDMI scaler as input to RF Modulator Channel #1
        4 - Connects to VGA to HDMI scaler as input to RF Modulator Channel #2
    */
    var INPUTS = {mac: '1', podium: '2', camcorder: '3', laptop: '4'};
    var OUTPUTS = {G1: 'A', G2: 'B', G3: 'C', G4: 'D'}
    var action = 'S' + INPUTS[source] + '_' + OUTPUTS[group];
    console.log(action);
    $.ajax({
      url: '/rcccav/video/vga_matrix_switch/' + action,
      type: 'get',
      headers: {},
      success: function( data ) {
        console.log(data);
        //$('#' + resultDiv).html(data);
        //setStatus(group + '_', group + '_' + INPUTS[source]);
        updateStatus();
      },
      error: function(data) {
        console.log(data);
        $('#' + resultDiv).html(data);
      }
    });

}

function doCommand(resultDiv) {
    var projector = $('input:radio[name=projectors]:checked').val();
    var action = $('input:radio[name=projector_actions]:checked').val();
    console.log(projector);
    console.log(action);

    //Now ready to send the command.
    $.ajax({
      url: '/rcccav/video/' + projector + '/' + action.toUpperCase(),
      type: 'get',
      headers: {},
      success: function( data ) {
        console.log(data);
        $('#' + resultDiv).html(data);
      },
      error: function(data) {
        console.log(data);
        $('#' + resultDiv).html(data);
      }
    });
}

function doComboCommand(resultDiv, group_prefix, onId) {
    var ACTIONS = {'G11_1': 'FREEZE_ON', 'G11_2': 'FREEZE_OFF',
                   'G21_1': 'FREEZE_ON', 'G21_2': 'FREEZE_OFF'};
    //Now ready to send the command.
    var action_name = ACTIONS[group_prefix + onId];
    $.ajax({
      url: '/rcccav/combo/' + group_prefix + '/' + action_name,
      type: 'get',
      headers: {},
      success: function( data ) {
        console.log(data);
        $('#' + resultDiv).html(data);
        setStatus(group_prefix, group_prefix + onId);
      },
      error: function(data) {
        console.log(data);
        $('#' + resultDiv).html(data);
      }
    });
}

function doRecording(resultDiv, action) {

    //Now ready to send the command.
    $.ajax({
      url: '/rcccav/video/recorder/' + action.toUpperCase(),
      type: 'get',
      headers: {},
      success: function( data ) {
        console.log(data);
        $('#' + resultDiv).html(data);
        if (action == 'START') {
            setStatus('G5_', 'G5_1');
        }
        else {
            setStatus('G5_', 'G5_2');
        }
      },
      error: function(data) {
        console.log(data);
        $('#' + resultDiv).html(data);
      }
    });
}

var ON_COLOR = 'red';
var OFF_COLOR = '#f2f2f2';
var GROUP_MAX = {G1_:4, G11_:2, G2_:4, G21_:2, G3_:4, G4_:4, G5_:2};

function setStatus(group_prefix, onId) {
    var buttonId = "";
    var group_max = GROUP_MAX[group_prefix];
    for (i = 1; i <= group_max; i++) {
        buttonId = group_prefix + i;
        if (buttonId != onId)
            $('#' + buttonId).css('background-color', OFF_COLOR);
        else
            $('#' + onId).css('background-color', ON_COLOR);
    }
}

function updateStatus() {
    $.ajax({
      url: '/rcccav/status',
      type: 'get',
      headers: {},
      success: function( data ) {
        console.log(data);
        $('#results').html(data);
      },
      error: function(data) {
        console.log(data);
        $('#results').html(data);
      }
    });
}
