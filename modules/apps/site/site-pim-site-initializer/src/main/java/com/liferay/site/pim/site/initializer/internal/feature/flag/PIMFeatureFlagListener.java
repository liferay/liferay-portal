/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.feature.flag;

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
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.pim.site.initializer.internal.util.PIMObjectEntryFolderUtil;
import com.liferay.site.pim.site.initializer.internal.util.SiteInitializerUtil;

/**
 * @author Stefano Motta
 */
public class PIMFeatureFlagListener implements FeatureFlagListener {

	public PIMFeatureFlagListener(
		SiteInitializer cmsSiteInitializer,
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		SiteInitializer pimSiteInitializer,
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService) {

		_cmsSiteInitializer = cmsSiteInitializer;
		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_pimSiteInitializer = pimSiteInitializer;
		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
	}

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!enabled) {
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

			SiteNavigationMenuItem siteNavigationMenuItem =
				_siteNavigationMenuItemLocalService.
					fetchSiteNavigationMenuItemByExternalReferenceCode(
						"PIMPRIMARYNAVIGATIONCONNECTORS", group.getGroupId());

			if (siteNavigationMenuItem == null) {
				SiteInitializerUtil.initialize(
					companyId, _cmsSiteInitializer, _pimSiteInitializer);
			}

			PIMObjectEntryFolderUtil.addProductsObjectEntryFolders(
				companyId, _depotEntryLocalService,
				_objectEntryFolderLocalService);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PIMFeatureFlagListener.class);

	private final SiteInitializer _cmsSiteInitializer;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final SiteInitializer _pimSiteInitializer;
	private final SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}