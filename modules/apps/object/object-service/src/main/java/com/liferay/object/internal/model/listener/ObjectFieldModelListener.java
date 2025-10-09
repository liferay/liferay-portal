/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionNameConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(service = ModelListener.class)
public class ObjectFieldModelListener extends BaseModelListener<ObjectField> {

	@Override
	public void onAfterCreate(ObjectField objectField)
		throws ModelListenerException {

		_addAssignToMeObjectAction(objectField);
	}

	@Override
	public void onAfterRemove(ObjectField objectField)
		throws ModelListenerException {

		_deleteAssignToMeObjectAction(objectField);
	}

	@Override
	public void onAfterUpdate(
			ObjectField originalObjectField, ObjectField objectField)
		throws ModelListenerException {

		if (originalObjectField.compareBusinessType(
				objectField.getBusinessType())) {

			return;
		}

		_addAssignToMeObjectAction(objectField);
		_deleteAssignToMeObjectAction(originalObjectField);
	}

	@Override
	public void onBeforeCreate(ObjectField objectField)
		throws ModelListenerException {

		_route(EventTypes.ADD, objectField);
	}

	@Override
	public void onBeforeRemove(ObjectField objectField)
		throws ModelListenerException {

		_route(EventTypes.DELETE, objectField);
	}

	@Override
	public void onBeforeUpdate(
			ObjectField originalObjectField, ObjectField objectField)
		throws ModelListenerException {

		try {
			_auditRouter.route(
				AuditMessageBuilder.buildAuditMessage(
					EventTypes.UPDATE, ObjectField.class.getName(),
					objectField.getObjectFieldId(),
					_getModifiedAttributes(originalObjectField, objectField)));
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _addAssignToMeObjectAction(ObjectField objectField)
		throws ModelListenerException {

		if (!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			_objectActionLocalService.addObjectAction(
				null, objectField.getUserId(),
				objectField.getObjectDefinitionId(), true, null, null,
				LocalizedMapUtil.getLocalizedMap(
					_language.get(
						LocaleUtil.getDefault(), "there-was-an-unknown-error")),
				LocalizedMapUtil.getLocalizedMap(
					_language.get(LocaleUtil.getDefault(), "assign-to-me")),
				ObjectActionNameConstants.NAME_ASSIGN_TO_ME,
				ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
				ObjectActionTriggerConstants.KEY_STANDALONE,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"objectDefinitionId", objectField.getObjectDefinitionId()
				).put(
					"predefinedValues",
					JSONUtil.putAll(
						JSONUtil.put(
							"name", objectField.getName()
						).put(
							"value",
							JSONUtil.put(
								"externalReferenceCode",
								"currentUserExternalReferenceCode"
							).put(
								"type", "User"
							).toString()
						)
					).toString()
				).build(),
				true);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _deleteAssignToMeObjectAction(ObjectField objectField)
		throws ModelListenerException {

		if (!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			ObjectAction objectAction =
				_objectActionLocalService.fetchObjectAction(
					objectField.getObjectDefinitionId(),
					ObjectActionNameConstants.NAME_ASSIGN_TO_ME);

			if (objectAction != null) {
				_objectActionLocalService.deleteObjectAction(objectAction);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private List<Attribute> _getModifiedAttributes(
		ObjectField originalObjectField, ObjectField objectField) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			objectField, originalObjectField);

		attributesBuilder.add("businessType");
		attributesBuilder.add("DBColumnName");
		attributesBuilder.add("DBType");
		attributesBuilder.add("indexed");
		attributesBuilder.add("indexedAsKeyword");
		attributesBuilder.add("indexedLanguageId");
		attributesBuilder.add("labelMap");
		attributesBuilder.add("listTypeDefinitionId");
		attributesBuilder.add("name");
		attributesBuilder.add("required");

		return attributesBuilder.getAttributes();
	}

	private void _route(String eventType, ObjectField objectField)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, ObjectField.class.getName(),
				objectField.getObjectFieldId(), null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"businessType", objectField.getBusinessType()
			).put(
				"DBColumnName", objectField.getDBColumnName()
			).put(
				"DBType", objectField.getDBType()
			).put(
				"indexed", objectField.isIndexed()
			).put(
				"indexedAsKeyword", objectField.isIndexedAsKeyword()
			).put(
				"indexedLanguageId", objectField.getIndexedLanguageId()
			).put(
				"labelMap", objectField.getLabelMap()
			).put(
				"listTypeDefinitionId", objectField.getListTypeDefinitionId()
			).put(
				"name", objectField.getName()
			).put(
				"required", objectField.isRequired()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private Language _language;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

}