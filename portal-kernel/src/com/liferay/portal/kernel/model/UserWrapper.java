/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link User}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see User
 * @generated
 */
public class UserWrapper
	extends BaseModelWrapper<User> implements ModelWrapper<User>, User {

	public UserWrapper(User user) {
		super(user);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("userId", getUserId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("contactId", getContactId());
		attributes.put("password", getPassword());
		attributes.put("passwordEncrypted", isPasswordEncrypted());
		attributes.put("passwordReset", isPasswordReset());
		attributes.put("passwordModifiedDate", getPasswordModifiedDate());
		attributes.put("digest", getDigest());
		attributes.put("reminderQueryQuestion", getReminderQueryQuestion());
		attributes.put("reminderQueryAnswer", getReminderQueryAnswer());
		attributes.put("graceLoginCount", getGraceLoginCount());
		attributes.put("screenName", getScreenName());
		attributes.put("emailAddress", getEmailAddress());
		attributes.put("facebookId", getFacebookId());
		attributes.put("googleUserId", getGoogleUserId());
		attributes.put("ldapServerId", getLdapServerId());
		attributes.put("openId", getOpenId());
		attributes.put("portraitId", getPortraitId());
		attributes.put("languageId", getLanguageId());
		attributes.put("timeZoneId", getTimeZoneId());
		attributes.put("greeting", getGreeting());
		attributes.put("comments", getComments());
		attributes.put("firstName", getFirstName());
		attributes.put("middleName", getMiddleName());
		attributes.put("lastName", getLastName());
		attributes.put("jobTitle", getJobTitle());
		attributes.put("loginDate", getLoginDate());
		attributes.put("loginIP", getLoginIP());
		attributes.put("lastLoginDate", getLastLoginDate());
		attributes.put("lastLoginIP", getLastLoginIP());
		attributes.put("lastFailedLoginDate", getLastFailedLoginDate());
		attributes.put("failedLoginAttempts", getFailedLoginAttempts());
		attributes.put("lockout", isLockout());
		attributes.put("lockoutDate", getLockoutDate());
		attributes.put("agreedToTermsOfUse", isAgreedToTermsOfUse());
		attributes.put("emailAddressVerified", isEmailAddressVerified());
		attributes.put("type", getType());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long contactId = (Long)attributes.get("contactId");

		if (contactId != null) {
			setContactId(contactId);
		}

		String password = (String)attributes.get("password");

		if (password != null) {
			setPassword(password);
		}

		Boolean passwordEncrypted = (Boolean)attributes.get(
			"passwordEncrypted");

		if (passwordEncrypted != null) {
			setPasswordEncrypted(passwordEncrypted);
		}

		Boolean passwordReset = (Boolean)attributes.get("passwordReset");

		if (passwordReset != null) {
			setPasswordReset(passwordReset);
		}

		Date passwordModifiedDate = (Date)attributes.get(
			"passwordModifiedDate");

		if (passwordModifiedDate != null) {
			setPasswordModifiedDate(passwordModifiedDate);
		}

		String digest = (String)attributes.get("digest");

		if (digest != null) {
			setDigest(digest);
		}

		String reminderQueryQuestion = (String)attributes.get(
			"reminderQueryQuestion");

		if (reminderQueryQuestion != null) {
			setReminderQueryQuestion(reminderQueryQuestion);
		}

		String reminderQueryAnswer = (String)attributes.get(
			"reminderQueryAnswer");

		if (reminderQueryAnswer != null) {
			setReminderQueryAnswer(reminderQueryAnswer);
		}

		Integer graceLoginCount = (Integer)attributes.get("graceLoginCount");

		if (graceLoginCount != null) {
			setGraceLoginCount(graceLoginCount);
		}

		String screenName = (String)attributes.get("screenName");

		if (screenName != null) {
			setScreenName(screenName);
		}

		String emailAddress = (String)attributes.get("emailAddress");

		if (emailAddress != null) {
			setEmailAddress(emailAddress);
		}

		Long facebookId = (Long)attributes.get("facebookId");

		if (facebookId != null) {
			setFacebookId(facebookId);
		}

		String googleUserId = (String)attributes.get("googleUserId");

		if (googleUserId != null) {
			setGoogleUserId(googleUserId);
		}

		Long ldapServerId = (Long)attributes.get("ldapServerId");

		if (ldapServerId != null) {
			setLdapServerId(ldapServerId);
		}

		String openId = (String)attributes.get("openId");

		if (openId != null) {
			setOpenId(openId);
		}

		Long portraitId = (Long)attributes.get("portraitId");

		if (portraitId != null) {
			setPortraitId(portraitId);
		}

		String languageId = (String)attributes.get("languageId");

		if (languageId != null) {
			setLanguageId(languageId);
		}

		String timeZoneId = (String)attributes.get("timeZoneId");

		if (timeZoneId != null) {
			setTimeZoneId(timeZoneId);
		}

		String greeting = (String)attributes.get("greeting");

		if (greeting != null) {
			setGreeting(greeting);
		}

		String comments = (String)attributes.get("comments");

		if (comments != null) {
			setComments(comments);
		}

		String firstName = (String)attributes.get("firstName");

		if (firstName != null) {
			setFirstName(firstName);
		}

		String middleName = (String)attributes.get("middleName");

		if (middleName != null) {
			setMiddleName(middleName);
		}

		String lastName = (String)attributes.get("lastName");

		if (lastName != null) {
			setLastName(lastName);
		}

		String jobTitle = (String)attributes.get("jobTitle");

		if (jobTitle != null) {
			setJobTitle(jobTitle);
		}

		Date loginDate = (Date)attributes.get("loginDate");

		if (loginDate != null) {
			setLoginDate(loginDate);
		}

		String loginIP = (String)attributes.get("loginIP");

		if (loginIP != null) {
			setLoginIP(loginIP);
		}

		Date lastLoginDate = (Date)attributes.get("lastLoginDate");

		if (lastLoginDate != null) {
			setLastLoginDate(lastLoginDate);
		}

		String lastLoginIP = (String)attributes.get("lastLoginIP");

		if (lastLoginIP != null) {
			setLastLoginIP(lastLoginIP);
		}

		Date lastFailedLoginDate = (Date)attributes.get("lastFailedLoginDate");

		if (lastFailedLoginDate != null) {
			setLastFailedLoginDate(lastFailedLoginDate);
		}

		Integer failedLoginAttempts = (Integer)attributes.get(
			"failedLoginAttempts");

		if (failedLoginAttempts != null) {
			setFailedLoginAttempts(failedLoginAttempts);
		}

		Boolean lockout = (Boolean)attributes.get("lockout");

		if (lockout != null) {
			setLockout(lockout);
		}

		Date lockoutDate = (Date)attributes.get("lockoutDate");

		if (lockoutDate != null) {
			setLockoutDate(lockoutDate);
		}

		Boolean agreedToTermsOfUse = (Boolean)attributes.get(
			"agreedToTermsOfUse");

		if (agreedToTermsOfUse != null) {
			setAgreedToTermsOfUse(agreedToTermsOfUse);
		}

		Boolean emailAddressVerified = (Boolean)attributes.get(
			"emailAddressVerified");

		if (emailAddressVerified != null) {
			setEmailAddressVerified(emailAddressVerified);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public User cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public Contact fetchContact() {
		return model.fetchContact();
	}

	@Override
	public String fetchPortraitURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.fetchPortraitURL(themeDisplay);
	}

	/**
	 * Returns the user's addresses.
	 *
	 * @return the user's addresses
	 */
	@Override
	public java.util.List<Address> getAddresses() {
		return model.getAddresses();
	}

	/**
	 * Returns the agreed to terms of use of this user.
	 *
	 * @return the agreed to terms of use of this user
	 */
	@Override
	public boolean getAgreedToTermsOfUse() {
		return model.getAgreedToTermsOfUse();
	}

	@Override
	public java.util.List<Group> getAllGroups()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAllGroups();
	}

	@Override
	public java.util.List<Role> getAllRoles()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAllRoles();
	}

	/**
	 * Returns the user's birth date.
	 *
	 * @return the user's birth date
	 */
	@Override
	public Date getBirthday()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getBirthday();
	}

	/**
	 * Returns the comments of this user.
	 *
	 * @return the comments of this user
	 */
	@Override
	public String getComments() {
		return model.getComments();
	}

	/**
	 * Returns the company ID of this user.
	 *
	 * @return the company ID of this user
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the user's company's mail domain.
	 *
	 * @return the user's company's mail domain
	 */
	@Override
	public String getCompanyMx()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCompanyMx();
	}

	/**
	 * Returns the user's associated contact.
	 *
	 * @return the user's associated contact
	 * @see Contact
	 */
	@Override
	public Contact getContact()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getContact();
	}

	/**
	 * Returns the contact ID of this user.
	 *
	 * @return the contact ID of this user
	 */
	@Override
	public long getContactId() {
		return model.getContactId();
	}

	/**
	 * Returns the create date of this user.
	 *
	 * @return the create date of this user
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this user.
	 *
	 * @return the ct collection ID of this user
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the digest of this user.
	 *
	 * @return the digest of this user
	 */
	@Override
	public String getDigest() {
		return model.getDigest();
	}

	/**
	 * Returns a digest for the user, incorporating the password.
	 *
	 * @param password a password to incorporate with the digest
	 * @return a digest for the user, incorporating the password
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getDigest(String password) {
		return model.getDigest(password);
	}

	/**
	 * Returns the user's primary email address, or a blank string if the
	 * address is fake.
	 *
	 * @return the user's primary email address, or a blank string if the
	 address is fake
	 */
	@Override
	public String getDisplayEmailAddress() {
		return model.getDisplayEmailAddress();
	}

	/**
	 * Returns the user's display URL based on the theme display, discounting
	 * the URL of the user's default intranet site home page.
	 *
	 * <p>
	 * The logic for the display URL to return is as follows:
	 * </p>
	 *
	 * <ol>
	 * <li>
	 * If the user is the guest user, return an empty string.
	 * </li>
	 * <li>
	 * Else, if a friendly URL is available for the user's profile, return that
	 * friendly URL.
	 * </li>
	 * <li>
	 * Otherwise, return the URL of the user's default extranet site home page.
	 * </li>
	 * </ol>
	 *
	 * @param themeDisplay the theme display
	 * @return the user's display URL
	 */
	@Override
	public String getDisplayURL(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDisplayURL(themeDisplay);
	}

	/**
	 * Returns the user's display URL based on the theme display.
	 *
	 * <p>
	 * The logic for the display URL to return is as follows:
	 * </p>
	 *
	 * <ol>
	 * <li>
	 * If the user is the guest user, return an empty string.
	 * </li>
	 * <li>
	 * Else, if a friendly URL is available for the user's profile, return that
	 * friendly URL.
	 * </li>
	 * <li>
	 * Else, if <code>privateLayout</code> is <code>true</code>, return the URL
	 * of the user's default intranet site home page.
	 * </li>
	 * <li>
	 * Otherwise, return the URL of the user's default extranet site home page.
	 * </li>
	 * </ol>
	 *
	 * @param themeDisplay the theme display
	 * @param privateLayout whether to use the URL of the user's default
	 intranet (versus extranet) site home page, if no friendly URL is
	 available for the user's profile
	 * @return the user's display URL
	 * @throws PortalException
	 */
	@Override
	public String getDisplayURL(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
			boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDisplayURL(themeDisplay, privateLayout);
	}

	/**
	 * Returns the email address of this user.
	 *
	 * @return the email address of this user
	 */
	@Override
	public String getEmailAddress() {
		return model.getEmailAddress();
	}

	/**
	 * Returns the user's email addresses.
	 *
	 * @return the user's email addresses
	 */
	@Override
	public java.util.List<EmailAddress> getEmailAddresses() {
		return model.getEmailAddresses();
	}

	/**
	 * Returns the email address verified of this user.
	 *
	 * @return the email address verified of this user
	 */
	@Override
	public boolean getEmailAddressVerified() {
		return model.getEmailAddressVerified();
	}

	/**
	 * Returns the external reference code of this user.
	 *
	 * @return the external reference code of this user
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the facebook ID of this user.
	 *
	 * @return the facebook ID of this user
	 */
	@Override
	public long getFacebookId() {
		return model.getFacebookId();
	}

	/**
	 * Returns the failed login attempts of this user.
	 *
	 * @return the failed login attempts of this user
	 */
	@Override
	public int getFailedLoginAttempts() {
		return model.getFailedLoginAttempts();
	}

	/**
	 * Returns <code>true</code> if the user is female.
	 *
	 * @return <code>true</code> if the user is female; <code>false</code>
	 otherwise
	 */
	@Override
	public boolean getFemale()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFemale();
	}

	/**
	 * Returns the first name of this user.
	 *
	 * @return the first name of this user
	 */
	@Override
	public String getFirstName() {
		return model.getFirstName();
	}

	/**
	 * Returns the user's full name.
	 *
	 * @return the user's full name
	 */
	@Override
	public String getFullName() {
		return model.getFullName();
	}

	/**
	 * Returns the user's full name.
	 *
	 * @return the user's full name
	 */
	@Override
	public String getFullName(boolean usePrefix, boolean useSuffix) {
		return model.getFullName(usePrefix, useSuffix);
	}

	/**
	 * Returns the google user ID of this user.
	 *
	 * @return the google user ID of this user
	 */
	@Override
	public String getGoogleUserId() {
		return model.getGoogleUserId();
	}

	/**
	 * Returns the grace login count of this user.
	 *
	 * @return the grace login count of this user
	 */
	@Override
	public int getGraceLoginCount() {
		return model.getGraceLoginCount();
	}

	/**
	 * Returns the greeting of this user.
	 *
	 * @return the greeting of this user
	 */
	@Override
	public String getGreeting() {
		return model.getGreeting();
	}

	@Override
	public Group getGroup() {
		return model.getGroup();
	}

	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	@Override
	public long[] getGroupIds() {
		return model.getGroupIds();
	}

	@Override
	public java.util.List<Group> getGroups() {
		return model.getGroups();
	}

	@Override
	public java.util.List<Group> getInheritedGroups()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getInheritedGroups();
	}

	@Override
	public java.util.List<Role> getInheritedRoles()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getInheritedRoles();
	}

	@Override
	public java.util.List<Group> getInheritedSiteGroups()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getInheritedSiteGroups();
	}

	@Override
	public java.util.List<Role> getInheritedSiteRoles() {
		return model.getInheritedSiteRoles();
	}

	@Override
	public String getInitials() {
		return model.getInitials();
	}

	/**
	 * Returns the job title of this user.
	 *
	 * @return the job title of this user
	 */
	@Override
	public String getJobTitle() {
		return model.getJobTitle();
	}

	/**
	 * Returns the language ID of this user.
	 *
	 * @return the language ID of this user
	 */
	@Override
	public String getLanguageId() {
		return model.getLanguageId();
	}

	/**
	 * Returns the last failed login date of this user.
	 *
	 * @return the last failed login date of this user
	 */
	@Override
	public Date getLastFailedLoginDate() {
		return model.getLastFailedLoginDate();
	}

	/**
	 * Returns the last login date of this user.
	 *
	 * @return the last login date of this user
	 */
	@Override
	public Date getLastLoginDate() {
		return model.getLastLoginDate();
	}

	/**
	 * Returns the last login ip of this user.
	 *
	 * @return the last login ip of this user
	 */
	@Override
	public String getLastLoginIP() {
		return model.getLastLoginIP();
	}

	/**
	 * Returns the last name of this user.
	 *
	 * @return the last name of this user
	 */
	@Override
	public String getLastName() {
		return model.getLastName();
	}

	/**
	 * Returns the ldap server ID of this user.
	 *
	 * @return the ldap server ID of this user
	 */
	@Override
	public long getLdapServerId() {
		return model.getLdapServerId();
	}

	@Override
	public java.util.Locale getLocale() {
		return model.getLocale();
	}

	/**
	 * Returns the lockout of this user.
	 *
	 * @return the lockout of this user
	 */
	@Override
	public boolean getLockout() {
		return model.getLockout();
	}

	/**
	 * Returns the lockout date of this user.
	 *
	 * @return the lockout date of this user
	 */
	@Override
	public Date getLockoutDate() {
		return model.getLockoutDate();
	}

	@Override
	public String getLogin()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getLogin();
	}

	/**
	 * Returns the login date of this user.
	 *
	 * @return the login date of this user
	 */
	@Override
	public Date getLoginDate() {
		return model.getLoginDate();
	}

	/**
	 * Returns the login ip of this user.
	 *
	 * @return the login ip of this user
	 */
	@Override
	public String getLoginIP() {
		return model.getLoginIP();
	}

	/**
	 * Returns <code>true</code> if the user is male.
	 *
	 * @return <code>true</code> if the user is male; <code>false</code>
	 otherwise
	 */
	@Override
	public boolean getMale()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getMale();
	}

	/**
	 * Returns the middle name of this user.
	 *
	 * @return the middle name of this user
	 */
	@Override
	public String getMiddleName() {
		return model.getMiddleName();
	}

	/**
	 * Returns the modified date of this user.
	 *
	 * @return the modified date of this user
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this user.
	 *
	 * @return the mvcc version of this user
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	@Override
	public java.util.List<Group> getMySiteGroups()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getMySiteGroups();
	}

	@Override
	public java.util.List<Group> getMySiteGroups(int max)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getMySiteGroups(max);
	}

	@Override
	public java.util.List<Group> getMySiteGroups(String[] classNames, int max)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getMySiteGroups(classNames, max);
	}

	/**
	 * Returns the open ID of this user.
	 *
	 * @return the open ID of this user
	 */
	@Override
	public String getOpenId() {
		return model.getOpenId();
	}

	@Override
	public long[] getOrganizationIds()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getOrganizationIds();
	}

	@Override
	public long[] getOrganizationIds(boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getOrganizationIds(includeAdministrative);
	}

	@Override
	public java.util.List<Organization> getOrganizations()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getOrganizations();
	}

	@Override
	public java.util.List<Organization> getOrganizations(
			boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getOrganizations(includeAdministrative);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
			boolean includeAdministrative, boolean includeParentOrganizations)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getOrganizations(
			includeAdministrative, includeParentOrganizations);
	}

	@Override
	public java.util.List<Group> getOrganizationsGroups()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getOrganizationsGroups();
	}

	@Override
	public java.util.List<Role> getOrganizationsRoles()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getOrganizationsRoles();
	}

	@Override
	public String getOriginalEmailAddress() {
		return model.getOriginalEmailAddress();
	}

	/**
	 * Returns the password of this user.
	 *
	 * @return the password of this user
	 */
	@Override
	public String getPassword() {
		return model.getPassword();
	}

	/**
	 * Returns the password encrypted of this user.
	 *
	 * @return the password encrypted of this user
	 */
	@Override
	public boolean getPasswordEncrypted() {
		return model.getPasswordEncrypted();
	}

	@Override
	public boolean getPasswordModified() {
		return model.getPasswordModified();
	}

	/**
	 * Returns the password modified date of this user.
	 *
	 * @return the password modified date of this user
	 */
	@Override
	public Date getPasswordModifiedDate() {
		return model.getPasswordModifiedDate();
	}

	@Override
	public PasswordPolicy getPasswordPolicy()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getPasswordPolicy();
	}

	/**
	 * Returns the password reset of this user.
	 *
	 * @return the password reset of this user
	 */
	@Override
	public boolean getPasswordReset() {
		return model.getPasswordReset();
	}

	@Override
	public String getPasswordUnencrypted() {
		return model.getPasswordUnencrypted();
	}

	@Override
	public java.util.List<Phone> getPhones() {
		return model.getPhones();
	}

	/**
	 * Returns the portrait ID of this user.
	 *
	 * @return the portrait ID of this user
	 */
	@Override
	public long getPortraitId() {
		return model.getPortraitId();
	}

	@Override
	public String getPortraitURL(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getPortraitURL(themeDisplay);
	}

	/**
	 * Returns the primary key of this user.
	 *
	 * @return the primary key of this user
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public int getPrivateLayoutsPageCount()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getPrivateLayoutsPageCount();
	}

	@Override
	public int getPublicLayoutsPageCount()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getPublicLayoutsPageCount();
	}

	/**
	 * Returns the reminder query answer of this user.
	 *
	 * @return the reminder query answer of this user
	 */
	@Override
	public String getReminderQueryAnswer() {
		return model.getReminderQueryAnswer();
	}

	/**
	 * Returns the reminder query question of this user.
	 *
	 * @return the reminder query question of this user
	 */
	@Override
	public String getReminderQueryQuestion() {
		return model.getReminderQueryQuestion();
	}

	@Override
	public java.util.Set<String> getReminderQueryQuestions()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getReminderQueryQuestions();
	}

	@Override
	public long[] getRoleIds() {
		return model.getRoleIds();
	}

	@Override
	public java.util.List<Role> getRoles() {
		return model.getRoles();
	}

	/**
	 * Returns the screen name of this user.
	 *
	 * @return the screen name of this user
	 */
	@Override
	public String getScreenName() {
		return model.getScreenName();
	}

	@Override
	public java.util.List<Group> getSiteGroups()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getSiteGroups();
	}

	@Override
	public java.util.List<Group> getSiteGroups(boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getSiteGroups(includeAdministrative);
	}

	@Override
	public java.util.List<Role> getSiteRoles()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getSiteRoles();
	}

	/**
	 * Returns the status of this user.
	 *
	 * @return the status of this user
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	@Override
	public long[] getTeamIds() {
		return model.getTeamIds();
	}

	@Override
	public java.util.List<Team> getTeams() {
		return model.getTeams();
	}

	@Override
	public java.util.TimeZone getTimeZone() {
		return model.getTimeZone();
	}

	/**
	 * Returns the time zone ID of this user.
	 *
	 * @return the time zone ID of this user
	 */
	@Override
	public String getTimeZoneId() {
		return model.getTimeZoneId();
	}

	/**
	 * Returns the type of this user.
	 *
	 * @return the type of this user
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	@Override
	public Date getUnlockDate()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getUnlockDate();
	}

	@Override
	public Date getUnlockDate(PasswordPolicy passwordPolicy) {
		return model.getUnlockDate(passwordPolicy);
	}

	@Override
	public long[] getUserGroupIds() {
		return model.getUserGroupIds();
	}

	@Override
	public java.util.List<UserGroupRole> getUserGroupRoles()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getUserGroupRoles();
	}

	@Override
	public java.util.List<UserGroup> getUserGroups() {
		return model.getUserGroups();
	}

	/**
	 * Returns the user ID of this user.
	 *
	 * @return the user ID of this user
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this user.
	 *
	 * @return the user uuid of this user
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this user.
	 *
	 * @return the uuid of this user
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public java.util.List<Website> getWebsites() {
		return model.getWebsites();
	}

	@Override
	public boolean hasCompanyMx()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.hasCompanyMx();
	}

	@Override
	public boolean hasCompanyMx(String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.hasCompanyMx(emailAddress);
	}

	@Override
	public boolean hasMySites()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.hasMySites();
	}

	@Override
	public boolean hasOrganization() {
		return model.hasOrganization();
	}

	@Override
	public boolean hasPrivateLayouts()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.hasPrivateLayouts();
	}

	@Override
	public boolean hasPublicLayouts()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.hasPublicLayouts();
	}

	@Override
	public boolean hasReminderQuery() {
		return model.hasReminderQuery();
	}

	@Override
	public boolean isActive() {
		return model.isActive();
	}

	/**
	 * Returns <code>true</code> if this user is agreed to terms of use.
	 *
	 * @return <code>true</code> if this user is agreed to terms of use; <code>false</code> otherwise
	 */
	@Override
	public boolean isAgreedToTermsOfUse() {
		return model.isAgreedToTermsOfUse();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #isGuestUser}
	 */
	@Deprecated
	@Override
	public boolean isDefaultUser() {
		return model.isDefaultUser();
	}

	@Override
	public boolean isEmailAddressComplete() {
		return model.isEmailAddressComplete();
	}

	@Override
	public boolean isEmailAddressVerificationComplete() {
		return model.isEmailAddressVerificationComplete();
	}

	/**
	 * Returns <code>true</code> if this user is email address verified.
	 *
	 * @return <code>true</code> if this user is email address verified; <code>false</code> otherwise
	 */
	@Override
	public boolean isEmailAddressVerified() {
		return model.isEmailAddressVerified();
	}

	@Override
	public boolean isFemale()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isFemale();
	}

	@Override
	public boolean isGuestUser() {
		return model.isGuestUser();
	}

	@Override
	public boolean isLayoutsUpdated() {
		return model.isLayoutsUpdated();
	}

	/**
	 * Returns <code>true</code> if this user is lockout.
	 *
	 * @return <code>true</code> if this user is lockout; <code>false</code> otherwise
	 */
	@Override
	public boolean isLockout() {
		return model.isLockout();
	}

	@Override
	public boolean isMale()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isMale();
	}

	@Override
	public boolean isOnDemandUser() {
		return model.isOnDemandUser();
	}

	/**
	 * Returns <code>true</code> if this user is password encrypted.
	 *
	 * @return <code>true</code> if this user is password encrypted; <code>false</code> otherwise
	 */
	@Override
	public boolean isPasswordEncrypted() {
		return model.isPasswordEncrypted();
	}

	@Override
	public boolean isPasswordModified() {
		return model.isPasswordModified();
	}

	/**
	 * Returns <code>true</code> if this user is password reset.
	 *
	 * @return <code>true</code> if this user is password reset; <code>false</code> otherwise
	 */
	@Override
	public boolean isPasswordReset() {
		return model.isPasswordReset();
	}

	@Override
	public boolean isPasswordResetRequired() {
		return model.isPasswordResetRequired();
	}

	@Override
	public boolean isReminderQueryComplete() {
		return model.isReminderQueryComplete();
	}

	@Override
	public boolean isServiceAccountUser() {
		return model.isServiceAccountUser();
	}

	@Override
	public boolean isSetupComplete() {
		return model.isSetupComplete();
	}

	@Override
	public boolean isTermsOfUseComplete() {
		return model.isTermsOfUseComplete();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets whether this user is agreed to terms of use.
	 *
	 * @param agreedToTermsOfUse the agreed to terms of use of this user
	 */
	@Override
	public void setAgreedToTermsOfUse(boolean agreedToTermsOfUse) {
		model.setAgreedToTermsOfUse(agreedToTermsOfUse);
	}

	/**
	 * Sets the comments of this user.
	 *
	 * @param comments the comments of this user
	 */
	@Override
	public void setComments(String comments) {
		model.setComments(comments);
	}

	/**
	 * Sets the company ID of this user.
	 *
	 * @param companyId the company ID of this user
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	@Override
	public void setContact(Contact contact) {
		model.setContact(contact);
	}

	/**
	 * Sets the contact ID of this user.
	 *
	 * @param contactId the contact ID of this user
	 */
	@Override
	public void setContactId(long contactId) {
		model.setContactId(contactId);
	}

	/**
	 * Sets the create date of this user.
	 *
	 * @param createDate the create date of this user
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this user.
	 *
	 * @param ctCollectionId the ct collection ID of this user
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the digest of this user.
	 *
	 * @param digest the digest of this user
	 */
	@Override
	public void setDigest(String digest) {
		model.setDigest(digest);
	}

	/**
	 * Sets the email address of this user.
	 *
	 * @param emailAddress the email address of this user
	 */
	@Override
	public void setEmailAddress(String emailAddress) {
		model.setEmailAddress(emailAddress);
	}

	/**
	 * Sets whether this user is email address verified.
	 *
	 * @param emailAddressVerified the email address verified of this user
	 */
	@Override
	public void setEmailAddressVerified(boolean emailAddressVerified) {
		model.setEmailAddressVerified(emailAddressVerified);
	}

	/**
	 * Sets the external reference code of this user.
	 *
	 * @param externalReferenceCode the external reference code of this user
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the facebook ID of this user.
	 *
	 * @param facebookId the facebook ID of this user
	 */
	@Override
	public void setFacebookId(long facebookId) {
		model.setFacebookId(facebookId);
	}

	/**
	 * Sets the failed login attempts of this user.
	 *
	 * @param failedLoginAttempts the failed login attempts of this user
	 */
	@Override
	public void setFailedLoginAttempts(int failedLoginAttempts) {
		model.setFailedLoginAttempts(failedLoginAttempts);
	}

	/**
	 * Sets the first name of this user.
	 *
	 * @param firstName the first name of this user
	 */
	@Override
	public void setFirstName(String firstName) {
		model.setFirstName(firstName);
	}

	/**
	 * Sets the google user ID of this user.
	 *
	 * @param googleUserId the google user ID of this user
	 */
	@Override
	public void setGoogleUserId(String googleUserId) {
		model.setGoogleUserId(googleUserId);
	}

	/**
	 * Sets the grace login count of this user.
	 *
	 * @param graceLoginCount the grace login count of this user
	 */
	@Override
	public void setGraceLoginCount(int graceLoginCount) {
		model.setGraceLoginCount(graceLoginCount);
	}

	/**
	 * Sets the greeting of this user.
	 *
	 * @param greeting the greeting of this user
	 */
	@Override
	public void setGreeting(String greeting) {
		model.setGreeting(greeting);
	}

	@Override
	public void setGroup(Group group) {
		model.setGroup(group);
	}

	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	@Override
	public void setGroupIds(long[] groupIds) {
		model.setGroupIds(groupIds);
	}

	/**
	 * Sets the job title of this user.
	 *
	 * @param jobTitle the job title of this user
	 */
	@Override
	public void setJobTitle(String jobTitle) {
		model.setJobTitle(jobTitle);
	}

	/**
	 * Sets the language ID of this user.
	 *
	 * @param languageId the language ID of this user
	 */
	@Override
	public void setLanguageId(String languageId) {
		model.setLanguageId(languageId);
	}

	/**
	 * Sets the last failed login date of this user.
	 *
	 * @param lastFailedLoginDate the last failed login date of this user
	 */
	@Override
	public void setLastFailedLoginDate(Date lastFailedLoginDate) {
		model.setLastFailedLoginDate(lastFailedLoginDate);
	}

	/**
	 * Sets the last login date of this user.
	 *
	 * @param lastLoginDate the last login date of this user
	 */
	@Override
	public void setLastLoginDate(Date lastLoginDate) {
		model.setLastLoginDate(lastLoginDate);
	}

	/**
	 * Sets the last login ip of this user.
	 *
	 * @param lastLoginIP the last login ip of this user
	 */
	@Override
	public void setLastLoginIP(String lastLoginIP) {
		model.setLastLoginIP(lastLoginIP);
	}

	/**
	 * Sets the last name of this user.
	 *
	 * @param lastName the last name of this user
	 */
	@Override
	public void setLastName(String lastName) {
		model.setLastName(lastName);
	}

	@Override
	public void setLayoutsUpdated(boolean layoutsUpdated) {
		model.setLayoutsUpdated(layoutsUpdated);
	}

	/**
	 * Sets the ldap server ID of this user.
	 *
	 * @param ldapServerId the ldap server ID of this user
	 */
	@Override
	public void setLdapServerId(long ldapServerId) {
		model.setLdapServerId(ldapServerId);
	}

	/**
	 * Sets whether this user is lockout.
	 *
	 * @param lockout the lockout of this user
	 */
	@Override
	public void setLockout(boolean lockout) {
		model.setLockout(lockout);
	}

	/**
	 * Sets the lockout date of this user.
	 *
	 * @param lockoutDate the lockout date of this user
	 */
	@Override
	public void setLockoutDate(Date lockoutDate) {
		model.setLockoutDate(lockoutDate);
	}

	/**
	 * Sets the login date of this user.
	 *
	 * @param loginDate the login date of this user
	 */
	@Override
	public void setLoginDate(Date loginDate) {
		model.setLoginDate(loginDate);
	}

	/**
	 * Sets the login ip of this user.
	 *
	 * @param loginIP the login ip of this user
	 */
	@Override
	public void setLoginIP(String loginIP) {
		model.setLoginIP(loginIP);
	}

	/**
	 * Sets the middle name of this user.
	 *
	 * @param middleName the middle name of this user
	 */
	@Override
	public void setMiddleName(String middleName) {
		model.setMiddleName(middleName);
	}

	/**
	 * Sets the modified date of this user.
	 *
	 * @param modifiedDate the modified date of this user
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this user.
	 *
	 * @param mvccVersion the mvcc version of this user
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the open ID of this user.
	 *
	 * @param openId the open ID of this user
	 */
	@Override
	public void setOpenId(String openId) {
		model.setOpenId(openId);
	}

	@Override
	public void setOrganizationIds(long[] organizationIds) {
		model.setOrganizationIds(organizationIds);
	}

	/**
	 * Sets the password of this user.
	 *
	 * @param password the password of this user
	 */
	@Override
	public void setPassword(String password) {
		model.setPassword(password);
	}

	/**
	 * Sets whether this user is password encrypted.
	 *
	 * @param passwordEncrypted the password encrypted of this user
	 */
	@Override
	public void setPasswordEncrypted(boolean passwordEncrypted) {
		model.setPasswordEncrypted(passwordEncrypted);
	}

	@Override
	public void setPasswordModified(boolean passwordModified) {
		model.setPasswordModified(passwordModified);
	}

	/**
	 * Sets the password modified date of this user.
	 *
	 * @param passwordModifiedDate the password modified date of this user
	 */
	@Override
	public void setPasswordModifiedDate(Date passwordModifiedDate) {
		model.setPasswordModifiedDate(passwordModifiedDate);
	}

	/**
	 * Sets whether this user is password reset.
	 *
	 * @param passwordReset the password reset of this user
	 */
	@Override
	public void setPasswordReset(boolean passwordReset) {
		model.setPasswordReset(passwordReset);
	}

	@Override
	public void setPasswordUnencrypted(String passwordUnencrypted) {
		model.setPasswordUnencrypted(passwordUnencrypted);
	}

	/**
	 * Sets the portrait ID of this user.
	 *
	 * @param portraitId the portrait ID of this user
	 */
	@Override
	public void setPortraitId(long portraitId) {
		model.setPortraitId(portraitId);
	}

	/**
	 * Sets the primary key of this user.
	 *
	 * @param primaryKey the primary key of this user
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the reminder query answer of this user.
	 *
	 * @param reminderQueryAnswer the reminder query answer of this user
	 */
	@Override
	public void setReminderQueryAnswer(String reminderQueryAnswer) {
		model.setReminderQueryAnswer(reminderQueryAnswer);
	}

	/**
	 * Sets the reminder query question of this user.
	 *
	 * @param reminderQueryQuestion the reminder query question of this user
	 */
	@Override
	public void setReminderQueryQuestion(String reminderQueryQuestion) {
		model.setReminderQueryQuestion(reminderQueryQuestion);
	}

	@Override
	public void setRoleIds(long[] roleIds) {
		model.setRoleIds(roleIds);
	}

	/**
	 * Sets the screen name of this user.
	 *
	 * @param screenName the screen name of this user
	 */
	@Override
	public void setScreenName(String screenName) {
		model.setScreenName(screenName);
	}

	/**
	 * Sets the status of this user.
	 *
	 * @param status the status of this user
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	@Override
	public void setTeamIds(long[] teamIds) {
		model.setTeamIds(teamIds);
	}

	/**
	 * Sets the time zone ID of this user.
	 *
	 * @param timeZoneId the time zone ID of this user
	 */
	@Override
	public void setTimeZoneId(String timeZoneId) {
		model.setTimeZoneId(timeZoneId);
	}

	/**
	 * Sets the type of this user.
	 *
	 * @param type the type of this user
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	@Override
	public void setUserGroupIds(long[] userGroupIds) {
		model.setUserGroupIds(userGroupIds);
	}

	/**
	 * Sets the user ID of this user.
	 *
	 * @param userId the user ID of this user
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this user.
	 *
	 * @param userUuid the user uuid of this user
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this user.
	 *
	 * @param uuid the uuid of this user
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<User, Object>> getAttributeGetterFunctions() {
		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<User, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected UserWrapper wrap(User user) {
		return new UserWrapper(user);
	}

}