<?php

// Opening the count file
$file = fopen('count.txt', 'r+');

// Getting a one line String (being the count) and casting it to an integer
$count = (int) fgets($file);

// Adding 1 to the count and updating the file if the "increment" parameter is there
if (isset($_GET['increment'])) {
  $count++;
  fseek($file, 0);
  fputs($file, $count);
}

// Closing file
fclose($file);

//Sending the count as a result
echo $count;

