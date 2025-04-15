/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreter;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreterProvider;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.WindowState;

/**
 * @author Wade Cao
 */
public class RankingResultUtil {

	public static AssetRenderer<?> getAssetRenderer(
		String entryClassName, long entryClassPK) {

		DocumentBuilderFactory documentBuilderFactory =
			_documentBuilderFactorySnapshot.get();

		Document document = documentBuilderFactory.builder(
		).setString(
			Field.ENTRY_CLASS_NAME, entryClassName
		).setLong(
			Field.ENTRY_CLASS_PK, Long.valueOf(entryClassPK)
		).build();

		SearchResultInterpreter searchResultInterpreter =
			_getSearchResultInterpreter();

		try {
			return searchResultInterpreter.getAssetRenderer(document);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get asset renderer for class ",
						entryClassName, " with primary key ", entryClassPK),
					exception);
			}

			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static String getRankingResultViewURL(
		Document document, ResourceRequest resourceRequest,
		ResourceResponse resourceResponse, boolean viewInContext) {

		SearchResultInterpreter searchResultInterpreter =
			_getSearchResultInterpreter();

		PortletURL viewContentURL = resourceResponse.createRenderURL();
		String currentURL = PortalUtil.getCurrentURL(resourceRequest);

		try {
			viewContentURL.setParameter("mvcPath", "/view_content.jsp");
			viewContentURL.setParameter("redirect", currentURL);
			viewContentURL.setPortletMode(PortletMode.VIEW);
			viewContentURL.setWindowState(WindowState.MAXIMIZED);

			AssetEntry assetEntry = searchResultInterpreter.getAssetEntry(
				document);

			if (assetEntry == null) {
				return viewContentURL.toString();
			}

			viewContentURL.setParameter(
				"assetEntryId", String.valueOf(assetEntry.getEntryId()));
			viewContentURL.setParameter(
				"entryClassName", document.getString(Field.ENTRY_CLASS_NAME));
			viewContentURL.setParameter(
				"entryClassPK", document.getString(Field.ENTRY_CLASS_PK));

			if (!viewInContext) {
				return viewContentURL.toString();
			}

			String viewURL = searchResultInterpreter.getAssetURLViewInContext(
				document, PortalUtil.getLiferayPortletRequest(resourceRequest),
				PortalUtil.getLiferayPortletResponse(resourceResponse),
				viewContentURL.toString());

			if (Validator.isNull(viewURL)) {
				return viewContentURL.toString();
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			viewURL = HttpComponentsUtil.setParameter(
				viewURL, "inheritRedirect", viewInContext);

			Layout layout = themeDisplay.getLayout();

			String assetEntryLayoutUuid = assetEntry.getLayoutUuid();

			if (Validator.isNotNull(assetEntryLayoutUuid) &&
				!assetEntryLayoutUuid.equals(layout.getUuid())) {

				viewURL = HttpComponentsUtil.setParameter(
					viewURL, "redirect", currentURL);
			}

			return viewURL;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get ranking result view URL for class ",
						document.getString(Field.ENTRY_CLASS_NAME),
						" with primary key ",
						document.getString(Field.ENTRY_CLASS_PK)),
					exception);
			}

			return StringPool.BLANK;
		}
	}

	public static boolean isAssetDeleted(Document document) {
		SearchResultInterpreter searchResultInterpreter =
			_getSearchResultInterpreter();

		try {
			return searchResultInterpreter.isAssetDeleted(document);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get asset deletion status for class ",
						document.getString(Field.ENTRY_CLASS_NAME),
						" with primary key ",
						document.getString(Field.ENTRY_CLASS_PK)),
					exception);
			}

			return false;
		}
	}

	private static SearchResultInterpreter _getSearchResultInterpreter() {
		SearchResultInterpreterProvider searchResultInterpreterProvider =
			_searchResultInterpreterProviderSnapshot.get();

		return searchResultInterpreterProvider.getSearchResultInterpreter(
			ResultRankingsPortletKeys.RESULT_RANKINGS);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingResultUtil.class);

	private static final Snapshot<DocumentBuilderFactory>
		_documentBuilderFactorySnapshot = new Snapshot<>(
			RankingResultUtil.class, DocumentBuilderFactory.class);
	private static final Snapshot<SearchResultInterpreterProvider>
		_searchResultInterpreterProviderSnapshot = new Snapshot<>(
			RankingResultUtil.class, SearchResultInterpreterProvider.class);

}