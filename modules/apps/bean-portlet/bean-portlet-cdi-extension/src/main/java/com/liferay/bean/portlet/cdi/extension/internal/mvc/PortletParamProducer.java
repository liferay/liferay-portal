/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;

import jakarta.inject.Inject;

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
import java.lang.reflect.Type;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Neil Griffin
 */
@Dependent
public class PortletParamProducer {

	@Dependent
	@PortletParam
	@Produces
	public Boolean getBooleanParam(
		InjectionPoint injectionPoint, PortletRequest portletRequest) {

		String value = getStringParam(injectionPoint, portletRequest);

		if (value == null) {
			return null;
		}

		Annotated annotated = injectionPoint.getAnnotated();

		Annotation[] fieldAnnotations = _getFieldAnnotations(annotated);

		ParamConverter<Boolean> paramConverter = _getParamConverter(
			fieldAnnotations, annotated.getBaseType(), Boolean.class);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					fieldAnnotations, illegalArgumentException.getMessage(),
					value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a ParamConverterProvider for type Boolean");
		}

		return null;
	}

	@Dependent
	@PortletParam
	@Produces
	public Date getDateParam(
		InjectionPoint injectionPoint, PortletRequest portletRequest) {

		String value = getStringParam(injectionPoint, portletRequest);

		if (value == null) {
			return null;
		}

		Annotated annotated = injectionPoint.getAnnotated();

		Annotation[] fieldAnnotations = _getFieldAnnotations(annotated);

		ParamConverter<Date> paramConverter = _getParamConverter(
			fieldAnnotations, annotated.getBaseType(), Date.class);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					fieldAnnotations, illegalArgumentException.getMessage(),
					value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Unable to find a ParamConverterProvider for type Date");
		}

		return null;
	}

	@Dependent
	@PortletParam
	@Produces
	public Double getDoubleParam(
		InjectionPoint injectionPoint, PortletRequest portletRequest) {

		String value = getStringParam(injectionPoint, portletRequest);

		if (value == null) {
			return null;
		}

		Annotated annotated = injectionPoint.getAnnotated();

		Annotation[] fieldAnnotations = _getFieldAnnotations(annotated);

		ParamConverter<Double> paramConverter = _getParamConverter(
			fieldAnnotations, annotated.getBaseType(), Double.class);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					fieldAnnotations, illegalArgumentException.getMessage(),
					value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a ParamConverterProvider for type Double");
		}

		return null;
	}

	@Dependent
	@PortletParam
	@Produces
	public Float getFloatParam(
		InjectionPoint injectionPoint, PortletRequest portletRequest) {

		String value = getStringParam(injectionPoint, portletRequest);

		if (value == null) {
			return null;
		}

		Annotated annotated = injectionPoint.getAnnotated();

		Annotation[] fieldAnnotations = _getFieldAnnotations(annotated);

		ParamConverter<Float> paramConverter = _getParamConverter(
			fieldAnnotations, annotated.getBaseType(), Float.class);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					fieldAnnotations, illegalArgumentException.getMessage(),
					value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Unable to find a ParamConverterProvider for type Float");
		}

		return null;
	}

	@Dependent
	@PortletParam
	@Produces
	public Integer getIntegerParam(
		InjectionPoint injectionPoint, PortletRequest portletRequest) {

		String value = getStringParam(injectionPoint, portletRequest);

		if (value == null) {
			return null;
		}

		Annotated annotated = injectionPoint.getAnnotated();

		Annotation[] fieldAnnotations = _getFieldAnnotations(annotated);

		ParamConverter<Integer> paramConverter = _getParamConverter(
			fieldAnnotations, annotated.getBaseType(), Integer.class);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					fieldAnnotations, illegalArgumentException.getMessage(),
					value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a ParamConverterProvider for type Integer");
		}

		return null;
	}

	@Dependent
	@PortletParam
	@Produces
	public Long getLongParam(
		InjectionPoint injectionPoint, PortletRequest portletRequest) {

		String value = getStringParam(injectionPoint, portletRequest);

		if (value == null) {
			return null;
		}

		Annotated annotated = injectionPoint.getAnnotated();

		Annotation[] fieldAnnotations = _getFieldAnnotations(annotated);

		ParamConverter<Long> paramConverter = _getParamConverter(
			fieldAnnotations, annotated.getBaseType(), Long.class);

		if (paramConverter != null) {
			try {
				return paramConverter.fromString(value);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				_addBindingError(
					fieldAnnotations, illegalArgumentException.getMessage(),
					value);

				return null;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Unable to find a ParamConverterProvider for type Long");
		}

		return Long.valueOf(value);
	}

	@Dependent
	@PortletParam
	@Produces
	public String getStringParam(
		InjectionPoint injectionPoint, PortletRequest portletRequest) {

		Annotated annotated = injectionPoint.getAnnotated();

		String defaultValue = null;

		DefaultValue defaultValueAnnotation = annotated.getAnnotation(
			DefaultValue.class);

		if (defaultValueAnnotation != null) {
			defaultValue = defaultValueAnnotation.value();
		}

		CookieParam cookieParam = annotated.getAnnotation(CookieParam.class);

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
									defaultValue,
									"\" into a annotated annotated with ",
									"@CookieParam(\"", cookieParam.value(),
									"\") because the cookie does not have a ",
									"value"));
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
						"\" into a annotated annotated with @CookieParam(\"",
						cookieParam.value(), "\") because there is no cookie ",
						"with that name"));
			}

			return defaultValue;
		}

		FormParam formParam = annotated.getAnnotation(FormParam.class);

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
								"\" into a annotated annotated with ",
								"@FormParam(\"", formParam.value(),
								"\") because the request parameter does not ",
								"have a value"));
					}

					return defaultValue;
				}

				return parameterValue;
			}

			if (_log.isDebugEnabled()) {
				if (lifecyclePhase.equals(PortletRequest.RENDER_PHASE)) {
					_log.debug(
						StringBundler.concat(
							"Injection into a annotated annotated with ",
							"@FormParam(\"", formParam.value(), "\") is ",
							"invalid during the RENDER_PHASE"));
				}
				else {
					_log.debug(
						StringBundler.concat(
							"Injection into a annotated annotated with ",
							"@FormParam(\"", formParam.value(), "\") is ",
							"invalid during the ", lifecyclePhase, " (HTTP ",
							httpMethod, ") request"));
				}
			}

			return null;
		}

		HeaderParam headerParam = annotated.getAnnotation(HeaderParam.class);

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
									defaultValue,
									"\" into a annotated annotated with ",
									"@HeaderParam(\"", headerParam.value(),
									"\") because the header does not have a ",
									"value"));
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
						"\" into a annotated annotated with @HeaderParam(\"",
						headerParam.value(),
						"\") because there is no header with that name"));
			}

			return defaultValue;
		}

		QueryParam queryParam = annotated.getAnnotation(QueryParam.class);

		if (queryParam != null) {
			@SuppressWarnings("deprecation")
			String parameterValue = portletRequest.getParameter(
				queryParam.value());

			if (parameterValue == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Injecting the default value \"", defaultValue,
							"\" into a annotated annotated with @QueryParam(\"",
							queryParam.value(),
							"\") because the request parameter does not have ",
							"a value"));
				}

				return defaultValue;
			}

			return parameterValue;
		}

		return defaultValue;
	}

	private void _addBindingError(
		Annotation[] fieldAnnotations, String message, String value) {

		MutableBindingResult mutableBindingResult =
			BeanUtil.getMutableBindingResult(_beanManager);

		if (mutableBindingResult == null) {
			_log.error("Unable to add a binding error");
		}
		else {
			mutableBindingResult.addBindingError(
				new BindingErrorImpl(
					message, _getParamName(fieldAnnotations), value));
		}
	}

	private Annotation[] _getFieldAnnotations(Annotated annotated) {
		Set<Annotation> annotations = annotated.getAnnotations();

		return annotations.toArray(new Annotation[0]);
	}

	private <T> ParamConverter<T> _getParamConverter(
		Annotation[] annotations, Type baseType, Class<T> rawType) {

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

	@Inject
	private BeanManager _beanManager;

	@Inject
	@ParamConverterProviders
	private List<ParamConverterProvider> _paramConverterProviders;

}