package org.hisrc.delays.api.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hisrc.delays.api.model.ArrivalAndDeparture;
import org.hisrc.delays.api.model.TrainStatus;
import org.hisrc.delays.api.timetables.Event;
import org.hisrc.delays.api.timetables.ObjectFactory;
import org.hisrc.delays.api.timetables.Timetable;
import org.hisrc.delays.api.timetables.TimetableStop;
import org.hisrc.delays.api.timetables.TripLabel;

public class TrainStatusClient {

	public static final String PLAN_URL_TEMPLATE = "http://iris.noncd.db.de/iris-tts/timetable/plan/${stationEvaNumber}/${date}/${hour}";
	// public static final String PLAN_URL_TEMPLATE =
	// "http://iris.noncd.db.de/iris-tts/timetable/plan/8000105/171215/20";
	public static final String FCHG_URL_TEMPLATE = "http://iris.noncd.db.de/iris-tts/timetable/fchg/${stationEvaNumber}";

	private static final DateTimeFormatter yyMMdd = DateTimeFormatter.ofPattern("yyMMdd");
	private static final DateTimeFormatter yyMMddHHmm = DateTimeFormatter.ofPattern("yyMMddHHmm");

	private JAXBContext context;

	{
		try {
			context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
		} catch (JAXBException jaxbex) {
			throw new ExceptionInInitializerError(jaxbex);
		}
	}
	
	public TrainStatus getTrainStatus(LocalDate date, String trainNumber, String stationEvaNumber) throws IOException {
		
		final TimetableStop changedOrPlannedTimetableStop = findChangedOrPlannedTimetableStop(date, trainNumber, stationEvaNumber);
		if (changedOrPlannedTimetableStop == null) {
			return null;
		}
		else {
			String trainCategory = changedOrPlannedTimetableStop.getTl().getC();
			String stationName = changedOrPlannedTimetableStop.getTl().getO();
			
			final LocalDateTime plannedArrival; 
			final LocalDateTime changedArrival; 
			final Event arrivalEvent = changedOrPlannedTimetableStop.getAr();
			if (arrivalEvent != null) {
				String plannedArrivalTime = arrivalEvent.getPt();
				if (plannedArrivalTime != null) {
					plannedArrival = LocalDateTime.parse(plannedArrivalTime, yyMMddHHmm); 
				}
				else {
					plannedArrival = null;
				}
				String changedArrivalTime = arrivalEvent.getCt();
				if (changedArrivalTime != null) {
					changedArrival = LocalDateTime.parse(changedArrivalTime, yyMMddHHmm); 
				}
				else {
					changedArrival = null;
				}
			}
			else {
				plannedArrival = null;
				changedArrival = null;
			}
			final LocalDateTime plannedDeparture;
			final LocalDateTime changedDeparture;
			final Event departureEvent = changedOrPlannedTimetableStop.getDp();
			if (departureEvent != null) {
				String plannedDepartureTime = departureEvent.getPt();
				if (plannedDepartureTime != null) {
					plannedDeparture = LocalDateTime.parse(plannedDepartureTime, yyMMddHHmm); 
				}
				else {
					plannedDeparture = null;
				}
				String changedDepartureTime = departureEvent.getCt();
				if (changedDepartureTime != null) {
					changedDeparture = LocalDateTime.parse(changedDepartureTime, yyMMddHHmm); 
				}
				else {
					changedDeparture = null;
				}
			}
			else {
				plannedDeparture = null;
				changedDeparture = null;
			}
			
			final ArrivalAndDeparture planned = (plannedArrival == null || plannedDeparture == null) ? null : new ArrivalAndDeparture(plannedArrival, plannedDeparture);			
			final ArrivalAndDeparture changed = (changedArrival == null || changedDeparture == null) ? null : new ArrivalAndDeparture(changedArrival, changedDeparture);			
			
			return new TrainStatus(date, trainCategory, trainNumber, stationName, stationEvaNumber, planned, changed);
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

				System.out.println(System.currentTimeMillis() + " Unmarshalling [" + changedURL + "].");
				@SuppressWarnings("unchecked")
				JAXBElement<Timetable> timetableElement = (JAXBElement<Timetable>) unmarshaller.unmarshal(changedURL);
				System.out.println(System.currentTimeMillis() + " Finished unmarshalling [" + changedURL + "].");
				Timetable value = timetableElement.getValue();
				
				for (TimetableStop timetableStop : value.getS()) {
					if (Objects.equals(plannedTimetableStop.getId(), timetableStop.getId())) {
						timetableStop.setTl(plannedTimetableStop.getTl());
						
						if (timetableStop.getAr() == null) {
							timetableStop.setAr(new Event());
						}
						
						if (timetableStop.getAr().getPt() == null)
						{
							timetableStop.getAr().setPt(plannedTimetableStop.getAr().getPt());
						}
						if (timetableStop.getDp() == null) {
							timetableStop.setDp(new Event());
						}
						
						if (timetableStop.getDp().getPt() == null)
						{
							timetableStop.getDp().setPt(plannedTimetableStop.getDp().getPt());
						}
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
		
		int currentHour = LocalDateTime.now().getHour();
		
		List<Integer> hours = IntStream.concat(IntStream.range(currentHour, 24),
				IntStream.range(0, currentHour)).boxed().collect(Collectors.toList());
		for (int hour : hours) {

			try {
				final String hourString = String.format("%02d", hour);

				URL plannedURL = new URL(PLAN_URL_TEMPLATE.replaceAll("\\$\\{stationEvaNumber\\}", stationEvaNumber)
						.replaceAll("\\$\\{date\\}", dateString).replaceAll("\\$\\{hour\\}", hourString));

				Unmarshaller unmarshaller = context.createUnmarshaller();

				System.out.println(System.currentTimeMillis() + " Unmarshalling [" + plannedURL + "].");
				@SuppressWarnings("unchecked")
				JAXBElement<Timetable> timetableElement = (JAXBElement<Timetable>) unmarshaller.unmarshal(plannedURL);
				System.out.println(System.currentTimeMillis() + " Finished unmarshalling [" + plannedURL + "].");
				Timetable timetable = timetableElement.getValue();
				
				for (TimetableStop timetableStop : timetable.getS()) {
					
					if (timetableStop.getTl() != null &&
							trainNumber.equals(timetableStop.getTl().getN())) {
						if (timetableStop.getAr() == null) {
							timetableStop.setAr(new Event());
						}
						if (timetableStop.getDp() == null) {
							timetableStop.setDp(new Event());
						}
						
						// This is a bloody hack
						timetableStop.getTl().setO(timetable.getStation());
						return timetableStop;
					}
				}
			} catch (JAXBException ignored) {
			}
		}
		return null;
	}

}
