/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.instance.lifecycle;

import com.liferay.digital.sales.room.constants.DSRFragmentRendererConstants;
import com.liferay.digital.sales.room.web.internal.constants.DSRConstants;
import com.liferay.digital.sales.room.web.internal.constants.DSRSiteInitializerConstants;
import com.liferay.digital.sales.room.web.internal.util.SiteInitializerUtil;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.site.initializer.SiteInitializer;

import jakarta.servlet.ServletContext;

import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = PortalInstanceLifecycleListener.class
)
public class DSRInitialRequestPortalInstanceLifecycleListener
	extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		if (!LicenseManagerUtil.isAppEnabled(App.DSR)) {
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

			Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, DSRConstants.DSR_HOME_FRIENDLY_URL);

			if ((layout != null) &&
				ListUtil.exists(
					_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
						layout.getGroupId(), layout.getPlid()),
					fragmentEntryLink -> Objects.equals(
						fragmentEntryLink.getRendererKey(),
						DSRFragmentRendererConstants.
							FRAGMENT_RENDERER_KEY_DSR_VIEW_ROOMS))) {

				return;
			}

			SiteInitializerUtil.initialize(companyId, group, _siteInitializer);
		}
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=" + DSRSiteInitializerConstants.BUNDLE_SYMBOLIC_NAME + ")"
	)
	private ServletContext _servletContext;

	@Reference(
		target = "(site.initializer.key=com.liferay.digital.sales.room.site.initializer)"
	)
	private SiteInitializer _siteInitializer;

	@Reference
	private UserLocalService _userLocalService;

}