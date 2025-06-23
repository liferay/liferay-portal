/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.exception.DuplicateVocabularyException;
import com.liferay.asset.kernel.exception.DuplicateVocabularyExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.VocabularyNameException;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.exportimport.kernel.incomplete.model.IncompleteModelManagerUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
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
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.asset.service.base.AssetVocabularyLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides the local service for accessing, adding, deleting, and updating
 * asset vocabularies.
 *
 * @author Alvaro del Castillo
 * @author Eduardo Lundgren
 * @author Jorge Ferrer
 * @author Juan Fernández
 */
public class AssetVocabularyLocalServiceImpl
	extends AssetVocabularyLocalServiceBaseImpl {

	@Override
	public AssetVocabulary addDefaultVocabulary(long groupId)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		long guestUserId = _userLocalService.getGuestUserId(
			group.getCompanyId());

		Map<Locale, String> titleMap = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales(groupId)) {
			titleMap.put(
				locale,
				LanguageUtil.get(locale, PropsValues.ASSET_VOCABULARY_DEFAULT));
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(groupId);

		return assetVocabularyLocalService.addVocabulary(
			guestUserId, groupId, StringPool.BLANK, titleMap, null,
			StringPool.BLANK, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, int visibilityType, ServiceContext serviceContext)
		throws PortalException {

		return assetVocabularyLocalService.addVocabulary(
			null, userId, groupId, titleMap.get(LocaleUtil.getSiteDefault()),
			title, titleMap, descriptionMap, settings, visibilityType,
			serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, ServiceContext serviceContext)
		throws PortalException {

		return assetVocabularyLocalService.addVocabulary(
			userId, groupId, titleMap.get(LocaleUtil.getSiteDefault()), title,
			titleMap, descriptionMap, settings, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			ServiceContext serviceContext)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		return assetVocabularyLocalService.addVocabulary(
			userId, groupId, title,
			HashMapBuilder.put(
				locale, title
			).build(),
			HashMapBuilder.put(
				locale, StringPool.BLANK
			).build(),
			null, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String name, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, ServiceContext serviceContext)
		throws PortalException {

		return addVocabulary(
			null, userId, groupId, name, title, titleMap, descriptionMap,
			settings, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetVocabulary addVocabulary(
			String externalReferenceCode, long userId, long groupId,
			String name, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType, ServiceContext serviceContext)
		throws PortalException {

		// Vocabulary

		User user = _userLocalService.getUser(userId);

		Map<Locale, String> trimmedTitleMap = _getTrimmedTitleMap(titleMap);

		if (Validator.isNull(name)) {
			name = _generateVocabularyName(
				groupId, trimmedTitleMap.get(LocaleUtil.getSiteDefault()));
		}

		name = _getVocabularyName(name);

		validate(groupId, name);

		long vocabularyId = counterLocalService.increment();

		_validateExternalReferenceCode(externalReferenceCode, groupId);

		AssetVocabulary vocabulary = assetVocabularyPersistence.create(
			vocabularyId);

		vocabulary.setUuid(serviceContext.getUuid());
		vocabulary.setExternalReferenceCode(externalReferenceCode);
		vocabulary.setGroupId(groupId);
		vocabulary.setCompanyId(user.getCompanyId());
		vocabulary.setUserId(user.getUserId());
		vocabulary.setUserName(user.getFullName());
		vocabulary.setName(name);

		if (Validator.isNotNull(title)) {
			vocabulary.setTitle(title);
		}
		else {
			vocabulary.setTitleMap(trimmedTitleMap);
		}

		vocabulary.setDescriptionMap(descriptionMap);
		vocabulary.setSettings(settings);
		vocabulary.setVisibilityType(visibilityType);

		if (IncompleteModelManagerUtil.isIncompleteModel()) {
			vocabulary.setStatus(WorkflowConstants.STATUS_INCOMPLETE);
		}
		else {
			vocabulary.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		vocabulary = assetVocabularyPersistence.update(vocabulary);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addVocabularyResources(
				vocabulary, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addVocabularyResources(
				vocabulary, serviceContext.getModelPermissions());
		}

		return vocabulary;
	}

	@Override
	public void addVocabularyResources(
			AssetVocabulary vocabulary, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			vocabulary.getCompanyId(), vocabulary.getGroupId(),
			vocabulary.getUserId(), AssetVocabulary.class.getName(),
			vocabulary.getVocabularyId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addVocabularyResources(
			AssetVocabulary vocabulary, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			vocabulary.getCompanyId(), vocabulary.getGroupId(),
			vocabulary.getUserId(), AssetVocabulary.class.getName(),
			vocabulary.getVocabularyId(), modelPermissions);
	}

	@Override
	public void deleteVocabularies(long groupId) throws PortalException {
		List<AssetVocabulary> vocabularies =
			assetVocabularyPersistence.findByGroupId(groupId);

		for (AssetVocabulary vocabulary : vocabularies) {
			assetVocabularyLocalService.deleteVocabulary(vocabulary);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public AssetVocabulary deleteVocabulary(AssetVocabulary vocabulary)
		throws PortalException {

		// Vocabulary

		assetVocabularyPersistence.remove(vocabulary);

		// Resources

		_resourceLocalService.deleteResource(
			vocabulary.getCompanyId(), AssetVocabulary.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, vocabulary.getVocabularyId());

		// Categories

		_assetCategoryLocalService.deleteVocabularyCategories(
			vocabulary.getVocabularyId());

		return vocabulary;
	}

	@Override
	public void deleteVocabulary(long vocabularyId) throws PortalException {
		AssetVocabulary vocabulary =
			assetVocabularyPersistence.findByPrimaryKey(vocabularyId);

		assetVocabularyLocalService.deleteVocabulary(vocabulary);
	}

	@Override
	public AssetVocabulary fetchGroupVocabulary(long groupId, String name) {
		return assetVocabularyPersistence.fetchByG_N(groupId, name);
	}

	@Override
	public List<AssetVocabulary> getCompanyVocabularies(long companyId) {
		return assetVocabularyPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<AssetVocabulary> getGroupsVocabularies(long[] groupIds) {
		return getGroupsVocabularies(groupIds, null);
	}

	@Override
	public List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className) {

		return getGroupsVocabularies(
			groupIds, className, AssetCategoryConstants.ALL_CLASS_TYPE_PK);
	}

	@Override
	public List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className, long classTypePK) {

		List<AssetVocabulary> vocabularies =
			assetVocabularyPersistence.findByGroupId(groupIds);

		if (Validator.isNull(className)) {
			return vocabularies;
		}

		long classNameId = _classNameLocalService.getClassNameId(className);

		return ListUtil.filter(
			vocabularies,
			assetVocabulary ->
				assetVocabulary.isAssociatedToClassNameIdAndClassTypePK(
					classNameId, classTypePK));
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(long groupId)
		throws PortalException {

		return getGroupVocabularies(groupId, false);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean addDefaultVocabulary)
		throws PortalException {

		List<AssetVocabulary> vocabularies =
			assetVocabularyPersistence.findByGroupId(groupId);

		if (!vocabularies.isEmpty() || !addDefaultVocabulary) {
			return vocabularies;
		}

		AssetVocabulary vocabulary = addDefaultVocabulary(groupId);

		return ListUtil.fromArray(vocabulary);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType) {

		return assetVocabularyPersistence.findByG_V(groupId, visibilityType);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(long[] groupIds) {
		return assetVocabularyPersistence.findByGroupId(groupIds);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes) {

		return assetVocabularyPersistence.findByG_V(groupIds, visibilityTypes);
	}

	@Override
	public int getGroupVocabulariesCount(long[] groupIds) {
		return assetVocabularyPersistence.countByGroupId(groupIds);
	}

	@Override
	public AssetVocabulary getGroupVocabulary(long groupId, String name)
		throws PortalException {

		return assetVocabularyPersistence.findByG_N(groupId, name);
	}

	@Override
	public AssetVocabulary getOrAddIncompleteVocabulary(
			String externalReferenceCode, long userId, long groupId)
		throws PortalException {

		return IncompleteModelManagerUtil.getOrAddIncompleteModel(
			AssetVocabulary.class, externalReferenceCode,
			this::fetchAssetVocabularyByExternalReferenceCode,
			this::getAssetVocabularyByExternalReferenceCode, groupId,
			() -> assetVocabularyLocalService.addVocabulary(
				externalReferenceCode, userId, groupId, externalReferenceCode,
				externalReferenceCode,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), externalReferenceCode),
				null, null, AssetVocabularyConstants.VISIBILITY_TYPE_INCOMPLETE,
				new ServiceContext()));
	}

	@Override
	public List<AssetVocabulary> getVocabularies(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<AssetVocabulary> vocabularies = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long vocabularyId = GetterUtil.getLong(
				document.get(Field.ASSET_VOCABULARY_ID));

			AssetVocabulary vocabulary = fetchAssetVocabulary(vocabularyId);

			if (vocabulary == null) {
				vocabularies = null;

				Indexer<AssetVocabulary> indexer =
					IndexerRegistryUtil.getIndexer(AssetVocabulary.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (vocabularies != null) {
				vocabularies.add(vocabulary);
			}
		}

		return vocabularies;
	}

	@Override
	public List<AssetVocabulary> getVocabularies(long[] vocabularyIds)
		throws PortalException {

		List<AssetVocabulary> vocabularies = new ArrayList<>();

		for (long vocabularyId : vocabularyIds) {
			vocabularies.add(getVocabulary(vocabularyId));
		}

		return vocabularies;
	}

	@Override
	public AssetVocabulary getVocabulary(long vocabularyId)
		throws PortalException {

		return assetVocabularyPersistence.findByPrimaryKey(vocabularyId);
	}

	@Override
	public BaseModelSearchResult<AssetVocabulary> searchVocabularies(
			long companyId, long[] groupIds, String title,
			int[] visibilityTypes, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = buildSearchContext(
			companyId, groupIds, title, visibilityTypes, start, end, sort);

		return searchVocabularies(searchContext);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings)
		throws PortalException {

		return assetVocabularyLocalService.updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings,
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType)
		throws PortalException {

		AssetVocabulary vocabulary =
			assetVocabularyPersistence.findByPrimaryKey(vocabularyId);

		vocabulary.setTitleMap(_getTrimmedTitleMap(titleMap));
		vocabulary.setDescriptionMap(descriptionMap);
		vocabulary.setSettings(settings);
		vocabulary.setVisibilityType(visibilityType);

		if (vocabulary.getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			vocabulary.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		return assetVocabularyPersistence.update(vocabulary);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			ServiceContext serviceContext)
		throws PortalException {

		AssetVocabulary vocabulary =
			assetVocabularyPersistence.findByPrimaryKey(vocabularyId);

		vocabulary.setTitleMap(_getTrimmedTitleMap(titleMap));

		if (Validator.isNotNull(title)) {
			vocabulary.setTitle(title);
		}

		vocabulary.setDescriptionMap(descriptionMap);
		vocabulary.setSettings(settings);

		if (vocabulary.getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			vocabulary.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		return assetVocabularyPersistence.update(vocabulary);
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, String title, int[] visibilityTypes,
		int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.TITLE, title);
		searchContext.setAttribute(Field.VISIBILITY_TYPE, visibilityTypes);
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);
		searchContext.setKeywords(title);
		searchContext.setSorts(sort);
		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	protected boolean hasVocabulary(long groupId, String name) {
		if (assetVocabularyPersistence.countByG_N(groupId, name) == 0) {
			return false;
		}

		return true;
	}

	protected BaseModelSearchResult<AssetVocabulary> searchVocabularies(
			SearchContext searchContext)
		throws PortalException {

		Indexer<AssetVocabulary> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(AssetVocabulary.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<AssetVocabulary> vocabularies = getVocabularies(hits);

			if (vocabularies != null) {
				return new BaseModelSearchResult<>(
					vocabularies, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	protected void validate(long groupId, String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new VocabularyNameException(
				"Category vocabulary name cannot be null for group " + groupId);
		}

		if (hasVocabulary(groupId, name)) {
			throw new DuplicateVocabularyException(
				"A category vocabulary with the name " + name +
					" already exists");
		}
	}

	private String _generateVocabularyName(long groupId, String name) {
		String vocabularyName = _getVocabularyName(name);

		vocabularyName = StringUtil.replace(
			vocabularyName, CharPool.SPACE, CharPool.DASH);

		String curVocabularyName = vocabularyName;

		int count = 0;

		while (true) {
			AssetVocabulary vocabulary = assetVocabularyPersistence.fetchByG_N(
				groupId, curVocabularyName);

			if (vocabulary == null) {
				return curVocabularyName;
			}

			curVocabularyName = curVocabularyName + CharPool.DASH + count++;
		}
	}

	private Map<Locale, String> _getTrimmedTitleMap(
		Map<Locale, String> titleMap) {

		Map<Locale, String> trimmedTitleMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
			trimmedTitleMap.put(
				entry.getKey(),
				ModelHintsUtil.trimString(
					AssetVocabulary.class.getName(), "name", entry.getValue()));
		}

		return trimmedTitleMap;
	}

	private String _getVocabularyName(String vocabularyName) {
		if (vocabularyName != null) {
			vocabularyName = vocabularyName.trim();

			return StringUtil.toLowerCase(vocabularyName);
		}

		return StringPool.BLANK;
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		AssetVocabulary assetVocabulary =
			assetVocabularyPersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (assetVocabulary != null) {
			throw new DuplicateVocabularyExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate vocabulary external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}