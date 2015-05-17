package demo.database.configuration;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile("mysql")
@Configuration
@EnableJpaRepositories(
		entityManagerFactoryRef = "mySqlEntityManagerFactory", 
		transactionManagerRef = "mySqlTransactionManager", 
		basePackages = "demo.repository.mysql"
		)
@EnableTransactionManagement
public class MySqlConfiguration {

	private final String MYSQL_SCAN_PACKAGES = "demo.domain";

	@Bean(name = "mySqlDataSource")
	public DataSource dataSource() {
		DriverManagerDataSource source = new DriverManagerDataSource();
		source.setDriverClassName("com.mysql.jdbc.Driver");
		source.setUrl("root");
		source.setPassword("mysql");
		source.setUrl("jdbc:mysql://localhost:3306/test");
		return source;
	}

	@Bean(name = "h2EntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan(MYSQL_SCAN_PACKAGES);
		factory.setPersistenceUnitName("mySqlPersistenceUnit");
		factory.setDataSource(dataSource());

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.show_sql", "true");
		jpaProperties.put("hibernate.format_sql", "true");
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

		factory.setJpaProperties(jpaProperties);
		factory.afterPropertiesSet();

		return factory;
	}

	@Bean(name = "mySqlEntityManger")
	public EntityManager entityManger(
			@Qualifier("mySqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.createEntityManager();
	}

	@Bean(name = "mySqlTransactionManger")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		EntityManagerFactory factory = entityManagerFactory()
				.getNativeEntityManagerFactory();
		txManager.setEntityManagerFactory(factory);
		return txManager;
	}

	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
}
