/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.ExpandoTableSearchFixture;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.OrganizationBlueprint.OrganizationBlueprintBuilder;
import com.liferay.users.admin.test.util.search.OrganizationSearchFixture;
import com.liferay.users.admin.test.util.search.UserGroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class UserIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_setUpExpandoTableSearchFixture();
		_setUpIndexedFieldsFixture();
		_setUpIndexerFixture();
		_setUpUserSearchFixture();
	}

	@Test
	public void testAddress() throws Exception {
		User user1 = _addUser();

		_userSearchFixture.addAddress(user1);

		User user2 = _userLocalService.updateUser(user1);

		String searchTerm = user2.getFirstName();

		Document document = _indexerFixture.searchOnlyOne(searchTerm);

		_indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> map = _getExpectedFieldValues(user2);

		_populateAddressFieldValues(user2, map);

		FieldValuesAssert.assertFieldValues(
			document, map,
			name ->
				!name.contains(StringPool.PERIOD) && !name.equals("timestamp"),
			searchTerm);
	}

	@Test
	public void testCustomField() throws Exception {
		_expandoTableSearchFixture.addExpandoColumn(
			User.class, ExpandoColumnConstants.INDEX_TYPE_KEYWORD,
			"customField");

		User user = _addUser();

		ExpandoBridge expandoBridge = user.getExpandoBridge();

		String customFieldValue = RandomTestUtil.randomString();

		expandoBridge.setAttribute("customField", customFieldValue);

		try (SafeCloseable safeCloseable =
				ReindexCacheThreadLocal.openReindexMode()) {

			_indexerFixture.reindexCompany(user.getCompanyId());
		}

		Document document = _indexerFixture.searchOnlyOne(user.getFirstName());

		Assert.assertEquals(
			customFieldValue,
			document.get("expando__keyword__custom_fields__customField"));
	}

	@Test
	public void testJobTitle() throws Exception {
		User user1 = _addUser();

		user1.setJobTitle(RandomTestUtil.randomString());

		User user2 = _userLocalService.updateUser(user1);

		String searchTerm = user2.getFirstName();

		Document document = _indexerFixture.searchOnlyOne(searchTerm);

		_indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> map = _getExpectedFieldValues(user2);

		map.put("jobTitle", user2.getJobTitle());
		map.put(
			"jobTitle_sortable", StringUtil.toLowerCase(user2.getJobTitle()));

		FieldValuesAssert.assertFieldValues(
			document, map,
			name ->
				!name.contains(StringPool.PERIOD) && !name.equals("timestamp"),
			searchTerm);
	}

	@Test
	public void testOrganizationIds() throws Exception {
		Organization organization = _addOrganization();

		User user = _addUser();

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), user.getUserId());

		String searchTerm = user.getFirstName();

		Document document = _indexerFixture.searchOnlyOne(searchTerm);

		_indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> map = _getExpectedFieldValues(user);

		map.put("organizationIds", _getStringValue(user.getOrganizationIds()));

		FieldValuesAssert.assertFieldValues(
			document, map,
			name ->
				!name.contains(StringPool.PERIOD) && !name.equals("timestamp"),
			searchTerm);
	}

	@Test
	public void testUserGroupIds() throws Exception {
		User user = _addUser();

		UserGroup userGroup = _userGroupSearchFixture.addUserGroup(
			UserGroupSearchFixture.getTestUserGroupBlueprintBuilder());

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_userGroupLocalService.addGroupUserGroup(
			_group.getGroupId(), userGroup);

		String searchTerm = user.getFirstName();

		Document document = _indexerFixture.searchOnlyOne(searchTerm);

		_indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> map = _getExpectedFieldValues(user);

		map.put("userGroupIds", _getStringValue(user.getUserGroupIds()));

		FieldValuesAssert.assertFieldValues(
			document, map,
			name ->
				!name.contains(StringPool.PERIOD) && !name.equals("timestamp"),
			searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private Organization _addOrganization() {
		OrganizationBlueprintBuilder organizationBlueprintBuilder =
			OrganizationSearchFixture.getTestOrganizationBlueprintBuilder();

		return _organizationSearchFixture.addOrganization(
			organizationBlueprintBuilder.build());
	}

	private User _addUser() throws Exception {
		return _userSearchFixture.addUser(
			RandomTestUtil.randomString(), _group, new String[0]);
	}

	private String _getEmailAddressDomain(String emailAddress) {
		return emailAddress.substring(emailAddress.indexOf(StringPool.AT) + 1);
	}

	private Map<String, String> _getExpectedFieldValues(User user)
		throws Exception {

		String groupId = String.valueOf(user.getGroupIds()[0]);

		Map<String, String> map = HashMapBuilder.put(
			Field.COMPANY_ID, String.valueOf(user.getCompanyId())
		).put(
			Field.ENTRY_CLASS_NAME, User.class.getName()
		).put(
			Field.ENTRY_CLASS_PK, String.valueOf(user.getUserId())
		).put(
			Field.GROUP_ID, groupId
		).put(
			Field.SCOPE_GROUP_ID, groupId
		).put(
			Field.STATUS, String.valueOf(user.getStatus())
		).put(
			Field.TYPE, String.valueOf(user.getType())
		).put(
			Field.USER_ID, String.valueOf(user.getUserId())
		).put(
			Field.USER_NAME, StringUtil.toLowerCase(user.getFullName())
		).put(
			Field.getSortableFieldName(Field.USER_NAME),
			StringUtil.toLowerCase(user.getFullName())
		).put(
			"defaultUser", String.valueOf(user.isDefaultUser())
		).put(
			"emailAddress", user.getEmailAddress()
		).put(
			"emailAddressDomain", _getEmailAddressDomain(user.getEmailAddress())
		).put(
			"externalReferenceCode", user.getExternalReferenceCode()
		).put(
			"firstName", user.getFirstName()
		).put(
			"firstName_sortable", StringUtil.toLowerCase(user.getFirstName())
		).put(
			"fullName", user.getFullName()
		).put(
			"groupIds", groupId
		).put(
			"hasLoginDate",
			() -> {
				boolean hasLoginDate = false;

				if (user.getLastLoginDate() != null) {
					hasLoginDate = true;
				}

				return String.valueOf(hasLoginDate);
			}
		).put(
			"lastName", user.getLastName()
		).put(
			"lastName_sortable", StringUtil.toLowerCase(user.getLastName())
		).put(
			"organizationCount",
			() -> {
				long[] organizationIds = user.getOrganizationIds();

				return String.valueOf(organizationIds.length);
			}
		).put(
			"roleIds", _getStringValue(user.getRoleIds())
		).put(
			"roleNames",
			() -> {
				List<String> roleNames = new ArrayList<>();

				for (Role role :
						_roleLocalService.getRoles(user.getRoleIds())) {

					roleNames.add(StringUtil.toLowerCase(role.getName()));
				}

				return _getStringValue(roleNames);
			}
		).put(
			"screenName", user.getScreenName()
		).put(
			"screenName_sortable", StringUtil.toLowerCase(user.getScreenName())
		).build();

		_populateLocalizedNameFieldValues(map, user);

		_indexedFieldsFixture.populateUID(user, map);

		_indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, user.getCreateDate(), map);
		_indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, user.getModifiedDate(), map);

		_indexedFieldsFixture.populateDate(
			"birthDate", user.getBirthday(), map);

		_indexedFieldsFixture.populateRoleIdFields(
			user.getCompanyId(), User.class.getName(), user.getUserId(),
			user.getGroupId(), null, map);

		return map;
	}

	private Set<String> _getLocalizedCountryNames(Country country) {
		Set<String> countryNames = new HashSet<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String countryName = country.getName(locale);

			countryName = StringUtil.toLowerCase(countryName);

			countryNames.add(countryName);
		}

		return countryNames;
	}

	private String _getStringValue(List<String> values) {
		if (values.isEmpty()) {
			return "[]";
		}

		if (values.size() == 1) {
			return values.get(0);
		}

		Collections.sort(values);

		return String.valueOf(values);
	}

	private String _getStringValue(long[] longValues) {
		if (longValues.length == 0) {
			return "[]";
		}

		if (longValues.length == 1) {
			return String.valueOf(longValues[0]);
		}

		return String.valueOf(ListUtil.fromArray(longValues));
	}

	private void _populateAddressFieldValues(
		User user, Map<String, String> map) {

		List<String> cities = new ArrayList<>();
		List<String> countries = new ArrayList<>();
		List<String> regions = new ArrayList<>();
		List<String> streets = new ArrayList<>();
		List<String> zips = new ArrayList<>();

		for (Address address : user.getAddresses()) {
			cities.add(StringUtil.toLowerCase(address.getCity()));

			countries.addAll(_getLocalizedCountryNames(address.getCountry()));

			Region region = address.getRegion();

			regions.add(StringUtil.toLowerCase(region.getName()));

			streets.add(StringUtil.toLowerCase(address.getStreet1()));
			streets.add(StringUtil.toLowerCase(address.getStreet2()));
			streets.add(StringUtil.toLowerCase(address.getStreet3()));

			zips.add(StringUtil.toLowerCase(address.getZip()));
		}

		map.put("city", _getStringValue(cities));
		map.put("country", _getStringValue(countries));
		map.put("region", _getStringValue(regions));
		map.put("street", _getStringValue(streets));
		map.put("zip", _getStringValue(zips));
	}

	private void _populateLocalizedNameFieldValues(
		Map<String, String> map, User user) {

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			map.put(
				LocalizationUtil.getLocalizedName("firstName", languageId),
				user.getFirstName());
			map.put(
				LocalizationUtil.getLocalizedName("fullName", languageId),
				user.getFullName());
			map.put(
				LocalizationUtil.getLocalizedName("lastName", languageId),
				user.getLastName());
		}
	}

	private void _setUpExpandoTableSearchFixture() {
		_expandoTableSearchFixture = new ExpandoTableSearchFixture(
			_classNameLocalService, _expandoColumnLocalService,
			_expandoTableLocalService);

		_expandoColumns = _expandoTableSearchFixture.getExpandoColumns();
		_expandoTables = _expandoTableSearchFixture.getExpandoTables();
	}

	private void _setUpIndexedFieldsFixture() {
		_indexedFieldsFixture = new IndexedFieldsFixture(
			_resourcePermissionLocalService, _searchEngineHelper, _uidFactory);
	}

	private void _setUpIndexerFixture() {
		_indexerFixture = new IndexerFixture<>(User.class);
	}

	private void _setUpUserSearchFixture() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		_organizationSearchFixture = new OrganizationSearchFixture(
			_organizationLocalService);
		_userGroupSearchFixture = new UserGroupSearchFixture(
			_userGroupLocalService);

		_userSearchFixture = new UserSearchFixture(
			_userLocalService, groupSearchFixture, _organizationSearchFixture,
			_userGroupSearchFixture);

		_userSearchFixture.setUp();

		_addresses = _userSearchFixture.getAddresses();

		_groups = groupSearchFixture.getGroups();
		_organizations = _organizationSearchFixture.getOrganizations();
		_users = _userSearchFixture.getUsers();
		_userGroups = _userGroupSearchFixture.getUserGroups();
		_group = groupSearchFixture.addGroup(new GroupBlueprint());
	}

	@DeleteAfterTestRun
	private List<Address> _addresses = new ArrayList<>();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@DeleteAfterTestRun
	private List<ExpandoColumn> _expandoColumns;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	private ExpandoTableSearchFixture _expandoTableSearchFixture;

	@DeleteAfterTestRun
	private List<ExpandoTable> _expandoTables;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private IndexedFieldsFixture _indexedFieldsFixture;
	private IndexerFixture<User> _indexerFixture;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private OrganizationSearchFixture _organizationSearchFixture;

	@DeleteAfterTestRun
	private List<Organization> _organizations;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SearchEngineHelper _searchEngineHelper;

	@Inject
	private UIDFactory _uidFactory;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	private UserGroupSearchFixture _userGroupSearchFixture;

	@DeleteAfterTestRun
	private List<UserGroup> _userGroups = new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}