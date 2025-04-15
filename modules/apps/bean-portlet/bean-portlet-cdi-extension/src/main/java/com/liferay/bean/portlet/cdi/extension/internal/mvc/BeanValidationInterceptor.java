/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.annotation.Priority;

import jakarta.enterprise.inject.spi.BeanManager;

import jakarta.inject.Inject;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import jakarta.mvc.MvcContext;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Path;
import jakarta.validation.Validator;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.io.Serializable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Set;

/**
 * @author Neil Griffin
 */
@BeanValidationInterceptorBinding
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class BeanValidationInterceptor implements Serializable {

	@AroundInvoke
	public Object validateMethodInvocation(InvocationContext invocationContext)
		throws Exception {

		if (_validator == null) {
			return invocationContext.proceed();
		}

		Set<ConstraintViolation<Object>> constraintViolations =
			_validator.validate(invocationContext.getTarget());

		for (ConstraintViolation<Object> constraintViolation :
				constraintViolations) {

			Path propertyPath = constraintViolation.getPropertyPath();

			Path.Node lastPathNode = null;

			for (Path.Node pathNode : propertyPath) {
				lastPathNode = pathNode;
			}

			if ((lastPathNode != null) &&
				(lastPathNode.getKind() == ElementKind.PROPERTY)) {

				Object leafBean = constraintViolation.getLeafBean();

				Class<?> leafBeanClass = leafBean.getClass();

				BeanInfo beanInfo = Introspector.getBeanInfo(
					leafBean.getClass());

				PropertyDescriptor[] propertyDescriptors =
					beanInfo.getPropertyDescriptors();

				for (PropertyDescriptor propertyDescriptor :
						propertyDescriptors) {

					String propertyDescriptorName =
						propertyDescriptor.getName();

					if (propertyDescriptorName.equals("targetClass")) {
						Method method = propertyDescriptor.getReadMethod();

						leafBeanClass = (Class<?>)method.invoke(leafBean);
					}
				}

				Field field = leafBeanClass.getDeclaredField(
					lastPathNode.getName());

				String paramName = null;

				Annotation[] annotations = field.getAnnotations();

				for (Annotation annotation : annotations) {
					if (annotation instanceof CookieParam) {
						CookieParam cookieParam = (CookieParam)annotation;

						paramName = cookieParam.value();

						break;
					}
					else if (annotation instanceof FormParam) {
						FormParam formParam = (FormParam)annotation;

						paramName = formParam.value();

						break;
					}
					else if (annotation instanceof HeaderParam) {
						HeaderParam headerParam = (HeaderParam)annotation;

						paramName = headerParam.value();

						break;
					}
					else if (annotation instanceof QueryParam) {
						QueryParam queryParam = (QueryParam)annotation;

						paramName = queryParam.value();
					}
				}

				String interpolatedMessage = constraintViolation.getMessage();

				if (_messageInterpolator != null) {
					interpolatedMessage = _messageInterpolator.interpolate(
						constraintViolation.getMessageTemplate(),
						new MessageInterpolatorContextImpl(constraintViolation),
						_mvcContext.getLocale());
				}

				MutableBindingResult mutableBindingResult =
					BeanUtil.getMutableBindingResult(_beanManager);

				mutableBindingResult.addValidationError(
					new ValidationErrorImpl(
						constraintViolation, interpolatedMessage, paramName));
			}
		}

		return invocationContext.proceed();
	}

	private static final long serialVersionUID = 2378576156374329311L;

	@Inject
	private BeanManager _beanManager;

	@BeanValidationMessageInterpolator
	@Inject
	private MessageInterpolator _messageInterpolator;

	@Inject
	private MvcContext _mvcContext;

	@BeanValidationValidator
	@Inject
	private Validator _validator;

}