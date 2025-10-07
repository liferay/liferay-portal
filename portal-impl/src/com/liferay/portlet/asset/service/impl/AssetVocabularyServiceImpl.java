/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyDisplay;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.service.base.AssetVocabularyServiceBaseImpl;
import com.liferay.portlet.asset.service.permission.AssetCategoriesPermission;
import com.liferay.portlet.asset.service.permission.AssetVocabularyPermission;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides the remote service for accessing, adding, deleting, and updating
 * asset vocabularies. Its methods include permission checks.
 *
 * @author Alvaro del Castillo
 * @author Eduardo Lundgren
 * @author Jorge Ferrer
 * @author Juan Fernández
 */
public class AssetVocabularyServiceImpl extends AssetVocabularyServiceBaseImpl {

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType, ServiceContext serviceContext)
		throws PortalException {

		AssetCategoriesPermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_VOCABULARY);

		return assetVocabularyLocalService.addVocabulary(
			getUserId(), groupId, title, titleMap, descriptionMap, settings,
			visibilityType, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			ServiceContext serviceContext)
		throws PortalException {

		AssetCategoriesPermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_VOCABULARY);

		return assetVocabularyLocalService.addVocabulary(
			getUserId(), groupId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String title, ServiceContext serviceContext)
		throws PortalException {

		AssetCategoriesPermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_VOCABULARY);

		return assetVocabularyLocalService.addVocabulary(
			getUserId(), groupId, title, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String name, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, ServiceContext serviceContext)
		throws PortalException {

		AssetCategoriesPermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_VOCABULARY);

		return assetVocabularyLocalService.addVocabulary(
			getUserId(), groupId, name, title, titleMap, descriptionMap,
			settings, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			String externalReferenceCode, long groupId, String name,
			String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType, ServiceContext serviceContext)
		throws PortalException {

		AssetCategoriesPermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_VOCABULARY);

		return assetVocabularyLocalService.addVocabulary(
			externalReferenceCode, getUserId(), groupId, name, title, titleMap,
			descriptionMap, settings, visibilityType, serviceContext);
	}

	@Override
	public List<AssetVocabulary> deleteVocabularies(
			long[] vocabularyIds, ServiceContext serviceContext)
		throws PortalException {

		List<AssetVocabulary> failedVocabularies = new ArrayList<>();

		for (long vocabularyId : vocabularyIds) {
			try {
				AssetVocabularyPermission.check(
					getPermissionChecker(), vocabularyId, ActionKeys.DELETE);

				assetVocabularyLocalService.deleteVocabulary(vocabularyId);
			}
			catch (PortalException portalException) {
				if (serviceContext == null) {
					return null;
				}

				if (serviceContext.isFailOnPortalException()) {
					throw portalException;
				}

				AssetVocabulary vocabulary =
					assetVocabularyPersistence.fetchByPrimaryKey(vocabularyId);

				if (vocabulary == null) {
					vocabulary = assetVocabularyPersistence.create(
						vocabularyId);
				}

				failedVocabularies.add(vocabulary);
			}
		}

		return failedVocabularies;
	}

	@Override
	public void deleteVocabulary(long vocabularyId) throws PortalException {
		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.DELETE);

		assetVocabularyLocalService.deleteVocabulary(vocabularyId);
	}

	@Override
	public AssetVocabulary deleteVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetVocabulary vocabulary =
			assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, groupId);

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabulary.getVocabularyId(),
			ActionKeys.DELETE);

		return assetVocabularyLocalService.deleteVocabulary(vocabulary);
	}

	@Override
	public AssetVocabulary fetchVocabulary(long vocabularyId)
		throws PortalException {

		AssetVocabulary vocabulary =
			assetVocabularyLocalService.fetchAssetVocabulary(vocabularyId);

		if (vocabulary != null) {
			AssetVocabularyPermission.check(
				getPermissionChecker(), vocabulary, ActionKeys.VIEW);
		}

		return vocabulary;
	}

	@Override
	public AssetVocabulary fetchVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetVocabulary vocabulary =
			assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (vocabulary != null) {
			AssetVocabularyPermission.check(
				getPermissionChecker(), vocabulary, ActionKeys.VIEW);
		}

		return vocabulary;
	}

	@Override
	public AssetVocabulary getAssetVocabularyByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		AssetVocabulary vocabulary =
			assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, groupId);

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabulary.getVocabularyId(),
			ActionKeys.VIEW);

		return vocabulary;
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
			assetVocabularyPersistence.filterFindByGroupId(groupIds);

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

		return getGroupVocabularies(groupId, true);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary)
		throws PortalException {

		return getGroupVocabularies(
			groupId, createDefaultVocabulary, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary, int start, int end,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException {

		List<AssetVocabulary> vocabularies = getGroupVocabularies(
			groupId, start, end, orderByComparator);

		if (!vocabularies.isEmpty() || !createDefaultVocabulary) {
			return vocabularies;
		}

		int count = assetVocabularyLocalService.getGroupVocabulariesCount(
			new long[] {groupId});

		if (count > 0) {
			return vocabularies;
		}

		vocabularies = new ArrayList<>();

		AssetVocabulary vocabulary =
			assetVocabularyLocalService.addDefaultVocabulary(groupId);

		vocabularies.add(vocabulary);

		return vocabularies;
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType) {

		return assetVocabularyPersistence.filterFindByG_V(
			groupId, visibilityType);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return assetVocabularyPersistence.filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return assetVocabularyPersistence.filterFindByG_LikeN(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(long[] groupIds) {
		return assetVocabularyPersistence.filterFindByGroupId(groupIds);
	}

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes) {

		return assetVocabularyPersistence.filterFindByG_V(
			groupIds, visibilityTypes);
	}

	@Override
	public int getGroupVocabulariesCount(long groupId) {
		return assetVocabularyPersistence.filterCountByGroupId(groupId);
	}

	@Override
	public int getGroupVocabulariesCount(long groupId, String name) {
		return assetVocabularyPersistence.filterCountByG_LikeN(groupId, name);
	}

	@Override
	public int getGroupVocabulariesCount(long[] groupIds) {
		return assetVocabularyPersistence.filterCountByGroupId(groupIds);
	}

	@Override
	public AssetVocabularyDisplay getGroupVocabulariesDisplay(
			long groupId, String name, int start, int end,
			boolean addDefaultVocabulary,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException {

		List<AssetVocabulary> vocabularies;
		int total = 0;

		if (Validator.isNotNull(name)) {
			name = CustomSQLUtil.keywords(name)[0];

			vocabularies = getGroupVocabularies(
				groupId, name, start, end, orderByComparator);
			total = getGroupVocabulariesCount(groupId, name);
		}
		else {
			vocabularies = getGroupVocabularies(
				groupId, start, end, orderByComparator);
			total = getGroupVocabulariesCount(groupId);
		}

		if (addDefaultVocabulary && (total == 0) &&
			(assetVocabularyPersistence.countByGroupId(groupId) == 0)) {

			vocabularies = new ArrayList<>();

			vocabularies.add(
				assetVocabularyLocalService.addDefaultVocabulary(groupId));

			total = 1;
		}

		return new AssetVocabularyDisplay(vocabularies, total, start, end);
	}

	@Override
	public AssetVocabularyDisplay getGroupVocabulariesDisplay(
			long groupId, String name, int start, int end,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException {

		return getGroupVocabulariesDisplay(
			groupId, name, start, end, false, orderByComparator);
	}

	public AssetVocabulary getOrAddEmptyVocabulary(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetVocabulary vocabulary =
			assetVocabularyService.fetchVocabularyByExternalReferenceCode(
				externalReferenceCode, groupId);

		if (vocabulary != null) {
			return vocabulary;
		}

		AssetCategoriesPermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_VOCABULARY);

		return assetVocabularyLocalService.getOrAddEmptyVocabulary(
			externalReferenceCode, getUserId(), groupId);
	}

	@Override
	public AssetVocabulary getVocabulary(long vocabularyId)
		throws PortalException {

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.VIEW);

		return assetVocabularyLocalService.getVocabulary(vocabularyId);
	}

	@Override
	public AssetVocabularyDisplay searchVocabulariesDisplay(
			long groupId, String title, boolean addDefaultVocabulary, int start,
			int end)
		throws PortalException {

		return searchVocabulariesDisplay(
			groupId, title, addDefaultVocabulary, start, end, null);
	}

	@Override
	public AssetVocabularyDisplay searchVocabulariesDisplay(
			long groupId, String title, boolean addDefaultVocabulary, int start,
			int end, Sort sort)
		throws PortalException {

		User user = getUser();

		BaseModelSearchResult<AssetVocabulary> baseModelSearchResult =
			assetVocabularyLocalService.searchVocabularies(
				user.getCompanyId(), new long[] {groupId}, title, null, start,
				end, sort);

		List<AssetVocabulary> vocabularies =
			baseModelSearchResult.getBaseModels();
		int total = baseModelSearchResult.getLength();

		if (addDefaultVocabulary && (total == 0)) {
			total = assetVocabularyPersistence.countByGroupId(groupId);

			if (total == 0) {
				vocabularies = new ArrayList<>(1);

				AssetVocabulary defaultVocabulary =
					assetVocabularyLocalService.addDefaultVocabulary(groupId);

				vocabularies.add(defaultVocabulary);

				total = 1;
			}
		}

		return new AssetVocabularyDisplay(vocabularies, total, start, end);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings)
		throws PortalException {

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.UPDATE);

		return assetVocabularyLocalService.updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType)
		throws PortalException {

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.UPDATE);

		return assetVocabularyLocalService.updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings, visibilityType);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			ServiceContext serviceContext)
		throws PortalException {

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.UPDATE);

		return assetVocabularyLocalService.updateVocabulary(
			vocabularyId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

}