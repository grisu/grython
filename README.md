Grisu Jython
===========

The *Grisu Jython wrapper* is a Java package that contains both the [Grisu client library](https://github.com/grisu/grisu/wiki/Grisu-client-library) and a version of the [Jython](http://jython.org/) interpreter. Basically, it allows you to access all the convenience (Java) methods and objects of the *Grisu client library* via Python syntax. 

Beware, you can't run this package in a native Python environment. So, if you want to write a python script that relies on 3rd party modules which don't run in Jython (for example because they are written in C), you're (most likely) out of luck.


Prerequisites
---------------------

* Sun Java (version >= 6)
* (recommended) the [NeSI-tols installer package](http://code.ceres.auckland.ac.nz/downloads/nesi/nesi-tools-installer.jar) (make sure you check the '*grython*' option and put <install_dir>/bin in you system path -- in Windows this should be done for you, you might have to logout and login again to see the changes in your %PATH% environment variable)
* or (for advanced users/developers) the Grisu jython jar:
  * [stable](http://code.ceres.auckland.ac.nz/stable-downloads/grisu-jython.jar) or
  * [dev](http://code.ceres.auckland.ac.nz/snapshot-downloads/grisu-jython-dev.jar)


Documentation / Examples
---------------------------

* Javadoc for the Grisu client library whose classes/methods can be used from jython: [javadoc](https://code.ceres.auckland.ac.nz/jenkins/job/Grisu-SNAPSHOT-Javadoc/javadoc/)
* Script examples can be found here: [grython examples](https://github.com/grisu/grisu-jython/tree/master/src/main/jython/examples)
* More examples (but pure Java): [grisu examples](https://github.com/grisu/examples)
* Grisu wiki: https://github.com/grisu/grisu/wiki

Usage
----------

*grisu-jython* is started like so (if you used the NeSI tools installer package and adjusted your path):

    grython [-b <backend>]  [<script-name>.py]
	
or, alternatively:

    java -jar grisu-jython.jar [-b backend] [<script-name>.py]
	
To play around with it, we can use an interactive jython console or run a script (see below)

On your first run (or whenever your session credential expires), you will be asked for login information. For the sake of testing, use those temporary ones:

    username: grisudemo@myproxy.test.nesi.org.nz
	password: grisudemo

Also, we are login into the test backend (alias: testbed):

    grython -b testbed

    [1] Institution login
    [2] Institution login (using: 'The University of Auckland')
    [3] Certificate login
    [4] MyProxy login
    [0] Exit
    Please choose your login type: 4
    Please enter your username [grisudemo]: grisudemo@myproxy.test.nesi.org.nz
    Please enter password: *********
    Jython 2.5.2 (Release_2_5_2:7206, Mar 2 2011, 23:12:06) 
    [OpenJDK 64-Bit Server VM (Sun Microsystems Inc.)] on java1.6.0_24
    Type "help", "copyright", "credits" or "license" for more information.
    >>> 

### Interactive console

#### Getting the session

Login is done whenever *grython* can't find a valid session information (an x509 proxy in $HOME/.grid/grid.proxy) without you having to do anything. To get the object holding the session, you need to import it:

    from grisu.Grython import serviceInterface as si

Now you can get access to information and resources you are allowed to use. Have a look at the javadoc for the _ServiceInterface_ class to get an idea which methods are available: [link](https://code.ceres.auckland.ac.nz/jenkins/job/Grisu-SNAPSHOT-Javadoc/javadoc/grisu/control/ServiceInterface.html). Let's for example get some information about the dn (distinguised name -- your internal username):

	>>> si.getDN()      
	u'DC=nz,DC=org,DC=bestgrid,DC=slcs,O=The University of Auckland,CN=Markus Binsteiner _bK32o4Lh58A3vo9kKBcoKrJ7ZY'

Or, we can get some information about the backend we are connected to:

    >>> si.getInterfaceInfo('VERSION')
    u'0.5.4'
    >>> si.getInterfaceInfo('HOSTNAME')
    u'compute.services.bestgrid.org / 130.216.161.32'

Now, let's do something useful and submit a job:

    >>> from grisu.frontend.model.job import JobObject
	>>> job = JobObject(si)
	>>> job.setJobname("grython_demo_job")
    >>> job.setCommandline("echo \"Hello World\"")
	>>> job.createJob()  
    u'grython_demo_job'
	>>> job.submitJob()
    u'submission_task_grython_demo_job_1353884813463'
	
	# now we wait for the job to finish, which shouldn't take long...
	>>> job.waitForJobToFinish(10)
    True
	
	# and get the output of the job:
    >>> job.getStdOutContent()
    u'Hello World\n'

### Scripting

Of course, the interactive console is not really practical for your day-to-day workflows. You most definitely want to write scripts.  Here's a simple example of how to run a submission workflow using a script:






