/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.converter;

import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRuleSetting;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.object.model.ObjectValidationRule"
	},
	service = DTOConverter.class
)
public class ObjectValidationRuleDTOConverter
	implements DTOConverter
		<com.liferay.object.model.ObjectValidationRule, ObjectValidationRule> {

	@Override
	public String getContentType() {
		return ObjectValidationRule.class.getSimpleName();
	}

	@Override
	public ObjectValidationRule toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.object.model.ObjectValidationRule
				serviceBuilderObjectValidationRule)
		throws PortalException {

		if (serviceBuilderObjectValidationRule == null) {
			return null;
		}

		return new ObjectValidationRule() {
			{
				setActions(dtoConverterContext::getActions);
				setActive(serviceBuilderObjectValidationRule::isActive);
				setDateCreated(
					serviceBuilderObjectValidationRule::getCreateDate);
				setDateModified(
					serviceBuilderObjectValidationRule::getModifiedDate);
				setEngine(serviceBuilderObjectValidationRule::getEngine);
				setEngineLabel(
					() -> _language.get(
						dtoConverterContext.getLocale(),
						serviceBuilderObjectValidationRule.getEngine()));
				setErrorLabel(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectValidationRule.getErrorLabelMap()));
				setExternalReferenceCode(
					() ->
						serviceBuilderObjectValidationRule.
							getExternalReferenceCode());
				setId(
					() ->
						serviceBuilderObjectValidationRule.
							getObjectValidationRuleId());
				setName(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectValidationRule.getNameMap()));
				setObjectDefinitionExternalReferenceCode(
					() -> {
						ObjectDefinition objectDefinition =
							_objectDefinitionLocalService.getObjectDefinition(
								serviceBuilderObjectValidationRule.
									getObjectDefinitionId());

						return objectDefinition.getExternalReferenceCode();
					});
				setObjectDefinitionId(
					() ->
						serviceBuilderObjectValidationRule.
							getObjectDefinitionId());
				setObjectValidationRuleSettings(
					() -> TransformUtil.transformToArray(
						serviceBuilderObjectValidationRule.
							getObjectValidationRuleSettings(),
						objectValidationRuleSetting ->
							_toObjectValidationRuleSetting(
								objectValidationRuleSetting),
						ObjectValidationRuleSetting.class));
				setOutputType(
					() -> ObjectValidationRule.OutputType.create(
						serviceBuilderObjectValidationRule.getOutputType()));
				setScript(serviceBuilderObjectValidationRule::getScript);
				setSystem(serviceBuilderObjectValidationRule::isSystem);
			}
		};
	}

	private ObjectValidationRuleSetting _toObjectValidationRuleSetting(
		com.liferay.object.model.ObjectValidationRuleSetting
			objectValidationRuleSetting) {

		if (objectValidationRuleSetting == null) {
			return null;
		}

		return new ObjectValidationRuleSetting() {
			{
				setName(
					() -> {
						if (objectValidationRuleSetting.compareName(
								ObjectValidationRuleSettingConstants.
									NAME_COMPOSITE_KEY_OBJECT_FIELD_ID)) {

							return ObjectValidationRuleSettingConstants.
								NAME_COMPOSITE_KEY_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE;
						}

						if (objectValidationRuleSetting.compareName(
								ObjectValidationRuleSettingConstants.
									NAME_OUTPUT_OBJECT_FIELD_ID)) {

							return ObjectValidationRuleSettingConstants.
								NAME_OUTPUT_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE;
						}

						return objectValidationRuleSetting.getName();
					});
				setValue(
					() -> {
						if (!(objectValidationRuleSetting.compareName(
								ObjectValidationRuleSettingConstants.
									NAME_COMPOSITE_KEY_OBJECT_FIELD_ID) ||
							  objectValidationRuleSetting.compareName(
								  ObjectValidationRuleSettingConstants.
									  NAME_OUTPUT_OBJECT_FIELD_ID))) {

							return objectValidationRuleSetting.getValue();
						}

						ObjectField objectField =
							_objectFieldLocalService.getObjectField(
								GetterUtil.getLong(
									objectValidationRuleSetting.getValue()));

						return objectField.getExternalReferenceCode();
					});
			}
		};
	}

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}