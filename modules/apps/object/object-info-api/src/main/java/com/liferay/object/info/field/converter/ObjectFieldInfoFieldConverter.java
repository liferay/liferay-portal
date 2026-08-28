/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.field.converter;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.EmailInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.PhoneNumberInfoFieldType;
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
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.info.field.type.util.ObjectFieldInfoFieldTypeUtil;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
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
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class ObjectFieldInfoFieldConverter {

	public ObjectFieldInfoFieldConverter(
		DDMExpressionFactory ddmExpressionFactory,
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
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry,
		UserLocalService userLocalService) {

		_ddmExpressionFactory = ddmExpressionFactory;
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
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;
		_userLocalService = userLocalService;
	}

	public InfoField<?> addRelationshipInfoFieldAttributes(
		InfoField.FinalStep finalStep, ObjectRelationship objectRelationship) {

		return finalStep.attribute(
			RelationshipInfoFieldType.INHERITANCE, objectRelationship.isEdge()
		).attribute(
			RelationshipInfoFieldType.LABEL_FIELD_NAME,
			_getRelationshipLabelFieldName(objectRelationship)
		).attribute(
			RelationshipInfoFieldType.MULTIPLE,
			objectRelationship.compareType(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)
		).attribute(
			RelationshipInfoFieldType.URL,
			_getRelationshipURL(objectRelationship)
		).attribute(
			RelationshipInfoFieldType.VALUE_FIELD_NAME, "id"
		).build();
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
				_getLabelInfoLocalizedValue(objectField)
			).localizable(
				objectField.isLocalized()
			).readOnly(
				_isReadOnly(objectField)
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
					ObjectFieldConstants.BUSINESS_TYPE_EMAIL_ADDRESS)) {

			finalStep.attribute(
				EmailInfoFieldType.PREFERRED_DOMAINS,
				_getPreferredDomains(objectField));
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
					ObjectFieldConstants.BUSINESS_TYPE_PHONE_NUMBER)) {

			finalStep.attribute(
				PhoneNumberInfoFieldType.COUNTRY,
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_COUNTRY, objectField)
			).attribute(
				PhoneNumberInfoFieldType.COUNTRY_SOURCE,
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_COUNTRY_SOURCE,
					objectField)
			);
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

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			return addRelationshipInfoFieldAttributes(
				finalStep, objectRelationship);
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
				objectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS);

		if (acceptedFileExtensionsObjectFieldSetting == null) {
			return StringPool.BLANK;
		}

		return acceptedFileExtensionsObjectFieldSetting.getValue();
	}

	private FileInfoFieldType.FileSourceType _getFileSourceType(
		ObjectField objectField) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_FILE_SOURCE);

		if (objectFieldSetting == null) {
			return null;
		}

		if (Objects.equals(
				objectFieldSetting.getValue(),
				ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT) ||
			Objects.equals(
				objectFieldSetting.getValue(),
				ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA)) {

			return FileInfoFieldType.FileSourceType.DOCUMENTS_AND_MEDIA;
		}
		else if (Objects.equals(
					objectFieldSetting.getValue(),
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT) ||
				 Objects.equals(
					 objectFieldSetting.getValue(),
					 ObjectFieldSettingConstants.
						 VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA)) {

			return FileInfoFieldType.FileSourceType.USER_COMPUTER;
		}

		return null;
	}

	private Group _getGroup(ServiceContext serviceContext) {
		try {
			return serviceContext.getScopeGroup();
		}
		catch (Exception exception) {
			_log.error(exception);
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

	private InfoLocalizedValue<String> _getLabelInfoLocalizedValue(
		ObjectField objectField) {

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			objectField.getDefaultLanguageId());

		return InfoLocalizedValue.<String>builder(
		).defaultLocale(
			defaultLocale
		).values(
			objectField.getLabelMap()
		).value(
			defaultLocale,
			objectField.getLabel(objectField.getDefaultLanguageId())
		).build();
	}

	private long _getMaximumFileSize(ObjectField objectField) {
		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE);

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
				objectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_MAX_LENGTH);

		if (objectFieldSetting == null) {
			return defaultMaxLength;
		}

		return GetterUtil.getLong(
			objectFieldSetting.getValue(), defaultMaxLength);
	}

	private ObjectEntry _getObjectEntry() {
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

		return (ObjectEntry)layoutDisplayPageObjectProvider.getDisplayObject();
	}

	private long _getObjectEntryGroupId(ServiceContext serviceContext) {
		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return 0;
		}

		Object infoItem = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (!(infoItem instanceof ObjectEntry)) {
			return 0;
		}

		ObjectEntry objectEntry = (ObjectEntry)infoItem;

		return objectEntry.getGroupId();
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

		ObjectEntry objectEntry = _getObjectEntry();

		if (objectEntry != null) {
			listTypeEntryKey = MapUtil.getString(
				objectEntry.getValues(), objectField.getName(),
				listTypeEntryKey);
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

	private List<String> _getPreferredDomains(ObjectField objectField) {
		if (!GetterUtil.getBoolean(
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_ENABLED,
					objectField))) {

			return Collections.emptyList();
		}

		return ListUtil.fromArray(
			StringUtil.split(
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_DOMAINS,
					objectField),
				StringPool.COMMA));
	}

	private String _getRelationshipLabelFieldName(
		ObjectRelationship objectRelationship) {

		ObjectDefinition relatedObjectDefinition = null;

		if (objectRelationship.compareType(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			relatedObjectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId2());
		}
		else {
			relatedObjectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId1());
		}

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

	private String _getRelationshipURL(ObjectRelationship objectRelationship) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) || (serviceContext.getRequest() == null)) {
			return StringPool.BLANK;
		}

		ObjectDefinition relatedObjectDefinition = null;

		if (objectRelationship.compareType(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			relatedObjectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId2());
		}
		else {
			relatedObjectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId1());
		}

		if (relatedObjectDefinition == null) {
			return StringPool.BLANK;
		}

		RESTContextPathResolver restContextPathResolver =
			_restContextPathResolverRegistry.getRESTContextPathResolver(
				relatedObjectDefinition.getClassName());

		String restContextPath = null;

		Group group = _getGroup(serviceContext);

		if ((group != null) && group.isCMS()) {
			long groupId = _getObjectEntryGroupId(serviceContext);

			restContextPath = restContextPathResolver.getRESTContextPath(
				groupId);
		}
		else {
			restContextPath = restContextPathResolver.getRESTContextPath(
				_getGroupId(
					serviceContext.getRequest(), relatedObjectDefinition));
		}

		String portalURL =
			_portal.getPortalURL(serviceContext.getRequest()) +
				_portal.getPathContext() + restContextPath;

		if (_systemObjectDefinitionManagerRegistry != null) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						relatedObjectDefinition.getName());

			if (systemObjectDefinitionManager != null) {
				String additionalAPIURLParameters =
					systemObjectDefinitionManager.
						getAdditionalAPIURLParameters();

				if (Validator.isNotNull(additionalAPIURLParameters)) {
					return portalURL + StringPool.QUESTION +
						additionalAPIURLParameters;
				}
			}
		}

		return portalURL;
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

	private boolean _isReadOnly(ObjectField objectField) {
		ObjectEntry objectEntry = _getObjectEntry();
		User user = ObjectEntryInfoItemUtil.getUser();

		try {
			if (ObjectFieldUtil.isReadOnly(
					_ddmExpressionFactory, objectEntry, objectField,
					(user != null) ? user.getUserId() :
						PrincipalThreadLocal.getUserId())) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFieldInfoFieldConverter.class);

	private final DDMExpressionFactory _ddmExpressionFactory;
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
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;
	private final UserLocalService _userLocalService;

}