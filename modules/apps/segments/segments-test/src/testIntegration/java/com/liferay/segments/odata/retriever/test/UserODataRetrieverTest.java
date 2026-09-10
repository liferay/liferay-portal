/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.odata.retriever.test;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.odata.retriever.ODataRetriever;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class UserODataRetrieverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_companyGuestGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);
		_companyUser = UserTestUtil.getAdminUser(
			TestPropsValues.getCompanyId());
	}

	@Before
	public void setUp() throws Exception {
		_group1 = _addGroup();
		_group2 = _addGroup();
	}

	@Test
	public void testGetUserPrimaryKeysWithMoreUsersThanElasticsearchMaxResultWindow()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"INDEX_SEARCH_LIMIT", _ELASTICSEARCH_MAX_RESULT_WINDOW)) {

			String firstName = RandomTestUtil.randomString();

			Set<Long> expectedUserIds = new HashSet<>();

			for (int i = 0;
				 i < _MORE_USERS_THAN_ELASTICSEARCH_MAX_RESULT_WINDOW; i++) {

				User user = _addUser(firstName, _group1);

				expectedUserIds.add(user.getUserId());
			}

			long[] primaryKeys = _oDataRetriever.getResultPrimaryKeys(
				_group1.getCompanyId(),
				String.format("(firstName eq '%s')", firstName),
				LocaleUtil.getDefault(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertEquals(
				Arrays.toString(primaryKeys),
				_MORE_USERS_THAN_ELASTICSEARCH_MAX_RESULT_WINDOW,
				primaryKeys.length);
			Assert.assertEquals(
				expectedUserIds, SetUtil.fromArray(primaryKeys));
		}
	}

	@Test
	public void testGetUsersFilterByAncestorOrganizationIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Organization parentOrganization =
			_organizationLocalService.addOrganization(
				_companyUser.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomBoolean());

		Organization organization = _organizationLocalService.addOrganization(
			_companyUser.getUserId(), parentOrganization.getOrganizationId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomBoolean());

		_organizations.add(organization);

		_organizations.add(parentOrganization);

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), _user1);

		String filterString = String.format(
			"(firstName eq '%s') and (ancestorOrganizationIds eq '%s')",
			firstName, parentOrganization.getOrganizationId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByAssetTagIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Company company = _companyLocalService.getCompany(
			_user1.getCompanyId());

		AssetTag tag = AssetTestUtil.addTag(company.getGroupId(), "tag1");

		_assetTags.add(tag);

		_userLocalService.updateAsset(
			_companyUser.getUserId(), _user1, new long[0],
			new String[] {tag.getName()});

		String filterString = String.format(
			"(firstName eq '%s') and (assetTagIds eq '%s')", firstName,
			tag.getTagId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByBirthDateEquals() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date birthDate = _user1.getBirthday();

		Instant instant = birthDate.toInstant();

		_updateUserBirthday(
			_user2, Date.from(instant.plus(2, ChronoUnit.DAYS)));

		String filterString = String.format(
			"(birthDate eq %s) and (firstName eq '%s')", _toISOFormat(instant),
			firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByBirthDateGreater() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date birthDate = _user1.getBirthday();

		Instant instant = birthDate.toInstant();

		_updateUserBirthday(
			_user2, Date.from(instant.plus(2, ChronoUnit.DAYS)));

		String filterString = String.format(
			"(birthDate gt %s) and (firstName eq '%s')", _toISOFormat(instant),
			firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user2, users.get(0));
	}

	@Test
	public void testGetUsersFilterByBirthDateGreaterOrEquals()
		throws Exception {

		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date birthDate = _user1.getBirthday();

		Instant instant = birthDate.toInstant();

		_updateUserBirthday(
			_user2, Date.from(instant.plus(2, ChronoUnit.DAYS)));

		String filterString = String.format(
			"(birthDate ge %s) and (firstName eq '%s')", _toISOFormat(instant),
			firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(2, count);
	}

	@Test
	public void testGetUsersFilterByBirthDateLower() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date birthDate = _user1.getBirthday();

		Instant instant = birthDate.toInstant();

		_updateUserBirthday(
			_user2, Date.from(instant.minus(2, ChronoUnit.DAYS)));

		String filterString = String.format(
			"(birthDate lt %s) and (firstName eq '%s')", _toISOFormat(instant),
			firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user2, users.get(0));
	}

	@Test
	public void testGetUsersFilterByBirthDateLowerOrEquals() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date birthDate = _user1.getBirthday();

		Instant instant = birthDate.toInstant();

		_updateUserBirthday(
			_user2, Date.from(instant.minus(2, ChronoUnit.DAYS)));

		String filterString = String.format(
			"(birthDate le %s) and (firstName eq '%s')", _toISOFormat(instant),
			firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(2, count);
	}

	@Test
	public void testGetUsersFilterByCompanyId() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);

		String filterString = String.format(
			"(firstName eq '%s') and (companyId eq '%s')", firstName,
			_group1.getCompanyId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByDateModifiedEquals() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date modifiedDate = _user1.getModifiedDate();

		Instant instant = modifiedDate.toInstant();

		_user2.setModifiedDate(Date.from(instant.plusSeconds(1)));

		_user2 = _userLocalService.updateUser(_user2);

		String filterString = String.format(
			"(dateModified eq %s) and (firstName eq '%s')",
			ISO8601Utils.format(_user1.getModifiedDate()), firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByDateModifiedGreater() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date modifiedDate = _user1.getModifiedDate();

		Instant instant = modifiedDate.toInstant();

		_user2.setModifiedDate(Date.from(instant.plusSeconds(1)));

		_user2 = _userLocalService.updateUser(_user2);

		String filterString = String.format(
			"(dateModified gt %s) and (firstName eq '%s')",
			ISO8601Utils.format(_user1.getModifiedDate()), firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user2, users.get(0));
	}

	@Test
	public void testGetUsersFilterByDateModifiedGreaterOrEquals()
		throws Exception {

		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date modifiedDate = _user1.getModifiedDate();

		Instant instant = modifiedDate.toInstant();

		_user2.setModifiedDate(Date.from(instant.plusSeconds(1)));

		_user2 = _userLocalService.updateUser(_user2);

		String filterString = String.format(
			"(dateModified ge %s) and (firstName eq '%s')",
			ISO8601Utils.format(_user2.getModifiedDate()), firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user2, users.get(0));
	}

	@Test
	public void testGetUsersFilterByDateModifiedLower() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date modifiedDate = _user1.getModifiedDate();

		Instant instant = modifiedDate.toInstant();

		_user2.setModifiedDate(Date.from(instant.plusSeconds(1)));

		_user2 = _userLocalService.updateUser(_user2);

		String filterString = String.format(
			"(dateModified lt %s) and (firstName eq '%s')",
			ISO8601Utils.format(_user2.getModifiedDate()), firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByDateModifiedLowerOrEquals()
		throws Exception {

		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Date modifiedDate = _user1.getModifiedDate();

		Instant instant = modifiedDate.toInstant();

		_user2.setModifiedDate(Date.from(instant.plusSeconds(1)));

		_user2 = _userLocalService.updateUser(_user2);

		String filterString = String.format(
			"(dateModified le %s) and (firstName eq '%s')",
			ISO8601Utils.format(modifiedDate), firstName);

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByEmailAddress() throws Exception {
		_user1 = _addUser(_group1);

		String filterString =
			"(emailAddress eq '" + _user1.getEmailAddress() + "')";

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByFirstName() throws Exception {
		_user1 = _addUser(_group1);

		String filterString = "(firstName eq '" + _user1.getFirstName() + "')";

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByFirstNameAndLastName() throws Exception {
		_user1 = _addUser(_group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", _user1.getFirstName(), "') and (lastName eq ",
			"'", _user1.getLastName(), "')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByFirstNameAndNotTeamIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		_team = _addTeam();

		_userLocalService.addTeamUser(_team.getTeamId(), _user1);

		String filterString = String.format(
			"(firstName eq '%s') and (not (teamIds eq '%s'))", firstName,
			_team.getTeamId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			1);

		Assert.assertEquals(_user2, users.get(0));
	}

	@Test
	public void testGetUsersFilterByFirstNameOrLastName() throws Exception {
		_user1 = _addUser(_group1);
		_user2 = _addUser(_group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", _user1.getFirstName(), "') or (lastName eq '",
			_user2.getLastName(), "')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(2, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertTrue(users.contains(_user1));
		Assert.assertTrue(users.contains(_user2));
	}

	@Test
	public void testGetUsersFilterByFirstNameOrLastNameWithSameFirstName()
		throws Exception {

		_user1 = _addUser(_group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", _user1.getFirstName(), "') or (lastName eq ",
			"'nonexistentLastName')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByFirstNameOrLastNameWithSameFirstNameAndLastName()
		throws Exception {

		_user1 = _addUser(_group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", _user1.getFirstName(), "') or (lastName eq '",
			_user1.getLastName(), "')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByGroupId() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(
			firstName, new long[] {_group1.getGroupId(), _group2.getGroupId()});
		_user2 = _addUser(firstName, _group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", firstName, "') and (groupId eq '",
			_group2.getGroupId(), "')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByGroupIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(
			firstName, new long[] {_group1.getGroupId(), _group2.getGroupId()});
		_user2 = _addUser(firstName, _group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", firstName, "') and (groupIds eq '",
			_group2.getGroupId(), "')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByGroupIdsWithOr() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(
			firstName, new long[] {_group1.getGroupId(), _group2.getGroupId()});
		_user2 = _addUser(firstName, _group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", firstName, "') and ((groupIds eq '",
			_group2.getGroupId(), "') or (groupIds eq '", _group1.getGroupId(),
			"'))");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(2, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertTrue(users.contains(_user1));
		Assert.assertTrue(users.contains(_user2));
	}

	@Test
	public void testGetUsersFilterByGroupIdWithAnd() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(
			firstName, new long[] {_group1.getGroupId(), _group2.getGroupId()});
		_user2 = _addUser(firstName, _group1);

		String filterString = String.format(
			"(firstName eq '%s') and (groupId eq '%s') and (groupId eq '%s')",
			firstName, _group1.getGroupId(), _group2.getGroupId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByJobTitle() throws Exception {
		_user1 = _addUser(_group1);

		_user1.setJobTitle(RandomTestUtil.randomString());

		_user1 = _userLocalService.updateUser(_user1);

		_user2 = _addUser(_group1);

		String filterString = "(jobTitle eq '" + _user1.getJobTitle() + "')";

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByJobTitleContains() throws Exception {
		_user1 = _addUser(_group1);

		String jobTitlePrefix = RandomTestUtil.randomString();

		_user1.setJobTitle(jobTitlePrefix + RandomTestUtil.randomString());

		_user1 = _userLocalService.updateUser(_user1);

		_user2 = _addUser(_group1);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(),
			"contains(jobTitle, '" + jobTitlePrefix + "')",
			LocaleUtil.getDefault(), 0, 2);

		Assert.assertEquals(users.toString(), 1, users.size());
		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByLastName() throws Exception {
		_user1 = _addUser(_group1);
		_user2 = _addUser(_group1);

		String filterString = "(lastName eq '" + _user1.getLastName() + "')";

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByMultipleGroupIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(
			firstName, new long[] {_group1.getGroupId(), _group2.getGroupId()});
		_user2 = _addUser(firstName, _group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", firstName, "') and (groupIds eq '",
			_group2.getGroupId(), "') and (groupIds eq '", _group1.getGroupId(),
			"')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByOrganizationIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		Organization organization = _organizationLocalService.addOrganization(
			_companyUser.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomBoolean());

		_organizations.add(organization);

		_userLocalService.addOrganizationUsers(
			organization.getOrganizationId(), new long[] {_user1.getUserId()});

		String filterString = String.format(
			"(firstName eq '%s') and (organizationIds eq '%s')", firstName,
			organization.getOrganizationId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByRoleIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(_role.getRoleId(), _user1);

		String filterString = String.format(
			"(firstName eq '%s') and (roleIds eq '%s')", firstName,
			_role.getRoleId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByScopeGroupId() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(
			firstName, new long[] {_group1.getGroupId(), _group2.getGroupId()});
		_user2 = _addUser(firstName, _group1);

		String filterString = StringBundler.concat(
			"(firstName eq '", firstName, "') and (scopeGroupId eq '",
			_group2.getGroupId(), "')");

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByScreenName() throws Exception {
		_user1 = _addUser(_group1);
		_user2 = _addUser(_group1);

		String filterString =
			"(screenName eq '" + _user1.getScreenName() + "')";

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByTeamIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		_team = _addTeam();

		_userLocalService.addTeamUser(_team.getTeamId(), _user1);

		_userGroup = UserGroupTestUtil.addUserGroup(
			_companyGuestGroup.getGroupId());

		_userGroupLocalService.addTeamUserGroups(
			_team.getTeamId(), new long[] {_userGroup.getUserGroupId()});

		_userLocalService.addUserGroupUser(_userGroup.getUserGroupId(), _user2);

		String filterString = String.format(
			"(firstName eq '%s') and (teamIds eq '%s')", firstName,
			_team.getTeamId());

		Assert.assertEquals(
			2,
			_oDataRetriever.getResultsCount(
				_group1.getCompanyId(), filterString, LocaleUtil.getDefault()));

		List<User> results = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertTrue(results.contains(_user1));
		Assert.assertTrue(results.contains(_user2));

		_userGroupLocalService.unsetTeamUserGroups(
			_team.getTeamId(), new long[] {_userGroup.getUserGroupId()});

		Assert.assertEquals(
			1,
			_oDataRetriever.getResultsCount(
				_group1.getCompanyId(), filterString, LocaleUtil.getDefault()));

		results = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertTrue(results.contains(_user1));
		Assert.assertFalse(results.contains(_user2));
	}

	@Test
	public void testGetUsersFilterByUserGroupIds() throws Exception {
		String firstName = RandomTestUtil.randomString();

		_user1 = _addUser(firstName, _group1);
		_user2 = _addUser(firstName, _group1);

		_userGroup = UserGroupTestUtil.addUserGroup(
			_companyGuestGroup.getGroupId());

		_userLocalService.addUserGroupUser(_userGroup.getUserGroupId(), _user1);

		String filterString = String.format(
			"(firstName eq '%s') and (userGroupIds eq '%s')", firstName,
			_userGroup.getUserGroupId());

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByUserId() throws Exception {
		_user1 = _addUser(_group1);

		String filterString = "(userId eq '" + _user1.getUserId() + "')";

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersFilterByUserName() throws Exception {
		_user1 = _addUser(_group1);
		_user2 = _addUser(_group1);

		String filterString =
			"(userName eq '" + StringUtil.toLowerCase(_user1.getFullName()) +
				"')";

		int count = _oDataRetriever.getResultsCount(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault());

		Assert.assertEquals(1, count);

		List<User> users = _oDataRetriever.getResults(
			_group1.getCompanyId(), filterString, LocaleUtil.getDefault(), 0,
			2);

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetUsersWithMoreUsersThanElasticsearchMaxResultWindow()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"INDEX_SEARCH_LIMIT", _ELASTICSEARCH_MAX_RESULT_WINDOW)) {

			String firstName = RandomTestUtil.randomString();

			for (int i = 0;
				 i < _MORE_USERS_THAN_ELASTICSEARCH_MAX_RESULT_WINDOW; i++) {

				_addUser(firstName, _group1);
			}

			String filterString = String.format(
				"(firstName eq '%s')", firstName);

			List<User> users = _oDataRetriever.getResults(
				_group1.getCompanyId(), filterString, LocaleUtil.getDefault(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertEquals(
				users.toString(),
				_MORE_USERS_THAN_ELASTICSEARCH_MAX_RESULT_WINDOW, users.size());
		}
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), _companyUser.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_groups.add(group);

		return group;
	}

	private Team _addTeam() throws Exception {
		return _teamLocalService.addTeam(
			_companyUser.getUserId(), _companyGuestGroup.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private User _addUser(Group group) throws Exception {
		return _addUser(RandomTestUtil.randomString(), group);
	}

	private User _addUser(String firstName, Group group) throws Exception {
		return _addUser(firstName, new long[] {group.getGroupId()});
	}

	private User _addUser(String firstName, long... groupIds) throws Exception {
		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), _companyUser.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), firstName, RandomTestUtil.randomString(),
			groupIds, ServiceContextTestUtil.getServiceContext());

		_users.add(user);

		return user;
	}

	private String _toISOFormat(Instant instant) {
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		LocalDate localDate = zonedDateTime.toLocalDate();

		return localDate.format(DateTimeFormatter.ISO_DATE);
	}

	private void _updateUserBirthday(User user, Date birthDate)
		throws Exception {

		Contact contact = user.getContact();

		contact.setBirthday(birthDate);

		_contactLocalService.updateContact(contact);

		_userLocalService.updateUser(_user2);
	}

	private static final int _ELASTICSEARCH_MAX_RESULT_WINDOW = 10;

	private static final int _MORE_USERS_THAN_ELASTICSEARCH_MAX_RESULT_WINDOW =
		_ELASTICSEARCH_MAX_RESULT_WINDOW * 3;

	private static Group _companyGuestGroup;
	private static User _companyUser;

	@Inject
	private static GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private final List<AssetTag> _assetTags = new ArrayList<>();

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ContactLocalService _contactLocalService;

	private Group _group1;
	private Group _group2;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject(filter = "model.class.name=com.liferay.portal.kernel.model.User")
	private ODataRetriever<User> _oDataRetriever;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private Team _team;

	@Inject
	private TeamLocalService _teamLocalService;

	private User _user1;
	private User _user2;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}