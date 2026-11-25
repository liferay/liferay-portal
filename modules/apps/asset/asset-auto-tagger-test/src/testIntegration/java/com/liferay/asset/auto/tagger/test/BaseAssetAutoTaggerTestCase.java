/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.test;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseAssetAutoTaggerTestCase {

	public static final String ASSET_TAG_NAME_AUTO = "auto tag";

	public static final String ASSET_TAG_NAME_MANUAL = "manual tag";

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		group = GroupTestUtil.addGroup();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_assetAutoTagProviderServiceRegistration =
			bundleContext.registerService(
				AssetAutoTagProvider.class,
				model -> Arrays.asList(ASSET_TAG_NAME_AUTO),
				MapUtil.singletonDictionary(
					"model.class.name", DLFileEntryConstants.getClassName()));
	}

	@After
	public void tearDown() {
		_assetAutoTagProviderServiceRegistration.unregister();
	}

	protected FileEntry addFileEntry(ServiceContext serviceContext)
		throws PortalException {

		return DLAppServiceUtil.addFileEntry(
			null, group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), new byte[0],
			null, null, null, serviceContext);
	}

	protected AssetEntry addFileEntryAssetEntry(
			long groupId, ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), new byte[0],
			null, null, null, serviceContext);

		return AssetEntryLocalServiceUtil.getEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());
	}

	protected AssetEntry addFileEntryAssetEntry(ServiceContext serviceContext)
		throws PortalException {

		return addFileEntryAssetEntry(group.getGroupId(), serviceContext);
	}

	protected void applyAssetTagName(AssetEntry assetEntry, String assetTagName)
		throws PortalException {

		AssetEntryLocalServiceUtil.updateEntry(
			assetEntry.getUserId(), assetEntry.getGroupId(),
			assetEntry.getClassName(), assetEntry.getClassPK(),
			assetEntry.getCategoryIds(),
			ArrayUtil.append(assetEntry.getTagNames(), assetTagName));
	}

	protected void assertContainsAssetTagName(
		AssetEntry assetEntry, String assetTagName) {

		for (AssetTag assetTag : assetEntry.getTags()) {
			if (StringUtil.equals(assetTag.getName(), assetTagName)) {
				return;
			}
		}

		throw new AssertionError(
			String.format(
				"The asset entry was not tagged with \"%s\"", assetTagName));
	}

	protected void assertDoesNotContainAssetTagName(
		AssetEntry assetEntry, String assetTagName) {

		for (AssetTag assetTag : assetEntry.getTags()) {
			if (StringUtil.equals(assetTag.getName(), assetTagName)) {
				throw new AssertionError(
					String.format(
						"The asset entry was tagged with \"%s\"",
						assetTagName));
			}
		}
	}

	protected void assertHasNoTags(AssetEntry assetEntry) {
		List<AssetTag> assetTag = assetEntry.getTags();

		Assert.assertEquals(assetTag.toString(), 0, assetTag.size());
	}

	protected AssetEntry updateFileEntryAssetEntry(
			FileEntry fileEntry, ServiceContext serviceContext)
		throws PortalException {

		DLAppServiceUtil.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(), null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			DLVersionNumberIncrease.AUTOMATIC, new byte[0], null, null, null,
			serviceContext);

		return AssetEntryLocalServiceUtil.getEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());
	}

	protected void withAutoTaggerDisabled(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		_withAutoTagger(false, unsafeRunnable);
	}

	protected void withAutoTaggerEnabled(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		_withAutoTagger(true, unsafeRunnable);
	}

	@DeleteAfterTestRun
	protected Group group;

	private void _withAutoTagger(
			boolean enabled, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", enabled
			).build();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.asset.auto.tagger.internal.configuration." +
						"AssetAutoTaggerSystemConfiguration",
					dictionary)) {

			unsafeRunnable.run();
		}
	}

	private ServiceRegistration<?> _assetAutoTagProviderServiceRegistration;

}