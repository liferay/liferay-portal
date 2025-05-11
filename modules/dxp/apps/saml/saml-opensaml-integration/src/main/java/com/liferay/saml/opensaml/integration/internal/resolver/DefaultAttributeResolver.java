/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.resolver;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.saml.opensaml.integration.resolver.AttributeResolver;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.util.PortletPropsKeys;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Attribute;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Carlos Sierra
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = AttributeResolver.class
)
public class DefaultAttributeResolver implements AttributeResolver {

	@Override
	public void resolve(
		User user, AttributeResolverSAMLContext attributeResolverSAMLContext,
		AttributePublisher attributePublisher) {

		String entityId = attributeResolverSAMLContext.resolvePeerEntityId();

		boolean namespaceEnabled = _isAttributesNamespaceEnabled(entityId);

		for (String attributeName : getAttributeNames(entityId)) {
			if (attributeName.startsWith("expando:")) {
				attributeName = attributeName.substring(8);

				_addExpandoAttribute(
					user, attributePublisher, attributeName, namespaceEnabled);
			}
			else if (attributeName.equals("groups")) {
				_addGroupsAttribute(user, attributePublisher, namespaceEnabled);
			}
			else if (attributeName.startsWith("map:")) {
				attributeName = attributeName.substring(4);

				_addMapAttribute(
					user, attributePublisher, attributeName, namespaceEnabled);
			}
			else if (attributeName.equals("organizations")) {
				_addOrganizationsAttribute(
					user, attributePublisher, namespaceEnabled);
			}
			else if (attributeName.equals("organizationRoles")) {
				_addOrganizationRolesAttribute(
					user, attributePublisher, namespaceEnabled);
			}
			else if (attributeName.equals("roles")) {
				_addRolesAttribute(user, attributePublisher, namespaceEnabled);
			}
			else if (attributeName.startsWith("static:")) {
				attributeName = attributeName.substring(7);

				_addStaticAttribute(
					attributePublisher, attributeName, namespaceEnabled);
			}
			else if (attributeName.equals("siteRoles") ||
					 attributeName.equals("userGroupRoles")) {

				_addSiteRolesAttribute(
					user, attributePublisher, attributeName, namespaceEnabled);
			}
			else if (attributeName.equals("userGroups")) {
				_addUserGroupsAttribute(
					user, attributePublisher, namespaceEnabled);
			}
			else {
				_addUserAttribute(
					user, attributePublisher, attributeName, namespaceEnabled);
			}
		}

		if (_isPeerSalesForce(entityId)) {
			_addSalesForceAttributes(
				attributeResolverSAMLContext, attributePublisher);
		}
	}

	protected String[] getAttributeNames(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			SamlIdpSpConnection samlIdpSpConnection =
				_samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
					companyId, entityId);

			return StringUtil.splitLines(
				samlIdpSpConnection.getAttributeNames());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private void _addExpandoAttribute(
		User user, AttributePublisher attributePublisher, String attributeName,
		boolean namespaceEnabled) {

		ExpandoBridge expandoBridge = user.getExpandoBridge();

		Serializable value = expandoBridge.getAttribute(attributeName, false);

		if (value == null) {
			value = StringPool.BLANK;
		}

		if (!namespaceEnabled) {
			attributePublisher.publish(
				attributeName, Attribute.UNSPECIFIED, value.toString());
		}
		else {
			attributePublisher.publish(
				"urn:liferay:user:expando:" + attributeName,
				Attribute.URI_REFERENCE, value.toString());
		}
	}

	private void _addGroupsAttribute(
		User user, AttributePublisher attributePublisher,
		boolean namespaceEnabled) {

		try {
			List<Group> groups = user.getGroups();

			if (groups.isEmpty()) {
				return;
			}

			String name = null;
			String nameFormat = null;

			if (namespaceEnabled) {
				name = "urn:liferay:groups";
				nameFormat = Attribute.URI_REFERENCE;
			}
			else {
				name = "groups";
				nameFormat = Attribute.UNSPECIFIED;
			}

			attributePublisher.publish(
				name, nameFormat,
				TransformUtil.transformToArray(
					groups, Group::getName, String.class));
		}
		catch (Exception exception) {
			String message = StringBundler.concat(
				"Unable to get groups for user ", user.getUserId(), ": ",
				exception.getMessage());

			if (_log.isDebugEnabled()) {
				_log.debug(message, exception);
			}
			else {
				_log.error(message);
			}
		}
	}

	private void _addMapAttribute(
		User user, AttributePublisher attributePublisher, String attributeName,
		boolean namespaceEnabled) {

		if (attributeName.indexOf(StringPool.EQUAL) <= 0) {
			return;
		}

		String[] values = StringUtil.split(attributeName, StringPool.EQUAL);

		if (values.length > 2) {
			return;
		}

		String attributeValue = _beanProperties.getString(user, values[1]);

		if (namespaceEnabled) {
			attributePublisher.publish(
				values[0], Attribute.URI_REFERENCE, attributeValue);
		}
		else {
			attributePublisher.publish(
				values[0], Attribute.UNSPECIFIED, attributeValue);
		}
	}

	private void _addOrganizationRolesAttribute(
		User user, AttributePublisher attributePublisher,
		boolean namespaceEnabled) {

		try {
			List<UserGroupRole> userGroupRoles =
				_userGroupRoleLocalService.getUserGroupRoles(user.getUserId());

			Map<String, Set<Role>> groupRoles = new HashMap<>();

			for (UserGroupRole userGroupRole : userGroupRoles) {
				Role role = userGroupRole.getRole();

				if (role.getType() != RoleConstants.TYPE_ORGANIZATION) {
					continue;
				}

				Group group = userGroupRole.getGroup();

				Set<Role> roles = groupRoles.computeIfAbsent(
					group.getName(), k -> new HashSet<>());

				roles.add(userGroupRole.getRole());
			}

			for (Map.Entry<String, Set<Role>> entry : groupRoles.entrySet()) {
				String groupName = entry.getKey();

				String name = null;
				String nameFormat = null;

				if (namespaceEnabled) {
					name = "urn:liferay:organizationRole:" + groupName;
					nameFormat = Attribute.URI_REFERENCE;
				}
				else {
					name = "organizationRole:" + groupName;
					nameFormat = Attribute.UNSPECIFIED;
				}

				Set<Role> roles = entry.getValue();

				attributePublisher.publish(
					name, nameFormat,
					TransformUtil.transformToArray(
						roles, Role::getName, String.class));
			}
		}
		catch (Exception exception) {
			String message = StringBundler.concat(
				"Unable to get organization roles for user ", user.getUserId(),
				": ", exception.getMessage());

			if (_log.isDebugEnabled()) {
				_log.debug(message, exception);
			}
			else {
				_log.error(message);
			}
		}
	}

	private void _addOrganizationsAttribute(
		User user, AttributePublisher publisher, boolean namespaceEnabled) {

		try {
			List<Organization> organizations = user.getOrganizations();

			if (organizations.isEmpty()) {
				return;
			}

			String name = null;
			String nameFormat = null;

			if (namespaceEnabled) {
				name = "urn:liferay:organizations";
				nameFormat = Attribute.URI_REFERENCE;
			}
			else {
				name = "organizations";
				nameFormat = Attribute.UNSPECIFIED;
			}

			publisher.publish(
				name, nameFormat,
				TransformUtil.transformToArray(
					organizations, Organization::getName, String.class));
		}
		catch (Exception exception) {
			String message = StringBundler.concat(
				"Unable to get organizations for user ", user.getUserId(), ": ",
				exception.getMessage());

			if (_log.isDebugEnabled()) {
				_log.debug(message, exception);
			}
			else {
				_log.error(message);
			}
		}
	}

	private void _addRolesAttribute(
		User user, AttributePublisher attributePublisher,
		boolean namespaceEnabled) {

		try {
			List<Role> roles = user.getRoles();

			List<Group> groups = user.getGroups();
			List<Organization> organizations = user.getOrganizations();

			List<UserGroup> userGroups = user.getUserGroups();

			List<Group> inheritedSiteGroups =
				_groupLocalService.getUserGroupsRelatedGroups(userGroups);

			List<Group> organizationsRelatedGroups = Collections.emptyList();

			if (!organizations.isEmpty()) {
				organizationsRelatedGroups =
					_groupLocalService.getOrganizationsRelatedGroups(
						organizations);

				for (Group group : organizationsRelatedGroups) {
					if (!inheritedSiteGroups.contains(group)) {
						inheritedSiteGroups.add(group);
					}
				}
			}

			List<Group> allGroups = new ArrayList<>();

			allGroups.addAll(groups);
			allGroups.addAll(inheritedSiteGroups);
			allGroups.addAll(organizationsRelatedGroups);
			allGroups.addAll(
				_groupLocalService.getOrganizationsGroups(organizations));
			allGroups.addAll(
				_groupLocalService.getUserGroupsGroups(userGroups));

			Set<Role> uniqueRoles = new HashSet<>();

			uniqueRoles.addAll(roles);

			for (Group group : allGroups) {
				if (_roleLocalService.hasGroupRoles(group.getGroupId())) {
					uniqueRoles.addAll(
						_roleLocalService.getGroupRoles(group.getGroupId()));
				}
			}

			if (uniqueRoles.isEmpty()) {
				return;
			}

			String name = null;
			String nameFormat = null;

			if (namespaceEnabled) {
				name = "urn:liferay:roles";
				nameFormat = Attribute.URI_REFERENCE;
			}
			else {
				name = "roles";
				nameFormat = Attribute.UNSPECIFIED;
			}

			attributePublisher.publish(
				name, nameFormat,
				TransformUtil.transformToArray(
					uniqueRoles, Role::getName, String.class));
		}
		catch (Exception exception) {
			String message = StringBundler.concat(
				"Unable to get roles for user  ", user.getUserId(), ": ",
				exception.getMessage());

			if (_log.isDebugEnabled()) {
				_log.debug(message, exception);
			}
			else {
				_log.error(message);
			}
		}
	}

	private void _addSalesForceAttributes(
		AttributeResolverSAMLContext attributeResolverSAMLContext,
		AttributePublisher attributePublisher) {

		String samlIdpMetadataSalesForceLogoutURL = GetterUtil.getString(
			PropsUtil.get(
				PortletPropsKeys.SAML_IDP_METADATA_SALESFORCE_LOGOUT_URL));

		attributePublisher.publish(
			"logoutURL", Attribute.UNSPECIFIED,
			samlIdpMetadataSalesForceLogoutURL);

		String samlIdpMetadataSalesForceSsoStartPage = GetterUtil.getString(
			PropsUtil.get(
				PortletPropsKeys.SAML_IDP_METADATA_SALESFORCE_SSO_START_PAGE));

		List<String> locations =
			attributeResolverSAMLContext.resolveSsoServicesLocationForBinding(
				SAMLConstants.SAML2_POST_BINDING_URI);

		if (!locations.isEmpty()) {
			samlIdpMetadataSalesForceSsoStartPage = locations.get(0);
		}

		attributePublisher.publish(
			"ssoStartPage", Attribute.UNSPECIFIED,
			samlIdpMetadataSalesForceSsoStartPage);
	}

	private void _addSiteRolesAttribute(
		User user, AttributePublisher attributePublisher, String attributeName,
		boolean namespaceEnabled) {

		try {
			List<UserGroupRole> userGroupRoles =
				_userGroupRoleLocalService.getUserGroupRoles(user.getUserId());

			Map<String, Set<Role>> groupRoles = new HashMap<>();

			for (UserGroupRole userGroupRole : userGroupRoles) {
				Role role = userGroupRole.getRole();

				if ((role.getType() == RoleConstants.TYPE_ORGANIZATION) &&
					!attributeName.equals("userGroupRoles")) {

					continue;
				}

				Group group = userGroupRole.getGroup();

				Set<Role> roles = groupRoles.computeIfAbsent(
					group.getName(), k -> new HashSet<>());

				roles.add(userGroupRole.getRole());
			}

			List<UserGroupGroupRole> inheritedSiteRoles =
				_userGroupGroupRoleLocalService.getUserGroupGroupRolesByUser(
					user.getUserId());

			for (UserGroupGroupRole userGroupGroupRole : inheritedSiteRoles) {
				Group group = userGroupGroupRole.getGroup();
				Role role = userGroupGroupRole.getRole();

				Set<Role> roles = groupRoles.computeIfAbsent(
					group.getName(), k -> new HashSet<>());

				roles.add(role);
			}

			for (Map.Entry<String, Set<Role>> entry : groupRoles.entrySet()) {
				String groupName = entry.getKey();

				String name = null;
				String nameFormat = null;

				if (namespaceEnabled) {
					if (attributeName.equals("siteRoles")) {
						name = "urn:liferay:siteRole:" + groupName;
					}
					else {
						name = "urn:liferay:userGroupRole:" + groupName;
					}

					nameFormat = Attribute.URI_REFERENCE;
				}
				else {
					if (attributeName.equals("siteRoles")) {
						name = "siteRole:" + groupName;
					}
					else {
						name = "userGroupRole:" + groupName;
					}

					nameFormat = Attribute.UNSPECIFIED;
				}

				Set<Role> roles = entry.getValue();

				attributePublisher.publish(
					name, nameFormat,
					TransformUtil.transformToArray(
						roles, Role::getName, String.class));
			}
		}
		catch (Exception exception) {
			String message = StringBundler.concat(
				"Unable to get user group roles for user ", user.getUserId(),
				": ", exception.getMessage());

			if (_log.isDebugEnabled()) {
				_log.debug(message, exception);
			}
			else {
				_log.error(message);
			}
		}
	}

	private void _addStaticAttribute(
		AttributePublisher attributePublisher, String attributeName,
		boolean namespaceEnabled) {

		String attributeValue = StringPool.BLANK;

		if (attributeName.indexOf('=') > 0) {
			String[] values = StringUtil.split(attributeName, "=");

			attributeName = values[0];

			attributeValue = values[1];

			if (values.length > 2) {
				for (int i = 2; i < values.length; i++) {
					attributeValue = StringBundler.concat(
						attributeValue, "=", values[i]);
				}
			}
		}

		String nameFormat = null;

		if (namespaceEnabled) {
			nameFormat = Attribute.URI_REFERENCE;
		}
		else {
			nameFormat = Attribute.UNSPECIFIED;
		}

		attributePublisher.publish(attributeName, nameFormat, attributeValue);
	}

	private void _addUserAttribute(
		User user, AttributePublisher attributePublisher, String attributeName,
		boolean namespaceEnabled) {

		Serializable value = (Serializable)_beanProperties.getObject(
			user, attributeName);

		if (!namespaceEnabled) {
			attributePublisher.publish(
				attributeName, Attribute.UNSPECIFIED, value.toString());
		}
		else {
			attributePublisher.publish(
				"urn:liferay:user:" + attributeName, Attribute.URI_REFERENCE,
				value.toString());
		}
	}

	private void _addUserGroupsAttribute(
		User user, AttributePublisher attributePublisher,
		boolean namespaceEnabled) {

		try {
			List<UserGroup> userGroups = user.getUserGroups();

			if (userGroups.isEmpty()) {
				return;
			}

			String name = null;
			String nameFormat = null;

			if (namespaceEnabled) {
				name = "urn:liferay:membership:userGroups";
				nameFormat = Attribute.URI_REFERENCE;
			}
			else {
				name = "membership:userGroups";
				nameFormat = Attribute.UNSPECIFIED;
			}

			attributePublisher.publish(
				name, nameFormat,
				TransformUtil.transformToArray(
					userGroups, UserGroup::getName, String.class));
		}
		catch (Exception exception) {
			String message = StringBundler.concat(
				"Unable to get user groups for user ", user.getUserId(), ": ",
				exception.getMessage());

			if (_log.isDebugEnabled()) {
				_log.debug(message, exception);
			}
			else {
				_log.error(message);
			}
		}
	}

	private boolean _isAttributesNamespaceEnabled(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			SamlIdpSpConnection samlIdpSpConnection =
				_samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
					companyId, entityId);

			return samlIdpSpConnection.isAttributesNamespaceEnabled();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private boolean _isPeerSalesForce(String entityId) {
		if (entityId.equals(_SALESFORCE_ENTITY_ID)) {
			return true;
		}

		return GetterUtil.getBoolean(
			PropsUtil.get(
				PortletPropsKeys.
					SAML_IDP_METADATA_SALESFORCE_ATTRIBUTES_ENABLED,
				new Filter(entityId)));
	}

	private static final String _SALESFORCE_ENTITY_ID =
		"https://saml.salesforce.com";

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultAttributeResolver.class);

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

	@Reference
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}