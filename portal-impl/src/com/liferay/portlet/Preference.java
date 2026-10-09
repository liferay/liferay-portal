/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 */
public class Preference implements Cloneable, Serializable {

	public Preference(String name, String value) {
		this(name, new String[] {value});
	}

	public Preference(String name, String value, boolean readOnly) {
		this(name, new String[] {value}, readOnly);
	}

	public Preference(String name, String[] values) {
		this(name, values, false);
	}

	public Preference(String name, String[] values, boolean readOnly) {
		_name = name;
		_values = values;
		_readOnly = readOnly;
	}

	@Override
	public Object clone() {
		return new Preference(_name, _values, _readOnly);
	}

	public String getName() {
		return _name;
	}

	public boolean getReadOnly() {
		return _readOnly;
	}

	public String[] getValues() {
		return _values;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public void setValues(String[] values) {
		_values = values;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(6 + ((_values.length * 2) - 1));

		sb.append("{name=");
		sb.append(getName());
		sb.append(", readOnly=");
		sb.append(_readOnly);
		sb.append(", values=[");

		for (int i = 0; i < _values.length; i++) {
			sb.append(_values[i]);

			if (i >= (_values.length - 1)) {
				continue;
			}

			sb.append(StringPool.COMMA);
		}

		sb.append("]}");

		return sb.toString();
	}

	private final String _name;
	private final boolean _readOnly;
	private String[] _values;

}