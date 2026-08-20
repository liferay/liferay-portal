/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneComparator;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * @author Daniel Raposo
 */
public class PublishProcessDisplayContext {

	public PublishProcessDisplayContext(Group liveGroup, Locale locale) {
		_liveGroup = liveGroup;
		_locale = locale;
	}

	public String getPublishPreviewAPIURL() {
		return ScopeUtil.getAPIURL(_liveGroup, "/publish-preview");
	}

	public String getPublishProcessAPIURL() {
		return ScopeUtil.getAPIURL(_liveGroup, "/publish-processes");
	}

	public String getScheduledPublishProcessAPIURL() {
		return ScopeUtil.getAPIURL(_liveGroup, "/scheduled-publish-processes");
	}

	public JSONArray getTimeZonesJSONArray() {
		Set<TimeZone> timeZones = new TreeSet<>(new TimeZoneComparator());

		for (String timeZoneId : PropsUtil.getArray(PropsKeys.TIME_ZONES)) {
			timeZones.add(TimeZoneUtil.getTimeZone(timeZoneId));
		}

		return JSONUtil.toJSONArray(
			timeZones,
			timeZone -> JSONUtil.put(
				"label", _getTimeZoneLabel(timeZone)
			).put(
				"value", timeZone.getID()
			),
			_log);
	}

	public String getTitle(long scheduledPublishProcessId) {
		if (scheduledPublishProcessId <= 0) {
			return LanguageUtil.get(_locale, "new-publish-process");
		}

		try {
			for (SchedulerResponse schedulerResponse :
					SchedulerEngineHelperUtil.getScheduledJobs(
						StagingUtil.getSchedulerGroupName(
							DestinationNames.LAYOUTS_LOCAL_PUBLISHER,
							_liveGroup.getGroupId()),
						StorageType.PERSISTED)) {

				Message message = schedulerResponse.getMessage();

				if (scheduledPublishProcessId == GetterUtil.getLong(
						message.getPayload())) {

					String description = schedulerResponse.getDescription();

					if (Validator.isBlank(description)) {
						return LanguageUtil.get(
							_locale, "untitled-scheduled-publish-process");
					}

					return description;
				}
			}
		}
		catch (SchedulerException schedulerException) {
			_log.error(
				"Unable to get the scheduled publish process " +
					scheduledPublishProcessId,
				schedulerException);
		}

		return LanguageUtil.get(_locale, "new-publish-process");
	}

	public boolean isCommentsAndRatingsEnabled() {
		return ScopeUtil.isCommentsAndRatingsEnabled(_liveGroup);
	}

	public boolean isLookAndFeelEnabled() {
		return ScopeUtil.isLookAndFeelEnabled(_liveGroup);
	}

	private String _getTimeZoneLabel(TimeZone timeZone) {
		Date date = new Date();

		StringBundler sb = new StringBundler(7);

		sb.append("(UTC");

		int offset = timeZone.getOffset(date.getTime());

		if (offset != 0) {
			sb.append(
				String.format(
					" %+03d:%02d", offset / Time.HOUR,
					Math.abs(offset % Time.HOUR) / Time.MINUTE));
		}

		sb.append(") ");
		sb.append(
			timeZone.getDisplayName(
				timeZone.inDaylightTime(date), TimeZone.LONG, _locale));

		String timeZoneId = timeZone.getID();

		if (timeZoneId.contains("Phoenix")) {
			sb.append(" (");
			sb.append(timeZoneId);
			sb.append(")");
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishProcessDisplayContext.class);

	private final Group _liveGroup;
	private final Locale _locale;

}