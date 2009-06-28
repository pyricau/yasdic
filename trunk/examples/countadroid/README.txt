Add countadroid on /var/www (you need apache2 and mod_php) => cf LAMP tutorials

sudo ln -s [PATH_TO_YASDIC_SVN]/examples/countadroid/etc/web /var/www/countadroid

(you can also update the url in ContainerHolder class. Please note that 10.0.2.2 is a virtual IP to represent the localhost on your computer).

See Building_with_maven.txt for how to build the project.


You can go in your browser to http://localhost/countadroid/count.php?increment to check that the remote counter works
