package com.abc.evpnfree.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class CountriesNames {

    public static Map<String, String> getCountries() {
        Map<String, String> countries = new HashMap<String, String>();

        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {
            Locale locale = new Locale("", country);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry();

            if (!"".equals(iso) && !"".equals(code)
                    && !"".equals(name)) {
                countries.put(code, name);
            }
        }

        return countries;
    }
}
