package com.duocode.assigment.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.duocode.assigment.LinkResolver;
import com.duocode.assigment.models.Product;

import io.prismic.Api;
import io.prismic.Document;
import io.prismic.Fragment.StructuredText.Block;
import io.prismic.Fragment.StructuredText.Block.Heading;
import io.prismic.Fragment.StructuredText.Block.Paragraph;


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
		
		List<Document> document = api.query().submit().getResults();
		List<Product> products = createProducts(document);
		 model.addAttribute("documents", api.query().submit().getResults());
		 model.addAttribute("products", products);
		 model.addAttribute("linkResolver", linkResolver);
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
            model.addAttribute("linkResolver", linkResolver);
            return "detail";
        } else { 
            return "redirect:/documents/" + id + "/" + maybeDocument.getSlug() + "/";
        }
    }
	
	/**
	 * @param product
	 * @param document
	 */
	private List<Product> createProducts(List<Document> document) {
		List<Product> products = new ArrayList<Product>();
		Iterator<Document> itr = document.iterator();
		
		while(itr.hasNext()) {
			Product product =  new Product();
			
	         Document element = itr.next();
	         product.setId(element.getId());
	         product.setImage(((io.prismic.Fragment.Image)element.get("product.image")).getView("main").getUrl());
	         product.setPrice(((io.prismic.Fragment.Text) element.get("product.price")).getValue());
	         List<Block> nameBlocks = ((io.prismic.Fragment.StructuredText)
					 element.get("product.name")).getBlocks();
	         	StringBuilder name = new StringBuilder();
		         nameBlocks.forEach(block ->{
	     			try {	        				
	     				name.append(((Paragraph) block).getText());
	     			}catch (Exception e) {
							// TODO: handle exception
	     				name.append(((Heading) block).getText());
						}
	     	});
		         product.setName(name.toString());
		         
				
	         StringBuilder description = new StringBuilder();
	         List<Block> dscBlocks = ((io.prismic.Fragment.StructuredText) element.get("product.description")).getBlocks();
	        	dscBlocks.forEach(block ->{
	        			try {	        				
	        				description.append(((Paragraph) block).getText());
	        			}catch (Exception e) {
							// TODO: handle exception
	        				description.append(((Heading) block).getText());
						}
	        	});
	        	product.setDescription(description.toString());
	        	
	        	products.add(product);
	         
	      }
		return products;
	}
	
}
