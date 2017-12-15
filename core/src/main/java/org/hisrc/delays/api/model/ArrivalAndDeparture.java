package org.hisrc.delays.api.model;

import java.time.LocalDate;
import java.util.Objects;

public class ArrivalAndDeparture {

	private final LocalDate arrival;
	private final LocalDate departure;

	public ArrivalAndDeparture(LocalDate arrival, LocalDate departure) {
		this.arrival = arrival;
		this.departure = departure;
	}

	public LocalDate getArrival() {
		return arrival;
	}

	public LocalDate getDeparture() {
		return departure;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arrival, departure);
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
		ArrivalAndDeparture that = (ArrivalAndDeparture) object;
		return Objects.equals(this.arrival, that.arrival) && Objects.equals(this.departure, that.departure);
	}

	@Override
	public String toString() {
		return "[arrival=" + arrival + ", departure=" + departure + "]";
	}
}
