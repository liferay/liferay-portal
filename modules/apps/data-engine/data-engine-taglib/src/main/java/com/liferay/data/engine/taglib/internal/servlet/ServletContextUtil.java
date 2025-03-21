/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.taglib.internal.servlet;

import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Component(service = {})
public class ServletContextUtil {

	public static ServletContext getServletContext() {
		if (Validator.isNotNull(_instance)) {
			return _instance._getServletContext();
		}

		return null;
	}

	@Activate
	protected void activate() {
		_instance = this;
	}

	@Deactivate
	protected void deactivate() {
		_instance = null;
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.data.engine.taglib)",
		unbind = "-"
	)
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private ServletContext _getServletContext() {
		return _servletContext;
	}

	private static ServletContextUtil _instance;

	private ServletContext _servletContext;

}