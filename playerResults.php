<?php 
  $playerId = $_GET['playerId'];
?>

<html>
  <head>
    <h1>Player Results</h1>
    <script src="resemble/resemble.js"></script>
    
    <script>
        var masterImage, playerImage, diffImage, percentage;
    
        onload = function(){
          function getImages(){
              var stmnt = "SELECT image1, image2 FROM <?php echo $playerId; ?>Images WHERE userName='temp'";
              var url = "https://web.njit.edu/~edm8/cs491/database.php";
              var params = "code=get&stmnt=" + stmnt;
              
              var http = new XMLHttpRequest();
              http.open("POST", url, true);
              http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  
              http.onreadystatechange = function(){
                if(http.readyState == 4 && http.status == 200){
                  obj = JSON.parse(http.responseText);
                  masterImage = obj.image1;
                  playerImage = obj.image2;
                  getResembleValues();
                  //console.log(http.responseText);
                  
                }
              }
  
              http.send(params);
          }
          
          function getResembleValues(){
              resemble(masterImage).compareTo(playerImage).onComplete(function(data){
                diffImage = data.getImageDataUrl();
                percentage = data.misMatchPercentage;
                
                return data;
              });
              
              console.log(masterImage);
              console.log(playerImage);
              console.log(diffImage);
              console.log(percentage);
              document.getElementById('diffValue').innerHTML = percentage;
              document.getElementById('diffImage').src = diffImage;
              document.getElementById('image1').src = masterImage;
              document.getElementById('image2').src = playerImage;
          }
          
          getImages();
        }  
    </script>
  </head>
  <body>
    <div>
      <p>This is the master image.</p>
        <img id="image1" name="image1">
    </div>
  
    <div>
      <p>This is the player image.</p>
        <img id="image2" name="image2">
    </div>
  
    <div>
      <p>This is the difference image</p>
        <img id="diffImage" name="diffImage">
    </div>
    
    <div>
      <p>The players image is </p><p id="diffValue"></p><p> different than the master image.</p>
    </div>
</html>