/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.struts;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.runtime.exception.StatusException;
import com.liferay.saml.util.JspUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Mika Koivisto
 */
public abstract class BaseSamlStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!isEnabled(httpServletRequest)) {
			return "/common/referer_js.jsp";
		}

		httpServletRequest.setAttribute(
			WebKeys.RESOURCE_BUNDLE_LOADER,
			ResourceBundleLoaderUtil.getPortalResourceBundleLoader());

		Class<? extends BaseSamlStrutsAction> clazz = getClass();

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				clazz.getClassLoader())) {

			return doExecute(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else {
				_log.error(exception);
			}

			Class<?> exceptionClass = exception.getClass();

			SessionErrors.add(httpServletRequest, exceptionClass.getName());

			if (exception instanceof StatusException) {
				StatusException statusException = (StatusException)exception;

				SessionErrors.add(
					httpServletRequest, "statusCodeURI",
					statusException.getMessage());
			}

			JspUtil.dispatch(
				httpServletRequest, httpServletResponse,
				JspUtil.PATH_PORTAL_SAML_ERROR, "status");
		}

		return null;
	}

	public abstract boolean isEnabled(HttpServletRequest httpServletRequest);

	protected abstract String doExecute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSamlStrutsAction.class);

}