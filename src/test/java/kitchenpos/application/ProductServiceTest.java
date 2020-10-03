package kitchenpos.application;

import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductServiceTest extends ServiceTest {
    @Autowired
    private ProductDao productDao;

    private ProductService productService;

    static Stream<Arguments> invalidPrices() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(BigDecimal.valueOf(-1000))
        );
    }

    @BeforeEach
    void setUp() {
        productService = new ProductService(productDao);
        productIds = new ArrayList<>();
    }

    @DisplayName("새로운 상품 생성")
    @Test
    void createTest() {
        Product product = new Product();
        product.setName("강정치킨");
        product.setPrice(BigDecimal.valueOf(17_000));

        Product savedProduct = productService.create(product);
        productIds.add(savedProduct.getId());

        assertAll(
                () -> assertThat(savedProduct.getId()).isNotNull(),
                () -> assertThat(savedProduct.getName()).isEqualTo(product.getName()),
                () -> assertThat(savedProduct.getPrice()).isEqualTo(
                        product.getPrice().setScale(BIG_DECIMAL_FLOOR_SCALE, BigDecimal.ROUND_FLOOR))
        );
    }

    @DisplayName("새로운 상품 저장 시 가격을 잘못 입력했을 때 예외 출력")
    @ParameterizedTest
    @MethodSource("invalidPrices")
    void createWithInvalidPriceTest(BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);

        assertThatThrownBy(() -> {
            productService.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("저장된 모든 상품 반환")
    @Test
    void listTest() {
        saveProduct(productDao, "후라이드치킨", BigDecimal.valueOf(16_000));
        saveProduct(productDao, "양념치킨", BigDecimal.valueOf(16_000));

        List<Product> products = productService.list();

        assertThat(products).hasSize(2);
    }

    @AfterEach
    void tearDown() {
        deleteProduct();
    }
}