/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.list.exception.AssetListEntryAssetEntryRelAudienceEntryERCAndSegmentsEntryIdException;
import com.liferay.asset.list.exception.AssetListEntryAssetEntryRelPostionException;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.service.base.AssetListEntryAssetEntryRelLocalServiceBaseImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.mass.delete.MassDeleteCacheThreadLocal;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

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

		return addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, StringPool.BLANK, segmentsEntryId,
			position, serviceContext);
	}

	@Override
	public AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		return addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, StringPool.BLANK, segmentsEntryId,
			serviceContext);
	}

	@Override
	public AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, String audienceEntryERC,
			long segmentsEntryId, int position, ServiceContext serviceContext)
		throws PortalException {

		_validate(audienceEntryERC, segmentsEntryId);

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.fetchByA_AEERC_S_P(
				assetListEntryId, audienceEntryERC, segmentsEntryId, position);

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
		assetListEntryAssetEntryRel.setAssetEntryId(assetEntryId);
		assetListEntryAssetEntryRel.setAssetListEntryId(assetListEntryId);
		assetListEntryAssetEntryRel.setAudienceEntryERC(audienceEntryERC);
		assetListEntryAssetEntryRel.setSegmentsEntryId(segmentsEntryId);
		assetListEntryAssetEntryRel.setPosition(position);

		return assetListEntryAssetEntryRelPersistence.update(
			assetListEntryAssetEntryRel);
	}

	@Override
	public AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, String audienceEntryERC,
			long segmentsEntryId, ServiceContext serviceContext)
		throws PortalException {

		int position = getAssetListEntryAssetEntryRelsCount(
			assetListEntryId, audienceEntryERC, segmentsEntryId);

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.fetchByA_AEERC_S_P(
				assetListEntryId, audienceEntryERC, segmentsEntryId, position);

		if (assetListEntryAssetEntryRel != null) {
			throw new AssetListEntryAssetEntryRelPostionException();
		}

		return addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, audienceEntryERC, segmentsEntryId,
			position, serviceContext);
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
			assetListEntryAssetEntryRelPersistence.findByA_AEERC_S_GtP(
				assetListEntryAssetEntryRel.getAssetListEntryId(),
				assetListEntryAssetEntryRel.getAudienceEntryERC(),
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
			assetListEntryId, StringPool.BLANK, segmentsEntryId, position);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetListEntryAssetEntryRel deleteAssetListEntryAssetEntryRel(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int position)
		throws PortalException {

		return deleteAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelPersistence.findByA_AEERC_S_P(
				assetListEntryId, audienceEntryERC, segmentsEntryId, position));
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

		return getAssetListEntryAssetEntryRels(
			assetListEntryId, StringPool.BLANK, segmentsEntryId, start, end);
	}

	@Override
	public List<AssetListEntryAssetEntryRel> getAssetListEntryAssetEntryRels(
		long assetListEntryId, long[] segmentsEntryIds, int start, int end) {

		return assetListEntryAssetEntryRelPersistence.findByA_AEERC_S(
			assetListEntryId, StringPool.BLANK, segmentsEntryIds, start, end);
	}

	@Override
	public List<AssetListEntryAssetEntryRel> getAssetListEntryAssetEntryRels(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int start, int end) {

		return assetListEntryAssetEntryRelPersistence.findByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryId, start, end);
	}

	@Override
	public int getAssetListEntryAssetEntryRelsCount(long assetListEntryId) {
		return assetListEntryAssetEntryRelPersistence.countByAssetListEntryId(
			assetListEntryId);
	}

	@Override
	public int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, long segmentsEntryId) {

		return getAssetListEntryAssetEntryRelsCount(
			assetListEntryId, StringPool.BLANK, segmentsEntryId);
	}

	@Override
	public int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, long[] segmentsEntryIds) {

		return assetListEntryAssetEntryRelPersistence.countByA_AEERC_S(
			assetListEntryId, StringPool.BLANK, segmentsEntryIds);
	}

	@Override
	public int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId) {

		return assetListEntryAssetEntryRelPersistence.countByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryId);
	}

	@Override
	public AssetListEntryAssetEntryRel moveAssetListEntryAssetEntryRel(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws PortalException {

		return moveAssetListEntryAssetEntryRel(
			assetListEntryId, StringPool.BLANK, segmentsEntryId, position,
			newPosition);
	}

	@Override
	public AssetListEntryAssetEntryRel moveAssetListEntryAssetEntryRel(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int position, int newPosition)
		throws PortalException {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.findByA_AEERC_S_P(
				assetListEntryId, audienceEntryERC, segmentsEntryId, position);

		int count =
			assetListEntryAssetEntryRelPersistence.countByAssetListEntryId(
				assetListEntryId);

		if ((newPosition < 0) || (newPosition >= count)) {
			return assetListEntryAssetEntryRel;
		}

		AssetListEntryAssetEntryRel swapAssetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.fetchByA_AEERC_S_P(
				assetListEntryId, audienceEntryERC, segmentsEntryId,
				newPosition);

		if (swapAssetListEntryAssetEntryRel == null) {
			assetListEntryAssetEntryRel.setPosition(newPosition);

			return assetListEntryAssetEntryRelPersistence.update(
				assetListEntryAssetEntryRel);
		}

		assetListEntryAssetEntryRel.setPosition(-1);

		assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.update(
				assetListEntryAssetEntryRel);

		assetListEntryAssetEntryRelPersistence.flush();

		swapAssetListEntryAssetEntryRel.setPosition(position);

		assetListEntryAssetEntryRelPersistence.update(
			swapAssetListEntryAssetEntryRel);

		assetListEntryAssetEntryRelPersistence.flush();

		assetListEntryAssetEntryRel.setPosition(newPosition);

		return assetListEntryAssetEntryRelPersistence.update(
			assetListEntryAssetEntryRel);
	}

	@Override
	public AssetListEntryAssetEntryRel updateAssetListEntryAssetEntryRel(
			long assetListEntryAssetEntryRelId, long assetListEntryId,
			long assetEntryId, long segmentsEntryId, int position)
		throws PortalException {

		return updateAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelId, assetListEntryId, assetEntryId,
			StringPool.BLANK, segmentsEntryId, position);
	}

	@Override
	public AssetListEntryAssetEntryRel updateAssetListEntryAssetEntryRel(
			long assetListEntryAssetEntryRelId, long assetListEntryId,
			long assetEntryId, String audienceEntryERC, long segmentsEntryId,
			int position)
		throws PortalException {

		_validate(audienceEntryERC, segmentsEntryId);

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRelPersistence.findByPrimaryKey(
				assetListEntryAssetEntryRelId);

		assetListEntryAssetEntryRel.setAssetEntryId(assetEntryId);
		assetListEntryAssetEntryRel.setAssetListEntryId(assetListEntryId);
		assetListEntryAssetEntryRel.setAudienceEntryERC(audienceEntryERC);
		assetListEntryAssetEntryRel.setSegmentsEntryId(segmentsEntryId);
		assetListEntryAssetEntryRel.setPosition(position);

		return assetListEntryAssetEntryRelPersistence.update(
			assetListEntryAssetEntryRel);
	}

	private void _validate(String audienceEntryERC, long segmentsEntryId)
		throws PortalException {

		if (Validator.isNotNull(audienceEntryERC) && (segmentsEntryId > 0)) {
			throw new AssetListEntryAssetEntryRelAudienceEntryERCAndSegmentsEntryIdException();
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}