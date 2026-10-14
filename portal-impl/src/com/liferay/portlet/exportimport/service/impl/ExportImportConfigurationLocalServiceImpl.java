/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.impl;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.exportimport.service.base.ExportImportConfigurationLocalServiceBaseImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Daniel Kocsis
 * @author Akos Thurzo
 */
public class ExportImportConfigurationLocalServiceImpl
	extends ExportImportConfigurationLocalServiceBaseImpl {

	@CTAware
	@Override
	public ExportImportConfiguration addDraftExportImportConfiguration(
			long userId, int type, Map<String, Serializable> settingsMap)
		throws PortalException {

		return exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(
				userId, GetterUtil.getString(settingsMap.get("portletId")),
				type, settingsMap);
	}

	@CTAware
	@Override
	public ExportImportConfiguration addDraftExportImportConfiguration(
			long userId, String name, int type,
			Map<String, Serializable> settingsMap)
		throws PortalException {

		long groupId = GetterUtil.getLong(settingsMap.get("sourceGroupId"));

		if ((type == ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT) ||
			(type == ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET)) {

			groupId = GetterUtil.getLong(settingsMap.get("targetGroupId"));
		}

		return exportImportConfigurationLocalService.
			addExportImportConfiguration(
				userId, groupId, name, StringPool.BLANK, type, settingsMap,
				WorkflowConstants.STATUS_DRAFT, new ServiceContext());
	}

	@CTAware
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration addExportImportConfiguration(
			long userId, long groupId, String name, String description,
			int type, Map<String, Serializable> settingsMap, int status,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		long exportImportConfigurationId = counterLocalService.increment();

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.create(
				exportImportConfigurationId);

		exportImportConfiguration.setGroupId(groupId);
		exportImportConfiguration.setCompanyId(user.getCompanyId());
		exportImportConfiguration.setUserId(userId);
		exportImportConfiguration.setUserName(user.getFullName());
		exportImportConfiguration.setName(name);
		exportImportConfiguration.setDescription(description);
		exportImportConfiguration.setType(type);

		if (settingsMap != null) {
			exportImportConfiguration.setSettings(
				JSONFactoryUtil.serialize(settingsMap));
		}

		exportImportConfiguration.setStatus(status);
		exportImportConfiguration.setStatusByUserId(userId);
		exportImportConfiguration.setStatusByUserName(user.getScreenName());
		exportImportConfiguration.setStatusDate(new Date());

		return exportImportConfigurationPersistence.update(
			exportImportConfiguration);
	}

	@CTAware
	@Override
	public ExportImportConfiguration addExportImportConfiguration(
			long userId, long groupId, String name, String description,
			int type, Map<String, Serializable> settingsMap,
			ServiceContext serviceContext)
		throws PortalException {

		return exportImportConfigurationLocalService.
			addExportImportConfiguration(
				userId, groupId, name, description, type, settingsMap,
				WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@CTAware
	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ExportImportConfiguration deleteExportImportConfiguration(
		ExportImportConfiguration exportImportConfiguration) {

		exportImportConfigurationPersistence.remove(exportImportConfiguration);

		return exportImportConfiguration;
	}

	@CTAware
	@Override
	public ExportImportConfiguration deleteExportImportConfiguration(
			long exportImportConfigurationId)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		return exportImportConfigurationLocalService.
			deleteExportImportConfiguration(exportImportConfiguration);
	}

	@CTAware
	@Override
	public void deleteExportImportConfigurations(long groupId) {
		List<ExportImportConfiguration> exportImportConfigurations =
			exportImportConfigurationPersistence.findByGroupId(groupId);

		for (ExportImportConfiguration exportImportConfiguration :
				exportImportConfigurations) {

			exportImportConfigurationLocalService.
				deleteExportImportConfiguration(exportImportConfiguration);
		}
	}

	@Override
	public List<ExportImportConfiguration> getExportImportConfigurations(
			Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<ExportImportConfiguration> exportImportConfigurations =
			new ArrayList<>(documents.size());

		for (Document document : documents) {
			long exportImportConfigurationId = GetterUtil.getLong(
				document.get("exportImportConfigurationId"));

			ExportImportConfiguration exportImportConfiguration =
				exportImportConfigurationLocalService.
					getExportImportConfiguration(exportImportConfigurationId);

			if (exportImportConfiguration == null) {
				exportImportConfigurations = null;

				Indexer<ExportImportConfiguration> indexer =
					IndexerRegistryUtil.getIndexer(
						ExportImportConfiguration.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (exportImportConfigurations != null) {
				exportImportConfigurations.add(exportImportConfiguration);
			}
		}

		return exportImportConfigurations;
	}

	@Override
	public List<ExportImportConfiguration> getExportImportConfigurations(
		long groupId, int type) {

		return exportImportConfigurationPersistence.findByG_T_S(
			groupId, type, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public List<ExportImportConfiguration> getExportImportConfigurations(
		long groupId, int type, int start, int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return exportImportConfigurationPersistence.findByG_T_S(
			groupId, type, WorkflowConstants.STATUS_APPROVED, start, end,
			orderByComparator);
	}

	@Override
	public List<ExportImportConfiguration> getExportImportConfigurations(
		long companyId, long groupId, String keywords, int type, int start,
		int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return exportImportConfigurationFinder.findByKeywords(
			companyId, groupId, keywords, type,
			WorkflowConstants.STATUS_APPROVED, start, end, orderByComparator);
	}

	@Override
	public int getExportImportConfigurationsCount(long groupId, int type) {
		return exportImportConfigurationPersistence.countByG_T_S(
			groupId, type, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getExportImportConfigurationsCount(
		long companyId, long groupId, String keywords, int type) {

		return exportImportConfigurationFinder.countByKeywords(
			companyId, groupId, keywords, type,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration moveExportImportConfigurationToTrash(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		return exportImportConfigurationPersistence.findByPrimaryKey(
			exportImportConfigurationId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration restoreExportImportConfigurationFromTrash(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		return exportImportConfigurationPersistence.findByPrimaryKey(
			exportImportConfigurationId);
	}

	@Override
	public BaseModelSearchResult<ExportImportConfiguration>
			searchExportImportConfigurations(
				long companyId, long groupId, int type, String keywords,
				int start, int end, Sort sort)
		throws PortalException {

		String description = null;
		String name = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			description = keywords;
			name = keywords;
		}
		else {
			andOperator = true;
		}

		return searchExportImportConfigurations(
			companyId, groupId, type, name, description, andOperator, start,
			end, sort);
	}

	@Override
	public BaseModelSearchResult<ExportImportConfiguration>
			searchExportImportConfigurations(
				long companyId, long groupId, int type, String name,
				String description, boolean andSearch, int start, int end,
				Sort sort)
		throws PortalException {

		Indexer<ExportImportConfiguration> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				ExportImportConfiguration.class);

		SearchContext searchContext = buildSearchContext(
			companyId, groupId, type, name, description, andSearch, start, end,
			sort);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<ExportImportConfiguration> exportImportConfigurations =
				exportImportConfigurationLocalService.
					getExportImportConfigurations(hits);

			if (exportImportConfigurations != null) {
				return new BaseModelSearchResult<>(
					exportImportConfigurations, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration updateExportImportConfiguration(
			long userId, long exportImportConfigurationId, String name,
			String description, Map<String, Serializable> settingsMap,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		exportImportConfiguration.setUserId(userId);
		exportImportConfiguration.setUserName(user.getFullName());
		exportImportConfiguration.setName(name);
		exportImportConfiguration.setDescription(description);

		if (settingsMap != null) {
			exportImportConfiguration.setSettings(
				JSONFactoryUtil.serialize(settingsMap));
		}

		return exportImportConfigurationPersistence.update(
			exportImportConfiguration);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration updateStatus(
			long userId, long exportImportConfigurationId, int status)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		exportImportConfiguration.setStatus(status);
		exportImportConfiguration.setStatusByUserId(userId);
		exportImportConfiguration.setStatusByUserName(user.getScreenName());
		exportImportConfiguration.setStatusDate(new Date());

		return exportImportConfigurationPersistence.update(
			exportImportConfiguration);
	}

	protected SearchContext buildSearchContext(
		long companyId, long groupId, int type, String name, String description,
		boolean andSearch, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(andSearch);
		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"description", description
			).put(
				"groupId", groupId
			).put(
				"name", name
			).put(
				"type", type
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}