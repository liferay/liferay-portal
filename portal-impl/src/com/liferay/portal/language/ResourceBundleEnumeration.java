/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class ResourceBundleEnumeration implements Enumeration<String> {

	public ResourceBundleEnumeration(
		Set<String> set, Enumeration<String> enumeration) {

		_set = set;
		_enumeration = enumeration;

		_iterator = set.iterator();
	}

	@Override
	public boolean hasMoreElements() {
		if (_nextElement == null) {
			if (_iterator.hasNext()) {
				_nextElement = _iterator.next();
			}
			else if (_enumeration != null) {
				while ((_nextElement == null) &&
					   _enumeration.hasMoreElements()) {

					_nextElement = _enumeration.nextElement();

					if (!_set.contains(_nextElement)) {
						continue;
					}

					_nextElement = null;
				}
			}
		}

		if (_nextElement != null) {
			return true;
		}

		return false;
	}

	@Override
	public String nextElement() {
		if (hasMoreElements()) {
			String nextElement = _nextElement;

			_nextElement = null;

			return nextElement;
		}

		throw new NoSuchElementException();
	}

	private final Enumeration<String> _enumeration;
	private final Iterator<String> _iterator;
	private String _nextElement;
	private final Set<String> _set;

}