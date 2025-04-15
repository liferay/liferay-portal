/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;

import java.io.Serializable;

/**
 * @author Bruno Farache
 */
public class SerializableSessionAttributeListener
	implements HttpSessionAttributeListener {

	@Override
	public void attributeAdded(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		Object value = httpSessionBindingEvent.getValue();

		if ((value instanceof Serializable) || (value == null)) {
			return;
		}

		Class<?> clazz = value.getClass();

		_log.error(
			clazz.getName() +
				" is not serializable and will prevent this session from " +
					"being replicated");

		if (_requiresSerializable == null) {
			HttpSession httpSession = httpSessionBindingEvent.getSession();

			ServletContext servletContext = httpSession.getServletContext();

			_requiresSerializable = Boolean.valueOf(
				GetterUtil.getBoolean(
					servletContext.getInitParameter(
						"session-attributes-requires-serializable")));
		}

		if (_requiresSerializable) {
			HttpSession httpSession = httpSessionBindingEvent.getSession();

			httpSession.removeAttribute(httpSessionBindingEvent.getName());
		}
	}

	@Override
	public void attributeRemoved(
		HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	@Override
	public void attributeReplaced(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		attributeAdded(httpSessionBindingEvent);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SerializableSessionAttributeListener.class);

	private Boolean _requiresSerializable;

}