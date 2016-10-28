<?php 
  $image1 = $_GET['image1'];
  $image2 = $_GET['image2'];
  
  echo $image1;
?>

<html>
  <head>
  <h1>Processing...</h1>
    <script src="resemble/resemble.js"></script>
    <script>
    
      var image, percentage;
      var image1 = "<?php echo $image1; ?>";
      var image2 = "<?php echo $image2; ?>";
      
      onload= function(){
        resemble(image1).compareTo(image2).onComplete(function(data){
          image = data.getImageDataUrl();
          percentage = data.misMatchPercentage;
          
          return data;
        });
        
        console.log(image1);
        console.log(image2);
        console.log(image);
        console.log(percentage);
        document.getElementById('diffValue').value = percentage;
        document.getElementById('diffImage').value = encodeURIComponent(image);
        document.getElementById('image1').value = encodeURIComponent(image1);
        document.getElementById('image2').value = encodeURIComponent(image2);
        document.getElementById('myform').submit();
      }
    </script>
  </head>
  <body>
    <form method="post" action="playerResults.php" id="myform">
      <input type="text" id="diffValue" name="diffValue" hidden>
      <input type="text" id="diffImage" name="diffImage" hidden>
      <input type="text" id="image1" name="image1" hidden>
      <input type="text" id="image2" name="image2" hidden>
    </form>
</html>