/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.exception.NoSuchClassNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class DisplayPageTemplateServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddDisplayPageTemplate() throws PortalException {
		String name = RandomTestUtil.randomString();

		LayoutPageTemplateEntry displayPageTemplate =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null, name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		LayoutPageTemplateEntry persistedDisplayPageTemplate =
			_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
				displayPageTemplate.getLayoutPageTemplateEntryId());

		Assert.assertEquals(name, persistedDisplayPageTemplate.getName());
	}

	@Test(expected = NoSuchClassNameException.class)
	public void testAddDisplayPageWithInvalidClassNameId()
		throws PortalException {

		_createDisplayPageEntry(
			0, RandomTestUtil.randomLong(), RandomTestUtil.randomString());
	}

	@Test
	public void testDeleteDisplayPageTemplate() throws PortalException {
		LayoutPageTemplateEntry displayPageTemplate =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId());

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			displayPageTemplate.getLayoutPageTemplateEntryId());

		Assert.assertNull(
			_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
				displayPageTemplate.getLayoutPageTemplateEntryId()));
	}

	private LayoutPageTemplateEntry _createDisplayPageEntry(
			long classNameId, long classTypeId, String classTypeKey)
		throws PortalException {

		return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
			null, _group.getGroupId(), 0, null, classNameId, classTypeId,
			classTypeKey, RandomTestUtil.randomString(), 0,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Inject
	private static LayoutPageTemplateEntryService
		_layoutPageTemplateEntryService;

	@DeleteAfterTestRun
	private Group _group;

}