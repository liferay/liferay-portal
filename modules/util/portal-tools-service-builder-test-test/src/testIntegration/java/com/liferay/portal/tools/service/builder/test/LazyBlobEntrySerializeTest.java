/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry;
import com.liferay.portal.tools.service.builder.test.service.LazyBlobEntryLocalService;

import java.sql.Blob;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tina
 */
@RunWith(Arquillian.class)
public class LazyBlobEntrySerializeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Test
	public void test() throws Exception {
		_group = GroupTestUtil.addGroup();

		String blobContent = RandomTestUtil.randomString();

		LazyBlobEntry lazyBlobEntry =
			_lazyBlobEntryLocalService.addLazyBlobEntry(
				_group.getGroupId(), blobContent.getBytes(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assetLazyBlobEntry(lazyBlobEntry, blobContent);

		Serializer serializer = new Serializer();

		serializer.writeObject(lazyBlobEntry);

		Deserializer deserializer = new Deserializer(serializer.toByteBuffer());

		LazyBlobEntry serializedLazyBlobEntry = deserializer.readObject();

		Assert.assertEquals(lazyBlobEntry, serializedLazyBlobEntry);

		_assetLazyBlobEntry(serializedLazyBlobEntry, blobContent);

		_lazyBlobEntryLocalService.deleteLazyBlobEntry(
			lazyBlobEntry.getLazyBlobEntryId());
	}

	private void _assetLazyBlobEntry(
			LazyBlobEntry lazyBlobEntry, String expectedContent)
		throws Exception {

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(lazyBlobEntry, "_blob1BlobModel"));

		Blob blob1 = lazyBlobEntry.getBlob1();

		Assert.assertEquals(
			expectedContent,
			new String(blob1.getBytes(1, (int)blob1.length())));

		Assert.assertNotNull(
			ReflectionTestUtil.getFieldValue(lazyBlobEntry, "_blob1BlobModel"));

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(lazyBlobEntry, "_blob2BlobModel"));

		Blob blob2 = lazyBlobEntry.getBlob2();

		Assert.assertEquals(
			expectedContent,
			new String(blob2.getBytes(1, (int)blob2.length())));

		Assert.assertNotNull(
			ReflectionTestUtil.getFieldValue(lazyBlobEntry, "_blob2BlobModel"));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LazyBlobEntryLocalService _lazyBlobEntryLocalService;

}