/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.service.base.PortletPreferencesServiceBaseImpl;

import jakarta.portlet.ReadOnlyException;
import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.Map;

/**
 * @author Jorge Ferrer
 * @author Raymond Augé
 */
public class PortletPreferencesServiceImpl
	extends PortletPreferencesServiceBaseImpl {

	@Override
	public void deleteArchivedPreferences(long portletItemId)
		throws PortalException {

		PortletItem portletItem = _portletItemLocalService.getPortletItem(
			portletItemId);

		GroupPermissionUtil.check(
			getPermissionChecker(), portletItem.getGroupId(),
			ActionKeys.MANAGE_ARCHIVED_SETUPS);

		long ownerId = portletItemId;
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_ARCHIVED;
		long plid = 0;

		portletPreferencesLocalService.deletePortletPreferences(
			ownerId, ownerType, plid, portletItem.getPortletId());

		_portletItemLocalService.deletePortletItem(portletItemId);
	}

	@Override
	public void restoreArchivedPreferences(
			long groupId, Layout layout, String portletId, long portletItemId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException {

		restoreArchivedPreferences(
			groupId, layout, portletId,
			_portletItemLocalService.getPortletItem(portletItemId),
			jxPortletPreferences);
	}

	@Override
	public void restoreArchivedPreferences(
			long groupId, Layout layout, String portletId,
			PortletItem portletItem,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException {

		PortletPermissionUtil.check(
			getPermissionChecker(), groupId, layout, portletId,
			ActionKeys.CONFIGURATION);

		long ownerId = portletItem.getPortletItemId();
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_ARCHIVED;
		long plid = 0;

		jakarta.portlet.PortletPreferences archivedJxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				portletItem.getCompanyId(), ownerId, ownerType, plid,
				PortletIdCodec.decodePortletName(portletId));

		copyPreferences(archivedJxPortletPreferences, jxPortletPreferences);
	}

	@Override
	public void restoreArchivedPreferences(
			long groupId, String name, Layout layout, String portletId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException {

		PortletItem portletItem = _portletItemLocalService.getPortletItem(
			groupId, name, portletId, PortletPreferences.class.getName());

		restoreArchivedPreferences(
			groupId, layout, portletId, portletItem, jxPortletPreferences);
	}

	@Override
	public void updateArchivePreferences(
			long userId, long groupId, String name, String portletId,
			jakarta.portlet.PortletPreferences jxPortletPreferences)
		throws PortalException {

		PortletPermissionUtil.check(
			getPermissionChecker(), groupId, 0, portletId,
			ActionKeys.CONFIGURATION);

		PortletItem portletItem = _portletItemLocalService.updatePortletItem(
			userId, groupId, name, portletId,
			PortletPreferences.class.getName());

		long ownerId = portletItem.getPortletItemId();

		int ownerType = PortletKeys.PREFS_OWNER_TYPE_ARCHIVED;
		long plid = 0;

		jakarta.portlet.PortletPreferences archivedJxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				portletItem.getCompanyId(), ownerId, ownerType, plid,
				portletId);

		copyPreferences(jxPortletPreferences, archivedJxPortletPreferences);
	}

	protected void copyPreferences(
		jakarta.portlet.PortletPreferences sourceJxPortletPreferences,
		jakarta.portlet.PortletPreferences targetJxPortletPreferences) {

		try {
			Map<String, String[]> targetJxPortletPreferencesMap =
				targetJxPortletPreferences.getMap();

			for (String key : targetJxPortletPreferencesMap.keySet()) {
				try {
					targetJxPortletPreferences.reset(key);
				}
				catch (ReadOnlyException readOnlyException) {
					if (_log.isDebugEnabled()) {
						_log.debug(readOnlyException);
					}
				}
			}

			Map<String, String[]> sourceJxPortletPreferencesMap =
				sourceJxPortletPreferences.getMap();

			for (String key : sourceJxPortletPreferencesMap.keySet()) {
				try {
					targetJxPortletPreferences.setValues(
						key,
						sourceJxPortletPreferences.getValues(
							key, new String[0]));
				}
				catch (ReadOnlyException readOnlyException) {
					if (_log.isDebugEnabled()) {
						_log.debug(readOnlyException);
					}
				}
			}

			targetJxPortletPreferences.store();
		}
		catch (IOException ioException) {
			_log.error("Unable to copy jxPortletPreferences", ioException);
		}
		catch (ValidatorException validatorException) {
			throw new SystemException(validatorException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesServiceImpl.class);

	@BeanReference(type = PortletItemLocalService.class)
	private PortletItemLocalService _portletItemLocalService;

}