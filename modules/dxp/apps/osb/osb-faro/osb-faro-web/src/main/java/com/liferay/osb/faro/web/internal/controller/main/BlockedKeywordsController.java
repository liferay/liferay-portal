/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.engine.client.model.BlockedKeyword;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(service = {BlockedKeywordsController.class, FaroController.class})
@Path("/{groupId}/blocked_keywords")
@Produces(MediaType.APPLICATION_JSON)
public class BlockedKeywordsController extends BaseFaroController {

	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public Results<BlockedKeyword> addBlockedKeywords(
			@PathParam("groupId") long groupId,
			@FormParam("keywords") FaroParam<List<String>> keywordsFaroParam)
		throws Exception {

		return contactsEngineClient.addBlockedKeywords(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			keywordsFaroParam.getValue());
	}

	@DELETE
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void delete(
			@PathParam("groupId") long groupId,
			@FormParam("ids") FaroParam<List<String>> idsFaroParam)
		throws Exception {

		contactsEngineClient.deleteBlockedKeywords(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			idsFaroParam.getValue());
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public BlockedKeyword getBlockedKeyword(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		return contactsEngineClient.getBlockedKeyword(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("query") String query, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return new FaroResultsDisplay(
			contactsEngineClient.getBlockedKeywords(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), query,
				cur, delta, orderByFieldsFaroParam.getValue()));
	}

}