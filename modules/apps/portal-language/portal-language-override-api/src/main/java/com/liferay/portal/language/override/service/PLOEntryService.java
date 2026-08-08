/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.language.override.model.PLOEntry;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for PLOEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Drew Brokke
 * @see PLOEntryServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface PLOEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.language.override.service.impl.PLOEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the plo entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link PLOEntryServiceUtil} if injection and service tracking are not available.
	 */
	public PLOEntry addOrUpdatePLOEntry(
			String externalReferenceCode, long userId, String key,
			String languageId, String value)
		throws PortalException;

	public void deletePLOEntries(String key) throws PortalException;

	public PLOEntry deletePLOEntry(String key, String languageId)
		throws PortalException;

	public PLOEntry deletePLOEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PLOEntry> getPLOEntries() throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PLOEntry> getPLOEntries(
			int start, int end, OrderByComparator<PLOEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<PLOEntry> getPLOEntries(
			String keywords, int start, int end,
			OrderByComparator<PLOEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPLOEntriesCount() throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPLOEntriesCount(String keywords) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PLOEntry getPLOEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException;

	public void importPLOEntries(String languageId, Properties properties)
		throws IOException, PortalException;

	public void setPLOEntries(String key, Map<Locale, String> localizationMap)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1474791432