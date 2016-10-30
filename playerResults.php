<?php 
  $playerId = $_GET['playerId'];
?>

<html>
  <head>
    <h1>Player Results</h1>
    <script src="resemble/resemble.js"></script>
    
    <script>
        var masterImage, playerImage, diffImage, percentage, id, winnerBanner, playerOneScore, playerTwoScore;
    
        onload = function(){
          function getImages(){
              console.log("GET ID: " + id);
              var stmnt = "SELECT image1, image2 FROM " + id + "Images WHERE userName='temp'";
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
              console.log("RES ID: " + id);
              resemble(masterImage).compareTo(playerImage).onComplete(function(data){
                diffImage = data.getImageDataUrl();
                percentage = data.misMatchPercentage;
                
                return data;
              });
              
              console.log(masterImage);
              console.log(playerImage);
              console.log(diffImage);
              console.log(percentage);
              document.getElementById(id + "diffValue").innerHTML = percentage;
              document.getElementById(id + "diffImage").src = diffImage;
              document.getElementById(id + "image1").src = masterImage;
              document.getElementById(id + "image2").src = playerImage;
          
              
              if(id == "playerOne"){
                playerOneScore = percentage;
              }
              else{
                playerTwoScore = percentage;
              }
              
              if(playerOneScore != null && playerTwoScore != null){
                winnerBanner = document.getElementById("winnerBanner");
          
                if(playerOneScore < playerTwoScore){
                  winnerBanner.innerHTML = "PLAYER ONE WINS!";
                }
                else if(playerOneScore > playerTwoScore){
                  winnerBanner.innerHTML = "PLAYER TWO WINS!";
                }
                else{
                  winnerBanner.innerHTML = "IT'S A TIE!";
                }
              }
              
              
              if(id != "playerTwo"){
                id = "playerTwo";
                getImages(id);
              }
          }
          
          id = "playerOne";
          getImages(id);
          
        }  
    </script>
  </head>
  <body>
    <div id="winnerBanner" name="winnerBanner"></div>
    <div>
      <p>This is the master image.</p>
        <img id="playerOneimage1" name="playerOneimage1">
    </div>
  
    <div>
      <p>This is the player image.</p>
        <img id="playerOneimage2" name="playerOneimage2">
    </div>
  
    <div>
      <p>This is the difference image</p>
        <img id="playerOnediffImage" name="playerOnediffImage">
    </div>
    
    <div>
      <p> Player image is </p><p id="playerOnediffValue"></p><p> different than the master image.</p>
    </div>
        <div>
      <p>This is the master image.</p>
        <img id="playerTwoimage1" name="playerTwoimage1">
    </div>
  
    <div>
      <p>This is the player image.</p>
        <img id="playerTwoimage2" name="playerTwoimage2">
    </div>
    <div>
      <p>This is the difference image</p>
        <img id="playerTwodiffImage" name="playerTwodiffImage">
    </div>
    
    <div>
      <p>The players image is </p><p id="playerTwodiffValue"></p><p> different than the master image.</p>
    </div>
  </body>
</html>