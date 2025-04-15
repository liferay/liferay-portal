/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.search;

import com.liferay.analytics.settings.web.internal.model.Field;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Rachael Koestartyo
 */
public class FieldChecker extends EmptyOnClickRowChecker {

	public FieldChecker(
		String mvcRenderCommandName, RenderResponse renderResponse,
		String[] recommendedFieldNames, String[] requiredFieldNames,
		String[] selectedFieldNames) {

		super(renderResponse);

		if (StringUtil.equalsIgnoreCase(
				mvcRenderCommandName,
				"/analytics_settings/edit_synced_contacts_fields")) {

			setRowIds("syncedContactFieldNames");
		}
		else if (StringUtil.equalsIgnoreCase(
					mvcRenderCommandName,
					"/analytics_settings/edit_synced_users_fields")) {

			setRowIds("syncedUserFieldNames");
		}

		_recommendedFieldNames = recommendedFieldNames;
		_requiredFieldNames = requiredFieldNames;
		_selectedFieldNames = selectedFieldNames;
	}

	@Override
	public boolean isChecked(Object object) {
		Field field = (Field)object;

		if (ArrayUtil.contains(_recommendedFieldNames, field.getName()) ||
			ArrayUtil.contains(_requiredFieldNames, field.getName()) ||
			ArrayUtil.contains(_selectedFieldNames, field.getName())) {

			return true;
		}

		return super.isChecked(object);
	}

	@Override
	public boolean isDisabled(Object object) {
		Field field = (Field)object;

		if (ArrayUtil.contains(_requiredFieldNames, field.getName())) {
			return true;
		}

		return super.isDisabled(object);
	}

	private final String[] _recommendedFieldNames;
	private final String[] _requiredFieldNames;
	private final String[] _selectedFieldNames;

}