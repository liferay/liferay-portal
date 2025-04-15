/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.image.internal;

import com.liferay.image.ImageMagick;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.OSDetector;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;

import jakarta.portlet.PortletPreferences;

import java.io.File;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.im4java.process.ArrayListOutputConsumer;
import org.im4java.process.ProcessEvent;
import org.im4java.process.ProcessExecutor;
import org.im4java.process.ProcessTask;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 * @author Ivica Cardic
 */
@Component(service = ImageMagick.class)
public class ImageMagickImpl implements ImageMagick {

	@Override
	public Future<?> convert(List<String> arguments) throws Exception {
		if (!isEnabled()) {
			throw new IllegalStateException(
				"Cannot call \"convert\" when ImageMagick is disabled");
		}

		if (_globalSearchPath == null) {
			reset();
		}

		ProcessExecutor processExecutor =
			_processExecutorDCLSingleton.getSingleton(
				ImageMagickImpl::_createProcessExecutor);

		LiferayConvertCmd liferayConvertCmd = new LiferayConvertCmd();

		ProcessTask processTask = liferayConvertCmd.getProcessTask(
			_globalSearchPath, getResourceLimits(), arguments);

		processExecutor.execute(processTask);

		return processTask;
	}

	@Override
	public void destroy() {
		_processExecutorDCLSingleton.destroy(ProcessExecutor::shutdownNow);
	}

	@Override
	public String getGlobalSearchPath() {
		PortletPreferences portletPreferences = _prefsProps.getPreferences();

		String globalSearchPath = portletPreferences.getValue(
			PropsKeys.IMAGEMAGICK_GLOBAL_SEARCH_PATH, null);

		if (Validator.isNotNull(globalSearchPath)) {
			return globalSearchPath;
		}

		String filterName = null;

		if (OSDetector.isApple()) {
			filterName = "apple";
		}
		else if (OSDetector.isWindows()) {
			filterName = "windows";
		}
		else {
			filterName = "unix";
		}

		return PropsUtil.get(
			PropsKeys.IMAGEMAGICK_GLOBAL_SEARCH_PATH, new Filter(filterName));
	}

	@Override
	public Properties getResourceLimitsProperties() {
		Properties resourceLimitsProperties = _prefsProps.getProperties(
			PropsKeys.IMAGEMAGICK_RESOURCE_LIMIT, true);

		if (resourceLimitsProperties.isEmpty()) {
			resourceLimitsProperties = PropsUtil.getProperties(
				PropsKeys.IMAGEMAGICK_RESOURCE_LIMIT, true);
		}

		return resourceLimitsProperties;
	}

	@Override
	public String[] identify(List<String> arguments) throws Exception {
		if (!isEnabled()) {
			throw new IllegalStateException(
				"Cannot call \"identify\" when ImageMagick is disabled");
		}

		ProcessExecutor processExecutor =
			_processExecutorDCLSingleton.getSingleton(
				ImageMagickImpl::_createProcessExecutor);

		LiferayIdentifyCmd liferayIdentifyCmd = new LiferayIdentifyCmd();

		ArrayListOutputConsumer arrayListOutputConsumer =
			new ArrayListOutputConsumer();

		liferayIdentifyCmd.setOutputConsumer(arrayListOutputConsumer);

		ProcessTask processTask = liferayIdentifyCmd.getProcessTask(
			_globalSearchPath, getResourceLimits(), arguments);

		processExecutor.execute(processTask);

		processTask.get();

		List<String> output = arrayListOutputConsumer.getOutput();

		if (output != null) {
			return output.toArray(new String[0]);
		}

		return new String[0];
	}

	@Override
	public boolean isEnabled() {
		boolean enabled = false;

		try {
			enabled = _prefsProps.getBoolean(PropsKeys.IMAGEMAGICK_ENABLED);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		if (!enabled && !_warned && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Liferay is not configured to use ImageMagick and ",
					"Ghostscript. For better quality document and image ",
					"previews, install ImageMagick and Ghostscript. Enable ",
					"ImageMagick in portal.properties or in the Server ",
					"Administration section of the Control Panel at: ",
					"http://<server>/group/control_panel/manage/-/server",
					"/external-services."));

			_warned = true;
		}

		return enabled;
	}

	@Override
	public void reset() {
		if (isEnabled()) {
			try {
				_globalSearchPath = getGlobalSearchPath();

				_resourceLimitsProperties = getResourceLimitsProperties();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	@Override
	public byte[] scale(byte[] bytes, String mimeType, int width, int height)
		throws Exception {

		if ((width == 0) && (height == 0)) {
			return bytes;
		}

		File imageFile = null;
		File scaledImageFile = null;

		try {
			imageFile = _file.createTempFile(bytes);

			scaledImageFile = _file.createTempFile(mimeType);

			List<String> arguments = new ArrayList<>();

			arguments.add(imageFile.getAbsolutePath());
			arguments.add("-resize");

			if (height == 0) {
				height = width;
			}

			if (width == 0) {
				width = height;
			}

			arguments.add(StringBundler.concat(width, "x", height, ">"));
			arguments.add(scaledImageFile.getAbsolutePath());

			long start = System.currentTimeMillis();

			Future<?> future = convert(arguments);

			ProcessEvent processEvent = (ProcessEvent)future.get();

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Converted image with ImageMagick in ",
						System.currentTimeMillis() - start, "ms"));
			}

			if (_log.isDebugEnabled() &&
				(processEvent.getException() != null)) {

				_log.debug(processEvent.getException());
			}

			return _file.getBytes(scaledImageFile);
		}
		finally {
			if (imageFile != null) {
				imageFile.delete();
			}

			if (scaledImageFile != null) {
				scaledImageFile.delete();
			}
		}
	}

	protected LinkedList<String> getResourceLimits() {
		LinkedList<String> resourceLimits = new LinkedList<>();

		if (_resourceLimitsProperties == null) {
			return resourceLimits;
		}

		for (Map.Entry<Object, Object> entry :
				_resourceLimitsProperties.entrySet()) {

			String value = (String)entry.getValue();

			if (Validator.isNull(value)) {
				continue;
			}

			resourceLimits.add("-limit");
			resourceLimits.add((String)entry.getKey());
			resourceLimits.add(value);
		}

		return resourceLimits;
	}

	private static ProcessExecutor _createProcessExecutor() {
		ProcessExecutor processExecutor = new ProcessExecutor();

		processExecutor.setThreadFactory(
			new NamedThreadFactory(
				ImageMagickImpl.class.getName(), Thread.MIN_PRIORITY,
				PortalClassLoaderUtil.getClassLoader()));

		return processExecutor;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImageMagickImpl.class);

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	private String _globalSearchPath;

	@Reference
	private PrefsProps _prefsProps;

	private final DCLSingleton<ProcessExecutor> _processExecutorDCLSingleton =
		new DCLSingleton<>();
	private Properties _resourceLimitsProperties;
	private boolean _warned;

}