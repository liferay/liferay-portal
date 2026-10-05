/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutFinderBaseImpl
	extends BasePersistenceImpl<Layout, NoSuchLayoutException> {

	public LayoutFinderBaseImpl() {
		setModelClass(Layout.class);

		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("hidden", "hidden_");
		dbColumnNames.put("system", "system_");
		dbColumnNames.put("masterLayoutPageTemplateEntryERC", "masterLPTEERC");
		dbColumnNames.put(
			"masterLayoutPageTemplateEntryScopeERC", "masterLPTESERC");
		dbColumnNames.put(
			"portletLayoutPageTemplateEntryERC", "portletLPTEERC");
		dbColumnNames.put(
			"portletLayoutPageTemplateEntryScopeERC", "portletLPTESERC");
		dbColumnNames.put(
			"portletLayoutPageTemplateEntryLinkEnabled", "portletLPTELE");

		setDBColumnNames(dbColumnNames);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return getLayoutPersistence().getBadColumnNames();
	}

	/**
	 * Returns the layout persistence.
	 *
	 * @return the layout persistence
	 */
	public LayoutPersistence getLayoutPersistence() {
		return layoutPersistence;
	}

	/**
	 * Sets the layout persistence.
	 *
	 * @param layoutPersistence the layout persistence
	 */
	public void setLayoutPersistence(LayoutPersistence layoutPersistence) {
		this.layoutPersistence = layoutPersistence;
	}

	@BeanReference(type = LayoutPersistence.class)
	protected LayoutPersistence layoutPersistence;

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutFinderBaseImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1936939330