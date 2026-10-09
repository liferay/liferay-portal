/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.taglib.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Mergeable;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class OutputData implements Mergeable<OutputData>, Serializable {

	public void addDataSB(String outputKey, String webKey, StringBundler sb) {
		DataKey dataKey = new DataKey(outputKey, webKey);

		StringBundler mergedSB = _dataMap.get(dataKey);

		if (mergedSB == null) {
			_dataMap.put(dataKey, sb);
		}
		else {
			mergedSB.append(sb);
		}
	}

	public boolean addOutputKey(String outputKey) {
		return _outputKeys.add(outputKey);
	}

	public StringBundler getDataSB(String outputKey, String webKey) {
		DataKey dataKey = new DataKey(outputKey, webKey);

		return _dataMap.get(dataKey);
	}

	public StringBundler getMergedDataSB(String webKey) {
		StringBundler mergedSB = null;

		for (Map.Entry<DataKey, StringBundler> entry : _dataMap.entrySet()) {
			DataKey dataKey = entry.getKey();

			if (!dataKey._webKey.equals(webKey)) {
				continue;
			}

			if (mergedSB == null) {
				mergedSB = entry.getValue();
			}
			else {
				mergedSB.append(entry.getValue());
			}
		}

		return mergedSB;
	}

	public Set<String> getOutputKeys() {
		return _outputKeys;
	}

	@Override
	public OutputData merge(OutputData outputData) {
		if ((outputData == null) || (outputData == this)) {
			return this;
		}

		for (Map.Entry<DataKey, StringBundler> entry :
				outputData._dataMap.entrySet()) {

			DataKey dataKey = entry.getKey();

			String outputKey = dataKey._outputKey;

			if (_outputKeys.contains(outputKey)) {
				continue;
			}

			StringBundler sb = entry.getValue();

			StringBundler mergedSB = _dataMap.get(dataKey);

			if (mergedSB == null) {
				_dataMap.put(dataKey, sb);
			}
			else {
				mergedSB.append(sb);
			}

			if (!outputData._outputKeys.contains(outputKey)) {
				continue;
			}

			_outputKeys.add(outputKey);
		}

		return this;
	}

	public void setDataSB(String outputKey, String webKey, StringBundler sb) {
		DataKey dataKey = new DataKey(outputKey, webKey);

		_dataMap.put(dataKey, sb);
	}

	@Override
	public OutputData split() {
		return new OutputData();
	}

	private static final long serialVersionUID = 1L;

	private final Map<DataKey, StringBundler> _dataMap = new LinkedHashMap<>();
	private final Set<String> _outputKeys = new LinkedHashSet<>();

	private static class DataKey implements Serializable {

		public DataKey(String outputKey, String webKey) {
			_outputKey = GetterUtil.getString(outputKey);
			_webKey = webKey;
		}

		@Override
		public boolean equals(Object object) {
			DataKey dataKey = (DataKey)object;

			if (_outputKey.equals(dataKey._outputKey) &&
				_webKey.equals(dataKey._webKey)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return (_outputKey.hashCode() * 11) + _webKey.hashCode();
		}

		private static final long serialVersionUID = 1L;

		private final String _outputKey;
		private final String _webKey;

	}

}