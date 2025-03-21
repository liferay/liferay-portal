/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.display.context;

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPInstanceScreenNavigationConstants;
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
public class CPInstanceSubscriptionInfoDisplayContext
	extends BaseCPDefinitionSubscriptionInfoDisplayContext {

	public CPInstanceSubscriptionInfoDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CPSubscriptionTypeJSPContributorRegistry
			cpSubscriptionTypeJSPContributorRegistry,
		CPSubscriptionTypeRegistry cpSubscriptionTypeRegistry) {

		super(
			actionHelper, httpServletRequest,
			cpSubscriptionTypeJSPContributorRegistry,
			cpSubscriptionTypeRegistry);
	}

	public CPInstance getCPInstance() throws PortalException {
		return actionHelper.getCPInstance(cpRequestHelper.getRenderRequest());
	}

	public long getCPInstanceId() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		if (cpInstance == null) {
			return 0;
		}

		return cpInstance.getCPInstanceId();
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance"
		).setParameter(
			"cpDefinitionId", getCPDefinitionId()
		).setParameter(
			"cpInstanceId", getCPInstanceId()
		).setParameter(
			"screenNavigationCategoryKey",
			CPInstanceScreenNavigationConstants.
				CATEGORY_KEY_SUBSCRIPTION_OVERRIDE
		).setParameter(
			"screenNavigationEntryKey",
			CPInstanceScreenNavigationConstants.
				CATEGORY_KEY_SUBSCRIPTION_OVERRIDE
		).buildPortletURL();
	}

}