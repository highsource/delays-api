package org.hisrc.delays.api.timetables;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

public class UnmarshallerTest {

	public static final JAXBContext CONTEXT;
	static {
		try {
			CONTEXT = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
		} catch (JAXBException jaxbex) {
			throw new ExceptionInInitializerError(jaxbex);
		}
	}

	@Test
	public void unmarshallsFchg() throws JAXBException, IOException {
		Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();
		try (InputStream is = getClass().getResourceAsStream("/fchg-8000105.xml")) {
			JAXBElement<Timetable> timetableElement = unmarshaller.unmarshal(new StreamSource(is), Timetable.class);
			Timetable timetable = timetableElement.getValue();
			assertThat(timetable.getEva()).isEqualTo("8000105");
		}
	}

	@Test
	public void unmarshallsPlan() throws JAXBException, IOException {
		Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();
		try (InputStream is = getClass().getResourceAsStream("/plan-8000105-17121519.xml")) {
			JAXBElement<Timetable> timetableElement = unmarshaller.unmarshal(new StreamSource(is), Timetable.class);
			Timetable timetable = timetableElement.getValue();
			assertThat(timetable.getStation()).isEqualTo("Frankfurt(Main)Hbf");
		}
	}
}
