/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.internal.upgrade.v1_1_0;

import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateVersionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author Inácio Nery
 */
public class KaleoProcessUpgradeProcess extends UpgradeProcess {

	public KaleoProcessUpgradeProcess(
		DDLRecordSetLocalService ddlRecordSetLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		DDMStructureVersionLocalService ddmStructureVersionLocalService,
		DDMTemplateLocalService ddmTemplateLocalService,
		DDMTemplateVersionLocalService ddmTemplateVersionLocalService,
		ResourceActionLocalService resourceActionLocalService,
		ResourceActions resourceActions,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_ddlRecordSetLocalService = ddlRecordSetLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_ddmStructureVersionLocalService = ddmStructureVersionLocalService;
		_ddmTemplateLocalService = ddmTemplateLocalService;
		_ddmTemplateVersionLocalService = ddmTemplateVersionLocalService;
		_resourceActionLocalService = resourceActionLocalService;
		_resourceActions = resourceActions;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_initKaleoFormsDDMCompositeModelsResourceActions();

		_updateKaleoProcess();
		_updateKaleoProcessLink();
	}

	private String _getDDMStructureModelResourceName(DDMStructure ddmStructure)
		throws PortalException {

		return _resourceActions.getCompositeModelName(
			ddmStructure.getClassName(), DDMStructure.class.getName());
	}

	private String _getDDMTemplateModelResourceName(DDMTemplate ddmTemplate)
		throws PortalException {

		return _resourceActions.getCompositeModelName(
			ddmTemplate.getResourceClassName(), DDMTemplate.class.getName());
	}

	private Long _getNewDDMStructureId(long oldDDMStructureId)
		throws PortalException {

		Long newDDMStructureId = _ddmStructureMap.get(oldDDMStructureId);

		if (newDDMStructureId != null) {
			return newDDMStructureId;
		}

		DDMStructure oldDDMStructure = _ddmStructureLocalService.getStructure(
			oldDDMStructureId);

		ServiceContext serviceContext = new ServiceContext();

		DDMStructureVersion ddmStructureVersion =
			_ddmStructureVersionLocalService.getStructureVersion(
				oldDDMStructure.getStructureId(), oldDDMStructure.getVersion());

		serviceContext.setAttribute("status", ddmStructureVersion.getStatus());

		ModelPermissions oldDDMStructureModelPermissions =
			_getResourceModelPermissions(
				oldDDMStructure.getCompanyId(),
				_getDDMStructureModelResourceName(oldDDMStructure),
				oldDDMStructureId);

		serviceContext.setModelPermissions(oldDDMStructureModelPermissions);

		DDMStructure newDDMStructure = _ddmStructureLocalService.addStructure(
			null, oldDDMStructure.getUserId(), oldDDMStructure.getGroupId(),
			oldDDMStructure.getParentStructureId(),
			PortalUtil.getClassNameId(KaleoProcess.class),
			oldDDMStructure.getStructureKey(), oldDDMStructure.getNameMap(),
			oldDDMStructure.getDescriptionMap(), oldDDMStructure.getDDMForm(),
			oldDDMStructure.getDDMFormLayout(),
			oldDDMStructure.getStorageType(), oldDDMStructure.getType(),
			serviceContext);

		newDDMStructureId = newDDMStructure.getStructureId();

		_ddmStructureMap.put(oldDDMStructureId, newDDMStructureId);

		return newDDMStructureId;
	}

	private Long _getNewDDMTemplateId(long oldDDMTemplateId)
		throws PortalException {

		Long newDDMTemplateId = _ddmTemplateMap.get(oldDDMTemplateId);

		if (newDDMTemplateId != null) {
			return newDDMTemplateId;
		}

		DDMTemplate oldDDMTemplate = _ddmTemplateLocalService.getTemplate(
			oldDDMTemplateId);

		ServiceContext serviceContext = new ServiceContext();

		DDMTemplateVersion ddmTemplateVersion =
			_ddmTemplateVersionLocalService.getTemplateVersion(
				oldDDMTemplate.getTemplateId(), oldDDMTemplate.getVersion());

		serviceContext.setAttribute("status", ddmTemplateVersion.getStatus());

		serviceContext.setModelPermissions(
			_getResourceModelPermissions(
				oldDDMTemplate.getCompanyId(),
				_getDDMTemplateModelResourceName(oldDDMTemplate),
				oldDDMTemplateId));

		Long newDDMStructureId = _getNewDDMStructureId(
			oldDDMTemplate.getClassPK());

		Locale siteDefaultLocale = LocaleThreadLocal.getSiteDefaultLocale();

		LocaleThreadLocal.setSiteDefaultLocale(
			LocaleUtil.fromLanguageId(oldDDMTemplate.getDefaultLanguageId()));

		try {
			DDMTemplate newDDMTemplate = _ddmTemplateLocalService.addTemplate(
				null, oldDDMTemplate.getUserId(), oldDDMTemplate.getGroupId(),
				oldDDMTemplate.getClassNameId(), newDDMStructureId,
				PortalUtil.getClassNameId(KaleoProcess.class),
				oldDDMTemplate.getNameMap(), oldDDMTemplate.getDescriptionMap(),
				oldDDMTemplate.getType(), oldDDMTemplate.getMode(),
				oldDDMTemplate.getLanguage(), oldDDMTemplate.getScript(),
				serviceContext);

			newDDMTemplateId = newDDMTemplate.getTemplateId();
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);
		}

		_ddmTemplateMap.put(oldDDMTemplateId, newDDMTemplateId);

		return newDDMTemplateId;
	}

	private ModelPermissions _getResourceModelPermissions(
		long companyId, String resourceName, long primKey) {

		ModelPermissions modelPermissions = ModelPermissionsFactory.create(
			resourceName);

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(resourceName);

		for (ResourceAction resourceAction : resourceActions) {
			List<Role> roles = _resourcePermissionLocalService.getRoles(
				companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(primKey), resourceAction.getActionId());

			for (Role role : roles) {
				modelPermissions.addRolePermissions(
					role.getName(), resourceAction.getActionId());
			}
		}

		return modelPermissions;
	}

	private void _initKaleoFormsDDMCompositeModelsResourceActions()
		throws Exception {

		_resourceActions.populateModelResources(
			KaleoProcessUpgradeProcess.class.getClassLoader(),
			"/resource-actions/default.xml");
	}

	private void _updateDDLRecordSet(
			long ddlRecordSetId, Long newDDMStructureId)
		throws PortalException {

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.getRecordSet(
			ddlRecordSetId);

		ddlRecordSet.setDDMStructureId(newDDMStructureId);

		_ddlRecordSetLocalService.updateDDLRecordSet(ddlRecordSet);
	}

	private void _updateKaleoProcess() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select KaleoProcess.kaleoProcessId, ",
					"KaleoProcess.DDLRecordSetId, KaleoProcess.DDMTemplateId, ",
					"DDLRecordSet.DDMStructureId FROM KaleoProcess join ",
					"DDLRecordSet on DDLRecordSet.recordSetId = ",
					"KaleoProcess.DDLRecordSetId join DDMStructure on ",
					"DDMStructure.structureId = DDLRecordSet.DDMStructureId ",
					"where DDMStructure.classNameId <> ",
					PortalUtil.getClassNameId(KaleoProcess.class)));

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update KaleoProcess set DDMTemplateId = ? where " +
						"kaleoProcessId = ?")) {

			while (resultSet.next()) {
				long kaleoProcessId = resultSet.getLong("kaleoProcessId");
				long ddlRecordSetId = resultSet.getLong("DDLRecordSetId");
				long ddmTemplateId = resultSet.getLong("DDMTemplateId");

				long ddmStructureId = resultSet.getLong("DDMStructureId");

				_updateDDLRecordSet(
					ddlRecordSetId, _getNewDDMStructureId(ddmStructureId));

				preparedStatement2.setLong(
					1, _getNewDDMTemplateId(ddmTemplateId));
				preparedStatement2.setLong(2, kaleoProcessId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _updateKaleoProcessLink() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select KaleoProcessLink.kaleoProcessLinkId, ",
					"KaleoProcessLink.DDMTemplateId FROM KaleoProcessLink ",
					"join DDMTemplate on DDMTemplate.templateId = ",
					"KaleoProcessLink.DDMTemplateId where ",
					"DDMTemplate.resourceClassNameId <> ",
					PortalUtil.getClassNameId(KaleoProcess.class)));

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update KaleoProcessLink set DDMTemplateId = ? where " +
						"kaleoProcessLinkId = ?")) {

			while (resultSet.next()) {
				long kaleoProcessLinkId = resultSet.getLong(
					"kaleoProcessLinkId");

				long ddmTemplateId = resultSet.getLong("DDMTemplateId");

				preparedStatement2.setLong(
					1, _getNewDDMTemplateId(ddmTemplateId));

				preparedStatement2.setLong(2, kaleoProcessLinkId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private final DDLRecordSetLocalService _ddlRecordSetLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final HashMap<Long, Long> _ddmStructureMap = new HashMap<>();
	private final DDMStructureVersionLocalService
		_ddmStructureVersionLocalService;
	private final DDMTemplateLocalService _ddmTemplateLocalService;
	private final HashMap<Long, Long> _ddmTemplateMap = new HashMap<>();
	private final DDMTemplateVersionLocalService
		_ddmTemplateVersionLocalService;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourceActions _resourceActions;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}