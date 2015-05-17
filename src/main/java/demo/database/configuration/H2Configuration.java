package demo.database.configuration;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate3.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile("h2")
@Configuration
@EnableJpaRepositories(
		entityManagerFactoryRef 	= "h2EntityManagerFactory",
		transactionManagerRef 		= "h2TransactionManager",
		basePackages = "demo.repository"
		)
@EnableTransactionManagement
public class H2Configuration {
	
	private final String H2_SCAN_PACKAGES = "demo.domain";
	
	@Bean(name = "h2DataSource")
	@Primary
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		builder.setName("h2-in-memory-database");
		builder.setType(EmbeddedDatabaseType.H2);
		return builder.build();
	}

	@Bean(name = "h2EntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan(H2_SCAN_PACKAGES);
		factory.setPersistenceUnitName("h2PersistenceUnit");
		factory.setDataSource(dataSource());
		
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.show_sql", "true");
		jpaProperties.put("hibernate.format_sql", "true");
		
		factory.setJpaProperties(jpaProperties);
		factory.afterPropertiesSet();
		
		return factory;
	}
	
	@Bean(name = "h2EntityManger")
	@Primary
	public EntityManager entityManger(@Qualifier("h2EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.createEntityManager();
	}
	
	@Bean(name = "h2TransactionManger")
	@Primary
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		EntityManagerFactory factory = entityManagerFactory().getNativeEntityManagerFactory();
		txManager.setEntityManagerFactory(factory);
		return txManager;
	}
	
	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
	
	@Bean
	public ServletRegistrationBean h2ServletRegistrationBean() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
		registrationBean.addUrlMappings("/console/*");
		return registrationBean;
	}
}
