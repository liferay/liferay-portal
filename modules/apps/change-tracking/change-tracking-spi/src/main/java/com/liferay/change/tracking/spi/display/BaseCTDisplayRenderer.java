/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import java.sql.Blob;

import java.text.Format;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
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
		writer.write("table-nowrap\">");

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		buildDisplay(
			new DisplayBuilderImpl<>(
				displayContext, getResourceBundle(displayContext.getLocale()),
				themeDisplay));

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

	protected ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(locale, getClass());
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

		public DisplayContext<T> getDisplayContext();

		public Locale getLocale();

		public T getModel();

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