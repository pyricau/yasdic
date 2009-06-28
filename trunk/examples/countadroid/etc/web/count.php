<?php
$file = fopen('count.txt', 'r+');
$count = (int) fgets($file);

if (isset($_GET['increment'])) {
  $count++;
  fseek($file, 0);
  fputs($file, $count);
}

fclose($file);

echo $count;

