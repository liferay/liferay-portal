/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.provider;

import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.Language;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;
import com.liferay.site.pim.site.initializer.internal.frontend.data.set.model.PIMConnectorChannelFieldDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "fds.data.provider.key=" + PIMFDSNames.FIELD_MAPPINGS,
	service = FDSActionProvider.class
)
public class PIMConnectorChannelFieldFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
		long groupId, HttpServletRequest httpServletRequest, Object model) {

		PIMConnectorChannelFieldDisplay pimConnectorChannelFieldDisplay =
			(PIMConnectorChannelFieldDisplay)model;

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("id", "edit");
				dropdownItem.setHref(pimConnectorChannelFieldDisplay.getHref());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.put("className", "text-danger");
				dropdownItem.putData(
					"apiURL", pimConnectorChannelFieldDisplay.getAPIURL());
				dropdownItem.putData(
					"confirmationMessage",
					_language.get(
						httpServletRequest,
						"are-you-sure-you-want-to-clear-this-mapping"));
				dropdownItem.putData("id", "clear");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "clear"));
			}
		).build();
	}

	@Reference
	private Language _language;

}