/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.engine.client.CerebroEngineClient;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroEmailLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.EmailUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.mail.internet.InternetAddress;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = EmailReportHelper.class)
public class EmailReportHelper {

	public void sendEmail(
			String channelId, String frequency, long groupId, long userId)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		if (faroProject == null) {
			return;
		}

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return;
		}

		Channel channel = null;

		try {
			channel = _contactsEngineClient.getChannel(faroProject, channelId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return;
		}

		int groupIdCount = 0;

		for (Map<String, Object> dataSource : channel.getDataSources()) {
			List<String> groupIds = (List)dataSource.get("groupIds");

			groupIdCount += groupIds.size();
		}

		if (groupIdCount == 0) {
			return;
		}

		FaroChannel faroChannel = null;

		try {
			faroChannel = _faroChannelLocalService.getFaroChannel(
				channelId, group.getGroupId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return;
		}

		JSONObject siteMetricsJSONObject = _jsonFactory.createJSONObject(
			_cerebroEngineClient.getSiteMetrics(
				channelId, faroProject, "D", _getRangeKey(frequency)));

		Calendar calendar = Calendar.getInstance();

		String body = StringUtil.replace(
			_faroEmailLocalService.getTemplate(
				"com/liferay/osb/faro/dependencies/email-" + frequency +
					"-report.html"),
			new String[] {
				"[$BOUNCE_RATE$]", "[$BOUNCE_RATE_TREND$]", "[$COMPANY_NAME$]",
				"[$FREQUENCY$]", "[$FULL_REPORT_URL$]", "[$HEADER_IMAGE_PATH$]",
				"[$LOGO_ICON_URL$]", "[$MONTH$]", "[$PREVIOUS_BOUNCE_RATE$]",
				"[$PREVIOUS_SESSION_DURATION$]",
				"[$PREVIOUS_SESSIONS_PER_VISITOR$]",
				"[$PREVIOUS_UNIQUE_VISITORS$]", "[$PROPERTY_NAME$]",
				"[$SESSION_DURATION$]", "[$SESSION_DURATION_TREND$]",
				"[$SESSIONS_PER_VISITOR$]", "[$SESSIONS_PER_VISITOR_TREND$]",
				"[$UNIQUE_VISITORS$]", "[$UNIQUE_VISITORS_TREND$]",
				"[$WORKSPACE_NAME$]", "[$YEAR$]"
			},
			new String[] {
				_formatPercentage(
					_getMetricValue("bounceRateMetric", siteMetricsJSONObject)),
				_getMetricTrendIconURL(
					"bounceRateMetric", siteMetricsJSONObject),
				faroProject.getAccountName(), _getTemporal(frequency),
				EmailUtil.getWorkspaceURL(group),
				EmailUtil.getEmailBannerURL(frequency),
				EmailUtil.getLiferayLogoIconURL(),
				calendar.getDisplayName(
					Calendar.MONTH, Calendar.LONG, LocaleUtil.ENGLISH),
				_getPreviousMetricValue(
					"bounceRateMetric", siteMetricsJSONObject),
				_getPreviousMetricValue(
					"sessionDurationMetric", siteMetricsJSONObject),
				_getPreviousMetricValue(
					"sessionsPerVisitorMetric", siteMetricsJSONObject),
				_getPreviousMetricValue(
					"visitorsMetric", siteMetricsJSONObject),
				faroChannel.getName(),
				_formatDuration(
					_getMetricValue(
						"sessionDurationMetric", siteMetricsJSONObject)),
				_getMetricTrendIconURL(
					"sessionDurationMetric", siteMetricsJSONObject),
				_formatDecimal(
					_getMetricValue(
						"sessionsPerVisitorMetric", siteMetricsJSONObject)),
				_getMetricTrendIconURL(
					"sessionsPerVisitorMetric", siteMetricsJSONObject),
				_formatNumber(
					_getMetricValue("visitorsMetric", siteMetricsJSONObject)),
				_getMetricTrendIconURL("visitorsMetric", siteMetricsJSONObject),
				faroProject.getName(),
				String.valueOf(calendar.get(Calendar.YEAR))
			});

		_mailService.sendEmail(
			new MailMessage(
				new InternetAddress("ac@liferay.com", "Analytics Cloud"),
				new InternetAddress(user.getEmailAddress(), user.getFullName()),
				"Analytics Cloud: Your " + StringUtils.capitalize(frequency) +
					" Report",
				body, true));
	}

	private String _formatDecimal(String value) {
		Double doubleValue = GetterUtil.getDouble(value);

		return String.format("%.2f", doubleValue);
	}

	private String _formatDuration(String value) {
		String duration = DurationFormatUtils.formatDuration(
			Math.round(GetterUtil.getDouble(value)), "d'd'HH'h'mm'm'ss's'",
			true);

		if (duration.startsWith("0d")) {
			duration = duration.substring(2);
		}

		if (duration.startsWith("00h")) {
			duration = duration.substring(3);
		}

		if (duration.startsWith("00m")) {
			duration = duration.substring(3);
		}

		if (duration.startsWith("00s")) {
			duration = "0s";
		}

		return duration;
	}

	private String _formatNumber(String value) {
		Long longValue = GetterUtil.getLong(value);

		if (longValue < 1000) {
			return value;
		}

		int exp = (int)(Math.log(longValue) / Math.log(1000));

		return String.format(
			"%.2f%c", longValue / Math.pow(1000, exp),
			"kMGTPE".charAt(exp - 1));
	}

	private String _formatPercentage(String value) {
		Double doubleValue = GetterUtil.getDouble(value);

		return String.format("%.1f", doubleValue * 100) + "%";
	}

	private String _getMetricTrendIconURL(
		String name, JSONObject siteMetricsJSONObject) {

		JSONObject metricsJSONObject = siteMetricsJSONObject.getJSONObject(
			name);

		JSONObject trendJSONObject = metricsJSONObject.getJSONObject("trend");

		String trendClassification = trendJSONObject.getString(
			"trendClassification");

		if (Objects.equals(name, "bounceRateMetric")) {
			if (Objects.equals(trendClassification, "POSITIVE")) {
				trendClassification = "NEGATIVE";
			}
			else if (Objects.equals(trendClassification, "NEGATIVE")) {
				trendClassification = "POSITIVE";
			}
		}

		return EmailUtil.getTrendIconURL(trendClassification);
	}

	private String _getMetricValue(
		String name, JSONObject siteMetricsJSONObject) {

		JSONObject metricsJSONObject = siteMetricsJSONObject.getJSONObject(
			name);

		return metricsJSONObject.getString("value");
	}

	private String _getPreviousMetricValue(
		String name, JSONObject siteMetricsJSONObject) {

		JSONObject metricsJSONObject = siteMetricsJSONObject.getJSONObject(
			name);

		JSONObject trendJSONObject = metricsJSONObject.getJSONObject("trend");

		return Math.abs(trendJSONObject.getDouble("percentage", 0)) + "%";
	}

	private int _getRangeKey(String frequency) {
		if (Objects.equals(frequency, "daily")) {
			return 1;
		}
		else if (Objects.equals(frequency, "monthly")) {
			return 30;
		}

		return 7;
	}

	private String _getTemporal(String frequency) {
		if (Objects.equals(frequency, "daily")) {
			return "Day";
		}
		else if (Objects.equals(frequency, "monthly")) {
			return "Month";
		}

		return "Week";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EmailReportHelper.class);

	@Reference
	private CerebroEngineClient _cerebroEngineClient;

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private FaroEmailLocalService _faroEmailLocalService;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MailService _mailService;

	@Reference
	private UserLocalService _userLocalService;

}