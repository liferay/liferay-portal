/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditEventEmitterUtilTest {

	@Test
	public void testEmit() throws Exception {
		Path liferayHome = Files.createTempDirectory("fips-audit-test");

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LIFERAY_HOME", liferayHome.toString());
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID", "instance-1");
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_AUDIT_PROVIDER_CMVP_CERTIFICATE_ID", "4743")) {

			FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
				RandomTestUtil.randomString(), FIPSAuditSeverity.CRITICAL);

			fipsAuditEvent.put("from-state", "Operational");
			fipsAuditEvent.put("to-state", RandomTestUtil.randomString());

			FIPSAuditEventEmitterUtil.emit(fipsAuditEvent);

			String ndjson = _read(
				liferayHome.resolve("logs/fips-audit.ndjson"));

			Assert.assertTrue(ndjson, ndjson.endsWith("}\n"));
			Assert.assertTrue(
				ndjson, ndjson.contains("\"cmvp-certificate-id\":\"4743\""));
			Assert.assertTrue(
				ndjson,
				ndjson.contains("\"deployment-instance-id\":\"instance-1\""));
			Assert.assertTrue(
				ndjson, ndjson.contains("\"from-state\":\"Operational\""));
			Assert.assertTrue(ndjson, ndjson.contains("\"provider-name\":"));
		}
		finally {
			_delete(liferayHome);
		}
	}

	@Test
	public void testEmitDerivesStableDeploymentInstanceIdWhenUnset()
		throws Exception {

		Path liferayHome = Files.createTempDirectory("fips-audit-test");

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LIFERAY_HOME", liferayHome.toString());
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID", "");
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_AUDIT_PROVIDER_CMVP_CERTIFICATE_ID", "")) {

			FIPSAuditEventEmitterUtil.emit(
				new FIPSAuditEvent(
					RandomTestUtil.randomString(), FIPSAuditSeverity.INFO));
			FIPSAuditEventEmitterUtil.emit(
				new FIPSAuditEvent(
					RandomTestUtil.randomString(), FIPSAuditSeverity.INFO));

			String[] lines = StringUtil.split(
				_read(liferayHome.resolve("logs/fips-audit.ndjson")), '\n');

			String deploymentInstanceId = _extractDeploymentInstanceId(
				lines[0]);

			Assert.assertTrue(
				deploymentInstanceId, !deploymentInstanceId.isEmpty());
			Assert.assertEquals(
				deploymentInstanceId, _extractDeploymentInstanceId(lines[1]));

			Assert.assertTrue(
				Files.exists(
					liferayHome.resolve(
						"data/fips-audit-deployment-instance-id")));
		}
		finally {
			_delete(liferayHome);
		}
	}

	private void _delete(Path path) throws IOException {
		File file = path.toFile();

		File[] childFiles = file.listFiles();

		if (childFiles != null) {
			for (File childFile : childFiles) {
				_delete(childFile.toPath());
			}
		}

		Files.delete(path);
	}

	private String _extractDeploymentInstanceId(String ndjson) {
		String prefix = "\"deployment-instance-id\":\"";

		int start = ndjson.indexOf(prefix) + prefix.length();

		int end = ndjson.indexOf("\"", start);

		return ndjson.substring(start, end);
	}

	private String _read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

}