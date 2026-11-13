/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marko Cikos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
	service = FDSItemsActions.class
)
public class AdvancedFDSItemsActions implements FDSItemsActions {

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		String href = "/o/c/fdssamples/{id}";

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(portletResponse);

		return FDSActionDropdownItemList.of(
			FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
				FDSActionDropdownItemList.of(
					FDSActionDropdownItemBuilder.setHref(
						"#test-visibility-filter"
					).setIcon(
						"sun"
					).setLabel(
						"Sample Visibility Filter"
					).setTarget(
						"link"
					).setVisibilityFilters(
						HashMapBuilder.<String, Object>put(
							"color", "Yellow"
						).build()
					).build(
						"sampleVisibilityFilterMessage"
					),
					FDSActionDropdownItemBuilder.setIcon(
						"view"
					).setLabel(
						"View Details"
					).setTarget(
						"infoPanel"
					).build(
						"infoPanel"
					),
					FDSActionDropdownItemBuilder.setIcon(
						"view"
					).setLabel(
						"Sample View"
					).setTarget(
						"link"
					).build(
						"sampleMessage"
					),
					FDSActionDropdownItemBuilder.setHref(
						"#test-pencil"
					).setIcon(
						"pencil"
					).setLabel(
						"Sample Edit"
					).setTarget(
						"link"
					).build(
						"sampleEditMessage"
					),
					FDSActionDropdownItemBuilder.setHref(
						"#test-delete"
					).setIcon(
						"times-circle"
					).setLabel(
						"Sample Delete"
					).setTarget(
						"link"
					).build(
						"sampleDeleteMessage"
					),
					FDSActionDropdownItemBuilder.setHref(
						"#test-copy"
					).setIcon(
						"copy"
					).setLabel(
						"Sample Copy"
					).setTarget(
						"link"
					).build(
						"sampleMoveFolderMessage"
					),
					FDSActionDropdownItemBuilder.setHref(
						href
					).setIcon(
						"truck"
					).setLabel(
						"Async Success"
					).setMethod(
						"get"
					).setTarget(
						"async"
					).build(
						"asyncSuccess"
					),
					FDSActionDropdownItemBuilder.setHref(
						"http://localhost"
					).setIcon(
						"times-circle"
					).setLabel(
						"Async Connection Refused"
					).setMethod(
						"get"
					).setTarget(
						"async"
					).build(
						"asyncErrorConnectionRefused"
					),
					FDSActionDropdownItemBuilder.setData(
						HashMapBuilder.<String, Object>put(
							"disableHeader", false
						).build()
					).putData(
						"title", "Side Panel Title Provided by Action"
					).setHref(
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCRenderCommandName(
							"/side_panel/empty"
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString()
					).setIcon(
						"rectangle-split"
					).setLabel(
						"Side Panel With Action Title"
					).setTarget(
						"sidePanel"
					).build(
						"open-side-panel-no-title"
					),
					FDSActionDropdownItemBuilder.setData(
						HashMapBuilder.<String, Object>put(
							"disableHeader", false
						).build()
					).putData(
						"title", "Side Panel Title Provided by Action"
					).setHref(
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCRenderCommandName(
							"/side_panel/full"
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString()
					).setIcon(
						"rectangle-split"
					).setLabel(
						"Side Panel With Action and Content Title"
					).setTarget(
						"sidePanel"
					).build(
						"open-side-panel-no-title"
					),
					FDSActionDropdownItemBuilder.setData(
						HashMapBuilder.<String, Object>put(
							"disableHeader", true
						).build()
					).setHref(
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCRenderCommandName(
							"/side_panel/full"
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString()
					).setIcon(
						"rectangle-split"
					).setLabel(
						"Side Panel With Content Title"
					).setTarget(
						"sidePanel"
					).build(
						"open-side-panel-no-title"
					),
					FDSActionDropdownItemBuilder.setHref(
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCRenderCommandName(
							"/side_panel/empty"
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString()
					).setIcon(
						"rectangle-split"
					).setLabel(
						"Side Panel With No Title"
					).setTarget(
						"sidePanel"
					).build(
						"open-side-panel-without-title"
					),
					FDSActionDropdownItemBuilder.setHref(
						href + "/abc"
					).setIcon(
						"staging"
					).setLabel(
						"Async Resource Not Found"
					).setMethod(
						"get"
					).setTarget(
						"async"
					).build(
						"asyncErrorResourceNotFound"
					),
					FDSActionDropdownItemBuilder.setIcon(
						"reload"
					).setLabel(
						"Reload Data"
					).setTarget(
						"link"
					).build(
						"reload"
					),
					FDSActionDropdownItemBuilder.setIcon(
						"rectangle-split"
					).setLabel(
						"Open Side Panel"
					).setTarget(
						"link"
					).build(
						"openSidePanel"
					))
			).setSeparator(
				true
			).setType(
				"group"
			).build(
				"groupItems"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"hidden"
			).setSeparator(
				false
			).setType(
				"group"
			).build(
				"groupPermissionTest"
			),
			FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
				FDSActionDropdownItemList.of(
					FDSActionDropdownItemBuilder.setIcon(
						"separator"
					).setLabel(
						"Group Item"
					).setTarget(
						"link"
					).build(
						"sampleMessage"
					),
					FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
						FDSActionDropdownItemList.of(
							FDSActionDropdownItemBuilder.setIcon(
								"exclamation-circle"
							).setLabel(
								"Contextual Sub Item 1"
							).setTarget(
								"link"
							).build(
								"sampleMessage"
							),
							FDSActionDropdownItemBuilder.setIcon(
								"exclamation-circle"
							).setLabel(
								"Contextual Sub Item 2"
							).setTarget(
								"link"
							).build(
								"sampleMessage"
							))
					).setIcon(
						"nodes"
					).setLabel(
						"Contextual Item"
					).setSeparator(
						false
					).setType(
						"contextual"
					).build(
						"contextualItem"
					))
			).setSeparator(
				true
			).setType(
				"group"
			).build(
				"groupContextualItems"
			));
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
	}

}