/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.object.validation.rule;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = ObjectValidationRuleEngine.class)
public class LaunchEntryDuplicateObjectValidationRuleEngineImpl
	implements ObjectDefinitionScoped, ObjectValidationRuleEngine {

	@Override
	public Map<String, Object> execute(
		Map<String, Object> inputObjects, String script) {

		boolean validationCriteriaMet = true;

		try {
			validationCriteriaMet = !_hasDuplicateLaunchEntry(inputObjects);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			validationCriteriaMet = false;
		}

		return HashMapBuilder.<String, Object>put(
			"validationCriteriaMet", validationCriteriaMet
		).build();
	}

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return Arrays.asList("LaunchEntry");
	}

	@Override
	public String getKey() {
		return "javaDelegate#LaunchEntry#duplicate";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "launch-entry-duplicate");
	}

	private Predicate _getEqualsPredicate(
			String fieldName, long objectDefinitionId, Object value)
		throws PortalException {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, fieldName);

		Table<?> table = _objectFieldLocalService.getTable(
			objectDefinitionId, objectField.getName());

		Column<?, Object> column = (Column<?, Object>)table.getColumn(
			objectField.getDBColumnName());

		return column.eq(value);
	}

	private boolean _hasDuplicateLaunchEntry(Map<String, Object> inputObjects)
		throws PortalException {

		long companyId = CompanyThreadLocal.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "LaunchEntry");

		if (objectDefinition == null) {
			return false;
		}

		Map<String, Object> entryDTO = (Map<String, Object>)inputObjects.get(
			"entryDTO");

		Predicate predicate = ObjectEntryTable.INSTANCE.objectEntryId.neq(
			GetterUtil.getLong(entryDTO.get("id")));

		long objectDefinitionId = objectDefinition.getObjectDefinitionId();
		Map<String, Object> properties = (Map<String, Object>)entryDTO.get(
			"properties");

		predicate = predicate.and(
			_getEqualsPredicate(
				"className", objectDefinitionId,
				GetterUtil.getString(properties.get("className"))));
		predicate = predicate.and(
			_getEqualsPredicate(
				"classPK", objectDefinitionId,
				GetterUtil.getLong(properties.get("classPK"))));
		predicate = predicate.and(
			_getEqualsPredicate(
				"classVersion", objectDefinitionId,
				GetterUtil.getString(properties.get("classVersion"))));

		Group companyGroup = _groupLocalService.getCompanyGroup(companyId);

		long objectEntriesCount =
			_objectEntryLocalService.getObjectEntriesCount(
				companyGroup.getGroupId(),
				GetterUtil.getString(entryDTO.get("defaultLanguageId")),
				objectDefinition, predicate);

		if (objectEntriesCount > 0) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchEntryDuplicateObjectValidationRuleEngineImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}