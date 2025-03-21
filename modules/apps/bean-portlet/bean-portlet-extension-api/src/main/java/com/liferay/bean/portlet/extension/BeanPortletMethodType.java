/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.extension;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.annotations.ActionMethod;
import jakarta.portlet.annotations.DestroyMethod;
import jakarta.portlet.annotations.EventMethod;
import jakarta.portlet.annotations.HeaderMethod;
import jakarta.portlet.annotations.InitMethod;
import jakarta.portlet.annotations.RenderMethod;
import jakarta.portlet.annotations.ServeResourceMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.function.Function;

/**
 * @author Neil Griffin
 */
public enum BeanPortletMethodType {

	ACTION(
		ActionMethod.class, null, annotation -> null, true, annotation -> null,
		annotation -> 0,
		new Class<?>[] {ActionRequest.class, ActionResponse.class},
		annotation -> null,
		annotation -> {
			ActionMethod actionMethod = ActionMethod.class.cast(annotation);

			return new String[] {actionMethod.portletName()};
		},
		false),
	DESTROY(
		DestroyMethod.class, null, annotation -> null, false,
		annotation -> null, annotation -> 0, new Class<?>[0],
		annotation -> null,
		annotation -> {
			DestroyMethod destroyMethod = DestroyMethod.class.cast(annotation);

			return new String[] {destroyMethod.value()};
		},
		false),
	EVENT(
		EventMethod.class, null, annotation -> null, false, annotation -> null,
		annotation -> 0,
		new Class<?>[] {EventRequest.class, EventResponse.class},
		annotation -> null,
		annotation -> {
			EventMethod eventMethod = EventMethod.class.cast(annotation);

			return new String[] {eventMethod.portletName()};
		},
		false),
	HEADER(
		HeaderMethod.class, annotation -> null, annotation -> null, false,
		annotation -> {
			HeaderMethod headerMethod = HeaderMethod.class.cast(annotation);

			return headerMethod.include();
		},
		annotation -> {
			HeaderMethod headerMethod = HeaderMethod.class.cast(annotation);

			return headerMethod.ordinal();
		},
		new Class<?>[] {HeaderRequest.class, HeaderResponse.class},
		annotation -> {
			HeaderMethod headerMethod = HeaderMethod.class.cast(annotation);

			return new PortletMode(headerMethod.portletMode());
		},
		annotation -> {
			HeaderMethod headerMethod = HeaderMethod.class.cast(annotation);

			return headerMethod.portletNames();
		},
		true),
	INIT(
		InitMethod.class, null, annotation -> null, false, annotation -> null,
		annotation -> 0, new Class<?>[] {PortletConfig.class},
		annotation -> null,
		annotation -> {
			InitMethod initMethod = InitMethod.class.cast(annotation);

			return new String[] {initMethod.value()};
		},
		false),
	RENDER(
		RenderMethod.class, annotation -> null, annotation -> null, true,
		annotation -> {
			RenderMethod renderMethod = RenderMethod.class.cast(annotation);

			return renderMethod.include();
		},
		annotation -> {
			RenderMethod renderMethod = RenderMethod.class.cast(annotation);

			return renderMethod.ordinal();
		},
		new Class<?>[] {RenderRequest.class, RenderResponse.class},
		annotation -> {
			RenderMethod renderMethod = RenderMethod.class.cast(annotation);

			return new PortletMode(renderMethod.portletMode());
		},
		annotation -> {
			RenderMethod renderMethod = RenderMethod.class.cast(annotation);

			return renderMethod.portletNames();
		},
		true),
	SERVE_RESOURCE(
		ServeResourceMethod.class,
		annotation -> {
			ServeResourceMethod serveResourceMethod =
				ServeResourceMethod.class.cast(annotation);

			return serveResourceMethod.characterEncoding();
		},
		annotation -> {
			ServeResourceMethod serveResourceMethod =
				ServeResourceMethod.class.cast(annotation);

			return serveResourceMethod.contentType();
		},
		true,
		annotation -> {
			ServeResourceMethod serveResourceMethod =
				ServeResourceMethod.class.cast(annotation);

			return serveResourceMethod.include();
		},
		annotation -> {
			ServeResourceMethod serveResourceMethod =
				ServeResourceMethod.class.cast(annotation);

			return serveResourceMethod.ordinal();
		},
		new Class<?>[] {ResourceRequest.class, ResourceResponse.class},
		annotation -> PortletMode.VIEW,
		annotation -> {
			ServeResourceMethod serveResourceMethod =
				ServeResourceMethod.class.cast(annotation);

			return serveResourceMethod.portletNames();
		},
		true);

	public String getCharacterEncoding(Method method) {
		Annotation annotation = method.getAnnotation(_annotation);

		if (annotation == null) {
			return null;
		}

		return _characterEncodingFunction.apply(annotation);
	}

	public String getContentType(Method method) {
		Annotation annotation = method.getAnnotation(_annotation);

		if (annotation == null) {
			return null;
		}

		return _contentTypeFunction.apply(annotation);
	}

	public String getInclude(Method method) {
		Annotation annotation = method.getAnnotation(_annotation);

		if (annotation == null) {
			return null;
		}

		return _includeFunction.apply(annotation);
	}

	public int getOrdinal(Method method) {
		Annotation annotation = method.getAnnotation(_annotation);

		if (annotation == null) {
			return 0;
		}

		return _ordinalFunction.apply(annotation);
	}

	public PortletMode getPortletMode(Method method) {
		Annotation annotation = method.getAnnotation(_annotation);

		if ((annotation == null) || (_portletModeFunction == null)) {
			return null;
		}

		return _portletModeFunction.apply(annotation);
	}

	public String[] getPortletNames(Method method) {
		Annotation annotation = method.getAnnotation(_annotation);

		if (annotation == null) {
			return null;
		}

		return _portletNamesFunction.apply(annotation);
	}

	public boolean isMatch(Method method) {
		if (!method.isAnnotationPresent(_annotation)) {
			return false;
		}

		Class<?> returnClass = method.getReturnType();

		Class<?>[] parameterTypes = method.getParameterTypes();

		// Exact match

		if (returnClass.equals(Void.TYPE) &&
			_isAssignableFrom(parameterTypes)) {

			return true;
		}

		// Variant match

		if (_variant && (parameterTypes.length == 0) &&
			(returnClass.equals(Void.TYPE) ||
			 returnClass.equals(String.class))) {

			return true;
		}

		// Controller match

		if (_controller &&
			((parameterTypes.length == 0) ||
			 _isAssignableFrom(parameterTypes))) {

			return true;
		}

		_log.error(
			StringBundler.concat(
				"Method ", method, " does not have a valid signature for @",
				_annotation.getSimpleName()));

		return false;
	}

	private BeanPortletMethodType(
		Class<? extends Annotation> annotation,
		Function<Annotation, String> characterEncodingFunction,
		Function<Annotation, String> contentTypeFunction, boolean controller,
		Function<Annotation, String> includeFunction,
		Function<Annotation, Integer> ordinalFunction,
		Class<?>[] parameterTypes,
		Function<Annotation, PortletMode> portletModeFunction,
		Function<Annotation, String[]> portletNamesFunction, boolean variant) {

		_annotation = annotation;
		_characterEncodingFunction = characterEncodingFunction;
		_contentTypeFunction = contentTypeFunction;
		_controller = controller;
		_includeFunction = includeFunction;
		_ordinalFunction = ordinalFunction;
		_parameterTypes = parameterTypes;
		_portletModeFunction = portletModeFunction;
		_portletNamesFunction = portletNamesFunction;
		_variant = variant;
	}

	private boolean _isAssignableFrom(Class<?>[] parameterTypes) {
		if (parameterTypes.length == _parameterTypes.length) {
			for (int i = 0; i < parameterTypes.length; i++) {
				if (!_parameterTypes[i].isAssignableFrom(parameterTypes[i])) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanPortletMethodType.class);

	private final Class<? extends Annotation> _annotation;
	private final Function<Annotation, String> _characterEncodingFunction;
	private final Function<Annotation, String> _contentTypeFunction;
	private final boolean _controller;
	private final Function<Annotation, String> _includeFunction;
	private final Function<Annotation, Integer> _ordinalFunction;
	private final Class<?>[] _parameterTypes;
	private final Function<Annotation, PortletMode> _portletModeFunction;
	private final Function<Annotation, String[]> _portletNamesFunction;
	private final boolean _variant;

}