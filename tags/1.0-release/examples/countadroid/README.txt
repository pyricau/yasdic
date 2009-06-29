This application is an example to show how to use Yasdic in an Android application.

It is a simple counter apps, that works with both a local and a remote counter, depending on the counter you select.

To build the application and/or edit it with Eclipse, please read Building_with_maven.txt

To get the remote counter working, you need to follow the following instructions:

# Install apache2 and mod_php:
sudo apt-get install apache2 apache2-doc php5 libapache2-mod-php5

# Add a symbolic link to countadroid on /var/www (so that count.php may be executed):
sudo ln -s [PATH_TO_YASDIC_SVN]/examples/countadroid/etc/web /var/www/countadroid

# If needed, check that the user www-data has right to read and write the file count.txt

# You can go in your browser to http://localhost/countadroid/count.php?increment to check that the remote counter works

# You can also update the url in the ContainerHolder class (in the defineBeans method).
# Please note that 10.0.2.2 is a virtual IP to represent the localhost on your computer.



