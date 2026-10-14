/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.service.SiteSitemapRegenerationEntryLocalService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
public abstract class BaseSitemapModelListener<T extends BaseModel<T>>
	extends BaseModelListener<T> {

	@Override
	public void onAfterCreate(T model) {
		_addSiteSitemapRegenerationEntries(null, model);
	}

	@Override
	public void onAfterRemove(T model) {
		_addSiteSitemapRegenerationEntries(null, model);
	}

	@Override
	public void onAfterUpdate(T originalModel, T model) {
		_addSiteSitemapRegenerationEntries(originalModel, model);
	}

	protected abstract String getAssetTypeKey();

	protected Set<String> getLayoutUuids(T originalModel, T model)
		throws PortalException {

		return null;
	}

	@Reference
	protected DepotEntryGroupRelLocalService depotEntryGroupRelLocalService;

	@Reference
	protected DepotEntryLocalService depotEntryLocalService;

	@Reference
	protected SiteSitemapRegenerationEntryLocalService
		siteSitemapRegenerationEntryLocalService;

	@Reference
	protected SitemapConfigurationManager sitemapConfigurationManager;

	private void _addSiteSitemapRegenerationEntries(T originalModel, T model) {
		GroupedModel groupedModel = (GroupedModel)model;

		try {
			long companyId = groupedModel.getCompanyId();

			if (!sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
					companyId) ||
				!sitemapConfigurationManager.isXMLSitemapIndexCompanyEnabled(
					companyId)) {

				return;
			}

			long groupId = groupedModel.getGroupId();

			if (sitemapConfigurationManager.isIndexModeAssetTypeCompanyEnabled(
					companyId)) {

				siteSitemapRegenerationEntryLocalService.
					addSiteSitemapRegenerationEntry(
						getAssetTypeKey(), companyId, groupId);

				return;
			}

			Set<String> layoutUuids = getLayoutUuids(originalModel, model);

			if (layoutUuids == null) {
				for (long siteGroupId : _getSiteGroupIds(groupId)) {
					siteSitemapRegenerationEntryLocalService.
						addSiteSitemapRegenerationEntry(
							SitemapConstants.INDEX_MODE_PAGE_LAYOUT, companyId,
							siteGroupId);
				}

				return;
			}

			for (String layoutUuid : layoutUuids) {
				siteSitemapRegenerationEntryLocalService.
					addSiteSitemapRegenerationEntry(
						SitemapConstants.INDEX_MODE_PAGE_LAYOUT +
							StringPool.SLASH + layoutUuid,
						companyId, groupId);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to add XML sitemap regeneration entry", exception);
		}
	}

	private List<Long> _getSiteGroupIds(long groupId) {
		DepotEntry depotEntry = depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			return Collections.singletonList(groupId);
		}

		return TransformUtil.transform(
			depotEntryGroupRelLocalService.getDepotEntryGroupRels(depotEntry),
			DepotEntryGroupRel::getToGroupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSitemapModelListener.class);

}