/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
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
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for ListTypeDefinition. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeDefinitionServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ListTypeDefinitionService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.list.type.service.impl.ListTypeDefinitionServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the list type definition remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ListTypeDefinitionServiceUtil} if injection and service tracking are not available.
	 */
	public ListTypeDefinition addListTypeDefinition(
			String externalReferenceCode, Map<Locale, String> nameMap,
			boolean system, List<ListTypeEntry> listTypeEntries,
			ServiceContext serviceContext)
		throws PortalException;

	public ListTypeDefinition deleteListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws PortalException;

	public ListTypeDefinition deleteListTypeDefinition(
			long listTypeDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ListTypeDefinition fetchListTypeDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ListTypeDefinition getListTypeDefinition(long listTypeDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ListTypeDefinition getListTypeDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ListTypeDefinition> getListTypeDefinitions(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getListTypeDefinitionsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ListTypeDefinition getOrAddEmptyListTypeDefinition(
			String externalReferenceCode, boolean system)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public ListTypeDefinition updateListTypeDefinition(
			String externalReferenceCode, long listTypeDefinitionId,
			Map<Locale, String> nameMap, List<ListTypeEntry> listTypeEntries,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1153335295