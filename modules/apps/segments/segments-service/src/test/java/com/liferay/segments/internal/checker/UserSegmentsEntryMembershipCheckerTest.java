/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.checker;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Marcos Martins
 */
public class UserSegmentsEntryMembershipCheckerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsMemberContains() throws Exception {
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(customField/_00001_test, 'test1 test1'))",
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(customField/_00001_test, 'test1-/ÖÀñ'))",
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(emailAddress, '@test.com'))", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(firstName, 'Testing'))", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(firstName, 'Testing ÖÀñ'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(customField/_00001_test, 'tes'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(emailAddress, '@liferay.com'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(firstName, 'Tes'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(firstName, 'Test'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(contains(firstName, 'tes'))", _userAttributes));
	}

	@Test
	public void testIsMemberEquals() throws Exception {
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(customField/_00001_test eq 'test1 test1 %#*&')",
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(customField/_00001_test eq 'test1-ÖÀñ')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(customField/_00002_test eq false)", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(emailAddress eq 'user@liferay.com')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(jobTitle eq 'aaa')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(jobTitle eq 'aaa bbb - ÖÀñ')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(roleIds eq '2')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(userGroupIds eq '2')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(userId eq '2')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(customField/_00001_test eq 'custom test')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(customField/_00002_test eq true)", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified eq " +
					_dateFormat.format(_userAttributes.get("modifiedDate")) +
						")",
				_userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(emailAddress eq 'test@liferay.com')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(firstName eq 'Test')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(firstName eq 'test')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(jobTitle eq 'Test')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(jobTitle eq 'test')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(roleIds eq '1')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(userGroupIds eq '1')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(userId eq '1')", _userAttributes));
	}

	@Test
	public void testIsMemberGreaterThan() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new Date());

		calendar.add(Calendar.DAY_OF_MONTH, 1);

		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified gt " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));

		calendar.add(Calendar.DAY_OF_MONTH, -4);

		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified gt " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));
	}

	@Test
	public void testIsMemberGreaterThanOrEquals() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new Date());

		calendar.add(Calendar.DAY_OF_MONTH, 1);

		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified ge " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));

		calendar.add(Calendar.DAY_OF_MONTH, -4);

		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified ge " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));

		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified ge " +
					_dateFormat.format(_userAttributes.get("modifiedDate")) +
						")",
				_userAttributes));
	}

	@Test
	public void testIsMemberIn() throws Exception {
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(organizationIds in ('2'))", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (organizationIds in ('1'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(organizationIds in ('1'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (organizationIds in ('2'))", _userAttributes));
	}

	@Test
	public void testIsMemberLessThan() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new Date());

		calendar.add(Calendar.DAY_OF_MONTH, -1);

		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified lt " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));

		calendar.add(Calendar.DAY_OF_MONTH, 4);

		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified lt " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));
	}

	@Test
	public void testIsMemberLessThanOrEquals() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new Date());

		calendar.add(Calendar.DAY_OF_MONTH, -1);

		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified le " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));

		calendar.add(Calendar.DAY_OF_MONTH, 4);

		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified le " + _dateFormat.format(calendar.getTime()) +
					")",
				_userAttributes));

		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"(dateModified le " +
					_dateFormat.format(_userAttributes.get("modifiedDate")) +
						")",
				_userAttributes));
	}

	@Test
	public void testIsMemberLogicalOperators() throws Exception {
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				String.join(
					StringPool.BLANK,
					"(((lastName eq 'test' or (not (dateModified eq ",
					"2025-01-08T00:00:00.000Z)) or jobTitle eq 'Test')) and ",
					"((userId eq '0'))) and (classPK eq CLASS_PK)"),
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				String.join(
					StringPool.BLANK, "(contains(emailAddress, 'liferay') ",
					"and (not (emailAddress eq 'test@liferay.com')))"),
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				String.join(
					StringPool.BLANK, "(segmentEntryIds eq '30' or ",
					"segmentEntryIds eq '31') and (segmentEntryIds eq '32')"),
				_userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				String.join(
					StringPool.BLANK, "(((lastName eq 'test' or (not ",
					"(dateModified eq 2025-01-08T00:00:00.000Z)) or jobTitle ",
					"eq 'Test')) and ((userId eq '1'))) and (classPK eq ",
					"CLASS_PK)"),
				_userAttributes));
	}

	@Test
	public void testIsMemberNotContains() throws Exception {
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (contains(customField/_00001_test, 'tes'))",
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (contains(emailAddress, '@liferay.com'))",
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (contains(firstName, 'Tes'))", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (contains(firstName, 'Test'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (contains(customField/_00001_test, 'test1'))",
				_userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (contains(emailAddress, '@test.com'))", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (contains(firstName, 'Testing'))", _userAttributes));
	}

	@Test
	public void testIsMemberNotEquals() throws Exception {
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (customField/_00001_test eq 'custom test')",
				_userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (emailAddress eq 'test@liferay.com')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (jobTitle eq 'test')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (roleIds eq '1')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (userGroupIds eq '1')", _userAttributes));
		Assert.assertFalse(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (userId eq '1')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (customField/_00001_test eq 'test1')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (emailAddress eq 'user@liferay.com')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (jobTitle eq 'aaa')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (roleIds eq '2')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (userGroupIds eq '2')", _userAttributes));
		Assert.assertTrue(
			UserSegmentsEntryMembershipChecker.isMember(
				"not (userId eq '2')", _userAttributes));
	}

	private static final DateFormat _dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private final Map<String, Object> _userAttributes =
		HashMapBuilder.<String, Object>put(
			"assetCategoryIds", new long[] {1}
		).put(
			"assetTagIds", new long[] {1}
		).put(
			"classPK", 1
		).put(
			"customField/_00001_test", "custom test"
		).put(
			"customField/_00002_test", true
		).put(
			"emailAddress", "test@liferay.com"
		).put(
			"firstName", "Test"
		).put(
			"groupIds", new long[] {1}
		).put(
			"jobTitle", "test"
		).put(
			"lastLoginDate", new Date()
		).put(
			"lastName", "Test"
		).put(
			"loginDate", new Date()
		).put(
			"modifiedDate", new Date()
		).put(
			"organizationIds", new long[] {1}
		).put(
			"roleIds", new long[] {1}
		).put(
			"screenName", "test"
		).put(
			"segmentEntryIds", new long[] {1}
		).put(
			"teamIds", new long[] {1}
		).put(
			"userGroupIds", new long[] {1}
		).put(
			"userGroupRoleIds", new long[] {1}
		).put(
			"userId", 1
		).build();

}