/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.rest.dto.v1_0.Origin;
import com.liferay.exportimport.rest.dto.v1_0.ReportEntry;
import com.liferay.exportimport.rest.dto.v1_0.Status;
import com.liferay.exportimport.rest.dto.v1_0.Type;
import com.liferay.exportimport.rest.internal.odata.entity.v1_0.ReportEntryEntityModel;
import com.liferay.exportimport.rest.internal.util.BackgroundTaskUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.resource.v1_0.ReportEntryResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.scope.Scope;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/report-entry.properties",
	scope = ServiceScope.PROTOTYPE, service = ReportEntryResource.class
)
public class ReportEntryResourceImpl extends BaseReportEntryResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ReportEntry> getImportProcessReportEntriesPage(
			Long importProcessId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(importProcessId);

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_IMPORT_TASK_EXECUTOR);

		return _getReportEntriesPage(
			backgroundTask, filter, pagination, search, sorts);
	}

	@Override
	public Page<ReportEntry> getPublishProcessReportEntriesPage(
			Long publishProcessId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		PermissionUtil.checkPublishPermission(backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_PUBLISH_TASK_EXECUTOR);

		return _getReportEntriesPage(
			backgroundTask, filter, pagination, search, sorts);
	}

	@Override
	public ReportEntry getReportEntry(Long reportEntryId) throws Exception {
		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.getExportImportReportEntry(
				reportEntryId);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportReportEntry.getExportImportConfigurationId());

		int exportImportConfigurationType = exportImportConfiguration.getType();

		if ((exportImportConfigurationType ==
				ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT) ||
			(exportImportConfigurationType ==
				ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET)) {

			PermissionUtil.checkImportPermission(
				contextCompany.getCompanyId(),
				exportImportConfiguration.getGroupId());
		}
		else {
			PermissionUtil.checkPublishPermission(
				exportImportConfiguration.getGroupId());
		}

		return _toReportEntry(
			exportImportConfiguration, exportImportReportEntry);
	}

	private String _getOriginLabel(int origin) {
		if (origin == ExportImportReportEntryConstants.ORIGIN_BATCH) {
			return _language.get(
				contextAcceptLanguage.getPreferredLocale(), "batch");
		}
		else if (origin == ExportImportReportEntryConstants.ORIGIN_STAGING) {
			return _language.get(
				contextAcceptLanguage.getPreferredLocale(), "staging");
		}

		return null;
	}

	private Page<ReportEntry> _getReportEntriesPage(
			BackgroundTask backgroundTask, Filter filter, Pagination pagination,
			String search, Sort[] sorts)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				MapUtil.getLong(taskContextMap, "exportImportConfigurationId"));

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"exportImportConfigurationId_long",
						MapUtil.getString(
							taskContextMap, "exportImportConfigurationId")),
					BooleanClauseOccur.MUST);
			},
			filter, ExportImportReportEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setLocale(
					contextAcceptLanguage.getPreferredLocale());
			},
			sorts,
			document -> _toReportEntry(
				exportImportConfiguration,
				_exportImportReportEntryLocalService.getExportImportReportEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private String _getStatusLabel(int status) {
		if (status == ExportImportReportEntryConstants.STATUS_RESOLVED) {
			return _language.get(
				contextAcceptLanguage.getPreferredLocale(), "resolved");
		}
		else if (status == ExportImportReportEntryConstants.STATUS_UNRESOLVED) {
			return _language.get(
				contextAcceptLanguage.getPreferredLocale(), "not-resolved");
		}

		return null;
	}

	private Origin _toOrigin(int origin) {
		return new Origin() {
			{
				setCode(() -> origin);
				setLabel(() -> _getOriginLabel(origin));
			}
		};
	}

	private ReportEntry _toReportEntry(
		ExportImportConfiguration exportImportConfiguration,
		ExportImportReportEntry exportImportReportEntry) {

		return new ReportEntry() {
			{
				setClassExternalReferenceCode(
					exportImportReportEntry::getClassExternalReferenceCode);
				setClassPK(exportImportReportEntry::getClassPK);
				setConfigurationId(
					exportImportReportEntry::getExportImportConfigurationId);
				setCreator(
					() -> CreatorUtil.toCreator(
						null, _portal,
						_userLocalService.fetchUser(
							exportImportConfiguration.getUserId())));
				setDateCreated(exportImportReportEntry::getCreateDate);
				setDateModified(exportImportReportEntry::getModifiedDate);
				setErrorMessage(exportImportReportEntry::getErrorMessage);
				setErrorStacktrace(
					() -> NestedFieldsSupplier.supply(
						"errorStacktrace",
						nestedField ->
							exportImportReportEntry.getErrorStacktrace()));
				setId(exportImportReportEntry::getExportImportReportEntryId);
				setModelName(
					() -> _language.get(
						contextAcceptLanguage.getPreferredLocale(),
						exportImportReportEntry.getModelNameLanguageKey()));
				setOrigin(() -> _toOrigin(exportImportReportEntry.getOrigin()));
				setScope(
					() -> Scope.of(
						exportImportReportEntry.getGroupId(),
						contextAcceptLanguage.getPreferredLocale()));
				setStatus(() -> _toStatus(exportImportReportEntry.getStatus()));
				setType(() -> _toType(exportImportReportEntry.getType()));
			}
		};
	}

	private Status _toStatus(int status) {
		return new Status() {
			{
				setCode(() -> status);
				setLabel(
					() -> _language.get(
						contextAcceptLanguage.getPreferredLocale(),
						_getStatusLabel(status)));
			}
		};
	}

	private Type _toType(int type) {
		return new Type() {
			{
				setCode(() -> type);
				setLabel(
					() -> _language.get(
						contextAcceptLanguage.getPreferredLocale(),
						ExportImportReportEntryConstants.getTypeLabel(type)));
			}
		};
	}

	private static final String[] _CLASS_NAMES_IMPORT_TASK_EXECUTOR = {
		BackgroundTaskExecutorNames.LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR,
		BackgroundTaskExecutorNames.PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR
	};

	private static final String[] _CLASS_NAMES_PUBLISH_TASK_EXECUTOR = {
		BackgroundTaskExecutorNames.LAYOUT_STAGING_BACKGROUND_TASK_EXECUTOR
	};

	private static final EntityModel _entityModel =
		new ReportEntryEntityModel();

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}