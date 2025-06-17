/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.exception.DuplicateObjectValidationRuleExternalReferenceCodeException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.exception.ObjectValidationRuleNameException;
import com.liferay.object.exception.ObjectValidationRuleOutputTypeException;
import com.liferay.object.exception.ObjectValidationRuleScriptException;
import com.liferay.object.exception.ObjectValidationRuleSettingNameException;
import com.liferay.object.exception.ObjectValidationRuleSettingValueException;
import com.liferay.object.exception.ObjectValidationRuleSystemException;
import com.liferay.object.exception.RequiredObjectValidationRuleSettingException;
import com.liferay.object.internal.action.util.ObjectEntryVariablesUtil;
import com.liferay.object.internal.entry.util.ObjectEntryUtil;
import com.liferay.object.internal.validation.rule.FunctionObjectValidationRuleEngineImpl;
import com.liferay.object.internal.validation.rule.UniqueCompositeKeyObjectValidationRuleEngineImpl;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.model.ObjectValidationRuleSetting;
import com.liferay.object.scripting.exception.ObjectScriptingException;
import com.liferay.object.scripting.validator.ObjectScriptingValidator;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectValidationRuleSettingLocalService;
import com.liferay.object.service.base.ObjectValidationRuleLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectValidationRuleSettingPersistence;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.object.validation.rule.ObjectValidationRuleEngineRegistry;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectValidationRule",
	service = AopService.class
)
public class ObjectValidationRuleLocalServiceImpl
	extends ObjectValidationRuleLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectValidationRule addObjectValidationRule(
			String externalReferenceCode, long userId, long objectDefinitionId,
			boolean active, String engine, Map<Locale, String> errorLabelMap,
			Map<Locale, String> nameMap, String outputType, String script,
			boolean system,
			List<ObjectValidationRuleSetting> objectValidationRuleSettings)
		throws PortalException {

		_validateInvokerBundle(
			"Only allowed bundles can add system object validation rules",
			system);

		User user = _userLocalService.getUser(userId);
		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		_validate(
			externalReferenceCode, 0, user.getCompanyId(), objectDefinition,
			active, engine, nameMap, outputType, script, system,
			objectValidationRuleSettings);

		ObjectValidationRule objectValidationRule =
			objectValidationRulePersistence.create(
				counterLocalService.increment());

		if (Validator.isNull(externalReferenceCode)) {
			externalReferenceCode = objectValidationRule.getUuid();
		}

		objectValidationRule.setExternalReferenceCode(externalReferenceCode);
		objectValidationRule.setCompanyId(user.getCompanyId());
		objectValidationRule.setUserId(user.getUserId());
		objectValidationRule.setUserName(user.getFullName());
		objectValidationRule.setObjectDefinitionId(objectDefinitionId);
		objectValidationRule.setActive(active);
		objectValidationRule.setEngine(engine);
		objectValidationRule.setErrorLabelMap(errorLabelMap);
		objectValidationRule.setNameMap(nameMap);
		objectValidationRule.setOutputType(outputType);
		objectValidationRule.setScript(script);
		objectValidationRule.setSystem(system);

		objectValidationRule = objectValidationRulePersistence.update(
			objectValidationRule);

		long objectValidationRuleId =
			objectValidationRule.getObjectValidationRuleId();

		objectValidationRule.setObjectValidationRuleSettings(
			TransformUtil.transform(
				objectValidationRuleSettings,
				objectValidationRuleSetting ->
					_objectValidationRuleSettingLocalService.
						addObjectValidationRuleSetting(
							userId, objectValidationRuleId,
							objectValidationRuleSetting.getName(),
							objectValidationRuleSetting.getValue())));

		return objectValidationRule;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ObjectValidationRule deleteObjectValidationRule(
			long objectValidationRuleId)
		throws PortalException {

		ObjectValidationRule objectValidationRule =
			objectValidationRulePersistence.findByPrimaryKey(
				objectValidationRuleId);

		return deleteObjectValidationRule(objectValidationRule);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectValidationRule deleteObjectValidationRule(
			ObjectValidationRule objectValidationRule)
		throws PortalException {

		_validateInvokerBundle(
			"Only allowed bundles can delete system object validation rules",
			objectValidationRule.isSystem());

		objectValidationRule = objectValidationRulePersistence.remove(
			objectValidationRule);

		_objectValidationRuleSettingPersistence.removeByObjectValidationRuleId(
			objectValidationRule.getObjectValidationRuleId());

		return objectValidationRule;
	}

	@Override
	public void deleteObjectValidationRules(Long objectDefinitionId)
		throws PortalException {

		for (ObjectValidationRule objectValidationRule :
				objectValidationRulePersistence.findByObjectDefinitionId(
					objectDefinitionId)) {

			objectValidationRuleLocalService.deleteObjectValidationRule(
				objectValidationRule);
		}
	}

	@Override
	public ObjectValidationRule fetchObjectValidationRule(
		String externalReferenceCode, long objectDefinitionId) {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId);

		if (objectDefinition == null) {
			return null;
		}

		return objectValidationRulePersistence.fetchByERC_C_ODI(
			externalReferenceCode, objectDefinition.getCompanyId(),
			objectDefinitionId);
	}

	@Override
	public ObjectValidationRule getObjectValidationRule(
			long objectValidationRuleId)
		throws PortalException {

		ObjectValidationRule objectValidationRule =
			objectValidationRulePersistence.findByPrimaryKey(
				objectValidationRuleId);

		objectValidationRule.setObjectValidationRuleSettings(
			_objectValidationRuleSettingPersistence.
				findByObjectValidationRuleId(objectValidationRuleId));

		return objectValidationRule;
	}

	@Override
	public List<ObjectValidationRule> getObjectValidationRules(
		boolean active, String engine) {

		return objectValidationRulePersistence.findByA_E(active, engine);
	}

	@Override
	public List<ObjectValidationRule> getObjectValidationRules(
		long objectDefinitionId) {

		return _getObjectValidationRules(
			objectValidationRulePersistence.findByObjectDefinitionId(
				objectDefinitionId));
	}

	@Override
	public List<ObjectValidationRule> getObjectValidationRules(
		long objectDefinitionId, boolean active) {

		return _getObjectValidationRules(
			objectValidationRulePersistence.findByODI_A(
				objectDefinitionId, active));
	}

	@Override
	public List<ObjectValidationRule> getObjectValidationRules(
		long objectDefinitionId, String engine) {

		return _getObjectValidationRules(
			objectValidationRulePersistence.findByODI_E(
				objectDefinitionId, engine));
	}

	@Override
	public int getObjectValidationRulesCount(
		long objectDefinitionId, boolean active) {

		return objectValidationRulePersistence.countByODI_A(
			objectDefinitionId, active);
	}

	@Override
	public void unassociateObjectField(ObjectField objectField) {
		for (ObjectValidationRule objectValidationRule :
				objectValidationRulePersistence.findByODI_O(
					objectField.getObjectDefinitionId(),
					ObjectValidationRuleConstants.
						OUTPUT_TYPE_PARTIAL_VALIDATION)) {

			ObjectValidationRuleSetting objectValidationRuleSetting =
				_objectValidationRuleSettingPersistence.fetchByOVRI_N_V(
					objectValidationRule.getObjectValidationRuleId(),
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_ID,
					String.valueOf(objectField.getObjectFieldId()));

			if (objectValidationRuleSetting == null) {
				continue;
			}

			_objectValidationRuleSettingPersistence.remove(
				objectValidationRuleSetting);

			int count = _objectValidationRuleSettingPersistence.countByOVRI_N(
				objectValidationRule.getObjectValidationRuleId(),
				ObjectValidationRuleSettingConstants.
					NAME_OUTPUT_OBJECT_FIELD_ID);

			if (count == 0) {
				objectValidationRule.setOutputType(
					ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION);

				objectValidationRulePersistence.update(objectValidationRule);
			}
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectValidationRule updateObjectValidationRule(
			String externalReferenceCode, long objectValidationRuleId,
			boolean active, String engine, Map<Locale, String> errorLabelMap,
			Map<Locale, String> nameMap, String outputType, String script,
			List<ObjectValidationRuleSetting> objectValidationRuleSettings)
		throws PortalException {

		ObjectValidationRule objectValidationRule =
			objectValidationRulePersistence.findByPrimaryKey(
				objectValidationRuleId);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectValidationRule.getObjectDefinitionId());

		if (StringUtil.equals(
				engine,
				ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY)) {

			active = true;
		}

		_validate(
			externalReferenceCode,
			objectValidationRule.getObjectValidationRuleId(),
			objectValidationRule.getCompanyId(), objectDefinition, active,
			engine, nameMap, outputType, script,
			objectValidationRule.isSystem(), objectValidationRuleSettings);

		ObjectValidationRuleSetting objectValidationRuleSetting =
			_objectValidationRuleSettingPersistence.fetchByOVRI_N_V(
				objectValidationRuleId,
				ObjectValidationRuleSettingConstants.
					NAME_ALLOW_ACTIVE_STATUS_UPDATE,
				"true");

		if (objectDefinition.isModifiable() && objectDefinition.isSystem() &&
			!ObjectDefinitionUtil.isInvokerBundleAllowed() &&
			objectValidationRule.isSystem() &&
			(objectValidationRuleSetting != null) &&
			GetterUtil.getBoolean(objectValidationRuleSetting.getValue())) {

			objectValidationRule.setActive(active);

			objectValidationRule = objectValidationRulePersistence.update(
				objectValidationRule);

			objectValidationRule.setObjectValidationRuleSettings(
				_objectValidationRuleSettingPersistence.
					findByObjectValidationRuleId(
						objectValidationRule.getObjectValidationRuleId()));

			return objectValidationRule;
		}

		_validateInvokerBundle(
			"Only allowed bundles can edit system object validation rules",
			objectValidationRule.isSystem());

		objectValidationRule.setExternalReferenceCode(externalReferenceCode);
		objectValidationRule.setActive(active);
		objectValidationRule.setEngine(engine);
		objectValidationRule.setErrorLabelMap(errorLabelMap);
		objectValidationRule.setNameMap(nameMap);
		objectValidationRule.setOutputType(outputType);
		objectValidationRule.setScript(script);

		objectValidationRule = objectValidationRulePersistence.update(
			objectValidationRule);

		objectValidationRule.setObjectValidationRuleSettings(
			_updateObjectValidationRuleSettings(
				objectValidationRuleSettings,
				_objectDefinitionPersistence.fetchByPrimaryKey(
					objectValidationRule.getObjectDefinitionId()),
				objectValidationRule));

		return objectValidationRule;
	}

	@Override
	public void validate(
			BaseModel<?> baseModel, long objectDefinitionId,
			JSONObject payloadJSONObject, long userId)
		throws PortalException {

		if ((baseModel == null) ||
			ObjectEntryThreadLocal.isValidatedObjectEntry(
				(long)baseModel.getPrimaryKeyObj())) {

			return;
		}

		List<ObjectValidationRule> objectValidationRules =
			getObjectValidationRules(objectDefinitionId, true);

		if (ListUtil.isEmpty(objectValidationRules)) {
			return;
		}

		_validate(
			baseModel, objectDefinitionId, payloadJSONObject, userId,
			objectValidationRules);
	}

	@Override
	public void validate(
			List<String> externalReferenceCodes, ObjectEntry objectEntry,
			long userId)
		throws PortalException {

		List<ObjectValidationRule> objectValidationRules = null;

		if (ListUtil.isEmpty(externalReferenceCodes)) {
			objectValidationRules = getObjectValidationRules(
				objectEntry.getObjectDefinitionId(), true);
		}
		else {
			objectValidationRules = TransformUtil.transform(
				externalReferenceCodes,
				externalReferenceCode -> {
					ObjectValidationRule objectValidationRule =
						fetchObjectValidationRule(
							externalReferenceCode,
							objectEntry.getObjectDefinitionId());

					if (objectValidationRule.isActive()) {
						return objectValidationRule;
					}

					return null;
				});
		}

		_validate(
			objectEntry, objectEntry.getObjectDefinitionId(),
			ObjectEntryUtil.getPayloadJSONObject(
				_dtoConverterRegistry, _jsonFactory, null,
				_objectDefinitionPersistence.fetchByPrimaryKey(
					objectEntry.getObjectDefinitionId()),
				objectEntry, objectEntry, null,
				_userLocalService.getUser(userId)),
			userId, objectValidationRules);
	}

	private void _addRelatedObjectEntryValues(
			long objectDefinitionId, ObjectField relationshipObjectField,
			long userId, Map<String, Object> values)
		throws PortalException {

		long primaryKey = MapUtil.getLong(
			values, relationshipObjectField.getName());

		if (primaryKey == 0) {
			ListUtil.isNotEmptyForEach(
				_objectFieldPersistence.findByObjectDefinitionId(
					objectDefinitionId),
				objectField -> values.put(
					StringBundler.concat(
						relationshipObjectField.getName(), StringPool.UNDERLINE,
						objectField.getName()),
					null));

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		Map<String, Object> variables = ObjectEntryVariablesUtil.getVariables(
			_dtoConverterRegistry, objectDefinition, false,
			ObjectEntryUtil.getPayloadJSONObject(
				_dtoConverterRegistry, _jsonFactory, objectDefinition,
				primaryKey, _systemObjectDefinitionManagerRegistry, userId),
			_systemObjectDefinitionManagerRegistry);

		for (Map.Entry<String, Object> entry : variables.entrySet()) {
			values.put(
				StringBundler.concat(
					relationshipObjectField.getName(), StringPool.UNDERLINE,
					entry.getKey()),
				entry.getValue());
		}
	}

	private void _addRelatedObjectEntryValues(
			long objectDefinitionId, String script, long userId,
			Map<String, Object> variables)
		throws PortalException {

		ObjectRelationshipLocalService objectRelationshipLocalService =
			_objectRelationshipLocalServiceSnapshot.get();

		for (ObjectRelationship objectRelationship :
				objectRelationshipLocalService.
					getObjectRelationshipsByObjectDefinitionId2(
						objectDefinitionId,
						ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			ObjectField relationshipObjectField =
				_objectFieldPersistence.findByPrimaryKey(
					objectRelationship.getObjectFieldId2());

			if (!script.contains(
					relationshipObjectField.getName() + StringPool.UNDERLINE)) {

				continue;
			}

			_addRelatedObjectEntryValues(
				objectRelationship.getObjectDefinitionId1(),
				relationshipObjectField, userId,
				(Map<String, Object>)variables.get("baseModel"));
			_addRelatedObjectEntryValues(
				objectRelationship.getObjectDefinitionId1(),
				relationshipObjectField, userId,
				(Map<String, Object>)variables.get("originalBaseModel"));
		}
	}

	private List<ObjectValidationRule> _getObjectValidationRules(
		List<ObjectValidationRule> objectValidationRules) {

		for (ObjectValidationRule objectValidationRule :
				objectValidationRules) {

			objectValidationRule.setObjectValidationRuleSettings(
				_objectValidationRuleSettingPersistence.
					findByObjectValidationRuleId(
						objectValidationRule.getObjectValidationRuleId()));
		}

		return objectValidationRules;
	}

	private List<ObjectValidationRuleSetting>
			_updateObjectValidationRuleSettings(
				List<ObjectValidationRuleSetting>
					newObjectValidationRuleSettings,
				ObjectDefinition objectDefinition,
				ObjectValidationRule objectValidationRule)
		throws PortalException {

		for (ObjectValidationRuleSetting oldObjectValidationRuleSetting :
				_objectValidationRuleSettingPersistence.
					findByObjectValidationRuleId(
						objectValidationRule.getObjectValidationRuleId())) {

			boolean delete = true;

			for (ObjectValidationRuleSetting newObjectValidationRuleSetting :
					newObjectValidationRuleSettings) {

				if (StringUtil.equals(
						oldObjectValidationRuleSetting.getName(),
						newObjectValidationRuleSetting.getName()) &&
					StringUtil.equals(
						oldObjectValidationRuleSetting.getValue(),
						newObjectValidationRuleSetting.getValue())) {

					delete = false;

					break;
				}
			}

			if (!delete) {
				continue;
			}

			if (objectDefinition.isApproved() &&
				oldObjectValidationRuleSetting.compareName(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID)) {

				throw new RequiredObjectValidationRuleSettingException.
					MustNotDeleteObjectValidationRuleSettingPublishedObjectDefinition(
						oldObjectValidationRuleSetting.getName());
			}

			_objectValidationRuleSettingPersistence.remove(
				oldObjectValidationRuleSetting);
		}

		for (ObjectValidationRuleSetting newObjectValidationRuleSetting :
				newObjectValidationRuleSettings) {

			ObjectValidationRuleSetting oldObjectValidationRuleSetting =
				_objectValidationRuleSettingPersistence.fetchByOVRI_N_V(
					objectValidationRule.getObjectValidationRuleId(),
					newObjectValidationRuleSetting.getName(),
					newObjectValidationRuleSetting.getValue());

			if (oldObjectValidationRuleSetting != null) {
				continue;
			}

			_objectValidationRuleSettingLocalService.
				addObjectValidationRuleSetting(
					objectValidationRule.getUserId(),
					objectValidationRule.getObjectValidationRuleId(),
					newObjectValidationRuleSetting.getName(),
					newObjectValidationRuleSetting.getValue());
		}

		return newObjectValidationRuleSettings;
	}

	private void _validate(
			BaseModel<?> baseModel, long objectDefinitionId,
			JSONObject payloadJSONObject, long userId,
			List<ObjectValidationRule> objectValidationRules)
		throws PortalException {

		List<ObjectValidationRuleResult> objectValidationRuleResults =
			new ArrayList<>();

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId);

		Map<String, Object> variables = ObjectEntryVariablesUtil.getVariables(
			_dtoConverterRegistry, objectDefinition, payloadJSONObject,
			_systemObjectDefinitionManagerRegistry);

		for (ObjectValidationRule objectValidationRule :
				objectValidationRules) {

			Map<String, Object> results = new HashMap<>();

			ObjectValidationRuleEngine objectValidationRuleEngine =
				_objectValidationRuleEngineRegistry.
					getObjectValidationRuleEngine(
						objectValidationRule.getCompanyId(),
						objectValidationRule.getEngine());

			if (StringUtil.equals(
					objectValidationRuleEngine.getKey(),
					ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY)) {

				variables.put("objectValidationRule", objectValidationRule);

				results = objectValidationRuleEngine.execute(variables, null);
			}
			else if (StringUtil.equals(
						objectValidationRuleEngine.getKey(),
						ObjectValidationRuleConstants.ENGINE_TYPE_DDM)) {

				_addRelatedObjectEntryValues(
					objectDefinition.getObjectDefinitionId(),
					objectValidationRule.getScript(), userId, variables);

				results = objectValidationRuleEngine.execute(
					variables, objectValidationRule.getScript());
			}
			else if (StringUtil.equals(
						objectValidationRuleEngine.getKey(),
						ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY)) {

				results = objectValidationRuleEngine.execute(
					(Map<String, Object>)variables.get("baseModel"),
					objectValidationRule.getScript());
			}
			else if (StringUtil.startsWith(
						objectValidationRuleEngine.getKey(),
						ObjectValidationRuleConstants.
							ENGINE_TYPE_JAVA_DELEGATE_PREFIX)) {

				results = objectValidationRuleEngine.execute(
					HashMapBuilder.put(
						"entryDTO", variables.get("entryDTO")
					).put(
						"originalEntryDTO", variables.get("originalEntryDTO")
					).build(),
					null);
			}
			else {
				results = objectValidationRuleEngine.execute(
					(Map<String, Object>)variables.get("entryDTO"), null);
			}

			Locale locale = LocaleUtil.getMostRelevantLocale();

			User user = _userLocalService.fetchUser(userId);

			if (user != null) {
				locale = user.getLocale();
			}

			String errorMessage = null;

			if (!GetterUtil.getBoolean(results.get("validationCriteriaMet"))) {
				errorMessage = objectValidationRule.getErrorLabel(locale);
			}
			else if (GetterUtil.getBoolean(results.get("invalidScript"))) {
				errorMessage = _language.get(
					locale, "there-was-an-error-validating-your-data");
			}

			if (Validator.isNull(errorMessage)) {
				continue;
			}

			if (objectValidationRule.compareOutputType(
					ObjectValidationRuleConstants.
						OUTPUT_TYPE_PARTIAL_VALIDATION)) {

				for (ObjectValidationRuleSetting objectValidationRuleSetting :
						_objectValidationRuleSettingPersistence.findByOVRI_N(
							objectValidationRule.getObjectValidationRuleId(),
							ObjectValidationRuleSettingConstants.
								NAME_OUTPUT_OBJECT_FIELD_ID)) {

					ObjectField objectField =
						_objectFieldPersistence.fetchByPrimaryKey(
							GetterUtil.getLong(
								objectValidationRuleSetting.getValue()));

					if (objectField == null) {
						continue;
					}

					objectValidationRuleResults.add(
						new ObjectValidationRuleResult(
							errorMessage,
							objectValidationRule.getExternalReferenceCode(),
							objectField.getName()));
				}
			}
			else {
				objectValidationRuleResults.add(
					new ObjectValidationRuleResult(
						errorMessage,
						objectValidationRule.getExternalReferenceCode()));
			}
		}

		ObjectEntryThreadLocal.addValidatedObjectEntryId(
			(long)baseModel.getPrimaryKeyObj());

		if (ListUtil.isNotEmpty(objectValidationRuleResults)) {
			throw new ObjectValidationRuleEngineException(
				objectValidationRuleResults);
		}
	}

	private void _validate(
			String externalReferenceCode, long objectValidationRuleId,
			long companyId, ObjectDefinition objectDefinition, boolean active,
			String engine, Map<Locale, String> nameMap, String outputType,
			String script, boolean system,
			List<ObjectValidationRuleSetting> objectValidationRuleSettings)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			ObjectValidationRule objectValidationRule =
				objectValidationRulePersistence.fetchByERC_C_ODI(
					externalReferenceCode, companyId,
					objectDefinition.getObjectDefinitionId());

			if ((objectValidationRule != null) &&
				(objectValidationRule.getObjectValidationRuleId() !=
					objectValidationRuleId)) {

				throw new DuplicateObjectValidationRuleExternalReferenceCodeException();
			}
		}

		if (Validator.isNull(engine)) {
			throw new ObjectValidationRuleEngineException.MustNotBeNull();
		}

		if (Objects.equals(
				engine, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY) &&
			!_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded()) {

			throw new ObjectValidationRuleEngineException.NotAllowedEngine(
				ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY);
		}

		Locale locale = LocaleUtil.getSiteDefault();

		if ((nameMap == null) || Validator.isNull(nameMap.get(locale))) {
			throw new ObjectValidationRuleNameException(
				"Name is null for locale " + locale.getDisplayName());
		}

		if (!StringUtil.equals(
				outputType,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION) &&
			!StringUtil.equals(
				outputType,
				ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION)) {

			throw new ObjectValidationRuleOutputTypeException(
				"Invalid output type " + outputType);
		}

		if (!_objectValidationRuleEngineRegistry.hasObjectValidationRuleEngine(
				companyId, engine)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"No object validation rule engine is registered with ",
						"key ", engine));
			}

			return;
		}

		ObjectValidationRuleEngine objectValidationRuleEngine =
			_objectValidationRuleEngineRegistry.getObjectValidationRuleEngine(
				companyId, engine);

		if (Validator.isNull(script) &&
			!(objectValidationRuleEngine instanceof
				FunctionObjectValidationRuleEngineImpl ||
			  objectValidationRuleEngine instanceof
				  UniqueCompositeKeyObjectValidationRuleEngineImpl ||
			  StringUtil.startsWith(
				  engine,
				  ObjectValidationRuleConstants.
					  ENGINE_TYPE_JAVA_DELEGATE_PREFIX))) {

			throw new ObjectValidationRuleScriptException(
				"The script is required", "required");
		}

		try {
			if (Objects.equals(
					engine, ObjectValidationRuleConstants.ENGINE_TYPE_DDM)) {

				_ddmExpressionFactory.createExpression(
					CreateExpressionRequest.Builder.newBuilder(
						script
					).build());
			}
			else if (Objects.equals(
						engine,
						ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY)) {

				_objectScriptingValidator.validate("groovy", script);
			}
		}
		catch (ObjectScriptingException objectScriptingException) {
			throw new ObjectValidationRuleScriptException(
				objectScriptingException.getMessage(),
				objectScriptingException.getMessageKey());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			throw new ObjectValidationRuleScriptException(
				"The script syntax is invalid", "syntax-error");
		}

		if (StringUtil.equals(
				outputType,
				ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION) &&
			ListUtil.isEmpty(objectValidationRuleSettings)) {

			throw new ObjectValidationRuleSettingNameException.
				MissingRequiredName(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_ID);
		}

		Set<String> allowedObjectValidationRuleSettingNames = SetUtil.fromArray(
			ObjectValidationRuleSettingConstants.
				NAME_ALLOW_ACTIVE_STATUS_UPDATE,
			ObjectValidationRuleSettingConstants.
				NAME_COMPOSITE_KEY_OBJECT_FIELD_ID,
			ObjectValidationRuleSettingConstants.NAME_OUTPUT_OBJECT_FIELD_ID);

		int count = 0;

		for (ObjectValidationRuleSetting objectValidationRuleSetting :
				objectValidationRuleSettings) {

			if (!allowedObjectValidationRuleSettingNames.contains(
					objectValidationRuleSetting.getName()) ||
				((objectDefinition.isUnmodifiableSystemObject() ||
				  !objectDefinition.isSystem() || !system) &&
				 objectValidationRuleSetting.compareName(
					 ObjectValidationRuleSettingConstants.
						 NAME_ALLOW_ACTIVE_STATUS_UPDATE)) ||
				(objectValidationRuleSetting.compareName(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_ID) &&
				 !StringUtil.equals(
					 outputType,
					 ObjectValidationRuleConstants.
						 OUTPUT_TYPE_PARTIAL_VALIDATION))) {

				throw new ObjectValidationRuleSettingNameException.
					NotAllowedName(objectValidationRuleSetting.getName());
			}

			if (objectValidationRuleSetting.compareName(
					ObjectValidationRuleSettingConstants.
						NAME_ALLOW_ACTIVE_STATUS_UPDATE)) {

				if (Validator.isBoolean(
						objectValidationRuleSetting.getValue())) {

					continue;
				}

				throw new ObjectValidationRuleSettingValueException.
					InvalidValue(
						objectValidationRuleSetting.getName(),
						objectValidationRuleSetting.getValue());
			}

			ObjectField objectField = _objectFieldPersistence.fetchByPrimaryKey(
				GetterUtil.getLong(objectValidationRuleSetting.getValue()));

			if ((objectField == null) || objectField.isSystem() ||
				(objectValidationRuleSetting.compareName(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID) &&
				 !_compositeKeyObjectFieldBusinessTypes.contains(
					 objectField.getBusinessType()))) {

				throw new ObjectValidationRuleSettingValueException.
					InvalidValue(
						objectValidationRuleSetting.getName(),
						objectValidationRuleSetting.getValue());
			}

			if (!objectValidationRuleSetting.compareName(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID)) {

				continue;
			}

			ObjectValidationRuleSetting oldObjectValidationRuleSetting =
				_objectValidationRuleSettingPersistence.fetchByOVRI_N_V(
					objectValidationRuleId,
					objectValidationRuleSetting.getName(),
					objectValidationRuleSetting.getValue());

			if ((oldObjectValidationRuleSetting == null) &&
				objectDefinition.isApproved()) {

				ObjectFieldLocalService objectFieldLocalService =
					_objectFieldLocalServiceSnapshot.get();

				Column<?, ?> column = objectFieldLocalService.getColumn(
					objectDefinition.getObjectDefinitionId(),
					objectField.getName());

				ObjectEntryLocalService objectEntryLocalService =
					_objectEntryLocalServiceSnapshot.get();

				long objectEntriesCount =
					objectEntryLocalService.getObjectEntriesCount(
						0, objectDefinition, column.isNotNull());

				if (objectEntriesCount > 0) {
					throw new ObjectValidationRuleSettingValueException.
						InvalidValue(
							objectValidationRuleSetting.getName(),
							objectValidationRuleSetting.getValue());
				}
			}

			count++;
		}

		if (StringUtil.equals(
				engine,
				ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY)) {

			if (count > 5) {
				throw new ObjectValidationRuleSettingValueException.
					CompositeKeyMustHaveMaxObjectFields();
			}

			if (active && (count < 2)) {
				throw new ObjectValidationRuleSettingValueException.
					CompositeKeyMustHaveMinObjectFields();
			}
		}
	}

	private void _validateInvokerBundle(String message, boolean system)
		throws PortalException {

		if (!system || ObjectDefinitionUtil.isInvokerBundleAllowed()) {
			return;
		}

		throw new ObjectValidationRuleSystemException(message);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectValidationRuleLocalServiceImpl.class);

	private static final List<String> _compositeKeyObjectFieldBusinessTypes =
		Arrays.asList(
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT);
	private static final Snapshot<ObjectEntryLocalService>
		_objectEntryLocalServiceSnapshot = new Snapshot<>(
			ObjectValidationRuleLocalServiceImpl.class,
			ObjectEntryLocalService.class, null, true);
	private static final Snapshot<ObjectFieldLocalService>
		_objectFieldLocalServiceSnapshot = new Snapshot<>(
			ObjectValidationRuleLocalServiceImpl.class,
			ObjectFieldLocalService.class, null, true);
	private static final Snapshot<ObjectRelationshipLocalService>
		_objectRelationshipLocalServiceSnapshot = new Snapshot<>(
			ObjectValidationRuleLocalServiceImpl.class,
			ObjectRelationshipLocalService.class, null, true);

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectFieldPersistence _objectFieldPersistence;

	@Reference
	private ObjectScriptingValidator _objectScriptingValidator;

	@Reference
	private ObjectValidationRuleEngineRegistry
		_objectValidationRuleEngineRegistry;

	@Reference
	private ObjectValidationRuleSettingLocalService
		_objectValidationRuleSettingLocalService;

	@Reference
	private ObjectValidationRuleSettingPersistence
		_objectValidationRuleSettingPersistence;

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}