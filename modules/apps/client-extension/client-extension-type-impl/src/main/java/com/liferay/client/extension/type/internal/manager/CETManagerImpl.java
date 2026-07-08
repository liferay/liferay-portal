/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.manager;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeException;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.configuration.CETConfiguration;
import com.liferay.client.extension.type.deployer.CETDeployer;
import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.PortalCacheMapSynchronizeUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = CETManager.class)
public class CETManagerImpl implements CETManager {

	@Override
	public CET addCET(
			CETConfiguration cetConfiguration, long companyId,
			String externalReferenceCode)
		throws PortalException {

		CET cet = _cetFactory.create(
			cetConfiguration, companyId, externalReferenceCode, true);

		Map<String, CET> cetsMap = _getCETsMap(cet.getCompanyId());

		cetsMap.put(externalReferenceCode, cet);

		Map<String, List<ServiceRegistration<?>>> serviceRegistrationsMap =
			_getServiceRegistrationsMap(cet.getCompanyId());

		serviceRegistrationsMap.put(
			externalReferenceCode, _cetDeployer.deploy(cet));

		return cet;
	}

	@Override
	public void deleteCET(CET cet) {
		Map<String, CET> cetsMap = _getCETsMap(cet.getCompanyId());

		cetsMap.remove(cet.getExternalReferenceCode());

		_undeployCET(cet);
	}

	@Override
	public CET getCET(long companyId, String externalReferenceCode) {
		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.
				fetchClientExtensionEntryByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (clientExtensionEntry != null) {
			try {
				return _getCET(clientExtensionEntry);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return null;
			}
		}

		Map<String, CET> cetsMap = _getCETsMap(companyId);

		CET cet = cetsMap.get(externalReferenceCode);

		if (cet == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No CET found for external reference code " +
						externalReferenceCode);
			}
		}

		return cet;
	}

	@Override
	public List<CET> getCETs(
			long companyId, String keywords, String type, Pagination pagination,
			Sort sort)
		throws PortalException {

		// TODO Sort

		return ListUtil.subList(
			_getCETs(companyId, keywords, type), pagination.getStartPosition(),
			pagination.getEndPosition());
	}

	@Override
	public int getCETsCount(long companyId, String keywords, String type)
		throws PortalException {

		List<CET> cets = _getCETs(companyId, keywords, type);

		return cets.size();
	}

	@Activate
	protected void activate() throws Exception {
		PortalCacheMapSynchronizeUtil.synchronize(
			_getPortalCache(), _cets,
			new PortalCacheMapSynchronizeUtil.Synchronizer<Long, Object>() {

				@Override
				public void onSynchronize(
					Map<? extends Long, ? extends Object> map, Long key,
					Object value, int timeToLive) {

					map.remove(key);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_cets.clear();

		for (Map.Entry<Long, Map<String, CET>> entry1 : _cetsMaps.entrySet()) {
			Map<String, CET> cetsMap = entry1.getValue();

			for (Map.Entry<String, CET> entry2 : cetsMap.entrySet()) {
				CET cet = entry2.getValue();

				_undeployCET(cet);
			}
		}
	}

	private boolean _contains(String string1, String string2) {
		if ((string1 == null) || (string2 == null)) {
			return false;
		}

		string1 = StringUtil.toLowerCase(string1);
		string2 = StringUtil.toLowerCase(string2);

		return string1.contains(string2);
	}

	private CET _getCET(ClientExtensionEntry clientExtensionEntry)
		throws PortalException {

		long clientExtensionEntryId =
			clientExtensionEntry.getClientExtensionEntryId();

		CET cet = _cets.get(clientExtensionEntryId);

		if (cet != null) {
			return cet;
		}

		cet = _cetFactory.create(clientExtensionEntry, true);

		_cets.put(clientExtensionEntryId, cet);

		return cet;
	}

	private List<CET> _getCETs(long companyId, String keywords, String type)
		throws PortalException {

		List<ClientExtensionEntry> clientExtensionEntries = null;

		if (Validator.isNotNull(type)) {
			clientExtensionEntries =
				_clientExtensionEntryLocalService.getClientExtensionEntries(
					companyId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
		else {
			clientExtensionEntries =
				_clientExtensionEntryLocalService.getClientExtensionEntries(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		List<CET> cets = TransformUtil.transform(
			clientExtensionEntries,
			clientExtensionEntry -> {
				try {
					CET cet = _getCET(clientExtensionEntry);

					if (_isInclude(cet, keywords, type)) {
						return cet;
					}
				}
				catch (ClientExtensionEntryTypeException
							clientExtensionEntryTypeException) {

					if (_log.isDebugEnabled()) {
						_log.debug(clientExtensionEntryTypeException);
					}
				}

				return null;
			});

		Map<String, CET> cetsMap = _getCETsMap(companyId);

		cets.addAll(
			TransformUtil.transform(
				cetsMap.entrySet(),
				entry -> {
					CET cet = entry.getValue();

					if (_isInclude(cet, keywords, type)) {
						return cet;
					}

					return null;
				}));

		return cets;
	}

	private Map<String, CET> _getCETsMap(long companyId) {
		Map<String, CET> cetsMap = _cetsMaps.get(companyId);

		if (cetsMap == null) {
			cetsMap = new ConcurrentHashMap<>();

			_cetsMaps.put(companyId, cetsMap);
		}

		return cetsMap;
	}

	private PortalCache<Long, Object> _getPortalCache() throws Exception {
		ImplementationClassName implementationClassName =
			ClientExtensionEntry.class.getAnnotation(
				ImplementationClassName.class);

		Class<?> clazz = _clientExtensionEntryLocalService.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Class<?> implementationClass = classLoader.loadClass(
			implementationClassName.value());

		PortalCache<?, ?> portalCache = _entityCache.getPortalCache(
			implementationClass);

		return (PortalCache<Long, Object>)
			(PortalCache<?, ?>)PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM,
				portalCache.getPortalCacheName(), portalCache.isMVCC(),
				portalCache.isSharded());
	}

	private Map<String, List<ServiceRegistration<?>>>
		_getServiceRegistrationsMap(long companyId) {

		Map<String, List<ServiceRegistration<?>>> serviceRegistrationsMap =
			_serviceRegistrationsMaps.get(companyId);

		if (serviceRegistrationsMap == null) {
			serviceRegistrationsMap = new ConcurrentHashMap<>();

			_serviceRegistrationsMaps.put(companyId, serviceRegistrationsMap);
		}

		return serviceRegistrationsMap;
	}

	private boolean _isInclude(CET cet, String keywords, String type) {
		if (Validator.isNotNull(type) && !Objects.equals(type, cet.getType())) {
			return false;
		}

		if (Validator.isNotNull(keywords) &&
			!_contains(cet.getDescription(), keywords) &&
			!_contains(
				cet.getName(LocaleUtil.getMostRelevantLocale()), keywords) &&
			!_contains(cet.getSourceCodeURL(), keywords)) {

			return false;
		}

		String key = CETFactory.FEATURE_FLAG_KEYS.get(cet.getType());

		if ((key != null) &&
			!FeatureFlagManagerUtil.isEnabled(cet.getCompanyId(), key)) {

			return false;
		}

		return true;
	}

	private void _undeployCET(CET cet) {
		Map<String, List<ServiceRegistration<?>>> serviceRegistrationsMap =
			_getServiceRegistrationsMap(cet.getCompanyId());

		List<ServiceRegistration<?>> serviceRegistrations =
			serviceRegistrationsMap.remove(cet.getExternalReferenceCode());

		if (serviceRegistrations != null) {
			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(CETManagerImpl.class);

	@Reference
	private CETDeployer _cetDeployer;

	@Reference
	private CETFactory _cetFactory;

	private final Map<Long, CET> _cets = new ConcurrentHashMap<>();
	private final Map<Long, Map<String, CET>> _cetsMaps =
		new ConcurrentHashMap<>();

	@Reference
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Reference
	private EntityCache _entityCache;

	private final Map<Long, Map<String, List<ServiceRegistration<?>>>>
		_serviceRegistrationsMaps = new ConcurrentHashMap<>();

}