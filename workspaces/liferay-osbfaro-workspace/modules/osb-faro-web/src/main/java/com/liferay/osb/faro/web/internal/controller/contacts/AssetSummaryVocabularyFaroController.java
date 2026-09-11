/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.AssetSummaryVocabulary;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AssetSummaryVocabularyDisplay;
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
 * @author Thiago Buarque
 */
@Component(service = AssetSummaryVocabularyFaroController.class)
@Path("/{groupId}/asset-summary-vocabularies")
@Produces(MediaType.APPLICATION_JSON)
public class AssetSummaryVocabularyFaroController extends BaseFaroController {

	@GET
	public FaroFDSResultsDisplay<AssetSummaryVocabulary>
			getAssetSummaryVocabulariesFaroFDSResultsDisplay(
				@PathParam("groupId") long groupId,
				@QueryParam("accountId") String accountId,
				@QueryParam("channelId") long channelId,
				@QueryParam("individualId") String individualId,
				@QueryParam("keywords") String keywords,
				@QueryParam("page") int page,
				@DefaultValue("20") @QueryParam("pageSize") int pageSize,
				@QueryParam("rangeEnd") String rangeEnd,
				@DefaultValue("90") @QueryParam("rangeKey") int rangeKey,
				@QueryParam("rangeStart") String rangeStart,
				@DefaultValue(StringPool.BLANK) @QueryParam("sort") String
					sortString)
		throws Exception {

		return new FaroFDSResultsDisplay<>(
			contactsEngineClient.getAssetSummaryVocabularies(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				accountId, channelId, individualId, keywords, rangeEnd,
				rangeKey, rangeStart, sortString, page, pageSize),
			AssetSummaryVocabularyDisplay::new, page, pageSize);
	}

}