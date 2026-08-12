/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.ContactDataCleanupPreupgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ContactDataCleanupPreupgradeProcessTest
	extends ContactDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		_systemEvents = _systemEventLocalService.getSystemEvents(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@After
	public void tearDown() throws Exception {
		for (ClassName className :
				_classNameLocalService.getClassNames(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (!_classNames.contains(className)) {
				_classNameLocalService.deleteClassName(className);
			}
		}

		for (SystemEvent systemEvent :
				_systemEventLocalService.getSystemEvents(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (!_systemEvents.contains(systemEvent)) {
				_systemEventLocalService.deleteSystemEvent(systemEvent);
			}
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		User user = UserTestUtil.addUser();

		long contactId = user.getContactId();
		long userId = user.getUserId();

		_addressLocalService.addAddress(
			null, userId, Contact.class.getName(), contactId, 0,
			_getListTypeId(ListTypeConstants.CONTACT_ADDRESS), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			null, null, null, null, null, serviceContext);

		_emailAddressLocalService.addEmailAddress(
			null, userId, Contact.class.getName(), contactId,
			RandomTestUtil.randomString() + "@liferay.com",
			_getListTypeId(ListTypeConstants.CONTACT_EMAIL_ADDRESS), false,
			serviceContext);

		_phoneLocalService.addPhone(
			null, userId, Contact.class.getName(), contactId,
			RandomTestUtil.randomString(),
			String.valueOf(RandomTestUtil.randomInt()),
			_getListTypeId(ListTypeConstants.CONTACT_PHONE), false,
			serviceContext);

		_websiteLocalService.addWebsite(
			null, userId, Contact.class.getName(), contactId,
			"http://www.example.com",
			_getListTypeId(ListTypeConstants.CONTACT_WEBSITE), false,
			serviceContext);

		runSQL("delete from Contact_ where contactId = " + contactId);

		try {
			upgrade();

			_assetContactData(contactId, "Address");
			_assetContactData(contactId, "EmailAddress");
			_assetContactData(contactId, "Phone");
			_assetContactData(contactId, "Website");
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private void _assetContactData(long classPK, String tableName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select * from ", tableName, " where classNameId = ? and ",
					"classPK = ?"))) {

			preparedStatement.setLong(
				1, _classNameLocalService.getClassNameId(Contact.class));
			preparedStatement.setLong(2, classPK);

			ResultSet resultSet = preparedStatement.executeQuery();

			Assert.assertFalse(resultSet.next());
		}
	}

	private long _getListTypeId(String listTypeId) throws Exception {
		List<ListType> listTypes = _listTypeLocalService.getListTypes(
			TestPropsValues.getCompanyId(), listTypeId);

		ListType listType = listTypes.get(0);

		return listType.getListTypeId();
	}

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private List<ClassName> _classNames;

	@Inject
	private EmailAddressLocalService _emailAddressLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject
	private PhoneLocalService _phoneLocalService;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	private List<SystemEvent> _systemEvents;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WebsiteLocalService _websiteLocalService;

}