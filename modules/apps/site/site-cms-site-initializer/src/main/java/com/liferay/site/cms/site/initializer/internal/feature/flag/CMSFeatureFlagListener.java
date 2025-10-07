/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.feature.flag;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.site.cms.site.initializer.internal.util.SiteInitializerUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "feature.flag.key=LPD-17564", service = FeatureFlagListener.class
)
public class CMSFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (enabled && Objects.equals(featureFlagKey, "LPD-17564")) {
			Group group = _groupLocalService.fetchGroup(
				companyId, GroupConstants.CMS);

			if (group != null) {
				return;
			}

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				_groupLocalService.checkSystemGroups(companyId);

				SiteInitializerUtil.initialize(companyId, _siteInitializer);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSFeatureFlagListener.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.cms)"
	)
	private SiteInitializer _siteInitializer;

}