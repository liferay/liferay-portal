/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.web.internal.object.definitions.display.context.util.ObjectCodeEditorUtil;
import com.liferay.object.web.internal.util.ObjectFieldBusinessTypeUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Murilo Stodolni
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/get_object_field_info"
	},
	service = MVCResourceCommand.class
)
public class GetObjectFieldInfoMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			ParamUtil.getLong(resourceRequest, "objectFieldId"));

		if (objectField == null) {
			return;
		}

		Locale locale = _portal.getLocale(resourceRequest);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectField.getObjectDefinitionId());

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"objectFieldBusinessTypes",
				ObjectFieldBusinessTypeUtil.getObjectFieldBusinessTypeMaps(
					locale,
					ListUtil.filter(
						_objectFieldBusinessTypeRegistry.
							getObjectFieldBusinessTypes(),
						objectFieldBusinessType ->
							objectFieldBusinessType.isVisible(
								objectDefinition) &&
							(!StringUtil.equals(
								objectFieldBusinessType.getName(),
								ObjectFieldConstants.
									BUSINESS_TYPE_RELATIONSHIP) ||
							 Validator.isNotNull(
								 objectField.getRelationshipType()))))
			).put(
				"objectRelationshipId",
				() -> {
					if (!StringUtil.equals(
							objectField.getBusinessType(),
							ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

						return null;
					}

					ObjectRelationship objectRelationship =
						_objectRelationshipLocalService.
							fetchObjectRelationshipByObjectFieldId2(
								objectField.getObjectFieldId());

					return objectRelationship.getObjectRelationshipId();
				}
			).put(
				"readOnlySidebarElements",
				ObjectCodeEditorUtil.getCodeEditorElements(
					ddmExpressionFunction ->
						!ObjectCodeEditorUtil.DDMExpressionFunction.OLD_VALUE.
							equals(ddmExpressionFunction),
					ddmExpressionOperator -> true, true, false, locale,
					objectDefinition.getObjectDefinitionId(),
					objectField1 -> !objectField1.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION))
			).put(
				"sidebarElements",
				() -> {
					if (StringUtil.equals(
							objectField.getBusinessType(),
							ObjectFieldConstants.BUSINESS_TYPE_FORMULA)) {

						return ObjectCodeEditorUtil.getCodeEditorElements(
							ddmExpressionFunction -> false,
							ddmExpressionOperator ->
								_filterableDDMExpressionOperators.contains(
									ddmExpressionOperator),
							false, true, locale,
							objectField.getObjectDefinitionId(),
							objectField2 ->
								_filterableObjectFieldBusinessTypes.contains(
									objectField2.getBusinessType()));
					}

					return ObjectCodeEditorUtil.getCodeEditorElements(
						true, false, false, locale,
						objectField.getObjectDefinitionId(),
						objectField3 -> !objectField3.isSystem());
				}
			));
	}

	private static final Set<ObjectCodeEditorUtil.DDMExpressionOperator>
		_filterableDDMExpressionOperators = Collections.unmodifiableSet(
			SetUtil.fromArray(
				ObjectCodeEditorUtil.DDMExpressionOperator.DIVIDED_BY,
				ObjectCodeEditorUtil.DDMExpressionOperator.MINUS,
				ObjectCodeEditorUtil.DDMExpressionOperator.PLUS,
				ObjectCodeEditorUtil.DDMExpressionOperator.TIMES));
	private static final Set<String> _filterableObjectFieldBusinessTypes =
		Collections.unmodifiableSet(
			SetUtil.fromArray(
				ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL));

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

}