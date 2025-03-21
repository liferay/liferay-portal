/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeJSPContributorRegistry;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeRegistry;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryService;
import com.liferay.commerce.order.rule.web.internal.display.context.COREntryDisplayContext;
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
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class COREntryDetailsScreenNavigationEntry
	extends COREntryDetailsScreenNavigationCategory
	implements ScreenNavigationEntry<COREntry> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, COREntry corEntry) {
		if (corEntry == null) {
			return false;
		}

		boolean hasPermission = false;

		try {
			hasPermission = _corEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				corEntry.getCOREntryId(), ActionKeys.UPDATE);
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

		COREntryDisplayContext corEntryDisplayContext =
			new COREntryDisplayContext(
				_commerceCurrencyService, _corEntryModelResourcePermission,
				_corEntryService, _corEntryTypeJSPContributorRegistry,
				_corEntryTypeRegistry, httpServletRequest, _portal);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, corEntryDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse, "/cor_entry/details.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		COREntryDetailsScreenNavigationEntry.class);

	@Reference
	private CommerceCurrencyService _commerceCurrencyService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.order.rule.model.COREntry)"
	)
	private ModelResourcePermission<COREntry> _corEntryModelResourcePermission;

	@Reference
	private COREntryService _corEntryService;

	@Reference
	private COREntryTypeJSPContributorRegistry
		_corEntryTypeJSPContributorRegistry;

	@Reference
	private COREntryTypeRegistry _corEntryTypeRegistry;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

}