<?php 
  $servername = "sql.njit.edu";
  $username = "edm8";
  $password = "p1Z0TgN10";
  $db = "edm8";

  $code = $_POST['code'];
  $stmnt = $_POST['stmnt'];
  
  //creates connection to mysql
  $conn = new mysqli($servername, $username, $password, $db);

  //checks connection
  if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
  } 

  /***** Inserts image *****/
  if($code == "insert"){
    $sql = $stmnt;
    $result = $conn->query($sql);
    echo $stmnt;
  }
  /***** Retrieves Player One image *****/
  else if($code == "get"){
    $sql = $stmnt;
            
    $result = $conn->query($sql);        
            
    if ($result->num_rows > 0) {
    // output data of each row
      while($row = $result->fetch_assoc()){
        $dataURL["image1"] = $row["image1"];
        $dataURL["image2"] = $row["image2"];
      }
    }
    
    //echo print_r($dataURL);
    echo json_encode($dataURL);
  }
  
  $conn->close();
?>