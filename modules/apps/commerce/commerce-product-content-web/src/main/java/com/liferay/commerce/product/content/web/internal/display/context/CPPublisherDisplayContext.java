/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.media.CommerceCatalogDefaultImage;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.util.CPMedia;
import com.liferay.commerce.product.content.web.internal.helper.CPPublisherWebHelper;
import com.liferay.commerce.product.content.web.internal.util.CPMediaUtil;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceRegistry;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.filter.PortletURLWrapper;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPPublisherDisplayContext extends BaseCPPublisherDisplayContext {

	public CPPublisherDisplayContext(
			AMImageHTMLTagFactory amImageHTMLTagFactory,
			ConfigurationProvider configurationProvider,
			CommerceCatalogDefaultImage commerceCatalogDefaultImage,
			CommerceMediaResolver commerceMediaResolver,
			CPAttachmentFileEntryLocalService cpAttachmentFileEntryLocalService,
			CPContentListEntryRendererRegistry contentListEntryRendererRegistry,
			CPContentListRendererRegistry cpContentListRendererRegistry,
			CPDataSourceRegistry cpDataSourceRegistry,
			CPDefinitionHelper cpDefinitionHelper,
			CPDefinitionLocalService cpDefinitionLocalService,
			CPFriendlyURL cpFriendlyURL,
			CPPublisherWebHelper cpPublisherWebHelper,
			CPTypeRegistry cpTypeRegistry,
			DLFileEntryLocalService dlFileEntryLocalService,
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission,
			FriendlyURLEntryLocalService friendlyURLEntryLocalService,
			GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest, Portal portal)
		throws PortalException {

		super(
			configurationProvider, contentListEntryRendererRegistry,
			cpContentListRendererRegistry, cpPublisherWebHelper, cpTypeRegistry,
			groupLocalService, httpServletRequest);

		_amImageHTMLTagFactory = amImageHTMLTagFactory;
		_configurationProvider = configurationProvider;
		_commerceCatalogDefaultImage = commerceCatalogDefaultImage;
		_commerceMediaResolver = commerceMediaResolver;
		_cpAttachmentFileEntryLocalService = cpAttachmentFileEntryLocalService;
		_cpDataSourceRegistry = cpDataSourceRegistry;
		_cpDefinitionHelper = cpDefinitionHelper;
		_cpDefinitionLocalService = cpDefinitionLocalService;
		_cpFriendlyURL = cpFriendlyURL;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_dlFileEntryModelResourcePermission =
			dlFileEntryModelResourcePermission;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
		_portal = portal;

		_cProductId = _getCProductId();
		_delta = ParamUtil.getInteger(
			httpServletRequest, "delta", getPaginationDelta());
	}

	public Map<String, String> getCPContentListEntryRendererKeys() {
		Map<String, String> cpContentListEntryRendererKeys = new HashMap<>();

		for (CPType cpType : getCPTypes()) {
			String cpTypeName = cpType.getName();

			cpContentListEntryRendererKeys.put(
				cpTypeName, getCPTypeListEntryRendererKey(cpTypeName));
		}

		return cpContentListEntryRendererKeys;
	}

	public CPDataSourceResult getCPDataSourceResult() throws Exception {
		CPDataSourceResult cpDataSourceResult = null;

		if (isSelectionStyleDynamic()) {
			cpDataSourceResult = _getDynamicCPDataSourceResult(
				cpContentRequestHelper.getRequest(),
				_searchContainer.getStart(), _searchContainer.getEnd());
		}
		else if (isSelectionStyleDataSource()) {
			String dataSourceName = getDataSource();

			CPDataSource cpDataSource = _cpDataSourceRegistry.getCPDataSource(
				dataSourceName);

			if (cpDataSource == null) {
				cpDataSourceResult = new CPDataSourceResult(
					new ArrayList<>(), 0);
			}
			else {
				cpDataSourceResult = cpDataSource.getResult(
					cpContentRequestHelper.getRequest(),
					_searchContainer.getStart(), _searchContainer.getEnd());
			}
		}
		else if (isSelectionStyleManual()) {
			List<CPCatalogEntry> catalogEntries = getCPCatalogEntries();

			if (catalogEntries == null) {
				return null;
			}

			int end = _searchContainer.getEnd();

			if (end > catalogEntries.size()) {
				end = catalogEntries.size();
			}

			cpDataSourceResult = new CPDataSourceResult(
				catalogEntries.subList(_searchContainer.getStart(), end),
				catalogEntries.size());
		}

		return cpDataSourceResult;
	}

	public List<CPMedia> getCPMedias(
			long cpDefinitionId, ThemeDisplay themeDisplay)
		throws PortalException {

		return CPMediaUtil.getAttachmentCPMedias(
			_portal.getClassNameId(CPDefinition.class.getName()),
			cpDefinitionId, _cpAttachmentFileEntryLocalService,
			_dlFileEntryLocalService, _dlFileEntryModelResourcePermission,
			themeDisplay);
	}

	public List<CPMedia> getImages(
			long cpDefinitionId, ThemeDisplay themeDisplay)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		return CPMediaUtil.getImageCPMedias(
			_amImageHTMLTagFactory,
			_portal.getClassNameId(CPDefinition.class.getName()),
			cpDefinition.getCPDefinitionId(), _commerceCatalogDefaultImage,
			_commerceMediaResolver, _cpAttachmentFileEntryLocalService, false,
			cpDefinition.getGroupId(), themeDisplay);
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			cpContentRequestHelper.getLiferayPortletResponse();

		if (_hasCPContentPortlet() || (_getCProductId() != 0)) {
			PortletURL portletURL = new PortletURLWrapper(
				liferayPortletResponse.createRenderURL()) {

				@Override
				public String toString() {
					HttpServletRequest originalHttpServletRequest =
						PortalUtil.getOriginalServletRequest(
							cpContentRequestHelper.getRequest());

					ThemeDisplay themeDisplay =
						(ThemeDisplay)originalHttpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					Layout layout = themeDisplay.getLayout();

					String layoutFriendlyURL = layout.getFriendlyURL(
						themeDisplay.getLocale());

					String productURLSeparator =
						_cpFriendlyURL.getProductURLSeparator(
							themeDisplay.getCompanyId());

					return StringUtil.replace(
						super.toString(), layoutFriendlyURL,
						productURLSeparator + _getFriendlyURL());
				}

			};

			return PortletURLBuilder.create(
				portletURL
			).setParameter(
				"cProductId", _cProductId
			).setParameter(
				"delta", _delta
			).buildPortletURL();
		}

		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setParameter(
			"delta", _delta
		).buildPortletURL();
	}

	public SearchContainer<CPCatalogEntry> getSearchContainer()
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			cpContentRequestHelper.getLiferayPortletRequest(), getPortletURL(),
			null, "there-are-no-products");

		_searchContainer.setDelta(_delta);

		CPDataSourceResult cpDataSourceResult = getCPDataSourceResult();

		if (cpDataSourceResult != null) {
			_searchContainer.setResultsAndTotal(
				cpDataSourceResult::getCPCatalogEntries,
				cpDataSourceResult.getLength());
		}

		return _searchContainer;
	}

	private long _getCProductId() {
		HttpServletRequest httpServletRequest =
			cpContentRequestHelper.getRequest();

		long cProductId = ParamUtil.getLong(httpServletRequest, "cProductId");

		if (cProductId > 0) {
			return cProductId;
		}

		CPCatalogEntry cpCatalogEntry =
			(CPCatalogEntry)httpServletRequest.getAttribute(
				CPWebKeys.CP_CATALOG_ENTRY);

		if (cpCatalogEntry != null) {
			cProductId = cpCatalogEntry.getCProductId();
		}

		return cProductId;
	}

	private CPDataSourceResult _getDynamicCPDataSourceResult(
			HttpServletRequest httpServletRequest, int start, int end)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"commerceAccountGroupIds",
				() -> {
					CommerceContext commerceContext =
						(CommerceContext)httpServletRequest.getAttribute(
							CommerceWebKeys.COMMERCE_CONTEXT);

					if (commerceContext == null) {
						return null;
					}

					AccountEntry accountEntry =
						commerceContext.getAccountEntry();

					if (accountEntry == null) {
						return null;
					}

					return commerceContext.getCommerceAccountGroupIds();
				}
			).put(
				"params", new LinkedHashMap<String, Object>()
			).build());
		searchContext.setCompanyId(cpContentRequestHelper.getCompanyId());

		CPQuery cpQuery = new CPQuery();

		Company company = cpContentRequestHelper.getCompany();

		cpPublisherWebHelper.setCategoriesAndTags(
			company.getGroupId(), cpQuery,
			cpContentRequestHelper.getPortletPreferences());

		cpPublisherWebHelper.setOrdering(
			cpQuery, cpContentRequestHelper.getPortletPreferences());

		return _cpDefinitionHelper.search(
			cpContentRequestHelper.getScopeGroupId(), searchContext, cpQuery,
			start, end);
	}

	private String _getFriendlyURL() {
		FriendlyURLEntry friendlyURLEntry = null;

		try {
			friendlyURLEntry =
				_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					PortalUtil.getClassNameId(CProduct.class), _cProductId);
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"No friendly URL entry found for " + _getCProductId(),
					exception);
			}

			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay = cpContentRequestHelper.getThemeDisplay();

		return friendlyURLEntry.getUrlTitle(themeDisplay.getLanguageId());
	}

	private boolean _hasCPContentPortlet() {
		boolean hasCPContentPortlet = false;

		Layout layout = cpContentRequestHelper.getLayout();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		List<Portlet> portlets = layoutTypePortlet.getAllPortlets();

		for (Portlet portlet : portlets) {
			String portletId = portlet.getPortletId();

			if (portletId.startsWith(CPPortletKeys.CP_CONTENT_WEB)) {
				hasCPContentPortlet = true;

				break;
			}
		}

		return hasCPContentPortlet;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPPublisherDisplayContext.class);

	private final AMImageHTMLTagFactory _amImageHTMLTagFactory;
	private final CommerceCatalogDefaultImage _commerceCatalogDefaultImage;
	private final CommerceMediaResolver _commerceMediaResolver;
	private final ConfigurationProvider _configurationProvider;
	private final CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;
	private final CPDataSourceRegistry _cpDataSourceRegistry;
	private final CPDefinitionHelper _cpDefinitionHelper;
	private final CPDefinitionLocalService _cpDefinitionLocalService;
	private final CPFriendlyURL _cpFriendlyURL;
	private final long _cProductId;
	private final int _delta;
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;
	private final Portal _portal;
	private SearchContainer<CPCatalogEntry> _searchContainer;

}