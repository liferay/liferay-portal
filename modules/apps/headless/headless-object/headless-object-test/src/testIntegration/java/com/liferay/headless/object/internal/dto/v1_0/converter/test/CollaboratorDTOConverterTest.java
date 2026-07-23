/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.dto.v1_0.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.object.dto.v1_0.Collaborator;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class CollaboratorDTOConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectDefinition = _addObjectDefinition();

		_objectEntry = _addObjectEntry();
	}

	@Test
	@TestInfo("LPD-48130")
	public void testToDTO() throws Exception {
		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";
		Date expirationDate = new Date(System.currentTimeMillis() + Time.DAY);

		Ticket ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), _objectEntry.getModelClassName(),
			_objectEntry.getObjectEntryId(),
			TicketConstants.TYPE_INVITE_COLLABORATOR, emailAddress, null,
			expirationDate, null);

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), ticket.getTicketId(), 0, 0,
			_classNameLocalService.getClassNameId(
				_objectEntry.getModelClassName()),
			_objectEntry.getObjectEntryId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), expirationDate,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_tickets.add(ticket);

		_sharingEntries.add(sharingEntry);

		Collaborator collaborator = _toDTO(sharingEntry);

		Assert.assertArrayEquals(
			new String[] {SharingEntryAction.VIEW.getActionId()},
			collaborator.getActionIds());
		Assert.assertEquals(expirationDate, collaborator.getDateExpired());
		Assert.assertEquals(emailAddress, collaborator.getEmailAddress());
		Assert.assertNull(collaborator.getExternalReferenceCode());
		Assert.assertNull(collaborator.getId());
		Assert.assertEquals(emailAddress, collaborator.getName());
		Assert.assertNull(collaborator.getPortrait());
		Assert.assertTrue(collaborator.getShare());
		Assert.assertEquals("Email", collaborator.getType());

		User user1 = UserTestUtil.addUser();

		expirationDate = new Date(System.currentTimeMillis() + Time.DAY);

		sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, user1.getUserId(),
			_classNameLocalService.getClassNameId(
				_objectEntry.getModelClassName()),
			_objectEntry.getObjectEntryId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), expirationDate,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_sharingEntries.add(sharingEntry);

		collaborator = _toDTO(sharingEntry);

		Assert.assertArrayEquals(
			new String[] {SharingEntryAction.VIEW.getActionId()},
			collaborator.getActionIds());
		Assert.assertEquals(expirationDate, collaborator.getDateExpired());
		Assert.assertEquals(
			user1.getEmailAddress(), collaborator.getEmailAddress());
		Assert.assertEquals(
			user1.getExternalReferenceCode(),
			collaborator.getExternalReferenceCode());
		Assert.assertEquals(
			Long.valueOf(user1.getUserId()), collaborator.getId());
		Assert.assertEquals(user1.getFullName(), collaborator.getName());
		Assert.assertNull(collaborator.getPortrait());
		Assert.assertTrue(collaborator.getShare());
		Assert.assertEquals("User", collaborator.getType());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User user2 = company.getGuestUser();

		String name = PrincipalThreadLocal.getName();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PrincipalThreadLocal.setName(user2.getUserId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user2));

			collaborator = _toDTO(sharingEntry);

			Assert.assertArrayEquals(
				new String[] {SharingEntryAction.VIEW.getActionId()},
				collaborator.getActionIds());
			Assert.assertEquals(expirationDate, collaborator.getDateExpired());
			Assert.assertEquals(
				user1.getEmailAddress(), collaborator.getEmailAddress());
			Assert.assertNull(collaborator.getExternalReferenceCode());
			Assert.assertNull(collaborator.getId());
			Assert.assertEquals(
				user1.getEmailAddress(), collaborator.getName());
			Assert.assertNull(collaborator.getPortrait());
			Assert.assertTrue(collaborator.getShare());
			Assert.assertEquals("Email", collaborator.getType());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			PrincipalThreadLocal.setName(name);
		}

		user1.setPortraitId(RandomTestUtil.nextLong());

		_userLocalService.updateUser(user1);

		collaborator = _toDTO(sharingEntry);

		Assert.assertNotNull(collaborator.getPortrait());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		expirationDate = new Date(System.currentTimeMillis() + Time.DAY);

		sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, userGroup.getUserGroupId(), 0,
			_classNameLocalService.getClassNameId(
				_objectEntry.getModelClassName()),
			_objectEntry.getObjectEntryId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), expirationDate,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_sharingEntries.add(sharingEntry);

		collaborator = _toDTO(sharingEntry);

		Assert.assertArrayEquals(
			new String[] {SharingEntryAction.VIEW.getActionId()},
			collaborator.getActionIds());
		Assert.assertEquals(expirationDate, collaborator.getDateExpired());
		Assert.assertNull(collaborator.getEmailAddress());
		Assert.assertEquals(
			userGroup.getExternalReferenceCode(),
			collaborator.getExternalReferenceCode());
		Assert.assertEquals(
			Long.valueOf(userGroup.getUserGroupId()), collaborator.getId());
		Assert.assertEquals(userGroup.getName(), collaborator.getName());
		Assert.assertNull(collaborator.getPortrait());
		Assert.assertTrue(collaborator.getShare());
		Assert.assertEquals("UserGroup", collaborator.getType());
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		return ObjectDefinitionTestUtil.publishObjectDefinition(
			"A" + RandomTestUtil.randomString(),
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).indexed(
					true
				).indexedAsKeyword(
					true
				).name(
					"title"
				).localized(
					false
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE, TestPropsValues.getUserId());
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private Collaborator _toDTO(SharingEntry sharingEntry) throws Exception {
		DTOConverter<SharingEntry, Collaborator> dtoConverter =
			(DTOConverter<SharingEntry, Collaborator>)
				_dtoConverterRegistry.getDTOConverter(
					Collaborator.class.getName());

		return dtoConverter.toDTO(
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, sharingEntry.getSharingEntryId(),
				LocaleUtil.getDefault(), null, null),
			sharingEntry);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private final List<SharingEntry> _sharingEntries = new ArrayList<>();

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

	@DeleteAfterTestRun
	private final List<Ticket> _tickets = new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

}