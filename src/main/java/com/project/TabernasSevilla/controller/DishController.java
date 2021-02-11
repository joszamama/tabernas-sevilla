package com.project.TabernasSevilla.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.TabernasSevilla.domain.Actor;
import com.project.TabernasSevilla.domain.Dish;
import com.project.TabernasSevilla.domain.Review;
import com.project.TabernasSevilla.repository.ReviewRepository;
import com.project.TabernasSevilla.service.ActorService;
import com.project.TabernasSevilla.service.DishService;

@Controller
@RequestMapping("/dishes")
public class DishController extends AbstractController {

	private DishService dishService;

	private ActorService actorService;

	private ReviewRepository reviewRepo;

	@Autowired
	public DishController(DishService dishService, ActorService actorService, ReviewRepository reviewRepo) {
		super();
		this.dishService = dishService;
		this.actorService = actorService;
		this.reviewRepo = reviewRepo;
	}

	@GetMapping(path = "/{dishId}")
	public String showDishInfo(@PathVariable("dishId") int dishId, Model model) {
		String view = "dishes/dishInfo"; // vista a la que pasamos la informacion
		Dish dish = dishService.findById(dishId).get(); // plato en concreto, con toda su información
		model.addAttribute("dish", dish);
		List<Review> reviews = reviewRepo.findByDish(dishId);
		model.addAttribute("reviews", reviews);
		Review rev = new Review();

		model.addAttribute("reviewComment", rev);
		return view;
	}

	@GetMapping(path = "")
	public String dishList(Model model) {
		List<Dish> dishes = dishService.findAll();
		model.addAttribute("dishes", dishes);
		return "dishes/list";

	}

	@GetMapping(path = "/new")
	public String createDish(Model model) {
		String view = super.checkIfCurrentUserIsAllowed("dishes/createDishForm", "ADMIN");
		model.addAttribute("dish", new Dish());
		return view;
	}

	@PostMapping(path = "/save")
	public String saveDish(@Valid Dish dish, BindingResult result, Model model) {
		String view = super.checkIfCurrentUserIsAllowed("redirect:/dishes", "ADMIN");
		if (result.hasErrors()) {
			model.addAttribute("dish", dish);
			return "dishes/createDishForm";
		} else {
			dishService.save(dish);
			model.addAttribute("message", "Dish successfully saved");
		}
		return view;
	}

	@PostMapping(path = "/savecomment/{dishId}")
	public String saveComment(@Valid Review review, BindingResult result, Model model, RedirectAttributes reA,
			@PathVariable("dishId") int dishId) {
		String view = super.checkIfCurrentUserIsAllowed("redirect:/dishes/" + dishId, "CUSTOMER");
		Dish dish = dishService.findById(dishId).get();

		Actor actor = this.actorService.getPrincipal(); // usuario loggeado

		review.setActor(actor);
		review.setDish(dish);

		if (result.hasErrors()) {
			List<ObjectError> errors = result.getAllErrors();
			for (int i = 0; i < result.getErrorCount(); i++) {
				System.out.println("]]]]]]] error " + i + " is: " + errors.get(i).toString());
			}

			reA.addFlashAttribute("message", errors);
			return "redirect:/dishes/" + dishId;
		} else {
			reviewRepo.save(review);
			dishService.save(dish);
			view = "redirect:/dishes/" + dishId;
		}
		return view;
	}

	@GetMapping(path = "/delete/{dishId}")
	public String deleteDish(@PathVariable("dishId") int dishId, Model model) {

		String view = super.checkIfCurrentUserIsAllowed("redirect:/dishes", "ADMIN");

		if (view != "error") {
			Optional<Dish> dish = dishService.findById(dishId);
			if (dish.isPresent()) {
				Dish d = dishService.findById(dishId).get();
				d.setIsVisible(false);
				model.addAttribute("message", "Dish succesfully deleted");
				view = dishList(model);
			} else {
				model.addAttribute("message", "Dish not found");
				view = dishList(model);
			}
		}

		return view;
	}

	@GetMapping(value = "{dishId}/edit")
	public String updateDishForm(@PathVariable("dishId") int dishId, Model model) {
		String view = "dishes/updateDishForm";
		Optional<Dish> dish = this.dishService.findById(dishId);
		model.addAttribute(dish.get());
		return view;
	}

	@PostMapping(value = "{dishId}/edit")
	public String processUpdateDishForm(@Valid Dish dish, BindingResult result, @PathVariable("dishId") int dishId) {
		String view = super.checkIfCurrentUserIsAllowed("redirect:/dishes", "ADMIN");

		if (result.hasErrors()) {
			return view;
		} else {

			dish.setId(dishId);

			try {
				this.dishService.save(dish);
			} catch (Exception e) {
				FieldError error = new FieldError("dish", "picture", "Invalid URL" + e.getMessage());
				result.addError(error);
				return view;
			}

			return view;
		}
	}

}
