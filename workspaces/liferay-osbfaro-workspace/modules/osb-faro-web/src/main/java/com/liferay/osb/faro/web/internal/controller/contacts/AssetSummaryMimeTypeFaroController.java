/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.AssetSummaryMimeType;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AssetSummaryMimeTypeDisplay;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(service = AssetSummaryMimeTypeFaroController.class)
@Path("/{groupId}/asset-summary-mime-types")
@Produces(MediaType.APPLICATION_JSON)
public class AssetSummaryMimeTypeFaroController extends BaseFaroController {

	@GET
	public FaroFDSResultsDisplay<AssetSummaryMimeType>
			getAssetSummaryMimeTypesFaroFDSResultsDisplay(
				@PathParam("groupId") long groupId,
				@QueryParam("accountId") String accountId,
				@QueryParam("channelId") long channelId,
				@QueryParam("individualId") String individualId,
				@QueryParam("page") int page,
				@DefaultValue("20") @QueryParam("pageSize") int pageSize,
				@QueryParam("rangeEnd") String rangeEnd,
				@DefaultValue("30") @QueryParam("rangeKey") int rangeKey,
				@QueryParam("rangeStart") String rangeStart)
		throws Exception {

		return new FaroFDSResultsDisplay<>(
			contactsEngineClient.getAssetSummaryMimeTypes(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				accountId, channelId, individualId, rangeEnd, rangeKey,
				rangeStart, page, pageSize),
			AssetSummaryMimeTypeDisplay::new, page, pageSize);
	}

}