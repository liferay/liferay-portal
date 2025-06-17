/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.item.selector;

import com.liferay.commerce.channel.web.internal.search.CommerceCurrencyItemSelectorChecker;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabio Monaco
 */
@Component(service = ItemSelectorView.class)
public class CommerceCurrencyItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<CommerceCurrencyItemSelectorCriterion> {

	@Override
	public String getClassName() {
		return CommerceCurrency.class.getName();
	}

	@Override
	public Class<CommerceCurrencyItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return CommerceCurrencyItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "currencies");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			CommerceCurrencyItemSelectorCriterion
				commerceCurrencyItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			commerceCurrencyItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new CommerceCurrencyItemSelectorViewDescriptor(
				(HttpServletRequest)servletRequest, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private CommerceCurrencyService _commerceCurrencyService;

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<CommerceCurrencyItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class CommerceCurrencyItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public CommerceCurrencyItemDescriptor(
			CommerceCurrency commerceCurrency,
			HttpServletRequest httpServletRequest) {

			_commerceCurrency = commerceCurrency;
			_httpServletRequest = httpServletRequest;
		}

		@Override
		public String getIcon() {
			return null;
		}

		@Override
		public String getImageURL() {
			return null;
		}

		@Override
		public Date getModifiedDate() {
			return _commerceCurrency.getModifiedDate();
		}

		@Override
		public String getPayload() {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return JSONUtil.put(
				"className", CommerceCurrency.class.getName()
			).put(
				"classNameId",
				_portal.getClassNameId(CommerceCurrency.class.getName())
			).put(
				"classPK", _commerceCurrency.getCommerceCurrencyId()
			).put(
				"title", _commerceCurrency.getName(themeDisplay.getLocale())
			).put(
				"type",
				ResourceActionsUtil.getModelResource(
					themeDisplay.getLocale(), CommerceCurrency.class.getName())
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			return null;
		}

		@Override
		public String getTitle(Locale locale) {
			return _commerceCurrency.getName(locale);
		}

		@Override
		public long getUserId() {
			return _commerceCurrency.getUserId();
		}

		@Override
		public String getUserName() {
			return _commerceCurrency.getUserName();
		}

		private final CommerceCurrency _commerceCurrency;
		private final HttpServletRequest _httpServletRequest;

	}

	private class CommerceCurrencyItemSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<CommerceCurrency> {

		public CommerceCurrencyItemSelectorViewDescriptor(
			HttpServletRequest httpServletRequest, PortletURL portletURL) {

			_httpServletRequest = httpServletRequest;
			_portletURL = portletURL;

			_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
			_renderResponse = (RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);
		}

		@Override
		public String getDefaultDisplayStyle() {
			return "list";
		}

		@Override
		public ItemSelectorViewDescriptor.ItemDescriptor getItemDescriptor(
			CommerceCurrency commerceCurrency) {

			return new CommerceCurrencyItemDescriptor(
				commerceCurrency, _httpServletRequest);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new InfoItemItemSelectorReturnType();
		}

		@Override
		public SearchContainer<CommerceCurrency> getSearchContainer()
			throws PortalException {

			SearchContainer<CommerceCurrency> entriesSearchContainer =
				new SearchContainer<>(
					(PortletRequest)_httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_REQUEST),
					_portletURL, null, "no-entries-were-found");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			entriesSearchContainer.setResultsAndTotal(
				() -> _commerceCurrencyService.getCommerceCurrencies(
					themeDisplay.getCompanyId(),
					entriesSearchContainer.getStart(),
					entriesSearchContainer.getEnd(),
					entriesSearchContainer.getOrderByComparator()),
				_commerceCurrencyService.getCommerceCurrenciesCount(
					themeDisplay.getCompanyId()));

			entriesSearchContainer.setRowChecker(
				new CommerceCurrencyItemSelectorChecker(
					_renderResponse,
					ParamUtil.getLongValues(
						_portletRequest, "checkedCommerceCurrencyIds")));

			return entriesSearchContainer;
		}

		@Override
		public boolean isMultipleSelection() {
			return true;
		}

		@Override
		public boolean isShowBreadcrumb() {
			return false;
		}

		private final HttpServletRequest _httpServletRequest;
		private final PortletRequest _portletRequest;
		private final PortletURL _portletURL;
		private final RenderResponse _renderResponse;

	}

}