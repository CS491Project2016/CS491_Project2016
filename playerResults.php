<?php 
  $masterImage = $_POST['image1'];
  $userImage = $_POST['image2'];
  $diffImage = $_POST['diffImage'];
  $diffValue = $_POST['diffValue'];
?>

<html>
  <head>
    <h1>Results for player</h1>
  </head>
  <body>
    <div>
      <p>This is the master image.</p>
      <?php echo "<img src=" . $masterImage . ">"; ?>
    </div>
  
    <div>
      <p>This is the player image.</p>
      <?php echo "<img src=" . $userImage . ">"; ?>
    </div>
  
    <div>
      <p>This is the difference image</p>
      <?php echo "<img src=" . $diffImage . ">"; ?>
    </div>
    
    <div>
      <?php echo "<p><b>Your image was " . $diffValue . " different than the master image</b></p>"; ?>
    </div>
</html>