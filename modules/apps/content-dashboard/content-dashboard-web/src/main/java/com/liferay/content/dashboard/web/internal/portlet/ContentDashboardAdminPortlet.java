/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtypeFactoryRegistry;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.content.dashboard.web.internal.dao.search.ContentDashboardItemSearchContainerFactory;
import com.liferay.content.dashboard.web.internal.data.provider.ContentDashboardDataProvider;
import com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminDisplayContext;
import com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminManagementToolbarDisplayContext;
import com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminSharingDisplayContext;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryRegistry;
import com.liferay.content.dashboard.web.internal.item.filter.ContentDashboardItemFilterProviderRegistry;
import com.liferay.content.dashboard.web.internal.search.request.ContentDashboardSearchContextBuilder;
import com.liferay.content.dashboard.web.internal.searcher.ContentDashboardSearchRequestBuilderFactory;
import com.liferay.content.dashboard.web.internal.servlet.taglib.util.ContentDashboardDropdownItemsProvider;
import com.liferay.content.dashboard.web.internal.util.ContentDashboardUtil;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.constants.LanguageConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.sharing.display.context.util.SharingJavaScriptFactory;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.List;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-content-dashboard-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-company-wide=false",
		"com.liferay.portlet.preferences-owned-by-group=false",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Content Dashboard",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"jakarta.portlet.portlet-mode=text/html",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ContentDashboardAdminPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			_portal.getLocale(renderRequest), getClass());

		ContentDashboardDataProvider contentDashboardDataProvider =
			new ContentDashboardDataProvider(
				_aggregations,
				new ContentDashboardSearchContextBuilder(
					_portal.getHttpServletRequest(renderRequest),
					_assetCategoryLocalService, _assetVocabularyLocalService,
					_contentDashboardItemFilterProviderRegistry),
				_contentDashboardSearchRequestBuilderFactory,
				_portal.getLocale(renderRequest), _queries, resourceBundle,
				_searcher);

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(renderResponse);

		ContentDashboardItemSearchContainerFactory
			contentDashboardItemSearchContainerFactory =
				ContentDashboardItemSearchContainerFactory.getInstance(
					_assetCategoryLocalService, _assetVocabularyLocalService,
					_contentDashboardItemFactoryRegistry,
					_contentDashboardItemFilterProviderRegistry,
					_contentDashboardSearchRequestBuilderFactory,
					_infoSearchClassMapperRegistry, _portal, renderRequest,
					renderResponse, _searcher);

		SearchContainer<ContentDashboardItem<?>> searchContainer =
			contentDashboardItemSearchContainerFactory.create();

		List<AssetVocabulary> assetVocabularies = TransformUtil.transformToList(
			ContentDashboardUtil.getAssetVocabularyIds(renderRequest),
			assetVocabularyId -> {
				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.fetchAssetVocabulary(
						assetVocabularyId);

				if ((assetVocabulary == null) ||
					(assetVocabulary.getCategoriesCount() <= 0)) {

					return null;
				}

				return assetVocabulary;
			});

		ContentDashboardAdminDisplayContext
			contentDashboardAdminDisplayContext =
				new ContentDashboardAdminDisplayContext(
					assetVocabularies,
					contentDashboardDataProvider.getAssetVocabularyMetric(
						assetVocabularies),
					new ContentDashboardDropdownItemsProvider(
						_language, liferayPortletRequest,
						liferayPortletResponse, _portal),
					_contentDashboardItemSubtypeFactoryRegistry, _itemSelector,
					_language.get(
						_portal.getLocale(liferayPortletRequest),
						LanguageConstants.KEY_DIR),
					liferayPortletRequest, liferayPortletResponse, _portal,
					resourceBundle, searchContainer);

		renderRequest.setAttribute(
			ContentDashboardAdminDisplayContext.class.getName(),
			contentDashboardAdminDisplayContext);

		ContentDashboardAdminManagementToolbarDisplayContext
			contentDashboardAdminManagementToolbarDisplayContext =
				new ContentDashboardAdminManagementToolbarDisplayContext(
					_assetCategoryLocalService, _assetVocabularyLocalService,
					contentDashboardAdminDisplayContext,
					_contentDashboardItemFilterProviderRegistry,
					_groupLocalService,
					_portal.getHttpServletRequest(renderRequest), _itemSelector,
					_language, liferayPortletRequest, liferayPortletResponse,
					_portal.getLocale(renderRequest), _userLocalService);

		renderRequest.setAttribute(
			ContentDashboardAdminManagementToolbarDisplayContext.class.
				getName(),
			contentDashboardAdminManagementToolbarDisplayContext);

		renderRequest.setAttribute(
			ContentDashboardAdminSharingDisplayContext.class.getName(),
			new ContentDashboardAdminSharingDisplayContext(
				_contentDashboardItemFactoryRegistry,
				_portal.getHttpServletRequest(liferayPortletRequest),
				_infoSearchClassMapperRegistry));

		_sharingJavaScriptFactory.requestSharingJavaScript();

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ContentDashboardItemFactoryRegistry
		_contentDashboardItemFactoryRegistry;

	@Reference
	private ContentDashboardItemFilterProviderRegistry
		_contentDashboardItemFilterProviderRegistry;

	@Reference
	private ContentDashboardItemSubtypeFactoryRegistry
		_contentDashboardItemSubtypeFactoryRegistry;

	@Reference
	private ContentDashboardSearchRequestBuilderFactory
		_contentDashboardSearchRequestBuilderFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private Searcher _searcher;

	@Reference
	private SharingJavaScriptFactory _sharingJavaScriptFactory;

	@Reference
	private UserLocalService _userLocalService;

}