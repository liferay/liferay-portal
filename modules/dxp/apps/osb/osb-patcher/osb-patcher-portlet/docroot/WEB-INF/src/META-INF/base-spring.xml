<?xml version="1.0"?>

<beans
	default-destroy-method="destroy"
	default-init-method="afterPropertiesSet"
	xmlns="http://www.springframework.org/schema/beans"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd"
>
	<bean class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.spring.aop.ServiceBeanAutoProxyCreator" />
		<constructor-arg>
			<map>
				<entry key="beanMatcher">
					<bean class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
						<constructor-arg value="com.liferay.portal.spring.aop.ServiceBeanMatcher" />
					</bean>
				</entry>
				<entry key="methodInterceptor" value-ref="serviceAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.spring.bean.BeanReferenceAnnotationBeanPostProcessor" />
	</bean>
	<bean class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.spring.context.PortletBeanFactoryCleaner" />
	</bean>
	<bean id="portletClassLoader" class="com.liferay.portal.kernel.portlet.PortletClassLoaderUtil" factory-method="getClassLoader" />
	<bean id="servletContextName" class="com.liferay.portal.kernel.portlet.PortletClassLoaderUtil" factory-method="getServletContextName" />
	<bean id="basePersistence" abstract="true">
		<property name="dataSource" ref="liferayDataSource" />
		<property name="sessionFactory" ref="liferaySessionFactory" />
	</bean>
	<bean id="serviceAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.spring.aop.SkipAdvice" />
		<constructor-arg>
			<map>
				<entry key="nextMethodInterceptor" value-ref="accessControlAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="accessControlAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.security.ac.AccessControlAdvice" />
		<constructor-arg>
			<map>
				<entry key="accessControlAdvisor">
					<bean class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
						<constructor-arg value="com.liferay.portal.security.ac.AccessControlAdvisorImpl" />
					</bean>
				</entry>
				<entry key="nextMethodInterceptor" value-ref="portalResiliencyAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="portalResiliencyAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.resiliency.service.PortalResiliencyAdvice" />
		<constructor-arg>
			<map>
				<entry key="nextMethodInterceptor" value-ref="serviceMonitorAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="serviceMonitorAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.monitoring.statistics.service.ServiceMonitorAdvice" />
		<constructor-arg>
			<map>
				<entry key="nextMethodInterceptor" value-ref="asyncAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="asyncAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.messaging.async.AsyncAdvice" />
		<constructor-arg>
			<map>
				<entry key="defaultDestinationName" value="liferay/async_service" />
				<entry key="nextMethodInterceptor" value-ref="threadLocalCacheAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="threadLocalCacheAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.cache.ThreadLocalCacheAdvice" />
		<constructor-arg>
			<map>
				<entry key="nextMethodInterceptor" value-ref="bufferedIncrementAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="bufferedIncrementAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.increment.BufferedIncrementAdvice" />
		<constructor-arg>
			<map>
				<entry key="nextMethodInterceptor" value-ref="indexableAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="indexableAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.search.IndexableAdvice" />
		<constructor-arg>
			<map>
				<entry key="nextMethodInterceptor" value-ref="systemEventAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="systemEventAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.systemevent.SystemEventAdvice" />
		<constructor-arg>
			<map>
				<entry key="nextMethodInterceptor" value-ref="transactionAdvice" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="transactionAdvice" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.spring.transaction.TransactionInterceptorFactoryBean" />
		<constructor-arg>
			<map>
				<entry key="platformTransactionManager" value-ref="liferayTransactionManager" />
				<entry key="transactionAttributeSource" value-ref="transactionAttributeSource" />
			</map>
		</constructor-arg>
	</bean>
	<bean id="transactionAttributeSource" class="com.liferay.portal.kernel.spring.util.SpringFactoryUtil" factory-method="newBean">
		<constructor-arg value="com.liferay.portal.spring.transaction.AnnotationTransactionAttributeSource" />
	</bean>
</beans>