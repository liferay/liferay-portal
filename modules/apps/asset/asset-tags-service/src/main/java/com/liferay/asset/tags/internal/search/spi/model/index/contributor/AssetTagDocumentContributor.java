/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.internal.search.spi.model.index.contributor;

import com.liferay.asset.kernel.model.AssetEntries_AssetTagsTable;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagTable;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentContributor;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = DocumentContributor.class)
public class AssetTagDocumentContributor
	implements DocumentContributor<AssetTag> {

	@Override
	public void contribute(Document document, BaseModel<AssetTag> baseModel) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext != null) && serviceContext.isStrictAdd()) {
			return;
		}

		String className = document.get(Field.ENTRY_CLASS_NAME);

		long classNameId = _portal.getClassNameId(className);

		long classPK = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

		List<Object[]> assetTagObjectsList = _lookupAssetTagObjectsList(
			classNameId, classPK);

		if (ListUtil.isEmpty(assetTagObjectsList)) {
			return;
		}

		document.addKeyword(
			Field.ASSET_TAG_IDS,
			ListUtil.toLongArray(
				assetTagObjectsList,
				assetTagObjects -> (Long)assetTagObjects[0]));

		List<String> assetTagNamesList = ListUtil.toList(
			assetTagObjectsList, assetTagObjects -> (String)assetTagObjects[1]);

		String[] assetTagNames = assetTagNamesList.toArray(new String[0]);

		_contributeAssetTagNames(document, assetTagNames, baseModel);

		document.addText(Field.ASSET_TAG_NAMES, assetTagNames);
	}

	private void _contributeAssetTagNames(
		Document document, String[] assetTagNames,
		BaseModel<AssetTag> baseModel) {

		Long groupId = _getGroupId(baseModel);

		if (groupId == null) {
			return;
		}

		document.addText(
			_localization.getLocalizedName(
				Field.ASSET_TAG_NAMES,
				LocaleUtil.toLanguageId(_getSiteDefaultLocale(groupId))),
			assetTagNames);
	}

	private Long _getGroupId(BaseModel<?> baseModel) {
		if (baseModel instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)baseModel;

			return groupedModel.getGroupId();
		}

		if (baseModel instanceof Organization) {
			Organization organization = (Organization)baseModel;

			return organization.getGroupId();
		}

		if (!(baseModel instanceof User)) {
			return null;
		}

		User user = (User)baseModel;

		return user.getGroupId();
	}

	private Locale _getSiteDefaultLocale(long groupId) {
		try {
			return _portal.getSiteDefaultLocale(groupId);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private List<Object[]> _lookupAssetTagObjectsList(
		long classNameId, long classPK) {

		Map<Long, Map<Long, List<Object[]>>> assetTagObjectsListsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> _assetTagLocalService.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						AssetTagTable.INSTANCE
					),
					false),
				AssetTagDocumentContributor.class.getName(),
				count -> {
					Map<Long, Map<Long, List<Object[]>>>
						localAssetTagObjectsListsMap = new HashMap<>();

					if (count == 0) {
						return localAssetTagObjectsListsMap;
					}

					DSLQuery dslQuery = DSLQueryFactoryUtil.select(
						AssetEntryTable.INSTANCE.classNameId,
						AssetEntryTable.INSTANCE.classPK,
						AssetTagTable.INSTANCE.tagId,
						AssetTagTable.INSTANCE.name
					).from(
						AssetTagTable.INSTANCE
					).innerJoinON(
						AssetEntries_AssetTagsTable.INSTANCE,
						AssetTagTable.INSTANCE.tagId.eq(
							AssetEntries_AssetTagsTable.INSTANCE.tagId)
					).innerJoinON(
						AssetEntryTable.INSTANCE,
						AssetEntries_AssetTagsTable.INSTANCE.entryId.eq(
							AssetEntryTable.INSTANCE.entryId)
					);

					for (Object[] values :
							(List<Object[]>)_assetTagLocalService.dslQuery(
								dslQuery, false)) {

						Map<Long, List<Object[]>> assetTagObjectsLists =
							localAssetTagObjectsListsMap.computeIfAbsent(
								(Long)values[0], key -> new HashMap<>());

						List<Object[]> assetTagObjectsList =
							assetTagObjectsLists.computeIfAbsent(
								(Long)values[1], key -> new ArrayList<>());

						assetTagObjectsList.add(
							new Object[] {values[2], values[3]});
					}

					return localAssetTagObjectsListsMap;
				});

		if (assetTagObjectsListsMap == null) {
			List<AssetTag> assetTags = _assetTagLocalService.getTags(
				classNameId, classPK);

			if (assetTags.isEmpty()) {
				return null;
			}

			List<Object[]> assetTagObjectsList = new ArrayList<>(
				assetTags.size());

			for (AssetTag assetTag : assetTags) {
				assetTagObjectsList.add(
					new Object[] {assetTag.getTagId(), assetTag.getName()});
			}

			return assetTagObjectsList;
		}

		Map<Long, List<Object[]>> assetTagObjectsLists =
			assetTagObjectsListsMap.get(classNameId);

		if (assetTagObjectsLists == null) {
			return null;
		}

		return assetTagObjectsLists.get(classPK);
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}