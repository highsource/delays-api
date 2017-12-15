package org.hisrc.delays.api.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;

import org.hisrc.delays.api.model.TrainStatus;
import org.junit.Test;

public class TrainStatusClientTest {
	
	private final TrainStatusClient sut = new TrainStatusClient();
	
	@Test
	public void testICE592() throws IOException {
		TrainStatus trainStatus = sut.getTrainStatus(LocalDate.of(2017, 12, 15), "592", "8000105");
		assertThat(trainStatus.getDate()).isEqualTo(LocalDate.of(2017, 12, 15));
		assertThat(trainStatus.getTrainCategory()).isEqualTo("ICE");
		assertThat(trainStatus.getTrainNumber()).isEqualTo("592");
		assertThat(trainStatus.getStationName()).isEqualTo("Frankfurt(Main)Hbf");
		assertThat(trainStatus.getEva()).isEqualTo("8000105");
		assertThat(trainStatus.getPlanned()).isNotNull();
		assertThat(trainStatus.getPlanned().getArrival()).isNotNull();
		assertThat(trainStatus.getPlanned().getDeparture()).isNotNull();
		assertThat(trainStatus.getChanged()).isNotNull();
		assertThat(trainStatus.getChanged().getArrival()).isNotNull();
		assertThat(trainStatus.getChanged().getDeparture()).isNotNull();
		
	}

}
