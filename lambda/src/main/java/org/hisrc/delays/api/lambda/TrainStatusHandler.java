package org.hisrc.delays.api.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hisrc.delays.api.client.TrainStatusClient;
import org.hisrc.delays.api.model.ArrivalAndDeparture;
import org.hisrc.delays.api.model.TrainStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class TrainStatusHandler implements RequestStreamHandler {

	private JSONParser parser = new JSONParser();

	private TrainStatusClient trainStatusClient = new TrainStatusClient();

	@Override
	public void handleRequest(InputStream is, OutputStream os, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler.");

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		JSONObject responseJson = new JSONObject();
		try {
			logger.log("Parsing the input.");
			JSONObject event = (JSONObject) parser.parse(reader);

			JSONObject pathParameters = (JSONObject) event.get("pathParameters");

			int year = Integer.parseInt((String) pathParameters.get("year"));
			int month = Integer.parseInt((String) pathParameters.get("month"));
			int day = Integer.parseInt((String) pathParameters.get("day"));

			LocalDate date = LocalDate.of(year, month, day);

			String trainNumber = (String) pathParameters.get("trainNumber");
			String stationEvaNumber = (String) pathParameters.get("stationEvaNumber");

			TrainStatus trainStatus = trainStatusClient.getTrainStatus(date, trainNumber, stationEvaNumber);

			if (trainStatus == null) {
				responseJson.put("statusCode", "404");
			} else {
				final JSONObject body = new JSONObject();
				body.put("date", toJSONObject(date));
				body.put("trainCategory", trainStatus.getTrainCategory());
				body.put("trainNumber", trainStatus.getTrainNumber());
				body.put("stationEvaNumber", trainStatus.getEva());
				body.put("stationName", trainStatus.getStationName());

				if (trainStatus.getPlanned() != null) {
					body.put("planned", toJSONObject(trainStatus.getPlanned()));
				}
				if (trainStatus.getChanged() != null) {
					body.put("changed", toJSONObject(trainStatus.getChanged()));
				}
				responseJson.put("body", body.toJSONString());
			}

			JSONObject headers = new JSONObject();
			headers.put("Content-Type", "application/json");
			responseJson.put("headers", headers);
			responseJson.put("statusCode", "200");
			
			logger.log("Result:\n" + responseJson.toJSONString());
		} catch (Exception ex) {
			responseJson.put("statusCode", "400");
			responseJson.put("exception", ex);
		}
		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
		writer.write(responseJson.toJSONString());
		writer.close();
	}

	private JSONObject toJSONObject(ArrivalAndDeparture arrivalAndDeparture) {
		final JSONObject result = new JSONObject();
		if (arrivalAndDeparture.getArrival() != null) {
			result.put("arrival", toJSONObject(arrivalAndDeparture.getArrival()));
		}
		if (arrivalAndDeparture.getDeparture() != null) {
			result.put("departure", toJSONObject(arrivalAndDeparture.getDeparture()));
		}
		return result;
	}

	private JSONArray toJSONObject(LocalDate date) {
		final JSONArray result = new JSONArray();
		result.add(date.getYear());
		result.add(date.getMonthValue());
		result.add(date.getDayOfMonth());
		return result;
	}

	private JSONArray toJSONObject(LocalDateTime date) {
		final JSONArray result = new JSONArray();
		result.add(date.getYear());
		result.add(date.getMonthValue());
		result.add(date.getDayOfMonth());
		result.add(date.getHour());
		result.add(date.getMinute());
		return result;
	}
}