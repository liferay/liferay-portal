/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.info.item.provider;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.friendly.url.util.comparator.FriendlyURLEntryLocalizationComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "item.class.name=com.liferay.blogs.model.BlogsEntry",
	service = InfoItemFriendlyURLProvider.class
)
public class BlogsEntryInfoItemFriendlyURLProvider
	implements InfoItemFriendlyURLProvider<BlogsEntry> {

	@Override
	public String getFriendlyURL(BlogsEntry blogsEntry, String languageId) {
		FriendlyURLEntry mainFriendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_portal.getClassNameId(BlogsEntry.class),
				blogsEntry.getEntryId());

		if (mainFriendlyURLEntry == null) {
			return blogsEntry.getUrlTitle();
		}

		long groupId = _getGroupId();

		if ((groupId != GroupConstants.DEFAULT_LIVE_GROUP_ID) &&
			(groupId != mainFriendlyURLEntry.getGroupId())) {

			return _getGroupFriendlyURL(
				blogsEntry.getEntryId(), mainFriendlyURLEntry);
		}

		return mainFriendlyURLEntry.getCategorizedUrlTitle(languageId);
	}

	@Override
	public List<FriendlyURLEntryLocalization> getFriendlyURLEntryLocalizations(
		BlogsEntry blogsEntry, String languageId) {

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
			blogsEntry.getGroupId(), _portal.getClassNameId(BlogsEntry.class),
			blogsEntry.getEntryId(),
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			_friendlyURLEntryLocalizationComparator);
	}

	private String _getGroupFriendlyURL(
		long entryId, FriendlyURLEntry friendlyURLEntry) {

		Group group = _groupLocalService.fetchGroup(
			friendlyURLEntry.getGroupId());

		if (group == null) {
			return String.valueOf(entryId);
		}

		String groupFriendlyURL = group.getFriendlyURL();

		return groupFriendlyURL.replaceFirst(StringPool.SLASH, "") +
			StringPool.SLASH + friendlyURLEntry.getUrlTitle();
	}

	private long _getGroupId() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return GroupThreadLocal.getGroupId();
		}

		if (serviceContext.getThemeDisplay() == null) {
			return serviceContext.getScopeGroupId();
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (themeDisplay.getSiteGroupId() !=
				GroupConstants.DEFAULT_LIVE_GROUP_ID) {

			return themeDisplay.getSiteGroupId();
		}

		return themeDisplay.getScopeGroupId();
	}

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	private final FriendlyURLEntryLocalizationComparator
		_friendlyURLEntryLocalizationComparator =
			FriendlyURLEntryLocalizationComparator.getInstance(false);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}