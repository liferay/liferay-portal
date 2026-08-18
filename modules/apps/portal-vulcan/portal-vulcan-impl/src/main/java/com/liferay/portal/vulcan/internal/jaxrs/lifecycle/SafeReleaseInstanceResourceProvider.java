/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.lifecycle;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.message.Message;

/**
 * @author Alejandro Tardín
 */
public class SafeReleaseInstanceResourceProvider implements ResourceProvider {

	public SafeReleaseInstanceResourceProvider(
		ResourceProvider resourceProvider) {

		_resourceProvider = resourceProvider;
	}

	@Override
	public Object getInstance(Message message) {
		Object instance = _resourceProvider.getInstance(message);

		_instances.add(instance);

		return instance;
	}

	@Override
	public Class<?> getResourceClass() {
		return _resourceProvider.getResourceClass();
	}

	public boolean isSingleton() {
		return _resourceProvider.isSingleton();
	}

	@Override
	public void releaseInstance(Message message, Object object) {
		if (_instances.remove(object)) {
			try {
				_resourceProvider.releaseInstance(message, object);
			}
			catch (IllegalArgumentException illegalArgumentException) {

				// The service registration backing this instance was
				// unregistered while the request was in flight, so there is
				// nothing left to release the instance to, and the instance
				// is abandoned to garbage collection. Rethrowing would
				// replace the response the request already produced.

				if (_log.isDebugEnabled()) {
					_log.debug(illegalArgumentException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SafeReleaseInstanceResourceProvider.class);

	private final Set<Object> _instances = ConcurrentHashMap.newKeySet();
	private final ResourceProvider _resourceProvider;

}