/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.feature.flag;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.site.dsr.site.initializer.internal.constants.DSRConstants;
import com.liferay.site.dsr.site.initializer.internal.util.SiteInitializerUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "feature.flag.key=LPD-66359", service = FeatureFlagListener.class
)
public class DSRFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!enabled || !Objects.equals(featureFlagKey, "LPD-66359")) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			Group group = _groupLocalService.fetchGroup(
				companyId, GroupConstants.DSR);

			if (group == null) {
				group = _groupLocalService.addGroup(
					"L_" + GroupConstants.DSR,
					_userLocalService.getGuestUserId(companyId),
					GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
					GroupConstants.DEFAULT_LIVE_GROUP_ID,
					HashMapBuilder.put(
						LocaleUtil.getDefault(), GroupConstants.DSR
					).build(),
					null, GroupConstants.TYPE_SITE_PRIVATE, null, true,
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
					DSRConstants.DSR_FRIENDLY_URL, false, false, true, null);
			}

			SiteInitializerUtil.initialize(companyId, group, _siteInitializer);

			_addLayoutSetPrototype(companyId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _addLayoutSetPrototype(long companyId) throws PortalException {
		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				fetchLayoutSetPrototypeByUuidAndCompanyId(
					"L_DSR_LAYOUT_SET_PROTOTYPE", companyId);

		if (layoutSetPrototype != null) {
			return;
		}

		User user = _userLocalService.getGuestUser(companyId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute("addDefaultLayout", Boolean.FALSE);
		serviceContext.setUuid("L_DSR_LAYOUT_SET_PROTOTYPE");

		layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				user.getUserId(), companyId,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), GroupConstants.DSR
				).build(),
				null, true, true, serviceContext);

		SiteInitializerUtil.initialize(
			companyId, layoutSetPrototype.getGroup(),
			_layoutSetPrototypeSiteInitializer);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DSRFeatureFlagListener.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.digital.sales.room.site.initializer)"
	)
	private SiteInitializer _layoutSetPrototypeSiteInitializer;

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.dsr)"
	)
	private SiteInitializer _siteInitializer;

	@Reference
	private UserLocalService _userLocalService;

}