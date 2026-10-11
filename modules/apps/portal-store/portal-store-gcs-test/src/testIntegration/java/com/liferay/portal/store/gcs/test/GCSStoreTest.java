/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.gcs.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.store.test.util.BaseStoreTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class GCSStoreTest extends BaseStoreTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		String gcsStoreClassName = "com.liferay.portal.store.gcs.GCSStore";
		String dlStoreImpl = PropsUtil.get(PropsKeys.DL_STORE_IMPL);

		Assume.assumeTrue(
			StringBundler.concat(
				"Property \"", PropsKeys.DL_STORE_IMPL, "\" is not set to \"",
				gcsStoreClassName, "\""),
			dlStoreImpl.equals(gcsStoreClassName));
	}

	@Override
	@Test
	public void testGetFileSize() throws Exception {
		super.testGetFileSize();

		long companyId = RandomTestUtil.nextLong();

		try {
			long repositoryId = RandomTestUtil.nextLong();
			String fileName = RandomTestUtil.randomString();

			_store.addFile(
				companyId, repositoryId, fileName, Store.VERSION_DEFAULT,
				new UnsyncByteArrayInputStream(DATA_VERSION));
			_store.addFile(
				companyId, repositoryId, fileName, "1.1",
				new UnsyncByteArrayInputStream(new byte[0]));

			Assert.assertEquals(
				0,
				_store.getFileSize(
					companyId, repositoryId, fileName, StringPool.BLANK));
		}
		finally {
			_store.deleteDirectory(companyId);
		}
	}

	@Override
	@Test
	public void testGetFileVersions() throws Exception {
		super.testGetFileVersions();

		long companyId = RandomTestUtil.nextLong();

		try {
			long repositoryId = RandomTestUtil.nextLong();
			String fileName = RandomTestUtil.randomString();

			_store.addFile(
				companyId, repositoryId, fileName, Store.VERSION_DEFAULT,
				new UnsyncByteArrayInputStream(DATA_VERSION));
			_store.addFile(
				companyId, repositoryId,
				fileName + RandomTestUtil.randomString(), Store.VERSION_DEFAULT,
				new UnsyncByteArrayInputStream(DATA_VERSION));

			Assert.assertArrayEquals(
				new String[] {Store.VERSION_DEFAULT},
				_store.getFileVersions(companyId, repositoryId, fileName));
			Assert.assertArrayEquals(
				new String[] {Store.VERSION_DEFAULT},
				_store.getFileVersions(
					companyId, repositoryId, fileName + StringPool.SLASH));
		}
		finally {
			_store.deleteDirectory(companyId);
		}
	}

	@Override
	protected Store getStore() {
		return _store;
	}

	@Inject(
		filter = "store.type=com.liferay.portal.store.gcs.GCSStore",
		type = Store.class
	)
	private Store _store;

}