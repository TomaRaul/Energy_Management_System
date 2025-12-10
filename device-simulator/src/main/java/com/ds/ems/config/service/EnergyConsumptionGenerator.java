package com.ds.ems.config.service;

import java.time.LocalTime;
import java.util.Random;

public class EnergyConsumptionGenerator {
    private final Random random;
    private double baseLoad;
    private double currentLoad;

    public EnergyConsumptionGenerator() {
        this.random = new Random();
        // Base load intre 1.0 și 3.0 kWh pentru interval de 10 minute
        this.baseLoad = 1.0 + random.nextDouble() * 2.0;
        this.currentLoad = baseLoad;
    }

    /**
     Genereaza valorile din 10 in 10 minute
     Variatia cosnum in functie de interval
     (00:00-06:00): consum mic (50-70% din baseLoad)
     (06:00-09:00): consum crescător
     (09:00-18:00): consum mediu-ridicat
     (18:00-23:00): consum maxim (prim time)
     (23:00-00:00): consum descrescător
     */
    public double generateConsumption() {
        LocalTime currentTime = LocalTime.now();
        int hour = currentTime.getHour();

        double timeMultiplier;

        if (hour >= 0 && hour < 6) {
            timeMultiplier = 0.5 + random.nextDouble() * 0.2;
        } else if (hour >= 6 && hour < 9) {
            timeMultiplier = 0.7 + (hour - 6) * 0.1 + random.nextDouble() * 0.1;
        } else if (hour >= 9 && hour < 18) {
            timeMultiplier = 0.8 + random.nextDouble() * 0.3;
        } else if (hour >= 18 && hour < 23) {
            timeMultiplier = 1.1 + random.nextDouble() * 0.3;
        } else {
            timeMultiplier = 0.7 + random.nextDouble() * 0.2;
        }

        // variatie aleatorie pentru realism
        double smallVariation = (random.nextDouble() - 0.5) * 0.2;

        // Consum curent
        currentLoad = baseLoad * timeMultiplier + smallVariation;

        // verifica valoare reala(rezonabila)
        if (currentLoad < 0.1) {
            currentLoad = 0.1;
        }
        if (currentLoad > 10.0) {
            currentLoad = 10.0;
        }

        // 2 zecimale
        return Math.round(currentLoad * 100.0) / 100.0;
    }

    public void adjustBaseLoad() {
        // ajusteaza baseLoad pentru variație pe termen lung
        double adjustment = (random.nextDouble() - 0.5) * 0.3;
        baseLoad += adjustment;
        if (baseLoad < 1.0) baseLoad = 1.0;
        if (baseLoad > 4.0) baseLoad = 4.0;
    }
}
