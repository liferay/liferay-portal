/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import java.util.List;

/**
 * @author Luis Miguel Barcos
 */
public class SortUtil {

	public static Sort[] getSorts(
		AcceptLanguage acceptLanguage, EntityModel entityModel,
		SortParser sortParser, String sortString) {

		if (_log.isDebugEnabled()) {
			_log.debug("Sort parameter value: " + sortString);
		}

		if (Validator.isNull(sortString)) {
			return null;
		}

		if (_log.isDebugEnabled() && (entityModel != null)) {
			_log.debug("OData entity model name: " + entityModel.getName());
		}

		if (sortParser == null) {
			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("OData sort parser: " + sortParser);
		}

		com.liferay.portal.odata.sort.Sort oDataSort =
			new com.liferay.portal.odata.sort.Sort(
				sortParser.parse(sortString));

		if (_log.isDebugEnabled()) {
			_log.debug("OData sort: " + oDataSort);
		}

		List<SortField> sortFields = oDataSort.getSortFields();

		Sort[] sorts = new Sort[sortFields.size()];

		for (int i = 0; i < sortFields.size(); i++) {
			SortField sortField = sortFields.get(i);

			sorts[i] = new Sort(
				sortField.getSortableFieldName(
					acceptLanguage.getPreferredLocale()),
				sortField.getSortableFieldPath(
					acceptLanguage.getPreferredLocale()),
				_getSortType(sortField.getEntityFieldType()),
				!sortField.isAscending());
		}

		return sorts;
	}

	private static int _getSortType(EntityField.Type entityFieldType) {
		if (entityFieldType == EntityField.Type.DOUBLE) {
			return Sort.DOUBLE_TYPE;
		}

		if (entityFieldType == EntityField.Type.ID) {
			return Sort.LONG_TYPE;
		}

		if (entityFieldType == EntityField.Type.INTEGER) {
			return Sort.INT_TYPE;
		}

		return Sort.STRING_TYPE;
	}

	private static final Log _log = LogFactoryUtil.getLog(SortUtil.class);

}