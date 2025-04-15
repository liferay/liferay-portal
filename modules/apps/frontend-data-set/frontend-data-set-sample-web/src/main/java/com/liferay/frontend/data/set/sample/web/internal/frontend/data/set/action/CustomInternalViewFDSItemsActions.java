/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.CUSTOM_INTERNAL_VIEW,
	service = FDSItemsActions.class
)
public class CustomInternalViewFDSItemsActions implements FDSItemsActions {

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return Arrays.asList(
			new FDSActionDropdownItem(
				null, null, null, "#", "code", "openCode",
				_language.get(httpServletRequest, "code"), null, null, null,
				null, null, "link", null, "item"),
			new FDSActionDropdownItem(
				null, null, null, "#", "document", "openDocument",
				_language.get(httpServletRequest, "document"), null, null, null,
				null, null, "link", null, "item"),
			new FDSActionDropdownItem(
				null, null, null, "#", "cog", "turnGreen", "Turn Green", null,
				null, null, null, null, "link", null, "item"));
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.GROUP_PROXY;
	}

	@Reference
	private Language _language;

}