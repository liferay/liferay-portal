/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.display.context;

import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.product.util.CPSubscriptionTypeJSPContributorRegistry;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 * @author Alec Sloan
 */
public class CPDefinitionSubscriptionInfoDisplayContext
	extends BaseCPDefinitionSubscriptionInfoDisplayContext {

	public CPDefinitionSubscriptionInfoDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CPSubscriptionTypeJSPContributorRegistry
			cpSubscriptionTypeJSPContributorRegistry,
		CPSubscriptionTypeRegistry cpSubscriptionTypeRegistry) {

		super(
			actionHelper, httpServletRequest,
			cpSubscriptionTypeJSPContributorRegistry,
			cpSubscriptionTypeRegistry);
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"screenNavigationCategoryKey",
			getSelectedScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	@Override
	public String getSelectedScreenNavigationCategoryKey() {
		return CPDefinitionScreenNavigationConstants.CATEGORY_KEY_SUBSCRIPTION;
	}

}