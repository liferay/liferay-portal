/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.filter;

import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class FileSizeContentDashboardItemFilter
	implements ContentDashboardItemFilter {

	public FileSizeContentDashboardItemFilter(
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
				_getDropdownItem(SizeType.SMALL)
			).add(
				_getDropdownItem(SizeType.MEDIUM)
			).add(
				_getDropdownItem(SizeType.LARGE)
			).build()
		).setLabel(
			_language.get(_httpServletRequest, "size")
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

		BooleanFilter booleanFilter = new BooleanFilter();

		SizeType sizeType = SizeType.parse(parameterValues.get(0));

		if (sizeType != null) {
			booleanFilter.addRangeTerm(
				"size_sortable", sizeType.getStartValue(),
				sizeType.getEndValue());
		}

		return booleanFilter;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "filter-by-size");
	}

	@Override
	public String getName() {
		return "file-size";
	}

	@Override
	public String getParameterLabel(Locale locale) {
		return _language.get(locale, "size");
	}

	@Override
	public String getParameterName() {
		return "fileSize";
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

	private DropdownItem _getDropdownItem(SizeType sizeType) {
		return DropdownItemBuilder.setActive(
			_isSelected(sizeType)
		).setHref(
			_getURL(sizeType)
		).setLabel(
			_getLabel(sizeType)
		).build();
	}

	private String _getLabel(SizeType sizeType) {
		StringBundler sb = new StringBundler(5);

		sb.append(_language.get(_httpServletRequest, sizeType.getType()));
		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		if (sizeType == SizeType.LARGE) {
			sb.append(
				StringUtil.toLowerCase(
					_language.format(
						_httpServletRequest, "from-x",
						new Object[] {
							TextFormatter.formatStorageSize(
								sizeType.getStartValue(),
								_httpServletRequest.getLocale())
						})));
		}
		else if (sizeType == SizeType.MEDIUM) {
			sb.append(
				StringUtil.toLowerCase(
					_language.format(
						_httpServletRequest, "from-x-to-x",
						new Object[] {
							TextFormatter.formatStorageSize(
								sizeType.getStartValue(),
								_httpServletRequest.getLocale()),
							TextFormatter.formatStorageSize(
								sizeType.getEndValue(),
								_httpServletRequest.getLocale())
						})));
		}
		else {
			sb.append(
				StringUtil.toLowerCase(
					_language.format(
						_httpServletRequest, "up-to-x",
						new Object[] {
							TextFormatter.formatStorageSize(
								sizeType.getEndValue(),
								_httpServletRequest.getLocale())
						})));
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private String _getURL(SizeType sizeType) {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		String url = HttpComponentsUtil.removeParameter(
			_portal.getCurrentCompleteURL(_httpServletRequest),
			portletResponse.getNamespace() + getParameterName());

		return HttpComponentsUtil.addParameter(
			url, portletResponse.getNamespace() + getParameterName(),
			sizeType.getType());
	}

	private boolean _isSelected(SizeType sizeType) {
		List<String> parameterValues = getParameterValues();

		if (ListUtil.isEmpty(parameterValues)) {
			return false;
		}

		return Objects.equals(parameterValues.get(0), sizeType.getType());
	}

	private static final long _FILE_SIZE_MEDIUM = 1024 * 1024;

	private static final long _FILE_SIZE_SMALL = 150 * 1024;

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;

	private enum SizeType {

		LARGE("large", _FILE_SIZE_MEDIUM + 1, Long.MAX_VALUE),
		MEDIUM("medium", _FILE_SIZE_SMALL + 1, _FILE_SIZE_MEDIUM),
		SMALL("small", 0, _FILE_SIZE_SMALL);

		public static SizeType parse(String type) {
			for (SizeType sizeType : values()) {
				if (Objects.equals(sizeType.getType(), type)) {
					return sizeType;
				}
			}

			return null;
		}

		public long getEndValue() {
			return _endValue;
		}

		public long getStartValue() {
			return _startValue;
		}

		public String getType() {
			return _type;
		}

		@Override
		public String toString() {
			return _type;
		}

		private SizeType(String type, long startValue, long endValue) {
			_type = type;
			_startValue = startValue;
			_endValue = endValue;
		}

		private final long _endValue;
		private final long _startValue;
		private final String _type;

	}

}