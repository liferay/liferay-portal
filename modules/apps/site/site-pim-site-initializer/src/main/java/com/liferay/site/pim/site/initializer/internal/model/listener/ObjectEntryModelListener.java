/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.model.listener;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.exception.DuplicatePIMLinkException;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorConfigurationUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorDispatchTriggerUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMLinkUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMObjectEntryFolderUtil;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;
import com.liferay.site.pim.site.initializer.link.PIMLinkTypeRegistry;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterRemove(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterUpdate(originalObjectEntry, objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onBeforeCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onBeforeUpdate(originalObjectEntry, objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _deletePIMConnectorConfigurationObjectEntry(
			ObjectEntry pimConnectorObjectEntry)
		throws PortalException {

		ObjectEntry pimConnectorConfigurationObjectEntry =
			PIMConnectorConfigurationUtil.
				fetchPIMConnectorConfigurationObjectEntry(
					_pimConnectorRegistry.getPIMConnector(
						MapUtil.getString(
							pimConnectorObjectEntry.getValues(), "key")),
					pimConnectorObjectEntry);

		if (pimConnectorConfigurationObjectEntry == null) {
			return;
		}

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			_objectEntryLocalService.deleteObjectEntry(
				pimConnectorConfigurationObjectEntry);
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}
	}

	private void _deletePIMLinkObjectEntries(ObjectEntry objectEntry)
		throws PortalException {

		List<Map<String, Serializable>> valuesList =
			PIMLinkUtil.getValuesListByClassExternalReferenceCode(
				objectEntry.getExternalReferenceCode(),
				objectEntry.getModelClassName(), objectEntry.getCompanyId(),
				_filterFactory, objectEntry.getGroupId());

		if (valuesList.isEmpty()) {
			return;
		}

		ObjectDefinition objectDefinition =
			PIMLinkUtil.getPIMLinkObjectDefinition(objectEntry.getCompanyId());

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			for (Map<String, Serializable> values : valuesList) {
				_objectEntryLocalService.deleteObjectEntry(
					MapUtil.getLong(
						values, objectDefinition.getPKObjectFieldName()));
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}
	}

	private PIMConnectorChannelField _fetchPIMConnectorChannelField(
		String channelFieldName, long pimConnectorObjectEntryId) {

		ObjectEntry pimConnectorObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				pimConnectorObjectEntryId);

		if (pimConnectorObjectEntry == null) {
			return null;
		}

		PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
			MapUtil.getString(pimConnectorObjectEntry.getValues(), "key"));

		if (pimConnector == null) {
			return null;
		}

		for (PIMConnectorChannelField pimConnectorChannelField :
				pimConnector.getPIMConnectorChannelFields()) {

			if (Objects.equals(
					pimConnectorChannelField.getName(), channelFieldName)) {

				return pimConnectorChannelField;
			}
		}

		return null;
	}

	private ServiceContext _getServiceContext(long companyId, long groupId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(PrincipalThreadLocal.getUserId());

		return serviceContext;
	}

	private boolean _isPIMConnectorFieldMappingRuleObjectEntry(
		ObjectEntry objectEntry) {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		return Objects.equals(
			objectDefinition.getExternalReferenceCode(),
			PIMObjectDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING_RULE);
	}

	private boolean _isPIMConnectorObjectEntry(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		return Objects.equals(
			objectDefinition.getExternalReferenceCode(),
			PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_CONNECTOR);
	}

	private boolean _isPIMLinkObjectEntry(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		return Objects.equals(
			objectDefinition.getExternalReferenceCode(),
			PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK);
	}

	private boolean _isPIMProductObjectEntry(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				objectEntry.getCompanyId());

		if ((objectFolder != null) &&
			(objectDefinition.getObjectFolderId() ==
				objectFolder.getObjectFolderId())) {

			return true;
		}

		return false;
	}

	private void _onAfterCreate(ObjectEntry objectEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666")) {

			return;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if ((objectDefinition != null) &&
			Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				PIMObjectDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_CONNECTOR)) {

			PIMConnectorDispatchTriggerUtil.addDispatchTrigger(objectEntry);
		}
	}

	private void _onAfterRemove(ObjectEntry objectEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666")) {

			return;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (objectDefinition == null) {
			return;
		}

		if (Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				PIMObjectDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_CONNECTOR)) {

			PIMConnectorDispatchTriggerUtil.deleteDispatchTrigger(objectEntry);

			_deletePIMConnectorConfigurationObjectEntry(objectEntry);
		}
		else if (_isPIMProductObjectEntry(objectEntry)) {
			_deletePIMLinkObjectEntries(objectEntry);
		}
	}

	private void _onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666")) {

			return;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (objectDefinition == null) {
			return;
		}

		if (Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				PIMObjectDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_CONNECTOR)) {

			PIMConnectorDispatchTriggerUtil.updateDispatchTrigger(objectEntry);
		}
		else if (!Objects.equals(
					originalObjectEntry.getExternalReferenceCode(),
					objectEntry.getExternalReferenceCode()) &&
				 _isPIMProductObjectEntry(objectEntry)) {

			_updatePIMLinkObjectEntries(originalObjectEntry, objectEntry);
		}
	}

	private void _onBeforeCreate(ObjectEntry objectEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666") ||
			(objectEntry.getObjectDefinition() == null)) {

			return;
		}

		if (_isPIMConnectorFieldMappingRuleObjectEntry(objectEntry)) {
			_validatePIMConnectorFieldMappingRuleObjectEntry(null, objectEntry);
		}
		else if (_isPIMLinkObjectEntry(objectEntry)) {
			_validatePIMLinkObjectEntry(objectEntry);
		}
		else if (_isPIMProductObjectEntry(objectEntry)) {
			_setObjectEntryFolderId(objectEntry);
		}
	}

	private void _onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666") ||
			(objectEntry.getObjectDefinition() == null)) {

			return;
		}

		if (_isPIMConnectorFieldMappingRuleObjectEntry(objectEntry)) {
			_validatePIMConnectorFieldMappingRuleObjectEntry(
				originalObjectEntry, objectEntry);
		}
		else if (_isPIMConnectorObjectEntry(objectEntry)) {
			_validatePIMConnectorObjectEntry(originalObjectEntry, objectEntry);
		}
	}

	private void _setObjectEntryFolderId(ObjectEntry objectEntry)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntry.getObjectEntryFolderId());

		if ((objectEntryFolder == null) ||
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			objectEntryFolder =
				PIMObjectEntryFolderUtil.getOrAddProductsObjectEntryFolder(
					_groupLocalService.getGroup(objectEntry.getGroupId()),
					_objectEntryFolderLocalService);

			objectEntry.setObjectEntryFolderId(
				objectEntryFolder.getObjectEntryFolderId());
		}
	}

	private void _updatePIMLinkObjectEntries(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws PortalException {

		List<Map<String, Serializable>> valuesList =
			PIMLinkUtil.getValuesListByClassExternalReferenceCode(
				originalObjectEntry.getExternalReferenceCode(),
				objectEntry.getModelClassName(), objectEntry.getCompanyId(),
				_filterFactory, objectEntry.getGroupId());

		if (valuesList.isEmpty()) {
			return;
		}

		ObjectDefinition objectDefinition =
			PIMLinkUtil.getPIMLinkObjectDefinition(objectEntry.getCompanyId());

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			for (Map<String, Serializable> values : valuesList) {
				String fieldName = "sourceClassExternalReferenceCode";

				if (Objects.equals(
						MapUtil.getString(
							values, "targetClassExternalReferenceCode"),
						originalObjectEntry.getExternalReferenceCode()) &&
					Objects.equals(
						MapUtil.getString(values, "targetClassName"),
						objectEntry.getModelClassName())) {

					fieldName = "targetClassExternalReferenceCode";
				}

				_objectEntryLocalService.partialUpdateObjectEntry(
					PrincipalThreadLocal.getUserId(),
					MapUtil.getLong(
						values, objectDefinition.getPKObjectFieldName()),
					0,
					HashMapBuilder.<String, Serializable>put(
						fieldName, objectEntry.getExternalReferenceCode()
					).build(),
					_getServiceContext(
						objectEntry.getCompanyId(), objectEntry.getGroupId()));
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}
	}

	private void _validatePIMConnectorFieldMappingRuleObjectEntry(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws PortalException {

		Map<String, Serializable> values = objectEntry.getValues();

		long pimConnectorObjectEntryId = MapUtil.getLong(
			values, "r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId");

		if ((originalObjectEntry != null) &&
			(pimConnectorObjectEntryId != MapUtil.getLong(
				originalObjectEntry.getValues(),
				"r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId"))) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList(
					"r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId"),
				"\"r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId\" " +
					"is invalid",
				"x-is-invalid");
		}

		String channelFieldName = MapUtil.getString(values, "channelFieldName");

		PIMConnectorChannelField pimConnectorChannelField =
			_fetchPIMConnectorChannelField(
				channelFieldName, pimConnectorObjectEntryId);

		if ((pimConnectorChannelField == null) ||
			!Objects.equals(
				pimConnectorChannelField.getType(),
				ObjectFieldConstants.BUSINESS_TYPE_TEXT)) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList("channelFieldName"),
				"\"channelFieldName\" is invalid", "x-is-invalid");
		}

		if (!ArrayUtil.contains(
				new String[] {StringPool.BLANK, "lower", "upper"},
				MapUtil.getString(values, "textCase"))) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList("textCase"),
				"\"textCase\" is invalid", "x-is-invalid");
		}

		if (!ArrayUtil.contains(
				new int[] {0, 40, 80, 200, 1000},
				MapUtil.getInteger(values, "truncateLength"))) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList("truncateLength"),
				"\"truncateLength\" is invalid", "x-is-invalid");
		}

		if (MapUtil.getBoolean(values, "useFirstNonemptySource") &&
			pimConnectorChannelField.isMultiple()) {

			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList("useFirstNonemptySource"),
				"\"useFirstNonemptySource\" is invalid", "x-is-invalid");
		}

		int count = _objectEntryLocalService.getValuesListCount(
			new Long[] {objectEntry.getGroupId()}, objectEntry.getCompanyId(),
			objectEntry.getUserId(), objectEntry.getObjectDefinitionId(),
			_filterFactory.create(
				StringBundler.concat(
					"channelFieldName eq '", channelFieldName, "' and id ne '",
					objectEntry.getObjectEntryId(),
					"' and r_pimConnectorToPIMFieldMappingRules_",
					"l_pimConnectorId eq '", pimConnectorObjectEntryId, "'"),
				objectEntry.getObjectDefinition()),
			false, null);

		if (count > 0) {
			throw new ObjectEntryValuesException.InvalidObjectField(
				Collections.singletonList("channelFieldName"),
				"\"channelFieldName\" is invalid", "x-is-invalid");
		}
	}

	private void _validatePIMConnectorObjectEntry(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws PortalException {

		if (!Objects.equals(
				MapUtil.getString(originalObjectEntry.getValues(), "key"),
				MapUtil.getString(objectEntry.getValues(), "key"))) {

			throw new PIMConnectorException(
				"the-connector-type-cannot-be-changed-after-the-connector-is-" +
					"created");
		}
	}

	private void _validatePIMLinkObjectEntry(ObjectEntry objectEntry)
		throws PortalException {

		Map<String, Serializable> values = objectEntry.getValues();

		if (Validator.isNull(
				MapUtil.getString(
					values, "sourceClassExternalReferenceCode")) ||
			Validator.isNull(MapUtil.getString(values, "sourceClassName"))) {

			throw new UnsupportedOperationException();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinitionByClassName(
				objectEntry.getCompanyId(),
				MapUtil.getString(values, "sourceClassName"));

		objectEntry = _objectEntryLocalService.getObjectEntry(
			MapUtil.getString(values, "sourceClassExternalReferenceCode"),
			objectEntry.getGroupId(), objectDefinition.getObjectDefinitionId());

		PIMLinkUtil.checkPermission(objectEntry, ActionKeys.UPDATE);

		PIMLinkType pimLinkType = _pimLinkTypeRegistry.getPIMLinkType(
			MapUtil.getString(values, "type"));

		if ((pimLinkType == null) || !pimLinkType.isClustered()) {
			throw new UnsupportedOperationException();
		}

		objectEntry = PIMLinkUtil.fetchPIMLinkObjectEntry(
			objectEntry.getCompanyId(), _filterFactory,
			objectEntry.getGroupId(), objectEntry.getExternalReferenceCode(),
			objectEntry.getModelClassName(), MapUtil.getString(values, "type"));

		if (objectEntry != null) {
			throw new DuplicatePIMLinkException();
		}
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

	@Reference
	private PIMLinkTypeRegistry _pimLinkTypeRegistry;

}