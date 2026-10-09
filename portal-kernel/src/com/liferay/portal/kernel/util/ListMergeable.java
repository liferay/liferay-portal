/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class ListMergeable<T>
	implements Mergeable<ListMergeable<T>>, Serializable {

	public void add(T t) {
		_list.add(t);
	}

	public boolean contains(T t) {
		return _list.contains(t);
	}

	@Override
	public ListMergeable<T> merge(ListMergeable<T> listMergeable) {
		if ((listMergeable == null) || (listMergeable == this)) {
			return this;
		}

		for (T t : listMergeable._list) {
			if (_list.contains(t)) {
				continue;
			}

			_list.add(t);
		}

		return this;
	}

	public String mergeToString(String delimiter) {
		return StringUtil.merge(_list, delimiter);
	}

	@Override
	public ListMergeable<T> split() {
		return new ListMergeable<>();
	}

	private static final long serialVersionUID = 1L;

	private final List<T> _list = new ArrayList<>();

}