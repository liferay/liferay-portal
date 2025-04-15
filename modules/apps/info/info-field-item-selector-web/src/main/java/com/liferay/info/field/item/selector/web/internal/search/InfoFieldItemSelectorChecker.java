/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field.item.selector.web.internal.search;

import com.liferay.info.field.InfoField;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;

import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class InfoFieldItemSelectorChecker extends EmptyOnClickRowChecker {

	public InfoFieldItemSelectorChecker(
		RenderResponse renderResponse, List<String> checkedUniqueInfoFieldIds) {

		super(renderResponse);

		_checkedUniqueInfoFieldIds = checkedUniqueInfoFieldIds;
	}

	@Override
	public boolean isChecked(Object object) {
		InfoField<?> infoField = (InfoField<?>)object;

		return _checkedUniqueInfoFieldIds.contains(infoField.getUniqueId());
	}

	private final List<String> _checkedUniqueInfoFieldIds;

}