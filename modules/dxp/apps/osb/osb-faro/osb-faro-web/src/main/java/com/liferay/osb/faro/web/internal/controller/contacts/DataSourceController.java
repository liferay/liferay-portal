/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.osb.faro.contacts.model.constants.ContactsConstants;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.exception.InvalidFilterException;
import com.liferay.osb.faro.engine.client.model.Credentials;
import com.liferay.osb.faro.engine.client.model.DXPGroup;
import com.liferay.osb.faro.engine.client.model.DXPOrganization;
import com.liferay.osb.faro.engine.client.model.DXPUserGroup;
import com.liferay.osb.faro.engine.client.model.DataSource;
import com.liferay.osb.faro.engine.client.model.DataSourceField;
import com.liferay.osb.faro.engine.client.model.DataSourceProgress;
import com.liferay.osb.faro.engine.client.model.Event;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.FieldMappingMap;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.Provider;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.model.credentials.OAuth1Credentials;
import com.liferay.osb.faro.engine.client.model.credentials.OAuth2Credentials;
import com.liferay.osb.faro.engine.client.model.credentials.TokenCredentials;
import com.liferay.osb.faro.engine.client.model.provider.CSVProvider;
import com.liferay.osb.faro.engine.client.model.provider.LiferayProvider;
import com.liferay.osb.faro.engine.client.model.provider.SalesforceProvider;
import com.liferay.osb.faro.engine.client.util.EngineServiceURLUtil;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.annotations.PATCH;
import com.liferay.osb.faro.web.internal.annotations.Unauthenticated;
import com.liferay.osb.faro.web.internal.antivirus.ClamAVScanner;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.exception.FaroValidationException;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.DXPGroupDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.DXPOrganizationDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.DXPUserGroupDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceMappingDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.FieldMappingValuesDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.FieldValuesDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.LiferaySyncCountsDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.search.FaroSearchContext;
import com.liferay.osb.faro.web.internal.util.ContactsCSVHelper;
import com.liferay.osb.faro.web.internal.util.FieldMappingUtil;
import com.liferay.osb.faro.web.internal.util.OAuthUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.Consumes;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import java.io.File;
import java.io.FileInputStream;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = {DataSourceController.class, FaroController.class})
@Path("/{groupId}/data_source")
@Produces(MediaType.APPLICATION_JSON)
public class DataSourceController extends BaseFaroController {

	@Path("/connect")
	@POST
	@Unauthenticated
	public Map<String, Object> connect(
			@PathParam("groupId") long groupId,
			@DefaultValue("Liferay") @FormParam("name") String dataSourceName,
			@FormParam("portalURL") String portalURL,
			@FormParam("token") String token)
		throws Exception {

		Long faroProjectId = _tokenManager.getFaroProjectId(token);

		if (faroProjectId == null) {
			throw new FaroException(
				"Unable to connect datasource for token " + token);
		}

		FaroProject faroProject = faroProjectLocalService.getFaroProject(
			faroProjectId);

		DataSource dataSource = null;

		String dataSourceId = _tokenManager.getDataSourceId(token);

		if (dataSourceId == null) {
			dataSource = contactsEngineClient.addDataSource(
				faroProject, new TokenCredentials(), getUserId(),
				dataSourceName, portalURL, new LiferayProvider(), null,
				DataSource.Status.ACTIVE.toString());

			_tokenManager.setDataSourceId(dataSource.getId(), token);
		}
		else {
			dataSource = contactsEngineClient.patchDataSource(
				faroProject, dataSourceId, new TokenCredentials(), getUserId(),
				null, portalURL, new LiferayProvider(), null,
				DataSource.Status.ACTIVE.toString());

			_tokenManager.clearToken(token);
		}

		faroProject.setDataSourceConnected(true);

		faroProject = faroProjectLocalService.updateFaroProject(faroProject);

		TokenCredentials tokenCredentials =
			(TokenCredentials)dataSource.getCredentials();

		return HashMapBuilder.<String, Object>put(
			"liferayAnalyticsDataSourceId", dataSource.getId()
		).put(
			"liferayAnalyticsEndpointURL",
			EngineServiceURLUtil.getPublisherExternalURL(faroProject)
		).put(
			"liferayAnalyticsFaroBackendSecuritySignature",
			dataSource.getFaroBackendSecuritySignature()
		).put(
			"liferayAnalyticsFaroBackendURL",
			EngineServiceURLUtil.getBackendExternalURL(faroProject)
		).put(
			"liferayAnalyticsProjectId", faroProject.getProjectId()
		).put(
			"liferayAnalyticsURL", dataSource.getWorkspaceURL()
		).put(
			"publicKey", tokenCredentials.getPublicKey()
		).build();
	}

	@Path("/csv")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay createTypeCSV(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@FormParam("name") String name,
			@FormParam("fileVersionId") long fileVersionId,
			@FormParam("staticIndividualSegmentId") String
				staticIndividualSegmentId,
			@FormParam("event") Event event,
			@DefaultValue(StringPool.BLANK) @FormParam("fieldMappingMaps")
				FaroParam<List<FieldMappingMap>> fieldMappingMapsFaroParam)
		throws Exception {

		DataSourceDisplay dataSourceDisplay = create(
			groupId, null, new CSVProvider(), name, null, event,
			DataSource.Status.ACTIVE.name());

		createFieldMappings(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			dataSourceDisplay.getId(),
			FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
			FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
			fieldMappingMapsFaroParam.getValue());

		List<String> individualSegmentIds = new ArrayList<>();

		if (Validator.isNotNull(staticIndividualSegmentId)) {
			individualSegmentIds.add(staticIndividualSegmentId);
		}

		addCSVIndividuals(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), channelId,
			dataSourceDisplay.getId(), fileVersionId, individualSegmentIds);

		return dataSourceDisplay;
	}

	@Path("/liferay")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay createTypeLiferay(
			@PathParam("groupId") long groupId,
			@FormParam("credentials") Credentials credentials,
			@FormParam("name") String name, @FormParam("url") String url,
			@DefaultValue("INACTIVE") @FormParam("status") String status,
			@DefaultValue(StringPool.BLANK) @FormParam("fieldMappingMaps")
				FaroParam<List<FieldMappingMap>> fieldMappingMapsFaroParam)
		throws Exception {

		DataSourceDisplay dataSourceDisplay = create(
			groupId, credentials, new LiferayProvider(), name, url, null,
			status);

		createFieldMappings(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			dataSourceDisplay.getId(),
			FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
			FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
			fieldMappingMapsFaroParam.getValue());

		return dataSourceDisplay;
	}

	@Path("/salesforce")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay createTypeSalesforce(
			@PathParam("groupId") long groupId,
			@FormParam("credentials") Credentials credentials,
			@FormParam("name") String name, @FormParam("url") String url,
			@DefaultValue(StringPool.BLANK) @FormParam("accountsConfiguration")
				FaroParam<SalesforceProvider.AccountsConfiguration>
					accountsConfigurationFaroParam,
			@DefaultValue(StringPool.BLANK) @FormParam("contactsConfiguration")
				FaroParam<SalesforceProvider.ContactsConfiguration>
					contactsConfigurationFaroParam,
			@DefaultValue("INACTIVE") @FormParam("status") String status)
		throws Exception {

		SalesforceProvider salesforceProvider = new SalesforceProvider();

		salesforceProvider.setAccountsConfiguration(
			accountsConfigurationFaroParam.getValue());
		salesforceProvider.setContactsConfiguration(
			contactsConfigurationFaroParam.getValue());

		DataSourceDisplay dataSourceDisplay = create(
			groupId, credentials, salesforceProvider, name, url, null, status);

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		createFieldMappings(
			faroProject, dataSourceDisplay.getId(),
			FieldMappingConstants.CONTEXT_ORGANIZATION,
			FieldMappingConstants.OWNER_TYPE_ACCOUNT,
			FieldMappingConstants.getSalesforceAccountFieldMappingMaps());
		createFieldMappings(
			faroProject, dataSourceDisplay.getId(),
			FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
			FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
			FieldMappingConstants.getSalesforceIndividualFieldMappingMaps());

		return dataSourceDisplay;
	}

	@DELETE
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void delete(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		DataSource dataSource = contactsEngineClient.getDataSource(
			faroProject, id);

		contactsEngineClient.deleteDataSource(faroProject, dataSource.getId());

		_tokenManager.clearToken(
			dataSource.getId(), faroProject.getFaroProjectId());

		Provider provider = dataSource.getProvider();

		String type = provider.getType();

		if (!type.equals(CSVProvider.TYPE)) {
			return;
		}

		try {
			_contactsCSVHelper.deleteFileEntry(groupId, id);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Could not find CSV file for the datasource: " +
						dataSource.getName(),
					exception);
			}
		}
	}

	@Path("/{id}/disconnect")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void disconnect(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		DataSource dataSource = contactsEngineClient.getDataSource(
			faroProject, id);

		if (Objects.equals(
				dataSource.getState(), DataSource.State.DISCONNECTED.name()) &&
			Objects.equals(
				dataSource.getStatus(), DataSource.Status.INACTIVE.name())) {

			User user = getUser();

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", user.getLocale(), getClass());

			throw new FaroValidationException(
				null,
				_language.get(
					resourceBundle, "data-source-already-disconnected"));
		}

		contactsEngineClient.disconnectDataSource(faroProject, id);
	}

	@Path("/disconnect-all")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void disconnectAll(@PathParam("groupId") long groupId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		contactsEngineClient.disconnectDataSources(faroProject);

		faroProject.setDataSourceConnected(false);

		faroProjectLocalService.updateFaroProject(faroProject);
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public DataSourceDisplay getDataSourceDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		return new DataSourceDisplay(
			groupId,
			contactsEngineClient.getDataSource(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id));
	}

	@Path("/data_source_id")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public String getDataSourceId(
			@Context UriInfo uriInfo, @PathParam("groupId") long groupId,
			@FormParam("token") String token)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			new String(Base64.decode(token), StandardCharsets.UTF_8));

		String dataSourceId = _tokenManager.getDataSourceId(
			jsonObject.getString("token"));

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		_tokenManager.clearToken(dataSourceId, faroProject.getFaroProjectId());

		return dataSourceId;
	}

	@GET
	@Path("/mappings")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<DataSourceMappingDisplay> getDataSourceMappingDisplays(
			@PathParam("groupId") long groupId, @QueryParam("id") String id,
			@QueryParam("fileVersionId") long fileVersionId)
		throws Exception {

		List<DataSourceMappingDisplay> dataSourceMappingDisplays =
			new ArrayList<>();

		List<FieldValuesDisplay> fieldValuesDisplays = getFieldValuesDisplays(
			groupId, id, fileVersionId, null,
			FieldMappingConstants.CONTEXT_DEMOGRAPHICS, 1);

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		List<String> dataSourceFieldNames = new ArrayList<>();
		List<Object> dataSourceFieldValues = new ArrayList<>();

		for (FieldValuesDisplay fieldValuesDisplay : fieldValuesDisplays) {
			dataSourceFieldNames.add(fieldValuesDisplay.getName());
			dataSourceFieldValues.add(fieldValuesDisplay.getValues());
		}

		Set<String> fieldNames = new HashSet<>();

		for (List<String> fieldNameList :
				contactsEngineClient.getFieldNamesList(
					faroProject, dataSourceFieldNames,
					FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
					dataSourceFieldValues)) {

			fieldNames.addAll(fieldNameList);
		}

		Results<FieldMapping> fieldMappingResults =
			contactsEngineClient.getFieldMappings(
				faroProject, FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
				new ArrayList<>(fieldNames), 1, 10000, null);

		Map<String, Field> fieldsMap = new HashMap<>();

		for (List<Field> fieldsList :
				contactsEngineClient.getFieldsList(
					faroProject, FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
					new ArrayList<>(fieldNames), 1, 1, null)) {

			for (Field field : fieldsList) {
				fieldsMap.putIfAbsent(field.getName(), field);
			}
		}

		Map<String, FieldMappingValuesDisplay> fieldMappingValuesDisplayMap =
			new HashMap<>();

		for (FieldMapping fieldMapping : fieldMappingResults.getItems()) {
			fieldMappingValuesDisplayMap.put(
				fieldMapping.getFieldName(),
				new FieldMappingValuesDisplay(
					fieldMapping, fieldsMap.get(fieldMapping.getFieldName())));
		}

		Map<String, List<FieldMapping>> currentFieldMappingsMap =
			new HashMap<>();

		Results<FieldMapping> currentFieldMappingResults =
			contactsEngineClient.getFieldMappings(
				faroProject, FieldMappingConstants.CONTEXT_DEMOGRAPHICS, id,
				null);

		for (FieldMapping fieldMapping :
				currentFieldMappingResults.getItems()) {

			fieldNames.add(fieldMapping.getFieldName());

			List<FieldMapping> currentFieldMappings =
				currentFieldMappingsMap.computeIfAbsent(
					fieldMapping.getDataSourceFieldName(id),
					key -> new ArrayList<>());

			currentFieldMappings.add(fieldMapping);

			currentFieldMappingsMap.put(
				fieldMapping.getDataSourceFieldName(id), currentFieldMappings);
		}

		for (int i = 0; i < fieldValuesDisplays.size(); i++) {
			FieldValuesDisplay fieldValuesDisplay = fieldValuesDisplays.get(i);

			FieldMappingValuesDisplay currentFieldMappingValuesDisplay = null;

			List<FieldMapping> currentFieldMappings =
				currentFieldMappingsMap.get(fieldValuesDisplay.getName());

			if (ListUtil.isEmpty(currentFieldMappings)) {
				currentFieldMappings = Collections.singletonList(null);
			}

			for (FieldMapping currentFieldMapping : currentFieldMappings) {
				if (currentFieldMapping != null) {
					currentFieldMappingValuesDisplay =
						new FieldMappingValuesDisplay(
							currentFieldMapping,
							fieldsMap.get(currentFieldMapping.getFieldName()));
				}

				List<List<String>> fieldNamesList =
					contactsEngineClient.getFieldNamesList(
						faroProject, dataSourceFieldNames,
						FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
						dataSourceFieldValues);

				dataSourceMappingDisplays.add(
					new DataSourceMappingDisplay(
						fieldValuesDisplay.getName(),
						fieldValuesDisplay.getValues(),
						currentFieldMappingValuesDisplay,
						TransformUtil.transform(
							fieldNamesList.get(i),
							fieldName -> fieldMappingValuesDisplayMap.get(
								fieldName))));
			}
		}

		return dataSourceMappingDisplays;
	}

	@GET
	@Path("/{id}/mappings/lite")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<DataSourceMappingDisplay> getDataSourceMappingDisplaysLite(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@DefaultValue(FieldMappingConstants.CONTEXT_DEMOGRAPHICS)
			@QueryParam("context")
			String context)
		throws Exception {

		Map<String, FieldValuesDisplay> fieldValuesDisplayMap = new HashMap<>();

		List<FieldValuesDisplay> fieldValuesDisplays = getFieldValuesDisplays(
			groupId, id, 0, null, context, 1);

		for (FieldValuesDisplay fieldValuesDisplay : fieldValuesDisplays) {
			fieldValuesDisplayMap.put(
				fieldValuesDisplay.getName(), fieldValuesDisplay);
		}

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<FieldMapping> results = contactsEngineClient.getFieldMappings(
			faroProject, context, id, null);

		List<FieldMapping> items = results.getItems();

		List<String> itemsFieldName = TransformUtil.transform(
			items, FieldMapping::getFieldName);

		List<List<Field>> fieldsList = contactsEngineClient.getFieldsList(
			faroProject, context, itemsFieldName, 1, 1, null);

		Map<String, List<Field>> fieldsMap = new HashMap<>();

		for (List<Field> fields : fieldsList) {
			if (ListUtil.isNotNull(fields)) {
				Field field = fields.get(0);

				fieldsMap.put(field.getName(), fields);
			}
		}

		return TransformUtil.transform(
			items,
			item -> {
				String dataSourceFieldName = item.getDataSourceFieldName(id);

				List<String> fieldValues = new ArrayList<>();

				FieldValuesDisplay fieldValuesDisplay =
					fieldValuesDisplayMap.get(dataSourceFieldName);

				if (fieldValuesDisplay != null) {
					fieldValues = fieldValuesDisplay.getValues();
				}

				return new DataSourceMappingDisplay(
					dataSourceFieldName, fieldValues,
					new FieldMappingValuesDisplay(
						item, fieldsMap.get(item.getFieldName())),
					Collections.emptyList());
			});
	}

	@GET
	@Path("/{id}/progress")
	public Map<String, DataSourceProgress> getDataSourceProgress(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		return contactsEngineClient.getDataSourceProgressMap(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id);
	}

	@GET
	@Path("/{id}/delete_preview")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public Map<Integer, Integer> getDeletePreview(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return HashMapBuilder.put(
			FaroConstants.TYPE_INDIVIDUAL,
			() -> {
				Results<Individual> individualResults =
					contactsEngineClient.getIndividuals(
						faroProject, null, null, id, null, null, null, null,
						null, null, false, 1, 0, null);

				return individualResults.getTotal();
			}
		).put(
			FaroConstants.TYPE_SEGMENT_INDIVIDUALS,
			() -> {
				Results<IndividualSegment> individualSegmentResults =
					contactsEngineClient.getIndividualSegments(
						faroProject, null, id, null, null, null, null, null,
						IndividualSegment.Status.ACTIVE.name(), 1, 0, null);

				return individualSegmentResults.getTotal();
			}
		).build();
	}

	@Path("/{id}/groups_by_ids")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<DXPGroupDisplay> getDXPGroupDisplays(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@DefaultValue(StringPool.BLANK) @FormParam("groupIds") FaroParam
				<List<Long>> groupIdsFaroParam)
		throws Exception {

		return TransformUtil.transform(
			contactsEngineClient.getDataSourceDXPGroups(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				groupIdsFaroParam.getValue()),
			DXPGroupDisplay::new);
	}

	@Path("/{id}/user_groups_by_ids")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<DXPUserGroupDisplay> getDXPUserGroupDisplays(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@DefaultValue(StringPool.BLANK) @FormParam("userGroupIds") FaroParam
				<List<Long>> userGroupIdsFaroParam)
		throws Exception {

		return TransformUtil.transform(
			contactsEngineClient.getDataSourceDXPUserGroups(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				userGroupIdsFaroParam.getValue()),
			DXPUserGroupDisplay::new);
	}

	@Override
	public int[] getEntityTypes() {
		return _ENTITY_TYPES.clone();
	}

	@GET
	@Path("/field_values")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<FieldValuesDisplay> getFieldValuesDisplays(
			@PathParam("groupId") long groupId, @QueryParam("id") String id,
			@QueryParam("fileVersionId") long fileVersionId,
			@QueryParam("fieldName") String fieldName,
			@DefaultValue(FieldMappingConstants.CONTEXT_DEMOGRAPHICS)
			@QueryParam("context")
			String context,
			@QueryParam("count") int count)
		throws Exception {

		List<DataSourceField> dataSourceFields = null;

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		if (Validator.isNotNull(id)) {
			DataSource dataSource = contactsEngineClient.getDataSource(
				faroProject, id);

			Provider provider = dataSource.getProvider();

			String providerType = provider.getType();

			if (providerType.equals(CSVProvider.TYPE)) {
				Repository repository =
					_portletFileRepository.getPortletRepository(
						groupId, ContactsConstants.SERVICE_NAME);

				DLFileEntry dlFileEntry =
					_dlFileEntryLocalService.fetchFileEntry(
						groupId, repository.getDlFolderId(), id);

				if (dlFileEntry != null) {
					DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

					fileVersionId = dlFileVersion.getFileVersionId();
				}
			}
		}

		if (fileVersionId > 0) {
			dataSourceFields = _contactsCSVHelper.getDataSourceFields(
				fileVersionId, fieldName, count, true);
		}
		else {
			dataSourceFields = contactsEngineClient.getDataSourceFields(
				faroProject, id, context, count);
		}

		return TransformUtil.transform(
			dataSourceFields,
			dataSourceField -> {
				if (Validator.isNotNull(fieldName) &&
					!StringUtil.equals(dataSourceField.getName(), fieldName)) {

					return null;
				}

				return new FieldValuesDisplay(
					dataSourceField.getName(), dataSourceField.getValues());
			});
	}

	@GET
	@Path("/{id}/groups")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getGroups(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@DefaultValue("-1") @QueryParam("parentGroupId") long parentGroupId,
			@DefaultValue("true") @QueryParam("site") boolean site,
			@DefaultValue(StringPool.BLANK) @QueryParam("name") String name,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<DXPGroup> results = contactsEngineClient.getDataSourceDXPGroups(
			faroProject, id, parentGroupId, site, name, cur, delta);

		Function<DXPGroup, DXPGroupDisplay> function = DXPGroupDisplay::new;

		if (results.getTotal() > 0) {
			return new FaroResultsDisplay(results, function);
		}

		results = contactsEngineClient.getDataSourceDXPGroups(
			faroProject, id, parentGroupId, site, null, cur, delta);

		return new FaroResultsDisplay(results, function, true);
	}

	@Path("/{id}/liferay/sync_counts")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public LiferaySyncCountsDisplay getLiferaySyncCountsDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@DefaultValue(StringPool.BLANK) @FormParam("contactsConfiguration")
				FaroParam<LiferayProvider.ContactsConfiguration>
					contactsConfigurationFaroParam)
		throws Exception {

		return new LiferaySyncCountsDisplay(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
			contactsConfigurationFaroParam.getValue(), contactsEngineClient);
	}

	@GET
	@Path("/{type}/oauth_request_token_credentials")
	public Credentials getOAuthRequestTokenCredentials(
		@PathParam("groupId") long groupId, @PathParam("type") String type,
		@QueryParam("baseURL") String baseURL,
		@QueryParam("oAuthConsumerKey") String oAuthConsumerKey,
		@QueryParam("oAuthConsumerSecret") String oAuthConsumerSecret,
		@DefaultValue(StringPool.BLANK) @QueryParam("oAuthCallbackURL") String
			oAuthCallbackURL) {

		baseURL = getURL(baseURL);

		oAuthConsumerKey = oAuthConsumerKey.trim();
		oAuthConsumerSecret = oAuthConsumerSecret.trim();

		if (type.equals("liferay")) {
			return OAuthUtil.getLiferayOAuthCredentials(
				baseURL, oAuthConsumerKey, oAuthConsumerSecret,
				oAuthCallbackURL);
		}
		else if (type.equals("salesforce")) {
			return OAuthUtil.getSalesforceOAuthCredentials(
				baseURL, oAuthConsumerKey, oAuthConsumerSecret,
				oAuthCallbackURL);
		}

		return null;
	}

	@Path("/{id}/organizations_by_ids")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<DXPOrganizationDisplay> getOrganizations(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@DefaultValue(StringPool.BLANK) @FormParam("organizationIds")
				FaroParam<List<Long>> organizationIdsFaroParam)
		throws Exception {

		return TransformUtil.transform(
			contactsEngineClient.getDataSourceDXPOrganizations(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				organizationIdsFaroParam.getValue()),
			DXPOrganizationDisplay::new);
	}

	@GET
	@Path("/{id}/organizations")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getOrganizations(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@DefaultValue("-1") @QueryParam("parentOrganizationId") long
				parentOrganizationId,
			@DefaultValue(StringPool.BLANK) @QueryParam("name") String name,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<DXPOrganization> results =
			contactsEngineClient.getDataSourceDXPOrganizations(
				faroProject, id, parentOrganizationId, name, cur, delta);

		Function<DXPOrganization, DXPOrganizationDisplay> function =
			DXPOrganizationDisplay::new;

		if (results.getTotal() > 0) {
			return new FaroResultsDisplay(results, function);
		}

		results = contactsEngineClient.getDataSourceDXPOrganizations(
			faroProject, id, parentOrganizationId, null, cur, delta);

		return new FaroResultsDisplay(results, function, true);
	}

	@GET
	@Path("/token")
	@Produces(MediaType.TEXT_PLAIN)
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public String getToken(
			@Context UriInfo uriInfo, @PathParam("groupId") long groupId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return getToken(uriInfo, null, faroProject.getFaroProjectId());
	}

	@GET
	@Path("/{id}/token")
	@Produces(MediaType.TEXT_PLAIN)
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public String getToken(
			@Context UriInfo uriInfo, @PathParam("groupId") long groupId,
			@PathParam("id") String id)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return getToken(uriInfo, id, faroProject.getFaroProjectId());
	}

	@GET
	@Path("/{id}/user_groups")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getUserGroups(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("name") String name, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta)
		throws Exception {

		Results<DXPUserGroup> results =
			contactsEngineClient.getDataSourceDXPUserGroups(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id,
				name, cur, delta);

		Function<DXPUserGroup, DXPUserGroupDisplay> function =
			DXPUserGroupDisplay::new;

		return new FaroResultsDisplay(results, function);
	}

	@PATCH
	@Path("/{id}/csv")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay patchTypeCSV(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("name") String name,
			@FormParam("fileVersionId") long fileVersionId,
			@FormParam("event") Event event, @FormParam("status") String status,
			@DefaultValue(StringPool.BLANK) @FormParam("fieldMappingMaps")
				FaroParam<List<FieldMappingMap>> fieldMappingMapsFaroParam)
		throws Exception {

		return update(
			groupId, id, null, name, null, new CSVProvider(), CSVProvider.TYPE,
			fileVersionId, event, status, fieldMappingMapsFaroParam.getValue(),
			true);
	}

	@PATCH
	@Path("/{id}/liferay")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay patchTypeLiferay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("credentials") Credentials credentials,
			@FormParam("name") String name, @FormParam("url") String url,
			@DefaultValue(StringPool.BLANK) @FormParam("analyticsConfiguration")
				FaroParam<LiferayProvider.AnalyticsConfiguration>
					analyticsConfigurationFaroParam,
			@DefaultValue(StringPool.BLANK) @FormParam("contactsConfiguration")
				FaroParam<LiferayProvider.ContactsConfiguration>
					contactsConfigurationFaroParam,
			@FormParam("status") String status,
			@DefaultValue(StringPool.BLANK) @FormParam("fieldMappingMaps")
				FaroParam<List<FieldMappingMap>> fieldMappingMapsFaroParam)
		throws Exception {

		LiferayProvider liferayProvider = null;

		LiferayProvider.AnalyticsConfiguration analyticsConfiguration =
			analyticsConfigurationFaroParam.getValue();
		LiferayProvider.ContactsConfiguration contactsConfiguration =
			contactsConfigurationFaroParam.getValue();

		if ((analyticsConfiguration != null) &&
			(contactsConfiguration != null)) {

			liferayProvider = new LiferayProvider();

			liferayProvider.setAnalyticsConfiguration(analyticsConfiguration);
			liferayProvider.setContactsConfiguration(contactsConfiguration);
		}
		else if ((analyticsConfiguration != null) ||
				 (contactsConfiguration != null)) {

			DataSource dataSource = contactsEngineClient.getDataSource(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id);

			liferayProvider = (LiferayProvider)dataSource.getProvider();

			if (analyticsConfiguration != null) {
				liferayProvider.setAnalyticsConfiguration(
					analyticsConfiguration);
			}

			if (contactsConfiguration != null) {
				liferayProvider.setContactsConfiguration(contactsConfiguration);
			}
		}

		return update(
			groupId, id, credentials, name, url, liferayProvider,
			LiferayProvider.TYPE, 0, null, status,
			fieldMappingMapsFaroParam.getValue(), true);
	}

	@PATCH
	@Path("/{id}/salesforce")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay patchTypeSalesforce(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("credentials") Credentials credentials,
			@FormParam("name") String name, @FormParam("url") String url,
			@DefaultValue(StringPool.BLANK) @FormParam("accountsConfiguration")
				FaroParam<SalesforceProvider.AccountsConfiguration>
					accountsConfigurationFaroParam,
			@DefaultValue(StringPool.BLANK) @FormParam("contactsConfiguration")
				FaroParam<SalesforceProvider.ContactsConfiguration>
					contactsConfigurationFaroParam,
			@FormParam("status") String status)
		throws Exception {

		SalesforceProvider salesforceProvider = null;

		SalesforceProvider.AccountsConfiguration accountsConfiguration =
			accountsConfigurationFaroParam.getValue();
		SalesforceProvider.ContactsConfiguration contactsConfiguration =
			contactsConfigurationFaroParam.getValue();

		if ((accountsConfiguration != null) &&
			(contactsConfiguration != null)) {

			salesforceProvider = new SalesforceProvider();

			salesforceProvider.setAccountsConfiguration(accountsConfiguration);
			salesforceProvider.setContactsConfiguration(contactsConfiguration);
		}
		else if ((accountsConfiguration != null) ||
				 (contactsConfiguration != null)) {

			DataSource dataSource = contactsEngineClient.getDataSource(
				faroProjectLocalService.getFaroProjectByGroupId(groupId), id);

			salesforceProvider = (SalesforceProvider)dataSource.getProvider();

			if (accountsConfiguration != null) {
				salesforceProvider.setAccountsConfiguration(
					accountsConfiguration);
			}

			if (contactsConfiguration != null) {
				salesforceProvider.setContactsConfiguration(
					contactsConfiguration);
			}
		}

		return update(
			groupId, id, credentials, name, url, salesforceProvider,
			SalesforceProvider.TYPE, 0, null, status, null, true);
	}

	@Override
	public FaroResultsDisplay search(
			long groupId, FaroSearchContext faroSearchContext)
		throws Exception {

		return search(
			groupId, StringPool.BLANK, faroSearchContext.getQuery(), null, null,
			faroSearchContext.getCur(), faroSearchContext.getDelta(),
			faroSearchContext.getOrderByFields());
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay search(
			@PathParam("groupId") long groupId,
			@QueryParam("faroEntityId") String faroEntityId,
			@QueryParam("query") String query, @QueryParam("name") String name,
			@DefaultValue(StringPool.BLANK) @QueryParam("states") FaroParam
				<List<String>> statesFaroParam,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, faroEntityId, query, name, statesFaroParam.getValue(), cur,
			delta, orderByFieldsFaroParam.getValue());
	}

	@GET
	@Path("/channels")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByChannels(
			@PathParam("groupId") long groupId,
			@DefaultValue(StringPool.BLANK) @QueryParam("channelIds") FaroParam
				<List<String>> channelIdsFaroParam)
		throws PortalException {

		List<String> channelIds = channelIdsFaroParam.getValue();

		if (ListUtil.isNull(channelIds)) {
			return new FaroResultsDisplay();
		}

		Results<DataSource> dataSourceResults =
			contactsEngineClient.getDataSources(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelIds);

		Function<DataSource, DataSourceDisplay> function =
			dataSource -> new DataSourceDisplay(groupId, dataSource);

		return new FaroResultsDisplay(dataSourceResults, function);
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
			@PathParam("groupId") long groupId,
			@FormParam("faroEntityId") String faroEntityId,
			@FormParam("query") String query, @FormParam("name") String name,
			@DefaultValue(StringPool.BLANK) @FormParam("states") FaroParam
				<List<String>> statesFaroParam,
			@FormParam("cur") int cur, @FormParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @FormParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		return search(
			groupId, faroEntityId, query, name, statesFaroParam.getValue(), cur,
			delta, orderByFieldsFaroParam.getValue());
	}

	@Path("/{id}/csv")
	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay updateTypeCSV(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("name") String name,
			@FormParam("fileVersionId") long fileVersionId,
			@FormParam("event") Event event, @FormParam("status") String status,
			@DefaultValue(StringPool.BLANK) @FormParam("fieldMappingMaps")
				FaroParam<List<FieldMappingMap>> fieldMappingMapsFaroParam)
		throws Exception {

		return update(
			groupId, id, null, name, null, new CSVProvider(), CSVProvider.TYPE,
			fileVersionId, event, status, fieldMappingMapsFaroParam.getValue(),
			false);
	}

	@Path("/{id}/liferay")
	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay updateTypeLiferay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("credentials") Credentials credentials,
			@FormParam("name") String name, @FormParam("url") String url,
			@DefaultValue(StringPool.BLANK) @FormParam("analyticsConfiguration")
				FaroParam<LiferayProvider.AnalyticsConfiguration>
					analyticsConfigurationFaroParam,
			@DefaultValue(StringPool.BLANK) @FormParam("contactsConfiguration")
				FaroParam<LiferayProvider.ContactsConfiguration>
					contactsConfigurationFaroParam,
			@FormParam("status") String status,
			@DefaultValue(StringPool.BLANK) @FormParam("fieldMappingMaps")
				FaroParam<List<FieldMappingMap>> fieldMappingMapsFaroParam)
		throws Exception {

		LiferayProvider liferayProvider = new LiferayProvider();

		liferayProvider.setAnalyticsConfiguration(
			analyticsConfigurationFaroParam.getValue());
		liferayProvider.setContactsConfiguration(
			contactsConfigurationFaroParam.getValue());

		return update(
			groupId, id, credentials, name, url, liferayProvider,
			LiferayProvider.TYPE, 0, null, status,
			fieldMappingMapsFaroParam.getValue(), false);
	}

	@Path("/{id}/salesforce")
	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public DataSourceDisplay updateTypeSalesforce(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("credentials") Credentials credentials,
			@FormParam("name") String name, @FormParam("url") String url,
			@DefaultValue(StringPool.BLANK) @FormParam("accountsConfiguration")
				FaroParam<SalesforceProvider.AccountsConfiguration>
					accountsConfigurationFaroParam,
			@DefaultValue(StringPool.BLANK) @FormParam("contactsConfiguration")
				FaroParam<SalesforceProvider.ContactsConfiguration>
					contactsConfigurationFaroParam,
			@FormParam("status") String status)
		throws Exception {

		SalesforceProvider salesforceProvider = new SalesforceProvider();

		salesforceProvider.setAccountsConfiguration(
			accountsConfigurationFaroParam.getValue());
		salesforceProvider.setContactsConfiguration(
			contactsConfigurationFaroParam.getValue());

		return update(
			groupId, id, credentials, name, url, salesforceProvider,
			SalesforceProvider.TYPE, 0, null, status, null, false);
	}

	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/upload/{fileName}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public long uploadCSV(
			@PathParam("groupId") long groupId,
			@PathParam("fileName") String fileName, File file)
		throws Exception {

		return uploadCSV(groupId, null, fileName, file);
	}

	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/{id}/upload/{fileName}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public long uploadCSV(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@PathParam("fileName") String fileName, File file)
		throws Exception {

		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			_clamAVScanner.scan(fileInputStream);
		}

		_contactsCSVHelper.validateCSV(file);

		return _contactsCSVHelper.addContactsCSV(
			id, groupId, getUserId(), fileName, file);
	}

	protected void addCSVIndividuals(
			FaroProject faroProject, String channelId, String dataSourceId,
			long fileVersionId, List<String> individualSegmentIds)
		throws Exception {

		IndividualSegment individualSegment =
			contactsEngineClient.addIndividualSegment(
				faroProject, getUserId(), channelId, null, false,
				String.valueOf(fileVersionId),
				IndividualSegment.Type.STATIC.name(),
				IndividualSegment.Status.INACTIVE.name());

		individualSegmentIds.add(individualSegment.getId());

		contactsEngineClient.addCSVIndividuals(
			faroProject,
			_contactsCSVHelper.getIndividualFieldsMaps(fileVersionId),
			dataSourceId, individualSegmentIds);

		_contactsCSVHelper.updateFileEntry(
			getUserId(), fileVersionId, dataSourceId);
	}

	protected DataSourceDisplay create(
			long groupId, Credentials credentials, Provider provider,
			String name, String url, Event event, String status)
		throws Exception {

		validateName(name);
		validateProvider(provider);

		url = getURL(url);

		credentials = processCredentials(provider.getType(), credentials, url);

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		DataSource dataSource = contactsEngineClient.addDataSource(
			faroProject, credentials, getUserId(), name, url, provider, event,
			status);

		return new DataSourceDisplay(groupId, dataSource);
	}

	protected void createFieldMappings(
		FaroProject faroProject, String dataSourceId, String context,
		String ownerType, List<FieldMappingMap> fieldMappingMaps) {

		if (ListUtil.isEmpty(fieldMappingMaps)) {
			return;
		}

		validateFieldMappingMaps(fieldMappingMaps);

		List<FieldMappingMap> newFieldMappingMaps =
			FieldMappingUtil.getNewFieldMappingMaps(
				contactsEngineClient, faroProject, context, fieldMappingMaps);

		contactsEngineClient.addFieldMappings(
			faroProject, dataSourceId, context, ownerType, newFieldMappingMaps);

		List<FieldMappingMap> existingFieldMappingMaps = new ArrayList<>(
			fieldMappingMaps);

		existingFieldMappingMaps.removeAll(newFieldMappingMaps);

		contactsEngineClient.patchFieldMappings(
			faroProject, dataSourceId, context, ownerType,
			existingFieldMappingMaps);
	}

	protected String getToken(
			UriInfo uriInfo, String dataSourceId, long faroProjectId)
		throws Exception {

		String url = StringUtil.replaceFirst(
			String.valueOf(uriInfo.getRequestUri()), "/token", "/connect");

		if (dataSourceId != null) {
			url = StringUtil.replaceLast(
				url, StringPool.SLASH + dataSourceId, StringPool.BLANK);
		}

		String json = JSONUtil.put(
			"token", _tokenManager.getToken(dataSourceId, faroProjectId)
		).put(
			"url", url
		).toString();

		return Base64.encode(json.getBytes(StandardCharsets.UTF_8));
	}

	protected String getURL(String url) {
		if (url == null) {
			return null;
		}

		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}

		return url.trim();
	}

	protected Credentials processCredentials(
			String providerType, Credentials credentials, String url)
		throws Exception {

		if (credentials == null) {
			return null;
		}

		String type = credentials.getType();

		if (type.equals(OAuth1Credentials.TYPE)) {
			OAuth1Credentials oAuth1Credentials =
				(OAuth1Credentials)credentials;

			return OAuthUtil.getOAuth10aCredentials(
				url, oAuth1Credentials.getOAuthConsumerKey(),
				oAuth1Credentials.getOAuthConsumerSecret(),
				oAuth1Credentials.getOAuthToken(),
				oAuth1Credentials.getOAuthTokenSecret(),
				oAuth1Credentials.getOAuthVerifier());
		}
		else if (type.equals(OAuth2Credentials.TYPE)) {
			OAuth2Credentials oAuth2Credentials =
				(OAuth2Credentials)credentials;

			return OAuthUtil.getOAuth20Credentials(
				url, providerType, oAuth2Credentials.getOAuthClientId(),
				oAuth2Credentials.getOAuthClientSecret(),
				oAuth2Credentials.getOAuthCode(),
				oAuth2Credentials.getOAuthCallbackURL());
		}

		return credentials;
	}

	@SuppressWarnings("unchecked")
	protected FaroResultsDisplay search(
			long groupId, String faroEntityId, String query, String name,
			List<String> states, int cur, int delta,
			List<OrderByField> orderByFields)
		throws Exception {

		Results<DataSource> results = contactsEngineClient.getDataSources(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			faroEntityId, query, name, null, states, cur, delta, orderByFields);

		Function<DataSource, DataSourceDisplay> function =
			dataSource -> new DataSourceDisplay(groupId, dataSource);

		return new FaroResultsDisplay(results, function);
	}

	protected DataSourceDisplay update(
			long groupId, String id, Credentials credentials, String name,
			String url, Provider provider, String providerType,
			long fileVersionId, Event event, String status,
			List<FieldMappingMap> fieldMappingMaps, boolean patch)
		throws Exception {

		validateFieldMappingMaps(fieldMappingMaps);
		validateProvider(provider);

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		credentials = processCredentials(providerType, credentials, url);

		url = getURL(url);

		updateFieldMappings(faroProject, id, fieldMappingMaps);

		DataSource dataSource = null;

		if (patch) {
			dataSource = contactsEngineClient.patchDataSource(
				faroProject, id, credentials, getUserId(), name, url, provider,
				event, status);
		}
		else {
			dataSource = contactsEngineClient.updateDataSource(
				faroProject, id, credentials, getUserId(), name, url, provider,
				event, status);
		}

		if (fileVersionId > 0) {
			Results<IndividualSegment> results =
				contactsEngineClient.getIndividualSegments(
					faroProject, null, null, null, null,
					String.valueOf(fileVersionId),
					IndividualSegment.Type.STATIC.name(), null,
					IndividualSegment.Status.INACTIVE.name(), 1, 1, null);

			if (results.getTotal() == 0) {
				addCSVIndividuals(
					faroProject, null, id, fileVersionId,
					Collections.emptyList());
			}
		}

		return new DataSourceDisplay(groupId, dataSource);
	}

	protected void updateFieldMappings(
		FaroProject faroProject, String dataSourceId,
		List<FieldMappingMap> fieldMappingMaps) {

		if (ListUtil.isEmpty(fieldMappingMaps)) {
			return;
		}

		List<FieldMappingMap> deletedFieldMappingMaps = new ArrayList<>();

		try {
			Results<FieldMapping> results =
				contactsEngineClient.getFieldMappings(
					faroProject, FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
					dataSourceId, null);

			fieldMappingLoop:
			for (FieldMapping fieldMapping : results.getItems()) {
				Iterator<FieldMappingMap> iterator =
					fieldMappingMaps.iterator();

				while (iterator.hasNext()) {
					FieldMappingMap fieldMappingMap = iterator.next();

					String dataSourceFieldName =
						fieldMappingMap.getDataSourceFieldName();
					String name = fieldMappingMap.getName();

					if (dataSourceFieldName.equals(
							fieldMapping.getDataSourceFieldName(
								dataSourceId)) &&
						name.equals(fieldMapping.getFieldName())) {

						iterator.remove();

						continue fieldMappingLoop;
					}
				}

				deletedFieldMappingMaps.add(
					new FieldMappingMap(
						StringPool.BLANK, fieldMapping.getFieldName()));
			}
		}
		catch (InvalidFilterException invalidFilterException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Could not load any field mappings",
					invalidFilterException);
			}
		}

		fieldMappingMaps.addAll(deletedFieldMappingMaps);

		contactsEngineClient.patchFieldMappings(
			faroProject, dataSourceId,
			FieldMappingConstants.CONTEXT_DEMOGRAPHICS,
			FieldMappingConstants.OWNER_TYPE_INDIVIDUAL, fieldMappingMaps);
	}

	protected void validateFieldMappingMaps(
		List<FieldMappingMap> fieldMappingMaps) {

		if (fieldMappingMaps == null) {
			return;
		}

		for (FieldMappingMap fieldMappingMap : fieldMappingMaps) {
			_fieldMappingController.validateCreate(fieldMappingMap.getName());
		}
	}

	protected void validateName(String name) {
		if (Validator.isNull(name)) {
			throw new FaroException("Name cannot be blank");
		}
	}

	protected void validateProvider(Provider provider) {
		if (provider == null) {
			return;
		}

		String type = provider.getType();

		if (!type.equals(LiferayProvider.TYPE)) {
			return;
		}

		List<LiferayProvider.Container> containers = new ArrayList<>();
		List<String> ids = new ArrayList<>();

		LiferayProvider liferayProvider = (LiferayProvider)provider;

		LiferayProvider.AnalyticsConfiguration analyticsConfiguration =
			liferayProvider.getAnalyticsConfiguration();

		if (analyticsConfiguration != null) {
			containers.addAll(analyticsConfiguration.getSites());
		}

		LiferayProvider.ContactsConfiguration contactsConfiguration =
			liferayProvider.getContactsConfiguration();

		if (contactsConfiguration != null) {
			containers.addAll(contactsConfiguration.getOrganizations());
			containers.addAll(contactsConfiguration.getUserGroups());
		}

		for (LiferayProvider.Container container : containers) {
			if (!Validator.isNumber(container.getId())) {
				throw new FaroException(
					"Id must be a number: " + container.getId());
			}

			if (ids.contains(container.getId())) {
				throw new FaroException(
					"Id must be unique: " + container.getId());
			}

			ids.add(container.getId());
		}
	}

	private static final int[] _ENTITY_TYPES = {FaroConstants.TYPE_DATA_SOURCE};

	private static final Log _log = LogFactoryUtil.getLog(
		DataSourceController.class);

	@Reference
	private ClamAVScanner _clamAVScanner;

	@Reference
	private ContactsCSVHelper _contactsCSVHelper;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private FieldMappingController _fieldMappingController;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private PortletFileRepository _portletFileRepository;

	private final TokenManager _tokenManager = new TokenManager();

	private static class TokenManager {

		public void clearToken(String token) {
			_tokens.removeValue(token);
		}

		public void clearToken(String dataSourceId, long faroProjectId) {
			_tokens.remove(_getKey(dataSourceId, faroProjectId));
		}

		public String getDataSourceId(String token) {
			String key = _tokens.getKey(token);

			if (key == null) {
				return null;
			}

			String[] parts = StringUtil.split(key, StringPool.POUND);

			String dataSourceId = parts[0];

			if (Validator.isNull(dataSourceId)) {
				return null;
			}

			return dataSourceId;
		}

		public Long getFaroProjectId(String token) {
			String key = _tokens.getKey(token);

			if (key == null) {
				return null;
			}

			String[] parts = StringUtil.split(
				_tokens.getKey(token), StringPool.POUND);

			return GetterUtil.getLong(parts[1]);
		}

		public String getToken(String dataSourceId, long faroProjectId) {
			return _tokens.computeIfAbsent(
				_getKey(dataSourceId, faroProjectId),
				key -> {
					UUID uuid = UUID.randomUUID();

					return uuid.toString();
				});
		}

		public void setDataSourceId(String dataSourceId, String token) {
			if (!_tokens.containsValue(token)) {
				return;
			}

			Long faroProjectId = getFaroProjectId(token);

			clearToken(token);

			_tokens.put(_getKey(dataSourceId, faroProjectId), token);
		}

		private String _getKey(String dataSourceId, long faroProjectId) {
			return dataSourceId + StringPool.POUND + faroProjectId;
		}

		private static final BidiMap<String, String> _tokens =
			new DualHashBidiMap<>();

	}

}