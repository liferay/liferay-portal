/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.portlet.action;

import com.liferay.osb.faro.admin.web.internal.constants.FaroAdminPortletKeys;
import com.liferay.osb.faro.admin.web.internal.constants.FaroAdminWebKeys;
import com.liferay.osb.faro.admin.web.internal.model.FaroProjectAdminDisplay;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FaroAdminPortletKeys.FARO_ADMIN,
		"mvc.command.name=/faro_admin/info_panel"
	},
	service = MVCResourceCommand.class
)
public class InfoPanelMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		List<FaroProjectAdminDisplay> faroProjectAdminDisplays =
			new ArrayList<>();

		long[] faroProjectIds = ParamUtil.getLongValues(
			resourceRequest, "rowIds");

		if (faroProjectIds.length == 1) {
			Indexer<FaroProject> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(FaroProject.class);

			SearchContext searchContext = new SearchContext();

			searchContext.setAttribute("faroProjectId", faroProjectIds[0]);

			Hits hits = indexer.search(searchContext);

			List<Document> documents = hits.toList();

			if (!documents.isEmpty()) {
				faroProjectAdminDisplays.add(
					new FaroProjectAdminDisplay(
						_faroProjectLocalService.getFaroProject(
							faroProjectIds[0]),
						documents.get(0)));
			}
		}

		resourceRequest.setAttribute(
			FaroAdminWebKeys.FARO_PROJECT_ENTRIES, faroProjectAdminDisplays);
		resourceRequest.setAttribute(
			FaroAdminWebKeys.FARO_PROJECT_ENTRIES_COUNT, faroProjectIds.length);

		include(resourceRequest, resourceResponse, "/info_panel.jsp");
	}

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}