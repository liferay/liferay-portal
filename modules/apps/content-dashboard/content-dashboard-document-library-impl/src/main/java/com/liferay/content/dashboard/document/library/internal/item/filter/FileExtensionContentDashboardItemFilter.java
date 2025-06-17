/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.filter;

import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileExtensionItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Cristina González
 */
public class FileExtensionContentDashboardItemFilter
	implements ContentDashboardItemFilter {

	public FileExtensionContentDashboardItemFilter(
		HttpServletRequest httpServletRequest, ItemSelector itemSelector,
		Language language, Portal portal) {

		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;
		_language = language;
		_portal = portal;
	}

	@Override
	public DropdownItem getDropdownItem() {
		return DropdownItemBuilder.putData(
			"dialogTitle",
			() -> getLabel(_portal.getLocale(_httpServletRequest))
		).putData(
			"itemValueKey", (String)null
		).putData(
			"multiple", Boolean.TRUE.toString()
		).putData(
			"redirectURL", _getRedirectURL()
		).putData(
			"selectEventName", "selectedFileExtension"
		).putData(
			"selectItemURL", getURL()
		).putData(
			"urlParamName", getParameterName()
		).setActive(
			ListUtil.isNotEmpty(getParameterValues())
		).setLabel(
			_language.get(_httpServletRequest, "extension")
		).build();
	}

	@Override
	public Filter getFilter() {
		List<String> fileExtensions = getParameterValues();

		if (ListUtil.isEmpty(fileExtensions)) {
			return null;
		}

		TermsFilter termsFilter = new TermsFilter("fileExtension");

		for (String fileExtension : fileExtensions) {
			termsFilter.addValue(fileExtension);
		}

		return termsFilter;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "filter-by-extension");
	}

	@Override
	public String getName() {
		return "file-extension";
	}

	@Override
	public String getParameterLabel(Locale locale) {
		return _language.get(locale, "extension");
	}

	@Override
	public String getParameterName() {
		return "fileExtension";
	}

	@Override
	public List<String> getParameterValues() {
		return _getFileExtensions(_httpServletRequest);
	}

	@Override
	public Type getType() {
		return Type.ITEM_SELECTOR;
	}

	@Override
	public String getURL() {
		try {
			PortletRequest portletRequest =
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST);

			RequestBackedPortletURLFactory requestBackedPortletURLFactory =
				RequestBackedPortletURLFactoryUtil.create(portletRequest);

			FileExtensionItemSelectorCriterion
				fileExtensionItemSelectorCriterion =
					new FileExtensionItemSelectorCriterion();

			fileExtensionItemSelectorCriterion.
				setDesiredItemSelectorReturnTypes(
					Collections.singletonList(
						new UUIDItemSelectorReturnType()));

			PortletResponse portletResponse =
				(PortletResponse)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE);

			return PortletURLBuilder.create(
				_itemSelector.getItemSelectorURL(
					requestBackedPortletURLFactory,
					portletResponse.getNamespace() + "selectedFileExtension",
					fileExtensionItemSelectorCriterion)
			).setParameter(
				"checkedFileExtensions",
				() -> {
					List<String> fileExtensions = getParameterValues();

					return fileExtensions.toArray(new String[0]);
				}
			).buildString();
		}
		catch (Exception exception) {
			_log.error(exception);

			return StringPool.BLANK;
		}
	}

	private List<String> _getFileExtensions(
		HttpServletRequest httpServletRequest) {

		return Arrays.asList(
			ParamUtil.getStringValues(httpServletRequest, "fileExtension"));
	}

	private String _getRedirectURL() {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		return HttpComponentsUtil.removeParameter(
			_portal.getCurrentCompleteURL(_httpServletRequest),
			portletResponse.getNamespace() + getParameterName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileExtensionContentDashboardItemFilter.class);

	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final Language _language;
	private final Portal _portal;

}