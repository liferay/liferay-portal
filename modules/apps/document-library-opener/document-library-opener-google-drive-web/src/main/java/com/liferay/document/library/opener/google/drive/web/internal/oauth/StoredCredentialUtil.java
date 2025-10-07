/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.oauth;

import com.google.api.client.auth.oauth2.StoredCredential;

import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Marco Galluzzi
 */
public class StoredCredentialUtil {

	public static void add(
		long companyId, String key, StoredCredential storedCredential) {

		_add(companyId, key, storedCredential);

		if (ClusterExecutorUtil.isEnabled()) {
			_executeOnCluster(
				new MethodHandler(
					_addMethodKey, companyId, key, storedCredential));
		}
	}

	public static void clear() {
		_clear();
	}

	public static void clear(long companyId) {
		_clear(companyId);

		if (ClusterExecutorUtil.isEnabled()) {
			_executeOnCluster(
				new MethodHandler(_clearCompanyMethodKey, companyId));
		}
	}

	public static boolean containsKey(long companyId, String key) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.get(companyId);

		if (storedCredentials == null) {
			return false;
		}

		return storedCredentials.containsKey(key);
	}

	public static boolean containsValue(
		long companyId, StoredCredential storedCredential) {

		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.get(companyId);

		if (storedCredentials == null) {
			return false;
		}

		return storedCredentials.containsValue(storedCredential);
	}

	public static void delete(long companyId, String key) {
		_delete(companyId, key);

		if (ClusterExecutorUtil.isEnabled()) {
			_executeOnCluster(
				new MethodHandler(_deleteMethodKey, companyId, key));
		}
	}

	public static StoredCredential get(long companyId, String key) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.get(companyId);

		if (storedCredentials == null) {
			return null;
		}

		return storedCredentials.get(key);
	}

	public static boolean isEmpty(long companyId) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.get(companyId);

		if (storedCredentials == null) {
			return true;
		}

		return storedCredentials.isEmpty();
	}

	public static Set<String> keySet(long companyId) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.get(companyId);

		if (storedCredentials == null) {
			return Collections.emptySet();
		}

		return storedCredentials.keySet();
	}

	public static int size(long companyId) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.get(companyId);

		if (storedCredentials == null) {
			return 0;
		}

		return storedCredentials.size();
	}

	public static Collection<StoredCredential> values(long companyId) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.get(companyId);

		if (storedCredentials == null) {
			return Collections.emptyList();
		}

		return storedCredentials.values();
	}

	private static void _add(
		long companyId, String key, StoredCredential storedCredential) {

		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.computeIfAbsent(
				companyId, absentCompanyId -> new ConcurrentHashMap<>());

		storedCredentials.put(key, storedCredential);
	}

	private static void _clear() {
		_storedCredentials.clear();
	}

	private static void _clear(long companyId) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.computeIfAbsent(
				companyId, absentCompanyId -> new ConcurrentHashMap<>());

		storedCredentials.clear();
	}

	private static void _delete(long companyId, String key) {
		Map<String, StoredCredential> storedCredentials =
			_storedCredentials.computeIfAbsent(
				companyId, absentCompanyId -> new ConcurrentHashMap<>());

		storedCredentials.remove(key);
	}

	private static void _executeOnCluster(MethodHandler methodHandler) {
		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			methodHandler, true);

		clusterRequest.setFireAndForget(true);

		ClusterExecutorUtil.execute(clusterRequest);
	}

	private static final MethodKey _addMethodKey = new MethodKey(
		StoredCredentialUtil.class, "_add", long.class, String.class,
		StoredCredential.class);
	private static final MethodKey _clearCompanyMethodKey = new MethodKey(
		StoredCredentialUtil.class, "_clear", long.class);
	private static final MethodKey _deleteMethodKey = new MethodKey(
		StoredCredentialUtil.class, "_delete", long.class, String.class);
	private static final Map<Long, Map<String, StoredCredential>>
		_storedCredentials = new ConcurrentHashMap<>();

}