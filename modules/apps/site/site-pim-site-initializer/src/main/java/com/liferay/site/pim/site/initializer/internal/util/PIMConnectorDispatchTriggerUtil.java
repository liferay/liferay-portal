/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalServiceUtil;
import com.liferay.dispatch.service.DispatchTriggerLocalServiceUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.site.pim.site.initializer.internal.dispatch.executor.PIMConnectorDispatchTaskExecutor;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Stefano Motta
 */
public class PIMConnectorDispatchTriggerUtil {

	public static DispatchTrigger addDispatchTrigger(ObjectEntry objectEntry)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			DispatchTriggerLocalServiceUtil.addDispatchTrigger(
				_getExternalReferenceCode(
					objectEntry.getExternalReferenceCode()),
				objectEntry.getUserId(), PIMConnectorDispatchTaskExecutor.KEY,
				UnicodePropertiesBuilder.put(
					"pimConnectorObjectEntryId", objectEntry.getObjectEntryId()
				).build(),
				_getName(objectEntry), false);

		Company company = CompanyLocalServiceUtil.getCompany(
			objectEntry.getCompanyId());

		TimeZone timeZone = company.getTimeZone();

		Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone);

		return DispatchTriggerLocalServiceUtil.updateDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId(),
			MapUtil.getBoolean(objectEntry.getValues(), "active"),
			"0 0 0 * * ?", DispatchTaskClusterMode.SINGLE_NODE_PERSISTED, 0, 0,
			0, 0, 0, true, false, calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			timeZone.getID());
	}

	public static void deleteDispatchTrigger(ObjectEntry objectEntry)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			DispatchTriggerLocalServiceUtil.
				fetchDispatchTriggerByExternalReferenceCode(
					_getExternalReferenceCode(
						objectEntry.getExternalReferenceCode()),
					objectEntry.getCompanyId());

		if (dispatchTrigger != null) {
			DispatchTriggerLocalServiceUtil.deleteDispatchTrigger(
				dispatchTrigger);
		}
	}

	public static DispatchTrigger fetchDispatchTrigger(
		long companyId, String objectEntryExternalReferenceCode) {

		return DispatchTriggerLocalServiceUtil.
			fetchDispatchTriggerByExternalReferenceCode(
				_getExternalReferenceCode(objectEntryExternalReferenceCode),
				companyId);
	}

	public static DispatchLog fetchLatestDispatchLog(
		long companyId, String objectEntryExternalReferenceCode) {

		DispatchTrigger dispatchTrigger = fetchDispatchTrigger(
			companyId, objectEntryExternalReferenceCode);

		if (dispatchTrigger == null) {
			return null;
		}

		DispatchLog failedDispatchLog =
			DispatchLogLocalServiceUtil.fetchLatestDispatchLog(
				dispatchTrigger.getDispatchTriggerId(),
				DispatchTaskStatus.FAILED);
		DispatchLog successfulDispatchLog =
			DispatchLogLocalServiceUtil.fetchLatestDispatchLog(
				dispatchTrigger.getDispatchTriggerId(),
				DispatchTaskStatus.SUCCESSFUL);

		if (failedDispatchLog == null) {
			return successfulDispatchLog;
		}

		if (successfulDispatchLog == null) {
			return failedDispatchLog;
		}

		Date startDate = failedDispatchLog.getStartDate();

		if (startDate.after(successfulDispatchLog.getStartDate())) {
			return failedDispatchLog;
		}

		return successfulDispatchLog;
	}

	public static DispatchTrigger getDispatchTrigger(ObjectEntry objectEntry)
		throws PortalException {

		return DispatchTriggerLocalServiceUtil.
			getDispatchTriggerByExternalReferenceCode(
				_getExternalReferenceCode(
					objectEntry.getExternalReferenceCode()),
				objectEntry.getCompanyId());
	}

	public static void updateDispatchTrigger(ObjectEntry objectEntry)
		throws PortalException {

		DispatchTrigger dispatchTrigger = getDispatchTrigger(objectEntry);

		String name = _getName(objectEntry);

		if (!Objects.equals(dispatchTrigger.getName(), name)) {
			dispatchTrigger =
				DispatchTriggerLocalServiceUtil.updateDispatchTrigger(
					dispatchTrigger.getDispatchTriggerId(),
					dispatchTrigger.getDispatchTaskSettingsUnicodeProperties(),
					name);
		}

		boolean active = MapUtil.getBoolean(objectEntry.getValues(), "active");

		if (dispatchTrigger.isActive() == active) {
			return;
		}

		Calendar endDateCalendar = _getCalendar(
			dispatchTrigger.getTimeZoneEndDate());
		Calendar startDateCalendar = _getCalendar(
			dispatchTrigger.getTimeZoneStartDate());

		DispatchTriggerLocalServiceUtil.updateDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId(), active,
			dispatchTrigger.getCronExpression(),
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode()),
			endDateCalendar.get(Calendar.MONTH),
			endDateCalendar.get(Calendar.DATE),
			endDateCalendar.get(Calendar.YEAR),
			endDateCalendar.get(Calendar.HOUR_OF_DAY),
			endDateCalendar.get(Calendar.MINUTE),
			dispatchTrigger.getEndDate() == null,
			dispatchTrigger.isOverlapAllowed(),
			startDateCalendar.get(Calendar.MONTH),
			startDateCalendar.get(Calendar.DATE),
			startDateCalendar.get(Calendar.YEAR),
			startDateCalendar.get(Calendar.HOUR_OF_DAY),
			startDateCalendar.get(Calendar.MINUTE),
			dispatchTrigger.getTimeZoneId());
	}

	private static Calendar _getCalendar(Date date) {
		Calendar calendar = CalendarFactoryUtil.getCalendar();

		if (date != null) {
			calendar.setTime(date);
		}

		return calendar;
	}

	private static String _getExternalReferenceCode(
		String objectEntryExternalReferenceCode) {

		return "L_PIM_CONNECTOR_" + objectEntryExternalReferenceCode;
	}

	private static String _getName(ObjectEntry objectEntry) {
		return StringBundler.concat(
			MapUtil.getString(objectEntry.getValues(), "name"), " (",
			objectEntry.getObjectEntryId(), ")");
	}

}