/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.mvc.binding.BindingResult;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * @author Neil Griffin
 */
@Configuration
public class PortletParamProducer implements ApplicationContextAware {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Boolean getBooleanParam(InjectionPoint injectionPoint) {
		String value = getStringParam(injectionPoint);

		if (value == null) {
			return null;
		}

		AnnotatedElement annotatedElement =
			injectionPoint.getAnnotatedElement();

		Annotation[] annotations = annotatedElement.getAnnotations();

		ParamConverter<Boolean> paramConverter = _getParamConverter(
			Boolean.class, Boolean.class, annotations);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					annotations, illegalArgumentException.getMessage(), value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a ParamConverterProvider for type Boolean");
		}

		return null;
	}

	@Bean
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Date getDateParam(InjectionPoint injectionPoint) {
		String value = getStringParam(injectionPoint);

		if (value == null) {
			return null;
		}

		AnnotatedElement annotatedElement =
			injectionPoint.getAnnotatedElement();

		Annotation[] annotations = annotatedElement.getAnnotations();

		ParamConverter<Date> paramConverter = _getParamConverter(
			Date.class, Date.class, annotations);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					annotations, illegalArgumentException.getMessage(), value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Unable to find a ParamConverterProvider for type Date");
		}

		return null;
	}

	@Bean
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Double getDoubleParam(InjectionPoint injectionPoint) {
		String value = getStringParam(injectionPoint);

		if (value == null) {
			return null;
		}

		AnnotatedElement annotatedElement =
			injectionPoint.getAnnotatedElement();

		Annotation[] annotations = annotatedElement.getAnnotations();

		ParamConverter<Double> paramConverter = _getParamConverter(
			Double.class, Double.class, annotations);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					annotations, illegalArgumentException.getMessage(), value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a ParamConverterProvider for type Double");
		}

		return null;
	}

	@Bean
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Float getFloatParam(InjectionPoint injectionPoint) {
		String value = getStringParam(injectionPoint);

		if (value == null) {
			return null;
		}

		AnnotatedElement annotatedElement =
			injectionPoint.getAnnotatedElement();

		Annotation[] annotations = annotatedElement.getAnnotations();

		ParamConverter<Float> paramConverter = _getParamConverter(
			Float.class, Float.class, annotations);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					annotations, illegalArgumentException.getMessage(), value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Unable to find a ParamConverterProvider for type Float");
		}

		return null;
	}

	@Bean
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Integer getIntegerParam(InjectionPoint injectionPoint) {
		String value = getStringParam(injectionPoint);

		if (value == null) {
			return null;
		}

		AnnotatedElement annotatedElement =
			injectionPoint.getAnnotatedElement();

		Annotation[] annotations = annotatedElement.getAnnotations();

		ParamConverter<Integer> paramConverter = _getParamConverter(
			Integer.class, Integer.class, annotations);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					annotations, illegalArgumentException.getMessage(), value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a ParamConverterProvider for type Integer");
		}

		return null;
	}

	@Bean
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Long getLongParam(InjectionPoint injectionPoint) {
		String value = getStringParam(injectionPoint);

		if (value == null) {
			return null;
		}

		AnnotatedElement annotatedElement =
			injectionPoint.getAnnotatedElement();

		Annotation[] annotations = annotatedElement.getAnnotations();

		ParamConverter<Long> paramConverter = _getParamConverter(
			Long.class, Long.class, annotations);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					annotations, illegalArgumentException.getMessage(), value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Unable to find a ParamConverterProvider for type Long");
		}

		return Long.valueOf(value);
	}

	@Bean
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public String getStringParam(InjectionPoint injectionPoint) {
		PortletRequest portletRequest = _applicationContext.getBean(
			PortletRequest.class);

		AnnotatedElement annotatedElement =
			injectionPoint.getAnnotatedElement();

		String defaultValue = null;

		DefaultValue annotation = annotatedElement.getAnnotation(
			DefaultValue.class);

		if (annotation != null) {
			defaultValue = annotation.value();
		}

		CookieParam cookieParam = annotatedElement.getAnnotation(
			CookieParam.class);

		if (cookieParam != null) {
			Cookie[] cookies = portletRequest.getCookies();

			for (Cookie cookie : cookies) {
				if (Objects.equals(cookieParam.value(), cookie.getName())) {
					String cookieValue = cookie.getValue();

					if (cookieValue == null) {
						if (_log.isDebugEnabled()) {
							_log.debug(
								StringBundler.concat(
									"Injecting the default value \"",
									defaultValue, "\" into an element ",
									"annotated with @CookieParam(\"",
									cookieParam.value(), "\") because the ",
									"cookie does not have a value"));
						}

						return defaultValue;
					}

					return cookieValue;
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Injecting the default value \"", defaultValue,
						"\" into an element annotated with @CookieParam(\"",
						cookieParam.value(), "\") because there is no cookie ",
						"with that name"));
			}

			return defaultValue;
		}

		FormParam formParam = annotatedElement.getAnnotation(FormParam.class);

		if (formParam != null) {
			String lifecyclePhase = (String)portletRequest.getAttribute(
				PortletRequest.LIFECYCLE_PHASE);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			HttpServletRequest httpServletRequest = themeDisplay.getRequest();

			String httpMethod = httpServletRequest.getMethod();

			if (StringUtil.equalsIgnoreCase(httpMethod, "post") &&
				(lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
				 lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE))) {

				@SuppressWarnings("deprecation")
				String parameterValue = portletRequest.getParameter(
					formParam.value());

				if (parameterValue == null) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Injecting the default value \"", defaultValue,
								"\" into an element annotated with ",
								"@FormParam(\"", formParam.value(), "\") ",
								"because the request parameter does not have ",
								"a value"));
					}

					return defaultValue;
				}

				return parameterValue;
			}

			if (_log.isDebugEnabled()) {
				if (lifecyclePhase.equals(PortletRequest.RENDER_PHASE)) {
					_log.debug(
						StringBundler.concat(
							"Injection into an element annotated with ",
							"@FormParam(\"", formParam.value(), "\") is ",
							"invalid during the RENDER_PHASE"));
				}
				else {
					_log.debug(
						StringBundler.concat(
							"Injection into an element annotated with ",
							"@FormParam(\"", formParam.value(), "\") is ",
							"invalid during the ", lifecyclePhase, " (HTTP ",
							httpMethod, ") request"));
				}
			}

			return null;
		}

		HeaderParam headerParam = annotatedElement.getAnnotation(
			HeaderParam.class);

		if (headerParam != null) {
			Enumeration<String> enumeration = portletRequest.getPropertyNames();

			while (enumeration.hasMoreElements()) {
				String propertyName = enumeration.nextElement();

				if (Objects.equals(headerParam.value(), propertyName)) {
					String headerValue = portletRequest.getProperty(
						propertyName);

					if (headerValue == null) {
						if (_log.isDebugEnabled()) {
							_log.debug(
								StringBundler.concat(
									"Injecting the default value \"",
									defaultValue, "\" into an element ",
									"annotated with @HeaderParam(\"",
									headerParam.value(), "\") because the ",
									"header does not have a value"));
						}

						return defaultValue;
					}

					return headerValue;
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Injecting the default value \"", defaultValue,
						"\" into an element annotated with @HeaderParam(\"",
						headerParam.value(), "\") because there is no header ",
						"with that name"));
			}

			return defaultValue;
		}

		QueryParam queryParam = annotatedElement.getAnnotation(
			QueryParam.class);

		if (queryParam != null) {
			@SuppressWarnings("deprecation")
			String parameterValue = portletRequest.getParameter(
				queryParam.value());

			if (parameterValue == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Injecting the default value \"", defaultValue,
							"\" into an element annotated with @QueryParam(\"",
							queryParam.value(), "\") because the request ",
							"parameter does not have a value"));
				}

				return defaultValue;
			}

			return parameterValue;
		}

		return defaultValue;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		_applicationContext = applicationContext;
	}

	private void _addBindingError(
		Annotation[] annotations, String message, String value) {

		MutableBindingResult mutableBindingResult =
			(MutableBindingResult)_bindingResult;

		if (mutableBindingResult == null) {
			_log.error("Unable to add a binding error");
		}
		else {
			mutableBindingResult.addBindingError(
				new BindingErrorImpl(
					message, _getParamName(annotations), value));
		}
	}

	private <T> ParamConverter<T> _getParamConverter(
		Class<T> rawType, Type baseType, Annotation[] annotations) {

		for (ParamConverterProvider paramConverterProvider :
				_paramConverterProviders) {

			ParamConverter<T> paramConverter =
				paramConverterProvider.getConverter(
					rawType, baseType, annotations);

			if (paramConverter != null) {
				return paramConverter;
			}
		}

		return null;
	}

	private String _getParamName(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationClass = annotation.getClass();

			if (CookieParam.class.isAssignableFrom(annotationClass)) {
				CookieParam cookieParam = (CookieParam)annotation;

				return cookieParam.value();
			}

			if (FormParam.class.isAssignableFrom(annotationClass)) {
				FormParam formParam = (FormParam)annotation;

				return formParam.value();
			}

			if (HeaderParam.class.isAssignableFrom(annotationClass)) {
				HeaderParam headerParam = (HeaderParam)annotation;

				return headerParam.value();
			}

			if (QueryParam.class.isAssignableFrom(annotationClass)) {
				QueryParam queryParam = (QueryParam)annotation;

				return queryParam.value();
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletParamProducer.class);

	private ApplicationContext _applicationContext;

	@Autowired
	private BindingResult _bindingResult;

	@Autowired
	@ParamConverterProviders
	private List<ParamConverterProvider> _paramConverterProviders;

}