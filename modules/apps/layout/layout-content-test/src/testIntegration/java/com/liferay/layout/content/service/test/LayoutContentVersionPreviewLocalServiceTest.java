/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.content.exception.DuplicateLayoutContentVersionPreviewException;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-10622")
@RunWith(Arquillian.class)
public class LayoutContentVersionPreviewLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		_layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), draftLayout.getPlid(),
				WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	@TestInfo("LPD-90030")
	public void testAddLayoutContentVersionPreview() throws Exception {
		User user = _userLocalService.getUser(TestPropsValues.getUserId());
		String html = RandomTestUtil.randomString();
		String languageId = RandomTestUtil.randomString();
		String segmentsExperienceERC = RandomTestUtil.randomString();

		LayoutContentVersionPreview layoutContentVersionPreview =
			_layoutContentVersionPreviewLocalService.
				addLayoutContentVersionPreview(
					user.getUserId(),
					_layoutContentVersion.getLayoutContentVersionId(), html,
					languageId, segmentsExperienceERC);

		Assert.assertEquals(
			_layoutContentVersion.getGroupId(),
			layoutContentVersionPreview.getGroupId());
		Assert.assertEquals(
			_layoutContentVersion.getCompanyId(),
			layoutContentVersionPreview.getCompanyId());
		Assert.assertEquals(
			user.getUserId(), layoutContentVersionPreview.getUserId());
		Assert.assertEquals(
			user.getFullName(), layoutContentVersionPreview.getUserName());
		Assert.assertEquals(html, layoutContentVersionPreview.getHtml());
		Assert.assertEquals(
			languageId, layoutContentVersionPreview.getLanguageId());
		Assert.assertEquals(
			_layoutContentVersion.getLayoutContentVersionId(),
			layoutContentVersionPreview.getLayoutContentVersionId());
		Assert.assertEquals(
			segmentsExperienceERC,
			layoutContentVersionPreview.getSegmentsExperienceERC());

		DuplicateLayoutContentVersionPreviewException
			duplicateLayoutContentVersionPreviewException = Assert.assertThrows(
				DuplicateLayoutContentVersionPreviewException.class,
				() ->
					_layoutContentVersionPreviewLocalService.
						addLayoutContentVersionPreview(
							TestPropsValues.getUserId(),
							_layoutContentVersion.getLayoutContentVersionId(),
							RandomTestUtil.randomString(), languageId,
							segmentsExperienceERC));

		Assert.assertEquals(
			StringBundler.concat(
				"Duplicate layout content version preview for layout content ",
				"version ", _layoutContentVersion.getLayoutContentVersionId(),
				", language ID ", languageId,
				", and segments experience external reference code ",
				segmentsExperienceERC),
			duplicateLayoutContentVersionPreviewException.getMessage());
	}

	@Test
	@TestInfo("LPD-90030")
	public void testDeleteLayoutContentVersionPreviews() throws Exception {
		List<LayoutContentVersionPreview> initialLayoutContentVersionPreviews =
			_layoutContentVersionPreviewLocalService.
				getLayoutContentVersionPreviews(
					_layoutContentVersion.getLayoutContentVersionId());

		_addLayoutContentVersionPreviews(2);

		List<LayoutContentVersionPreview> layoutContentVersionPreviews =
			_layoutContentVersionPreviewLocalService.
				getLayoutContentVersionPreviews(
					_layoutContentVersion.getLayoutContentVersionId());

		Assert.assertEquals(
			layoutContentVersionPreviews.toString(),
			initialLayoutContentVersionPreviews.size() + 2,
			layoutContentVersionPreviews.size());

		_layoutContentVersionPreviewLocalService.
			deleteLayoutContentVersionPreviews(
				_layoutContentVersion.getLayoutContentVersionId());

		layoutContentVersionPreviews =
			_layoutContentVersionPreviewLocalService.
				getLayoutContentVersionPreviews(
					_layoutContentVersion.getLayoutContentVersionId());

		Assert.assertTrue(
			layoutContentVersionPreviews.toString(),
			layoutContentVersionPreviews.isEmpty());
	}

	@Test
	@TestInfo("LPD-90030")
	public void testFetchLayoutContentVersionPreview() throws Exception {
		String html = RandomTestUtil.randomString();
		String languageId = RandomTestUtil.randomString();
		String segmentsExperienceERC = RandomTestUtil.randomString();

		_layoutContentVersionPreviewLocalService.addLayoutContentVersionPreview(
			TestPropsValues.getUserId(),
			_layoutContentVersion.getLayoutContentVersionId(), html, languageId,
			segmentsExperienceERC);

		LayoutContentVersionPreview layoutContentVersionPreview =
			_layoutContentVersionPreviewLocalService.
				fetchLayoutContentVersionPreview(
					_layoutContentVersion.getLayoutContentVersionId(),
					languageId, segmentsExperienceERC);

		Assert.assertEquals(html, layoutContentVersionPreview.getHtml());

		Assert.assertNull(
			_layoutContentVersionPreviewLocalService.
				fetchLayoutContentVersionPreview(
					_layoutContentVersion.getLayoutContentVersionId(),
					RandomTestUtil.randomString(), segmentsExperienceERC));
	}

	@Test
	@TestInfo("LPD-90030")
	public void testGetLayoutContentVersionPreviews() throws Exception {
		List<LayoutContentVersionPreview> initialLayoutContentVersionPreviews =
			_layoutContentVersionPreviewLocalService.
				getLayoutContentVersionPreviews(
					_layoutContentVersion.getLayoutContentVersionId());

		_addLayoutContentVersionPreviews(2);

		List<LayoutContentVersionPreview> layoutContentVersionPreviews =
			_layoutContentVersionPreviewLocalService.
				getLayoutContentVersionPreviews(
					_layoutContentVersion.getLayoutContentVersionId());

		Assert.assertEquals(
			layoutContentVersionPreviews.toString(),
			initialLayoutContentVersionPreviews.size() + 2,
			layoutContentVersionPreviews.size());
	}

	@Test
	@TestInfo("LPD-103339")
	public void testGetSegmentsExperienceERCsLanguageIds() throws Exception {
		String languageId1 = RandomTestUtil.randomString();
		String segmentsExperienceERC1 = RandomTestUtil.randomString();
		String segmentsExperienceERC2 = RandomTestUtil.randomString();

		_addLayoutContentVersionPreviews(
			languageId1, segmentsExperienceERC1, segmentsExperienceERC2);

		String languageId2 = RandomTestUtil.randomString();

		_addLayoutContentVersionPreviews(languageId2, segmentsExperienceERC1);

		Map<String, List<String>> segmentsExperienceERCsLanguageIds =
			_layoutContentVersionPreviewLocalService.
				getSegmentsExperienceERCsLanguageIds(
					_layoutContentVersion.getLayoutContentVersionId());

		List<String> languageIds = segmentsExperienceERCsLanguageIds.get(
			segmentsExperienceERC1);

		Assert.assertTrue(
			languageIds.toString(), languageIds.contains(languageId1));
		Assert.assertTrue(
			languageIds.toString(), languageIds.contains(languageId2));
		Assert.assertEquals(languageIds.toString(), 2, languageIds.size());

		languageIds = segmentsExperienceERCsLanguageIds.get(
			segmentsExperienceERC2);

		Assert.assertTrue(
			languageIds.toString(), languageIds.contains(languageId1));
		Assert.assertEquals(languageIds.toString(), 1, languageIds.size());
	}

	private void _addLayoutContentVersionPreviews(int count) throws Exception {
		String segmentsExperienceERC = RandomTestUtil.randomString();

		for (int i = 0; i < count; i++) {
			_layoutContentVersionPreviewLocalService.
				addLayoutContentVersionPreview(
					TestPropsValues.getUserId(),
					_layoutContentVersion.getLayoutContentVersionId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), segmentsExperienceERC);
		}
	}

	private void _addLayoutContentVersionPreviews(
			String languageId, String... segmentsExperienceERCs)
		throws Exception {

		for (String segmentsExperienceERC : segmentsExperienceERCs) {
			_layoutContentVersionPreviewLocalService.
				addLayoutContentVersionPreview(
					TestPropsValues.getUserId(),
					_layoutContentVersion.getLayoutContentVersionId(),
					RandomTestUtil.randomString(), languageId,
					segmentsExperienceERC);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	private LayoutContentVersion _layoutContentVersion;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

	@Inject
	private UserLocalService _userLocalService;

}