/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.Provider;
import java.security.Security;

import java.util.UUID;

/**
 * Process-wide static entry point for emitting FIPS audit events to the
 * canonical NDJSON audit log at
 * <code>${liferay.home}/logs/fips-audit.ndjson</code>.
 *
 * <p>
 * The FIPS application state machine drives events during boot, before the OSGi
 * runtime exists, so the sink and envelope sources are wired without any
 * framework dependency: the CMVP certificate ID and deployment instance ID come
 * from deployment configuration (a {@link PropsKeys} property), the provider
 * name from the validated JCE provider, and each record is appended and
 * {@code fsync}ed to disk so a critical Error State entry survives a crash.
 * </p>
 *
 * @author Jorge García Jiménez
 */
public class FIPSAuditEventEmitterUtil {

	public static synchronized void emit(FIPSAuditEvent fipsAuditEvent) {
		_fipsAuditEventEmitter.emit(fipsAuditEvent);
	}

	private static String _getCMVPCertificateId() {
		return PropsValues.FIPS_AUDIT_PROVIDER_CMVP_CERTIFICATE_ID;
	}

	private static String _getDeploymentInstanceId() {
		String deploymentInstanceId =
			PropsValues.FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID;

		if (Validator.isNotNull(deploymentInstanceId)) {
			return deploymentInstanceId;
		}

		Path path = Paths.get(
			PropsValues.LIFERAY_HOME, "data",
			"fips-audit-deployment-instance-id");

		try {
			if (Files.exists(path)) {
				String persistedId = new String(
					Files.readAllBytes(path), StandardCharsets.UTF_8);

				return persistedId.trim();
			}

			String deploymentInstanceUUID = UUID.randomUUID(
			).toString();

			Files.createDirectories(path.getParent());

			Files.write(
				path, deploymentInstanceUUID.getBytes(StandardCharsets.UTF_8));

			return deploymentInstanceUUID;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(
				"Unable to resolve the FIPS deployment instance ID",
				ioException);
		}
	}

	private static String _getProviderName() {
		Provider[] providers = Security.getProviders();

		if (ArrayUtil.isEmpty(providers)) {
			return "";
		}

		Provider provider = providers[0];

		return provider.getName();
	}

	private static final FIPSAuditEventEmitter _fipsAuditEventEmitter =
		new FIPSAuditEventEmitter(
			new FileSink(), FIPSAuditEventEmitterUtil::_getCMVPCertificateId,
			FIPSAuditEventEmitterUtil::_getDeploymentInstanceId,
			FIPSAuditEventEmitterUtil::_getProviderName);

	private static class FileSink implements Appendable, Flushable {

		@Override
		public Appendable append(char c) {
			return append(String.valueOf(c));
		}

		@Override
		public Appendable append(CharSequence charSequence) {
			try {
				OutputStream outputStream = _getOutputStream();

				outputStream.write(
					charSequence.toString(
					).getBytes(
						StandardCharsets.UTF_8
					));

				return this;
			}
			catch (IOException ioException) {
				throw new UncheckedIOException(
					"Unable to write the FIPS audit log", ioException);
			}
		}

		@Override
		public Appendable append(
			CharSequence charSequence, int start, int end) {

			return append(charSequence.subSequence(start, end));
		}

		@Override
		public void flush() {
			if (_fileOutputStream == null) {
				return;
			}

			try {
				_fileOutputStream.flush();

				FileDescriptor fileDescriptor = _fileOutputStream.getFD();

				fileDescriptor.sync();

				_fileOutputStream.close();
			}
			catch (IOException ioException) {
				throw new UncheckedIOException(
					"Unable to flush the FIPS audit log", ioException);
			}
			finally {
				_fileOutputStream = null;
			}
		}

		private OutputStream _getOutputStream() throws IOException {
			if (_fileOutputStream == null) {
				Path path = Paths.get(
					PropsValues.LIFERAY_HOME, "logs", "fips-audit.ndjson");

				Files.createDirectories(path.getParent());

				_fileOutputStream = new FileOutputStream(path.toFile(), true);
			}

			return _fileOutputStream;
		}

		private FileOutputStream _fileOutputStream;

	}

}