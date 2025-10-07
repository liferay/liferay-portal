/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.feature.flag;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.WebAppPool;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "feature.flag.key=LPS-196935",
	service = FeatureFlagListener.class
)
public class DDLFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		PortletCategory portletCategory = (PortletCategory)WebAppPool.get(
			companyId, WebKeys.PORTLET_CATEGORY);

		if (portletCategory == null) {
			return;
		}

		Portlet portlet = _portletLocalService.fetchPortletById(
			companyId, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY);

		if (portlet == null) {
			return;
		}

		String categoryName = "category.hidden";

		if (enabled) {
			categoryName = "category.collaboration";
		}

		try {
			_portletLocalService.deployRemotePortlet(
				new long[] {companyId}, portlet, new String[] {categoryName},
				false, false);

			_portletLocalService.clearCache();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDLFeatureFlagListener.class);

	@Reference
	private PortletLocalService _portletLocalService;

}