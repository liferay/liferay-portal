/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.base.CommerceChannelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceChannel"
	},
	service = AopService.class
)
public class CommerceChannelServiceImpl extends CommerceChannelServiceBaseImpl {

	@Override
	public CommerceChannel addCommerceChannel(
			String externalReferenceCode, long accountEntryId, long siteGroupId,
			String name, String type,
			UnicodeProperties typeSettingsUnicodeProperties,
			String commerceCurrencyCode, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceChannelModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null, CPActionKeys.ADD_COMMERCE_CHANNEL);

		return commerceChannelLocalService.addCommerceChannel(
			externalReferenceCode, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			serviceContext);
	}

	@Override
	public CommerceChannel addOrUpdateCommerceChannel(
			String externalReferenceCode, long accountEntryId, long siteGroupId,
			String name, String type,
			UnicodeProperties typeSettingsUnicodeProperties,
			String commerceCurrencyCode, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceChannelModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null, CPActionKeys.ADD_COMMERCE_CHANNEL);

		return commerceChannelLocalService.addOrUpdateCommerceChannel(
			getUserId(), externalReferenceCode, accountEntryId, siteGroupId,
			name, type, typeSettingsUnicodeProperties, commerceCurrencyCode,
			serviceContext);
	}

	@Override
	public CommerceChannel deleteCommerceChannel(long commerceChannelId)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.DELETE);

		return commerceChannelLocalService.deleteCommerceChannel(
			commerceChannelId);
	}

	@Override
	public CommerceChannel fetchCommerceChannel(long commerceChannelId)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelPersistence.fetchByPrimaryKey(commerceChannelId);

		if (commerceChannel != null) {
			_commerceChannelModelResourcePermission.check(
				getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);
		}

		return commerceChannel;
	}

	@Override
	public CommerceChannel fetchCommerceChannelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelLocalService.
				fetchCommerceChannelByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceChannel != null) {
			_commerceChannelModelResourcePermission.check(
				getPermissionChecker(), commerceChannel.getCommerceChannelId(),
				ActionKeys.VIEW);
		}

		return commerceChannel;
	}

	@Override
	public CommerceChannel getCommerceChannel(long commerceChannelId)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelPersistence.findByPrimaryKey(commerceChannelId);
	}

	@Override
	public CommerceChannel getCommerceChannelByOrderGroupId(long groupId)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				groupId);

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannel, ActionKeys.VIEW);

		return commerceChannel;
	}

	@Override
	public List<CommerceChannel> getCommerceChannels(long companyId)
		throws PortalException {

		return commerceChannelPersistence.filterFindByCompanyId(companyId);
	}

	@Override
	public List<CommerceChannel> getEligibleCommerceChannels(
			long accountEntryId, String name, int start, int end)
		throws PortalException {

		List<CommerceChannel> commerceChannels = new ArrayList<>();

		for (CommerceChannel commerceChannel :
				commerceChannelLocalService.getEligibleCommerceChannels(
					accountEntryId, name, start, end)) {

			if (_commerceChannelModelResourcePermission.contains(
					getPermissionChecker(), commerceChannel, ActionKeys.VIEW)) {

				commerceChannels.add(commerceChannel);
			}
		}

		return commerceChannels;
	}

	@Override
	public List<CommerceChannel> search(long companyId) throws PortalException {
		return commerceChannelLocalService.search(companyId);
	}

	@Override
	public List<CommerceChannel> search(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		return commerceChannelLocalService.search(
			companyId, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceChannelsCount(long companyId, String keywords)
		throws PortalException {

		return commerceChannelLocalService.searchCommerceChannelsCount(
			companyId, keywords);
	}

	@Override
	public CommerceChannel updateCommerceChannel(
			long commerceChannelId, long accountEntryId, long siteGroupId,
			String name, String type,
			UnicodeProperties typeSettingsUnicodeProperties,
			String commerceCurrencyCode, String priceDisplayType,
			boolean discountsTargetNetPrice)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		_commerceChannelModelResourcePermission.check(
			permissionChecker, commerceChannelId, ActionKeys.UPDATE);

		PortletResourcePermission portletResourcePermission =
			_commerceChannelModelResourcePermission.
				getPortletResourcePermission();

		if (!portletResourcePermission.contains(
				permissionChecker, null, CPActionKeys.VIEW_COMMERCE_CHANNELS)) {

			CommerceChannel commerceChannel =
				commerceChannelPersistence.findByPrimaryKey(commerceChannelId);

			accountEntryId = commerceChannel.getAccountEntryId();
		}

		return commerceChannelLocalService.updateCommerceChannel(
			commerceChannelId, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			priceDisplayType, discountsTargetNetPrice);
	}

	@Override
	public CommerceChannel updateCommerceChannelExternalReferenceCode(
			String externalReferenceCode, long commerceChannelId)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.UPDATE);

		return commerceChannelLocalService.
			updateCommerceChannelExternalReferenceCode(
				externalReferenceCode, commerceChannelId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

}