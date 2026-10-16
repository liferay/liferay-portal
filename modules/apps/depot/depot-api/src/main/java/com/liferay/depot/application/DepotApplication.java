/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.application;

import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Alejandro Tardín
 */
public interface DepotApplication {

	public default List<String> getClassNames() {
		return Collections.emptyList();
	}

	public default String getLabel(Locale locale) {
		return PortalUtil.getPortletTitle(getPortletId(), locale);
	}

	public String getPortletId();

	public default boolean isCustomizable() {
		return false;
	}

	public default boolean isDepotTypeSupported(int depotType) {
		return true;
	}

}