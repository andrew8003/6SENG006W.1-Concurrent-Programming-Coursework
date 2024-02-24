//Concurrent Programming Coursework, Andrew Hanna , W1816963

import java.util.concurrent.TimeUnit;

public class TicketMachine_w1816963 {

    private int PaperCount;
    private int TonerCount;

    // Random number for the paper and toner to be filled to at runtime
    public TicketMachine_w1816963() {
        this.PaperCount = (int) (Math.random() * 10) + 1;
        this.TonerCount = (int) (Math.random() * 10) + 1;
    }

    class Passenger implements Runnable {
        private int ticketsToPrint;

        public Passenger(int ticketsToPrint) {
            this.ticketsToPrint = ticketsToPrint;
        }

        @Override
        public void run() {
            synchronized (TicketMachine_w1816963.this) {
                for (int i = 0; i < ticketsToPrint; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Check if both paper and toner are available
                    while (PaperCount <= 0 || TonerCount <= 0) {
                        // Refill paper and/or toner if empty
                        if (PaperCount <= 0) {
                            System.out.println("Out of paper. Refilling...");
                            PaperCount = 10;
                        }

                        if (TonerCount <= 0) {
                            System.out.println("Out of toner. Refilling...");
                            TonerCount = 10;
                        }

                        // Time taken to refill for realism?
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("Passenger printing ticket " + (i + 1));
                    PaperCount--;
                    TonerCount--;
                }
            }
        }
    }

    class TicketPaperTechnician implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                synchronized (TicketMachine_w1816963.this) {
                    if (PaperCount == 0) {
                        System.out.println("Out of paper. Refilling...");
                        PaperCount = 10;
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Paper Refilled");
                    }
                }
            }
        }
    }

    class TicketTonerTechnician implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                synchronized (TicketMachine_w1816963.this) {
                    if (TonerCount == 0) {
                        System.out.println("Out of toner. Refilling...");
                        TonerCount = 10;
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Toner Refilled");
                    }
                }
            }
        }
    }

    class TickedPrintingSystem {
        public void printTicket() {
            synchronized (TicketMachine_w1816963.this) {
                // Check if there is enough paper and toner for printing
                if (TonerCount >= 1 && PaperCount >= 1) {
                    System.out.println("Printing Ticket");
                } else {
                    // Refill paper and toner if necessary
                    if (PaperCount < 1) {
                        System.out.println("Out of paper. Refilling...");
                        PaperCount = 10; // Refill paper to max capacity
                    }

                    if (TonerCount < 1) {
                        System.out.println("Out of toner. Refilling...");
                        TonerCount = 10; // Refill toner to max capacity
                    }

                    System.out.println("Printing is not possible. Paper and/or toner has been refilled.");
                }
            }
        }
    }

    class PurchaseTicket {
        public void purchaseTicket() {
            java.util.Scanner scanner = new java.util.Scanner(System.in);

            int numTickets;
            do {
                System.out.print("Enter the number of tickets to print (max 3): ");
                numTickets = scanner.nextInt();

                if (numTickets < 1 || numTickets > 3) {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                }

            } while (numTickets < 1 || numTickets > 3);

            Passenger passenger = new Passenger(numTickets);
            Thread passengerThread = new Thread(passenger);

            TicketPaperTechnician paperTechnician = new TicketPaperTechnician();
            Thread paperTechnicianThread = new Thread(paperTechnician);

            TicketTonerTechnician tonerTechnician = new TicketTonerTechnician();
            Thread tonerTechnicianThread = new Thread(tonerTechnician);

            TickedPrintingSystem printingSystem = new TickedPrintingSystem();

            passengerThread.start();
            paperTechnicianThread.start();
            tonerTechnicianThread.start();

            try {
                passengerThread.join();
                paperTechnicianThread.join();
                tonerTechnicianThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            printingSystem.printTicket();
        }
    }

    public static void main(String[] args) {
        TicketMachine_w1816963 ticketMachine = new TicketMachine_w1816963();

        System.out.println("Please Type How Many Tickets You Would Like To Print Below -- ");

        while (true) {
            System.out.println("Current Paper Status: " + ticketMachine.PaperCount);
            System.out.println("Current Toner Status: " + ticketMachine.TonerCount);

            ticketMachine.new PurchaseTicket().purchaseTicket();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
