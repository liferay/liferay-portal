/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.action;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.portal.kernel.portlet.ConfigurationAction;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.RECENT_CONTENT,
	service = ConfigurationAction.class
)
public class RecentContentConfigurationAction
	extends AssetPublisherConfigurationAction {

	@Override
	protected String getDefaultSelectionStyle() {
		return AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC;
	}

}