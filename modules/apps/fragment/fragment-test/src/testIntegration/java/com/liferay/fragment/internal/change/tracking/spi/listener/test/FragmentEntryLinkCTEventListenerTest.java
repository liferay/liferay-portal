/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.change.tracking.spi.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionServiceUtil;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkCTEventListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, StringUtil.randomString(), null);

		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testOnAfterPublish() throws Exception {
		FragmentEntryLink fragmentEntryLink = null;

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-html");

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					null, fragmentEntry.getExternalReferenceCode(),
					fragmentEntry.getScopeERC(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(_layout.getPlid()),
					_layout.getPlid(), fragmentEntry.getCss(),
					fragmentEntry.getHtml(), fragmentEntry.getJs(),
					fragmentEntry.getConfiguration(),
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put(
							"element-html",
							JSONUtil.put(
								"defaultValue",
								StringBundler.concat(
									"<div class=\"fragment-html-test\">",
									"previewCTCollectionId=",
									_ctCollection.getCtCollectionId(),
									"</div>")))
					).toString(),
					StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
					fragmentEntry.getType(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));
		}

		CTCollectionServiceUtil.publishCTCollection(
			TestPropsValues.getUserId(), _ctCollection.getCtCollectionId());

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		String editableValues = fragmentEntryLink.getEditableValues();

		Assert.assertFalse(editableValues.contains("previewCTCollectionId="));
	}

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}