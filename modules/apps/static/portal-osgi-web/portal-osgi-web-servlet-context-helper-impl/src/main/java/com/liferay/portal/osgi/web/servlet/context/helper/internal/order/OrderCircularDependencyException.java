/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.internal.order;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.order.Order;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Vernon Singleton
 * @author Juan González
 */
public class OrderCircularDependencyException extends Exception {

	public OrderCircularDependencyException(
		Order.Path path, List<WebXMLDefinition> webXMLDefinitions) {

		super(_getMessage(path, webXMLDefinitions));
	}

	private static String _getMessage(
		Order.Path path, List<WebXMLDefinition> webXMLDefinitions) {

		StringBundler sb = new StringBundler();

		sb.append("Circular dependencies detected when traversing ");
		sb.append(path.name());
		sb.append(" declarations:");

		for (WebXMLDefinition webXMLDefinition : webXMLDefinitions) {
			Order order = webXMLDefinition.getOrder();

			EnumMap<Order.Path, String[]> routes = order.getRoutes();

			String[] names = routes.get(path);

			if (names.length == 0) {
				continue;
			}

			sb.append("\n");
			sb.append(webXMLDefinition.getFragmentName());
			sb.append(" ");
			sb.append(path.name());
			sb.append(": ");
			sb.append(Arrays.asList(names));
		}

		return sb.toString();
	}

}