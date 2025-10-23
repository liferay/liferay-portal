/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.rest.dto.v1_0.Origin;
import com.liferay.exportimport.rest.dto.v1_0.ReportEntry;
import com.liferay.exportimport.rest.dto.v1_0.Scope;
import com.liferay.exportimport.rest.dto.v1_0.Status;
import com.liferay.exportimport.rest.dto.v1_0.Type;
import com.liferay.exportimport.rest.internal.odata.entity.v1_0.ReportEntryEntityModel;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.resource.v1_0.ReportEntryResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.Collections;
import java.util.Locale;
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

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-35914")) {

			throw new NotFoundException();
		}

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(importProcessId);

		PermissionUtil.checkPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

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
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toReportEntry(
				_exportImportReportEntryLocalService.getExportImportReportEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ReportEntry getReportEntry(Long reportEntryId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-35914")) {

			throw new NotFoundException();
		}

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.getExportImportReportEntry(
				reportEntryId);

		PermissionUtil.checkPermission(
			contextCompany.getCompanyId(),
			exportImportReportEntry.getGroupId());

		return _toReportEntry(exportImportReportEntry);
	}

	private String _getGroupName(Group group, Locale locale) {
		try {
			String descriptiveName = group.getDescriptiveName(locale);

			if (Validator.isNotNull(descriptiveName)) {
				return descriptiveName;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return group.getName(locale);
	}

	private Scope.Type _getGroupType(Group group) {
		if (group.isDepot()) {
			return Scope.Type.ASSET_LIBRARY;
		}
		else if (group.isSite()) {
			return Scope.Type.SITE;
		}
		else if (group.isCMS()) {
			return Scope.Type.SPACE;
		}

		return null;
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

	private Scope _getScope(Group group) {
		if (group == null) {
			return null;
		}

		return new Scope() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setKey(group::getGroupKey);
				setLabel(
					() -> _getGroupName(
						group, contextAcceptLanguage.getPreferredLocale()));
				setType(() -> _getGroupType(group));
			}
		};
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

	private String _getTypeLabel(int type) {
		if (type == ExportImportReportEntryConstants.TYPE_EMPTY) {
			return _language.get(
				contextAcceptLanguage.getPreferredLocale(), "empty");
		}
		else if (type == ExportImportReportEntryConstants.TYPE_ERROR) {
			return _language.get(
				contextAcceptLanguage.getPreferredLocale(), "error");
		}

		return null;
	}

	private String _toModelName(String modelName) {
		String modelResourceKey = "model.resource." + modelName;

		String value = _language.get(
			contextAcceptLanguage.getPreferredLocale(), modelResourceKey);

		if (!StringUtil.equals(modelResourceKey, value)) {
			return value;
		}

		return _language.get(
			contextAcceptLanguage.getPreferredLocale(), modelName);
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
			ExportImportReportEntry exportImportReportEntry)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportReportEntry.getExportImportConfigurationId());

		Group group = _groupLocalService.getGroup(
			exportImportReportEntry.getGroupId());

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
						"errorStackTrace",
						nestedField ->
							exportImportReportEntry.getErrorStacktrace()));
				setId(exportImportReportEntry::getExportImportReportEntryId);
				setModelName(
					() -> _toModelName(exportImportReportEntry.getModelName()));
				setOrigin(() -> _toOrigin(exportImportReportEntry.getOrigin()));
				setScope(() -> _getScope(group));
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
						_getTypeLabel(type)));
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReportEntryResourceImpl.class);

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
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}