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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class FileResolutionContentDashboardItemFilter
	implements ContentDashboardItemFilter {

	public FileResolutionContentDashboardItemFilter(
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
				_getDropdownItem(ContentDashboardConstants.Resolution.SMALL)
			).add(
				_getDropdownItem(ContentDashboardConstants.Resolution.MEDIUM)
			).add(
				_getDropdownItem(ContentDashboardConstants.Resolution.LARGE)
			).build()
		).setLabel(
			_language.get(_httpServletRequest, "resolution")
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

		TermsFilter termsFilter = new TermsFilter("resolution");

		if (ListUtil.isEmpty(parameterValues)) {
			return termsFilter;
		}

		ContentDashboardConstants.Resolution resolution =
			ContentDashboardConstants.Resolution.parse(parameterValues.get(0));

		if (resolution != null) {
			termsFilter.addValue(resolution.getType());
		}

		return termsFilter;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "filter-by-resolution");
	}

	@Override
	public String getName() {
		return "resolution";
	}

	@Override
	public String getParameterLabel(Locale locale) {
		return _language.get(locale, "resolution");
	}

	@Override
	public String getParameterName() {
		return "resolution";
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
		ContentDashboardConstants.Resolution resolution) {

		return DropdownItemBuilder.setActive(
			_isSelected(resolution)
		).setHref(
			_getURL(resolution)
		).setLabel(
			_getLabel(resolution)
		).build();
	}

	private String _getLabel(ContentDashboardConstants.Resolution resolution) {
		StringBundler sb = new StringBundler(5);

		sb.append(_language.get(_httpServletRequest, resolution.getType()));
		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		if (resolution == ContentDashboardConstants.Resolution.LARGE) {
			sb.append(
				StringUtil.toLowerCase(
					_language.format(
						_httpServletRequest, "from-x",
						new Object[] {
							_getResolutionLabel(
								resolution.getStartLengthValue(),
								resolution.getStartWidthValue())
						})));
		}
		else if (resolution == ContentDashboardConstants.Resolution.MEDIUM) {
			sb.append(
				StringUtil.toLowerCase(
					_language.format(
						_httpServletRequest, "from-x-to-x",
						new Object[] {
							_getResolutionLabel(
								resolution.getStartLengthValue(),
								resolution.getStartWidthValue()),
							_getResolutionLabel(
								resolution.getEndLengthValue(),
								resolution.getEndWidthValue())
						})));
		}
		else if (resolution == ContentDashboardConstants.Resolution.SMALL) {
			sb.append(
				StringUtil.toLowerCase(
					_language.format(
						_httpServletRequest, "up-to-x",
						new Object[] {
							_getResolutionLabel(
								resolution.getEndLengthValue(),
								resolution.getEndWidthValue())
						})));
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private String _getResolutionLabel(long length, long width) {
		StringBundler sb = new StringBundler(3);

		sb.append(width);
		sb.append("x");
		sb.append(length);

		return sb.toString();
	}

	private String _getURL(ContentDashboardConstants.Resolution resolution) {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		String url = HttpComponentsUtil.removeParameter(
			_portal.getCurrentCompleteURL(_httpServletRequest),
			portletResponse.getNamespace() + getParameterName());

		return HttpComponentsUtil.addParameter(
			url, portletResponse.getNamespace() + getParameterName(),
			resolution.getType());
	}

	private boolean _isSelected(
		ContentDashboardConstants.Resolution resolution) {

		List<String> parameterValues = getParameterValues();

		if (ListUtil.isEmpty(parameterValues)) {
			return false;
		}

		return Objects.equals(parameterValues.get(0), resolution.getType());
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;

}