/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.indexer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.search.engine.ConnectionInformation;
import com.liferay.portal.search.engine.NodeInformation;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.OrganizationBlueprint.OrganizationBlueprintBuilder;
import com.liferay.users.admin.test.util.search.OrganizationSearchFixture;
import com.liferay.users.admin.test.util.search.UserBlueprint;
import com.liferay.users.admin.test.util.search.UserGroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class UserIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		OrganizationSearchFixture organizationSearchFixture =
			new OrganizationSearchFixture(organizationLocalService);

		UserGroupSearchFixture userGroupSearchFixture =
			new UserGroupSearchFixture(userGroupLocalService);

		UserSearchFixture userSearchFixture = new UserSearchFixture(
			userLocalService, groupSearchFixture, organizationSearchFixture,
			userGroupSearchFixture);

		userSearchFixture.setUp();

		_group = groupSearchFixture.addGroup(new GroupBlueprint());
		_groups = groupSearchFixture.getGroups();
		_groupSearchFixture = groupSearchFixture;

		_organizations = organizationSearchFixture.getOrganizations();
		_organizationSearchFixture = organizationSearchFixture;

		_userGroups = userGroupSearchFixture.getUserGroups();
		_userGroupSearchFixture = userGroupSearchFixture;

		_users = userSearchFixture.getUsers();
		_userSearchFixture = userSearchFixture;
	}

	@Test
	public void testEmailAddress() throws Exception {
		User user = addUser();

		String emailAddress = user.getEmailAddress();

		assertEmailAddressFieldValue(
			emailAddress, byQueryString(StringUtil.toUpperCase(emailAddress)));
	}

	@Test
	public void testEmailAddressDomain() throws Exception {
		String emailAddress = StringUtil.toLowerCase(
			RandomTestUtil.randomString() + "@test.com");

		addUserWithEmailAddress(emailAddress);

		assertEmailAddressFieldValue(emailAddress, byQueryString("test.com"));
	}

	@Test
	public void testEmailAddressField() throws Exception {
		User user = addUser();

		String emailAddress = user.getEmailAddress();

		assertEmailAddressFieldValue(
			emailAddress, byAttribute("emailAddress", emailAddress));
	}

	@Test
	public void testEmailAddressPrefix() throws Exception {
		User user = addUser();

		String emailAddress = user.getEmailAddress();

		assertEmailAddressFieldValue(
			emailAddress,
			byQueryString(
				StringUtil.removeSubstring(emailAddress, "@liferay.com")));
	}

	@Test
	public void testEmptyQuery() throws Exception {
		User user = addUser();

		assertUserId(user.getUserId(), byQueryString(null));
		assertUserId(user.getUserId(), byQueryString(StringPool.BLANK));
	}

	@Test
	public void testFirstNameExactPhrase() throws Exception {
		String firstName = "Mary Jane";
		String middleName = "Watson";
		String lastName = "Parker";

		addUserWithNameFields(firstName, middleName, lastName);

		assertFirstNameFieldValue(
			firstName, byAttribute("firstName", "\"Mary Jane\""));
	}

	@Test
	public void testFirstNameMixedExactPhrase() throws Exception {
		String firstName = "Mary Jane Watson";
		String middleName = "Joanne";
		String lastName = "Parker";

		_userSearchFixture.addUser(
			getUserBlueprintBuilder(
			).firstName(
				firstName
			).middleName(
				middleName
			).lastName(
				lastName
			));

		assertNoHits(byAttribute("firstName", "\"Mary Watson\""));
		assertNoHits(byAttribute("firstName", "\"Mary Jane\" Missingword"));

		assertFirstNameFieldValue(
			firstName, byAttribute("firstName", "Mary \"Jane Watson\""));
	}

	@Test
	public void testLikeCharacter() throws Exception {
		User user = addUser();

		assertUserId(user.getUserId(), byQueryString(StringPool.PERCENT));

		assertNoHits(
			byQueryString(StringPool.PERCENT + RandomTestUtil.randomString()));
	}

	@Test
	public void testLuceneQueryParserUnfriendlyCharacters() {
		addUser();

		assertNoHits(byQueryString(StringPool.AT));
		assertNoHits(
			byQueryString(StringPool.AT + RandomTestUtil.randomString()));
		assertNoHits(byQueryString(StringPool.EXCLAMATION));
		assertNoHits(
			byQueryString(
				StringPool.EXCLAMATION + RandomTestUtil.randomString()));
	}

	@Test
	public void testNameFieldsChinese() {
		String firstName = "姓氏";
		String lastName = "名字";

		User user = addUserWithNameFields(firstName, null, lastName);

		assertFieldValue(
			"firstName", firstName, byAttribute("firstName", "姓氏"));
		assertFieldValue("lastName", lastName, byAttribute("lastName", "名字"));

		assertUserId(user.getUserId(), byQueryString("名字"));
		assertUserId(user.getUserId(), byQueryString("名字姓氏"));
		assertUserId(user.getUserId(), byQueryString(user.getFullName()));
	}

	@Test
	public void testNameFieldsJapanese() {
		String firstName = "宮崎";
		String lastName = "駿";

		User user = addUserWithNameFields(firstName, null, lastName);

		assertFieldValue(
			"firstName", firstName, byAttribute("firstName", "宮崎"));
		assertFieldValue("lastName", lastName, byAttribute("lastName", "駿"));

		assertUserId(user.getUserId(), byQueryString("宮崎"));
		assertUserId(user.getUserId(), byQueryString("駿 宮崎"));
		assertUserId(user.getUserId(), byQueryString(user.getFullName()));
	}

	@Test
	public void testNameFieldsNotTokenized() throws Exception {
		String firstName = "Liferay7";
		String lastName = "dell'Apostrophe";
		String middleName = "ALLOY_4";

		testNameFields(firstName, lastName, middleName);
	}

	@Test
	public void testNameFieldsNotTokenizedLowercase() throws Exception {
		String firstName = "liferay7";
		String lastName = "dell'apostrophe";
		String middleName = "alloy_4";

		testNameFields(firstName, lastName, middleName);
	}

	@Test
	public void testNameFieldsRandomString() throws Exception {
		String firstName = RandomTestUtil.randomString();
		String lastName = RandomTestUtil.randomString();
		String middleName = "Middle";

		User user = addUserWithNameFields(firstName, middleName, lastName);

		assertUserId(user.getUserId(), byQueryString(firstName));
		assertUserId(user.getUserId(), byQueryString(user.getFullName()));
	}

	@Test
	public void testNameFieldsSpanish() {
		String firstName = "José";
		String lastName = "Sánchez";
		String middleName = "Pedro";

		User user = addUserWithNameFields(firstName, middleName, lastName);

		assertFieldValue(
			"firstName", firstName, byAttribute("firstName", "José"));
		assertFieldValue(
			"lastName", lastName, byAttribute("lastName", "Sánchez"));
		assertFieldValue(
			"middleName", middleName, byAttribute("middleName", "Pedro"));

		assertUserId(user.getUserId(), byQueryString("Pedro"));
		assertUserId(user.getUserId(), byQueryString("José Sánchez"));
		assertUserId(user.getUserId(), byQueryString("Sánchez José"));
		assertUserId(user.getUserId(), byQueryString(user.getFullName()));
	}

	@Test
	public void testNamesPrefix() throws Exception {
		String firstName = "First";
		String lastName = "Last";
		String middleName = "Middle";

		addUserWithNameFields(firstName, middleName, lastName);

		assertFieldValue("firstName", firstName, byQueryString("Fir"));
		assertFieldValue("lastName", lastName, byQueryString("LasT"));
		assertFieldValue("middleName", middleName, byQueryString("midd"));
	}

	@Test
	public void testNamesSubstring() throws Exception {
		String firstName = "First";
		String lastName = "Last";
		String middleName = "Middle";

		addUserWithNameFields(firstName, middleName, lastName);

		assertNoHits(byQueryString("irst"));
		assertNoHits(byQueryString("asT"));
		assertNoHits(byQueryString("idd"));
	}

	@Test
	public void testNoGuestUser() throws Exception {
		User user = userLocalService.getGuestUser(_group.getCompanyId());

		assertNoHits(byQueryString(user.getScreenName()));
	}

	@Test
	public void testReindexOneContact() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(UserIndexerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			"indexer.class.name", "com.liferay.portal.kernel.model.Contact");

		long userId = TestPropsValues.getUserId();

		ServiceRegistration<ModelDocumentContributor<Contact>>
			serviceRegistration = bundleContext.registerService(
				(Class<ModelDocumentContributor<Contact>>)
					(Class<?>)ModelDocumentContributor.class,
				new ModelDocumentContributor<Contact>() {

					@Override
					public void contribute(Document document, Contact contact) {
						Assert.assertEquals(userId, contact.getClassPK());
					}

				},
				properties);

		try {
			UserTestUtil.addUser();

			Indexer<User> userIndexer = IndexerRegistryUtil.getIndexer(
				User.class.getName());

			userIndexer.reindex(User.class.getName(), userId);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testScreenName() throws Exception {
		String screenName = RandomTestUtil.randomString();

		addUserWithScreenName(screenName);

		assertScreenNameFieldValue(screenName, byQueryString(screenName));
	}

	@Test
	public void testScreenNameField() throws Exception {
		String screenName = RandomTestUtil.randomString();

		addUserWithScreenName(screenName);

		assertScreenNameFieldValue(
			screenName,
			byAttribute("screenName", StringUtil.toLowerCase(screenName)));
	}

	@Test
	public void testScreenNamePrefix() throws Exception {
		String screenName = "Open4Life" + RandomTestUtil.randomString();

		addUserWithScreenName(screenName);

		assertNoHits(byQueryString("4lif"));
		assertScreenNameFieldValue(screenName, byQueryString("open"));
		assertScreenNameFieldValue(screenName, byQueryString("open4life"));
		assertScreenNameFieldValue(screenName, byQueryString("OPE"));
	}

	@Test
	public void testSummaryHighlight() throws Exception {
		String firstName = "First";
		String lastName = "Last";
		String middleName = "Middle";

		User user = addUserWithNameFields(firstName, middleName, lastName);

		assertSummary(
			firstName,
			StringBundler.concat(
				HighlightUtil.HIGHLIGHT_TAG_OPEN, firstName,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE, StringPool.SPACE, middleName,
				StringPool.SPACE, lastName),
			user.getUserId());

		String expectedFullNameHighlight = _getExpectedFullNameHighlight(
			firstName, middleName, lastName);

		assertSummary(
			StringUtil.toLowerCase(firstName + " " + lastName),
			expectedFullNameHighlight, user.getUserId());
		assertSummary(
			lastName + " " + firstName, expectedFullNameHighlight,
			user.getUserId());
	}

	@Test
	public void testUserInGroupViaOrganization() throws Exception {
		User user = addUser();

		Organization organization = addOrganization();

		organizationLocalService.addUserOrganization(
			user.getUserId(), organization);

		Group group = addGroup();

		organizationLocalService.addGroupOrganization(
			group.getGroupId(), organization);

		assertFindUserByGroup(user.getFullName(), group.getGroupId());
	}

	@Test
	public void testUserInGroupViaUserGroup() throws Exception {
		User user = addUser();

		UserGroup userGroup = addUserGroup();

		Group group = addGroup();

		userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		userGroupLocalService.addGroupUserGroup(group.getGroupId(), userGroup);

		assertFindUserByGroup(user.getFullName(), group.getGroupId());
	}

	@Test
	public void testUserInOrganizationGroup() throws Exception {
		User user = addUser();

		Organization organization = addOrganization();

		Group group = organization.getGroup();

		organizationLocalService.addUserOrganization(
			user.getUserId(), organization);

		toggleCreateSite(organization, true);

		assertFindUserByGroup(user.getFullName(), group.getGroupId());
	}

	@Test
	public void testUserInRole() throws Exception {
		User user = addUser();

		List<Long> oldRoleIds = ListUtil.fromArray(user.getRoleIds());

		assertFindUserByRoles(user.getFullName(), oldRoleIds, oldRoleIds);

		Long roleId = getRoleId(RoleConstants.SITE_ADMINISTRATOR);

		userLocalService.addRoleUser(roleId, user.getUserId());

		List<Long> newRoleIds = new ArrayList<>(oldRoleIds);

		newRoleIds.add(roleId);

		assertFindUserByRoles(
			user.getFullName(), Arrays.asList(roleId), newRoleIds);

		assertFindUserByRoles(
			user.getFullName(),
			Arrays.asList(
				roleId, getRoleId(RoleConstants.SITE_CONTENT_REVIEWER)),
			newRoleIds);
	}

	@Test
	public void testUserNotInGroupViaOrganization() throws Exception {
		User user = addUser();

		Organization organization = addOrganization();

		organizationLocalService.addUserOrganization(
			user.getUserId(), organization);

		Group group = addGroup();

		long groupId = group.getGroupId();

		organizationLocalService.addGroupOrganization(groupId, organization);

		organizationLocalService.unsetGroupOrganizations(
			groupId, new long[] {organization.getOrganizationId()});

		assertNoHits(byUserInGroup(user, group));
	}

	@Test
	public void testUserNotInGroupViaUserGroup() throws Exception {
		User user = addUser();

		UserGroup userGroup = addUserGroup();

		userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		Group group = addGroup();

		long groupId = group.getGroupId();

		userGroupLocalService.addGroupUserGroup(groupId, userGroup);

		userGroupLocalService.unsetGroupUserGroups(
			groupId, new long[] {userGroup.getUserGroupId()});

		assertNoHits(byUserInGroup(user, group));
	}

	@Test
	public void testUserNotInOrganizationGroup() throws Exception {
		User user = addUser();

		Organization organization = addOrganization();

		Group group = organization.getGroup();

		organizationLocalService.addUserOrganization(
			user.getUserId(), organization);

		toggleCreateSite(organization, true);

		toggleCreateSite(organization, false);

		assertNoHits(byUserInGroup(user, group));
	}

	@Test
	public void testUserNotInRole() throws Exception {
		User user = addUser();

		userLocalService.addRoleUser(
			getRoleId(RoleConstants.SITE_ADMINISTRATOR), user.getUserId());

		assertNoHits(
			byRoles(
				Collections.singletonList(
					getRoleId(RoleConstants.SITE_CONTENT_REVIEWER))));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Rule
	public TestName testName = new TestName();

	protected Group addGroup() {
		return _groupSearchFixture.addGroup(new GroupBlueprint());
	}

	protected Organization addOrganization() {
		OrganizationBlueprintBuilder organizationBlueprintBuilder =
			OrganizationSearchFixture.getTestOrganizationBlueprintBuilder();

		return _organizationSearchFixture.addOrganization(
			organizationBlueprintBuilder.build());
	}

	protected User addUser() {
		return _userSearchFixture.addUser(getUserBlueprintBuilder());
	}

	protected UserGroup addUserGroup() {
		return _userGroupSearchFixture.addUserGroup(
			UserGroupSearchFixture.getTestUserGroupBlueprintBuilder());
	}

	protected void addUserWithEmailAddress(String emailAddress) {
		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			getUserBlueprintBuilder();

		_userSearchFixture.addUser(
			userBlueprintBuilder.emailAddress(emailAddress));
	}

	protected User addUserWithNameFields(
		String firstName, String middleName, String lastName) {

		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			getUserBlueprintBuilder();

		return _userSearchFixture.addUser(
			userBlueprintBuilder.firstName(
				firstName
			).middleName(
				middleName
			).lastName(
				lastName
			));
	}

	protected void addUserWithScreenName(String screenName) {
		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			getUserBlueprintBuilder();

		_userSearchFixture.addUser(userBlueprintBuilder.screenName(screenName));
	}

	protected void assertEmailAddressFieldValue(
		String emailAddress, Consumer<SearchRequestBuilder> consumer) {

		assertFieldValue("emailAddress", emailAddress, consumer);
	}

	protected void assertFieldValue(
		String fieldName, Object fieldValue,
		Consumer<SearchRequestBuilder>... consumers) {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, search(consumers));
	}

	protected void assertFindUserByGroup(String queryString, long groupId) {
		assertFieldValue(
			"groupId", Arrays.asList(_group.getGroupId(), groupId),
			byGroup(groupId), byQueryString(queryString));
	}

	protected void assertFindUserByRoles(
		String queryString, List<Long> filterRoleIds,
		List<Long> expectedRoleIds) {

		assertFieldValue(
			"roleIds", expectedRoleIds, byRoles(filterRoleIds),
			byQueryString(queryString));
	}

	protected void assertFirstNameFieldValue(
		String firstName, Consumer<SearchRequestBuilder> consumer) {

		assertFieldValue("firstName", firstName, consumer);
	}

	protected void assertNoHits(Consumer<SearchRequestBuilder> consumer) {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), search(consumer));
	}

	protected void assertScreenNameFieldValue(
		String screenName, Consumer<SearchRequestBuilder> consumer) {

		assertFieldValue(
			"screenName", StringUtil.toLowerCase(screenName), consumer);
	}

	protected void assertSummary(String queryString, String title, long userId)
		throws Exception {

		SearchResponse searchResponse = search(
			searchRequestBuilder -> searchRequestBuilder.highlightEnabled(
				true
			).ownerUserId(
				userId
			).queryString(
				queryString
			));

		List<Document> documents = searchResponse.getDocuments71();

		Document document = documents.get(0);

		Summary summary = indexer.getSummary(document, null, null, null);

		Assert.assertEquals(StringPool.BLANK, summary.getContent());
		Assert.assertEquals(title, summary.getTitle());
	}

	protected void assertUserId(
		long userId, Consumer<SearchRequestBuilder> consumer) {

		assertFieldValue(
			Field.USER_ID, String.valueOf(userId), consumer,
			searchRequestBuilder -> searchRequestBuilder.ownerUserId(userId));
	}

	protected Consumer<SearchRequestBuilder> byAttribute(
		String field, String value) {

		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(field, value));
	}

	protected Consumer<SearchRequestBuilder> byGroup(Long groupId) {
		return searchRequestBuilder -> searchRequestBuilder.groupIds(groupId);
	}

	protected Consumer<SearchRequestBuilder> byQueryString(String queryString) {
		return searchRequestBuilder -> searchRequestBuilder.queryString(
			queryString);
	}

	protected Consumer<SearchRequestBuilder> byRoles(List<Long> roleIds) {
		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				"params",
				getParamsLinkedHashMap(
					"usersRoles", getUsersRolesParamValue(roleIds))));
	}

	protected Consumer<SearchRequestBuilder> byUserInGroup(
		User user, Group group) {

		return searchRequestBuilder -> searchRequestBuilder.groupIds(
			group.getGroupId()
		).ownerUserId(
			user.getUserId()
		);
	}

	protected LinkedHashMap<?, ?> getParamsLinkedHashMap(
		String key, Object value) {

		return new LinkedHashMap<>(Collections.singletonMap(key, value));
	}

	protected Long getRoleId(String name) throws Exception {
		Role role = roleLocalService.getRole(_group.getCompanyId(), name);

		return role.getRoleId();
	}

	protected UserBlueprint.UserBlueprintBuilder getUserBlueprintBuilder() {
		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			_userSearchFixture.getTestUserBlueprintBuilder();

		return userBlueprintBuilder.groupIds(
			_group.getGroupId()
		).jobTitle(
			testName.getMethodName()
		);
	}

	protected Object getUsersRolesParamValue(List<Long> roleIds) {
		if (roleIds.size() == 1) {
			return roleIds.get(0);
		}

		return _toArrayOfLong(roleIds);
	}

	protected SearchResponse search(
		Consumer<SearchRequestBuilder>... consumers) {

		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClasses(
				User.class
			).withSearchRequestBuilder(
				consumers
			).build());
	}

	protected void testNameFields(
		String firstName, String lastName, String middleName) {

		addUserWithNameFields(firstName, middleName, lastName);

		assertFieldValue(
			"firstName", firstName, byAttribute("firstName", firstName));
		assertFieldValue(
			"lastName", lastName, byAttribute("lastName", lastName));
		assertFieldValue(
			"middleName", middleName, byAttribute("middleName", middleName));
	}

	protected void toggleCreateSite(Organization organization, boolean site)
		throws Exception {

		organizationLocalService.updateOrganization(
			null, organization.getCompanyId(), organization.getOrganizationId(),
			organization.getParentOrganizationId(), organization.getName(),
			organization.getType(), organization.getRegionId(),
			organization.getCountryId(), organization.getStatusListTypeId(),
			organization.getComments(), false, null, site, null);
	}

	@Inject
	protected GroupLocalService groupLocalService;

	@Inject(filter = "indexer.class.name=com.liferay.portal.kernel.model.User")
	protected Indexer<User> indexer;

	@Inject
	protected OrganizationLocalService organizationLocalService;

	@Inject
	protected RoleLocalService roleLocalService;

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected UserGroupLocalService userGroupLocalService;

	@Inject
	protected UserLocalService userLocalService;

	private Version _getElasticsearchVersion() {
		ConnectionInformation connectionInformation =
			_searchEngineInformation.getConnectionInformationList(
			).get(
				0
			);

		NodeInformation nodeInformation =
			connectionInformation.getNodeInformationList(
			).get(
				0
			);

		return Version.parseVersion(nodeInformation.getVersion());
	}

	private String _getExpectedFullNameHighlight(
		String firstName, String middleName, String lastName) {

		if (StringUtil.startsWith(
				_searchEngineInformation.getVendorString(), "Elasticsearch") &&
			(_getElasticsearchVersion().compareTo(
				Version.parseVersion("8.10.2")) >= 0)) {

			return StringBundler.concat(
				HighlightUtil.HIGHLIGHT_TAG_OPEN, firstName, StringPool.SPACE,
				middleName, StringPool.SPACE, lastName,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE);
		}

		return StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, firstName,
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, StringPool.SPACE, middleName,
			StringPool.SPACE, HighlightUtil.HIGHLIGHT_TAG_OPEN, lastName,
			HighlightUtil.HIGHLIGHT_TAG_CLOSE);
	}

	private Long[] _toArrayOfLong(List<Long> list) {
		return list.toArray(new Long[0]);
	}

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private GroupSearchFixture _groupSearchFixture;

	@DeleteAfterTestRun
	private List<Organization> _organizations;

	private OrganizationSearchFixture _organizationSearchFixture;

	@Inject
	private SearchEngineInformation _searchEngineInformation;

	@DeleteAfterTestRun
	private List<UserGroup> _userGroups;

	private UserGroupSearchFixture _userGroupSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}