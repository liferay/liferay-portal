/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class UserModelListenerTest extends BaseModelListenerTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testOnAfterAddAssociation() throws Exception {
		_updateUser(new long[] {cmpProjectObjectEntry.getGroupId()});

		assertAuditMessage("CMP_ADD_MEMBER");
	}

	@Test
	public void testOnAfterRemoveAssociation() throws Exception {
		_updateUser(new long[] {cmpProjectObjectEntry.getGroupId()});

		assertAuditMessage("CMP_ADD_MEMBER");

		_updateUser(new long[] {TestPropsValues.getGroupId()});

		assertAuditMessage("CMP_REMOVE_MEMBER");
	}

	@Override
	protected ModelListener<User> getModelListener() {
		return _userModelListener;
	}

	private void _updateUser(long[] groupIds) throws Exception {
		Contact contact = _user.getContact();

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(_user.getBirthday());

		_userLocalService.updateUser(
			_user.getUserId(), _user.getPassword(), null, null,
			_user.isPasswordReset(), null, null, _user.getScreenName(),
			_user.getEmailAddress(), true, null, _user.getLanguageId(),
			_user.getTimeZoneId(), _user.getGreeting(), _user.getComments(),
			_user.getFirstName(), _user.getMiddleName(), _user.getLastName(),
			contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
			_user.isMale(), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
			contact.getSmsSn(), contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(), _user.getJobTitle(),
			groupIds, _user.getOrganizationIds(), null, null,
			_user.getUserGroupIds(),
			ServiceContextTestUtil.getServiceContext());
	}

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.model.listener.UserModelListener"
	)
	private ModelListener<User> _userModelListener;

}