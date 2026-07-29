/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.contacts.model.constants.JSONConstants;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChangeAggregation;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentRealTimeMembership;
import com.liferay.osb.faro.engine.client.model.RealTimeMembershipMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.constants.FaroPreferencesConstants;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.controller.main.PreferencesFaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualSegmentActivationDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualSegmentDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualSegmentMembershipChangeDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.search.FaroSearchContext;
import com.liferay.osb.faro.web.internal.util.IndividualSegmentUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.ListUtil;
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

import java.util.Arrays;
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
@Component(
	service = {FaroController.class, IndividualSegmentFaroController.class}
)
@Path("/{groupId}/individual_segment")
@Produces(MediaType.APPLICATION_JSON)
public class IndividualSegmentFaroController extends BaseFaroController {

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
	public IndividualSegmentDisplay createIndividualSegmentDisplay(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@FormParam("externalReferenceCode") String externalReferenceCode,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("individualIds")
			FaroParam
				<List<String>> individualIdsFaroParam,
			@FormParam("filter") String filterString,
			@FormParam("includeAnonymousUsers") boolean includeAnonymousUsers,
			@FormParam("name") String name,
			@DefaultValue("INDIVIDUAL") @FormParam("segmentCategory") String
				segmentCategory,
			@FormParam("segmentType") String segmentType,
			@FormParam("sequential") boolean sequential)
		throws Exception {

		validateCreate(channelId, segmentCategory, segmentType);

		return createIndividualSegment(
			channelId, groupId, externalReferenceCode, filterString,
			includeAnonymousUsers, name, segmentCategory, segmentType,
			sequential);
	}

	@DELETE
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public void deleteIndividualSegments(
			@PathParam("groupId") long groupId,
			@FormParam("ids") FaroParam<List<String>> idsFaroParam)
		throws Exception {

		contactsEngineClient.deleteIndividualSegments(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			idsFaroParam.getValue());

		_preferencesFaroController.removeIndividualSegmentsPreferences(
			groupId, idsFaroParam.getValue(),
			FaroPreferencesConstants.SCOPE_GROUP);
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
			getIndividualSegmentMembershipChangeAggregations(
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
	public FaroResultsDisplay
			getIndividualSegmentMembershipChangesFaroResultsDisplay(
				@PathParam("groupId") long groupId, @PathParam("id") String id,
				@QueryParam("query") String query,
				@DefaultValue(StringPool.BLANK) @QueryParam("startDate")
					FaroParam<Date> startDateFaroParam,
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
	public FaroResultsDisplay getIndividualSegmentMembershipsFaroResultsDisplay(
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
	@Path("/{id}/real-time-memberships")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay
			getIndividualSegmentRealTimeMembershipsFaroResultsDisplay(
				@PathParam("groupId") long groupId, @PathParam("id") String id,
				@QueryParam("day") String day,
				@DefaultValue(StringPool.BLANK) @QueryParam("profileTypes")
					FaroParam<List<String>> profileTypesFaroParam,
				@QueryParam("query") String query,
				@DefaultValue(StringPool.BLANK) @QueryParam("types") FaroParam
					<List<String>> typesFaroParam,
				@QueryParam("cur") int cur, @QueryParam("delta") int delta,
				@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
					FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		Results<IndividualSegmentRealTimeMembership> results =
			contactsEngineClient.getIndividualSegmentRealTimeMemberships(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), day,
				id, profileTypesFaroParam.getValue(), query,
				typesFaroParam.getValue(), cur, delta,
				orderByFieldsFaroParam.getValue());

		return new FaroResultsDisplay(results);
	}

	@GET
	@Path("/{id}/real-time-membership-metric")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public RealTimeMembershipMetric getRealTimeMembershipMetric(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		return contactsEngineClient.getRealTimeMembershipMetric(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id);
	}

	@GET
	@Path("/unassigned")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getUnassignedIndividualSegmentFaroResultsDisplay(
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
			null, null, faroSearchContext.getCur(),
			faroSearchContext.getDelta(), faroSearchContext.getOrderByFields());
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
			@DefaultValue(StringPool.BLANK) @QueryParam("segmentCategories")
				FaroParam<List<String>> segmentCategoriesFaroParam,
			@DefaultValue(StringPool.BLANK) @QueryParam("segmentTypes")
				FaroParam<List<String>> segmentTypesFaroParam,
			@QueryParam("state") String state, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		List<String> segmentTypes = segmentTypesFaroParam.getValue();

		if (ListUtil.isEmpty(segmentTypes)) {
			segmentTypes = Arrays.asList("BATCH", "REAL_TIME");
		}

		return search(
			groupId, channelId, contactsEntityId, contactsEntityType,
			dataSourceId, query, segmentCategoriesFaroParam.getValue(),
			segmentTypes, state, cur, delta, orderByFieldsFaroParam.getValue());
	}

	@GET
	@Path("/search")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroFDSResultsDisplay<IndividualSegment> search(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("dataSourceId") String dataSourceId,
			@QueryParam("search") String search,
			@DefaultValue(StringPool.BLANK) @QueryParam("segmentCategories")
				FaroParam<List<String>> segmentCategoriesFaroParam,
			@DefaultValue(StringPool.BLANK) @QueryParam("segmentTypes")
				FaroParam<List<String>> segmentTypesFaroParam,
			@QueryParam("state") String state, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize)
		throws Exception {

		List<String> segmentTypes = segmentTypesFaroParam.getValue();

		if (ListUtil.isEmpty(segmentTypes)) {
			segmentTypes = Arrays.asList("BATCH", "REAL_TIME");
		}

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return new FaroFDSResultsDisplay<>(
			contactsEngineClient.getIndividualSegments(
				faroProject, channelId, dataSourceId, search,
				Arrays.asList("authorName", "name"), null,
				segmentCategoriesFaroParam.getValue(), segmentTypes, state,
				IndividualSegment.Status.ACTIVE.name(), page, pageSize, null),
			IndividualSegmentDisplay::new, page, pageSize);
	}

	@Path("/{id}/activation")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentActivationDisplay
			updateIndividualSegmentActivationDisplay(
				@PathParam("groupId") long groupId,
				@FormParam("cronExpression") String cronExpression,
				@FormParam("frequencyType") String frequencyType,
				@PathParam("id") long segmentId,
				@DefaultValue(StringPool.BLANK) @FormParam("scheduleEndDate")
					FaroParam<Date> scheduleEndDateFaroParam,
				@DefaultValue(StringPool.BLANK) @FormParam("scheduleStartDate")
					FaroParam<Date> scheduleStartDateFaroParam,
				@FormParam("scheduleType") String scheduleType)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return new IndividualSegmentActivationDisplay(
			contactsEngineClient.updateSegmentActivation(
				faroProject, cronExpression, frequencyType,
				scheduleEndDateFaroParam.getValue(),
				scheduleStartDateFaroParam.getValue(), scheduleType,
				segmentId));
	}

	@Path("/{id}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualSegmentDisplay updateIndividualSegmentDisplay(
			@PathParam("groupId") long groupId,
			@FormParam("externalReferenceCode") String externalReferenceCode,
			@PathParam("id") String id,
			@FormParam("filter") String filterString,
			@FormParam("includeAnonymousUsers") boolean includeAnonymousUsers,
			@DefaultValue(StringPool.BLANK) @FormParam("individualIds")
				FaroParam<List<String>> individualIdsFaroParam,
			@FormParam("name") String name,
			@FormParam("sequential") boolean sequential)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		IndividualSegment individualSegment =
			contactsEngineClient.getIndividualSegment(faroProject, id, false);

		validateUpdate(individualSegment);

		return updateIndividualSegment(
			groupId, individualSegment, externalReferenceCode, filterString,
			includeAnonymousUsers, name, sequential);
	}

	protected IndividualSegmentDisplay createIndividualSegment(
			String channelId, long groupId, String externalReferenceCode,
			String filterString, boolean includeAnonymousUsers, String name,
			String segmentCategory, String segmentType, boolean sequential)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return new IndividualSegmentDisplay(
			contactsEngineClient.addIndividualSegment(
				faroProject, getUserId(), channelId, externalReferenceCode,
				filterString, includeAnonymousUsers, name, segmentCategory,
				segmentType, sequential,
				IndividualSegment.Status.ACTIVE.name()));
	}

	protected FaroResultsDisplay<IndividualSegment> search(
			long groupId, String channelId, String contactsEntityId,
			int contactsEntityType, String dataSourceId, String query,
			List<String> segmentCategories, List<String> segmentTypes,
			String state, int cur, int delta, List<OrderByField> orderByFields)
		throws Exception {

		Results<IndividualSegment> results = null;

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		if (Validator.isNull(contactsEntityId)) {
			results = contactsEngineClient.getIndividualSegments(
				faroProject, channelId, dataSourceId, query,
				List.of("authorName", "name"), null, segmentCategories,
				segmentTypes, state, IndividualSegment.Status.ACTIVE.name(),
				cur, delta, orderByFields);
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

	protected IndividualSegmentDisplay updateIndividualSegment(
			long groupId, IndividualSegment individualSegment,
			String externalReferenceCode, String filterString,
			boolean includeAnonymousUsers, String name, boolean sequential)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return new IndividualSegmentDisplay(
			contactsEngineClient.updateIndividualSegment(
				faroProject, individualSegment.getId(), getUserId(),
				individualSegment.getChannelId(), externalReferenceCode,
				filterString, includeAnonymousUsers, name,
				individualSegment.getSegmentType(), sequential));
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
			individualSegment.getChannelId(),
			individualSegment.getExternalReferenceCode(), null, false, name,
			individualSegment.getSegmentType(), false);

		updateMembership(faroProject, individualSegment.getId(), individualIds);

		return new IndividualSegmentDisplay(individualSegment);
	}

	protected void validateCategory(String segmentCategory) {
		if (!segmentCategory.equals(
				IndividualSegment.Category.ACCOUNT.name()) &&
			!segmentCategory.equals(
				IndividualSegment.Category.INDIVIDUAL.name())) {

			throw new FaroException(
				"Invalid segment category: " + segmentCategory);
		}
	}

	protected void validateCreate(
		String channelId, String segmentCategory, String segmentType) {

		if (Validator.isNull(channelId)) {
			throw new FaroException("Invalid channel ID: " + channelId);
		}

		validateCategory(segmentCategory);
		validateType(segmentType);
	}

	protected void validateStatus(String status) {
		if (!status.equals(IndividualSegment.Status.ACTIVE.name())) {
			throw new FaroException(
				"You cannot modify segments of type: " + status);
		}
	}

	protected void validateType(String segmentType) {
		if (!segmentType.equals(IndividualSegment.Type.BATCH.name()) &&
			!segmentType.equals(IndividualSegment.Type.REAL_TIME.name())) {

			throw new FaroException("Invalid segment type: " + segmentType);
		}
	}

	protected void validateUpdate(IndividualSegment individualSegment) {
		validateStatus(individualSegment.getStatus());
		validateType(individualSegment.getSegmentType());
	}

	private static final int[] _ENTITY_TYPES = {
		FaroConstants.TYPE_SEGMENT_INDIVIDUALS
	};

	@Reference
	private PreferencesFaroController _preferencesFaroController;

}