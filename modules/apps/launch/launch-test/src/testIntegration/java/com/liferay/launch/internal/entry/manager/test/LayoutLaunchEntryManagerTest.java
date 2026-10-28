/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-10622")
@RunWith(Arquillian.class)
public class LayoutLaunchEntryManagerTest
	extends BaseLaunchEntryManagerTestCase
		<LayoutLaunchEntryManagerTest.Values> {

	@Override
	@Test
	public void testAddDraft() throws Exception {
		super.testAddDraft();

		_testAddDraftCopiesLayoutContent();
		_testAddDraftWithExistingLaunchDraft();
	}

	@Override
	@Test
	public void testFetchDraft() throws Exception {
		super.testFetchDraft();

		_testFetchDraftLayoutByLaunchSetId();
	}

	@Override
	@Test
	public void testGetPreviewBaseModel() throws Exception {
		super.testGetPreviewBaseModel();

		_testGetPreviewBaseModelForPublishedVersionReturnsLive();
		_testGetPreviewBaseModelPreservesPublishedFriendlyURL();
	}

	@Override
	@Test
	public void testGetVersions() throws Exception {
		super.testGetVersions();

		_testGetVersionsWithExistingMainDraft();
	}

	@Override
	@Test
	public void testPublishDraft() throws Exception {
		super.testPublishDraft();

		_testPublishDraftReturnsStableVersionKey();
		_testPublishDraftWithExistingMainDraft();
	}

	@Override
	protected long addEntry() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentPublishedLayout(
			group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		return layout.getPlid();
	}

	@Override
	protected Values createValue() {
		return new Values(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Override
	protected String getBackURLParameterName() {
		return "p_l_back_url";
	}

	@Override
	protected Values getDraftValue(LaunchEntryManager.Draft draft)
		throws Exception {

		return _getValues(_layoutLocalService.getLayout(draft.getDraftId()));
	}

	@Override
	protected Values getEntryValue(long classPK) throws Exception {
		return _getValues(_layoutLocalService.getLayout(classPK));
	}

	@Override
	protected List<Serializable> getListedPrimaryKeys(long classPK)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(classPK);

		return TransformUtil.transform(
			_layoutLocalService.getLayouts(
				layout.getGroupId(), layout.isPrivateLayout()),
			Layout::getPlid);
	}

	@Override
	protected Class<?> getModelClass() {
		return Layout.class;
	}

	@Override
	protected Map<String, String> getSelectedItemData(long classPK) {
		return TreeMapBuilder.<String, String>create(
			String.CASE_INSENSITIVE_ORDER
		).put(
			"plid", String.valueOf(classPK)
		).build();
	}

	@Override
	protected void updateDraft(LaunchEntryManager.Draft draft, Values values)
		throws Exception {

		_updateLayout(draft.getDraftId(), values);
	}

	@Override
	protected void updateLiveEntry(long classPK, Values values)
		throws Exception {

		_updateLayout(classPK, values);
	}

	protected static final class Values {

		public Values(
			String masterLayoutPageTemplateEntryERC, String styleBookEntryERC) {

			_masterLayoutPageTemplateEntryERC =
				masterLayoutPageTemplateEntryERC;
			_styleBookEntryERC = styleBookEntryERC;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof Values)) {
				return false;
			}

			Values values = (Values)object;

			if (Objects.equals(
					_masterLayoutPageTemplateEntryERC,
					values._masterLayoutPageTemplateEntryERC) &&
				Objects.equals(_styleBookEntryERC, values._styleBookEntryERC)) {

				return true;
			}

			return false;
		}

		public String getMasterLayoutPageTemplateEntryERC() {
			return _masterLayoutPageTemplateEntryERC;
		}

		public String getStyleBookEntryERC() {
			return _styleBookEntryERC;
		}

		@Override
		public int hashCode() {
			return Objects.hash(
				_masterLayoutPageTemplateEntryERC, _styleBookEntryERC);
		}

		private final String _masterLayoutPageTemplateEntryERC;
		private final String _styleBookEntryERC;

	}

	private Values _getValues(Layout layout) {
		return new Values(
			layout.getMasterLayoutPageTemplateEntryERC(),
			layout.getStyleBookEntryERC());
	}

	private void _testAddDraftCopiesLayoutContent() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentPublishedLayout(
			group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		Values publishedLayoutValue = createValue();

		_updateLayout(layout.getPlid(), publishedLayoutValue);

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		List<LaunchEntryManager.Version> versions =
			launchEntryManager.getVersions(layout.getPlid());

		LaunchEntryManager.Version publishedVersion = versions.get(0);

		LaunchEntryManager.Draft draft = launchEntryManager.addDraft(
			layout.getPlid(), RandomTestUtil.randomLong(),
			publishedVersion.getVersionKey());

		Assert.assertEquals(publishedLayoutValue, getDraftValue(draft));

		Assert.assertEquals(versions.toString(), 1, versions.size());

		Layout draftLayout = layout.fetchDraftLayout();

		Values mainDraftLayoutValue = createValue();

		_updateLayout(draftLayout.getPlid(), mainDraftLayoutValue);

		versions = launchEntryManager.getVersions(layout.getPlid());

		Set<String> versionKeys = new HashSet<>();

		for (LaunchEntryManager.Version version : versions) {
			String versionKey = version.getVersionKey();

			Assert.assertTrue(versionKeys.add(versionKey));

			Values expectedValue = publishedLayoutValue;

			if (!Objects.equals(versionKey, publishedVersion.getVersionKey())) {
				expectedValue = mainDraftLayoutValue;
			}

			LaunchEntryManager.Draft currentDraft = launchEntryManager.addDraft(
				layout.getPlid(), RandomTestUtil.randomLong(), versionKey);

			Assert.assertEquals(expectedValue, getDraftValue(currentDraft));

			Assert.assertNotEquals(
				draft.getDraftId(), currentDraft.getDraftId());
		}

		Assert.assertEquals(versions.toString(), 2, versions.size());
	}

	private void _testAddDraftWithExistingLaunchDraft() throws Exception {
		long classPK = addEntry();

		Values entryValue = getEntryValue(classPK);

		long launchSetId = RandomTestUtil.randomLong();

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Draft draft1 = launchEntryManager.addDraft(
			classPK, launchSetId, String.valueOf(classPK));

		Assert.assertEquals(entryValue, getDraftValue(draft1));

		Values values = createValue();

		updateDraft(draft1, values);

		Assert.assertEquals(values, getDraftValue(draft1));

		LaunchEntryManager.Draft draft2 = launchEntryManager.addDraft(
			classPK, launchSetId, String.valueOf(classPK));

		Assert.assertEquals(draft1.getDraftId(), draft2.getDraftId());
		Assert.assertEquals(entryValue, getDraftValue(draft2));

		Layout launchDraftLayout = _layoutLocalService.getLayout(
			draft2.getDraftId());

		Assert.assertEquals(launchSetId, launchDraftLayout.getLaunchSetId());

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		Assert.assertEquals(0, draftLayout.getLaunchSetId());
	}

	private void _testFetchDraftLayoutByLaunchSetId() throws Exception {
		long classPK = addEntry();

		Layout mainDraftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		long launchSetId = RandomTestUtil.randomLong();

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Draft launchDraft = launchEntryManager.addDraft(
			classPK, launchSetId, String.valueOf(classPK));

		Layout launchDraftLayout = _layoutLocalService.fetchDraftLayout(
			classPK, launchSetId);

		Assert.assertEquals(
			launchDraft.getDraftId(), launchDraftLayout.getPlid());
		Assert.assertEquals(launchSetId, launchDraftLayout.getLaunchSetId());

		Assert.assertEquals(
			mainDraftLayout, _layoutLocalService.fetchDraftLayout(classPK, 0));
	}

	private void _testGetPreviewBaseModelForPublishedVersionReturnsLive()
		throws Exception {

		long classPK = addEntry();

		Values value1 = createValue();

		LaunchEntryManager.Draft draft1 = addDraft(classPK);

		updateDraft(draft1, value1);

		Values value2 = createValue();

		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		updateDraft(draft2, value2);

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion1 =
			launchEntryManager.publishDraft(classPK, draft1.getDraftId());

		launchEntryManager.publishDraft(classPK, draft2.getDraftId());

		LaunchEntryManager.Version version1 = getVersion(
			classPK, publishedVersion1.getVersionKey());

		try (SafeCloseable safeCloseable = setPreviewIdWithSafeCloseable(
				classPK, version1::getPreviewBaseModel)) {

			Assert.assertEquals(value2, getEntryValue(classPK));
		}
	}

	private void _testGetPreviewBaseModelPreservesPublishedFriendlyURL()
		throws Exception {

		long classPK = addEntry();

		LaunchEntryManager.Draft draft = addDraft(classPK);

		Layout previewBaseModel = (Layout)draft.getPreviewBaseModel();

		Layout publishedLayout = _layoutLocalService.getLayout(classPK);

		Assert.assertEquals(draft.getDraftId(), previewBaseModel.getPlid());
		Assert.assertEquals(
			publishedLayout.getFriendlyURL(),
			previewBaseModel.getFriendlyURL());
		Assert.assertEquals(
			publishedLayout.getFriendlyURL(LocaleUtil.getSiteDefault()),
			previewBaseModel.getFriendlyURL(LocaleUtil.getSiteDefault()));
		Assert.assertEquals(
			publishedLayout.getFriendlyURLMap(),
			previewBaseModel.getFriendlyURLMap());
	}

	private void _testGetVersionsWithExistingMainDraft() throws Exception {
		long classPK = addEntry();

		Layout mainDraft = _layoutLocalService.fetchDraftLayout(classPK);

		_updateLayout(mainDraft.getPlid(), createValue());

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		List<String> versionKeys = TransformUtil.transform(
			launchEntryManager.getVersions(classPK),
			LaunchEntryManager.Version::getVersionKey);

		Assert.assertTrue(versionKeys.contains(String.valueOf(classPK)));
		Assert.assertTrue(
			versionKeys.contains(String.valueOf(mainDraft.getPlid())));
		Assert.assertEquals(versionKeys.toString(), 2, versionKeys.size());
	}

	private void _testPublishDraftReturnsStableVersionKey() throws Exception {
		long classPK = addEntry();

		LaunchEntryManager.Draft draft1 = addDraft(classPK);

		updateDraft(draft1, createValue());

		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		updateDraft(draft2, createValue());

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion2 =
			launchEntryManager.publishDraft(classPK, draft2.getDraftId());

		LaunchEntryManager.Version publishedVersion1 =
			launchEntryManager.publishDraft(classPK, draft1.getDraftId());

		Assert.assertEquals(
			publishedVersion2.getVersionKey(),
			publishedVersion1.getVersionKey());
	}

	private void _testPublishDraftWithExistingMainDraft() throws Exception {
		long classPK = addEntry();

		Values values = createValue();

		LaunchEntryManager.Draft draft = addDraft(classPK);

		updateDraft(draft, values);

		Layout mainDraft = _layoutLocalService.fetchDraftLayout(classPK);

		_updateLayout(mainDraft.getPlid(), createValue());

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		launchEntryManager.publishDraft(classPK, draft.getDraftId());

		mainDraft = _layoutLocalService.getLayout(mainDraft.getPlid());

		Assert.assertEquals(values, _getValues(mainDraft));
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, mainDraft.getStatus());
	}

	private void _updateLayout(long plid, Values values) throws Exception {
		Layout layout = _layoutLocalService.getLayout(plid);

		layout.setStyleBookEntryERC(values.getStyleBookEntryERC());
		layout.setMasterLayoutPageTemplateEntryERC(
			values.getMasterLayoutPageTemplateEntryERC());

		if (layout.isDraftLayout()) {
			layout.setStatus(WorkflowConstants.STATUS_DRAFT);
		}

		_layoutLocalService.updateLayout(layout);
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

}