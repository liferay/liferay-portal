/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.term.entry.type.CommerceTermEntryTypeRegistry;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryRelService;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.commerce.term.web.internal.display.context.CommerceTermEntryQualifiersDisplayContext;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommerceTermEntryQualifiersScreenNavigationEntry
	extends CommerceTermEntryQualifiersScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceTermEntry> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommerceTermEntry commerceTermEntry) {
		if (commerceTermEntry == null) {
			return false;
		}

		boolean hasPermission = false;

		try {
			hasPermission = _commerceTermEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commerceTermEntry.getCommerceTermEntryId(), ActionKeys.UPDATE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return hasPermission;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CommerceTermEntryQualifiersDisplayContext
			commerceTermEntryQualifiersDisplayContext =
				new CommerceTermEntryQualifiersDisplayContext(
					_commerceTermEntryModelResourcePermission,
					_commerceTermEntryRelService, _commerceTermEntryService,
					_commerceTermEntryTypeRegistry, httpServletRequest,
					_portal);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commerceTermEntryQualifiersDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/commerce_term_entry/qualifiers.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTermEntryQualifiersScreenNavigationEntry.class);

	@Reference(
		target = "(model.class.name=com.liferay.commerce.term.model.CommerceTermEntry)"
	)
	private ModelResourcePermission<CommerceTermEntry>
		_commerceTermEntryModelResourcePermission;

	@Reference
	private CommerceTermEntryRelService _commerceTermEntryRelService;

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

	@Reference
	private CommerceTermEntryTypeRegistry _commerceTermEntryTypeRegistry;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

}