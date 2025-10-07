/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.upgrade.v0_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.scim.rest.client.dto.v1_0.Group;
import com.liferay.scim.rest.client.dto.v1_0.MultiValuedAttribute;
import com.liferay.scim.rest.client.dto.v1_0.Name;
import com.liferay.scim.rest.client.dto.v1_0.UserSchemaExtension;
import com.liferay.scim.rest.client.http.HttpInvoker;
import com.liferay.scim.rest.client.resource.v1_0.GroupResource;
import com.liferay.scim.rest.client.resource.v1_0.UserResource;
import com.liferay.scim.rest.resource.v1_0.test.BaseUserResourceTestCase;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ExpandoColumnUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeExpandoColumns() throws Exception {
		BaseUserResourceTestCase.setUpClass();

		String pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.scim.rest.internal.configuration." +
				"ScimClientOAuth2ApplicationConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"matcherField", "email"
			).put(
				"oAuth2ApplicationName", "scim-client-test"
			).put(
				"userId", TestPropsValues.getUserId()
			).build());

		try {
			GroupResource.Builder groupResourceBuilder =
				GroupResource.builder();

			Company company = _companyLocalService.getCompany(
				TestPropsValues.getCompanyId());
			String languageId = UpgradeProcessUtil.getDefaultLanguageId(
				TestPropsValues.getCompanyId());
			User user = _userLocalService.getUser(TestPropsValues.getUserId());

			GroupResource groupResource = groupResourceBuilder.authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				company.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.fromLanguageId(languageId)
			).build();

			UserResource.Builder userResourceBuilder = UserResource.builder();

			UserResource userResource = userResourceBuilder.authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				company.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.fromLanguageId(languageId)
			).build();

			HttpInvoker.HttpResponse httpResponse =
				groupResource.postV2GroupHttpResponse(
					new Group() {
						{
							displayName = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							externalId = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							id = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
						}
					});

			Assert.assertEquals(2, httpResponse.getStatusCode() / 100);

			httpResponse = userResource.postV2UserHttpResponse(_randomUser());

			Assert.assertEquals(2, httpResponse.getStatusCode() / 100);

			_setScimClientIdExpandoColumn(User.class.getName());
			_setScimClientIdExpandoColumn(UserGroup.class.getName());

			_assertScimClientIdExpandoColumn(User.class.getName(), false);
			_assertScimClientIdExpandoColumn(UserGroup.class.getName(), false);

			_runUpgrade();

			_assertScimClientIdExpandoColumn(User.class.getName(), true);
			_assertScimClientIdExpandoColumn(UserGroup.class.getName(), true);
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(pid);
		}
	}

	private void _assertScimClientIdExpandoColumn(
			String className, boolean hidden)
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), "scimClientId");

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		Assert.assertEquals(
			hidden,
			GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					ExpandoColumnConstants.PROPERTY_HIDDEN)));
	}

	private com.liferay.scim.rest.client.dto.v1_0.User _randomUser()
		throws Exception {

		StringBundler profileUrlSB = new StringBundler(3);

		profileUrlSB.append("http://");
		profileUrlSB.append(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		profileUrlSB.append(".com");

		String randomUserName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		return new com.liferay.scim.rest.client.dto.v1_0.User() {
			{
				active = true;
				displayName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				emails = new MultiValuedAttribute[] {
					new MultiValuedAttribute() {
						{
							primary = true;
							type = "default";
							value = randomUserName + "@liferay.com";
						}
					}
				};
				externalId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				locale = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = new Name() {
					{
						familyName = RandomTestUtil.randomString();
						givenName = RandomTestUtil.randomString();
						middleName = RandomTestUtil.randomString();
					}
				};
				nickName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				password = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				preferredLanguage = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				profileUrl = profileUrlSB.toString();
				schemas = new String[] {
					"urn:ietf:params:scim:schemas:core:2.0:User",
					"urn:ietf:params:scim:schemas:extension:liferay:2.0:User"
				};
				timezone = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				urn_ietf_params_scim_schemas_extension_liferay_2_0_User =
					new UserSchemaExtension() {
						{
							birthday = DateUtils.truncate(
								new Date(), Calendar.DATE);
							male = true;
						}
					};
				userName = randomUserName;
				userType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.scim.rest.internal.upgrade.v0_0_1." +
				"ExpandoColumnUpgradeProcess");

		upgradeProcess.upgrade();
	}

	private void _setScimClientIdExpandoColumn(String className)
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), "scimClientId");

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.PROPERTY_HIDDEN, "false");

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		_expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.scim.rest.internal.upgrade.registry.ScimRestUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private UserLocalService _userLocalService;

}