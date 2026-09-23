/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.google.merchant.internal.background.task;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import com.liferay.commerce.google.merchant.internal.constants.CommerceGoogleMerchantConstants;
import com.liferay.commerce.google.merchant.internal.jsch.FingerprintHostKeyRepository;
import com.liferay.commerce.google.merchant.internal.sftp.SftpConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thomas Stewart
 */
@Component(
	configurationPid = "com.liferay.commerce.google.merchant.internal.sftp.SftpConfiguration",
	property = "background.task.executor.class.name=com.liferay.commerce.google.merchant.internal.background.task.GoogleMerchantBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class GoogleMerchantBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		// TODO: product definition contents will be sent to the uploader once
		// TODO: completed

		_upload("test.xml", "TEST");

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_sftpConfiguration = ConfigurableUtil.createConfigurable(
			SftpConfiguration.class, properties);
	}

	private void _upload(String fileName, String fileContent) throws Exception {
		ChannelSftp channelSftp = null;
		Session jschSession = null;

		try {
			String host = _sftpConfiguration.host();
			String password = _secretResolver.resolve(
				CompanyConstants.SYSTEM, _sftpConfiguration.password());
			int port = _sftpConfiguration.port();
			String username = _sftpConfiguration.username();

			JSch jSch = new JSch();

			FingerprintHostKeyRepository fingerprintHostKeyRepository =
				new FingerprintHostKeyRepository(
					jSch, _sftpConfiguration.fingerprint());

			jSch.setHostKeyRepository(fingerprintHostKeyRepository);

			jschSession = jSch.getSession(username, host);

			jschSession.setPassword(password);
			jschSession.setPort(port);

			jschSession.connect();

			channelSftp = (ChannelSftp)jschSession.openChannel(
				CommerceGoogleMerchantConstants.JSCH_CHANNEL_SFTP);

			channelSftp.connect();

			InputStream inputStream = new ByteArrayInputStream(
				fileContent.getBytes(StandardCharsets.UTF_8));

			channelSftp.put(inputStream, fileName);
		}
		finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}

			if (jschSession != null) {
				jschSession.disconnect();
			}
		}
	}

	@Reference
	private SecretResolver _secretResolver;

	private SftpConfiguration _sftpConfiguration;

}