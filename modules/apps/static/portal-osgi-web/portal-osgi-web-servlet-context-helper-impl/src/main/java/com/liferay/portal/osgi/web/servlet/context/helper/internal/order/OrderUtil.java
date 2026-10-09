/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.internal.order;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.order.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Vernon Singleton
 * @author Juan González
 */
public class OrderUtil {

	public static List<WebXMLDefinition> getOrderedWebXMLDefinitions(
			List<WebXMLDefinition> webXMLDefinitions,
			List<String> absoluteOrderingNames)
		throws OrderBeforeAndAfterException, OrderCircularDependencyException,
			   OrderMaxAttemptsException {

		if (ListUtil.isEmpty(absoluteOrderingNames)) {
			return _getOrderedWebXMLDefinitions(webXMLDefinitions);
		}

		return _getOrderedWebXMLDefinitions(
			webXMLDefinitions, absoluteOrderingNames);
	}

	private static String[] _appendAndSort(String[]... namesArray) {
		Map<String, Integer> map = new HashMap<>();

		if ((namesArray[0] != null) &&
			(Arrays.binarySearch(namesArray[0], Order.OTHERS) >= 0)) {

			map.put(Order.OTHERS, 1);
		}

		for (String[] names : namesArray) {
			for (String name : names) {
				if (name.equals(Order.OTHERS)) {
					continue;
				}

				map.put(name, 1);
			}
		}

		Set<String> set = map.keySet();

		String[] orderedNames = set.toArray(new String[0]);

		Arrays.sort(orderedNames);

		return orderedNames;
	}

	private static void _checkForBothBeforeAndAfter(
			WebXMLDefinition webXMLDefinition)
		throws OrderBeforeAndAfterException {

		Order order = webXMLDefinition.getOrder();

		EnumMap<Order.Path, String[]> routes = order.getRoutes();

		Map<String, Integer> map = new HashMap<>();

		String[] beforeNames = routes.get(Order.Path.BEFORE);

		for (String beforeName : beforeNames) {
			Integer value = map.get(beforeName);

			if (value == null) {
				value = 1;
			}
			else {
				value += 1;
			}

			map.put(beforeName, value);
		}

		String[] afterNames = routes.get(Order.Path.AFTER);

		for (String afterName : afterNames) {
			Integer value = map.get(afterName);

			if (value == null) {
				value = 1;
			}
			else {
				value += 1;
			}

			map.put(afterName, value);
		}

		Set<String> set = map.keySet();

		String[] names = set.toArray(new String[0]);

		for (String name : names) {
			if (map.get(name) > 1) {
				throw new OrderBeforeAndAfterException(
					webXMLDefinition.getFragmentName(), name);
			}
		}
	}

	private static void _checkForSpecExceptions(
			List<WebXMLDefinition> webXMLDefinitions)
		throws OrderBeforeAndAfterException, OrderCircularDependencyException {

		for (WebXMLDefinition webXMLDefinition : webXMLDefinitions) {
			_checkForBothBeforeAndAfter(webXMLDefinition);

			for (Order.Path path : Order.Path.values()) {
				_mapRoutes(webXMLDefinition, path, webXMLDefinitions);
			}
		}
	}

	private static List<String> _getFragmentNames(
		WebXMLDefinition[] webXMLDefinitions) {

		List<String> fragmentNames = new LinkedList<>();

		for (WebXMLDefinition webXMLDefinition : webXMLDefinitions) {
			fragmentNames.add(webXMLDefinition.getFragmentName());
		}

		return fragmentNames;
	}

	private static List<WebXMLDefinition> _getOrderedWebXMLDefinitions(
			List<WebXMLDefinition> webXMLDefinitions)
		throws OrderBeforeAndAfterException, OrderCircularDependencyException,
			   OrderMaxAttemptsException {

		_checkForSpecExceptions(webXMLDefinitions);

		webXMLDefinitions = _preSort(webXMLDefinitions);

		WebXMLDefinition[] webXMLDefinitionsArray = webXMLDefinitions.toArray(
			new WebXMLDefinition[0]);

		_innerSort(webXMLDefinitionsArray);

		_postSort(webXMLDefinitionsArray);

		return new ArrayList<>(Arrays.asList(webXMLDefinitionsArray));
	}

	private static List<WebXMLDefinition> _getOrderedWebXMLDefinitions(
		List<WebXMLDefinition> webXMLDefinitions,
		List<String> absoluteOrderingNames) {

		List<WebXMLDefinition> orderedWebXMLDefinitions = new ArrayList<>();

		List<WebXMLDefinition> tempWebXMLDefinitions =
			new CopyOnWriteArrayList<>();

		tempWebXMLDefinitions.addAll(webXMLDefinitions);

		for (String absoluteOrderingName : absoluteOrderingNames) {
			if (Order.OTHERS.equals(absoluteOrderingName)) {
				continue;
			}

			boolean found = false;

			for (WebXMLDefinition webXMLDefinition : tempWebXMLDefinitions) {
				String fragmentName = webXMLDefinition.getFragmentName();

				if (!found && absoluteOrderingName.equals(fragmentName)) {
					found = true;

					orderedWebXMLDefinitions.add(webXMLDefinition);

					tempWebXMLDefinitions.remove(webXMLDefinition);
				}
				else if (found && absoluteOrderingName.equals(fragmentName)) {
					break;
				}
			}
		}

		int index = absoluteOrderingNames.indexOf(Order.OTHERS);

		if (index != -1) {
			for (WebXMLDefinition webXMLDefinition : tempWebXMLDefinitions) {
				orderedWebXMLDefinitions.add(index, webXMLDefinition);
			}
		}

		return orderedWebXMLDefinitions;
	}

	private static Map<String, WebXMLDefinition> _getWebXMLDefinitionsMap(
		List<WebXMLDefinition> webXMLDefinitions) {

		Map<String, WebXMLDefinition> webXMLDefinitionsMap = new HashMap<>();

		for (WebXMLDefinition webXMLDefinition : webXMLDefinitions) {
			webXMLDefinitionsMap.put(
				webXMLDefinition.getFragmentName(), webXMLDefinition);
		}

		return webXMLDefinitionsMap;
	}

	private static int _innerSort(WebXMLDefinition[] webXMLDefinitions)
		throws OrderMaxAttemptsException {

		boolean attempting = true;
		int attempts = 0;

		while (attempting) {
			if (attempts > _MAX_ATTEMPTS) {
				throw new OrderMaxAttemptsException(_MAX_ATTEMPTS);
			}

			attempting = false;

			int last = webXMLDefinitions.length - 1;

			for (int i = 0; i < webXMLDefinitions.length; i++) {
				int x = i;

				int y = x + 1;

				if (x == last) {
					y = x;

					x = 0;
				}

				if (!_isDisordered(
						webXMLDefinitions[x], webXMLDefinitions[y])) {

					continue;
				}

				WebXMLDefinition webXMLDefinition = webXMLDefinitions[x];

				webXMLDefinitions[x] = webXMLDefinitions[y];

				webXMLDefinitions[y] = webXMLDefinition;

				attempting = true;
			}

			attempts++;
		}

		return attempts;
	}

	private static boolean _isDisordered(
		WebXMLDefinition webXMLDefinition1,
		WebXMLDefinition webXMLDefinition2) {

		Order order1 = webXMLDefinition1.getOrder();
		Order order2 = webXMLDefinition2.getOrder();

		if (order1.isOrdered() && !order2.isOrdered()) {
			EnumMap<Order.Path, String[]> routes = order1.getRoutes();

			if (ArrayUtil.isNotEmpty(routes.get(Order.Path.AFTER)) &&
				!order1.isBeforeOthers()) {

				return true;
			}
		}

		if (order2.isBefore(webXMLDefinition1.getFragmentName()) ||
			order1.isAfter(webXMLDefinition2.getFragmentName())) {

			return true;
		}

		if (order1.isAfterOthers() &&
			!order1.isBefore(webXMLDefinition2.getFragmentName()) &&
			!(order1.isAfterOthers() && order2.isAfterOthers())) {

			return true;
		}

		if (order2.isBeforeOthers() &&
			!order2.isAfter(webXMLDefinition1.getFragmentName()) &&
			!(order1.isBeforeOthers() && order2.isBeforeOthers())) {

			return true;
		}

		return false;
	}

	private static void _mapRoutes(
			WebXMLDefinition webXMLDefinition, Order.Path path,
			List<WebXMLDefinition> webXMLDefinitions)
		throws OrderCircularDependencyException {

		Order order = webXMLDefinition.getOrder();

		EnumMap<Order.Path, String[]> orderRoutes = order.getRoutes();

		String[] pathNames = orderRoutes.get(path);

		for (String pathName : pathNames) {
			if (pathName.equals(Order.OTHERS)) {
				continue;
			}

			for (WebXMLDefinition curWebXMLDefinition : webXMLDefinitions) {
				if (!pathName.equals(curWebXMLDefinition.getFragmentName())) {
					continue;
				}

				Order curOrder = curWebXMLDefinition.getOrder();

				EnumMap<Order.Path, String[]> curRoutes = curOrder.getRoutes();

				String[] curPathNames = curRoutes.get(path);

				String fragmentName = webXMLDefinition.getFragmentName();

				if (Arrays.binarySearch(curPathNames, fragmentName) >= 0) {
					throw new OrderCircularDependencyException(
						path, webXMLDefinitions);
				}

				Order.Path oppositePath = null;

				if (path == Order.Path.BEFORE) {
					oppositePath = Order.Path.AFTER;
				}
				else {
					oppositePath = Order.Path.BEFORE;
				}

				String[] oppositePathNames = curRoutes.get(oppositePath);

				if (Arrays.binarySearch(oppositePathNames, fragmentName) < 0) {
					EnumMap<Order.Path, String[]> routes = new EnumMap<>(
						Order.Path.class);

					routes.put(path, curPathNames);
					routes.put(
						oppositePath,
						_appendAndSort(
							curRoutes.get(oppositePath),
							new String[] {fragmentName}));

					curOrder.setRoutes(routes);
				}

				if (ArrayUtil.isEmpty(curPathNames)) {
					continue;
				}

				EnumMap<Order.Path, String[]> routes = new EnumMap<>(
					Order.Path.class);

				routes.put(path, _appendAndSort(pathNames, curPathNames));
				routes.put(oppositePath, orderRoutes.get(oppositePath));

				order.setRoutes(routes);
			}
		}
	}

	private static void _postSort(WebXMLDefinition[] webXMLDefinitions) {
		int i = 0;

		while (i < webXMLDefinitions.length) {
			List<String> fragmentNames = _getFragmentNames(webXMLDefinitions);

			boolean done = true;

			for (int j = 0; j < webXMLDefinitions.length; j++) {
				int k = 0;

				for (String curFragmentName : fragmentNames) {
					if (curFragmentName.equals(
							webXMLDefinitions[j].getFragmentName())) {

						break;
					}

					Order order = webXMLDefinitions[j].getOrder();

					if (order.isBefore(curFragmentName)) {
						WebXMLDefinition webXMLDefinition = null;

						for (int l = 0; l < webXMLDefinitions.length; l++) {
							if (l == k) {
								webXMLDefinition = webXMLDefinitions[l];
							}

							if ((webXMLDefinition != null) && (l != j)) {
								webXMLDefinitions[l] = webXMLDefinitions[l + 1];
							}

							if (l == j) {
								webXMLDefinitions[l] = webXMLDefinition;

								done = false;

								break;
							}
						}

						if (!done) {
							break;
						}
					}

					k = k + 1;
				}
			}

			if (done) {
				break;
			}
		}
	}

	private static List<WebXMLDefinition> _preSort(
		List<WebXMLDefinition> webXMLDefinitions) {

		List<WebXMLDefinition> preSortWebXMLDefinitions = new ArrayList<>();

		Map<String, Integer> map = new LinkedHashMap<>();
		List<WebXMLDefinition> tempWebXMLDefinitions = new LinkedList<>();

		for (WebXMLDefinition webXMLDefinition : webXMLDefinitions) {
			Order order = webXMLDefinition.getOrder();

			if (Validator.isNull(webXMLDefinition.getFragmentName()) &&
				!order.isOrdered()) {

				tempWebXMLDefinitions.add(webXMLDefinition);
			}
			else {
				EnumMap<Order.Path, String[]> routes = order.getRoutes();

				String[] afterPathNames = routes.get(Order.Path.AFTER);
				String[] beforePathNames = routes.get(Order.Path.BEFORE);

				map.put(
					webXMLDefinition.getFragmentName(),
					afterPathNames.length + beforePathNames.length);
			}
		}

		map = _sortDescendingByValue(map);

		Map<String, WebXMLDefinition> webXMLDefinitionsMap =
			_getWebXMLDefinitionsMap(webXMLDefinitions);

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			preSortWebXMLDefinitions.add(
				webXMLDefinitionsMap.get(entry.getKey()));
		}

		for (WebXMLDefinition webXMLDefinition : tempWebXMLDefinitions) {
			preSortWebXMLDefinitions.add(webXMLDefinition);
		}

		return preSortWebXMLDefinitions;
	}

	private static Map<String, Integer> _sortDescendingByValue(
		Map<String, Integer> map) {

		List<Map.Entry<String, Integer>> list = new LinkedList<>(
			map.entrySet());

		Collections.sort(list, _COMPARATOR);

		Map<String, Integer> sortedMap = new LinkedHashMap<>();

		for (Map.Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private static final Comparator<Map.Entry<String, Integer>> _COMPARATOR =
		new MapEntryComparator();

	private static final int _MAX_ATTEMPTS =
		Integer.MAX_VALUE / (Byte.MAX_VALUE * Byte.MAX_VALUE * Byte.MAX_VALUE);

	private static class MapEntryComparator
		implements Comparator<Map.Entry<String, Integer>> {

		@Override
		public int compare(
			Map.Entry<String, Integer> map1, Map.Entry<String, Integer> map2) {

			Integer integer1 = map1.getValue();
			Integer integer2 = map2.getValue();

			return integer2.compareTo(integer1);
		}

	}

}