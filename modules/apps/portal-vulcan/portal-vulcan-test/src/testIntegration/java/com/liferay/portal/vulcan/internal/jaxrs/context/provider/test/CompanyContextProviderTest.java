/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockFeature;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockMessage;

import jakarta.ws.rs.core.Feature;

import org.apache.cxf.jaxrs.ext.ContextProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class CompanyContextProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		MockFeature mockFeature = new MockFeature(_feature);

		_contextProvider = (ContextProvider<Company>)mockFeature.getObject(
			"com.liferay.portal.vulcan.internal.jaxrs.context.provider." +
				"CompanyContextProvider");
	}

	@Test
	public void testCreateContext() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try {
			User user = UserTestUtil.addCompanyAdminUser(company);

			Group group = GroupTestUtil.addGroup(
				company.getCompanyId(), user.getUserId(), 0L);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest() {
					{
						addHeader("Host", company.getVirtualHostname());
						setRemoteHost(company.getPortalURL(group.getGroupId()));
					}
				};

			Assert.assertEquals(
				company,
				_contextProvider.createContext(
					new MockMessage(mockHttpServletRequest)));
		}
		finally {
			CompanyLocalServiceUtil.deleteCompany(company.getCompanyId());
		}
	}

	private ContextProvider<Company> _contextProvider;

	@Inject(
		filter = "component.name=com.liferay.portal.vulcan.internal.jaxrs.feature.VulcanFeature"
	)
	private Feature _feature;

}