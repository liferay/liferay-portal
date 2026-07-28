/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionNameConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.definition.notification.term.util.ObjectDefinitionNotificationTermUtil;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
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

		if (!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			_addAssignToMeObjectAction(objectField);
			_addNotifyAssigneeObjectActions(objectField);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectField objectField)
		throws ModelListenerException {

		if (!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			_deleteAssignToMeObjectAction(objectField);
			_deleteNotifyAssigneeObjectActions(objectField);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectField originalObjectField, ObjectField objectField)
		throws ModelListenerException {

		if (originalObjectField.compareBusinessType(
				objectField.getBusinessType())) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

				_addAssignToMeObjectAction(objectField);
				_addNotifyAssigneeObjectActions(objectField);
			}

			if (originalObjectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

				_deleteAssignToMeObjectAction(originalObjectField);
				_deleteNotifyAssigneeObjectActions(originalObjectField);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
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
					ObjectField.class.getName(), objectField.getObjectFieldId(),
					EventTypes.UPDATE,
					_getModifiedAttributes(originalObjectField, objectField)));
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _addAssignToMeObjectAction(ObjectField objectField)
		throws PortalException {

		_objectActionLocalService.addObjectAction(
			null, objectField.getUserId(), objectField.getObjectDefinitionId(),
			true, null, null,
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

	private void _addNotificationTemplateObjectAction(
			String conditionExpression, String label, String name,
			String notificationTemplateExternalReferenceCode,
			String objectActionTriggerKey, ObjectField objectField)
		throws PortalException {

		_objectActionLocalService.addObjectAction(
			null, objectField.getUserId(), objectField.getObjectDefinitionId(),
			true, conditionExpression, null,
			LocalizedMapUtil.getLocalizedMap(
				_language.get(
					LocaleUtil.getDefault(), "there-was-an-unknown-error")),
			LocalizedMapUtil.getLocalizedMap(label), name,
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			objectActionTriggerKey,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"notificationTemplateExternalReferenceCode",
				notificationTemplateExternalReferenceCode
			).build(),
			true);
	}

	private void _addNotifyAssigneeObjectActions(ObjectField objectField)
		throws PortalException {

		String notificationTemplateExternalReferenceCode =
			_getAssigneeNotificationTemplateExternalReferenceCode(objectField);
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectField.getObjectDefinitionId());

		_notificationTemplateLocalService.addAssigneeNotificationTemplate(
			notificationTemplateExternalReferenceCode, objectField.getUserId(),
			ObjectDefinitionNotificationTermUtil.getObjectFieldTermName(
				objectDefinition.getShortName(), objectField.getName()));

		_addNotificationTemplateObjectAction(
			StringBundler.concat(
				"isEmpty(", objectField.getName(), ") == false"),
			_language.get(
				LocaleUtil.getDefault(), "assignee-notification-on-add"),
			ObjectActionNameConstants.NAME_NOTIFY_ASSIGNEE_ON_AFTER_ADD,
			notificationTemplateExternalReferenceCode,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, objectField);
		_addNotificationTemplateObjectAction(
			StringBundler.concat(
				"isEmpty(", objectField.getName(), ") == false && oldValue(\"",
				objectField.getName(), "\") != ", objectField.getName()),
			_language.get(
				LocaleUtil.getDefault(), "assignee-notification-on-update"),
			ObjectActionNameConstants.NAME_NOTIFY_ASSIGNEE_ON_AFTER_UPDATE,
			notificationTemplateExternalReferenceCode,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, objectField);
	}

	private void _deleteAssignToMeObjectAction(ObjectField objectField)
		throws PortalException {

		_deleteObjectAction(
			ObjectActionNameConstants.NAME_ASSIGN_TO_ME, objectField);
	}

	private void _deleteNotifyAssigneeObjectActions(ObjectField objectField)
		throws PortalException {

		_deleteObjectAction(
			ObjectActionNameConstants.NAME_NOTIFY_ASSIGNEE_ON_AFTER_ADD,
			objectField);
		_deleteObjectAction(
			ObjectActionNameConstants.NAME_NOTIFY_ASSIGNEE_ON_AFTER_UPDATE,
			objectField);

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.
				fetchNotificationTemplateByExternalReferenceCode(
					_getAssigneeNotificationTemplateExternalReferenceCode(
						objectField),
					objectField.getCompanyId());

		if (notificationTemplate != null) {
			_notificationTemplateLocalService.deleteNotificationTemplate(
				notificationTemplate);
		}
	}

	private void _deleteObjectAction(String name, ObjectField objectField)
		throws PortalException {

		ObjectAction objectAction = _objectActionLocalService.fetchObjectAction(
			objectField.getObjectDefinitionId(), name);

		if (objectAction != null) {
			_objectActionLocalService.deleteObjectAction(objectAction);
		}
	}

	private String _getAssigneeNotificationTemplateExternalReferenceCode(
		ObjectField objectField) {

		return objectField.getExternalReferenceCode() +
			"_ASSIGNEE_NOTIFICATION_TEMPLATE";
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
				ObjectField.class.getName(), objectField.getObjectFieldId(),
				eventType, null);

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
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}