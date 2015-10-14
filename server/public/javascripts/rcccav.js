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
        $('#' + resultDiv).text(data);
      },
      error: function(data) {
        console.log(data);
        $('#' + resultDiv).text(data);
      }
  });

}
