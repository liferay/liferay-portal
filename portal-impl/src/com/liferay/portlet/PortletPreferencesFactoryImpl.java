/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.StAXReaderUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.portletconfiguration.util.ConfigurationPortletRequest;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PreferencesValidator;
import jakarta.portlet.filter.PortletRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Minhchau Dang
 * @author Raymond Augé
 */
public class PortletPreferencesFactoryImpl
	implements PortletPreferencesFactory {

	public static Map<String, Preference> createPreferencesMap(String xml) {
		Map<String, Preference> preferencesMap = new HashMap<>();

		XMLEventReader xmlEventReader = null;

		try {
			XMLInputFactory xmlInputFactory =
				StAXReaderUtil.getXMLInputFactory();

			xmlEventReader = xmlInputFactory.createXMLEventReader(
				new UnsyncStringReader(xml));

			while (xmlEventReader.hasNext()) {
				XMLEvent xmlEvent = xmlEventReader.nextEvent();

				if (xmlEvent.isStartElement()) {
					StartElement startElement = xmlEvent.asStartElement();

					QName startElementName = startElement.getName();

					String elementName = startElementName.getLocalPart();

					if (elementName.equals("preference")) {
						Preference preference = readPreference(xmlEventReader);

						preferencesMap.put(preference.getName(), preference);
					}
				}
			}
		}
		catch (XMLStreamException xmlStreamException) {
			throw new SystemException(xmlStreamException);
		}
		finally {
			if (xmlEventReader != null) {
				try {
					xmlEventReader.close();
				}
				catch (XMLStreamException xmlStreamException) {
					if (_log.isDebugEnabled()) {
						_log.debug(xmlStreamException);
					}
				}
			}
		}

		return preferencesMap;
	}

	@Override
	public void checkControlPanelPortletPreferences(
			ThemeDisplay themeDisplay, Portlet portlet)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		if (!group.isControlPanel()) {
			return;
		}

		String portletId = portlet.getPortletId();

		boolean hasControlPanelAccessPermission =
			PortletPermissionUtil.hasControlPanelAccessPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), portletId);

		if (!hasControlPanelAccessPermission) {
			return;
		}

		PortletPreferences portletPreferences = getStrictLayoutPortletSetup(
			layout, portletId);

		if (portletPreferences instanceof StrictPortletPreferencesImpl) {
			getLayoutPortletSetup(layout, portletId);
		}

		if (portlet.isInstanceable()) {
			return;
		}

		PortletPreferencesLocalServiceUtil.getPreferences(
			getPortletPreferencesIds(
				themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
				layout, portletId, false));
	}

	@Override
	public PortletPreferences fromDefaultXML(String xml) {
		Map<String, Preference> preferencesMap = toPreferencesMap(xml);

		return new PortletPreferencesImpl(xml, preferencesMap);
	}

	@Override
	public PortalPreferencesImpl fromXML(
		long ownerId, int ownerType, String xml) {

		Map<PortalPreferenceKey, String[]> preferences = new HashMap<>();

		Map<String, Preference> preferencesMap = toPreferencesMap(xml);

		for (Preference preference : preferencesMap.values()) {
			String namespace = null;

			String key = preference.getName();

			int index = key.indexOf(CharPool.POUND);

			if (index > 0) {
				namespace = key.substring(0, index);

				key = key.substring(index + 1);
			}

			preferences.put(
				new PortalPreferenceKey(namespace, key),
				preference.getValues());
		}

		return new PortalPreferencesImpl(
			ownerId, ownerType, preferences, false);
	}

	@Override
	public PortletPreferencesImpl fromXML(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String xml) {

		Map<String, Preference> preferencesMap = toPreferencesMap(xml);

		return new PortletPreferencesImpl(
			companyId, ownerId, ownerType, plid, portletId, xml,
			preferencesMap);
	}

	@Override
	public PortletPreferences getExistingPortletSetup(
			Layout layout, String portletId)
		throws PortalException {

		if (Validator.isNull(portletId)) {
			return null;
		}

		PortletPreferences portletPreferences = getStrictPortletSetup(
			layout, portletId);

		if (portletPreferences instanceof StrictPortletPreferencesImpl) {
			throw new PrincipalException();
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences getExistingPortletSetup(
			PortletRequest portletRequest)
		throws PortalException {

		String portletResource = ParamUtil.getString(
			portletRequest, "portletResource");

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return getExistingPortletSetup(
			themeDisplay.getLayout(), portletResource);
	}

	@Override
	public PortletPreferences getLayoutPortletSetup(
		Layout layout, String portletId) {

		return getLayoutPortletSetup(layout, portletId, null);
	}

	@Override
	public PortletPreferences getLayoutPortletSetup(
		Layout layout, String portletId, String defaultPreferences) {

		long ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;

		if (PortletIdCodec.hasUserId(portletId)) {
			ownerId = PortletIdCodec.decodeUserId(portletId);
			ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
		}

		return getLayoutPortletSetup(
			layout.getCompanyId(), ownerId, ownerType, layout.getPlid(),
			portletId, defaultPreferences);
	}

	@Override
	public PortletPreferences getLayoutPortletSetup(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String defaultPreferences) {

		return PortletPreferencesLocalServiceUtil.getPreferences(
			companyId, ownerId, ownerType, plid, portletId, defaultPreferences);
	}

	@Override
	public PortalPreferences getPortalPreferences(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getRealUser();

		long userId = themeDisplay.getUserId();

		String doAsUserId = themeDisplay.getDoAsUserId();

		if ((user != null) && !user.isGuestUser() &&
			Validator.isNotNull(doAsUserId) &&
			!Objects.equals(String.valueOf(userId), doAsUserId)) {

			Company company = themeDisplay.getCompany();

			try {
				userId = GetterUtil.getLong(
					EncryptorUtil.decrypt(company.getKeyObj(), doAsUserId),
					userId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to decrypt user ID from " + doAsUserId,
						exception);
				}
				else if (_log.isWarnEnabled()) {
					_log.warn("Unable to decrypt user ID from " + doAsUserId);
				}
			}
		}

		return getPortalPreferences(
			httpServletRequest.getSession(), userId, themeDisplay.isSignedIn());
	}

	@Override
	public PortalPreferences getPortalPreferences(
		HttpSession httpSession, long userId, boolean signedIn) {

		PortalPreferences portalPreferences = null;

		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				PortalPreferencesLocalServiceUtil.getPreferences(
					userId, PortletKeys.PREFS_OWNER_TYPE_USER);

		if (signedIn) {
			portalPreferences =
				portalPreferencesWrapper.getPortalPreferencesImpl();
		}
		else {
			if (httpSession != null) {
				portalPreferences = (PortalPreferences)httpSession.getAttribute(
					WebKeys.PORTAL_PREFERENCES);
			}

			if (portalPreferences == null) {
				portalPreferences =
					portalPreferencesWrapper.getPortalPreferencesImpl();

				if (httpSession != null) {
					httpSession.setAttribute(
						WebKeys.PORTAL_PREFERENCES, portalPreferences);
				}
			}
		}

		portalPreferences.setSignedIn(signedIn);
		portalPreferences.setUserId(userId);

		return portalPreferences;
	}

	@Override
	public PortalPreferences getPortalPreferences(
		long userId, boolean signedIn) {

		return getPortalPreferences(null, userId, signedIn);
	}

	@Override
	public PortalPreferences getPortalPreferences(
		PortletRequest portletRequest) {

		return getPortalPreferences(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

	@Override
	public PortletPreferences getPortletPreferences(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		return PortletPreferencesLocalServiceUtil.getPreferences(
			getPortletPreferencesIds(httpServletRequest, portletId));
	}

	@Override
	public PortletPreferencesIds getPortletPreferencesIds(
			HttpServletRequest httpServletRequest, Layout layout,
			String portletId)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		boolean modeEditGuest = false;

		String portletMode = ParamUtil.getString(
			httpServletRequest, "p_p_mode");

		if (portletMode.equals(LiferayPortletMode.EDIT_GUEST.toString()) ||
			((layoutTypePortlet != null) &&
			 layoutTypePortlet.hasModeEditGuestPortletId(portletId))) {

			modeEditGuest = true;
		}

		return _getPortletPreferencesIds(
			themeDisplay, themeDisplay.getSiteGroupId(),
			PortalUtil.getUserId(httpServletRequest), layout, portletId,
			modeEditGuest);
	}

	@Override
	public PortletPreferencesIds getPortletPreferencesIds(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		return getPortletPreferencesIds(httpServletRequest, layout, portletId);
	}

	@Override
	public PortletPreferencesIds getPortletPreferencesIds(
			long siteGroupId, long userId, Layout layout, String portletId,
			boolean modeEditGuest)
		throws PortalException {

		return _getPortletPreferencesIds(
			null, siteGroupId, userId, layout, portletId, modeEditGuest);
	}

	@Override
	public PortletPreferencesIds getPortletPreferencesIds(
			long companyId, long siteGroupId, long layoutGroupId, long plid,
			String portletId)
		throws IllegalArgumentException {

		String originalPortletId = portletId;

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			companyId, portletId);

		if (portlet.isPreferencesCompanyWide() ||
			(portlet.isPreferencesOwnedByGroup() &&
			 !portlet.isPreferencesUniquePerLayout())) {

			portletId = PortletIdCodec.decodePortletName(portletId);
		}

		long ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;
		int ownerType = 0;

		Group group = GroupLocalServiceUtil.fetchGroup(siteGroupId);

		if ((group != null) && group.isLayout()) {
			plid = group.getClassPK();
		}

		if (PortletIdCodec.hasUserId(originalPortletId)) {
			ownerId = PortletIdCodec.decodeUserId(originalPortletId);
			ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
		}
		else if (portlet.isPreferencesUniquePerLayout()) {
			ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;
		}
		else if (portlet.isPreferencesOwnedByGroup()) {
			plid = PortletKeys.PREFS_PLID_SHARED;

			if (siteGroupId > LayoutConstants.DEFAULT_PLID) {
				ownerId = siteGroupId;
			}
			else {
				ownerId = layoutGroupId;
			}

			ownerType = PortletKeys.PREFS_OWNER_TYPE_GROUP;
		}
		else if (portlet.isPreferencesCompanyWide()) {
			plid = PortletKeys.PREFS_PLID_SHARED;

			ownerId = companyId;
			ownerType = PortletKeys.PREFS_OWNER_TYPE_COMPANY;
		}

		if (ownerType == 0) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Portlet ", originalPortletId,
					" has owner type 0 because of wrong properties or wrong ",
					"data handler settings"));
		}

		if ((ownerId == 0) && (plid == 0)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Portlet ", originalPortletId,
					" has owner ID of 0 and PLID of 0 because of wrong ",
					"properties or wrong data handler settings"));
		}

		return new PortletPreferencesIds(
			companyId, ownerId, ownerType, plid, portletId);
	}

	@Override
	public PortletPreferencesIds getPortletPreferencesIds(
		long companyId, long siteGroupId, long plid, String portletId,
		String settingsScope) {

		int ownerType = 0;
		long ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;

		if (settingsScope.equals(
				PortletPreferencesFactoryConstants.SETTINGS_SCOPE_COMPANY)) {

			ownerId = companyId;
			ownerType = PortletKeys.PREFS_OWNER_TYPE_COMPANY;
			plid = PortletKeys.PREFS_PLID_SHARED;
		}
		else if (settingsScope.equals(
					PortletPreferencesFactoryConstants.SETTINGS_SCOPE_GROUP)) {

			ownerId = siteGroupId;
			ownerType = PortletKeys.PREFS_OWNER_TYPE_GROUP;
			plid = PortletKeys.PREFS_PLID_SHARED;
		}
		else if (settingsScope.equals(
					PortletPreferencesFactoryConstants.
						SETTINGS_SCOPE_PORTLET_INSTANCE)) {

			ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;
			ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;

			if (PortletIdCodec.hasUserId(portletId)) {
				ownerId = PortletIdCodec.decodeUserId(portletId);
				ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
			}
		}

		return new PortletPreferencesIds(
			companyId, ownerId, ownerType, plid, portletId);
	}

	@Override
	public PortletPreferences getPortletSetup(
		HttpServletRequest httpServletRequest, String portletId) {

		return getPortletSetup(httpServletRequest, portletId, null);
	}

	@Override
	public PortletPreferences getPortletSetup(
		HttpServletRequest httpServletRequest, String portletId,
		String defaultPreferences) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest instanceof ConfigurationPortletRequest) {
			PortletRequestWrapper portletRequestWrapper =
				(PortletRequestWrapper)portletRequest;

			return portletRequestWrapper.getPreferences();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getPortletSetup(
			themeDisplay.getSiteGroupId(), themeDisplay.getLayout(), portletId,
			defaultPreferences);
	}

	@Override
	public PortletPreferences getPortletSetup(
		Layout layout, String portletId, String defaultPreferences) {

		return getPortletSetup(
			LayoutConstants.DEFAULT_PLID, layout, portletId,
			defaultPreferences);
	}

	@Override
	public PortletPreferences getPortletSetup(
		long siteGroupId, Layout layout, String portletId,
		String defaultPreferences) {

		return getPortletSetup(
			layout.getCompanyId(), siteGroupId, layout.getGroupId(),
			layout.getPlid(), portletId, defaultPreferences, false);
	}

	@Override
	public PortletPreferences getPortletSetup(PortletRequest portletRequest) {
		return getPortletSetup(
			portletRequest, PortalUtil.getPortletId(portletRequest));
	}

	@Override
	public PortletPreferences getPortletSetup(
		PortletRequest portletRequest, String portletId) {

		if (portletRequest instanceof ConfigurationPortletRequest) {
			PortletRequestWrapper portletRequestWrapper =
				(PortletRequestWrapper)portletRequest;

			return portletRequestWrapper.getPreferences();
		}

		return getPortletSetup(
			PortalUtil.getHttpServletRequest(portletRequest), portletId);
	}

	@Override
	public Map<Long, PortletPreferences> getPortletSetupMap(
		long companyId, long groupId, long ownerId, int ownerType,
		String portletId, boolean privateLayout) {

		Map<Long, PortletPreferences> portletSetupMap = new HashMap<>();

		List<com.liferay.portal.kernel.model.PortletPreferences>
			portletPreferencesList =
				PortletPreferencesLocalServiceUtil.getPortletPreferences(
					companyId, groupId, ownerId, ownerType, portletId,
					privateLayout);

		for (com.liferay.portal.kernel.model.PortletPreferences
				portletPreferences : portletPreferencesList) {

			portletSetupMap.put(
				portletPreferences.getPlid(),
				PortletPreferencesLocalServiceUtil.getPreferences(
					companyId, ownerId, ownerType, portletPreferences.getPlid(),
					portletId));
		}

		return portletSetupMap;
	}

	@Override
	public PortletPreferences getPreferences(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		PortletPreferences portletPreferences = null;

		if (portletRequest != null) {
			PortletPreferencesWrapper portletPreferencesWrapper =
				(PortletPreferencesWrapper)portletRequest.getPreferences();

			portletPreferences =
				portletPreferencesWrapper.getPortletPreferencesImpl();
		}

		return portletPreferences;
	}

	@Override
	public PreferencesValidator getPreferencesValidator(Portlet portlet) {
		return PortalUtil.getPreferencesValidator(portlet);
	}

	@Override
	public PortletPreferences getStrictLayoutPortletSetup(
		Layout layout, String portletId) {

		long ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;

		if (PortletIdCodec.hasUserId(portletId)) {
			ownerId = PortletIdCodec.decodeUserId(portletId);
			ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
		}

		return PortletPreferencesLocalServiceUtil.getStrictPreferences(
			layout.getCompanyId(), ownerId, ownerType, layout.getPlid(),
			portletId);
	}

	@Override
	public PortletPreferences getStrictPortletSetup(
		Layout layout, String portletId) {

		return getPortletSetup(
			layout.getCompanyId(), LayoutConstants.DEFAULT_PLID,
			layout.getGroupId(), layout.getPlid(), portletId, StringPool.BLANK,
			true);
	}

	@Override
	public PortletPreferences getStrictPortletSetup(
		long companyId, long groupId, String portletId) {

		return getPortletSetup(
			companyId, LayoutConstants.DEFAULT_PLID, groupId,
			LayoutConstants.DEFAULT_PLID, portletId, StringPool.BLANK, true);
	}

	@Override
	public StrictPortletPreferencesImpl strictFromXML(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String xml) {

		Map<String, Preference> preferencesMap = toPreferencesMap(xml);

		return new StrictPortletPreferencesImpl(
			companyId, ownerId, ownerType, plid, portletId, xml,
			preferencesMap);
	}

	@Override
	public String toXML(PortalPreferences portalPreferences) {
		PortalPreferencesImpl portalPreferencesImpl = null;

		if (portalPreferences instanceof PortalPreferencesWrapper) {
			PortalPreferencesWrapper portalPreferencesWrapper =
				(PortalPreferencesWrapper)portalPreferences;

			portalPreferencesImpl =
				portalPreferencesWrapper.getPortalPreferencesImpl();
		}
		else {
			portalPreferencesImpl = (PortalPreferencesImpl)portalPreferences;
		}

		return portalPreferencesImpl.toXML();
	}

	@Override
	public String toXML(PortletPreferences portletPreferences) {
		PortletPreferencesImpl portletPreferencesImpl = null;

		if (portletPreferences instanceof PortletPreferencesWrapper) {
			PortletPreferencesWrapper portletPreferencesWrapper =
				(PortletPreferencesWrapper)portletPreferences;

			portletPreferencesImpl =
				portletPreferencesWrapper.getPortletPreferencesImpl();
		}
		else {
			portletPreferencesImpl = (PortletPreferencesImpl)portletPreferences;
		}

		return portletPreferencesImpl.toXML();
	}

	protected static Preference readPreference(XMLEventReader xmlEventReader)
		throws XMLStreamException {

		String name = null;
		List<String> values = new ArrayList<>();
		boolean readOnly = false;

		while (xmlEventReader.hasNext()) {
			XMLEvent xmlEvent = xmlEventReader.nextEvent();

			if (xmlEvent.isStartElement()) {
				StartElement startElement = xmlEvent.asStartElement();

				QName startElementName = startElement.getName();

				String elementName = startElementName.getLocalPart();

				if (elementName.equals("name")) {
					name = StAXReaderUtil.read(xmlEventReader);
				}
				else if (elementName.equals("value")) {
					String value = StAXReaderUtil.read(xmlEventReader);

					values.add(value);
				}
				else if (elementName.equals("read-only")) {
					String value = StAXReaderUtil.read(xmlEventReader);

					readOnly = GetterUtil.getBoolean(value);
				}
			}
			else if (xmlEvent.isEndElement()) {
				EndElement endElement = xmlEvent.asEndElement();

				QName endElementName = endElement.getName();

				String elementName = endElementName.getLocalPart();

				if (elementName.equals("preference")) {
					break;
				}
			}
		}

		return new Preference(name, values.toArray(new String[0]), readOnly);
	}

	protected PortletPreferences getPortletSetup(
		long companyId, long siteGroupId, long layoutGroupId, long plid,
		String portletId, String defaultPreferences, boolean strictMode) {

		PortletPreferencesIds portletPreferencesIds = getPortletPreferencesIds(
			companyId, siteGroupId, layoutGroupId, plid, portletId);

		if (strictMode) {
			return PortletPreferencesLocalServiceUtil.getStrictPreferences(
				portletPreferencesIds.getCompanyId(),
				portletPreferencesIds.getOwnerId(),
				portletPreferencesIds.getOwnerType(),
				portletPreferencesIds.getPlid(),
				portletPreferencesIds.getPortletId());
		}

		return PortletPreferencesLocalServiceUtil.getPreferences(
			portletPreferencesIds.getCompanyId(),
			portletPreferencesIds.getOwnerId(),
			portletPreferencesIds.getOwnerType(),
			portletPreferencesIds.getPlid(),
			portletPreferencesIds.getPortletId(), defaultPreferences);
	}

	protected Map<String, Preference> toPreferencesMap(String xml) {
		if (Objects.equals(xml, PortletConstants.DEFAULT_PREFERENCES)) {
			if (_defaultPreferencesMap == null) {
				_defaultPreferencesMap = createPreferencesMap(
					PortletConstants.DEFAULT_PREFERENCES);
			}

			return _defaultPreferencesMap;
		}

		if (Validator.isNull(xml)) {
			return Collections.emptyMap();
		}

		String cacheKey = _encodeCacheKey(xml);

		Map<String, Preference> preferencesMap = _preferencesMapPortalCache.get(
			cacheKey);

		if (preferencesMap != null) {
			return preferencesMap;
		}

		preferencesMap = createPreferencesMap(xml);

		_preferencesMapPortalCache.put(cacheKey, preferencesMap);

		return preferencesMap;
	}

	private String _encodeCacheKey(String xml) {
		if (xml.length() <=
				PropsValues.PORTLET_PREFERENCES_CACHE_KEY_THRESHOLD_SIZE) {

			return xml;
		}

		CacheKeyGenerator cacheKeyGenerator =
			CacheKeyGeneratorUtil.getCacheKeyGenerator(
				PortletPreferencesFactoryImpl.class.getName());

		if (_log.isDebugEnabled()) {
			_log.debug("Cache key generator " + cacheKeyGenerator.getClass());
		}

		return String.valueOf(cacheKeyGenerator.getCacheKey(xml));
	}

	private PortletPreferencesIds _getPortletPreferencesIds(
			ThemeDisplay themeDisplay, long siteGroupId, long userId,
			Layout layout, String portletId, boolean modeEditGuest)
		throws PortalException {

		String originalPortletId = portletId;

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			layout.getCompanyId(), portletId);

		if (modeEditGuest) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			boolean hasUpdateLayoutPermission = LayoutPermissionUtil.contains(
				permissionChecker, layout, ActionKeys.UPDATE);

			if (!layout.isPrivateLayout() && hasUpdateLayoutPermission) {
			}
			else {

				// Only users with the correct permissions can update guest
				// preferences

				throw new PrincipalException.MustHavePermission(
					permissionChecker, Layout.class.getName(),
					layout.getLayoutId(), ActionKeys.UPDATE);
			}
		}

		long ownerId = 0;
		int ownerType = 0;
		long plid = 0;

		if (PortletIdCodec.hasUserId(originalPortletId) &&
			(PortletIdCodec.decodeUserId(originalPortletId) == userId)) {

			ownerId = userId;
			ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
			plid = layout.getPlid();
		}
		else if (portlet.isPreferencesCompanyWide()) {
			ownerId = layout.getCompanyId();
			ownerType = PortletKeys.PREFS_OWNER_TYPE_COMPANY;
			plid = PortletKeys.PREFS_PLID_SHARED;
			portletId = PortletIdCodec.decodePortletName(portletId);
		}
		else {
			if (portlet.isPreferencesUniquePerLayout()) {
				ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;

				long masterLayoutPlid = layout.getMasterLayoutPlid();

				long portletPreferencesCount =
					PortletPreferencesLocalServiceUtil.
						getPortletPreferencesCount(
							PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
							masterLayoutPlid, portletId);

				if ((masterLayoutPlid > 0) && (portletPreferencesCount > 0)) {
					plid = masterLayoutPlid;
				}
				else {
					plid = layout.getPlid();
				}

				if (themeDisplay != null) {
					if (themeDisplay.isPortletEmbedded(
							layout.getGroupId(), layout, portletId)) {

						ownerId = layout.getGroupId();
						plid = PortletKeys.PREFS_PLID_SHARED;
					}
				}
				else if (layout.isPortletEmbedded(
							portletId, layout.getGroupId())) {

					ownerId = layout.getGroupId();
					plid = PortletKeys.PREFS_PLID_SHARED;
				}

				ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;

				if (portlet.isPreferencesOwnedByGroup()) {
				}
				else {
					if ((userId <= 0) || modeEditGuest) {
						userId = UserLocalServiceUtil.getGuestUserId(
							layout.getCompanyId());
					}

					ownerId = userId;
					ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
				}
			}
			else {
				plid = PortletKeys.PREFS_PLID_SHARED;

				if (portlet.isPreferencesOwnedByGroup()) {
					ownerId = siteGroupId;
					ownerType = PortletKeys.PREFS_OWNER_TYPE_GROUP;
					portletId = PortletIdCodec.decodePortletName(portletId);
				}
				else {
					if ((userId <= 0) || modeEditGuest) {
						userId = UserLocalServiceUtil.getGuestUserId(
							layout.getCompanyId());
					}

					ownerId = userId;
					ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
				}
			}
		}

		return new PortletPreferencesIds(
			layout.getCompanyId(), ownerId, ownerType, plid, portletId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesFactoryImpl.class);

	private Map<String, Preference> _defaultPreferencesMap;
	private final PortalCache<String, Map<String, Preference>>
		_preferencesMapPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM,
			PortletPreferencesFactoryImpl.class.getName());

}