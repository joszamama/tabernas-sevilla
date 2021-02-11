package com.project.TabernasSevilla.domain;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {

	@ManyToOne(fetch=FetchType.LAZY)
	private Actor actor;
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	private RestaurantOrder order;
	
	@NotNull
	private Instant deliveryDate;
	
	private Instant readDate;
	@NotBlank
	private String message;
}
