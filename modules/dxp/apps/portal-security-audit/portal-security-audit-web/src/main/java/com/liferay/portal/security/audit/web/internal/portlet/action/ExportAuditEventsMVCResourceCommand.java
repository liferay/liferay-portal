/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.web.internal.portlet.action;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayResourceResponse;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.ContentTypes;
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
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AuditPortletKeys.AUDIT,
		"mvc.command.name=/audit/export_audit_events"
	},
	service = MVCResourceCommand.class
)
public class ExportAuditEventsMVCResourceCommand
	extends BaseMVCResourceCommand {

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

	private String _formatDate(Date date) {
		if (date instanceof Timestamp) {
			date = new Date(date.getTime());
		}

		return CSVUtil.encode(String.valueOf(date));
	}

	private String _getAuditEventCSV(AuditEvent auditEvent) {
		return StringBundler.concat(
			StringPool.QUOTE,
			StringUtil.merge(
				TransformUtil.transform(
					_functions.values(),
					function -> function.apply(auditEvent)),
				StringBundler.concat(
					StringPool.QUOTE, StringPool.COMMA, StringPool.QUOTE)),
			StringPool.QUOTE, StringPool.NEW_LINE);
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
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
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

		int percentage = 10;
		int total = auditEvents.size();

		progressTracker.setPercent(percentage);

		StringBundler sb = new StringBundler(auditEvents.size() + 1);

		sb.append(StringPool.QUOTE);
		sb.append(
			StringUtil.merge(
				_functions.keySet(),
				StringPool.QUOTE + StringPool.COMMA + StringPool.QUOTE));
		sb.append(StringPool.QUOTE);
		sb.append(StringPool.NEW_LINE);

		for (int i = 0; i < auditEvents.size(); i++) {
			AuditEvent auditEvent = auditEvents.get(i);

			sb.append(_getAuditEventCSV(auditEvent));

			percentage = Math.min(10 + ((i * 90) / total), 99);

			progressTracker.setPercent(percentage);
		}

		progressTracker.finish(resourceRequest);

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportAuditEventsMVCResourceCommand.class);

	private final LinkedHashMap<String, Function<AuditEvent, String>>
		_functions =
			LinkedHashMapBuilder.<String, Function<AuditEvent, String>>put(
				"event-id",
				auditEvent -> String.valueOf(auditEvent.getAuditEventId())
			).put(
				"create-date",
				auditEvent -> _formatDate(auditEvent.getCreateDate())
			).put(
				"group-id",
				auditEvent -> String.valueOf(auditEvent.getGroupId())
			).put(
				"resource-id", AuditEvent::getClassPK
			).put(
				"resource-name", AuditEvent::getClassName
			).put(
				"resource-action", AuditEvent::getEventType
			).put(
				"user-id", auditEvent -> String.valueOf(auditEvent.getUserId())
			).put(
				"user-name", AuditEvent::getUserName
			).put(
				"client-host", AuditEvent::getClientHost
			).put(
				"client-ip", AuditEvent::getClientIP
			).put(
				"server-name", AuditEvent::getServerName
			).put(
				"session-id", AuditEvent::getSessionID
			).put(
				"additional-information", AuditEvent::getAdditionalInfo
			).build();

	@Reference
	private Portal _portal;

}