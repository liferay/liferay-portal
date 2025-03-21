/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.contacts.model.constants.JSONConstants;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.exception.DuplicateEntryException;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.search.FaroSearchContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.annotation.security.RolesAllowed;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, IndividualController.class})
@Path("/{groupId}/individual")
@Produces(MediaType.APPLICATION_JSON)
public class IndividualController extends BaseFaroController {

	@Path("/{id}/memberships")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualDisplay addMemberships(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("individualSegmentIds") FaroParam<List<String>>
				individualSegmentIdsFaroParam)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		validateAddMemberships(
			faroProject, individualSegmentIdsFaroParam.getValue());

		for (String individualSegmentId :
				individualSegmentIdsFaroParam.getValue()) {

			try {
				contactsEngineClient.addMembership(
					faroProject, individualSegmentId, id);
			}
			catch (DuplicateEntryException duplicateEntryException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"The individual already belongs to the segment: " +
							individualSegmentId,
						duplicateEntryException);
				}
			}
		}

		return getIndividualDisplay(groupId, id, null);
	}

	@GET
	@Path("/{id}/details")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public Map<String, Map<String, List<Field>>> getDetails(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Individual individual = contactsEngineClient.getIndividual(
			faroProject, id, null);

		_setFieldMappingDisplayName(
			individual.getCustom(),
			contactsEngineClient.getFieldMappings(
				faroProject, FieldMappingConstants.CONTEXT_CUSTOM, null,
				FieldMappingConstants.OWNER_TYPE_INDIVIDUAL, null, 1, 10000,
				null));
		_setFieldMappingDisplayName(
			individual.getDemographics(),
			contactsEngineClient.getFieldMappings(
				faroProject, FieldMappingConstants.CONTEXT_DEMOGRAPHICS, null,
				FieldMappingConstants.OWNER_TYPE_INDIVIDUAL, null, 1, 10000,
				null));

		return HashMapBuilder.<String, Map<String, List<Field>>>put(
			"custom", individual.getCustom()
		).put(
			"demographics", individual.getDemographics()
		).build();
	}

	@GET
	@Path("/distribution")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getDistribution(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("fieldMappingFieldName") String fieldMappingFieldName,
			@QueryParam("individualSegmentId") String individualSegmentId,
			@QueryParam("count") int count,
			@QueryParam("numberOfBins") int numberOfBins,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return new FaroResultsDisplay(
			contactsEngineClient.getIndividualsDistribution(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId, fieldMappingFieldName, individualSegmentId, count,
				numberOfBins, orderByFieldsFaroParam.getValue()));
	}

	@GET
	@Path("/enriched_profiles_count")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay getEnrichedProfilesCount(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") long channelId)
		throws Exception {

		Long enrichedProfilesCount =
			contactsEngineClient.getEnrichedProfilesCount(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId);

		return new FaroResultsDisplay(
			Collections.emptyList(), enrichedProfilesCount.intValue());
	}

	@Override
	public int[] getEntityTypes() {
		return _ENTITY_TYPES.clone();
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public IndividualDisplay getIndividualDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("channelId") String channelId)
		throws Exception {

		return new IndividualDisplay(
			contactsEngineClient.getIndividual(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				channelId));
	}

	@Override
	public FaroResultsDisplay search(
			long groupId, FaroSearchContext faroSearchContext)
		throws Exception {

		return search(
			groupId, null, null, null, null, null, null, null,
			faroSearchContext.getQuery(), false, Collections.emptyList(),
			faroSearchContext.getCur(), faroSearchContext.getDelta(),
			faroSearchContext.getOrderByFields());
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("accountId") String accountId,
			@QueryParam("channelId") String channelId,
			@QueryParam("dataSourceId") String dataSourceId,
			@QueryParam("individualSegmentId") String individualSegmentId,
			@QueryParam("notIndividualSegmentId") String notIndividualSegmentId,
			@QueryParam("interestName") String interestName,
			@QueryParam("filter") String filter,
			@QueryParam("query") String query,
			@QueryParam("includeAnonymousUsers") boolean includeAnonymousUsers,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@QueryParam("includePropertyNames")
			FaroParam
				<List<String>> includePropertyNamesFaroParam,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, accountId, channelId, dataSourceId, individualSegmentId,
			notIndividualSegmentId, interestName, filter, query,
			includeAnonymousUsers, includePropertyNamesFaroParam.getValue(),
			cur, delta, orderByFieldsFaroParam.getValue());
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
			@PathParam("groupId") long groupId,
			@FormParam("accountId") String accountId,
			@FormParam("channelId") String channelId,
			@FormParam("dataSourceId") String dataSourceId,
			@FormParam("individualSegmentId") String individualSegmentId,
			@FormParam("notIndividualSegmentId") String notIndividualSegmentId,
			@FormParam("interestName") String interestName,
			@FormParam("filter") String filter,
			@FormParam("query") String query,
			@FormParam("includeAnonymousUsers") boolean includeAnonymousUsers,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("includePropertyNames")
			FaroParam
				<List<String>> includePropertyNamesFaroParam,
			@FormParam("cur") int cur, @FormParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @FormParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, accountId, channelId, dataSourceId, individualSegmentId,
			notIndividualSegmentId, interestName, filter, query,
			includeAnonymousUsers, includePropertyNamesFaroParam.getValue(),
			cur, delta, orderByFieldsFaroParam.getValue());
	}

	@GET
	@Path("/field_values")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay searchValues(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") long channelId,
			@QueryParam("fieldMappingFieldName") String fieldMappingFieldName,
			@QueryParam("query") String query, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta)
		throws Exception {

		Results<Object> results = contactsEngineClient.getFieldValues(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), channelId,
			query, fieldMappingFieldName, cur, delta);

		return new FaroResultsDisplay(results);
	}

	@SuppressWarnings("unchecked")
	protected FaroResultsDisplay search(
			long groupId, String accountId, String channelId,
			String dataSourceId, String individualSegmentId,
			String notIndividualSegmentId, String interestName, String filter,
			String query, boolean includeAnonymousUsers,
			List<String> includePropertyNames, int cur, int delta,
			List<OrderByField> orderByFields)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<Individual> results = contactsEngineClient.getIndividuals(
			faroProject, accountId, channelId, dataSourceId,
			individualSegmentId, notIndividualSegmentId, interestName, filter,
			query, FieldMappingConstants.getSearchFieldMappingNames(),
			includeAnonymousUsers, cur, delta, orderByFields);

		Function<Individual, IndividualDisplay> function = individual -> {
			IndividualDisplay individualDisplay = new IndividualDisplay(
				individual);

			individualDisplay.addProperties(includePropertyNames);

			return individualDisplay;
		};

		return new FaroResultsDisplay(results, function);
	}

	protected void validateAddMemberships(
		FaroProject faroProject, List<String> individualSegmentIds) {

		for (String individualSegmentId : individualSegmentIds) {
			IndividualSegment individualSegment =
				contactsEngineClient.getIndividualSegment(
					faroProject, individualSegmentId, false);

			String segmentType = individualSegment.getSegmentType();

			if (!segmentType.equals(IndividualSegment.Type.STATIC.name())) {
				throw new FaroException(
					"You cannot modify memberships of: " +
						individualSegment.getName());
			}
		}
	}

	private void _setFieldMappingDisplayName(
		Map<String, List<Field>> fieldsMap, Results<FieldMapping> results) {

		if (fieldsMap.isEmpty()) {
			return;
		}

		Map<String, FieldMapping> fieldMappingMap = new HashMap<>();

		for (FieldMapping fieldMapping : results.getItems()) {
			fieldMappingMap.put(fieldMapping.getFieldName(), fieldMapping);
		}

		for (Map.Entry<String, List<Field>> entry : fieldsMap.entrySet()) {
			FieldMapping fieldMapping = fieldMappingMap.get(entry.getKey());

			if (fieldMapping == null) {
				continue;
			}

			for (Field field : entry.getValue()) {
				field.setName(fieldMapping.getDisplayName());
			}
		}
	}

	private static final int[] _ENTITY_TYPES = {FaroConstants.TYPE_INDIVIDUAL};

	private static final Log _log = LogFactoryUtil.getLog(
		IndividualController.class);

}