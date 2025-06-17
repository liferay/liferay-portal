/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.item.selector;

import com.liferay.commerce.channel.web.internal.search.CountryItemSelectorChecker;
import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CountryService;
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
 * @author Stefano Motta
 */
@Component(service = ItemSelectorView.class)
public class CountryItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<CountryItemSelectorCriterion> {

	@Override
	public String getClassName() {
		return Country.class.getName();
	}

	@Override
	public Class<CountryItemSelectorCriterion> getItemSelectorCriterionClass() {
		return CountryItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "countries");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			CountryItemSelectorCriterion countryItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, countryItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new CountryItemSelectorViewDescriptor(
				(HttpServletRequest)servletRequest, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private CountryService _countryService;

	@Reference
	private ItemSelectorViewDescriptorRenderer<CountryItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class CountryItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public CountryItemDescriptor(
			Country country, HttpServletRequest httpServletRequest) {

			_country = country;
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
			return _country.getModifiedDate();
		}

		@Override
		public String getPayload() {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return JSONUtil.put(
				"className", Country.class.getName()
			).put(
				"classNameId", _portal.getClassNameId(Country.class.getName())
			).put(
				"classPK", _country.getCountryId()
			).put(
				"title", _country.getName(themeDisplay.getLocale())
			).put(
				"type",
				ResourceActionsUtil.getModelResource(
					themeDisplay.getLocale(), Country.class.getName())
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			return null;
		}

		@Override
		public String getTitle(Locale locale) {
			return _country.getTitle(locale);
		}

		@Override
		public long getUserId() {
			return _country.getUserId();
		}

		@Override
		public String getUserName() {
			return _country.getUserName();
		}

		private final Country _country;
		private final HttpServletRequest _httpServletRequest;

	}

	private class CountryItemSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<Country> {

		public CountryItemSelectorViewDescriptor(
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
			Country country) {

			return new CountryItemDescriptor(country, _httpServletRequest);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new InfoItemItemSelectorReturnType();
		}

		@Override
		public SearchContainer<Country> getSearchContainer() {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			SearchContainer<Country> entriesSearchContainer =
				new SearchContainer<>(
					(PortletRequest)_httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_REQUEST),
					_portletURL, null, "no-entries-were-found");

			entriesSearchContainer.setResultsAndTotal(
				() -> _countryService.getCompanyCountries(
					themeDisplay.getCompanyId(),
					entriesSearchContainer.getStart(),
					entriesSearchContainer.getEnd(),
					entriesSearchContainer.getOrderByComparator()),
				_countryService.getCompanyCountriesCount(
					themeDisplay.getCompanyId()));

			entriesSearchContainer.setRowChecker(
				new CountryItemSelectorChecker(
					_renderResponse,
					ParamUtil.getLongValues(
						_portletRequest, "checkedCountryIds")));

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