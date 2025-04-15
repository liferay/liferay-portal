/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cal.Recurrence;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.scheduler.CronTextUtil;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.model.Entry;
import com.liferay.portal.reports.engine.console.service.DefinitionService;
import com.liferay.portal.reports.engine.console.service.EntryService;
import com.liferay.portal.reports.engine.console.util.ReportsEngineConsoleUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Gavin Wan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
		"mvc.command.name=/reports_admin/add_scheduler"
	},
	service = MVCActionCommand.class
)
public class AddSchedulerMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long definitionId = ParamUtil.getLong(actionRequest, "definitionId");
		Calendar startCalendar = ReportsEngineConsoleUtil.getDate(
			actionRequest, "schedulerStartDate", true);

		Date schedulerEndDate = null;

		int endDateType = ParamUtil.getInteger(actionRequest, "endDateType");

		if (endDateType == _END_DATE_TYPE_END_BY) {
			Calendar endCalendar = ReportsEngineConsoleUtil.getDate(
				actionRequest, "schedulerEndDate", true);

			schedulerEndDate = endCalendar.getTime();
		}

		int recurrenceType = ParamUtil.getInteger(
			actionRequest, "recurrenceType");

		JSONArray entryReportParametersJSONArray =
			_jsonFactory.createJSONArray();

		Definition definition = _definitionService.getDefinition(definitionId);

		JSONArray reportParametersJSONArray = _jsonFactory.createJSONArray(
			definition.getReportParameters());

		for (int i = 0; i < reportParametersJSONArray.length(); i++) {
			JSONObject definitionReportParameterJSONObject =
				reportParametersJSONArray.getJSONObject(i);

			entryReportParametersJSONArray.put(
				JSONUtil.put(
					"key", definitionReportParameterJSONObject.getString("key")
				).put(
					"value",
					_getEntryReportParameterValue(
						actionRequest, definitionReportParameterJSONObject)
				));
		}

		_entryService.addEntry(
			themeDisplay.getScopeGroupId(), definitionId,
			ParamUtil.getString(actionRequest, "format"), true,
			startCalendar.getTime(), schedulerEndDate,
			recurrenceType != Recurrence.NO_RECURRENCE,
			CronTextUtil.getCronText(
				actionRequest, startCalendar, true, recurrenceType),
			ParamUtil.getString(actionRequest, "emailNotifications"),
			ParamUtil.getString(actionRequest, "emailDelivery"),
			_portal.getPortletId(actionRequest),
			ParamUtil.getString(actionRequest, "generatedReportsURL"),
			ParamUtil.getString(actionRequest, "reportName"),
			entryReportParametersJSONArray.toString(),
			ServiceContextFactory.getInstance(
				Entry.class.getName(), actionRequest));
	}

	private String _getEntryReportParameterValue(
		ActionRequest actionRequest,
		JSONObject definitionReportParameterJSONObject) {

		String key = definitionReportParameterJSONObject.getString("key");
		String type = definitionReportParameterJSONObject.getString("type");

		if (!type.equals("date")) {
			return ParamUtil.getString(actionRequest, "parameterValue" + key);
		}

		String variable = ParamUtil.getString(
			actionRequest, "useVariable" + key);

		if (variable.equals("endDate")) {
			if (ParamUtil.getInteger(actionRequest, "endDateType") ==
					_END_DATE_TYPE_NO_END_DATE) {

				return StringPool.NULL;
			}

			Calendar calendar = ReportsEngineConsoleUtil.getDate(
				actionRequest, "schedulerEndDate", true);

			return _dateFormat.format(calendar.getTime());
		}
		else if (variable.equals("startDate")) {
			Calendar calendar = ReportsEngineConsoleUtil.getDate(
				actionRequest, "schedulerStartDate", true);

			return _dateFormat.format(calendar.getTime());
		}

		Calendar calendar = ReportsEngineConsoleUtil.getDate(
			actionRequest, key, false);

		return _dateFormat.format(calendar.getTime());
	}

	private static final int _END_DATE_TYPE_END_BY = 1;

	private static final int _END_DATE_TYPE_NO_END_DATE = 0;

	private final DateFormat _dateFormat =
		DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");

	@Reference
	private DefinitionService _definitionService;

	@Reference
	private EntryService _entryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}