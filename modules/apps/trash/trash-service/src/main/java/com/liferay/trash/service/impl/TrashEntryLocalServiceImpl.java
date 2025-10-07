/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.model.impl.TrashEntryImpl;
import com.liferay.trash.service.base.TrashEntryLocalServiceBaseImpl;
import com.liferay.trash.service.persistence.TrashVersionPersistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, checking, and deleting
 * trash entries in the Recycle Bin.
 *
 * @author Zsolt Berentey
 */
@Component(
	property = "model.class.name=com.liferay.trash.model.TrashEntry",
	service = AopService.class
)
public class TrashEntryLocalServiceImpl extends TrashEntryLocalServiceBaseImpl {

	/**
	 * Moves an entry to trash.
	 *
	 * @param  userId the primary key of the user removing the entity
	 * @param  groupId the primary key of the entry's group
	 * @param  className the class name of the entity
	 * @param  classPK the primary key of the entity
	 * @param  classUuid the UUID of the entity's class
	 * @param  referrerClassName the referrer class name used to add a deletion
	 *         {@link SystemEvent}
	 * @param  status the status of the entity prior to being moved to trash
	 * @param  statusOVPs the primary keys and statuses of any of the entry's
	 *         versions (e.g., {@link
	 *         com.liferay.portlet.documentlibrary.model.DLFileVersion})
	 * @param  typeSettingsUnicodeProperties the type settings properties
	 * @return the trashEntry
	 */
	@Override
	public TrashEntry addTrashEntry(
			long userId, long groupId, String className, long classPK,
			String classUuid, String referrerClassName, int status,
			List<ObjectValuePair<Long, Integer>> statusOVPs,
			UnicodeProperties typeSettingsUnicodeProperties)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		TrashEntry trashEntry = trashEntryPersistence.fetchByCN_CPK(
			classNameId, classPK);

		if (trashEntry != null) {
			return trashEntry;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		SystemEvent systemEvent = trashHandler.addDeletionSystemEvent(
			userId, groupId, classPK, classUuid, referrerClassName);

		long entryId = counterLocalService.increment();

		trashEntry = trashEntryPersistence.create(entryId);

		trashEntry.setGroupId(groupId);

		User user = _userLocalService.getUserById(userId);

		trashEntry.setCompanyId(user.getCompanyId());
		trashEntry.setUserId(user.getUserId());
		trashEntry.setUserName(user.getFullName());

		trashEntry.setCreateDate(new Date());
		trashEntry.setClassNameId(classNameId);
		trashEntry.setClassPK(classPK);

		if (systemEvent != null) {
			trashEntry.setSystemEventSetKey(systemEvent.getSystemEventSetKey());
		}

		if (typeSettingsUnicodeProperties != null) {
			trashEntry.setTypeSettingsProperties(typeSettingsUnicodeProperties);
		}

		trashEntry.setStatus(status);

		trashEntry = trashEntryPersistence.update(trashEntry);

		if (statusOVPs != null) {
			for (ObjectValuePair<Long, Integer> statusOVP : statusOVPs) {
				long versionId = counterLocalService.increment();

				TrashVersion trashVersion = _trashVersionPersistence.create(
					versionId);

				trashVersion.setEntryId(entryId);
				trashVersion.setClassNameId(classNameId);
				trashVersion.setClassPK(statusOVP.getKey());
				trashVersion.setStatus(statusOVP.getValue());

				_trashVersionPersistence.update(trashVersion);
			}
		}

		return trashEntry;
	}

	@Override
	public void checkEntries() throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			trashEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			(TrashEntry trashEntry) -> {
				Group group = _groupLocalService.fetchGroup(
					trashEntry.getGroupId());

				if (group == null) {
					return;
				}

				Date createDate = trashEntry.getCreateDate();

				Date date = _getMaxAge(group);

				if (createDate.before(date) || !_isTrashEnabled(group)) {
					TrashHandler trashHandler =
						TrashHandlerRegistryUtil.getTrashHandler(
							trashEntry.getClassName());

					if (trashHandler != null) {
						try {
							trashHandler.deleteTrashEntry(
								trashEntry.getClassPK());
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}
						}
					}
				}
			});
		actionableDynamicQuery.setTransactionConfig(
			DefaultActionableDynamicQuery.REQUIRES_NEW_TRANSACTION_CONFIG);

		actionableDynamicQuery.performActions();
	}

	@Override
	public void deleteEntries(long groupId) {
		deleteEntries(groupId, false);
	}

	@Override
	public void deleteEntries(long groupId, boolean deleteTrashedModels) {
		List<TrashEntry> entries = getEntries(groupId);

		for (TrashEntry entry : entries) {
			deleteEntry(entry);

			if (deleteTrashedModels) {
				TrashHandler trashHandler =
					TrashHandlerRegistryUtil.getTrashHandler(
						entry.getClassName());

				if (trashHandler != null) {
					try {
						trashHandler.deleteTrashEntry(entry.getClassPK());
					}
					catch (Exception exception) {
						_log.error(exception);
					}
				}
			}
		}
	}

	/**
	 * Deletes the trash entry with the primary key.
	 *
	 * @param  entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	@Override
	public TrashEntry deleteEntry(long entryId) {
		TrashEntry entry = trashEntryPersistence.fetchByPrimaryKey(entryId);

		return deleteEntry(entry);
	}

	/**
	 * Deletes the trash entry with the entity class name and primary key.
	 *
	 * @param  className the class name of entity
	 * @param  classPK the primary key of the entry
	 * @return the trash entry with the entity class name and primary key
	 */
	@Override
	public TrashEntry deleteEntry(String className, long classPK) {
		TrashEntry entry = trashEntryPersistence.fetchByCN_CPK(
			_classNameLocalService.getClassNameId(className), classPK);

		return deleteEntry(entry);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public TrashEntry deleteEntry(TrashEntry trashEntry) {
		if (trashEntry != null) {
			_trashVersionPersistence.removeByEntryId(trashEntry.getEntryId());

			trashEntry = trashEntryPersistence.remove(trashEntry);

			_systemEventLocalService.deleteSystemEvents(
				trashEntry.getGroupId(), trashEntry.getSystemEventSetKey());
		}

		return trashEntry;
	}

	@Override
	public void deleteTrashEntries(long companyId, String className) {
		List<TrashEntry> trashEntries = trashEntryPersistence.findByC_CN(
			companyId, _classNameLocalService.getClassNameId(className));

		for (TrashEntry trashEntry : trashEntries) {
			deleteEntry(trashEntry);
		}
	}

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param  entryId the primary key of the entry
	 * @return the trash entry with the primary key
	 */
	@Override
	public TrashEntry fetchEntry(long entryId) {
		return trashEntryPersistence.fetchByPrimaryKey(entryId);
	}

	/**
	 * Returns the trash entry with the entity class name and primary key.
	 *
	 * @param  className the class name of the entity
	 * @param  classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	@Override
	public TrashEntry fetchEntry(String className, long classPK) {
		return trashEntryPersistence.fetchByCN_CPK(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	/**
	 * Returns the trash entries with the matching group ID.
	 *
	 * @param  groupId the primary key of the group
	 * @return the trash entries with the group ID
	 */
	@Override
	public List<TrashEntry> getEntries(long groupId) {
		return trashEntryPersistence.findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of trash entries to return
	 * @param  end the upper bound of the range of trash entries to return (not
	 *         inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public List<TrashEntry> getEntries(long groupId, int start, int end) {
		return trashEntryPersistence.findByGroupId(groupId, start, end);
	}

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of trash entries to return
	 * @param  end the upper bound of the range of trash entries to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the trash entries
	 *         (optionally <code>null</code>)
	 * @return the range of matching trash entries ordered by comparator
	 *         <code>orderByComparator</code>
	 */
	@Override
	public List<TrashEntry> getEntries(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return trashEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<TrashEntry> getEntries(long groupId, String className) {
		return trashEntryPersistence.findByG_CN(
			groupId, _classNameLocalService.getClassNameId(className));
	}

	/**
	 * Returns the number of trash entries with the group ID.
	 *
	 * @param  groupId the primary key of the group
	 * @return the number of matching trash entries
	 */
	@Override
	public int getEntriesCount(long groupId) {
		return trashEntryPersistence.countByGroupId(groupId);
	}

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param  entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	@Override
	public TrashEntry getEntry(long entryId) throws PortalException {
		return trashEntryPersistence.findByPrimaryKey(entryId);
	}

	/**
	 * Returns the entry with the entity class name and primary key.
	 *
	 * @param  className the class name of the entity
	 * @param  classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	@Override
	public TrashEntry getEntry(String className, long classPK)
		throws PortalException {

		return trashEntryPersistence.findByCN_CPK(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public Hits search(
		long companyId, long groupId, long userId, String keywords, int start,
		int end, Sort sort) {

		try {
			Indexer<TrashEntry> indexer = _indexerRegistry.nullSafeGetIndexer(
				TrashEntry.class);

			SearchContext searchContext = _buildSearchContext(
				companyId, groupId, userId, keywords, start, end, sort);

			return indexer.search(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public BaseModelSearchResult<TrashEntry> searchTrashEntries(
		long companyId, long groupId, long userId, String keywords, int start,
		int end, Sort sort) {

		try {
			Indexer<TrashEntry> indexer = _indexerRegistry.nullSafeGetIndexer(
				TrashEntry.class);

			SearchContext searchContext = _buildSearchContext(
				companyId, groupId, userId, keywords, start, end, sort);

			Hits hits = indexer.search(searchContext);

			List<TrashEntry> trashEntries = _getEntries(hits);

			return new BaseModelSearchResult<>(trashEntries, hits.getLength());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private SearchContext _buildSearchContext(
		long companyId, long groupId, long userId, String keywords, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setKeywords(keywords);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);
		searchContext.setUserId(userId);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<TrashEntry> _getEntries(Hits hits) {
		List<TrashEntry> entries = new ArrayList<>();

		for (Document document : hits.getDocs()) {
			String entryClassName = GetterUtil.getString(
				document.get(Field.ENTRY_CLASS_NAME));
			long classPK = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			TrashEntry entry = fetchEntry(entryClassName, classPK);

			if (entry != null) {
				entries.add(entry);

				continue;
			}

			try {
				String userName = GetterUtil.getString(
					document.get(Field.REMOVED_BY_USER_NAME));

				Date removedDate = document.getDate(Field.REMOVED_DATE);

				entry = new TrashEntryImpl();

				entry.setUserName(userName);
				entry.setCreateDate(removedDate);

				TrashHandler trashHandler =
					TrashHandlerRegistryUtil.getTrashHandler(entryClassName);

				TrashRenderer trashRenderer = trashHandler.getTrashRenderer(
					classPK);

				entry.setClassName(trashRenderer.getClassName());
				entry.setClassPK(trashRenderer.getClassPK());

				String rootEntryClassName = GetterUtil.getString(
					document.get(Field.ROOT_ENTRY_CLASS_NAME));
				long rootEntryClassPK = GetterUtil.getLong(
					document.get(Field.ROOT_ENTRY_CLASS_PK));

				TrashEntry rootTrashEntry = fetchEntry(
					rootEntryClassName, rootEntryClassPK);

				if (rootTrashEntry != null) {
					entry.setRootEntry(rootTrashEntry);
				}

				entries.add(entry);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to find trash entry for ", entryClassName,
							" with primary key ", classPK),
						exception);
				}
			}
		}

		return entries;
	}

	private Date _getMaxAge(Group group) throws PortalException {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new Date());

		int trashEntriesMaxAge = PrefsPropsUtil.getInteger(
			group.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE,
			PropsValues.TRASH_ENTRIES_MAX_AGE);

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getParentLiveGroupTypeSettingsProperties();

		calendar.add(
			Calendar.MINUTE,
			-GetterUtil.getInteger(
				typeSettingsUnicodeProperties.getProperty("trashEntriesMaxAge"),
				trashEntriesMaxAge));

		return calendar.getTime();
	}

	private boolean _isTrashEnabled(Group group) {
		boolean companyTrashEnabled = PrefsPropsUtil.getBoolean(
			group.getCompanyId(), PropsKeys.TRASH_ENABLED);

		if (!companyTrashEnabled) {
			return false;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getParentLiveGroupTypeSettingsProperties();

		return GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("trashEnabled"), true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TrashEntryLocalServiceImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	@Reference
	private TrashVersionPersistence _trashVersionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}