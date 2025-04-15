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
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.CLASSIC,
	service = FDSItemsActions.class
)
public class ClassicFDSItemsActions implements FDSItemsActions {

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return Arrays.asList(
			new FDSActionDropdownItem(
				null, null, null, "#", "book", "navigateToLibrary",
				_language.get(httpServletRequest, "book"), null, null, null,
				null, null, "link", null, "item"),
			new FDSActionDropdownItem(
				null, null, null, "#", "archive", "navigateToArchive",
				_language.get(httpServletRequest, "job-archive"), null, null,
				null, null, null, "link", null, "item"),
			new FDSActionDropdownItem(
				null, null, null, "#", "cog", "deactivate",
				_language.get(httpServletRequest, "deactivate"), null, null,
				null, null, null, "link", null, "item",
				HashMapBuilder.<String, Object>put(
					"active", Boolean.TRUE
				).build()),
			new FDSActionDropdownItem(
				null, null, null, "#", "cog", "activate",
				_language.get(httpServletRequest, "activate"), null, null, null,
				null, null, "link", null, "item",
				HashMapBuilder.<String, Object>put(
					"active", Boolean.FALSE
				).build()),
			new FDSActionDropdownItem(
				null, null, null, "#", "cog", "activity",
				_language.get(httpServletRequest, "activity"), null, null, null,
				null, null, "link", null, "item",
				HashMapBuilder.<String, Object>put(
					"active", Boolean.TRUE
				).put(
					"emailAddress", "manager.user@liferay.com"
				).build()));
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
	}

	@Reference
	private Language _language;

}