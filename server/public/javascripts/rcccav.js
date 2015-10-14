if (window.console) {
  console.log("Welcome to RCCCAV's JavaScript!");
}

function turnSystem(resultDiv, cmd) {

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
    Outputs:
        1 - Kramer 1:3 RGBHV Distributor which connects Group1 projectors via RGBHV
        2 - Connects to group2 project via VGA over CAT6
        3 - Connects to VGA to HDMI scaler as input to RF Modulator Channel #1
        4 - Connects to VGA to HDMI scaler as input to RF Modulator Channel #2
    */
    var INPUTS = {mac: '1', podium: '2', camcorder: '3'};
    var OUTPUTS = {G1: 'A', G2: 'B', G3: 'C', G4: 'D'}
    var action = 'S' + INPUTS[source] + '_' + OUTPUTS[group];
    console.log(action);
  $.ajax({
      url: '/rcccav/video/vga_matrix_switch/' + action,
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
