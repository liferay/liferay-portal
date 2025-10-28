/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.exception.DefaultLayoutUtilityPageEntryException;
import com.liferay.layout.utility.page.exception.DuplicateLayoutUtilityPageEntryExternalReferenceCodeException;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class LayoutUtilityPageEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddLayoutUtilityPageEntry() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				true, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), 0, _serviceContext);

		Assert.assertTrue(
			Validator.isNotNull(
				layoutUtilityPageEntry.getExternalReferenceCode()));

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertTrue(layout.isPublished());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, layout.getStatus());

		layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), 0, _serviceContext);

		Assert.assertTrue(
			Validator.isNotNull(
				layoutUtilityPageEntry.getExternalReferenceCode()));

		layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertFalse(layout.isPublished());
		Assert.assertEquals(WorkflowConstants.STATUS_DRAFT, layout.getStatus());

		try {
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				layoutUtilityPageEntry.getExternalReferenceCode(),
				TestPropsValues.getUserId(), _group.getGroupId(), 0, 0, true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0,
				_serviceContext);

			Assert.fail();
		}
		catch (DuplicateLayoutUtilityPageEntryExternalReferenceCodeException
					duplicateLayoutUtilityPageEntryExternalReferenceCodeException) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					duplicateLayoutUtilityPageEntryExternalReferenceCodeException);
			}
		}
	}

	@Test
	@TestInfo("LPD-67157")
	public void testDeleteLayoutUtilityPageEntryByExternalReferenceCode()
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				true, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), 0, _serviceContext);

		_layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntry.getExternalReferenceCode(),
			layoutUtilityPageEntry.getGroupId());

		Assert.assertNull(
			_layoutUtilityPageEntryLocalService.fetchLayoutUtilityPageEntry(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId()));
		Assert.assertNull(
			_systemEventLocalService.fetchSystemEvent(
				_group.getGroupId(), _portal.getClassNameId(Layout.class),
				layoutUtilityPageEntry.getPlid(),
				SystemEventConstants.TYPE_DELETE));
		Assert.assertNotNull(
			_systemEventLocalService.fetchSystemEvent(
				_group.getGroupId(),
				_portal.getClassNameId(LayoutUtilityPageEntry.class),
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
				SystemEventConstants.TYPE_DELETE));
	}

	@Test
	public void testGetExternalReferenceCode() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.addLayoutUtilityPageEntry(
				null, _group.getGroupId(), 0, 0, true,
				"Test Layout Utility Page", RandomTestUtil.randomString(), 0,
				_serviceContext);

		Assert.assertEquals(
			"test-layout-utility-page",
			layoutUtilityPageEntry.getExternalReferenceCode());

		_testGetExternalReferenceCode(
			layoutUtilityPageEntry.getExternalReferenceCode(),
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId());

		layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.addLayoutUtilityPageEntry(
				"ERC", _group.getGroupId(), 0, 0, true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0,
				_serviceContext);

		Assert.assertEquals(
			"ERC", layoutUtilityPageEntry.getExternalReferenceCode());

		_testGetExternalReferenceCode(
			layoutUtilityPageEntry.getExternalReferenceCode(),
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId());
	}

	@Test
	public void testSetDefaultLayoutUtilityPageEntry() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry1 =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				true, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, 0,
				_serviceContext);

		Assert.assertTrue(
			layoutUtilityPageEntry1.isDefaultLayoutUtilityPageEntry());

		LayoutUtilityPageEntry layoutUtilityPageEntry2 =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, 0,
				_serviceContext);

		Assert.assertFalse(
			layoutUtilityPageEntry2.isDefaultLayoutUtilityPageEntry());

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry2.getPlid());

		Assert.assertFalse(layout.isPublished());

		try {
			_layoutUtilityPageEntryLocalService.
				setDefaultLayoutUtilityPageEntry(
					layoutUtilityPageEntry2.getLayoutUtilityPageEntryId());

			Assert.fail();
		}
		catch (DefaultLayoutUtilityPageEntryException
					defaultLayoutUtilityPageEntryException) {

			if (_log.isDebugEnabled()) {
				_log.debug(defaultLayoutUtilityPageEntryException);
			}
		}

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		layoutUtilityPageEntry2 =
			_layoutUtilityPageEntryLocalService.
				setDefaultLayoutUtilityPageEntry(
					layoutUtilityPageEntry2.getLayoutUtilityPageEntryId());

		Assert.assertTrue(
			layoutUtilityPageEntry2.isDefaultLayoutUtilityPageEntry());

		layoutUtilityPageEntry1 =
			_layoutUtilityPageEntryLocalService.getLayoutUtilityPageEntry(
				layoutUtilityPageEntry1.getLayoutUtilityPageEntryId());

		Assert.assertFalse(
			layoutUtilityPageEntry1.isDefaultLayoutUtilityPageEntry());
	}

	@Test
	@TestInfo("LPD-52440")
	public void testUpdateLayoutUtilityPageEntry() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				true, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, 0,
				_serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Locale locale = LocaleUtil.getSiteDefault();

		String title = layout.getTitle(locale);

		Assert.assertEquals(title, layout.getName(locale));

		String name = RandomTestUtil.randomString();

		_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId(), name);

		layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertEquals(name, layout.getName(locale));
		Assert.assertEquals(title, layout.getTitle(locale));
	}

	private void _testGetExternalReferenceCode(
			String externalReferenceCode, long layoutUtilityPageEntryId)
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(
			layoutUtilityPageEntryId,
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryLocalServiceTest.class);

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	@DeleteAfterTestRun
	private User _user;

}