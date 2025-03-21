/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.FieldMappingMap;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.FieldMappingDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.FieldMappingValuesDisplay;
import com.liferay.osb.faro.web.internal.util.FieldMappingUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, FieldMappingController.class})
@Path("/{groupId}/field_mapping")
@Produces(MediaType.APPLICATION_JSON)
public class FieldMappingController extends BaseFaroController {

	@Path("/defaults")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public void addDefaultFieldMappings(@PathParam("groupId") long groupId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		contactsEngineClient.addFieldMappings(
			faroProject, null, FieldMappingConstants.CONTEXT_ORGANIZATION,
			FieldMappingConstants.OWNER_TYPE_ACCOUNT,
			FieldMappingUtil.getNewFieldMappingMaps(
				contactsEngineClient, faroProject,
				FieldMappingConstants.CONTEXT_ORGANIZATION,
				FieldMappingConstants.getSalesforceAccountFieldMappingMaps()));

		List<FieldMappingMap> fieldMappingMaps = new ArrayList<>();

		fieldMappingMaps.addAll(
			FieldMappingConstants.getDefaultFieldMappingMaps());
		fieldMappingMaps.addAll(
			FieldMappingConstants.getLiferayFieldMappingMaps());
		fieldMappingMaps.addAll(
			FieldMappingConstants.getSalesforceIndividualFieldMappingMaps());

		contactsEngineClient.addFieldMappings(
			faroProject, null, FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
			FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
			FieldMappingUtil.getNewFieldMappingMaps(
				contactsEngineClient, faroProject,
				FieldMappingConstants.CONTEXT_DEMOGRAPHICS, fieldMappingMaps));
	}

	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public FieldMappingDisplay create(
			@PathParam("groupId") long groupId, @FormParam("name") String name,
			@FormParam("type") String type)
		throws Exception {

		validateCreate(name);

		return new FieldMappingDisplay(
			contactsEngineClient.addFieldMapping(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
				Collections.emptyMap(), name, type,
				FieldMappingConstants.OWNER_TYPE_INDIVIDUAL, false));
	}

	@GET
	@Path("/{fieldName}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FieldMappingDisplay getFieldMappingDisplay(
			@PathParam("groupId") long groupId,
			@PathParam("fieldName") String fieldName)
		throws Exception {

		return new FieldMappingDisplay(
			contactsEngineClient.getFieldMapping(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				fieldName));
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("context") String context,
			@QueryParam("displayName") String displayName,
			@QueryParam("ownerType") String ownerType,
			@QueryParam("query") String query, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@QueryParam("orderByType") String orderByType)
		throws Exception {

		List<OrderByField> orderByFields = null;

		if (Validator.isNotNull(orderByType)) {
			orderByFields = Collections.singletonList(
				new OrderByField("displayName", orderByType, true));
		}

		Results<FieldMapping> results = contactsEngineClient.getFieldMappings(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), context,
			displayName, ownerType, query, cur, delta, orderByFields);

		Function<FieldMapping, FieldMappingDisplay> function =
			FieldMappingDisplay::new;

		return new FaroResultsDisplay(results, function);
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
			@PathParam("groupId") long groupId,
			@FormParam("context") String context,
			@FormParam("displayName") String displayName,
			@FormParam("ownerType") String ownerType,
			@FormParam("query") String query, @FormParam("cur") int cur,
			@FormParam("delta") int delta,
			@FormParam("orderByType") String orderByType)
		throws Exception {

		return search(
			groupId, context, displayName, ownerType, query, cur, delta,
			orderByType);
	}

	@GET
	@Path("/suggestions")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay searchSuggestions(
			@PathParam("groupId") long groupId,
			@QueryParam("query") String query, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta)
		throws Exception {

		List<FieldMappingValuesDisplay> fieldMappingValuesDisplays =
			new ArrayList<>();

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<FieldMapping> results = contactsEngineClient.getFieldMappings(
			faroProject, FieldMappingConstants.CONTEXT_DEMOGRAPHICS, null, null,
			query, cur, delta, null);

		List<FieldMapping> fieldMappings = results.getItems();

		List<List<Field>> fieldsList = contactsEngineClient.getFieldsList(
			faroProject, FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
			TransformUtil.transform(fieldMappings, FieldMapping::getFieldName),
			1, 1, null);

		for (int i = 0; i < fieldMappings.size(); i++) {
			fieldMappingValuesDisplays.add(
				new FieldMappingValuesDisplay(
					fieldMappings.get(i), fieldsList.get(i)));
		}

		return new FaroResultsDisplay(
			fieldMappingValuesDisplays, results.getTotal());
	}

	protected void validateCreate(String name) {
		Matcher matcher = _pattern.matcher(name);

		if (!matcher.find()) {
			throw new FaroException("Invalid field mapping name: " + name);
		}
	}

	private static final Pattern _pattern = Pattern.compile(
		"^[A-Za-z_][\\w]{0,126}[A-Za-z0-9]$");

}