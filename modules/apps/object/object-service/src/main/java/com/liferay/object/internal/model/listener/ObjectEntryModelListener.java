/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.internal.entry.util.ObjectEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldTable;
import com.liferay.object.model.ObjectRelationshipTable;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.model.ObjectViewFilterColumnTable;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.ObjectViewFilterColumnLocalService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		_route(EventTypes.ADD, null, objectEntry);

		_runRelevantObjectEntryModelListeners(
			objectEntry,
			relevantObjectEntryModelListener ->
				relevantObjectEntryModelListener.onAfterCreate(objectEntry));
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		_route(EventTypes.DELETE, null, objectEntry);

		try {
			_updateObjectViewFilterColumn(StringPool.BLANK, objectEntry);

			long userId = PrincipalThreadLocal.getUserId();

			if (userId == 0) {
				userId = objectEntry.getUserId();
			}

			User user = _userLocalService.getUser(userId);

			if (!objectEntry.isInTrash()) {
				_executeObjectActions(
					ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
					objectEntry, objectEntry, user);
			}

			ObjectEntry rootObjectEntry =
				_objectEntryLocalService.fetchObjectEntry(
					objectEntry.getRootObjectEntryId());

			if (!FeatureFlagManagerUtil.isEnabled(
					objectEntry.getCompanyId(), "LPD-34594") ||
				(rootObjectEntry == null)) {

				return;
			}

			_executeObjectActions(
				ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE, null,
				rootObjectEntry, user);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}

		_runRelevantObjectEntryModelListeners(
			objectEntry,
			relevantObjectEntryModelListener ->
				relevantObjectEntryModelListener.onAfterRemove(objectEntry));
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_route(EventTypes.UPDATE, originalObjectEntry, objectEntry);

		_runRelevantObjectEntryModelListeners(
			objectEntry,
			relevantObjectEntryModelListener ->
				relevantObjectEntryModelListener.onAfterUpdate(
					originalObjectEntry, objectEntry));

		if (StringUtil.equals(
				originalObjectEntry.getExternalReferenceCode(),
				objectEntry.getExternalReferenceCode())) {

			return;
		}

		try {
			_updateObjectViewFilterColumn(
				objectEntry.getExternalReferenceCode(), originalObjectEntry);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		_validateObjectEntry(null, objectEntry);

		_runRelevantObjectEntryModelListeners(
			objectEntry,
			relevantObjectEntryModelListener ->
				relevantObjectEntryModelListener.onBeforeCreate(objectEntry));
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		_runRelevantObjectEntryModelListeners(
			objectEntry,
			relevantObjectEntryModelListener ->
				relevantObjectEntryModelListener.onBeforeRemove(objectEntry));
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_validateObjectEntry(originalObjectEntry, objectEntry);

		_runRelevantObjectEntryModelListeners(
			objectEntry,
			relevantObjectEntryModelListener ->
				relevantObjectEntryModelListener.onBeforeUpdate(
					originalObjectEntry, objectEntry));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_relevantObjectEntryModelListeners = ServiceTrackerListFactory.open(
			bundleContext, RelevantObjectEntryModelListener.class);
	}

	private void _executeObjectActions(
			String objectActionTriggerKey, ObjectEntry originalObjectEntry,
			ObjectEntry objectEntry, User user)
		throws PortalException {

		_objectActionEngine.executeObjectActions(
			objectEntry.getModelClassName(), objectEntry.getCompanyId(),
			objectActionTriggerKey,
			() -> ObjectEntryUtil.getPayloadJSONObject(
				_dtoConverterRegistry, _jsonFactory, objectActionTriggerKey,
				_objectDefinitionLocalService.getObjectDefinition(
					objectEntry.getObjectDefinitionId()),
				objectEntry, originalObjectEntry, null, user),
			user.getUserId());
	}

	private AuditMessage _getAuditMessage(
		String eventType, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry) {

		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			eventType, objectEntry, null);

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			Map<String, Serializable> values = objectEntry.getValues();

			additionalInfoJSONObject.put(
				objectField.getName(),
				_getAuditValue(objectField, values.get(objectField.getName())));
		}

		return auditMessage;
	}

	private Object _getAuditValue(ObjectField objectField, Object value) {
		if (Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			long dlFileEntryId = GetterUtil.getLong(value);

			try {
				DLFileEntry dlFileEntry =
					_dlFileEntryLocalService.getDLFileEntry(dlFileEntryId);

				return JSONUtil.put(
					"dlFileEntryId", dlFileEntryId
				).put(
					"title", dlFileEntry.getTitle()
				);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			String key = GetterUtil.getString(value);

			try {
				ListTypeEntry listTypeEntry =
					_listTypeEntryLocalService.getListTypeEntry(
						objectField.getListTypeDefinitionId(), key);

				return JSONUtil.put(
					"key", key
				).put(
					"name", listTypeEntry.getNameCurrentValue()
				);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			long objectEntryId = GetterUtil.getLong(value);

			try {
				ObjectEntry objectEntry =
					_objectEntryLocalService.getObjectEntry(objectEntryId);

				return JSONUtil.put(
					"objectEntryId", objectEntryId
				).put(
					"titleValue", objectEntry.getTitleValue()
				);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return value;
	}

	private List<Attribute> _getModifiedAttributes(
		ObjectDefinition objectDefinition,
		Map<String, Serializable> originalValues,
		Map<String, Serializable> values) {

		List<Attribute> attributes = new ArrayList<>();

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			Object originalValue = originalValues.get(objectField.getName());
			Object value = values.get(objectField.getName());

			if (!Objects.equals(originalValue, value)) {
				attributes.add(
					new Attribute(
						objectField.getName(),
						_getAuditValue(objectField, value),
						_getAuditValue(objectField, originalValue)));
			}
		}

		return attributes;
	}

	private void _route(
		String eventType, ObjectEntry originalObjectEntry,
		ObjectEntry objectEntry) {

		try {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectEntry.getObjectDefinitionId());

			if (!objectDefinition.isEnableObjectEntryHistory()) {
				return;
			}

			if (StringUtil.equals(EventTypes.UPDATE, eventType)) {
				_auditRouter.route(
					AuditMessageBuilder.buildAuditMessage(
						EventTypes.UPDATE, objectEntry,
						_getModifiedAttributes(
							objectDefinition, originalObjectEntry.getValues(),
							objectEntry.getValues())));
			}
			else {
				_auditRouter.route(
					_getAuditMessage(eventType, objectDefinition, objectEntry));
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private void _runRelevantObjectEntryModelListeners(
		ObjectEntry objectEntry,
		UnsafeConsumer<RelevantObjectEntryModelListener, ModelListenerException>
			unsafeConsumer) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		for (RelevantObjectEntryModelListener relevantObjectEntryModelListener :
				_relevantObjectEntryModelListeners) {

			if (Objects.equals(
					objectDefinition.getExternalReferenceCode(),
					relevantObjectEntryModelListener.
						getObjectDefinitionExternalReferenceCode())) {

				unsafeConsumer.accept(relevantObjectEntryModelListener);
			}
		}
	}

	private void _updateObjectViewFilterColumn(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws PortalException {

		List<ObjectViewFilterColumn> objectViewFilterColumns =
			_objectViewFilterColumnLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					ObjectViewFilterColumnTable.INSTANCE
				).from(
					ObjectViewFilterColumnTable.INSTANCE
				).innerJoinON(
					ObjectFieldTable.INSTANCE,
					ObjectFieldTable.INSTANCE.name.eq(
						ObjectViewFilterColumnTable.INSTANCE.objectFieldName)
				).innerJoinON(
					ObjectRelationshipTable.INSTANCE,
					ObjectRelationshipTable.INSTANCE.objectFieldId2.eq(
						ObjectFieldTable.INSTANCE.objectFieldId)
				).where(
					ObjectRelationshipTable.INSTANCE.objectDefinitionId1.eq(
						objectEntry.getObjectDefinitionId())
				));

		for (ObjectViewFilterColumn objectViewFilterColumn :
				objectViewFilterColumns) {

			JSONArray valueJSONArray = objectViewFilterColumn.getJSONArray();

			JSONArray newValueJSONArray = new JSONArrayImpl();

			if (valueJSONArray != null) {
				for (int i = 0; i < valueJSONArray.length(); i++) {
					if (StringUtil.equals(
							(String)valueJSONArray.get(i),
							objectEntry.getExternalReferenceCode())) {

						if (!StringUtil.equals(
								externalReferenceCode, StringPool.BLANK)) {

							newValueJSONArray.put(externalReferenceCode);
						}
					}
					else {
						newValueJSONArray.put((String)valueJSONArray.get(i));
					}
				}
			}

			if (newValueJSONArray.length() == 0) {
				_objectViewFilterColumnLocalService.
					deleteObjectViewFilterColumn(objectViewFilterColumn);

				continue;
			}

			objectViewFilterColumn.setJSON(
				StringBundler.concat(
					"{\"", objectViewFilterColumn.getFilterType(), "\":",
					newValueJSONArray, "}"));

			_objectViewFilterColumnLocalService.updateObjectViewFilterColumn(
				objectViewFilterColumn);
		}
	}

	private void _validateObjectEntry(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		if (ObjectEntryThreadLocal.isSkipObjectValidationRules()) {
			return;
		}

		try {
			long userId = PrincipalThreadLocal.getUserId();

			if (userId == 0) {
				userId = objectEntry.getUserId();
			}

			int count =
				_objectValidationRuleLocalService.getObjectValidationRulesCount(
					objectEntry.getObjectDefinitionId(), true);

			if (count > 0) {
				_objectValidationRuleLocalService.validate(
					objectEntry, objectEntry.getObjectDefinitionId(),
					ObjectEntryUtil.getPayloadJSONObject(
						_dtoConverterRegistry, _jsonFactory, null,
						_objectDefinitionLocalService.getObjectDefinition(
							objectEntry.getObjectDefinitionId()),
						objectEntry, originalObjectEntry, null,
						_userLocalService.getUser(userId)),
					userId);
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectActionEngine _objectActionEngine;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Reference
	private ObjectViewFilterColumnLocalService
		_objectViewFilterColumnLocalService;

	private ServiceTrackerList<RelevantObjectEntryModelListener>
		_relevantObjectEntryModelListeners;

	@Reference
	private UserLocalService _userLocalService;

}