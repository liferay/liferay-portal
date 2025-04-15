/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.sites.kernel.util.SitesUtil;

import jakarta.portlet.PortletPreferences;

import java.util.List;

/**
 * @author Adolfo Pérez
 */
public abstract class BasePortletLayoutFinder implements PortletLayoutFinder {

	@Override
	public Result find(ThemeDisplay themeDisplay, long groupId)
		throws PortalException {

		String[] portletIds = getPortletIds();

		if ((themeDisplay.getPlid() != LayoutConstants.DEFAULT_PLID) &&
			(groupId == themeDisplay.getScopeGroupId())) {

			try {
				Layout layout = LayoutLocalServiceUtil.getLayout(
					themeDisplay.getPlid());

				LayoutTypePortlet layoutTypePortlet =
					(LayoutTypePortlet)layout.getLayoutType();

				for (String portletId : portletIds) {
					if (!layoutTypePortlet.hasPortletId(portletId, false) ||
						!LayoutPermissionUtil.contains(
							themeDisplay.getPermissionChecker(), layout,
							ActionKeys.VIEW)) {

						continue;
					}

					portletId = getPortletId(layoutTypePortlet, portletId);

					return new ResultImpl(
						themeDisplay.getPlid(), portletId, false);
				}
			}
			catch (NoSuchLayoutException noSuchLayoutException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchLayoutException);
				}
			}
		}

		Object[] plidAndPortletId = _fetchPlidAndPortletId(
			themeDisplay.getPermissionChecker(), groupId, portletIds);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (((plidAndPortletId == null) || (boolean)plidAndPortletId[2]) &&
			(scopeGroup.isSite() ||
			 SitesUtil.isUserGroupLayoutSetViewable(
				 themeDisplay.getPermissionChecker(), scopeGroup))) {

			Object[] scopePlidAndPortletId = _fetchPlidAndPortletId(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), portletIds);

			if (scopePlidAndPortletId != null) {
				plidAndPortletId = scopePlidAndPortletId;
			}
		}

		if (plidAndPortletId == null) {
			throw new NoSuchLayoutException(
				_getErrorMessage(groupId, themeDisplay, portletIds));
		}

		return new ResultImpl(
			(long)plidAndPortletId[0], (String)plidAndPortletId[1],
			(boolean)plidAndPortletId[2]);
	}

	protected String getPortletId(
		LayoutTypePortlet layoutTypePortlet, String portletId) {

		for (Portlet curPortlet :
				layoutTypePortlet.getAllNonembeddedPortlets()) {

			String curRootPortletId = PortletIdCodec.decodePortletName(
				curPortlet.getPortletId());

			if (portletId.equals(curRootPortletId)) {
				return curPortlet.getPortletId();
			}
		}

		Layout layout = layoutTypePortlet.getLayout();

		List<com.liferay.portal.kernel.model.PortletPreferences>
			layoutPortletPreferences =
				PortletPreferencesLocalServiceUtil.getPortletPreferences(
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
					portletId);

		if (!layoutPortletPreferences.isEmpty()) {
			com.liferay.portal.kernel.model.PortletPreferences
				portletPreferences = layoutPortletPreferences.get(0);

			return portletPreferences.getPortletId();
		}

		return null;
	}

	protected abstract String[] getPortletIds();

	protected class ResultImpl implements PortletLayoutFinder.Result {

		public ResultImpl(long plid, String portletId, boolean signInRequired) {
			_plid = plid;
			_portletId = portletId;
			_signInRequired = signInRequired;
		}

		@Override
		public long getPlid() {
			return _plid;
		}

		@Override
		public String getPortletId() {
			return _portletId;
		}

		@Override
		public boolean isSignInRequired() {
			return _signInRequired;
		}

		private final long _plid;
		private final String _portletId;
		private final boolean _signInRequired;

	}

	private Object[] _fetchPlidAndPortletId(
			PermissionChecker permissionChecker, long groupId,
			String[] portletIds)
		throws PortalException {

		Object[] fallbackPlidAndPortletId = null;

		for (String curPortletId : portletIds) {
			long plid = PortalUtil.getPlidFromPortletId(groupId, curPortletId);

			if (plid == LayoutConstants.DEFAULT_PLID) {
				continue;
			}

			Layout layout = LayoutLocalServiceUtil.getLayout(plid);

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			String portletId = getPortletId(layoutTypePortlet, curPortletId);

			if (!LayoutPermissionUtil.contains(
					permissionChecker, LayoutLocalServiceUtil.getLayout(plid),
					ActionKeys.VIEW) &&
				!permissionChecker.isSignedIn()) {

				fallbackPlidAndPortletId = new Object[] {plid, portletId, true};

				continue;
			}

			return new Object[] {plid, portletId, false};
		}

		return fallbackPlidAndPortletId;
	}

	private String _getErrorMessage(
		long groupId, ThemeDisplay themeDisplay, String[] portletIds) {

		StringBundler sb = new StringBundler((portletIds.length * 2) + 5);

		sb.append("{groupId=");
		sb.append(groupId);
		sb.append(", plid=");
		sb.append(themeDisplay.getPlid());

		for (String portletId : portletIds) {
			sb.append(", portletId=");
			sb.append(portletId);
		}

		sb.append("}");

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasePortletLayoutFinder.class);

}