<?php
$profile = $_GET['user'];
$device = $_GET['device'];
$hostname = "localhost";
$host_database = "zomato";
$user = "root";
$password = "zomato";
$conn = new mysqli($hostname, $user, $password,$host_database);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
$sql = "SELECT * FROM `table1` WHERE pid='$profile' AND bid='$device'";
$result = $conn->query($sql);
if($result->num_rows>0){
	while($row = $result->fetch_assoc()){
		//echo $row["pid"].' '.$row['bid'].' '.$row['timestamp'];
		//$deltatime= strtotime($row['timestamp'])-strtotime(time());
		$deltatime=time() - $row['timestamp'];
		if($row['count'] !=0)echo "FALSE";
		//else if($deltatime/60  > 10 && $row['timestamp'] ==0)
		else {echo "TRUE";
		$r="UPDATE  table1 SET count = count+1 WHERE bid='$device' AND pid='$profile' ";
		$res1=mysqli_query($conn,$r);	
	//	$row['count']=$row['count']+1;
	}


	}
}else{
	$r="INSERT INTO table1 (pid, bid) VALUES ('$profile','$device')";
	//echo "TRUE";
	//echo $r;
	echo "TRUE";
	$res1=mysqli_query($conn,$r);
    if (!$res1) {
    die('Something really bad happened. Please try again!' . mysql_error());
}
}
?>