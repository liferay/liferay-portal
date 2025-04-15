/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.dynamic.data.lists.constants.DDLConstants;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
	service = ExportImportPortletPreferencesProcessor.class
)
public class DDLDisplayExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return null;
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(_capability);
	}

	@Override
	public boolean isPublishDisplayedContent() {
		return false;
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			return portletPreferences;
		}

		try {
			portletDataContext.addPortletPermissions(
				DDLConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw new PortletDataException(
				"Unable to export portlet permissions", portalException);
		}

		String portletId = portletDataContext.getPortletId();

		String recordSetId = portletPreferences.getValue("recordSetId", null);

		if (recordSetId == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No record set ID found in preferences of portlet " +
						portletId);
			}

			return portletPreferences;
		}

		long groupId = GetterUtil.getLong(
			portletPreferences.getValue("groupId", StringPool.BLANK));

		if (groupId == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No group ID found in preferences of portlet " + portletId);
			}

			return portletPreferences;
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No group found with group ID " + groupId);
			}

			return portletPreferences;
		}

		if (ExportImportThreadLocal.isStagingInProcess() &&
			!group.isStagedPortlet(DDLPortletKeys.DYNAMIC_DATA_LISTS)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Dynamic data lists are not staged in group " +
						group.getName());
			}

			return portletPreferences;
		}

		long scopeGroupId = portletDataContext.getScopeGroupId();

		if (groupId != scopeGroupId) {
			portletDataContext.setScopeGroupId(groupId);
		}

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.fetchRecordSet(
			groupId, portletPreferences.getValue("recordSetKey", null));

		if (ddlRecordSet == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Portlet ", portletId,
						" references invalid record set ID ", recordSetId));
			}

			portletDataContext.setScopeGroupId(scopeGroupId);

			return portletPreferences;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, portletId, ddlRecordSet);

		boolean exportModel = _isExportModel(
			portletDataContext, DDLRecord.class.getName());

		if (exportModel) {
			try {
				ActionableDynamicQuery actionableDynamicQuery =
					_ddlRecordStagedModelRepository.
						getExportActionableDynamicQuery(portletDataContext);

				ActionableDynamicQuery.AddCriteriaMethod addCriteriaMethod =
					actionableDynamicQuery.getAddCriteriaMethod();

				actionableDynamicQuery.setAddCriteriaMethod(
					dynamicQuery -> {
						addCriteriaMethod.addCriteria(dynamicQuery);

						Property property = PropertyFactoryUtil.forName(
							"recordSetId");

						dynamicQuery.add(
							property.eq(ddlRecordSet.getRecordSetId()));
					});

				actionableDynamicQuery.setGroupId(ddlRecordSet.getGroupId());
				actionableDynamicQuery.setPerformActionMethod(
					(DDLRecord ddlRecord) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext, portletId, ddlRecord));

				actionableDynamicQuery.performActions();
			}
			catch (PortalException portalException) {
				throw new PortletDataException(
					"Unable to export referenced records", portalException);
			}
		}

		_exportReferenceDDMTemplate(
			portletDataContext, portletId,
			GetterUtil.getLong(
				portletPreferences.getValue("displayDDMTemplateId", null)));
		_exportReferenceDDMTemplate(
			portletDataContext, portletId,
			GetterUtil.getLong(
				portletPreferences.getValue("formDDMTemplateId", null)));

		portletDataContext.setScopeGroupId(scopeGroupId);

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			portletDataContext.importPortletPermissions(
				DDLConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw new PortletDataException(
				"Unable to export portlet permissions", portalException);
		}

		long groupId = GetterUtil.getLong(
			portletPreferences.getValue("groupId", null));

		if ((groupId == portletDataContext.getCompanyGroupId()) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			portletDataContext.setScopeType("company");
		}

		String recordSetId = portletPreferences.getValue("recordSetId", null);

		groupId = MapUtil.getLong(
			(Map<String, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDLRecordSet.class + ".groupId"),
			recordSetId,
			MapUtil.getLong(
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Group.class),
				groupId, groupId));

		long scopeGroupId = portletDataContext.getScopeGroupId();

		portletDataContext.setScopeGroupId(groupId);

		if (Validator.isNotNull(recordSetId) && (groupId > 0)) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if (group == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("No group found with group ID " + groupId);
				}

				return portletPreferences;
			}

			if (!ExportImportThreadLocal.isStagingInProcess() ||
				group.isStagedPortlet(DDLPortletKeys.DYNAMIC_DATA_LISTS)) {

				try {
					portletPreferences.setValue(
						"groupId", String.valueOf(groupId));
					portletPreferences.setValue(
						"recordSetId",
						String.valueOf(
							MapUtil.getLong(
								(Map<Long, Long>)
									portletDataContext.getNewPrimaryKeysMap(
										DDLRecordSet.class),
								GetterUtil.getLong(recordSetId))));
				}
				catch (ReadOnlyException readOnlyException) {
					throw new PortletDataException(
						"Unable to update portlet preferences during import",
						readOnlyException);
				}
			}
		}

		Map<Long, Long> ddmTemplateIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMTemplate.class);

		long displayDDMTemplateId = GetterUtil.getLong(
			portletPreferences.getValue("displayDDMTemplateId", null));
		long formDDMTemplateId = GetterUtil.getLong(
			portletPreferences.getValue("formDDMTemplateId", null));

		try {
			portletPreferences.setValue(
				"displayDDMTemplateId",
				String.valueOf(
					MapUtil.getLong(
						ddmTemplateIds, displayDDMTemplateId,
						displayDDMTemplateId)));
			portletPreferences.setValue(
				"formDDMTemplateId",
				String.valueOf(
					MapUtil.getLong(
						ddmTemplateIds, formDDMTemplateId, formDDMTemplateId)));
		}
		catch (ReadOnlyException readOnlyException) {
			throw new PortletDataException(
				"Unable to update portlet preferences during import",
				readOnlyException);
		}

		portletDataContext.setScopeGroupId(scopeGroupId);

		return portletPreferences;
	}

	private void _exportReferenceDDMTemplate(
			PortletDataContext portletDataContext, String portletId,
			long ddmTemplateId)
		throws PortletDataException {

		if (ddmTemplateId == 0) {
			return;
		}

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchDDMTemplate(
			ddmTemplateId);

		if (ddmTemplate == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to export referenced template " + ddmTemplateId);
			}

			return;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, portletId, ddmTemplate);
	}

	private boolean _isExportModel(
		PortletDataContext portletDataContext, String className) {

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		boolean exportModel = MapUtil.getBoolean(parameterMap, className);

		if (exportModel) {
			return true;
		}

		return MapUtil.getBoolean(
			parameterMap,
			className + StringPool.POUND +
				StagedModelType.REFERRER_CLASS_NAME_ALL,
			true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDLDisplayExportImportPortletPreferencesProcessor.class);

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	private Capability _capability;

	@Reference
	private DDLRecordSetLocalService _ddlRecordSetLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.lists.model.DDLRecord)"
	)
	private StagedModelRepository<DDLRecord> _ddlRecordStagedModelRepository;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}