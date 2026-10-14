/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.portal.kernel.change.tracking.CTAware;
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
 * Provides the remote service interface for FragmentComposition. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCompositionServiceUtil
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
public interface FragmentCompositionService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentCompositionServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the fragment composition remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link FragmentCompositionServiceUtil} if injection and service tracking are not available.
	 */
	public FragmentComposition addFragmentComposition(
			String externalReferenceCode, long groupId,
			long fragmentCollectionId, String fragmentCompositionKey,
			String name, String description, String data,
			long previewFileEntryId, int status, ServiceContext serviceContext)
		throws PortalException;

	public FragmentComposition deleteFragmentComposition(
			long fragmentCompositionId)
		throws PortalException;

	public FragmentComposition deleteFragmentComposition(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentComposition fetchFragmentComposition(
		long fragmentCompositionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentComposition fetchFragmentComposition(
		long groupId, String fragmentCompositionKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentComposition getFragmentCompositionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentComposition> getFragmentCompositions(
			long fragmentCollectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentComposition> getFragmentCompositions(
			long fragmentCollectionId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentComposition> getFragmentCompositions(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentComposition> getFragmentCompositions(
			long groupId, long fragmentCollectionId, int start, int end,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentComposition> getFragmentCompositions(
			long groupId, long fragmentCollectionId, String name, int start,
			int end, OrderByComparator<FragmentComposition> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCompositionsCount(long fragmentCollectionId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public FragmentComposition moveFragmentComposition(
			long fragmentCompositionId, long fragmentCollectionId)
		throws PortalException;

	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, long previewFileEntryId)
		throws PortalException;

	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, long fragmentCollectionId, String name,
			String description, String data, long previewFileEntryId,
			int status)
		throws PortalException;

	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, String name)
		throws PortalException;

	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, String name, String description,
			String data, long previewFileEntryId, int status)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1975666850