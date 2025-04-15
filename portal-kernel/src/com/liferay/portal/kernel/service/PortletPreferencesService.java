/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Transactional;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for PortletPreferences. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see PortletPreferencesServiceUtil
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
public interface PortletPreferencesService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.PortletPreferencesServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the portlet preferences remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link PortletPreferencesServiceUtil} if injection and service tracking are not available.
	 */
	public void deleteArchivedPreferences(long portletItemId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public void restoreArchivedPreferences(
			long groupId, Layout layout, String portletId, long portletItemId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException;

	public void restoreArchivedPreferences(
			long groupId, Layout layout, String portletId,
			PortletItem portletItem,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException;

	public void restoreArchivedPreferences(
			long groupId, String name, Layout layout, String portletId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException;

	public void updateArchivePreferences(
			long userId, long groupId, String name, String portletId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException;

}