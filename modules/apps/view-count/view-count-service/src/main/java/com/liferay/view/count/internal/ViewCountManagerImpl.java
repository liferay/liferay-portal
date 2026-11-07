/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.internal;

import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.view.count.service.ViewCountEntryLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = ViewCountManager.class)
public class ViewCountManagerImpl implements ViewCountManager {

	@Override
	public void deleteViewCount(
		long companyId, long classNameId, long classPK) {

		_viewCountEntryLocalService.deleteViewCount(
			companyId, classNameId, classPK);
	}

	@Override
	public long getViewCount(long companyId, long classNameId, long classPK) {
		return _viewCountEntryLocalService.getViewCount(
			companyId, classNameId, classPK);
	}

	@Override
	public Table<?> getViewCountEntryTable() {
		return _viewCountEntryLocalService.getViewCountEntryTable();
	}

	@Override
	public void incrementViewCount(
		long companyId, long classNameId, long classPK, int increment) {

		_viewCountEntryLocalService.incrementViewCount(
			companyId, classNameId, classPK, increment);
	}

	@Override
	public boolean isViewCountEnabled() {
		return _viewCountEntryLocalService.isViewCountEnabled();
	}

	@Override
	public boolean isViewCountEnabled(long classNameId) {
		return _viewCountEntryLocalService.isViewCountEnabled(classNameId);
	}

	@Reference
	private ViewCountEntryLocalService _viewCountEntryLocalService;

}