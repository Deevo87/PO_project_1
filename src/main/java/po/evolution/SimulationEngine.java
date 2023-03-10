package po.evolution;

import gui.SimulationController;

public class SimulationEngine implements Runnable {
    private Thread t;
    private boolean suspended;
    private AbstractWorldMap map;
    private boolean running = true;
    private boolean exportStats = false;
    private String statsFileName = "";

    private SimulationController app;

    private  int refresh;

    public SimulationEngine(AbstractWorldMap map, SimulationController app) {
        this.map = map;
        this.app = app;
        t = new Thread(this);
        suspended = false;
        t.start();
    }

    public SimulationEngine(AbstractWorldMap map, SimulationController app , String statsFileName) {
        this.map = map;
        this.app = app;
        this.exportStats = true;
        this.statsFileName = statsFileName;
        t = new Thread(this);
        suspended = false;
        map.stats.prepareAndCreateCSV(statsFileName);
        t.start();
    }

    public Thread getThread() {
        return t;
    }

    private void waitForResume() {
        synchronized(this) {
            try {
                while (suspended) wait();
            } catch (InterruptedException e) {
                System.out.println("Execution thread was interrupted " + e.getMessage());
                System.exit(1);
            }
        }
    }

    public void pauseExecutution() {
        suspended = true;
    }

    public synchronized void resumeExecution() {
        notify();
        suspended = false;
    }

    public void terminate() {
        running = false;
    }

    public void run() {

        while (running) {
            if (map.animals.size() == 0) {
                map.calculateFree();
                app.refreshMap();
                return;
            }
            if (suspended) waitForResume();
            map.clearDead();

            for (Animal a : map.animals) {

                a.move();
                map.calculateFree();
            }

            for (Vector2d pos : map.occupiedPositions) {
                map.feast(pos);
            }

            for (Vector2d pos : map.occupiedPositions) {
                map.procreate(pos);
            }

            map.spawnPlants(map.params.plantsPerDay);
            map.calculateFree();
            app.refreshMap();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (exportStats) {
                map.stats.writeToCSV(statsFileName);
            }

        }

    }
}
