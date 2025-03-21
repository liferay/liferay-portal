/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal.search;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.SetUtil;

import jakarta.portlet.RenderResponse;

import java.util.Set;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceChannelItemSelectorChecker extends EmptyOnClickRowChecker {

	public CommerceChannelItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedCommerceChannelIds) {

		super(renderResponse);

		_checkedCommerceChannelIds = SetUtil.fromArray(
			checkedCommerceChannelIds);
	}

	@Override
	public boolean isChecked(Object object) {
		CommerceChannel commerceChannel = (CommerceChannel)object;

		return _checkedCommerceChannelIds.contains(
			commerceChannel.getCommerceChannelId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final Set<Long> _checkedCommerceChannelIds;

}