/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.spi.display;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesConverterUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.LinkTag;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import java.sql.Blob;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Preston Crary
 */
public abstract class BaseCTDisplayRenderer<T extends BaseModel<T>>
	implements CTDisplayRenderer<T> {

	@Override
	public String getEditURL(HttpServletRequest httpServletRequest, T model)
		throws Exception {

		return null;
	}

	@Override
	public abstract Class<T> getModelClass();

	@Override
	public abstract String getTitle(Locale locale, T model)
		throws PortalException;

	@Override
	public String getTypeName(Locale locale) {
		Class<T> modelClass = getModelClass();

		return LanguageUtil.get(
			getResourceBundle(locale), "model.resource." + modelClass.getName(),
			modelClass.getName());
	}

	@Override
	public boolean isHideable(T model) {
		return false;
	}

	@Override
	public void render(DisplayContext<T> displayContext) throws Exception {
		HttpServletResponse httpServletResponse =
			displayContext.getHttpServletResponse();

		Writer writer = httpServletResponse.getWriter();

		writer.write("<div class=\"table-responsive\"><table class=\"");
		writer.write("publications-render-table table table-autofit ");
		writer.write("table-nowrap table-striped\">");

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		DisplayBuilder<T> displayBuilder = new DisplayBuilderImpl<>(
			displayContext, getResourceBundle(displayContext.getLocale()),
			themeDisplay);

		displayBuilder.displaySectionHeader("metadata");

		buildDisplay(displayBuilder);
		buildStructureDisplay(displayBuilder);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
			boolean showAllData = (Boolean)httpServletRequest.getAttribute(
				"showAllData");

			if (showAllData) {
				T model = displayContext.getModel();

				_buildTableContent(
					httpServletResponse, model.getModelAttributes());
			}
		}

		writer.write("</table></div>");
	}

	protected void buildDisplay(DisplayBuilder<T> displayBuilder)
		throws PortalException {

		T model = displayBuilder.getModel();

		Map<String, Function<T, Object>> attributeGetterFunctions =
			model.getAttributeGetterFunctions();

		for (Map.Entry<String, Function<T, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			Function<T, Object> function = entry.getValue();

			displayBuilder.display(
				CamelCaseUtil.fromCamelCase(entry.getKey()),
				function.apply(model));
		}
	}

	protected void buildStructureDisplay(DisplayBuilder<T> displayBuilder)
		throws Exception {

		T model = displayBuilder.getModel();

		Map<String, Function<T, Object>> attributeGetterFunctions =
			model.getAttributeGetterFunctions();

		if (attributeGetterFunctions.containsKey("DDMStructureId")) {
			Function<T, Object> ddmStructureIdGetterFunction =
				attributeGetterFunctions.get("DDMStructureId");

			Function<T, Object> idGetterFunction = attributeGetterFunctions.get(
				"id");

			if (idGetterFunction == null) {
				return;
			}

			DDMStructure ddmStructure =
				DDMStructureLocalServiceUtil.getStructure(
					(Long)ddmStructureIdGetterFunction.apply(model));

			DDMForm ddmForm = ddmStructure.getDDMForm();

			DDMFormValues ddmFormValues =
				DDMFieldLocalServiceUtil.getDDMFormValues(
					ddmForm, (Long)idGetterFunction.apply(model));

			if (ddmFormValues == null) {
				return;
			}

			ddmFormValues.setDDMFormFieldValues(
				DDMFormValuesConverterUtil.addMissingDDMFormFieldValues(
					ddmForm.getDDMFormFields(),
					ddmFormValues.getDDMFormFieldValuesMap(true)));

			Map<String, List<DDMFormFieldValue>> ddmFormFieldValues =
				ddmFormValues.getDDMFormFieldValuesMap(true);

			List<DDMFormFieldValue> imageDDMFormFieldValues = new ArrayList<>();
			List<DDMFormFieldValue> nonimageDDMFormFieldValues =
				new ArrayList<>();

			ddmFormFieldValues.forEach(
				(key, value) -> value.forEach(
					ddmFormFieldValue -> {
						DDMFormField ddmFormField =
							ddmFormFieldValue.getDDMFormField();

						if (StringUtil.equals(
								ddmFormField.getType(),
								DDMFormFieldTypeConstants.IMAGE)) {

							imageDDMFormFieldValues.add(ddmFormFieldValue);
						}
						else if (!StringUtil.equals(
									ddmFormField.getType(),
									DDMFormFieldTypeConstants.FIELDSET)) {

							nonimageDDMFormFieldValues.add(ddmFormFieldValue);
						}
					}));

			Locale locale = displayBuilder.getLocale();

			if (!nonimageDDMFormFieldValues.isEmpty()) {
				displayBuilder.displaySectionHeader("fields");

				nonimageDDMFormFieldValues.forEach(
					ddmFormFieldValue -> _buildStructureNonimageDisplay(
						ddmFormFieldValue, displayBuilder, locale));
			}

			ListUtil.isNotEmptyForEach(
				imageDDMFormFieldValues,
				ddmFormFieldValue -> _buildStructureImageDisplay(
					ddmFormFieldValue, displayBuilder, locale));
		}
	}

	protected String getDownloadLink(
		DisplayContext<?> displayContext, String version, long size,
		String fileName) {

		LinkTag linkTag = new LinkTag();

		linkTag.setDisplayType("primary");
		linkTag.setHref(displayContext.getDownloadURL(version, size, fileName));
		linkTag.setIcon("download");
		linkTag.setLabel("download");
		linkTag.setSmall(true);
		linkTag.setType("button");

		try {
			return linkTag.doTagAsString(
				displayContext.getHttpServletRequest(),
				displayContext.getHttpServletResponse());
		}
		catch (JspException jspException) {
			return ReflectionUtil.throwException(jspException);
		}
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(locale, getClass());
	}

	protected String renderDisplayPagePreview(
			AssetDisplayPageFriendlyURLProvider
				assetDisplayPageFriendlyURLProvider,
			DisplayContext<T> displayContext)
		throws PortalException {

		T model = displayContext.getModel();

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				model.getModelClass());

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			model.getModelClassName(), (Long)model.getPrimaryKeyObj());

		if (!AssetDisplayPageUtil.hasAssetDisplayPage(
				assetEntry.getGroupId(), assetEntry)) {

			return null;
		}

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			new ClassPKInfoItemIdentifier(assetEntry.getClassPK());

		String previewURL = assetDisplayPageFriendlyURLProvider.getFriendlyURL(
			new InfoItemReference(
				assetEntry.getClassName(), classPKInfoItemIdentifier),
			themeDisplay);

		previewURL = HttpComponentsUtil.addParameter(
			previewURL, "p_l_mode", Constants.PREVIEW);
		previewURL = HttpComponentsUtil.addParameter(
			previewURL, "previewCTCollectionId",
			assetEntry.getCtCollectionId());

		return StringBundler.concat(
			"<iframe frameborder=\"0\" onload=\"this.style.height = ",
			"(this.contentWindow.document.body.scrollHeight+20) + 'px';\" ",
			"src=\"", previewURL, "\" width=\"100%\"></iframe>");
	}

	protected interface DisplayBuilder<T> {

		public DisplayBuilder<T> display(String languageKey, Object value);

		public DisplayBuilder<T> display(
			String languageKey, Object value, boolean escape);

		public DisplayBuilder<T> display(
			String languageKey, Object value, boolean escape,
			boolean formatted);

		public DisplayBuilder<T> display(
			String languageKey, String value, boolean escape);

		public DisplayBuilder<T> display(
			String languageKey,
			UnsafeSupplier<Object, Exception> unsafeSupplier);

		public DisplayBuilder<T> display(
			String languageKey,
			UnsafeSupplier<Object, Exception> unsafeSupplier, boolean escape);

		public DisplayBuilder<T> display(
			String languageKey,
			UnsafeSupplier<Object, Exception> unsafeSupplier, boolean escape,
			boolean formatted);

		public DisplayBuilder<T> displaySectionHeader(String languageKey);

		public DisplayContext<T> getDisplayContext();

		public Locale getLocale();

		public T getModel();

	}

	private void _buildStructureImageDisplay(
		DDMFormFieldValue ddmFormFieldValue, DisplayBuilder<T> displayBuilder,
		Locale locale) {

		try {
			Value value = ddmFormFieldValue.getValue();

			DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

			LocalizedValue label = ddmFormField.getLabel();

			displayBuilder.displaySectionHeader(
				StringBundler.concat(
					LanguageUtil.get(locale, "image"), StringPool.COLON,
					StringPool.SPACE, label.getString(locale)));

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				value.getString(locale));

			DLFileEntry dlFileEntry =
				DLFileEntryLocalServiceUtil.fetchDLFileEntry(
					jsonObject.getLong("fileEntryId"));

			displayBuilder.display(
				"mime-type",
				(dlFileEntry != null) ? dlFileEntry.getMimeType() :
					StringPool.BLANK
			).display(
				"version",
				(dlFileEntry != null) ? dlFileEntry.getVersion() :
					StringPool.BLANK
			).display(
				"size",
				(dlFileEntry != null) ? dlFileEntry.getSize() : StringPool.BLANK
			).display(
				"download",
				() -> {
					if (dlFileEntry == null) {
						return StringPool.BLANK;
					}

					return getDownloadLink(
						displayBuilder.getDisplayContext(),
						dlFileEntry.getVersion(), dlFileEntry.getSize(),
						dlFileEntry.getFileName());
				},
				false
			);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private void _buildStructureNonimageDisplay(
		DDMFormFieldValue ddmFormFieldValue, DisplayBuilder<T> displayBuilder,
		Locale locale) {

		DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

		LocalizedValue label = ddmFormField.getLabel();

		String labelString = label.getString(locale);

		Value value = ddmFormFieldValue.getValue();

		if ((value == null) || (value.getString(locale) == null) ||
			StringUtil.equals(value.getString(locale), StringPool.BLANK)) {

			displayBuilder.display(labelString, StringPool.BLANK);

			return;
		}

		String valueString = value.getString(locale);

		if (StringUtil.equals(
				ddmFormField.getType(), DDMFormFieldTypeConstants.COLOR)) {

			displayBuilder.display(
				labelString,
				StringBundler.concat(
					"<span class=\"mr-2\">",
					StringPool.POUND.concat(valueString),
					"</span><span style=\"background-color: ",
					StringPool.POUND.concat(valueString),
					"; display: inline-block; height: 80%; vertical-align: ",
					"middle; width: 50%;\" />"),
				false);

			return;
		}

		if (StringUtil.equals(
				ddmFormField.getType(), DDMFormFieldTypeConstants.GRID)) {

			displayBuilder.display(
				labelString,
				_getGridOptionValues(ddmFormField, locale, valueString));

			return;
		}

		displayBuilder.display(
			labelString, _getOptionValue(ddmFormField, locale, valueString));
	}

	private void _buildTableContent(
		HttpServletResponse httpServletResponse,
		Map<String, Object> modelAttributes) {

		try {
			Writer writer = httpServletResponse.getWriter();

			for (Map.Entry<String, Object> entry : modelAttributes.entrySet()) {
				writer.write("<tr><td class=\"publications-key-td ");
				writer.write("table-cell-expand-small\">");

				writer.write(entry.getKey());

				writer.write("</td><td class=\"table-cell-expand\">");

				Object value = entry.getValue();

				if (Objects.equals(value, StringPool.BLANK)) {
					writer.write("null");
				}
				else {
					writer.write(String.valueOf(value));
				}

				writer.write("</td></tr>");
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _getGridLabelString(
		LocalizedValue localizedValue, String defaultLabelString,
		Locale locale) {

		if (localizedValue != null) {
			return HtmlUtil.escape(localizedValue.getString(locale));
		}

		return defaultLabelString;
	}

	private String _getGridOptionValues(
		DDMFormField ddmFormField, Locale locale, String optionValueString) {

		try {
			JSONObject valuesJSONObject = JSONFactoryUtil.createJSONObject(
				optionValueString);

			if (valuesJSONObject.length() == 0) {
				return StringPool.BLANK;
			}

			StringBundler sb = new StringBundler(valuesJSONObject.length() * 6);

			DDMFormFieldOptions columnsDDMFormFieldOptions =
				(DDMFormFieldOptions)ddmFormField.getProperty("columns");

			DDMFormFieldOptions rowsDDMFormFieldOptions =
				(DDMFormFieldOptions)ddmFormField.getProperty("rows");

			Set<String> rowOptions = rowsDDMFormFieldOptions.getOptionsValues();

			for (String rowOption : rowOptions) {
				if (!valuesJSONObject.has(rowOption)) {
					continue;
				}

				sb.append(StringPool.OPEN_CURLY_BRACE);

				LocalizedValue rowLabel =
					rowsDDMFormFieldOptions.getOptionLabels(rowOption);

				sb.append(
					StringUtil.quote(
						_getGridLabelString(rowLabel, rowOption, locale)));

				sb.append(": ");

				String columnOption = valuesJSONObject.getString(rowOption);

				LocalizedValue columnLabel =
					columnsDDMFormFieldOptions.getOptionLabels(columnOption);

				sb.append(
					StringUtil.quote(
						_getGridLabelString(
							columnLabel, columnOption, locale)));

				sb.append(StringPool.CLOSE_CURLY_BRACE);
				sb.append(StringPool.COMMA_AND_SPACE);
			}

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);
			}

			return sb.toString();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return StringPool.BLANK;
	}

	private String _getOptionValue(
		DDMFormField ddmFormField, Locale locale, String valueString) {

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		LocalizedValue optionLabel = ddmFormFieldOptions.getOptionLabels(
			valueString);

		if (optionLabel != null) {
			return optionLabel.getString(locale);
		}

		if (!StringUtil.startsWith(valueString, StringPool.OPEN_BRACKET) ||
			!StringUtil.endsWith(valueString, StringPool.CLOSE_BRACKET)) {

			return valueString;
		}

		try {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray(valueString);

			if (jsonArray.length() == 0) {
				return StringPool.BLANK;
			}

			StringBundler sb = new StringBundler(jsonArray.length() + 1);

			sb.append(StringPool.OPEN_BRACKET);

			for (int i = 0; i < jsonArray.length(); i++) {
				if (i > 0) {
					sb.append(StringPool.COMMA_AND_SPACE);
				}

				String optionValueString = jsonArray.getString(i);

				if (optionValueString.isEmpty()) {
					continue;
				}

				LocalizedValue localizedValue =
					ddmFormFieldOptions.getOptionLabels(optionValueString);

				sb.append(StringUtil.quote(localizedValue.getString(locale)));
			}

			sb.append(StringPool.CLOSE_BRACKET);

			return sb.toString();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseCTDisplayRenderer.class);

	private static class DisplayBuilderImpl<T> implements DisplayBuilder<T> {

		@Override
		public DisplayBuilder<T> display(String languageKey, Object value) {
			return display(languageKey, value, true);
		}

		@Override
		public DisplayBuilder<T> display(
			String languageKey, Object value, boolean escape) {

			return display(languageKey, value, escape, false);
		}

		@Override
		public DisplayBuilder<T> display(
			String languageKey, Object value, boolean escape,
			boolean formatted) {

			HttpServletResponse httpServletResponse =
				_displayContext.getHttpServletResponse();

			try {
				Writer writer = httpServletResponse.getWriter();

				writer.write("<tr><td class=\"publications-key-td ");
				writer.write("table-cell-expand-small\">");
				writer.write(LanguageUtil.get(_resourceBundle, languageKey));
				writer.write("</td><td class=\"table-cell-expand\">");

				if (formatted) {
					writer.write("<pre>");
				}

				if (value instanceof Blob) {
					String downloadURL = _displayContext.getDownloadURL(
						languageKey, 0, null);

					if (downloadURL == null) {
						writer.write(
							LanguageUtil.get(_resourceBundle, "no-download"));
					}
					else {
						writer.write("<a href=\"");
						writer.write(downloadURL);
						writer.write("\" >");
						writer.write(
							LanguageUtil.get(_resourceBundle, "download"));
						writer.write("</a>");
					}
				}
				else if (value instanceof Date) {
					Format format = FastDateFormatFactoryUtil.getDateTime(
						_resourceBundle.getLocale(),
						_themeDisplay.getTimeZone());

					writer.write(format.format(value));
				}
				else {
					if (escape) {
						writer.write(HtmlUtil.escape(String.valueOf(value)));
					}
					else {
						writer.write(String.valueOf(value));
					}
				}

				if (formatted) {
					writer.write("</pre>");
				}

				writer.write("</td></tr>");
			}
			catch (IOException ioException) {
				throw new UncheckedIOException(ioException);
			}

			return this;
		}

		@Override
		public DisplayBuilder<T> display(
			String languageKey, String value, boolean escape) {

			return display(languageKey, value, escape, false);
		}

		@Override
		public DisplayBuilder<T> display(
			String languageKey,
			UnsafeSupplier<Object, Exception> unsafeSupplier) {

			return display(languageKey, unsafeSupplier, true);
		}

		@Override
		public DisplayBuilder<T> display(
			String languageKey,
			UnsafeSupplier<Object, Exception> unsafeSupplier, boolean escape) {

			return display(languageKey, unsafeSupplier, escape, false);
		}

		@Override
		public DisplayBuilder<T> display(
			String languageKey,
			UnsafeSupplier<Object, Exception> unsafeSupplier, boolean escape,
			boolean formatted) {

			try {
				Object value = unsafeSupplier.get();

				if (value != null) {
					display(languageKey, value, escape, formatted);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}

			return this;
		}

		@Override
		public DisplayBuilder<T> displaySectionHeader(String languageKey) {
			HttpServletResponse httpServletResponse =
				_displayContext.getHttpServletResponse();

			try {
				Writer writer = httpServletResponse.getWriter();

				writer.write("<tr><td class=\"publications-section-header ");
				writer.write("table-cell-expand-small\">");
				writer.write(
					StringUtil.toUpperCase(
						LanguageUtil.get(_resourceBundle, languageKey)));
				writer.write("</td><td class=\"publications-section-header ");
				writer.write("table-cell-expand\" /></tr>");
			}
			catch (IOException ioException) {
				throw new UncheckedIOException(ioException);
			}

			return this;
		}

		@Override
		public DisplayContext<T> getDisplayContext() {
			return _displayContext;
		}

		@Override
		public Locale getLocale() {
			return _resourceBundle.getLocale();
		}

		@Override
		public T getModel() {
			return _displayContext.getModel();
		}

		private DisplayBuilderImpl(
			DisplayContext<T> displayContext, ResourceBundle resourceBundle,
			ThemeDisplay themeDisplay) {

			_displayContext = displayContext;
			_resourceBundle = resourceBundle;
			_themeDisplay = themeDisplay;
		}

		private final DisplayContext<T> _displayContext;
		private final ResourceBundle _resourceBundle;
		private final ThemeDisplay _themeDisplay;

	}

}