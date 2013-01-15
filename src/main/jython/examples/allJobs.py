'''
Example on how to query all jobs

Created on 18/12/2012

@author: markus
'''

# this next one gets you the serviceinterface created duringn startup, so you don't have to worry about it
from grisu.Grython import serviceInterface as si
from grisu.model import GrisuRegistryManager
import sys
from grisu.control import JobConstants

registry = GrisuRegistryManager.getDefault(si)

uem = registry.getUserEnvironmentManager()

jobs = uem.getCurrentJobs(True)

for job in jobs:
    
    print job.jobname() + " : " + str(job.getStatus())
    # or, if you want string status
    print job.jobname() + " : " + JobConstants.translateStatus(job.getStatus())

sys.exit(0)


