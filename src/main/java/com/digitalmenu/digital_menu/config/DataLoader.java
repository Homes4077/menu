package com.digitalmenu.digital_menu.config;

import com.digitalmenu.digital_menu.entity.Business;
import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.model.MenuItem;
import com.digitalmenu.digital_menu.repository.BusinessRepository;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import com.digitalmenu.digital_menu.repository.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public DataLoader(BusinessRepository businessRepository,
                      CategoryRepository categoryRepository,
                      MenuItemRepository menuItemRepository) {
        this.businessRepository = businessRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void run(String... args) {
        if (businessRepository.count() == 0) {
            Business paleo = new Business();
            paleo.setName("PALEO HOTEL & SPA");
            paleo.setSlug("paleo-hotel");
            paleo.setDescription("Luxury Dining & Wellness Retreat");
            paleo.setWhatsappNumber("254791635413");
            paleo.setLocation("Nairobi, Kenya");
            paleo.setOpeningHours("07:00 AM - 11:00 PM");
            paleo.setTheme("LUXURY");

            Business savedBusiness = businessRepository.save(paleo);

            Category mains = new Category();
            mains.setBusiness(savedBusiness);
            mains.setName("GOURMET MAINS");
            mains.setDisplayOrder(1);
            Category savedMains = categoryRepository.save(mains);

            Category drinks = new Category();
            drinks.setBusiness(savedBusiness);
            drinks.setName("SIGNATURE DRINKS");
            drinks.setDisplayOrder(2);
            Category savedDrinks = categoryRepository.save(drinks);

            MenuItem item1 = new MenuItem();
            item1.setCategory(savedMains);
            item1.setName("21-Day Aged Ribeye Steak");
            item1.setDescription("Prime beef cut served with truffle mash and grilled asparagus.");
            item1.setPrice(new BigDecimal("2400.00"));
            item1.setAvailable(true);
            item1.setDisplayOrder(1);
            item1.setFeatured(true);
            menuItemRepository.save(item1);

            MenuItem item2 = new MenuItem();
            item2.setCategory(savedDrinks);
            item2.setName("Paleo Sunrise Cocktail");
            item2.setDescription("Freshly squeezed tropical fruits infused with mint and botanical gin.");
            item2.setPrice(new BigDecimal("850.00"));
            item2.setAvailable(true);
            item2.setDisplayOrder(1);
            menuItemRepository.save(item2);

            System.out.println(">>> Sample data loaded for Paleo Hotel & Spa");
        }
    }
}