This is the Video and Audio control system software project for
Raleigh Chinese Christian Church

To start the server go to server directory and run the following command::

    ./activator run -Dhttp.address=0.0.0.0 -Dhttp.port=80

The above command will start the server on any nic and at port 80. This
command will only be suitable for development.


For distribution for any platform, run the following command:

    ./activator dist

The above command will produce a distribution of the application in the
following directory:

    target/universal/rcccav-1.0.zip

This file can be downloaded, then unzip. To run the service in the production
mode, simply run bin/rcccav from command line.

    bin/rcccav

The above command will start up the AV system at port 80. You can also choose
to run the command with -d option to run it in debug mode like this:

    bin/rcccav -d

For production:

To build a Ubuntu native package so that it can be installed as a Ubuntu
service, run the following command:

    ./activate debian:packageBin

The above command will create a debian package named rcccav_1.0_all.deb in
target directory. Then you can simply run the following command to install
it to a brand new machine after you download that file.

    sudo dpkg -i rcccav_1.0_all.deb

To remove the service, run the following command:

    sudo apt-get purge --auto-remove rcccav

To start/stop the service simply run the following command

    sudo service rcccav start or stop

The service has now been configured to run at port 80 by default


For development:

Run the following command to build:

    ./activate clean stage

Go to target/universal/stage directory, then run the following command:

    bin/rcccav -d

The above command will run rcccav project in debug mode and produce log
files in stage/logs directory.

Prerequisite:

This application requires Sun JDK 8, alsa-utils and lame. Following the
procedures below to install these prerequisites:

Sun JDK 8:

    sudo add-apt-repository ppa:webupd8team/java
    sudo apt-get update
    sudo apt-get install oracle-java8-installer

alsa-utils and lame:

    sudo apt-get install alsa-utils lame
