/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.scan;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
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
@Component(service = SEOStudioScanCreator.class)
public class SEOStudioScanCreator {

	public void createScans(
			long seoStudioDomainId, String triggeredBy, long userId)
		throws Exception {

		ObjectEntry seoStudioDomainObjectEntry =
			_objectEntryLocalService.getObjectEntry(seoStudioDomainId);

		Map<String, Serializable> values =
			seoStudioDomainObjectEntry.getValues();

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

		List<String> enabledEngineKeys = TransformUtil.transform(
			enginesJSONObject.keySet(),
			engineKey -> {
				JSONObject engineJSONObject = enginesJSONObject.getJSONObject(
					engineKey);

				if ((engineJSONObject != null) &&
					engineJSONObject.getBoolean("enabled")) {

					return engineKey;
				}

				return null;
			});

		if (ListUtil.isEmpty(enabledEngineKeys)) {
			return;
		}

		long companyId = seoStudioDomainObjectEntry.getCompanyId();

		long accountEntryId = GetterUtil.getLong(
			values.get("r_accountToSEOStudioDomains_accountEntryId"));

		ObjectDefinition seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", companyId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		ObjectEntry seoStudioScanRunObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, userId,
				seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"name", GetterUtil.getString(values.get("hostname"))
				).put(
					"r_accountToSEOStudioScanRuns_accountEntryId",
					accountEntryId
				).put(
					"r_seoStudioDomainToSEOStudioScanRuns_seoStudioDomainId",
					seoStudioDomainId
				).put(
					"requestDate", new Date()
				).put(
					"state", "running"
				).put(
					"triggeredBy", triggeredBy
				).put(
					"triggeringUserId", userId
				).build(),
				serviceContext);

		ObjectDefinition seoStudioScanObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN", companyId);

		for (String engineKey : enabledEngineKeys) {
			JSONObject engineJSONObject = enginesJSONObject.getJSONObject(
				engineKey);

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
					seoStudioScanRunObjectEntry.getObjectEntryId()
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