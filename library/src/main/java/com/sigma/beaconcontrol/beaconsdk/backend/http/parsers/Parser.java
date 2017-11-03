package com.sigma.beaconcontrol.beaconsdk.backend.http.parsers;

import java.util.List;
import java.util.Map;

/**
 * Created by Wilson on 7/13/17.
 */

public interface Parser {
    Parser parse(String responseString, int statusCode, Map<String, List<String>> headers);
}
