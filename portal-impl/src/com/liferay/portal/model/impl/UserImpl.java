/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.security.auth.EmailAddressGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ContactLocalServiceUtil;
import com.liferay.portal.kernel.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.kernel.service.PhoneLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.WebsiteLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.auth.EmailAddressGeneratorFactory;
import com.liferay.portal.util.PropsValues;
import com.liferay.users.admin.kernel.util.UserInitialsGeneratorUtil;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Represents a portal user, providing access to the user's contact information,
 * groups, organizations, teams, user groups, roles, locale, timezone, and more.
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Wesley Gong
 */
public class UserImpl extends UserBaseImpl {

	@Override
	public Contact fetchContact() {
		if (_contact == _NULL_CONTACT) {
			return null;
		}

		if (_contact == null) {
			Contact contact = ContactLocalServiceUtil.fetchContact(
				getContactId());

			if (contact == null) {
				_contact = _NULL_CONTACT;
			}
			else {
				_contact = contact;
			}
		}

		return _contact;
	}

	@Override
	public String fetchPortraitURL(ThemeDisplay themeDisplay) {
		Contact contact = fetchContact();

		if (contact == null) {
			return StringPool.BLANK;
		}

		return UserConstants.getPortraitURL(
			themeDisplay.getPathImage(), contact.isMale(), getPortraitId(),
			getUserUuid());
	}

	/**
	 * Returns the user's addresses.
	 *
	 * @return the user's addresses
	 */
	@Override
	public List<Address> getAddresses() {
		return AddressLocalServiceUtil.getAddresses(
			getCompanyId(), Contact.class.getName(), getContactId());
	}

	@Override
	public List<Group> getAllGroups() throws PortalException {
		return ListUtil.concat(
			getGroups(), getInheritedGroups(), getInheritedSiteGroups(),
			getOrganizationsGroups(), getSiteGroups());
	}

	@Override
	public List<Role> getAllRoles() throws PortalException {
		return ListUtil.concat(
			getInheritedRoles(), getInheritedSiteRoles(),
			getOrganizationsRoles(), getRoles(), getSiteRoles());
	}

	/**
	 * Returns the user's birth date.
	 *
	 * @return the user's birth date
	 */
	@Override
	public Date getBirthday() throws PortalException {
		return getContact().getBirthday();
	}

	/**
	 * Returns the user's company's mail domain.
	 *
	 * @return the user's company's mail domain
	 */
	@Override
	public String getCompanyMx() throws PortalException {
		Company company = CompanyLocalServiceUtil.getCompanyById(
			getCompanyId());

		return company.getMx();
	}

	/**
	 * Returns the user's associated contact.
	 *
	 * @return the user's associated contact
	 * @see    Contact
	 */
	@Override
	public Contact getContact() throws PortalException {
		if ((_contact == null) || (_contact == _NULL_CONTACT)) {
			_contact = ContactLocalServiceUtil.getContact(getContactId());
		}

		return _contact;
	}

	/**
	 * Returns a digest for the user, incorporating the password.
	 *
	 * @param      password a password to incorporate with the digest
	 * @return     a digest for the user, incorporating the password
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getDigest(String password) {
		return DigesterUtil.digestHex(
			Digester.MD5, String.valueOf(getUserId()), Portal.PORTAL_REALM,
			password);
	}

	/**
	 * Returns the user's primary email address, or a blank string if the
	 * address is fake.
	 *
	 * @return the user's primary email address, or a blank string if the
	 *         address is fake
	 */
	@Override
	public String getDisplayEmailAddress() {
		String emailAddress = super.getEmailAddress();

		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		if (emailAddressGenerator.isFake(emailAddress)) {
			emailAddress = StringPool.BLANK;
		}

		return emailAddress;
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
	 * @param  themeDisplay the theme display
	 * @return the user's display URL
	 */
	@Override
	public String getDisplayURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return getDisplayURL(themeDisplay, false);
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
	 * @param  themeDisplay the theme display
	 * @param  privateLayout whether to use the URL of the user's default
	 *         intranet (versus extranet) site home page, if no friendly URL is
	 *         available for the user's profile
	 * @return the user's display URL
	 * @throws PortalException
	 */
	@Override
	public String getDisplayURL(
			ThemeDisplay themeDisplay, boolean privateLayout)
		throws PortalException {

		if (isGuestUser() || (themeDisplay == null)) {
			return StringPool.BLANK;
		}

		String profileFriendlyURL = getProfileFriendlyURL();

		if (profileFriendlyURL != null) {
			return PortalUtil.addPreservedParameters(
				themeDisplay,
				StringBundler.concat(
					themeDisplay.getPortalURL(), PortalUtil.getPathContext(),
					profileFriendlyURL));
		}

		Group group = getGroup();

		return group.getDisplayURL(themeDisplay, privateLayout);
	}

	/**
	 * Returns the user's email addresses.
	 *
	 * @return the user's email addresses
	 */
	@Override
	public List<EmailAddress> getEmailAddresses() {
		return EmailAddressLocalServiceUtil.getEmailAddresses(
			getCompanyId(), Contact.class.getName(), getContactId());
	}

	/**
	 * Returns <code>true</code> if the user is female.
	 *
	 * @return <code>true</code> if the user is female; <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean getFemale() throws PortalException {
		return !getMale();
	}

	/**
	 * Returns the user's full name.
	 *
	 * @return the user's full name
	 */
	@AutoEscape
	@Override
	public String getFullName() {
		return getFullName(false, false);
	}

	/**
	 * Returns the user's full name.
	 *
	 * @return the user's full name
	 */
	@AutoEscape
	@Override
	public String getFullName(boolean usePrefix, boolean useSuffix) {
		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		long prefixListTypeId = 0;

		if (usePrefix) {
			Contact contact = fetchContact();

			if (contact != null) {
				prefixListTypeId = contact.getPrefixListTypeId();
			}
		}

		long suffixListTypeId = 0;

		if (useSuffix) {
			Contact contact = fetchContact();

			if (contact != null) {
				suffixListTypeId = contact.getSuffixListTypeId();
			}
		}

		return fullNameGenerator.getLocalizedFullName(
			getFirstName(), getMiddleName(), getLastName(), getLocale(),
			prefixListTypeId, suffixListTypeId);
	}

	@Override
	public Group getGroup() {
		if (_group == null) {
			_group = GroupLocalServiceUtil.fetchGroup(getGroupId());
		}

		return _group;
	}

	@Override
	public long getGroupId() {
		if (_groupId == -1) {
			_group = GroupLocalServiceUtil.fetchUserGroup(
				getCompanyId(), getUserId());

			if (_group != null) {
				_groupId = _group.getGroupId();

				groupIdUpdateEntityCacheConsumer.accept(_groupId);
			}
		}

		return _groupId;
	}

	@Override
	public long[] getGroupIds() {
		if (_groupIds == null) {
			_groupIds = UserLocalServiceUtil.getGroupPrimaryKeys(getUserId());
		}

		return _groupIds;
	}

	@Override
	public List<Group> getGroups() {
		return GroupLocalServiceUtil.getUserGroups(getUserId());
	}

	@Override
	public List<Group> getInheritedGroups() throws PortalException {
		return GroupLocalServiceUtil.getUserGroupsGroups(getUserGroups());
	}

	@Override
	public List<Role> getInheritedRoles() throws PortalException {
		Set<Role> roles = new HashSet<>();

		for (Group group :
				ListUtil.filter(
					getAllGroups(),
					group -> RoleLocalServiceUtil.hasGroupRoles(
						group.getGroupId()))) {

			roles.addAll(
				RoleLocalServiceUtil.getGroupRoles(group.getGroupId()));
		}

		return ListUtil.fromCollection(roles);
	}

	@Override
	public List<Group> getInheritedSiteGroups() throws PortalException {
		Set<Group> groups = new HashSet<>();

		groups.addAll(
			GroupLocalServiceUtil.getUserGroupsRelatedGroups(getUserGroups()));
		groups.addAll(_getOrganizationRelatedGroups());

		return ListUtil.fromCollection(groups);
	}

	@Override
	public List<Role> getInheritedSiteRoles() {
		return TransformUtil.transform(
			UserGroupGroupRoleLocalServiceUtil.getUserGroupGroupRolesByUser(
				getUserId()),
			UserGroupGroupRole::getRole);
	}

	@Override
	public String getInitials() {
		String initials = UserInitialsGeneratorUtil.getInitials(this);

		if (initials == null) {
			String firstInitial = StringUtil.shorten(getFirstName(), 1);
			String lastInitial = StringUtil.shorten(getLastName(), 1);

			initials = StringUtil.toUpperCase(firstInitial.concat(lastInitial));
		}

		return initials;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public String getLogin() throws PortalException {
		String login = null;

		Company company = CompanyLocalServiceUtil.getCompanyById(
			getCompanyId());

		String authType = company.getAuthType();

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			login = getEmailAddress();
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			login = getScreenName();
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			login = String.valueOf(getUserId());
		}

		return login;
	}

	/**
	 * Returns <code>true</code> if the user is male.
	 *
	 * @return <code>true</code> if the user is male; <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean getMale() throws PortalException {
		return getContact().getMale();
	}

	@Override
	public List<Group> getMySiteGroups() throws PortalException {
		return getMySiteGroups(null, QueryUtil.ALL_POS);
	}

	@Override
	public List<Group> getMySiteGroups(int max) throws PortalException {
		return getMySiteGroups(null, max);
	}

	@Override
	public List<Group> getMySiteGroups(String[] classNames, int max)
		throws PortalException {

		return GroupServiceUtil.getUserSitesGroups(
			getUserId(), classNames, max);
	}

	@Override
	public long[] getOrganizationIds() throws PortalException {
		if (_organizationIds == null) {
			_organizationIds = UserLocalServiceUtil.getOrganizationPrimaryKeys(
				getUserId());
		}

		return _organizationIds;
	}

	@Override
	public long[] getOrganizationIds(boolean includeAdministrative)
		throws PortalException {

		if (!includeAdministrative) {
			return getOrganizationIds();
		}

		return OrganizationLocalServiceUtil.getUserOrganizationIds(
			getUserId(), true);
	}

	@Override
	public List<Organization> getOrganizations() throws PortalException {
		return getOrganizations(false, false);
	}

	@Override
	public List<Organization> getOrganizations(boolean includeAdministrative)
		throws PortalException {

		return getOrganizations(includeAdministrative, false);
	}

	@Override
	public List<Organization> getOrganizations(
			boolean includeAdministrative, boolean includeParentOrganizations)
		throws PortalException {

		List<Organization> organizations =
			OrganizationLocalServiceUtil.getUserOrganizations(
				getUserId(), includeAdministrative);

		if (includeParentOrganizations) {
			organizations.addAll(_getParentOrganizations(organizations));
		}

		return organizations;
	}

	@Override
	public List<Group> getOrganizationsGroups() throws PortalException {
		return GroupLocalServiceUtil.getOrganizationsGroups(
			getOrganizations(
				false, !PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT));
	}

	@Override
	public List<Role> getOrganizationsRoles() throws PortalException {
		return TransformUtil.transform(
			ListUtil.filter(
				getUserGroupRoles(), UserGroupRole::hasOrganizationRole),
			UserGroupRole::getRole);
	}

	@Override
	public String getOriginalEmailAddress() {
		return getColumnOriginalValue("emailAddress");
	}

	@Override
	public boolean getPasswordModified() {
		return _passwordModified;
	}

	@Override
	public PasswordPolicy getPasswordPolicy() throws PortalException {
		if (_passwordPolicy == null) {
			_passwordPolicy =
				PasswordPolicyLocalServiceUtil.getPasswordPolicyByUser(this);
		}

		return _passwordPolicy;
	}

	@Override
	public String getPasswordUnencrypted() {
		return _passwordUnencrypted;
	}

	@Override
	public List<Phone> getPhones() {
		return PhoneLocalServiceUtil.getPhones(
			getCompanyId(), Contact.class.getName(), getContactId());
	}

	@Override
	public String getPortraitURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return UserConstants.getPortraitURL(
			themeDisplay.getPathImage(), isMale(), getPortraitId(),
			getUserUuid());
	}

	@Override
	public int getPrivateLayoutsPageCount() throws PortalException {
		return LayoutLocalServiceUtil.getLayoutsCount(this, true);
	}

	@Override
	public int getPublicLayoutsPageCount() throws PortalException {
		return LayoutLocalServiceUtil.getLayoutsCount(this, false);
	}

	@Override
	public Set<String> getReminderQueryQuestions() throws PortalException {
		Set<String> questions = new TreeSet<>();

		List<Organization> organizations =
			OrganizationLocalServiceUtil.getUserOrganizations(getUserId());

		for (Organization organization : organizations) {
			Set<String> organizationQuestions =
				organization.getReminderQueryQuestions(getLanguageId());

			if (organizationQuestions.isEmpty()) {
				Organization parentOrganization =
					organization.getParentOrganization();

				while (organizationQuestions.isEmpty() &&
					   (parentOrganization != null)) {

					organizationQuestions =
						parentOrganization.getReminderQueryQuestions(
							getLanguageId());

					parentOrganization =
						parentOrganization.getParentOrganization();
				}
			}

			questions.addAll(organizationQuestions);
		}

		if (questions.isEmpty()) {
			Set<String> defaultQuestions = SetUtil.fromArray(
				PrefsPropsUtil.getStringArray(
					getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_QUESTIONS,
					StringPool.COMMA));

			questions.addAll(defaultQuestions);
		}

		return questions;
	}

	@Override
	public long[] getRoleIds() {
		if (_roleIds == null) {
			_roleIds = UserLocalServiceUtil.getRolePrimaryKeys(getUserId());
		}

		return _roleIds;
	}

	@Override
	public List<Role> getRoles() {
		return RoleLocalServiceUtil.getUserRoles(getUserId());
	}

	@Override
	public List<Group> getSiteGroups() throws PortalException {
		return getSiteGroups(false);
	}

	@Override
	public List<Group> getSiteGroups(boolean includeAdministrative)
		throws PortalException {

		return GroupLocalServiceUtil.getUserSitesGroups(
			getUserId(), includeAdministrative);
	}

	@Override
	public List<Role> getSiteRoles() throws PortalException {
		return TransformUtil.transform(
			ListUtil.filter(getUserGroupRoles(), UserGroupRole::hasSiteRole),
			UserGroupRole::getRole);
	}

	@Override
	public long[] getTeamIds() {
		if (_teamIds == null) {
			_teamIds = UserLocalServiceUtil.getTeamPrimaryKeys(getUserId());
		}

		return _teamIds;
	}

	@Override
	public List<Team> getTeams() {
		return TeamLocalServiceUtil.getUserTeams(getUserId());
	}

	@Override
	public TimeZone getTimeZone() {
		return _timeZone;
	}

	@Override
	public Date getUnlockDate() throws PortalException {
		return getUnlockDate(getPasswordPolicy());
	}

	@Override
	public Date getUnlockDate(PasswordPolicy passwordPolicy) {
		Date lockoutDate = getLockoutDate();

		return new Date(
			lockoutDate.getTime() +
				(passwordPolicy.getLockoutDuration() * 1000));
	}

	@Override
	public long[] getUserGroupIds() {
		if (_userGroupIds == null) {
			_userGroupIds = UserLocalServiceUtil.getUserGroupPrimaryKeys(
				getUserId());
		}

		return _userGroupIds;
	}

	@Override
	public List<UserGroupRole> getUserGroupRoles() throws PortalException {
		return UserGroupRoleLocalServiceUtil.getUserGroupRoles(getUserId());
	}

	@Override
	public List<UserGroup> getUserGroups() {
		return UserGroupLocalServiceUtil.getUserUserGroups(getUserId());
	}

	@Override
	public List<Website> getWebsites() {
		return WebsiteLocalServiceUtil.getWebsites(
			getCompanyId(), Contact.class.getName(), getContactId());
	}

	@Override
	public boolean hasCompanyMx() throws PortalException {
		return hasCompanyMx(getEmailAddress());
	}

	@Override
	public boolean hasCompanyMx(String emailAddress) throws PortalException {
		if (Validator.isNull(emailAddress)) {
			return false;
		}

		Company company = CompanyLocalServiceUtil.getCompanyById(
			getCompanyId());

		return company.hasCompanyMx(emailAddress);
	}

	@Override
	public boolean hasMySites() throws PortalException {
		if (isGuestUser()) {
			return false;
		}

		if ((PrefsPropsUtil.getBoolean(
				getCompanyId(),
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED) ||
			 PrefsPropsUtil.getBoolean(
				 getCompanyId(),
				 PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED)) &&
			(getUserId() == PrincipalThreadLocal.getUserId())) {

			return true;
		}

		List<Group> groups = getMySiteGroups(1);

		return !groups.isEmpty();
	}

	@Override
	public boolean hasOrganization() {
		return OrganizationLocalServiceUtil.hasUserOrganizations(getUserId());
	}

	@Override
	public boolean hasPrivateLayouts() throws PortalException {
		return LayoutLocalServiceUtil.hasLayouts(this, true);
	}

	@Override
	public boolean hasPublicLayouts() throws PortalException {
		return LayoutLocalServiceUtil.hasLayouts(this, false);
	}

	@Override
	public boolean hasReminderQuery() {
		if (Validator.isNotNull(getReminderQueryQuestion()) &&
			Validator.isNotNull(getReminderQueryAnswer())) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isActive() {
		if (getStatus() == WorkflowConstants.STATUS_APPROVED) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #isGuestUser}
	 */
	@Deprecated
	@Override
	public boolean isDefaultUser() {
		return isGuestUser();
	}

	@Override
	public boolean isEmailAddressComplete() {
		if (isGuestUser()) {
			return true;
		}

		if (Validator.isNull(getEmailAddress()) ||
			(PropsValues.USERS_EMAIL_ADDRESS_REQUIRED &&
			 Validator.isNull(getDisplayEmailAddress()))) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isEmailAddressVerificationComplete() {
		if (isGuestUser() || isEmailAddressVerified() ||
			isServiceAccountUser()) {

			return true;
		}

		boolean emailAddressVerificationRequired = false;

		try {
			Company company = CompanyLocalServiceUtil.getCompany(
				getCompanyId());

			emailAddressVerificationRequired = company.isStrangersVerify();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return !emailAddressVerificationRequired;
	}

	@Override
	public boolean isFemale() throws PortalException {
		return getFemale();
	}

	@Override
	public boolean isGuestUser() {
		if (getType() == UserConstants.TYPE_GUEST) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isMale() throws PortalException {
		return getMale();
	}

	@Override
	public boolean isOnDemandUser() {
		if (getType() == UserConstants.TYPE_ON_DEMAND_USER) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isPasswordModified() {
		return _passwordModified;
	}

	@Override
	public boolean isPasswordResetRequired() {
		if (isGuestUser() || !isPasswordReset() || isServiceAccountUser()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isReminderQueryComplete() {
		if (isGuestUser() || isOnDemandUser()) {
			return true;
		}

		if (PrefsPropsUtil.getBoolean(
				getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_ENABLED,
				PropsValues.USERS_REMINDER_QUERIES_ENABLED) &&
			(Validator.isNull(getReminderQueryQuestion()) ||
			 Validator.isNull(getReminderQueryAnswer()))) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isServiceAccountUser() {
		if ((getType() == UserConstants.TYPE_DEFAULT_SERVICE_ACCOUNT) ||
			(getType() == UserConstants.TYPE_SERVICE_ACCOUNT)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isSetupComplete() {
		if (isGuestUser()) {
			return true;
		}

		if (isEmailAddressComplete() && isEmailAddressVerificationComplete() &&
			!_isRequirePasswordReset() && isReminderQueryComplete() &&
			isTermsOfUseComplete()) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isTermsOfUseComplete() {
		if (isGuestUser() || isAgreedToTermsOfUse()) {
			return true;
		}

		boolean termsOfUseRequired = PrefsPropsUtil.getBoolean(
			getCompanyId(), PropsKeys.TERMS_OF_USE_REQUIRED,
			PropsValues.TERMS_OF_USE_REQUIRED);

		return !termsOfUseRequired;
	}

	@Override
	public void setContact(Contact contact) {
		_contact = contact;
	}

	/**
	 * Sets the user's digest.
	 *
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void setDigest(String digest) {
		super.setDigest(digest);
	}

	@Override
	public void setGroup(Group group) {
		_group = group;
	}

	@Override
	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;
	}

	@Override
	public void setLanguageId(String languageId) {
		if (isGuestUser()) {
			_locale = LocaleUtil.fromLanguageId(languageId, false);
		}
		else {
			_locale = LocaleUtil.fromLanguageId(languageId);
		}

		super.setLanguageId(LocaleUtil.toLanguageId(_locale));
	}

	@Override
	public void setOrganizationIds(long[] organizationIds) {
		_organizationIds = organizationIds;
	}

	@Override
	public void setPasswordModified(boolean passwordModified) {
		_passwordModified = passwordModified;
	}

	@Override
	public void setPasswordUnencrypted(String passwordUnencrypted) {
		_passwordUnencrypted = passwordUnencrypted;
	}

	@Override
	public void setRoleIds(long[] roleIds) {
		_roleIds = roleIds;
	}

	@Override
	public void setTeamIds(long[] teamIds) {
		_teamIds = teamIds;
	}

	@Override
	public void setTimeZoneId(String timeZoneId) {
		if (Validator.isNull(timeZoneId)) {
			TimeZone defaultTimeZone = TimeZoneUtil.getDefault();

			timeZoneId = defaultTimeZone.getID();
		}

		_timeZone = TimeZoneUtil.getTimeZone(timeZoneId);

		super.setTimeZoneId(timeZoneId);
	}

	@Override
	public void setUserGroupIds(long[] userGroupIds) {
		_userGroupIds = userGroupIds;
	}

	protected String getProfileFriendlyURL() {
		if (!_HAS_USERS_PROFILE_FRIENDLY_URL) {
			return null;
		}

		String normalizedScreenName = FriendlyURLNormalizerUtil.normalize(
			getScreenName());

		return StringUtil.replace(
			PropsValues.USERS_PROFILE_FRIENDLY_URL,
			new String[] {"${liferay:screenName}", "${liferay:userId}"},
			new String[] {
				HtmlUtil.escapeURL(normalizedScreenName),
				String.valueOf(getUserId())
			});
	}

	private List<Group> _getOrganizationRelatedGroups() throws PortalException {
		List<Organization> organizations = getOrganizations(
			false, !PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT);

		if (organizations.isEmpty()) {
			return Collections.emptyList();
		}

		return GroupLocalServiceUtil.getOrganizationsRelatedGroups(
			organizations);
	}

	private List<Organization> _getParentOrganizations(
			List<Organization> organizations)
		throws PortalException {

		return TransformUtil.transform(
			organizations,
			organization -> {
				Organization parentOrganization =
					organization.getParentOrganization();

				if ((parentOrganization == null) ||
					organizations.contains(parentOrganization)) {

					return null;
				}

				return parentOrganization;
			});
	}

	private boolean _isRequirePasswordReset() {
		if (!isPasswordReset() ||
			((_passwordPolicy != null) && !_passwordPolicy.isChangeable())) {

			return false;
		}

		return true;
	}

	private static final boolean _HAS_USERS_PROFILE_FRIENDLY_URL =
		Validator.isNotNull(PropsValues.USERS_PROFILE_FRIENDLY_URL);

	private static final Contact _NULL_CONTACT = new ContactImpl();

	private static final Log _log = LogFactoryUtil.getLog(UserImpl.class);

	private Contact _contact;
	private Group _group;

	@CacheField(permanent = true, propagateToInterface = true)
	private long _groupId = -1;

	private long[] _groupIds;
	private Locale _locale;
	private long[] _organizationIds;
	private boolean _passwordModified;
	private PasswordPolicy _passwordPolicy;
	private String _passwordUnencrypted;
	private long[] _roleIds;
	private long[] _teamIds;
	private TimeZone _timeZone;
	private long[] _userGroupIds;

}