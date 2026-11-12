/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.clamd.scanner.internal;

import com.liferay.antivirus.clamd.scanner.internal.configuration.ClamdAntivirusScannerConfiguration;
import com.liferay.document.library.kernel.antivirus.AntivirusScanner;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.antivirus.AntivirusVirusFoundException;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import fi.solita.clamav.ClamAVClient;
import fi.solita.clamav.ClamAVSizeLimitException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.antivirus.clamd.scanner.internal.configuration.ClamdAntivirusScannerConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = AntivirusScanner.class
)
public class ClamdAntivirusScanner implements AntivirusScanner {

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void scan(byte[] bytes) throws AntivirusScannerException {
		try {
			byte[] reply = _clamdClient.scan(bytes);

			if (!ClamAVClient.isCleanReply(reply)) {
				throw new AntivirusVirusFoundException(
					"Virus detected in byte array", _getVirusName(reply));
			}
		}
		catch (ClamAVSizeLimitException clamAVSizeLimitException) {
			throw new AntivirusScannerException(
				AntivirusScannerException.SIZE_LIMIT_EXCEEDED,
				clamAVSizeLimitException);
		}
		catch (IOException ioException) {
			throw new AntivirusScannerException(
				AntivirusScannerException.PROCESS_FAILURE, ioException);
		}
	}

	@Override
	public void scan(File file) throws AntivirusScannerException {
		try (InputStream inputStream = new FileInputStream(file)) {
			byte[] reply = _clamdClient.scan(inputStream);

			if (!ClamAVClient.isCleanReply(reply)) {
				throw new AntivirusVirusFoundException(
					"Virus detected in " + file.getAbsolutePath(),
					_getVirusName(reply));
			}
		}
		catch (ClamAVSizeLimitException clamAVSizeLimitException) {
			throw new AntivirusScannerException(
				AntivirusScannerException.SIZE_LIMIT_EXCEEDED,
				clamAVSizeLimitException);
		}
		catch (IOException ioException) {
			throw new AntivirusScannerException(
				AntivirusScannerException.PROCESS_FAILURE, ioException);
		}
	}

	@Override
	public void scan(InputStream inputStream) throws AntivirusScannerException {
		try {
			byte[] reply = _clamdClient.scan(inputStream);

			if (!ClamAVClient.isCleanReply(reply)) {
				throw new AntivirusVirusFoundException(
					"Virus detected in stream", _getVirusName(reply));
			}
		}
		catch (ClamAVSizeLimitException clamAVSizeLimitException) {
			throw new AntivirusScannerException(
				AntivirusScannerException.SIZE_LIMIT_EXCEEDED,
				clamAVSizeLimitException);
		}
		catch (IOException ioException) {
			throw new AntivirusScannerException(
				AntivirusScannerException.PROCESS_FAILURE, ioException);
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		ClamdAntivirusScannerConfiguration clamdAntivirusScannerConfiguration =
			ConfigurableUtil.createConfigurable(
				ClamdAntivirusScannerConfiguration.class, properties);

		if (Validator.isBlank(clamdAntivirusScannerConfiguration.hostname()) &&
			_log.isWarnEnabled()) {

			_log.warn(
				"The ClamAV hostname is blank, so scans will connect to the " +
					"loopback address");
		}

		_clamdClient = new ClamAVClient(
			clamdAntivirusScannerConfiguration.hostname(),
			clamdAntivirusScannerConfiguration.port(),
			clamdAntivirusScannerConfiguration.timeout());
	}

	private String _getVirusName(byte[] reply) {
		String virusName = new String(reply, StandardCharsets.US_ASCII);

		return virusName.substring(
			"stream: ".length(), virusName.length() - (" FOUND".length() + 1));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClamdAntivirusScanner.class);

	private ClamAVClient _clamdClient;

}