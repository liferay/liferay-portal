/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.field.converter;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.localized.bundle.FunctionInfoLocalizedValue;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.info.field.type.util.ObjectFieldInfoFieldTypeUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.rest.context.path.RESTContextPathResolver;
import com.liferay.object.rest.context.path.RESTContextPathResolverRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class ObjectFieldInfoFieldConverter {

	public ObjectFieldInfoFieldConverter(
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectConfiguration objectConfiguration,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		ObjectStateFlowLocalService objectStateFlowLocalService,
		ObjectStateLocalService objectStateLocalService, Portal portal,
		RESTContextPathResolverRegistry restContextPathResolverRegistry,
		UserLocalService userLocalService) {

		_listTypeEntryLocalService = listTypeEntryLocalService;
		_objectConfiguration = objectConfiguration;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFieldSettingLocalService = objectFieldSettingLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_objectStateFlowLocalService = objectStateFlowLocalService;
		_objectStateLocalService = objectStateLocalService;
		_portal = portal;
		_restContextPathResolverRegistry = restContextPathResolverRegistry;
		_userLocalService = userLocalService;
	}

	public InfoField<?> getInfoField(
		boolean editable, String namespace, ObjectField objectField) {

		return _addAttributes(
			InfoField.builder(
			).infoFieldType(
				ObjectFieldInfoFieldTypeUtil.getInfoFieldType(objectField)
			).namespace(
				namespace
			).name(
				objectField.getName()
			).editable(
				editable
			).labelInfoLocalizedValue(
				InfoLocalizedValue.<String>builder(
				).defaultLocale(
					LocaleUtil.fromLanguageId(
						objectField.getDefaultLanguageId())
				).values(
					objectField.getLabelMap()
				).build()
			).localizable(
				objectField.isLocalized()
			).readOnly(
				Objects.equals(
					objectField.getReadOnly(),
					ObjectFieldConstants.READ_ONLY_TRUE)
			).required(
				objectField.isRequired()
			),
			objectField);
	}

	private InfoField<?> _addAttributes(
		InfoField.FinalStep finalStep, ObjectField objectField) {

		if (Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			finalStep.attribute(
				FileInfoFieldType.ALLOWED_FILE_EXTENSIONS,
				_getAcceptedFileExtensions(objectField)
			).attribute(
				FileInfoFieldType.FILE_SOURCE, _getFileSourceType(objectField)
			).attribute(
				FileInfoFieldType.MAX_FILE_SIZE,
				_getMaximumFileSize(objectField)
			);
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_DECIMAL)) {

			finalStep.attribute(
				NumberInfoFieldType.DECIMAL, true
			).attribute(
				NumberInfoFieldType.DECIMAL_PART_MAX_LENGTH, 16
			);
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER)) {

			finalStep.attribute(
				NumberInfoFieldType.MAX_VALUE,
				BigDecimal.valueOf(
					ObjectFieldValidationConstants.
						BUSINESS_TYPE_INTEGER_VALUE_MAX)
			).attribute(
				NumberInfoFieldType.MIN_VALUE,
				BigDecimal.valueOf(
					ObjectFieldValidationConstants.
						BUSINESS_TYPE_INTEGER_VALUE_MIN)
			);
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER)) {

			finalStep.attribute(
				NumberInfoFieldType.MAX_VALUE,
				BigDecimal.valueOf(
					ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX)
			).attribute(
				NumberInfoFieldType.MIN_VALUE,
				BigDecimal.valueOf(
					ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN)
			);
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT)) {

			finalStep.attribute(
				LongTextInfoFieldType.MAX_LENGTH,
				_getMaxLength(objectField, 65000));
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			finalStep.attribute(
				MultiselectInfoFieldType.OPTIONS,
				TransformUtil.transform(
					_listTypeEntryLocalService.getListTypeEntries(
						objectField.getListTypeDefinitionId()),
					listTypeEntry -> new OptionInfoFieldType(
						Objects.equals(
							ObjectFieldSettingUtil.getDefaultValue(
								null, objectField, null),
							listTypeEntry.getKey()),
						new FunctionInfoLocalizedValue<>(
							listTypeEntry::getName),
						listTypeEntry.getKey())));
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			finalStep.attribute(
				SelectInfoFieldType.OPTIONS,
				_getOptionInfoFieldTypes(objectField));
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL)) {

			finalStep.attribute(
				NumberInfoFieldType.DECIMAL, true
			).attribute(
				NumberInfoFieldType.DECIMAL_PART_MAX_LENGTH, 16
			).attribute(
				NumberInfoFieldType.MAX_VALUE,
				new BigDecimal(
					ObjectFieldValidationConstants.
						BUSINESS_TYPE_PRECISION_DECIMAL_VALUE_MAX)
			).attribute(
				NumberInfoFieldType.MIN_VALUE,
				new BigDecimal(
					ObjectFieldValidationConstants.
						BUSINESS_TYPE_PRECISION_DECIMAL_VALUE_MIN)
			);
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			finalStep.attribute(
				RelationshipInfoFieldType.LABEL_FIELD_NAME,
				_getRelationshipLabelFieldName(objectField)
			).attribute(
				RelationshipInfoFieldType.URL, _getRelationshipURL(objectField)
			).attribute(
				RelationshipInfoFieldType.VALUE_FIELD_NAME, "id"
			);
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_TEXT)) {

			finalStep.attribute(
				TextInfoFieldType.MAX_LENGTH, _getMaxLength(objectField, 280));
		}

		return finalStep.build();
	}

	private String _getAcceptedFileExtensions(ObjectField objectField) {
		ObjectFieldSetting acceptedFileExtensionsObjectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(), "acceptedFileExtensions");

		if (acceptedFileExtensionsObjectFieldSetting == null) {
			return StringPool.BLANK;
		}

		return acceptedFileExtensionsObjectFieldSetting.getValue();
	}

	private FileInfoFieldType.FileSourceType _getFileSourceType(
		ObjectField objectField) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(), "fileSource");

		if (objectFieldSetting == null) {
			return null;
		}

		if (Objects.equals(
				objectFieldSetting.getValue(), "documentsAndMedia")) {

			return FileInfoFieldType.FileSourceType.DOCUMENTS_AND_MEDIA;
		}
		else if (Objects.equals(
					objectFieldSetting.getValue(), "userComputer")) {

			return FileInfoFieldType.FileSourceType.USER_COMPUTER;
		}

		return null;
	}

	private long _getGroupId(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		try {
			ObjectScopeProvider objectScopeProvider =
				_objectScopeProviderRegistry.getObjectScopeProvider(
					objectDefinition.getScope());

			return objectScopeProvider.getGroupId(httpServletRequest);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return 0L;
		}
	}

	private LayoutDisplayPageObjectProvider
		_getLayoutDisplayPageObjectProvider() {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return null;
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if ((layoutDisplayPageObjectProvider == null) ||
			!(layoutDisplayPageObjectProvider.getDisplayObject() instanceof
				ObjectEntry)) {

			return null;
		}

		return layoutDisplayPageObjectProvider;
	}

	private long _getMaximumFileSize(ObjectField objectField) {
		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(), "maximumFileSize");

		long maximumFileSizeForGuestUsers =
			_objectConfiguration.maximumFileSizeForGuestUsers();

		if (objectFieldSetting == null) {
			return maximumFileSizeForGuestUsers;
		}

		long maximumFileSize = GetterUtil.getLong(
			objectFieldSetting.getValue());

		if ((maximumFileSizeForGuestUsers < maximumFileSize) &&
			_isGuestUser()) {

			maximumFileSize = maximumFileSizeForGuestUsers;
		}

		return maximumFileSize;
	}

	private long _getMaxLength(ObjectField objectField, long defaultMaxLength) {
		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(), "maxLength");

		if (objectFieldSetting == null) {
			return defaultMaxLength;
		}

		return GetterUtil.getLong(
			objectFieldSetting.getValue(), defaultMaxLength);
	}

	private List<OptionInfoFieldType> _getOptionInfoFieldTypes(
		ObjectField objectField) {

		String defaultValue = String.valueOf(
			ObjectFieldSettingUtil.getDefaultValue(null, objectField, null));

		if (!objectField.isState()) {
			return _getOptionInfoFieldTypes(
				defaultValue,
				_listTypeEntryLocalService.getListTypeEntries(
					objectField.getListTypeDefinitionId()));
		}

		String listTypeEntryKey = defaultValue;

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider();

		if (layoutDisplayPageObjectProvider != null) {
			ObjectEntry objectEntry =
				(ObjectEntry)layoutDisplayPageObjectProvider.getDisplayObject();

			if (objectEntry != null) {
				listTypeEntryKey = MapUtil.getString(
					objectEntry.getValues(), objectField.getName(),
					listTypeEntryKey);
			}
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), listTypeEntryKey);

		if (listTypeEntry == null) {
			return Collections.emptyList();
		}

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		ObjectState objectState =
			_objectStateLocalService.fetchObjectStateFlowObjectState(
				listTypeEntry.getListTypeEntryId(),
				objectStateFlow.getObjectStateFlowId());

		return _getOptionInfoFieldTypes(
			defaultValue,
			ListUtil.concat(
				Collections.singletonList(listTypeEntry),
				TransformUtil.transform(
					_objectStateLocalService.getNextObjectStates(
						objectState.getObjectStateId()),
					nextObjectState ->
						_listTypeEntryLocalService.fetchListTypeEntry(
							nextObjectState.getListTypeEntryId()))));
	}

	private List<OptionInfoFieldType> _getOptionInfoFieldTypes(
		String defaultValue, List<ListTypeEntry> listTypeEntries) {

		return TransformUtil.transform(
			listTypeEntries,
			listTypeEntry -> new OptionInfoFieldType(
				Objects.equals(defaultValue, listTypeEntry.getKey()),
				new FunctionInfoLocalizedValue<>(listTypeEntry::getName),
				listTypeEntry.getKey()));
	}

	private String _getRelationshipLabelFieldName(ObjectField objectField) {
		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectFieldId2(
					objectField.getObjectFieldId());

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		if (relatedObjectDefinition == null) {
			return "id";
		}

		ObjectField titleObjectField =
			_objectFieldLocalService.fetchObjectField(
				relatedObjectDefinition.getTitleObjectFieldId());

		if (titleObjectField == null) {
			return "id";
		}
		else if (Objects.equals(titleObjectField.getName(), "createDate")) {
			return "dateCreated";
		}
		else if (Objects.equals(titleObjectField.getName(), "modifiedDate")) {
			return "dateModified";
		}

		return titleObjectField.getName();
	}

	private String _getRelationshipURL(ObjectField objectField) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) || (serviceContext.getRequest() == null)) {
			return StringPool.BLANK;
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectFieldId2(
					objectField.getObjectFieldId());

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		if (relatedObjectDefinition == null) {
			return StringPool.BLANK;
		}

		RESTContextPathResolver restContextPathResolver =
			_restContextPathResolverRegistry.getRESTContextPathResolver(
				relatedObjectDefinition.getClassName());

		String restContextPath = restContextPathResolver.getRESTContextPath(
			_getGroupId(serviceContext.getRequest(), relatedObjectDefinition));

		return _portal.getPortalURL(serviceContext.getRequest()) +
			_portal.getPathContext() + restContextPath;
	}

	private boolean _isGuestUser() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return true;
		}

		User user = _userLocalService.fetchUser(serviceContext.getUserId());

		if ((user == null) || user.isGuestUser()) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFieldInfoFieldConverter.class);

	private final ListTypeEntryLocalService _listTypeEntryLocalService;
	private final ObjectConfiguration _objectConfiguration;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final ObjectStateFlowLocalService _objectStateFlowLocalService;
	private final ObjectStateLocalService _objectStateLocalService;
	private final Portal _portal;
	private final RESTContextPathResolverRegistry
		_restContextPathResolverRegistry;
	private final UserLocalService _userLocalService;

}