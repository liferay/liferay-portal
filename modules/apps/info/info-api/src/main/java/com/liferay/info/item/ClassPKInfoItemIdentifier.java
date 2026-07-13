/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item;

import com.liferay.info.item.provider.filter.InfoItemServiceFilter;
import com.liferay.petra.string.StringBundler;

import java.util.Objects;

/**
 * @author Jorge Ferrer
 */
public class ClassPKInfoItemIdentifier extends BaseInfoItemIdentifier {

	public static final InfoItemServiceFilter INFO_ITEM_SERVICE_FILTER =
		getInfoItemServiceFilter(ClassPKInfoItemIdentifier.class);

	public ClassPKInfoItemIdentifier(long classPK) {
		_classPK = classPK;
	}

	public ClassPKInfoItemIdentifier(long classPK, String version) {
		this(classPK);

		setVersion(version);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ClassPKInfoItemIdentifier)) {
			return false;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)object;

		return Objects.equals(_classPK, classPKInfoItemIdentifier._classPK);
	}

	public long getClassPK() {
		return _classPK;
	}

	@Override
	public InfoItemServiceFilter getInfoItemServiceFilter() {
		return INFO_ITEM_SERVICE_FILTER;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_classPK);
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{className=", ClassPKInfoItemIdentifier.class.getName(),
			", classPK=", _classPK, "}");
	}

	private final long _classPK;

}