/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.Activity;
import com.liferay.osb.faro.engine.client.model.ActivityAggregation;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.ActivityDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.ActivityHistoryDisplay;
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
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(service = {ActivityController.class, FaroController.class})
@Path("/{groupId}/activity")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityController extends BaseFaroController {

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public ActivityDisplay getActivityDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		return new ActivityDisplay(
			contactsEngineClient.getActivity(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id));
	}

	@GET
	@Path("/history")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public ActivityHistoryDisplay getActivityHistoryDisplay(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@QueryParam("interval") String interval, @QueryParam("max") int max,
			@QueryParam("rangeEnd") String rangeEnd,
			@QueryParam("rangeStart") String rangeStart)
		throws Exception {

		Results<ActivityAggregation> results =
			contactsEngineClient.getActivityAggregations(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId, contactsEntityId,
				contactsHelper.getOwnerType(contactsEntityType), rangeEnd,
				rangeStart, interval, max * 2);

		List<ActivityAggregation> activityAggregations = results.getItems();

		return new ActivityHistoryDisplay(
			activityAggregations.subList(
				activityAggregations.size() / 2, activityAggregations.size()),
			activityAggregations.subList(0, activityAggregations.size() / 2));
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@QueryParam("query") String query,
			@DefaultValue(StringPool.BLANK) @QueryParam("startDate") FaroParam
				<Date> startDateFaroParam,
			@DefaultValue(StringPool.BLANK) @QueryParam("endDate") FaroParam
				<Date> endDateFaroParam,
			@QueryParam("action") int action, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		Results<Activity> results = contactsEngineClient.getActivities(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			contactsEntityId, contactsHelper.getOwnerType(contactsEntityType),
			null, query, startDateFaroParam.getValue(),
			endDateFaroParam.getValue(), action, cur, delta,
			orderByFieldsFaroParam.getValue());

		Function<Activity, ActivityDisplay> function = ActivityDisplay::new;

		return new FaroResultsDisplay(results, function);
	}

	@GET
	@Path("/asset")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchAssets(
			@PathParam("groupId") long groupId,
			@QueryParam("query") String query,
			@QueryParam("applicationId") String applicationId,
			@QueryParam("channelId") String channelId,
			@QueryParam("eventId") String eventId, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return new FaroResultsDisplay(
			contactsEngineClient.getActivityAssets(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), query,
				applicationId, channelId, eventId, cur, delta,
				orderByFieldsFaroParam.getValue()));
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
			@PathParam("groupId") long groupId,
			@FormParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@FormParam("query") String query,
			@DefaultValue(StringPool.BLANK) @FormParam("startDate") FaroParam
				<Date> startDateFaroParam,
			@DefaultValue(StringPool.BLANK) @FormParam("endDate") FaroParam
				<Date> endDateFaroParam,
			@FormParam("action") int action, @QueryParam("cur") int cur,
			@FormParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @FormParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, contactsEntityId, contactsEntityType, query,
			startDateFaroParam, endDateFaroParam, action, cur, delta,
			orderByFieldsFaroParam);
	}

	@GET
	@Path("/count")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public int searchCount(
			@PathParam("groupId") long groupId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@QueryParam("query") String query,
			@DefaultValue(StringPool.BLANK) @QueryParam("startDate") FaroParam
				<Date> startDateFaroParam,
			@DefaultValue(StringPool.BLANK) @QueryParam("endDate") FaroParam
				<Date> endDateFaroParam,
			@QueryParam("action") int action)
		throws Exception {

		Results<Activity> results = contactsEngineClient.getActivities(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			contactsEntityId, contactsHelper.getOwnerType(contactsEntityType),
			null, null, startDateFaroParam.getValue(),
			endDateFaroParam.getValue(), action, 1, 1, null);

		return results.getTotal();
	}

}