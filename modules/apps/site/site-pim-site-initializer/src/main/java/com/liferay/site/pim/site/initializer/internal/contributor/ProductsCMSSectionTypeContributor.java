/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.site.cms.site.initializer.contributor.CMSSectionTypeContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stefano Motta
 */
@Component(service = CMSSectionTypeContributor.class)
public class ProductsCMSSectionTypeContributor
	implements CMSSectionTypeContributor {

	@Override
	public String getCMSSectionType() {
		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-96666")) {

			return null;
		}

		return "products";
	}

}