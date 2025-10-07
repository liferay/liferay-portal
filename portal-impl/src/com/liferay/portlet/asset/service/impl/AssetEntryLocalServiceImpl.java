/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntries_AssetTagsTable;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.search.AssetSearcherFactoryUtil;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.kernel.validator.AssetEntryValidator;
import com.liferay.asset.kernel.validator.AssetEntryValidatorExclusionRule;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.mass.delete.MassDeleteCacheThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.RenderLayoutContentThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManagerUtil;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.service.base.AssetEntryLocalServiceBaseImpl;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;
import com.liferay.portlet.asset.util.DeletedAssetEntryThreadLocal;
import com.liferay.portlet.asset.util.DeletedAssetObjectThreadLocal;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.service.SocialActivityCounterLocalService;

import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides the local service for accessing, deleting, updating, and validating
 * asset entries.
 *
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Zsolt Berentey
 */
public class AssetEntryLocalServiceImpl extends AssetEntryLocalServiceBaseImpl {

	@Override
	public void deleteEntries(long companyId, String className)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		ActionableDynamicQuery actionableDynamicQuery =
			new DefaultActionableDynamicQuery() {

				@Override
				protected void actionsCompleted() throws PortalException {
					Session session = assetEntryPersistence.openSession();

					session.flush();

					session.clear();
				}

				@Override
				protected void intervalCompleted(
						long startPrimaryKey, long endPrimaryKey)
					throws PortalException {

					Session session = assetEntryPersistence.openSession();

					session.flush();

					session.clear();
				}

			};

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));

				Property classNameIdProperty = PropertyFactoryUtil.forName(
					"classNameId");

				dynamicQuery.add(classNameIdProperty.eq(classNameId));
			});
		actionableDynamicQuery.setBaseLocalService(this);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(AssetEntry.class);
		actionableDynamicQuery.setPerformActionMethod(
			(AssetEntry assetEntry) -> {

				// Must do aop service call to go through service wrappers

				try (SafeCloseable safeCloseable =
						DeletedAssetObjectThreadLocal.
							setAssetObjectWithSafeCloseable(
								assetEntry.getClassNameId(),
								assetEntry.getClassPK())) {

					assetEntryLocalService.deleteEntry(assetEntry);
				}
			});
		actionableDynamicQuery.setPrimaryKeyPropertyName("entryId");

		try (SafeCloseable safeCloseable1 =
				_removeFunction.setWithSafeCloseable(Function.identity())) {

			actionableDynamicQuery.performActions();
		}

		Session session = assetEntryPersistence.openSession();

		try {
			String sql =
				"delete from " + AssetEntryModelImpl.TABLE_NAME +
					" where companyId = ? and classNameId = ?";

			session.apply(
				connection -> {
					try (PreparedStatement preparedStatement =
							connection.prepareStatement(sql)) {

						preparedStatement.setLong(1, companyId);
						preparedStatement.setLong(2, classNameId);

						int results = preparedStatement.executeUpdate();

						if (results > 0) {
							assetEntryPersistence.clearCache();
						}
					}
				});
		}
		finally {
			assetEntryPersistence.closeSession(session);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetEntry deleteEntry(AssetEntry entry) throws PortalException {
		return _deleteEntry(entry, _removeFunction.get());
	}

	@Override
	public AssetEntry deleteEntry(long entryId) throws PortalException {
		AssetEntry entry = assetEntryPersistence.findByPrimaryKey(entryId);

		return deleteEntry(entry);
	}

	@Override
	public AssetEntry deleteEntry(String className, long classPK)
		throws PortalException {

		AssetEntry entry = assetEntryPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(className), classPK);

		if (entry != null) {
			return deleteEntry(entry);
		}

		return null;
	}

	@Override
	public void deleteGroupEntries(long groupId) throws PortalException {
		List<AssetEntry> assetEntries = getGroupEntries(groupId);

		for (AssetEntry assetEntry : assetEntries) {
			deleteEntry(assetEntry);
		}
	}

	@Override
	public void destroy() {
		super.destroy();

		_assetEntryValidatorExclusionRuleServiceTrackerMap.close();
		_assetEntryValidatorServiceTrackerMap.close();
	}

	@Override
	public AssetEntry fetchEntry(long entryId) {
		return assetEntryPersistence.fetchByPrimaryKey(entryId);
	}

	@Override
	public AssetEntry fetchEntry(long classNameId, long classPK) {
		return assetEntryPersistence.fetchByC_C(classNameId, classPK);
	}

	@Override
	public AssetEntry fetchEntry(long groupId, String classUuid) {
		return assetEntryPersistence.fetchByG_CU(groupId, classUuid);
	}

	@Override
	public AssetEntry fetchEntry(String className, long classPK) {
		return assetEntryLocalService.fetchEntry(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<AssetEntry> getCompanyEntries(
		long companyId, int start, int end) {

		return assetEntryPersistence.findByCompanyId(companyId, start, end);
	}

	@Override
	public int getCompanyEntriesCount(long companyId) {
		return assetEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public List<AssetEntry> getEntries(AssetEntryQuery entryQuery) {
		return assetEntryFinder.findEntries(entryQuery);
	}

	@Override
	public List<AssetEntry> getEntries(
		long[] groupIds, long[] classNameIds, long[] classTypeIds,
		String keywords, String userName, String title, String description,
		Boolean listable, boolean advancedSearch, boolean andOperator,
		int start, int end, String orderByCol1, String orderByCol2,
		String orderByType1, String orderByType2) {

		return getEntries(
			getAssetEntryQuery(
				groupIds, classNameIds, classTypeIds, keywords, userName, title,
				description, listable, advancedSearch, andOperator, start, end,
				orderByCol1, orderByCol2, orderByType1, orderByType2));
	}

	@Override
	public List<AssetEntry> getEntries(
		long[] groupIds, long[] classNameIds, String keywords, String userName,
		String title, String description, Boolean listable,
		boolean advancedSearch, boolean andOperator, int start, int end,
		String orderByCol1, String orderByCol2, String orderByType1,
		String orderByType2) {

		return getEntries(
			getAssetEntryQuery(
				groupIds, classNameIds, keywords, userName, title, description,
				listable, advancedSearch, andOperator, start, end, orderByCol1,
				orderByCol2, orderByType1, orderByType2));
	}

	@Override
	public int getEntriesCount(AssetEntryQuery entryQuery) {
		return assetEntryFinder.countEntries(entryQuery);
	}

	@Override
	public int getEntriesCount(
		long[] groupIds, long[] classNameIds, long[] classTypeIds,
		String keywords, String userName, String title, String description,
		Boolean listable, boolean advancedSearch, boolean andOperator) {

		return getEntriesCount(
			getAssetEntryQuery(
				groupIds, classNameIds, classTypeIds, keywords, userName, title,
				description, listable, advancedSearch, andOperator,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null, null, null, null));
	}

	@Override
	public int getEntriesCount(
		long[] groupIds, long[] classNameIds, String keywords, String userName,
		String title, String description, Boolean listable,
		boolean advancedSearch, boolean andOperator) {

		return getEntriesCount(
			groupIds, classNameIds, new long[0], keywords, userName, title,
			description, listable, advancedSearch, andOperator);
	}

	@Override
	public AssetEntry getEntry(long entryId) throws PortalException {
		return assetEntryPersistence.findByPrimaryKey(entryId);
	}

	@Override
	public AssetEntry getEntry(long groupId, String classUuid)
		throws PortalException {

		return assetEntryPersistence.findByG_CU(groupId, classUuid);
	}

	@Override
	public AssetEntry getEntry(String className, long classPK)
		throws PortalException {

		return assetEntryPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public double getEntryPriority(long classNameId, long classPK) {
		AssetEntry assetEntry = assetEntryPersistence.fetchByC_C(
			classNameId, classPK);

		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getPriority();
	}

	@Override
	public double getEntryPriority(String className, long classPK) {
		return getEntryPriority(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<AssetEntry> getGroupEntries(long groupId) {
		return assetEntryPersistence.findByGroupId(groupId);
	}

	@Override
	public List<AssetEntry> getTopViewedEntries(
		String className, boolean asc, int start, int end) {

		return getTopViewedEntries(new String[] {className}, asc, start, end);
	}

	@Override
	public List<AssetEntry> getTopViewedEntries(
		String[] className, boolean asc, int start, int end) {

		long[] classNameIds = new long[className.length];

		for (int i = 0; i < className.length; i++) {
			classNameIds[i] = _classNameLocalService.getClassNameId(
				className[i]);
		}

		AssetEntryQuery entryQuery = new AssetEntryQuery();

		entryQuery.setClassNameIds(classNameIds);
		entryQuery.setEnd(end);
		entryQuery.setExcludeZeroViewCount(true);
		entryQuery.setOrderByCol1("viewCount");
		entryQuery.setOrderByType1(asc ? "ASC" : "DESC");
		entryQuery.setStart(start);

		return assetEntryFinder.findEntries(entryQuery);
	}

	@Override
	public void incrementViewCounter(long userId, AssetEntry assetEntry)
		throws PortalException {

		if (!PropsValues.ASSET_ENTRY_INCREMENT_VIEW_COUNTER_ENABLED ||
			RenderLayoutContentThreadLocal.isRenderLayoutContent()) {

			return;
		}

		User user = _userLocalService.getUser(userId);

		assetEntryLocalService.incrementViewCounter(
			assetEntry.getCompanyId(), user.getUserId(),
			assetEntry.getClassName(), assetEntry.getClassPK(), 1);

		if (!user.isGuestUser()) {
			SocialActivityManagerUtil.addActivity(
				user.getUserId(), assetEntry, SocialActivityConstants.TYPE_VIEW,
				StringPool.BLANK, 0);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetEntry incrementViewCounter(
			long companyId, long userId, String className, long classPK)
		throws PortalException {

		if (!PropsValues.ASSET_ENTRY_INCREMENT_VIEW_COUNTER_ENABLED) {
			return getEntry(className, classPK);
		}

		User user = _userLocalService.getUser(userId);

		assetEntryLocalService.incrementViewCounter(
			companyId, user.getUserId(), className, classPK, 1);

		AssetEntry assetEntry = getEntry(className, classPK);

		if (!user.isGuestUser()) {
			SocialActivityManagerUtil.addActivity(
				user.getUserId(), assetEntry, SocialActivityConstants.TYPE_VIEW,
				StringPool.BLANK, 0);
		}

		return assetEntry;
	}

	@Override
	@Transactional(enabled = false)
	public void incrementViewCounter(
		long companyId, long userId, String className, long classPK,
		int increment) {

		if (!PropsValues.ASSET_ENTRY_INCREMENT_VIEW_COUNTER_ENABLED ||
			ExportImportThreadLocal.isImportInProcess() || (classPK <= 0)) {

			return;
		}

		AssetEntry entry = assetEntryPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(className), classPK);

		if (entry == null) {
			return;
		}

		ViewCountManagerUtil.incrementViewCount(
			companyId, _classNameLocalService.getClassNameId(AssetEntry.class),
			entry.getEntryId(), increment);
	}

	@Override
	public void reindex(List<AssetEntry> entries) throws PortalException {
		for (AssetEntry entry : entries) {
			reindex(entry);
		}
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end, Sort sort) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, keywords, null, null,
				showNonindexable, statuses, false, start, end, sort);

			return doSearch(classNameIds, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable, int status,
		int start, int end) {

		return search(
			companyId, groupIds, userId, className, classTypeId, keywords,
			keywords, keywords, null, null, showNonindexable,
			new int[] {status}, false, start, end);
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, keywords, null, null,
				showNonindexable, statuses, false, start, end);

			return doSearch(companyId, className, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end, Sort sort) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, keywords, null, null,
				showNonindexable, statuses, false, start, end, sort);

			return doSearch(companyId, className, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, int status, int start, int end) {

		return search(
			companyId, groupIds, userId, className, classTypeId, keywords,
			false, status, start, end);
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int status, boolean andSearch, int start, int end) {

		return search(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames,
			showNonindexable, new int[] {status}, andSearch, start, end);
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch, int start, int end) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, userName, title,
				description, assetCategoryIds, assetTagNames, showNonindexable,
				statuses, andSearch, start, end);

			return doSearch(companyId, className, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, int status,
		boolean andSearch, int start, int end) {

		return search(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames, false,
			new int[] {status}, andSearch, start, end);
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		String keywords, int status, int start, int end) {

		return search(
			companyId, groupIds, userId, className, 0, keywords, status, start,
			end);
	}

	@Override
	public Hits search(
		long companyId, long[] groupIds, long userId, String className,
		String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, int status,
		boolean andSearch, int start, int end) {

		return search(
			companyId, groupIds, userId, className, 0, userName, title,
			description, assetCategoryIds, assetTagNames, status, andSearch,
			start, end);
	}

	@Override
	public long searchCount(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, keywords, null, null,
				showNonindexable, statuses, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			return doSearchCount(classNameIds, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, keywords, null, null,
				showNonindexable, statuses, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			return doSearchCount(companyId, className, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, String assetCategoryIds,
		String assetTagNames, boolean showInvisible, boolean showNonindexable,
		int[] statuses, boolean andSearch) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, keywords,
				assetCategoryIds, assetTagNames, showNonindexable, statuses,
				andSearch, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			return doSearchCount(companyId, className, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showInvisible,
		boolean showNonindexable, int[] statuses, boolean andSearch) {

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, groupIds, userId, classTypeId, userName, title,
				description, assetCategoryIds, assetTagNames, showNonindexable,
				statuses, andSearch, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			return doSearchCount(companyId, className, searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch) {

		return searchCount(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames,
			showNonindexable, false, statuses, andSearch);
	}

	@Override
	public AssetEntry updateEntry(
			long userId, long groupId, Date createDate, Date modifiedDate,
			String className, long classPK, String classUuid, long classTypeId,
			long[] categoryIds, String[] tagNames, boolean listable,
			boolean visible, Date startDate, Date endDate, Date publishDate,
			Date expirationDate, String mimeType, String title,
			String description, String summary, String url, String layoutUuid,
			int height, int width, Double priority)
		throws PortalException {

		return updateEntry(
			userId, groupId, createDate, modifiedDate, className, classPK,
			classUuid, classTypeId, categoryIds, tagNames, listable, visible,
			startDate, endDate, publishDate, expirationDate, mimeType, title,
			description, summary, url, layoutUuid, height, width, priority,
			null);
	}

	@Override
	public AssetEntry updateEntry(
			long userId, long groupId, Date createDate, Date modifiedDate,
			String className, long classPK, String classUuid, long classTypeId,
			long[] categoryIds, String[] tagNames, boolean listable,
			boolean visible, Date startDate, Date endDate, Date publishDate,
			Date expirationDate, String mimeType, String title,
			String description, String summary, String url, String layoutUuid,
			int height, int width, Double priority,
			ServiceContext serviceContext)
		throws PortalException {

		// Entry

		long classNameId = _classNameLocalService.getClassNameId(className);

		validate(
			groupId, className, classPK, classTypeId, categoryIds, tagNames);

		AssetEntry entry = null;

		boolean strictAdd = false;

		if (serviceContext != null) {
			strictAdd = serviceContext.isStrictAdd();
		}

		if (!strictAdd) {
			entry = assetEntryPersistence.fetchByC_C(classNameId, classPK);
		}

		long entryId = 0;

		boolean oldVisible = false;

		if (entry == null) {
			entryId = counterLocalService.increment();
		}
		else {
			entryId = entry.getEntryId();

			oldVisible = entry.isVisible();
		}

		// Tags

		if ((tagNames != null) && ((entry != null) || (tagNames.length > 0))) {
			Group siteGroup = _getAssetTagSiteGroup(groupId, serviceContext);

			List<AssetTag> tags = _assetTagLocalService.checkTags(
				userId, siteGroup, tagNames);

			if (visible) {
				if (entry == null) {
					for (AssetTag tag : tags) {
						_assetTagLocalService.incrementAssetCount(
							tag.getTagId(), classNameId);
					}
				}
				else {
					List<AssetTag> oldTags = assetEntryPersistence.getAssetTags(
						entryId);

					for (AssetTag oldTag : oldTags) {
						if (!tags.contains(oldTag)) {
							_assetTagLocalService.decrementAssetCount(
								oldTag.getTagId(), classNameId);
						}
					}

					for (AssetTag tag : tags) {
						if (!oldTags.contains(tag)) {
							_assetTagLocalService.incrementAssetCount(
								tag.getTagId(), classNameId);
						}
					}
				}
			}
			else if (oldVisible) {
				List<AssetTag> oldTags = assetEntryPersistence.getAssetTags(
					entryId);

				for (AssetTag oldTag : oldTags) {
					_assetTagLocalService.decrementAssetCount(
						oldTag.getTagId(), classNameId);
				}
			}

			assetEntryPersistence.setAssetTags(entryId, tags);
		}

		// Update entry after tags so that entry listeners have access to the
		// saved categories and tags

		if (entry == null) {
			entry = assetEntryPersistence.create(entryId);

			Group group = _groupPersistence.findByPrimaryKey(groupId);

			entry.setCompanyId(group.getCompanyId());

			entry.setUserId(userId);

			User user = _userLocalService.fetchUser(userId);

			if (user != null) {
				entry.setUserName(user.getFullName());
			}
			else {
				entry.setUserName(StringPool.BLANK);
			}

			if (createDate == null) {
				createDate = new Date();
			}

			entry.setCreateDate(createDate);

			entry.setClassNameId(classNameId);
			entry.setClassPK(classPK);
			entry.setClassUuid(classUuid);

			if (priority == null) {
				entry.setPriority(0);
			}
		}
		else {
			entry = assetEntryPersistence.findByPrimaryKey(entryId);
		}

		entry.setGroupId(groupId);

		if (modifiedDate == null) {
			modifiedDate = new Date();
		}

		entry.setModifiedDate(modifiedDate);
		entry.setClassTypeId(classTypeId);
		entry.setListable(listable);
		entry.setVisible(visible);
		entry.setStartDate(startDate);
		entry.setEndDate(endDate);

		if (publishDate != null) {
			entry.setPublishDate(publishDate);
		}

		entry.setExpirationDate(expirationDate);
		entry.setMimeType(mimeType);
		entry.setTitle(title);
		entry.setDescription(description);
		entry.setSummary(summary);
		entry.setUrl(url);
		entry.setLayoutUuid(layoutUuid);
		entry.setHeight(height);
		entry.setWidth(width);

		if (priority != null) {
			entry.setPriority(priority.doubleValue());
		}

		entry = assetEntryPersistence.update(entry);

		// Indexer

		if ((serviceContext == null) || serviceContext.isIndexingEnabled()) {
			reindex(entry);
		}

		return entry;
	}

	@Override
	public AssetEntry updateEntry(
			long userId, long groupId, String className, long classPK,
			long[] categoryIds, String[] tagNames)
		throws PortalException {

		AssetEntry entry = assetEntryPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(className), classPK);

		if (entry != null) {
			return assetEntryLocalService.updateEntry(
				userId, groupId, entry.getCreateDate(), entry.getModifiedDate(),
				className, classPK, entry.getClassUuid(),
				entry.getClassTypeId(), categoryIds, tagNames,
				entry.isListable(), entry.isVisible(), entry.getStartDate(),
				entry.getEndDate(), entry.getPublishDate(),
				entry.getExpirationDate(), entry.getMimeType(),
				entry.getTitle(), entry.getDescription(), entry.getSummary(),
				entry.getUrl(), entry.getLayoutUuid(), entry.getHeight(),
				entry.getWidth(), entry.getPriority());
		}

		return assetEntryLocalService.updateEntry(
			userId, groupId, null, null, className, classPK, null, 0,
			categoryIds, tagNames, true, true, null, null, null, null, null,
			null, null, null, null, null, 0, 0, (Double)null);
	}

	@Override
	public AssetEntry updateEntry(
			String className, long classPK, Date publishDate,
			Date expirationDate, boolean listable, boolean visible)
		throws PortalException {

		AssetEntry entry = assetEntryPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK);

		entry.setListable(listable);
		entry.setPublishDate(publishDate);
		entry.setExpirationDate(expirationDate);

		return updateVisible(entry, visible);
	}

	@Override
	public AssetEntry updateVisible(AssetEntry entry, boolean visible)
		throws PortalException {

		if (visible == entry.isVisible()) {
			return assetEntryPersistence.update(entry);
		}

		entry.setVisible(visible);

		entry = assetEntryPersistence.update(entry);

		List<AssetTag> tags = assetEntryPersistence.getAssetTags(
			entry.getEntryId());

		if (visible) {
			for (AssetTag tag : tags) {
				_assetTagLocalService.incrementAssetCount(
					tag.getTagId(), entry.getClassNameId());
			}

			_socialActivityCounterLocalService.enableActivityCounters(
				entry.getClassNameId(), entry.getClassPK());
		}
		else {
			for (AssetTag tag : tags) {
				_assetTagLocalService.decrementAssetCount(
					tag.getTagId(), entry.getClassNameId());
			}

			_socialActivityCounterLocalService.disableActivityCounters(
				entry.getClassNameId(), entry.getClassPK());
		}

		return entry;
	}

	@Override
	public AssetEntry updateVisible(
			String className, long classPK, boolean visible)
		throws PortalException {

		AssetEntry entry = assetEntryPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK);

		return updateVisible(entry, visible);
	}

	@Override
	public void validate(
			long groupId, String className, long classPK, long classTypePK,
			long[] categoryIds, String[] tagNames)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		List<AssetEntryValidatorExclusionRule>
			assetEntryValidatorExclusionRules =
				_assetEntryValidatorExclusionRuleServiceTrackerMap.getService(
					className);

		if (assetEntryValidatorExclusionRules != null) {
			for (AssetEntryValidatorExclusionRule
					assetEntryValidatorExclusionRule :
						assetEntryValidatorExclusionRules) {

				if (assetEntryValidatorExclusionRule.isValidationExcluded(
						groupId, className, classPK, classTypePK, categoryIds,
						tagNames)) {

					return;
				}
			}
		}

		for (AssetEntryValidator assetEntryValidator :
				_getAssetEntryValidators(className)) {

			assetEntryValidator.validate(
				groupId, className, classPK, classTypePK, categoryIds,
				tagNames);
		}
	}

	@Override
	public void validate(
			long groupId, String className, long classTypePK,
			long[] categoryIds, String[] tagNames)
		throws PortalException {

		validate(groupId, className, 0L, classTypePK, categoryIds, tagNames);
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, long userId, long classTypeId,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch, int start, int end) {

		return buildSearchContext(
			companyId, groupIds, userId, classTypeId, assetCategoryIds,
			assetTagNames, showNonindexable, statuses, andSearch, start, end,
			null);
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, long userId, long classTypeId,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(andSearch);
		searchContext.setAssetCategoryIds(
			StringUtil.split(assetCategoryIds, 0L));
		searchContext.setAssetTagNames(StringUtil.split(assetTagNames));
		searchContext.setAttribute("paginationType", "regular");

		if (showNonindexable) {
			searchContext.setAttribute("showNonindexable", Boolean.TRUE);
		}

		searchContext.setAttribute("status", statuses);

		if (classTypeId >= 0) {
			searchContext.setClassTypeIds(new long[] {classTypeId});
		}

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);
		searchContext.setSorts(sort);
		searchContext.setStart(start);
		searchContext.setUserId(userId);

		return searchContext;
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, long userId, long classTypeId,
		String keywords, String assetCategoryIds, String assetTagNames,
		boolean showNonindexable, int[] statuses, boolean andSearch, int start,
		int end) {

		SearchContext searchContext = buildSearchContext(
			companyId, groupIds, userId, classTypeId, assetCategoryIds,
			assetTagNames, showNonindexable, statuses, andSearch, start, end);

		searchContext.setKeywords(keywords);

		return searchContext;
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, long userId, long classTypeId,
		String keywords, String assetCategoryIds, String assetTagNames,
		boolean showNonindexable, int[] statuses, boolean andSearch, int start,
		int end, Sort sort) {

		SearchContext searchContext = buildSearchContext(
			companyId, groupIds, userId, classTypeId, assetCategoryIds,
			assetTagNames, showNonindexable, statuses, andSearch, start, end,
			sort);

		searchContext.setKeywords(keywords);

		return searchContext;
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, long userId, long classTypeId,
		String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch, int start, int end) {

		SearchContext searchContext = buildSearchContext(
			companyId, groupIds, userId, classTypeId, assetCategoryIds,
			assetTagNames, showNonindexable, statuses, andSearch, start, end);

		searchContext.setAttribute(Field.DESCRIPTION, description);
		searchContext.setAttribute(Field.TITLE, title);
		searchContext.setAttribute(Field.USER_NAME, userName);

		return searchContext;
	}

	protected long[] checkCategories(
			String className, long classPK, long[] categoryIds)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return categoryIds;
		}

		List<AssetCategory> oldCategories =
			_assetCategoryLocalService.getCategories(className, classPK);

		for (AssetCategory category : oldCategories) {
			if (!ArrayUtil.contains(categoryIds, category.getCategoryId()) &&
				!AssetCategoryPermission.contains(
					permissionChecker, category, ActionKeys.VIEW)) {

				categoryIds = ArrayUtil.append(
					categoryIds, category.getCategoryId());
			}
		}

		return categoryIds;
	}

	protected Hits doSearch(
			long companyId, String className, SearchContext searchContext)
		throws Exception {

		return doSearch(getClassNameIds(companyId, className), searchContext);
	}

	protected Hits doSearch(long[] classNameIds, SearchContext searchContext)
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setClassNameIds(classNameIds);

		_setAssetCategoryIds(
			searchContext.getAssetCategoryIds(), searchContext.isAndSearch(),
			assetEntryQuery);
		_setAssetTagNames(
			searchContext.getGroupIds(), searchContext.getAssetTagNames(),
			searchContext.isAndSearch(), assetEntryQuery);

		BaseSearcher baseSearcher = AssetSearcherFactoryUtil.createBaseSearcher(
			assetEntryQuery);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(_hasScoreSort(searchContext));

		return baseSearcher.search(searchContext);
	}

	protected long doSearchCount(
			long companyId, String className, SearchContext searchContext)
		throws Exception {

		return doSearchCount(
			getClassNameIds(companyId, className), searchContext);
	}

	protected long doSearchCount(
			long[] classNameIds, SearchContext searchContext)
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setClassNameIds(classNameIds);

		_setAssetCategoryIds(
			searchContext.getAssetCategoryIds(), searchContext.isAndSearch(),
			assetEntryQuery);
		_setAssetTagNames(
			searchContext.getGroupIds(), searchContext.getAssetTagNames(),
			searchContext.isAndSearch(), assetEntryQuery);

		BaseSearcher baseSearcher = AssetSearcherFactoryUtil.createBaseSearcher(
			assetEntryQuery);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return baseSearcher.searchCount(searchContext);
	}

	protected AssetEntryQuery getAssetEntryQuery(
		long[] groupIds, long[] classNameIds, long[] classTypeIds,
		String keywords, String userName, String title, String description,
		Boolean listable, boolean advancedSearch, boolean andOperator,
		int start, int end, String orderByCol1, String orderByCol2,
		String orderByType1, String orderByType2) {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setClassNameIds(classNameIds);

		if (ArrayUtil.isNotEmpty(classTypeIds)) {
			assetEntryQuery.setClassTypeIds(classTypeIds);
		}

		assetEntryQuery.setEnd(end);
		assetEntryQuery.setGroupIds(groupIds);
		assetEntryQuery.setListable(listable);
		assetEntryQuery.setOrderByCol1(orderByCol1);
		assetEntryQuery.setOrderByCol2(orderByCol2);
		assetEntryQuery.setOrderByType1(orderByType1);
		assetEntryQuery.setOrderByType2(orderByType2);
		assetEntryQuery.setStart(start);

		if (advancedSearch) {
			assetEntryQuery.setAndOperator(andOperator);
			assetEntryQuery.setDescription(description);
			assetEntryQuery.setTitle(title);
			assetEntryQuery.setUserName(userName);
		}
		else {
			assetEntryQuery.setKeywords(keywords);
		}

		return assetEntryQuery;
	}

	protected AssetEntryQuery getAssetEntryQuery(
		long[] groupIds, long[] classNameIds, String keywords, String userName,
		String title, String description, Boolean listable,
		boolean advancedSearch, boolean andOperator, int start, int end,
		String orderByCol1, String orderByCol2, String orderByType1,
		String orderByType2) {

		return getAssetEntryQuery(
			groupIds, classNameIds, new long[0], keywords, userName, title,
			description, listable, advancedSearch, andOperator, start, end,
			orderByCol1, orderByCol2, orderByType1, orderByType2);
	}

	protected long[] getClassNameIds(long companyId, String className) {
		if (Validator.isNotNull(className)) {
			return new long[] {
				_classNameLocalService.getClassNameId(className)
			};
		}

		return AssetRendererFactoryRegistryUtil.getClassNameIds(
			companyId, true);
	}

	protected long[] getTagIds(long[] groupIds, String tagName) {
		if (groupIds != null) {
			return _assetTagLocalService.getTagIds(groupIds, tagName);
		}

		return _assetTagLocalService.getTagIds(tagName);
	}

	protected void reindex(AssetEntry entry) throws PortalException {
		Indexer<Object> indexer = IndexerRegistryUtil.getIndexer(
			entry.getClassName());

		if (indexer == null) {
			return;
		}

		AssetRenderer<?> assetRenderer = entry.getAssetRenderer();

		if (assetRenderer == null) {
			indexer.reindex(entry.getClassName(), entry.getClassPK());

			return;
		}

		indexer.reindex(assetRenderer.getAssetObject());
	}

	private AssetEntry _deleteEntry(
			AssetEntry entry, Function<AssetEntry, AssetEntry> removefunction)
		throws PortalException {

		// Tags

		Map<Long, List<Object[]>> partitionAssetEntryAssetTagIds =
			MassDeleteCacheThreadLocal.getMassDeleteCache(
				AssetEntryLocalServiceImpl.class.getName() + ".deleteEntry",
				() -> MapUtil.toPartitionMap(
					dslQuery(
						DSLQueryFactoryUtil.select(
							AssetEntries_AssetTagsTable.INSTANCE.entryId,
							AssetEntries_AssetTagsTable.INSTANCE.tagId
						).from(
							AssetEntries_AssetTagsTable.INSTANCE
						).where(
							AssetEntries_AssetTagsTable.INSTANCE.companyId.eq(
								CompanyThreadLocal.getCompanyId())
						)),
					ids -> (Long)ids[0]));

		if (partitionAssetEntryAssetTagIds == null) {
			List<AssetTag> tags = assetEntryPersistence.getAssetTags(
				entry.getEntryId());

			for (AssetTag tag : tags) {
				if (entry.isVisible()) {
					_assetTagLocalService.decrementAssetCount(
						tag.getTagId(), entry.getClassNameId());
				}
			}

			// Entry

			entry = assetEntryPersistence.remove(entry);
		}
		else {
			List<Object[]> assertEntryAssetTagIds =
				partitionAssetEntryAssetTagIds.remove(entry.getEntryId());

			if (assertEntryAssetTagIds != null) {
				for (Object[] assetEntryAssetTag : assertEntryAssetTagIds) {
					assetTagPersistence.remove((Long)assetEntryAssetTag[1]);
				}
			}

			// Entry

			if (removefunction == null) {
				assetEntryPersistence.remove(entry);
			}
			else {
				assetEntryPersistence.removeByFunction(entry, removefunction);
			}
		}

		// View count

		ViewCountManagerUtil.deleteViewCount(
			entry.getCompanyId(),
			_classNameLocalService.getClassNameId(AssetEntry.class),
			entry.getEntryId());

		// Social

		try (SafeCloseable safeCloseable =
				DeletedAssetEntryThreadLocal.setAssetEntryWithSafeCloseable(
					entry)) {

			SocialActivityManagerUtil.deleteActivities(entry);
		}

		return entry;
	}

	private List<AssetEntryValidator> _getAssetEntryValidators(
		String className) {

		List<AssetEntryValidator> assetEntryValidators = new ArrayList<>();

		List<AssetEntryValidator> generalAssetEntryValidators =
			_assetEntryValidatorServiceTrackerMap.getService("*");

		if (ListUtil.isNotEmpty(generalAssetEntryValidators)) {
			assetEntryValidators.addAll(generalAssetEntryValidators);
		}

		if (Validator.isNotNull(className)) {
			List<AssetEntryValidator> classNameAssetEntryValidators =
				_assetEntryValidatorServiceTrackerMap.getService(className);

			if (ListUtil.isNotEmpty(classNameAssetEntryValidators)) {
				assetEntryValidators.addAll(classNameAssetEntryValidators);
			}
		}

		return assetEntryValidators;
	}

	private Group _getAssetTagSiteGroup(
			long groupId, ServiceContext serviceContext)
		throws PortalException {

		long scopeGroupId = groupId;

		if (serviceContext != null) {
			scopeGroupId = GetterUtil.getLong(
				serviceContext.getAttribute("assetTagScopeGroupId"), groupId);
		}

		return _groupPersistence.findByPrimaryKey(
			PortalUtil.getSiteGroupId(scopeGroupId));
	}

	private boolean _hasScoreSort(SearchContext searchContext) {
		for (Sort sort : searchContext.getSorts()) {
			if ((sort != null) && (sort.getType() == Sort.SCORE_TYPE)) {
				return true;
			}
		}

		return false;
	}

	private void _setAssetCategoryIds(
		long[] assetCategoryIds, boolean andSearch,
		AssetEntryQuery assetEntryQuery) {

		if (ArrayUtil.isEmpty(assetCategoryIds)) {
			return;
		}

		if (andSearch) {
			assetEntryQuery.setAnyCategoryIds(assetCategoryIds);
		}
		else {
			assetEntryQuery.setAllCategoryIds(assetCategoryIds);
		}
	}

	private void _setAssetTagNames(
		long[] groupIds, String[] assetTagNames, boolean andSearch,
		AssetEntryQuery assetEntryQuery) {

		if (andSearch) {
			for (String assetTagName : assetTagNames) {
				long[] allAssetTagIds = getTagIds(groupIds, assetTagName);

				assetEntryQuery.addAllTagIdsArray(allAssetTagIds);
			}
		}
		else {
			if (ArrayUtil.isNotEmpty(assetTagNames)) {
				long[] assetTagIds = getTagIds(
					groupIds, StringUtil.merge(assetTagNames));

				assetEntryQuery.setAnyTagIds(assetTagIds);
			}
		}
	}

	private static final CentralizedThreadLocal
		<Function<AssetEntry, AssetEntry>> _removeFunction =
			new CentralizedThreadLocal<>(
				AssetEntryLocalServiceImpl.class.getName() +
					"._removeFunction");

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	private final ServiceTrackerMap
		<String, List<AssetEntryValidatorExclusionRule>>
			_assetEntryValidatorExclusionRuleServiceTrackerMap =
				ServiceTrackerMapFactory.openMultiValueMap(
					SystemBundleUtil.getBundleContext(),
					AssetEntryValidatorExclusionRule.class, "model.class.name");
	private final ServiceTrackerMap<String, List<AssetEntryValidator>>
		_assetEntryValidatorServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				SystemBundleUtil.getBundleContext(), AssetEntryValidator.class,
				"model.class.name");

	@BeanReference(type = AssetTagLocalService.class)
	private AssetTagLocalService _assetTagLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = SocialActivityCounterLocalService.class)
	private SocialActivityCounterLocalService
		_socialActivityCounterLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}