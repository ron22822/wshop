package com.example.wshop;

import com.example.wshop.constant.ActivityEnum;
import com.example.wshop.constant.RoleEnum;
import com.example.wshop.constant.StatusEnum;
import com.example.wshop.model.*;
import com.example.wshop.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest
public class RepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    public void roleRepositoryTest(){
        Role role_admin = roleRepository.findById(1L).orElseThrow(() -> new RuntimeException("Role ADMIN not Found"));
        Role role_user = roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("Role USER not Found"));

        if(!RoleEnum.ADMIN.getString().equals(role_admin.getRolename())){
            throw new RuntimeException("Role ADMIN have incorrect name");
        }
        if(!RoleEnum.USER.getString().equals(role_user.getRolename())){
            throw new RuntimeException("Role USER have incorrect name");
        }
    }

    @Test
    @Transactional
    public void createUserAndProfile(){
        User user = new User();
        user.setUsername("user_test");
        user.setPassword("password");
        Role role = roleRepository.findByRolename(RoleEnum.ADMIN.getString()).orElseThrow(
                () -> new RuntimeException("Role does not exist")
        );
        user.setRole(role);
        user.setEmail("test@mail.ru");
        User userRet = userRepository.saveAndFlush(user);

        Profile profile = new Profile();
        profile.setProfileid(userRet.getUserid());
        profile.setFirstname("Ivan");
        profile.setLastname("Ivanov");
        profile.setGender("Male");
        profile.setBirthday(Date.valueOf("2003-10-10"));
        profile.setUser(userRet);
        profileRepository.saveAndFlush(profile);

        Profile profileRet = profileRepository.findByUser(userRet).orElseThrow(
                () -> new RuntimeException("Not find profile")
        );
    }

    @Test
    @Transactional
    public void createProduct(){
        Category category = new Category();
        category.setCategoryname("Smartphone");
        categoryRepository.saveAndFlush(category);
        Supplier supplier = new Supplier();
        supplier.setSuppliername("Samsung");
        supplierRepository.saveAndFlush(supplier);

        Product product = new Product();
        product.setProductname("Galaxy S24");
        product.setActivity(ActivityEnum.ACTIVE.getString());
        product.setTotalquantity(1000);
        product.setPrice(BigDecimal.valueOf(130000.33));
        product.setCategory(categoryRepository.findByCategoryname("Smartphone").orElseThrow(
                () -> new RuntimeException("Not find Category")
        ));
        product.setSupplier(supplierRepository.findBySuppliername("Samsung").orElseThrow(
                () -> new RuntimeException("Not find Supplier")
        ));
        productRepository.saveAndFlush(product);

        Product productInactive = new Product();
        productInactive.setProductname("Galaxy S24++");
        productInactive.setActivity(ActivityEnum.INACTIVE.getString());
        productInactive.setTotalquantity(1000);
        productInactive.setPrice(BigDecimal.valueOf(130000.33));
        productInactive.setCategory(categoryRepository.findByCategoryname("Smartphone").orElseThrow(
                () -> new RuntimeException("Not find Category")
        ));
        productInactive.setSupplier(supplierRepository.findBySuppliername("Samsung").orElseThrow(
                () -> new RuntimeException("Not find Supplier")
        ));
        productRepository.saveAndFlush(productInactive);

        System.out.println("--------------------");
        List<Product> activeProductList = productRepository.findAllActiveProductsWithCategoryAndSupplier();
        activeProductList.forEach(p -> System.out.println(p));
        System.out.println("--------------------");
        List<Product> allProductList = productRepository.findAll();
        allProductList.forEach(p -> System.out.println(p));
        System.out.println("--------------------");
    }

    @Test
    @Transactional
    public void createOrder(){
        User user = new User();
        user.setUsername("user_test_order");
        user.setPassword("password");
        Role role = roleRepository.findByRolename(RoleEnum.ADMIN.getString()).orElseThrow(
                () -> new RuntimeException("Role does not exist")
        );
        user.setRole(role);
        user.setEmail("test@mail.ru");
        User userRet = userRepository.saveAndFlush(user);

        Category category = new Category();
        category.setCategoryname("Lemonade");
        categoryRepository.saveAndFlush(category);
        Supplier supplier = new Supplier();
        supplier.setSuppliername("CocaCola");
        supplierRepository.saveAndFlush(supplier);

        Product productCola = new Product();
        productCola.setProductname("Cola");
        productCola.setActivity(ActivityEnum.INACTIVE.getString());
        productCola.setTotalquantity(100000);
        productCola.setPrice(BigDecimal.valueOf(58.80));
        productCola.setCategory(categoryRepository.findByCategoryname("Lemonade").orElseThrow(
                () -> new RuntimeException("Not find Category")
        ));
        productCola.setSupplier(supplierRepository.findBySuppliername("CocaCola").orElseThrow(
                () -> new RuntimeException("Not find Supplier")
        ));
        Product productColaRet = productRepository.saveAndFlush(productCola);

        Product productSprite = new Product();
        productSprite.setProductname("Sprite");
        productSprite.setActivity(ActivityEnum.INACTIVE.getString());
        productSprite.setTotalquantity(100000);
        productSprite.setPrice(BigDecimal.valueOf(58.80));
        productSprite.setCategory(categoryRepository.findByCategoryname("Lemonade").orElseThrow(
                () -> new RuntimeException("Not find Category")
        ));
        productSprite.setSupplier(supplierRepository.findBySuppliername("CocaCola").orElseThrow(
                () -> new RuntimeException("Not find Supplier")
        ));
        Product productSpriteRet = productRepository.saveAndFlush(productSprite);

        Order order = new Order();
        order.setUser(userRet);
        order.setOrderdate(Date.valueOf("2024-11-24"));
        order.setStatus(StatusEnum.CREATED.getString());
        Order orderRet = orderRepository.saveAndFlush(order);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setItemcount(10);
        OrderItemId orderItemId1 = new OrderItemId();
        orderItemId1.setOrderid(orderRet.getOrderid());
        orderItemId1.setProductid(productColaRet.getProductid());
        orderItem1.setId(orderItemId1);
        orderItem1.setOrderid(orderRet.getOrderid());
        orderItem1.setProductid(productColaRet.getProductid());
        orderItemRepository.saveAndFlush(orderItem1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setItemcount(10);
        OrderItemId orderItemId2 = new OrderItemId();
        orderItemId2.setOrderid(orderRet.getOrderid());
        orderItemId2.setProductid(productSpriteRet.getProductid());
        orderItem2.setId(orderItemId2);
        orderItem2.setOrderid(orderRet.getOrderid());
        orderItem2.setProductid(productSpriteRet.getProductid());
        orderItemRepository.saveAndFlush(orderItem2);

        entityManager.detach(userRet);

        User userRetOrder = userRepository.findUserWithOrders(userRet.getUserid()).get();
        Set<Order> orders = userRetOrder.getOrders();
        System.out.println("--------------------");
        System.out.println("Колличество заказов - "+ orders.size());
        orders.forEach(o -> System.out.println(o));
        System.out.println("--------------------");
    }

}
