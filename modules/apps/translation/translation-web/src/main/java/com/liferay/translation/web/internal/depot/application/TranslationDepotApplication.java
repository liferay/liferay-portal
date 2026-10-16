/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.depot.application;

import com.liferay.depot.application.DepotApplication;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.translation.constants.TranslationPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(service = DepotApplication.class)
public class TranslationDepotApplication implements DepotApplication {

	@Override
	public String getPortletId() {
		return TranslationPortletKeys.TRANSLATION;
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