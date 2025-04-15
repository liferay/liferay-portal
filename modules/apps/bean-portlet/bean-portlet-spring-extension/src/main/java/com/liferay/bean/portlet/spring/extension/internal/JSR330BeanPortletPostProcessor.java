/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.beans.PropertyDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author Neil Griffin
 */
public class JSR330BeanPortletPostProcessor
	extends AutowiredAnnotationBeanPostProcessor {

	public JSR330BeanPortletPostProcessor() {
		_autowiredAnnotationTypes.add(Autowired.class);

		try {
			@SuppressWarnings("unchecked")
			Class<? extends Annotation> injectAnnotation =
				(Class<? extends Annotation>)ClassUtils.forName(
					"jakarta.inject.Inject",
					JSR330BeanPortletPostProcessor.class.getClassLoader());

			_autowiredAnnotationTypes.add(injectAnnotation);
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(classNotFoundException);
			}
		}

		_autowiredAnnotationTypes.add(Value.class);
	}

	@Override
	public PropertyValues postProcessProperties(
		PropertyValues propertyValues, Object beanInstance, String beanName) {

		InjectionMetadata injectionMetadata = _getInjectionMetadata(
			beanInstance.getClass(), beanName, propertyValues);

		try {
			injectionMetadata.inject(beanInstance, beanName, propertyValues);
		}
		catch (Throwable throwable) {
			if (throwable instanceof BeanCreationException) {
				throw (RuntimeException)throwable;
			}

			throw new BeanCreationException(
				beanName, "Unable to @Inject dependencies", throwable);
		}

		return propertyValues;
	}

	@Override
	public void processInjection(Object beanInstance) {
		Class<?> beanClass = beanInstance.getClass();

		InjectionMetadata injectionMetadata = _getInjectionMetadata(
			beanClass, beanClass.getName(), null);

		try {
			injectionMetadata.inject(beanInstance, null, null);
		}
		catch (Throwable throwable) {
			if (throwable instanceof BeanCreationException) {
				throw (RuntimeException)throwable;
			}

			throw new BeanCreationException("Unable to @Inject dependencies");
		}
	}

	@Override
	public void resetBeanDefinition(String beanName) {
		super.resetBeanDefinition(beanName);

		_injectionMetadataCache.remove(beanName);
	}

	@Override
	public void setAutowiredAnnotationType(
		Class<? extends Annotation> autowiredAnnotationType) {

		super.setAutowiredAnnotationType(autowiredAnnotationType);

		_autowiredAnnotationTypes.clear();
		_autowiredAnnotationTypes.add(autowiredAnnotationType);
	}

	@Override
	public void setAutowiredAnnotationTypes(
		Set<Class<? extends Annotation>> autowiredAnnotationTypes) {

		super.setAutowiredAnnotationTypes(autowiredAnnotationTypes);

		_autowiredAnnotationTypes.clear();
		_autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);

		_configurableListableBeanFactory =
			(ConfigurableListableBeanFactory)beanFactory;
	}

	private AnnotationAttributes _getAnnotationAttributes(
		AccessibleObject accessibleObject) {

		Annotation[] annotations = accessibleObject.getAnnotations();

		if (annotations.length <= 0) {
			return null;
		}

		for (Class<? extends Annotation> autowiredAnnotationType :
				_autowiredAnnotationTypes) {

			AnnotationAttributes mergedAnnotationAttributes =
				AnnotatedElementUtils.getMergedAnnotationAttributes(
					accessibleObject, autowiredAnnotationType);

			if (mergedAnnotationAttributes != null) {
				return mergedAnnotationAttributes;
			}
		}

		return null;
	}

	private InjectionMetadata _getInjectionMetadata(Class<?> beanClass) {
		List<InjectionMetadata.InjectedElement> injectedElements1 =
			new ArrayList<>();
		Class<?> curClass = beanClass;

		while ((curClass != null) && (curClass != Object.class)) {
			List<InjectionMetadata.InjectedElement> injectedElements2 =
				new ArrayList<>();

			Field[] fields = curClass.getDeclaredFields();

			for (Field field : fields) {
				AnnotationAttributes annotationAttributes =
					_getAnnotationAttributes(field);

				if (annotationAttributes != null) {
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}

					boolean required = determineRequiredStatus(
						annotationAttributes);

					injectedElements2.add(
						new JSR330InjectedFieldElement(
							_configurableListableBeanFactory, field, required));
				}
			}

			Method[] methods = curClass.getDeclaredMethods();

			for (Method method : methods) {
				Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(
					method);

				if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(
						method, bridgedMethod)) {

					continue;
				}

				AnnotationAttributes annotationAttributes =
					_getAnnotationAttributes(bridgedMethod);

				if ((annotationAttributes != null) &&
					method.equals(
						ClassUtils.getMostSpecificMethod(method, beanClass))) {

					if (Modifier.isStatic(method.getModifiers())) {
						continue;
					}

					PropertyDescriptor propertyDescriptor =
						BeanUtils.findPropertyForMethod(
							bridgedMethod, beanClass);
					boolean required = determineRequiredStatus(
						annotationAttributes);

					injectedElements2.add(
						new JSR330InjectedMethodElement(
							_configurableListableBeanFactory, method,
							propertyDescriptor, required));
				}
			}

			injectedElements1.addAll(0, injectedElements2);

			curClass = curClass.getSuperclass();
		}

		return new InjectionMetadata(beanClass, injectedElements1);
	}

	private InjectionMetadata _getInjectionMetadata(
		Class<?> beanClass, String beanName, PropertyValues propertyValues) {

		String key = beanName;

		if (StringUtils.isEmpty(key)) {
			key = beanClass.getName();
		}

		InjectionMetadata injectionMetadata = _injectionMetadataCache.get(key);

		if (InjectionMetadata.needsRefresh(injectionMetadata, beanClass)) {
			synchronized (_injectionMetadataCache) {
				injectionMetadata = _injectionMetadataCache.get(key);

				if (InjectionMetadata.needsRefresh(
						injectionMetadata, beanClass)) {

					if (injectionMetadata != null) {
						injectionMetadata.clear(propertyValues);
					}

					injectionMetadata = _getInjectionMetadata(beanClass);

					_injectionMetadataCache.put(key, injectionMetadata);
				}
			}
		}

		return injectionMetadata;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSR330BeanPortletPostProcessor.class);

	private final Set<Class<? extends Annotation>> _autowiredAnnotationTypes =
		new LinkedHashSet<>();
	private ConfigurableListableBeanFactory _configurableListableBeanFactory;
	private final Map<String, InjectionMetadata> _injectionMetadataCache =
		new ConcurrentHashMap<>();

}