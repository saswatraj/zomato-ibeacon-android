<?php
$bid = $_GET['bid'];
//$device = $_GET['device'];
$hostname = "localhost";
$host_database = "zomato";
$user = "root";
$password = "zomato";
$conn = new mysqli($hostname, $user, $password,$host_database);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
$sql = "SELECT * FROM `beacontable` WHERE bid='$bid'";
$result = $conn->query($sql);
if($result->num_rows>0){
	while($row = $result->fetch_assoc()){
		//echo $row['bid'];
		echo $row['weblink'].'_'.$row['coupontext'];

	}
}else{
	$r="INSERT INTO beacontable (bid) VALUES ($bid)";
	$res1=mysqli_query($conn,$r);
	
    if (!$res1) {
    die('Something really bad happened. Please try again!' . mysql_error());
}
}
?>