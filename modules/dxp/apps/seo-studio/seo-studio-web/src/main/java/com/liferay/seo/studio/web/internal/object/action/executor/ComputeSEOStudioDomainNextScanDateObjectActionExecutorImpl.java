/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor;

import com.liferay.object.action.executor.BaseObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.seo.studio.web.internal.util.SEOStudioScanScheduleUtil;

import java.io.Serializable;

import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan McCann
 */
@Component(service = ObjectActionExecutor.class)
public class ComputeSEOStudioDomainNextScanDateObjectActionExecutorImpl
	extends BaseObjectActionExecutor implements ObjectDefinitionScoped {

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return List.of("SEOStudioDomain");
	}

	@Override
	public String getKey() {
		return "compute-seo-studio-domain-next-scan-date";
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

		Date nextScanDate = null;

		if (GetterUtil.getBoolean(values.get("autoScanEnabled"))) {
			nextScanDate = SEOStudioScanScheduleUtil.getNextScanDate(
				Instant.now(),
				GetterUtil.getInteger(values.get("scanDayOfMonth")),
				GetterUtil.getString(values.get("scanDayOfWeek")),
				GetterUtil.getString(values.get("scanFrequency")),
				GetterUtil.getString(values.get("scanTime")),
				GetterUtil.getString(values.get("scanTimeZone")));
		}

		if (Objects.equals(nextScanDate, values.get("nextScanDate"))) {
			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		_objectEntryLocalService.partialUpdateObjectEntry(
			userId, seoStudioDomainId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"nextScanDate", nextScanDate
			).build(),
			serviceContext);
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}