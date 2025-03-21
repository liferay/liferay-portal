/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.display.context;

import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.util.CPSubscriptionType;
import com.liferay.commerce.product.util.CPSubscriptionTypeJSPContributor;
import com.liferay.commerce.product.util.CPSubscriptionTypeJSPContributorRegistry;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Alec Sloan
 */
public class BaseCPDefinitionSubscriptionInfoDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public BaseCPDefinitionSubscriptionInfoDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CPSubscriptionTypeJSPContributorRegistry
			cpSubscriptionTypeJSPContributorRegistry,
		CPSubscriptionTypeRegistry cpSubscriptionTypeRegistry) {

		super(actionHelper, httpServletRequest);

		_cpSubscriptionTypeJSPContributorRegistry =
			cpSubscriptionTypeJSPContributorRegistry;
		_cpSubscriptionTypeRegistry = cpSubscriptionTypeRegistry;
	}

	public CPSubscriptionType getCPSubscriptionType(String subscriptionType) {
		if (Validator.isNull(subscriptionType)) {
			List<CPSubscriptionType> cpSubscriptionTypes =
				getCPSubscriptionTypes();

			return cpSubscriptionTypes.get(0);
		}

		return _cpSubscriptionTypeRegistry.getCPSubscriptionType(
			subscriptionType);
	}

	public CPSubscriptionTypeJSPContributor getCPSubscriptionTypeJSPContributor(
		String subscriptionType) {

		return _cpSubscriptionTypeJSPContributorRegistry.
			getCPSubscriptionTypeJSPContributor(subscriptionType);
	}

	public String getCPSubscriptionTypeLabel(
		int subscriptionLength, CPSubscriptionType cpSubscriptionType,
		Locale locale) {

		String cpSubscriptionTypeLabel = cpSubscriptionType.getLabel(locale);

		if (subscriptionLength > 1) {
			return LanguageUtil.get(
				locale,
				StringUtil.toLowerCase(
					cpSubscriptionType.getLabel(LocaleUtil.US) +
						CharPool.LOWER_CASE_S));
		}

		return LanguageUtil.get(httpServletRequest, cpSubscriptionTypeLabel);
	}

	public List<CPSubscriptionType> getCPSubscriptionTypes() {
		return _cpSubscriptionTypeRegistry.getCPSubscriptionTypes();
	}

	private final CPSubscriptionTypeJSPContributorRegistry
		_cpSubscriptionTypeJSPContributorRegistry;
	private final CPSubscriptionTypeRegistry _cpSubscriptionTypeRegistry;

}