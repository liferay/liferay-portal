/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.osgi.commands.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.service.AssetAutoTaggerEntryLocalService;
import com.liferay.asset.auto.tagger.test.BaseAssetAutoTaggerTestCase;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
@Sync
public class AssetAutoTaggerOSGiCommandsTest
	extends BaseAssetAutoTaggerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testCommitAutoTagsRemovesAllTheAutoTaggerEntries()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0);

		List<AssetEntry> assetEntries = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			assetEntries.add(addFileEntryAssetEntry(serviceContext));
		}

		_tagAllUntagged(DLFileEntryConstants.getClassName());

		for (AssetEntry assetEntry : assetEntries) {
			assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);

			List<AssetAutoTaggerEntry> assetAutoTaggerEntries =
				_assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
					assetEntry);

			Assert.assertEquals(
				assetAutoTaggerEntries.toString(), 1,
				assetAutoTaggerEntries.size());
		}

		_commitAutoTags(DLFileEntryConstants.getClassName());

		for (AssetEntry assetEntry : assetEntries) {
			List<AssetAutoTaggerEntry> assetAutoTaggerEntries =
				_assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
					assetEntry);

			Assert.assertEquals(
				assetAutoTaggerEntries.toString(), 0,
				assetAutoTaggerEntries.size());

			assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);
		}
	}

	@Test
	public void testTagAllUntaggedTagsAllTheAssetsThatHaveNoTags()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0);

		withAutoTaggerDisabled(
			() -> {
				AssetEntry assetEntryWithPreviousTags = addFileEntryAssetEntry(
					serviceContext);

				applyAssetTagName(
					assetEntryWithPreviousTags, ASSET_TAG_NAME_MANUAL);

				assertContainsAssetTagName(
					assetEntryWithPreviousTags, ASSET_TAG_NAME_MANUAL);

				AssetEntry assetEntryWithNoPreviousTags =
					addFileEntryAssetEntry(serviceContext);

				assertHasNoTags(assetEntryWithNoPreviousTags);

				withAutoTaggerEnabled(
					() -> {
						_tagAllUntagged(DLFileEntryConstants.getClassName());

						assertContainsAssetTagName(
							assetEntryWithNoPreviousTags, ASSET_TAG_NAME_AUTO);

						assertDoesNotContainAssetTagName(
							assetEntryWithPreviousTags, ASSET_TAG_NAME_AUTO);
					});
			});
	}

	@Test
	public void testTagAllUntaggedTagsAllTheAssetsThatHaveNoTagsWithAnAssetEntryAutoTagProvider()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			AssetAutoTaggerOSGiCommandsTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> assetAutoTagProviderServiceRegistration =
			bundleContext.registerService(
				AssetAutoTagProvider.class,
				model -> Arrays.asList(ASSET_TAG_NAME_AUTO),
				MapUtil.singletonDictionary(
					"model.class.name", AssetEntry.class.getName()));

		String className = RandomTestUtil.randomString();

		ServiceRegistration<?> assetRendererFactoryServiceRegistration =
			bundleContext.registerService(
				AssetRendererFactory.class,
				new TestAssetRendererFactory(className), null);

		try {
			withAutoTaggerDisabled(
				() -> {
					AssetEntry assetEntryWithPreviousTags =
						_assetEntryLocalService.updateEntry(
							TestPropsValues.getUserId(), group.getGroupId(),
							className, RandomTestUtil.randomLong(), new long[0],
							new String[0]);

					applyAssetTagName(
						assetEntryWithPreviousTags, ASSET_TAG_NAME_MANUAL);

					assertContainsAssetTagName(
						assetEntryWithPreviousTags, ASSET_TAG_NAME_MANUAL);

					AssetEntry assetEntryWithNoPreviousTags =
						_assetEntryLocalService.updateEntry(
							TestPropsValues.getUserId(), group.getGroupId(),
							className, RandomTestUtil.randomLong(), new long[0],
							new String[0]);

					assertHasNoTags(assetEntryWithNoPreviousTags);

					withAutoTaggerEnabled(
						() -> {
							_tagAllUntagged(
								DLFileEntryConstants.getClassName());

							assertContainsAssetTagName(
								assetEntryWithNoPreviousTags,
								ASSET_TAG_NAME_AUTO);

							assertDoesNotContainAssetTagName(
								assetEntryWithPreviousTags,
								ASSET_TAG_NAME_AUTO);
						});
				});
		}
		finally {
			assetAutoTagProviderServiceRegistration.unregister();
			assetRendererFactoryServiceRegistration.unregister();
		}
	}

	@Test
	public void testUnTagAllRemovesAllTheAutoTags() throws Exception {
		AssetEntry assetEntry = addFileEntryAssetEntry(
			ServiceContextTestUtil.getServiceContext(group.getGroupId(), 0));

		applyAssetTagName(assetEntry, ASSET_TAG_NAME_MANUAL);

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_MANUAL);

		_untagAll(DLFileEntryConstants.getClassName());

		assertDoesNotContainAssetTagName(assetEntry, ASSET_TAG_NAME_AUTO);

		assertContainsAssetTagName(assetEntry, ASSET_TAG_NAME_MANUAL);
	}

	private void _commitAutoTags(String... classNames) throws Exception {
		Class<?> clazz = _assetAutoTaggerOSGiCommands.getClass();

		Method method = clazz.getMethod(
			"commitAutoTags", String.class, String[].class);

		method.invoke(
			_assetAutoTaggerOSGiCommands, String.valueOf(group.getCompanyId()),
			classNames);
	}

	private void _tagAllUntagged(String... classNames) throws Exception {
		Class<?> clazz = _assetAutoTaggerOSGiCommands.getClass();

		Method method = clazz.getMethod(
			"tagAllUntagged", String.class, String[].class);

		method.invoke(
			_assetAutoTaggerOSGiCommands, String.valueOf(group.getCompanyId()),
			classNames);
	}

	private void _untagAll(String... classNames) throws Exception {
		Class<?> clazz = _assetAutoTaggerOSGiCommands.getClass();

		Method method = clazz.getMethod(
			"untagAll", String.class, String[].class);

		method.invoke(
			_assetAutoTaggerOSGiCommands, String.valueOf(group.getCompanyId()),
			classNames);
	}

	@Inject(
		filter = "osgi.command.scope=assetAutoTagger",
		type = Inject.NoType.class
	)
	private static Object _assetAutoTaggerOSGiCommands;

	@Inject
	private AssetAutoTaggerEntryLocalService _assetAutoTaggerEntryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	private class TestAssetRendererFactory
		extends BaseAssetRendererFactory<Object> {

		public TestAssetRendererFactory(String className) {
			_className = className;
		}

		@Override
		public AssetRenderer<Object> getAssetRenderer(long classPK, int type) {
			return new BaseAssetRenderer() {

				@Override
				public Object getAssetObject() {
					return null;
				}

				@Override
				public String getClassName() {
					return _className;
				}

				@Override
				public long getClassPK() {
					return RandomTestUtil.randomLong();
				}

				@Override
				public long getGroupId() {
					return group.getGroupId();
				}

				@Override
				public String getSummary(
					PortletRequest portletRequest,
					PortletResponse portletResponse) {

					return null;
				}

				@Override
				public String getTitle(Locale locale) {
					return null;
				}

				@Override
				public long getUserId() {
					return RandomTestUtil.randomLong();
				}

				@Override
				public String getUserName() {
					return null;
				}

				@Override
				public String getUuid() {
					return null;
				}

				@Override
				public boolean include(
					HttpServletRequest httpServletRequest,
					HttpServletResponse httpServletResponse, String template) {

					return false;
				}

			};
		}

		@Override
		public String getClassName() {
			return _className;
		}

		@Override
		public String getType() {
			return "test";
		}

		private final String _className;

	}

}