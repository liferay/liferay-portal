/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Gabor Komaromi
 */
public class PortletLayoutListenerHelper {

	public PortletLayoutListenerHelper(
		LayoutLocalService layoutLocalService,
		PortletLocalService portletLocalService,
		PortletPreferencesLocalService portletPreferencesLocalService) {

		_layoutLocalService = layoutLocalService;
		_portletLocalService = portletLocalService;
		_portletPreferencesLocalService = portletPreferencesLocalService;
	}

	public void setUpPortlet(Layout layout, String portletId, long userId) {
		Portlet portlet = _portletLocalService.getPortletById(
			layout.getCompanyId(), portletId);

		if ((portlet == null) || portlet.isUndeployedPortlet()) {
			return;
		}

		PortletLayoutListener portletLayoutListener =
			portlet.getPortletLayoutListenerInstance();

		if (portletLayoutListener == null) {
			return;
		}

		_setUpPortlet(layout, portletId, portletLayoutListener, userId);
	}

	public void setUpPortlets(
		long companyId, long groupId, boolean privateLayout, long userId) {

		for (Portlet portlet : _portletLocalService.getPortlets(companyId)) {
			if (portlet.isUndeployedPortlet()) {
				continue;
			}

			PortletLayoutListener portletLayoutListener =
				portlet.getPortletLayoutListenerInstance();

			if (portletLayoutListener == null) {
				continue;
			}

			for (PortletPreferences portletPreferences :
					_portletPreferencesLocalService.getPortletPreferences(
						companyId, groupId, PortletKeys.PREFS_OWNER_ID_DEFAULT,
						PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
						portlet.getPortletId(), privateLayout)) {

				Layout layout = _layoutLocalService.fetchLayout(
					portletPreferences.getPlid());

				if (layout == null) {
					continue;
				}

				_setUpPortlet(
					layout, portletPreferences.getPortletId(),
					portletLayoutListener, userId);
			}
		}
	}

	private ServiceContext _getServiceContext(Layout layout, long userId) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}
		else {
			serviceContext = (ServiceContext)serviceContext.clone();
		}

		serviceContext.setCompanyId(layout.getCompanyId());
		serviceContext.setScopeGroupId(layout.getGroupId());
		serviceContext.setUserId(userId);

		return serviceContext;
	}

	private void _setUpPortlet(
		Layout layout, String portletId,
		PortletLayoutListener portletLayoutListener, long userId) {

		ServiceContextThreadLocal.pushServiceContext(
			_getServiceContext(layout, userId));

		try {
			portletLayoutListener.onSetup(portletId, layout.getPlid());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to set up portlet ", portletId, " on layout ",
						layout.getPlid()),
					exception);
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletLayoutListenerHelper.class);

	private final LayoutLocalService _layoutLocalService;
	private final PortletLocalService _portletLocalService;
	private final PortletPreferencesLocalService
		_portletPreferencesLocalService;

}