/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.options.web.internal.constants.CommerceSpecificationOptionFDSNames;
import com.liferay.commerce.product.options.web.internal.model.ListTypeDefinition;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "fds.data.provider.key=" + CommerceSpecificationOptionFDSNames.LIST_TYPE_DEFINITIONS,
	service = FDSActionProvider.class
)
public class CommerceSpecificationOptionListTypeDefinitionFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
		long groupId, HttpServletRequest httpServletRequest, Object model) {

		ListTypeDefinition listTypeDefinition = (ListTypeDefinition)model;

		return Arrays.asList(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						httpServletRequest,
						ObjectPortletKeys.LIST_TYPE_DEFINITIONS,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/list_type_definitions/edit_list_type_definition"
				).setParameter(
					"listTypeDefinitionId",
					listTypeDefinition.getListTypeDefinitionId()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"pencil", "edit",
				_language.get(httpServletRequest, Constants.EDIT), "get", null,
				"sidePanel"),
			new FDSActionDropdownItem(
				null, "trash", "removePicklistRelation",
				_language.get(httpServletRequest, Constants.REMOVE), null, null,
				null));
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}