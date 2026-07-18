/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.friendly.url.exception.FriendlyURLCategoryException;
import com.liferay.friendly.url.exception.FriendlyURLLengthException;
import com.liferay.friendly.url.exception.FriendlyURLLocalizationUrlTitleException;
import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryLocalizationException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.model.FriendlyURLEntryMapping;
import com.liferay.friendly.url.service.base.FriendlyURLEntryLocalServiceBaseImpl;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryMappingPersistence;
import com.liferay.friendly.url.util.comparator.FriendlyURLEntryCreateDateComparator;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.friendly.url.model.FriendlyURLEntry",
	service = AopService.class
)
public class FriendlyURLEntryLocalServiceImpl
	extends FriendlyURLEntryLocalServiceBaseImpl {

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			ServiceContext serviceContext)
		throws PortalException {

		validate(groupId, classNameId, parentClassPK, classPK, urlTitleMap);

		_validateAssetCategories(urlTitleMap, serviceContext);

		FriendlyURLEntryMapping friendlyURLEntryMapping =
			_friendlyURLEntryMappingPersistence.fetchByC_C(
				classNameId, classPK);

		if (friendlyURLEntryMapping == null) {
			long friendlyURLMappingId = counterLocalService.increment();

			friendlyURLEntryMapping =
				_friendlyURLEntryMappingPersistence.create(
					friendlyURLMappingId);

			friendlyURLEntryMapping.setClassNameId(classNameId);
			friendlyURLEntryMapping.setClassPK(classPK);
		}

		Map<String, String> existingUrlTitleMap = _getURLTitleMap(
			friendlyURLEntryMapping);

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryPersistence.fetchByPrimaryKey(
				friendlyURLEntryMapping.getFriendlyURLEntryId());

		if ((friendlyURLEntry != null) &&
			_containsAllURLTitles(existingUrlTitleMap, urlTitleMap)) {

			_updateAssetEntry(friendlyURLEntry, serviceContext);

			return friendlyURLEntry;
		}

		Group group = _groupLocalService.getGroup(groupId);

		long friendlyURLEntryId = counterLocalService.increment();

		friendlyURLEntry = friendlyURLEntryPersistence.create(
			friendlyURLEntryId);

		friendlyURLEntry.setUuid(serviceContext.getUuid());
		friendlyURLEntry.setDefaultLanguageId(defaultLanguageId);
		friendlyURLEntry.setGroupId(groupId);
		friendlyURLEntry.setCompanyId(group.getCompanyId());
		friendlyURLEntry.setClassNameId(classNameId);
		friendlyURLEntry.setParentClassPK(parentClassPK);
		friendlyURLEntry.setClassPK(classPK);

		if ((BatchEngineThreadLocal.isBatchImportInProcess() &&
			 _isBatchPortletDataHandler(classNameId, group.getCompanyId())) ||
			!ExportImportThreadLocal.isImportInProcess()) {

			friendlyURLEntryMapping.setFriendlyURLEntryId(friendlyURLEntryId);

			_friendlyURLEntryMappingPersistence.update(friendlyURLEntryMapping);
		}

		friendlyURLEntry = friendlyURLEntryPersistence.update(friendlyURLEntry);

		_updateFriendlyURLEntryLocalizations(
			friendlyURLEntry, classNameId, parentClassPK,
			_merge(urlTitleMap, existingUrlTitleMap));

		// Asset

		_updateAssetEntry(friendlyURLEntry, serviceContext);

		return friendlyURLEntry;
	}

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			Map<String, String> urlTitleMap, ServiceContext serviceContext)
		throws PortalException {

		return addFriendlyURLEntry(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			classPK, LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			urlTitleMap, serviceContext);
	}

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			ServiceContext serviceContext)
		throws PortalException {

		return addFriendlyURLEntry(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			classPK, defaultLanguageId, urlTitleMap, serviceContext);
	}

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK, String urlTitle,
			ServiceContext serviceContext)
		throws PortalException {

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		return addFriendlyURLEntry(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			classPK, defaultLanguageId,
			Collections.singletonMap(defaultLanguageId, urlTitle),
			serviceContext);
	}

	@Override
	public void deleteCompanyFriendlyURLEntries(
		long companyId, long classNameId) {

		for (FriendlyURLEntry friendlyURLEntry :
				friendlyURLEntryPersistence.findByC_C(companyId, classNameId)) {

			_deleteFriendlyURLEntry(friendlyURLEntry);
		}
	}

	@Override
	public FriendlyURLEntry deleteFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		FriendlyURLEntry deletedFriendlyURLEntry =
			friendlyURLEntryPersistence.remove(friendlyURLEntry);

		FriendlyURLEntryMapping friendlyURLEntryMapping =
			_friendlyURLEntryMappingPersistence.fetchByC_C(
				friendlyURLEntry.getClassNameId(),
				friendlyURLEntry.getClassPK());

		if ((friendlyURLEntryMapping != null) &&
			(friendlyURLEntryMapping.getFriendlyURLEntryId() ==
				friendlyURLEntry.getFriendlyURLEntryId())) {

			friendlyURLEntry = friendlyURLEntryPersistence.fetchByG_C_C_First(
				friendlyURLEntry.getGroupId(),
				friendlyURLEntry.getClassNameId(),
				friendlyURLEntry.getClassPK(),
				FriendlyURLEntryCreateDateComparator.getInstance(false));

			if (friendlyURLEntry == null) {
				_friendlyURLEntryMappingPersistence.remove(
					friendlyURLEntryMapping);
			}
			else {
				friendlyURLEntryMapping.setFriendlyURLEntryId(
					friendlyURLEntry.getFriendlyURLEntryId());

				_friendlyURLEntryMappingPersistence.update(
					friendlyURLEntryMapping);
			}
		}

		// Asset

		_deleteAssetEntry(
			FriendlyURLEntry.class.getName(),
			deletedFriendlyURLEntry.getFriendlyURLEntryId());

		return deletedFriendlyURLEntry;
	}

	@Override
	public FriendlyURLEntry deleteFriendlyURLEntry(long friendlyURLEntryId)
		throws PortalException {

		return deleteFriendlyURLEntry(
			friendlyURLEntryPersistence.findByPrimaryKey(friendlyURLEntryId));
	}

	@Override
	public void deleteFriendlyURLEntry(
		long groupId, long classNameId, long classPK) {

		FriendlyURLEntryMapping friendlyURLEntryMapping =
			_friendlyURLEntryMappingPersistence.fetchByC_C(
				classNameId, classPK);

		if (friendlyURLEntryMapping == null) {
			return;
		}

		List<FriendlyURLEntry> friendlyURLEntries =
			friendlyURLEntryPersistence.findByG_C_C(
				groupId, classNameId, classPK);

		for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
			friendlyURLEntryPersistence.remove(friendlyURLEntry);

			// Asset

			_deleteAssetEntry(
				FriendlyURLEntry.class.getName(),
				friendlyURLEntry.getFriendlyURLEntryId());
		}

		_friendlyURLEntryMappingPersistence.remove(friendlyURLEntryMapping);
	}

	@Override
	public void deleteFriendlyURLLocalizationEntry(
			FriendlyURLEntryLocalization friendlyURLEntryLocalization)
		throws PortalException {

		friendlyURLEntryLocalization =
			friendlyURLEntryLocalizationPersistence.remove(
				friendlyURLEntryLocalization);

		long friendlyURLEntryId =
			friendlyURLEntryLocalization.getFriendlyURLEntryId();

		int count =
			friendlyURLEntryLocalizationPersistence.countByFriendlyURLEntryId(
				friendlyURLEntryId);

		if (count == 0) {
			FriendlyURLEntry friendlyURLEntry =
				friendlyURLEntryPersistence.fetchByPrimaryKey(
					friendlyURLEntryId);

			if (friendlyURLEntry == null) {
				return;
			}

			friendlyURLEntryLocalService.deleteFriendlyURLEntry(
				friendlyURLEntryId);
		}

		// Asset

		_deleteAssetEntry(FriendlyURLEntry.class.getName(), friendlyURLEntryId);
	}

	@Override
	public void deleteFriendlyURLLocalizationEntry(
			long friendlyURLEntryId, String languageId)
		throws PortalException {

		deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryLocalizationPersistence.
				findByFriendlyURLEntryId_LanguageId(
					friendlyURLEntryId, languageId));
	}

	@Override
	public void deleteGroupFriendlyURLEntries(long groupId, long classNameId) {
		for (FriendlyURLEntry friendlyURLEntry :
				friendlyURLEntryPersistence.findByG_C(groupId, classNameId)) {

			_deleteFriendlyURLEntry(friendlyURLEntry);
		}
	}

	@Override
	public FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			friendlyURLEntryLocalizationPersistence.fetchByG_C_P_U_First(
				groupId, classNameId, parentClassPK,
				_friendlyURLNormalizer.normalizeWithEncoding(urlTitle), null);

		if (friendlyURLEntryLocalization == null) {
			return null;
		}

		return friendlyURLEntryPersistence.fetchByPrimaryKey(
			friendlyURLEntryLocalization.getFriendlyURLEntryId());
	}

	@Override
	public FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, String urlTitle) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			friendlyURLEntryLocalizationPersistence.fetchByG_C_U_First(
				groupId, classNameId,
				_friendlyURLNormalizer.normalizeWithEncoding(urlTitle), null);

		if (friendlyURLEntryLocalization == null) {
			return null;
		}

		return friendlyURLEntryPersistence.fetchByPrimaryKey(
			friendlyURLEntryLocalization.getFriendlyURLEntryId());
	}

	@Override
	public FriendlyURLEntryLocalization fetchFriendlyURLEntryLocalization(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		return friendlyURLEntryLocalizationPersistence.fetchByG_C_P_L_U(
			groupId, classNameId, parentClassPK,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			_friendlyURLNormalizer.normalizeWithEncoding(urlTitle));
	}

	@Override
	public FriendlyURLEntryLocalization fetchFriendlyURLEntryLocalization(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return friendlyURLEntryLocalizationPersistence.fetchByG_C_P_L_U(
			groupId, classNameId, parentClassPK, languageId,
			_friendlyURLNormalizer.normalizeWithEncoding(urlTitle));
	}

	@Override
	public FriendlyURLEntry fetchMainFriendlyURLEntry(
		long classNameId, long classPK) {

		FriendlyURLEntryMapping friendlyURLEntryMapping =
			_friendlyURLEntryMappingPersistence.fetchByC_C(
				classNameId, classPK);

		if (friendlyURLEntryMapping == null) {
			return null;
		}

		return friendlyURLEntryPersistence.fetchByPrimaryKey(
			friendlyURLEntryMapping.getFriendlyURLEntryId());
	}

	@Override
	public List<FriendlyURLEntry> getFriendlyURLEntries(
		long groupId, long classNameId, long classPK) {

		return friendlyURLEntryPersistence.findByG_C_C(
			groupId, classNameId, classPK);
	}

	@Override
	public FriendlyURLEntryLocalization getFriendlyURLEntryLocalization(
			long groupId, long classNameId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return friendlyURLEntryLocalizationPersistence.findByG_C_P_L_U(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			_friendlyURLNormalizer.normalizeWithEncoding(urlTitle));
	}

	@Override
	public FriendlyURLEntryLocalization getFriendlyURLEntryLocalization(
			long groupId, long classNameId, String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return friendlyURLEntryLocalizationPersistence.findByG_C_P_L_U(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			languageId, _friendlyURLNormalizer.normalizeWithEncoding(urlTitle));
	}

	@Override
	public List<FriendlyURLEntryLocalization> getFriendlyURLEntryLocalizations(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return friendlyURLEntryLocalizationPersistence.findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end,
			orderByComparator);
	}

	@Override
	public FriendlyURLEntry getMainFriendlyURLEntry(
			long classNameId, long classPK)
		throws PortalException {

		FriendlyURLEntryMapping friendlyURLEntryMapping =
			_friendlyURLEntryMappingPersistence.findByC_C(classNameId, classPK);

		return friendlyURLEntryPersistence.findByPrimaryKey(
			friendlyURLEntryMapping.getFriendlyURLEntryId());
	}

	@Override
	public String getUniqueUrlTitle(
		long groupId, long classNameId, long parentClassPK, long classPK,
		String urlTitle, String languageId) {

		if (urlTitle.startsWith(StringPool.SLASH)) {
			urlTitle = urlTitle.replaceAll("^/+", StringPool.SLASH);
		}

		String normalizedUrlTitle =
			_friendlyURLNormalizer.normalizeWithEncoding(urlTitle);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String curUrlTitle = _getURLEncodedSubstring(
			urlTitle, normalizedUrlTitle, maxLength);

		String prefix = curUrlTitle;

		if (Validator.isNull(languageId)) {
			languageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());
		}

		for (int i = 1;
			 _hasFriendlyURLEntryWithUrlTitle(
				 groupId, classNameId, parentClassPK, classPK, curUrlTitle,
				 languageId);
			 i++) {

			String suffix = StringPool.DASH + i;

			prefix = _getURLEncodedSubstring(
				urlTitle, prefix, maxLength - suffix.length());

			curUrlTitle = _friendlyURLNormalizer.normalizeWithEncoding(
				prefix + suffix);
		}

		return curUrlTitle;
	}

	@Override
	public String getUniqueUrlTitle(
		long groupId, long classNameId, long classPK, String urlTitle,
		String languageId) {

		return getUniqueUrlTitle(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			classPK, urlTitle, languageId);
	}

	@Override
	public Map<String, String> getUniqueUrlTitleMap(
		long groupId, long classNameId, long parentClassPK, long classPK,
		Map<Locale, String> titleMap) {

		Map<String, String> urlTitleMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
			if (Validator.isNotNull(entry.getValue())) {
				String languageId = LocaleUtil.toLanguageId(entry.getKey());

				urlTitleMap.put(
					languageId,
					friendlyURLEntryLocalService.getUniqueUrlTitle(
						groupId, classNameId, parentClassPK, classPK,
						entry.getValue(), languageId));
			}
		}

		return urlTitleMap;
	}

	@Override
	public void setMainFriendlyURLEntry(FriendlyURLEntry friendlyURLEntry) {
		FriendlyURLEntryMapping friendlyURLEntryMapping =
			_friendlyURLEntryMappingPersistence.fetchByC_C(
				friendlyURLEntry.getClassNameId(),
				friendlyURLEntry.getClassPK());

		if (friendlyURLEntryMapping == null) {
			long friendlyURLEntryMappingId = counterLocalService.increment();

			friendlyURLEntryMapping =
				_friendlyURLEntryMappingPersistence.create(
					friendlyURLEntryMappingId);

			friendlyURLEntryMapping.setClassNameId(
				friendlyURLEntry.getClassNameId());
			friendlyURLEntryMapping.setClassPK(friendlyURLEntry.getClassPK());
		}

		friendlyURLEntryMapping.setFriendlyURLEntryId(
			friendlyURLEntry.getFriendlyURLEntryId());

		_friendlyURLEntryMappingPersistence.update(friendlyURLEntryMapping);
	}

	@Override
	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long parentClassPK,
			long classPK, String defaultLanguageId,
			Map<String, String> urlTitleMap, ServiceContext serviceContext)
		throws PortalException {

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryPersistence.findByPrimaryKey(friendlyURLEntryId);

		validate(
			friendlyURLEntry.getGroupId(), classNameId, parentClassPK, classPK,
			urlTitleMap);

		_validateAssetCategories(urlTitleMap, serviceContext);

		friendlyURLEntry.setDefaultLanguageId(defaultLanguageId);
		friendlyURLEntry.setClassNameId(classNameId);
		friendlyURLEntry.setParentClassPK(parentClassPK);
		friendlyURLEntry.setClassPK(classPK);

		friendlyURLEntry = friendlyURLEntryPersistence.update(friendlyURLEntry);

		_updateFriendlyURLEntryLocalizations(
			friendlyURLEntry, classNameId, parentClassPK, urlTitleMap);

		// Asset

		_updateAssetEntry(friendlyURLEntry, serviceContext);

		return friendlyURLEntry;
	}

	@Override
	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap)
		throws PortalException {

		return updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId, classPK, defaultLanguageId,
			urlTitleMap, null);
	}

	@Override
	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			ServiceContext serviceContext)
		throws PortalException {

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryPersistence.findByPrimaryKey(friendlyURLEntryId);

		return updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId,
			friendlyURLEntry.getParentClassPK(), classPK, defaultLanguageId,
			urlTitleMap, serviceContext);
	}

	@Override
	public FriendlyURLEntryLocalization updateFriendlyURLLocalization(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		return friendlyURLEntryLocalizationPersistence.update(
			friendlyURLEntryLocalization);
	}

	@Override
	public FriendlyURLEntryLocalization updateFriendlyURLLocalization(
			long friendlyURLLocalizationId, String urlTitle)
		throws PortalException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			friendlyURLEntryLocalizationPersistence.fetchByPrimaryKey(
				friendlyURLLocalizationId);

		if (friendlyURLEntryLocalization == null) {
			return null;
		}

		friendlyURLEntryLocalization.setUrlTitle(urlTitle);

		return friendlyURLEntryLocalizationPersistence.update(
			friendlyURLEntryLocalization);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			Map<String, String> urlTitleMap)
		throws PortalException {

		for (Map.Entry<String, String> entry : urlTitleMap.entrySet()) {
			validate(
				groupId, classNameId, parentClassPK, classPK, entry.getKey(),
				entry.getValue());
		}
	}

	@Override
	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String urlTitle)
		throws PortalException {

		validate(
			groupId, classNameId, parentClassPK, classPK,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), urlTitle);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String languageId, String urlTitle)
		throws PortalException {

		if (urlTitle.endsWith(StringPool.SLASH)) {
			throw new FriendlyURLLocalizationUrlTitleException.
				MustNotHaveTrailingSlash();
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String normalizedUrlTitle =
			_friendlyURLNormalizer.normalizeWithEncoding(urlTitle);

		if (normalizedUrlTitle.length() > maxLength) {
			throw new FriendlyURLLengthException(
				urlTitle + " is longer than " + maxLength);
		}

		FriendlyURLEntryLocalization existingFriendlyURLEntryLocalization =
			friendlyURLEntryLocalizationPersistence.fetchByG_C_P_L_U(
				groupId, classNameId, parentClassPK, languageId,
				normalizedUrlTitle);

		if ((existingFriendlyURLEntryLocalization != null) &&
			(existingFriendlyURLEntryLocalization.getClassPK() != classPK)) {

			throw new DuplicateFriendlyURLEntryException(
				existingFriendlyURLEntryLocalization.toString());
		}
	}

	@Override
	public void validate(
			long groupId, long classNameId, long classPK,
			Map<String, String> urlTitleMap)
		throws PortalException {

		validate(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			classPK, urlTitleMap);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long classPK, String urlTitle)
		throws PortalException {

		validate(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			classPK, urlTitle);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long classPK, String languageId,
			String urlTitle)
		throws PortalException {

		validate(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			classPK, languageId, urlTitle);
	}

	@Override
	public void validate(long groupId, long classNameId, String urlTitle)
		throws PortalException {

		validate(
			groupId, classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			0, LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), urlTitle);
	}

	private boolean _containsAllURLTitles(
		Map<String, String> existingUrlTitleMap,
		Map<String, String> urlTitleMap) {

		for (Map.Entry<String, String> entry : urlTitleMap.entrySet()) {
			String urlTitle = _friendlyURLNormalizer.normalizeWithEncoding(
				entry.getValue());

			if (!urlTitle.equals(existingUrlTitleMap.get(entry.getKey()))) {
				return false;
			}
		}

		return true;
	}

	private void _deleteAssetEntry(String className, long classPK) {
		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		if (assetEntry == null) {
			return;
		}

		try {
			_assetEntryLocalService.deleteEntry(assetEntry.getEntryId());
		}
		catch (PortalException portalException) {
			ReflectionUtil.throwException(portalException);
		}
	}

	private void _deleteFriendlyURLEntry(FriendlyURLEntry friendlyURLEntry) {
		friendlyURLEntryLocalizationPersistence.removeByFriendlyURLEntryId(
			friendlyURLEntry.getFriendlyURLEntryId());

		friendlyURLEntryPersistence.remove(friendlyURLEntry);

		FriendlyURLEntryMapping friendlyURLEntryMapping =
			_friendlyURLEntryMappingPersistence.fetchByC_C(
				friendlyURLEntry.getClassNameId(),
				friendlyURLEntry.getClassPK());

		if ((friendlyURLEntryMapping != null) &&
			(friendlyURLEntryMapping.getFriendlyURLEntryId() ==
				friendlyURLEntry.getFriendlyURLEntryId())) {

			_friendlyURLEntryMappingPersistence.remove(friendlyURLEntryMapping);
		}

		_deleteAssetEntry(
			FriendlyURLEntry.class.getName(),
			friendlyURLEntry.getFriendlyURLEntryId());
	}

	private String _getURLEncodedSubstring(
		String decodedString, String encodedString, int maxLength) {

		int endPos = decodedString.length();

		while (encodedString.length() > maxLength) {
			endPos--;

			if ((endPos > 0) &&
				Character.isHighSurrogate(decodedString.charAt(endPos - 1))) {

				endPos--;
			}

			encodedString = _friendlyURLNormalizer.normalizeWithEncoding(
				decodedString.substring(0, endPos));
		}

		return encodedString;
	}

	private Map<String, String> _getURLTitleMap(
		FriendlyURLEntryMapping friendlyURLEntryMapping) {

		Map<String, String> urlTitleMap = new HashMap<>();

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				friendlyURLEntryLocalizationPersistence.
					findByFriendlyURLEntryId(
						friendlyURLEntryMapping.getFriendlyURLEntryId())) {

			urlTitleMap.put(
				friendlyURLEntryLocalization.getLanguageId(),
				friendlyURLEntryLocalization.getUrlTitle());
		}

		return urlTitleMap;
	}

	private boolean _hasFriendlyURLEntryWithUrlTitle(
		long groupId, long classNameId, long parentClassPK, long notClassPK,
		String urlTitle, String languageId) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			friendlyURLEntryLocalizationPersistence.fetchByG_C_P_L_U(
				groupId, classNameId, parentClassPK, languageId, urlTitle);

		if ((friendlyURLEntryLocalization != null) &&
			(friendlyURLEntryLocalization.getClassPK() != notClassPK)) {

			return true;
		}

		friendlyURLEntryLocalization =
			friendlyURLEntryLocalizationPersistence.fetchByG_C_P_NotL_U_First(
				groupId, classNameId, parentClassPK, languageId, urlTitle,
				null);

		if ((friendlyURLEntryLocalization != null) &&
			(friendlyURLEntryLocalization.getClassPK() != notClassPK)) {

			return true;
		}

		return false;
	}

	private boolean _isBatchPortletDataHandler(
		long classNameId, long companyId) {

		String className = _portal.fetchClassName(classNameId);

		String[] classNames = StringUtil.split(
			className, ResourceActionsUtil.getCompositeModelNameSeparator());

		Portlet portlet = _exportImportHelper.getDataSiteLevelPortlet(
			classNames[0], companyId, true);

		if (portlet == null) {
			return false;
		}

		PortletDataHandler portletDataHandlerInstance =
			portlet.getPortletDataHandlerInstance();

		return portletDataHandlerInstance.isBatch();
	}

	private Map<String, String> _merge(
		Map<String, String> masterMap, Map<String, String> copyMap) {

		Map<String, String> map = new HashMap<>(copyMap);

		MapUtil.merge(masterMap, map);

		return map;
	}

	private Map<String, String> _sortUrlTitleMap(
		long groupId, Map<String, String> urlTitleMap) {

		Map<String, String> sortedUrlTitleMap = new LinkedHashMap<>();

		for (Locale locale : _language.getAvailableLocales(groupId)) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String value = urlTitleMap.get(languageId);

			if (value == null) {
				continue;
			}

			sortedUrlTitleMap.put(languageId, value);
		}

		return sortedUrlTitleMap;
	}

	private void _updateAssetEntry(
			FriendlyURLEntry friendlyURLEntry, ServiceContext serviceContext)
		throws PortalException {

		if (serviceContext == null) {
			return;
		}

		Map<String, Serializable> attributes = serviceContext.getAttributes();

		if (attributes.containsKey("friendlyURLAssetCategoryIds")) {
			_assetEntryLocalService.updateEntry(
				serviceContext.getUserId(), friendlyURLEntry.getGroupId(),
				friendlyURLEntry.getCreateDate(),
				friendlyURLEntry.getModifiedDate(),
				FriendlyURLEntry.class.getName(),
				friendlyURLEntry.getFriendlyURLEntryId(),
				friendlyURLEntry.getUuid(), 0,
				GetterUtil.getLongValues(
					serviceContext.getAttribute("friendlyURLAssetCategoryIds")),
				new String[0], true, false, null, null, null, null,
				ContentTypes.TEXT_PLAIN, null, null, null, null, null, 0, 0,
				serviceContext.getAssetPriority());
		}
	}

	private void _updateFriendlyURLEntryLocalizations(
			FriendlyURLEntry friendlyURLEntry, long classNameId,
			long parentClassPK, Map<String, String> urlTitleMap)
		throws PortalException {

		urlTitleMap = _sortUrlTitleMap(
			friendlyURLEntry.getGroupId(), urlTitleMap);

		for (Map.Entry<String, String> entry : urlTitleMap.entrySet()) {
			String oldURLTitle = entry.getValue();

			String normalizedUrlTitle =
				_friendlyURLNormalizer.normalizeWithEncoding(oldURLTitle);

			if (Validator.isNotNull(normalizedUrlTitle)) {
				FriendlyURLEntryLocalization
					existingFriendlyURLEntryLocalization =
						friendlyURLEntryLocalizationPersistence.
							fetchByG_C_P_L_U(
								friendlyURLEntry.getGroupId(), classNameId,
								parentClassPK, entry.getKey(),
								normalizedUrlTitle);

				if (existingFriendlyURLEntryLocalization != null) {
					String existingUrlTitle =
						existingFriendlyURLEntryLocalization.getUrlTitle();

					if (existingUrlTitle.equals(oldURLTitle)) {
						existingFriendlyURLEntryLocalization.
							setFriendlyURLEntryId(
								friendlyURLEntry.getFriendlyURLEntryId());

						updateFriendlyURLLocalization(
							existingFriendlyURLEntryLocalization);
					}
				}
				else {
					updateFriendlyURLEntryLocalization(
						friendlyURLEntry, entry.getKey(), normalizedUrlTitle);
				}
			}
			else if ((normalizedUrlTitle != null) &&
					 normalizedUrlTitle.equals(StringPool.BLANK)) {

				String defaultLanguageId =
					friendlyURLEntry.getDefaultLanguageId();

				if (!defaultLanguageId.equals(entry.getKey())) {
					FriendlyURLEntryLocalization friendlyURLEntryLocalization =
						friendlyURLEntryLocalizationPersistence.
							fetchByFriendlyURLEntryId_LanguageId(
								friendlyURLEntry.getFriendlyURLEntryId(),
								entry.getKey());

					if (friendlyURLEntryLocalization != null) {
						deleteFriendlyURLLocalizationEntry(
							friendlyURLEntry.getFriendlyURLEntryId(),
							entry.getKey());
					}
				}
			}
		}
	}

	private void _validateAssetCategories(
			Map<String, String> urlTitleMap, ServiceContext serviceContext)
		throws PortalException {

		if (serviceContext == null) {
			return;
		}

		long[] friendlyURLAssetCategoryIds = GetterUtil.getLongValues(
			serviceContext.getAttribute("friendlyURLAssetCategoryIds"));

		if (ArrayUtil.isEmpty(friendlyURLAssetCategoryIds)) {
			return;
		}

		for (Map.Entry<String, String> entry : urlTitleMap.entrySet()) {
			String value = entry.getValue();

			if (value.contains(StringPool.SLASH)) {
				throw new FriendlyURLCategoryException();
			}
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private FriendlyURLEntryMappingPersistence
		_friendlyURLEntryMappingPersistence;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}