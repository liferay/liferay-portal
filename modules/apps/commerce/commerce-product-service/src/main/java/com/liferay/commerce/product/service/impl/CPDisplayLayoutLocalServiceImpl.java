/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.CPDisplayLayoutEntryException;
import com.liferay.commerce.product.exception.CPDisplayLayoutEntryUuidException;
import com.liferay.commerce.product.internal.util.CPDefinitionLocalServiceCircularDependencyUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.service.base.CPDisplayLayoutLocalServiceBaseImpl;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateEntryException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Layout;
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
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPDisplayLayout",
	service = AopService.class
)
public class CPDisplayLayoutLocalServiceImpl
	extends CPDisplayLayoutLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDisplayLayout addCPDisplayLayout(
			long userId, long groupId, Class<?> clazz, long classPK,
			String layoutPageTemplateEntryUuid, String layoutUuid)
		throws PortalException {

		_validate(groupId, classPK, layoutPageTemplateEntryUuid, layoutUuid);

		long classNameId = _classNameLocalService.getClassNameId(clazz);

		CPDisplayLayout oldCPDisplayLayout =
			cpDisplayLayoutPersistence.fetchByG_C_C(
				groupId, classNameId, classPK);

		if ((clazz == CPDefinition.class) &&
			CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				classPK)) {

			try {
				CPDefinition newCPDefinition =
					CPDefinitionLocalServiceCircularDependencyUtil.
						copyCPDefinition(classPK);

				classPK = newCPDefinition.getCPDefinitionId();
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}

			oldCPDisplayLayout = cpDisplayLayoutPersistence.fetchByG_C_C(
				groupId, classNameId, classPK);
		}

		if (oldCPDisplayLayout != null) {
			oldCPDisplayLayout.setLayoutPageTemplateEntryUuid(
				layoutPageTemplateEntryUuid);

			return cpDisplayLayoutPersistence.update(oldCPDisplayLayout);
		}

		long cpDisplayLayoutId = counterLocalService.increment();

		CPDisplayLayout cpDisplayLayout = createCPDisplayLayout(
			cpDisplayLayoutId);

		cpDisplayLayout.setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		cpDisplayLayout.setCompanyId(user.getCompanyId());

		cpDisplayLayout.setClassNameId(classNameId);
		cpDisplayLayout.setClassPK(classPK);
		cpDisplayLayout.setLayoutPageTemplateEntryUuid(
			layoutPageTemplateEntryUuid);
		cpDisplayLayout.setLayoutUuid(layoutUuid);

		return cpDisplayLayoutPersistence.update(cpDisplayLayout);
	}

	@Override
	public void deleteCPDisplayLayouts(Class<?> clazz, long classPK) {
		List<CPDisplayLayout> cpDisplayLayouts =
			cpDisplayLayoutPersistence.findByC_C(
				_classNameLocalService.getClassNameId(clazz), classPK);

		for (CPDisplayLayout cpDisplayLayout : cpDisplayLayouts) {
			cpDisplayLayoutLocalService.deleteCPDisplayLayout(cpDisplayLayout);
		}
	}

	@Override
	public CPDisplayLayout fetchCPDisplayLayout(
		long groupId, Class<?> clazz, long classPK) {

		return cpDisplayLayoutPersistence.fetchByG_C_C(
			groupId, _classNameLocalService.getClassNameId(clazz), classPK);
	}

	@Override
	public List<CPDisplayLayout>
		getCPDisplayLayoutsByGroupIdAndLayoutPageTemplateEntryUuid(
			long groupId, String layoutPageTemplateEntryUuid) {

		return cpDisplayLayoutPersistence.findByG_LPTEU(
			groupId, layoutPageTemplateEntryUuid);
	}

	@Override
	public List<CPDisplayLayout> getCPDisplayLayoutsByGroupIdAndLayoutUuid(
		long groupId, String layoutUuid) {

		return cpDisplayLayoutPersistence.findByG_L(groupId, layoutUuid);
	}

	@Override
	public BaseModelSearchResult<CPDisplayLayout> searchCPDisplayLayout(
			long companyId, long groupId, String className, Integer type,
			String keywords, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupId, className, type, keywords, start, end, sort);

		return _searchCPDisplayLayout(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDisplayLayout updateCPDisplayLayout(
			long cpDisplayLayoutId, long classPK,
			String layoutPageTemplateEntryUuid, String layoutUuid)
		throws PortalException {

		CPDisplayLayout cpDisplayLayout =
			cpDisplayLayoutPersistence.findByPrimaryKey(cpDisplayLayoutId);

		_validate(
			cpDisplayLayout.getGroupId(), cpDisplayLayout.getClassPK(),
			layoutPageTemplateEntryUuid, layoutUuid);

		cpDisplayLayout.setClassPK(classPK);
		cpDisplayLayout.setLayoutPageTemplateEntryUuid(
			layoutPageTemplateEntryUuid);
		cpDisplayLayout.setLayoutUuid(layoutUuid);

		return cpDisplayLayoutPersistence.update(cpDisplayLayout);
	}

	private SearchContext _buildSearchContext(
		long companyId, long groupId, String className, Integer type,
		String keywords, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				"entryModelClassName", className
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).put(
				"searchFilterEnabled", true
			).put(
				"type", type
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {groupId});

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CPDisplayLayout> _getCPDisplayLayouts(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		return TransformUtil.transform(
			documents,
			document -> {
				long cpDisplayLayoutId = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				CPDisplayLayout cpDisplayLayout = fetchCPDisplayLayout(
					cpDisplayLayoutId);

				if (cpDisplayLayout != null) {
					return cpDisplayLayout;
				}

				Indexer<CPDisplayLayout> indexer =
					IndexerRegistryUtil.getIndexer(CPDisplayLayout.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());

				return null;
			});
	}

	private BaseModelSearchResult<CPDisplayLayout> _searchCPDisplayLayout(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CPDisplayLayout> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPDisplayLayout.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CPDisplayLayout> cpDisplayLayouts = _getCPDisplayLayouts(hits);

			if (cpDisplayLayouts != null) {
				return new BaseModelSearchResult<>(
					cpDisplayLayouts, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private void _validate(
			long groupId, long classPK, String layoutPageTemplateEntryUuid,
			String layoutUuid)
		throws PortalException {

		if (classPK <= 0) {
			throw new CPDisplayLayoutEntryException();
		}

		if (Validator.isNull(layoutPageTemplateEntryUuid) &&
			Validator.isNull(layoutUuid)) {

			throw new CPDisplayLayoutEntryUuidException();
		}

		if (Validator.isNotNull(layoutPageTemplateEntryUuid)) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByUuidAndGroupId(
						layoutPageTemplateEntryUuid, groupId);

			if (layoutPageTemplateEntry == null) {
				throw new NoSuchPageTemplateEntryException();
			}
		}

		if (Validator.isNotNull(layoutUuid)) {
			Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				layoutUuid, groupId, false);

			if (layout == null) {
				layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
					layoutUuid, groupId, true);
			}

			if (layout == null) {
				throw new NoSuchLayoutException();
			}
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}