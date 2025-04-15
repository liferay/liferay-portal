/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.account.web.internal.display.context.helper.CommerceAccountRelRequestHelper;
import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.service.CommerceShippingOptionAccountEntryRelService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.commerce.shipping.engine.fixed.util.comparator.CommerceShippingFixedOptionNameComparator;
import com.liferay.commerce.util.comparator.CommerceShippingMethodNameComparator;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
public class CommerceAccountDisplayContext {

	public CommerceAccountDisplayContext(
			ModelResourcePermission<AccountEntry>
				accountEntryModelResourcePermission,
			AccountEntryService accountEntryService,
			CommerceChannelAccountEntryRelService
				commerceChannelAccountEntryRelService,
			CommerceChannelService commerceChannelService,
			CommerceShippingFixedOptionService
				commerceShippingFixedOptionService,
			CommerceShippingMethodService commerceShippingMethodService,
			CommerceShippingOptionAccountEntryRelService
				commerceShippingOptionAccountEntryRelService,
			HttpServletRequest httpServletRequest, Language language,
			Portal portal, UserService userService)
		throws PortalException {

		_accountEntryModelResourcePermission =
			accountEntryModelResourcePermission;
		_accountEntryService = accountEntryService;
		_commerceChannelAccountEntryRelService =
			commerceChannelAccountEntryRelService;
		_commerceChannelService = commerceChannelService;
		_commerceShippingFixedOptionService =
			commerceShippingFixedOptionService;
		_commerceShippingMethodService = commerceShippingMethodService;
		_commerceShippingOptionAccountEntryRelService =
			commerceShippingOptionAccountEntryRelService;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
		_userService = userService;

		long accountEntryId = ParamUtil.getLong(
			httpServletRequest, "accountEntryId");

		_accountEntry = _accountEntryService.fetchAccountEntry(accountEntryId);

		_commerceAccountRelRequestHelper = new CommerceAccountRelRequestHelper(
			httpServletRequest);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		_commerceChannel = _commerceChannelService.fetchCommerceChannel(
			commerceChannelId);

		_commerceShippingOptionAccountEntryRel =
			_commerceShippingOptionAccountEntryRelService.
				fetchCommerceShippingOptionAccountEntryRel(
					accountEntryId, commerceChannelId);

		_type = ParamUtil.getInteger(httpServletRequest, "type");
	}

	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel()
		throws PortalException {

		long commerceChannelAccountEntryRelId = ParamUtil.getLong(
			_httpServletRequest, "commerceChannelAccountEntryRelId");

		return _commerceChannelAccountEntryRelService.
			fetchCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRelId);
	}

	public AccountEntry getAccountEntry() {
		return _accountEntry;
	}

	public long getAccountEntryId() {
		if (_accountEntry == null) {
			return 0;
		}

		return _accountEntry.getAccountEntryId();
	}

	public String getAddCommerceChannelAccountEntryRelRenderURL() {
		return PortletURLBuilder.createRenderURL(
			_commerceAccountRelRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/account_entries_admin/edit_account_entry_user"
		).setParameter(
			"accountEntryId", _accountEntry.getAccountEntryId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<User> getAllowedUsers() throws PortalException {
		return TransformUtil.transform(
			_userService.getCompanyUsers(
				_commerceAccountRelRequestHelper.getCompanyId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			user -> {
				if (_accountEntryModelResourcePermission.contains(
						PermissionCheckerFactoryUtil.create(user), 0,
						CommerceAccountActionKeys.
							MANAGE_AVAILABLE_ACCOUNTS_VIA_USER_CHANNEL_REL)) {

					return user;
				}

				return null;
			});
	}

	public long getCommerceChannelId() {
		if (_commerceChannel == null) {
			return 0;
		}

		return _commerceChannel.getCommerceChannelId();
	}

	public String getCommerceChannelsEmptyOptionKey() throws PortalException {
		int commerceChannelAccountEntryRelsCount =
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRelsCount(
					_accountEntry.getAccountEntryId(),
					CommerceChannelAccountEntryRelConstants.TYPE_USER);

		if (commerceChannelAccountEntryRelsCount > 0) {
			return "all-other-channels";
		}

		return "all-channels";
	}

	public List<CommerceShippingFixedOption> getCommerceShippingFixedOptions()
		throws PortalException {

		if (_commerceChannel == null) {
			return Collections.emptyList();
		}

		Locale locale = _portal.getLocale(_httpServletRequest);

		List<CommerceShippingFixedOption> commerceShippingFixedOptions =
			new ArrayList<>();

		for (CommerceShippingMethod commerceShippingMethod :
				_commerceShippingMethodService.getCommerceShippingMethods(
					_commerceChannel.getGroupId(), true, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS,
					new CommerceShippingMethodNameComparator(true, locale))) {

			commerceShippingFixedOptions.addAll(
				_commerceShippingFixedOptionService.
					getCommerceShippingFixedOptions(
						commerceShippingMethod.getCommerceShippingMethodId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						new CommerceShippingFixedOptionNameComparator(
							true, locale)));
		}

		return commerceShippingFixedOptions;
	}

	public CommerceShippingOptionAccountEntryRel
		getCommerceShippingOptionAccountEntryRel() {

		return _commerceShippingOptionAccountEntryRel;
	}

	public String getCommerceShippingOptionLabel(
			CommerceShippingFixedOption commerceShippingFixedOption)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingFixedOption.getCommerceShippingMethodId());

		StringBundler sb = new StringBundler(3);

		Locale locale = _portal.getLocale(_httpServletRequest);

		sb.append(commerceShippingMethod.getName(locale));

		sb.append(" / ");
		sb.append(commerceShippingFixedOption.getName(locale));

		return sb.toString();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasPermission(CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS)) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						getAddCommerceChannelAccountEntryRelRenderURL());
					dropdownItem.setLabel(
						_language.get(_httpServletRequest, "add-user"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public List<CommerceChannel> getFilteredCommerceChannels()
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			fetchCommerceChannelAccountEntryRel();

		List<Long> commerceChannelIds = TransformUtil.transform(
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRels(
					_accountEntry.getAccountEntryId(), _type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null),
			commerceChannel -> {
				if ((commerceChannelAccountEntryRel == null) ||
					(commerceChannel.getCommerceChannelId() !=
						commerceChannelAccountEntryRel.
							getCommerceChannelId())) {

					return commerceChannel.getCommerceChannelId();
				}

				return null;
			});

		return ListUtil.filter(
			_commerceChannelService.getEligibleCommerceChannels(
				_accountEntry.getAccountEntryId(), null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			commerceChannel -> !commerceChannelIds.contains(
				commerceChannel.getCommerceChannelId()));
	}

	public String getModalTitle() {
		return _language.get(
			_commerceAccountRelRequestHelper.getRequest(), "set-user");
	}

	public String getName() {
		return _accountEntry.getName();
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _accountEntryModelResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			_accountEntry.getAccountEntryId(), actionId);
	}

	public boolean isCommerceChannelSelected(long commerceChannelId)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			fetchCommerceChannelAccountEntryRel();

		if (commerceChannelAccountEntryRel == null) {
			if (commerceChannelId == 0) {
				return true;
			}

			return false;
		}

		if (commerceChannelAccountEntryRel.getCommerceChannelId() ==
				commerceChannelId) {

			return true;
		}

		return false;
	}

	public boolean isCommerceShippingFixedOptionChecked(String key) {
		if ((_commerceShippingOptionAccountEntryRel == null) &&
			Validator.isBlank(key)) {

			return true;
		}

		if ((_commerceShippingOptionAccountEntryRel != null) &&
			key.equals(
				_commerceShippingOptionAccountEntryRel.
					getCommerceShippingOptionKey())) {

			return true;
		}

		return false;
	}

	public boolean isUserSelected(long userId) throws PortalException {
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			fetchCommerceChannelAccountEntryRel();

		if (commerceChannelAccountEntryRel == null) {
			return false;
		}

		if (commerceChannelAccountEntryRel.getClassPK() == userId) {
			return true;
		}

		return false;
	}

	private final AccountEntry _accountEntry;
	private final ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;
	private final AccountEntryService _accountEntryService;
	private final CommerceAccountRelRequestHelper
		_commerceAccountRelRequestHelper;
	private final CommerceChannel _commerceChannel;
	private final CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;
	private final CommerceChannelService _commerceChannelService;
	private final CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;
	private final CommerceShippingMethodService _commerceShippingMethodService;
	private final CommerceShippingOptionAccountEntryRel
		_commerceShippingOptionAccountEntryRel;
	private final CommerceShippingOptionAccountEntryRelService
		_commerceShippingOptionAccountEntryRelService;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;
	private final int _type;
	private final UserService _userService;

}