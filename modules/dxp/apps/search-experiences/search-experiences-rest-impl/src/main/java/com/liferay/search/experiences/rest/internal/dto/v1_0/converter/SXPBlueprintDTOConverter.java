/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.asset.AssetSubtypeIdentifier;
import com.liferay.portal.search.asset.AssetSubtypeIdentifierBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.ElementInstance;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.util.ConfigurationUtil;
import com.liferay.search.experiences.rest.dto.v1_0.util.ElementInstanceUtil;
import com.liferay.search.experiences.rest.internal.dto.v1_0.converter.util.SXPDTOConverterUtil;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;
import com.liferay.search.experiences.service.SXPElementLocalService;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	enabled = false,
	property = "dto.class.name=com.liferay.search.experiences.model.SXPBlueprint",
	service = DTOConverter.class
)
public class SXPBlueprintDTOConverter
	implements DTOConverter
		<com.liferay.search.experiences.model.SXPBlueprint, SXPBlueprint> {

	@Override
	public String getContentType() {
		return SXPBlueprint.class.getSimpleName();
	}

	@Override
	public SXPBlueprint toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint =
			_sxpBlueprintLocalService.getSXPBlueprint(
				(Long)dtoConverterContext.getId());

		return toDTO(dtoConverterContext, sxpBlueprint);
	}

	@Override
	public SXPBlueprint toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint)
		throws Exception {

		return new SXPBlueprint() {
			{
				setCollectionProviderSubtypeName(
					() -> _getSubtypeName(
						sxpBlueprint.getCompanyId(),
						sxpBlueprint.getConfigurationJSON(),
						dtoConverterContext.getLocale()));
				setCollectionProviderTypeName(
					() -> _getTypeName(
						sxpBlueprint.getCompanyId(),
						sxpBlueprint.getConfigurationJSON(),
						dtoConverterContext.getLocale()));
				setConfiguration(
					() -> _toConfiguration(
						sxpBlueprint.getConfigurationJSON()));
				setCreateDate(sxpBlueprint::getCreateDate);
				setDescription(
					() -> _language.get(
						dtoConverterContext.getLocale(),
						sxpBlueprint.getDescription(
							dtoConverterContext.getLocale())));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						sxpBlueprint.getDescriptionMap()));
				setElementInstances(
					() -> _translateElementInstances(
						_toElementInstances(
							sxpBlueprint.getElementInstancesJSON()),
						dtoConverterContext.getLocale()));
				setExternalReferenceCode(
					sxpBlueprint::getExternalReferenceCode);
				setId(sxpBlueprint::getSXPBlueprintId);
				setModifiedDate(sxpBlueprint::getModifiedDate);
				setSchemaVersion(sxpBlueprint::getSchemaVersion);
				setTitle(
					() -> _language.get(
						dtoConverterContext.getLocale(),
						sxpBlueprint.getTitle(
							dtoConverterContext.getLocale())));
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						sxpBlueprint.getTitleMap()));
				setUserName(sxpBlueprint::getUserName);
				setVersion(sxpBlueprint::getVersion);
			}
		};
	}

	@Override
	public SXPBlueprint toDTO(
		com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint) {

		return new SXPBlueprint() {
			{
				setCollectionProviderSubtypeName(
					() -> _getSubtypeName(
						sxpBlueprint.getCompanyId(),
						sxpBlueprint.getConfigurationJSON(),
						LocaleUtil.fromLanguageId(
							sxpBlueprint.getDefaultLanguageId())));
				setCollectionProviderTypeName(
					() -> _getTypeName(
						sxpBlueprint.getCompanyId(),
						sxpBlueprint.getConfigurationJSON(),
						LocaleUtil.fromLanguageId(
							sxpBlueprint.getDefaultLanguageId())));
				setConfiguration(
					() -> _toConfiguration(
						sxpBlueprint.getConfigurationJSON()));
				setCreateDate(sxpBlueprint::getCreateDate);
				setDescription(sxpBlueprint::getDescription);
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, sxpBlueprint.getDescriptionMap()));
				setElementInstances(
					() -> _toElementInstances(
						sxpBlueprint.getElementInstancesJSON()));
				setExternalReferenceCode(
					sxpBlueprint::getExternalReferenceCode);
				setId(sxpBlueprint::getSXPBlueprintId);
				setModifiedDate(sxpBlueprint::getModifiedDate);
				setSchemaVersion(sxpBlueprint::getSchemaVersion);
				setTitle(sxpBlueprint::getTitle);
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, sxpBlueprint.getTitleMap()));
				setUserName(sxpBlueprint::getUserName);
				setVersion(sxpBlueprint::getVersion);
			}
		};
	}

	private AssetSubtypeIdentifier _getAssetSubtypeIdentifier(String json) {
		try {
			JSONObject configurationJSONObject = _jsonFactory.createJSONObject(
				json);

			if (configurationJSONObject == null) {
				return null;
			}

			JSONObject generalConfigurationJSONObject =
				configurationJSONObject.getJSONObject("generalConfiguration");

			if ((generalConfigurationJSONObject == null) ||
				!generalConfigurationJSONObject.getBoolean(
					"collectionProvider")) {

				return null;
			}

			return _assetSubtypeIdentifierBuilder.searchableAssetType(
				generalConfigurationJSONObject.getString(
					"collectionProviderType")
			).build();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private String _getSubtypeName(long companyId, String json, Locale locale) {
		AssetSubtypeIdentifier assetSubtypeIdentifier =
			_getAssetSubtypeIdentifier(json);

		if ((assetSubtypeIdentifier == null) ||
			Validator.isBlank(
				assetSubtypeIdentifier.getSubtypeExternalReferenceCode())) {

			return StringPool.BLANK;
		}

		try {
			if (StringUtil.equals(
					assetSubtypeIdentifier.getClassName(),
					DLFileEntry.class.getName())) {

				DLFileEntryType dlFileEntryType;

				if (StringUtil.equals(
						assetSubtypeIdentifier.getGroupExternalReferenceCode(),
						StringPool.BLANK)) {

					dlFileEntryType =
						_dlFileEntryTypeLocalService.
							getBasicDocumentDLFileEntryType();
				}
				else {
					Group group =
						_groupLocalService.getGroupByExternalReferenceCode(
							assetSubtypeIdentifier.
								getGroupExternalReferenceCode(),
							companyId);

					dlFileEntryType =
						_dlFileEntryTypeLocalService.
							getDLFileEntryTypeByExternalReferenceCode(
								assetSubtypeIdentifier.
									getSubtypeExternalReferenceCode(),
								group.getGroupId());
				}

				return dlFileEntryType.getName(locale);
			}

			if (StringUtil.equals(
					assetSubtypeIdentifier.getClassName(),
					JournalArticle.class.getName())) {

				Group group =
					_groupLocalService.getGroupByExternalReferenceCode(
						assetSubtypeIdentifier.getGroupExternalReferenceCode(),
						companyId);

				DDMStructure ddmStructure =
					_ddmStructureLocalService.
						fetchStructureByExternalReferenceCode(
							assetSubtypeIdentifier.
								getSubtypeExternalReferenceCode(),
							group.getGroupId(),
							_classNameLocalService.getClassNameId(
								JournalArticle.class));

				return ddmStructure.getName(locale);
			}
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(exception);
			}
		}

		return StringPool.BLANK;
	}

	private String _getTypeName(long companyId, String json, Locale locale) {
		AssetSubtypeIdentifier assetSubtypeIdentifier =
			_getAssetSubtypeIdentifier(json);

		if (assetSubtypeIdentifier == null) {
			return StringPool.BLANK;
		}

		String className = assetSubtypeIdentifier.getClassName();

		try {
			if (className.startsWith(
					ObjectDefinitionConstants.
						CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.
						fetchObjectDefinitionByClassName(companyId, className);

				if (objectDefinition != null) {
					return objectDefinition.getLabel(
						LocaleUtil.toLanguageId(locale));
				}
			}

			return ResourceActionsUtil.getModelResource(locale, className);
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(exception);
			}
		}

		return StringPool.BLANK;
	}

	private void _setLocalizedDescriptionAndTitle(
		Map<Locale, String> descriptionMap, String fallbackDescription,
		String fallbackTitle, Locale locale, SXPElement sxpElement,
		Map<Locale, String> titleMap) {

		sxpElement.setDescription(
			() -> SXPDTOConverterUtil.translate(
				fallbackDescription, _language, locale, descriptionMap));
		sxpElement.setTitle(
			() -> SXPDTOConverterUtil.translate(
				fallbackTitle, _language, locale, titleMap));
	}

	private Configuration _toConfiguration(String json) {
		try {
			return ConfigurationUtil.toConfiguration(json);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private ElementInstance[] _toElementInstances(String json) {
		try {
			return ElementInstanceUtil.toElementInstances(json);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private ElementInstance[] _translateElementInstances(
		ElementInstance[] elementInstances, Locale locale) {

		if (elementInstances == null) {
			return null;
		}

		for (ElementInstance elementInstance : elementInstances) {
			SXPElement sxpElement = elementInstance.getSxpElement();

			ElementDefinition elementDefinition =
				sxpElement.getElementDefinition();

			sxpElement.setElementDefinition(
				() -> SXPDTOConverterUtil.translate(
					elementDefinition, _language, locale));

			try {
				com.liferay.search.experiences.model.SXPElement
					serviceBuilderSXPElement =
						_sxpElementLocalService.getSXPElement(
							(Long)sxpElement.getId());

				_setLocalizedDescriptionAndTitle(
					serviceBuilderSXPElement.getDescriptionMap(),
					serviceBuilderSXPElement.getFallbackDescription(),
					serviceBuilderSXPElement.getFallbackTitle(), locale,
					sxpElement, serviceBuilderSXPElement.getTitleMap());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		return elementInstances;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SXPBlueprintDTOConverter.class);

	@Reference
	private AssetSubtypeIdentifierBuilder _assetSubtypeIdentifierBuilder;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

	@Reference
	private SXPElementLocalService _sxpElementLocalService;

}