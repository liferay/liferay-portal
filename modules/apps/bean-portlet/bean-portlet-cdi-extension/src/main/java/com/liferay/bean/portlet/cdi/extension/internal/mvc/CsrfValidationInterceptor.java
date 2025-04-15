/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.annotation.Priority;

import jakarta.inject.Inject;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import jakarta.mvc.security.Csrf;
import jakarta.mvc.security.CsrfProtected;

import jakarta.portlet.ClientDataRequest;

import jakarta.ws.rs.core.Configuration;

import java.io.Serializable;

import java.lang.reflect.Method;

/**
 * @author Neil Griffin
 */
@CsrfValidationInterceptorBinding
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CsrfValidationInterceptor implements Serializable {

	@AroundInvoke
	public Object validateMethodInvocation(InvocationContext invocationContext)
		throws Exception {

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

		if (csrfOptions == Csrf.CsrfOptions.OFF) {
			return invocationContext.proceed();
		}

		Method method = invocationContext.getMethod();

		if ((csrfOptions == Csrf.CsrfOptions.EXPLICIT) &&
			!method.isAnnotationPresent(CsrfProtected.class)) {

			return invocationContext.proceed();
		}

		boolean proceed = false;

		Object[] args = invocationContext.getParameters();

		if (args.length == 2) {
			if (args[0] instanceof ClientDataRequest) {
				ClientDataRequest clientDataRequest =
					(ClientDataRequest)args[0];

				String requestMethod = StringUtil.toLowerCase(
					clientDataRequest.getMethod());

				if (requestMethod.equals("post")) {
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
						_log.error(
							"The CSRF token is invalid", principalException);
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
			return invocationContext.proceed();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CsrfValidationInterceptor.class);

	private static final long serialVersionUID = 1348567603498123441L;

	@Inject
	private Configuration _configuration;

}