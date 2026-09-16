/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for CommerceChannel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CommerceChannelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CommerceChannelService
 * @generated
 */
public class CommerceChannelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CommerceChannelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceChannel addCommerceChannel(
			String externalReferenceCode, long accountEntryId, long siteGroupId,
			String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceChannel(
			externalReferenceCode, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			serviceContext);
	}

	public static CommerceChannel addOrUpdateCommerceChannel(
			String externalReferenceCode, long accountEntryId, long siteGroupId,
			String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceChannel(
			externalReferenceCode, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			serviceContext);
	}

	public static CommerceChannel deleteCommerceChannel(long commerceChannelId)
		throws PortalException {

		return getService().deleteCommerceChannel(commerceChannelId);
	}

	public static CommerceChannel fetchCommerceChannel(long commerceChannelId)
		throws PortalException {

		return getService().fetchCommerceChannel(commerceChannelId);
	}

	public static CommerceChannel fetchCommerceChannelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommerceChannelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceChannel getCommerceChannel(long commerceChannelId)
		throws PortalException {

		return getService().getCommerceChannel(commerceChannelId);
	}

	public static CommerceChannel getCommerceChannelByOrderGroupId(long groupId)
		throws PortalException {

		return getService().getCommerceChannelByOrderGroupId(groupId);
	}

	public static List<CommerceChannel> getCommerceChannels(long companyId)
		throws PortalException {

		return getService().getCommerceChannels(companyId);
	}

	public static List<CommerceChannel> getEligibleCommerceChannels(
			long accountEntryId, String name, int start, int end)
		throws PortalException {

		return getService().getEligibleCommerceChannels(
			accountEntryId, name, start, end);
	}

	public static CommerceChannel getOrAddEmptyCommerceChannel(
			String externalReferenceCode)
		throws PortalException {

		return getService().getOrAddEmptyCommerceChannel(externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<CommerceChannel> search(long companyId)
		throws PortalException {

		return getService().search(companyId);
	}

	public static List<CommerceChannel> search(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().search(companyId, keywords, start, end, sort);
	}

	public static int searchCommerceChannelsCount(
			long companyId, String keywords)
		throws PortalException {

		return getService().searchCommerceChannelsCount(companyId, keywords);
	}

	public static CommerceChannel updateCommerceChannel(
			long commerceChannelId, long accountEntryId, long siteGroupId,
			String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode, String priceDisplayType,
			boolean discountsTargetNetPrice)
		throws PortalException {

		return getService().updateCommerceChannel(
			commerceChannelId, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			priceDisplayType, discountsTargetNetPrice);
	}

	public static CommerceChannel updateCommerceChannelExternalReferenceCode(
			String externalReferenceCode, long commerceChannelId)
		throws PortalException {

		return getService().updateCommerceChannelExternalReferenceCode(
			externalReferenceCode, commerceChannelId);
	}

	public static CommerceChannelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceChannelService> _serviceSnapshot =
		new Snapshot<>(
			CommerceChannelServiceUtil.class, CommerceChannelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:2060162770