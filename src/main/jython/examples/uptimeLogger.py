'''
Created on 28/11/2012

@author: markus
'''

from grisu.Grython import serviceInterface as si
from grisu.model import GrisuRegistryManager
from time import strftime, localtime
import sys
import time

fm = GrisuRegistryManager.getDefault(si).getFileManager()

waittime = 30


while True:
    
    try:
        
        listing = fm.ls('gsiftp://pan.nesi.org.nz/home/mbin029/ll', True)
        if (listing):
#            for f in listing.getChildren():
#                print f.getName()
            print "Working: "+strftime("%Y-%m-%d %H:%M:%S", localtime(None))
                
        else:
            
            print 'ERROR: no filelist'
        
    except:
        eType, value, traceback = sys.exc_info()
        print 'ERROR: '+strftime("%Y-%m-%d %H:%M:%S", localtime())+"  ("+value.getLocalizedMessage()+")"
        
    time.sleep(waittime)
    
sys.exit(0)