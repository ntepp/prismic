package com.duocode.assigment.controllers;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.duocode.assigment.LinkResolver;

import io.prismic.Api;
import io.prismic.Document;


@Controller
public class ContentController {
	
	@Value("${prismic.api.url}")
	private String prismicApi;
    
	@Autowired
	private LinkResolver linkResolver;
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
    public static class DocumentNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap model) {
		System.out.println("Trying to connect to: "+prismicApi);
		Api api = Api.get(prismicApi);
		
		Iterator<Document> itr = api.query().submit().getResults().iterator();
		while(itr.hasNext()) {
	         Document element = itr.next();
	         //System.out.print(element.getSlug() + " "+element.asHtml(linkResolver));
	      }
		System.out.println();
		 model.addAttribute("documents", api.query().submit().getResults());
		 
		 model.addAttribute("linkResolver", linkResolver);
        // model.addAttribute("documents", api.query().submit().getResults());
        return "index";
    }
	
	@RequestMapping(value = "/documents/{id:[-_a-zA-Z0-9]{16}}/{slug}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") String id,
            @PathVariable("slug") String slug, ModelMap model) {
		Api api = Api.get(prismicApi);
        Document maybeDocument = api.getByID(id);
        if (maybeDocument == null) {
            throw new DocumentNotFoundException();
        }
        if (maybeDocument.getSlug().equals(slug)) {
            model.addAttribute("document", maybeDocument);
            return "detail";
        } else { 
            return "redirect:/documents/" + id + "/" + maybeDocument.getSlug() + "/";
        }
    }
	
}
