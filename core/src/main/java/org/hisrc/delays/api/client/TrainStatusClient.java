package org.hisrc.delays.api.client;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hisrc.delays.api.timetables.ObjectFactory;
import org.hisrc.delays.api.timetables.Timetable;
import org.hisrc.delays.api.timetables.TimetableStop;

public class TrainStatusClient {

	public static final String PLAN_URL_TEMPLATE = "http://iris.noncd.db.de/iris-tts/timetable/plan/${stationEvaNumber}/${date}/${hour}";
	// public static final String PLAN_URL_TEMPLATE =
	// "http://iris.noncd.db.de/iris-tts/timetable/plan/8000105/171215/20";
	public static final String FCHG_URL_TEMPLATE = "http://iris.noncd.db.de/iris-tts/timetable/fchg/${stationEvaNumber}";

	private static final DateTimeFormatter yyMMdd = DateTimeFormatter.ofPattern("yyMMdd");

	private JAXBContext context;

	{
		try {
			context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
		} catch (JAXBException jaxbex) {
			throw new ExceptionInInitializerError(jaxbex);
		}
	}
	
	public TimetableStop findChangedOrPlannedTimetableStop(LocalDate date, String trainNumber, String stationEvaNumber) throws IOException {
		TimetableStop plannedTimetableStop = findPlannedTimetableStop(date, trainNumber, stationEvaNumber);
		
		if (plannedTimetableStop == null) {
			return null;
		}
		else if (plannedTimetableStop.getId() == null){
			return plannedTimetableStop;
		}
		else {
			try {
				URL changedURL = new URL(FCHG_URL_TEMPLATE.replaceAll("\\$\\{stationEvaNumber\\}", stationEvaNumber));
				Unmarshaller unmarshaller = context.createUnmarshaller();

				@SuppressWarnings("unchecked")
				JAXBElement<Timetable> timetableElement = (JAXBElement<Timetable>) unmarshaller.unmarshal(changedURL);
				Timetable value = timetableElement.getValue();
				
				for (TimetableStop timetableStop : value.getS()) {
					if (Objects.equals(plannedTimetableStop.getId(), timetableStop.getId())) {
						return timetableStop;
					}
				}
			} catch (JAXBException jaxbex) {
				throw new IOException(jaxbex);
			}
			return plannedTimetableStop;
		}
	}

	public TimetableStop findPlannedTimetableStop(LocalDate date, String trainNumber, String stationEvaNumber) throws IOException {
		final String dateString = yyMMdd.format(date);

		for (int hour = 0; hour < 24; hour++) {

			try {
				final String hourString = String.format("%02d", hour);

				URL plannedURL = new URL(PLAN_URL_TEMPLATE.replaceAll("\\$\\{stationEvaNumber\\}", stationEvaNumber)
						.replaceAll("\\$\\{date\\}", dateString).replaceAll("\\$\\{hour\\}", hourString));

				Unmarshaller unmarshaller = context.createUnmarshaller();

				@SuppressWarnings("unchecked")
				JAXBElement<Timetable> timetableElement = (JAXBElement<Timetable>) unmarshaller.unmarshal(plannedURL);
				Timetable value = timetableElement.getValue();
				
				for (TimetableStop timetableStop : value.getS()) {
					
					if (timetableStop.getTl() != null &&
							trainNumber.equals(timetableStop.getTl().getN())) {
						return timetableStop;
					}
				}
			} catch (JAXBException ignored) {
			}
		}
		return null;
	}

}
