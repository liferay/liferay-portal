/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.sort;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortBuilder;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import java.util.Locale;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class SortBuilderImpl implements SortBuilder {

	public SortBuilderImpl(Sorts sorts) {
		_sorts = sorts;
	}

	@Override
	public Sort build() {
		if (!Validator.isBlank(_field)) {
			return _sorts.field(_getSortableField(), _sortOrder);
		}

		throw new UnsupportedOperationException();
	}

	@Override
	public SortBuilder field(String field) {
		_field = field;

		return this;
	}

	@Override
	public SortBuilder locale(Locale locale) {
		_locale = locale;

		return this;
	}

	@Override
	public SortBuilder sortOrder(SortOrder sortOrder) {
		_sortOrder = sortOrder;

		return this;
	}

	private String _getSortableField() {
		if ((_locale != null) && _field.equals(Field.TITLE)) {
			return StringBundler.concat(
				"localized_",
				LocalizationUtil.getLocalizedName(
					_field, LocaleUtil.toLanguageId(_locale)),
				"_sortable");
		}

		return _field;
	}

	private String _field;
	private Locale _locale;
	private SortOrder _sortOrder;
	private final Sorts _sorts;

}