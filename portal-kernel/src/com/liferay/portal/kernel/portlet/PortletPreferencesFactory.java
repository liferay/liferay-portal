/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PreferencesValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface PortletPreferencesFactory {

	public void checkControlPanelPortletPreferences(
			ThemeDisplay themeDisplay, Portlet portlet)
		throws PortalException;

	public PortletPreferences fromDefaultXML(String xml);

	public PortalPreferences fromXML(long ownerId, int ownerType, String xml);

	public PortletPreferences fromXML(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String xml);

	public PortletPreferences getExistingPortletSetup(
			Layout layout, String portletId)
		throws PortalException;

	public PortletPreferences getExistingPortletSetup(
			PortletRequest portletRequest)
		throws PortalException;

	public PortletPreferences getLayoutPortletSetup(
		Layout layout, String portletId);

	public PortletPreferences getLayoutPortletSetup(
		Layout layout, String portletId, String defaultPreferences);

	public PortletPreferences getLayoutPortletSetup(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String defaultPreferences);

	public PortalPreferences getPortalPreferences(
		HttpServletRequest httpServletRequest);

	public PortalPreferences getPortalPreferences(
		HttpSession httpSession, long userId, boolean signedIn);

	public PortalPreferences getPortalPreferences(
		long userId, boolean signedIn);

	public PortalPreferences getPortalPreferences(
		PortletRequest portletRequest);

	public PortletPreferences getPortletPreferences(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException;

	public PortletPreferencesIds getPortletPreferencesIds(
			HttpServletRequest httpServletRequest, Layout selLayout,
			String portletId)
		throws PortalException;

	public PortletPreferencesIds getPortletPreferencesIds(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException;

	public PortletPreferencesIds getPortletPreferencesIds(
			long scopeGroupId, long userId, Layout layout, String portletId,
			boolean modeEditGuest)
		throws PortalException;

	public PortletPreferencesIds getPortletPreferencesIds(
			long companyId, long siteGroupId, long layoutGroupId, long plid,
			String portletId)
		throws IllegalArgumentException;

	public PortletPreferencesIds getPortletPreferencesIds(
		long companyId, long siteGroupId, long plid, String portletId,
		String settingsScope);

	public PortletPreferences getPortletSetup(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException;

	public PortletPreferences getPortletSetup(
			HttpServletRequest httpServletRequest, String portletId,
			String defaultPreferences)
		throws PortalException;

	public PortletPreferences getPortletSetup(
		Layout layout, String portletId, String defaultPreferences);

	public PortletPreferences getPortletSetup(
		long scopeGroupId, Layout layout, String portletId,
		String defaultPreferences);

	public PortletPreferences getPortletSetup(PortletRequest portletRequest)
		throws PortalException;

	public PortletPreferences getPortletSetup(
			PortletRequest portletRequest, String portletId)
		throws PortalException;

	public Map<Long, PortletPreferences> getPortletSetupMap(
		long companyId, long groupId, long ownerId, int ownerType,
		String portletId, boolean privateLayout);

	public PortletPreferences getPreferences(
		HttpServletRequest httpServletRequest);

	public PreferencesValidator getPreferencesValidator(Portlet portlet);

	public PortletPreferences getStrictLayoutPortletSetup(
		Layout layout, String portletId);

	public PortletPreferences getStrictPortletSetup(
		Layout layout, String portletId);

	public PortletPreferences getStrictPortletSetup(
		long companyId, long groupId, String portletId);

	public PortletPreferences strictFromXML(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String xml);

	public String toXML(PortalPreferences portalPreferences);

	public String toXML(PortletPreferences portletPreferences);

}