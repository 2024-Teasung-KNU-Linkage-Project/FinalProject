//DB와의 연결 설정

package com.OdorPreventSystem.global.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@MapperScan(basePackages = "com.OdorPreventSystem.domain.mapper")
@PropertySource("classpath:/application.yml")
public class DatabaseConfiguration {
	// Spring의 ApplicationContext를 자동으로 주입
	@Autowired
	private ApplicationContext applicationContext;
	// HikariCP 설정을 위한 Bean 정의
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		// HikariCP 설정 객체를 반환
		return new HikariConfig();
	}
	// DataSource 설정을 위한 Bean 정의
	@Bean
	public DataSource dataSource() throws Exception {
		// HikariConfig 설정을 기반으로 HikariDataSource 객체를 생성
		DataSource dataSource = new HikariDataSource(hikariConfig());
		// 데이터 소스 정보를 콘솔에 출력
		System.out.println(dataSource.toString());
		return dataSource;// 데이터 소스 객체를 반환
	}
	
	//contains all things about database connection and sql execution
	// SqlSessionFactory 설정을 위한 Bean 정의
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		// SqlSessionFactoryBean 객체를 생성
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);// 생성된 데이터 소스를 설정
		// MyBatis 매퍼 파일의 위치를 설정
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/**/sql-*.xml"));
		sqlSessionFactoryBean.setConfiguration(mybatisConfig());// MyBatis 설정을 추가
		return sqlSessionFactoryBean.getObject();
	}

	// MyBatis설정을 위한 Bean 정의
	@Bean
	@ConfigurationProperties(prefix="mybatis.configuration")
	public org.apache.ibatis.session.Configuration mybatisConfig() {
		// MyBatis 설정 객체를 반환
		return new org.apache.ibatis.session.Configuration();
	}

	// SqlSessionTemplate 설정을 위한 Bean 정의
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		// SqlSessionTemplate 객체를 생성하여 반환
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
