/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.application;

import com.liferay.depot.application.DepotApplication;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = DepotApplication.class)
public class JournalDepotApplication implements DepotApplication {

	@Override
	public List<String> getClassNames() {
		return Arrays.asList(
			JournalArticle.class.getName(), JournalFolder.class.getName());
	}

	@Override
	public String getPortletId() {
		return JournalPortletKeys.JOURNAL;
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