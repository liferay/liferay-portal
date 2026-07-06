/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.site.manager.SitemapManager;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
public abstract class BaseSitemapModelListener<T extends BaseModel<T>>
	extends BaseModelListener<T> {

	@Override
	public void onAfterCreate(T model) {
		_scheduleRegenerateSitemap(model);
	}

	@Override
	public void onAfterRemove(T model) {
		_scheduleRegenerateSitemap(model);
	}

	@Override
	public void onAfterUpdate(T originalModel, T model) {
		_scheduleRegenerateSitemap(model);
	}

	protected abstract String getAssetTypeKey();

	@Reference
	protected SitemapManager sitemapManager;

	private void _scheduleRegenerateSitemap(T model) {
		GroupedModel groupedModel = (GroupedModel)model;

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				sitemapManager.scheduleRegenerateSitemap(
					getAssetTypeKey(), groupedModel.getCompanyId(),
					groupedModel.getGroupId(), null);

				return null;
			});
	}

}