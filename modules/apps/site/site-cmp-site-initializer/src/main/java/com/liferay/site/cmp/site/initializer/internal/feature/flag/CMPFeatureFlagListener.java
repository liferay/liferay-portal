/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.feature.flag;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.site.cmp.site.initializer.internal.util.SiteInitializerUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Abelenda
 */
@Component(
	property = "feature.flag.key=LPD-58677", service = FeatureFlagListener.class
)
public class CMPFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!enabled || !Objects.equals(featureFlagKey, "LPD-58677") ||
			!LicenseManagerUtil.isAppEnabled(App.CMP)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			_groupLocalService.checkSystemGroups(companyId);

			SiteInitializerUtil.initialize(
				_cmpSiteInitializer, _cmsSiteInitializer, companyId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMPFeatureFlagListener.class);

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.cmp)"
	)
	private SiteInitializer _cmpSiteInitializer;

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.cms)"
	)
	private SiteInitializer _cmsSiteInitializer;

	@Reference
	private GroupLocalService _groupLocalService;

}