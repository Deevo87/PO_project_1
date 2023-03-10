package po.evolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import com.opencsv.CSVWriter;

public class Statistics {
    private int animalsDied = 0, animals = 0, plants = 0, free = 0;
    private double averageLifetime = 0.0, averageEnergy = 0.0;
    private List<Integer> genotype;
    private int[] genotypeArray = new int[0];

    public Statistics(int animals, int free, double averageEnergy) {
        this.animals = animals;
        this.free = free;
        this.averageEnergy = averageEnergy;
    }

    public void prepareAndCreateCSV(String filename) {
        try {
            File f = new File("src/main/resources/statistics/" + filename);
            System.out.println(f.createNewFile());
            FileWriter writer = new FileWriter(f);

            CSVWriter csvWriter = new CSVWriter(writer);
            String[] headers = {
                "Animals Alive",
                "Plants Present",
                "Free Fields",
                "Dominant Genotype",
                "Average Energy",
                "Average Lifetime"
            };

            csvWriter.writeNext(headers);
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void writeToCSV(String filename) {
        try {
            File f = new File("src/main/resources/statistics/" + filename);
            FileWriter writer = new FileWriter(f, true);

            CSVWriter csvWriter = new CSVWriter(writer);
            String[] statistics = {
                String.valueOf(animals),
                String.valueOf(plants),
                String.valueOf(free),
                Arrays.toString(genotypeArray),
                String.valueOf(averageEnergy),
                String.valueOf(averageLifetime)
            };

            csvWriter.writeNext(statistics);
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // setters
    void onMove() {
        double totalEnergy = (double) animals * averageEnergy;
        totalEnergy -= 1.0;
        averageEnergy = totalEnergy / (double) animals;
    }

    void onProcreation() {
        double totalEnergy = (double) animals * averageEnergy;
        averageEnergy = totalEnergy / (double) ++animals;
    }

    void onAnimalDeath(int newLifetime) {
        double totalEnergy = (double) animals * averageEnergy;
        averageEnergy = totalEnergy / (double) --animals;

        double daysLived = (double) animalsDied * averageLifetime;

        averageLifetime = (daysLived + (double) newLifetime) / ((double) ++animalsDied);
    }

    void onPlantSpawn(int newPlantsNum) { plants = newPlantsNum; }
    void onPlantEaten(int energy) {
        --plants;

        double totalEnergy = (double) animals * averageEnergy;
        totalEnergy += (double) energy;
        averageEnergy = totalEnergy / animals;

    }

    void acquireDominantGenotype(List<Integer> genotype) {
        this.genotype = genotype;
        genotypeArray = genotype.stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }

    void updateFree(int newFree) { free = newFree; }

    // getters
    public int getAnimalNum() { return animals; }
    public int getPlantNum() { return plants; }
    public int getFree() { return free; }

    public int[] getDominantGenotype() { return genotypeArray; }

    public double getAverageLifetime() { return averageLifetime; }
    public double getAverageEnergy() { return averageEnergy; }
}
