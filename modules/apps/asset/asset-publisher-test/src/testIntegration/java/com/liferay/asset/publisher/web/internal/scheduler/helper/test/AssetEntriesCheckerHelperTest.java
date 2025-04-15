/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.scheduler.helper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.test.util.AssetPublisherTestUtil;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.util.AssetHelper;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import jakarta.portlet.PortletPreferences;

import java.lang.reflect.Constructor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author István András Dézsi
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class AssetEntriesCheckerHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER);

		_setUpAssetEntriesCheckerHelper();
	}

	@Test
	public void testGetAssetEntriesFromAssetListSelectionAssetPublisher()
		throws Exception {

		AssetEntry assetEntry1 = _addAssetEntry();
		AssetEntry assetEntry2 = _addAssetEntry();

		_setAssetListSelectionStylePreference(assetEntry1, assetEntry2);

		_assertAssetEntries(
			Arrays.asList(assetEntry1, assetEntry2),
			ReflectionTestUtil.invoke(
				_assetEntriesCheckerHelper, "_getAssetEntries",
				new Class<?>[] {PortletPreferences.class, Layout.class},
				LayoutTestUtil.getPortletPreferences(_layout, _portletId),
				_layout));
	}

	@Test
	public void testGetAssetEntriesFromDynamicSelectionAssetPublisher()
		throws Exception {

		_setDynamicSelectionStylePreference();

		_assertAssetEntries(
			Arrays.asList(_addAssetEntry(), _addAssetEntry(), _addAssetEntry()),
			ReflectionTestUtil.invoke(
				_assetEntriesCheckerHelper, "_getAssetEntries",
				new Class<?>[] {PortletPreferences.class, Layout.class},
				LayoutTestUtil.getPortletPreferences(_layout, _portletId),
				_layout));
	}

	@Test
	public void testGetAssetEntriesFromDynamicSelectionAssetPublisherWithoutDynamicSubscriptionLimit()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.asset.publisher.web.internal.configuration." +
						"AssetPublisherWebConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"dynamicSubscriptionLimit", 0
					).build())) {

			_setDynamicSelectionStylePreference();

			_assertAssetEntries(
				Arrays.asList(
					_addAssetEntry(), _addAssetEntry(), _addAssetEntry()),
				ReflectionTestUtil.invoke(
					_assetEntriesCheckerHelper, "_getAssetEntries",
					new Class<?>[] {PortletPreferences.class, Layout.class},
					LayoutTestUtil.getPortletPreferences(_layout, _portletId),
					_layout));
		}
	}

	@Test
	public void testGetAssetEntriesFromManualSelectionAssetPublisher()
		throws Exception {

		AssetEntry assetEntry1 = _addAssetEntry();
		AssetEntry assetEntry2 = _addAssetEntry();
		AssetEntry assetEntry3 = _addAssetEntry();
		AssetEntry assetEntry4 = _addAssetEntry();

		_setPortletManualSelectionStylePreference(
			assetEntry1, assetEntry2, assetEntry3, assetEntry4);

		_assertAssetEntries(
			Arrays.asList(assetEntry1, assetEntry2, assetEntry3, assetEntry4),
			ReflectionTestUtil.invoke(
				_assetEntriesCheckerHelper, "_getAssetEntries",
				new Class<?>[] {PortletPreferences.class, Layout.class},
				LayoutTestUtil.getPortletPreferences(_layout, _portletId),
				_layout));
	}

	private AssetEntry _addAssetEntry() throws Exception {
		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, RandomTestUtil.randomString(),
			1, 1, 1965, 0, 0, true, true, null, StringPool.BLANK, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return _assetEntryLocalService.getEntry(
			_group.getGroupId(), blogsEntry.getUuid());
	}

	private void _assertAssetEntries(
		List<AssetEntry> expectedAssetEntries,
		List<AssetEntry> actualAssetEntries) {

		Assert.assertEquals(
			actualAssetEntries.toString(), expectedAssetEntries.size(),
			actualAssetEntries.size());

		Iterator<AssetEntry> expectedAssetEntriesIterator =
			expectedAssetEntries.iterator();

		while (expectedAssetEntriesIterator.hasNext()) {
			AssetEntry expectedAssetEntry = expectedAssetEntriesIterator.next();

			Assert.assertTrue(
				ListUtil.exists(
					actualAssetEntries,
					actualAssetEntry ->
						Objects.equals(
							expectedAssetEntry.getClassName(),
							actualAssetEntry.getClassName()) &&
						Objects.equals(
							expectedAssetEntry.getClassUuid(),
							actualAssetEntry.getClassUuid())));
		}
	}

	private void _setAssetListSelectionStylePreference(
			AssetEntry... assetEntries)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		for (AssetEntry assetEntry : assetEntries) {
			_assetListEntryLocalService.addAssetEntrySelection(
				assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
				0, serviceContext);
		}

		portletPreferences.setValue(
			"assetListEntryExternalReferenceCode",
			assetListEntry.getExternalReferenceCode());
		portletPreferences.setValue("selectionStyle", "asset-list");

		portletPreferences.store();
	}

	private void _setDynamicSelectionStylePreference() throws Exception {
		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		portletPreferences.setValue("selectionStyle", "dynamic");

		portletPreferences.store();
	}

	private void _setPortletManualSelectionStylePreference(
			AssetEntry... assetEntries)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		portletPreferences.setValue("selectionStyle", "manual");

		String[] assetEntryXmls = portletPreferences.getValues(
			"assetEntryXml", new String[0]);

		for (AssetEntry assetEntry : assetEntries) {
			String assetEntryXml = AssetPublisherTestUtil.getAssetEntryXml(
				assetEntry);

			if (!ArrayUtil.contains(assetEntryXmls, assetEntryXml)) {
				assetEntryXmls = ArrayUtil.append(
					assetEntryXmls, assetEntryXml);
			}
		}

		portletPreferences.setValues("assetEntryXml", assetEntryXmls);

		portletPreferences.store();
	}

	private void _setUpAssetEntriesCheckerHelper() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			AssetEntriesCheckerHelperTest.class);

		Bundle assetPublisherWebBundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.asset.publisher.web");

		Assert.assertNotNull(
			"Unable to find asset-publisher-web bundle",
			assetPublisherWebBundle);

		Class<?> clazz = assetPublisherWebBundle.loadClass(
			"com.liferay.asset.publisher.web.internal.scheduler.helper." +
				"AssetEntriesCheckerHelper");

		Constructor<?> constructor = clazz.getConstructor();

		_assetEntriesCheckerHelper = constructor.newInstance();

		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper, "_assetEntryLocalService",
			_assetEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper, "_assetHelper", _assetHelper);
		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper, "_assetListAssetEntryProvider",
			_assetListAssetEntryProvider);
		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper,
			"_assetListEntrySegmentsEntryRelLocalService",
			_assetListEntrySegmentsEntryRelLocalService);
		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper, "_assetPublisherHelper",
			_assetPublisherHelper);
		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper, "_configurationProvider",
			_configurationProvider);
		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper, "_groupLocalService",
			_groupLocalService);
		ReflectionTestUtil.setFieldValue(
			_assetEntriesCheckerHelper, "_segmentsConfigurationProvider",
			_segmentsConfigurationProvider);
	}

	private Object _assetEntriesCheckerHelper;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetHelper _assetHelper;

	@Inject
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private AssetPublisherHelper _assetPublisherHelper;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;
	private String _portletId;

	@Inject
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

}