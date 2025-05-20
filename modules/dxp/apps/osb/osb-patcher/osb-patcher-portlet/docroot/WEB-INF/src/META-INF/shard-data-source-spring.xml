<?xml version="1.0"?>

<beans
	default-destroy-method="destroy"
	default-init-method="afterPropertiesSet"
	xmlns="http://www.springframework.org/schema/beans"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd"
>
	<bean id="com.liferay.portal.dao.shard.advice.ShardAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.dao.shard.advice.ShardAdvice" />
	</bean>
	<bean id="com.liferay.portal.dao.shard.advice.ShardPersistenceAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.dao.shard.advice.ShardPersistenceAdvice" />
		<constructor-arg>
			<map>
				<entry key="shardAdvice" value-ref="com.liferay.portal.dao.shard.advice.ShardAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<aop:config proxy-target-class="false">
		<aop:advisor advice-ref="com.liferay.portal.dao.shard.advice.ShardPersistenceAdvice" pointcut="bean(*Persistence) || bean(*Finder)" />
	</aop:config>
</beans>