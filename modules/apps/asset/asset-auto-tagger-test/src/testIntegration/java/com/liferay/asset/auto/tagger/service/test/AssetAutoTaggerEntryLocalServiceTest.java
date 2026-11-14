/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.service.AssetAutoTaggerEntryLocalService;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AssetAutoTaggerEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testAddAssetAutoTaggerEntries() throws PortalException {
		AssetEntry assetEntry = _addAssetEntry();
		String assetTagName1 = RandomTestUtil.randomString();
		String assetTagName2 = RandomTestUtil.randomString() + StringPool.STAR;
		String assetTagName3 = RandomTestUtil.randomString();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.asset.auto.tagger.service.impl." +
					"AssetAutoTaggerEntryLocalServiceImpl",
				LoggerTestUtil.WARN)) {

			_assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntries(
				assetEntry,
				Arrays.asList(assetTagName1, assetTagName2, assetTagName3));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.WARN, logEntry.getPriority());
			Assert.assertEquals(
				"Unable to add auto tag: " + assetTagName2,
				logEntry.getMessage());

			AssetTagException assetTagException =
				(AssetTagException)logEntry.getThrowable();

			Assert.assertEquals(
				AssetTagException.INVALID_CHARACTER,
				assetTagException.getType());

			String message = assetTagException.getMessage();

			Assert.assertTrue(
				message.startsWith(
					"Tag name has one or more invalid characters: "));
		}

		List<AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
				assetEntry);

		Assert.assertEquals(
			assetAutoTaggerEntries.toString(), 2,
			assetAutoTaggerEntries.size());

		for (String assetTagName :
				Arrays.asList(assetTagName1, assetTagName3)) {

			AssetTag assetTag = _assetTagLocalService.getTag(
				_group.getGroupId(), assetTagName);

			Assert.assertEquals(1, assetTag.getAssetCount());
			Assert.assertNotNull(
				_assetAutoTaggerEntryLocalService.fetchAssetAutoTaggerEntry(
					assetEntry.getEntryId(), assetTag.getTagId()));
		}
	}

	@Test
	public void testAddAssetAutoTaggerEntryWithExistingAssetTag()
		throws PortalException {

		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "Tag",
			_serviceContext);

		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "tag",
			_serviceContext);

		AssetAutoTaggerEntry assetAutoTaggerEntry =
			_assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntry(
				AssetTestUtil.addAssetEntry(_group.getGroupId()),
				assetTag.getName());

		Assert.assertNotNull(assetAutoTaggerEntry);
		Assert.assertEquals(
			assetTag.getTagId(), assetAutoTaggerEntry.getAssetTagId());
	}

	@Test
	public void testAddAssetAutoTaggerEntryWithoutExistingAssetTag()
		throws PortalException {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		assetEntry.setUserId(TestPropsValues.getUserId());

		assetEntry = _assetEntryLocalService.updateAssetEntry(assetEntry);

		AssetAutoTaggerEntry assetAutoTaggerEntry =
			_assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntry(
				assetEntry, "Tag");

		Assert.assertNotNull(assetAutoTaggerEntry);

		AssetTag assetTag = _assetTagLocalService.fetchAssetTag(
			assetAutoTaggerEntry.getAssetTagId());

		Assert.assertNotNull(assetTag);
		Assert.assertEquals("Tag", assetTag.getName());
	}

	@Test
	public void testDeleteAssetAutoTaggerEntries() throws PortalException {
		AssetEntry assetEntry = _addAssetEntry();
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);

		_assetTagLocalService.addAssetEntryAssetTag(
			assetEntry.getEntryId(), assetTag);

		AssetAutoTaggerEntry assetAutoTaggerEntry =
			_assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntry(
				assetEntry, RandomTestUtil.randomString());

		Assert.assertEquals(
			Collections.singletonList(assetAutoTaggerEntry),
			_assetAutoTaggerEntryLocalService.deleteAssetAutoTaggerEntries(
				assetEntry));

		Assert.assertEquals(
			Collections.emptyList(),
			_assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
				assetEntry));
		Assert.assertEquals(
			Collections.singletonList(assetTag),
			_assetTagLocalService.getAssetEntryAssetTags(
				assetEntry.getEntryId()));

		AssetTag autoAssetTag = _assetTagLocalService.getAssetTag(
			assetAutoTaggerEntry.getAssetTagId());

		Assert.assertEquals(0, autoAssetTag.getAssetCount());
	}

	private AssetEntry _addAssetEntry() throws PortalException {
		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		assetEntry.setUserId(TestPropsValues.getUserId());

		return _assetEntryLocalService.updateAssetEntry(assetEntry);
	}

	@Inject
	private AssetAutoTaggerEntryLocalService _assetAutoTaggerEntryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

}