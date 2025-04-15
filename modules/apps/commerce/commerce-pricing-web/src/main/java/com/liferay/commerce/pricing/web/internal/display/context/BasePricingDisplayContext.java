/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.pricing.web.internal.display.context.helper.CommercePricingRequestHelper;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Riccardo Alberti
 */
public abstract class BasePricingDisplayContext {

	public BasePricingDisplayContext(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;

		portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			this.httpServletRequest);

		commercePricingRequestHelper = new CommercePricingRequestHelper(
			httpServletRequest);

		liferayPortletRequest =
			commercePricingRequestHelper.getLiferayPortletRequest();
		liferayPortletResponse =
			commercePricingRequestHelper.getLiferayPortletResponse();
	}

	protected final CommercePricingRequestHelper commercePricingRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;
	protected final PortalPreferences portalPreferences;

}