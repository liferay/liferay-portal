/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatConstants;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;
import com.liferay.portal.search.similar.results.web.internal.contributor.SimilarResultsContributor;
import com.liferay.portal.search.similar.results.web.internal.display.context.SimilarResultsDocumentDisplayContext;
import com.liferay.portal.search.similar.results.web.internal.portlet.SimilarResultsPortletPreferences;
import com.liferay.portal.search.similar.results.web.internal.portlet.SimilarResultsPortletPreferencesImpl;
import com.liferay.portal.search.similar.results.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationHelper;
import com.liferay.portal.search.summary.Summary;
import com.liferay.portal.search.summary.SummaryBuilder;
import com.liferay.portal.search.summary.SummaryBuilderFactory;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Wade Cao
 */
public class SimilarResultsDocumentDisplayContextBuilder {

	public SimilarResultsDocumentDisplayContextBuilder(
		SimilarResultsRoute similarResultsRoute) {

		_similarResultsRoute = similarResultsRoute;
	}

	public SimilarResultsDocumentDisplayContext build() {
		try {
			if (_documentBuilderFactory != null) {
				_document = _documentBuilderFactory.builder(
					_legacyDocument
				).build();
			}

			String className = _getFieldValueString(Field.ENTRY_CLASS_NAME);

			long classPK = _getEntryClassPK();

			return build(className, classPK);
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

	public SimilarResultsDocumentDisplayContextBuilder
		setAssetEntryLocalService(
			AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setDocument(
		com.liferay.portal.kernel.search.Document legacyDocument) {

		_legacyDocument = legacyDocument;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setDocument(
		Document document) {

		_document = document;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder
		setDocumentBuilderFactory(
			DocumentBuilderFactory documentBuilderFactory) {

		_documentBuilderFactory = documentBuilderFactory;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setFastDateFormatFactory(
		FastDateFormatFactory fastDateFormatFactory) {

		_fastDateFormatFactory = fastDateFormatFactory;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setHighlightEnabled(
		boolean highlightEnabled) {

		_highlightEnabled = highlightEnabled;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setIndexerRegistry(
		IndexerRegistry indexerRegistry) {

		_indexerRegistry = indexerRegistry;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setLocale(
		Locale locale) {

		_locale = locale;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setPortal(
		Portal portal) {

		_portal = portal;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setRenderRequest(
		RenderRequest renderRequest) {

		_renderRequest = renderRequest;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setRenderResponse(
		RenderResponse renderResponse) {

		_renderResponse = renderResponse;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setResourceActions(
		ResourceActions resourceActions) {

		_resourceActions = resourceActions;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setSummaryBuilderFactory(
		SummaryBuilderFactory summaryBuilderFactory) {

		_summaryBuilderFactory = summaryBuilderFactory;

		return this;
	}

	public SimilarResultsDocumentDisplayContextBuilder setThemeDisplay(
		ThemeDisplay themeDisplay) {

		_themeDisplay = themeDisplay;

		return this;
	}

	protected SimilarResultsDocumentDisplayContext build(
			String className, long classPK)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

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

		Summary summary = _getSummary(className, assetRenderer);

		if (summary == null) {
			SummaryBuilder summaryBuilder =
				_summaryBuilderFactory.newInstance();

			summary = summaryBuilder.build();
		}

		return build(summary, className, classPK, assetRenderer);
	}

	protected SimilarResultsDocumentDisplayContext build(
			Summary summary, String className, long classPK,
			AssetRenderer<?> assetRenderer)
		throws Exception {

		SimilarResultsDocumentDisplayContext
			similarResultsDocumentDisplayContext =
				new SimilarResultsDocumentDisplayContext();

		if (Validator.isNotNull(summary.getContent())) {
			similarResultsDocumentDisplayContext.setContent(
				summary.getContent());
		}
		else {
			similarResultsDocumentDisplayContext.setContent(StringPool.BLANK);
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		_buildCreationDateString(similarResultsDocumentDisplayContext);
		_buildCreatorUserName(similarResultsDocumentDisplayContext);

		_buildImage(
			similarResultsDocumentDisplayContext, className, classPK,
			assetRenderer);

		_buildModelResource(similarResultsDocumentDisplayContext, className);

		_buildCategoriesString(
			similarResultsDocumentDisplayContext, assetEntry);

		similarResultsDocumentDisplayContext.setTitle(
			getTitle(assetEntry, summary));
		similarResultsDocumentDisplayContext.setViewURL(
			_getViewURL(assetEntry, assetRenderer, className, classPK));

		return similarResultsDocumentDisplayContext;
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

	protected String getTitle(AssetEntry assetEntry, Summary summary) {
		String title = summary.getTitle();

		if (Validator.isBlank(title)) {
			title = assetEntry.getTitle(_locale);
		}

		return title;
	}

	private void _buildCategoriesString(
		SimilarResultsDocumentDisplayContext
			similarResultsDocumentDisplayContext,
		AssetEntry assetEntry) {

		similarResultsDocumentDisplayContext.setCategoriesString(
			StringPool.BLANK);

		if (assetEntry == null) {
			return;
		}

		List<AssetCategory> assetCategories = assetEntry.getCategories();

		assetCategories.forEach(
			assetCategory -> {
				if (Validator.isBlank(
						similarResultsDocumentDisplayContext.
							getCategoriesString())) {

					similarResultsDocumentDisplayContext.setCategoriesString(
						assetCategory.getName());
				}
				else {
					similarResultsDocumentDisplayContext.setCategoriesString(
						"," + assetCategory.getName());
				}
			});
	}

	private void _buildCreationDateString(
		SimilarResultsDocumentDisplayContext
			similarResultsDocumentDisplayContext) {

		String dateString = SearchStringUtil.maybe(
			_getFieldValueString(Field.CREATE_DATE));

		if (dateString == null) {
			return;
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			Date date = dateFormat.parse(dateString);

			if (date != null) {
				similarResultsDocumentDisplayContext.setCreationDateString(
					_formatCreationDate(date));
			}
		}
		catch (Exception exception) {
			throw new IllegalArgumentException(
				"Unable to parse date string: " + dateString, exception);
		}
	}

	private void _buildCreatorUserName(
		SimilarResultsDocumentDisplayContext
			similarResultsDocumentDisplayContext) {

		String creatorUserName = _getFieldValueString(Field.USER_NAME);

		if (!Validator.isBlank(creatorUserName)) {
			similarResultsDocumentDisplayContext.setCreatorUserName(
				creatorUserName);
		}
		else {
			similarResultsDocumentDisplayContext.setCreatorUserName(
				StringPool.BLANK);
		}
	}

	private void _buildImage(
		SimilarResultsDocumentDisplayContext
			similarResultsDocumentDisplayContext,
		String className, long classPK, AssetRenderer<?> assetRenderer) {

		String thumbnailURLString = null;
		String assetClassName = BlogsEntry.class.getName();

		if (assetClassName.equals(className)) {
			BlogsEntry blogsEntry = (BlogsEntry)assetRenderer.getAssetObject();

			try {
				thumbnailURLString = blogsEntry.getCoverImageURL(_themeDisplay);

				if (Validator.isNull(thumbnailURLString)) {
					thumbnailURLString = blogsEntry.getSmallImageURL(
						_themeDisplay);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Blogs entry thumbnail URL exception and contains " +
							"blogs entry ID " + blogsEntry.getEntryId(),
						exception);
				}
			}

			similarResultsDocumentDisplayContext.setThumbnailURLString(
				thumbnailURLString);
			similarResultsDocumentDisplayContext.setIconId("blogs");

			return;
		}

		assetClassName = JournalArticle.class.getName();

		if (assetClassName.equals(className)) {
			try {
				thumbnailURLString = assetRenderer.getThumbnailPath(
					_renderRequest);
				similarResultsDocumentDisplayContext.setIconId("web-content");
				similarResultsDocumentDisplayContext.setThumbnailURLString(
					thumbnailURLString);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Journal article thumbnail URL exception and " +
							"contains journal article class PK " + classPK,
						exception);
				}
			}

			return;
		}

		assetClassName = DLFolder.class.getName();

		if (assetClassName.equals(className)) {
			similarResultsDocumentDisplayContext.setIconId("folder");

			return;
		}

		assetClassName = DLFileEntry.class.getName();

		if (!assetClassName.equals(className)) {
			return;
		}

		Object assetObject = assetRenderer.getAssetObject();

		if (assetObject instanceof FileEntry) {
			FileEntry fileEntry = (FileEntry)assetObject;

			similarResultsDocumentDisplayContext.setIconId(
				fileEntry.getIconCssClass());

			try {
				thumbnailURLString = DLURLHelperUtil.getThumbnailSrc(
					fileEntry, _themeDisplay);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"File entry thumbnail url exception and contains " +
							"file classPK " + classPK,
						exception);
				}
			}

			similarResultsDocumentDisplayContext.setThumbnailURLString(
				thumbnailURLString);
		}
		else {
			DLFileEntry dlFileEntry =
				(DLFileEntry)assetRenderer.getAssetObject();

			similarResultsDocumentDisplayContext.setIconId(
				dlFileEntry.getIconCssClass());
		}
	}

	private void _buildModelResource(
		SimilarResultsDocumentDisplayContext
			similarResultsDocumentDisplayContext,
		String className) {

		String modelResource = _resourceActions.getModelResource(
			_themeDisplay.getLocale(), className);

		if (!Validator.isBlank(modelResource)) {
			similarResultsDocumentDisplayContext.setModelResource(
				modelResource);
		}
	}

	private SimilarResultsDocumentDisplayContext
		_buildTemporarilyUnavailable() {

		SimilarResultsDocumentDisplayContext
			similarResultsDocumentDisplayContext =
				new SimilarResultsDocumentDisplayContext();

		similarResultsDocumentDisplayContext.setTemporarilyUnavailable(true);

		return similarResultsDocumentDisplayContext;
	}

	private String _formatCreationDate(Date date) {
		Format format = _fastDateFormatFactory.getDateTime(
			FastDateFormatConstants.MEDIUM, FastDateFormatConstants.SHORT,
			_locale, _themeDisplay.getTimeZone());

		return format.format(date);
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

	private Indexer<Object> _getIndexer(String className) {
		if (_indexerRegistry != null) {
			return _indexerRegistry.getIndexer(className);
		}

		return IndexerRegistryUtil.getIndexer(className);
	}

	private Summary _getSummary(
			String className, AssetRenderer<?> assetRenderer)
		throws Exception {

		SummaryBuilder summaryBuilder = _summaryBuilderFactory.newInstance();

		summaryBuilder.setHighlight(_highlightEnabled);

		Indexer<?> indexer = _getIndexer(className);

		if (indexer != null) {
			String snippet = _document.getString(Field.SNIPPET);

			com.liferay.portal.kernel.search.Summary summary =
				indexer.getSummary(
					_legacyDocument, snippet, _renderRequest, _renderResponse);

			if (summary != null) {
				summaryBuilder.setContent(summary.getContent());
				summaryBuilder.setLocale(summary.getLocale());
				summaryBuilder.setMaxContentLength(
					summary.getMaxContentLength());
				summaryBuilder.setTitle(summary.getTitle());

				return summaryBuilder.build();
			}
			else if (assetRenderer != null) {
				summaryBuilder.setContent(
					assetRenderer.getSearchSummary(_locale));
				summaryBuilder.setLocale(_locale);
				summaryBuilder.setTitle(assetRenderer.getTitle(_locale));

				return summaryBuilder.build();
			}
		}
		else if (assetRenderer != null) {
			summaryBuilder.setContent(assetRenderer.getSearchSummary(_locale));
			summaryBuilder.setLocale(_locale);
			summaryBuilder.setTitle(assetRenderer.getTitle(_locale));

			return summaryBuilder.build();
		}

		return null;
	}

	private String _getViewURL(
		AssetEntry assetEntry, AssetRenderer<?> assetRenderer, String className,
		long classPK) {

		SimilarResultsPortletPreferences similarResultsPortletPreferences =
			new SimilarResultsPortletPreferencesImpl(
				_renderRequest.getPreferences());

		if (Objects.equals(
				similarResultsPortletPreferences.getLinkBehavior(),
				"view-in-context")) {

			try {
				String url = assetRenderer.getURLViewInContext(
					_portal.getLiferayPortletRequest(_renderRequest),
					_portal.getLiferayPortletResponse(_renderResponse), null);

				if (!Validator.isBlank(url)) {
					return url;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		String currentURL = _portal.getCurrentURL(_renderRequest);

		if (_similarResultsRoute == null) {
			return currentURL;
		}

		SimilarResultsContributor similarResultsContributor =
			_similarResultsRoute.getContributor();

		DestinationBuilderImpl destinationBuilderImpl =
			new DestinationBuilderImpl(currentURL);

		DestinationHelper destinationHelper = new DestinationHelper() {

			@Override
			public AssetEntry getAssetEntry() {
				return assetEntry;
			}

			@Override
			public AssetRenderer<?> getAssetRenderer() {
				return assetRenderer;
			}

			@Override
			public String getAssetViewURL() {
				try {
					return assetRenderer.getURLView(
						_portal.getLiferayPortletResponse(_renderResponse),
						_renderRequest.getWindowState());
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				return null;
			}

			@Override
			public String getClassName() {
				return className;
			}

			@Override
			public long getClassPK() {
				return classPK;
			}

			@Override
			public Object getRouteParameter(String name) {
				return _similarResultsRoute.getRouteParameter(name);
			}

			@Override
			public long getScopeGroupId() {
				return _themeDisplay.getScopeGroupId();
			}

			@Override
			public String getUID() {
				return _getFieldValueString(Field.UID);
			}

		};

		similarResultsContributor.writeDestination(
			destinationBuilderImpl, destinationHelper);

		return destinationBuilderImpl.build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SimilarResultsDocumentDisplayContextBuilder.class);

	private AssetEntryLocalService _assetEntryLocalService;
	private Document _document;
	private DocumentBuilderFactory _documentBuilderFactory;
	private FastDateFormatFactory _fastDateFormatFactory;
	private boolean _highlightEnabled;
	private IndexerRegistry _indexerRegistry;
	private com.liferay.portal.kernel.search.Document _legacyDocument;
	private Locale _locale;
	private Portal _portal;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;
	private ResourceActions _resourceActions;
	private final SimilarResultsRoute _similarResultsRoute;
	private SummaryBuilderFactory _summaryBuilderFactory;
	private ThemeDisplay _themeDisplay;

}