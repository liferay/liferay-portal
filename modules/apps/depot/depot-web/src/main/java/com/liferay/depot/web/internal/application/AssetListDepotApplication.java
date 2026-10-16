/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.application;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.depot.application.DepotApplication;
import com.liferay.depot.constants.DepotConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(service = DepotApplication.class)
public class AssetListDepotApplication implements DepotApplication {

	@Override
	public String getPortletId() {
		return AssetListPortletKeys.ASSET_LIST;
	}

	@Override
	public boolean isCustomizable() {
		return true;
	}

	@Override
	public boolean isDepotTypeSupported(int depotType) {
		if (depotType != DepotConstants.TYPE_DESIGN_LIBRARY) {
			return true;
		}

		return false;
	}

}