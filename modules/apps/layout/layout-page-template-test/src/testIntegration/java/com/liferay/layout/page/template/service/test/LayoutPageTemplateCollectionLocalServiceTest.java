/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionGroupIdException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
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
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateCollectionLocalServiceTest {

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
	}

	@Test
	public void testAddLayoutPageTemplateCollection() throws Exception {
		String externalReferenceCode = StringUtil.randomString();

		_layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(), null,
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				_serviceContext);

		Assert.assertNotNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId()));

		try {
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					externalReferenceCode, TestPropsValues.getUserId(),
					_group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					_serviceContext);

			Assert.fail();
		}
		catch (DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException
					duplicateLayoutPageTemplateCollectionExternalReferenceCodeException) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					duplicateLayoutPageTemplateCollectionExternalReferenceCodeException);
			}
		}

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		try {
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(),
					companyGroup.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						companyGroup.getGroupId()));

			Assert.fail();
		}
		catch (LayoutPageTemplateCollectionGroupIdException
					layoutPageTemplateCollectionGroupIdException) {

			if (_log.isDebugEnabled()) {
				_log.debug(layoutPageTemplateCollectionGroupIdException);
			}
		}

		String layoutPageTemplateCollectionKey = RandomTestUtil.randomString();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					layoutPageTemplateCollectionKey,
					RandomTestUtil.randomString(), null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					_serviceContext);

		Assert.assertEquals(
			layoutPageTemplateCollectionKey,
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionKey());

		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotBeDuplicate.class,
			layoutPageTemplateCollectionKey,
			StringBundler.concat(
				"Duplicate layout page template collection for group ",
				_group.getGroupId(),
				" with layout page template collection key ",
				layoutPageTemplateCollectionKey));

		layoutPageTemplateCollectionKey =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotContainInvalidCharacters.class,
			layoutPageTemplateCollectionKey,
			StringBundler.concat(
				"Layout page template collection key ",
				layoutPageTemplateCollectionKey,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		layoutPageTemplateCollectionKey = RandomTestUtil.randomString(80);

		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotExceedMaximumSize.class,
			layoutPageTemplateCollectionKey,
			StringBundler.concat(
				"Layout page template collection key ",
				layoutPageTemplateCollectionKey,
				" must have fewer than 75 characters"));
	}

	@Test
	@TestInfo("LPD-67157")
	public void testDeleteLayoutPageTemplateCollectionByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					externalReferenceCode, TestPropsValues.getUserId(),
					_group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					_serviceContext);

		Assert.assertNotNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				RandomTestUtil.randomString());

		_layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(
				externalReferenceCode, _group.getGroupId());

		Assert.assertNull(
			_layoutLocalService.fetchLayout(layoutPageTemplateEntry.getPlid()));
		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId()));
		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
		Assert.assertNull(
			_systemEventLocalService.fetchSystemEvent(
				_group.getGroupId(), _portal.getClassNameId(Layout.class),
				layoutPageTemplateEntry.getPlid(),
				SystemEventConstants.TYPE_DELETE));
		Assert.assertNotNull(
			_systemEventLocalService.fetchSystemEvent(
				_group.getGroupId(),
				_portal.getClassNameId(LayoutPageTemplateCollection.class),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				SystemEventConstants.TYPE_DELETE));
		Assert.assertNull(
			_systemEventLocalService.fetchSystemEvent(
				_group.getGroupId(),
				_portal.getClassNameId(LayoutPageTemplateEntry.class),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				SystemEventConstants.TYPE_DELETE));
	}

	private void
		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			Class<?> clazz, String layoutPageTemplateCollectionKey,
			String message) {

		try {
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					layoutPageTemplateCollectionKey,
					RandomTestUtil.randomString(), null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					_serviceContext);

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertEquals(clazz, portalException.getClass());
			Assert.assertEquals(message, portalException.getMessage());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateCollectionLocalServiceTest.class);

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

}