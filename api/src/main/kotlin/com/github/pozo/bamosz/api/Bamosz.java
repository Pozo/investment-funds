package com.github.pozo.bamosz.api;

import com.github.pozo.bamosz.Fund;
import com.github.pozo.bamosz.Rate;

import java.util.Date;
import java.util.List;

public interface Bamosz {

    // funds

    List<Fund> findAllFunds();

    List<Fund> findAllFundsByName(String name);

    List<Fund> findAllFundsByCurrency(String currency);

    // rates
    List<Rate> findAllRatesByISIN(String isin);

    List<Rate> findAllRatesByISINBetween(String isin, Date from, Date to);

    Rate findMostRecentRateByISIN(String isin);

}
