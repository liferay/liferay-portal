/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.scheduler;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.model.SiteSitemapRegenerationEntry;
import com.liferay.site.service.SiteSitemapRegenerationEntryLocalService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = SchedulerJobConfiguration.class)
public class XMLSitemapRegenerationSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeConsumer<Long, Exception>
		getCompanyJobExecutorUnsafeConsumer() {

		return companyId -> _regenerateSitemaps(companyId);
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _regenerateSitemaps(companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			1, TimeUnit.HOUR);
	}

	private void _regenerateSitemaps(long companyId) {
		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntries(companyId);

		if (siteSitemapRegenerationEntries.isEmpty()) {
			return;
		}

		Set<String> regeneratedKeys = new HashSet<>();

		for (SiteSitemapRegenerationEntry siteSitemapRegenerationEntry :
				siteSitemapRegenerationEntries) {

			_siteSitemapRegenerationEntryLocalService.
				deleteSiteSitemapRegenerationEntry(
					siteSitemapRegenerationEntry);

			try {
				String regeneratedKey = StringBundler.concat(
					siteSitemapRegenerationEntry.getAssetTypeKey(),
					StringPool.POUND,
					siteSitemapRegenerationEntry.getGroupId());

				if (regeneratedKeys.add(regeneratedKey)) {
					_sitemapManager.regenerateSitemap(
						siteSitemapRegenerationEntry.getAssetTypeKey(),
						companyId, siteSitemapRegenerationEntry.getGroupId());
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to regenerate the sitemap for XML sitemap " +
						"regeneration entry " + siteSitemapRegenerationEntry,
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		XMLSitemapRegenerationSchedulerJobConfiguration.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SiteSitemapRegenerationEntryLocalService
		_siteSitemapRegenerationEntryLocalService;

}