/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.item.provider.util;

import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.type.ActionInfoFieldType;
import com.liferay.info.field.type.ImageInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.field.type.URLInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.info.item.ObjectEntryInfoItemFields;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryInfoItemFormProviderUtil {

	public static InfoForm getInfoForm(
			InfoFieldSet basicInformationInfoFieldSet,
			InfoFieldSet displayPageInfoFieldSet,
			InfoItemFieldReaderFieldSetProvider
				infoItemFieldReaderFieldSetProvider,
			String modelClassName,
			ObjectActionLocalService objectActionLocalService,
			ObjectDefinition objectDefinition, long objectDefinitionId,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			TemplateInfoItemFieldSetProvider templateInfoItemFieldSetProvider)
		throws NoSuchFormVariationException {

		return InfoForm.builder(
		).infoFieldSetEntry(
			basicInformationInfoFieldSet
		).<NoSuchFormVariationException>infoFieldSetEntry(
			unsafeConsumer -> {
				if (objectDefinitionId != 0) {
					ObjectDefinition currentObjectDefinition =
						objectDefinitionLocalService.fetchObjectDefinition(
							objectDefinitionId);

					if (currentObjectDefinition == null) {
						throw new NoSuchFormVariationException(
							String.valueOf(objectDefinitionId),
							new NoSuchObjectDefinitionException());
					}

					unsafeConsumer.accept(
						_getInfoFieldSet(
							true, false, currentObjectDefinition.getLabelMap(),
							currentObjectDefinition.getName(),
							ObjectField.class.getSimpleName(),
							currentObjectDefinition,
							objectDefinitionLocalService,
							objectFieldInfoFieldConverter,
							objectFieldLocalService,
							objectRelationshipLocalService, null));
				}
			}
		).infoFieldSetEntry(
			unsafeConsumer -> {
				for (ObjectField objectField :
						objectFieldLocalService.getObjectFields(
							objectDefinitionId, false)) {

					if (!objectField.compareBusinessType(
							ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

						continue;
					}

					unsafeConsumer.accept(
						InfoFieldSet.builder(
						).infoFieldSetEntry(
							InfoField.builder(
							).infoFieldType(
								URLInfoFieldType.INSTANCE
							).namespace(
								ObjectField.class.getSimpleName()
							).name(
								objectField.getObjectFieldId() + "#downloadURL"
							).labelInfoLocalizedValue(
								InfoLocalizedValue.localize(
									ObjectEntryInfoItemFields.class,
									"download-url")
							).build()
						).infoFieldSetEntry(
							InfoField.builder(
							).infoFieldType(
								TextInfoFieldType.INSTANCE
							).namespace(
								ObjectField.class.getSimpleName()
							).name(
								objectField.getObjectFieldId() + "#fileName"
							).labelInfoLocalizedValue(
								InfoLocalizedValue.localize(
									ObjectEntryInfoItemFields.class,
									"file-name")
							).build()
						).infoFieldSetEntry(
							InfoField.builder(
							).infoFieldType(
								TextInfoFieldType.INSTANCE
							).namespace(
								ObjectField.class.getSimpleName()
							).name(
								objectField.getObjectFieldId() + "#mimeType"
							).labelInfoLocalizedValue(
								InfoLocalizedValue.localize(
									ObjectEntryInfoItemFields.class,
									"mime-type")
							).build()
						).infoFieldSetEntry(
							InfoField.builder(
							).infoFieldType(
								ImageInfoFieldType.INSTANCE
							).namespace(
								ObjectField.class.getSimpleName()
							).name(
								objectField.getObjectFieldId() + "#previewURL"
							).labelInfoLocalizedValue(
								InfoLocalizedValue.localize(
									ObjectEntryInfoItemFields.class,
									"preview-url")
							).build()
						).infoFieldSetEntry(
							InfoField.builder(
							).infoFieldType(
								TextInfoFieldType.INSTANCE
							).namespace(
								ObjectField.class.getSimpleName()
							).name(
								objectField.getObjectFieldId() + "#size"
							).labelInfoLocalizedValue(
								InfoLocalizedValue.localize(
									ObjectEntryInfoItemFields.class, "size")
							).build()
						).labelInfoLocalizedValue(
							InfoLocalizedValue.<String>builder(
							).defaultLocale(
								LocaleUtil.fromLanguageId(
									objectField.getDefaultLanguageId())
							).values(
								objectField.getLabelMap()
							).build()
						).name(
							objectField.getName()
						).build());
				}
			}
		).infoFieldSetEntry(
			unsafeConsumer -> {
				for (ObjectRelationship objectRelationship :
						objectRelationshipLocalService.getObjectRelationships(
							objectDefinitionId,
							ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

					if (Objects.equals(
							objectDefinitionId,
							objectRelationship.getObjectDefinitionId1()) &&
						FeatureFlagManagerUtil.isEnabled("LPD-50377")) {

						return;
					}

					ObjectDefinition relatedObjectDefinition = null;

					if (FeatureFlagManagerUtil.isEnabled("LPD-60546") &&
						!Objects.equals(
							objectDefinitionId,
							objectRelationship.getObjectDefinitionId1())) {

						relatedObjectDefinition =
							objectDefinitionLocalService.fetchObjectDefinition(
								objectRelationship.getObjectDefinitionId1());
					}

					if ((relatedObjectDefinition == null) ||
						relatedObjectDefinition.isUnmodifiableSystemObject()) {

						continue;
					}

					Map<Locale, String> fieldSetLabelMap = new HashMap<>();

					Map<Locale, String> labelMap =
						relatedObjectDefinition.getLabelMap();

					for (Map.Entry<Locale, String> entry :
							labelMap.entrySet()) {

						Locale locale = entry.getKey();

						fieldSetLabelMap.put(
							locale,
							StringBundler.concat(
								objectRelationship.getLabel(locale),
								StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
								entry.getValue(),
								StringPool.CLOSE_PARENTHESIS));
					}

					unsafeConsumer.accept(
						_getInfoFieldSet(
							true, false, fieldSetLabelMap,
							objectRelationship.getName(),
							ObjectEntryInfoItemUtil.getInfoFieldNamespace(
								relatedObjectDefinition, objectRelationship),
							relatedObjectDefinition,
							objectDefinitionLocalService,
							objectFieldInfoFieldConverter,
							objectFieldLocalService,
							objectRelationshipLocalService, objectDefinition));
				}
			}
		).infoFieldSetEntry(
			templateInfoItemFieldSetProvider.getInfoFieldSet(modelClassName)
		).infoFieldSetEntry(
			displayPageInfoFieldSet
		).infoFieldSetEntry(
			infoItemFieldReaderFieldSetProvider.getInfoFieldSet(modelClassName)
		).infoFieldSetEntry(
			unsafeConsumer -> {
				InfoFieldSet.Builder infoFieldSetBuilder = InfoFieldSet.builder(
				).labelInfoLocalizedValue(
					InfoLocalizedValue.localize(
						ObjectEntryInfoItemFields.class, "actions")
				).name(
					"actions"
				);

				for (ObjectAction objectAction :
						objectActionLocalService.getObjectActions(
							objectDefinition.getObjectDefinitionId(),
							ObjectActionTriggerConstants.KEY_STANDALONE)) {

					unsafeConsumer.accept(
						infoFieldSetBuilder.infoFieldSetEntry(
							InfoField.builder(
							).infoFieldType(
								ActionInfoFieldType.INSTANCE
							).namespace(
								ObjectAction.class.getSimpleName()
							).name(
								objectAction.getName()
							).labelInfoLocalizedValue(
								InfoLocalizedValue.<String>builder(
								).defaultLocale(
									LocaleUtil.fromLanguageId(
										objectAction.getDefaultLanguageId())
								).values(
									objectAction.getLabelMap()
								).build()
							).build()
						).build());
				}
			}
		).labelInfoLocalizedValue(
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(
					objectDefinition.getDefaultLanguageId())
			).values(
				objectDefinition.getLabelMap()
			).build()
		).name(
			modelClassName
		).build();
	}

	private static InfoFieldSet _getInfoFieldSet(
		boolean editable, boolean friendlyURL, Map<Locale, String> labelMap,
		String name, String namespace, ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectDefinition parentObjectDefinition) {

		return InfoFieldSet.builder(
		).infoFieldSetEntry(
			unsafeConsumer -> {
				for (ObjectField objectField :
						objectFieldLocalService.getObjectFields(
							objectDefinition.getObjectDefinitionId())) {

					if (objectField.isMetadata()) {
						continue;
					}

					if (Validator.isNotNull(
							objectField.getRelationshipType())) {

						ObjectRelationship objectRelationship =
							objectRelationshipLocalService.
								fetchObjectRelationshipByObjectFieldId2(
									objectField.getObjectFieldId());

						if ((parentObjectDefinition != null) &&
							Objects.equals(
								objectRelationship.getObjectDefinitionId1(),
								parentObjectDefinition.
									getObjectDefinitionId())) {

							continue;
						}

						ObjectDefinition relatedObjectDefinition =
							objectDefinitionLocalService.fetchObjectDefinition(
								objectRelationship.getObjectDefinitionId1());

						if ((relatedObjectDefinition == null) ||
							!relatedObjectDefinition.isActive()) {

							continue;
						}
					}

					unsafeConsumer.accept(
						objectFieldInfoFieldConverter.getInfoField(
							editable, namespace, objectField));
				}

				if (friendlyURL) {
					unsafeConsumer.accept(
						ObjectEntryInfoItemFields.getFriendlyURLInfoField(
							objectDefinition.isEnableFriendlyURLCustomization(),
							name, namespace));
				}
			}
		).infoFieldSetEntry(
			unsafeConsumer -> {
				for (ObjectRelationship objectRelationship :
						objectRelationshipLocalService.getObjectRelationships(
							objectDefinition.getObjectDefinitionId(),
							ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

					if (objectRelationship.isSelf() ||
						Objects.equals(
							objectDefinition.getObjectDefinitionId(),
							objectRelationship.getObjectDefinitionId2()) ||
						!FeatureFlagManagerUtil.isEnabled("LPD-50377")) {

						continue;
					}

					ObjectDefinition relatedObjectDefinition =
						objectDefinitionLocalService.fetchObjectDefinition(
							objectRelationship.getObjectDefinitionId2());

					if (relatedObjectDefinition == null) {
						_log.error(
							new NoSuchObjectDefinitionException(
								String.valueOf(
									objectRelationship.
										getObjectDefinitionId1())));

						continue;
					}

					if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
						continue;
					}

					Map<Locale, String> fieldSetLabelMap = new HashMap<>();

					Map<Locale, String> parentLabelMap =
						relatedObjectDefinition.getLabelMap();

					for (Map.Entry<Locale, String> entry :
							parentLabelMap.entrySet()) {

						Locale locale = entry.getKey();

						fieldSetLabelMap.put(
							locale,
							StringBundler.concat(
								objectRelationship.getLabel(locale),
								StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
								entry.getValue(),
								StringPool.CLOSE_PARENTHESIS));
					}

					unsafeConsumer.accept(
						_getInfoFieldSet(
							true, false, fieldSetLabelMap,
							objectRelationship.getName(),
							ObjectEntryInfoItemUtil.getInfoFieldNamespace(
								relatedObjectDefinition, objectRelationship),
							relatedObjectDefinition,
							objectDefinitionLocalService,
							objectFieldInfoFieldConverter,
							objectFieldLocalService,
							objectRelationshipLocalService, objectDefinition));
				}
			}
		).labelInfoLocalizedValue(
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(
					objectDefinition.getDefaultLanguageId())
			).values(
				labelMap
			).build()
		).name(
			name
		).relationship(
			parentObjectDefinition != null
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryInfoItemFormProviderUtil.class);

}