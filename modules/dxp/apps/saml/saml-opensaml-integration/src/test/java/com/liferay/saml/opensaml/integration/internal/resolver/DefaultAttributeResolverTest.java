/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.resolver;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.internal.BaseSamlTestCase;
import com.liferay.saml.opensaml.integration.internal.util.SamlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnRequest;

/**
 * @author Mika Koivisto
 */
public class DefaultAttributeResolverTest extends BaseSamlTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_beanProperties = Mockito.mock(BeanProperties.class);

		ReflectionTestUtil.setFieldValue(
			_defaultAttributeResolver, "_beanProperties", _beanProperties);

		ReflectionTestUtil.setFieldValue(
			_defaultAttributeResolver, "_groupLocalService", groupLocalService);

		_user = Mockito.mock(User.class);

		_expandoBridge = Mockito.mock(ExpandoBridge.class);

		Mockito.when(
			_user.getExpandoBridge()
		).thenReturn(
			_expandoBridge
		);

		_roleLocalService = Mockito.mock(RoleLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_defaultAttributeResolver, "_roleLocalService", _roleLocalService);

		_messageContext = new MessageContext<>();

		SAMLPeerEntityContext samlPeerEntityContext =
			_messageContext.getSubcontext(SAMLPeerEntityContext.class, true);

		samlPeerEntityContext.setEntityId(SP_ENTITY_ID);

		_userGroupGroupRoleLocalService = Mockito.mock(
			UserGroupGroupRoleLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_defaultAttributeResolver, "_userGroupGroupRoleLocalService",
			_userGroupGroupRoleLocalService);

		_userGroupRoleLocalService = Mockito.mock(
			UserGroupRoleLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_defaultAttributeResolver, "_userGroupRoleLocalService",
			_userGroupRoleLocalService);
	}

	@Test
	public void testResolveExpandoAttributes() throws Exception {
		Mockito.when(
			_expandoBridge.getAttribute(
				Mockito.eq("customerId"), Mockito.anyBoolean())
		).thenReturn(
			"12345"
		);

		_attributeNamesAtomicReference.set(new String[] {"expando:customerId"});

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(), "customerId", "12345");
	}

	@Test
	public void testResolveGroupsAttributes() throws Exception {
		_attributeNamesAtomicReference.set(new String[] {"groups"});

		List<Group> groups = new ArrayList<>();

		Group group1 = Mockito.mock(Group.class);

		Mockito.when(
			group1.getName()
		).thenReturn(
			"Test 1"
		);

		groups.add(group1);

		Group group2 = Mockito.mock(Group.class);

		Mockito.when(
			group2.getName()
		).thenReturn(
			"Test 2"
		);

		groups.add(group2);

		Mockito.when(
			_user.getGroups()
		).thenReturn(
			groups
		);

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(), "groups",
			new String[] {"Test 1", "Test 2"});
	}

	@Test
	public void testResolveOrganizationRolesAttributes() throws Exception {
		_attributeNamesAtomicReference.set(new String[] {"organizationRoles"});

		Group group1 = Mockito.mock(Group.class);

		Mockito.when(
			group1.getName()
		).thenReturn(
			"Group Test 1"
		);

		Role role1 = Mockito.mock(Role.class);

		Mockito.when(
			role1.getName()
		).thenReturn(
			"Role Test 1"
		);

		Mockito.when(
			role1.getType()
		).thenReturn(
			RoleConstants.TYPE_ORGANIZATION
		);

		Role role2 = Mockito.mock(Role.class);

		Mockito.when(
			role2.getName()
		).thenReturn(
			"Role Test 2"
		);

		Mockito.when(
			role2.getType()
		).thenReturn(
			RoleConstants.TYPE_ORGANIZATION
		);

		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		UserGroupRole userGroupRole1 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole1.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole1.getRole()
		).thenReturn(
			role1
		);

		userGroupRoles.add(userGroupRole1);

		UserGroupRole userGroupRole2 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole2.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole2.getRole()
		).thenReturn(
			role2
		);

		userGroupRoles.add(userGroupRole2);

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(Mockito.anyLong())
		).thenReturn(
			userGroupRoles
		);

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(),
			"organizationRole:Group Test 1",
			new String[] {"Role Test 1", "Role Test 2"});
	}

	@Test
	public void testResolveOrganizationsAttributes() throws Exception {
		_attributeNamesAtomicReference.set(new String[] {"organizations"});

		List<Organization> organizations = new ArrayList<>();

		Organization organization1 = Mockito.mock(Organization.class);

		Mockito.when(
			organization1.getName()
		).thenReturn(
			"Test 1"
		);

		organizations.add(organization1);

		Organization organization2 = Mockito.mock(Organization.class);

		Mockito.when(
			organization2.getName()
		).thenReturn(
			"Test 2"
		);

		organizations.add(organization2);

		Mockito.when(
			_user.getOrganizations()
		).thenReturn(
			organizations
		);

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(), "organizations",
			new String[] {"Test 1", "Test 2"});
	}

	@Test
	public void testResolveRolesAttributes() throws Exception {
		_attributeNamesAtomicReference.set(new String[] {"roles"});

		List<Role> roles = new ArrayList<>();

		Role role1 = Mockito.mock(Role.class);

		Mockito.when(
			role1.getName()
		).thenReturn(
			"Test 1"
		);

		roles.add(role1);

		Role role2 = Mockito.mock(Role.class);

		Mockito.when(
			role2.getName()
		).thenReturn(
			"Test 2"
		);

		roles.add(role2);

		Mockito.when(
			_user.getRoles()
		).thenReturn(
			roles
		);

		List<Group> groups = new ArrayList<>();

		Group group1 = Mockito.mock(Group.class);

		Mockito.when(
			group1.getName()
		).thenReturn(
			"Group Test 1"
		);

		groups.add(group1);

		Mockito.when(
			_user.getGroups()
		).thenReturn(
			groups
		);

		Mockito.when(
			_roleLocalService.hasGroupRoles(Mockito.anyLong())
		).thenReturn(
			Boolean.TRUE
		);

		List<Role> groupRoles = new ArrayList<>();

		Role groupRole1 = Mockito.mock(Role.class);

		Mockito.when(
			groupRole1.getName()
		).thenReturn(
			"Group Role Test 1"
		);

		groupRoles.add(groupRole1);

		Mockito.when(
			_roleLocalService.getGroupRoles(Mockito.anyLong())
		).thenReturn(
			groupRoles
		);

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(), "roles",
			new String[] {"Test 1", "Test 2", "Group Role Test 1"});
	}

	@Test
	public void testResolveSiteRolesAttributes() throws Exception {
		_attributeNamesAtomicReference.set(new String[] {"siteRoles"});

		Group group1 = Mockito.mock(Group.class);

		Mockito.when(
			group1.getName()
		).thenReturn(
			"Group Test 1"
		);

		Role role1 = Mockito.mock(Role.class);

		Mockito.when(
			role1.getName()
		).thenReturn(
			"Role Test 1"
		);

		Role role2 = Mockito.mock(Role.class);

		Mockito.when(
			role2.getName()
		).thenReturn(
			"Role Test 2"
		);

		Role role3 = Mockito.mock(Role.class);

		Mockito.when(
			role3.getName()
		).thenReturn(
			"Org Role Test"
		);

		Mockito.when(
			role3.getType()
		).thenReturn(
			RoleConstants.TYPE_ORGANIZATION
		);

		Role role4 = Mockito.mock(Role.class);

		Mockito.when(
			role4.getName()
		).thenReturn(
			"Inherited Role Test"
		);

		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		UserGroupRole userGroupRole1 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole1.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole1.getRole()
		).thenReturn(
			role1
		);

		userGroupRoles.add(userGroupRole1);

		UserGroupRole userGroupRole2 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole2.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole2.getRole()
		).thenReturn(
			role2
		);

		userGroupRoles.add(userGroupRole2);

		UserGroupRole userGroupRole3 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole3.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole3.getRole()
		).thenReturn(
			role3
		);

		userGroupRoles.add(userGroupRole3);

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(Mockito.anyLong())
		).thenReturn(
			userGroupRoles
		);

		List<UserGroupGroupRole> userGroupGroupRoles = new ArrayList<>();

		UserGroupGroupRole userGroupGroupRole = Mockito.mock(
			UserGroupGroupRole.class);

		Mockito.when(
			userGroupGroupRole.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupGroupRole.getRole()
		).thenReturn(
			role4
		);

		userGroupGroupRoles.add(userGroupGroupRole);

		Mockito.when(
			_userGroupGroupRoleLocalService.getUserGroupGroupRolesByUser(
				Mockito.anyLong())
		).thenReturn(
			userGroupGroupRoles
		);

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(), "siteRole:Group Test 1",
			new String[] {"Role Test 1", "Role Test 2", "Inherited Role Test"});
	}

	@Test
	public void testResolveStaticAttributes() throws Exception {
		_attributeNamesAtomicReference.set(
			new String[] {
				"static:emailAddress=test@liferay.com",
				"static:screenName=test=test2"
			});

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		List<Attribute> attributes = attributePublisherImpl.getAttributes();

		assertEquals(attributes, "emailAddress", "test@liferay.com");
		assertEquals(attributes, "screenName", "test=test2");
	}

	@Test
	public void testResolveUserAttributes() throws Exception {
		Mockito.when(
			_beanProperties.getObject(
				Mockito.any(User.class), Mockito.eq("emailAddress"))
		).thenReturn(
			"test@liferay.com"
		);

		Mockito.when(
			_beanProperties.getObject(
				Mockito.any(User.class), Mockito.eq("firstName"))
		).thenReturn(
			"Test"
		);

		Mockito.when(
			_beanProperties.getObject(
				Mockito.any(User.class), Mockito.eq("lastName"))
		).thenReturn(
			"Test"
		);

		Mockito.when(
			_beanProperties.getObject(
				Mockito.any(User.class), Mockito.eq("screenName"))
		).thenReturn(
			"test"
		);

		Mockito.when(
			_beanProperties.getObject(
				Mockito.any(User.class), Mockito.eq("uuid"))
		).thenReturn(
			"xxxx-xxxx-xxx-xxxx"
		);

		_attributeNamesAtomicReference.set(
			new String[] {
				"emailAddress", "firstName", "lastName", "screenName", "uuid"
			});

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		List<Attribute> attributes = attributePublisherImpl.getAttributes();

		assertEquals(attributes, "emailAddress", "test@liferay.com");
		assertEquals(attributes, "firstName", "Test");
		assertEquals(attributes, "lastName", "Test");
		assertEquals(attributes, "screenName", "test");
		assertEquals(attributes, "uuid", "xxxx-xxxx-xxx-xxxx");
	}

	@Test
	public void testResolveUserGroupRolesAttributes() throws Exception {
		_attributeNamesAtomicReference.set(new String[] {"userGroupRoles"});

		Group group1 = Mockito.mock(Group.class);

		Mockito.when(
			group1.getName()
		).thenReturn(
			"Group Test 1"
		);

		Role role1 = Mockito.mock(Role.class);

		Mockito.when(
			role1.getName()
		).thenReturn(
			"Role Test 1"
		);

		Role role2 = Mockito.mock(Role.class);

		Mockito.when(
			role2.getName()
		).thenReturn(
			"Role Test 2"
		);

		Role role3 = Mockito.mock(Role.class);

		Mockito.when(
			role3.getName()
		).thenReturn(
			"Org Role Test"
		);

		Mockito.when(
			role3.getType()
		).thenReturn(
			RoleConstants.TYPE_ORGANIZATION
		);

		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		UserGroupRole userGroupRole1 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole1.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole1.getRole()
		).thenReturn(
			role1
		);

		userGroupRoles.add(userGroupRole1);

		UserGroupRole userGroupRole2 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole2.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole2.getRole()
		).thenReturn(
			role2
		);

		userGroupRoles.add(userGroupRole2);

		UserGroupRole userGroupRole3 = Mockito.mock(UserGroupRole.class);

		Mockito.when(
			userGroupRole3.getGroup()
		).thenReturn(
			group1
		);

		Mockito.when(
			userGroupRole3.getRole()
		).thenReturn(
			role3
		);

		userGroupRoles.add(userGroupRole3);

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(Mockito.anyLong())
		).thenReturn(
			userGroupRoles
		);

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(),
			"userGroupRole:Group Test 1",
			new String[] {"Role Test 1", "Role Test 2", "Org Role Test"});
	}

	@FeatureFlag("LPD-66611")
	@Test
	public void testResolveUserGroupsAttributes() throws Exception {
		_attributeNamesAtomicReference.set(new String[] {"userGroups"});

		List<UserGroup> userGroups = new ArrayList<>();

		UserGroup userGroup1 = Mockito.mock(UserGroup.class);

		Mockito.when(
			userGroup1.getName()
		).thenReturn(
			"Test 1"
		);

		userGroups.add(userGroup1);

		UserGroup userGroup2 = Mockito.mock(UserGroup.class);

		Mockito.when(
			userGroup2.getName()
		).thenReturn(
			"Test 2"
		);

		userGroups.add(userGroup2);

		Mockito.when(
			_user.getUserGroups()
		).thenReturn(
			userGroups
		);

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		_defaultAttributeResolver.resolve(
			_user, new AttributeResolverSAMLContextImpl(_messageContext),
			attributePublisherImpl);

		assertEquals(
			attributePublisherImpl.getAttributes(), "membership:userGroups",
			new String[] {"Test 1", "Test 2"});
		assertEquals(
			attributePublisherImpl.getAttributes(), "userGroups",
			new String[] {"Test 1", "Test 2"});
	}

	protected void assertEquals(
		List<Attribute> attributes, String attributeName,
		String attributeValue) {

		Assert.assertEquals(
			attributeValue,
			SamlUtil.getValueAsString(
				SamlUtil.getAttribute(attributes, attributeName)));
	}

	protected void assertEquals(
		List<Attribute> attributes, String attributeName,
		String[] attributeValues) {

		Attribute attribute = SamlUtil.getAttribute(attributes, attributeName);

		Assert.assertNotNull(attribute);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		Assert.assertEquals(
			xmlObjects.toString(), attributeValues.length, xmlObjects.size());

		for (XMLObject xmlObject : xmlObjects) {
			String attributeValue = SamlUtil.getValueAsString(xmlObject);

			Assert.assertTrue(
				ArrayUtil.contains(attributeValues, attributeValue));
		}
	}

	private final AtomicReference<String[]> _attributeNamesAtomicReference =
		new AtomicReference<>();
	private BeanProperties _beanProperties;

	private final DefaultAttributeResolver _defaultAttributeResolver =
		new DefaultAttributeResolver() {

			@Override
			protected String[] getAttributeNames(String entityId) {
				if (Objects.equals(SP_ENTITY_ID, entityId)) {
					return _attributeNamesAtomicReference.get();
				}

				return null;
			}

		};

	private ExpandoBridge _expandoBridge;
	private MessageContext<AuthnRequest> _messageContext;
	private RoleLocalService _roleLocalService;
	private User _user;
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}