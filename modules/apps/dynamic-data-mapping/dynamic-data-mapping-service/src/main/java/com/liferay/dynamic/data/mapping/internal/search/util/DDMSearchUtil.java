/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.search.util;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Rafael Praxedes
 */
public class DDMSearchUtil {

	public static SearchContext buildStructureSearchContext(
		DDMPermissionSupport ddmPermissionSupport, long companyId,
		long[] groupIds, long userId, long classNameId, Long classPK,
		String name, String description, String storageType, Integer type,
		int status, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.CLASS_NAME_ID, classNameId);
		searchContext.setAttribute(Field.CLASS_PK, classPK);
		searchContext.setAttribute(Field.DESCRIPTION, description);
		searchContext.setAttribute(Field.NAME, name);
		searchContext.setAttribute(Field.STATUS, status);

		try {
			searchContext.setAttribute(
				"resourcePermissionName",
				ddmPermissionSupport.getStructureModelResourceName(
					classNameId));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		searchContext.setAttribute("storageType", storageType);
		searchContext.setAttribute("type", type);
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);
		searchContext.setStart(start);

		if (userId > 0) {
			searchContext.setUserId(userId);
		}

		if (orderByComparator != null) {
			searchContext.setSorts(
				_getSortsFromComparator(
					searchContext.getLanguageId(), orderByComparator));
		}

		return searchContext;
	}

	public static SearchContext buildStructureSearchContext(
		DDMPermissionSupport ddmPermissionSupport, long companyId,
		long[] groupIds, long classNameId, Long classPK, String name,
		String description, String storageType, Integer type, int status,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return buildStructureSearchContext(
			ddmPermissionSupport, companyId, groupIds, 0, classNameId, classPK,
			name, description, storageType, type, status, start, end,
			orderByComparator);
	}

	public static SearchContext buildTemplateSearchContext(
		DDMPermissionSupport ddmPermissionSupport, long companyId, long groupId,
		long userId, long classNameId, long classPK, long resourceClassNameId,
		String name, String description, String type, String mode,
		String language, int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return buildTemplateSearchContext(
			ddmPermissionSupport, companyId, new long[] {groupId}, userId,
			new long[] {classNameId}, new long[] {classPK}, resourceClassNameId,
			name, description, type, mode, language, status, start, end,
			orderByComparator);
	}

	public static SearchContext buildTemplateSearchContext(
		DDMPermissionSupport ddmPermissionSupport, long companyId, long groupId,
		long classNameId, long classPK, long resourceClassNameId, String name,
		String description, String type, String mode, String language,
		int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return buildTemplateSearchContext(
			ddmPermissionSupport, companyId, new long[] {groupId},
			new long[] {classNameId}, new long[] {classPK}, resourceClassNameId,
			name, description, type, mode, language, status, start, end,
			orderByComparator);
	}

	public static SearchContext buildTemplateSearchContext(
		DDMPermissionSupport ddmPermissionSupport, long companyId,
		long[] groupIds, long userId, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.DESCRIPTION, description);
		searchContext.setAttribute(Field.NAME, name);
		searchContext.setAttribute(Field.STATUS, status);
		searchContext.setAttribute("classNameIds", classNameIds);
		searchContext.setAttribute("classPKs", classPKs);
		searchContext.setAttribute("language", language);
		searchContext.setAttribute("mode", mode);
		searchContext.setAttribute("resourceClassNameId", resourceClassNameId);

		try {
			searchContext.setAttribute(
				"resourcePermissionName",
				ddmPermissionSupport.getTemplateModelResourceName(
					resourceClassNameId));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		searchContext.setAttribute("type", type);
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);
		searchContext.setStart(start);

		if (userId > 0) {
			searchContext.setUserId(userId);
		}

		if (orderByComparator != null) {
			searchContext.setSorts(
				_getSortsFromComparator(
					searchContext.getLanguageId(), orderByComparator));
		}

		return searchContext;
	}

	public static SearchContext buildTemplateSearchContext(
		DDMPermissionSupport ddmPermissionSupport, long companyId,
		long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return buildTemplateSearchContext(
			ddmPermissionSupport, companyId, groupIds, 0, classNameIds,
			classPKs, resourceClassNameId, name, description, type, mode,
			language, status, start, end, orderByComparator);
	}

	public static <T> List<T> doSearch(
		SearchContext searchContext, Class<T> modelClass,
		UnsafeFunction<Long, T, ? extends PortalException>
			getModelUnsafeFunction) {

		try {
			Indexer<T> indexer = IndexerRegistryUtil.getIndexer(modelClass);

			Hits hits = indexer.search(searchContext);

			return TransformUtil.transformToList(
				hits.getDocs(),
				document -> getModelUnsafeFunction.apply(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return Collections.emptyList();
	}

	public static <T> int doSearchCount(
		SearchContext searchContext, Class<T> modelClass) {

		try {
			Indexer<T> indexer = IndexerRegistryUtil.getIndexer(modelClass);

			return (int)indexer.searchCount(searchContext);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return 0;
	}

	private static Sort[] _getSortsFromComparator(
		String languageId,
		OrderByComparator<? extends BaseModel<?>> orderByComparator) {

		return TransformUtil.transform(
			orderByComparator.getOrderByFields(),
			orderByFieldName -> {
				String fieldName = _fieldNameOrderByCols.getOrDefault(
					orderByFieldName, orderByFieldName);

				if (StringUtil.equals(fieldName, Field.NAME)) {
					fieldName = Field.getSortableFieldName(
						Field.getLocalizedName(languageId, "localized_name"));
				}

				int sortType = _fieldNameSortTypes.getOrDefault(
					fieldName, Sort.STRING_TYPE);

				return new Sort(
					fieldName, sortType, !orderByComparator.isAscending());
			},
			Sort.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(DDMSearchUtil.class);

	private static final Map<String, String> _fieldNameOrderByCols =
		HashMapBuilder.put(
			"createDate", Field.CREATE_DATE
		).put(
			"modifiedDate", Field.MODIFIED_DATE
		).put(
			"structureId", Field.ENTRY_CLASS_PK
		).put(
			"templateId", Field.ENTRY_CLASS_PK
		).build();
	private static final Map<String, Integer> _fieldNameSortTypes =
		HashMapBuilder.put(
			Field.CREATE_DATE, Sort.LONG_TYPE
		).put(
			Field.ENTRY_CLASS_PK, Sort.LONG_TYPE
		).put(
			Field.MODIFIED_DATE, Sort.LONG_TYPE
		).build();

}