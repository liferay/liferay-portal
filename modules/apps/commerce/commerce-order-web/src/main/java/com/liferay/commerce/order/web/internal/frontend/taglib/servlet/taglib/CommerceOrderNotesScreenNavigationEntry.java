/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.constants.CommerceOrderScreenNavigationConstants;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alec Sloan
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommerceOrderNotesScreenNavigationEntry
	implements ScreenNavigationEntry<CommerceOrder> {

	public static final String KEY = "order-notes";

	@Override
	public String getCategoryKey() {
		return CommerceOrderScreenNavigationConstants.
			CATEGORY_KEY_COMMERCE_ORDER_NOTES;
	}

	@Override
	public String getEntryKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, KEY);
	}

	@Override
	public String getScreenNavigationKey() {
		return CommerceOrderScreenNavigationConstants.
			SCREEN_NAVIGATION_KEY_COMMERCE_ORDER_GENERAL;
	}

	@Override
	public boolean isVisible(User user, CommerceOrder commerceOrder) {
		boolean hasPermission = false;
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try {
			hasPermission =
				_commerceOrderModelResourcePermission.contains(
					permissionChecker, commerceOrder,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES) ||
				_commerceOrderModelResourcePermission.contains(
					permissionChecker, commerceOrder,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES);
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

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/commerce_order/notes.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderNotesScreenNavigationEntry.class);

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

}