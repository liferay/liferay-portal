/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util.comparator;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.Collator;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class StructureNameComparator extends OrderByComparator<DDMStructure> {

	public static final String ORDER_BY_ASC =
		"CAST_CLOB_TEXT(DDMStructure.name) ASC";

	public static final String ORDER_BY_DESC =
		"CAST_CLOB_TEXT(DDMStructure.name) DESC";

	public static final String[] ORDER_BY_FIELDS = {"name"};

	public StructureNameComparator() {
		this(false);
	}

	public StructureNameComparator(boolean ascending) {
		this(ascending, LocaleUtil.getDefault());
	}

	public StructureNameComparator(boolean ascending, Locale locale) {
		_ascending = ascending;
		_locale = locale;

		_collator = CollatorUtil.getInstance(locale);
	}

	@Override
	public int compare(DDMStructure ddmStructure1, DDMStructure ddmStructure2) {
		String name1 = StringUtil.toLowerCase(ddmStructure1.getName(_locale));
		String name2 = StringUtil.toLowerCase(ddmStructure2.getName(_locale));

		int value = _collator.compare(name1, name2);

		if (_ascending) {
			return value;
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}

		return ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private final boolean _ascending;
	private final Collator _collator;
	private final Locale _locale;

}