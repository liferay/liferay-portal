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
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
	service = FDSCreationMenu.class
)
public class AdvancedFDSCreationMenu implements FDSCreationMenu {

	@Override
	public CreationMenu getCreationMenu(HttpServletRequest httpServletRequest) {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			DropdownItemBuilder.putData(
				"id", "newTask"
			).setHref(
				"#"
			).setIcon(
				"check-circle"
			).setLabel(
				_language.get(httpServletRequest, "new-task")
			).setTarget(
				"link"
			).build()
		).addPrimaryDropdownItem(
			DropdownItemBuilder.putData(
				"id", "formsGroup"
			).setDropdownItems(
				DropdownItemListBuilder.add(
					DropdownItemBuilder.putData(
						"confirmationMessage",
						_language.get(httpServletRequest, "are-you-sure")
					).putData(
						"confirmationMessageType", "warning"
					).putData(
						"modalSize", "full-screen"
					).putData(
						"permissionKey", "update"
					).putData(
						"title",
						_language.get(httpServletRequest, "my-products")
					).setHref(
						"#"
					).setIcon(
						"bolt"
					).setLabel(
						_language.get(httpServletRequest, "open-form")
					).setTarget(
						"modal"
					).build()
				).add(
					DropdownItemBuilder.putData(
						"id", "newForm"
					).setHref(
						"#"
					).setIcon(
						"forms"
					).setLabel(
						_language.get(httpServletRequest, "new-form")
					).setTarget(
						"link"
					).build()
				).build()
			).setLabel(
				_language.get(httpServletRequest, "forms")
			).setSeparator(
				true
			).setType(
				"group"
			).build()
		).addPrimaryDropdownItem(
			DropdownItemBuilder.putData(
				"id", "permissionTest"
			).setDropdownItems(
				DropdownItemListBuilder.add(
					DropdownItemBuilder.putData(
						"id", "newTemplate"
					).putData(
						"permissionKey", "update"
					).setHref(
						"#"
					).setLabel(
						_language.get(httpServletRequest, "new-template")
					).setTarget(
						"link"
					).build()
				).build()
			).setLabel(
				_language.get(httpServletRequest, "templates")
			).setType(
				"contextual"
			).build()
		).build();
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.DETACHED;
	}

	@Reference
	private Language _language;

}