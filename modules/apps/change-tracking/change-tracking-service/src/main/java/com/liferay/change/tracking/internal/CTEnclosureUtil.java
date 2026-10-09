/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal;

import com.liferay.change.tracking.closure.CTClosure;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Preston Crary
 */
public class CTEnclosureUtil {

	public static Map<Long, Set<Long>> getEnclosureMap(
		CTClosure ctClosure, long modelClassNameId, long modelClassPK) {

		return getEnclosureMap(
			ctClosure,
			Collections.singleton(
				new AbstractMap.SimpleImmutableEntry<>(
					modelClassNameId, Collections.singleton(modelClassPK))));
	}

	public static Map<Long, Set<Long>> getEnclosureMap(
		CTClosure ctClosure, Set<Map.Entry<Long, Set<Long>>> rootEntries) {

		Map<Long, Set<Long>> enclosureMap = new HashMap<>();

		Queue<Map.Entry<Long, ? extends Collection<Long>>> queue =
			new LinkedList<>(rootEntries);

		Map.Entry<Long, ? extends Collection<Long>> entry = null;

		while ((entry = queue.poll()) != null) {
			long classNameId = entry.getKey();

			for (long classPK : entry.getValue()) {
				Set<Long> classPKs = enclosureMap.computeIfAbsent(
					classNameId, key -> new HashSet<>());

				if (!classPKs.add(classPK)) {
					continue;
				}

				Map<Long, ? extends Collection<Long>> childPKsMap =
					ctClosure.getChildPKsMap(classNameId, classPK);

				if (childPKsMap.isEmpty()) {
					continue;
				}

				queue.addAll(childPKsMap.entrySet());
			}
		}

		return enclosureMap;
	}

	public static Set<Map.Entry<Long, Long>> getEnclosureParentEntries(
		CTClosure ctClosure, Map<Long, Set<Long>> enclosureMap) {

		Set<Map.Entry<Long, Long>> parentEntries = new HashSet<>();

		traverseParentEntries(
			ctClosure, enclosureMap,
			parentEntry -> {
				parentEntries.add(parentEntry);

				return true;
			});

		return parentEntries;
	}

	public static boolean traverseParentEntries(
		CTClosure ctClosure, Map<Long, Set<Long>> enclosureMap,
		Predicate<Map.Entry<Long, Long>> predicate) {

		Set<Map.Entry<Long, Long>> visitedParentEntries = new HashSet<>();

		Queue<Map.Entry<Long, Long>> queue = new LinkedList<>();

		for (Map.Entry<Long, Set<Long>> entry : enclosureMap.entrySet()) {
			long classNameId = entry.getKey();

			for (long classPK : entry.getValue()) {
				queue.add(
					new AbstractMap.SimpleImmutableEntry<>(
						classNameId, classPK));
			}
		}

		while (!queue.isEmpty()) {
			Map.Entry<Long, Long> entry = queue.poll();

			Map<Long, List<Long>> parentPKsMap = ctClosure.getParentPKsMap(
				entry.getKey(), entry.getValue());

			for (Map.Entry<Long, List<Long>> parentPKsEntry :
					parentPKsMap.entrySet()) {

				long parentClassNameId = parentPKsEntry.getKey();

				for (long parentClassPK : parentPKsEntry.getValue()) {
					Map.Entry<Long, Long> parentEntry =
						new AbstractMap.SimpleImmutableEntry<>(
							parentClassNameId, parentClassPK);

					if (!visitedParentEntries.add(parentEntry)) {
						continue;
					}

					Set<Long> enclosureClassPKs = enclosureMap.get(
						parentClassNameId);

					if (((enclosureClassPKs == null) ||
						 !enclosureClassPKs.contains(parentClassPK)) &&
						!predicate.test(parentEntry)) {

						return false;
					}

					queue.add(parentEntry);
				}
			}
		}

		return true;
	}

}