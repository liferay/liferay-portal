/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.results.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatConstants;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;

import jakarta.portlet.ResourceRequest;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author André de Oliveira
 * @author Bryan Engler
 */
public class RankingJSONBuilder {

	public RankingJSONBuilder(
		DLAppLocalService dlAppLocalService,
		FastDateFormatFactory fastDateFormatFactory,
		ResourceActions resourceActions, ResourceRequest resourceRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_dlAppLocalService = dlAppLocalService;
		_fastDateFormatFactory = fastDateFormatFactory;
		_resourceActions = resourceActions;

		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, new HashMap<String, Object>());
		_locale = themeDisplay.getLocale();
		_themeDisplay = themeDisplay;
	}

	public JSONObject build() {
		return JSONUtil.put(
			"author", _getAuthor()
		).put(
			"clicks", _document.getString("clicks")
		).put(
			"date", _getDateString()
		).put(
			"deleted", _deleted
		).put(
			"description", _getDescription()
		).put(
			"hidden", _hidden
		).put(
			"icon", _getIcon()
		).put(
			"id", _document.getString(Field.UID)
		).put(
			"pinned", _pinned
		).put(
			"title", _getTitle()
		).put(
			"type", _getType()
		).put(
			"viewURL", _viewURL
		);
	}

	public RankingJSONBuilder deleted(boolean deleted) {
		_deleted = deleted;

		return this;
	}

	public RankingJSONBuilder document(Document document) {
		_document = document;

		return this;
	}

	public RankingJSONBuilder hidden(boolean hidden) {
		_hidden = hidden;

		return this;
	}

	public RankingJSONBuilder pinned(boolean pinned) {
		_pinned = pinned;

		return this;
	}

	public RankingJSONBuilder viewURL(String viewURL) {
		_viewURL = viewURL;

		return this;
	}

	private boolean _containsMimeType(String[] mimeTypes, String mimeType) {
		for (String curMimeType : mimeTypes) {
			int pos = curMimeType.indexOf("/");

			if (pos != -1) {
				if (mimeType.equals(curMimeType)) {
					return true;
				}
			}
			else {
				if (mimeType.startsWith(curMimeType)) {
					return true;
				}
			}
		}

		return false;
	}

	private String _formatDate(Date date) {
		if (date == null) {
			return StringPool.BLANK;
		}

		Format format = _fastDateFormatFactory.getDateTime(
			FastDateFormatConstants.MEDIUM, FastDateFormatConstants.SHORT,
			_locale, _themeDisplay.getTimeZone());

		return format.format(date);
	}

	private String _getAuthor() {
		if (_isUser()) {
			return _document.getString("screenName");
		}

		return _document.getString(Field.USER_NAME);
	}

	private Date _getCreateDate() {
		String dateStringFieldValue = _document.getString(Field.CREATE_DATE);

		if (Validator.isNull(dateStringFieldValue)) {
			return null;
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			return dateFormat.parse(dateStringFieldValue);
		}
		catch (Exception exception) {
			throw new IllegalArgumentException(
				"Unable to parse date string: " + dateStringFieldValue,
				exception);
		}
	}

	private String _getDateString() {
		return _formatDate(_getCreateDate());
	}

	private String _getDescription() {
		String description = _document.getString(
			Field.getLocalizedName(_locale, Field.CONTENT));

		if (Validator.isBlank(description)) {
			description = _document.getString(Field.CONTENT);
		}

		if (Validator.isBlank(description)) {
			description = _document.getString(
				Field.getLocalizedName(_locale, Field.DESCRIPTION));
		}

		if (Validator.isBlank(description)) {
			description = _document.getString(Field.DESCRIPTION);
		}

		return StringUtil.shorten(description, 200);
	}

	private String _getIcon() {
		if (_isFileEntry()) {
			long entryClassPK = _document.getLong(Field.ENTRY_CLASS_PK);

			try {
				FileEntry fileEntry = _dlAppLocalService.getFileEntry(
					entryClassPK);

				return _getIconFileMimeType(fileEntry.getMimeType());
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get file entry for " + entryClassPK,
						portalException);
				}

				return "document-default";
			}
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_document.getString(Field.ENTRY_CLASS_NAME));

		if (assetRendererFactory != null) {
			return assetRendererFactory.getIconCssClass();
		}

		return null;
	}

	private String _getIconFileMimeType(String mimeType) {
		if (_containsMimeType(_dlConfiguration.codeFileMimeTypes(), mimeType)) {
			return "document-code";
		}
		else if (_containsMimeType(
					_dlConfiguration.compressedFileMimeTypes(), mimeType)) {

			return "document-compressed";
		}
		else if (_containsMimeType(
					_dlConfiguration.multimediaFileMimeTypes(), mimeType)) {

			if (mimeType.startsWith("image")) {
				return "document-image";
			}

			return "document-multimedia";
		}
		else if (_containsMimeType(
					_dlConfiguration.presentationFileMimeTypes(), mimeType)) {

			return "document-presentation";
		}
		else if (_containsMimeType(
					_dlConfiguration.spreadSheetFileMimeTypes(), mimeType)) {

			return "document-table";
		}
		else if (_containsMimeType(
					_dlConfiguration.textFileMimeTypes(), mimeType)) {

			return "document-text";
		}
		else if (_containsMimeType(
					_dlConfiguration.vectorialFileMimeTypes(), mimeType)) {

			return "document-pdf";
		}

		return "document-default";
	}

	private String _getTitle() {
		if (_isUser()) {
			return _document.getString("fullName");
		}

		String title = _document.getString(
			Field.getLocalizedName(_locale, Field.TITLE));

		if (Validator.isBlank(title)) {
			title = _document.getString(Field.TITLE);
		}

		if (Validator.isBlank(title)) {
			title = _document.getString(
				Field.getLocalizedName(_locale, Field.NAME));
		}

		if (Validator.isBlank(title)) {
			title = _document.getString(Field.NAME);
		}

		return title;
	}

	private String _getType() {
		String entryClassName = _document.getString(Field.ENTRY_CLASS_NAME);

		return _resourceActions.getModelResource(_locale, entryClassName);
	}

	private boolean _isFileEntry() {
		String entryClassName = _document.getString(Field.ENTRY_CLASS_NAME);

		return entryClassName.equals(DLFileEntryConstants.getClassName());
	}

	private boolean _isUser() {
		String entryClassName = _document.getString(Field.ENTRY_CLASS_NAME);

		return entryClassName.equals(User.class.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingJSONBuilder.class);

	private boolean _deleted;
	private final DLAppLocalService _dlAppLocalService;
	private final DLConfiguration _dlConfiguration;
	private Document _document;
	private final FastDateFormatFactory _fastDateFormatFactory;
	private boolean _hidden;
	private final Locale _locale;
	private boolean _pinned;
	private final ResourceActions _resourceActions;
	private final ThemeDisplay _themeDisplay;
	private String _viewURL;

}