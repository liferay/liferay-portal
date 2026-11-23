/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ImportTaskPreAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinitionSetting;
import com.liferay.object.admin.rest.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jhosseph Gonzalez
 */
@Component(service = ImportTaskPreAction.class)
public class ObjectDefinitionImportTaskPreAction
	implements ImportTaskPreAction {

	@Override
	public void run(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			ImportTaskContext importTaskContext, Object item)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				batchEngineImportTask.getCompanyId(), "LPD-49856") ||
			!ExportImportThreadLocal.isImportInProcess() ||
			!(item instanceof ObjectDefinition objectDefinition)) {

			return;
		}

		WorkflowDefinitionLink[] workflowDefinitionLinks =
			objectDefinition.getWorkflowDefinitionLinks();

		if (ArrayUtil.isEmpty(workflowDefinitionLinks)) {
			return;
		}

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						objectDefinition.getExternalReferenceCode(),
						batchEngineImportTask.getCompanyId());

		String scope = objectDefinition.getScope();

		if ((serviceBuilderObjectDefinition != null) &&
			serviceBuilderObjectDefinition.isUnmodifiableSystemObject()) {

			scope = serviceBuilderObjectDefinition.getScope();
		}

		boolean acceptAllGroups = GetterUtil.getBoolean(
			_getObjectDefinitionSettingValue(
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				objectDefinition));
		String acceptedGroupExternalReferenceCodes = GetterUtil.getString(
			_getObjectDefinitionSettingValue(
				ObjectDefinitionSettingConstants.
					NAME_ACCEPTED_GROUP_EXTERNAL_REFERENCE_CODES,
				objectDefinition));
		List<String> invalidGroupExternalReferenceCodes = new ArrayList<>();
		List<String> missingGroupExternalReferenceCodes = new ArrayList<>();
		List<String> missingWorkflowDefinitionNames = new ArrayList<>();
		List<WorkflowDefinitionLink> validWorkflowDefinitionLinks =
			new ArrayList<>();

		for (WorkflowDefinitionLink workflowDefinitionLink :
				workflowDefinitionLinks) {

			String groupExternalReferenceCode =
				workflowDefinitionLink.getGroupExternalReferenceCode();

			if (Validator.isNotNull(groupExternalReferenceCode)) {
				Group group =
					_groupLocalService.fetchGroupByExternalReferenceCode(
						groupExternalReferenceCode,
						batchEngineImportTask.getCompanyId());

				if (group == null) {
					missingGroupExternalReferenceCodes.add(
						groupExternalReferenceCode);

					continue;
				}

				ObjectScopeProvider objectScopeProvider =
					_objectScopeProviderRegistry.getObjectScopeProvider(scope);

				if (!objectScopeProvider.isValidGroupId(group.getGroupId()) ||
					(StringUtil.equals(
						scope, ObjectDefinitionConstants.SCOPE_DEPOT) &&
					 !acceptAllGroups &&
					 !ArrayUtil.contains(
						 acceptedGroupExternalReferenceCodes.split("\\s*,\\s*"),
						 groupExternalReferenceCode))) {

					invalidGroupExternalReferenceCodes.add(
						groupExternalReferenceCode);

					continue;
				}
			}

			if (Validator.isNull(
					workflowDefinitionLink.
						getWorkflowDefinitionExternalReferenceCode())) {

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCompanyId(
					batchEngineImportTask.getCompanyId());

				KaleoDefinition kaleoDefinition =
					_kaleoDefinitionLocalService.fetchKaleoDefinition(
						workflowDefinitionLink.getWorkflowDefinitionName(),
						serviceContext);

				if (kaleoDefinition == null) {
					missingWorkflowDefinitionNames.add(
						workflowDefinitionLink.getWorkflowDefinitionName());

					continue;
				}
			}

			validWorkflowDefinitionLinks.add(workflowDefinitionLink);
		}

		List<String> messages = new ArrayList<>();

		if (!invalidGroupExternalReferenceCodes.isEmpty()) {
			messages.add(
				_language.format(
					LocaleUtil.getDefault(),
					StringBundler.concat(
						"the-following-sites-or-asset-libraries-are-not-valid-",
						"for-the-object-definition-scope-and-their-workflow-",
						"definition-links-were-removed-from-the-object-",
						"definition-x-x"),
					new Object[] {
						StringUtil.merge(
							invalidGroupExternalReferenceCodes,
							StringPool.COMMA_AND_SPACE),
						_getLabel(objectDefinition)
					}));
		}

		if (!missingGroupExternalReferenceCodes.isEmpty()) {
			messages.add(
				_language.format(
					LocaleUtil.getDefault(),
					"the-following-sites-or-asset-libraries-do-not-exist-and-" +
						"were-removed-from-the-workflow-definition-links-of-" +
							"the-object-definition-x-x",
					new Object[] {
						StringUtil.merge(
							missingGroupExternalReferenceCodes,
							StringPool.COMMA_AND_SPACE),
						_getLabel(objectDefinition)
					}));
		}

		if (!missingWorkflowDefinitionNames.isEmpty()) {
			messages.add(
				_language.format(
					LocaleUtil.getDefault(),
					"the-following-workflow-definitions-do-not-exist-and-" +
						"were-removed-from-the-workflow-definition-links-of-" +
							"the-object-definition-x-x",
					new Object[] {
						StringUtil.merge(
							missingWorkflowDefinitionNames,
							StringPool.COMMA_AND_SPACE),
						_getLabel(objectDefinition)
					}));
		}

		if (messages.isEmpty()) {
			return;
		}

		long classPK = 0;

		if (serviceBuilderObjectDefinition != null) {
			classPK = serviceBuilderObjectDefinition.getObjectDefinitionId();
		}

		_exportImportReportEntryLocalService.getOrAddExportImportReportEntry(
			0, batchEngineImportTask.getCompanyId(),
			objectDefinition.getExternalReferenceCode(),
			_portal.getClassNameId(
				com.liferay.object.model.ObjectDefinition.class),
			classPK,
			GetterUtil.getLong(
				ExportImportThreadLocal.getExportImportConfigurationId()),
			ExportImportReportEntryConstants.TYPE_WARNING,
			StringUtil.merge(messages, StringPool.SPACE), null,
			"object-definition");

		objectDefinition.setWorkflowDefinitionLinks(
			() -> validWorkflowDefinitionLinks.toArray(
				new WorkflowDefinitionLink[0]));
	}

	private String _getLabel(ObjectDefinition objectDefinition) {
		Map<String, String> labelMap = objectDefinition.getLabel();

		if (labelMap == null) {
			return objectDefinition.getName();
		}

		return GetterUtil.getString(
			labelMap.get(LocaleUtil.toLanguageId(LocaleUtil.getDefault())),
			objectDefinition.getName());
	}

	private Object _getObjectDefinitionSettingValue(
		String name, ObjectDefinition objectDefinition) {

		ObjectDefinitionSetting[] objectDefinitionSettings =
			objectDefinition.getObjectDefinitionSettings();

		if (objectDefinitionSettings == null) {
			return null;
		}

		for (ObjectDefinitionSetting objectDefinitionSetting :
				objectDefinitionSettings) {

			if (StringUtil.equals(name, objectDefinitionSetting.getName())) {
				return objectDefinitionSetting.getValue();
			}
		}

		return null;
	}

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private Portal _portal;

}