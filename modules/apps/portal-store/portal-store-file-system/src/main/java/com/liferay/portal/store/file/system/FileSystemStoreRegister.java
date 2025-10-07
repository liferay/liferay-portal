/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.file.system;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.convert.documentlibrary.FileSystemStoreRootDirException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration;
import com.liferay.portal.store.file.system.lenient.LenientStore;
import com.liferay.portal.store.file.system.safe.file.name.SafeFileNameStore;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration",
	service = {}
)
public class FileSystemStoreRegister {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		FileSystemStoreConfiguration fileSystemStoreConfiguration =
			ConfigurableUtil.createConfigurable(
				FileSystemStoreConfiguration.class, properties);

		if (Validator.isBlank(fileSystemStoreConfiguration.rootDir())) {
			throw new IllegalArgumentException(
				"File system root directory is not set",
				new FileSystemStoreRootDirException());
		}

		FileSystemStore fileSystemStore = new FileSystemStore(
			fileSystemStoreConfiguration);

		_serviceRegistration = bundleContext.registerService(
			Store.class, _wrapStore(fileSystemStore),
			HashMapDictionaryBuilder.<String, Object>put(
				"rootDir", fileSystemStore.getRootDir()
			).put(
				"store.type", FileSystemStore.class.getName()
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	private Store _wrapStore(Store store) {
		if (!GetterUtil.getBoolean(
				PropsUtil.get("dl.store.file.system.lenient"))) {

			return new SafeFileNameStore(store);
		}

		return new LenientStore(new SafeFileNameStore(store));
	}

	private ServiceRegistration<Store> _serviceRegistration;

}