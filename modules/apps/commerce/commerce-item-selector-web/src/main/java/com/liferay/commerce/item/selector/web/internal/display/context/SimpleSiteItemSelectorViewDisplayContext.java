/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.item.selector.web.internal.display.context;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
public class SimpleSiteItemSelectorViewDisplayContext
	extends BaseCommerceItemSelectorViewDisplayContext<Group> {

	public SimpleSiteItemSelectorViewDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		GroupService groupService, HttpServletRequest httpServletRequest,
		PortletURL portletURL, String itemSelectedEventName, boolean search) {

		super(httpServletRequest, portletURL, itemSelectedEventName);

		_commerceChannelLocalService = commerceChannelLocalService;
		_groupService = groupService;
		_search = search;
	}

	public String getChannelUsingSite(long siteGroupId) throws PortalException {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				siteGroupId);

		if (commerceChannel == null) {
			return StringPool.BLANK;
		}

		return commerceChannel.getName();
	}

	public long getGroupId() {
		return ParamUtil.getLong(
			cpRequestHelper.getRenderRequest(), "siteGroupId", -1);
	}

	@Override
	public PortletURL getPortletURL() {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setParameter(
			"siteGroupId", getGroupId()
		).buildPortletURL();
	}

	@Override
	public SearchContainer<Group> getSearchContainer() throws PortalException {
		if (searchContainer != null) {
			return searchContainer;
		}

		String emptyResultsMessage = "no-sites-were-found";

		long groupId = getGroupId();

		if (groupId > 0) {
			emptyResultsMessage = "no-sites-were-found-in-x";

			Group group = _groupService.getGroup(groupId);

			Locale locale = cpRequestHelper.getLocale();

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());

			emptyResultsMessage = LanguageUtil.format(
				resourceBundle, emptyResultsMessage, group.getName(locale),
				false);
		}

		searchContainer = new SearchContainer<>(
			cpRequestHelper.getRenderRequest(), getPortletURL(), null,
			emptyResultsMessage);

		searchContainer.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		searchContainer.setOrderByComparator(
			new GroupNameComparator(orderByAsc));
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			() -> _groupService.search(
				cpRequestHelper.getCompanyId(),
				new long[] {
					ClassNameLocalServiceUtil.getClassNameId(Group.class),
					ClassNameLocalServiceUtil.getClassNameId(Organization.class)
				},
				null,
				LinkedHashMapBuilder.<String, Object>put(
					"active", true
				).put(
					"site", true
				).build(),
				searchContainer.getStart(), searchContainer.getEnd(), null),
			_groupService.searchCount(
				cpRequestHelper.getCompanyId(), null, null, new String[0]));
		searchContainer.setSearch(_search);

		return searchContainer;
	}

	public boolean isSiteAvailable(long siteGroupId) {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				siteGroupId);

		if (commerceChannel == null) {
			return true;
		}

		return false;
	}

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final GroupService _groupService;
	private final boolean _search;

}