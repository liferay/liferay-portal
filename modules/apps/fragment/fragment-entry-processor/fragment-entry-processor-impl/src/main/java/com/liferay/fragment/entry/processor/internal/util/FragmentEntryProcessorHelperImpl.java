/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.internal.util;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.AlertTag;
import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.ImageInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.field.type.URLInfoFieldType;
import com.liferay.info.formatter.InfoCollectionTextFormatter;
import com.liferay.info.formatter.InfoTextFormatter;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.info.type.Labeled;
import com.liferay.info.type.WebImage;
import com.liferay.info.type.WebURL;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.net.URI;
import java.net.URISyntaxException;

import java.text.DateFormat;
import java.text.ParseException;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentEntryProcessorHelper.class)
public class FragmentEntryProcessorHelperImpl
	implements FragmentEntryProcessorHelper {

	@Override
	public String getEditableValue(JSONObject jsonObject, Locale locale) {
		String value = jsonObject.getString(
			_language.getLanguageId(locale), null);

		if (value != null) {
			return value;
		}

		return jsonObject.getString(
			_language.getLanguageId(LocaleUtil.getSiteDefault()),
			jsonObject.getString("defaultValue", null));
	}

	@Override
	public Object getFieldValue(
			JSONObject editableValueJSONObject,
			Map<InfoItemReference, InfoItemFieldValues> infoDisplaysFieldValues,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		InfoItemFieldMapped infoItemFieldMapped = getInfoItemFieldMapped(
			editableValueJSONObject, fragmentEntryProcessorContext);

		if (infoItemFieldMapped == null) {
			return null;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			infoItemFieldMapped.getClassName());

		InfoItemIdentifier infoItemIdentifier =
			infoItemFieldMapped.getInfoItemIdentifier();

		if ((trashHandler != null) &&
			(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			if (trashHandler.isInTrash(
					classPKInfoItemIdentifier.getClassPK())) {

				return null;
			}
		}

		InfoItemFieldValuesProvider infoItemFieldValuesProvider =
			_getInfoItemFieldValuesProvider(infoItemFieldMapped.getClassName());

		if (infoItemFieldValuesProvider == null) {
			return null;
		}

		InfoItemFieldValues infoItemFieldValues = infoDisplaysFieldValues.get(
			infoItemFieldMapped.getInfoItemReference());

		if ((infoItemFieldMapped.getObject() != null) &&
			(infoItemFieldValues == null)) {

			infoItemFieldValues =
				infoItemFieldValuesProvider.getInfoItemFieldValues(
					infoItemFieldMapped.getObject());

			infoDisplaysFieldValues.put(
				infoItemFieldMapped.getInfoItemReference(),
				infoItemFieldValues);
		}

		return getMappedInfoItemFieldValue(
			editableValueJSONObject, infoItemFieldMapped.getFieldName(),
			fragmentEntryProcessorContext, infoItemFieldValues);
	}

	@Override
	public long getFileEntryId(
		InfoItemReference infoItemReference, String fieldName, Locale locale) {

		return _getFileEntryId(
			infoItemReference.getClassName(), _getInfoItem(infoItemReference),
			fieldName, locale);
	}

	@Override
	public long getFileEntryId(
			long classNameId, long classPK, String fieldName, Locale locale)
		throws PortalException {

		if (classNameId == 0) {
			return 0;
		}

		String className = _portal.fetchClassName(classNameId);
		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			classPK);

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				infoItemIdentifier.getInfoItemServiceFilter());

		if (infoItemObjectProvider == null) {
			return 0;
		}

		Object object = infoItemObjectProvider.getInfoItem(infoItemIdentifier);

		if (object == null) {
			return 0;
		}

		return _getFileEntryId(className, object, fieldName, locale);
	}

	@Override
	public long getFileEntryId(String className, long classPK) {
		if (!Objects.equals(className, FileEntry.class.getName())) {
			return 0;
		}

		return classPK;
	}

	@Override
	public long getFileEntryId(WebImage webImage) {
		InfoItemReference infoItemReference = webImage.getInfoItemReference();

		if ((infoItemReference == null) ||
			!Objects.equals(
				infoItemReference.getClassName(), FileEntry.class.getName())) {

			return 0;
		}

		InfoItemIdentifier fileEntryInfoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(fileEntryInfoItemIdentifier instanceof
				ClassPKInfoItemIdentifier)) {

			return 0;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)fileEntryInfoItemIdentifier;

		return classPKInfoItemIdentifier.getClassPK();
	}

	@Override
	public InfoItemFieldMapped getInfoItemFieldMapped(
		JSONObject editableValueJSONObject,
		FragmentEntryProcessorContext fragmentEntryProcessorContext) {

		if (!isMapped(editableValueJSONObject) &&
			!isMappedCollection(editableValueJSONObject) &&
			!isMappedDisplayPage(editableValueJSONObject)) {

			return null;
		}

		String fieldName = StringPool.BLANK;
		InfoItemReference infoItemReference = null;
		Object object = null;

		if (isMapped(editableValueJSONObject)) {
			String className = _infoSearchClassMapperRegistry.getClassName(
				_portal.fetchClassName(
					editableValueJSONObject.getLong("classNameId")));
			String externalReferenceCode = editableValueJSONObject.getString(
				"externalReferenceCode");

			fieldName = editableValueJSONObject.getString("fieldId");

			InfoItemIdentifier infoItemIdentifier = null;

			if (Validator.isNotNull(externalReferenceCode)) {
				infoItemIdentifier = new ERCInfoItemIdentifier(
					externalReferenceCode);
			}
			else {
				infoItemIdentifier = new ClassPKInfoItemIdentifier(
					editableValueJSONObject.getLong("classPK"));
			}

			if ((fragmentEntryProcessorContext.getPreviewClassPK() > 0) &&
				(fragmentEntryProcessorContext.getPreviewClassPK() ==
					editableValueJSONObject.getLong("classPK"))) {

				infoItemIdentifier = new ClassPKInfoItemIdentifier(
					fragmentEntryProcessorContext.getPreviewClassPK());

				if (Validator.isNotNull(
						fragmentEntryProcessorContext.getPreviewVersion())) {

					infoItemIdentifier.setVersion(
						fragmentEntryProcessorContext.getPreviewVersion());
				}
			}

			infoItemReference = new InfoItemReference(
				className, infoItemIdentifier);

			object = _getInfoItem(infoItemReference);
		}
		else if (isMappedCollection(editableValueJSONObject)) {
			infoItemReference =
				fragmentEntryProcessorContext.getContextInfoItemReference();

			if (infoItemReference == null) {
				return null;
			}

			fieldName = editableValueJSONObject.getString("collectionFieldId");

			object = _getInfoItem(infoItemReference);
		}
		else if (isMappedDisplayPage(editableValueJSONObject)) {
			HttpServletRequest httpServletRequest =
				fragmentEntryProcessorContext.getHttpServletRequest();

			if (httpServletRequest == null) {
				return null;
			}

			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				(LayoutDisplayPageObjectProvider<?>)
					httpServletRequest.getAttribute(
						LayoutDisplayPageWebKeys.
							LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

			if (layoutDisplayPageObjectProvider == null) {
				return null;
			}

			InfoItemIdentifier infoItemIdentifier = null;

			if (Validator.isNotNull(
					layoutDisplayPageObjectProvider.
						getExternalReferenceCode())) {

				infoItemIdentifier = new ERCInfoItemIdentifier(
					layoutDisplayPageObjectProvider.getExternalReferenceCode());
			}
			else {
				infoItemIdentifier = new ClassPKInfoItemIdentifier(
					layoutDisplayPageObjectProvider.getClassPK());
			}

			infoItemReference = new InfoItemReference(
				layoutDisplayPageObjectProvider.getClassName(),
				infoItemIdentifier);

			fieldName = editableValueJSONObject.getString("mappedField");

			object = layoutDisplayPageObjectProvider.getDisplayObject();
		}

		return new InfoItemFieldMapped(fieldName, infoItemReference, object);
	}

	@Override
	public Object getMappedInfoItemFieldValue(
		JSONObject editableValueJSONObject, String fieldName,
		FragmentEntryProcessorContext fragmentEntryProcessorContext,
		InfoItemFieldValues infoItemFieldValues) {

		if (infoItemFieldValues == null) {
			return null;
		}

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(fieldName);

		if (infoFieldValue == null) {
			return null;
		}

		InfoField infoField = infoFieldValue.getInfoField();

		if (!_hasViewPermission(
				fragmentEntryProcessorContext.getHttpServletRequest(),
				infoItemFieldValues.getInfoItemReference())) {

			if (!Objects.equals(
					fragmentEntryProcessorContext.getMode(),
					FragmentEntryLinkConstants.EDIT) ||
				(infoField.getInfoFieldType() instanceof FileInfoFieldType) ||
				(infoField.getInfoFieldType() instanceof ImageInfoFieldType) ||
				(infoField.getInfoFieldType() instanceof URLInfoFieldType)) {

				return StringPool.BLANK;
			}

			AlertTag alertTag = new AlertTag();

			alertTag.setDefaultTitleDisabled(true);
			alertTag.setDisplayType("secondary");
			alertTag.setMessage(
				_language.get(
					fragmentEntryProcessorContext.getHttpServletRequest(),
					"this-content-cannot-be-displayed-due-to-permission-" +
						"restrictions"));
			alertTag.setSymbol("password-policies");

			try {
				return alertTag.doTagAsString(
					fragmentEntryProcessorContext.getHttpServletRequest(),
					fragmentEntryProcessorContext.getHttpServletResponse());
			}
			catch (JspException jspException) {
				if (_log.isDebugEnabled()) {
					_log.debug(jspException);
				}
			}

			return StringPool.BLANK;
		}

		JSONObject configJSONObject = editableValueJSONObject.getJSONObject(
			"config");

		if (infoField.isRepeatable()) {
			List<InfoFieldValue<Object>> infoFieldValues = new ArrayList<>(
				infoItemFieldValues.getInfoFieldValues(fieldName));

			infoFieldValue = _getSpecificIteration(
				infoFieldValues, configJSONObject);

			if (infoFieldValue == null) {
				return null;
			}
		}

		Object value = infoFieldValue.getValue(
			fragmentEntryProcessorContext.getLocale());

		if (value == null) {
			return StringPool.BLANK;
		}

		if (value instanceof Collection) {
			List<Object> list = new ArrayList<>((Collection<Object>)value);

			if (list.isEmpty()) {
				return StringPool.BLANK;
			}

			if (JSONUtil.isEmpty(configJSONObject)) {
				return _format(list, fragmentEntryProcessorContext.getLocale());
			}

			String iterationType = configJSONObject.getString("iterationType");

			if (Objects.equals(_ITERATION_TYPE_ALL, iterationType)) {
				return _format(list, fragmentEntryProcessorContext.getLocale());
			}

			value = _getSpecificIteration(list, configJSONObject);
		}

		if (value instanceof Date) {
			Date date = (Date)value;

			return _getDateValue(
				editableValueJSONObject, date,
				_getShortTimeStylePattern(
					fragmentEntryProcessorContext.getLocale()),
				fragmentEntryProcessorContext.getLocale());
		}
		else if (value instanceof KeyLocalizedLabelPair) {
			KeyLocalizedLabelPair keyLocalizedLabelPair =
				(KeyLocalizedLabelPair)value;

			return HtmlUtil.escape(
				keyLocalizedLabelPair.getLabel(
					fragmentEntryProcessorContext.getLocale()));
		}
		else if (value instanceof KeyValuePair) {
			KeyValuePair keyValuePair = (KeyValuePair)value;

			return HtmlUtil.escape(keyValuePair.getValue());
		}
		else if (value instanceof Labeled) {
			Labeled labeledFieldValue = (Labeled)value;

			return labeledFieldValue.getLabel(
				fragmentEntryProcessorContext.getLocale());
		}
		else if (value instanceof String) {
			infoField = infoFieldValue.getInfoField();

			if (infoField.getInfoFieldType() instanceof DateInfoFieldType) {
				Locale dateLocale = LocaleUtil.getSiteDefault();

				if (infoField.isLocalizable()) {
					InfoLocalizedValue<String> infoLocalizedValue =
						(InfoLocalizedValue<String>)infoFieldValue.getValue();

					dateLocale = infoLocalizedValue.getDefaultLocale();

					Set<Locale> availableLocales =
						infoLocalizedValue.getAvailableLocales();

					if (availableLocales.contains(
							fragmentEntryProcessorContext.getLocale())) {

						dateLocale = fragmentEntryProcessorContext.getLocale();
					}
				}

				try {
					DateFormat dateFormat =
						DateFormatFactoryUtil.getSimpleDateFormat(
							_getShortTimeStylePattern(dateLocale), dateLocale);

					Date date = dateFormat.parse(value.toString());

					return _getDateValue(
						editableValueJSONObject, date,
						_getShortTimeStylePattern(
							fragmentEntryProcessorContext.getLocale()),
						fragmentEntryProcessorContext.getLocale());
				}
				catch (ParseException parseException1) {
					if (_log.isDebugEnabled()) {
						_log.debug(parseException1);
					}

					try {
						DateFormat dateFormat =
							DateFormatFactoryUtil.getSimpleDateFormat(
								_getDefaultPattern(dateLocale), dateLocale);

						return _getDateValue(
							editableValueJSONObject,
							dateFormat.parse(value.toString()),
							_getDefaultPattern(
								fragmentEntryProcessorContext.getLocale()),
							fragmentEntryProcessorContext.getLocale());
					}
					catch (ParseException parseException2) {
						if (_log.isDebugEnabled()) {
							_log.debug(parseException2);
						}

						return value;
					}
				}
			}
			else if (infoField.getInfoFieldType() instanceof
						TextInfoFieldType) {

				URI uri = null;

				try {
					uri = HttpComponentsUtil.getURI((String)value);
				}
				catch (URISyntaxException uriSyntaxException) {
					if (_log.isDebugEnabled()) {
						_log.debug(uriSyntaxException);
					}
				}

				if (uri != null) {
					return value;
				}

				return HtmlUtil.escape((String)value);
			}

			return (String)value;
		}
		else if (value instanceof WebImage) {
			WebImage webImage = (WebImage)value;

			JSONObject valueJSONObject = webImage.toJSONObject();

			long fileEntryId = getFileEntryId(webImage);

			if (fileEntryId != 0) {
				valueJSONObject.put("fileEntryId", String.valueOf(fileEntryId));
			}

			return valueJSONObject;
		}
		else if (value instanceof WebURL) {
			WebURL webURL = (WebURL)value;

			return webURL.toJSONObject();
		}

		Class<?> fieldValueClass = value.getClass();

		InfoTextFormatter<Object> infoTextFormatter =
			(InfoTextFormatter<Object>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoTextFormatter.class, fieldValueClass.getName());

		if (infoTextFormatter != null) {
			return infoTextFormatter.format(
				value, fragmentEntryProcessorContext.getLocale());
		}

		return value.toString();
	}

	@Override
	public boolean hasViewPermission(
		JSONObject editableValueJSONObject,
		FragmentEntryProcessorContext fragmentEntryProcessorContext) {

		HttpServletRequest httpServletRequest =
			fragmentEntryProcessorContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-19955")) {

			return true;
		}

		InfoItemFieldMapped infoItemFieldMapped = getInfoItemFieldMapped(
			editableValueJSONObject, fragmentEntryProcessorContext);

		if (infoItemFieldMapped == null) {
			return true;
		}

		return _hasViewPermission(
			fragmentEntryProcessorContext.getHttpServletRequest(),
			infoItemFieldMapped.getInfoItemReference());
	}

	@Override
	public boolean isMapped(JSONObject jsonObject) {
		long classNameId = jsonObject.getLong("classNameId");
		long classPK = jsonObject.getLong("classPK");
		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");
		String fieldId = jsonObject.getString("fieldId");

		if ((classNameId > 0) &&
			((classPK > 0) || Validator.isNotNull(externalReferenceCode)) &&
			Validator.isNotNull(fieldId)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isMappedCollection(JSONObject jsonObject) {
		return jsonObject.has("collectionFieldId");
	}

	@Override
	public boolean isMappedDisplayPage(JSONObject jsonObject) {
		return Validator.isNotNull(jsonObject.get("mappedField"));
	}

	private String _format(List<Object> list, Locale locale) {
		Object firstItem = list.get(0);

		Class<?> firstItemClass = firstItem.getClass();

		InfoCollectionTextFormatter<Object> infoCollectionTextFormatter =
			_getInfoCollectionTextFormatter(firstItemClass.getName());

		return infoCollectionTextFormatter.format(list, locale);
	}

	private String _getDateValue(
		JSONObject editableValueJSONObject, Date date, String defaultPattern,
		Locale locale) {

		if (editableValueJSONObject == null) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				defaultPattern, locale);

			return dateFormat.format(date);
		}

		JSONObject configJSONObject = editableValueJSONObject.getJSONObject(
			"config");

		if (configJSONObject == null) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				defaultPattern, locale);

			return dateFormat.format(date);
		}

		JSONObject dateFormatJSONObject = configJSONObject.getJSONObject(
			"dateFormat");

		if (dateFormatJSONObject == null) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				defaultPattern, locale);

			return dateFormat.format(date);
		}

		String pattern = dateFormatJSONObject.getString(
			_language.getLanguageId(locale), null);

		if (Validator.isNull(pattern)) {
			pattern = dateFormatJSONObject.getString(
				_language.getLanguageId(LocaleUtil.getSiteDefault()), null);
		}

		if (Validator.isNull(pattern)) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				defaultPattern, locale);

			return dateFormat.format(date);
		}

		try {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				pattern, locale);

			return dateFormat.format(date);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				defaultPattern, locale);

			return dateFormat.format(date);
		}
	}

	private String _getDefaultPattern(Locale locale) {
		if (_defaultPatterns.containsKey(locale)) {
			return _defaultPatterns.get(locale);
		}

		String defaultPattern =
			DateTimeFormatterBuilder.getLocalizedDateTimePattern(
				FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);

		_defaultPatterns.put(locale, defaultPattern);

		return defaultPattern;
	}

	private long _getFileEntryId(
		String className, Object displayObject, String fieldName,
		Locale locale) {

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		if (infoItemFieldValuesProvider == null) {
			return 0;
		}

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValuesProvider.getInfoFieldValue(
				displayObject, fieldName);

		Object value = StringPool.BLANK;

		if (infoFieldValue != null) {
			value = infoFieldValue.getValue(locale);
		}

		if (!(value instanceof WebImage)) {
			return 0;
		}

		WebImage webImage = (WebImage)value;

		return getFileEntryId(webImage);
	}

	private InfoCollectionTextFormatter<Object> _getInfoCollectionTextFormatter(
		String itemClassName) {

		if (itemClassName.equals(String.class.getName())) {
			return _INFO_COLLECTION_TEXT_FORMATTER;
		}

		InfoCollectionTextFormatter<Object> infoCollectionTextFormatter =
			(InfoCollectionTextFormatter<Object>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoCollectionTextFormatter.class, itemClassName);

		if (infoCollectionTextFormatter == null) {
			infoCollectionTextFormatter = _INFO_COLLECTION_TEXT_FORMATTER;
		}

		return infoCollectionTextFormatter;
	}

	private Object _getInfoItem(InfoItemReference infoItemReference) {
		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, infoItemReference.getClassName(),
				infoItemIdentifier.getInfoItemServiceFilter());

		if (infoItemObjectProvider == null) {
			return null;
		}

		try {
			return infoItemObjectProvider.getInfoItem(infoItemIdentifier);
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}
		}

		return null;
	}

	private InfoItemFieldValuesProvider<Object> _getInfoItemFieldValuesProvider(
		String className) {

		className = _infoSearchClassMapperRegistry.getClassName(className);

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		if (infoItemFieldValuesProvider == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get info item form provider for class " +
						className);
			}

			return null;
		}

		return infoItemFieldValuesProvider;
	}

	private String _getShortTimeStylePattern(Locale locale) {
		if (_shortTimeStylePatterns.containsKey(locale)) {
			return _shortTimeStylePatterns.get(locale);
		}

		String sortTimeStylePattern =
			DateTimeFormatterBuilder.getLocalizedDateTimePattern(
				FormatStyle.SHORT, FormatStyle.SHORT, IsoChronology.INSTANCE,
				locale);

		_shortTimeStylePatterns.put(locale, sortTimeStylePattern);

		return sortTimeStylePattern;
	}

	private <T> T _getSpecificIteration(
		List<T> list, JSONObject configJSONObject) {

		if (list.isEmpty()) {
			return null;
		}

		if (JSONUtil.isEmpty(configJSONObject)) {
			return list.get(0);
		}

		String iterationType = configJSONObject.getString("iterationType");

		if (Objects.equals(iterationType, _ITERATION_TYPE_NUMBER)) {
			int iterationNumber = configJSONObject.getInt("iterationNumber", 0);

			if ((iterationNumber > 0) && (iterationNumber <= list.size())) {
				return list.get(iterationNumber - 1);
			}

			return null;
		}
		else if (Objects.equals(iterationType, _ITERATION_TYPE_LAST)) {
			return list.get(list.size() - 1);
		}

		return list.get(0);
	}

	private boolean _hasViewPermission(
		HttpServletRequest httpServletRequest,
		InfoItemReference infoItemReference) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-19955")) {

			return true;
		}

		try {
			InfoItemPermissionProvider infoItemPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemPermissionProvider.class,
					infoItemReference.getClassName());

			if ((infoItemPermissionProvider != null) &&
				!infoItemPermissionProvider.hasPermission(
					themeDisplay.getPermissionChecker(), infoItemReference,
					ActionKeys.VIEW)) {

				return false;
			}

			return true;
		}
		catch (InfoItemPermissionException infoItemPermissionException) {
			_log.error(infoItemPermissionException);

			return false;
		}
	}

	private static final InfoCollectionTextFormatter<Object>
		_INFO_COLLECTION_TEXT_FORMATTER =
			new CommaSeparatedInfoCollectionTextFormatter();

	private static final String _ITERATION_TYPE_ALL = "all";

	private static final String _ITERATION_TYPE_LAST = "last";

	private static final String _ITERATION_TYPE_NUMBER = "iteration-number";

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryProcessorHelperImpl.class);

	private static final Map<Locale, String> _defaultPatterns = new HashMap<>();
	private static final Map<Locale, String> _shortTimeStylePatterns =
		new HashMap<>();

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}