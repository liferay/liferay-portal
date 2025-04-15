/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.result.display.context.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.AssetRendererFactoryLookup;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.result.SearchResultContributor;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatConstants;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;
import com.liferay.portal.search.summary.Summary;
import com.liferay.portal.search.summary.SummaryBuilder;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultFieldDisplayContext;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.web.internal.util.SearchUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author André de Oliveira
 */
public class SearchResultSummaryDisplayContextBuilder {

	public SearchResultSummaryDisplayContext build() throws Exception {
		try {
			if (_documentBuilderFactory != null) {
				_document = _documentBuilderFactory.builder(
					_legacyDocument
				).build();
			}

			String entryClassName = _getFieldValueString(
				Field.ENTRY_CLASS_NAME);

			long entryClassPK = _getEntryClassPK();

			if (Validator.isBlank(entryClassName) && (entryClassPK == 0)) {
				return _buildFromPlainDocument();
			}

			SearchResultSummaryDisplayContext
				searchResultSummaryDisplayContext = build(
					entryClassName, entryClassPK);

			_checkViewURL(
				GetterUtil.getLong(_legacyDocument.get(Field.CLASS_NAME_ID)),
				GetterUtil.getLong(_legacyDocument.get(Field.CLASS_PK)),
				entryClassName, searchResultSummaryDisplayContext);

			return searchResultSummaryDisplayContext;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(exception.toString());
			}

			return _buildTemporarilyUnavailable();
		}
	}

	public SearchResultSummaryDisplayContextBuilder setAbridged(
		boolean abridged) {

		_abridged = abridged;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setAssetEntryLocalService(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder
		setAssetRendererFactoryLookup(
			AssetRendererFactoryLookup assetRendererFactoryLookup) {

		_assetRendererFactoryLookup = assetRendererFactoryLookup;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setClassNameLocalService(
		ClassNameLocalService classNameLocalService) {

		_classNameLocalService = classNameLocalService;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setCurrentURL(
		String currentURL) {

		_currentURL = currentURL;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setDocument(
		com.liferay.portal.kernel.search.Document document) {

		_legacyDocument = document;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setDocumentBuilderFactory(
		DocumentBuilderFactory documentBuilderFactory) {

		_documentBuilderFactory = documentBuilderFactory;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setFastDateFormatFactory(
		FastDateFormatFactory fastDateFormatFactory) {

		_fastDateFormatFactory = fastDateFormatFactory;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setGroupLocalService(
		GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setHighlightEnabled(
		boolean highlightEnabled) {

		_highlightEnabled = highlightEnabled;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setImageRequested(
		boolean imageRequested) {

		_imageRequested = imageRequested;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setIndexerRegistry(
		IndexerRegistry indexerRegistry) {

		_indexerRegistry = indexerRegistry;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setLanguage(
		Language language) {

		_language = language;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setLocale(Locale locale) {
		_locale = locale;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder
		setObjectDefinitionLocalService(
			ObjectDefinitionLocalService objectDefinitionLocalService) {

		_objectDefinitionLocalService = objectDefinitionLocalService;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setPortletURLFactory(
		PortletURLFactory portletURLFactory) {

		_portletURLFactory = portletURLFactory;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setRenderRequest(
		RenderRequest renderRequest) {

		_renderRequest = renderRequest;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setRenderResponse(
		RenderResponse renderResponse) {

		_renderResponse = renderResponse;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setRequest(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setResourceActions(
		ResourceActions resourceActions) {

		_resourceActions = resourceActions;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setSearchResultPreferences(
		SearchResultPreferences searchResultPreferences) {

		_searchResultPreferences = searchResultPreferences;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder
		setSearchResultViewURLSupplier(
			SearchResultViewURLSupplier searchResultViewURLSupplier) {

		_searchResultViewURLSupplier = searchResultViewURLSupplier;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setSummaryBuilderFactory(
		SummaryBuilderFactory summaryBuilderFactory) {

		_summaryBuilderFactory = summaryBuilderFactory;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setThemeDisplay(
		ThemeDisplay themeDisplay) {

		_themeDisplay = themeDisplay;

		return this;
	}

	public SearchResultSummaryDisplayContextBuilder setUserLocalService(
		UserLocalService userLocalService) {

		_userLocalService = userLocalService;

		return this;
	}

	protected SearchResultSummaryDisplayContext build(
			String className, long classPK)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			getAssetRendererFactoryByClassName(className);

		AssetRenderer<?> assetRenderer = null;

		if (assetRendererFactory != null) {
			long resourcePrimKey = GetterUtil.getLong(
				_getFieldValueString(Field.ROOT_ENTRY_CLASS_PK));

			if (resourcePrimKey > 0) {
				classPK = resourcePrimKey;
			}

			assetRenderer = getAssetRenderer(
				className, classPK, assetRendererFactory);
		}

		Summary summary = getSummary(className, assetRenderer);

		if (summary == null) {
			return null;
		}

		return build(
			summary, className, classPK, assetRendererFactory, assetRenderer);
	}

	protected SearchResultSummaryDisplayContext build(
			Summary summary, String className, long classPK,
			AssetRendererFactory<?> assetRendererFactory,
			AssetRenderer<?> assetRenderer)
		throws Exception {

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			new SearchResultSummaryDisplayContext();

		searchResultSummaryDisplayContext.setClassName(className);
		searchResultSummaryDisplayContext.setClassPK(classPK);

		if (Validator.isNotNull(summary.getContent())) {
			searchResultSummaryDisplayContext.setContent(summary.getContent());
			searchResultSummaryDisplayContext.setContentVisible(true);
		}

		searchResultSummaryDisplayContext.setHighlightedTitle(
			summary.getTitle());
		searchResultSummaryDisplayContext.setPortletURL(
			_portletURLFactory.getPortletURL());

		if (assetRenderer != null) {
			searchResultSummaryDisplayContext.setTitle(
				assetRenderer.getTitle(summary.getLocale()));
		}

		if (_abridged) {
			return searchResultSummaryDisplayContext;
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		_buildAssetCategoriesOrTags(
			assetEntry, searchResultSummaryDisplayContext);

		_buildAssetRendererURLDownload(
			assetRenderer, assetRendererFactory,
			searchResultSummaryDisplayContext, summary);
		_buildCreationDateString(searchResultSummaryDisplayContext);
		_buildCreatorUserName(searchResultSummaryDisplayContext);
		_buildCreatorUserPortrait(searchResultSummaryDisplayContext);
		_buildDocumentForm(searchResultSummaryDisplayContext);
		_buildImage(
			assetRenderer, assetRendererFactory,
			searchResultSummaryDisplayContext);
		_buildLocaleReminder(searchResultSummaryDisplayContext, summary);
		_buildModelResource(className, searchResultSummaryDisplayContext);
		_buildModifiedByUserName(searchResultSummaryDisplayContext);
		_buildModifiedByUserPortrait(searchResultSummaryDisplayContext);
		_buildModifiedDateString(searchResultSummaryDisplayContext);
		_buildPublishedDateString(searchResultSummaryDisplayContext);
		_buildUserPortrait(
			assetEntry, className, searchResultSummaryDisplayContext);
		_buildViewURL(className, classPK, searchResultSummaryDisplayContext);

		return searchResultSummaryDisplayContext;
	}

	protected long getAssetEntryUserId(AssetEntry assetEntry) {
		if (Objects.equals(assetEntry.getClassName(), User.class.getName())) {
			return assetEntry.getClassPK();
		}

		return assetEntry.getUserId();
	}

	protected AssetRenderer<?> getAssetRenderer(
		String className, long classPK,
		AssetRendererFactory<?> assetRendererFactory) {

		try {
			return assetRendererFactory.getAssetRenderer(classPK);
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Unable to get asset renderer for class ", className,
					" with primary key ", classPK),
				exception);
		}
	}

	protected AssetRendererFactory<?> getAssetRendererFactoryByClassName(
		String className) {

		if (_assetRendererFactoryLookup != null) {
			return _assetRendererFactoryLookup.
				getAssetRendererFactoryByClassName(className);
		}

		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(className);
	}

	protected Indexer<Object> getIndexer(String className) {
		if (_indexerRegistry != null) {
			return _indexerRegistry.getIndexer(className);
		}

		return IndexerRegistryUtil.getIndexer(className);
	}

	protected String getSearchResultViewURL(String className, long classPK) {
		if (_searchResultViewURLSupplier != null) {
			return _searchResultViewURLSupplier.getSearchResultViewURL();
		}

		return SearchUtil.getSearchResultViewURL(
			_renderRequest, _renderResponse, className, classPK,
			_searchResultPreferences.isViewInContext(), _currentURL);
	}

	protected Summary getSummary(
			String className, AssetRenderer<?> assetRenderer)
		throws SearchException {

		SummaryBuilder summaryBuilder = _summaryBuilderFactory.newInstance();

		summaryBuilder.setHighlight(_highlightEnabled);

		Indexer<?> indexer = getIndexer(className);

		if (indexer != null) {
			String snippet = _getFieldValueString(Field.SNIPPET);

			com.liferay.portal.kernel.search.Summary summary =
				indexer.getSummary(
					_legacyDocument, snippet, _renderRequest, _renderResponse);

			if (summary != null) {
				summaryBuilder.setContent(summary.getContent());
				summaryBuilder.setLocale(summary.getLocale());
				summaryBuilder.setMaxContentLength(
					summary.getMaxContentLength());

				if (assetRenderer != null) {
					summaryBuilder.setTitle(
						_appendStagingLabel(summary.getTitle(), assetRenderer));
				}
				else {
					summaryBuilder.setTitle(summary.getTitle());
				}

				return summaryBuilder.build();
			}
		}
		else if (assetRenderer != null) {
			summaryBuilder.setContent(assetRenderer.getSearchSummary(_locale));
			summaryBuilder.setLocale(_locale);
			summaryBuilder.setTitle(
				_appendStagingLabel(
					assetRenderer.getTitle(_locale), assetRenderer));

			return summaryBuilder.build();
		}

		return null;
	}

	private String _appendStagingLabel(
		String title, AssetRenderer<?> assetRenderer) {

		Group group = _groupLocalService.fetchGroup(assetRenderer.getGroupId());

		if ((group != null) && group.isStagingGroup()) {
			title = StringBundler.concat(
				title, StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
				_language.get(_httpServletRequest, "staged"),
				StringPool.CLOSE_PARENTHESIS);
		}

		return title;
	}

	private void _buildAssetCategoriesOrTags(
		AssetEntry assetEntry,
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		if (_hasAssetCategoriesOrTags(assetEntry)) {
			searchResultSummaryDisplayContext.setAssetCategoriesOrTagsVisible(
				true);
			searchResultSummaryDisplayContext.setFieldAssetCategoryIds(
				"category");
			searchResultSummaryDisplayContext.setFieldAssetTagNames("tag");
		}
	}

	private void _buildAssetRendererURLDownload(
			AssetRenderer<?> assetRenderer,
			AssetRendererFactory<?> assetRendererFactory,
			SearchResultSummaryDisplayContext searchResultSummaryDisplayContext,
			Summary summary)
		throws Exception {

		if (_hasAssetRendererURLDownload(assetRenderer)) {
			searchResultSummaryDisplayContext.setAssetRendererURLDownload(
				assetRenderer.getURLDownload(_themeDisplay));
			searchResultSummaryDisplayContext.
				setAssetRendererURLDownloadVisible(
					assetRendererFactory.hasPermission(
						_themeDisplay.getPermissionChecker(),
						assetRenderer.getClassPK(), ActionKeys.DOWNLOAD));
			searchResultSummaryDisplayContext.setAssetRendererDownloadSize(
				_getFieldValueLong("size"));
			searchResultSummaryDisplayContext.setTitle(
				assetRenderer.getTitle(summary.getLocale()));
		}
	}

	private void _buildCreationDateString(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		String dateString = StringUtil.trim(
			_getFieldValueString(Field.CREATE_DATE));

		if (Validator.isBlank(dateString)) {
			return;
		}

		searchResultSummaryDisplayContext.setCreationDateString(
			_formatDate(_parseDateStringFieldValue(dateString)));
		searchResultSummaryDisplayContext.setCreationDateVisible(true);
	}

	private void _buildCreatorUserName(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		User user = _userLocalService.fetchUser(
			_getFieldValueLong(Field.USER_ID));

		if (user != null) {
			searchResultSummaryDisplayContext.setCreatorUserName(
				user.getFullName());
			searchResultSummaryDisplayContext.setCreatorVisible(true);
		}
	}

	private void _buildCreatorUserPortrait(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		String creatorUserPortraitUrlString = _getPortraitURLString(
			_getFieldValueLong(Field.USER_ID));

		if (creatorUserPortraitUrlString != null) {
			searchResultSummaryDisplayContext.setCreatorUserPortraitURLString(
				creatorUserPortraitUrlString);
			searchResultSummaryDisplayContext.setCreatorUserPortraitVisible(
				true);
		}
	}

	private void _buildDocumentForm(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		if (_searchResultPreferences.isDisplayResultsInDocumentForm()) {
			searchResultSummaryDisplayContext.
				setDocumentFormFieldDisplayContexts(
					_buildFieldDisplayContexts(_getAllFieldNames()));
			searchResultSummaryDisplayContext.setDocumentFormVisible(true);
		}
	}

	private List<SearchResultFieldDisplayContext> _buildFieldDisplayContexts(
		List<String> fieldNames) {

		return TransformUtil.transform(
			ListUtil.sort(fieldNames), this::_buildFieldWithHighlight);
	}

	private SearchResultFieldDisplayContext _buildFieldWithHighlight(
		String fieldName) {

		SearchResultFieldDisplayContext searchResultFieldDisplayContext =
			new SearchResultFieldDisplayContext();

		searchResultFieldDisplayContext.setName(fieldName);
		searchResultFieldDisplayContext.setValuesToString(
			_getHighlightedFieldValue(fieldName));

		return searchResultFieldDisplayContext;
	}

	private SearchResultSummaryDisplayContext _buildFromPlainDocument()
		throws PortletException {

		Set<String> set = new LinkedHashSet<>(
			Arrays.asList(
				SearchStringUtil.splitAndUnquote(
					_searchResultPreferences.getFieldsToDisplay())));

		boolean star = set.remove(StringPool.STAR);

		boolean all;

		if (star || set.isEmpty()) {
			all = true;
		}
		else {
			all = false;
		}

		List<String> fieldNames = new ArrayList<>(set);

		List<String> allFieldNames = _getAllFieldNames();

		String contentFieldName;
		boolean contentVisible;
		boolean fieldsVisible;
		String titleFieldName;

		if (fieldNames.isEmpty()) {
			contentFieldName = null;
			contentVisible = false;
			fieldsVisible = true;
			titleFieldName = allFieldNames.get(0);
		}
		else if (fieldNames.size() == 1) {
			contentFieldName = null;
			contentVisible = false;
			fieldsVisible = false;
			titleFieldName = fieldNames.get(0);
		}
		else {
			contentFieldName = fieldNames.get(1);
			contentVisible = true;
			fieldsVisible = fieldNames.size() > 2;
			titleFieldName = fieldNames.get(0);
		}

		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			new SearchResultSummaryDisplayContext();

		searchResultSummaryDisplayContext.setContentVisible(contentVisible);
		searchResultSummaryDisplayContext.setFieldsVisible(fieldsVisible);

		SummaryBuilder summaryBuilder = _summaryBuilderFactory.newInstance();

		if (contentFieldName != null) {
			summaryBuilder.setContent(
				_getHighlightedValuesToString(contentFieldName));
		}

		summaryBuilder.setHighlight(_highlightEnabled);
		summaryBuilder.setTitle(_getHighlightedValuesToString(titleFieldName));

		Summary summary = summaryBuilder.build();

		if (contentVisible) {
			searchResultSummaryDisplayContext.setContent(summary.getContent());
		}

		if (fieldsVisible) {
			List<String> visibleFieldNames;

			if (all) {
				visibleFieldNames = allFieldNames;
			}
			else {
				visibleFieldNames = fieldNames;
			}

			searchResultSummaryDisplayContext.setFieldDisplayContexts(
				_buildFieldDisplayContexts(visibleFieldNames));
		}

		searchResultSummaryDisplayContext.setHighlightedTitle(
			summary.getTitle());
		searchResultSummaryDisplayContext.setPortletURL(
			_portletURLFactory.getPortletURL());
		searchResultSummaryDisplayContext.setViewURL(StringPool.BLANK);

		_buildDocumentForm(searchResultSummaryDisplayContext);

		return searchResultSummaryDisplayContext;
	}

	private void _buildImage(
		AssetRenderer<?> assetRenderer,
		AssetRendererFactory<?> assetRendererFactory,
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		if (!_imageRequested || (assetRendererFactory == null)) {
			return;
		}

		String iconCssClass = assetRendererFactory.getIconCssClass();

		if (Validator.isNotNull(iconCssClass)) {
			searchResultSummaryDisplayContext.setIconId(iconCssClass);
			searchResultSummaryDisplayContext.setIconVisible(true);
			searchResultSummaryDisplayContext.setPathThemeImages(
				_themeDisplay.getPathThemeImages());
		}

		try {
			String thumbnailURLString = assetRenderer.getThumbnailPath(
				_renderRequest);

			if (Validator.isNotNull(thumbnailURLString)) {
				searchResultSummaryDisplayContext.setThumbnailURLString(
					thumbnailURLString);
				searchResultSummaryDisplayContext.setThumbnailVisible(true);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private void _buildLocaleReminder(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext,
		Summary summary) {

		if (_locale != summary.getLocale()) {
			Locale summaryLocale = summary.getLocale();

			searchResultSummaryDisplayContext.setLocaleLanguageId(
				LocaleUtil.toLanguageId(summaryLocale));
			searchResultSummaryDisplayContext.setLocaleReminder(
				_language.format(
					_httpServletRequest,
					"this-result-comes-from-the-x-version-of-this-content",
					summaryLocale.getDisplayLanguage(_locale), false));

			searchResultSummaryDisplayContext.setLocaleReminderVisible(true);
		}
	}

	private void _buildModelResource(
		String className,
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		String modelResource = _resourceActions.getModelResource(
			_themeDisplay.getLocale(), className);

		if (className.startsWith(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					_themeDisplay.getCompanyId(), className);

			if (objectDefinition != null) {
				modelResource = objectDefinition.getLabel(
					_themeDisplay.getLocale());
			}
		}

		if (!Validator.isBlank(modelResource)) {
			searchResultSummaryDisplayContext.setModelResource(modelResource);
			searchResultSummaryDisplayContext.setModelResourceVisible(true);
		}
	}

	private void _buildModifiedByUserName(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		User user = _userLocalService.fetchUser(
			_getFieldValueLong("statusByUserId"));

		if (user != null) {
			searchResultSummaryDisplayContext.setModifiedByUserName(
				user.getFullName());
			searchResultSummaryDisplayContext.setModifiedByUserNameVisible(
				true);
		}
	}

	private void _buildModifiedByUserPortrait(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		String modifiedByUserPortraitURLString = _getPortraitURLString(
			_getFieldValueLong("statusByUserId"));

		if (modifiedByUserPortraitURLString != null) {
			searchResultSummaryDisplayContext.
				setModifiedByUserPortraitURLString(
					modifiedByUserPortraitURLString);
			searchResultSummaryDisplayContext.setModifiedByUserPortraitVisible(
				true);
		}
	}

	private void _buildModifiedDateString(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		String dateString = StringUtil.trim(
			_getFieldValueString(Field.MODIFIED_DATE));

		if (Validator.isBlank(dateString)) {
			return;
		}

		searchResultSummaryDisplayContext.setModifiedDateString(
			_formatDate(_parseDateStringFieldValue(dateString)));
		searchResultSummaryDisplayContext.setModifiedDateVisible(true);
	}

	private void _buildPublishedDateString(
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		String dateString = StringUtil.trim(
			_getFieldValueString(Field.PUBLISH_DATE));

		if (Validator.isBlank(dateString)) {
			return;
		}

		searchResultSummaryDisplayContext.setPublishedDateString(
			_formatDate(_parseDateStringFieldValue(dateString)));
		searchResultSummaryDisplayContext.setPublishedDateVisible(true);
	}

	private SearchResultSummaryDisplayContext _buildTemporarilyUnavailable() {
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext =
			new SearchResultSummaryDisplayContext();

		searchResultSummaryDisplayContext.setTemporarilyUnavailable(true);

		return searchResultSummaryDisplayContext;
	}

	private void _buildUserPortrait(
		AssetEntry assetEntry, String className,
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		AssetEntry childAssetEntry = _assetEntryLocalService.fetchEntry(
			className, _getEntryClassPK());

		if (childAssetEntry != null) {
			assetEntry = childAssetEntry;
		}

		if (assetEntry != null) {
			long assetEntryUserId = getAssetEntryUserId(assetEntry);

			searchResultSummaryDisplayContext.setAssetEntryUserId(
				assetEntryUserId);

			String portraitURLString = _getPortraitURLString(assetEntryUserId);

			if (portraitURLString != null) {
				searchResultSummaryDisplayContext.setUserPortraitURLString(
					portraitURLString);
				searchResultSummaryDisplayContext.setUserPortraitVisible(true);
			}
		}
	}

	private void _buildViewURL(
		String className, long classPK,
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext) {

		searchResultSummaryDisplayContext.setViewURL(
			getSearchResultViewURL(className, classPK));
	}

	private void _checkViewURL(
			long classNameId, long classPK, String entryClassName,
			SearchResultSummaryDisplayContext searchResultSummaryDisplayContext)
		throws Exception {

		if ((classNameId > 0) && (classPK > 0) &&
			_hasSearchResultContributor(entryClassName)) {

			ClassName className = _classNameLocalService.getClassName(
				classNameId);

			_buildViewURL(
				className.getClassName(), classPK,
				searchResultSummaryDisplayContext);
		}
	}

	private String _formatDate(Date date) {
		Format format = _fastDateFormatFactory.getDateTime(
			FastDateFormatConstants.MEDIUM, FastDateFormatConstants.SHORT,
			_locale, _themeDisplay.getTimeZone());

		return format.format(date);
	}

	private List<String> _getAllFieldNames() {
		if (_document != null) {
			Map<String, com.liferay.portal.search.document.Field> map =
				_document.getFields();

			return new ArrayList<>(map.keySet());
		}

		Map<String, Field> map = _legacyDocument.getFields();

		return new ArrayList<>(map.keySet());
	}

	private long _getEntryClassPK() {
		return _getFieldValueLong(Field.ENTRY_CLASS_PK);
	}

	private long _getFieldValueLong(String fieldName) {
		if (_document != null) {
			return GetterUtil.getLong(_document.getLong(fieldName));
		}

		return GetterUtil.getLong(_legacyDocument.get(fieldName));
	}

	private String _getFieldValueString(String fieldName) {
		if (_document != null) {
			return _document.getString(fieldName);
		}

		return _legacyDocument.get(fieldName);
	}

	private List<String> _getFieldValueStrings(String fieldName) {
		if (_document != null) {
			return _document.getStrings(fieldName);
		}

		Field field = _legacyDocument.getField(fieldName);

		return Arrays.asList(field.getValues());
	}

	private String _getHighlightedFieldName(String fieldName) {
		if (!_highlightEnabled) {
			return fieldName;
		}

		String snippetFieldName = StringBundler.concat(
			Field.SNIPPET, StringPool.UNDERLINE, fieldName);

		if (_isFieldPresent(snippetFieldName)) {
			return snippetFieldName;
		}

		return fieldName;
	}

	private String _getHighlightedFieldValue(String fieldName) {
		SummaryBuilder summaryBuilder = _summaryBuilderFactory.newInstance();

		summaryBuilder.setContent(_getHighlightedValuesToString(fieldName));
		summaryBuilder.setHighlight(_highlightEnabled);

		Summary summary = summaryBuilder.build();

		return summary.getContent();
	}

	private String _getHighlightedValuesToString(String fieldName) {
		return _getValuesToString(_getHighlightedFieldName(fieldName));
	}

	private String _getPortraitURLString(long userId) {
		User user = _userLocalService.fetchUser(userId);

		try {
			if ((user == null) || (user.getPortraitId() == 0)) {
				return null;
			}

			return user.getPortraitURL(_themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private String _getValuesToString(String fieldName) {
		List<String> values = _getFieldValueStrings(fieldName);

		if (values.isEmpty()) {
			return StringPool.BLANK;
		}

		if (values.size() == 1) {
			return values.get(0);
		}

		return String.valueOf(values);
	}

	private boolean _hasAssetCategoriesOrTags(AssetEntry assetEntry) {
		if (assetEntry == null) {
			return false;
		}

		if (ArrayUtil.isNotEmpty(assetEntry.getCategoryIds()) ||
			ArrayUtil.isNotEmpty(assetEntry.getTagNames())) {

			return true;
		}

		return false;
	}

	private boolean _hasAssetRendererURLDownload(
		AssetRenderer<?> assetRenderer) {

		if ((assetRenderer == null) ||
			Validator.isNull(assetRenderer.getURLDownload(_themeDisplay))) {

			return false;
		}

		return true;
	}

	private boolean _hasSearchResultContributor(String entryClassName) {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		try {
			Collection<ServiceReference<SearchResultContributor>>
				serviceReferences = bundleContext.getServiceReferences(
					SearchResultContributor.class, null);

			if (!serviceReferences.isEmpty()) {
				return true;
			}

			for (ServiceReference<SearchResultContributor> serviceReference :
					serviceReferences) {

				SearchResultContributor searchResultContributor =
					bundleContext.getService(serviceReference);

				if (StringUtil.equals(
						entryClassName,
						searchResultContributor.getEntryClassName())) {

					return true;
				}
			}
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			_log.error(invalidSyntaxException);
		}

		return false;
	}

	private boolean _isFieldPresent(String fieldName) {
		if (_document != null) {
			Map<String, com.liferay.portal.search.document.Field> map =
				_document.getFields();

			return map.containsKey(fieldName);
		}

		return _legacyDocument.hasField(fieldName);
	}

	private Date _parseDateStringFieldValue(String dateStringFieldValue) {
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

	private static final Log _log = LogFactoryUtil.getLog(
		SearchResultSummaryDisplayContextBuilder.class);

	private boolean _abridged;
	private AssetEntryLocalService _assetEntryLocalService;
	private AssetRendererFactoryLookup _assetRendererFactoryLookup;
	private ClassNameLocalService _classNameLocalService;
	private String _currentURL;
	private Document _document;
	private DocumentBuilderFactory _documentBuilderFactory;
	private FastDateFormatFactory _fastDateFormatFactory;
	private GroupLocalService _groupLocalService;
	private boolean _highlightEnabled;
	private HttpServletRequest _httpServletRequest;
	private boolean _imageRequested;
	private IndexerRegistry _indexerRegistry;
	private Language _language;
	private com.liferay.portal.kernel.search.Document _legacyDocument;
	private Locale _locale;
	private ObjectDefinitionLocalService _objectDefinitionLocalService;
	private PortletURLFactory _portletURLFactory;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;
	private ResourceActions _resourceActions;
	private SearchResultPreferences _searchResultPreferences;
	private SearchResultViewURLSupplier _searchResultViewURLSupplier;
	private SummaryBuilderFactory _summaryBuilderFactory;
	private ThemeDisplay _themeDisplay;
	private UserLocalService _userLocalService;

}