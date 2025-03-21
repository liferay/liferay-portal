/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.PageVisited;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, PagesVisitedController.class})
@Path("/{groupId}/pages_visited")
@Produces(MediaType.APPLICATION_JSON)
public class PagesVisitedController extends BaseFaroController {

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public PageVisited getPageVisited(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		return contactsEngineClient.getPageVisited(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactEntityType,
			@QueryParam("query") String query,
			@QueryParam("interestName") String interestName,
			@DefaultValue(StringPool.BLANK) @QueryParam("startDate") FaroParam
				<Date> startDateFaroParam,
			@DefaultValue(StringPool.BLANK) @QueryParam("endDate") FaroParam
				<Date> endDateFaroParam,
			@DefaultValue(StringPool.TRUE) @QueryParam("active") boolean active,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		Results<PageVisited> results = contactsEngineClient.getPagesVisited(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), channelId,
			contactsEntityId, contactsHelper.getOwnerType(contactEntityType),
			query, interestName, startDateFaroParam.getValue(),
			endDateFaroParam.getValue(), active, cur, delta,
			orderByFieldsFaroParam.getValue());

		return new FaroResultsDisplay(results);
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@FormParam("contactsEntityId") String contactsEntityId,
			@FormParam("contactsEntityType") int contactsEntityType,
			@FormParam("query") String query,
			@FormParam("interestName") String interestName,
			@DefaultValue(StringPool.BLANK) @FormParam("startDate") FaroParam
				<Date> startDateFaroParam,
			@DefaultValue(StringPool.BLANK) @FormParam("endDate") FaroParam
				<Date> endDateFaroParam,
			@DefaultValue(StringPool.TRUE) @FormParam("active") boolean active,
			@FormParam("cur") int cur, @FormParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @FormParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, channelId, contactsEntityId, contactsEntityType, query,
			interestName, startDateFaroParam, endDateFaroParam, active, cur,
			delta, orderByFieldsFaroParam);
	}

}