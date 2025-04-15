/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.search;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 */
public class ImageConfigurationEntriesChecker extends EmptyOnClickRowChecker {

	public ImageConfigurationEntriesChecker(
		LiferayPortletResponse liferayPortletResponse) {

		super(liferayPortletResponse);

		_liferayPortletResponse = liferayPortletResponse;
	}

	@Override
	public String getAllRowsCheckBox() {
		return null;
	}

	@Override
	public String getAllRowsCheckBox(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String primaryKey) {

		String name = AMImageConfigurationEntry.class.getSimpleName();

		String checkBoxRowIds = _getEntryRowIds();

		return getRowCheckBox(
			httpServletRequest, checked, disabled,
			_liferayPortletResponse.getNamespace() + RowChecker.ROW_IDS + name,
			primaryKey, checkBoxRowIds, "'#" + getAllRowIds() + "'",
			StringPool.BLANK);
	}

	private String _getEntryRowIds() {
		return StringBundler.concat(
			"['", _liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
			AMImageConfigurationEntry.class.getSimpleName(), "']");
	}

	private final LiferayPortletResponse _liferayPortletResponse;

}