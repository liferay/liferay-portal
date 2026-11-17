/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.asset;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;

/**
 * @author Georgel Pop
 */
public class AssetAnalyticsAttributesProvider {

	public static final String ACTION_IMPRESSION = "impression";

	public static final String ACTION_VIEW = "view";

	public static final String FIELD_AUTHOR = "author";

	public static final String FIELD_CONTENT = "content";

	public static final String FIELD_DATE = "date";

	public static final String FIELD_TITLE = "title";

	public AssetAnalyticsAttributesProvider(
		AssetEntry assetEntry, AssetRenderer<?> assetRenderer, Locale locale) {

		_assetEntry = assetEntry;
		_assetRenderer = assetRenderer;
		_locale = locale;
	}

	public String buildAttributes(String action, String field) {
		if ((_assetEntry == null) || !_isAnalyticsEnabled()) {
			return StringPool.BLANK;
		}

		return HtmlUtil.buildData(_getAttributes(action, field));
	}

	private String _getAnalyticsExternalReferenceCode() {
		if (_assetRenderer == null) {
			return null;
		}

		if (_assetRenderer.getAssetObject() instanceof
				ExternalReferenceCodeModel externalReferenceCodeModel) {

			return externalReferenceCodeModel.getExternalReferenceCode();
		}

		return null;
	}

	private String _getAnalyticsObjectDefinitionLabel() {
		ObjectDefinition objectDefinition = _getObjectDefinition();

		if (objectDefinition == null) {
			return null;
		}

		return objectDefinition.getLabel(objectDefinition.getDefaultLocale());
	}

	private String _getAnalyticsObjectDefinitionName() {
		ObjectDefinition objectDefinition = _getObjectDefinition();

		if (objectDefinition == null) {
			return null;
		}

		String name = objectDefinition.getName();

		if (Validator.isNull(name)) {
			return null;
		}

		return TextFormatter.format(name, TextFormatter.K);
	}

	private String _getAnalyticsSubtype() {
		if (_assetRenderer == null) {
			return null;
		}

		if (_assetRenderer.getAssetObject() instanceof
				JournalArticle journalArticle) {

			return StringUtil.toLowerCase(journalArticle.getDDMStructureKey());
		}

		return null;
	}

	private String _getAnalyticsType() {
		String className = _assetEntry.getClassName();

		if (className.startsWith(_CLASS_NAME_OBJECT_DEFINITION)) {
			return "object-entry";
		}

		String type = _types.get(className);

		if (Validator.isNotNull(type)) {
			return type;
		}

		return className;
	}

	private Map<String, Object> _getAttributes(String action, String field) {
		return TreeMapBuilder.<String, Object>put(
			"analytics-asset-action", () -> action
		).put(
			"analytics-asset-field", () -> field
		).put(
			"analytics-asset-id", () -> String.valueOf(_assetEntry.getClassPK())
		).put(
			"analytics-asset-subtype", () -> _getAnalyticsSubtype()
		).put(
			"analytics-asset-title",
			() -> ((_assetRenderer != null) && (_locale != null)) ?
				_assetRenderer.getTitle(_locale) : null
		).put(
			"analytics-asset-type", () -> _getAnalyticsType()
		).put(
			"analytics-external-reference-code",
			() -> _getAnalyticsExternalReferenceCode()
		).put(
			"analytics-object-definition-label",
			() -> _getAnalyticsObjectDefinitionLabel()
		).put(
			"analytics-object-definition-name",
			() -> _getAnalyticsObjectDefinitionName()
		).build();
	}

	private ObjectDefinition _getObjectDefinition() {
		if (_objectDefinition != null) {
			return _objectDefinition;
		}

		String className = _assetEntry.getClassName();

		if (!className.startsWith(_CLASS_NAME_OBJECT_DEFINITION)) {
			return null;
		}

		_objectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinitionByClassName(
				_assetEntry.getCompanyId(), className);

		return _objectDefinition;
	}

	private boolean _isAnalyticsEnabled() {
		AnalyticsSettingsManager analyticsSettingsManager =
			_analyticsSettingsManagerSnapshot.get();

		if (analyticsSettingsManager == null) {
			return false;
		}

		try {
			return analyticsSettingsManager.isAnalyticsEnabled(
				_assetEntry.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private static final String _CLASS_NAME_OBJECT_DEFINITION =
		"com.liferay.object.model.ObjectDefinition";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetAnalyticsAttributesProvider.class);

	private static final Snapshot<AnalyticsSettingsManager>
		_analyticsSettingsManagerSnapshot = new Snapshot<>(
			AssetAnalyticsAttributesProvider.class,
			AnalyticsSettingsManager.class);
	private static final Map<String, String> _types = HashMapBuilder.put(
		BlogsEntry.class.getName(), "blog"
	).put(
		DLFileEntry.class.getName(), "document"
	).put(
		JournalArticle.class.getName(), "web-content"
	).build();

	private final AssetEntry _assetEntry;
	private final AssetRenderer<?> _assetRenderer;
	private final Locale _locale;
	private ObjectDefinition _objectDefinition;

}