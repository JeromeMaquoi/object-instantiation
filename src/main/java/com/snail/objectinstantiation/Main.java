package com.snail.objectinstantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            log.error("No argument provided. Exiting the application");
            System.exit(1);
        }

        String path = args[0];



    }
}