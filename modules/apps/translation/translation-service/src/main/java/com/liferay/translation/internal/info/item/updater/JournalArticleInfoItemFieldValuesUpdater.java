/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.info.item.updater;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = "item.class.name=com.liferay.journal.model.JournalArticle",
	service = InfoItemFieldValuesUpdater.class
)
public class JournalArticleInfoItemFieldValuesUpdater
	implements InfoItemFieldValuesUpdater<JournalArticle> {

	@Override
	public JournalArticle updateFromInfoItemFieldValues(
			JournalArticle journalArticle,
			InfoItemFieldValues infoItemFieldValues)
		throws Exception {

		JournalArticle latestArticle =
			_journalArticleLocalService.getLatestArticle(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				WorkflowConstants.STATUS_ANY);

		User user = _userLocalService.fetchUser(latestArticle.getUserId());

		if (user == null) {
			user = _userLocalService.getUser(GuestOrUserUtil.getUserId());
		}

		Map<Locale, String> importedLocaleTitleMap = new HashMap<>();
		Map<Locale, String> importedLocaleDescriptionMap = new HashMap<>();
		Map<Locale, Map<String, List<String>>> importedLocaleContentMap =
			new HashMap<>();
		Set<Locale> translatedLocales = new HashSet<>();
		Map<String, List<String>> fieldNameContentMap = new HashMap<>();

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoLocalizedValue<Object> infoLocalizedValue =
				_getInfoLocalizedValue(infoFieldValue);

			if (infoLocalizedValue == null) {
				continue;
			}

			InfoField infoField = infoFieldValue.getInfoField();

			for (Locale locale : infoLocalizedValue.getAvailableLocales()) {
				if ((infoFieldValue.getValue(locale) == null) ||
					!(infoFieldValue.getValue(locale) instanceof String)) {

					continue;
				}

				translatedLocales.add(locale);

				String fieldName = infoField.getName();
				String fieldUniqueId = infoField.getUniqueId();

				String valueString = String.valueOf(
					infoFieldValue.getValue(locale));

				if (Objects.equals(fieldName, "description") &&
					fieldUniqueId.startsWith(
						JournalArticle.class.getSimpleName() +
							StringPool.UNDERLINE)) {

					importedLocaleDescriptionMap.put(locale, valueString);
				}
				else if (Objects.equals(fieldName, "title") &&
						 fieldUniqueId.startsWith(
							 JournalArticle.class.getSimpleName() +
								 StringPool.UNDERLINE)) {

					importedLocaleTitleMap.put(locale, valueString);
				}
				else {
					List<String> values = fieldNameContentMap.computeIfAbsent(
						fieldName, name -> new ArrayList<>());

					values.add(valueString);

					importedLocaleContentMap.put(locale, fieldNameContentMap);
				}
			}
		}

		Map<Locale, String> titleMap = latestArticle.getTitleMap();
		Map<Locale, String> descriptionMap = latestArticle.getDescriptionMap();

		DDMStructure ddmStructure = latestArticle.getDDMStructure();

		Fields fields = _ddmFormValuesToFieldsConverter.convert(
			ddmStructure, latestArticle.getDDMFormValues());

		for (Locale targetLocale : translatedLocales) {
			titleMap.put(
				targetLocale,
				_getTranslatedString(
					latestArticle.getTitle(targetLocale),
					latestArticle.getTitle(),
					importedLocaleTitleMap.get(targetLocale)));
			descriptionMap.put(
				targetLocale,
				_getTranslatedString(
					latestArticle.getDescription(targetLocale),
					latestArticle.getDescription(),
					importedLocaleDescriptionMap.get(targetLocale)));
			fields = _getTranslatedFields(
				fields, ddmStructure, importedLocaleContentMap, targetLocale);
		}

		int[] displayDateArray = _getDateArray(
			user, latestArticle.getDisplayDate());
		int[] expirationDateArray = _getDateArray(
			user, latestArticle.getExpirationDate());
		int[] reviewDateArray = _getDateArray(
			user, latestArticle.getReviewDate());

		ServiceContext serviceContext = _getServiceContext(latestArticle);

		return _journalArticleLocalService.updateArticle(
			user.getUserId(), latestArticle.getGroupId(),
			latestArticle.getFolderId(), latestArticle.getArticleId(),
			latestArticle.getVersion(), titleMap, descriptionMap,
			latestArticle.getFriendlyURLMap(),
			_journalConverter.getContent(
				ddmStructure, fields, ddmStructure.getGroupId()),
			latestArticle.getDDMTemplateKey(), latestArticle.getLayoutUuid(),
			displayDateArray[0], displayDateArray[1], displayDateArray[2],
			displayDateArray[3], displayDateArray[4], expirationDateArray[0],
			expirationDateArray[1], expirationDateArray[2],
			expirationDateArray[3], expirationDateArray[4],
			_isNeverExpire(latestArticle), reviewDateArray[0],
			reviewDateArray[1], reviewDateArray[2], reviewDateArray[3],
			reviewDateArray[4], _isNeverReview(latestArticle),
			latestArticle.isIndexable(), latestArticle.isSmallImage(),
			latestArticle.getSmallImageId(),
			latestArticle.getSmallImageSource(),
			latestArticle.getSmallImageURL(), null, null, null, serviceContext);
	}

	private void _addNewTranslatedDDMField(
			DDMStructure ddmStructure, Locale targetLocale, String ddmFieldName,
			Fields ddmFields, List<String> ddmFieldValues)
		throws Exception {

		Field ddmField = new Field(
			ddmStructure.getStructureId(), ddmFieldName,
			Collections.emptyList(), ddmFields.getDefaultLocale());

		ddmField.setValues(
			targetLocale,
			ListUtil.toList(
				ddmFieldValues,
				value -> _getSerializable(ddmField, value, targetLocale)));

		ddmFields.put(ddmField);

		_updateFieldsDisplay(ddmFields, ddmFieldName);
	}

	private AssetEntry _getAssetLinkEntry(
		long assetEntryId, AssetLink assetLink) {

		try {
			if ((assetEntryId > 0) ||
				(assetLink.getEntryId1() == assetEntryId)) {

				return _assetEntryLocalService.getEntry(
					assetLink.getEntryId2());
			}

			return _assetEntryLocalService.getEntry(assetLink.getEntryId1());
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private long _getAssetLinkEntryId(long assetEntryId, AssetLink assetLink) {
		AssetEntry assetEntry = _getAssetLinkEntry(assetEntryId, assetLink);

		return assetEntry.getEntryId();
	}

	private int[] _getDateArray(User user, Date date) {
		if (date == null) {
			return new int[] {0, 0, 0, 0, 0};
		}

		int[] dateArray = new int[5];

		Calendar calendar = CalendarFactoryUtil.getCalendar(user.getTimeZone());

		calendar.setTime(date);

		dateArray[0] = calendar.get(Calendar.MONTH);
		dateArray[1] = calendar.get(Calendar.DATE);
		dateArray[2] = calendar.get(Calendar.YEAR);
		dateArray[3] = calendar.get(Calendar.HOUR);
		dateArray[4] = calendar.get(Calendar.MINUTE);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			dateArray[3] += 12;
		}

		return dateArray;
	}

	private InfoLocalizedValue<Object> _getInfoLocalizedValue(
		InfoFieldValue<Object> infoFieldValue) {

		Object value = infoFieldValue.getValue();

		if (value instanceof InfoLocalizedValue) {
			return (InfoLocalizedValue)value;
		}

		return null;
	}

	private Serializable _getSerializable(
		Field field, String value, Locale targetLocale) {

		try {
			return FieldConstants.getSerializable(
				targetLocale, targetLocale, field.getDataType(), value);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private ServiceContext _getServiceContext(JournalArticle journalArticle)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());

		List<AssetLink> assetLinks = _assetLinkLocalService.getDirectLinks(
			assetEntry.getEntryId(), false);

		serviceContext.setAssetLinkEntryIds(
			ListUtil.toLongArray(
				assetLinks,
				assetLink -> _getAssetLinkEntryId(
					assetEntry.getEntryId(), assetLink)));

		serviceContext.setAssetPriority(assetEntry.getPriority());
		serviceContext.setAssetTagNames(assetEntry.getTagNames());

		ExpandoBridge expandoBridge = journalArticle.getExpandoBridge();

		serviceContext.setExpandoBridgeAttributes(
			expandoBridge.getAttributes());

		serviceContext.setScopeGroupId(journalArticle.getGroupId());

		if (journalArticle.getStatus() != WorkflowConstants.STATUS_APPROVED) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}
		else {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}

		return serviceContext;
	}

	private Fields _getTranslatedFields(
			Fields fields, DDMStructure ddmStructure,
			Map<Locale, Map<String, List<String>>> importedLocaleContentMap,
			Locale targetLocale)
		throws Exception {

		Map<String, List<String>> contentFieldMap =
			importedLocaleContentMap.get(targetLocale);

		if ((contentFieldMap == null) || contentFieldMap.isEmpty()) {
			return fields;
		}

		for (Map.Entry<String, List<String>> entry :
				contentFieldMap.entrySet()) {

			Field field = fields.get(entry.getKey());

			if (field != null) {
				field.setValues(
					targetLocale,
					ListUtil.toList(
						entry.getValue(),
						value -> _getSerializable(field, value, targetLocale)));
			}
			else if (ddmStructure.hasField(entry.getKey())) {
				_addNewTranslatedDDMField(
					ddmStructure, targetLocale, entry.getKey(), fields,
					entry.getValue());
			}
		}

		return fields;
	}

	private String _getTranslatedString(
		String currentString, String defaultString, String importedString) {

		if (importedString != null) {
			return importedString;
		}

		if (Validator.isNotNull(currentString)) {
			return currentString;
		}

		return defaultString;
	}

	private boolean _isNeverExpire(JournalArticle journalArticle) {
		if (journalArticle.getExpirationDate() == null) {
			return true;
		}

		return false;
	}

	private boolean _isNeverReview(JournalArticle journalArticle) {
		if (journalArticle.getReviewDate() == null) {
			return true;
		}

		return false;
	}

	private void _updateFieldsDisplay(Fields ddmFields, String fieldName) {
		String fieldsDisplayValue = StringBundler.concat(
			fieldName, DDM.INSTANCE_SEPARATOR, StringUtil.randomString());

		Field fieldsDisplayField = ddmFields.get(DDM.FIELDS_DISPLAY_NAME);

		String[] fieldsDisplayValues = StringUtil.split(
			(String)fieldsDisplayField.getValue());

		fieldsDisplayValues = ArrayUtil.append(
			fieldsDisplayValues, fieldsDisplayValue);

		fieldsDisplayField.setValue(StringUtil.merge(fieldsDisplayValues));
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private UserLocalService _userLocalService;

}