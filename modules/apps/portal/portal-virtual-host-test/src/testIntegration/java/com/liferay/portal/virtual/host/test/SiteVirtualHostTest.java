/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.virtual.host.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.LayoutSetVirtualHostException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.net.IDN;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class SiteVirtualHostTest extends BaseVirtualHostTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompanyWithWebId(
			true, generateVirtualHostName());

		_guestGroup = _groupLocalService.getGroup(
			_company.getCompanyId(),
			PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

		_group1 = _addGroup(_company, null, generateVirtualHostName());

		_childGroup1 = _addGroup(_company, _group1, generateVirtualHostName());

		_group2 = _addGroup(_company, null, generateVirtualHostName());

		_childGroup2 = _addGroup(_company, _group2, null);

		_group3 = _addGroup(_company, null, null);

		_childGroup3 = _addGroup(_company, _group3, generateVirtualHostName());
	}

	@After
	public void tearDown() throws Exception {
		if (_companyConfigurationTemporarySwapper != null) {
			_companyConfigurationTemporarySwapper.close();
		}

		_groupVirtualHostNames.clear();
	}

	@Test
	public void testGroupRestrictedViaVirtualHost() throws Exception {
		_setStrictMode(false);

		String companyVirtualHostName = _company.getVirtualHostname();

		_assertURLtoString(false, _group1, companyVirtualHostName);
		_assertURLtoString(false, _group2, companyVirtualHostName);
		_assertURLtoString(true, _group3, companyVirtualHostName);
		_assertURLtoString(false, _childGroup1, companyVirtualHostName);
		_assertURLtoString(true, _childGroup2, companyVirtualHostName);
		_assertURLtoString(false, _childGroup3, companyVirtualHostName);
		_assertURLtoString(true, _guestGroup, companyVirtualHostName);

		String group1VirtualHostName = _groupVirtualHostNames.get(_group1);

		_assertURLtoString(true, _group1, group1VirtualHostName);
		_assertURLtoString(false, _group2, group1VirtualHostName);
		_assertURLtoString(true, _group3, group1VirtualHostName);
		_assertURLtoString(false, _childGroup1, group1VirtualHostName);
		_assertURLtoString(true, _childGroup2, group1VirtualHostName);
		_assertURLtoString(false, _childGroup3, group1VirtualHostName);
		_assertURLtoString(true, _guestGroup, group1VirtualHostName);

		String childGroup1VirtualHostName = _groupVirtualHostNames.get(
			_childGroup1);

		_assertURLtoString(false, _group1, childGroup1VirtualHostName);
		_assertURLtoString(false, _group2, childGroup1VirtualHostName);
		_assertURLtoString(true, _group3, childGroup1VirtualHostName);
		_assertURLtoString(true, _childGroup1, childGroup1VirtualHostName);
		_assertURLtoString(true, _childGroup2, childGroup1VirtualHostName);
		_assertURLtoString(false, _childGroup3, childGroup1VirtualHostName);
		_assertURLtoString(true, _guestGroup, childGroup1VirtualHostName);

		String group2VirtualHostName = _groupVirtualHostNames.get(_group2);

		_assertURLtoString(false, _group1, group2VirtualHostName);
		_assertURLtoString(true, _group2, group2VirtualHostName);
		_assertURLtoString(true, _group3, group2VirtualHostName);
		_assertURLtoString(false, _childGroup1, group2VirtualHostName);
		_assertURLtoString(true, _childGroup2, group2VirtualHostName);
		_assertURLtoString(false, _childGroup3, group2VirtualHostName);
		_assertURLtoString(true, _guestGroup, group2VirtualHostName);

		String childGroup3VirtualHostName = _groupVirtualHostNames.get(
			_childGroup3);

		_assertURLtoString(false, _group1, childGroup3VirtualHostName);
		_assertURLtoString(false, _group2, childGroup3VirtualHostName);
		_assertURLtoString(true, _group3, childGroup3VirtualHostName);
		_assertURLtoString(false, _childGroup1, childGroup3VirtualHostName);
		_assertURLtoString(true, _childGroup2, childGroup3VirtualHostName);
		_assertURLtoString(true, _childGroup3, childGroup3VirtualHostName);
		_assertURLtoString(true, _guestGroup, childGroup3VirtualHostName);
	}

	@Test
	public void testGroupRestrictedViaVirtualHostWithBypass() throws Exception {
		_setStrictMode(true);

		String companyVirtualHostName = _company.getVirtualHostname();

		_assertURLtoString(true, _group1, companyVirtualHostName);
		_assertURLtoString(true, _group2, companyVirtualHostName);
		_assertURLtoString(true, _group3, companyVirtualHostName);
		_assertURLtoString(true, _childGroup1, companyVirtualHostName);
		_assertURLtoString(true, _childGroup2, companyVirtualHostName);
		_assertURLtoString(true, _childGroup3, companyVirtualHostName);
		_assertURLtoString(true, _guestGroup, companyVirtualHostName);

		String group1VirtualHostName = _groupVirtualHostNames.get(_group1);

		_assertURLtoString(true, _group1, group1VirtualHostName);
		_assertURLtoString(false, _group2, group1VirtualHostName);
		_assertURLtoString(true, _group3, group1VirtualHostName);
		_assertURLtoString(false, _childGroup1, group1VirtualHostName);
		_assertURLtoString(true, _childGroup2, group1VirtualHostName);
		_assertURLtoString(false, _childGroup3, group1VirtualHostName);
		_assertURLtoString(true, _guestGroup, group1VirtualHostName);
	}

	@Test
	public void testGroupsAccessibleViaVirtualHost() throws Exception {
		String companyVirtualHostName = _company.getVirtualHostname();

		_assertURLtoString(true, _group1, companyVirtualHostName);
		_assertURLtoString(true, _group2, companyVirtualHostName);
		_assertURLtoString(true, _group3, companyVirtualHostName);
		_assertURLtoString(true, _childGroup1, companyVirtualHostName);
		_assertURLtoString(true, _childGroup2, companyVirtualHostName);
		_assertURLtoString(true, _childGroup3, companyVirtualHostName);
		_assertURLtoString(true, _guestGroup, companyVirtualHostName);

		String group1VirtualHostName = _groupVirtualHostNames.get(_group1);

		_assertURLtoString(true, _group1, group1VirtualHostName);
		_assertURLtoString(true, _group2, group1VirtualHostName);
		_assertURLtoString(true, _group3, group1VirtualHostName);
		_assertURLtoString(true, _childGroup1, group1VirtualHostName);
		_assertURLtoString(true, _childGroup2, group1VirtualHostName);
		_assertURLtoString(true, _childGroup3, group1VirtualHostName);
		_assertURLtoString(true, _guestGroup, group1VirtualHostName);

		String childGroup1VirtualHostName = _groupVirtualHostNames.get(
			_childGroup1);

		_assertURLtoString(true, _group1, childGroup1VirtualHostName);
		_assertURLtoString(true, _group2, childGroup1VirtualHostName);
		_assertURLtoString(true, _group3, childGroup1VirtualHostName);
		_assertURLtoString(true, _childGroup1, childGroup1VirtualHostName);
		_assertURLtoString(true, _childGroup2, childGroup1VirtualHostName);
		_assertURLtoString(true, _childGroup3, childGroup1VirtualHostName);
		_assertURLtoString(true, _guestGroup, childGroup1VirtualHostName);

		String group2VirtualHostName = _groupVirtualHostNames.get(_group2);

		_assertURLtoString(true, _group1, group2VirtualHostName);
		_assertURLtoString(true, _group2, group2VirtualHostName);
		_assertURLtoString(true, _group3, group2VirtualHostName);
		_assertURLtoString(true, _childGroup1, group2VirtualHostName);
		_assertURLtoString(true, _childGroup2, group2VirtualHostName);
		_assertURLtoString(true, _childGroup3, group2VirtualHostName);
		_assertURLtoString(true, _guestGroup, group2VirtualHostName);

		String childGroup3VirtualHostName = _groupVirtualHostNames.get(
			_childGroup3);

		_assertURLtoString(true, _group1, childGroup3VirtualHostName);
		_assertURLtoString(true, _group2, childGroup3VirtualHostName);
		_assertURLtoString(true, _group3, childGroup3VirtualHostName);
		_assertURLtoString(true, _childGroup1, childGroup3VirtualHostName);
		_assertURLtoString(true, _childGroup2, childGroup3VirtualHostName);
		_assertURLtoString(true, _childGroup3, childGroup3VirtualHostName);
		_assertURLtoString(true, _guestGroup, childGroup3VirtualHostName);
	}

	@Test
	public void testUpdateVirtualHosts() throws Exception {
		_virtualHostGroup1 = GroupTestUtil.addGroupToCompany(
			_company.getCompanyId());
		_virtualHostGroup2 = GroupTestUtil.addGroupToCompany(
			_company.getCompanyId());

		String unicodeHostname1 = _getRandomUnicodeHostname();

		String asciiHostname1 = IDN.toASCII(unicodeHostname1);

		_updateVirtualHosts(_virtualHostGroup2, asciiHostname1);

		_updateVirtualHosts(_virtualHostGroup2, null);
		_updateVirtualHosts(_virtualHostGroup1, unicodeHostname1);

		Assert.assertThrows(
			LayoutSetVirtualHostException.class,
			() -> _updateVirtualHosts(_virtualHostGroup2, asciiHostname1));

		String unicodeHostname2 = _getRandomUnicodeHostname();

		String asciiHostname2 = IDN.toASCII(unicodeHostname2);

		_updateVirtualHosts(_virtualHostGroup2, unicodeHostname2);

		_updateVirtualHosts(_virtualHostGroup2, null);
		_updateVirtualHosts(_virtualHostGroup1, asciiHostname2);

		Assert.assertThrows(
			LayoutSetVirtualHostException.class,
			() -> _updateVirtualHosts(_virtualHostGroup2, unicodeHostname2));

		_updateVirtualHosts(
			_virtualHostGroup2,
			StringUtil.toLowerCase(RandomTestUtil.randomString(64)) + ".com");
		_updateVirtualHosts(_virtualHostGroup2, generateVirtualHostName());

		String unicodeHostname3 = _getRandomUnicodeHostname();

		String asciiHostname3 = IDN.toASCII(unicodeHostname3);

		_updateVirtualHosts(_virtualHostGroup2, asciiHostname3);

		_updateVirtualHosts(_virtualHostGroup2, null);

		_company = _companyLocalService.updateCompany(
			_company.getCompanyId(), unicodeHostname3, _company.getMx(),
			_company.getMaxUsers(), _company.isActive());

		Assert.assertThrows(
			LayoutSetVirtualHostException.class,
			() -> _updateVirtualHosts(_virtualHostGroup2, asciiHostname3));
	}

	private Group _addGroup(
			Company company, Group parentGroup, String virtualHostName)
		throws Exception {

		Group group;

		if (parentGroup == null) {
			group = GroupTestUtil.addGroupToCompany(company.getCompanyId());
		}
		else {
			group = GroupTestUtil.addGroupToCompany(
				company.getCompanyId(), parentGroup.getGroupId());
		}

		if (virtualHostName != null) {
			_layoutSetLocalService.updateVirtualHosts(
				group.getGroupId(), false,
				TreeMapBuilder.put(
					virtualHostName, StringPool.BLANK
				).build());

			_groupVirtualHostNames.put(group, virtualHostName);
		}

		LayoutTestUtil.addTypePortletLayout(group);

		return group;
	}

	private void _assertURLtoString(
			boolean accessible, Group group, String virtualHostName)
		throws Exception {

		assertURLtoString(
			(body, responseCode) -> {
				if (!accessible) {
					Assert.assertEquals(
						HttpServletResponse.SC_NOT_FOUND, (long)responseCode);

					return;
				}

				Assert.assertEquals(
					HttpServletResponse.SC_OK, (long)responseCode);
				Assert.assertTrue(body.contains(group.getDescriptiveName()));

				Layout layout = _layoutLocalService.fetchDefaultLayout(
					group.getGroupId(), false);

				Assert.assertTrue(
					body.contains(layout.getName(LocaleUtil.getDefault())));
			},
			StringBundler.concat(
				"http://", virtualHostName, ":",
				PortalUtil.getPortalServerPort(false), "/web",
				group.getFriendlyURL()));
	}

	private String _getRandomUnicodeHostname() {
		return "b\u00fccher" +
			StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".com";
	}

	private void _setStrictMode(boolean allowDefaultInstanceURLBypass)
		throws Exception {

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				_company.getCompanyId(),
				"com.liferay.site.internal.configuration." +
					"SiteVirtualHostConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
					"allowDefaultInstanceURLBypass",
					allowDefaultInstanceURLBypass
				).put(
					"strictModeEnabled", true
				).build());
	}

	private void _updateVirtualHosts(Group group, String virtualHostName)
		throws Exception {

		_layoutSetLocalService.updateVirtualHosts(
			group.getGroupId(), false,
			TreeMapBuilder.put(
				() -> virtualHostName, StringPool.BLANK
			).build());
	}

	private Group _childGroup1;
	private Group _childGroup2;
	private Group _childGroup3;

	@DeleteAfterTestRun
	private Company _company;

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group1;
	private Group _group2;
	private Group _group3;

	@Inject
	private GroupLocalService _groupLocalService;

	private final Map<Group, String> _groupVirtualHostNames = new HashMap<>();
	private Group _guestGroup;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@DeleteAfterTestRun
	private Group _virtualHostGroup1;

	@DeleteAfterTestRun
	private Group _virtualHostGroup2;

}