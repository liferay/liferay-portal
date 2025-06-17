/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRelTable;
import com.liferay.asset.list.model.AssetListEntryTable;
import com.liferay.asset.list.service.base.AssetListEntrySegmentsEntryRelLocalServiceBaseImpl;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelPersistence;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel",
	service = AopService.class
)
public class AssetListEntrySegmentsEntryRelLocalServiceImpl
	extends AssetListEntrySegmentsEntryRelLocalServiceBaseImpl {

	@Override
	public AssetListEntrySegmentsEntryRel addAssetListEntrySegmentsEntryRel(
			long userId, long groupId, long assetListEntryId, int priority,
			long segmentsEntryId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long assetListEntrySegmentsEntryRelId = counterLocalService.increment();

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			assetListEntrySegmentsEntryRelPersistence.create(
				assetListEntrySegmentsEntryRelId);

		assetListEntrySegmentsEntryRel.setUuid(serviceContext.getUuid());
		assetListEntrySegmentsEntryRel.setGroupId(groupId);
		assetListEntrySegmentsEntryRel.setCompanyId(user.getCompanyId());
		assetListEntrySegmentsEntryRel.setUserId(userId);
		assetListEntrySegmentsEntryRel.setUserName(user.getFullName());
		assetListEntrySegmentsEntryRel.setCreateDate(new Date());
		assetListEntrySegmentsEntryRel.setModifiedDate(new Date());
		assetListEntrySegmentsEntryRel.setAssetListEntryId(assetListEntryId);
		assetListEntrySegmentsEntryRel.setPriority(priority);
		assetListEntrySegmentsEntryRel.setSegmentsEntryId(segmentsEntryId);
		assetListEntrySegmentsEntryRel.setTypeSettings(typeSettings);

		return assetListEntrySegmentsEntryRelPersistence.update(
			assetListEntrySegmentsEntryRel);
	}

	@Override
	public AssetListEntrySegmentsEntryRel addAssetListEntrySegmentsEntryRel(
			long userId, long groupId, long assetListEntryId,
			long segmentsEntryId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		return assetListEntrySegmentsEntryRelLocalService.
			addAssetListEntrySegmentsEntryRel(
				userId, groupId, assetListEntryId,
				assetListEntrySegmentsEntryRelPersistence.
					countByAssetListEntryId(assetListEntryId),
				segmentsEntryId, typeSettings, serviceContext);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetListEntrySegmentsEntryRel deleteAssetListEntrySegmentsEntryRel(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		assetListEntrySegmentsEntryRelPersistence.remove(
			assetListEntrySegmentsEntryRel);

		_assetListEntryAssetEntryRelPersistence.removeByA_S(
			assetListEntrySegmentsEntryRel.getAssetListEntryId(),
			assetListEntrySegmentsEntryRel.getSegmentsEntryId());

		return assetListEntrySegmentsEntryRel;
	}

	@Override
	public void deleteAssetListEntrySegmentsEntryRel(
			long assetListEntryId, long segmentsEntryId)
		throws PortalException {

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			assetListEntrySegmentsEntryRelPersistence.findByA_S(
				assetListEntryId, segmentsEntryId);

		assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRel(
				assetListEntrySegmentsEntryRel);
	}

	@Override
	public void deleteAssetListEntrySegmentsEntryRelByAssetListEntryId(
		long assetListEntryId) {

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			assetListEntrySegmentsEntryRelPersistence.findByAssetListEntryId(
				assetListEntryId);

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				assetListEntrySegmentsEntryRels) {

			assetListEntrySegmentsEntryRelLocalService.
				deleteAssetListEntrySegmentsEntryRel(
					assetListEntrySegmentsEntryRel);
		}
	}

	@Override
	public void deleteAssetListEntrySegmentsEntryRelBySegmentsEntryId(
		long segmentsEntryId) {

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			assetListEntrySegmentsEntryRelPersistence.findBySegmentsEntryId(
				segmentsEntryId);

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				assetListEntrySegmentsEntryRels) {

			assetListEntrySegmentsEntryRelLocalService.
				deleteAssetListEntrySegmentsEntryRel(
					assetListEntrySegmentsEntryRel);
		}
	}

	@Override
	public AssetListEntrySegmentsEntryRel fetchAssetListEntrySegmentsEntryRel(
		long assetListEntryId, long segmentsEntryId) {

		return assetListEntrySegmentsEntryRelPersistence.fetchByA_S(
			assetListEntryId, segmentsEntryId);
	}

	@Override
	public List<AssetListEntrySegmentsEntryRel>
		fetchAssetListEntrySegmentsEntryRels(
			long assetListEntryId, long[] segmentsEntryIds) {

		return assetListEntrySegmentsEntryRelPersistence.findByA_S_C(
			assetListEntryId, segmentsEntryIds);
	}

	@Override
	public List<AssetListEntrySegmentsEntryRel>
		fetchDynamicAssetListEntrySegmentsEntryRels(long companyId) {

		return assetListEntrySegmentsEntryRelPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				AssetListEntrySegmentsEntryRelTable.INSTANCE
			).from(
				AssetListEntrySegmentsEntryRelTable.INSTANCE
			).innerJoinON(
				AssetListEntryTable.INSTANCE,
				AssetListEntryTable.INSTANCE.assetListEntryId.eq(
					AssetListEntrySegmentsEntryRelTable.INSTANCE.
						assetListEntryId)
			).where(
				AssetListEntryTable.INSTANCE.type.eq(
					AssetListEntryTypeConstants.TYPE_DYNAMIC
				).and(
					AssetListEntrySegmentsEntryRelTable.INSTANCE.companyId.eq(
						companyId)
				)
			));
	}

	@Override
	public AssetListEntrySegmentsEntryRel getAssetListEntrySegmentsEntryRel(
			long assetListEntryId, long segmentsEntryId)
		throws PortalException {

		return assetListEntrySegmentsEntryRelPersistence.findByA_S(
			assetListEntryId, segmentsEntryId);
	}

	@Override
	public List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(
			long assetListEntryId, int start, int end) {

		return assetListEntrySegmentsEntryRelPersistence.findByAssetListEntryId(
			assetListEntryId, start, end);
	}

	@Override
	public List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(
			long assetListEntryId, long[] segmentsEntryIds, int start, int end,
			OrderByComparator<AssetListEntrySegmentsEntryRel>
				orderByComparator) {

		return assetListEntrySegmentsEntryRelPersistence.findByA_S_C(
			assetListEntryId, segmentsEntryIds, start, end, orderByComparator);
	}

	@Override
	public int getAssetListEntrySegmentsEntryRelsCount(long assetListEntryId) {
		return assetListEntrySegmentsEntryRelPersistence.
			countByAssetListEntryId(assetListEntryId);
	}

	@Override
	public AssetListEntrySegmentsEntryRel
		updateAssetListEntrySegmentsEntryRelTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings) {

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			assetListEntrySegmentsEntryRelPersistence.fetchByA_S(
				assetListEntryId, segmentsEntryId);

		assetListEntrySegmentsEntryRel.setModifiedDate(new Date());
		assetListEntrySegmentsEntryRel.setTypeSettings(typeSettings);

		return assetListEntrySegmentsEntryRelPersistence.update(
			assetListEntrySegmentsEntryRel);
	}

	@Override
	public void updateVariationsPriority(long[] variationsPriority) {
		for (int priority = 0; priority < variationsPriority.length;
			 priority++) {

			AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
				null;

			try {
				assetListEntrySegmentsEntryRel =
					getAssetListEntrySegmentsEntryRel(
						variationsPriority[priority]);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}

			assetListEntrySegmentsEntryRel.setPriority(priority);

			updateAssetListEntrySegmentsEntryRel(
				assetListEntrySegmentsEntryRel);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntrySegmentsEntryRelLocalServiceImpl.class);

	@Reference
	private AssetListEntryAssetEntryRelPersistence
		_assetListEntryAssetEntryRelPersistence;

	@Reference
	private UserLocalService _userLocalService;

}