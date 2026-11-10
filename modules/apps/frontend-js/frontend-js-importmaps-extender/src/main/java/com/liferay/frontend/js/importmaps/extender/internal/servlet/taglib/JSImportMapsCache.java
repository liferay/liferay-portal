/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.importmaps.extender.internal.servlet.taglib;

import com.liferay.frontend.js.importmaps.extender.DynamicJSImportMapsContributor;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Iván Zaera Avellón
 */
public class JSImportMapsCache {

	public static final long COMPANY_ID_ALL = 0;

	public JSImportMapsCache(Portal portal) {
		_portal = portal;
	}

	public JSImportMapsRegistration register(
		long companyId,
		DynamicJSImportMapsContributor dynamicJSImportMapsContributor) {

		Map<Long, DynamicJSImportMapsContributor>
			dynamicJSImportMapsContributors =
				_getDynamicJSImportMapsContributors(companyId);

		long id = _nextId.getAndIncrement();

		dynamicJSImportMapsContributors.put(id, dynamicJSImportMapsContributor);

		return () -> dynamicJSImportMapsContributors.remove(id);
	}

	public JSImportMapsRegistration register(
		long companyId, JSONObject jsonObject, String scope) {

		if (scope == null) {
			Map<Long, String> globalImportMapsValues =
				_getGlobalImportMapsValues(companyId);

			long id = _nextId.getAndIncrement();

			String value = jsonObject.toString();

			value = value.substring(1, value.length() - 1);

			globalImportMapsValues.put(id, value);

			return () -> globalImportMapsValues.remove(id);
		}

		Map<String, String> scopedImportMapsValues = _getScopedImportMapsValues(
			companyId);

		String value = scopedImportMapsValues.putIfAbsent(
			scope, jsonObject.toString());

		if (value != null) {
			_log.error(
				StringBundler.concat(
					"Import map ", jsonObject, " for scope ", scope, " will ",
					"be ignored because there is already an import map ",
					"registered under that scope"));

			return () -> {
			};
		}

		return () -> scopedImportMapsValues.remove(scope);
	}

	public void writeImportMaps(
			HttpServletRequest httpServletRequest, Writer writer)
		throws IOException {

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (companyId == COMPANY_ID_ALL) {
			throw new IllegalArgumentException(
				"Company ID cannot be " + COMPANY_ID_ALL);
		}

		writer.write("{\"imports\": {");

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		StringBundler sb = unsyncStringWriter.getStringBundler();

		_appendStaticImports(_getGlobalImportMapsValues(COMPANY_ID_ALL), sb);
		_appendStaticImports(_getGlobalImportMapsValues(companyId), sb);

		Map<Long, DynamicJSImportMapsContributor>
			dynamicJSImportMapsContributors1 =
				_getDynamicJSImportMapsContributors(COMPANY_ID_ALL);

		_appendDynamicImports(
			dynamicJSImportMapsContributors1, httpServletRequest,
			unsyncStringWriter);

		Map<Long, DynamicJSImportMapsContributor>
			dynamicJSImportMapsContributors2 =
				_getDynamicJSImportMapsContributors(companyId);

		_appendDynamicImports(
			dynamicJSImportMapsContributors2, httpServletRequest,
			unsyncStringWriter);

		writer.write(sb.toString());

		writer.write("}, \"scopes\": {");

		sb.setIndex(0);

		_appendStaticScopes(_getScopedImportMapsValues(COMPANY_ID_ALL), sb);
		_appendStaticScopes(_getScopedImportMapsValues(companyId), sb);

		_appendDynamicScopes(
			dynamicJSImportMapsContributors1, httpServletRequest,
			unsyncStringWriter);
		_appendDynamicScopes(
			dynamicJSImportMapsContributors2, httpServletRequest,
			unsyncStringWriter);

		writer.write(sb.toString());

		writer.write("}}");
	}

	private void _appendDynamicImports(
			Map<Long, DynamicJSImportMapsContributor>
				dynamicJSImportMapsContributors,
			HttpServletRequest httpServletRequest,
			UnsyncStringWriter unsyncStringWriter)
		throws IOException {

		StringBundler sb = unsyncStringWriter.getStringBundler();

		for (DynamicJSImportMapsContributor dynamicJSImportMapsContributor :
				dynamicJSImportMapsContributors.values()) {

			int index = sb.index();

			if (index > 0) {
				sb.append(StringPool.COMMA);
			}

			int contentIndex = sb.index();

			dynamicJSImportMapsContributor.writeGlobalImports(
				httpServletRequest, unsyncStringWriter);

			if (sb.index() > contentIndex) {
				continue;
			}

			sb.setIndex(index);
		}
	}

	private void _appendDynamicScopes(
			Map<Long, DynamicJSImportMapsContributor>
				dynamicJSImportMapsContributors,
			HttpServletRequest httpServletRequest,
			UnsyncStringWriter unsyncStringWriter)
		throws IOException {

		StringBundler sb = unsyncStringWriter.getStringBundler();

		for (DynamicJSImportMapsContributor dynamicJSImportMapsContributor :
				dynamicJSImportMapsContributors.values()) {

			int index = sb.index();

			if (index > 0) {
				sb.append(StringPool.COMMA);
			}

			int contentIndex = sb.index();

			dynamicJSImportMapsContributor.writeScopedImports(
				httpServletRequest, unsyncStringWriter);

			if (sb.index() > contentIndex) {
				continue;
			}

			sb.setIndex(index);
		}
	}

	private void _appendStaticImports(
		Map<Long, String> globalImportMapsValues, StringBundler sb) {

		boolean first = true;

		for (String value : globalImportMapsValues.values()) {
			if (first) {
				first = false;

				if (sb.index() > 0) {
					sb.append(StringPool.COMMA);
				}
			}
			else {
				sb.append(StringPool.COMMA);
			}

			sb.append(value);
		}
	}

	private void _appendStaticScopes(
		Map<String, String> scopedImportMapsValues, StringBundler sb) {

		boolean first = true;

		for (Map.Entry<String, String> entry :
				scopedImportMapsValues.entrySet()) {

			if (first) {
				first = false;

				if (sb.index() > 0) {
					sb.append(StringPool.COMMA);
				}
			}
			else {
				sb.append(StringPool.COMMA);
			}

			sb.append(StringPool.QUOTE);
			sb.append(entry.getKey());
			sb.append("\": ");
			sb.append(entry.getValue());
		}
	}

	private Map<Long, DynamicJSImportMapsContributor>
		_getDynamicJSImportMapsContributors(Long companyId) {

		Map<Long, DynamicJSImportMapsContributor>
			dynamicJSImportMapsContributors =
				_dynamicJSImportMapsContributorsMap.get(companyId);

		if (dynamicJSImportMapsContributors != null) {
			return dynamicJSImportMapsContributors;
		}

		_dynamicJSImportMapsContributorsMap.putIfAbsent(
			companyId, new ConcurrentHashMap<>());

		return _dynamicJSImportMapsContributorsMap.get(companyId);
	}

	private Map<Long, String> _getGlobalImportMapsValues(Long companyId) {
		Map<Long, String> globalImportMapsValues1 =
			_globalImportMapsValuesMap.get(companyId);

		if (globalImportMapsValues1 != null) {
			return globalImportMapsValues1;
		}

		_globalImportMapsValuesMap.putIfAbsent(
			companyId, new ConcurrentHashMap<>());

		return _globalImportMapsValuesMap.get(companyId);
	}

	private Map<String, String> _getScopedImportMapsValues(Long companyId) {
		Map<String, String> scopedImportMapsValues1 =
			_scopedImportMapsValuesMap.get(companyId);

		if (scopedImportMapsValues1 != null) {
			return scopedImportMapsValues1;
		}

		_scopedImportMapsValuesMap.putIfAbsent(
			companyId, new ConcurrentHashMap<>());

		return _scopedImportMapsValuesMap.get(companyId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSImportMapsCache.class);

	private final Map<Long, Map<Long, DynamicJSImportMapsContributor>>
		_dynamicJSImportMapsContributorsMap = new ConcurrentHashMap<>();
	private final Map<Long, Map<Long, String>> _globalImportMapsValuesMap =
		new ConcurrentHashMap<>();
	private final AtomicLong _nextId = new AtomicLong();
	private final Portal _portal;
	private final Map<Long, Map<String, String>> _scopedImportMapsValuesMap =
		new ConcurrentHashMap<>();

}