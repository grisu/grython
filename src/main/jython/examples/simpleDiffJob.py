#! /usr/bin/grython -b testbed
'''

A simple job submission to show how to submit a job with input files.

Created on 26/11/2012

@author: Markus Binsteiner
'''

from grisu.Grython import serviceInterface as si
from grisu.frontend.model.job import JobObject
from grisu.model import FileManager
import sys


print 'Parsing commandline arguments...'
file1url = sys.argv[1]
file1Name = FileManager.getFilename(file1url)
file2url = sys.argv[2]
file2Name = FileManager.getFilename(file2url);


print 'Creating job...'
# create the job object
job = JobObject(si);
# set a unique jobname
job.setTimestampJobname("diff_job")
print 'Set jobname to: '+ job.getJobname()

# set the commandline that needs to be executed
job.setCommandline('diff ' + file1Name+ ' ' + file2Name)

job.addInputFileUrl(file1url);
job.addInputFileUrl(file2url);

# create the job on the backend
job.createJob()
print 'Submitting job...'
# submit the job
job.submitJob()

print 'Waiting for the job to finish...'
# this waits until the job is finished. Checks every 10 seconds (which would be too often for a real job)
job.waitForJobToFinish(10)

print 'Job finished. Status: '+job.getStatusString(False)
# download and cache the jobs' stdout and display it's content
print "Stdout: " + job.getStdOutContent()
# download and cache the jobs' stderr and display it's content
print "Stderr: " + job.getStdErrContent()
# kill and clean the job on the backend
job.kill(True)

# don't forget to exit properly. this cleans up possible existing threads/executors
sys.exit()