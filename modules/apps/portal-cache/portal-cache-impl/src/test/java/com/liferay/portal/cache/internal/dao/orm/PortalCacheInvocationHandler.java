/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.nio.ByteBuffer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tina Tian
 * @author Preston Crary
 */
public class PortalCacheInvocationHandler implements InvocationHandler {

	public PortalCacheInvocationHandler(
		String portalCacheName, boolean serialized) {

		_portalCacheName = portalCacheName;
		_serialized = serialized;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		String methodName = method.getName();

		if (methodName.equals("get")) {
			if (_serialized) {
				byte[] bytes = (byte[])_map.get(args[0]);

				Deserializer deserializer = new Deserializer(
					ByteBuffer.wrap(bytes));

				return deserializer.readObject();
			}

			return _map.get(args[0]);
		}

		if (methodName.equals("getPortalCacheName")) {
			return _portalCacheName;
		}

		if (methodName.equals("hashCode")) {
			return System.identityHashCode(proxy);
		}

		if (methodName.equals("isSharded")) {
			return false;
		}

		if (methodName.equals("put")) {
			if (_serialized) {
				Serializer serializer = new Serializer();

				serializer.writeObject((Serializable)args[1]);

				ByteBuffer byteBuffer = serializer.toByteBuffer();

				_map.put((Serializable)args[0], byteBuffer.array());
			}
			else {
				_map.put((Serializable)args[0], args[1]);
			}
		}

		if (methodName.equals("remove")) {
			return _map.remove(args[0]);
		}

		if (methodName.equals("removeAll")) {
			_map.clear();
		}

		return null;
	}

	private final Map<Serializable, Object> _map = new HashMap<>();
	private final String _portalCacheName;
	private final boolean _serialized;

}