/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
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
 * Provides the remote service interface for CommerceDiscountOrderTypeRel. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see CommerceDiscountOrderTypeRelServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceDiscountOrderTypeRelService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountOrderTypeRelServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce discount order type rel remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceDiscountOrderTypeRelServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceDiscountOrderTypeRel addCommerceDiscountOrderTypeRel(
			long commerceDiscountId, long commerceOrderTypeId, int priority,
			ServiceContext serviceContext)
		throws PortalException;

	public void deleteCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscountOrderTypeRel fetchCommerceDiscountOrderTypeRel(
			long commerceDiscountId, long commerceOrderTypeId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscountOrderTypeRel getCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountOrderTypeRel> getCommerceDiscountOrderTypeRels(
			long commerceDiscountId, String name, int start, int end,
			OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountOrderTypeRelsCount(
			long commerceDiscountId, String name)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public CommerceDiscountOrderTypeRel updatePriority(
			long commerceDiscountOrderTypeRelId, int priority)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1918438667