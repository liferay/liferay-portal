/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.dto.converter;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
@Component(service = DTOConverterRegistry.class)
public class DTOConverterRegistryImpl implements DTOConverterRegistry {

	@Override
	public Set<String> getDTOClassNames() {
		return _serviceTrackerMap.keySet();
	}

	@Override
	public DTOConverter<?, ?> getDTOConverter(String dtoClassName) {
		return _getDTOConverter(dtoClassName);
	}

	@Override
	public DTOConverter<?, ?> getDTOConverter(
		String applicationName, String dtoClassName, String version) {

		return _getDTOConverter(
			_getKey(applicationName, dtoClassName, version));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext,
			(Class<DTOConverter<?, ?>>)(Class<?>)DTOConverter.class,
			"(dto.class.name=*)",
			(serviceReference, emitter) -> {
				String dtoClassName = (String)serviceReference.getProperty(
					"dto.class.name");

				emitter.emit(dtoClassName);

				String applicationName = (String)serviceReference.getProperty(
					"application.name");
				String version = (String)serviceReference.getProperty(
					"version");

				if (!Validator.isBlank(applicationName) &&
					!Validator.isBlank(version)) {

					emitter.emit(
						_getKey(applicationName, dtoClassName, version));
				}
			},
			new DTOConverterServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private DTOConverter<?, ?> _getDTOConverter(String key) {
		List<DTOConverterHolder> dtoConverterHolders =
			_serviceTrackerMap.getService(key);

		if (ListUtil.isEmpty(dtoConverterHolders)) {
			return null;
		}

		if (dtoConverterHolders.size() == 1) {
			DTOConverterHolder dtoConverterHolder = dtoConverterHolders.get(0);

			return dtoConverterHolder.getDTOConverter();
		}

		DTOConverter<?, ?> defaultDTOConverter = null;

		for (DTOConverterHolder dtoConverterHolder : dtoConverterHolders) {
			if (!dtoConverterHolder.isDefault()) {
				continue;
			}

			if (defaultDTOConverter != null) {
				return null;
			}

			defaultDTOConverter = dtoConverterHolder.getDTOConverter();
		}

		return defaultDTOConverter;
	}

	private String _getKey(
		String applicationName, String dtoClassName, String version) {

		return StringBundler.concat(
			applicationName, StringPool.POUND, dtoClassName, StringPool.POUND,
			version);
	}

	private ServiceTrackerMap<String, List<DTOConverterHolder>>
		_serviceTrackerMap;

	private static class DTOConverterHolder {

		public DTOConverterHolder(
			boolean defaultDTOConverter, DTOConverter<?, ?> dtoConverter) {

			_defaultDTOConverter = defaultDTOConverter;
			_dtoConverter = dtoConverter;
		}

		public DTOConverter<?, ?> getDTOConverter() {
			return _dtoConverter;
		}

		public boolean isDefault() {
			return _defaultDTOConverter;
		}

		private final boolean _defaultDTOConverter;
		private final DTOConverter<?, ?> _dtoConverter;

	}

	private static class DTOConverterServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<DTOConverter<?, ?>, DTOConverterHolder> {

		public DTOConverterServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public DTOConverterHolder addingService(
			ServiceReference<DTOConverter<?, ?>> serviceReference) {

			return new DTOConverterHolder(
				GetterUtil.getBoolean(serviceReference.getProperty("default")),
				_bundleContext.getService(serviceReference));
		}

		@Override
		public void modifiedService(
			ServiceReference<DTOConverter<?, ?>> serviceReference,
			DTOConverterHolder dtoConverterHolder) {
		}

		@Override
		public void removedService(
			ServiceReference<DTOConverter<?, ?>> serviceReference,
			DTOConverterHolder dtoConverterHolder) {

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}