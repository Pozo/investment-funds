package com.github.pozo.investmentfunds.grabber;

import org.apache.camel.main.Main;

/**
 * Main class that boot the Camel application
 */
public final class InvestmentFundsApplication {

    private InvestmentFundsApplication() { }

    public static void main(String[] args) throws Exception {
        // use Camels Main class
        Main main = new Main(InvestmentFundsApplication.class);
        // now keep the application running until the JVM is terminated (ctrl + c or sigterm)
        main.run(args);
    }

}
