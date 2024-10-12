package org.example.tools.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.PostConstruct;
import org.apache.commons.compress.utils.Lists;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private List<Product> productList = Lists.newArrayList();

    @PostConstruct
    public void init() {
        this.productList.add(Product.builder().id(1).name("Pomme").type("Fruits").quantity(322).build());
        this.productList.add(Product.builder().id(2).name("Tomate").type("Légumes").quantity(449).build());
        this.productList.add(Product.builder().id(3).name("Aubergine").type("Légumes").quantity(122).build());
        this.productList.add(Product.builder().id(4).name("Poire").type("Fruits").quantity(113).build());
    }


    @Tool("retourne la liste du stock de produits")
    public List<Product> all() {
        return this.productList;
    }

    @Tool("recherche d'un produit dont l'identifiant est id")
    public Product getProduct(@P("identifiant du produit") int id) throws BadRequestException {
        return this.productList
                .stream()
                .filter(product -> id == product.getId()).findAny()
                .orElseThrow((() -> new BadRequestException("product id " + id + " not found")));
    }

    @Tool("recherche d'un produit dont le nom est name")
    public Product getProduct(String name) throws BadRequestException {
        return this.productList
                .stream()
                .filter(product -> name.equals(product.getName()))
                .findAny()
                .orElseThrow((() -> new BadRequestException("product name " + name + " not found")));
    }

    @Tool("""
    ajoute un produit avec l'identifiant id, le nom name, le type et la quantité
    par défaut la quantité est égal à 100
    """)
    public void addProduct(int id, String name, String type, int quantity) {
        this.productList.add(Product.builder().id(id).name(name).type(type).quantity(quantity).build());
    }

    @Tool("mise à jour de la fiche d'un produit à partir d'un id, name, type et quantity")
    public void updateProduct(int id, String name, String type, int quantity) throws BadRequestException {
        Product findProduct = this.productList
                .stream()
                .filter(product -> id == product.getId()).findAny()
                .orElseThrow((() -> new BadRequestException("product id " + id + " not found")));
        findProduct.setName(name);
        findProduct.setType(type);
        findProduct.setQuantity(quantity);
    }

    @Tool("supprime un produit dont l'identifiant est id")
    public void deleteProduct(int id) throws BadRequestException {
        Product findProduct = this.productList
                .stream()
                .filter(product -> id == product.getId()).findAny()
                .orElseThrow((() -> new BadRequestException("product id " + id + " not found")));
        this.productList.remove(findProduct);
    }
}
