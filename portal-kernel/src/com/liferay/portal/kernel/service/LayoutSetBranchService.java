/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for LayoutSetBranch. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranchServiceUtil
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@AccessControlled
@Deprecated
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface LayoutSetBranchService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutSetBranchServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout set branch remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutSetBranchServiceUtil} if injection and service tracking are not available.
	 */
	public LayoutSetBranch addLayoutSetBranch(
			long groupId, boolean privateLayout, String name,
			String description, boolean master, long copyLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException;

	public void deleteLayoutSetBranch(long layoutSetBranchId)
		throws PortalException;

	public void deleteLayoutSetBranch(
			long currentLayoutPlid, long layoutSetBranchId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutSetBranch> getLayoutSetBranches(
		long groupId, boolean privateLayout);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public LayoutSetBranch mergeLayoutSetBranch(
			long layoutSetBranchId, long mergeLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException;

	public LayoutSetBranch updateLayoutSetBranch(
			long groupId, long layoutSetBranchId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1754162115