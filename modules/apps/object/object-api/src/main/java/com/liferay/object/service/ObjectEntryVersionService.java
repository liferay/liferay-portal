/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for ObjectEntryVersion. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectEntryVersionServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectEntryVersionService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryVersionServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object entry version remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectEntryVersionServiceUtil} if injection and service tracking are not available.
	 */
	public ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException;

	public ObjectEntryVersion expireObjectEntryVersion(
			ObjectEntry objectEntry, ServiceContext serviceContext, int version)
		throws PortalException;

	public void expireObjectEntryVersions(
			ObjectEntry objectEntry, ServiceContext serviceContext)
		throws Exception;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryVersion> getObjectEntryVersions(
			long objectEntryId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntryVersionsCount(long objectEntryId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

}