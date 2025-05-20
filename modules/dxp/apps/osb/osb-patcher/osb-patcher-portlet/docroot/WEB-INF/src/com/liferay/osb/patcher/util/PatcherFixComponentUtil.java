/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;

import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixComponentUtil {

	public static PatcherFixComponent fetchPatcherFixComponent(String name)
		throws Exception {

		AlloyServiceInvoker patcherFixComponentAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherFixComponent.class.getName());

		List<PatcherFixComponent> patcherFixComponents =
			patcherFixComponentAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"name", name});

		if (!patcherFixComponents.isEmpty()) {
			return patcherFixComponents.get(0);
		}

		return null;
	}

	public static List<PatcherFixComponent> getPatcherFixComponents(
			String columnName, boolean columnAscending)
		throws Exception {

		List<PatcherFixComponent> patcherFixComponents =
			PatcherFixComponentLocalServiceUtil.getPatcherFixComponents(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(
			PatcherFixComponentModelImpl.TABLE_NAME, columnName,
			columnAscending);

		return ListUtil.sort(patcherFixComponents, obc);
	}

}