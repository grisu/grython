'''
Created on 25/01/2013

@author: markus
'''

from grisu.jcommons.utils import WalltimeUtils
from grith.jgrith.cred import MyProxyCred, ProxyCred, AbstractCred
from grith.jgrith.cred.callbacks import CliCallback
import sys

threshold_string = sys.argv[2]
threshold = WalltimeUtils.fromShortStringToSeconds(threshold_string)

cred = None
try:
    cred = MyProxyCred.loadFromDefault()
except:
    try:
        cred = ProxyCred()
    except:
        pass
        
create_new_cred = False

if not cred:
    
    print "No credential found, we have to create a new one..."
    create_new_cred = True

else:
    lifetime = cred.getRemainingLifetime()
    lifetime_string = WalltimeUtils.convertSeconds(lifetime)

    if lifetime < threshold:
        print "Credential found, but not enough time left: "+lifetime_string+" ( "+str(lifetime)+" sec )"
        create_new_cred = True
    else:
        print "Credential found, enough time left: "+lifetime_string+" ( "+str(lifetime)+" sec )"

if create_new_cred:
    
    try:
        cred = AbstractCred.loadFromConfig(None, CliCallback());
    except:
        instance = sys.exc_info()[1]
        print "Could not create credential: " + instance.getLocalizedMessage()
        sys.exit(1)
        
    lifetime = cred.getRemainingLifetime()
    
    if lifetime < threshold:
        print "Credential created, but lifetime "+str(lifetime)+" not longer than threshold ( "+str(threshold)+" sec )"
        sys.exit(1)
    
    lifetime_string = WalltimeUtils.convertSeconds(lifetime)
    print "Credential created, lifetime: "+lifetime_string+" ( "+str(lifetime)+" sec )"
    sys.exit(0)
    
else:
    sys.exit(0)
