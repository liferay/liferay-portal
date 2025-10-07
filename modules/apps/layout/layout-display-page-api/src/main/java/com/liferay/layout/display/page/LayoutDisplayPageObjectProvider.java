/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.display.page;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Jorge Ferrer
 */
public interface LayoutDisplayPageObjectProvider<T> {

	public default String getClassName() {
		return PortalUtil.getClassName(getClassNameId());
	}

	public long getClassNameId();

	public long getClassPK();

	public long getClassTypeId();

	public String getDescription(Locale locale);

	public T getDisplayObject();

	public default String getExternalReferenceCode() {
		return StringPool.BLANK;
	}

	public long getGroupId();

	public String getKeywords(Locale locale);

	public default String getParentExternalReferenceCode() {
		return StringPool.BLANK;
	}

	public default List<LayoutDisplayPageObjectProvider<T>>
		getRelatedLayoutDisplayPageObjectProviders(String contentType) {

		return Collections.emptyList();
	}

	public default String getScopeExternalReferenceCode(long groupId) {
		if (getGroupId() == groupId) {
			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return group.getExternalReferenceCode();
	}

	public String getTitle(Locale locale);

	public String getURLTitle(Locale locale);

}