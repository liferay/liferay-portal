/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.provider;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = GroupItemSelectorProvider.class)
public class DesignLibraryDepotGroupItemSelectorProvider
	extends BaseDepotGroupItemSelectorProvider {

	@Override
	public String getGroupType() {
		return "design-library";
	}

	@Override
	public boolean isEnabled() {
		return FeatureFlagManagerUtil.isEnabled(
			CompanyThreadLocal.getNonsystemCompanyId(), "LPD-57283");
	}

	@Override
	protected int getDepotEntryType() {
		return DepotConstants.TYPE_DESIGN_LIBRARY;
	}

	@Override
	protected String getEmptyResultsMessageKey() {
		return "no-design-libraries-were-found";
	}

	@Override
	protected String getLabelKey() {
		return "design-libraries";
	}

}