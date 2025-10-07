/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateEntryExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryDefaultTemplateException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryGroupIdException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryLayoutPageTemplateCollectionIdException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryLayoutPageTemplateEntryKeyException;
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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				_group.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	public void testAddLayoutPageTemplateEntry() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		_assertLayoutPageTemplateEntry(false, layoutPageTemplateEntry);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertFalse(layout.isPublished());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0, true, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_assertLayoutPageTemplateEntry(true, layoutPageTemplateEntry);

		Assert.assertTrue(layoutPageTemplateEntry.isDefaultTemplate());

		layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertTrue(layout.isPublished());

		try {
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0, true, 0,
				0, 0, WorkflowConstants.STATUS_DRAFT, _serviceContext);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryDefaultTemplateException
					layoutPageTemplateEntryDefaultTemplateException) {

			if (_log.isDebugEnabled()) {
				_log.debug(layoutPageTemplateEntryDefaultTemplateException);
			}
		}

		try {
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0, false, 0,
				0, 0, WorkflowConstants.STATUS_DRAFT, _serviceContext);

			Assert.fail();
		}
		catch (DuplicateLayoutPageTemplateEntryExternalReferenceCodeException
					duplicateLayoutPageTemplateEntryExternalReferenceCodeException) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					duplicateLayoutPageTemplateEntryExternalReferenceCodeException);
			}
		}

		_assertLayoutPageTemplateEntry(
			false,
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0, false, 0, 0, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext));
		_assertLayoutPageTemplateEntry(
			false,
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, false, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, _serviceContext));

		LayoutPageTemplateCollection displayPageLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		_assertLayoutPageTemplateEntry(
			true,
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				displayPageLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, true, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, _serviceContext));

		_assertLayoutPageTemplateEntry(
			false,
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE, 0, false, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, _serviceContext));

		_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			_group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			LayoutPageTemplateEntryTypeConstants.BASIC);
		_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			_group.getGroupId(),
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);
		_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			_group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE);

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
			companyGroup.getGroupId(),
			LayoutPageTemplateEntryTypeConstants.BASIC);
		_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
			companyGroup.getGroupId(),
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
		_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
			companyGroup.getGroupId(),
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		_assertLayoutPageTemplateEntry(
			false,
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), companyGroup.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE, 0, false, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, _serviceContext));

		_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			companyGroup.getGroupId(), RandomTestUtil.randomLong(),
			LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE);

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY, _serviceContext);

		Group depotGroup = depotEntry.getGroup();

		try {
			_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
				depotGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.BASIC);
			_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
				depotGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
			_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
				depotGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);
			_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
				depotGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE);
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}

		String layoutPageTemplateEntryKey = RandomTestUtil.randomString();

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				layoutPageTemplateEntryKey, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0, false, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, _serviceContext);

		Assert.assertEquals(
			layoutPageTemplateEntryKey,
			layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());

		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			LayoutPageTemplateEntryLayoutPageTemplateEntryKeyException.
				MustNotBeDuplicate.class,
			layoutPageTemplateEntryKey,
			StringBundler.concat(
				"Duplicate layout page template entry for group ",
				_group.getGroupId(), " with layout page template entry key ",
				layoutPageTemplateEntryKey));

		layoutPageTemplateEntryKey =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			LayoutPageTemplateEntryLayoutPageTemplateEntryKeyException.
				MustNotContainInvalidCharacters.class,
			layoutPageTemplateEntryKey,
			StringBundler.concat(
				"Layout page template entry key ", layoutPageTemplateEntryKey,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		layoutPageTemplateEntryKey = RandomTestUtil.randomString(80);

		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			LayoutPageTemplateEntryLayoutPageTemplateEntryKeyException.
				MustNotExceedMaximumSize.class,
			layoutPageTemplateEntryKey,
			StringBundler.concat(
				"Layout page template entry key ", layoutPageTemplateEntryKey,
				" must have fewer than 75 characters"));
	}

	@Test
	public void testDeleteLayoutPageTemplateEntryByExternalReferenceCode()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getExternalReferenceCode(),
			layoutPageTemplateEntry.getGroupId());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
	}

	@Test
	@TestInfo("LPD-61203")
	public void testMoveLayoutPageTemplateEntry() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		LayoutPageTemplateCollection basicLayoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				_group.getGroupId());

		_testMoveLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			basicLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());

		LayoutPageTemplateCollection displayPageLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			displayPageLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());

		_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT);

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		_testMoveLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			displayPageLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());
		_testMoveLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT);
		_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			basicLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			basicLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());
		_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			displayPageLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_testMoveLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			basicLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());
		_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			displayPageLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());
		_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT);

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), companyGroup.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					companyGroup, TestPropsValues.getUserId()));

		try {
			_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				basicLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());
		}
		finally {
			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
		}
	}

	@Test
	public void testUpdateLayoutPageTemplateEntryDefaultTemplate()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		try {
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), true);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryDefaultTemplateException
					layoutPageTemplateEntryDefaultTemplateException) {

			if (_log.isDebugEnabled()) {
				_log.debug(layoutPageTemplateEntryDefaultTemplateException);
			}
		}

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				WorkflowConstants.STATUS_APPROVED);

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), true);

		Assert.assertTrue(layoutPageTemplateEntry.isDefaultTemplate());

		try {
			_layoutPageTemplateEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				WorkflowConstants.STATUS_DRAFT);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryDefaultTemplateException
					layoutPageTemplateEntryDefaultTemplateException) {

			if (_log.isDebugEnabled()) {
				_log.debug(layoutPageTemplateEntryDefaultTemplateException);
			}
		}

		try {
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				TestPropsValues.getUserId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				RandomTestUtil.randomString(), WorkflowConstants.STATUS_DRAFT);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryDefaultTemplateException
					layoutPageTemplateEntryDefaultTemplateException) {

			if (_log.isDebugEnabled()) {
				_log.debug(layoutPageTemplateEntryDefaultTemplateException);
			}
		}
	}

	@Test
	public void testUpdateLayoutPageTemplateEntryName() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC,
				masterLayoutPageTemplateEntry.getPlid(),
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				_serviceContext);

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		_layoutLocalService.updateStyleBookEntryId(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			styleBookEntry.getStyleBookEntryId());

		Layout draftLayout = layout.fetchDraftLayout();

		_layoutLocalService.updateStyleBookEntryId(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(), styleBookEntry.getStyleBookEntryId());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				RandomTestUtil.randomString());

		layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(), layout.getStyleBookEntryId());

		draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			draftLayout.getMasterLayoutPlid());
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			draftLayout.getStyleBookEntryId());
	}

	private void _assertLayoutPageTemplateEntry(
		boolean defaultTemplate,
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		if (defaultTemplate) {
			Assert.assertTrue(layoutPageTemplateEntry.isDefaultTemplate());
		}
		else {
			Assert.assertFalse(layoutPageTemplateEntry.isDefaultTemplate());
		}

		Assert.assertTrue(
			Validator.isNotNull(
				layoutPageTemplateEntry.getExternalReferenceCode()));
	}

	private void
		_testAddLayoutPageTemplateCollectionWithInvalidLayoutPageTemplateCollectionKey(
			Class<?> clazz, String layoutPageTemplateEntryKey, String message) {

		try {
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				layoutPageTemplateEntryKey, 0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0, false, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, _serviceContext);

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertEquals(clazz, portalException.getClass());
			Assert.assertEquals(message, portalException.getMessage());
		}
	}

	private void
			_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryGroupIdException(
				long groupId, int type)
		throws PortalException {

		try {
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), groupId,
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, 0, 0, RandomTestUtil.randomString(), type, 0, false, 0, 0,
				0, WorkflowConstants.STATUS_APPROVED, _serviceContext);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryGroupIdException
					layoutPageTemplateEntryGroupIdException) {

			if (_log.isDebugEnabled()) {
				_log.debug(layoutPageTemplateEntryGroupIdException);
			}
		}
	}

	private void
			_testAddLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
				long groupId, long layoutPageTemplateCollectionId, int type)
		throws Exception {

		try {
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), groupId,
				layoutPageTemplateCollectionId, null, 0, 0,
				RandomTestUtil.randomString(), type, 0, false, 0, 0, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryLayoutPageTemplateCollectionIdException
					layoutPageTemplateEntryLayoutPageTemplateCollectionIdException) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					layoutPageTemplateEntryLayoutPageTemplateCollectionIdException);
			}
		}
	}

	private void _testMoveLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long layoutPageTemplateCollectionId)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.moveLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, layoutPageTemplateCollectionId);

		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			layoutPageTemplateEntry.getLayoutPageTemplateCollectionId());
	}

	private void
			_testMoveLayoutPageTemplateEntryLayoutPageTemplateEntryLayoutPageTemplateCollectionIdException(
				long layoutPageTemplateEntryId,
				long layoutPageTemplateCollectionId)
		throws Exception {

		try {
			_layoutPageTemplateEntryLocalService.moveLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, layoutPageTemplateCollectionId);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryLayoutPageTemplateCollectionIdException
					layoutPageTemplateEntryLayoutPageTemplateCollectionIdException) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					layoutPageTemplateEntryLayoutPageTemplateCollectionIdException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryLocalServiceTest.class);

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private LayoutPageTemplateCollection _layoutPageTemplateCollection;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}