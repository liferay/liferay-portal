/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.web.internal.health.status;

import com.liferay.commerce.constants.CommerceHealthStatusConstants;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.wish.list.constants.CommerceWishListPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"commerce.health.status.display.order:Integer=20",
		"commerce.health.status.key=" + WishListContentCommerceHealthStatus.KEY
	},
	service = CommerceHealthStatus.class
)
public class WishListContentCommerceHealthStatus
	implements CommerceHealthStatus {

	public static final String KEY = "wish-list-content";

	@Override
	public void fixIssue(HttpServletRequest httpServletRequest)
		throws PortalException {

		long groupId = _portal.getScopeGroupId(httpServletRequest);

		if (isFixed(_portal.getCompanyId(httpServletRequest), groupId)) {
			return;
		}

		boolean privateLayout = true;

		List<Layout> layouts = _layoutService.getLayouts(groupId, true);

		if (layouts.isEmpty()) {
			privateLayout = false;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Layout.class.getName(), httpServletRequest);

		Layout layout = _layoutService.addLayout(
			null, groupId, privateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Wish List", "Wish List",
			null, LayoutConstants.TYPE_PORTLET, true, "/wishlist",
			serviceContext);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.addPortletId(
			_portal.getUserId(httpServletRequest),
			CommerceWishListPortletKeys.COMMERCE_WISH_LIST_CONTENT);

		_layoutService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	@Override
	public String getDescription(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle, "wish-list-content-health-status-description");
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle, "wish-list-content-health-status-name");
	}

	@Override
	public int getType() {
		return CommerceHealthStatusConstants.
			COMMERCE_HEALTH_STATUS_TYPE_GROUP_INSTANCE;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public boolean isFixed(long companyId, long commerceChannelId)
		throws PortalException {

		long plid = _portal.getPlidFromPortletId(
			commerceChannelId,
			CommerceWishListPortletKeys.COMMERCE_WISH_LIST_CONTENT);

		if (plid > 0) {
			return true;
		}

		return false;
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

}