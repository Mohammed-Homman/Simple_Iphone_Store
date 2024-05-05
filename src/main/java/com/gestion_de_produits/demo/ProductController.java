package com.gestion_de_produits.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/allProducts")
    public ModelAndView showProducts() {
        ModelAndView mav = new ModelAndView("products");
        mav.addObject("products", productRepository.findAll());
        return mav;
    }

    @GetMapping("/addProduct")
    public ModelAndView addProductForm() {
        ModelAndView mav = new ModelAndView("addProduct");
        mav.addObject("product", new Product());
        return mav;
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        if (product != null && product.getModelName() != null) {
            Optional<Product> existingProductOpt = productRepository.findByModelName(product.getModelName());
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();
                existingProduct.setQuantite(existingProduct.getQuantite() + 1);
                existingProduct.setPrice(product.getPrice());
                productRepository.save(existingProduct);
                redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
            } else {
                product.setQuantite(1);
                productRepository.save(product);
                redirectAttributes.addFlashAttribute("message", "New product saved successfully!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Error saving product. Please check the details and try again.");
        }
        return "redirect:/allProducts";
    }
    @PutMapping("/updateProduct/{id}")
    public String updateProduct(@PathVariable Long id, @RequestParam(required = false) Double price, @RequestParam(required = false) Integer quantite, RedirectAttributes redirectAttributes) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (price != null) product.setPrice(price);
            if (quantite != null) product.setQuantite(quantite);
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
        }
        return "redirect:/allProducts";
    }
}

