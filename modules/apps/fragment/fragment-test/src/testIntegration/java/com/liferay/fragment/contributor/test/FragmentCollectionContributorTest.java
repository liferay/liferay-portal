/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class FragmentCollectionContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_originalServiceContext = ServiceContextThreadLocal.getServiceContext();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(_originalServiceContext);
	}

	@Test
	public void testPropagateContributedFragmentEntry() throws Exception {
		String fragmentCollectionContributorKey = RandomTestUtil.randomString();

		String fragmentEntryKey = StringBundler.concat(
			fragmentCollectionContributorKey, StringPool.DASH,
			RandomTestUtil.randomString());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				segmentsExperienceId, layout.getPlid(), StringPool.BLANK,
				"<div data-lfr-editable-id=\"editable-1\" " +
					"data-lfr-editable-type=\"rich-text\">EDITABLE 1</div>",
				StringPool.BLANK, StringPool.BLANK, null, StringPool.BLANK, 0,
				fragmentEntryKey, FragmentConstants.TYPE_COMPONENT,
				_serviceContext);

		String modifiedHtml = StringBundler.concat(
			"<div data-lfr-editable-id=\"editable-1\" ",
			"data-lfr-editable-type=\"rich-text\">EDITABLE 1</div>",
			"<div data-lfr-editable-id=\"editable-2\" ",
			"data-lfr-editable-type=\"rich-text\">EDITABLE 2</div>");

		ServiceRegistration<?> serviceRegistration = _getServiceRegistration(
			new TestFragmentCollectionContributor(
				fragmentCollectionContributorKey,
				HashMapBuilder.put(
					FragmentConstants.TYPE_COMPONENT,
					_getFragmentEntry(
						fragmentEntryKey, modifiedHtml,
						FragmentConstants.TYPE_COMPONENT)
				).build()));

		try {
			FragmentEntryLink persistedFragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId());

			Assert.assertEquals(
				modifiedHtml, persistedFragmentEntryLink.getHtml());

			JSONObject jsonObject =
				persistedFragmentEntryLink.getEditableValuesJSONObject();

			JSONObject editableFragmentEntryProcessorJSONObject =
				jsonObject.getJSONObject(
					"com.liferay.fragment.entry.processor.editable." +
						"EditableFragmentEntryProcessor");

			Assert.assertEquals(
				editableFragmentEntryProcessorJSONObject.toString(), 2,
				editableFragmentEntryProcessorJSONObject.length());
			Assert.assertTrue(
				editableFragmentEntryProcessorJSONObject.has("editable-1"));

			JSONObject editable1JSONObject =
				editableFragmentEntryProcessorJSONObject.getJSONObject(
					"editable-1");

			Assert.assertEquals(
				"EDITABLE 1", editable1JSONObject.get("defaultValue"));

			Assert.assertTrue(
				editableFragmentEntryProcessorJSONObject.has("editable-2"));

			JSONObject editable2JSONObject =
				editableFragmentEntryProcessorJSONObject.getJSONObject(
					"editable-2");

			Assert.assertEquals(
				"EDITABLE 2", editable2JSONObject.get("defaultValue"));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testRegisterContributedFragmentEntries() {
		String fragmentCollectionContributorKey = RandomTestUtil.randomString();

		TestFragmentCollectionContributor testFragmentCollectionContributor =
			new TestFragmentCollectionContributor(
				fragmentCollectionContributorKey,
				HashMapBuilder.put(
					-1,
					_getFragmentEntry(
						"test-unsupported-fragment-entry",
						RandomTestUtil.randomString(), -1)
				).put(
					FragmentConstants.TYPE_COMPONENT,
					_getFragmentEntry(
						"test-component-fragment-entry",
						RandomTestUtil.randomString(),
						FragmentConstants.TYPE_COMPONENT)
				).put(
					FragmentConstants.TYPE_INPUT,
					_getFragmentEntry(
						"test-input-fragment-entry",
						RandomTestUtil.randomString(),
						FragmentConstants.TYPE_INPUT)
				).put(
					FragmentConstants.TYPE_SECTION,
					_getFragmentEntry(
						"test-section-fragment-entry",
						RandomTestUtil.randomString(),
						FragmentConstants.TYPE_SECTION)
				).build());

		ServiceRegistration<?> serviceRegistration = _getServiceRegistration(
			testFragmentCollectionContributor);

		try {
			Map<String, FragmentEntry> fragmentEntries =
				_fragmentCollectionContributorRegistry.getFragmentEntries();

			Assert.assertNotNull(
				fragmentEntries.get("test-component-fragment-entry"));
			Assert.assertNotNull(
				fragmentEntries.get("test-input-fragment-entry"));
			Assert.assertNotNull(
				fragmentEntries.get("test-section-fragment-entry"));
			Assert.assertNull(
				fragmentEntries.get("test-unsupported-fragment-entry"));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testRegisterFragmentCollectionContributor() {
		String fragmentCollectionContributorKey = RandomTestUtil.randomString();

		ServiceRegistration<?> serviceRegistration = _getServiceRegistration(
			new TestFragmentCollectionContributor(
				fragmentCollectionContributorKey, new HashMap<>()));

		try {
			Assert.assertNotNull(
				_fragmentCollectionContributorRegistry.
					getFragmentCollectionContributor(
						fragmentCollectionContributorKey));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private FragmentEntry _getFragmentEntry(String key, String html, int type) {
		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.createFragmentEntry(0L);

		fragmentEntry.setFragmentEntryKey(key);
		fragmentEntry.setName(RandomTestUtil.randomString());
		fragmentEntry.setCss(null);
		fragmentEntry.setHtml(html);
		fragmentEntry.setJs(null);
		fragmentEntry.setConfiguration(null);
		fragmentEntry.setType(type);

		return fragmentEntry;
	}

	private ServiceRegistration<?> _getServiceRegistration(
		TestFragmentCollectionContributor testFragmentCollectionContributor) {

		Bundle bundle = FrameworkUtil.getBundle(
			FragmentCollectionContributorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			FragmentCollectionContributor.class,
			testFragmentCollectionContributor,
			MapUtil.singletonDictionary(
				"fragment.collection.key",
				testFragmentCollectionContributor.getFragmentCollectionKey()));
	}

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _originalServiceContext;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}