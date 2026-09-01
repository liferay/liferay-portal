/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.site.model.SiteSitemapRegenerationEntry;
import com.liferay.site.service.base.SiteSitemapRegenerationEntryLocalServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "model.class.name=com.liferay.site.model.SiteSitemapRegenerationEntry",
	service = AopService.class
)
public class SiteSitemapRegenerationEntryLocalServiceImpl
	extends SiteSitemapRegenerationEntryLocalServiceBaseImpl {

	@Override
	public SiteSitemapRegenerationEntry addSiteSitemapRegenerationEntry(
		String assetTypeKey, long companyId, long groupId) {

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry =
			siteSitemapRegenerationEntryPersistence.fetchByG_C_A_First(
				groupId, companyId, assetTypeKey, null);

		if (siteSitemapRegenerationEntry != null) {
			return siteSitemapRegenerationEntry;
		}

		siteSitemapRegenerationEntry =
			siteSitemapRegenerationEntryPersistence.create(
				counterLocalService.increment());

		siteSitemapRegenerationEntry.setGroupId(groupId);
		siteSitemapRegenerationEntry.setCompanyId(companyId);
		siteSitemapRegenerationEntry.setAssetTypeKey(assetTypeKey);

		return siteSitemapRegenerationEntryPersistence.update(
			siteSitemapRegenerationEntry);
	}

	@Override
	public void deleteSiteSitemapRegenerationEntries(long companyId) {
		siteSitemapRegenerationEntryPersistence.removeByCompanyId(companyId);
	}

	@Override
	public List<SiteSitemapRegenerationEntry> getSiteSitemapRegenerationEntries(
		long companyId) {

		return siteSitemapRegenerationEntryPersistence.findByCompanyId(
			companyId);
	}

	@Override
	public int getSiteSitemapRegenerationEntriesCount(long companyId) {
		return siteSitemapRegenerationEntryPersistence.countByCompanyId(
			companyId);
	}

}