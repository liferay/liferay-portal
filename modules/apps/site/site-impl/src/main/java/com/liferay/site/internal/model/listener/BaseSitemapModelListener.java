/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.site.manager.SitemapManager;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
public abstract class BaseSitemapModelListener<T extends BaseModel<T>>
	extends BaseModelListener<T> {

	@Override
	public void onAfterCreate(T model) {
		GroupedModel groupedModel = (GroupedModel)model;

		_scheduleRegenerateSitemap(
			groupedModel.getCompanyId(), groupedModel.getGroupId());
	}

	@Override
	public void onAfterRemove(T model) {
		GroupedModel groupedModel = (GroupedModel)model;

		_scheduleRegenerateSitemap(
			groupedModel.getCompanyId(), groupedModel.getGroupId());
	}

	@Override
	public void onAfterUpdate(T originalModel, T model) {
		GroupedModel groupedModel = (GroupedModel)model;

		_scheduleRegenerateSitemap(
			groupedModel.getCompanyId(), groupedModel.getGroupId());
	}

	protected abstract String getAssetTypeKey();

	@Reference
	protected SitemapManager sitemapManager;

	private void _scheduleRegenerateSitemap(long companyId, long groupId) {
		String regenerateSitemapKey = StringBundler.concat(
			getAssetTypeKey(), StringPool.POUND, companyId, StringPool.POUND,
			groupId);

		Set<String> regenerateSitemapKeys = _regenerateSitemapKeys.get();

		if (regenerateSitemapKeys.add(regenerateSitemapKey)) {
			TransactionCallbackUtil.registerCompletionCallback(
				() -> regenerateSitemapKeys.remove(regenerateSitemapKey));

			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					sitemapManager.scheduleRegenerateSitemap(
						getAssetTypeKey(), companyId, groupId, null);

					return null;
				});
		}
	}

	private static final ThreadLocal<Set<String>> _regenerateSitemapKeys =
		new CentralizedThreadLocal<>(
			BaseSitemapModelListener.class + "._regenerateSitemapKeys",
			HashSet::new);

}