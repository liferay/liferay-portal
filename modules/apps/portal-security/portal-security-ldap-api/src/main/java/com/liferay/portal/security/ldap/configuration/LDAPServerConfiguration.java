/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(
	category = "ldap", factoryInstanceLabelAttribute = "ldapServerId",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY,
	visibilityControllerKey = "ldap-server"
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration",
	localization = "content/Language", name = "ldap-server-configuration-name"
)
@ProviderType
public interface LDAPServerConfiguration {

	public static final long LDAP_SERVER_ID_DEFAULT = 0;

	@Meta.AD(deflt = "0", name = "company-id", required = false)
	public long companyId();

	@Meta.AD(deflt = "0", name = "ldap-server-id", required = false)
	public long ldapServerId();

	@Meta.AD(deflt = "", name = "server-name", required = false)
	public String serverName();

	@Meta.AD(
		deflt = "3000", description = "ldap-clock-skew-description",
		name = "clock-skew", required = false
	)
	public long clockSkew();

	@Meta.AD(
		deflt = "ldap://localhost:10389", name = "base-provider-url",
		required = false
	)
	public String baseProviderURL();

	@Meta.AD(
		deflt = "", description = "base-dn-help", name = "base-dn",
		required = false
	)
	public String baseDN();

	@Meta.AD(deflt = "", name = "security-principal", required = false)
	public String securityPrincipal();

	@Meta.AD(
		deflt = "secret", name = "security-credential", required = false,
		type = Meta.Type.Password
	)
	public String securityCredential();

	@Meta.AD(
		deflt = "(mail=@email_address@)",
		description = "authentication-search-filter-help",
		name = "authentication-search-filter", required = false
	)
	public String authSearchFilter();

	@Meta.AD(
		deflt = "(objectClass=inetOrgPerson)",
		description = "user-search-filter-help", name = "user-search-filter",
		required = false
	)
	public String userSearchFilter();

	@Meta.AD(
		deflt = "emailAddress=mail|firstName=givenName|group=groupMembership|jobTitle=title|lastName=sn|password=userPassword|screenName=cn|uuid=uuid",
		description = "user-mappings-help", name = "user-mappings",
		required = false
	)
	public String[] userMappings();

	@Meta.AD(
		deflt = "", description = "user-custom-mappings-help",
		name = "user-custom-mappings", required = false
	)
	public String[] userCustomMappings();

	@Meta.AD(
		deflt = "", description = "user-ignore-attributes-help",
		name = "user-ignore-attributes", required = false
	)
	public String[] userIgnoreAttributes();

	@Meta.AD(
		deflt = "birthday=|facebookSn=|jabberSn=|skypeSn=|smsSn=|twitterSn=",
		description = "contact-mappings-help", name = "contact-mappings",
		required = false
	)
	public String[] contactMappings();

	@Meta.AD(
		deflt = "", description = "contact-custom-mappings-help",
		name = "contact-custom-mappings", required = false
	)
	public String[] contactCustomMappings();

	@Meta.AD(
		deflt = "true", name = "group-search-filter-enabled", required = false
	)
	public boolean groupSearchFilterEnabled();

	@Meta.AD(
		deflt = "true", description = "ignore-user-search-filter-for-auth-help",
		name = "ignore-user-search-filter-for-auth", required = false
	)
	public boolean ignoreUserSearchFilterForAuth();

	@Meta.AD(
		deflt = "(objectClass=groupOfUniqueNames)",
		description = "group-search-filter-help", name = "group-search-filter",
		required = false
	)
	public String groupSearchFilter();

	@Meta.AD(
		deflt = "description=description|groupName=cn|user=uniqueMember",
		description = "group-mappings-help", name = "group-mappings",
		required = false
	)
	public String[] groupMappings();

	@Meta.AD(
		deflt = "", description = "users-dn-help", name = "users-dn",
		required = false
	)
	public String usersDN();

	@Meta.AD(
		deflt = "top|person|inetOrgPerson|organizationalPerson",
		description = "user-default-object-classes-help",
		name = "user-default-object-classes", required = false
	)
	public String[] userDefaultObjectClasses();

	@Meta.AD(
		deflt = "", description = "groups-dn-help", name = "groups-dn",
		required = false
	)
	public String groupsDN();

	@Meta.AD(
		deflt = "top|groupOfUniqueNames",
		description = "group-default-object-classes-help",
		name = "group-default-object-classes", required = false
	)
	public String[] groupDefaultObjectClasses();

	@Meta.AD(deflt = "", name = "modified-date", required = false)
	public String modifiedDate();

}