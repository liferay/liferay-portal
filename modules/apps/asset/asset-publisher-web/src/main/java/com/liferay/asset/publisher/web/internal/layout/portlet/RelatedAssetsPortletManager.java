/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.layout.portlet;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.layout.portlet.PortletManager;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.RELATED_ASSETS,
	service = PortletManager.class
)
public class RelatedAssetsPortletManager implements PortletManager {

	@Override
	public boolean isVisible(Layout layout) {
		return FeatureFlagManagerUtil.isEnabled(
			layout.getCompanyId(), "LPD-39304");
	}

}