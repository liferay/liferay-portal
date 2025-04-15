/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.product.navigation.personal.menu;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.product.navigation.personal.menu.BasePersonalMenuEntry;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.sharing.web.internal.constants.SharingPortletKeys;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"product.navigation.personal.menu.entry.order:Integer=300",
		"product.navigation.personal.menu.group:Integer=200"
	},
	service = PersonalMenuEntry.class
)
public class SharedAssetsPersonalMenuEntry extends BasePersonalMenuEntry {

	@Override
	public String getPortletId() {
		return SharingPortletKeys.SHARED_ASSETS;
	}

	@Override
	public boolean isShow(
			PortletRequest portletRequest, PermissionChecker permissionChecker)
		throws PortalException {

		SharingConfiguration companySharingConfiguration =
			_sharingConfigurationFactory.getCompanySharingConfiguration(
				_portal.getCompany(portletRequest));

		if (!companySharingConfiguration.isEnabled()) {
			return false;
		}

		return super.isShow(portletRequest, permissionChecker);
	}

	@Reference
	private Portal _portal;

	@Reference
	private SharingConfigurationFactory _sharingConfigurationFactory;

}