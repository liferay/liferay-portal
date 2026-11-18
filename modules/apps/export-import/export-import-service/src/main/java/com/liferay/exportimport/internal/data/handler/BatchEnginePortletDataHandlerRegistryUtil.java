/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Vendel Toreki
 * @author Petteri Karttunen
 */
public class BatchEnginePortletDataHandlerRegistryUtil {

	public static BatchEnginePortletDataHandler getByClassName(
		long companyId, String className) {

		String portletId = _classNamePortletIdsMap.get(className);

		if ((portletId == null) || !_isAllowed(companyId, portletId)) {
			return null;
		}

		return _portletIdBatchEnginePortletDataHandlersMap.get(portletId);
	}

	public static String getPortletId(long companyId, String key) {
		String portletId = _allCompaniesKeyPortletIdsMap.get(key);

		if (portletId == null) {
			Map<String, String> keyPortletIdsMap =
				_companyIdKeyPortletIdsMap.get(companyId);

			if (keyPortletIdsMap != null) {
				portletId = keyPortletIdsMap.get(key);
			}
		}

		if (!_isAllowed(companyId, portletId)) {
			return null;
		}

		return portletId;
	}

	public static boolean hasByClassName(String className, long companyId) {
		String portletId = _classNamePortletIdsMap.get(className);

		if (portletId == null) {
			return false;
		}

		return _isAllowed(companyId, portletId);
	}

	protected static BatchEnginePortletDataHandler getByPortletId(
		long companyId, String portletId) {

		if (!_isAllowed(companyId, portletId)) {
			return null;
		}

		return _portletIdBatchEnginePortletDataHandlersMap.get(portletId);
	}

	protected static void registerBatchEnginePortletDataHandler(
		BatchEnginePortletDataHandler batchEnginePortletDataHandler,
		long companyId, String portletId) {

		if (_isAllCompanies(companyId)) {
			_allCompaniesPortletIds.add(portletId);
		}
		else {
			_portletIdCompanyIdMap.put(portletId, companyId);
		}

		_portletIdBatchEnginePortletDataHandlersMap.put(
			portletId, batchEnginePortletDataHandler);

		for (String className : batchEnginePortletDataHandler.getClassNames()) {
			_classNamePortletIdsMap.put(className, portletId);
		}
	}

	protected static void registerKey(
		long companyId, String key, String portletId) {

		if (_isAllCompanies(companyId)) {
			_allCompaniesKeyPortletIdsMap.put(key, portletId);
		}
		else {
			_companyIdKeyPortletIdsMap.compute(
				companyId,
				(__, keyPortletIdsMap) -> {
					if (keyPortletIdsMap == null) {
						keyPortletIdsMap = new ConcurrentHashMap<>();
					}

					keyPortletIdsMap.put(key, portletId);

					return keyPortletIdsMap;
				});
		}
	}

	protected static void unregister(long companyId, String portletId) {
		if (_isAllCompanies(companyId)) {
			_allCompaniesPortletIds.remove(portletId);
		}
		else {
			_portletIdCompanyIdMap.remove(portletId, companyId);
		}

		_portletIdBatchEnginePortletDataHandlersMap.remove(portletId);

		Set<Map.Entry<String, String>> entries =
			_classNamePortletIdsMap.entrySet();

		entries.removeIf(entry -> Objects.equals(entry.getValue(), portletId));
	}

	protected static void unregisterKey(
		long companyId, String key, String portletId) {

		if (_isAllCompanies(companyId)) {
			_allCompaniesKeyPortletIdsMap.remove(key, portletId);
		}

		_companyIdKeyPortletIdsMap.computeIfPresent(
			companyId,
			(__, keyPortletIdsMap) -> {
				keyPortletIdsMap.remove(key, portletId);

				if (keyPortletIdsMap.isEmpty()) {
					return null;
				}

				return keyPortletIdsMap;
			});
	}

	private static boolean _isAllCompanies(long companyId) {
		if (companyId == 0) {
			return true;
		}

		return false;
	}

	private static boolean _isAllowed(long companyId, String portletId) {
		if (portletId == null) {
			return false;
		}

		if (_allCompaniesPortletIds.contains(portletId)) {
			return true;
		}

		return Objects.equals(companyId, _portletIdCompanyIdMap.get(portletId));
	}

	private static final Map<String, String> _allCompaniesKeyPortletIdsMap =
		new ConcurrentHashMap<>();
	private static final Set<String> _allCompaniesPortletIds =
		new CopyOnWriteArraySet<>();
	private static final Map<String, String> _classNamePortletIdsMap =
		new ConcurrentHashMap<>();
	private static final Map<Long, Map<String, String>>
		_companyIdKeyPortletIdsMap = new ConcurrentHashMap<>();
	private static final Map<String, BatchEnginePortletDataHandler>
		_portletIdBatchEnginePortletDataHandlersMap = new ConcurrentHashMap<>();
	private static final Map<String, Long> _portletIdCompanyIdMap =
		new ConcurrentHashMap<>();

}