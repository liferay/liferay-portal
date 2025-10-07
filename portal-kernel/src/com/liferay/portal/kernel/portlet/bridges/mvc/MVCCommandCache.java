/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergio González
 * @author Raymond Augé
 */
public class MVCCommandCache<T extends MVCCommand> {

	public MVCCommandCache(
		T emptyMVCCommand, String packagePrefix, String portletName,
		Class<T> mvcCommandClass, String mvcCommandPostFix) {

		this(
			emptyMVCCommand, packagePrefix, portletName, portletName,
			mvcCommandClass, mvcCommandPostFix);
	}

	public MVCCommandCache(
		T emptyMVCCommand, String packagePrefix, String portletName,
		String portletId, Class<T> mvcCommandClass, String mvcCommandPostFix) {

		_emptyMVCCommand = emptyMVCCommand;
		_mvcCommandClass = mvcCommandClass;
		_mvcCommandPostFix = mvcCommandPostFix;

		if (Validator.isNotNull(packagePrefix) &&
			!packagePrefix.endsWith(StringPool.PERIOD)) {

			packagePrefix = packagePrefix + StringPool.PERIOD;
		}

		_packagePrefix = packagePrefix;

		if (portletId.equals(portletName)) {
			_filterString =
				"(&(jakarta.portlet.name=" + portletId +
					")(mvc.command.name=*))";
		}
		else {
			_filterString = StringBundler.concat(
				"(&(|(jakarta.portlet.name=", portletName,
				")(jakarta.portlet.name=", portletId,
				"))(mvc.command.name=*))");
		}

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			SystemBundleUtil.getBundleContext(), _mvcCommandClass,
			_filterString, _SERVICE_REFERENCE_MAPPER);
	}

	public void close() {
		_serviceTrackerMap.close();
	}

	public T getMVCCommand(String mvcCommandName) {
		T mvcCommand = _serviceTrackerMap.getService(mvcCommandName);

		if (mvcCommand != null) {
			return mvcCommand;
		}

		String className = null;

		try {
			mvcCommand = _mvcCommandCache.get(mvcCommandName);

			if (mvcCommand != null) {
				return mvcCommand;
			}

			if (Validator.isNull(_packagePrefix)) {
				return _emptyMVCCommand;
			}

			className = StringBundler.concat(
				_packagePrefix, Character.toUpperCase(mvcCommandName.charAt(0)),
				mvcCommandName.substring(1), _mvcCommandPostFix);

			mvcCommand = (T)InstanceFactory.newInstance(className);

			_mvcCommandCache.put(mvcCommandName, mvcCommand);

			return mvcCommand;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to instantiate MVCCommand " + className, exception);
			}

			_mvcCommandCache.put(mvcCommandName, _emptyMVCCommand);

			return _emptyMVCCommand;
		}
	}

	public Set<String> getMVCCommandNames() {
		return _serviceTrackerMap.keySet();
	}

	public List<T> getMVCCommands(String key) {
		List<T> mvcCommands = _mvcCommands.get(key);

		String[] mvcCommandNames = StringUtil.split(key);

		if ((mvcCommands != null) &&
			(mvcCommands.size() == mvcCommandNames.length)) {

			return mvcCommands;
		}

		mvcCommands = new ArrayList<>();

		for (String mvcCommandName : mvcCommandNames) {
			T mvcCommand = getMVCCommand(mvcCommandName);

			if (mvcCommand != _emptyMVCCommand) {
				mvcCommands.add(mvcCommand);
			}
			else {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to find MVCCommand " + key);
				}
			}
		}

		_mvcCommands.put(key, mvcCommands);

		for (MVCCommand mvcCommand : mvcCommands) {
			String mvcCommandClassName = ClassUtil.getClassName(mvcCommand);

			List<String> keys = _mvcCommandKeys.get(mvcCommandClassName);

			if (keys == null) {
				keys = new ArrayList<>();

				_mvcCommandKeys.put(mvcCommandClassName, keys);
			}

			keys.add(key);
		}

		return mvcCommands;
	}

	public boolean isEmpty() {
		return _mvcCommandCache.isEmpty();
	}

	private static final ServiceReferenceMapper<String, MVCCommand>
		_SERVICE_REFERENCE_MAPPER = (serviceReference, emitter) -> {
			List<String> mvcCommandNames = StringUtil.asList(
				serviceReference.getProperty("mvc.command.name"));

			for (String mvcCommandName : mvcCommandNames) {
				emitter.emit(mvcCommandName);
			}
		};

	private static final Log _log = LogFactoryUtil.getLog(
		MVCCommandCache.class);

	private final T _emptyMVCCommand;
	private final String _filterString;
	private final Map<String, T> _mvcCommandCache = new ConcurrentHashMap<>();
	private final Class<T> _mvcCommandClass;
	private final Map<String, List<String>> _mvcCommandKeys =
		new ConcurrentHashMap<>();
	private final String _mvcCommandPostFix;
	private final Map<String, List<T>> _mvcCommands = new ConcurrentHashMap<>();
	private final String _packagePrefix;
	private final ServiceTrackerMap<String, T> _serviceTrackerMap;

}