/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.item.selector.web.internal.search;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.PortletResponse;

/**
 * @author Eudaldo Alonso
 */
public class EntriesChecker extends EmptyOnClickRowChecker {

	public EntriesChecker(
		PortletResponse portletResponse, String[] selectedTagNames) {

		super(portletResponse);

		_selectedTagNames = selectedTagNames;
	}

	@Override
	public boolean isChecked(Object object) {
		if (ArrayUtil.isEmpty(_selectedTagNames)) {
			return false;
		}

		AssetTag tag = (AssetTag)object;

		return ArrayUtil.contains(_selectedTagNames, tag.getName());
	}

	private final String[] _selectedTagNames;

}