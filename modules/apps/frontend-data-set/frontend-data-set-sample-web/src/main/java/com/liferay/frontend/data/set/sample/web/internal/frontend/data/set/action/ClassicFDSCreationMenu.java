/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.CLASSIC,
	service = FDSCreationMenu.class
)
public class ClassicFDSCreationMenu implements FDSCreationMenu {

	@Override
	public CreationMenu getCreationMenu(HttpServletRequest httpServletRequest) {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			DropdownItemBuilder.putData(
				"confirmationMessage",
				_language.get(httpServletRequest, "are-you-sure")
			).putData(
				"confirmationMessageType", "warning"
			).putData(
				"id", "openCalendar"
			).putData(
				"modalSize", "full-screen"
			).putData(
				"title", _language.get(httpServletRequest, "calendar")
			).setHref(
				"#"
			).setIcon(
				"calendar"
			).setLabel(
				_language.get(httpServletRequest, "calendar")
			).setTarget(
				"modal"
			).build()
		).addPrimaryDropdownItem(
			DropdownItemBuilder.putData(
				"modalSize", "full-screen"
			).putData(
				"title", _language.get(httpServletRequest, "blogs")
			).setHref(
				"#"
			).setIcon(
				"blogs"
			).setLabel(
				_language.get(httpServletRequest, "blogs")
			).setTarget(
				"modal"
			).build()
		).build();
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
	}

	@Reference
	private Language _language;

}