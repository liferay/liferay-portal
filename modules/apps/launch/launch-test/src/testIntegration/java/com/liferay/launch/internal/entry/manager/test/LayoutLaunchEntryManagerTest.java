/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

		_testGetPreviewBaseModelPreservesPublishedFriendlyURL();
	}

	@Override
	protected long addEntry() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentPublishedLayout(
			group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		return layout.getPlid();
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
	protected Values randomValue() {
		return new Values(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Override
	protected void updateDraft(LaunchEntryManager.Draft draft, Values value)
		throws Exception {

		_updateLayout(draft.getDraftId(), value);
	}

	@Override
	protected void updateLiveEntry(long classPK, Values value)
		throws Exception {

		_updateLayout(classPK, value);
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

	private void _testAddDraftWithExistingLaunchDraft() throws Exception {
		long classPK = addEntry();

		Values entryValue = getEntryValue(classPK);

		long launchSetId = RandomTestUtil.randomLong();

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Draft draft1 = launchEntryManager.addDraft(
			classPK, launchSetId, String.valueOf(classPK));

		Assert.assertEquals(entryValue, getDraftValue(draft1));

		Values value = randomValue();

		updateDraft(draft1, value);

		Assert.assertEquals(value, getDraftValue(draft1));

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

	private void _testGetPreviewBaseModelPreservesPublishedFriendlyURL()
		throws Exception {

		long classPK = addEntry();

		Layout publishedLayout = _layoutLocalService.getLayout(classPK);

		LaunchEntryManager.Draft draft = addDraft(classPK);

		Layout previewBaseModel = (Layout)draft.getPreviewBaseModel();

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

	private void _updateLayout(long plid, Values value) throws Exception {
		Layout layout = _layoutLocalService.getLayout(plid);

		layout.setStyleBookEntryERC(value.getStyleBookEntryERC());
		layout.setMasterLayoutPageTemplateEntryERC(
			value.getMasterLayoutPageTemplateEntryERC());

		_layoutLocalService.updateLayout(layout);
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

}