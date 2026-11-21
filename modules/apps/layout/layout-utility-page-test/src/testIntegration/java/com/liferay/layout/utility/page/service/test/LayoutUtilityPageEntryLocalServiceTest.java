/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.exception.DefaultLayoutUtilityPageEntryException;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
	@TestInfo({"LPD-71983", "LPD-74327"})
	public void testAddLayoutUtilityPageEntry() throws Exception {
		String name = String.valueOf(RandomTestUtil.nextInt());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				true, name, RandomTestUtil.randomString(), null,
				_serviceContext);

		Assert.assertTrue(
			Validator.isNotNull(
				layoutUtilityPageEntry.getExternalReferenceCode()));

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertEquals(
			StringBundler.concat(StringPool.SLASH, name, StringPool.DASH, 1),
			layout.getFriendlyURL());
		Assert.assertTrue(layout.isPublished());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, layout.getStatus());

		layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, _serviceContext);

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
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null, _serviceContext);

			Assert.fail();
		}
		catch (DuplicateExternalReferenceCodeException
					duplicateExternalReferenceCodeException) {

			if (_log.isDebugEnabled()) {
				_log.debug(duplicateExternalReferenceCodeException);
			}
		}

		_testAddLayoutUtilityPageEntryWithExternalReferenceCode();
	}

	@Test
	public void testGetExternalReferenceCode() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.addLayoutUtilityPageEntry(
				null, _group.getGroupId(), 0, 0, true,
				"Test Layout Utility Page", RandomTestUtil.randomString(), null,
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
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null, _serviceContext);

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
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				_serviceContext);

		Assert.assertTrue(
			layoutUtilityPageEntry1.isDefaultLayoutUtilityPageEntry());

		LayoutUtilityPageEntry layoutUtilityPageEntry2 =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
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
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				_serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Locale locale = LocaleUtil.getSiteDefault();

		String title = layout.getTitle(locale);

		Assert.assertEquals(title, layout.getName(locale));

		String name = RandomTestUtil.randomString();

		_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId(), name,
			_serviceContext);

		layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertEquals(name, layout.getName(locale));
		Assert.assertEquals(title, layout.getTitle(locale));
	}

	private void _assertExternalReferenceCodes(
			Layout layout,
			UnsafeBiFunction<String, String, Boolean, Exception>
				unsafeBiFunction)
		throws Exception {

		Assert.assertTrue(
			layout.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				layout.getExternalReferenceCode(), "-layout"));

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				layout.getPlid());

		Assert.assertTrue(
			segmentsExperience.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				segmentsExperience.getExternalReferenceCode(),
				"-layout-default"));

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertTrue(
			draftLayout.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				draftLayout.getExternalReferenceCode(), "-layout-draft"));

		segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				draftLayout.getPlid());

		Assert.assertTrue(
			segmentsExperience.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				segmentsExperience.getExternalReferenceCode(),
				"-layout-draft-default"));
	}

	private void _testAddLayoutUtilityPageEntryWithExternalReferenceCode()
		throws Exception {

		_testAddLayoutUtilityPageEntryWithExternalReferenceCode(
			RandomTestUtil.randomString(),
			externalReferenceCode ->
				(curExternalReferenceCode, suffix) -> Objects.equals(
					curExternalReferenceCode, externalReferenceCode + suffix));

		_testAddLayoutUtilityPageEntryWithExternalReferenceCode(
			null,
			externalReferenceCode ->
				(curExternalReferenceCode, suffix) -> Objects.equals(
					curExternalReferenceCode, externalReferenceCode + suffix));

		Map<String, String> expectedExternalReferenceCodesMap =
			HashMapBuilder.put(
				"-layout", RandomTestUtil.randomString()
			).put(
				"-layout-default", RandomTestUtil.randomString()
			).put(
				"-layout-draft", RandomTestUtil.randomString()
			).put(
				"-layout-draft-default", RandomTestUtil.randomString()
			).build();

		_serviceContext.setAttribute(
			"defaultSegmentsExperienceExternalReferenceCode",
			expectedExternalReferenceCodesMap.get("-layout-default"));
		_serviceContext.setAttribute(
			"draftLayoutDefaultSegmentsExperienceExternalReferenceCode",
			expectedExternalReferenceCodesMap.get("-layout-draft-default"));
		_serviceContext.setAttribute(
			"draftLayoutExternalReferenceCode",
			expectedExternalReferenceCodesMap.get("-layout-draft"));
		_serviceContext.setAttribute(
			"layoutExternalReferenceCode",
			expectedExternalReferenceCodesMap.get("-layout"));

		try {
			_testAddLayoutUtilityPageEntryWithExternalReferenceCode(
				RandomTestUtil.randomString(),
				externalReferenceCode ->
					(curExternalReferenceCode, suffix) -> Objects.equals(
						curExternalReferenceCode,
						expectedExternalReferenceCodesMap.get(suffix)));
		}
		finally {
			_serviceContext.removeAttribute(
				"defaultSegmentsExperienceExternalReferenceCode");
			_serviceContext.removeAttribute(
				"draftLayoutDefaultSegmentsExperienceExternalReferenceCode");
			_serviceContext.removeAttribute("draftLayoutExternalReferenceCode");
			_serviceContext.removeAttribute("layoutExternalReferenceCode");
		}
	}

	private void _testAddLayoutUtilityPageEntryWithExternalReferenceCode(
			String externalReferenceCode,
			UnsafeFunction
				<String, UnsafeBiFunction<String, String, Boolean, Exception>,
				 Exception> unsafeFunction)
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(), 0, 0, true, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, _serviceContext);

		if (Validator.isNull(externalReferenceCode)) {
			Assert.assertNotNull(
				layoutUtilityPageEntry.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				externalReferenceCode,
				layoutUtilityPageEntry.getExternalReferenceCode());
		}

		_assertExternalReferenceCodes(
			_layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()),
			unsafeFunction.apply(
				layoutUtilityPageEntry.getExternalReferenceCode()));
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
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}