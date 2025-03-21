/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRuleSetting;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.admin.rest.internal.odata.entity.v1_0.ObjectValidationRuleEntityModel;
import com.liferay.object.admin.rest.resource.v1_0.ObjectValidationRuleResource;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.ObjectValidationRuleService;
import com.liferay.object.service.ObjectValidationRuleSettingLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-validation-rule.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ObjectValidationRuleResource.class
)
public class ObjectValidationRuleResourceImpl
	extends BaseObjectValidationRuleResourceImpl {

	@Override
	public void deleteObjectValidationRule(Long objectValidationRuleId)
		throws Exception {

		_objectValidationRuleService.deleteObjectValidationRule(
			objectValidationRuleId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ObjectValidationRule>
			getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
				String externalReferenceCode, String search,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getObjectDefinitionObjectValidationRulesPage(
			objectDefinition.getObjectDefinitionId(), search, pagination,
			sorts);
	}

	@NestedField(
		parentClass = com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition.class,
		value = "objectValidationRules"
	)
	@Override
	public Page<ObjectValidationRule>
			getObjectDefinitionObjectValidationRulesPage(
				Long objectDefinitionId, String search, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE,
					"postObjectDefinitionObjectValidationRule",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE,
					"postObjectDefinitionObjectValidationRuleBatch",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectValidationRuleBatch",
					ObjectDefinition.class.getName(), null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW,
					"getObjectDefinitionObjectValidationRulesPage",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectValidationRuleBatch",
					ObjectDefinition.class.getName(), null)
			).build(),
			booleanQuery -> {
			},
			null, com.liferay.object.model.ObjectValidationRule.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setAttribute(
					"objectDefinitionId", objectDefinitionId);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toObjectValidationRule(
				_objectValidationRuleService.getObjectValidationRule(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectValidationRule getObjectValidationRule(
			Long objectValidationRuleId)
		throws Exception {

		return _toObjectValidationRule(
			_objectValidationRuleService.getObjectValidationRule(
				objectValidationRuleId));
	}

	@Override
	public ObjectValidationRule
			postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				String externalReferenceCode,
				ObjectValidationRule objectValidationRule)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return postObjectDefinitionObjectValidationRule(
			objectDefinition.getObjectDefinitionId(), objectValidationRule);
	}

	@Override
	public ObjectValidationRule postObjectDefinitionObjectValidationRule(
			Long objectDefinitionId, ObjectValidationRule objectValidationRule)
		throws Exception {

		return _toObjectValidationRule(
			_objectValidationRuleService.addObjectValidationRule(
				objectValidationRule.getExternalReferenceCode(),
				objectDefinitionId,
				GetterUtil.getBoolean(objectValidationRule.getActive()),
				objectValidationRule.getEngine(),
				LocalizedMapUtil.populateLocalizedMap(
					objectValidationRule.getErrorLabel()),
				LocalizedMapUtil.populateLocalizedMap(
					objectValidationRule.getName()),
				GetterUtil.getString(
					objectValidationRule.getOutputTypeAsString(),
					ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION),
				objectValidationRule.getScript(),
				GetterUtil.getBoolean(objectValidationRule.getSystem()),
				_toObjectValidationRuleSettings(
					objectDefinitionId, _objectFieldLocalService,
					_objectValidationRuleSettingLocalService,
					objectValidationRule.getObjectValidationRuleSettings())));
	}

	@Override
	public ObjectValidationRule putObjectValidationRule(
			Long objectValidationRuleId,
			ObjectValidationRule objectValidationRule)
		throws Exception {

		com.liferay.object.model.ObjectValidationRule
			serviceBuilderObjectValidationRule =
				_objectValidationRuleLocalService.getObjectValidationRule(
					objectValidationRuleId);

		return _toObjectValidationRule(
			_objectValidationRuleService.updateObjectValidationRule(
				objectValidationRule.getExternalReferenceCode(),
				objectValidationRuleId, objectValidationRule.getActive(),
				objectValidationRule.getEngine(),
				LocalizedMapUtil.populateLocalizedMap(
					objectValidationRule.getErrorLabel()),
				LocalizedMapUtil.populateLocalizedMap(
					objectValidationRule.getName()),
				GetterUtil.getString(
					objectValidationRule.getOutputTypeAsString(),
					ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION),
				objectValidationRule.getScript(),
				_toObjectValidationRuleSettings(
					serviceBuilderObjectValidationRule.getObjectDefinitionId(),
					_objectFieldLocalService,
					_objectValidationRuleSettingLocalService,
					objectValidationRule.getObjectValidationRuleSettings())));
	}

	@Override
	protected void preparePatch(
		ObjectValidationRule objectValidationRule,
		ObjectValidationRule existingObjectValidationRule) {

		if (objectValidationRule.getObjectValidationRuleSettings() == null) {
			return;
		}

		existingObjectValidationRule.setObjectValidationRuleSettings(
			() -> ArrayUtil.append(
				objectValidationRule.getObjectValidationRuleSettings(),
				existingObjectValidationRule.
					getObjectValidationRuleSettings()));
	}

	private com.liferay.object.model.ObjectValidationRuleSetting
			_setObjectValidationRuleSettingProperties(
				String nameObjectFieldId,
				ObjectFieldLocalService objectFieldLocalService,
				ObjectValidationRuleSetting objectValidationRuleSetting,
				long objectDefinitionId,
				com.liferay.object.model.ObjectValidationRuleSetting
					serviceBuilderObjectValidationRuleSetting)
		throws PortalException {

		serviceBuilderObjectValidationRuleSetting.setName(nameObjectFieldId);

		ObjectField objectField = objectFieldLocalService.getObjectField(
			String.valueOf(objectValidationRuleSetting.getValue()),
			objectDefinitionId);

		serviceBuilderObjectValidationRuleSetting.setValue(
			String.valueOf(objectField.getObjectFieldId()));

		return serviceBuilderObjectValidationRuleSetting;
	}

	private ObjectValidationRule _toObjectValidationRule(
			com.liferay.object.model.ObjectValidationRule
				serviceBuilderObjectValidationRule)
		throws Exception {

		return _objectValidationRuleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false,
				HashMapBuilder.put(
					"delete",
					() -> {
						if (serviceBuilderObjectValidationRule.isSystem()) {
							return null;
						}

						return addAction(
							ActionKeys.DELETE, "deleteObjectValidationRule",
							ObjectDefinition.class.getName(),
							serviceBuilderObjectValidationRule.
								getObjectDefinitionId());
					}
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, "getObjectValidationRule",
						ObjectDefinition.class.getName(),
						serviceBuilderObjectValidationRule.
							getObjectDefinitionId())
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, "putObjectValidationRule",
						ObjectDefinition.class.getName(),
						serviceBuilderObjectValidationRule.
							getObjectDefinitionId())
				).build(),
				null, null, contextAcceptLanguage.getPreferredLocale(), null,
				null),
			serviceBuilderObjectValidationRule);
	}

	private List<com.liferay.object.model.ObjectValidationRuleSetting>
		_toObjectValidationRuleSettings(
			long objectDefinitionId,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectValidationRuleSettingLocalService
				objectValidationRuleSettingLocalService,
			ObjectValidationRuleSetting[] objectValidationRuleSettings) {

		return transformToList(
			objectValidationRuleSettings,
			objectValidationRuleSetting -> {
				com.liferay.object.model.ObjectValidationRuleSetting
					serviceBuilderObjectValidationRuleSetting =
						objectValidationRuleSettingLocalService.
							createObjectValidationRuleSetting(0L);

				if (StringUtil.equals(
						objectValidationRuleSetting.getName(),
						ObjectValidationRuleSettingConstants.
							NAME_COMPOSITE_KEY_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE)) {

					return _setObjectValidationRuleSettingProperties(
						ObjectValidationRuleSettingConstants.
							NAME_COMPOSITE_KEY_OBJECT_FIELD_ID,
						objectFieldLocalService, objectValidationRuleSetting,
						objectDefinitionId,
						serviceBuilderObjectValidationRuleSetting);
				}

				if (StringUtil.equals(
						objectValidationRuleSetting.getName(),
						ObjectValidationRuleSettingConstants.
							NAME_OUTPUT_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE)) {

					return _setObjectValidationRuleSettingProperties(
						ObjectValidationRuleSettingConstants.
							NAME_OUTPUT_OBJECT_FIELD_ID,
						objectFieldLocalService, objectValidationRuleSetting,
						objectDefinitionId,
						serviceBuilderObjectValidationRuleSetting);
				}

				serviceBuilderObjectValidationRuleSetting.setName(
					objectValidationRuleSetting.getName());
				serviceBuilderObjectValidationRuleSetting.setValue(
					String.valueOf(objectValidationRuleSetting.getValue()));

				return serviceBuilderObjectValidationRuleSetting;
			});
	}

	private static final EntityModel _entityModel =
		new ObjectValidationRuleEntityModel();

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference(
		target = DTOConverterConstants.OBJECT_VALIDATION_RULE_DTO_CONVERTER
	)
	private DTOConverter
		<com.liferay.object.model.ObjectValidationRule, ObjectValidationRule>
			_objectValidationRuleDTOConverter;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Reference
	private ObjectValidationRuleService _objectValidationRuleService;

	@Reference
	private ObjectValidationRuleSettingLocalService
		_objectValidationRuleSettingLocalService;

}