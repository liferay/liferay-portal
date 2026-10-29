/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.service.impl;

import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.service.base.AssetAutoTaggerEntryLocalServiceBaseImpl;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "model.class.name=com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry",
	service = AopService.class
)
public class AssetAutoTaggerEntryLocalServiceImpl
	extends AssetAutoTaggerEntryLocalServiceBaseImpl {

	@Override
	public void addAssetAutoTaggerEntries(
		AssetEntry assetEntry, List<String> assetTagNames) {

		for (String assetTagName : assetTagNames) {
			try {
				addAssetAutoTaggerEntry(assetEntry, assetTagName);
			}
			catch (AssetTagException assetTagException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						String.format(
							"Unable to add auto tag: %s", assetTagName),
						assetTagException);
				}
			}
			catch (PortalException portalException) {
				_log.error(
					String.format("Unable to add auto tag: %s", assetTagName),
					portalException);
			}
		}
	}

	@Override
	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
		AssetEntry assetEntry, AssetTag assetTag) {

		AssetAutoTaggerEntry existingAssetAutoTaggerEntry =
			assetAutoTaggerEntryPersistence.fetchByA_A(
				assetEntry.getEntryId(), assetTag.getTagId());

		if (existingAssetAutoTaggerEntry != null) {
			return existingAssetAutoTaggerEntry;
		}

		long assetAutoTaggerEntryId = counterLocalService.increment();

		AssetAutoTaggerEntry assetAutoTaggerEntry =
			assetAutoTaggerEntryPersistence.create(assetAutoTaggerEntryId);

		assetAutoTaggerEntry.setGroupId(assetEntry.getGroupId());
		assetAutoTaggerEntry.setCompanyId(assetEntry.getCompanyId());
		assetAutoTaggerEntry.setAssetEntryId(assetEntry.getEntryId());
		assetAutoTaggerEntry.setAssetTagId(assetTag.getTagId());

		return assetAutoTaggerEntryPersistence.update(assetAutoTaggerEntry);
	}

	@Override
	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
			AssetEntry assetEntry, String assetTagName)
		throws PortalException {

		AssetTag assetTag = _assetTagLocalService.fetchTag(
			assetEntry.getGroupId(), assetTagName);

		if (assetTag == null) {
			assetTag = _assetTagLocalService.addTag(
				null, assetEntry.getUserId(), assetEntry.getGroupId(),
				assetTagName, new ServiceContext());
		}

		_assetTagLocalService.addAssetEntryAssetTag(
			assetEntry.getEntryId(), assetTag);

		_assetTagLocalService.incrementAssetCount(
			assetTag.getTagId(), assetEntry.getClassNameId());

		return addAssetAutoTaggerEntry(assetEntry, assetTag);
	}

	@Override
	public List<AssetAutoTaggerEntry> deleteAssetAutoTaggerEntries(
			AssetEntry assetEntry)
		throws PortalException {

		List<AssetAutoTaggerEntry> assetAutoTaggerEntries =
			assetAutoTaggerEntryPersistence.findByAssetEntryId(
				assetEntry.getEntryId());

		for (AssetAutoTaggerEntry assetAutoTaggerEntry :
				assetAutoTaggerEntries) {

			_assetTagLocalService.deleteAssetEntryAssetTag(
				assetAutoTaggerEntry.getAssetEntryId(),
				assetAutoTaggerEntry.getAssetTagId());

			_assetTagLocalService.decrementAssetCount(
				assetAutoTaggerEntry.getAssetTagId(),
				assetEntry.getClassNameId());
		}

		return assetAutoTaggerEntries;
	}

	@Override
	public AssetAutoTaggerEntry fetchAssetAutoTaggerEntry(
		long assetEntryId, long assetTagId) {

		return assetAutoTaggerEntryPersistence.fetchByA_A(
			assetEntryId, assetTagId);
	}

	@Override
	public List<AssetAutoTaggerEntry> getAssetAutoTaggerEntries(
		AssetEntry assetEntry) {

		return assetAutoTaggerEntryPersistence.findByAssetEntryId(
			assetEntry.getEntryId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetAutoTaggerEntryLocalServiceImpl.class);

	@Reference
	private AssetTagLocalService _assetTagLocalService;

}