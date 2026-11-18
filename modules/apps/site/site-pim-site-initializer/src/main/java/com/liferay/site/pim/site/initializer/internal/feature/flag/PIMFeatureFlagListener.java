/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.feature.flag;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.InitialRequestSyncUtil;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.pim.site.initializer.internal.util.PIMObjectEntryFolderUtil;
import com.liferay.site.pim.site.initializer.internal.util.SiteInitializerUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "feature.flag.key=LPD-96666", service = FeatureFlagListener.class
)
public class PIMFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!enabled || !Objects.equals(featureFlagKey, "LPD-96666")) {
			return;
		}

		// On startup the feature flag manager runs as soon as this listener is
		// registered. Sync on the initial request, the same phase the CMS site
		// initializer runs in.

		InitialRequestSyncUtil.registerSyncCallable(
			new CompanyInheritableThreadLocalCallable<>(
				() -> {
					_initialize(companyId);

					return null;
				}));
	}

	private void _initialize(long companyId) {
		Group group = _groupLocalService.fetchGroup(
			companyId, GroupConstants.CMS);

		if (group == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			_groupLocalService.checkSystemGroups(companyId);

			// The PIM site initializer appends to the pages and the primary
			// navigation menu the CMS one creates, so run that one first.

			com.liferay.site.cms.site.initializer.util.SiteInitializerUtil.
				initialize(companyId, _cmsSiteInitializer);

			SiteInitializerUtil.initialize(companyId, _pimSiteInitializer);

			for (DepotEntry depotEntry :
					_depotEntryLocalService.getDepotEntries(
						companyId, DepotConstants.TYPE_SPACE)) {

				PIMObjectEntryFolderUtil.getOrAddProductsObjectEntryFolder(
					depotEntry.getGroup(), _objectEntryFolderLocalService);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PIMFeatureFlagListener.class);

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.cms)"
	)
	private SiteInitializer _cmsSiteInitializer;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.pim)"
	)
	private SiteInitializer _pimSiteInitializer;

}