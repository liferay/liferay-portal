/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.SiteTemplate;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.resource.v1_0.SiteTemplateResource;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class SiteTemplateResourceTest extends BaseSiteTemplateResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetSiteTemplatesPage() throws Exception {
		super.testGetSiteTemplatesPage();

		CMSTestUtil.getOrAddGroup(SiteTemplateResourceTest.class);

		_testGetSiteTemplatesPageWithAssetLibraryMember();
		_testGetSiteTemplatesPageWithExcludedSiteExternalReferenceCodes();
		_testGetSiteTemplatesPageWithUser(
			_addUserWithDepotRole(
				_addDepotEntry(),
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR));
		_testGetSiteTemplatesPageWithUser(
			_addUserWithDepotRole(
				_addDepotEntry(), DepotRolesConstants.ASSET_LIBRARY_OWNER));
		_testGetSiteTemplatesPageWithUser(
			_addUserWithRegularRole(RoleConstants.CMS_ADMINISTRATOR));
	}

	@Override
	protected SiteTemplate randomSiteTemplate() {
		return new SiteTemplate() {
			{
				active = RandomTestUtil.randomBoolean();
				defaultLanguageId = "en-US";
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				description_i18n = HashMapBuilder.put(
					"en-US", description
				).build();
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name_i18n = HashMapBuilder.put(
					"en-US", name
				).build();
				pagesUpdateable = RandomTestUtil.randomBoolean();
				siteExternalReferenceCode = RandomTestUtil.randomString();
			}
		};
	}

	@Override
	protected SiteTemplate testGetSiteTemplatesPage_addSiteTemplate(
			SiteTemplate siteTemplate)
		throws Exception {

		Map<Locale, String> names = new HashMap<>();

		Map<String, String> nameMap = siteTemplate.getName_i18n();

		for (Map.Entry<String, String> entry : nameMap.entrySet()) {
			names.put(
				LocaleUtil.fromLanguageId(entry.getKey()), entry.getValue());
		}

		Map<Locale, String> descriptions = new HashMap<>();

		Map<String, String> descriptionMap = siteTemplate.getDescription_i18n();

		for (Map.Entry<String, String> entry : descriptionMap.entrySet()) {
			descriptions.put(
				LocaleUtil.fromLanguageId(entry.getKey()), entry.getValue());
		}

		_layoutSetPrototypeLocalService.addLayoutSetPrototype(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), names,
			descriptions, siteTemplate.getActive(),
			siteTemplate.getPagesUpdateable(), new ServiceContext());

		return siteTemplate;
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	private User _addUserWithDepotRole(DepotEntry depotEntry, String roleName)
		throws Exception {

		User user = UserTestUtil.addUser(
			testCompany, RandomTestUtil.randomString());

		Role role = _roleLocalService.getRole(
			testCompany.getCompanyId(), roleName);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), depotEntry.getGroupId(),
			new long[] {role.getRoleId()});

		return user;
	}

	private User _addUserWithRegularRole(String roleName) throws Exception {
		User user = UserTestUtil.addUser(
			testCompany, RandomTestUtil.randomString());

		Role role = _roleLocalService.getRole(
			testCompany.getCompanyId(), roleName);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		return user;
	}

	private SiteTemplateResource _getSiteTemplateResource(User user) {
		return SiteTemplateResource.builder(
		).authentication(
			user.getEmailAddress(), user.getPasswordUnencrypted()
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testGetSiteTemplatesPageWithAssetLibraryMember()
		throws Exception {

		testGetSiteTemplatesPage_addSiteTemplate(randomSiteTemplate());

		SiteTemplateResource siteTemplateResource = _getSiteTemplateResource(
			_addUserWithDepotRole(
				_addDepotEntry(), DepotRolesConstants.ASSET_LIBRARY_MEMBER));

		Page<SiteTemplate> siteTemplatesPage =
			siteTemplateResource.getSiteTemplatesPage(
				null, null, Pagination.of(1, 500));

		Collection<SiteTemplate> siteTemplates = siteTemplatesPage.getItems();

		Assert.assertTrue(siteTemplates.isEmpty());
	}

	private void _testGetSiteTemplatesPageWithExcludedSiteExternalReferenceCodes()
		throws Exception {

		boolean excludedSiteTemplateFound = false;
		boolean includedSiteTemplateFound = false;

		SiteTemplate siteTemplate1 = randomSiteTemplate();

		siteTemplate1.setActive(true);

		testGetSiteTemplatesPage_addSiteTemplate(siteTemplate1);

		SiteTemplate siteTemplate2 = randomSiteTemplate();

		siteTemplate2.setActive(true);

		testGetSiteTemplatesPage_addSiteTemplate(siteTemplate2);

		Page<SiteTemplate> siteTemplatesPage =
			siteTemplateResource.getSiteTemplatesPage(
				true, null, Pagination.of(1, 100));

		String excludedSiteExternalReferenceCode = null;
		String includedSiteExternalReferenceCode = null;

		for (SiteTemplate siteTemplate : siteTemplatesPage.getItems()) {
			if (Objects.equals(
					siteTemplate1.getName(), siteTemplate.getName())) {

				excludedSiteExternalReferenceCode =
					siteTemplate.getSiteExternalReferenceCode();
			}
			else if (Objects.equals(
						siteTemplate2.getName(), siteTemplate.getName())) {

				includedSiteExternalReferenceCode =
					siteTemplate.getSiteExternalReferenceCode();
			}
		}

		siteTemplatesPage = siteTemplateResource.getSiteTemplatesPage(
			true, new String[] {excludedSiteExternalReferenceCode},
			Pagination.of(1, 100));

		for (SiteTemplate siteTemplate : siteTemplatesPage.getItems()) {
			String siteExternalReferenceCode =
				siteTemplate.getSiteExternalReferenceCode();

			if (excludedSiteExternalReferenceCode.equals(
					siteExternalReferenceCode)) {

				excludedSiteTemplateFound = true;
			}
			else if (includedSiteExternalReferenceCode.equals(
						siteExternalReferenceCode)) {

				includedSiteTemplateFound = true;
			}
		}

		Assert.assertFalse(excludedSiteTemplateFound);
		Assert.assertTrue(includedSiteTemplateFound);
	}

	private void _testGetSiteTemplatesPageWithUser(User user) throws Exception {
		SiteTemplate siteTemplate = testGetSiteTemplatesPage_addSiteTemplate(
			randomSiteTemplate());

		SiteTemplateResource siteTemplateResource = _getSiteTemplateResource(
			user);

		Page<SiteTemplate> siteTemplatesPage =
			siteTemplateResource.getSiteTemplatesPage(
				null, null, Pagination.of(1, 500));

		assertContains(
			siteTemplate, (List<SiteTemplate>)siteTemplatesPage.getItems());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}