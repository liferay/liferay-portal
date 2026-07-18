/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.exception.AssetTagNameException;
import com.liferay.asset.kernel.exception.DuplicateTagException;
import com.liferay.asset.kernel.exception.NoSuchTagException;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.NumberIncrement;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.service.base.AssetTagLocalServiceBaseImpl;
import com.liferay.portlet.asset.util.comparator.AssetTagNameComparator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the local service for accessing, adding, checking, deleting,
 * merging, and updating asset tags.
 *
 * @author Brian Wing Shun Chan
 * @author Alvaro del Castillo
 * @author Jorge Ferrer
 * @author Bruno Farache
 */
public class AssetTagLocalServiceImpl extends AssetTagLocalServiceBaseImpl {

	/**
	 * Adds an asset tag.
	 *
	 * @param externalReferenceCode
	 * @param userId                the primary key of the user adding the asset tag
	 * @param groupId               the primary key of the group in which the asset tag is to
	 *                              be added
	 * @param name                  the asset tag's name
	 * @param serviceContext        the service context to be applied
	 * @return the asset tag that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetTag addTag(
			String externalReferenceCode, long userId, long groupId,
			String name, ServiceContext serviceContext)
		throws PortalException {

		// Tag

		User user = _userPersistence.findByPrimaryKey(userId);

		long tagId = counterLocalService.increment();

		AssetTag tag = assetTagPersistence.create(tagId);

		tag.setUuid(serviceContext.getUuid());
		tag.setExternalReferenceCode(externalReferenceCode);
		tag.setGroupId(groupId);
		tag.setCompanyId(user.getCompanyId());
		tag.setUserId(user.getUserId());
		tag.setUserName(user.getFullName());

		name = StringUtil.trim(name);

		validate(name);

		if (hasTag(groupId, name)) {
			throw new DuplicateTagException(
				"A tag with the name " + name + " already exists");
		}

		tag.setName(name);

		return assetTagPersistence.update(tag);
	}

	/**
	 * Returns the asset tags matching the group and names, creating new asset
	 * tags matching the names if the group doesn't already have them.
	 *
	 * <p>
	 * For each name, if an asset tag with the name doesn't already exist in the
	 * group, this method creates a new asset tag with the name in the group.
	 * </p>
	 *
	 * @param  userId the primary key of the user checking the asset tags
	 * @param  group the group in which to check the asset tags
	 * @param  names the asset tag names
	 * @return the asset tags matching the group and names and new asset tags
	 *         matching the names that don't already exist in the group
	 */
	@Override
	public List<AssetTag> checkTags(long userId, Group group, String[] names)
		throws PortalException {

		List<AssetTag> tags = new ArrayList<>();

		for (String name : names) {
			name = StringUtil.trim(name);

			AssetTag tag = fetchTag(group.getGroupId(), name);

			if (tag == null) {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setAddGroupPermissions(true);
				serviceContext.setAddGuestPermissions(true);
				serviceContext.setScopeGroupId(group.getGroupId());

				tag = addTag(
					null, userId, group.getGroupId(), name, serviceContext);
			}

			if (tag != null) {
				tags.add(tag);
			}
		}

		return tags;
	}

	/**
	 * Returns the asset tags matching the group and names, creating new asset
	 * tags matching the names if the group doesn't already have them.
	 *
	 * @param  userId the primary key of the user checking the asset tags
	 * @param  groupId the primary key of the group in which check the asset
	 *         tags
	 * @param  names the asset tag names
	 * @return the asset tags matching the group and names and new asset tags
	 *         matching the names that don't already exist in the group
	 */
	@Override
	public List<AssetTag> checkTags(long userId, long groupId, String[] names)
		throws PortalException {

		return checkTags(userId, _groupLocalService.getGroup(groupId), names);
	}

	/**
	 * Decrements the number of assets to which the asset tag has been applied.
	 *
	 * @param  tagId the primary key of the asset tag
	 * @param  classNameId the class name ID of the entity to which the asset
	 *         tag had been applied
	 * @return the asset tag
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetTag decrementAssetCount(long tagId, long classNameId)
		throws PortalException {

		AssetTag tag = assetTagPersistence.findByPrimaryKey(tagId);

		tag.setAssetCount(Math.max(0, tag.getAssetCount() - 1));

		return assetTagPersistence.update(tag);
	}

	/**
	 * Deletes all asset tags in the group.
	 *
	 * @param groupId the primary key of the group in which to delete all asset
	 *        tags
	 */
	@Override
	public void deleteGroupTags(long groupId) throws PortalException {
		List<AssetTag> tags = getGroupTags(groupId);

		for (AssetTag tag : tags) {
			assetTagLocalService.deleteTag(tag);
		}
	}

	/**
	 * Deletes the asset tag.
	 *
	 * @param tag the asset tag to be deleted
	 */
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteTag(AssetTag tag) throws PortalException {

		// Entries

		List<AssetEntry> entries = assetTagPersistence.getAssetEntries(
			tag.getTagId());

		// Tag

		assetTagPersistence.remove(tag);

		// Indexer

		_assetEntryLocalService.reindex(entries);

		Indexer<AssetTag> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			AssetTag.class);

		indexer.delete(tag);
	}

	/**
	 * Deletes the asset tag.
	 *
	 * @param tagId the primary key of the asset tag
	 */
	@Override
	public void deleteTag(long tagId) throws PortalException {
		AssetTag tag = assetTagPersistence.findByPrimaryKey(tagId);

		assetTagLocalService.deleteTag(tag);
	}

	/**
	 * Returns the asset tag with the name in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the asset tag's name
	 * @return the asset tag with the name in the group or <code>null</code> if
	 *         it could not be found
	 */
	@Override
	public AssetTag fetchTag(long groupId, String name) {
		List<AssetTag> assetTags = assetTagPersistence.findByG_N(groupId, name);

		for (AssetTag assetTag : assetTags) {
			if (StringUtil.equals(assetTag.getName(), name)) {
				return assetTag;
			}
		}

		return null;
	}

	/**
	 * Returns the asset tags of the asset entry.
	 *
	 * @param  entryId the primary key of the asset entry
	 * @return the asset tags of the asset entry
	 */
	@Override
	public List<AssetTag> getEntryTags(long entryId) {
		return assetEntryPersistence.getAssetTags(entryId);
	}

	/**
	 * Returns the asset tags in the groups.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @return the asset tags in the groups
	 */
	@Override
	public List<AssetTag> getGroupsTags(long[] groupIds) {
		List<AssetTag> groupsTags = new ArrayList<>();

		for (long groupId : groupIds) {
			groupsTags.addAll(getGroupTags(groupId));
		}

		return groupsTags;
	}

	/**
	 * Returns the asset tags in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the asset tags in the group
	 */
	@Override
	public List<AssetTag> getGroupTags(long groupId) {
		return assetTagPersistence.findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the asset tags in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of asset tags
	 * @param  end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of matching asset tags
	 */
	@Override
	public List<AssetTag> getGroupTags(long groupId, int start, int end) {
		return assetTagPersistence.findByGroupId(groupId, start, end);
	}

	/**
	 * Returns a range of all the asset tags in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of asset tags
	 * @param  end the upper bound of the range of asset tags (not inclusive)
	 * @param  orderByComparator the comparator to order the asset tags
	 *         (optionally <code>null</code>)
	 * @return the range of matching asset tags
	 */
	@Override
	public List<AssetTag> getGroupTags(
		long groupId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return assetTagPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset tags in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the number of asset tags in the group
	 */
	@Override
	public int getGroupTagsCount(long groupId) {
		return assetTagPersistence.countByGroupId(groupId);
	}

	/**
	 * Returns the asset tag with the primary key.
	 *
	 * @param  tagId the primary key of the asset tag
	 * @return the asset tag with the primary key
	 */
	@Override
	public AssetTag getTag(long tagId) throws PortalException {
		return assetTagPersistence.findByPrimaryKey(tagId);
	}

	/**
	 * Returns the asset tag with the name in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the name of the asset tag
	 * @return the asset tag with the name in the group
	 */
	@Override
	public AssetTag getTag(long groupId, String name) throws PortalException {
		AssetTag assetTag = fetchTag(groupId, name);

		if (assetTag != null) {
			return assetTag;
		}

		throw new NoSuchTagException(
			StringBundler.concat(
				"No asset tag found for group ID ", groupId, " and name \"",
				name, "\""));
	}

	/**
	 * Returns the primary keys of the asset tags with the names in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  names the names of the asset tags
	 * @return the primary keys of the asset tags with the names in the group
	 */
	@Override
	public long[] getTagIds(long groupId, String[] names) {
		List<Long> tagIds = new ArrayList<>(names.length);

		for (String name : names) {
			AssetTag tag = fetchTag(groupId, name);

			if (tag == null) {
				continue;
			}

			tagIds.add(tag.getTagId());
		}

		return ArrayUtil.toArray(tagIds.toArray(new Long[0]));
	}

	/**
	 * Returns the primary keys of the asset tags with the name in the groups.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @param  name the name of the asset tags
	 * @return the primary keys of the asset tags with the name in the groups
	 */
	@Override
	public long[] getTagIds(long[] groupIds, String name) {
		List<Long> tagIds = new ArrayList<>(groupIds.length);

		for (long groupId : groupIds) {
			AssetTag tag = fetchTag(groupId, name);

			if (tag == null) {
				continue;
			}

			tagIds.add(tag.getTagId());
		}

		return ArrayUtil.toArray(tagIds.toArray(new Long[0]));
	}

	/**
	 * Returns the primary keys of the asset tags with the names in the groups.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @param  names the names of the asset tags
	 * @return the primary keys of the asset tags with the names in the groups
	 */
	@Override
	public long[] getTagIds(long[] groupIds, String[] names) {
		long[] tagsIds = new long[0];

		for (long groupId : groupIds) {
			tagsIds = ArrayUtil.append(tagsIds, getTagIds(groupId, names));
		}

		return tagsIds;
	}

	/**
	 * Returns the primary keys of the asset tags with the names.
	 *
	 * @param  name the name of the asset tags
	 * @return the primary keys of the asset tags with the names
	 */
	@Override
	public long[] getTagIds(String name) {
		return TransformUtil.transformToLongArray(
			assetTagPersistence.findByName(name),
			assetTag -> {
				if (StringUtil.equals(assetTag.getName(), name)) {
					return assetTag.getTagId();
				}

				return null;
			});
	}

	/**
	 * Returns the names of all the asset tags.
	 *
	 * @return the names of all the asset tags
	 */
	@Override
	public String[] getTagNames() {
		return getTagNames(getTags());
	}

	/**
	 * Returns the names of the asset tags of the entity.
	 *
	 * @param  classNameId the class name ID of the entity
	 * @param  classPK the primary key of the entity
	 * @return the names of the asset tags of the entity
	 */
	@Override
	public String[] getTagNames(long classNameId, long classPK) {
		return getTagNames(getTags(classNameId, classPK));
	}

	/**
	 * Returns the names of the asset tags of the entity
	 *
	 * @param  className the class name of the entity
	 * @param  classPK the primary key of the entity
	 * @return the names of the asset tags of the entity
	 */
	@Override
	public String[] getTagNames(String className, long classPK) {
		return getTagNames(getTags(className, classPK));
	}

	/**
	 * Returns all the asset tags.
	 *
	 * @return the asset tags
	 */
	@Override
	public List<AssetTag> getTags() {
		return assetTagPersistence.findAll();
	}

	/**
	 * Returns the asset tags of the entity.
	 *
	 * @param  classNameId the class name ID of the entity
	 * @param  classPK the primary key of the entity
	 * @return the asset tags of the entity
	 */
	@Override
	public List<AssetTag> getTags(long classNameId, long classPK) {
		AssetEntry entry = assetEntryPersistence.fetchByC_C(
			classNameId, classPK);

		if (entry == null) {
			return Collections.emptyList();
		}

		return assetEntryPersistence.getAssetTags(entry.getEntryId());
	}

	@Override
	public List<AssetTag> getTags(long groupId, long classNameId, String name) {
		return assetTagFinder.findByG_C_N(
			groupId, classNameId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	@Override
	public List<AssetTag> getTags(
		long groupId, long classNameId, String name, int start, int end) {

		return assetTagFinder.findByG_C_N(
			groupId, classNameId, name, start, end, null);
	}

	/**
	 * Returns the asset tags of the entity.
	 *
	 * @param  className the class name of the entity
	 * @param  classPK the primary key of the entity
	 * @return the asset tags of the entity
	 */
	@Override
	@ThreadLocalCachable
	public List<AssetTag> getTags(String className, long classPK) {
		return getTags(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public int getTagsSize(long groupId, long classNameId, String name) {
		return assetTagFinder.countByG_C_N(groupId, classNameId, name);
	}

	/**
	 * Returns <code>true</code> if the group contains an asset tag with the
	 * name.
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the name of the asset tag
	 * @return <code>true</code> if the group contains an asset tag with the
	 *         name; <code>false</code> otherwise.
	 */
	@Override
	public boolean hasTag(long groupId, String name) {
		AssetTag tag = fetchTag(groupId, name);

		if (tag != null) {
			return true;
		}

		return false;
	}

	/**
	 * Increments the number of assets to which the asset tag has been applied.
	 *
	 * @param  tagId the primary key of the asset tag
	 * @param  classNameId the class name ID of the entity to which the asset
	 *         tag is being applied
	 * @return the asset tag
	 */
	@BufferedIncrement(incrementClass = NumberIncrement.class)
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetTag incrementAssetCount(long tagId, long classNameId)
		throws PortalException {

		AssetTag tag = assetTagPersistence.findByPrimaryKey(tagId);

		tag.setAssetCount(tag.getAssetCount() + 1);

		return assetTagPersistence.update(tag);
	}

	/**
	 * Replaces all occurrences of the first asset tag with the second asset tag
	 * and deletes the first asset tag.
	 *
	 * @param fromTagId the primary key of the asset tag to be replaced
	 * @param toTagId the primary key of the asset tag to apply to the asset
	 *        entries of the other asset tag
	 */
	@Override
	public void mergeTags(long fromTagId, long toTagId) throws PortalException {
		List<AssetEntry> entries = assetTagPersistence.getAssetEntries(
			fromTagId);

		assetTagPersistence.addAssetEntries(toTagId, entries);

		deleteTag(fromTagId);

		for (AssetEntry entry : entries) {
			incrementAssetCount(toTagId, entry.getClassNameId());
		}
	}

	/**
	 * Returns the asset tags in the group whose names match the pattern.
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the pattern to match
	 * @param  start the lower bound of the range of asset tags
	 * @param  end the upper bound of the range of asset tags (not inclusive)
	 * @return the asset tags in the group whose names match the pattern
	 */
	@Override
	public List<AssetTag> search(
		long groupId, String name, int start, int end) {

		return search(new long[] {groupId}, name, start, end);
	}

	/**
	 * Returns the asset tags in the groups whose names match the pattern.
	 *
	 * @param  groupIds the primary keys of the groups
	 * @param  name the pattern to match
	 * @param  start the lower bound of the range of asset tags
	 * @param  end the upper bound of the range of asset tags (not inclusive)
	 * @return the asset tags in the groups whose names match the pattern
	 */
	@Override
	public List<AssetTag> search(
		long[] groupIds, String name, int start, int end) {

		return assetTagPersistence.findByG_LikeN(
			groupIds, name, start, end, new AssetTagNameComparator());
	}

	@Override
	public BaseModelSearchResult<AssetTag> searchTags(
			long[] groupIds, String name, int start, int end, Sort sort)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		SearchContext searchContext = buildSearchContext(
			serviceContext.getCompanyId(), groupIds, name, start, end, sort);

		return searchTags(searchContext);
	}

	@Override
	public void subscribeTag(long userId, long groupId, long tagId)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This method is implemented in com.liferay.asset.tags.internal." +
				"service.AssetTagLocalServiceWrapper");
	}

	@Override
	public void unsubscribeTag(long userId, long tagId) throws PortalException {
		throw new UnsupportedOperationException(
			"This method is implemented in com.liferay.asset.tags.internal." +
				"service.AssetTagLocalServiceWrapper");
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetTag updateTag(
			String externalReferenceCode, long userId, long tagId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		// Tag

		AssetTag tag = assetTagPersistence.findByPrimaryKey(tagId);

		String oldName = tag.getName();

		name = StringUtil.trim(name);

		if (!name.equals(oldName) && hasTag(tag.getGroupId(), name)) {
			throw new DuplicateTagException(
				"A tag with the name " + name + " already exists");
		}

		String tagName = tag.getName();

		if (!tagName.equals(name)) {
			AssetTag existingAssetTag = fetchTag(tag.getGroupId(), name);

			if ((existingAssetTag != null) &&
				(existingAssetTag.getTagId() != tagId)) {

				throw new DuplicateTagException(
					"A tag with the name " + name + " already exists");
			}
		}

		validate(name);

		tag.setExternalReferenceCode(externalReferenceCode);
		tag.setName(name);

		tag = assetTagPersistence.update(tag);

		// Indexer

		if (!oldName.equals(name)) {
			List<AssetEntry> entries = assetTagPersistence.getAssetEntries(
				tag.getTagId());

			_assetEntryLocalService.reindex(entries);
		}

		return tag;
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, String name, int start, int end,
		Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.NAME, name
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);
		searchContext.setKeywords(name);
		searchContext.setStart(start);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	protected String[] getTagNames(List<AssetTag> tags) {
		return ListUtil.toArray(tags, AssetTag.NAME_ACCESSOR);
	}

	protected List<AssetTag> getTags(Hits hits) throws PortalException {
		List<Document> documents = hits.toList();

		List<AssetTag> tags = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long tagId = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

			AssetTag tag = fetchAssetTag(tagId);

			if (tag == null) {
				tags = null;

				Indexer<AssetTag> indexer = IndexerRegistryUtil.getIndexer(
					AssetTag.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (tags != null) {
				tags.add(tag);
			}
		}

		return tags;
	}

	protected BaseModelSearchResult<AssetTag> searchTags(
			SearchContext searchContext)
		throws PortalException {

		Indexer<AssetTag> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			AssetTag.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<AssetTag> tags = getTags(hits);

			if (tags != null) {
				return new BaseModelSearchResult<>(tags, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	protected void validate(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new AssetTagNameException(
				"Tag name cannot be an empty string");
		}

		if (!_isValidWord(name)) {
			throw new AssetTagException(
				"Tag name has one or more invalid characters: " +
					StringUtil.merge(_INVALID_CHARACTERS, StringPool.SPACE),
				AssetTagException.INVALID_CHARACTER);
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			AssetTag.class.getName(), "name");

		if (name.length() > maxLength) {
			throw new AssetTagException(
				"Tag name has more than " + maxLength + " characters",
				AssetTagException.MAX_LENGTH);
		}
	}

	private boolean _isValidWord(String word) {
		if (Validator.isBlank(word)) {
			return false;
		}

		char[] wordCharArray = word.toCharArray();

		for (char c : wordCharArray) {
			for (char invalidChar : _INVALID_CHARACTERS) {
				if (c == invalidChar) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Word ", word, " is not valid because ", c,
								" is not allowed"));
					}

					return false;
				}
			}
		}

		return true;
	}

	private static final char[] _INVALID_CHARACTERS = {
		CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.AT,
		CharPool.BACK_SLASH, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.COLON, CharPool.COMMA, CharPool.EQUAL, CharPool.GREATER_THAN,
		CharPool.FORWARD_SLASH, CharPool.LESS_THAN, CharPool.NEW_LINE,
		CharPool.OPEN_BRACKET, CharPool.OPEN_CURLY_BRACE, CharPool.PERCENT,
		CharPool.PIPE, CharPool.PLUS, CharPool.POUND, CharPool.PRIME,
		CharPool.QUESTION, CharPool.QUOTE, CharPool.RETURN, CharPool.SEMICOLON,
		CharPool.SLASH, CharPool.STAR, CharPool.TILDE
	};

	private static final Log _log = LogFactoryUtil.getLog(
		AssetTagLocalServiceImpl.class);

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}