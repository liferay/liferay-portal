/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider;

import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.info.item.ObjectEntryInfoItemFields;
import com.liferay.object.info.item.provider.util.ObjectEntryInfoItemFormProviderUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.context.path.RESTContextPathResolverRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

/**
 * @author Jorge Ferrer
 * @author Guilherme Camacho
 */
public class ObjectEntryInfoItemFormProvider
	implements InfoItemFormProvider<ObjectEntry> {

	public ObjectEntryInfoItemFormProvider(
		AssetEntryInfoItemFieldSetProvider assetEntryInfoItemFieldSetProvider,
		DisplayPageInfoItemFieldSetProvider displayPageInfoItemFieldSetProvider,
		GroupLocalService groupLocalService, ObjectDefinition objectDefinition,
		InfoItemFieldReaderFieldSetProvider infoItemFieldReaderFieldSetProvider,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectActionLocalService objectActionLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		RESTContextPathResolverRegistry restContextPathResolverRegistry,
		TemplateInfoItemFieldSetProvider templateInfoItemFieldSetProvider,
		UserLocalService userLocalService) {

		_assetEntryInfoItemFieldSetProvider =
			assetEntryInfoItemFieldSetProvider;
		_displayPageInfoItemFieldSetProvider =
			displayPageInfoItemFieldSetProvider;
		_groupLocalService = groupLocalService;
		_objectDefinition = objectDefinition;
		_infoItemFieldReaderFieldSetProvider =
			infoItemFieldReaderFieldSetProvider;
		_listTypeEntryLocalService = listTypeEntryLocalService;
		_objectActionLocalService = objectActionLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldInfoFieldConverter = objectFieldInfoFieldConverter;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFieldSettingLocalService = objectFieldSettingLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_restContextPathResolverRegistry = restContextPathResolverRegistry;
		_templateInfoItemFieldSetProvider = templateInfoItemFieldSetProvider;
		_userLocalService = userLocalService;
	}

	@Override
	public InfoForm getInfoForm() {
		return _getInfoForm(0);
	}

	@Override
	public InfoForm getInfoForm(ObjectEntry objectEntry) {
		return _getInfoForm(objectEntry.getGroupId());
	}

	@Override
	public InfoForm getInfoForm(String formVariationKey, long groupId) {
		return _getInfoForm(groupId);
	}

	private long _getCategorizationGroupId(long groupId) {
		if (_objectDefinition.isCMS()) {
			Group group = _groupLocalService.fetchGroup(
				_objectDefinition.getCompanyId(), GroupConstants.CMS);

			if (group != null) {
				return group.getGroupId();
			}
		}

		return groupId;
	}

	private InfoFieldSet _getCategorizationInfoFieldSet(long groupId) {
		if (!_objectDefinition.isEnableCategorization()) {
			return null;
		}

		long categorizationGroupId = _getCategorizationGroupId(groupId);

		if (categorizationGroupId == 0) {
			return _assetEntryInfoItemFieldSetProvider.getInfoFieldSet(
				_objectDefinition.getClassName());
		}

		return _assetEntryInfoItemFieldSetProvider.getInfoFieldSet(
			_objectDefinition.getClassName(), 0, categorizationGroupId);
	}

	private InfoForm _getInfoForm(long groupId) {
		try {
			return ObjectEntryInfoItemFormProviderUtil.getInfoForm(
				InfoFieldSet.builder(
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.authorInfoField
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.createDateInfoField
				).infoFieldSetEntry(
					unsafeConsumer -> unsafeConsumer.accept(
						ObjectEntryInfoItemFields.getDisplayDateInfoField(
							_objectDefinition))
				).infoFieldSetEntry(
					unsafeConsumer -> unsafeConsumer.accept(
						ObjectEntryInfoItemFields.getExpirationDateInfoField(
							_objectDefinition))
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.externalReferenceCodeInfoField
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.modifiedDateInfoField
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.getFriendlyURLInfoField(
						_objectDefinition)
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.objectEntryIdInfoField
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.publishDateInfoField
				).infoFieldSetEntry(
					unsafeConsumer -> unsafeConsumer.accept(
						ObjectEntryInfoItemFields.getReviewDateInfoField(
							_objectDefinition))
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.statusInfoField
				).infoFieldSetEntry(
					ObjectEntryInfoItemFields.userProfileImageInfoField
				).labelInfoLocalizedValue(
					InfoLocalizedValue.localize(getClass(), "basic-information")
				).name(
					"basic-information"
				).build(),
				_getCategorizationInfoFieldSet(groupId),
				_displayPageInfoItemFieldSetProvider.getInfoFieldSet(
					_objectDefinition.getClassName(), StringPool.BLANK,
					ObjectEntry.class.getSimpleName(), groupId),
				_infoItemFieldReaderFieldSetProvider,
				_objectDefinition.getClassName(), _objectActionLocalService,
				_objectDefinition, _objectDefinition.getObjectDefinitionId(),
				_objectDefinitionLocalService, _objectFieldInfoFieldConverter,
				_objectFieldLocalService, _objectRelationshipLocalService,
				_templateInfoItemFieldSetProvider);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private final AssetEntryInfoItemFieldSetProvider
		_assetEntryInfoItemFieldSetProvider;
	private final DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;
	private final GroupLocalService _groupLocalService;
	private final InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;
	private final ListTypeEntryLocalService _listTypeEntryLocalService;
	private final ObjectActionLocalService _objectActionLocalService;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldInfoFieldConverter _objectFieldInfoFieldConverter;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final RESTContextPathResolverRegistry
		_restContextPathResolverRegistry;
	private final TemplateInfoItemFieldSetProvider
		_templateInfoItemFieldSetProvider;
	private final UserLocalService _userLocalService;

}