/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.osgi.commands;

import com.liferay.asset.auto.tagger.AssetAutoTagger;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfigurationFactory;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.service.AssetAutoTaggerEntryLocalService;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"osgi.command.function=commitAutoTags",
		"osgi.command.function=tagAllUntagged",
		"osgi.command.function=untagAll", "osgi.command.scope=assetAutoTagger"
	},
	service = OSGiCommands.class
)
public class AssetAutoTaggerOSGiCommands implements OSGiCommands {

	public void commitAutoTags(String companyId) {
		_forEachAssetEntry(
			companyId,
			assetEntry -> {
				List<AssetAutoTaggerEntry> assetAutoTaggerEntries =
					_assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
						assetEntry);

				for (AssetAutoTaggerEntry assetAutoTaggerEntry :
						assetAutoTaggerEntries) {

					_assetAutoTaggerEntryLocalService.
						deleteAssetAutoTaggerEntry(assetAutoTaggerEntry);
				}

				if (!assetAutoTaggerEntries.isEmpty()) {
					System.out.println(
						String.format(
							"Commited %d auto tags for asset entry %s",
							assetAutoTaggerEntries.size(),
							assetEntry.getTitle()));
				}
			});
	}

	public void tagAllUntagged(String companyId) {
		AssetAutoTaggerConfiguration assetAutoTaggerConfiguration =
			_assetAutoTaggerConfigurationFactory.
				getSystemAssetAutoTaggerConfiguration();

		if (!assetAutoTaggerConfiguration.isEnabled()) {
			System.out.println("Asset auto tagger is disabled");

			return;
		}

		_forEachAssetEntry(
			companyId,
			assetEntry -> {
				String[] oldAssetTagNames = assetEntry.getTagNames();

				if (oldAssetTagNames.length > 0) {
					return;
				}

				_assetAutoTagger.tag(assetEntry);

				String[] newAssetTagNames = assetEntry.getTagNames();

				if (oldAssetTagNames.length != newAssetTagNames.length) {
					System.out.println(
						String.format(
							"Added %d tags to asset entry %s",
							newAssetTagNames.length - oldAssetTagNames.length,
							assetEntry.getTitle()));
				}
			});
	}

	public void untagAll(String companyId) {
		_forEachAssetEntry(
			companyId,
			assetEntry -> {
				String[] oldAssetTagNames = assetEntry.getTagNames();

				_assetAutoTagger.untag(assetEntry);

				String[] newAssetTagNames = assetEntry.getTagNames();

				if (oldAssetTagNames.length != newAssetTagNames.length) {
					System.out.println(
						String.format(
							"Deleted %d tags to asset entry %s",
							oldAssetTagNames.length - newAssetTagNames.length,
							assetEntry.getTitle()));
				}
			});
	}

	private void _forEachAssetEntry(
		String companyId,
		UnsafeConsumer<AssetEntry, PortalException> consumer) {

		try {
			ActionableDynamicQuery actionableDynamicQuery =
				_assetEntryLocalService.getActionableDynamicQuery();

			if (Validator.isNotNull(companyId)) {
				actionableDynamicQuery.setCompanyId(Long.valueOf(companyId));
			}

			actionableDynamicQuery.setPerformActionMethod(
				(AssetEntry assetEntry) -> {
					try (SafeCloseable safeCloseable =
							CompanyThreadLocal.setCompanyIdWithSafeCloseable(
								assetEntry.getCompanyId())) {

						consumer.accept(assetEntry);
					}
				});

			actionableDynamicQuery.performActions();
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetAutoTaggerOSGiCommands.class);

	@Reference
	private AssetAutoTagger _assetAutoTagger;

	@Reference
	private AssetAutoTaggerConfigurationFactory
		_assetAutoTaggerConfigurationFactory;

	@Reference
	private AssetAutoTaggerEntryLocalService _assetAutoTaggerEntryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

}