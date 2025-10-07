/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.audio.internal;

import com.liferay.document.library.kernel.util.AudioConverter;
import com.liferay.document.library.preview.audio.internal.configuration.DLAudioFFMPEGAudioConverterConfiguration;
import com.liferay.petra.io.AutoDeleteFileInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.document.library.preview.audio.internal.configuration.DLAudioFFMPEGAudioConverterConfiguration",
	service = AudioConverter.class
)
public class DLAudioFFMPEGAudioConverter implements AudioConverter {

	@Override
	public InputStream generateAudioPreview(File file, String format)
		throws Exception {

		File destinationFile = FileUtil.createTempFile(format);

		Properties audioProperties = PropsUtil.getProperties(
			PropsKeys.DL_FILE_ENTRY_PREVIEW_AUDIO, false);

		_runFFMPEGCommand(
			Arrays.asList(
				"ffmpeg", "-y", "-i", file.getAbsolutePath(), "-b:a",
				String.valueOf(
					_getProperty(
						audioProperties,
						PropsKeys.DL_FILE_ENTRY_PREVIEW_AUDIO_BIT_RATE, format,
						_AUDIO_BIT_RATE_DEFAULT)),
				"-ar",
				String.valueOf(
					_getProperty(
						audioProperties,
						PropsKeys.DL_FILE_ENTRY_PREVIEW_AUDIO_SAMPLE_RATE,
						format, _AUDIO_SAMPLE_RATE_DEFAULT)),
				destinationFile.getAbsolutePath()));

		return new AutoDeleteFileInputStream(destinationFile);
	}

	@Override
	public boolean isEnabled() {
		return _dlAudioFFMPEGAudioConverterConfiguration.enabled();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlAudioFFMPEGAudioConverterConfiguration =
			ConfigurableUtil.createConfigurable(
				DLAudioFFMPEGAudioConverterConfiguration.class, properties);
	}

	private void _consumeProcessInputStream(InputStream inputStream)
		throws IOException {

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(inputStream));

		while (bufferedReader.ready()) {
			bufferedReader.readLine();
		}
	}

	private int _getProperty(
		Properties audioProperties, String name, String audioContainer,
		int defaultValue) {

		return GetterUtil.getInteger(
			audioProperties.getProperty(
				StringBundler.concat(
					name, StringPool.OPEN_BRACKET, audioContainer,
					StringPool.CLOSE_BRACKET)),
			defaultValue);
	}

	private void _runFFMPEGCommand(List<String> ffmpegCommand)
		throws Exception {

		ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		InputStream inputStream = process.getInputStream();

		while (true) {
			try {
				_consumeProcessInputStream(inputStream);

				if (!process.waitFor(5, TimeUnit.SECONDS)) {
					continue;
				}

				if (process.exitValue() != 0) {
					throw new Exception(
						StringBundler.concat(
							"FFMPEG command ",
							StringUtil.merge(ffmpegCommand, StringPool.SPACE),
							" failed with exit status ", process.exitValue()));
				}

				return;
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}
			}
		}
	}

	private static final int _AUDIO_BIT_RATE_DEFAULT = 64000;

	private static final int _AUDIO_SAMPLE_RATE_DEFAULT = 44100;

	private static final Log _log = LogFactoryUtil.getLog(
		DLAudioFFMPEGAudioConverter.class);

	private volatile DLAudioFFMPEGAudioConverterConfiguration
		_dlAudioFFMPEGAudioConverterConfiguration;

}