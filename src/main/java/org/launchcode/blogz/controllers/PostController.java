package org.launchcode.blogz.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.blogz.models.Post;
import org.launchcode.blogz.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PostController extends AbstractController {

	@RequestMapping(value = "/blog/newpost", method = RequestMethod.GET)
	public String newPostForm() {
		return "newpost";
	}
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, Model model) {
		
		User author = getUserFromSession(request.getSession());
		String title = request.getParameter("title");
		String body = request.getParameter("body");
		
		if ( title.equals("") || body.equals("")) {
			model.addAttribute("title", title);
			model.addAttribute("body", body);
			model.addAttribute("error", "Title and Body required for each post");
			return "newpost";
		}
		
		Post post = new Post(title, body, author);
		postDao.save(post);
		
		String postURL = "/blog/" + author.getUsername() + "/" + post.getUid();
		return String.format("redirect:%s", postURL);
		  		
	}
	
	@RequestMapping(value = "/blog/{username}/{uid}", method = RequestMethod.GET)
	public String singlePost(@PathVariable String username, @PathVariable int uid, Model model) {
		
		User author = userDao.findByUsername(username);
		Post post = null;
				
		for (Post p : author.getPosts()) {
			if (p.getUid() == uid) post = p;
		}
		
		model.addAttribute("post", post);

		return "post";
	}
	
	@RequestMapping(value = "/blog/{username}", method = RequestMethod.GET)
	public String userPosts(@PathVariable String username, Model model) {
		
		User author = userDao.findByUsername(username);
		
		model.addAttribute("posts", author.getPosts());
		
		return "blog";
	}
	
}
