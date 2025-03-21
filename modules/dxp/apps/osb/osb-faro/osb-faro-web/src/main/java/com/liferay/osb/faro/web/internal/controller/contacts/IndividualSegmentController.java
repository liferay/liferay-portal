/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.contacts.model.constants.JSONConstants;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChangeAggregation;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.constants.FaroPreferencesConstants;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.controller.main.PreferencesController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualSegmentDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualSegmentMembershipChangeDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.search.FaroSearchContext;
import com.liferay.osb.faro.web.internal.util.IndividualSegmentUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, IndividualSegmentController.class})
@Path("/{groupId}/individual_segment")
@Produces(MediaType.APPLICATION_JSON)
public class IndividualSegmentController extends BaseFaroController {

	@Path("/{id}/memberships")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentDisplay addMemberships(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("individualIds") FaroParam<List<String>>
				individualIdsFaroParam)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		IndividualSegment individualSegment =
			contactsEngineClient.getIndividualSegment(faroProject, id, false);

		validateUpdateMemberships(individualSegment);

		contactsEngineClient.addMemberships(
			faroProject, id, individualIdsFaroParam.getValue());

		return new IndividualSegmentDisplay(individualSegment);
	}

	@Path("/{id}/channel/{channelId}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentDisplay assignChannel(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@PathParam("channelId") String channelId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		IndividualSegment individualSegment =
			contactsEngineClient.getIndividualSegment(faroProject, id, false);

		contactsEngineClient.assignChannelToIndividualSegment(
			faroProject, id, channelId);

		individualSegment.setChannelId(channelId);

		return new IndividualSegmentDisplay(individualSegment);
	}

	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentDisplay create(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("individualIds")
			FaroParam
				<List<String>> individualIdsFaroParam,
			@FormParam("filter") String filter,
			@FormParam("includeAnonymousUsers") boolean includeAnonymousUsers,
			@FormParam("name") String name,
			@FormParam("segmentType") String segmentType)
		throws Exception {

		validateCreate(channelId, segmentType);

		if (segmentType.equals(IndividualSegment.Type.DYNAMIC.name())) {
			return createDynamic(
				channelId, groupId, filter, includeAnonymousUsers, name);
		}
		else if (segmentType.equals(IndividualSegment.Type.STATIC.name())) {
			return createStatic(
				channelId, groupId, individualIdsFaroParam.getValue(), name);
		}

		return null;
	}

	@DELETE
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public void delete(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		contactsEngineClient.deleteIndividualSegment(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id);

		_preferencesController.removeIndividualSegmentPreferences(
			groupId, id, FaroPreferencesConstants.SCOPE_GROUP);
	}

	@DELETE
	@Path("/{id}/memberships")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentDisplay deleteMemberships(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("individualIds") FaroParam<List<String>>
				individualIdsFaroParam)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		IndividualSegment individualSegment =
			contactsEngineClient.getIndividualSegment(faroProject, id, false);

		validateUpdateMemberships(individualSegment);

		for (String individualId : individualIdsFaroParam.getValue()) {
			contactsEngineClient.deleteMembership(
				faroProject, id, individualId);
		}

		return new IndividualSegmentDisplay(individualSegment);
	}

	@GET
	@Path("/{id}/distribution")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<Map<String, Object>> getDistribution(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("fieldMappingFieldName") String fieldMappingFieldName,
			@QueryParam("binSize") double binSize,
			@QueryParam("numberOfBins") int numberOfBins,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta)
		throws Exception {

		return IndividualSegmentUtil.getFieldDistribution(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
			fieldMappingFieldName, binSize, numberOfBins, cur, delta,
			contactsEngineClient);
	}

	@Override
	public int[] getEntityTypes() {
		return _ENTITY_TYPES.clone();
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentDisplay getIndividualSegmentDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("includeReferencedObjects") boolean
				includeReferencedObjects)
		throws Exception {

		return new IndividualSegmentDisplay(
			contactsEngineClient.getIndividualSegment(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				includeReferencedObjects));
	}

	@GET
	@Path("/{id}/memberships/changes/aggregations")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<IndividualSegmentMembershipChangeAggregation>
			getMembershipChangeAggregations(
				@PathParam("groupId") long groupId, @PathParam("id") String id,
				@QueryParam("interval") String interval,
				@QueryParam("max") int max)
		throws Exception {

		Results<IndividualSegmentMembershipChangeAggregation> results =
			contactsEngineClient.
				getIndividualSegmentMembershipChangeAggregations(
					faroProjectLocalService.getFaroProjectByGroupId(groupId),
					id, interval, max);

		return results.getItems();
	}

	@GET
	@Path("/{id}/memberships/changes")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay getMembershipChanges(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("query") String query,
			@DefaultValue(StringPool.BLANK) @QueryParam("startDate") FaroParam
				<Date> startDateFaroParam,
			@DefaultValue(StringPool.BLANK) @QueryParam("endDate") FaroParam
				<Date> endDateFaroParam,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		Results<IndividualSegmentMembershipChange> results =
			contactsEngineClient.getIndividualSegmentMembershipChanges(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				query, startDateFaroParam.getValue(),
				endDateFaroParam.getValue(), cur, delta,
				orderByFieldsFaroParam.getValue());

		Function
			<IndividualSegmentMembershipChange,
			 IndividualSegmentMembershipChangeDisplay> function =
				IndividualSegmentMembershipChangeDisplay::new;

		return new FaroResultsDisplay(results, function);
	}

	@GET
	@Path("/{id}/memberships")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay getMemberships(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		Results<IndividualSegmentMembership> results =
			contactsEngineClient.getIndividualSegmentMemberships(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				cur, delta, orderByFieldsFaroParam.getValue());

		return new FaroResultsDisplay(results);
	}

	@GET
	@Path("/unassigned")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getUnassigned(
			@PathParam("groupId") long groupId, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<IndividualSegment> results =
			contactsEngineClient.getUnassignedIndividualSegments(
				faroProject, cur, delta, orderByFieldsFaroParam.getValue());

		Function<IndividualSegment, IndividualSegmentDisplay> function =
			IndividualSegmentDisplay::new;

		return new FaroResultsDisplay(results, function);
	}

	@Override
	public FaroResultsDisplay search(
			long groupId, FaroSearchContext faroSearchContext)
		throws Exception {

		return search(
			groupId, null, null, 0, null, faroSearchContext.getQuery(), null,
			null, faroSearchContext.getCur(), faroSearchContext.getDelta(),
			faroSearchContext.getOrderByFields());
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@QueryParam("dataSourceId") String dataSourceId,
			@QueryParam("query") String query,
			@QueryParam("segmentType") String segmentType,
			@QueryParam("state") String state, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, channelId, contactsEntityId, contactsEntityType,
			dataSourceId, query, segmentType, state, cur, delta,
			orderByFieldsFaroParam.getValue());
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@FormParam("contactsEntityId") String contactsEntityId,
			@FormParam("contactsEntityType") int contactsEntityType,
			@FormParam("dataSourceId") String dataSourceId,
			@FormParam("query") String query,
			@FormParam("segmentType") String segmentType,
			@FormParam("state") String state, @FormParam("cur") int cur,
			@FormParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @FormParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, channelId, contactsEntityId, contactsEntityType,
			dataSourceId, query, state, segmentType, cur, delta,
			orderByFieldsFaroParam.getValue());
	}

	@Path("/{id}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentDisplay update(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("filter") String filter,
			@FormParam("includeAnonymousUsers") boolean includeAnonymousUsers,
			@DefaultValue(StringPool.BLANK) @FormParam("individualIds")
				FaroParam<List<String>> individualIdsFaroParam,
			@FormParam("name") String name)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		IndividualSegment individualSegment =
			contactsEngineClient.getIndividualSegment(faroProject, id, false);

		validateUpdate(individualSegment);

		String segmentType = individualSegment.getSegmentType();

		if (segmentType.equals(IndividualSegment.Type.DYNAMIC.name())) {
			return updateDynamic(
				groupId, individualSegment, filter, includeAnonymousUsers,
				name);
		}
		else if (segmentType.equals(IndividualSegment.Type.STATIC.name())) {
			return updateStatic(
				groupId, individualSegment, individualIdsFaroParam.getValue(),
				name);
		}

		return new IndividualSegmentDisplay(individualSegment);
	}

	protected IndividualSegmentDisplay createDynamic(
			String channelId, long groupId, String filter,
			boolean includeAnonymousUsers, String name)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return new IndividualSegmentDisplay(
			contactsEngineClient.addIndividualSegment(
				faroProject, getUserId(), channelId, filter,
				includeAnonymousUsers, name,
				IndividualSegment.Type.DYNAMIC.name(),
				IndividualSegment.Status.ACTIVE.name()));
	}

	protected IndividualSegmentDisplay createStatic(
			String channelId, long groupId, List<String> individualIds,
			String name)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		IndividualSegment individualSegment =
			contactsEngineClient.addIndividualSegment(
				faroProject, getUserId(), channelId, null, false, name,
				IndividualSegment.Type.STATIC.name(),
				IndividualSegment.Status.ACTIVE.name());

		contactsEngineClient.addMemberships(
			faroProject, individualSegment.getId(), individualIds);

		return new IndividualSegmentDisplay(individualSegment);
	}

	protected FaroResultsDisplay<IndividualSegment> search(
			long groupId, String channelId, String contactsEntityId,
			int contactsEntityType, String dataSourceId, String query,
			String segmentType, String state, int cur, int delta,
			List<OrderByField> orderByFields)
		throws Exception {

		Results<IndividualSegment> results = null;

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		if (Validator.isNull(contactsEntityId)) {
			results = contactsEngineClient.getIndividualSegments(
				faroProject, channelId, dataSourceId, query,
				Collections.singletonList("name"), null, segmentType, state,
				IndividualSegment.Status.ACTIVE.name(), cur, delta,
				orderByFields);
		}
		else if (contactsEntityType == FaroConstants.TYPE_ACCOUNT) {
			results = contactsEngineClient.getAccountIndividualSegments(
				faroProject, contactsEntityId, channelId, query,
				IndividualSegment.Status.ACTIVE.name(), cur, delta,
				orderByFields);
		}
		else if (contactsEntityType == FaroConstants.TYPE_INDIVIDUAL) {
			results = contactsEngineClient.getIndividualIndividualSegments(
				faroProject, channelId, contactsEntityId, query,
				IndividualSegment.Status.ACTIVE.name(), cur, delta,
				orderByFields);
		}

		if (results != null) {
			return new FaroResultsDisplay<>(
				results, IndividualSegmentDisplay::new);
		}

		return new FaroResultsDisplay<>();
	}

	protected IndividualSegmentDisplay updateDynamic(
			long groupId, IndividualSegment individualSegment, String filter,
			boolean includeAnonymousUsers, String name)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return new IndividualSegmentDisplay(
			contactsEngineClient.updateIndividualSegment(
				faroProject, individualSegment.getId(), getUserId(),
				individualSegment.getChannelId(), filter, includeAnonymousUsers,
				name, individualSegment.getSegmentType()));
	}

	protected void updateMembership(
		FaroProject faroProject, String individualSegmentId,
		List<String> individualIds) {

		if (individualIds == null) {
			return;
		}

		Results<IndividualSegmentMembership> results =
			contactsEngineClient.getIndividualSegmentMemberships(
				faroProject, individualSegmentId, 1, 10000, null);

		individualSegmentMembershipLoop:
		for (IndividualSegmentMembership individualSegmentMembership :
				results.getItems()) {

			Iterator<String> iterator = individualIds.iterator();

			while (iterator.hasNext()) {
				String individualId = iterator.next();

				if (individualId.equals(
						individualSegmentMembership.getIndividualId())) {

					iterator.remove();

					continue individualSegmentMembershipLoop;
				}
			}

			contactsEngineClient.deleteMembership(
				faroProject,
				individualSegmentMembership.getIndividualSegmentId(),
				individualSegmentMembership.getIndividualId());
		}

		contactsEngineClient.addMemberships(
			faroProject, individualSegmentId, individualIds);
	}

	protected IndividualSegmentDisplay updateStatic(
			long groupId, IndividualSegment individualSegment,
			List<String> individualIds, String name)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		individualSegment = contactsEngineClient.updateIndividualSegment(
			faroProject, individualSegment.getId(), getUserId(),
			individualSegment.getChannelId(), null, false, name,
			individualSegment.getSegmentType());

		updateMembership(faroProject, individualSegment.getId(), individualIds);

		return new IndividualSegmentDisplay(individualSegment);
	}

	protected void validateCreate(String channelId, String segmentType) {
		if (Validator.isNull(channelId)) {
			throw new FaroException("Invalid channel ID: " + channelId);
		}

		if (!segmentType.equals(IndividualSegment.Type.STATIC.name()) &&
			!segmentType.equals(IndividualSegment.Type.DYNAMIC.name())) {

			throw new FaroException("Invalid segment type: " + segmentType);
		}
	}

	protected void validateStatus(String status) {
		if (!status.equals(IndividualSegment.Status.ACTIVE.name())) {
			throw new FaroException(
				"You cannot modify segments of type: " + status);
		}
	}

	protected void validateUpdate(IndividualSegment individualSegment) {
		validateStatus(individualSegment.getStatus());
	}

	protected void validateUpdateMemberships(
		IndividualSegment individualSegment) {

		String segmentType = individualSegment.getSegmentType();

		if (!segmentType.equals(IndividualSegment.Type.STATIC.name())) {
			throw new FaroException(
				"You cannot modify memberships of type: " + segmentType);
		}

		validateStatus(individualSegment.getStatus());
	}

	private static final int[] _ENTITY_TYPES = {
		FaroConstants.TYPE_SEGMENT_INDIVIDUALS
	};

	@Reference
	private PreferencesController _preferencesController;

}