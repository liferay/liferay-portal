/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import com.liferay.bean.portlet.extension.BeanPortletMethod;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.util.Set;

/**
 * @author Neil Griffin
 */
public class BeanValidationInterceptor extends BeanPortletMethodInterceptor {

	public BeanValidationInterceptor(
		BeanPortletMethod beanPortletMethod, boolean controller,
		MessageInterpolator messageInterpolator,
		MutableBindingResult mutableBindingResult, MvcContext mvcContext,
		Object validationObject, Validator validator) {

		super(beanPortletMethod, controller);

		_messageInterpolator = messageInterpolator;
		_mutableBindingResult = mutableBindingResult;
		_mvcContext = mvcContext;
		_validationObject = validationObject;
		_validator = validator;
	}

	@Override
	public Object invoke(Object... args) throws ReflectiveOperationException {
		if ((_validator == null) || !isController()) {
			return super.invoke(args);
		}

		Set<ConstraintViolation<Object>> constraintViolations =
			_validator.validate(_validationObject);

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

				_mutableBindingResult.addValidationError(
					new ValidationErrorImpl(
						constraintViolation, interpolatedMessage, paramName));
			}
		}

		return super.invoke(args);
	}

	private final MessageInterpolator _messageInterpolator;
	private final MutableBindingResult _mutableBindingResult;
	private final MvcContext _mvcContext;
	private final Object _validationObject;
	private final Validator _validator;

}