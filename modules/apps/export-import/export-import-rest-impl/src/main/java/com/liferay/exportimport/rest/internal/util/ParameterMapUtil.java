/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.rest.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.ImportProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.RequestPortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.RequestPortletDataHandlerControl;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.BadRequestException;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author Daniel Raposo
 */
public class ParameterMapUtil {

	public static Map<String, String[]> putDateRangeParameters(
		String dateRangeType, Date startDate, Date endDate,
		Map<String, String[]> parameterMap, User user) {

		if (dateRangeType == null) {
			if ((endDate == null) && (startDate == null)) {
				dateRangeType = _DATE_RANGE_TYPE_ALL;
			}
			else {
				dateRangeType = _DATE_RANGE_TYPE_DATE_RANGE;
			}
		}

		if (dateRangeType.equals(_DATE_RANGE_TYPE_ALL)) {
			parameterMap.put(
				ExportImportDateUtil.RANGE,
				new String[] {ExportImportDateUtil.RANGE_ALL});

			return parameterMap;
		}

		if (dateRangeType.equals(_DATE_RANGE_TYPE_LAST)) {
			if (startDate == null) {
				throw new BadRequestException(
					"The last date range type needs a start date");
			}

			Date date = new Date();

			if (startDate.after(date)) {
				throw new BadRequestException(
					"The start date must be in the past for the last date " +
						"range type");
			}

			long hours = Math.max(
				1,
				(date.getTime() - startDate.getTime() + (Time.HOUR / 2)) /
					Time.HOUR);

			parameterMap.put("last", new String[] {String.valueOf(hours)});

			parameterMap.put(
				ExportImportDateUtil.RANGE,
				new String[] {ExportImportDateUtil.RANGE_LAST});

			return parameterMap;
		}

		if (!dateRangeType.equals(_DATE_RANGE_TYPE_DATE_RANGE)) {
			throw new BadRequestException(
				"The date range type \"" + dateRangeType +
					"\" is not supported");
		}

		if ((endDate == null) && (startDate == null)) {
			throw new BadRequestException(
				"The date range type needs a start or end date");
		}

		if ((endDate != null) && (startDate != null) &&
			!startDate.before(endDate)) {

			throw new BadRequestException(
				"The start date must be before the end date");
		}

		parameterMap.put(
			ExportImportDateUtil.RANGE,
			new String[] {ExportImportDateUtil.RANGE_DATE_RANGE});

		if (startDate == null) {
			startDate = new Date(0);
		}

		_putDateParameters(startDate, parameterMap, "startDate", user);

		if (endDate != null) {
			_putDateParameters(endDate, parameterMap, "endDate", user);
		}

		return parameterMap;
	}

	public static Map<String, String[]> toParameterMap(
		ExportProcessRequest exportProcessRequest, boolean portletScoped) {

		Map<String, String[]> parameterMap = _getDefaultParameterMap(
			portletScoped);

		_addRequestPortletDataHandlers(
			exportProcessRequest.getRequestPortletDataHandlers(), parameterMap);

		parameterMap.put(
			PortletDataHandlerKeys.COMMENTS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(exportProcessRequest.getComments()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(exportProcessRequest.getDeletions()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						exportProcessRequest.getSiteTemplateSettings()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						exportProcessRequest.getSitePagesSettings()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.LOGO,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(exportProcessRequest.getLogo()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						exportProcessRequest.getPermissions()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.RATINGS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(exportProcessRequest.getRatings()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						exportProcessRequest.getThemeSettings()))
			});

		return parameterMap;
	}

	public static Map<String, String[]> toParameterMap(
		ImportProcessRequest importProcessRequest, boolean portletScoped) {

		Map<String, String[]> parameterMap = _getDefaultParameterMap(
			portletScoped);

		_addRequestPortletDataHandlers(
			importProcessRequest.getRequestPortletDataHandlers(), parameterMap);

		parameterMap.put(
			PortletDataHandlerKeys.COMMENTS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(importProcessRequest.getComments()))
			});

		ImportProcessRequest.DataStrategy dataStrategy =
			importProcessRequest.getDataStrategy();

		if (dataStrategy != null) {
			parameterMap.put(
				PortletDataHandlerKeys.DATA_STRATEGY,
				new String[] {"DATA_STRATEGY_" + dataStrategy});
		}

		parameterMap.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(importProcessRequest.getDeletions()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						importProcessRequest.getSiteTemplateSettings()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						importProcessRequest.getSitePagesSettings()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.LOGO,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(importProcessRequest.getLogo()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						importProcessRequest.getPermissions()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.RATINGS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(importProcessRequest.getRatings()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						importProcessRequest.getThemeSettings()))
			});

		ImportProcessRequest.UserIdStrategy userIdStrategy =
			importProcessRequest.getUserIdStrategy();

		if (userIdStrategy != null) {
			parameterMap.put(
				PortletDataHandlerKeys.USER_ID_STRATEGY,
				new String[] {userIdStrategy.toString()});
		}

		return parameterMap;
	}

	private static void _addRequestPortletDataHandlerControls(
		RequestPortletDataHandlerControl[] requestPortletDataHandlerControls,
		Map<String, String[]> parameterMap) {

		if (requestPortletDataHandlerControls == null) {
			return;
		}

		for (RequestPortletDataHandlerControl requestPortletDataHandlerControl :
				requestPortletDataHandlerControls) {

			String name = requestPortletDataHandlerControl.getName();

			if (Validator.isBlank(name)) {
				continue;
			}

			String[] values = requestPortletDataHandlerControl.getValues();

			if (ArrayUtil.isEmpty(values)) {
				values = new String[] {Boolean.TRUE.toString()};
			}

			parameterMap.put(name, values);

			_addRequestPortletDataHandlerControls(
				requestPortletDataHandlerControl.
					getRequestPortletDataHandlerControls(),
				parameterMap);
		}
	}

	private static void _addRequestPortletDataHandlers(
		RequestPortletDataHandler[] requestPortletDataHandlers,
		Map<String, String[]> parameterMap) {

		if (requestPortletDataHandlers == null) {
			return;
		}

		for (RequestPortletDataHandler requestPortletDataHandler :
				requestPortletDataHandlers) {

			String name = requestPortletDataHandler.getName();

			if (Validator.isBlank(name)) {
				continue;
			}

			parameterMap.put(name, new String[] {Boolean.TRUE.toString()});

			_addRequestPortletDataHandlerControls(
				requestPortletDataHandler.
					getRequestPortletDataHandlerControls(),
				parameterMap);
		}
	}

	private static Map<String, String[]> _getDefaultParameterMap(
		boolean portletScoped) {

		return HashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
		).put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL,
			new String[] {String.valueOf(!portletScoped)}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {String.valueOf(!portletScoped)}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {String.valueOf(!portletScoped)}
		).put(
			PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL,
			new String[] {String.valueOf(!portletScoped)}
		).put(
			PortletDataHandlerKeys.USER_ID_STRATEGY,
			new String[] {UserIdStrategy.CURRENT_USER_ID}
		).build();
	}

	private static void _putDateParameters(
		Date date, Map<String, String[]> parameterMap, String prefix,
		User user) {

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone(), user.getLocale());

		calendar.setTime(date);

		parameterMap.put(
			prefix + "AmPm",
			new String[] {String.valueOf(calendar.get(Calendar.AM_PM))});
		parameterMap.put(
			prefix + "Day",
			new String[] {String.valueOf(calendar.get(Calendar.DATE))});
		parameterMap.put(
			prefix + "Hour",
			new String[] {String.valueOf(calendar.get(Calendar.HOUR))});
		parameterMap.put(
			prefix + "Minute",
			new String[] {String.valueOf(calendar.get(Calendar.MINUTE))});
		parameterMap.put(
			prefix + "Month",
			new String[] {String.valueOf(calendar.get(Calendar.MONTH))});
		parameterMap.put(
			prefix + "Second",
			new String[] {String.valueOf(calendar.get(Calendar.SECOND))});
		parameterMap.put(
			prefix + "Year",
			new String[] {String.valueOf(calendar.get(Calendar.YEAR))});
	}

	private static final String _DATE_RANGE_TYPE_ALL = "ALL";

	private static final String _DATE_RANGE_TYPE_DATE_RANGE = "DATE_RANGE";

	private static final String _DATE_RANGE_TYPE_LAST = "LAST";

}