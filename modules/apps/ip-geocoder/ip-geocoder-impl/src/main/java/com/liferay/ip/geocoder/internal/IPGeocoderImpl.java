/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ip.geocoder.internal;

import com.liferay.ip.geocoder.IPGeocoder;
import com.liferay.ip.geocoder.IPInfo;
import com.liferay.ip.geocoder.internal.configuration.IPGeocoderConfiguration;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;

import java.net.InetAddress;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.ip.geocoder.internal.configuration.IPGeocoderConfiguration",
	name = "IPGeocoder", service = IPGeocoder.class
)
public class IPGeocoderImpl implements IPGeocoder {

	@Override
	public IPInfo getIPInfo(HttpServletRequest httpServletRequest) {
		String ipAddress = _getIPAddress(httpServletRequest);

		if (Validator.isNull(ipAddress)) {
			return new IPInfo(StringPool.BLANK, ipAddress);
		}

		String countryCode = _portalCache.get(ipAddress);

		if (countryCode == null) {
			countryCode = _getCountryCode(ipAddress);

			_portalCache.put(ipAddress, countryCode);
		}

		return new IPInfo(countryCode, ipAddress);
	}

	@Activate
	protected void activate(Map<String, String> properties) {
		_properties = properties;

		_portalCache =
			(PortalCache<String, String>)_singleVMPool.getPortalCache(
				IPGeocoderImpl.class.getName());
	}

	private DatabaseReader _createDatabaseReader() {
		try {
			return new DatabaseReader.Builder(
				_getFile()
			).withCache(
				new CHMCache()
			).build();
		}
		catch (IOException ioException) {
			_log.error("Unable to activate IP Geocoder", ioException);

			throw new RuntimeException(
				"Unable to activate IP Geocoder", ioException);
		}
	}

	private String _getCountryCode(String ipAddress) {
		try {
			InetAddress inetAddress = InetAddress.getByName(ipAddress);

			if (inetAddress.isAnyLocalAddress() ||
				inetAddress.isLoopbackAddress()) {

				return StringPool.BLANK;
			}

			DatabaseReader databaseReader =
				_databaseReaderDCLSingleton.getSingleton(
					this::_createDatabaseReader);

			CountryResponse countryResponse = databaseReader.country(
				inetAddress);

			if (countryResponse == null) {
				return StringPool.BLANK;
			}

			Country country = countryResponse.getCountry();

			return country.getIsoCode();
		}
		catch (AddressNotFoundException addressNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(addressNotFoundException);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return StringPool.BLANK;
	}

	private File _getFile() throws IOException {
		IPGeocoderConfiguration ipGeocoderConfiguration =
			ConfigurableUtil.createConfigurable(
				IPGeocoderConfiguration.class, _properties);

		if (Validator.isNotNull(ipGeocoderConfiguration.filePath())) {
			File file = new File(ipGeocoderConfiguration.filePath());

			if (file.exists()) {
				if (_log.isInfoEnabled()) {
					_log.info("Use file " + ipGeocoderConfiguration.filePath());
				}

				return file;
			}
		}

		Class<?> clazz = getClass();

		File file = FileUtil.createTempFile(
			clazz.getResourceAsStream("/com.maxmind.geolite2.country.mmdb"));

		if (_log.isInfoEnabled()) {
			_log.info("Use temp file " + file);
		}

		return file;
	}

	private String _getIPAddress(HttpServletRequest httpServletRequest) {

		// diplomatie.gouv.fr resolves to 77.158.88.130
		// gov.uk resolves to 151.101.192.144
		// state.gov resolves to 34.233.79.178

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String mockIPGeocoderRemoteAddr =
			originalHttpServletRequest.getParameter("mockIPGeocoderRemoteAddr");

		if (mockIPGeocoderRemoteAddr != null) {
			return mockIPGeocoderRemoteAddr;
		}

		return httpServletRequest.getRemoteAddr();
	}

	private static final Log _log = LogFactoryUtil.getLog(IPGeocoderImpl.class);

	private final DCLSingleton<DatabaseReader> _databaseReaderDCLSingleton =
		new DCLSingleton<>();

	@Reference
	private Portal _portal;

	private PortalCache<String, String> _portalCache;
	private volatile Map<String, String> _properties;

	@Reference
	private SingleVMPool _singleVMPool;

}