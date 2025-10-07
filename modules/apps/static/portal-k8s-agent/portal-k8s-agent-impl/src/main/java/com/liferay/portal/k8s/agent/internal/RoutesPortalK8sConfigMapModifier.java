/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.k8s.agent.custodian.VirtualInstanceCustodian;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalInetSocketAddressEventListener;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gregory Amerson
 */
@Component(
	property = "service.ranking:Integer=-1",
	service = {PortalK8sConfigMapModifier.class, VirtualInstanceCustodian.class}
)
public class RoutesPortalK8sConfigMapModifier
	implements PortalInetSocketAddressEventListener, PortalK8sConfigMapModifier,
			   VirtualInstanceCustodian {

	@Override
	public void clean(List<String> virtualInstanceIds) {
		Path liferayRoutesPath = _getLiferayRoutesPath();

		if (liferayRoutesPath == null) {
			return;
		}

		File liferayRoutesFile = liferayRoutesPath.toFile();

		for (String directoryName : FileUtil.listDirs(liferayRoutesFile)) {
			if (Objects.equals(directoryName, "default")) {
				continue;
			}

			if (!virtualInstanceIds.contains(directoryName)) {
				File routesDirectory = new File(
					liferayRoutesFile, directoryName);

				if (routesDirectory.exists()) {
					FileUtil.deltree(routesDirectory);

					if (_log.isDebugEnabled()) {
						_log.debug("Deleted custodian " + directoryName);
					}
				}
			}
		}
	}

	@Override
	public Result modifyConfigMap(
		Consumer<PortalK8sConfigMapModifier.ConfigMapModel>
			configMapModelConsumer,
		String configMapName) {

		Objects.requireNonNull(
			configMapModelConsumer, "Config map model consumer is null");

		Map<String, String> annotations = new TreeMap<>();
		Map<String, String> binaryData = new TreeMap<>();
		Map<String, String> data = new TreeMap<>();
		Map<String, String> labels = new TreeMap<>();

		configMapModelConsumer.accept(
			new ConfigMapModel() {

				@Override
				public Map<String, String> annotations() {
					return annotations;
				}

				@Override
				public Map<String, String> binaryData() {
					return binaryData;
				}

				@Override
				public Map<String, String> data() {
					return data;
				}

				@Override
				public Map<String, String> labels() {
					return labels;
				}

			});

		if (binaryData.isEmpty() && data.isEmpty()) {
			if (_log.isInfoEnabled()) {
				_log.info("Deleting routes for " + configMapName);
			}

			try {
				_deleteRoutes(configMapName, labels);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to delete routes for " + configMapName, exception);
			}

			return Result.DELETED;
		}

		try {
			_writeRoutes(data, labels);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to write routes for " + configMapName, exception);
		}

		return Result.CREATED;
	}

	@Override
	public void portalLocalInetSocketAddressConfigured(
		InetSocketAddress localInetSocketAddress, boolean secure) {

		_updateDXPRoutes(secure);
	}

	@Override
	public void portalServerInetSocketAddressConfigured(
		InetSocketAddress serverInetSocketAddress, boolean secure) {

		_updateDXPRoutes(secure);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistrations.add(
			bundleContext.registerService(
				ModelListener.class, new CompanyModelListener(), null));
		_serviceRegistrations.add(
			bundleContext.registerService(
				PortalInetSocketAddressEventListener.class, this, null));
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private void _deleteRoutes(String configMapName, Map<String, String> labels)
		throws Exception {

		Path liferayRoutesPath = _getLiferayRoutesPath();
		String virtualInstanceId = labels.get(
			"dxp.lxc.liferay.com/virtualInstanceId");

		if ((liferayRoutesPath == null) || (virtualInstanceId == null)) {
			return;
		}

		if (Objects.equals(
				virtualInstanceId, PropsValues.COMPANY_DEFAULT_WEB_ID)) {

			virtualInstanceId = "default";
		}

		Path virtualInstanceIdPath = liferayRoutesPath.resolve(
			virtualInstanceId);

		Matcher matcher = _lxcDXPMetadataPattern.matcher(configMapName);

		if (matcher.matches()) {
			_file.deltree(virtualInstanceIdPath.toFile());
		}
		else {
			matcher = _lxcExtInitMetadataPattern.matcher(configMapName);

			String projectName = labels.get("ext.lxc.liferay.com/projectName");

			if (matcher.matches() && (projectName != null)) {
				Path projectPath = virtualInstanceIdPath.resolve(projectName);

				if (Files.exists(projectPath)) {
					_file.deltree(projectPath.toFile());
				}
			}
		}
	}

	private Path _getLiferayRoutesPath() {
		Path liferayRoutesPath = Paths.get(PropsValues.LIFERAY_HOME, "routes");

		try {
			liferayRoutesPath = Files.createDirectories(liferayRoutesPath);
		}
		catch (IOException ioException) {
			_log.error("Unable to create routes directory", ioException);
		}

		return liferayRoutesPath;
	}

	private int _getPortalLocalPort() {
		return _portal.getPortalLocalPort(
			Objects.equals(PropsValues.WEB_SERVER_PROTOCOL, "https"));
	}

	private void _updateDXPRoutes(boolean secure) {
		try {
			Files.walkFileTree(
				_getLiferayRoutesPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (!Objects.equals(
								String.valueOf(path.getFileName()), "dxp")) {

							return FileVisitResult.CONTINUE;
						}

						Map<String, String> data = new HashMap<>();

						File dir = path.toFile();

						for (File file : dir.listFiles()) {
							data.put(
								file.getName(),
								new String(Files.readAllBytes(file.toPath())));
						}

						_updatePortalLocalPort(
							data, _portal.getPortalLocalPort(secure));

						data.forEach(
							(key, value) -> {
								try {
									Path keyPath = path.resolve(key);

									_file.write(
										keyPath.toFile(), value.getBytes());
								}
								catch (IOException ioException) {
									ReflectionUtil.throwException(ioException);
								}
							});

						return FileVisitResult.SKIP_SUBTREE;
					}

				});
		}
		catch (IOException ioException) {
			_log.error("Unable to update DXP routes", ioException);
		}
	}

	private void _updatePortalLocalPort(
		Map<String, String> labels, int portalLocalPort) {

		if (portalLocalPort <= 0) {
			return;
		}

		String lxcDXPMainDomain = labels.getOrDefault(
			"com.liferay.lxc.dxp.main.domain",
			labels.get("com.liferay.lxc.dxp.mainDomain"));

		if ((lxcDXPMainDomain != null) &&
			(lxcDXPMainDomain.indexOf(':') == -1)) {

			labels.put(
				"com.liferay.lxc.dxp.main.domain",
				lxcDXPMainDomain + ":" + portalLocalPort);
			labels.put(
				"com.liferay.lxc.dxp.mainDomain",
				lxcDXPMainDomain + ":" + portalLocalPort);
		}

		List<String> lxcDXPDomains = StringUtil.split(
			labels.get("com.liferay.lxc.dxp.domains"), CharPool.NEW_LINE);

		if (lxcDXPDomains.isEmpty()) {
			return;
		}

		List<String> updatedLXCDXPDomains = new ArrayList<>();

		for (String lxcDXPDomain : lxcDXPDomains) {
			if ((lxcDXPDomain != null) && (lxcDXPDomain.indexOf(":") == -1)) {
				updatedLXCDXPDomains.add(lxcDXPDomain + ":" + portalLocalPort);
			}
			else {
				updatedLXCDXPDomains.add(lxcDXPDomain);
			}
		}

		labels.put(
			"com.liferay.lxc.dxp.domains",
			StringUtil.merge(updatedLXCDXPDomains, StringPool.NEW_LINE));
	}

	private void _write(Path path, Map<String, String> data) {
		data.forEach(
			(key, value) -> {
				try {
					Path keyPath = path.resolve(key);

					_file.write(keyPath.toFile(), value.getBytes());
				}
				catch (IOException ioException) {
					ReflectionUtil.throwException(ioException);
				}
			});
	}

	private void _writeRoutes(
			Map<String, String> data, Map<String, String> labels)
		throws Exception {

		Path liferayRoutesPath = _getLiferayRoutesPath();

		if (liferayRoutesPath == null) {
			return;
		}

		String metadataType = labels.get("lxc.liferay.com/metadataType");
		String virtualInstanceId = labels.get(
			"dxp.lxc.liferay.com/virtualInstanceId");

		if ((metadataType == null) || (virtualInstanceId == null)) {
			return;
		}

		if (Objects.equals(
				virtualInstanceId, PropsValues.COMPANY_DEFAULT_WEB_ID)) {

			virtualInstanceId = "default";
		}

		Path virtualInstanceIdPath = liferayRoutesPath.resolve(
			virtualInstanceId);

		Files.createDirectories(virtualInstanceIdPath);

		if (Objects.equals(metadataType, "dxp")) {
			Path dxpRoutesPath = virtualInstanceIdPath.resolve("dxp");

			Files.createDirectories(dxpRoutesPath);

			int portalLocalPort = _getPortalLocalPort();

			if (portalLocalPort > 0) {
				_updatePortalLocalPort(data, portalLocalPort);
			}

			_write(dxpRoutesPath, data);
		}
		else if (Objects.equals(metadataType, "ext-init")) {
			String projectName = labels.get("ext.lxc.liferay.com/projectName");

			Path projectPath = virtualInstanceIdPath.resolve(projectName);

			Files.createDirectories(projectPath);

			_write(projectPath, data);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RoutesPortalK8sConfigMapModifier.class);

	private static final Pattern _lxcDXPMetadataPattern = Pattern.compile(
		"(.*)-lxc-dxp-metadata$");
	private static final Pattern _lxcExtInitMetadataPattern = Pattern.compile(
		"(.*)-lxc-ext-init-metadata$");

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private Portal _portal;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	private static class CompanyModelListener
		extends BaseModelListener<Company> {

		@Override
		public void onAfterRemove(Company company)
			throws ModelListenerException {

			if (Objects.equals(
					company.getWebId(), PropsValues.COMPANY_DEFAULT_WEB_ID)) {

				return;
			}

			PortalK8sConfigMapModifier portalK8sConfigMapModifier =
				_portalK8sConfigMapModifierSnapshot.get();

			portalK8sConfigMapModifier.modifyConfigMap(
				configMapModel -> {
					Map<String, String> data = configMapModel.data();

					data.clear();

					Map<String, String> labels = configMapModel.labels();

					labels.put(
						"dxp.lxc.liferay.com/virtualInstanceId",
						company.getWebId());
				},
				_getConfigMapName(company));
		}

		private String _getConfigMapName(Company company) {
			return company.getWebId() + "-lxc-dxp-metadata";
		}

		private static final Snapshot<PortalK8sConfigMapModifier>
			_portalK8sConfigMapModifierSnapshot = new Snapshot<>(
				CompanyModelListener.class, PortalK8sConfigMapModifier.class,
				null, true);

	}

}