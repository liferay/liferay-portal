/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.pricing.web.internal.display.context.CommercePricingClassDisplayContext;
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
public class CommercePricingClassDetailsScreenNavigationEntry
	extends CommercePricingClassDetailsScreenNavigationCategory
	implements ScreenNavigationEntry<CommercePricingClass> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(
		User user, CommercePricingClass commercePricingClass) {

		if (commercePricingClass == null) {
			return false;
		}

		boolean hasPermission = false;

		try {
			hasPermission =
				_commercePricingClassModelResourcePermission.contains(
					PermissionThreadLocal.getPermissionChecker(),
					commercePricingClass.getCommercePricingClassId(),
					ActionKeys.UPDATE);
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

		CommercePricingClassDisplayContext commercePricingClassDisplayContext =
			new CommercePricingClassDisplayContext(
				httpServletRequest,
				_commercePricingClassModelResourcePermission,
				_commercePricingClassService, _portal);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commercePricingClassDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/commerce_pricing_class/details.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePricingClassDetailsScreenNavigationEntry.class);

	@Reference(
		target = "(model.class.name=com.liferay.commerce.pricing.model.CommercePricingClass)"
	)
	private ModelResourcePermission<CommercePricingClass>
		_commercePricingClassModelResourcePermission;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

}