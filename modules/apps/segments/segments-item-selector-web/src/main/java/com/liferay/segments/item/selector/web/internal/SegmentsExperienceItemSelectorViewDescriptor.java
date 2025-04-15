/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.item.selector.SegmentsExperienceItemSelectorCriterion;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SegmentsExperienceItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<SegmentsExperience> {

	public SegmentsExperienceItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		SegmentsExperienceItemSelectorCriterion
			segmentsExperienceItemSelectorCriterion) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_segmentsExperienceItemSelectorCriterion =
			segmentsExperienceItemSelectorCriterion;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[0];
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		SegmentsExperience segmentsExperience) {

		return new SegmentsExperienceItemDescriptor(
			_httpServletRequest, segmentsExperience);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public SearchContainer<SegmentsExperience> getSearchContainer()
		throws PortalException {

		SearchContainer<SegmentsExperience> searchContainer =
			new SearchContainer<>(
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_REQUEST),
				_portletURL, null, "there-are-no-items-to-display");

		searchContainer.setResultsAndTotal(
			SegmentsExperienceLocalServiceUtil.getSegmentsExperiences(
				_themeDisplay.getScopeGroupId(),
				_segmentsExperienceItemSelectorCriterion.getPlid(), true));

		return searchContainer;
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final SegmentsExperienceItemSelectorCriterion
		_segmentsExperienceItemSelectorCriterion;
	private final ThemeDisplay _themeDisplay;

}