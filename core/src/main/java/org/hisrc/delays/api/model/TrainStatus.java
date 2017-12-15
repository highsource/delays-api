package org.hisrc.delays.api.model;

import java.time.LocalDate;
import java.util.Objects;

public class TrainStatus {

	private final LocalDate date;
	private final String trainCategory;
	private final String trainNumber;
	private final String stationName;
	private final String eva;
	private final ArrivalAndDeparture planned;
	private final ArrivalAndDeparture changed;

	public TrainStatus(LocalDate date, String trainCategory, String trainNumber, String stationName, String eva,
			ArrivalAndDeparture planned, ArrivalAndDeparture changed) {
		this.date = date;
		this.trainCategory = trainCategory;
		this.trainNumber = trainNumber;
		this.stationName = stationName;
		this.eva = eva;
		this.planned = planned;
		this.changed = changed;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getTrainCategory() {
		return trainCategory;
	}

	public String getTrainNumber() {
		return trainNumber;
	}

	public String getStationName() {
		return stationName;
	}

	public String getEva() {
		return eva;
	}

	public ArrivalAndDeparture getPlanned() {
		return planned;
	}

	public ArrivalAndDeparture getChanged() {
		return changed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, trainCategory, trainNumber, stationName, eva, planned, changed);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		TrainStatus that = (TrainStatus) object;
		return Objects.equals(this.date, that.date) && Objects.equals(this.trainCategory, that.trainCategory)
				&& Objects.equals(this.trainNumber, that.trainNumber)
				&& Objects.equals(this.stationName, that.stationName) && Objects.equals(this.eva, that.eva)
				&& Objects.equals(this.planned, that.planned) && Objects.equals(this.changed, that.changed);
	}

}