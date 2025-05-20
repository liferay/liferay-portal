<?xml version="1.0"?>

<beans
	default-destroy-method="destroy"
	default-init-method="afterPropertiesSet"
	xmlns="http://www.springframework.org/schema/beans"
	xmlns:util="http://www.springframework.org/schema/util"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.0.xsd"
>
	<bean class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.cluster.ClusterableChainableMethodAdviceInjector" />
		<constructor-arg>
			<map>
				<entry key="parentChainableMethodAdvice" value-ref="serviceAdvice" />
			</map>
		</constructor-arg>
	</bean>
</beans>