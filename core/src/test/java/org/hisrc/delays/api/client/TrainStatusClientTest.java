package org.hisrc.delays.api.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;

public class TrainStatusClientTest {
	
	private final TrainStatusClient sut = new TrainStatusClient();
	
	@Test
	public void testICE592() throws IOException {
		assertThat(sut.findChangedOrPlannedTimetableStop(LocalDate.of(2017, 12, 15), "592", "8000105").getAr().getCt()).isNotNull();
	}

}
