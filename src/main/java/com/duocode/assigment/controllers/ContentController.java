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
	
	
	List<Product> products = new ArrayList<Product>();
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
    public static class DocumentNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap model) {
		System.out.println("Trying to connect to: "+prismicApi);
		Product product =  new Product();
		
		Api api = Api.get(prismicApi);
		
		List<Document> document = api.query().submit().getResults();
		products = createProducts(product, document);
		System.out.println("Price: "+products.get(0).getPrice()+" Description: "+products.get(0).getDescription());
		 model.addAttribute("documents", api.query().submit().getResults());
		  
		 model.addAttribute("linkResolver", linkResolver);
        // model.addAttribute("documents", api.query().submit().getResults());
        return "index";
    }

	/**
	 * @param product
	 * @param document
	 */
	private List<Product> createProducts(Product product, List<Document> document) {
		Iterator<Document> itr = document.iterator();
		
		//itr.getFragments().get("prismicpage.bandeau_des_offres_background_color")
		while(itr.hasNext()) {
	         Document element = itr.next();
	         product.setImage(((io.prismic.Fragment.Image)element.get("product.image")).getView("main").getUrl());
	         product.setPrice(((io.prismic.Fragment.Text) element.get("product.price")).getValue());
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
	
}
