/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.collection.contributor.account.selector;

import com.liferay.fragment.contributor.BaseFragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fragment.collection.key=COMMERCE_ACCOUNT_SELECTOR_FRAGMENTS",
	service = FragmentCollectionContributor.class
)
public class CommerceAccountSelectorFragmentCollectionContributor
	extends BaseFragmentCollectionContributor {

	@Override
	public String getFragmentCollectionKey() {
		return "COMMERCE_ACCOUNT_SELECTOR_FRAGMENTS";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isEnabled() {
		return FeatureFlagManagerUtil.isEnabled(
			CompanyThreadLocal.getCompanyId(), "LPD-58472");
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.fragment.collection.contributor.account.selector)"
	)
	private ServletContext _servletContext;

}