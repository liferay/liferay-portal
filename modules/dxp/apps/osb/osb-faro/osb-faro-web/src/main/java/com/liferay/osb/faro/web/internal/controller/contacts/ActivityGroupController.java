/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.ActivityGroup;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.ActivityGroupDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
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
@Component(service = {ActivityGroupController.class, FaroController.class})
@Path("/{groupId}/activity_group")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityGroupController extends BaseFaroController {

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@QueryParam("query") String query,
			@DefaultValue(StringPool.BLANK) @QueryParam("startDate") FaroParam
				<Date> startDateFaroParam,
			@DefaultValue(StringPool.BLANK) @QueryParam("endDate") FaroParam
				<Date> endDateFaroParam,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		Results<ActivityGroup> results = contactsEngineClient.getActivityGroups(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), channelId,
			contactsEntityId, contactsHelper.getOwnerType(contactsEntityType),
			query, startDateFaroParam.getValue(), endDateFaroParam.getValue(),
			cur, delta, orderByFieldsFaroParam.getValue());

		return new FaroResultsDisplay(
			TransformUtil.transform(
				results.getItems(),
				activityGroup -> {
					ActivityGroupDisplay activityGroupDisplay =
						new ActivityGroupDisplay(activityGroup);

					if (ListUtil.isEmpty(
							activityGroupDisplay.getActivityDisplays())) {

						return null;
					}

					return activityGroupDisplay;
				}),
			results.getTotal());
	}

}