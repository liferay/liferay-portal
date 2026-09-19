/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.friendly.url.info.item.provider;

import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.friendly.url.util.comparator.FriendlyURLEntryLocalizationComparator;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "item.class.name=com.liferay.portal.kernel.model.Layout",
	service = InfoItemFriendlyURLProvider.class
)
public class LayoutInfoItemFriendlyURLProvider
	implements InfoItemFriendlyURLProvider<Layout> {

	@Override
	public String getFriendlyURL(Layout layout, String languageId) {
		return layout.getFriendlyURL(LocaleUtil.fromLanguageId(languageId));
	}

	@Override
	public List<FriendlyURLEntryLocalization> getFriendlyURLEntryLocalizations(
		Layout layout, String languageId) {

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
			layout.getGroupId(),
			_layoutFriendlyURLEntryHelper.getClassNameId(
				layout.isPrivateLayout()),
			layout.getPlid(), languageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			_friendlyURLEntryLocalizationComparator);
	}

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	private final FriendlyURLEntryLocalizationComparator
		_friendlyURLEntryLocalizationComparator =
			FriendlyURLEntryLocalizationComparator.getInstance(false);

	@Reference
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

}