/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.web.internal.portlet.action;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayResourceResponse;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.AuditEvent;
import com.liferay.portal.security.audit.web.internal.constants.AuditPortletKeys;
import com.liferay.portal.security.audit.web.internal.display.context.AuditDisplayContext;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.sql.Timestamp;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.router.configuration.CSVLogMessageFormatterConfiguration",
	property = {
		"jakarta.portlet.name=" + AuditPortletKeys.AUDIT,
		"mvc.command.name=/audit/export_audit_events"
	},
	service = MVCResourceCommand.class
)
public class ExportAuditEventsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_columns = GetterUtil.getStringValues(properties.get("columns"));

		if (ArrayUtil.isEmpty(_columns)) {
			_columns = _getDefaultColumns();
		}
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			SessionMessages.add(
				resourceRequest,
				_portal.getPortletId(resourceRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

			String auditEventsCSV = _getAuditEventsCSV(
				_getColumns(_portal.getCompanyId(resourceRequest)),
				resourceRequest, resourceResponse);

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse, "audit_events.csv",
				auditEventsCSV.getBytes(), ContentTypes.TEXT_CSV_UTF8);
		}
		catch (Exception exception) {
			SessionErrors.add(resourceRequest, exception.getClass());

			_log.error(exception);
		}
	}

	private String _buildCSV(
		List<AuditEvent> auditEvents, String[] columns,
		ProgressTracker progressTracker) {

		if (progressTracker != null) {
			progressTracker.setPercent(10);
		}

		int total = auditEvents.size();

		StringBundler sb = new StringBundler((columns.length * total * 2) + 2);

		sb.append(
			StringUtil.merge(
				TransformUtil.transform(columns, CSVUtil::encode, String.class),
				StringPool.COMMA));
		sb.append(StringPool.NEW_LINE);

		int count = 0;

		for (AuditEvent auditEvent : auditEvents) {
			for (String column : columns) {
				Function<AuditEvent, String> function = _functions.get(column);

				if (function != null) {
					sb.append(
						CSVUtil.encode(
							GetterUtil.getString(function.apply(auditEvent))));
				}
				else {
					sb.append(StringPool.BLANK);
				}

				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);

			sb.append(StringPool.NEW_LINE);

			if (progressTracker != null) {
				progressTracker.setPercent(
					Math.min(10 + ((count * 90) / total), 99));
			}

			count++;
		}

		return sb.toString();
	}

	private String _formatDate(Date date) {
		if (date instanceof Timestamp) {
			date = new Date(date.getTime());
		}

		return CSVUtil.encode(String.valueOf(date));
	}

	private List<AuditEvent> _getAuditEvents(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		LiferayResourceResponse liferayResourceResponse =
			(LiferayResourceResponse)resourceResponse;

		liferayResourceResponse.createRenderURL(AuditPortletKeys.AUDIT);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		AuditDisplayContext auditDisplayContext = new AuditDisplayContext(
			_portal.getHttpServletRequest(resourceRequest),
			_portal.getLiferayPortletRequest(resourceRequest),
			liferayResourceResponse, themeDisplay.getTimeZone());

		auditDisplayContext.setPaging(false);

		SearchContainer<AuditEvent> searchContainer =
			auditDisplayContext.getSearchContainer();

		return searchContainer.getResults();
	}

	private String _getAuditEventsCSV(
			String[] columns, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse)
		throws Exception {

		List<AuditEvent> auditEvents = _getAuditEvents(
			resourceRequest, resourceResponse);

		if (auditEvents.isEmpty()) {
			return StringPool.BLANK;
		}

		String exportProgressId = ParamUtil.getString(
			resourceRequest, "exportProgressId");

		ProgressTracker progressTracker = new ProgressTracker(exportProgressId);

		progressTracker.start(resourceRequest);

		String csv = _buildCSV(auditEvents, columns, progressTracker);

		progressTracker.finish(resourceRequest);

		return csv;
	}

	private String[] _getColumns(long companyId) {
		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-6417")) {
			return _getDefaultColumns();
		}

		return _columns;
	}

	private String[] _getDefaultColumns() {
		Set<String> keys = _functions.keySet();

		return keys.toArray(new String[0]);
	}

	private String _getEmailAddress(AuditEvent auditEvent) {
		if (auditEvent.getUserId() <= 0) {
			return StringPool.BLANK;
		}

		User user = _userLocalService.fetchUser(auditEvent.getUserId());

		if (user == null) {
			return StringPool.BLANK;
		}

		return user.getEmailAddress();
	}

	private String _getScreenName(AuditEvent auditEvent) {
		if (auditEvent.getUserId() <= 0) {
			return StringPool.BLANK;
		}

		User user = _userLocalService.fetchUser(auditEvent.getUserId());

		if (user == null) {
			return StringPool.BLANK;
		}

		return user.getScreenName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportAuditEventsMVCResourceCommand.class);

	private volatile String[] _columns;
	private final LinkedHashMap<String, Function<AuditEvent, String>>
		_functions =
			LinkedHashMapBuilder.<String, Function<AuditEvent, String>>put(
				"additionalInfo", AuditEvent::getAdditionalInfo
			).put(
				"className", AuditEvent::getClassName
			).put(
				"classPK", AuditEvent::getClassPK
			).put(
				"clientHost", AuditEvent::getClientHost
			).put(
				"clientIP", AuditEvent::getClientIP
			).put(
				"companyId",
				auditEvent -> String.valueOf(auditEvent.getCompanyId())
			).put(
				"eventType", AuditEvent::getEventType
			).put(
				"message", AuditEvent::getMessage
			).put(
				"serverName", AuditEvent::getServerName
			).put(
				"serverPort",
				auditEvent -> String.valueOf(auditEvent.getServerPort())
			).put(
				"sessionID", AuditEvent::getSessionID
			).put(
				"timestamp",
				auditEvent -> _formatDate(auditEvent.getCreateDate())
			).put(
				"userEmailAddress", this::_getEmailAddress
			).put(
				"userId", auditEvent -> String.valueOf(auditEvent.getUserId())
			).put(
				"userLogin", this::_getScreenName
			).put(
				"userName", AuditEvent::getUserName
			).build();

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}