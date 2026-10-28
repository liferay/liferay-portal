/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.service.base.AssetListEntryAudienceEntryRelLocalServiceBaseImpl;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelPersistence;
import com.liferay.asset.list.util.comparator.AssetListEntryAudienceEntryRelPriorityComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntryAudienceEntryRel",
	service = AopService.class
)
public class AssetListEntryAudienceEntryRelLocalServiceImpl
	extends AssetListEntryAudienceEntryRelLocalServiceBaseImpl {

	@Override
	public AssetListEntryAudienceEntryRel addAssetListEntryAudienceEntryRel(
			long userId, long groupId, long assetListEntryId,
			String audienceEntryERC, int priority, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			assetListEntryAudienceEntryRelPersistence.create(
				counterLocalService.increment());

		assetListEntryAudienceEntryRel.setUuid(serviceContext.getUuid());
		assetListEntryAudienceEntryRel.setGroupId(groupId);
		assetListEntryAudienceEntryRel.setCompanyId(user.getCompanyId());
		assetListEntryAudienceEntryRel.setUserId(userId);
		assetListEntryAudienceEntryRel.setUserName(user.getFullName());
		assetListEntryAudienceEntryRel.setAssetListEntryId(assetListEntryId);
		assetListEntryAudienceEntryRel.setAudienceEntryERC(audienceEntryERC);
		assetListEntryAudienceEntryRel.setPriority(priority);
		assetListEntryAudienceEntryRel.setTypeSettings(typeSettings);

		return assetListEntryAudienceEntryRelPersistence.update(
			assetListEntryAudienceEntryRel);
	}

	@Override
	public AssetListEntryAudienceEntryRel addAssetListEntryAudienceEntryRel(
			long userId, long groupId, long assetListEntryId,
			String audienceEntryERC, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		return assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				userId, groupId, assetListEntryId, audienceEntryERC,
				assetListEntryAudienceEntryRelPersistence.
					countByAssetListEntryId(assetListEntryId),
				typeSettings, serviceContext);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetListEntryAudienceEntryRel deleteAssetListEntryAudienceEntryRel(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		assetListEntryAudienceEntryRelPersistence.remove(
			assetListEntryAudienceEntryRel);

		_assetListEntryAssetEntryRelPersistence.removeByA_AEERC_S(
			assetListEntryAudienceEntryRel.getAssetListEntryId(),
			assetListEntryAudienceEntryRel.getAudienceEntryERC(), 0);

		return assetListEntryAudienceEntryRel;
	}

	@Override
	public void deleteAssetListEntryAudienceEntryRel(
			long assetListEntryId, String audienceEntryERC)
		throws PortalException {

		assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRelPersistence.findByA_AEERC(
					assetListEntryId, audienceEntryERC));
	}

	@Override
	public void deleteAssetListEntryAudienceEntryRelByAssetListEntryId(
		long assetListEntryId) {

		for (AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel :
				assetListEntryAudienceEntryRelPersistence.
					findByAssetListEntryId(assetListEntryId)) {

			assetListEntryAudienceEntryRelLocalService.
				deleteAssetListEntryAudienceEntryRel(
					assetListEntryAudienceEntryRel);
		}
	}

	@Override
	public void deleteAssetListEntryAudienceEntryRelByAudienceEntryERC(
		long companyId, String audienceEntryERC) {

		for (AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel :
				assetListEntryAudienceEntryRelPersistence.findByC_AEERC(
					companyId, audienceEntryERC)) {

			assetListEntryAudienceEntryRelLocalService.
				deleteAssetListEntryAudienceEntryRel(
					assetListEntryAudienceEntryRel);
		}
	}

	@Override
	public AssetListEntryAudienceEntryRel fetchAssetListEntryAudienceEntryRel(
		long assetListEntryId, String audienceEntryERC) {

		return assetListEntryAudienceEntryRelPersistence.fetchByA_AEERC(
			assetListEntryId, audienceEntryERC);
	}

	@Override
	public AssetListEntryAudienceEntryRel fetchAssetListEntryAudienceEntryRel(
		long assetListEntryId, String[] audienceEntryERCs) {

		if (ArrayUtil.isEmpty(audienceEntryERCs)) {
			return null;
		}

		List<AssetListEntryAudienceEntryRel> assetListEntryAudienceEntryRels =
			assetListEntryAudienceEntryRelPersistence.findByA_AEERC_C(
				assetListEntryId, audienceEntryERCs, 0, 1,
				AssetListEntryAudienceEntryRelPriorityComparator.getInstance(
					true));

		if (assetListEntryAudienceEntryRels.isEmpty()) {
			return null;
		}

		return assetListEntryAudienceEntryRels.get(0);
	}

	@Override
	public List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRels(
			long assetListEntryId, int start, int end) {

		return assetListEntryAudienceEntryRelPersistence.findByAssetListEntryId(
			assetListEntryId, start, end);
	}

	@Override
	public int getAssetListEntryAudienceEntryRelsCount(long assetListEntryId) {
		return assetListEntryAudienceEntryRelPersistence.
			countByAssetListEntryId(assetListEntryId);
	}

	@Override
	public AssetListEntryAudienceEntryRel
			updateAssetListEntryAudienceEntryRelTypeSettings(
				long assetListEntryId, String audienceEntryERC,
				String typeSettings)
		throws PortalException {

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			assetListEntryAudienceEntryRelPersistence.findByA_AEERC(
				assetListEntryId, audienceEntryERC);

		assetListEntryAudienceEntryRel.setTypeSettings(typeSettings);

		return assetListEntryAudienceEntryRelPersistence.update(
			assetListEntryAudienceEntryRel);
	}

	@Override
	public void updatePriorities(long[] assetListEntryAudienceEntryRelIds)
		throws PortalException {

		for (int priority = 0;
			 priority < assetListEntryAudienceEntryRelIds.length; priority++) {

			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
				assetListEntryAudienceEntryRelPersistence.findByPrimaryKey(
					assetListEntryAudienceEntryRelIds[priority]);

			assetListEntryAudienceEntryRel.setPriority(priority);

			assetListEntryAudienceEntryRelPersistence.update(
				assetListEntryAudienceEntryRel);
		}
	}

	@Reference
	private AssetListEntryAssetEntryRelPersistence
		_assetListEntryAssetEntryRelPersistence;

	@Reference
	private UserLocalService _userLocalService;

}