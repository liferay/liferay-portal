/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.web.internal.frontend.data.set.provider;

import com.liferay.commerce.account.web.internal.constants.CommerceAccountFDSNames;
import com.liferay.commerce.account.web.internal.model.User;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "fds.data.provider.key=" + CommerceAccountFDSNames.ACCOUNT_ENTRY_DEFAULT_USERS,
	service = FDSDataProvider.class
)
public class AccountEntryDefaultUsersDataSetFDSDataProvider
	implements FDSDataProvider<User> {

	@Override
	public List<User> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long accountEntryId = ParamUtil.getLong(
			httpServletRequest, "accountEntryId");
		int type = ParamUtil.getInteger(httpServletRequest, "type");

		return TransformUtil.transform(
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRels(
					accountEntryId,
					CommerceChannelAccountEntryRelConstants.TYPE_USER,
					fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), null),
			commerceChannelAccountEntryRel -> {
				com.liferay.portal.kernel.model.User user =
					_userService.getUserById(
						commerceChannelAccountEntryRel.getClassPK());

				return new User(
					commerceChannelAccountEntryRel.getAccountEntryId(),
					_getChannelName(
						accountEntryId,
						commerceChannelAccountEntryRel.getCommerceChannelId(),
						httpServletRequest, type),
					commerceChannelAccountEntryRel.
						getCommerceChannelAccountEntryRelId(),
					user.getFullName(),
					commerceChannelAccountEntryRel.getType());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long accountEntryId = ParamUtil.getLong(
			httpServletRequest, "accountEntryId");

		return _commerceChannelAccountEntryRelService.
			getCommerceChannelAccountEntryRelsCount(
				accountEntryId,
				CommerceChannelAccountEntryRelConstants.TYPE_USER);
	}

	private String _getChannelName(
			long accountEntryId, long commerceChannelId,
			HttpServletRequest httpServletRequest, int type)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannel(commerceChannelId);

		if (commerceChannel == null) {
			List<CommerceChannelAccountEntryRel>
				commerceChannelAccountEntryRels =
					_commerceChannelAccountEntryRelService.
						getCommerceChannelAccountEntryRels(
							accountEntryId, type, QueryUtil.ALL_POS,
							QueryUtil.ALL_POS, null);

			if (commerceChannelAccountEntryRels.size() == 1) {
				CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
					commerceChannelAccountEntryRels.get(0);

				if (commerceChannelAccountEntryRel.getCommerceChannelId() ==
						0) {

					return _language.get(httpServletRequest, "all-channels");
				}
			}
			else if (!commerceChannelAccountEntryRels.isEmpty()) {
				return _language.get(httpServletRequest, "all-other-channels");
			}

			return _language.get(httpServletRequest, "all-channels");
		}

		return commerceChannel.getName();
	}

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private Language _language;

	@Reference
	private UserService _userService;

}