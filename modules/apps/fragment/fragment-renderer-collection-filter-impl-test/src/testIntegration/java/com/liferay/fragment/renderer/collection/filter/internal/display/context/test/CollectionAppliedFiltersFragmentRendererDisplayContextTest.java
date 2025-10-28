/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.collection.filter.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.collection.filter.constants.FragmentCollectionFilterConstants;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class CollectionAppliedFiltersFragmentRendererDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testGetAppliedFiltersWithXSS() throws Exception {
		FragmentEntry fragmentEntry = _getFragmentEntry();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				fragmentEntry.getExternalReferenceCode(),
				fragmentEntry.getScopeERC(), _defaultSegmentsExperienceId,
				_layout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"targetCollections",
						new String[] {RandomTestUtil.randomString()})
				).toString(),
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		FragmentRendererContext fragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String script = "<script>alert(document.location)</script>";

		mockHttpServletRequest.addParameter(
			FragmentCollectionFilterConstants.FILTER_PREFIX + "keywords_" +
				fragmentEntryLink.getFragmentEntryLinkId(),
			script);

		_collectionAppliedFiltersFragmentRenderer.render(
			fragmentRendererContext, mockHttpServletRequest,
			new MockHttpServletResponse());

		Object
			collectionAppliedFiltersFragmentRendererDisplayContextDisplayContext =
				mockHttpServletRequest.getAttribute(
					"com.liferay.fragment.renderer.collection.filter." +
						"internal.display.context.CollectionAppliedFilters" +
							"FragmentRendererDisplayContext");

		List<Map<String, String>> appliedFilters = ReflectionTestUtil.invoke(
			collectionAppliedFiltersFragmentRendererDisplayContextDisplayContext,
			"getAppliedFilters", new Class<?>[0]);

		Assert.assertEquals(
			appliedFilters.toString(), 1, appliedFilters.size());

		for (Map<String, String> appliedFilter : appliedFilters) {
			Assert.assertEquals(
				HtmlUtil.escape(script), appliedFilter.get("filterLabel"));
		}
	}

	private FragmentEntry _getFragmentEntry() throws Exception {
		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK,
			"Fragment Entry HTML", StringPool.BLANK, false, null, null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	@Inject(
		filter = "component.name=com.liferay.fragment.renderer.collection.filter.internal.CollectionAppliedFiltersFragmentRenderer",
		type = FragmentRenderer.class
	)
	private FragmentRenderer _collectionAppliedFiltersFragmentRenderer;

	private long _defaultSegmentsExperienceId;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}