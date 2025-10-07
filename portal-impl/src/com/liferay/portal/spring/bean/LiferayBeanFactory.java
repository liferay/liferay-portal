/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.bean;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.spring.aop.BaseServiceBeanAutoProxyCreator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author Preston Crary
 */
public class LiferayBeanFactory extends DefaultListableBeanFactory {

	public LiferayBeanFactory(BeanFactory parentBeanFactory) {
		super(parentBeanFactory);
	}

	@Override
	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
		super.addBeanPostProcessor(beanPostProcessor);

		if ((beanPostProcessor instanceof
				InstantiationAwareBeanPostProcessor) &&
			!(beanPostProcessor instanceof BaseServiceBeanAutoProxyCreator)) {

			_postProcessPropertyValues = true;
		}
	}

	@Override
	protected void invokeCustomInitMethod(
			String beanName, Object bean, RootBeanDefinition rootBeanDefinition,
			String initMethodName)
		throws Throwable {

		if (!PropsValues.SPRING_BEANFACTORY_STRICT_LIFECYCLE_ENABLED) {
			super.invokeCustomInitMethod(
				beanName, bean, rootBeanDefinition, initMethodName);

			return;
		}

		Method initMethod = _getMethod(bean.getClass(), initMethodName);

		if (initMethod != null) {
			try {
				initMethod.invoke(bean);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getTargetException();
			}
		}
	}

	@Override
	protected void populateBean(
		String beanName, RootBeanDefinition rootBeanDefinition,
		BeanWrapper beanWrapper) {

		PropertyValues propertyValues = rootBeanDefinition.getPropertyValues();

		if (beanWrapper == null) {
			if (propertyValues.isEmpty()) {
				return;
			}

			throw new BeanCreationException(
				rootBeanDefinition.getResourceDescription(), beanName,
				"Unable to apply property values to null instance");
		}

		if (!_isContinueWithPropertyPopulation(
				beanName, rootBeanDefinition, beanWrapper)) {

			return;
		}

		if ((rootBeanDefinition.getResolvedAutowireMode() ==
				RootBeanDefinition.AUTOWIRE_BY_NAME) ||
			(rootBeanDefinition.getResolvedAutowireMode() ==
				RootBeanDefinition.AUTOWIRE_BY_TYPE)) {

			MutablePropertyValues mutablePropertyValues =
				new MutablePropertyValues(propertyValues);

			if (rootBeanDefinition.getResolvedAutowireMode() ==
					RootBeanDefinition.AUTOWIRE_BY_NAME) {

				autowireByName(
					beanName, rootBeanDefinition, beanWrapper,
					mutablePropertyValues);
			}

			if (rootBeanDefinition.getResolvedAutowireMode() ==
					RootBeanDefinition.AUTOWIRE_BY_TYPE) {

				autowireByType(
					beanName, rootBeanDefinition, beanWrapper,
					mutablePropertyValues);
			}

			propertyValues = mutablePropertyValues;
		}

		boolean hasInstantiationAwareBeanPostProcessors =
			hasInstantiationAwareBeanPostProcessors();

		boolean needsDependencyCheck = true;

		if (rootBeanDefinition.getDependencyCheck() ==
				RootBeanDefinition.DEPENDENCY_CHECK_NONE) {

			needsDependencyCheck = false;
		}

		if ((hasInstantiationAwareBeanPostProcessors &&
			 _postProcessPropertyValues) ||
			needsDependencyCheck) {

			if (hasInstantiationAwareBeanPostProcessors) {
				for (BeanPostProcessor beanPostProcessor :
						getBeanPostProcessors()) {

					if (beanPostProcessor instanceof
							InstantiationAwareBeanPostProcessor
								instantiationAwareBeanPostProcessor) {

						propertyValues =
							instantiationAwareBeanPostProcessor.
								postProcessProperties(
									propertyValues,
									beanWrapper.getWrappedInstance(), beanName);

						if (propertyValues == null) {
							return;
						}
					}
				}
			}

			if (needsDependencyCheck) {
				checkDependencies(
					beanName, rootBeanDefinition,
					filterPropertyDescriptorsForDependencyCheck(
						beanWrapper, true),
					propertyValues);
			}
		}

		applyPropertyValues(
			beanName, rootBeanDefinition, beanWrapper, propertyValues);
	}

	@Override
	protected void registerDisposableBeanIfNecessary(
		String beanName, Object bean, RootBeanDefinition rootBeanDefinition) {

		if (!PropsValues.SPRING_BEANFACTORY_STRICT_LIFECYCLE_ENABLED) {
			super.registerDisposableBeanIfNecessary(
				beanName, bean, rootBeanDefinition);

			return;
		}

		String destroyMethodName = rootBeanDefinition.getDestroyMethodName();

		if (destroyMethodName == null) {
			return;
		}

		Method destroyMethod = _getMethod(bean.getClass(), destroyMethodName);

		if (destroyMethod != null) {
			Method finalDestroyMethod = destroyMethod;

			registerDisposableBean(
				beanName, () -> finalDestroyMethod.invoke(bean));
		}
	}

	private Method _getMethod(Class<?> clazz, String methodName) {
		while ((clazz != null) && (clazz != Object.class)) {
			Method method = ReflectionUtil.fetchDeclaredMethod(
				clazz, methodName);

			if (method != null) {
				return method;
			}

			clazz = clazz.getSuperclass();
		}

		return null;
	}

	private boolean _isContinueWithPropertyPopulation(
		String beanName, RootBeanDefinition rootBeanDefinition,
		BeanWrapper beanWrapper) {

		if (!rootBeanDefinition.isSynthetic() &&
			hasInstantiationAwareBeanPostProcessors()) {

			for (BeanPostProcessor beanPostProcessor :
					getBeanPostProcessors()) {

				if (beanPostProcessor instanceof
						InstantiationAwareBeanPostProcessor) {

					InstantiationAwareBeanPostProcessor
						instantiationAwareBeanPostProcessor =
							(InstantiationAwareBeanPostProcessor)
								beanPostProcessor;

					if (!instantiationAwareBeanPostProcessor.
							postProcessAfterInstantiation(
								beanWrapper.getWrappedInstance(), beanName)) {

						return false;
					}
				}
			}
		}

		return true;
	}

	private boolean _postProcessPropertyValues;

}