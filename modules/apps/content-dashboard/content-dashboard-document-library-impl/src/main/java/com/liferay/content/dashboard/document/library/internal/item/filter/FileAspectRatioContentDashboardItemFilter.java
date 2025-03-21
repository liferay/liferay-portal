/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.filter;

import com.liferay.content.dashboard.document.library.internal.constants.ContentDashboardConstants;
import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class FileAspectRatioContentDashboardItemFilter
	implements ContentDashboardItemFilter {

	public FileAspectRatioContentDashboardItemFilter(
		HttpServletRequest httpServletRequest, Language language,
		Portal portal) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
	}

	@Override
	public DropdownItem getDropdownItem() {
		return DropdownItemBuilder.setDropdownItems(
			DropdownItemListBuilder.add(
				_getDropdownItem(ContentDashboardConstants.AspectRatio.WIDE)
			).add(
				_getDropdownItem(ContentDashboardConstants.AspectRatio.TALL)
			).add(
				_getDropdownItem(ContentDashboardConstants.AspectRatio.SQUARE)
			).build()
		).setLabel(
			_language.get(_httpServletRequest, "content-dashboard-aspect-ratio")
		).setType(
			"contextual"
		).build();
	}

	@Override
	public Filter getFilter() {
		List<String> parameterValues = getParameterValues();

		if (ListUtil.isEmpty(parameterValues)) {
			return null;
		}

		TermsFilter termsFilter = new TermsFilter("aspectRatio");

		if (ListUtil.isEmpty(parameterValues)) {
			return termsFilter;
		}

		ContentDashboardConstants.AspectRatio aspectRatio =
			ContentDashboardConstants.AspectRatio.parse(parameterValues.get(0));

		if (aspectRatio != null) {
			termsFilter.addValue(aspectRatio.getType());
		}

		return termsFilter;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "filter-by-aspect-ratio");
	}

	@Override
	public String getName() {
		return "aspect-ratio";
	}

	@Override
	public String getParameterLabel(Locale locale) {
		return _language.get(locale, "content-dashboard-aspect-ratio");
	}

	@Override
	public String getParameterName() {
		return "aspectRatio";
	}

	@Override
	public List<String> getParameterValues() {
		return Arrays.asList(
			ParamUtil.getStringValues(_httpServletRequest, getParameterName()));
	}

	@Override
	public Type getType() {
		return Type.SUBMENU;
	}

	@Override
	public String getURL() {
		return null;
	}

	private DropdownItem _getDropdownItem(
		ContentDashboardConstants.AspectRatio aspectRatio) {

		return DropdownItemBuilder.setActive(
			_isSelected(aspectRatio)
		).setHref(
			_getURL(aspectRatio)
		).setLabel(
			_language.get(_httpServletRequest, aspectRatio.getType())
		).build();
	}

	private String _getURL(ContentDashboardConstants.AspectRatio aspectRatio) {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		String url = HttpComponentsUtil.removeParameter(
			_portal.getCurrentCompleteURL(_httpServletRequest),
			portletResponse.getNamespace() + getParameterName());

		return HttpComponentsUtil.addParameter(
			url, portletResponse.getNamespace() + getParameterName(),
			aspectRatio.getType());
	}

	private boolean _isSelected(
		ContentDashboardConstants.AspectRatio aspectRatio) {

		List<String> parameterValues = getParameterValues();

		if (ListUtil.isEmpty(parameterValues)) {
			return false;
		}

		return Objects.equals(parameterValues.get(0), aspectRatio.getType());
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;

}