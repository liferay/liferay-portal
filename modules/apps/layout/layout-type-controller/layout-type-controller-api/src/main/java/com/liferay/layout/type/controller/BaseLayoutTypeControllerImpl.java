/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.servlet.TransferHeadersHelperUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseLayoutTypeControllerImpl
	implements LayoutTypeController {

	@Override
	public String[] getConfigurationActionDelete() {
		return StringPool.EMPTY_ARRAY;
	}

	@Override
	public String[] getConfigurationActionUpdate() {
		return StringPool.EMPTY_ARRAY;
	}

	@Override
	public String getType() {
		return StringPool.BLANK;
	}

	@Override
	public String includeEditContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout)
		throws Exception {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			TransferHeadersHelperUtil.getTransferHeadersRequestDispatcher(
				servletContext.getRequestDispatcher(getEditPage()));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		ServletResponse servletResponse = createServletResponse(
			httpServletResponse, unsyncStringWriter);

		try {
			addAttributes(httpServletRequest);

			requestDispatcher.include(httpServletRequest, servletResponse);
		}
		finally {
			removeAttributes(httpServletRequest);
		}

		return unsyncStringWriter.toString();
	}

	@Override
	public boolean includeLayoutContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout)
		throws Exception {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			TransferHeadersHelperUtil.getTransferHeadersRequestDispatcher(
				servletContext.getRequestDispatcher(getViewPage()));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		ServletResponse servletResponse = createServletResponse(
			httpServletResponse, unsyncStringWriter);

		String contentType = servletResponse.getContentType();

		String includeServletPath = (String)httpServletRequest.getAttribute(
			RequestDispatcher.INCLUDE_SERVLET_PATH);

		try {
			addAttributes(httpServletRequest);

			requestDispatcher.include(httpServletRequest, servletResponse);
		}
		finally {
			removeAttributes(httpServletRequest);

			httpServletRequest.setAttribute(
				RequestDispatcher.INCLUDE_SERVLET_PATH, includeServletPath);
		}

		if (contentType != null) {
			httpServletResponse.setContentType(contentType);
		}

		httpServletRequest.setAttribute(
			WebKeys.LAYOUT_CONTENT, unsyncStringWriter.getStringBundler());

		return false;
	}

	@Override
	public boolean isBrowsable() {
		return true;
	}

	@Override
	public boolean isCheckLayoutViewPermission() {
		return true;
	}

	@Override
	public boolean isFullPageDisplayable() {
		return false;
	}

	@Override
	public boolean isInstanceable() {
		return true;
	}

	@Override
	public boolean matches(
		HttpServletRequest httpServletRequest, String friendlyURL,
		Layout layout) {

		try {
			Map<Locale, String> friendlyURLMap = layout.getFriendlyURLMap();

			Set<Locale> locales = LanguageUtil.getAvailableLocales(
				layout.getGroupId());

			for (Locale locale : locales) {
				if (friendlyURL.equals(friendlyURLMap.get(locale))) {
					return true;
				}
			}
		}
		catch (SystemException systemException) {
			throw new RuntimeException(systemException);
		}

		return false;
	}

	protected void addAttributes(HttpServletRequest httpServletRequest) {
	}

	protected abstract ServletResponse createServletResponse(
		HttpServletResponse httpServletResponse,
		UnsyncStringWriter unsyncStringWriter);

	protected abstract String getEditPage();

	protected abstract ServletContext getServletContext();

	protected abstract String getViewPage();

	protected void removeAttributes(HttpServletRequest httpServletRequest) {
	}

}