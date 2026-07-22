/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Gomes
 */
@Component(
	property = "bulk.selection.action.key=add.object.to.project",
	service = BulkSelectionAction.class
)
public class AddObjectToProjectBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (!(object instanceof ObjectEntry)) {
			throw new IllegalArgumentException("Unsupported object " + object);
		}

		String[] projectScopeKeys = (String[])inputMap.get("projectScopeKeys");

		if (ArrayUtil.isEmpty(projectScopeKeys)) {
			throw new IllegalArgumentException(
				"No project scope keys were provided");
		}

		ObjectEntry objectEntry = (ObjectEntry)object;

		long companyId = objectEntry.getCompanyId();

		ObjectDefinition cmpProjectLinkObjectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT_LINK", companyId);
		ObjectDefinition cmpProjectObjectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", companyId);

		Group group = _groupLocalService.getGroup(objectEntry.getGroupId());

		for (String projectScopeKey : projectScopeKeys) {
			Long projectGroupId = GroupUtil.getGroupId(
				companyId, projectScopeKey, _groupLocalService);

			if (projectGroupId == null) {
				throw new IllegalArgumentException(
					"Unable to resolve the project scope key \"" +
						projectScopeKey + "\"");
			}

			List<ObjectEntry> cmpProjectObjectEntries =
				objectEntryLocalService.getObjectEntries(
					projectGroupId,
					cmpProjectObjectDefinition.getObjectDefinitionId(), 0, 1);

			if (cmpProjectObjectEntries.isEmpty()) {
				throw new IllegalArgumentException(
					"Unable to find a project in group " + projectGroupId);
			}

			ObjectEntry cmpProjectObjectEntry = cmpProjectObjectEntries.get(0);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(companyId);
			serviceContext.setScopeGroupId(projectGroupId);
			serviceContext.setUserId(user.getUserId());

			try {
				_objectEntryService.addObjectEntry(
					projectGroupId,
					cmpProjectLinkObjectDefinition.getObjectDefinitionId(), 0,
					null,
					HashMapBuilder.<String, Serializable>put(
						"classExternalReferenceCode",
						objectEntry.getExternalReferenceCode()
					).put(
						"className", objectEntry.getModelClassName()
					).put(
						"groupExternalReferenceCode",
						group.getExternalReferenceCode()
					).put(
						"r_cmpProjectToCMPProjectLinks_c_cmpProjectId",
						cmpProjectObjectEntry.getObjectEntryId()
					).build(),
					serviceContext);
			}
			catch (ModelListenerException modelListenerException) {
				Throwable throwable = modelListenerException.getCause();

				if (!(throwable instanceof
						ObjectValidationRuleEngineException)) {

					throw modelListenerException;
				}

				ObjectValidationRuleEngineException
					objectValidationRuleEngineException =
						(ObjectValidationRuleEngineException)throwable;

				List<ObjectValidationRuleResult> objectValidationRuleResults =
					objectValidationRuleEngineException.
						getObjectValidationRuleResults();

				if (ListUtil.isEmpty(objectValidationRuleResults)) {
					throw modelListenerException;
				}

				for (ObjectValidationRuleResult objectValidationRuleResult :
						objectValidationRuleResults) {

					if (!Objects.equals(
							objectValidationRuleResult.
								getExternalReferenceCode(),
							"L_CMP_PROJECT_LINK_UNIQUE")) {

						throw modelListenerException;
					}
				}

				if (_log.isWarnEnabled()) {
					_log.warn(modelListenerException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddObjectToProjectBulkSelectionAction.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}