/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.friendly.url.test.util.configuration.manager.FriendlyURLSeparatorConfigurationManagerTemporarySwapper;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.LayoutFriendlyURLSeparatorComposite;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class LayoutFriendlyURLSeparatorCompositeTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		ReflectionTestUtil.setFieldValue(
			FriendlyURLResolverRegistryUtil.class, "_urlSeparators",
			_urlSeparators);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();

		_urlSeparators.remove();
	}

	@Test
	public void testGetLayoutFriendlyURLSeparatorComposite() throws Exception {
		_assertGetLayoutFriendlyURLSeparatorComposite(
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);
	}

	@Test
	public void testGetLayoutFriendlyURLSeparatorCompositeWithConfiguredURLSeparator()
		throws Exception {

		String journalArticleFriendlyURLSeparator = "/journal-test1/";

		try (FriendlyURLSeparatorConfigurationManagerTemporarySwapper
				friendlyURLSeparatorConfigurationManagerTemporarySwapper =
					new FriendlyURLSeparatorConfigurationManagerTemporarySwapper(
						_group.getCompanyId(),
						JSONUtil.put(
							JournalArticle.class.getName(),
							journalArticleFriendlyURLSeparator
						).toString())) {

			_assertGetLayoutFriendlyURLSeparatorComposite(
				journalArticleFriendlyURLSeparator);
		}
	}

	private void _assertGetLayoutFriendlyURLSeparatorComposite(
			String urlSeparator)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutFriendlyURLSeparatorComposite
			layoutFriendlyURLSeparatorComposite =
				_portal.getLayoutFriendlyURLSeparatorComposite(
					_group.getGroupId(), true,
					urlSeparator + journalArticle.getUrlTitle(),
					new HashMap<String, String[]>(),
					HashMapBuilder.<String, Object>put(
						"request",
						() -> {
							MockHttpServletRequest mockHttpServletRequest =
								new MockHttpServletRequest();

							mockHttpServletRequest.setAttribute(
								WebKeys.COMPANY_ID, _group.getCompanyId());

							return mockHttpServletRequest;
						}
					).build());

		Assert.assertNotNull(layoutFriendlyURLSeparatorComposite);
		Assert.assertEquals(
			urlSeparator,
			layoutFriendlyURLSeparatorComposite.getURLSeparator());
	}

	private static final ThreadLocal<String[]> _urlSeparators =
		new CentralizedThreadLocal<>(
			FriendlyURLResolverRegistryUtil.class.getName() +
				"._urlSeparators");

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

}