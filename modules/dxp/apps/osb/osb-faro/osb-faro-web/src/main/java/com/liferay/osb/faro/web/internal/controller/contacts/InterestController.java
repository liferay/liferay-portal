/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.InterestUtil;
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

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shinn Lok
 */
@Component(service = {FaroController.class, InterestController.class})
@Path("/{groupId}/interest")
@Produces(MediaType.APPLICATION_JSON)
public class InterestController extends BaseFaroController {

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("query") String query,
			@QueryParam("interval") String interval, @QueryParam("max") int max,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return InterestUtil.getInterests(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), channelId,
			contactsEntityId, query, cur, delta,
			orderByFieldsFaroParam.getValue(), contactsEngineClient);
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@FormParam("contactsEntityId") String contactsEntityId,
			@FormParam("query") String query,
			@FormParam("interval") String interval, @FormParam("max") int max,
			@FormParam("cur") int cur, @FormParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @FormParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, channelId, contactsEntityId, query, interval, max, cur,
			delta, orderByFieldsFaroParam);
	}

	@GET
	@Path("/keywords")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchKeywords(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("query") String query, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta)
		throws Exception {

		return new FaroResultsDisplay(
			contactsEngineClient.getInterestKeywords(
				channelId,
				faroProjectLocalService.getFaroProjectByGroupId(groupId), query,
				cur, delta));
	}

}