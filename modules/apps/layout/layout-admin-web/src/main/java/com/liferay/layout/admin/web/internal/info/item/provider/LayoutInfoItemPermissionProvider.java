/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.provider;

import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.layout.admin.web.internal.info.item.helper.LayoutInfoItemPermissionProviderHelper;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = InfoItemPermissionProvider.class)
public class LayoutInfoItemPermissionProvider
	implements InfoItemPermissionProvider<Layout> {

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			InfoItemReference infoItemReference, String actionId)
		throws InfoItemPermissionException {

		return _layoutInfoItemPermissionProviderHelper.hasPermission(
			permissionChecker, infoItemReference, actionId);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, Layout layout, String actionId)
		throws InfoItemPermissionException {

		return _layoutInfoItemPermissionProviderHelper.hasPermission(
			permissionChecker, layout, actionId);
	}

	@Activate
	protected void activate() {
		_layoutInfoItemPermissionProviderHelper =
			new LayoutInfoItemPermissionProviderHelper(
				_layoutModelResourcePermission);
	}

	private volatile LayoutInfoItemPermissionProviderHelper
		_layoutInfoItemPermissionProviderHelper;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Layout)"
	)
	private ModelResourcePermission<Layout> _layoutModelResourcePermission;

}