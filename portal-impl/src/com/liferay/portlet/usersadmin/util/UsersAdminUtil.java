/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.usersadmin.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.AddressServiceUtil;
import com.liferay.portal.kernel.service.CountryServiceUtil;
import com.liferay.portal.kernel.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.kernel.service.EmailAddressServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrgLaborLocalServiceUtil;
import com.liferay.portal.kernel.service.OrgLaborServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PhoneLocalServiceUtil;
import com.liferay.portal.kernel.service.PhoneServiceUtil;
import com.liferay.portal.kernel.service.RegionServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.WebsiteLocalServiceUtil;
import com.liferay.portal.kernel.service.WebsiteServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.service.permission.UserGroupRolePermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.kernel.util.comparator.GroupTypeComparator;
import com.liferay.portal.kernel.util.comparator.OrganizationNameComparator;
import com.liferay.portal.kernel.util.comparator.OrganizationTypeComparator;
import com.liferay.portal.kernel.util.comparator.RoleDescriptionComparator;
import com.liferay.portal.kernel.util.comparator.RoleNameComparator;
import com.liferay.portal.kernel.util.comparator.RoleTypeComparator;
import com.liferay.portal.kernel.util.comparator.UserEmailAddressComparator;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.util.comparator.UserGroupDescriptionComparator;
import com.liferay.portal.kernel.util.comparator.UserGroupNameComparator;
import com.liferay.portal.kernel.util.comparator.UserJobTitleComparator;
import com.liferay.portal.kernel.util.comparator.UserLastLoginDateComparator;
import com.liferay.portal.kernel.util.comparator.UserLastNameComparator;
import com.liferay.portal.kernel.util.comparator.UserScreenNameComparator;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Julio Camarero
 */
public class UsersAdminUtil {

	public static final String CUSTOM_QUESTION = "write-my-own-question";

	public static final Accessor<Organization, String>
		ORGANIZATION_COUNTRY_NAME_ACCESSOR =
			new Accessor<Organization, String>() {

				@Override
				public String get(Organization organization) {
					Address address = organization.getAddress();

					Country country = address.getCountry();

					String countryName = country.getName(
						LocaleThreadLocal.getThemeDisplayLocale());

					if (Validator.isNull(countryName)) {
						country = CountryServiceUtil.fetchCountry(
							organization.getCountryId());

						if (country != null) {
							countryName = country.getName(
								LocaleThreadLocal.getThemeDisplayLocale());
						}
					}

					return countryName;
				}

				@Override
				public Class<String> getAttributeClass() {
					return String.class;
				}

				@Override
				public Class<Organization> getTypeClass() {
					return Organization.class;
				}

			};

	public static final Accessor<Organization, String>
		ORGANIZATION_REGION_NAME_ACCESSOR =
			new Accessor<Organization, String>() {

				@Override
				public String get(Organization organization) {
					Address address = organization.getAddress();

					Region region = address.getRegion();

					String regionName = region.getName();

					if (Validator.isNull(regionName)) {
						region = RegionServiceUtil.fetchRegion(
							organization.getRegionId());

						if (region != null) {
							regionName = LanguageUtil.get(
								LocaleThreadLocal.getThemeDisplayLocale(),
								region.getName());
						}
					}

					return regionName;
				}

				@Override
				public Class<String> getAttributeClass() {
					return String.class;
				}

				@Override
				public Class<Organization> getTypeClass() {
					return Organization.class;
				}

			};

	public static final Accessor<UserGroupGroupRole, Long>
		USER_GROUP_GROUP_ROLE_ID_ACCESSOR =
			new Accessor<UserGroupGroupRole, Long>() {

				@Override
				public Long get(UserGroupGroupRole userGroupGroupRole) {
					Role role = RoleLocalServiceUtil.fetchRole(
						userGroupGroupRole.getRoleId());

					if (role == null) {
						return 0L;
					}

					return role.getRoleId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<UserGroupGroupRole> getTypeClass() {
					return UserGroupGroupRole.class;
				}

			};

	public static final Accessor<UserGroupGroupRole, String>
		USER_GROUP_GROUP_ROLE_TITLE_ACCESSOR =
			new Accessor<UserGroupGroupRole, String>() {

				@Override
				public String get(UserGroupGroupRole userGroupGroupRole) {
					Role role = RoleLocalServiceUtil.fetchRole(
						userGroupGroupRole.getRoleId());

					if (role == null) {
						return StringPool.BLANK;
					}

					return role.getTitle(
						LocaleThreadLocal.getThemeDisplayLocale());
				}

				@Override
				public Class<String> getAttributeClass() {
					return String.class;
				}

				@Override
				public Class<UserGroupGroupRole> getTypeClass() {
					return UserGroupGroupRole.class;
				}

			};

	public static final Accessor<UserGroupRole, Long>
		USER_GROUP_ROLE_ID_ACCESSOR = new Accessor<UserGroupRole, Long>() {

			@Override
			public Long get(UserGroupRole userGroupRole) {
				Role role = RoleLocalServiceUtil.fetchRole(
					userGroupRole.getRoleId());

				if (role == null) {
					return 0L;
				}

				return role.getRoleId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<UserGroupRole> getTypeClass() {
				return UserGroupRole.class;
			}

		};

	public static final Accessor<UserGroupRole, String>
		USER_GROUP_ROLE_TITLE_ACCESSOR = new Accessor<UserGroupRole, String>() {

			@Override
			public String get(UserGroupRole userGroupRole) {
				Role role = RoleLocalServiceUtil.fetchRole(
					userGroupRole.getRoleId());

				if (role == null) {
					return StringPool.BLANK;
				}

				return role.getTitle(LocaleThreadLocal.getThemeDisplayLocale());
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<UserGroupRole> getTypeClass() {
				return UserGroupRole.class;
			}

		};

	public static void addPortletBreadcrumbEntries(
			Organization organization, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/users_admin/organizations_view_tree"
		).setParameter(
			"screenNavigationCategoryKey", "organizations"
		).setParameter(
			"usersListView", "tree"
		).buildPortletURL();

		List<Organization> ancestorOrganizations = organization.getAncestors();

		Collections.reverse(ancestorOrganizations);

		for (Organization ancestorOrganization : ancestorOrganizations) {
			portletURL.setParameter(
				"organizationId",
				String.valueOf(ancestorOrganization.getOrganizationId()));

			if (OrganizationPermissionUtil.contains(
					permissionChecker, ancestorOrganization, ActionKeys.VIEW)) {

				PortalUtil.addPortletBreadcrumbEntry(
					httpServletRequest, ancestorOrganization.getName(),
					portletURL.toString());
			}
		}

		Organization unescapedOrganization = organization.toUnescapedModel();

		portletURL.setParameter(
			"organizationId",
			String.valueOf(unescapedOrganization.getOrganizationId()));

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, unescapedOrganization.getName(), null);
	}

	public static long[] addRequiredRoles(long userId, long[] roleIds)
		throws PortalException {

		return addRequiredRoles(UserLocalServiceUtil.getUser(userId), roleIds);
	}

	public static long[] addRequiredRoles(User user, long[] roleIds)
		throws PortalException {

		if (user.isGuestUser()) {
			return removeRequiredRoles(user, roleIds);
		}

		Role administratorRole = RoleLocalServiceUtil.getRole(
			user.getCompanyId(), RoleConstants.ADMINISTRATOR);

		if (!ArrayUtil.contains(roleIds, administratorRole.getRoleId())) {
			long[] administratorUserIds = UserLocalServiceUtil.getRoleUserIds(
				administratorRole.getRoleId(), UserConstants.TYPE_REGULAR);

			if (ArrayUtil.contains(administratorUserIds, user.getUserId()) &&
				(administratorUserIds.length == 1)) {

				roleIds = ArrayUtil.append(
					roleIds, administratorRole.getRoleId());
			}
		}

		Role userRole = RoleLocalServiceUtil.getRole(
			user.getCompanyId(), RoleConstants.USER);

		if (!ArrayUtil.contains(roleIds, userRole.getRoleId())) {
			roleIds = ArrayUtil.append(roleIds, userRole.getRoleId());
		}

		return roleIds;
	}

	public static List<Role> filterGroupRoles(
			PermissionChecker permissionChecker, long groupId, List<Role> roles)
		throws PortalException {

		List<Role> filteredGroupRoles = ListUtil.copy(roles);

		Iterator<Role> iterator = filteredGroupRoles.iterator();

		while (iterator.hasNext()) {
			Role groupRole = iterator.next();

			String roleName = groupRole.getName();

			if (roleName.equals(RoleConstants.ORGANIZATION_USER) ||
				roleName.equals(RoleConstants.SITE_MEMBER)) {

				iterator.remove();
			}
		}

		if (permissionChecker.isCompanyAdmin() ||
			permissionChecker.isGroupOwner(groupId)) {

			return filteredGroupRoles;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (!GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.ASSIGN_USER_ROLES) &&
			!OrganizationPermissionUtil.contains(
				permissionChecker, group.getOrganizationId(),
				ActionKeys.ASSIGN_USER_ROLES)) {

			return Collections.emptyList();
		}

		iterator = filteredGroupRoles.iterator();

		while (iterator.hasNext()) {
			Role groupRole = iterator.next();

			String roleName = groupRole.getName();

			if (roleName.equals(RoleConstants.ORGANIZATION_ADMINISTRATOR) ||
				roleName.equals(RoleConstants.ORGANIZATION_OWNER) ||
				roleName.equals(RoleConstants.SITE_ADMINISTRATOR) ||
				roleName.equals(RoleConstants.SITE_OWNER) ||
				!RolePermissionUtil.contains(
					permissionChecker, groupId, groupRole.getRoleId(),
					ActionKeys.ASSIGN_MEMBERS)) {

				iterator.remove();
			}
		}

		return filteredGroupRoles;
	}

	public static List<Group> filterGroups(
			PermissionChecker permissionChecker, List<Group> groups)
		throws PortalException {

		if (permissionChecker.isCompanyAdmin()) {
			return groups;
		}

		List<Group> filteredGroups = ListUtil.copy(groups);

		Iterator<Group> iterator = filteredGroups.iterator();

		while (iterator.hasNext()) {
			Group group = iterator.next();

			if (!GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.ASSIGN_MEMBERS)) {

				iterator.remove();
			}
		}

		return filteredGroups;
	}

	public static List<Organization> filterOrganizations(
			PermissionChecker permissionChecker,
			List<Organization> organizations)
		throws PortalException {

		if (permissionChecker.isCompanyAdmin()) {
			return organizations;
		}

		List<Organization> filteredOrganizations = ListUtil.copy(organizations);

		Iterator<Organization> iterator = filteredOrganizations.iterator();

		while (iterator.hasNext()) {
			Organization organization = iterator.next();

			if (!OrganizationPermissionUtil.contains(
					permissionChecker, organization,
					ActionKeys.ASSIGN_MEMBERS)) {

				iterator.remove();
			}
		}

		return filteredOrganizations;
	}

	public static List<Role> filterRoles(
		PermissionChecker permissionChecker, List<Role> roles) {

		List<Role> filteredRoles = ListUtil.copy(roles);

		Iterator<Role> iterator = filteredRoles.iterator();

		while (iterator.hasNext()) {
			Role role = iterator.next();

			String roleName = role.getName();

			if (roleName.equals(RoleConstants.GUEST) ||
				roleName.equals(RoleConstants.ORGANIZATION_USER) ||
				roleName.equals(RoleConstants.OWNER) ||
				roleName.equals(RoleConstants.SITE_MEMBER) ||
				roleName.equals(RoleConstants.USER)) {

				iterator.remove();
			}
		}

		if (permissionChecker.isCompanyAdmin()) {
			return filteredRoles;
		}

		iterator = filteredRoles.iterator();

		while (iterator.hasNext()) {
			Role role = iterator.next();

			if (!RolePermissionUtil.contains(
					permissionChecker, role.getRoleId(),
					ActionKeys.ASSIGN_MEMBERS)) {

				iterator.remove();
			}
		}

		return filteredRoles;
	}

	public static long[] filterUnsetGroupUserIds(
			PermissionChecker permissionChecker, long groupId, long[] userIds)
		throws PortalException {

		long[] filteredUserIds = userIds;

		for (long userId : userIds) {
			if (SiteMembershipPolicyUtil.isMembershipProtected(
					permissionChecker, userId, groupId)) {

				filteredUserIds = ArrayUtil.remove(filteredUserIds, userId);
			}
		}

		return filteredUserIds;
	}

	public static long[] filterUnsetOrganizationUserIds(
			PermissionChecker permissionChecker, long organizationId,
			long[] userIds)
		throws PortalException {

		long[] filteredUserIds = userIds;

		for (long userId : userIds) {
			if (OrganizationMembershipPolicyUtil.isMembershipProtected(
					permissionChecker, userId, organizationId)) {

				filteredUserIds = ArrayUtil.remove(filteredUserIds, userId);
			}
		}

		return filteredUserIds;
	}

	public static List<UserGroupRole> filterUserGroupRoles(
			PermissionChecker permissionChecker,
			List<UserGroupRole> userGroupRoles)
		throws PortalException {

		List<UserGroupRole> filteredUserGroupRoles = ListUtil.copy(
			userGroupRoles);

		Iterator<UserGroupRole> iterator = filteredUserGroupRoles.iterator();

		while (iterator.hasNext()) {
			UserGroupRole userGroupRole = iterator.next();

			Role role = userGroupRole.getRole();

			String roleName = role.getName();

			if (roleName.equals(RoleConstants.ORGANIZATION_USER) ||
				roleName.equals(RoleConstants.SITE_MEMBER)) {

				iterator.remove();
			}
		}

		if (permissionChecker.isCompanyAdmin()) {
			return filteredUserGroupRoles;
		}

		iterator = filteredUserGroupRoles.iterator();

		while (iterator.hasNext()) {
			UserGroupRole userGroupRole = iterator.next();

			if (!UserGroupRolePermissionUtil.contains(
					permissionChecker, userGroupRole.getGroupId(),
					userGroupRole.getRoleId())) {

				iterator.remove();
			}
		}

		return filteredUserGroupRoles;
	}

	public static List<UserGroup> filterUserGroups(
		PermissionChecker permissionChecker, List<UserGroup> userGroups) {

		if (permissionChecker.isCompanyAdmin()) {
			return userGroups;
		}

		List<UserGroup> filteredUserGroups = ListUtil.copy(userGroups);

		Iterator<UserGroup> iterator = filteredUserGroups.iterator();

		while (iterator.hasNext()) {
			UserGroup userGroup = iterator.next();

			if (!UserGroupPermissionUtil.contains(
					permissionChecker, userGroup.getUserGroupId(),
					ActionKeys.ASSIGN_MEMBERS)) {

				iterator.remove();
			}
		}

		return filteredUserGroups;
	}

	public static List<Address> getAddresses(ActionRequest actionRequest) {
		return getAddresses(actionRequest, Collections.<Address>emptyList());
	}

	public static List<Address> getAddresses(
		ActionRequest actionRequest, List<Address> defaultAddresses) {

		String addressesIndexesString = actionRequest.getParameter(
			"addressesIndexes");

		if (addressesIndexesString == null) {
			return defaultAddresses;
		}

		List<Address> addresses = new ArrayList<>();

		int[] addressesIndexes = StringUtil.split(addressesIndexesString, 0);

		int addressPrimary = ParamUtil.getInteger(
			actionRequest, "addressPrimary");

		for (int addressesIndex : addressesIndexes) {
			long countryId = ParamUtil.getLong(
				actionRequest, "addressCountryId" + addressesIndex);
			String city = ParamUtil.getString(
				actionRequest, "addressCity" + addressesIndex);
			String street1 = ParamUtil.getString(
				actionRequest, "addressStreet1_" + addressesIndex);
			String street2 = ParamUtil.getString(
				actionRequest, "addressStreet2_" + addressesIndex);
			String street3 = ParamUtil.getString(
				actionRequest, "addressStreet3_" + addressesIndex);
			String zip = ParamUtil.getString(
				actionRequest, "addressZip" + addressesIndex);

			if ((countryId == 0) && Validator.isNull(city) &&
				Validator.isNull(street1) && Validator.isNull(street2) &&
				Validator.isNull(street3) && Validator.isNull(zip)) {

				continue;
			}

			long addressId = ParamUtil.getLong(
				actionRequest, "addressId" + addressesIndex);

			long listTypeId = ParamUtil.getLong(
				actionRequest, "addressListTypeId" + addressesIndex);
			long regionId = ParamUtil.getLong(
				actionRequest, "addressRegionId" + addressesIndex);
			boolean mailing = ParamUtil.getBoolean(
				actionRequest, "addressMailing" + addressesIndex);

			boolean primary = false;

			if (addressesIndex == addressPrimary) {
				primary = true;
			}

			Address address = AddressLocalServiceUtil.createAddress(addressId);

			address.setCountryId(countryId);
			address.setListTypeId(listTypeId);
			address.setRegionId(regionId);
			address.setCity(city);
			address.setMailing(mailing);
			address.setPrimary(primary);
			address.setStreet1(street1);
			address.setStreet2(street2);
			address.setStreet3(street3);
			address.setZip(zip);

			addresses.add(address);
		}

		return addresses;
	}

	public static List<EmailAddress> getEmailAddresses(
		ActionRequest actionRequest) {

		return getEmailAddresses(
			actionRequest, Collections.<EmailAddress>emptyList());
	}

	public static List<EmailAddress> getEmailAddresses(
		ActionRequest actionRequest, List<EmailAddress> defaultEmailAddresses) {

		String emailAddressesIndexesString = actionRequest.getParameter(
			"emailAddressesIndexes");

		if (emailAddressesIndexesString == null) {
			return defaultEmailAddresses;
		}

		List<EmailAddress> emailAddresses = new ArrayList<>();

		int[] emailAddressesIndexes = StringUtil.split(
			emailAddressesIndexesString, 0);

		int emailAddressPrimary = ParamUtil.getInteger(
			actionRequest, "emailAddressPrimary");

		for (int emailAddressesIndex : emailAddressesIndexes) {
			String address = ParamUtil.getString(
				actionRequest, "emailAddressAddress" + emailAddressesIndex);

			if (Validator.isNull(address)) {
				continue;
			}

			long listTypeId = ParamUtil.getLong(
				actionRequest, "emailAddressListTypeId" + emailAddressesIndex);

			boolean primary = false;

			if (emailAddressesIndex == emailAddressPrimary) {
				primary = true;
			}

			long emailAddressId = ParamUtil.getLong(
				actionRequest, "emailAddressId" + emailAddressesIndex);

			EmailAddress emailAddress =
				EmailAddressLocalServiceUtil.createEmailAddress(emailAddressId);

			emailAddress.setAddress(address);
			emailAddress.setListTypeId(listTypeId);
			emailAddress.setPrimary(primary);

			emailAddresses.add(emailAddress);
		}

		return emailAddresses;
	}

	public static long[] getGroupIds(PortletRequest portletRequest)
		throws PortalException {

		long[] groupIds = new long[0];

		User user = PortalUtil.getSelectedUser(portletRequest);

		if (user != null) {
			groupIds = user.getGroupIds();
		}

		return _getRequestPrimaryKeys(
			portletRequest, groupIds, "addGroupIds", "deleteGroupIds");
	}

	public static OrderByComparator<Group> getGroupOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<Group> orderByComparator = null;

		if (orderByCol.equals("name")) {
			orderByComparator = new GroupNameComparator(orderByAsc);
		}
		else if (orderByCol.equals("type")) {
			orderByComparator = GroupTypeComparator.getInstance(orderByAsc);
		}
		else {
			orderByComparator = new GroupNameComparator(orderByAsc);
		}

		return orderByComparator;
	}

	public static Long[] getOrganizationIds(List<Organization> organizations) {
		if (ListUtil.isEmpty(organizations)) {
			return new Long[0];
		}

		Long[] organizationIds = new Long[organizations.size()];

		for (int i = 0; i < organizations.size(); i++) {
			Organization organization = organizations.get(i);

			organizationIds[i] = Long.valueOf(organization.getOrganizationId());
		}

		return organizationIds;
	}

	public static long[] getOrganizationIds(PortletRequest portletRequest)
		throws PortalException {

		long[] organizationIds = new long[0];

		User user = PortalUtil.getSelectedUser(portletRequest);

		if (user != null) {
			organizationIds = user.getOrganizationIds();
		}

		return _getRequestPrimaryKeys(
			portletRequest, organizationIds, "addOrganizationIds",
			"deleteOrganizationIds");
	}

	public static OrderByComparator<Organization>
		getOrganizationOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<Organization> orderByComparator = null;

		if (orderByCol.equals("name")) {
			orderByComparator = OrganizationNameComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("type")) {
			orderByComparator = OrganizationTypeComparator.getInstance(
				orderByAsc);
		}
		else {
			orderByComparator = OrganizationNameComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	public static List<Organization> getOrganizations(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<Organization> organizations = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long organizationId = GetterUtil.getLong(
				document.get(Field.ORGANIZATION_ID));

			Organization organization =
				OrganizationLocalServiceUtil.fetchOrganization(organizationId);

			if (organization == null) {
				organizations = null;

				Indexer<Organization> indexer = IndexerRegistryUtil.getIndexer(
					Organization.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (organizations != null) {
				organizations.add(organization);
			}
		}

		return organizations;
	}

	public static List<OrgLabor> getOrgLabors(ActionRequest actionRequest) {
		List<OrgLabor> orgLabors = new ArrayList<>();

		int[] orgLaborsIndexes = StringUtil.split(
			ParamUtil.getString(actionRequest, "orgLaborsIndexes"), 0);

		for (int orgLaborsIndex : orgLaborsIndexes) {
			long listTypeId = ParamUtil.getLong(
				actionRequest, "orgLaborListTypeId" + orgLaborsIndex, -1);

			if (listTypeId == -1) {
				continue;
			}

			long orgLaborId = ParamUtil.getLong(
				actionRequest, "orgLaborId" + orgLaborsIndex);

			int sunOpen = ParamUtil.getInteger(
				actionRequest, "sunOpen" + orgLaborsIndex, -1);
			int sunClose = ParamUtil.getInteger(
				actionRequest, "sunClose" + orgLaborsIndex, -1);
			int monOpen = ParamUtil.getInteger(
				actionRequest, "monOpen" + orgLaborsIndex, -1);
			int monClose = ParamUtil.getInteger(
				actionRequest, "monClose" + orgLaborsIndex, -1);
			int tueOpen = ParamUtil.getInteger(
				actionRequest, "tueOpen" + orgLaborsIndex, -1);
			int tueClose = ParamUtil.getInteger(
				actionRequest, "tueClose" + orgLaborsIndex, -1);
			int wedOpen = ParamUtil.getInteger(
				actionRequest, "wedOpen" + orgLaborsIndex, -1);
			int wedClose = ParamUtil.getInteger(
				actionRequest, "wedClose" + orgLaborsIndex, -1);
			int thuOpen = ParamUtil.getInteger(
				actionRequest, "thuOpen" + orgLaborsIndex, -1);
			int thuClose = ParamUtil.getInteger(
				actionRequest, "thuClose" + orgLaborsIndex, -1);
			int friOpen = ParamUtil.getInteger(
				actionRequest, "friOpen" + orgLaborsIndex, -1);
			int friClose = ParamUtil.getInteger(
				actionRequest, "friClose" + orgLaborsIndex, -1);
			int satOpen = ParamUtil.getInteger(
				actionRequest, "satOpen" + orgLaborsIndex, -1);
			int satClose = ParamUtil.getInteger(
				actionRequest, "satClose" + orgLaborsIndex, -1);

			OrgLabor orgLabor = OrgLaborLocalServiceUtil.createOrgLabor(
				orgLaborId);

			orgLabor.setListTypeId(listTypeId);
			orgLabor.setSunOpen(sunOpen);
			orgLabor.setSunClose(sunClose);
			orgLabor.setMonOpen(monOpen);
			orgLabor.setMonClose(monClose);
			orgLabor.setTueOpen(tueOpen);
			orgLabor.setTueClose(tueClose);
			orgLabor.setWedOpen(wedOpen);
			orgLabor.setWedClose(wedClose);
			orgLabor.setThuOpen(thuOpen);
			orgLabor.setThuClose(thuClose);
			orgLabor.setFriOpen(friOpen);
			orgLabor.setFriClose(friClose);
			orgLabor.setSatOpen(satOpen);
			orgLabor.setSatClose(satClose);

			orgLabors.add(orgLabor);
		}

		return orgLabors;
	}

	public static List<Phone> getPhones(ActionRequest actionRequest) {
		return getPhones(actionRequest, Collections.<Phone>emptyList());
	}

	public static List<Phone> getPhones(
		ActionRequest actionRequest, List<Phone> defaultPhones) {

		String phonesIndexesString = actionRequest.getParameter(
			"phonesIndexes");

		if (phonesIndexesString == null) {
			return defaultPhones;
		}

		List<Phone> phones = new ArrayList<>();

		int[] phonesIndexes = StringUtil.split(phonesIndexesString, 0);

		int phonePrimary = ParamUtil.getInteger(actionRequest, "phonePrimary");

		for (int phonesIndex : phonesIndexes) {
			String number = ParamUtil.getString(
				actionRequest, "phoneNumber" + phonesIndex);
			String extension = ParamUtil.getString(
				actionRequest, "phoneExtension" + phonesIndex);

			if (Validator.isNull(number) && Validator.isNull(extension)) {
				continue;
			}

			long typeId = ParamUtil.getLong(
				actionRequest, "phoneListTypeId" + phonesIndex);

			boolean primary = false;

			if (phonesIndex == phonePrimary) {
				primary = true;
			}

			long phoneId = ParamUtil.getLong(
				actionRequest, "phoneId" + phonesIndex);

			Phone phone = PhoneLocalServiceUtil.createPhone(phoneId);

			phone.setNumber(number);
			phone.setExtension(extension);
			phone.setListTypeId(typeId);
			phone.setPrimary(primary);

			phones.add(phone);
		}

		return phones;
	}

	public static long[] getRoleIds(PortletRequest portletRequest)
		throws PortalException {

		long[] roleIds = new long[0];

		User user = PortalUtil.getSelectedUser(portletRequest);

		if (user != null) {
			roleIds = user.getRoleIds();
		}

		return _getRequestPrimaryKeys(
			portletRequest, roleIds, "addRoleIds", "deleteRoleIds");
	}

	public static OrderByComparator<Role> getRoleOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<Role> orderByComparator = null;

		if (orderByCol.equals("name")) {
			orderByComparator = RoleNameComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("description")) {
			orderByComparator = RoleDescriptionComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("type")) {
			orderByComparator = RoleTypeComparator.getInstance(orderByAsc);
		}
		else {
			orderByComparator = RoleNameComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	public static <T> String getUserColumnText(
		Locale locale, List<? extends T> list, Accessor<T, String> accessor,
		int count) {

		String result = ListUtil.toString(
			list, accessor, StringPool.COMMA_AND_SPACE);

		if (list.size() < count) {
			String message = LanguageUtil.format(
				locale, "and-x-more", String.valueOf(count - list.size()),
				false);

			result += StringPool.SPACE + message;
		}

		return result;
	}

	public static long[] getUserGroupIds(PortletRequest portletRequest)
		throws PortalException {

		long[] userGroupIds = new long[0];

		User user = PortalUtil.getSelectedUser(portletRequest);

		if (user != null) {
			userGroupIds = user.getUserGroupIds();
		}

		return _getRequestPrimaryKeys(
			portletRequest, userGroupIds, "addUserGroupIds",
			"deleteUserGroupIds");
	}

	public static OrderByComparator<UserGroup> getUserGroupOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<UserGroup> orderByComparator = null;

		if (orderByCol.equals("name")) {
			orderByComparator = UserGroupNameComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("description")) {
			orderByComparator = UserGroupDescriptionComparator.getInstance(
				orderByAsc);
		}
		else {
			orderByComparator = UserGroupNameComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	public static List<UserGroupRole> getUserGroupRoles(
			PortletRequest portletRequest)
		throws PortalException {

		User user = PortalUtil.getSelectedUser(portletRequest);

		if (user == null) {
			return Collections.emptyList();
		}

		Set<UserGroupRole> userGroupRoles = new HashSet<>(
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(user.getUserId()));

		userGroupRoles.addAll(
			_getUserGroupRoles(
				portletRequest, user, "addGroupRolesGroupIds",
				"addGroupRolesRoleIds"));
		userGroupRoles.removeAll(
			_getUserGroupRoles(
				portletRequest, user, "deleteGroupRolesGroupIds",
				"deleteGroupRolesRoleIds"));

		return new ArrayList<>(userGroupRoles);
	}

	public static List<UserGroup> getUserGroups(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<UserGroup> userGroups = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long userGroupId = GetterUtil.getLong(
				document.get(Field.USER_GROUP_ID));

			UserGroup userGroup = UserGroupLocalServiceUtil.fetchUserGroup(
				userGroupId);

			if (userGroup == null) {
				userGroups = null;

				Indexer<UserGroup> indexer = IndexerRegistryUtil.getIndexer(
					UserGroup.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (userGroups != null) {
				userGroups.add(userGroup);
			}
		}

		return userGroups;
	}

	public static OrderByComparator<User> getUserOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<User> orderByComparator = null;

		if (orderByCol.equals("email-address")) {
			orderByComparator = UserEmailAddressComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("first-name")) {
			orderByComparator = UserFirstNameComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("job-title")) {
			orderByComparator = UserJobTitleComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("last-login-date")) {
			orderByComparator = UserLastLoginDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("last-name")) {
			orderByComparator = UserLastNameComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("screen-name")) {
			orderByComparator = UserScreenNameComparator.getInstance(
				orderByAsc);
		}
		else {
			orderByComparator = UserLastNameComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	public static List<User> getUsers(Hits hits) throws PortalException {
		List<Document> documents = hits.toList();

		List<User> users = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long userId = GetterUtil.getLong(document.get(Field.USER_ID));

			User user = UserLocalServiceUtil.fetchUser(userId);

			if (user == null) {
				users = null;

				Indexer<User> indexer = IndexerRegistryUtil.getIndexer(
					User.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (users != null) {
				users.add(user);
			}
		}

		return users;
	}

	public static List<Website> getWebsites(ActionRequest actionRequest) {
		return getWebsites(actionRequest, Collections.<Website>emptyList());
	}

	public static List<Website> getWebsites(
		ActionRequest actionRequest, List<Website> defaultWebsites) {

		String websitesIndexesString = actionRequest.getParameter(
			"websitesIndexes");

		if (websitesIndexesString == null) {
			return defaultWebsites;
		}

		List<Website> websites = new ArrayList<>();

		int[] websitesIndexes = StringUtil.split(websitesIndexesString, 0);

		int websitePrimary = ParamUtil.getInteger(
			actionRequest, "websitePrimary");

		for (int websitesIndex : websitesIndexes) {
			String url = ParamUtil.getString(
				actionRequest, "websiteUrl" + websitesIndex);

			if (Validator.isNull(url)) {
				continue;
			}

			long listTypeId = ParamUtil.getLong(
				actionRequest, "websiteListTypeId" + websitesIndex);

			boolean primary = false;

			if (websitesIndex == websitePrimary) {
				primary = true;
			}

			long websiteId = ParamUtil.getLong(
				actionRequest, "websiteId" + websitesIndex);

			Website website = WebsiteLocalServiceUtil.createWebsite(websiteId);

			website.setUrl(url);
			website.setListTypeId(listTypeId);
			website.setPrimary(primary);

			websites.add(website);
		}

		return websites;
	}

	public static boolean hasUpdateFieldPermission(
			PermissionChecker permissionChecker, User updatingUser,
			User updatedUser, String field)
		throws PortalException {

		if (updatedUser == null) {
			return true;
		}

		if (updatingUser == null) {
			long updatingUserId = PrincipalThreadLocal.getUserId();

			if (updatingUserId > 0) {
				updatingUser = UserLocalServiceUtil.fetchUserById(
					updatingUserId);
			}
		}

		if ((updatingUser != null) && !updatingUser.equals(updatedUser) &&
			UserPermissionUtil.contains(
				permissionChecker, updatingUser.getUserId(),
				ActionKeys.UPDATE_USER)) {

			return true;
		}

		for (String userType :
				PropsUtil.getArray(PropsKeys.FIELD_EDITABLE_USER_TYPES)) {

			if (userType.equals("user-with-mx") && updatedUser.hasCompanyMx()) {
				return true;
			}

			if (userType.equals("user-without-mx") &&
				!updatedUser.hasCompanyMx()) {

				return true;
			}
		}

		for (String roleName :
				PropsUtil.getArray(PropsKeys.FIELD_EDITABLE_ROLES)) {

			Role role = RoleLocalServiceUtil.fetchRole(
				updatedUser.getCompanyId(), roleName);

			if ((role != null) &&
				RoleLocalServiceUtil.hasUserRole(
					updatedUser.getUserId(), role.getRoleId())) {

				return true;
			}
		}

		String emailAddress = updatedUser.getEmailAddress();

		for (String domainName :
				PropsUtil.getArray(PropsKeys.FIELD_EDITABLE_DOMAINS)) {

			if (emailAddress.endsWith(domainName)) {
				return true;
			}
		}

		String[] fieldEditableDomainNames = PropsUtil.getArray(
			PropsKeys.FIELD_EDITABLE_DOMAINS, new Filter(field));

		for (String domainName : fieldEditableDomainNames) {
			if (domainName.equals(StringPool.STAR) ||
				emailAddress.endsWith(domainName)) {

				return true;
			}
		}

		return false;
	}

	public static long[] removeRequiredRoles(long userId, long[] roleIds)
		throws PortalException {

		return removeRequiredRoles(
			UserLocalServiceUtil.getUser(userId), roleIds);
	}

	public static long[] removeRequiredRoles(User user, long[] roleIds)
		throws PortalException {

		Role role = RoleLocalServiceUtil.getRole(
			user.getCompanyId(), RoleConstants.USER);

		return ArrayUtil.remove(roleIds, role.getRoleId());
	}

	public static void updateAddresses(
			String className, long classPK, List<Address> addresses)
		throws PortalException {

		Set<Long> addressIds = new HashSet<>();

		for (Address address : addresses) {
			long addressId = address.getAddressId();

			String name = address.getName();
			String description = address.getDescription();
			String street1 = address.getStreet1();
			String street2 = address.getStreet2();
			String street3 = address.getStreet3();
			String city = address.getCity();
			String zip = address.getZip();
			long regionId = address.getRegionId();
			long countryId = address.getCountryId();
			long listTypeId = address.getListTypeId();
			boolean mailing = address.isMailing();
			boolean primary = address.isPrimary();
			String phoneNumber = address.getPhoneNumber();

			if (addressId <= 0) {
				address = AddressServiceUtil.addAddress(
					address.getExternalReferenceCode(), className, classPK,
					name, description, street1, street2, street3, city, zip,
					regionId, countryId, listTypeId, mailing, primary,
					phoneNumber, new ServiceContext());

				addressId = address.getAddressId();
			}
			else {
				AddressServiceUtil.updateAddress(
					addressId, name, description, street1, street2, street3,
					city, zip, regionId, countryId, listTypeId, mailing,
					primary, phoneNumber);
			}

			addressIds.add(addressId);
		}

		addresses = AddressServiceUtil.getAddresses(className, classPK);

		for (Address address : addresses) {
			if (!addressIds.contains(address.getAddressId())) {
				AddressServiceUtil.deleteAddress(address.getAddressId());
			}
		}
	}

	public static void updateEmailAddresses(
			String className, long classPK, List<EmailAddress> emailAddresses)
		throws PortalException {

		Set<Long> emailAddressIds = new HashSet<>();

		for (EmailAddress emailAddress : emailAddresses) {
			long emailAddressId = emailAddress.getEmailAddressId();

			String address = emailAddress.getAddress();
			long listTypeId = emailAddress.getListTypeId();
			boolean primary = emailAddress.isPrimary();

			if (emailAddressId <= 0) {
				emailAddress = EmailAddressServiceUtil.addEmailAddress(
					emailAddress.getExternalReferenceCode(), className, classPK,
					address, listTypeId, primary, new ServiceContext());

				emailAddressId = emailAddress.getEmailAddressId();
			}
			else {
				EmailAddressServiceUtil.updateEmailAddress(
					emailAddress.getExternalReferenceCode(), emailAddressId,
					address, listTypeId, primary);
			}

			emailAddressIds.add(emailAddressId);
		}

		emailAddresses = EmailAddressServiceUtil.getEmailAddresses(
			className, classPK);

		for (EmailAddress emailAddress : emailAddresses) {
			if (!emailAddressIds.contains(emailAddress.getEmailAddressId())) {
				EmailAddressServiceUtil.deleteEmailAddress(
					emailAddress.getEmailAddressId());
			}
		}
	}

	public static void updateOrgLabors(long classPK, List<OrgLabor> orgLabors)
		throws PortalException {

		Set<Long> orgLaborsIds = new HashSet<>();

		for (OrgLabor orgLabor : orgLabors) {
			long orgLaborId = orgLabor.getOrgLaborId();

			long listTypeId = orgLabor.getListTypeId();
			int sunOpen = orgLabor.getSunOpen();
			int sunClose = orgLabor.getSunClose();
			int monOpen = orgLabor.getMonOpen();
			int monClose = orgLabor.getMonClose();
			int tueOpen = orgLabor.getTueOpen();
			int tueClose = orgLabor.getTueClose();
			int wedOpen = orgLabor.getWedOpen();
			int wedClose = orgLabor.getWedClose();
			int thuOpen = orgLabor.getThuOpen();
			int thuClose = orgLabor.getThuClose();
			int friOpen = orgLabor.getFriOpen();
			int friClose = orgLabor.getFriClose();
			int satOpen = orgLabor.getSatOpen();
			int satClose = orgLabor.getSatClose();

			if (orgLaborId <= 0) {
				orgLabor = OrgLaborServiceUtil.addOrgLabor(
					classPK, listTypeId, sunOpen, sunClose, monOpen, monClose,
					tueOpen, tueClose, wedOpen, wedClose, thuOpen, thuClose,
					friOpen, friClose, satOpen, satClose);

				orgLaborId = orgLabor.getOrgLaborId();
			}
			else {
				OrgLaborServiceUtil.updateOrgLabor(
					orgLaborId, listTypeId, sunOpen, sunClose, monOpen,
					monClose, tueOpen, tueClose, wedOpen, wedClose, thuOpen,
					thuClose, friOpen, friClose, satOpen, satClose);
			}

			orgLaborsIds.add(orgLaborId);
		}

		orgLabors = OrgLaborServiceUtil.getOrgLabors(classPK);

		for (OrgLabor orgLabor : orgLabors) {
			if (!orgLaborsIds.contains(orgLabor.getOrgLaborId())) {
				OrgLaborServiceUtil.deleteOrgLabor(orgLabor.getOrgLaborId());
			}
		}
	}

	public static void updatePhones(
			String className, long classPK, List<Phone> phones)
		throws PortalException {

		Set<Long> phoneIds = new HashSet<>();

		for (Phone phone : phones) {
			long phoneId = phone.getPhoneId();

			String externalReferenceCode = phone.getExternalReferenceCode();
			String number = phone.getNumber();
			String extension = phone.getExtension();
			long listTypeId = phone.getListTypeId();
			boolean primary = phone.isPrimary();

			if (phoneId <= 0) {
				phone = PhoneServiceUtil.addPhone(
					externalReferenceCode, className, classPK, number,
					extension, listTypeId, primary, new ServiceContext());

				phoneId = phone.getPhoneId();
			}
			else {
				PhoneServiceUtil.updatePhone(
					externalReferenceCode, phoneId, number, extension,
					listTypeId, primary);
			}

			phoneIds.add(phoneId);
		}

		phones = PhoneServiceUtil.getPhones(className, classPK);

		for (Phone phone : phones) {
			if (!phoneIds.contains(phone.getPhoneId())) {
				PhoneServiceUtil.deletePhone(phone.getPhoneId());
			}
		}
	}

	public static void updateWebsites(
			String className, long classPK, List<Website> websites)
		throws PortalException {

		Set<Long> websiteIds = new HashSet<>();

		for (Website website : websites) {
			String externalReferenceCode = website.getExternalReferenceCode();
			long websiteId = website.getWebsiteId();
			String url = website.getUrl();
			long listTypeId = website.getListTypeId();
			boolean primary = website.isPrimary();

			if (websiteId <= 0) {
				website = WebsiteServiceUtil.addWebsite(
					externalReferenceCode, className, classPK, url, listTypeId,
					primary, new ServiceContext());

				websiteId = website.getWebsiteId();
			}
			else {
				WebsiteServiceUtil.updateWebsite(
					externalReferenceCode, websiteId, url, listTypeId, primary);
			}

			websiteIds.add(websiteId);
		}

		websites = WebsiteServiceUtil.getWebsites(className, classPK);

		for (Website website : websites) {
			if (!websiteIds.contains(website.getWebsiteId())) {
				WebsiteServiceUtil.deleteWebsite(website.getWebsiteId());
			}
		}
	}

	private static long[] _getRequestPrimaryKeys(
		PortletRequest portletRequest, long[] currentPKs, String addParam,
		String deleteParam) {

		Set<Long> primaryKeys = SetUtil.fromArray(currentPKs);

		long[] addPrimaryKeys = StringUtil.split(
			ParamUtil.getString(portletRequest, addParam), 0L);
		long[] deletePrimaryKeys = StringUtil.split(
			ParamUtil.getString(portletRequest, deleteParam), 0L);

		for (long addPrimaryKey : addPrimaryKeys) {
			primaryKeys.add(addPrimaryKey);
		}

		for (long deletePrimaryKey : deletePrimaryKeys) {
			primaryKeys.remove(deletePrimaryKey);
		}

		return ArrayUtil.toLongArray(primaryKeys);
	}

	private static Set<UserGroupRole> _getUserGroupRoles(
		PortletRequest portletRequest, User user, String groupIdsParam,
		String roleIdsParam) {

		Set<UserGroupRole> userGroupRoles = new HashSet<>();

		long[] groupRolesGroupIds = StringUtil.split(
			ParamUtil.getString(portletRequest, groupIdsParam), 0L);
		long[] groupRolesRoleIds = StringUtil.split(
			ParamUtil.getString(portletRequest, roleIdsParam), 0L);

		if (groupRolesGroupIds.length != groupRolesRoleIds.length) {
			return userGroupRoles;
		}

		long userId = 0;

		if (user != null) {
			userId = user.getUserId();
		}

		for (int i = 0; i < groupRolesGroupIds.length; i++) {
			if ((groupRolesGroupIds[i] == 0) || (groupRolesRoleIds[i] == 0)) {
				continue;
			}

			UserGroupRole userGroupRole =
				UserGroupRoleLocalServiceUtil.createUserGroupRole(0);

			userGroupRole.setUserId(userId);
			userGroupRole.setGroupId(groupRolesGroupIds[i]);
			userGroupRole.setRoleId(groupRolesRoleIds[i]);

			userGroupRoles.add(userGroupRole);
		}

		return userGroupRoles;
	}

}