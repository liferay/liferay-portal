/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodDecorator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.annotation.ManagedBean;

import jakarta.mvc.Controller;
import jakarta.mvc.MvcContext;
import jakarta.mvc.binding.BindingResult;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validator;

import jakarta.ws.rs.core.Configuration;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Neil Griffin
 */
@ManagedBean("beanPortletMethodDecorator")
public class BeanPortletMethodDecoratorMVCImpl
	implements ApplicationContextAware, BeanPortletMethodDecorator {

	@Override
	public BeanPortletMethod getBeanPortletMethod(
		BeanPortletMethod beanPortletMethod, PortletConfig portletConfig,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		boolean controller = _isController(beanPortletMethod);

		Object eventObject = _getEventObject(beanPortletMethod.getBeanClass());

		return new ControllerInterceptor(
			_applicationEventPublisher,
			new BeanValidationInterceptor(
				new CsrfValidationInterceptor(
					beanPortletMethod, _configuration, controller),
				controller, _messageInterpolator,
				(MutableBindingResult)_bindingResult, _mvcContext, eventObject,
				_validator),
			controller, eventObject, portletRequest, portletResponse);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		_applicationContext = applicationContext;
	}

	private Object _getEventObject(Class<?> beanClass) {
		Object eventObject = _applicationContext.getBean(beanClass);

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(
				eventObject.getClass());

			PropertyDescriptor[] propertyDescriptors =
				beanInfo.getPropertyDescriptors();

			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String propertyDescriptorName = propertyDescriptor.getName();

				if (propertyDescriptorName.equals("targetObject")) {
					Method method = propertyDescriptor.getReadMethod();

					eventObject = method.invoke(eventObject);

					break;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return eventObject;
	}

	private boolean _isController(BeanPortletMethod beanPortletMethod) {
		Method method = beanPortletMethod.getMethod();

		if (method.isAnnotationPresent(Controller.class)) {
			return true;
		}

		Class<?> declaringClass = method.getDeclaringClass();

		return declaringClass.isAnnotationPresent(Controller.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanPortletMethodDecoratorMVCImpl.class);

	private ApplicationContext _applicationContext;

	@Autowired
	private ApplicationEventPublisher _applicationEventPublisher;

	@Autowired
	private BindingResult _bindingResult;

	@Autowired
	private Configuration _configuration;

	@Autowired
	@BeanValidationMessageInterpolator
	private MessageInterpolator _messageInterpolator;

	@Autowired
	private MvcContext _mvcContext;

	@Autowired
	@BeanValidationValidator
	private Validator _validator;

}