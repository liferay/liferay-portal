/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.info.item.provider;

import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.info.item.ObjectEntryInfoItemFields;
import com.liferay.object.info.item.provider.util.ObjectEntryInfoItemFormProviderUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

/**
 * @author Carolina Barbosa
 */
public class SystemObjectEntryInfoItemFormProvider
	implements InfoItemFormProvider<SystemObjectEntry> {

	public SystemObjectEntryInfoItemFormProvider(
		DisplayPageInfoItemFieldSetProvider displayPageInfoItemFieldSetProvider,
		InfoItemFieldReaderFieldSetProvider infoItemFieldReaderFieldSetProvider,
		String itemClassName, ObjectActionLocalService objectActionLocalService,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		TemplateInfoItemFieldSetProvider templateInfoItemFieldSetProvider) {

		_displayPageInfoItemFieldSetProvider =
			displayPageInfoItemFieldSetProvider;
		_infoItemFieldReaderFieldSetProvider =
			infoItemFieldReaderFieldSetProvider;
		_itemClassName = itemClassName;
		_objectActionLocalService = objectActionLocalService;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldInfoFieldConverter = objectFieldInfoFieldConverter;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_templateInfoItemFieldSetProvider = templateInfoItemFieldSetProvider;
	}

	@Override
	public InfoForm getInfoForm() {
		try {
			return _getInfoForm(
				0,
				_displayPageInfoItemFieldSetProvider.getInfoFieldSet(
					_itemClassName, StringPool.BLANK,
					SystemObjectEntry.class.getSimpleName(), 0));
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			throw new RuntimeException(noSuchFormVariationException);
		}
	}

	@Override
	public InfoForm getInfoForm(String formVariationKey, long groupId)
		throws NoSuchFormVariationException {

		long objectDefinitionId = GetterUtil.getLong(formVariationKey);

		if (objectDefinitionId == 0) {
			objectDefinitionId = _objectDefinition.getObjectDefinitionId();
		}

		return _getInfoForm(
			objectDefinitionId,
			_displayPageInfoItemFieldSetProvider.getInfoFieldSet(
				_itemClassName, StringPool.BLANK,
				SystemObjectEntry.class.getSimpleName(), groupId));
	}

	@Override
	public InfoForm getInfoForm(SystemObjectEntry systemObjectEntry) {
		try {
			return _getInfoForm(
				_objectDefinition.getObjectDefinitionId(),
				_displayPageInfoItemFieldSetProvider.getInfoFieldSet(
					_itemClassName, StringPool.BLANK,
					SystemObjectEntry.class.getSimpleName(), 0));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(
				StringBundler.concat(
					"Unable to get object definition ",
					_objectDefinition.getObjectDefinitionId(),
					" for primary key ", systemObjectEntry.getClassPK()),
				portalException);
		}
	}

	private InfoForm _getInfoForm(
			long objectDefinitionId, InfoFieldSet displayPageInfoFieldSet)
		throws NoSuchFormVariationException {

		return ObjectEntryInfoItemFormProviderUtil.getInfoForm(
			InfoFieldSet.builder(
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.authorInfoField
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.createDateInfoField
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.expirationDateInfoField
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.externalReferenceCodeInfoField
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.modifiedDateInfoField
			).infoFieldSetEntry(
				unsafeConsumer -> {
					if (!FeatureFlagManagerUtil.isEnabled(
							_objectDefinition.getCompanyId(), "LPD-21926")) {

						return;
					}

					unsafeConsumer.accept(
						ObjectEntryInfoItemFields.getFriendlyURLInfoField(
							_objectDefinition));
				}
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.objectEntryIdInfoField
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.reviewDateInfoField
			).infoFieldSetEntry(
				ObjectEntryInfoItemFields.statusInfoField
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(getClass(), "basic-information")
			).name(
				"basic-information"
			).build(),
			displayPageInfoFieldSet, _infoItemFieldReaderFieldSetProvider,
			_itemClassName, _objectActionLocalService, _objectDefinition,
			objectDefinitionId, _objectDefinitionLocalService,
			_objectFieldInfoFieldConverter, _objectFieldLocalService,
			_objectRelationshipLocalService, _templateInfoItemFieldSetProvider);
	}

	private final DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;
	private final InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;
	private final String _itemClassName;
	private final ObjectActionLocalService _objectActionLocalService;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldInfoFieldConverter _objectFieldInfoFieldConverter;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final TemplateInfoItemFieldSetProvider
		_templateInfoItemFieldSetProvider;

}