<?xml version="1.0"?>

<beans
	default-destroy-method="destroy"
	default-init-method="afterPropertiesSet"
	xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd"
>
	<bean id="liferayHibernateSessionFactory" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.spring.hibernate.PortletHibernateConfiguration" />
		<constructor-arg>
			<map>
				<entry key="dataSource" value-ref="liferayDataSource" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="liferaySessionFactory" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.dao.orm.hibernate.PortletSessionFactoryImpl" />
		<constructor-arg>
			<map>
				<entry key="dataSource" value-ref="liferayDataSource" />
				<entry key="sessionFactoryClassLoader" value-ref="portletClassLoader" />
				<entry key="sessionFactoryImplementor" value-ref="liferayHibernateSessionFactory" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="liferayTransactionManager" class="com.liferay.portal.kernel.util.InfrastructureUtil" factory-method="getTransactionManager" />
</beans>