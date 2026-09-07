/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.osb.faro.engine.client.model.Campaign;
import com.liferay.osb.faro.engine.client.model.CampaignMetric;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AccountDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.CampaignDisplay;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Ferrari
 */
@Component(service = CampaignFaroController.class)
@Path("/{groupId}/campaigns")
@Produces(MediaType.APPLICATION_JSON)
public class CampaignFaroController extends BaseFaroController {

	@GET
	@Path("/{id}/accounts")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroFDSResultsDisplay<Account> getAccountsFaroFDSResultsDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("channelId") long channelId,
			@QueryParam("filter") String filterString,
			@QueryParam("page") int page,
			@DefaultValue("20") @QueryParam("pageSize") int pageSize,
			@QueryParam("search") String search,
			@DefaultValue(StringPool.BLANK) @QueryParam("sort") String
				sortString)
		throws Exception {

		return new FaroFDSResultsDisplay<>(
			contactsEngineClient.getCampaignAccounts(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId, filterString, id, search, sortString, page,
				pageSize),
			AccountDisplay::new, page, pageSize);
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public CampaignDisplay getCampaignDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("channelId") long channelId)
		throws Exception {

		return new CampaignDisplay(
			contactsEngineClient.getCampaign(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId, id));
	}

	@GET
	@Path("/metrics")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<CampaignMetric> getCampaignMetrics(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") long channelId)
		throws Exception {

		return contactsEngineClient.getCampaignMetrics(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			channelId);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroFDSResultsDisplay<Campaign> getCampaignsFaroFDSResultsDisplay(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") long channelId,
			@QueryParam("filter") String filterString,
			@QueryParam("page") int page,
			@DefaultValue("20") @QueryParam("pageSize") int pageSize,
			@QueryParam("search") String search,
			@DefaultValue(StringPool.BLANK) @QueryParam("sort") String
				sortString)
		throws Exception {

		return new FaroFDSResultsDisplay<>(
			contactsEngineClient.getCampaigns(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId, filterString, search, sortString, page, pageSize),
			CampaignDisplay::new, page, pageSize);
	}

}