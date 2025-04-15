/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.enterprise.product.notification.web.internal.servlet.taglib;

import com.liferay.enterprise.product.notification.web.internal.EPNManager;
import com.liferay.enterprise.product.notification.web.internal.constants.EPNWebKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = DynamicInclude.class)
public class EPNJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		String bodyHTML = _epnManager.getBodyHTML(
			_portal.getLocale(httpServletRequest),
			_portal.getUserId(httpServletRequest));

		if (Validator.isNotNull(bodyHTML)) {
			httpServletRequest.setAttribute(
				EPNWebKeys.MODAL_BODY_HTML, bodyHTML);

			super.include(httpServletRequest, httpServletResponse, key);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/view.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EPNJSPDynamicInclude.class);

	@Reference
	private EPNManager _epnManager;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.enterprise.product.notification.web)"
	)
	private ServletContext _servletContext;

}