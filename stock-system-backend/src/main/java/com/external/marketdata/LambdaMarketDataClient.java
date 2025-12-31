package com.external.marketdata;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Profile("prod")
public class LambdaMarketDataClient implements MarketDataClient {

    @Value("${lambda.functionName}")
    private String functionName;

    private final AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();

    @Override
    public Map<String, Double> getPrices(String symbols) {
        try {
            String payload = "{\"symbols\":\"" + symbols + "\"}";

            InvokeRequest request = new InvokeRequest()
                    .withFunctionName(functionName)
                    .withPayload(payload);

            InvokeResult result = awsLambda.invoke(request);
            String json = new String(result.getPayload().array());

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(json, new TypeReference<Map<String, Double>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Lambda", e);
        }
    }
}