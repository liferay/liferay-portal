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

/**
 * @author Brian Wing Shun Chan
 */
public class PortletPreferencesFactoryUtil {

	public static void checkControlPanelPortletPreferences(
			ThemeDisplay themeDisplay, Portlet portlet)
		throws PortalException {

		_portletPreferencesFactory.checkControlPanelPortletPreferences(
			themeDisplay, portlet);
	}

	public static PortletPreferences fromDefaultXML(String xml) {
		return _portletPreferencesFactory.fromDefaultXML(xml);
	}

	public static PortalPreferences fromXML(
		long ownerId, int ownerType, String xml) {

		return _portletPreferencesFactory.fromXML(ownerId, ownerType, xml);
	}

	public static PortletPreferences fromXML(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String xml) {

		return _portletPreferencesFactory.fromXML(
			companyId, ownerId, ownerType, plid, portletId, xml);
	}

	public static PortletPreferences getExistingPortletSetup(
			Layout layout, String portletId)
		throws PortalException {

		return _portletPreferencesFactory.getExistingPortletSetup(
			layout, portletId);
	}

	public static PortletPreferences getExistingPortletSetup(
			PortletRequest portletRequest)
		throws PortalException {

		return _portletPreferencesFactory.getExistingPortletSetup(
			portletRequest);
	}

	public static PortletPreferences getLayoutPortletSetup(
		Layout layout, String portletId) {

		return _portletPreferencesFactory.getLayoutPortletSetup(
			layout, portletId);
	}

	public static PortletPreferences getLayoutPortletSetup(
		Layout layout, String portletId, String defaultPreferences) {

		return _portletPreferencesFactory.getLayoutPortletSetup(
			layout, portletId, defaultPreferences);
	}

	public static PortletPreferences getLayoutPortletSetup(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String defaultPreferences) {

		return _portletPreferencesFactory.getLayoutPortletSetup(
			companyId, ownerId, ownerType, plid, portletId, defaultPreferences);
	}

	public static PortalPreferences getPortalPreferences(
		HttpServletRequest httpServletRequest) {

		return _portletPreferencesFactory.getPortalPreferences(
			httpServletRequest);
	}

	public static PortalPreferences getPortalPreferences(
		HttpSession httpSession, long userId, boolean signedIn) {

		return _portletPreferencesFactory.getPortalPreferences(
			httpSession, userId, signedIn);
	}

	public static PortalPreferences getPortalPreferences(
		long userId, boolean signedIn) {

		return _portletPreferencesFactory.getPortalPreferences(
			userId, signedIn);
	}

	public static PortalPreferences getPortalPreferences(
		PortletRequest portletRequest) {

		return _portletPreferencesFactory.getPortalPreferences(portletRequest);
	}

	public static PortletPreferences getPortletPreferences(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		return _portletPreferencesFactory.getPortletPreferences(
			httpServletRequest, portletId);
	}

	public static PortletPreferencesFactory getPortletPreferencesFactory() {
		return _portletPreferencesFactory;
	}

	public static PortletPreferencesIds getPortletPreferencesIds(
			HttpServletRequest httpServletRequest, Layout selLayout,
			String portletId)
		throws PortalException {

		return _portletPreferencesFactory.getPortletPreferencesIds(
			httpServletRequest, selLayout, portletId);
	}

	public static PortletPreferencesIds getPortletPreferencesIds(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		return _portletPreferencesFactory.getPortletPreferencesIds(
			httpServletRequest, portletId);
	}

	public static PortletPreferencesIds getPortletPreferencesIds(
			long siteGroupId, long userId, Layout layout, String portletId,
			boolean modeEditGuest)
		throws PortalException {

		return _portletPreferencesFactory.getPortletPreferencesIds(
			siteGroupId, userId, layout, portletId, modeEditGuest);
	}

	public static PortletPreferencesIds getPortletPreferencesIds(
		long companyId, long siteGroupId, long plid, String portletId,
		String settingsScope) {

		return _portletPreferencesFactory.getPortletPreferencesIds(
			companyId, siteGroupId, plid, portletId, settingsScope);
	}

	public static PortletPreferences getPortletSetup(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		return _portletPreferencesFactory.getPortletSetup(
			httpServletRequest, portletId);
	}

	public static PortletPreferences getPortletSetup(
			HttpServletRequest httpServletRequest, String portletId,
			String defaultPreferences)
		throws PortalException {

		return _portletPreferencesFactory.getPortletSetup(
			httpServletRequest, portletId, defaultPreferences);
	}

	public static PortletPreferences getPortletSetup(
		Layout layout, String portletId, String defaultPreferences) {

		return _portletPreferencesFactory.getPortletSetup(
			layout, portletId, defaultPreferences);
	}

	public static PortletPreferences getPortletSetup(
		long siteGroupId, Layout layout, String portletId,
		String defaultPreferences) {

		return _portletPreferencesFactory.getPortletSetup(
			siteGroupId, layout, portletId, defaultPreferences);
	}

	public static PortletPreferences getPortletSetup(
			PortletRequest portletRequest)
		throws PortalException {

		return _portletPreferencesFactory.getPortletSetup(portletRequest);
	}

	public static PortletPreferences getPortletSetup(
			PortletRequest portletRequest, String portletId)
		throws PortalException {

		return _portletPreferencesFactory.getPortletSetup(
			portletRequest, portletId);
	}

	public static Map<Long, PortletPreferences> getPortletSetupMap(
		long companyId, long groupId, long ownerId, int ownerType,
		String portletId, boolean privateLayout) {

		return _portletPreferencesFactory.getPortletSetupMap(
			companyId, groupId, ownerId, ownerType, portletId, privateLayout);
	}

	public static PortletPreferences getPreferences(
		HttpServletRequest httpServletRequest) {

		return _portletPreferencesFactory.getPreferences(httpServletRequest);
	}

	public static PreferencesValidator getPreferencesValidator(
		Portlet portlet) {

		return _portletPreferencesFactory.getPreferencesValidator(portlet);
	}

	public static PortletPreferences getStrictLayoutPortletSetup(
		Layout layout, String portletId) {

		return _portletPreferencesFactory.getStrictLayoutPortletSetup(
			layout, portletId);
	}

	public static PortletPreferences getStrictPortletSetup(
		Layout layout, String portletId) {

		return _portletPreferencesFactory.getStrictPortletSetup(
			layout, portletId);
	}

	public static PortletPreferences getStrictPortletSetup(
		long companyId, long groupId, String portletId) {

		return _portletPreferencesFactory.getStrictPortletSetup(
			companyId, groupId, portletId);
	}

	public static PortletPreferences strictFromXML(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String xml) {

		return _portletPreferencesFactory.strictFromXML(
			companyId, ownerId, ownerType, plid, portletId, xml);
	}

	public static String toXML(PortalPreferences portalPreferences) {
		return _portletPreferencesFactory.toXML(portalPreferences);
	}

	public static String toXML(PortletPreferences portletPreferences) {
		return _portletPreferencesFactory.toXML(portletPreferences);
	}

	public void setPortletPreferencesFactory(
		PortletPreferencesFactory portletPreferencesFactory) {

		_portletPreferencesFactory = portletPreferencesFactory;
	}

	private static PortletPreferencesFactory _portletPreferencesFactory;

}