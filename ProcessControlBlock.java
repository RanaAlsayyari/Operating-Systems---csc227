class ProcessControlBlock {
    public String processID;
    public int priority;
    public int arrivalTime;
    public int cpuBurst;
    public int remainingBurst;
    public int startTime;
    public int terminationTime;
    public int turnaroundTime;
    public int waitingTime;
    public int responseTime;
    public static int processCount = 1;

    public ProcessControlBlock( int priority, int arrivalTime, int cpuBurst) {
        this.processID = "P" + (ProcessControlBlock.processCount++);
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.cpuBurst = cpuBurst;
        this.remainingBurst=cpuBurst;
        this.startTime = -1; // Initialize to -1 indicating not yet started
        this.terminationTime = -1; // Initialize to -1 indicating not yet terminated
    }

   
  
    // Calculate turnaround time
    public double calculateTurnaroundTime() {
        turnaroundTime= this.terminationTime - this.arrivalTime;
        return turnaroundTime;
    }

    // Calculate waiting time
    public double calculateWaitingTime() {
        waitingTime=this.turnaroundTime - this.cpuBurst;
        return waitingTime;
    }

    // Calculate response time
    public double calculateResponseTime() {
         responseTime=this.startTime - this.arrivalTime;
         return responseTime;
    }

    


    
    @Override
    public String toString() {
        return "Process ID: " + processID +
                "\nPriority: " + priority +
                "\nArrival Time: " + arrivalTime +
                "\nCPU Burst: " + cpuBurst +
                "\nStart Time: " + startTime +
                "\nTermination Time: " + terminationTime +
                "\nTurnaround Time: " + turnaroundTime +
                "\nWaiting Time: " + waitingTime +
                "\nResponse Time: " + responseTime;
    }
}