/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CPTaxCategory. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see CPTaxCategoryServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CPTaxCategoryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPTaxCategoryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cp tax category remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CPTaxCategoryServiceUtil} if injection and service tracking are not available.
	 */
	public CPTaxCategory addCPTaxCategory(
			String externalReferenceCode, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, ServiceContext serviceContext)
		throws PortalException;

	public int countCPTaxCategoriesByCompanyId(long companyId, String keyword)
		throws PortalException;

	public void deleteCPTaxCategory(long cpTaxCategoryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPTaxCategory fetchCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	public List<CPTaxCategory> findCPTaxCategoriesByCompanyId(
			long companyId, String keyword, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPTaxCategory> getCPTaxCategories(long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPTaxCategory> getCPTaxCategories(
			long companyId, int start, int end,
			OrderByComparator<CPTaxCategory> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPTaxCategoriesCount(long companyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPTaxCategory getCPTaxCategory(long cpTaxCategoryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPTaxCategory getCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPTaxCategory getOrAddEmptyCPTaxCategory(
			String externalReferenceCode)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CPTaxCategory> searchCPTaxCategories(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException;

	public CPTaxCategory updateCPTaxCategory(
			String externalReferenceCode, long cpTaxCategoryId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1284878944