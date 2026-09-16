/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommercePricingClassService}.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassService
 * @generated
 */
public class CommercePricingClassServiceWrapper
	implements CommercePricingClassService,
			   ServiceWrapper<CommercePricingClassService> {

	public CommercePricingClassServiceWrapper() {
		this(null);
	}

	public CommercePricingClassServiceWrapper(
		CommercePricingClassService commercePricingClassService) {

		_commercePricingClassService = commercePricingClassService;
	}

	@Override
	public CommercePricingClass addCommercePricingClass(
			String externalReferenceCode,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.addCommercePricingClass(
			externalReferenceCode, titleMap, descriptionMap, serviceContext);
	}

	@Override
	public CommercePricingClass addOrUpdateCommercePricingClass(
			String externalReferenceCode, long commercePricingClassId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.addOrUpdateCommercePricingClass(
			externalReferenceCode, commercePricingClassId, titleMap,
			descriptionMap, serviceContext);
	}

	@Override
	public CommercePricingClass deleteCommercePricingClass(
			long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.deleteCommercePricingClass(
			commercePricingClassId);
	}

	@Override
	public CommercePricingClass fetchCommercePricingClass(
			long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.fetchCommercePricingClass(
			commercePricingClassId);
	}

	@Override
	public CommercePricingClass
			fetchCommercePricingClassByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.
			fetchCommercePricingClassByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommercePricingClass getCommercePricingClass(
			long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.getCommercePricingClass(
			commercePricingClassId);
	}

	@Override
	public int getCommercePricingClassCountByCPDefinitionId(
			long cpDefinitionId, String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _commercePricingClassService.
			getCommercePricingClassCountByCPDefinitionId(cpDefinitionId, title);
	}

	@Override
	public java.util.List<CommercePricingClass> getCommercePricingClasses(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePricingClass> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.getCommercePricingClasses(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePricingClassesCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.getCommercePricingClassesCount(
			companyId);
	}

	@Override
	public int getCommercePricingClassesCount(long cpDefinitionId, String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _commercePricingClassService.getCommercePricingClassesCount(
			cpDefinitionId, title);
	}

	@Override
	public CommercePricingClass getOrAddEmptyCommercePricingClass(
			String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.getOrAddEmptyCommercePricingClass(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePricingClassService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<CommercePricingClass> searchByCPDefinitionId(
			long cpDefinitionId, String title, int start, int end)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _commercePricingClassService.searchByCPDefinitionId(
			cpDefinitionId, title, start, end);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePricingClass> searchCommercePricingClasses(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.searchCommercePricingClasses(
			companyId, keywords, start, end, sort);
	}

	@Override
	public CommercePricingClass updateCommercePricingClass(
			long commercePricingClassId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.updateCommercePricingClass(
			commercePricingClassId, titleMap, descriptionMap, serviceContext);
	}

	@Override
	public CommercePricingClass updateCommercePricingClassExternalReferenceCode(
			String externalReferenceCode, long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassService.
			updateCommercePricingClassExternalReferenceCode(
				externalReferenceCode, commercePricingClassId);
	}

	@Override
	public CommercePricingClassService getWrappedService() {
		return _commercePricingClassService;
	}

	@Override
	public void setWrappedService(
		CommercePricingClassService commercePricingClassService) {

		_commercePricingClassService = commercePricingClassService;
	}

	private CommercePricingClassService _commercePricingClassService;

}
// LIFERAY-SERVICE-BUILDER-HASH:110208071