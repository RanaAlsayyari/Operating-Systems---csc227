import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;

public class driver {

    public static ArrayList<ProcessControlBlock> Q1 = new ArrayList<>();
    public static ArrayList<ProcessControlBlock> Q2 = new ArrayList<>();
    public static List<ProcessControlBlock> schedulingOrder = new ArrayList<>();
    public static List<ProcessControlBlock> doneProcesses = new ArrayList<>();
    public static int timer=0;

    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("Menu:");
            System.out.println("1. Enter process' information");
            System.out.println("2. Report detailed information about each process and different scheduling criteria");
            System.out.println("3. Exit the program");
            System.out.print("Enter your choice: ");
            choice = input.nextInt();
            switch (choice) {
                case 1:
                    processInfo();
                    break;
                case 2:
                    //scheduleProcesses();

                    reportInformation();

                    break;
                case 3:
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a valid option.");
            }
        } while (choice != 3);

    }

    private static void processInfo() {
        System.out.print("Enter the number of processes: ");
        int numProcesses = input.nextInt();

        for (int i = 1; i <= numProcesses; i++) {
            System.out.println("\n Enter details for process P" + i + ":");
            System.out.print("Priority (1 or 2): ");
            int priority = input.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = input.nextInt();
            System.out.print("CPU Burst Time: ");
            int cpuBurst = input.nextInt();

            ProcessControlBlock pcb = new ProcessControlBlock(priority, arrivalTime, cpuBurst);
            //System.out.println(pcb.toString());

            // Add the process to the appropriate queue
            if (priority == 1)
                Q1.add(pcb);
            else
                Q2.add(pcb);
        }

    }

    private static void reportInformation() {



        scheduleProcesses();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Report.txt"))) {
            // Write scheduling order to file and display in console

            System.out.print("Scheduling order of processes: ");
            writer.write("Scheduling order of processes: ");
            for (ProcessControlBlock process : schedulingOrder) {
                System.out.print(process.processID + " | ");
                writer.write(process.processID + " | ");
            }
            System.out.println();
            System.out.println();

            writer.newLine();

            for (ProcessControlBlock process : doneProcesses) {
                System.out.println("Process ID: " + process.processID);
                System.out.println("Priority: " + process.priority);
                System.out.println("Arrival time: " + process.arrivalTime);
                System.out.println("CPU burst time: " + process.cpuBurst);
                System.out.println("Start time: " + process.startTime);
                System.out.println("Termination time: " + process.terminationTime);
                System.out.println("Turnaround time: " + process.calculateTurnaroundTime());
                System.out.println("Waiting time: " + process.calculateWaitingTime());
                System.out.println("Response time: " + process.calculateResponseTime());
                System.out.println();

                writer.write("Process ID: " + process.processID);
                writer.newLine();
                writer.write("Priority: " + process.priority);
                writer.newLine();
                writer.write("Arrival time: " + process.arrivalTime);
                writer.newLine();
                writer.write("CPU burst time: " + process.cpuBurst);
                writer.newLine();
                writer.write("Start time: " + process.startTime);
                writer.newLine();
                writer.write("Termination time: " + process.terminationTime);
                writer.newLine();
                writer.write("Turnaround time: " + process.turnaroundTime);
                writer.newLine();
                writer.write("Waiting time: " + process.waitingTime);
                writer.newLine();
                writer.write("Response time: " + process.responseTime);
                writer.newLine();
                writer.newLine();
            }

            // Calculate and display average turnaround time, waiting time, and response
            // time
            int totalTurnaroundTime = 0;
            int totalWaitingTime = 0;
            int totalResponseTime = 0;
            for (ProcessControlBlock process : doneProcesses) {
                totalTurnaroundTime += process.turnaroundTime;
                totalWaitingTime += process.waitingTime;
                totalResponseTime += process.responseTime;
            }
            double avgTurnaroundTime = (double) totalTurnaroundTime / doneProcesses.size();
            double avgWaitingTime = (double) totalWaitingTime / doneProcesses.size();
            double avgResponseTime = (double) totalResponseTime / doneProcesses.size();

            System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
            System.out.println("Average Waiting Time: " + avgWaitingTime);
            System.out.println("Average Response Time: " + avgResponseTime);

            writer.write("Average Turnaround Time: " + avgTurnaroundTime);
            writer.newLine();
            writer.write("Average Waiting Time: " + avgWaitingTime);
            writer.newLine();
            writer.write("Average Response Time: " + avgResponseTime);
            writer.newLine();

        } // end witing
        catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }

    }

    private static void scheduleProcesses(){
        if (Q1.isEmpty() && Q2.isEmpty()) {
            System.out.println("No processes to schedule.");
            return;
        }
        schedulingOrder.clear();
        timer=0;
        int quantum=3;
        sortProcesses(Q1); //so the queues are sorted based on arrival time (easier to check)
        sortProcesses(Q2);
        ProcessControlBlock currentP;
        ProcessControlBlock sjfCurrent= new ProcessControlBlock( 0, -1, 0); //Just initilizing so i can check if i have a preempted process or not

        while(!Q1.isEmpty() || !Q2.isEmpty() || sjfCurrent.remainingBurst!=0) { //while i have processes
            if(!Q1.isEmpty() && Q1.get(0).arrivalTime<=timer){ //round robin schedueler
                currentP=Q1.remove(0);
                schedulingOrder.add(currentP);
                if (currentP.startTime<0) {  //to check if this is the first time the process has been picked up
                    currentP.startTime=timer;
                }

                for(int i=0; i<quantum && currentP.remainingBurst>0; i++){  //dec bursttime 
                currentP.remainingBurst--;
                timer++;}

                if(currentP.remainingBurst==0){ //check if process finished or put it back in queue
                    currentP.terminationTime=timer;
                    doneProcesses.add(currentP);
                }

                else{
                    findPlace(currentP);}
                }

            else if((!Q2.isEmpty() && Q2.get(0).arrivalTime<=timer) || sjfCurrent.remainingBurst>0 ){  //check if i have q2 processes
                int j=0;
                if (sjfCurrent.remainingBurst<=0){ // check if i have preempted
                if(Q2.get(0).arrivalTime<=timer){ //sjf schedueler (find shortest)
                    ProcessControlBlock shortestP=Q2.get(0);
                    for (int i = 1; i < Q2.size() && Q2.get(i).arrivalTime<=timer ; i++) { 
                        if (Q2.get(i).cpuBurst < shortestP.cpuBurst) { 
                            shortestP = Q2.get(i); 
                            j=i;
                        } 
                    }}
                    sjfCurrent= Q2.remove(j);
                }


                    schedulingOrder.add(sjfCurrent);


                    if (sjfCurrent.startTime<0) {  //to check if this is the first time the process has been picked up before or first time (starttime)
                        sjfCurrent.startTime=timer;
                    }

                    while (( Q1.isEmpty() || Q1.get(0).arrivalTime>timer) && sjfCurrent.remainingBurst>0 ) { //while i have nothing in q1 and the process still has burst
                        sjfCurrent.remainingBurst--;
                        timer++;
                    }
                    if (sjfCurrent.remainingBurst==0) { //process done
                        sjfCurrent.terminationTime=timer;
                        doneProcesses.add(sjfCurrent);

                    }






            }

            else{ //no processes arrived so just inc timer
                timer++;
            }

        }

    }

    

    public static void sortProcesses(ArrayList<ProcessControlBlock> Processes) {
        if (Processes.isEmpty()) {
            return;    
        }
        Collections.sort(Processes, new Comparator<ProcessControlBlock>() {
            @Override
            public int compare(ProcessControlBlock p1, ProcessControlBlock p2) {
                return p1.arrivalTime - p2.arrivalTime;
            }
        });
    }

    public static void findPlace(ProcessControlBlock p){
    boolean added = false;

    for (int i = 0; i < Q1.size(); i++) {
        if (Q1.get(i).arrivalTime > timer) {
            Q1.add(i, p);
            added = true;
            break;
        }
    }

    if (!added) {
        Q1.add(p);
    }
    return;
}

}// end class

