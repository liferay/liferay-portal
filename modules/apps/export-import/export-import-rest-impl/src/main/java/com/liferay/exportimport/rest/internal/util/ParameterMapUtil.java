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
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.dto.v1_0.PublishProcessRequest;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.BadRequestException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Daniel Raposo
 */
public class ParameterMapUtil {

	public static final String CRON_EXPRESSION = "cronExpression";

	public static final String TIME_ZONE_ID = "timeZoneId";

	public static PortletDataHandlerTreeSelection
		getPortletDataHandlerTreeSelection(
			String controlName, Map<String, String[]> parameterMap) {

		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			new PortletDataHandlerTreeSelection();

		portletDataHandlerTreeSelection.setName(() -> controlName);

		_setPortletDataHandlerTreeSelection(
			parameterMap, portletDataHandlerTreeSelection);

		return portletDataHandlerTreeSelection;
	}

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

		if (dateRangeType.equals(_DATE_RANGE_TYPE_FROM_LAST_PUBLISH_DATE)) {
			parameterMap.put(
				ExportImportDateUtil.RANGE,
				new String[] {
					ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE
				});

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
		ExportProcessRequest exportProcessRequest,
		PortletDataHandler[] portletDataHandlers, boolean portletScoped) {

		Map<String, String[]> parameterMap = _getDefaultParameterMap(
			portletScoped);

		_putPortletDataHandlers(
			portletDataHandlers, parameterMap,
			exportProcessRequest.getPortletDataHandlers());

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

		String[] siteExternalReferenceCodes = ArrayUtil.filter(
			exportProcessRequest.getSiteExternalReferenceCodes(),
			Validator::isNotNull);

		if (ArrayUtil.isNotEmpty(siteExternalReferenceCodes)) {
			parameterMap.put(
				PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
				ArrayUtil.unique(siteExternalReferenceCodes));
		}

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

		return parameterMap;
	}

	public static Map<String, String[]> toParameterMap(
		ImportProcessRequest importProcessRequest,
		PortletDataHandler[] portletDataHandlers, boolean portletScoped) {

		Map<String, String[]> parameterMap = _getDefaultParameterMap(
			portletScoped);

		_putPortletDataHandlers(
			portletDataHandlers, parameterMap,
			importProcessRequest.getPortletDataHandlers());

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

		String[] siteExternalReferenceCodes = ArrayUtil.filter(
			importProcessRequest.getSiteExternalReferenceCodes(),
			Validator::isNotNull);

		if (ArrayUtil.isNotEmpty(siteExternalReferenceCodes)) {
			parameterMap.put(
				PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
				ArrayUtil.unique(siteExternalReferenceCodes));
		}

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

		ImportProcessRequest.UserIdStrategy userIdStrategy =
			importProcessRequest.getUserIdStrategy();

		if (userIdStrategy != null) {
			parameterMap.put(
				PortletDataHandlerKeys.USER_ID_STRATEGY,
				new String[] {userIdStrategy.toString()});
		}

		return parameterMap;
	}

	public static Map<String, String[]> toParameterMap(
		PublishProcessRequest publishProcessRequest,
		PortletDataHandler[] portletDataHandlers) {

		Map<String, String[]> parameterMap = new LinkedHashMap<>();

		_putPortletDataHandlers(
			portletDataHandlers, parameterMap,
			publishProcessRequest.getPortletDataHandlers());

		parameterMap.put(
			PortletDataHandlerKeys.COMMENTS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(publishProcessRequest.getComments()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(publishProcessRequest.getDeletions()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(
						publishProcessRequest.getPermissions()))
			});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.FALSE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.RATINGS,
			new String[] {
				String.valueOf(
					GetterUtil.getBoolean(publishProcessRequest.getRatings()))
			});

		return parameterMap;
	}

	public static PublishProcessRequest toPublishProcessRequest(
		PortletDataHandler[] availablePortletDataHandlers, Locale locale,
		Map<String, String[]> parameterMap, TimeZone timeZone) {

		PublishProcessRequest publishProcessRequest =
			new PublishProcessRequest() {
				{
					setComments(
						() -> MapUtil.getBoolean(
							parameterMap, PortletDataHandlerKeys.COMMENTS));
					setDateRangeType(
						() -> PublishProcessRequest.DateRangeType.ALL);
					setDeletions(
						() -> MapUtil.getBoolean(
							parameterMap, PortletDataHandlerKeys.DELETIONS));
					setPermissions(
						() -> MapUtil.getBoolean(
							parameterMap, PortletDataHandlerKeys.PERMISSIONS));
					setPortletDataHandlers(
						() -> _toPortletDataHandlers(
							availablePortletDataHandlers, parameterMap));
					setRatings(
						() -> MapUtil.getBoolean(
							parameterMap, PortletDataHandlerKeys.RATINGS));
				}
			};

		String range = MapUtil.getString(
			parameterMap, ExportImportDateUtil.RANGE,
			ExportImportDateUtil.RANGE_ALL);

		if (range.equals(ExportImportDateUtil.RANGE_DATE_RANGE)) {
			publishProcessRequest.setDateRangeType(
				() -> PublishProcessRequest.DateRangeType.DATE_RANGE);
			publishProcessRequest.setEndDate(
				() -> _getDate(locale, parameterMap, "endDate", timeZone));
			publishProcessRequest.setStartDate(
				() -> _getDate(locale, parameterMap, "startDate", timeZone));
		}
		else if (range.equals(
					ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE)) {

			publishProcessRequest.setDateRangeType(
				() ->
					PublishProcessRequest.DateRangeType.FROM_LAST_PUBLISH_DATE);
		}
		else if (range.equals(ExportImportDateUtil.RANGE_LAST)) {
			publishProcessRequest.setDateRangeType(
				() -> PublishProcessRequest.DateRangeType.LAST);
			publishProcessRequest.setStartDate(
				() -> new Date(
					System.currentTimeMillis() -
						(MapUtil.getLong(parameterMap, "last") * Time.HOUR)));
		}

		return publishProcessRequest;
	}

	private static Date _getDate(
		Locale locale, Map<String, String[]> parameterMap, String prefix,
		TimeZone timeZone) {

		if (!parameterMap.containsKey(prefix + "Year")) {
			return null;
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

		calendar.clear();

		calendar.set(
			Calendar.AM_PM, MapUtil.getInteger(parameterMap, prefix + "AmPm"));
		calendar.set(
			Calendar.DATE, MapUtil.getInteger(parameterMap, prefix + "Day"));
		calendar.set(
			Calendar.HOUR, MapUtil.getInteger(parameterMap, prefix + "Hour"));
		calendar.set(
			Calendar.MINUTE,
			MapUtil.getInteger(parameterMap, prefix + "Minute"));
		calendar.set(
			Calendar.MONTH, MapUtil.getInteger(parameterMap, prefix + "Month"));
		calendar.set(
			Calendar.SECOND,
			MapUtil.getInteger(parameterMap, prefix + "Second"));
		calendar.set(
			Calendar.YEAR, MapUtil.getInteger(parameterMap, prefix + "Year"));

		Date date = calendar.getTime();

		if (date.getTime() == 0) {
			return null;
		}

		return date;
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

	private static void _putPortletDataHandlerControl(
		PortletDataHandlerControl availablePortletDataHandlerControl,
		Map<String, String[]> parameterMap,
		PortletDataHandlerControl portletDataHandlerControl) {

		if (portletDataHandlerControl == null) {
			return;
		}

		String name = availablePortletDataHandlerControl.getName();

		if (availablePortletDataHandlerControl instanceof
				PortletDataHandlerBoolean availablePortletDataHandlerBoolean) {

			PortletDataHandlerBoolean portletDataHandlerBoolean =
				(PortletDataHandlerBoolean)portletDataHandlerControl;

			boolean selected = GetterUtil.getBoolean(
				portletDataHandlerBoolean.getSelected());

			parameterMap.put(name, new String[] {String.valueOf(selected)});

			if (selected) {
				_putPortletDataHandlerControls(
					availablePortletDataHandlerBoolean.
						getPortletDataHandlerControls(),
					parameterMap, name,
					portletDataHandlerBoolean.getPortletDataHandlerControls());
			}
		}
		else if (availablePortletDataHandlerControl instanceof
					PortletDataHandlerChoice
						availablePortletDataHandlerChoice) {

			PortletDataHandlerChoice portletDataHandlerChoice =
				(PortletDataHandlerChoice)portletDataHandlerControl;

			String choice = portletDataHandlerChoice.getChoice();

			if (Validator.isNull(choice)) {
				return;
			}

			if (!ArrayUtil.exists(
					availablePortletDataHandlerChoice.getChoices(),
					availableChoice -> Objects.equals(
						availableChoice.getName(), choice))) {

				throw new BadRequestException(
					StringBundler.concat(
						"The choice \"", choice,
						"\" is not one of the choices of the control \"", name,
						"\""));
			}

			parameterMap.put(name, new String[] {choice});

			_putPortletDataHandlerControls(
				availablePortletDataHandlerChoice.
					getPortletDataHandlerControls(),
				parameterMap, name,
				portletDataHandlerChoice.getPortletDataHandlerControls());
		}
		else if (availablePortletDataHandlerControl instanceof
					PortletDataHandlerTreeSelection) {

			PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
				(PortletDataHandlerTreeSelection)portletDataHandlerControl;

			boolean selected = GetterUtil.getBoolean(
				portletDataHandlerTreeSelection.getSelected());

			parameterMap.put(name, new String[] {String.valueOf(selected)});

			if (!selected) {
				return;
			}

			parameterMap.put(
				name + _TREE_SELECTION_SUFFIX_ALL,
				new String[] {
					String.valueOf(
						GetterUtil.getBoolean(
							portletDataHandlerTreeSelection.getSelectAll(),
							true))
				});

			_putStringValues(
				parameterMap, name + _TREE_SELECTION_SUFFIX_EXCLUDED_ITEMS,
				portletDataHandlerTreeSelection.
					getExcludedItemExternalReferenceCodes());
			_putStringValues(
				parameterMap, name + _TREE_SELECTION_SUFFIX_EXCLUDED_SUBTREES,
				portletDataHandlerTreeSelection.
					getExcludedSubtreeExternalReferenceCodes());
			_putStringValues(
				parameterMap, name + _TREE_SELECTION_SUFFIX_ITEMS,
				portletDataHandlerTreeSelection.
					getItemExternalReferenceCodes());
			_putStringValues(
				parameterMap, name + _TREE_SELECTION_SUFFIX_SUBTREES,
				portletDataHandlerTreeSelection.
					getSubtreeExternalReferenceCodes());
		}
	}

	private static void _putPortletDataHandlerControls(
		PortletDataHandlerControl[] availablePortletDataHandlerControls,
		Map<String, String[]> parameterMap, String parentName,
		PortletDataHandlerControl[] portletDataHandlerControls) {

		Map<String, PortletDataHandlerControl> portletDataHandlerControlsMap =
			new LinkedHashMap<>();

		if (portletDataHandlerControls != null) {
			for (PortletDataHandlerControl portletDataHandlerControl :
					portletDataHandlerControls) {

				PortletDataHandlerControl previousPortletDataHandlerControl =
					portletDataHandlerControlsMap.put(
						portletDataHandlerControl.getName(),
						portletDataHandlerControl);

				if (previousPortletDataHandlerControl != null) {
					throw new BadRequestException(
						StringBundler.concat(
							"The control \"",
							portletDataHandlerControl.getName(),
							"\" is sent more than once in \"", parentName,
							"\""));
				}
			}
		}

		if (availablePortletDataHandlerControls != null) {
			for (PortletDataHandlerControl availablePortletDataHandlerControl :
					availablePortletDataHandlerControls) {

				PortletDataHandlerControl portletDataHandlerControl =
					portletDataHandlerControlsMap.remove(
						availablePortletDataHandlerControl.getName());

				if ((portletDataHandlerControl != null) &&
					(portletDataHandlerControl.getType() !=
						availablePortletDataHandlerControl.getType())) {

					throw new BadRequestException(
						StringBundler.concat(
							"The control \"",
							availablePortletDataHandlerControl.getName(),
							"\" is of type \"",
							availablePortletDataHandlerControl.
								getTypeAsString(),
							"\", not \"",
							portletDataHandlerControl.getTypeAsString(), "\""));
				}

				_putPortletDataHandlerControl(
					availablePortletDataHandlerControl, parameterMap,
					portletDataHandlerControl);
			}
		}

		if (!portletDataHandlerControlsMap.isEmpty()) {
			throw new BadRequestException(
				StringBundler.concat(
					"No control exists with name ",
					StringUtil.merge(
						TransformUtil.transform(
							portletDataHandlerControlsMap.keySet(),
							name -> StringUtil.quote(name, CharPool.QUOTE)),
						", "),
					" in \"", parentName, "\""));
		}
	}

	private static void _putPortletDataHandlers(
		PortletDataHandler[] availablePortletDataHandlers,
		Map<String, String[]> parameterMap,
		PortletDataHandler[] portletDataHandlers) {

		if (portletDataHandlers == null) {
			return;
		}

		Map<String, PortletDataHandler> availablePortletDataHandlersMap =
			new HashMap<>();

		for (PortletDataHandler availablePortletDataHandler :
				availablePortletDataHandlers) {

			availablePortletDataHandlersMap.put(
				availablePortletDataHandler.getName(),
				availablePortletDataHandler);
		}

		for (PortletDataHandler portletDataHandler : portletDataHandlers) {
			String name = portletDataHandler.getName();

			PortletDataHandler availablePortletDataHandler =
				availablePortletDataHandlersMap.get(name);

			if (availablePortletDataHandler == null) {
				throw new BadRequestException(
					StringBundler.concat(
						"No portlet data handler exists with name \"", name,
						"\""));
			}

			if (!GetterUtil.getBoolean(
					portletDataHandler.getSelected(), true)) {

				continue;
			}

			parameterMap.put(name, new String[] {Boolean.TRUE.toString()});

			_putPortletDataHandlerControls(
				availablePortletDataHandler.getPortletDataHandlerControls(),
				parameterMap, name,
				portletDataHandler.getPortletDataHandlerControls());
		}
	}

	private static void _putStringValues(
		Map<String, String[]> parameterMap, String name, String[] values) {

		if (ArrayUtil.isNotEmpty(values)) {
			parameterMap.put(name, values);
		}
	}

	private static void _setPortletDataHandlerControls(
		Map<String, String[]> parameterMap,
		PortletDataHandlerControl[] portletDataHandlerControls) {

		if (portletDataHandlerControls == null) {
			return;
		}

		for (PortletDataHandlerControl portletDataHandlerControl :
				portletDataHandlerControls) {

			String name = portletDataHandlerControl.getName();

			if (portletDataHandlerControl instanceof
					PortletDataHandlerBoolean portletDataHandlerBoolean) {

				portletDataHandlerBoolean.setSelected(
					() -> MapUtil.getBoolean(parameterMap, name));

				_setPortletDataHandlerControls(
					parameterMap,
					portletDataHandlerBoolean.getPortletDataHandlerControls());
			}
			else if (portletDataHandlerControl instanceof
						PortletDataHandlerChoice portletDataHandlerChoice) {

				portletDataHandlerChoice.setChoice(
					() -> MapUtil.getString(parameterMap, name, null));

				_setPortletDataHandlerControls(
					parameterMap,
					portletDataHandlerChoice.getPortletDataHandlerControls());
			}
			else if (portletDataHandlerControl instanceof
						PortletDataHandlerTreeSelection
							portletDataHandlerTreeSelection) {

				_setPortletDataHandlerTreeSelection(
					parameterMap, portletDataHandlerTreeSelection);

				portletDataHandlerTreeSelection.setSelected(
					() -> MapUtil.getBoolean(parameterMap, name));
			}
		}
	}

	private static void _setPortletDataHandlerTreeSelection(
		Map<String, String[]> parameterMap,
		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection) {

		String name = portletDataHandlerTreeSelection.getName();

		portletDataHandlerTreeSelection.setExcludedItemExternalReferenceCodes(
			() -> GetterUtil.getStringValues(
				(Object)parameterMap.get(
					name + _TREE_SELECTION_SUFFIX_EXCLUDED_ITEMS)));
		portletDataHandlerTreeSelection.
			setExcludedSubtreeExternalReferenceCodes(
				() -> GetterUtil.getStringValues(
					(Object)parameterMap.get(
						name + _TREE_SELECTION_SUFFIX_EXCLUDED_SUBTREES)));
		portletDataHandlerTreeSelection.setItemExternalReferenceCodes(
			() -> GetterUtil.getStringValues(
				(Object)parameterMap.get(name + _TREE_SELECTION_SUFFIX_ITEMS)));
		portletDataHandlerTreeSelection.setSelectAll(
			() -> MapUtil.getBoolean(
				parameterMap, name + _TREE_SELECTION_SUFFIX_ALL, true));
		portletDataHandlerTreeSelection.setSubtreeExternalReferenceCodes(
			() -> GetterUtil.getStringValues(
				(Object)parameterMap.get(
					name + _TREE_SELECTION_SUFFIX_SUBTREES)));
	}

	private static PortletDataHandler[] _toPortletDataHandlers(
		PortletDataHandler[] availablePortletDataHandlers,
		Map<String, String[]> parameterMap) {

		return TransformUtil.transform(
			availablePortletDataHandlers,
			availablePortletDataHandler -> {
				if (!MapUtil.getBoolean(
						parameterMap, availablePortletDataHandler.getName())) {

					return null;
				}

				_setPortletDataHandlerControls(
					parameterMap,
					availablePortletDataHandler.
						getPortletDataHandlerControls());

				return availablePortletDataHandler;
			},
			PortletDataHandler.class);
	}

	private static final String _DATE_RANGE_TYPE_ALL = "ALL";

	private static final String _DATE_RANGE_TYPE_DATE_RANGE = "DATE_RANGE";

	private static final String _DATE_RANGE_TYPE_FROM_LAST_PUBLISH_DATE =
		"FROM_LAST_PUBLISH_DATE";

	private static final String _DATE_RANGE_TYPE_LAST = "LAST";

	private static final String _TREE_SELECTION_SUFFIX_ALL = "_all";

	private static final String _TREE_SELECTION_SUFFIX_EXCLUDED_ITEMS =
		"_excluded-items";

	private static final String _TREE_SELECTION_SUFFIX_EXCLUDED_SUBTREES =
		"_excluded-subtrees";

	private static final String _TREE_SELECTION_SUFFIX_ITEMS = "_items";

	private static final String _TREE_SELECTION_SUFFIX_SUBTREES = "_subtrees";

}