/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.internal.resource.v1_0.util;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Jorge García Jiménez
 */
public class DateRangeUtil {

	public static <T> List<T> filter(
		Function<T, Date> dateModifiedFunction, Collection<T> items,
		Map<String, Serializable> parameters) {

		long endTime = GetterUtil.getLong(parameters.get("endTime"));
		long startTime = GetterUtil.getLong(parameters.get("startTime"));

		if ((endTime == 0) && (startTime == 0)) {
			return ListUtil.fromCollection(items);
		}

		return ListUtil.filter(
			ListUtil.fromCollection(items),
			item -> {
				Date dateModified = dateModifiedFunction.apply(item);

				long time = dateModified.getTime();

				if ((endTime != 0) && (time > endTime)) {
					return false;
				}

				if ((startTime != 0) && (time < startTime)) {
					return false;
				}

				return true;
			});
	}

	public static Map<String, Serializable> getParameters(
		PortletDataContext portletDataContext) {

		return HashMapBuilder.<String, Serializable>put(
			"endTime", () -> _getTimeString(portletDataContext.getEndDate())
		).put(
			"startTime", () -> _getTimeString(portletDataContext.getStartDate())
		).build();
	}

	private static String _getTimeString(Date date) {
		if (date == null) {
			return null;
		}

		return String.valueOf(date.getTime());
	}

}