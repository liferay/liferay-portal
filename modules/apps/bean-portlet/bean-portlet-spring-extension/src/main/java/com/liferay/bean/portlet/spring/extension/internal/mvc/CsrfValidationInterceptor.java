/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.mvc.security.Csrf;
import jakarta.mvc.security.CsrfProtected;

import jakarta.portlet.ClientDataRequest;

import jakarta.ws.rs.core.Configuration;

import java.lang.reflect.Method;

/**
 * @author Neil Griffin
 */
public class CsrfValidationInterceptor extends BeanPortletMethodInterceptor {

	public CsrfValidationInterceptor(
		BeanPortletMethod beanPortletMethod, Configuration configuration,
		boolean controller) {

		super(beanPortletMethod, controller);

		_configuration = configuration;

		_method = beanPortletMethod.getMethod();
	}

	@Override
	public Object invoke(Object... args) throws ReflectiveOperationException {
		if (!isController()) {
			return super.invoke(args);
		}

		Csrf.CsrfOptions csrfOptions = Csrf.CsrfOptions.EXPLICIT;

		Object csrfProtection = _configuration.getProperty(
			Csrf.CSRF_PROTECTION);

		if (csrfProtection != null) {
			if (csrfProtection instanceof Csrf.CsrfOptions) {
				csrfOptions = (Csrf.CsrfOptions)csrfProtection;
			}
			else {
				try {
					csrfOptions = Csrf.CsrfOptions.valueOf(
						csrfProtection.toString());
				}
				catch (IllegalArgumentException illegalArgumentException) {
					_log.error(illegalArgumentException);
				}
			}
		}

		if ((csrfOptions == Csrf.CsrfOptions.OFF) ||
			((csrfOptions == Csrf.CsrfOptions.EXPLICIT) &&
			 !_method.isAnnotationPresent(CsrfProtected.class))) {

			return super.invoke(args);
		}

		boolean proceed = false;

		if (args.length == 2) {
			if (args[0] instanceof ClientDataRequest) {
				ClientDataRequest clientDataRequest =
					(ClientDataRequest)args[0];

				String method = StringUtil.toLowerCase(
					clientDataRequest.getMethod());

				if (method.equals("post")) {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)clientDataRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					try {
						AuthTokenUtil.checkCSRFToken(
							themeDisplay.getRequest(),
							CsrfValidationInterceptor.class.getName());

						proceed = true;
					}
					catch (PrincipalException principalException) {
						_log.error("Invalid CSRF token", principalException);
					}
				}
				else {
					proceed = true;
				}
			}
			else {
				_log.error(
					"The first parameter of the method signature must be an " +
						"ActionRequest or ResourceRequest");
			}
		}
		else {
			_log.error(
				"The method signature must include (ActionRequest, " +
					"ActionResponse) or (ResourceRequest, ResourceResponse) " +
						"as parameters");
		}

		if (proceed) {
			return super.invoke(args);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CsrfValidationInterceptor.class);

	private final Configuration _configuration;
	private final Method _method;

}