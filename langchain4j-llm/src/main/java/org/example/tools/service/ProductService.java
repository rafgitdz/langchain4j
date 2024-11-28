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

    private final List<Product> productList = Lists.newArrayList();

    @PostConstruct
    public void init() {
        this.productList.add(Product.builder().id(1).name("Apple").type("Fruits").quantity(322).build());
        this.productList.add(Product.builder().id(2).name("Tomatoe").type("Vegetables").quantity(449).build());
        this.productList.add(Product.builder().id(3).name("Strawberry").type("Vegetables").quantity(122).build());
        this.productList.add(Product.builder().id(4).name("Pear").type("Fruits").quantity(113).build());
    }

    @Tool("returns the list of products in stock")
    public List<Product> all() {
        return this.productList;
    }

    @Tool("search for a product whose identifier is id")
    public Product getProduct(@P("product identifier") int id) throws BadRequestException {
        return this.productList
                .stream()
                .filter(product -> id == product.getId()).findAny()
                .orElseThrow((() -> new BadRequestException("product id " + id + " not found")));
    }

    @Tool("search for a product with the name")
    public Product getProduct(String name) throws BadRequestException {
        return this.productList
                .stream()
                .filter(product -> name.equals(product.getName()))
                .findAny()
                .orElseThrow((() -> new BadRequestException("product name " + name + " not found")));
    }

    @Tool("""
            adds a product with identifier id, name name, type and quantity
            by default the quantity is 100
    """)
    public void addProduct(int id, String name, String type, int quantity) {
        this.productList.add(Product.builder().id(id).name(name).type(type).quantity(quantity).build());
    }

    @Tool("update a product file using an id, name, type and quantity")
    public void updateProduct(int id, String name, String type, int quantity) throws BadRequestException {
        Product findProduct = this.productList
                .stream()
                .filter(product -> id == product.getId()).findAny()
                .orElseThrow((() -> new BadRequestException("product id " + id + " not found")));
        findProduct.setName(name);
        findProduct.setType(type);
        findProduct.setQuantity(quantity);
    }

    @Tool("deletes a product whose identifier is id")
    public void deleteProduct(int id) throws BadRequestException {
        Product findProduct = this.productList
                .stream()
                .filter(product -> id == product.getId()).findAny()
                .orElseThrow((() -> new BadRequestException("product id " + id + " not found")));
        this.productList.remove(findProduct);
    }
}
