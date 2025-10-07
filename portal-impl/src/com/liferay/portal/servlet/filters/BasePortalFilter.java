/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BasePortalFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled() {
		return _filterEnabled;
	}

	@Override
	public void setFilterEnabled(boolean filterEnabled) {
		_filterEnabled = filterEnabled;
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private boolean _filterEnabled = GetterUtil.getBoolean(
		PropsUtil.get(getClass().getName()), true);
	private final Log _log = LogFactoryUtil.getLog(getClass());

}