/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.extender.internal.configuration;

import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;

import java.nio.ByteBuffer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Tina Tian
 */
@Component(service = {})
public class PortletConfigurationExtender
	implements BundleTrackerCustomizer<ServiceRegistration<Configuration>> {

	@Override
	public ServiceRegistration<Configuration> addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		if (!BundleUtil.isLiferayServiceBundle(bundle)) {
			return null;
		}

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		Configuration portletConfiguration =
			ConfigurationFactoryUtil.getConfiguration(classLoader, "portlet");

		if (portletConfiguration == null) {
			return null;
		}

		try {
			String[] resourceActionsConfigurations = StringUtil.split(
				portletConfiguration.get(PropsKeys.RESOURCE_ACTIONS_CONFIGS));

			boolean checkResourceActions = !_isUpToDate(
				classLoader, resourceActionsConfigurations);

			_resourceActions.populateModelResources(
				classLoader, resourceActionsConfigurations,
				checkResourceActions);

			if (!PropsValues.RESOURCE_ACTIONS_STRICT_MODE_ENABLED) {
				_resourceActions.populatePortletResources(
					classLoader, resourceActionsConfigurations,
					checkResourceActions);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to read resource actions config in " +
					PropsKeys.RESOURCE_ACTIONS_CONFIGS,
				exception);
		}

		return _bundleContext.registerService(
			Configuration.class, portletConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"name", "portlet"
			).put(
				"origin.bundle.symbolic.name", bundle.getSymbolicName()
			).build());
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		ServiceRegistration<Configuration> serviceRegistration) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		ServiceRegistration<Configuration> serviceRegistration) {

		serviceRegistration.unregister();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_urlTimestamps = _loadURLTimestamps(bundleContext);

		_bundleContext = bundleContext;

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_saveURLTimestamps(_bundleContext, _refreshedURLTimestamps);
	}

	private void _enqueue(
		Queue<String> queue, String resourceActionsConfiguration) {

		queue.add(resourceActionsConfiguration);

		if (!resourceActionsConfiguration.endsWith("-ext.xml")) {
			queue.add(
				StringUtil.replace(
					resourceActionsConfiguration, ".xml", "-ext.xml"));
		}
	}

	private boolean _isUpToDate(
			ClassLoader classLoader, String[] resourceActionsConfigurations)
		throws Exception {

		Map<URL, Long> urlTimestamps = _toURLTimestamps(
			classLoader, resourceActionsConfigurations);

		boolean upToDate = true;

		for (Map.Entry<URL, Long> entry : urlTimestamps.entrySet()) {
			URL url = entry.getKey();

			String urlString = _trimFwkHash(url.toExternalForm());

			Long cachedLastModifiedTime = _urlTimestamps.get(urlString);

			if ((cachedLastModifiedTime == null) ||
				!cachedLastModifiedTime.equals(entry.getValue())) {

				upToDate = false;
			}

			_refreshedURLTimestamps.put(urlString, entry.getValue());
		}

		return upToDate;
	}

	private Map<String, Long> _loadURLTimestamps(BundleContext bundleContext) {
		Map<String, Long> urlTimestamps = new HashMap<>();

		File dataFile = bundleContext.getDataFile("urlTimestamps.data");

		if (dataFile.exists() && !StartupHelperUtil.isDBNew() &&
			!StartupHelperUtil.isUpgrading()) {

			try {
				Deserializer deserializer = new Deserializer(
					ByteBuffer.wrap(FileUtil.getBytes(dataFile)));

				Bundle bundle = bundleContext.getBundle();

				if ((deserializer.readBoolean() ==
						PropsValues.RESOURCE_ACTIONS_STRICT_MODE_ENABLED) &&
					(deserializer.readLong() == bundle.getLastModified())) {

					int size = deserializer.readInt();

					for (int i = 0; i < size; i++) {
						urlTimestamps.put(
							deserializer.readString(), deserializer.readLong());
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to load url timestamps data", exception);
				}
			}
		}
		else {
			dataFile.delete();
		}

		return urlTimestamps;
	}

	private void _saveURLTimestamps(
		BundleContext bundleContext, Map<String, Long> urlTimestamps) {

		Bundle bundle = bundleContext.getBundle();

		Serializer serializer = new Serializer();

		serializer.writeBoolean(
			PropsValues.RESOURCE_ACTIONS_STRICT_MODE_ENABLED);

		serializer.writeLong(bundle.getLastModified());

		serializer.writeInt(urlTimestamps.size());

		for (Map.Entry<String, Long> entry : urlTimestamps.entrySet()) {
			serializer.writeString(entry.getKey());
			serializer.writeLong(entry.getValue());
		}

		File dataFile = bundleContext.getDataFile("urlTimestamps.data");

		try (OutputStream outputStream = new FileOutputStream(dataFile)) {
			serializer.writeTo(outputStream);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to write url timestamps data", exception);
			}
		}
	}

	private Map<URL, Long> _toURLTimestamps(
			ClassLoader classLoader, String[] resourceActionsConfigurations)
		throws Exception {

		Map<URL, Long> urlTimestamps = new HashMap<>();

		Queue<String> queue = new LinkedList<>();

		for (String resourceActionsConfiguration :
				resourceActionsConfigurations) {

			_enqueue(queue, resourceActionsConfiguration);
		}

		String resourceActionsConfiguration = null;

		while ((resourceActionsConfiguration = queue.poll()) != null) {
			URL url = classLoader.getResource(resourceActionsConfiguration);

			if (url == null) {
				continue;
			}

			URLConnection urlConnection = url.openConnection();

			urlTimestamps.put(url, urlConnection.getLastModified());

			try (InputStream inputStream = urlConnection.getInputStream()) {
				String content = StreamUtil.toString(inputStream);

				if (content.contains("<resource ")) {
					Document document = UnsecureSAXReaderUtil.read(
						content, true);

					Element rootElement = document.getRootElement();

					for (Element resourceElement :
							rootElement.elements("resource")) {

						_enqueue(
							queue,
							StringUtil.trim(
								resourceElement.attributeValue("file")));
					}
				}
			}
		}

		return urlTimestamps;
	}

	private String _trimFwkHash(String urlString) {
		int startIndex = urlString.indexOf(".fwk");

		if (startIndex != -1) {
			int endIndex = urlString.indexOf(CharPool.SLASH, startIndex + 4);

			if (endIndex != -1) {
				String head = urlString.substring(0, startIndex);

				return head.concat(urlString.substring(endIndex));
			}
		}

		return urlString;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletConfigurationExtender.class);

	private BundleContext _bundleContext;
	private BundleTracker<?> _bundleTracker;
	private final Map<String, Long> _refreshedURLTimestamps = new HashMap<>();

	@Reference
	private ResourceActions _resourceActions;

	private Map<String, Long> _urlTimestamps;

}