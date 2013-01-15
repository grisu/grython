#!/usr/bin/grython


'''
Beast workflow script

Created on 04/01/2013

@author: markus
'''

from grisu.Grython import serviceInterface as si
from grisu.frontend.model.job import JobObject
from grisu.jcommons.utils import WalltimeUtils
from grisu.model import FileManager, GrisuRegistryManager
from optparse import OptionParser
import os
import sys


megabeast_path = '/share/apps/ultrabeast/ultrabeast'
#remote_home_dir = '/home/mbin029'
remote_home_dir = os.getenv("HOME")

        
uem = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager()
fm = GrisuRegistryManager.getDefault(si).getFileManager()

def parse_commandline_arguments(args):

    # parsing commandline input

    parser = OptionParser()
    parser.add_option("-f", "--file", dest="filepath", action="store",
                      help="beast input xml file", metavar="FILE")
    parser.add_option("-w", "--walltime", dest="walltime", action="store",
                      help="walltime of the job (format like: '1d20h30m'")
    parser.add_option("-c", "--cpus", type='int',
                      action="store", dest="cpus", default=1,
                      help="number of cpus to use")
    parser.add_option("-r", "--runs", type='int',
                      action="store", dest="runs", default=1,
                      help="number of times to submit the job")
    parser.add_option("-n", "--jobname",
                      action="store", dest="jobname",
                      help="the jobname of this batch of jobs")
    parser.add_option("--memory",
                      action="store", dest="memory", default='2g',
                      help="how much memory to request for this job (format: 10g200m30k")
    parser.add_option("--action",
                      action="store", dest="action",
                      help="whether to submit, monitor or download the results of a job run")
    
    
    (options, args) = parser.parse_args()
    
    if not options.action:
        parser.print_help()
        parser.error("No action provided")
    else:
        action = options.action 
        
    if not options.jobname:
        parser.print_help()
        parser.error("No jobname provided")
                
    if 'submit' == action:
        
        if not options.walltime:
            parser.print_help()
            parser.error('No walltime provided.')
    
        if not options.filepath:
            parser.print_help()
            parser.error('No input file provided.')
    
        if not os.path.exists(options.filepath):
            parser.print_help()
            parser.error("Can't find file: "+options.filepath)
            
    elif 'monitor' == action:
        pass
    
    else:
        parser.print_help()
        parser.error("Action '"+action+"' not valid")
               
    return options


class BeastRun:
    
    def __init__(self, options):
        
        self.options = options
        self.action = options.action
        self.cpus = options.cpus
        if options.walltime:
            self.walltime = WalltimeUtils.fromShortStringToSeconds(options.walltime)
        self.jobname = options.jobname
        self.filepath = options.filepath
        if self.filepath:
            self.filename = FileManager.getFilename(self.filepath)
        self.runs = options.runs
        self.memory = options.memory
        
        self.refresh_jobs()

    # get all current jobs that start with the specified jobname
    def refresh_jobs(self):
        
        self.jobs = []
        for jobname in uem.getCurrentJobnames(True):
            if self.jobname+'_run_' in jobname:
                self.jobs.append(jobname)
            
    def get_jobs(self):
        
        all_jobs = []
        for jobname in self.jobs:
            job = JobObject(si, jobname);
            all_jobs.append(job)
            
        return all_jobs
    
    def monitor(self):

        if not self.jobs:
            print "No job that is part of run '"+self.jobname+"' found. Exiting..."
            sys.exit(1)
            
        status = {}
    
        for job in self.get_jobs():
            
            s = job.getStatusString(False)
            try: 
                current = status[s]
            except:
                current = 0

            status[s] = current+1
            
        for k in status.keys():
            print k + ': '+str(status[k])
            
    
    def submit(self):
        
        if self.jobs:
            print 'There is already a run with the name "'+self.jobname+'". Exiting...'
            sys.exit(1)
            
        # uploading input file once, so we don't need to do it for every job again and again
        fm.cp(self.filepath, 'gsiftp://pan.nesi.org.nz/~/inputfiles/'+self.jobname, True)
        
        for i in range(0,self.runs):
            
            # create the job object
            job = JobObject(si);
            # set a unique jobname
            number = str(i+1).zfill(4)
            job.setUniqueJobname(self.jobname+"_run_"+number)
            # set the commandline that needs to be executed
            job.setCommandline(megabeast_path+' '+remote_home_dir+'/inputfiles/'+self.jobname+'/'+self.filename)
            job.setSubmissionLocation('pan:pan.nesi.org.nz')
            
            job.setCpus(self.cpus)

            job.setWalltime(self.walltime)
            
            job.setMemory(self.memory)
            
            job.setApplication('UltraBEAST')
            job.setApplicationVersion('0.1')
            
            #job.addInputFileUrl(self.filepath)
            
            # create the job on the backend and specify the VO to use
            temp_jobname = job.createJob("/nz/nesi")
            print "created job: '"+temp_jobname+"', submitting..."
            # submit the job
            job.submitJob()
            print "submission finished: " + temp_jobname
            
            # this waits until the job is finished. Checks every 10 seconds (which would be too often for a real job)
            #job.waitForJobToFinish(10)
            
            # download and cache the jobs' stdout and display it's content
            #print "Stdout: " + job.getStdOutContent()
            # download and cache the jobs' stderr and display it's content
            #print "Stderr: " + job.getStdErrContent()
            # kill and clean the job on the backend
            #job.kill(True)


if __name__ == '__main__':
    
    options = parse_commandline_arguments(sys.argv[1:])
    
    if "submit" == options.action:
        BeastRun(options).submit()
        
    elif "monitor" == options.action:
        BeastRun(options).monitor()
        
    elif "download" == options.action:
        BeastRun(options).download()
    
    else:
        print "Action '"+options.action+"' not recognised."
        sys.exit(1)
        
        
    # don't forget to exit properly. this cleans up possible existing threads/executors
    sys.exit()
