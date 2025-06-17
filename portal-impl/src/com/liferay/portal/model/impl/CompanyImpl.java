/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.service.CompanyInfoLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.security.Key;

import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 */
public class CompanyImpl extends CompanyBaseImpl {

	@Override
	public int compareTo(Company company) {
		String webId1 = getWebId();

		if (webId1.equals(PropsValues.COMPANY_DEFAULT_WEB_ID)) {
			return -1;
		}

		String webId2 = company.getWebId();

		if (webId2.equals(PropsValues.COMPANY_DEFAULT_WEB_ID)) {
			return 1;
		}

		return webId1.compareTo(webId2);
	}

	@Override
	public String getAdminName() {
		return "Administrator";
	}

	@Override
	public String getAuthType() {
		return PrefsPropsUtil.getString(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
			PropsValues.COMPANY_SECURITY_AUTH_TYPE);
	}

	@Override
	public CompanyInfo getCompanyInfo() {
		if (_companyInfo == null) {
			CompanyInfo companyInfo = CompanyInfoLocalServiceUtil.fetchCompany(
				getCompanyId());

			if (companyInfo == null) {
				companyInfo = CompanyInfoLocalServiceUtil.createCompanyInfo(
					CounterLocalServiceUtil.increment());

				companyInfo.setCompanyId(getCompanyId());
			}

			_companyInfo = companyInfo;
		}

		return _companyInfo;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #getGuestUser}
	 */
	@Deprecated
	@Override
	public User getDefaultUser() throws PortalException {
		return getGuestUser();
	}

	@Override
	public String getDefaultWebId() {
		return PropsValues.COMPANY_DEFAULT_WEB_ID;
	}

	@Override
	public String getEmailAddress() {

		// Primary email address

		return "admin@" + getMx();
	}

	@Override
	public Group getGroup() throws PortalException {
		if (getCompanyId() > CompanyConstants.SYSTEM) {
			if (_group == null) {
				_group = GroupLocalServiceUtil.fetchGroup(getGroupId());
			}

			return _group;
		}

		return new GroupImpl();
	}

	@Override
	public long getGroupId() {
		if (_groupId == -1) {
			_group = GroupLocalServiceUtil.fetchCompanyGroup(getCompanyId());

			if (_group != null) {
				_groupId = _group.getGroupId();

				groupIdUpdateEntityCacheConsumer.accept(_groupId);
			}
		}

		return _groupId;
	}

	@Override
	public User getGuestUser() throws PortalException {
		return UserLocalServiceUtil.getGuestUser(getCompanyId());
	}

	@Override
	public String getKey() {
		CompanyInfo companyInfo = getCompanyInfo();

		return companyInfo.getKey();
	}

	@Override
	public Key getKeyObj() {
		if (_keyObj == null) {
			String key = getKey();

			if (Validator.isNotNull(key)) {
				_keyObj = EncryptorUtil.deserializeKey(key);
			}
		}

		return _keyObj;
	}

	@Override
	public Locale getLocale() throws PortalException {
		return getGuestUser().getLocale();
	}

	@AutoEscape
	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public String getPortalURL(long groupId) throws PortalException {
		int portalServerPort = PortalUtil.getPortalServerPort(false);

		String portalURL = PortalUtil.getPortalURL(
			getVirtualHostname(), portalServerPort, false);

		if (groupId <= 0) {
			return portalURL;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (group.hasPublicLayouts()) {
			LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
				groupId, false);

			TreeMap<String, String> virtualHostnames =
				layoutSet.getVirtualHostnames();

			if (!virtualHostnames.isEmpty()) {
				portalURL = PortalUtil.getPortalURL(
					virtualHostnames.firstKey(), portalServerPort, false);
			}
		}
		else if (group.hasPrivateLayouts()) {
			LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
				groupId, true);

			TreeMap<String, String> virtualHostnames =
				layoutSet.getVirtualHostnames();

			if (!virtualHostnames.isEmpty()) {
				portalURL = PortalUtil.getPortalURL(
					virtualHostnames.firstKey(), portalServerPort, false);
			}
		}

		return portalURL;
	}

	@Override
	public String getPortalURL(long groupId, boolean privateLayout)
		throws PortalException {

		int portalServerPort = PortalUtil.getPortalServerPort(false);

		String portalURL = PortalUtil.getPortalURL(
			getVirtualHostname(), portalServerPort, false);

		if (groupId <= 0) {
			return portalURL;
		}

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			groupId, privateLayout);

		TreeMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (!virtualHostnames.isEmpty()) {
			portalURL = PortalUtil.getPortalURL(
				virtualHostnames.firstKey(), portalServerPort, false);
		}

		return portalURL;
	}

	@Override
	public String getShortName() throws PortalException {
		return getName();
	}

	@Override
	public TimeZone getTimeZone() throws PortalException {
		return getGuestUser().getTimeZone();
	}

	@Override
	public String getVirtualHostname() {

		// Call Validator.isNotNull on _virtualHostname because of the field is
		// an especially annotated CacheField. See LCD-14360.

		if (Validator.isNotNull(_virtualHostname)) {
			return _virtualHostname;
		}

		VirtualHost virtualHost =
			VirtualHostLocalServiceUtil.fetchCompanyDefaultVirtualHost(
				getCompanyId());

		if (virtualHost == null) {
			return StringPool.BLANK;
		}

		_virtualHostname = virtualHost.getHostname();

		virtualHostnameUpdateEntityCacheConsumer.accept(_virtualHostname);

		return _virtualHostname;
	}

	@Override
	public boolean hasCompanyMx(String emailAddress) {
		emailAddress = StringUtil.toLowerCase(emailAddress.trim());

		int pos = emailAddress.indexOf(CharPool.AT);

		if (pos == -1) {
			return false;
		}

		String mx = emailAddress.substring(pos + 1);

		if (mx.equals(getMx())) {
			return true;
		}

		String[] mailHostNames = PrefsPropsUtil.getStringArray(
			getCompanyId(), PropsKeys.ADMIN_MAIL_HOST_NAMES,
			StringPool.NEW_LINE, PropsValues.ADMIN_MAIL_HOST_NAMES);

		for (String mailHostName : mailHostNames) {
			if (StringUtil.equalsIgnoreCase(mx, mailHostName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAutoLogin() {
		return PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_AUTO_LOGIN,
			PropsValues.COMPANY_SECURITY_AUTO_LOGIN);
	}

	@Override
	public boolean isSendPasswordResetLink() {
		return PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_SEND_PASSWORD_RESET_LINK,
			PropsValues.COMPANY_SECURITY_SEND_PASSWORD_RESET_LINK);
	}

	@Override
	public boolean isSiteLogo() {
		return PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_SITE_LOGO,
			PropsValues.COMPANY_SECURITY_SITE_LOGO);
	}

	@Override
	public boolean isStrangers() {
		return PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_STRANGERS,
			PropsValues.COMPANY_SECURITY_STRANGERS);
	}

	@Override
	public boolean isStrangersVerify() {
		return PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_STRANGERS_VERIFY,
			PropsValues.COMPANY_SECURITY_STRANGERS_VERIFY);
	}

	@Override
	public boolean isStrangersWithMx() {
		return PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_STRANGERS_WITH_MX,
			PropsValues.COMPANY_SECURITY_STRANGERS_WITH_MX);
	}

	@Override
	public boolean isUpdatePasswordRequired() {
		return PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.COMPANY_SECURITY_UPDATE_PASSWORD_REQUIRED,
			PropsValues.COMPANY_SECURITY_UPDATE_PASSWORD_REQUIRED);
	}

	@Override
	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setKey(String key) {
		CompanyInfo companyInfo = getCompanyInfo();

		companyInfo.setKey(key);

		_keyObj = null;
	}

	@Override
	public void setKeyObj(Key keyObj) {
		_keyObj = keyObj;
	}

	@Override
	public void setVirtualHostname(String virtualHostname) {
		_virtualHostname = virtualHostname;
	}

	private CompanyInfo _companyInfo;
	private Group _group;

	@CacheField(permanent = true, propagateToInterface = true)
	private long _groupId = -1;

	private Key _keyObj;

	@CacheField(propagateToInterface = true)
	private String _virtualHostname;

}