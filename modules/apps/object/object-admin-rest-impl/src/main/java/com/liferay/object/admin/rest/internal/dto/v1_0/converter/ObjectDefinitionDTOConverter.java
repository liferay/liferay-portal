/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.converter;

import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectDefinitionUtil;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "dto.class.name=com.liferay.object.model.ObjectDefinition",
	service = DTOConverter.class
)
public class ObjectDefinitionDTOConverter
	implements DTOConverter
		<com.liferay.object.model.ObjectDefinition, ObjectDefinition> {

	@Override
	public String getContentType() {
		return ObjectDefinition.class.getSimpleName();
	}

	@Override
	public ObjectDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition)
		throws Exception {

		if (serviceBuilderObjectDefinition == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionUtil.toObjectDefinition(
				_groupLocalService, dtoConverterContext.getLocale(),
				_notificationTemplateLocalService, _objectActionLocalService,
				_objectDefinitionLocalService, _objectFieldDTOConverter,
				_objectFieldLocalService, _objectLayoutLocalService,
				_objectRelationshipDTOConverter,
				_objectRelationshipLocalService,
				_objectValidationRuleDTOConverter,
				_objectValidationRuleLocalService, _objectViewDTOConverter,
				_objectViewLocalService, _portal,
				serviceBuilderObjectDefinition,
				_systemObjectDefinitionManagerRegistry, _userLocalService,
				_workflowDefinitionLinkLocalService);

		objectDefinition.setActions(dtoConverterContext::getActions);

		return objectDefinition;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_FIELD_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectField, ObjectField>
		_objectFieldDTOConverter;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_RELATIONSHIP_DTO_CONVERTER)
	private DTOConverter
		<ObjectRelationship,
		 com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship>
			_objectRelationshipDTOConverter;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference(
		target = DTOConverterConstants.OBJECT_VALIDATION_RULE_DTO_CONVERTER
	)
	private DTOConverter
		<com.liferay.object.model.ObjectValidationRule, ObjectValidationRule>
			_objectValidationRuleDTOConverter;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_VIEW_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectView, ObjectView>
		_objectViewDTOConverter;

	@Reference
	private ObjectViewLocalService _objectViewLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}