/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.omni.search;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.product.navigation.omni.search.OmniSearchResult;
import com.liferay.product.navigation.omni.search.OmniSearchResultProvider;
import com.liferay.product.navigation.omni.search.constants.OmniSearchConstants;

import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Castro
 * @author Thiago Buarque
 */
@Component(
	property = "omni.search.result.provider.order:Integer=300",
	service = OmniSearchResultProvider.class
)
public class SearchOmniSearchResultProvider
	implements OmniSearchResultProvider {

	@Override
	public List<OmniSearchResult> getOmniSearchResults(
			String keywords, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			ThemeDisplay themeDisplay)
		throws PortalException {

		List<OmniSearchResult> omniSearchResults = new ArrayList<>();

		SearchResponse searchResponse = _search(keywords, themeDisplay);

		String redirect = _getRedirect(liferayPortletRequest, themeDisplay);

		SearchHits searchHits = searchResponse.getSearchHits();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			try {
				OmniSearchResult omniSearchResult = _toOmniSearchResult(
					document, liferayPortletRequest, liferayPortletResponse,
					redirect, themeDisplay);

				if (omniSearchResult != null) {
					omniSearchResults.add(omniSearchResult);
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to process search hit " +
							document.getString(Field.ENTRY_CLASS_NAME),
						exception);
				}
			}
		}

		if (omniSearchResults.isEmpty()) {
			return Collections.emptyList();
		}

		return Collections.singletonList(
			new OmniSearchResult(
				"search", omniSearchResults,
				StringBundler.concat(
					LanguageUtil.get(themeDisplay.getLocale(), "results"), " (",
					searchResponse.getTotalHits(), ")")));
	}

	private String _getAssetURL(
			AssetRenderer<?> assetRenderer,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PermissionChecker permissionChecker, String redirect)
		throws Exception {

		if (assetRenderer.hasEditPermission(permissionChecker)) {
			PortletURL editPortletURL = assetRenderer.getURLEdit(
				liferayPortletRequest, liferayPortletResponse,
				LiferayWindowState.NORMAL, redirect);

			if (editPortletURL != null) {
				return editPortletURL.toString();
			}
		}

		return assetRenderer.getURLViewInContext(
			liferayPortletRequest, liferayPortletResponse, redirect);
	}

	private String _getRedirect(
			LiferayPortletRequest liferayPortletRequest,
			ThemeDisplay themeDisplay)
		throws PortalException {

		String redirect = _portal.escapeRedirect(
			ParamUtil.getString(
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(liferayPortletRequest)),
				"redirect"));

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		Layout layout = themeDisplay.getLayout();

		if (layout == null) {
			return themeDisplay.getURLHome();
		}

		return _portal.getLayoutFriendlyURL(layout, themeDisplay);
	}

	private SearchResponse _search(String keywords, ThemeDisplay themeDisplay) {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				themeDisplay.getCompanyId()
			).from(
				0
			).queryString(
				keywords
			).size(
				OmniSearchConstants.MAX_ENTRIES_PER_SECTION
			).withSearchContext(
				searchContext -> {
					searchContext.setGroupIds(new long[0]);
					searchContext.setLocale(themeDisplay.getLocale());
					searchContext.setUserId(themeDisplay.getUserId());
				}
			).build());
	}

	private OmniSearchResult _toLayoutOmniSearchResult(
			long classPK, Locale locale, ThemeDisplay themeDisplay)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(classPK);

		if (layout == null) {
			return null;
		}

		String description = ResourceActionsUtil.getModelResource(
			locale, Layout.class.getName());

		Group group = layout.getGroup();

		if (group != null) {
			description =
				description + " - " + group.getDescriptiveName(locale);
		}

		return new OmniSearchResult(
			description, "page", layout.getName(locale),
			_portal.getLayoutFriendlyURL(layout, themeDisplay));
	}

	private OmniSearchResult _toOmniSearchResult(
			Document document, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, String redirect,
			ThemeDisplay themeDisplay)
		throws Exception {

		String className = document.getString(Field.ENTRY_CLASS_NAME);

		if (Validator.isBlank(className)) {
			return null;
		}

		long classPK = GetterUtil.getLong(
			document.getString(Field.ROOT_ENTRY_CLASS_PK));

		if (classPK <= 0) {
			classPK = GetterUtil.getLong(
				document.getString(Field.ENTRY_CLASS_PK));
		}

		Locale locale = themeDisplay.getLocale();

		if (className.equals(Layout.class.getName())) {
			return _toLayoutOmniSearchResult(classPK, locale, themeDisplay);
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory == null) {
			return null;
		}

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			classPK);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if ((assetRenderer == null) ||
			!assetRenderer.hasViewPermission(permissionChecker)) {

			return null;
		}

		String url = _getAssetURL(
			assetRenderer, liferayPortletRequest, liferayPortletResponse,
			permissionChecker, redirect);

		if (Validator.isBlank(url)) {
			return null;
		}

		return new OmniSearchResult(
			assetRendererFactory.getTypeName(locale),
			assetRendererFactory.getIconCssClass(),
			assetRenderer.getTitle(locale), url);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchOmniSearchResultProvider.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}