/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
public abstract class BaseDDMFormMVCResourceCommand
	extends BaseMVCResourceCommand {

	public String formatDate(Date date, Locale locale, String timezoneId) {
		DateTimeFormatter dateTimeFormatter =
			DateTimeFormatter.ofLocalizedDateTime(
				FormatStyle.MEDIUM, FormatStyle.SHORT);

		dateTimeFormatter = dateTimeFormatter.withLocale(locale);

		LocalDateTime localDateTime = LocalDateTime.ofInstant(
			date.toInstant(), ZoneId.of(timezoneId));

		return dateTimeFormatter.format(localDateTime);
	}

	public void writeResponse(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			DDMFormInstance formInstance)
		throws Exception {

		Map<String, Object> response = HashMapBuilder.<String, Object>put(
			"ddmStructureId", formInstance.getStructureId()
		).put(
			"formInstanceId", formInstance.getFormInstanceId()
		).put(
			"modifiedDate",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)resourceRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				User user = themeDisplay.getUser();

				return formatDate(
					formInstance.getModifiedDate(), user.getLocale(),
					user.getTimeZoneId());
			}
		).build();

		JSONSerializer jsonSerializer = jsonFactory.createJSONSerializer();

		PortletResponseUtil.write(
			resourceResponse, jsonSerializer.serializeDeep(response));
	}

	@Reference
	protected JSONFactory jsonFactory;

}