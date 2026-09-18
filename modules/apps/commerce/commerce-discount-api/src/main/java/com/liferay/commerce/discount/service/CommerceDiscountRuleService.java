/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CommerceDiscountRule. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see CommerceDiscountRuleServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceDiscountRuleService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountRuleServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce discount rule remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceDiscountRuleServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceDiscountRule addCommerceDiscountRule(
			long commerceDiscountId, String type, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException;

	public CommerceDiscountRule addCommerceDiscountRule(
			long commerceDiscountId, String name, String type,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException;

	public CommerceDiscountRule addOrUpdateCommerceDiscountRule(
			String externalReferenceCode, long commerceDiscountRuleId,
			long commerceDiscountId, String name, String type,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException;

	public void deleteCommerceDiscountRule(long commerceDiscountRuleId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscountRule fetchCommerceDiscountRule(
			long commerceDiscountRuleId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscountRule getCommerceDiscountRule(
			long commerceDiscountRuleId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRule> getCommerceDiscountRules(
			long commerceDiscountId, int start, int end,
			OrderByComparator<CommerceDiscountRule> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRule> getCommerceDiscountRules(
			long commerceDiscountId, String name, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountRulesCount(long commerceDiscountId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountRulesCount(
			long commerceDiscountId, String name)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public CommerceDiscountRule updateCommerceDiscountRule(
			long commerceDiscountRuleId, String type, String typeSettings)
		throws PortalException;

	public CommerceDiscountRule updateCommerceDiscountRule(
			long commerceDiscountRuleId, String name, String type,
			String typeSettings)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1021485481