package kitchenpos.service.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql({"/truncate.sql", "/test_data.sql"})
public abstract class ServiceTest {
}