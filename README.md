This is the Video and Audio control system software project for
Raleigh Chinese Christian Church

To start the server go to server directory and run the following command::

    ./activator run -Dhttp.address=0.0.0.0 -Dhttp.port=80

The above command will start the server on any nic and at port 80. This
command will only be suitable for development.


To produce a distribution, run the following command:

    ./activator dist

The above command will produce a distribution of the application in the
following directory:

    target/universal/rcccav-1.0-SNAPSHOT.zip
    
This file can be downloaded, then unzip. To run the service in the production
mode, simply run bin/rcccav from command line.

    bin/rcccav -Dhttp.address=0.0.0.0 -Dhttp.port=80