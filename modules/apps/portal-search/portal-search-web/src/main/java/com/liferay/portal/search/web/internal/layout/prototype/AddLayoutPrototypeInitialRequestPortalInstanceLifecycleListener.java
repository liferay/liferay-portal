/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.layout.prototype;

import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Localization;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 * @author Lino Alves
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddLayoutPrototypeInitialRequestPortalInstanceLifecycleListener
	extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);

		_searchLayoutFactory = new SearchLayoutFactory(
			_groupLocalService, _layoutLocalService,
			_layoutPrototypeLocalService, _localization, _userLocalService);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		Layout layout = _searchLayoutFactory.createSearchLayoutPrototype(
			companyId);

		if (layout == null) {
			return;
		}

		Group guestGroup = _groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		try {
			MergeLayoutPrototypesThreadLocal.setInProgress(true);

			_searchLayoutFactory.createSearchLayout(guestGroup);
		}
		finally {
			MergeLayoutPrototypesThreadLocal.setInProgress(false);
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private Localization _localization;

	private SearchLayoutFactory _searchLayoutFactory;

	@Reference
	private UserLocalService _userLocalService;

}