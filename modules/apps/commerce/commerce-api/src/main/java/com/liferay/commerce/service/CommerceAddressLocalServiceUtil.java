/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceAddress;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the local service utility for CommerceAddress. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceAddressLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAddressLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceAddressLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceAddressLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceAddress addCommerceAddress(
			String externalReferenceCode, String className, long classPK,
			long countryId, long regionId, String city, String description,
			String name, String phoneNumber, String street1, String street2,
			String street3, String subtype, int type, String zip,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceAddress(
			externalReferenceCode, className, classPK, countryId, regionId,
			city, description, name, phoneNumber, street1, street2, street3,
			subtype, type, zip, serviceContext);
	}

	public static CommerceAddress copyCommerceAddress(
			long sourceCommerceAddressId, String className, long classPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyCommerceAddress(
			sourceCommerceAddressId, className, classPK, serviceContext);
	}

	public static CommerceAddress createCommerceAddress(
		long commerceAddressId) {

		return getService().createCommerceAddress(commerceAddressId);
	}

	public static CommerceAddress deleteCommerceAddress(
			CommerceAddress commerceAddress)
		throws PortalException {

		return getService().deleteCommerceAddress(commerceAddress);
	}

	public static void deleteCommerceAddresses(String className, long classPK)
		throws PortalException {

		getService().deleteCommerceAddresses(className, classPK);
	}

	public static void deleteCountryCommerceAddresses(long countryId)
		throws PortalException {

		getService().deleteCountryCommerceAddresses(countryId);
	}

	public static void deleteRegionCommerceAddresses(long regionId)
		throws PortalException {

		getService().deleteRegionCommerceAddresses(regionId);
	}

	public static CommerceAddress fetchCommerceAddress(long commerceAddressId) {
		return getService().fetchCommerceAddress(commerceAddressId);
	}

	public static CommerceAddress fetchCommerceAddressByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCommerceAddressByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceAddress geolocateCommerceAddress(
			long commerceAddressId)
		throws PortalException {

		return getService().geolocateCommerceAddress(commerceAddressId);
	}

	public static List<CommerceAddress> getBillingAndShippingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException {

		return getService().getBillingAndShippingCommerceAddresses(
			companyId, className, classPK);
	}

	public static List<CommerceAddress> getBillingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException {

		return getService().getBillingCommerceAddresses(
			companyId, className, classPK);
	}

	public static List<CommerceAddress> getBillingCommerceAddresses(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException {

		return getService().getBillingCommerceAddresses(
			channelId, className, classPK, start, end);
	}

	public static List<CommerceAddress> getBillingCommerceAddresses(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().getBillingCommerceAddresses(
			companyId, className, classPK, commerceChannelId, keywords, start,
			end, sort);
	}

	public static int getBillingCommerceAddressesCount(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException {

		return getService().getBillingCommerceAddressesCount(
			channelId, className, classPK, start, end);
	}

	public static int getBillingCommerceAddressesCount(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords)
		throws PortalException {

		return getService().getBillingCommerceAddressesCount(
			companyId, className, classPK, commerceChannelId, keywords);
	}

	public static CommerceAddress getCommerceAddress(long commerceAddressId)
		throws PortalException {

		return getService().getCommerceAddress(commerceAddressId);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	public static List<CommerceAddress> getCommerceAddresses(
		long groupId, String className, long classPK) {

		return getService().getCommerceAddresses(groupId, className, classPK);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	public static List<CommerceAddress> getCommerceAddresses(
		long groupId, String className, long classPK, int start, int end,
		OrderByComparator<CommerceAddress> orderByComparator) {

		return getService().getCommerceAddresses(
			groupId, className, classPK, start, end, orderByComparator);
	}

	public static List<CommerceAddress> getCommerceAddresses(
		String className, long classPK, int start, int end,
		OrderByComparator<CommerceAddress> orderByComparator) {

		return getService().getCommerceAddresses(
			className, classPK, start, end, orderByComparator);
	}

	public static List<CommerceAddress> getCommerceAddressesByCompanyId(
		long companyId, String className, long classPK) {

		return getService().getCommerceAddressesByCompanyId(
			companyId, className, classPK);
	}

	public static List<CommerceAddress> getCommerceAddressesByCompanyId(
		long companyId, String className, long classPK, int start, int end,
		OrderByComparator<CommerceAddress> orderByComparator) {

		return getService().getCommerceAddressesByCompanyId(
			companyId, className, classPK, start, end, orderByComparator);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	public static int getCommerceAddressesCount(
		long groupId, String className, long classPK) {

		return getService().getCommerceAddressesCount(
			groupId, className, classPK);
	}

	public static int getCommerceAddressesCount(
		String className, long classPK) {

		return getService().getCommerceAddressesCount(className, classPK);
	}

	public static int getCommerceAddressesCountByCompanyId(
		long companyId, String className, long classPK) {

		return getService().getCommerceAddressesCountByCompanyId(
			companyId, className, classPK);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<CommerceAddress> getShippingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException {

		return getService().getShippingCommerceAddresses(
			companyId, className, classPK);
	}

	public static List<CommerceAddress> getShippingCommerceAddresses(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException {

		return getService().getShippingCommerceAddresses(
			channelId, className, classPK, start, end);
	}

	public static List<CommerceAddress> getShippingCommerceAddresses(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().getShippingCommerceAddresses(
			companyId, className, classPK, commerceChannelId, keywords, start,
			end, sort);
	}

	public static int getShippingCommerceAddressesCount(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords)
		throws PortalException {

		return getService().getShippingCommerceAddressesCount(
			companyId, className, classPK, commerceChannelId, keywords);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company. Don't need to pass groupId
	 */
	@Deprecated
	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceAddress> searchCommerceAddresses(
				long companyId, long groupId, String className, long classPK,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceAddresses(
			companyId, groupId, className, classPK, keywords, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceAddress> searchCommerceAddresses(
				long companyId, String className, long classPK, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceAddresses(
			companyId, className, classPK, keywords, start, end, sort);
	}

	public static CommerceAddress updateCommerceAddress(
			String externalReferenceCode, long commerceAddressId,
			long countryId, long regionId, String city, String description,
			String name, String phoneNumber, String street1, String street2,
			String street3, String subtype, int type, String zip,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceAddress(
			externalReferenceCode, commerceAddressId, countryId, regionId, city,
			description, name, phoneNumber, street1, street2, street3, subtype,
			type, zip, serviceContext);
	}

	public static CommerceAddressLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceAddressLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceAddressLocalServiceUtil.class,
			CommerceAddressLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-947118601