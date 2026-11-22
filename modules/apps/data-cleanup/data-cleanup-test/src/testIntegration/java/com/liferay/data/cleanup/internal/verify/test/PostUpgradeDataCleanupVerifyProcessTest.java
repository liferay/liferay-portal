/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.BundleContextTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Constructor;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class PostUpgradeDataCleanupVerifyProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetPostUpgradeDataCleanupProcesses() throws Exception {
		Bundle bundle = BundleUtil.getBundle(
			BundleContextTestUtil.getBundleContext(
				PostUpgradeDataCleanupVerifyProcessTest.class),
			"com.liferay.data.cleanup.impl");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.data.cleanup.internal.verify." +
				"PostUpgradeDataCleanupVerifyProcess");

		Constructor<?> constructor = clazz.getDeclaredConstructor();

		Object postUpgradeDataCleanupVerifyProcess = constructor.newInstance();

		Snapshot<Object> snapshot = new Snapshot<>(
			PostUpgradeDataCleanupVerifyProcessTest.class, Object.class,
			"(component.name=__unavailable__)");

		try (AutoCloseable autoCloseable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					clazz, "_indexInformationSnapshot", snapshot);
			AutoCloseable autoCloseable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					clazz, "_indexNameBuilderSnapshot", snapshot);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.data.cleanup.internal.verify." +
					"PostUpgradeDataCleanupVerifyProcess",
				LoggerTestUtil.WARN)) {

			List<?> postUpgradeDataCleanupProcesses = ReflectionTestUtil.invoke(
				postUpgradeDataCleanupVerifyProcess,
				"_getPostUpgradeDataCleanupProcesses", new Class<?>[0]);

			Assert.assertEquals(
				postUpgradeDataCleanupProcesses.toString(), 4,
				postUpgradeDataCleanupProcesses.size());

			List<String> classNames = TransformUtil.transform(
				postUpgradeDataCleanupProcesses,
				postUpgradeDataCleanupProcess -> {
					Class<?> postUpgradeDataCleanupProcessClass =
						postUpgradeDataCleanupProcess.getClass();

					return postUpgradeDataCleanupProcessClass.getSimpleName();
				});

			Assert.assertFalse(
				classNames.toString(),
				classNames.contains(
					"SearchIndexPostUpgradeDataCleanupProcess"));

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 1, messages.size());

			Assert.assertEquals(
				"Skipping search index cleanup: search engine unavailable",
				messages.get(0));
		}
	}

}