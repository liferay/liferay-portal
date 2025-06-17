/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author André de Oliveira
 */
public class ModelPreFilterContributorsRegistry {

	public ModelPreFilterContributorsRegistry(
		ServiceTrackerMap<String, List<ModelPreFilterContributor>>
			classNameServiceTrackerMap,
		ServiceTrackerMap<String, List<ModelPreFilterContributor>>
			mandatoryServiceTrackerMap) {

		_classNameServiceTrackerMap = classNameServiceTrackerMap;
		_mandatoryServiceTrackerMap = mandatoryServiceTrackerMap;
	}

	public List<ModelPreFilterContributor> filterModelPreFilterContributor(
		String entryClassName, Collection<String> excludes,
		Collection<String> includes, boolean mandatoryOnly) {

		List<ModelPreFilterContributor> modelPreFilterContributors =
			new ArrayList<>();

		_addAll(modelPreFilterContributors, _getAllClassesContributors());
		_addAll(
			modelPreFilterContributors, _getClassContributors(entryClassName));

		if ((excludes != null) && (excludes.size() == 1) &&
			excludes.contains(StringPool.STAR)) {

			mandatoryOnly = true;
		}

		if (mandatoryOnly) {
			_retainAll(modelPreFilterContributors, _getMandatoryContributors());
		}
		else {
			List<String> mandatoryContributorClassNames =
				_getMandatoryContributorNames(_getMandatoryContributors());

			if ((includes != null) && !includes.isEmpty() &&
				!((includes.size() == 1) &&
				  includes.contains(StringPool.STAR))) {

				modelPreFilterContributors.removeIf(
					modelPreFilterContributor -> {
						String className = _getClassName(
							modelPreFilterContributor);

						return !mandatoryContributorClassNames.contains(
							className) &&
							   !includes.contains(className);
					});
			}

			if ((excludes != null) && !excludes.isEmpty()) {
				modelPreFilterContributors.removeIf(
					modelPreFilterContributor -> {
						String className = _getClassName(
							modelPreFilterContributor);

						return !mandatoryContributorClassNames.contains(
							className) &&
							   excludes.contains(className);
					});
			}
		}

		return modelPreFilterContributors;
	}

	private void _addAll(
		List<ModelPreFilterContributor> list1,
		List<ModelPreFilterContributor> list2) {

		if (list2 == null) {
			return;
		}

		list1.addAll(list2);
	}

	private List<ModelPreFilterContributor> _getAllClassesContributors() {
		return _classNameServiceTrackerMap.getService("ALL");
	}

	private List<ModelPreFilterContributor> _getClassContributors(
		String entryClassName) {

		return _classNameServiceTrackerMap.getService(entryClassName);
	}

	private String _getClassName(Object object) {
		Class<?> clazz = object.getClass();

		return clazz.getName();
	}

	private List<String> _getMandatoryContributorNames(
		List<ModelPreFilterContributor> mandatoryContributors) {

		return TransformUtil.transform(
			mandatoryContributors,
			modelPreFilterContributor -> _getClassName(
				modelPreFilterContributor));
	}

	private List<ModelPreFilterContributor> _getMandatoryContributors() {
		return _mandatoryServiceTrackerMap.getService("true");
	}

	private void _retainAll(
		List<ModelPreFilterContributor> list1,
		List<ModelPreFilterContributor> list2) {

		if (list2 == null) {
			return;
		}

		list1.retainAll(list2);
	}

	private final ServiceTrackerMap<String, List<ModelPreFilterContributor>>
		_classNameServiceTrackerMap;
	private final ServiceTrackerMap<String, List<ModelPreFilterContributor>>
		_mandatoryServiceTrackerMap;

}