/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.AssetSummary;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AssetSummaryDisplay;
import com.liferay.petra.string.StringPool;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcos Martins
 */
@Component(service = AssetSummaryFaroController.class)
@Path("/{groupId}/asset-summary")
@Produces(MediaType.APPLICATION_JSON)
public class AssetSummaryFaroController extends BaseFaroController {

	@GET
	public FaroFDSResultsDisplay<AssetSummary>
			getAssetSummaryFaroFDSResultsDisplay(
				@PathParam("groupId") long groupId,
				@QueryParam("channelId") long channelId,
				@QueryParam("filter") String filterString,
				@QueryParam("objectType") String objectType,
				@QueryParam("page") int page,
				@DefaultValue("20") @QueryParam("pageSize") int pageSize,
				@DefaultValue("90") @QueryParam("rangeKey") int rangeKey,
				@QueryParam("search") String search,
				@QueryParam("selectedMetric") String selectedMetric,
				@DefaultValue(StringPool.BLANK) @QueryParam("sort") String
					sortString)
		throws Exception {

		return new FaroFDSResultsDisplay<>(
			contactsEngineClient.getAssetSummaries(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId, filterString, search, objectType, rangeKey,
				selectedMetric, page, pageSize, sortString),
			AssetSummaryDisplay::new, page, pageSize);
	}

}