/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchPortletItemException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.settings.ArchivedSettings;
import com.liferay.portal.kernel.settings.ArchivedSettingsFactory;
import com.liferay.portal.kernel.settings.SettingsException;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Jorge Ferrer
 */
@Component(service = ArchivedSettingsFactory.class)
public class ArchivedSettingsFactoryImpl implements ArchivedSettingsFactory {

	@Override
	public ArchivedSettings getPortletInstanceArchivedSettings(
			long groupId, String portletId, String name)
		throws SettingsException {

		try {
			return new ArchivedSettingsImpl(
				_getPortletItem(groupId, portletId, name));
		}
		catch (PortalException portalException) {
			throw new SettingsException(portalException);
		}
	}

	@Override
	public List<ArchivedSettings> getPortletInstanceArchivedSettingsList(
		long groupId, String portletId) {

		return TransformUtil.transform(
			_portletItemLocalService.getPortletItems(
				groupId, portletId,
				com.liferay.portal.kernel.model.PortletPreferences.class.
					getName()),
			portletItem -> new ArchivedSettingsImpl(portletItem));
	}

	private PortletItem _getPortletItem(
			long groupId, String portletId, String name)
		throws PortalException {

		PortletItem portletItem = null;

		try {
			portletItem = _portletItemLocalService.getPortletItem(
				groupId, name, portletId, PortletPreferences.class.getName());
		}
		catch (NoSuchPortletItemException noSuchPortletItemException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPortletItemException);
			}

			portletItem = _portletItemLocalService.updatePortletItem(
				PrincipalThreadLocal.getUserId(), groupId, name, portletId,
				PortletPreferences.class.getName());
		}

		return portletItem;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ArchivedSettingsFactoryImpl.class);

	@Reference
	private PortletItemLocalService _portletItemLocalService;

}