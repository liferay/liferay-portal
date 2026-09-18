/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceDiscountRuleService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountRuleService
 * @generated
 */
public class CommerceDiscountRuleServiceWrapper
	implements CommerceDiscountRuleService,
			   ServiceWrapper<CommerceDiscountRuleService> {

	public CommerceDiscountRuleServiceWrapper() {
		this(null);
	}

	public CommerceDiscountRuleServiceWrapper(
		CommerceDiscountRuleService commerceDiscountRuleService) {

		_commerceDiscountRuleService = commerceDiscountRuleService;
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			addCommerceDiscountRule(
				long commerceDiscountId, String type, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.addCommerceDiscountRule(
			commerceDiscountId, type, typeSettings, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			addCommerceDiscountRule(
				long commerceDiscountId, String name, String type,
				String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.addCommerceDiscountRule(
			commerceDiscountId, name, type, typeSettings, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			addOrUpdateCommerceDiscountRule(
				String externalReferenceCode, long commerceDiscountRuleId,
				long commerceDiscountId, String name, String type,
				String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.addOrUpdateCommerceDiscountRule(
			externalReferenceCode, commerceDiscountRuleId, commerceDiscountId,
			name, type, typeSettings, serviceContext);
	}

	@Override
	public void deleteCommerceDiscountRule(long commerceDiscountRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountRuleService.deleteCommerceDiscountRule(
			commerceDiscountRuleId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			fetchCommerceDiscountRule(long commerceDiscountRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.fetchCommerceDiscountRule(
			commerceDiscountRuleId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			getCommerceDiscountRule(long commerceDiscountRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.getCommerceDiscountRule(
			commerceDiscountRuleId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRule>
				getCommerceDiscountRules(
					long commerceDiscountId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.discount.model.
							CommerceDiscountRule> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.getCommerceDiscountRules(
			commerceDiscountId, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRule>
				getCommerceDiscountRules(
					long commerceDiscountId, String name, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.getCommerceDiscountRules(
			commerceDiscountId, name, start, end);
	}

	@Override
	public int getCommerceDiscountRulesCount(long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.getCommerceDiscountRulesCount(
			commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountRulesCount(
			long commerceDiscountId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.getCommerceDiscountRulesCount(
			commerceDiscountId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountRuleService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			updateCommerceDiscountRule(
				long commerceDiscountRuleId, String type, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.updateCommerceDiscountRule(
			commerceDiscountRuleId, type, typeSettings);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRule
			updateCommerceDiscountRule(
				long commerceDiscountRuleId, String name, String type,
				String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRuleService.updateCommerceDiscountRule(
			commerceDiscountRuleId, name, type, typeSettings);
	}

	@Override
	public CommerceDiscountRuleService getWrappedService() {
		return _commerceDiscountRuleService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountRuleService commerceDiscountRuleService) {

		_commerceDiscountRuleService = commerceDiscountRuleService;
	}

	private CommerceDiscountRuleService _commerceDiscountRuleService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1282884497