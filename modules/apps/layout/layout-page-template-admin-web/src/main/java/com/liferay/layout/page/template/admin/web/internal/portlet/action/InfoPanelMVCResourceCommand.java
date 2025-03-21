/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yurena Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/info_panel"
	},
	service = MVCResourceCommand.class
)
public class InfoPanelMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		resourceRequest.setAttribute(
			LayoutPageTemplateAdminWebKeys.LAYOUT_PAGE_TEMPLATE_COLLECTIONS,
			_getLayoutPageTemplateCollections(resourceRequest));
		resourceRequest.setAttribute(
			LayoutPageTemplateAdminWebKeys.LAYOUT_PAGE_TEMPLATE_ENTRIES,
			_getLayoutPageTemplateEntries(resourceRequest));
		resourceRequest.setAttribute(
			InfoItemServiceRegistry.class.getName(), _infoItemServiceRegistry);

		include(resourceRequest, resourceResponse, "/info_panel.jsp");
	}

	private List<LayoutPageTemplateCollection>
			_getLayoutPageTemplateCollections(ResourceRequest resourceRequest)
		throws Exception {

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
			new ArrayList<>();

		long[] layoutPageTemplateCollectionIds = ParamUtil.getLongValues(
			resourceRequest, "rowIdsLayoutPageTemplateCollection");

		for (long layoutPageTemplateCollectionId :
				layoutPageTemplateCollectionIds) {

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						layoutPageTemplateCollectionId);

			if (layoutPageTemplateCollection != null) {
				layoutPageTemplateCollections.add(layoutPageTemplateCollection);
			}
		}

		return layoutPageTemplateCollections;
	}

	private List<LayoutPageTemplateEntry> _getLayoutPageTemplateEntries(
			ResourceRequest resourceRequest)
		throws Exception {

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			new ArrayList<>();

		long[] layoutPageTemplateEntryIds = ParamUtil.getLongValues(
			resourceRequest, "rowIds");

		for (long layoutPageTemplateEntryId : layoutPageTemplateEntryIds) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
					layoutPageTemplateEntryId);

			if (layoutPageTemplateEntry != null) {
				layoutPageTemplateEntries.add(layoutPageTemplateEntry);
			}
		}

		return layoutPageTemplateEntries;
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

}