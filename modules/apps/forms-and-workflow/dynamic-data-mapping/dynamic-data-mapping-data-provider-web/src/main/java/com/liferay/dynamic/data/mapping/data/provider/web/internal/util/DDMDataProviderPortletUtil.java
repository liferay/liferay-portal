/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.util.comparator.DataProviderInstanceModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.DataProviderInstanceNameComparator;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leonardo Barros
 */
public class DDMDataProviderPortletUtil {

	public static OrderByComparator<DDMDataProviderInstance>
		getDDMDataProviderOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<DDMDataProviderInstance> orderByComparator = null;

		if (orderByCol.equals("modified-date")) {
			orderByComparator = new DataProviderInstanceModifiedDateComparator(
				orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = new DataProviderInstanceNameComparator(
				orderByAsc);
		}

		return orderByComparator;
	}

	public static Set<String> getDDMFormFieldNamesByType(
		DDMForm ddmForm, String type) {

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		Collection<DDMFormField> ddmFormFields = ddmFormFieldsMap.values();

		Stream<DDMFormField> ddmFormFieldStream = ddmFormFields.stream();

		Set<String> ddmFormFieldNames = ddmFormFieldStream.filter(
			ddmFormField -> Objects.equals(ddmFormField.getType(), type)
		).map(
			DDMFormField::getName
		).collect(
			Collectors.toSet()
		);

		return ddmFormFieldNames;
	}

}