package org.launchcode.blogz.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.blogz.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController extends AbstractController {

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupForm() {
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, Model model) {

		String username = "";
		String password = "";

		String submittedUsername = request.getParameter("username");
		String submittedPassword = request.getParameter("password");
		String submittedVerify = request.getParameter("verify");

		User existingUser = userDao.findByUsername(submittedUsername);

		if (existingUser != null) {
			
			model.addAttribute("username_error", "That username is already taken");
			return "signup";
			
		} else { // no pre-existing user --> validate submitted fields
						
			if ( !User.isValidUsername(submittedUsername) ) {
				
				model.addAttribute("username_error", "Invalid username");
				return "signup";
				
			} else {
				username = submittedUsername;
				model.addAttribute("username", username);
				
				if ( !User.isValidPassword(submittedPassword) ) {
					
					model.addAttribute("password_error", "Invalid password");
					return "signup";
				
				} else { 
					password = submittedPassword;
					
					if ( !password.equals(submittedVerify)) {
						
						model.addAttribute("verify_error", "Invalid password verification");
						return "signup";
					
					} else { // all fields valid --> create user, add to table, add to session
						
						User user = new User(username, password);
						userDao.save(user);
						setUserInSession(request.getSession(), user);	
					}
				}
			}
		}

		return "redirect:blog/newpost";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm() {
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, Model model) {
		
		String submittedUsername = request.getParameter("username");
		String submittedPassword = request.getParameter("password");
		
		User user = userDao.findByUsername(submittedUsername);
		model.addAttribute("username", submittedUsername);
		
		if (user == null) {
	
			model.addAttribute("error", "Username not found");
			return "login";
		
		} else {
			
			if ( !user.isMatchingPassword(submittedPassword)) {
				
				model.addAttribute("error", "Invalid password");
				return "login";
			
			} else { setUserInSession(request.getSession(), user); }
		}
		return "redirect:blog/newpost";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}
}
