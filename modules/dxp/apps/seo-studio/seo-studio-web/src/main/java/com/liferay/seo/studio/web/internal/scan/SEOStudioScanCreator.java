/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.scan;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.text.Format;

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
			Date scheduledScanDate, long seoStudioDomainId, String triggeredBy,
			long userId)
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

		String scheduledScanKey = _getScheduledScanKey(
			scheduledScanDate, seoStudioDomainId);

		ObjectDefinition seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", companyId);

		if ((scheduledScanKey != null) &&
			_hasSEOStudioScanRun(
				companyId, scheduledScanKey,
				seoStudioScanRunObjectDefinition)) {

			return;
		}

		try {
			long accountEntryId = GetterUtil.getLong(
				values.get("r_accountToSEOStudioDomains_accountEntryId"));
			String hostname = GetterUtil.getString(values.get("hostname"));

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(companyId);
			serviceContext.setUserId(userId);

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					ObjectDefinition seoStudioScanObjectDefinition =
						_objectDefinitionLocalService.
							getObjectDefinitionByExternalReferenceCode(
								"L_SEO_STUDIO_SCAN", companyId);

					ObjectEntry seoStudioScanRunObjectEntry =
						_addSEOStudioScanRunObjectEntry(
							accountEntryId, hostname, scheduledScanKey,
							seoStudioDomainId,
							seoStudioScanRunObjectDefinition.
								getObjectDefinitionId(),
							serviceContext, triggeredBy, userId);

					for (String engineKey : enabledEngineKeys) {
						_addSEOStudioScanObjectEntry(
							accountEntryId,
							enginesJSONObject.getJSONObject(engineKey),
							engineKey,
							seoStudioScanObjectDefinition.
								getObjectDefinitionId(),
							seoStudioScanRunObjectEntry.getObjectEntryId(),
							serviceContext, userId);
					}

					return null;
				});
		}
		catch (Throwable throwable) {
			throw new Exception(throwable);
		}
	}

	private void _addSEOStudioScanObjectEntry(
			long accountEntryId, JSONObject engineJSONObject, String engineKey,
			long seoStudioScanObjectDefinitionId, long seoStudioScanRunId,
			ServiceContext serviceContext, long userId)
		throws Exception {

		_objectEntryLocalService.addObjectEntry(
			0, userId, seoStudioScanObjectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_accountToSEOStudioScans_accountEntryId", accountEntryId
			).put(
				"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId",
				seoStudioScanRunId
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

	private ObjectEntry _addSEOStudioScanRunObjectEntry(
			long accountEntryId, String hostname, String scheduledScanKey,
			long seoStudioDomainId, long seoStudioScanRunObjectDefinitionId,
			ServiceContext serviceContext, String triggeredBy, long userId)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, userId, seoStudioScanRunObjectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"name", hostname
			).put(
				"r_accountToSEOStudioScanRuns_accountEntryId", accountEntryId
			).put(
				"r_seoStudioDomainToSEOStudioScanRuns_seoStudioDomainId",
				seoStudioDomainId
			).put(
				"requestDate", new Date()
			).put(
				"scheduledScanKey", scheduledScanKey
			).put(
				"state", "running"
			).put(
				"triggeredBy", triggeredBy
			).put(
				"triggeringUserId", userId
			).build(),
			serviceContext);
	}

	private String _getScheduledScanKey(
		Date scheduledScanDate, long seoStudioDomainId) {

		if (scheduledScanDate == null) {
			return null;
		}

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmm", TimeZoneUtil.getTimeZone("UTC"));

		return StringBundler.concat(
			seoStudioDomainId, StringPool.UNDERLINE,
			format.format(scheduledScanDate));
	}

	private boolean _hasSEOStudioScanRun(
			long companyId, String scheduledScanKey,
			ObjectDefinition seoStudioScanRunObjectDefinition)
		throws Exception {

		return ListUtil.isNotEmpty(
			_objectEntryLocalService.getPrimaryKeys(
				new Long[] {0L}, companyId, 0,
				seoStudioScanRunObjectDefinition.getObjectDefinitionId(),
				_filterFactory.create(
					StringBundler.concat(
						"scheduledScanKey eq '", scheduledScanKey, "'"),
					seoStudioScanRunObjectDefinition),
				false, null, 0, 1, null));
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}