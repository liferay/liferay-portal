/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.application;

import com.liferay.depot.application.DepotApplication;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = DepotApplication.class)
public class DLDepotApplication implements DepotApplication {

	@Override
	public List<String> getClassNames() {
		return Arrays.asList(
			DLFileEntry.class.getName(), DLFolder.class.getName(),
			FileEntry.class.getName());
	}

	@Override
	public String getPortletId() {
		return DLPortletKeys.DOCUMENT_LIBRARY_ADMIN;
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