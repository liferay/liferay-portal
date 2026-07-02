/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.test.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author Máté Thurzó
 */
public class AssetTestUtil {

	public static AssetEntry addAssetEntry(long groupId) {
		return addAssetEntry(groupId, null);
	}

	public static AssetEntry addAssetEntry(long groupId, Date publishDate) {
		return addAssetEntry(
			groupId, publishDate, RandomTestUtil.randomString());
	}

	public static AssetEntry addAssetEntry(
		long groupId, Date publishDate, String className) {

		long assetEntryId = CounterLocalServiceUtil.increment();

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.createAssetEntry(
			assetEntryId);

		assetEntry.setGroupId(groupId);
		assetEntry.setClassName(className);
		assetEntry.setClassPK(RandomTestUtil.randomLong());
		assetEntry.setVisible(true);
		assetEntry.setPublishDate(publishDate);

		return AssetEntryLocalServiceUtil.updateAssetEntry(assetEntry);
	}

	public static AssetCategory addCategory(long groupId, long vocabularyId)
		throws Exception {

		return addCategory(
			groupId, vocabularyId,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
	}

	public static AssetCategory addCategory(
			long groupId, long vocabularyId, long parentCategoryId)
		throws Exception {

		Locale locale = LocaleUtil.getSiteDefault();

		Map<Locale, String> titleMap = HashMapBuilder.put(
			locale, RandomTestUtil.randomString()
		).build();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			locale, RandomTestUtil.randomString()
		).build();

		String[] categoryProperties = null;

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return AssetCategoryLocalServiceUtil.addCategory(
			null, TestPropsValues.getUserId(), groupId, parentCategoryId,
			titleMap, descriptionMap, vocabularyId, false, categoryProperties,
			serviceContext);
	}

	public static AssetCategory addCategory(
			String externalReferenceCode, long groupId, long vocabularyId)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		Map<Locale, String> titleMap = HashMapBuilder.put(
			locale, RandomTestUtil.randomString()
		).build();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			locale, RandomTestUtil.randomString()
		).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return AssetCategoryLocalServiceUtil.addCategory(
			externalReferenceCode, TestPropsValues.getUserId(), groupId,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, titleMap,
			descriptionMap, vocabularyId, false, null, serviceContext);
	}

	public static AssetTag addTag(long groupId) throws Exception {
		return addTag(groupId, RandomTestUtil.randomString());
	}

	public static AssetTag addTag(long groupId, String assetTagName)
		throws PortalException {

		return addTag(null, groupId, assetTagName);
	}

	public static AssetTag addTag(
			String externalReferenceCode, long groupId, String assetTagName)
		throws PortalException {

		long userId = TestPropsValues.getUserId();

		return AssetTagLocalServiceUtil.addTag(
			externalReferenceCode, userId, groupId, assetTagName,
			ServiceContextTestUtil.getServiceContext(groupId, userId));
	}

	public static AssetVocabulary addVocabulary(long groupId) throws Exception {
		long userId = TestPropsValues.getUserId();

		return AssetVocabularyLocalServiceUtil.addVocabulary(
			userId, groupId, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(groupId, userId));
	}

	public static AssetVocabulary addVocabulary(
			long groupId, long classNameId, long classTypePK, boolean required)
		throws Exception {

		return addVocabulary(
			groupId, classNameId, classTypePK, false, required);
	}

	public static AssetVocabulary addVocabulary(
			long groupId, long classNameId, long classTypePK,
			boolean depotRequired, boolean required)
		throws Exception {

		Locale locale = LocaleUtil.getSiteDefault();

		Map<Locale, String> titleMap = HashMapBuilder.put(
			locale, RandomTestUtil.randomString()
		).build();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			locale, RandomTestUtil.randomString()
		).build();

		AssetVocabularySettingsHelper vocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		vocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			new long[] {classNameId}, new long[] {classTypePK},
			new boolean[] {depotRequired}, new boolean[] {required});
		vocabularySettingsHelper.setMultiValued(true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return AssetVocabularyServiceUtil.addVocabulary(
			groupId, RandomTestUtil.randomString(), titleMap, descriptionMap,
			vocabularySettingsHelper.toString(), serviceContext);
	}

	public static AssetVocabulary addVocabulary(long groupId, String name)
		throws Exception {

		long userId = TestPropsValues.getUserId();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId, userId);

		return AssetVocabularyLocalServiceUtil.addVocabulary(
			userId, groupId, name, name,
			Collections.singletonMap(LocaleUtil.getDefault(), name),
			Collections.emptyMap(), StringPool.BLANK, serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			String externalReferenceCode, long groupId)
		throws PortalException {

		long userId = TestPropsValues.getUserId();

		String name = RandomTestUtil.randomString();

		return AssetVocabularyLocalServiceUtil.addVocabulary(
			externalReferenceCode, userId, groupId, name, name,
			Collections.singletonMap(LocaleUtil.getDefault(), name),
			Collections.emptyMap(), StringPool.BLANK,
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext(groupId, userId));
	}

}