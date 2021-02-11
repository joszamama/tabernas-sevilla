package com.project.TabernasSevilla.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.project.TabernasSevilla.domain.Admin;
import com.project.TabernasSevilla.domain.Booking;
import com.project.TabernasSevilla.domain.Establishment;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class BookingServiceTest {
	@Autowired
	private BookingService bookingService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private EstablishmentService estService;

	@Test
	public void testingRegisterandSave() {
		Booking b = new Booking();
		b.setActor(this.adminService.findAll().get(0));
		b.setContactPhone("655778899");
		b.setEstablishment(this.estService.findById(1));
		b.setNotes("Comida de navidad");
		b.setPlacementDate(Instant.now());
		b.setReservationDate(Instant.now().plus(Duration.ofDays(2)));
		b.setSeating(4);
		Booking saved1 = this.bookingService.register(b, this.adminService.findAll().get(0));
		assertThat(saved1.getContactPhone()).isEqualTo(b.getContactPhone());
	}

	@Test
	public void testingNullRegisterandSave() {
		Booking b = new Booking();
		b.setActor(new Admin());
		b.setContactPhone("655778899");
		b.setEstablishment(new Establishment());
		b.setNotes("Comida de navidad");
		b.setPlacementDate(null);
		b.setReservationDate(null);
		b.setSeating(4);
//		Las fechas no pueden ser null
		assertThrows(NullPointerException.class, () -> {
			Booking regis = this.bookingService.register(b, this.adminService.findAll().get(0));
			Booking saved = this.bookingService.save(regis);
		});
	}

	@Test
	public void testingBadRegisterandSave() {
		Booking b = new Booking();
		b.setActor(new Admin());
		b.setContactPhone("655778899");
		
		Establishment est = new Establishment();
		
		b.setEstablishment(est);
		b.setNotes("Comida de navidad");
		b.setPlacementDate(Instant.now());
		b.setReservationDate(Instant.now());
		b.setSeating(4);
//		No puedo reservar con menos de dos horas de antelación
		assertThrows(IllegalArgumentException.class, () -> {
			Booking regis = this.bookingService.register(b, this.adminService.findAll().get(0));
			Booking saved = this.bookingService.save(regis);
		});
	}

	@Test
	public void shouldFindByEstablishment() {
		List<Booking> lista = new ArrayList<>();
		Establishment establ = this.estService.findById(2);
		Booking book = new Booking();
		List<Admin> adminA = this.adminService.findAll();
		Admin savedA = adminA.get(0);
		book.setActor(savedA);
		book.setContactPhone("655778899");
		book.setNotes("Comida de navidad");
		book.setPlacementDate(Instant.now());
		book.setReservationDate(Instant.now().plus(Duration.ofDays(2)));
		book.setSeating(4);
		book.setEstablishment(establ);
		this.bookingService.save(book);
		lista = this.bookingService.findByEstablishment(establ);
		assertThat(lista.get(0)).isEqualTo(book);
	}

}
