/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service.impl;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.model.AssetDisplayPageEntryTable;
import com.liferay.asset.display.page.service.base.AssetDisplayPageEntryLocalServiceBaseImpl;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mass.delete.MassDeleteCacheThreadLocal;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "model.class.name=com.liferay.asset.display.page.model.AssetDisplayPageEntry",
	service = AopService.class
)
public class AssetDisplayPageEntryLocalServiceImpl
	extends AssetDisplayPageEntryLocalServiceBaseImpl {

	@Override
	public AssetDisplayPageEntry addAssetDisplayPageEntry(
			long userId, long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId, int type,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long assetDisplayPageEntryId = counterLocalService.increment();

		AssetDisplayPageEntry assetDisplayPageEntry =
			assetDisplayPageEntryPersistence.create(assetDisplayPageEntryId);

		assetDisplayPageEntry.setUuid(serviceContext.getUuid());
		assetDisplayPageEntry.setGroupId(groupId);
		assetDisplayPageEntry.setCompanyId(user.getCompanyId());
		assetDisplayPageEntry.setUserId(user.getUserId());
		assetDisplayPageEntry.setUserName(user.getFullName());
		assetDisplayPageEntry.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		assetDisplayPageEntry.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		assetDisplayPageEntry.setClassNameId(classNameId);
		assetDisplayPageEntry.setClassPK(classPK);
		assetDisplayPageEntry.setLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
		assetDisplayPageEntry.setType(type);
		assetDisplayPageEntry.setPlid(
			_getPlid(classNameId, classPK, layoutPageTemplateEntryId));

		assetDisplayPageEntry = assetDisplayPageEntryPersistence.update(
			assetDisplayPageEntry);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntryId);

		if (layoutPageTemplateEntry != null) {
			layoutPageTemplateEntry.setModifiedDate(new Date());

			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry);
		}

		return assetDisplayPageEntry;
	}

	@Override
	public AssetDisplayPageEntry addAssetDisplayPageEntry(
			long userId, long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId, ServiceContext serviceContext)
		throws PortalException {

		return addAssetDisplayPageEntry(
			userId, groupId, classNameId, classPK, layoutPageTemplateEntryId,
			AssetDisplayPageConstants.TYPE_DEFAULT, serviceContext);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK)
		throws PortalException {

		Map<Long, List<AssetDisplayPageEntry>>
			partitionAssetDisplayPageEntries =
				MassDeleteCacheThreadLocal.getMassDeleteCache(
					StringBundler.concat(
						AssetDisplayPageEntryLocalServiceImpl.class.getName(),
						".fetchAssetDisplayPageEntry#", groupId, classNameId),
					() -> MapUtil.toPartitionMap(
						assetDisplayPageEntryPersistence.findByG_CN(
							groupId, classNameId),
						AssetDisplayPageEntry::getClassPK));

		if (partitionAssetDisplayPageEntries == null) {
			AssetDisplayPageEntry assetDisplayPageEntry =
				assetDisplayPageEntryPersistence.fetchByG_C_C(
					groupId, classNameId, classPK);

			if (assetDisplayPageEntry != null) {
				assetDisplayPageEntryPersistence.remove(assetDisplayPageEntry);
			}
		}
		else {
			List<AssetDisplayPageEntry> assetDisplayPageEntries =
				partitionAssetDisplayPageEntries.remove(classPK);

			ListUtil.isNotEmptyForEach(
				assetDisplayPageEntries,
				assetDisplayPageEntry ->
					assetDisplayPageEntryPersistence.remove(
						assetDisplayPageEntry));
		}
	}

	@Override
	public AssetDisplayPageEntry fetchAssetDisplayPageEntry(
		long groupId, long classNameId, long classPK) {

		return assetDisplayPageEntryPersistence.fetchByG_C_C(
			groupId, classNameId, classPK);
	}

	@Override
	public List<AssetDisplayPageEntry> getAssetDisplayPageEntries(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			AssetDisplayPageEntryTable.INSTANCE
		).from(
			AssetDisplayPageEntryTable.INSTANCE
		).innerJoinON(
			AssetEntryTable.INSTANCE,
			AssetDisplayPageEntryTable.INSTANCE.classPK.eq(
				AssetEntryTable.INSTANCE.classPK)
		).where(
			_getPredicate(
				classNameId, classTypeId, layoutPageTemplateEntryId,
				defaultTemplate)
		).orderBy(
			AssetDisplayPageEntryTable.INSTANCE, orderByComparator
		).limit(
			start, end
		);

		return assetDisplayPageEntryPersistence.dslQuery(dslQuery);
	}

	@Override
	public List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId) {

		return assetDisplayPageEntryPersistence.findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
	}

	@Override
	public List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return assetDisplayPageEntryPersistence.findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId, start, end, orderByComparator);
	}

	@Override
	public int getAssetDisplayPageEntriesCount(long groupId, long classNameId) {
		return assetDisplayPageEntryPersistence.countByG_CN(
			groupId, classNameId);
	}

	@Override
	public int getAssetDisplayPageEntriesCount(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			AssetDisplayPageEntryTable.INSTANCE
		).innerJoinON(
			AssetEntryTable.INSTANCE,
			AssetDisplayPageEntryTable.INSTANCE.classPK.eq(
				AssetEntryTable.INSTANCE.classPK)
		).where(
			_getPredicate(
				classNameId, classTypeId, layoutPageTemplateEntryId,
				defaultTemplate)
		);

		return assetDisplayPageEntryPersistence.dslQueryCount(dslQuery);
	}

	@Override
	public int getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId) {

		return assetDisplayPageEntryPersistence.
			countByLayoutPageTemplateEntryId(layoutPageTemplateEntryId);
	}

	@Override
	public AssetDisplayPageEntry updateAssetDisplayPageEntry(
			long assetDisplayPageEntryId, long layoutPageTemplateEntryId,
			int type)
		throws PortalException {

		AssetDisplayPageEntry assetDisplayPageEntry =
			assetDisplayPageEntryPersistence.findByPrimaryKey(
				assetDisplayPageEntryId);

		assetDisplayPageEntry.setModifiedDate(new Date());
		assetDisplayPageEntry.setLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
		assetDisplayPageEntry.setType(type);

		long plid = _getPlid(
			assetDisplayPageEntry.getClassNameId(),
			assetDisplayPageEntry.getClassPK(), layoutPageTemplateEntryId);

		assetDisplayPageEntry.setPlid(plid);

		assetDisplayPageEntry = assetDisplayPageEntryPersistence.update(
			assetDisplayPageEntry);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntryId);

		if (layoutPageTemplateEntry != null) {
			layoutPageTemplateEntry.setModifiedDate(new Date());

			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry);
		}

		return assetDisplayPageEntry;
	}

	private long _getPlid(
		long classNameId, long classPK, long layoutPageTemplateEntryId) {

		String className = _portal.getClassName(classNameId);

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					CompanyThreadLocal.getCompanyId(), className);

		if (layoutDisplayPageProvider == null) {
			return LayoutConstants.DEFAULT_PLID;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntryId);

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry.getPlid();
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		AssetEntry assetEntry = null;

		if (assetRendererFactory != null) {
			try {
				assetEntry = assetRendererFactory.getAssetEntry(
					className, classPK);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}
		}
		else {
			assetEntry = _assetEntryLocalService.fetchEntry(
				classNameId, classPK);
		}

		if (assetEntry == null) {
			return LayoutConstants.DEFAULT_PLID;
		}

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			assetEntry.getLayoutUuid(), assetEntry.getGroupId(), false);

		if (layout != null) {
			return layout.getPlid();
		}

		layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			assetEntry.getLayoutUuid(), assetEntry.getGroupId(), true);

		if (layout != null) {
			return layout.getPlid();
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	private Predicate _getPredicate(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate) {

		return AssetDisplayPageEntryTable.INSTANCE.classNameId.eq(
			classNameId
		).and(
			() -> {
				String className = _portal.fetchClassName(classNameId);

				if (Validator.isNull(className)) {
					return AssetEntryTable.INSTANCE.classNameId.eq(classNameId);
				}

				return AssetEntryTable.INSTANCE.classNameId.eq(
					_portal.getClassNameId(
						_infoSearchClassMapperRegistry.getSearchClassName(
							className)));
			}
		).and(
			AssetDisplayPageEntryTable.INSTANCE.layoutPageTemplateEntryId.eq(
				layoutPageTemplateEntryId
			).and(
				AssetDisplayPageEntryTable.INSTANCE.type.eq(
					AssetDisplayPageConstants.TYPE_SPECIFIC)
			).withParentheses(
			).or(
				() -> {
					if (!defaultTemplate) {
						return null;
					}

					return AssetDisplayPageEntryTable.INSTANCE.type.eq(
						AssetDisplayPageConstants.TYPE_DEFAULT);
				}
			).withParentheses()
		).and(
			() -> {
				if (classTypeId > 0) {
					return AssetEntryTable.INSTANCE.classTypeId.eq(classTypeId);
				}

				return null;
			}
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetDisplayPageEntryLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}