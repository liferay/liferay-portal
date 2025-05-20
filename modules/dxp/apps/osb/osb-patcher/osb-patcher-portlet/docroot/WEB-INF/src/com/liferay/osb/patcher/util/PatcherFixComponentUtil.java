/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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