/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the User service. Represents a row in the &quot;User_&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see UserModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.UserImpl")
@ProviderType
public interface User extends PersistedModel, UserModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.UserImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<User, Long> USER_ID_ACCESSOR =
		new Accessor<User, Long>() {

			@Override
			public Long get(User user) {
				return user.getUserId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<User> getTypeClass() {
				return User.class;
			}

		};

	public Contact fetchContact();

	public String fetchPortraitURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	/**
	 * Returns the user's addresses.
	 *
	 * @return the user's addresses
	 */
	public java.util.List<Address> getAddresses();

	public java.util.List<Group> getAllGroups()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Role> getAllRoles()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns the user's birth date.
	 *
	 * @return the user's birth date
	 */
	public java.util.Date getBirthday()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns the user's company's mail domain.
	 *
	 * @return the user's company's mail domain
	 */
	public String getCompanyMx()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns the user's associated contact.
	 *
	 * @return the user's associated contact
	 * @see Contact
	 */
	public Contact getContact()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns a digest for the user, incorporating the password.
	 *
	 * @param password a password to incorporate with the digest
	 * @return a digest for the user, incorporating the password
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public String getDigest(String password);

	/**
	 * Returns the user's primary email address, or a blank string if the
	 * address is fake.
	 *
	 * @return the user's primary email address, or a blank string if the
	 address is fake
	 */
	public String getDisplayEmailAddress();

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
	public String getDisplayURL(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException;

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
	public String getDisplayURL(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
			boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns the user's email addresses.
	 *
	 * @return the user's email addresses
	 */
	public java.util.List<EmailAddress> getEmailAddresses();

	/**
	 * Returns <code>true</code> if the user is female.
	 *
	 * @return <code>true</code> if the user is female; <code>false</code>
	 otherwise
	 */
	public boolean getFemale()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns the user's full name.
	 *
	 * @return the user's full name
	 */
	@com.liferay.portal.kernel.bean.AutoEscape
	public String getFullName();

	/**
	 * Returns the user's full name.
	 *
	 * @return the user's full name
	 */
	@com.liferay.portal.kernel.bean.AutoEscape
	public String getFullName(boolean usePrefix, boolean useSuffix);

	public Group getGroup();

	public long getGroupId();

	public long[] getGroupIds();

	public java.util.List<Group> getGroups();

	public java.util.List<Group> getInheritedGroups()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Role> getInheritedRoles()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Group> getInheritedSiteGroups()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Role> getInheritedSiteRoles();

	public String getInitials();

	public java.util.Locale getLocale();

	public String getLogin()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns <code>true</code> if the user is male.
	 *
	 * @return <code>true</code> if the user is male; <code>false</code>
	 otherwise
	 */
	public boolean getMale()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Group> getMySiteGroups()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Group> getMySiteGroups(int max)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Group> getMySiteGroups(String[] classNames, int max)
		throws com.liferay.portal.kernel.exception.PortalException;

	public long[] getOrganizationIds()
		throws com.liferay.portal.kernel.exception.PortalException;

	public long[] getOrganizationIds(boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Organization> getOrganizations()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Organization> getOrganizations(
			boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Organization> getOrganizations(
			boolean includeAdministrative, boolean includeParentOrganizations)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Group> getOrganizationsGroups()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Role> getOrganizationsRoles()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getOriginalEmailAddress();

	public boolean getPasswordModified();

	public PasswordPolicy getPasswordPolicy()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getPasswordUnencrypted();

	public java.util.List<Phone> getPhones();

	public String getPortraitURL(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException;

	public int getPrivateLayoutsPageCount()
		throws com.liferay.portal.kernel.exception.PortalException;

	public int getPublicLayoutsPageCount()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.Set<String> getReminderQueryQuestions()
		throws com.liferay.portal.kernel.exception.PortalException;

	public long[] getRoleIds();

	public java.util.List<Role> getRoles();

	public java.util.List<Group> getSiteGroups()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Group> getSiteGroups(boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<Role> getSiteRoles()
		throws com.liferay.portal.kernel.exception.PortalException;

	public long[] getTeamIds();

	public java.util.List<Team> getTeams();

	public java.util.TimeZone getTimeZone();

	public java.util.Date getUnlockDate()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.Date getUnlockDate(PasswordPolicy passwordPolicy);

	public long[] getUserGroupIds();

	public java.util.List<UserGroupRole> getUserGroupRoles()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<UserGroup> getUserGroups();

	public java.util.List<Website> getWebsites();

	public boolean hasCompanyMx()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean hasCompanyMx(String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean hasMySites()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean hasOrganization();

	public boolean hasPrivateLayouts()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean hasPublicLayouts()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean hasReminderQuery();

	public boolean isActive();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #isGuestUser}
	 */
	@Deprecated
	public boolean isDefaultUser();

	public boolean isEmailAddressComplete();

	public boolean isEmailAddressVerificationComplete();

	public boolean isFemale()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isGuestUser();

	public boolean isLayoutsUpdated();

	public boolean isMale()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isOnDemandUser();

	public boolean isPasswordModified();

	public boolean isPasswordResetRequired();

	public boolean isReminderQueryComplete();

	public boolean isServiceAccountUser();

	public boolean isSetupComplete();

	public boolean isTermsOfUseComplete();

	public void setContact(Contact contact);

	public void setGroup(Group group);

	public void setGroupId(long groupId);

	public void setGroupIds(long[] groupIds);

	public void setLayoutsUpdated(boolean layoutsUpdated);

	public void setOrganizationIds(long[] organizationIds);

	public void setPasswordModified(boolean passwordModified);

	public void setPasswordUnencrypted(String passwordUnencrypted);

	public void setRoleIds(long[] roleIds);

	public void setTeamIds(long[] teamIds);

	public void setUserGroupIds(long[] userGroupIds);

}