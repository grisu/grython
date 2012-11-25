'''
Example on how to display information about packages available via the grid.

Created on 11/12/2009

@author: markus
'''

# this next one gets you the serviceinterface created duringn startup, so you don't have to worry about it
from grisu.Grython import serviceInterface as si
from grisu.model import GrisuRegistryManager
import sys

sys.argv.pop(0)
registry = GrisuRegistryManager.getDefault(si)

if sys.argv[0] == "--list":
    
    apps = registry.getResourceInformation().getAllApplications()
    
    for app in apps:
        print app.getName()
        
    sys.exit(0)

for app in sys.argv:

    info = registry.getApplicationInformation(app)
    print 'Application: '+app

    info = registry.getApplicationInformation(app)

    for queue in info.getAvailableAllSubmissionLocations():

        subLoc = queue.toString()

        print("\tSubmissionLocation: "+subLoc)
        print("\tVersions: ")
        for version in info.getAvailableVersions(subLoc):

            print "\t\t" + version.toString()

        print

    print
    print




