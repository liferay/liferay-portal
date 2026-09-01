/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.service.SiteSitemapRegenerationEntryLocalService;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
public abstract class BaseSitemapModelListener<T extends BaseModel<T>>
	extends BaseModelListener<T> {

	@Override
	public void onAfterCreate(T model) {
		_addSiteSitemapRegenerationEntry(model);
	}

	@Override
	public void onAfterRemove(T model) {
		_addSiteSitemapRegenerationEntry(model);
	}

	@Override
	public void onAfterUpdate(T originalModel, T model) {
		_addSiteSitemapRegenerationEntry(model);
	}

	protected abstract String getAssetTypeKey();

	@Reference
	protected SitemapConfigurationManager sitemapConfigurationManager;

	@Reference
	protected SiteSitemapRegenerationEntryLocalService
		siteSitemapRegenerationEntryLocalService;

	private void _addSiteSitemapRegenerationEntry(T model) {
		GroupedModel groupedModel = (GroupedModel)model;

		try {
			long companyId = groupedModel.getCompanyId();

			if (!sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
					companyId) ||
				!sitemapConfigurationManager.isIndexModeAssetTypeCompanyEnabled(
					companyId)) {

				return;
			}

			siteSitemapRegenerationEntryLocalService.
				addSiteSitemapRegenerationEntry(
					getAssetTypeKey(), companyId, groupedModel.getGroupId());
		}
		catch (Exception exception) {
			_log.error(
				"Unable to add XML sitemap regeneration entry", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSitemapModelListener.class);

}