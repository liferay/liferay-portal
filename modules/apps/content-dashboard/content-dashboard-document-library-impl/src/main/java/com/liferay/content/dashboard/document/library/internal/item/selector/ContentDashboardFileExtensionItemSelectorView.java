/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.selector;

import com.liferay.content.dashboard.document.library.internal.item.display.context.ContentDashboardFileExtensionItemSelectorViewDisplayContext;
import com.liferay.content.dashboard.document.library.internal.item.provider.FileExtensionGroupsProvider;
import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileExtensionItemSelectorCriterion;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = ItemSelectorView.class
)
public class ContentDashboardFileExtensionItemSelectorView
	implements ItemSelectorView<FileExtensionItemSelectorCriterion> {

	@Override
	public Class<FileExtensionItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return FileExtensionItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		return ResourceBundleUtil.getString(resourceBundle, "extension");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FileExtensionItemSelectorCriterion
				fileExtensionItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/view_content_dashboard_file_extensions.jsp");

		servletRequest.setAttribute(
			ContentDashboardFileExtensionItemSelectorViewDisplayContext.class.
				getName(),
			new ContentDashboardFileExtensionItemSelectorViewDisplayContext(
				_getContentDashboardFileExtensionGroupsJSONArray(
					fileExtensionItemSelectorCriterion, servletRequest),
				itemSelectedEventName));

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private JSONArray _getContentDashboardFileExtensionGroupsJSONArray(
		FileExtensionItemSelectorCriterion fileExtensionItemSelectorCriterion,
		ServletRequest servletRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)servletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<FileExtensionGroupsProvider.FileExtensionGroup>
			fileExtensionGroups =
				_fileExtensionGroupsProvider.getFileExtensionGroups();

		List<String> existingFileExtensions = _getExistingFileExtensions(
			fileExtensionItemSelectorCriterion, themeDisplay.getRequest());

		Set<String> otherFileExtensions = SetUtil.fromList(
			ListUtil.filter(
				existingFileExtensions, _fileExtensionGroupsProvider::isOther));

		return JSONUtil.toJSONArray(
			fileExtensionGroups,
			fileExtensionGroup -> fileExtensionGroup.toJSONObject(
				SetUtil.fromArray(
					servletRequest.getParameterValues("checkedFileExtensions")),
				existingFileExtensions, _dlMimeTypeDisplayContextSnapshot.get(),
				_language, themeDisplay.getLocale(), otherFileExtensions),
			_log);
	}

	private List<String> _getExistingFileExtensions(
		FileExtensionItemSelectorCriterion fileExtensionItemSelectorCriterion,
		HttpServletRequest httpServletRequest) {

		SearchContext searchContext = SearchContextFactory.getInstance(
			httpServletRequest);

		long[] selectedGroupIds =
			fileExtensionItemSelectorCriterion.getSelectedGroupIds();

		if (selectedGroupIds != null) {
			searchContext.setGroupIds(selectedGroupIds);
		}
		else {
			searchContext.setGroupIds(new long[0]);
		}

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
				searchContext
			).emptySearchEnabled(
				true
			).entryClassNames(
				DLFileEntry.class.getName()
			).fields(
				Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK, Field.UID
			).highlightEnabled(
				false
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.addAggregation(
				_aggregations.terms("extensions", "extension")
			).size(
				0
			).build());

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)searchResponse.getAggregationResult(
				"extensions");

		return TransformUtil.transform(
			termsAggregationResult.getBuckets(), Bucket::getKey);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardFileExtensionItemSelectorView.class);

	private static final Snapshot<DLMimeTypeDisplayContext>
		_dlMimeTypeDisplayContextSnapshot = new Snapshot<>(
			ContentDashboardFileExtensionItemSelectorView.class,
			DLMimeTypeDisplayContext.class, null, true);
	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private Aggregations _aggregations;

	@Reference
	private FileExtensionGroupsProvider _fileExtensionGroupsProvider;

	@Reference
	private Language _language;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.content.dashboard.document.library.impl)"
	)
	private ServletContext _servletContext;

}