/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor;

import com.liferay.object.action.executor.BaseObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan McCann
 */
@Component(service = ObjectActionExecutor.class)
public class CreateSEOStudioScansObjectActionExecutorImpl
	extends BaseObjectActionExecutor implements ObjectDefinitionScoped {

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return List.of("SEOStudioDomain");
	}

	@Override
	public String getKey() {
		return "create-seo-studio-scans";
	}

	@Override
	protected void doExecute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		long seoStudioDomainId = payloadJSONObject.getLong("classPK");

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			seoStudioDomainId);

		String scanConfigJSON = GetterUtil.getString(values.get("scanConfig"));

		if (Validator.isNull(scanConfigJSON)) {
			return;
		}

		JSONObject scanConfigJSONObject = _jsonFactory.createJSONObject(
			scanConfigJSON);

		JSONObject enginesJSONObject = scanConfigJSONObject.getJSONObject(
			"engines");

		if (enginesJSONObject == null) {
			return;
		}

		ObjectDefinition seoStudioScanObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN", companyId);
		ObjectDefinition seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", companyId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		long accountEntryId = GetterUtil.getLong(
			values.get("r_accountToSEOStudioDomains_accountEntryId"));

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, userId, seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"name", GetterUtil.getString(values.get("hostname"))
			).put(
				"r_accountToSEOStudioScanRuns_accountEntryId", accountEntryId
			).put(
				"r_seoStudioDomainToSEOStudioScanRuns_seoStudioDomainId",
				seoStudioDomainId
			).put(
				"requestDate", new Date()
			).put(
				"state", "running"
			).put(
				"triggeredBy", "manual"
			).put(
				"triggeringUserId", userId
			).build(),
			serviceContext);

		for (String engineKey : enginesJSONObject.keySet()) {
			JSONObject engineJSONObject = enginesJSONObject.getJSONObject(
				engineKey);

			if ((engineJSONObject == null) ||
				!engineJSONObject.getBoolean("enabled")) {

				continue;
			}

			_objectEntryLocalService.addObjectEntry(
				0, userId,
				seoStudioScanObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"r_accountToSEOStudioScans_accountEntryId", accountEntryId
				).put(
					"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId",
					objectEntry.getObjectEntryId()
				).put(
					"scanRange", "full"
				).put(
					"scanScope", "entireDomain"
				).put(
					"scanType", engineKey
				).put(
					"scopeConfig",
					() -> {
						JSONObject scopeConfigJSONObject =
							_jsonFactory.createJSONObject(
								engineJSONObject.toString());

						scopeConfigJSONObject.remove("enabled");

						return scopeConfigJSONObject.toString();
					}
				).build(),
				serviceContext);
		}
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}