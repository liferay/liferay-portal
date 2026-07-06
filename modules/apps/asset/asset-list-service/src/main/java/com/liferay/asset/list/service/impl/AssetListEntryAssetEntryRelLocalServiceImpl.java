/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.list.exception.AssetListEntryAssetEntryRelPostionException;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.service.base.AssetListEntryAssetEntryRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.mass.delete.MassDeleteCacheThreadLocal;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel savinov
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntryAssetEntryRel",
	service = AopService.class
)
public class AssetListEntryAssetEntryRelLocalServiceImpl
	extends AssetListEntryAssetEntryRelLocalServiceBaseImpl {

	@Override
	public AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			int position, ServiceContext serviceContext)
		throws PortalException {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.fetchByA_S_P(
				assetListEntryId, segmentsEntryId, position);

		if (assetListEntryAssetEntryRel != null) {
			throw new AssetListEntryAssetEntryRelPostionException();
		}

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long assetListEntryAssetEntryRelId = counterLocalService.increment();

		assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.create(
				assetListEntryAssetEntryRelId);

		assetListEntryAssetEntryRel.setUuid(serviceContext.getUuid());
		assetListEntryAssetEntryRel.setGroupId(
			serviceContext.getScopeGroupId());
		assetListEntryAssetEntryRel.setCompanyId(serviceContext.getCompanyId());
		assetListEntryAssetEntryRel.setUserId(serviceContext.getUserId());
		assetListEntryAssetEntryRel.setUserName(user.getFullName());
		assetListEntryAssetEntryRel.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		assetListEntryAssetEntryRel.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		assetListEntryAssetEntryRel.setAssetListEntryId(assetListEntryId);
		assetListEntryAssetEntryRel.setAssetEntryId(assetEntryId);
		assetListEntryAssetEntryRel.setSegmentsEntryId(segmentsEntryId);
		assetListEntryAssetEntryRel.setPosition(position);

		return assetListEntryAssetEntryRelPersistence.update(
			assetListEntryAssetEntryRel);
	}

	@Override
	public AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		int position = getAssetListEntryAssetEntryRelsCount(
			assetListEntryId, segmentsEntryId);

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.fetchByA_S_P(
				assetListEntryId, segmentsEntryId, position);

		if (assetListEntryAssetEntryRel != null) {
			throw new AssetListEntryAssetEntryRelPostionException();
		}

		return addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, segmentsEntryId, position,
			serviceContext);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetListEntryAssetEntryRel deleteAssetListEntryAssetEntryRel(
			AssetListEntryAssetEntryRel assetListEntryAssetEntryRel)
		throws PortalException {

		assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.remove(
				assetListEntryAssetEntryRel);

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			assetListEntryAssetEntryRelPersistence.findByA_S_GtP(
				assetListEntryAssetEntryRel.getAssetListEntryId(),
				assetListEntryAssetEntryRel.getSegmentsEntryId(),
				assetListEntryAssetEntryRel.getPosition());

		for (AssetListEntryAssetEntryRel curAssetListEntryAssetEntryRel :
				assetListEntryAssetEntryRels) {

			curAssetListEntryAssetEntryRel.setPosition(
				curAssetListEntryAssetEntryRel.getPosition() - 1);

			assetListEntryAssetEntryRelPersistence.update(
				curAssetListEntryAssetEntryRel);
		}

		return assetListEntryAssetEntryRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetListEntryAssetEntryRel deleteAssetListEntryAssetEntryRel(
			long assetListEntryId, long segmentsEntryId, int position)
		throws PortalException {

		return deleteAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelPersistence.findByA_S_P(
				assetListEntryId, segmentsEntryId, position));
	}

	@Override
	public void deleteAssetListEntryAssetEntryRelByAssetEntryId(
			long assetEntryId)
		throws PortalException {

		String ownerName =
			AssetListEntryAssetEntryRelLocalServiceImpl.class.getName() +
				".deleteAssetListEntryAssetEntryRelByAssetEntryId";

		Map<Long, List<AssetListEntryAssetEntryRel>>
			partitionAssetListEntryAssetEntryRels =
				MassDeleteCacheThreadLocal.getMassDeleteCache(
					ownerName,
					() -> MapUtil.toPartitionMap(
						assetListEntryAssetEntryRelPersistence.findAll(),
						AssetListEntryAssetEntryRel::getAssetEntryId));

		if (partitionAssetListEntryAssetEntryRels == null) {
			for (AssetListEntryAssetEntryRel assetListEntryAssetEntryRel :
					assetListEntryAssetEntryRelPersistence.findByAssetEntryId(
						assetEntryId)) {

				deleteAssetListEntryAssetEntryRel(assetListEntryAssetEntryRel);
			}
		}
		else {
			List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
				partitionAssetListEntryAssetEntryRels.remove(assetEntryId);

			if (assetListEntryAssetEntryRels != null) {
				for (AssetListEntryAssetEntryRel assetListEntryAssetEntryRel :
						assetListEntryAssetEntryRels) {

					deleteAssetListEntryAssetEntryRel(
						assetListEntryAssetEntryRel);
				}
			}
		}
	}

	@Override
	public void deleteAssetListEntryAssetEntryRelByAssetListEntryId(
		long assetListEntryId) {

		assetListEntryAssetEntryRelPersistence.removeByAssetListEntryId(
			assetListEntryId);
	}

	@Override
	public List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRelByAssetEntryId(long assetEntryId) {

		return assetListEntryAssetEntryRelPersistence.findByAssetEntryId(
			assetEntryId);
	}

	@Override
	public List<AssetListEntryAssetEntryRel> getAssetListEntryAssetEntryRels(
		long assetListEntryId, int start, int end) {

		return assetListEntryAssetEntryRelPersistence.findByAssetListEntryId(
			assetListEntryId, start, end);
	}

	@Override
	public List<AssetListEntryAssetEntryRel> getAssetListEntryAssetEntryRels(
		long assetListEntryId, long segmentsEntryId, int start, int end) {

		return assetListEntryAssetEntryRelPersistence.findByA_S(
			assetListEntryId, segmentsEntryId, start, end);
	}

	@Override
	public List<AssetListEntryAssetEntryRel> getAssetListEntryAssetEntryRels(
		long assetListEntryId, long[] segmentsEntryIds, int start, int end) {

		return assetListEntryAssetEntryRelPersistence.findByA_S(
			assetListEntryId, segmentsEntryIds, start, end);
	}

	@Override
	public int getAssetListEntryAssetEntryRelsCount(long assetListEntryId) {
		return assetListEntryAssetEntryRelPersistence.countByAssetListEntryId(
			assetListEntryId);
	}

	@Override
	public int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, long segmentsEntryId) {

		return assetListEntryAssetEntryRelPersistence.countByA_S(
			assetListEntryId, segmentsEntryId);
	}

	@Override
	public int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, long[] segmentsEntryIds) {

		return assetListEntryAssetEntryRelPersistence.countByA_S(
			assetListEntryId, segmentsEntryIds);
	}

	@Override
	public AssetListEntryAssetEntryRel moveAssetListEntryAssetEntryRel(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws PortalException {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.findByA_S_P(
				assetListEntryId, segmentsEntryId, position);

		int count =
			assetListEntryAssetEntryRelPersistence.countByAssetListEntryId(
				assetListEntryId);

		if ((newPosition < 0) || (newPosition >= count)) {
			return assetListEntryAssetEntryRel;
		}

		AssetListEntryAssetEntryRel swapAssetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.fetchByA_S_P(
				assetListEntryId, segmentsEntryId, newPosition);

		if (swapAssetListEntryAssetEntryRel == null) {
			assetListEntryAssetEntryRel.setPosition(newPosition);

			return assetListEntryAssetEntryRelPersistence.update(
				assetListEntryAssetEntryRel);
		}

		assetListEntryAssetEntryRel.setPosition(-1);

		assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.update(
				assetListEntryAssetEntryRel);

		long assetListEntryAssetEntryRelId =
			assetListEntryAssetEntryRel.getAssetListEntryAssetEntryRelId();

		swapAssetListEntryAssetEntryRel.setPosition(-2);

		swapAssetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.update(
				swapAssetListEntryAssetEntryRel);

		long swapAssetListEntryAssetEntryRelId =
			swapAssetListEntryAssetEntryRel.getAssetListEntryAssetEntryRelId();

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				AssetListEntryAssetEntryRel
					callbackAssetListEntryAssetEntryRel =
						assetListEntryAssetEntryRelLocalService.
							fetchAssetListEntryAssetEntryRel(
								assetListEntryAssetEntryRelId);

				callbackAssetListEntryAssetEntryRel.setPosition(newPosition);

				assetListEntryAssetEntryRelLocalService.
					updateAssetListEntryAssetEntryRel(
						callbackAssetListEntryAssetEntryRel);

				callbackAssetListEntryAssetEntryRel =
					assetListEntryAssetEntryRelLocalService.
						fetchAssetListEntryAssetEntryRel(
							swapAssetListEntryAssetEntryRelId);

				callbackAssetListEntryAssetEntryRel.setPosition(position);

				assetListEntryAssetEntryRelLocalService.
					updateAssetListEntryAssetEntryRel(
						callbackAssetListEntryAssetEntryRel);

				return null;
			});

		return assetListEntryAssetEntryRel;
	}

	@Override
	public AssetListEntryAssetEntryRel updateAssetListEntryAssetEntryRel(
			long assetListEntryAssetEntryRelId, long assetListEntryId,
			long assetEntryId, long segmentsEntryId, int position)
		throws PortalException {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.findByPrimaryKey(
				assetListEntryAssetEntryRelId);

		assetListEntryAssetEntryRel.setAssetListEntryId(assetListEntryId);
		assetListEntryAssetEntryRel.setAssetEntryId(assetEntryId);
		assetListEntryAssetEntryRel.setSegmentsEntryId(segmentsEntryId);
		assetListEntryAssetEntryRel.setPosition(position);

		return assetListEntryAssetEntryRelPersistence.update(
			assetListEntryAssetEntryRel);
	}

	@Reference
	private UserLocalService _userLocalService;

}