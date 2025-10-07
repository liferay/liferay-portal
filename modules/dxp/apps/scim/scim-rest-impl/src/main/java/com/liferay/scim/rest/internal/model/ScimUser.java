/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.model;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.objects.plainobjects.ScimAddress;

/**
 * @author Rafael Praxedes
 */
public class ScimUser {

	public List<ScimAddress> getAddresses() {
		return _addresses;
	}

	public Date getBirthday() {
		return _birthday;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public String[] getEmailAddresses() {
		return _emailAddresses;
	}

	public String[] getEntitlements() {
		return _entitlements;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public String getFirstName() {
		return _firstName;
	}

	public long[] getGroupIds() {
		return _groupIds;
	}

	public String getId() {
		return _id;
	}

	public Map<String, String> getIMs() {
		return _ims;
	}

	public String getJobTitle() {
		return _jobTitle;
	}

	public String getLastName() {
		return _lastName;
	}

	public Locale getLocale() {
		return _locale;
	}

	public String getMiddleName() {
		return _middleName;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public String getNickName() {
		return _nickName;
	}

	public long[] getOrganizationIds() {
		return _organizationIds;
	}

	public String getPassword() {
		return _password;
	}

	public List<MultiValuedComplexType>
		getPhoneNumberMultiValuedComplexTypes() {

		return _phoneNumberMultiValuedComplexTypes;
	}

	public String[] getPhotos() {
		return _photos;
	}

	public String getPreferredLanguage() {
		return _preferredLanguage;
	}

	public long getPrefix() {
		return _prefix;
	}

	public String getProfileUrl() {
		return _profileUrl;
	}

	public long[] getRoleIds() {
		return _roleIds;
	}

	public String getScreenName() {
		return _screenName;
	}

	public long getSuffix() {
		return _suffix;
	}

	public String getTimeZoneId() {
		return _timeZoneId;
	}

	public long[] getUserGroupIds() {
		return _userGroupIds;
	}

	public String getUserType() {
		return _userType;
	}

	public String[] getX509Certificates() {
		return _x509Certificates;
	}

	public boolean isActive() {
		return _active;
	}

	public boolean isAutoPassword() {
		return _autoPassword;
	}

	public boolean isAutoScreenName() {
		return _autoScreenName;
	}

	public boolean isMale() {
		return _male;
	}

	public boolean isPasswordReset() {
		return _passwordReset;
	}

	public boolean isSendEmail() {
		return _sendEmail;
	}

	public boolean isUpdatePassword() {
		return _updatePassword;
	}

	public void setActive(boolean active) {
		_active = active;
	}

	public void setAddresses(List<ScimAddress> addresses) {
		_addresses = addresses;
	}

	public void setAutoPassword(boolean autoPassword) {
		_autoPassword = autoPassword;
	}

	public void setAutoScreenName(boolean autoScreenName) {
		_autoScreenName = autoScreenName;
	}

	public void setBirthday(Date birthday) {
		_birthday = birthday;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	public void setEmailAddresses(String[] emailAddresses) {
		_emailAddresses = emailAddresses;
	}

	public void setEntitlements(String[] entitlements) {
		_entitlements = entitlements;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		_externalReferenceCode = externalReferenceCode;
	}

	public void setFirstName(String firstName) {
		_firstName = firstName;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIMs(Map<String, String> ims) {
		_ims = ims;
	}

	public void setJobTitle(String jobTitle) {
		_jobTitle = jobTitle;
	}

	public void setLastName(String lastName) {
		_lastName = lastName;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setMale(boolean male) {
		_male = male;
	}

	public void setMiddleName(String middleName) {
		_middleName = middleName;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public void setNickName(String nickName) {
		_nickName = nickName;
	}

	public void setOrganizationIds(long[] organizationIds) {
		_organizationIds = organizationIds;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public void setPasswordReset(boolean passwordReset) {
		_passwordReset = passwordReset;
	}

	public void setPhoneNumberMultiValuedComplexTypes(
		List<MultiValuedComplexType> phoneNumberMultiValuedComplexTypes) {

		_phoneNumberMultiValuedComplexTypes =
			phoneNumberMultiValuedComplexTypes;
	}

	public void setPhotos(String[] photos) {
		_photos = photos;
	}

	public void setPreferredLanguage(String preferredLanguage) {
		_preferredLanguage = preferredLanguage;
	}

	public void setPrefix(long prefix) {
		_prefix = prefix;
	}

	public void setProfileUrl(String profileUrl) {
		_profileUrl = profileUrl;
	}

	public void setRoleIds(long[] roleIds) {
		if (FeatureFlagManagerUtil.isEnabled("LPD-56434")) {
			_roleIds = roleIds;
		}
	}

	public void setScreenName(String screenName) {
		_screenName = screenName;
	}

	public void setSendEmail(boolean sendEmail) {
		_sendEmail = sendEmail;
	}

	public void setSuffix(long suffix) {
		_suffix = suffix;
	}

	public void setTimeZoneId(String timeZoneId) {
		_timeZoneId = timeZoneId;
	}

	public void setUpdatePassword(boolean updatePassword) {
		_updatePassword = updatePassword;
	}

	public void setUserGroupIds(long[] userGroupIds) {
		_userGroupIds = userGroupIds;
	}

	public void setUserType(String userType) {
		_userType = userType;
	}

	public void setX509Certificates(String[] x509Certificates) {
		_x509Certificates = x509Certificates;
	}

	private boolean _active;
	private List<ScimAddress> _addresses;
	private boolean _autoPassword;
	private boolean _autoScreenName;
	private Date _birthday;
	private long _companyId;
	private Date _createDate;
	private String _displayName;
	private String[] _emailAddresses;
	private String[] _entitlements;
	private String _externalReferenceCode;
	private String _firstName;
	private long[] _groupIds;
	private String _id;
	private Map<String, String> _ims;
	private String _jobTitle;
	private String _lastName;
	private Locale _locale;
	private boolean _male;
	private String _middleName;
	private Date _modifiedDate;
	private String _nickName;
	private long[] _organizationIds;
	private String _password;
	private boolean _passwordReset;
	private List<MultiValuedComplexType> _phoneNumberMultiValuedComplexTypes;
	private String[] _photos;
	private String _preferredLanguage;
	private long _prefix;
	private String _profileUrl;
	private long[] _roleIds;
	private String _screenName;
	private boolean _sendEmail;
	private long _suffix;
	private String _timeZoneId;
	private boolean _updatePassword;
	private long[] _userGroupIds;
	private String _userType;
	private String[] _x509Certificates;

}