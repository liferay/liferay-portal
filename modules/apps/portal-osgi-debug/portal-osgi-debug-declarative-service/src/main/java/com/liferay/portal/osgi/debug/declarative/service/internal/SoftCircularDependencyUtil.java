/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.debug.declarative.service.internal;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;

/**
 * @author Shuyang Zhou
 */
public class SoftCircularDependencyUtil {

	public static String listSoftCircularDependencies(
		ServiceComponentRuntime serviceComponentRuntime,
		BundleContext bundleContext) {

		Collection<ComponentDescriptionDTO> componentDescriptionDTOs =
			serviceComponentRuntime.getComponentDescriptionDTOs(
				bundleContext.getBundles());

		Map<ComponentDescriptionKey, ComponentDescriptionDTO>
			componentDescriptionDTOIndexMap = _toIndexMap(
				componentDescriptionDTOs);

		Map<ComponentDescriptionDTO, List<Dependency>> closureNavigationMap =
			_createClosureNavigationMap(
				bundleContext, componentDescriptionDTOs,
				componentDescriptionDTOIndexMap);

		Set<List<Dependency>> circularDependencies =
			_findSoftCircularDependencies(closureNavigationMap);

		StringBundler sb = new StringBundler();

		for (List<Dependency> dependencies : circularDependencies) {
			sb.append(StringPool.OPEN_CURLY_BRACE);

			for (Dependency dependency : dependencies) {
				dependency.appendToChain(sb);
			}

			sb.append("(circular reference)}\n");
		}

		return sb.toString();
	}

	private static Map<ComponentDescriptionDTO, List<Dependency>>
		_createClosureNavigationMap(
			BundleContext bundleContext,
			Collection<ComponentDescriptionDTO> componentDescriptionDTOs,
			Map<ComponentDescriptionKey, ComponentDescriptionDTO>
				componentDescriptionDTOIndexMap) {

		Set<Dependency> fullyVisitedDependencies = new HashSet<>();

		Map<ComponentDescriptionDTO, List<Dependency>> closureNavigationMap =
			new TreeMap<>(_comparator);

		while (true) {
			int size = componentDescriptionDTOs.size();

			Iterator<ComponentDescriptionDTO> iterator =
				componentDescriptionDTOs.iterator();

			while (iterator.hasNext()) {
				ComponentDescriptionDTO fromComponentDescriptionDTO =
					iterator.next();

				List<Dependency> dependencies = new LinkedList<>();

				for (ReferenceDTO referenceDTO :
						fromComponentDescriptionDTO.references) {

					ServiceReference<?>[] serviceReferences = null;

					try {
						serviceReferences = bundleContext.getServiceReferences(
							referenceDTO.interfaceName, referenceDTO.target);
					}
					catch (InvalidSyntaxException invalidSyntaxException) {
						String reference = referenceDTO.bind;

						if (reference == null) {
							reference = referenceDTO.field;
						}

						_log.error(
							StringBundler.concat(
								"Invalid filter \"", referenceDTO.target,
								"\" from",
								fromComponentDescriptionDTO.implementationClass,
								"[", reference, "]"),
							invalidSyntaxException);

						continue;
					}

					if (serviceReferences == null) {
						continue;
					}

					for (ServiceReference<?> serviceReference :
							serviceReferences) {

						Object service = bundleContext.getService(
							serviceReference);

						if (service == null) {
							continue;
						}

						Bundle bundle = serviceReference.getBundle();

						if (bundle == null) {
							continue;
						}

						Class<?> clazz = service.getClass();

						bundleContext.ungetService(serviceReference);

						ComponentDescriptionDTO toComponentDescriptionDTO =
							componentDescriptionDTOIndexMap.get(
								new ComponentDescriptionKey(
									bundle.getBundleId(), clazz.getName()));

						if (toComponentDescriptionDTO == null) {
							continue;
						}

						dependencies.add(
							new Dependency(
								fromComponentDescriptionDTO,
								toComponentDescriptionDTO, referenceDTO));
					}
				}

				if (fullyVisitedDependencies.containsAll(dependencies)) {
					fullyVisitedDependencies.add(
						new Dependency(
							null, fromComponentDescriptionDTO, null));

					iterator.remove();

					closureNavigationMap.remove(fromComponentDescriptionDTO);
				}
				else {
					closureNavigationMap.put(
						fromComponentDescriptionDTO, dependencies);
				}
			}

			if (size == componentDescriptionDTOs.size()) {
				break;
			}
		}

		for (List<Dependency> dependencies : closureNavigationMap.values()) {
			Iterator<Dependency> iterator = dependencies.iterator();

			while (iterator.hasNext()) {
				Dependency dependency = iterator.next();

				if (closureNavigationMap.containsKey(
						dependency.getToComponentDescriptionDTO())) {

					continue;
				}

				iterator.remove();
			}
		}

		return closureNavigationMap;
	}

	private static Set<List<Dependency>> _findSoftCircularDependencies(
		Map<ComponentDescriptionDTO, List<Dependency>> closureNavigationMap) {

		Set<List<Dependency>> softCircularDependencies = new LinkedHashSet<>();

		Set<Dependency> excludedDependencies = new HashSet<>();

		for (Map.Entry<ComponentDescriptionDTO, List<Dependency>> entry :
				closureNavigationMap.entrySet()) {

			Dependency startDependency = new Dependency(
				null, entry.getKey(), null);

			if (excludedDependencies.contains(startDependency)) {
				continue;
			}

			List<Dependency> dependencies = new ArrayList<>();

			dependencies.add(startDependency);

			Deque<List<Dependency>> backTrace = new LinkedList<>();

			backTrace.push(dependencies);

			while (!backTrace.isEmpty()) {
				LinkedList<Dependency> trace = new LinkedList<>();

				for (List<Dependency> curDependencies : backTrace) {
					trace.addFirst(curDependencies.get(0));
				}

				Dependency dependency = trace.getLast();

				List<Dependency> nextDependencies = new ArrayList<>(
					closureNavigationMap.get(
						dependency.getToComponentDescriptionDTO()));

				nextDependencies.removeAll(excludedDependencies);

				if (nextDependencies.isEmpty()) {
					List<Dependency> topDependencies = null;

					while ((topDependencies = backTrace.poll()) != null) {
						if (topDependencies.size() > 1) {
							backTrace.push(
								topDependencies.subList(
									1, topDependencies.size()));

							break;
						}
					}
				}
				else {
					dependency = nextDependencies.get(0);

					int index = trace.indexOf(dependency);

					if (index == -1) {
						backTrace.push(nextDependencies);
					}
					else {
						if (nextDependencies.size() > 1) {
							backTrace.push(
								nextDependencies.subList(
									1, nextDependencies.size()));
						}
						else {
							List<Dependency> topDependencies = null;

							while ((topDependencies = backTrace.poll()) !=
										null) {

								if (topDependencies.size() > 1) {
									backTrace.push(
										topDependencies.subList(
											1, topDependencies.size()));

									break;
								}
							}
						}

						List<Dependency> newTrace = trace.subList(
							index + 1, trace.size());

						newTrace.add(dependency);

						excludedDependencies.addAll(newTrace);

						if (newTrace.size() > 1) {
							softCircularDependencies.add(newTrace);
						}
					}
				}
			}
		}

		return softCircularDependencies;
	}

	private static Map<ComponentDescriptionKey, ComponentDescriptionDTO>
		_toIndexMap(
			Collection<ComponentDescriptionDTO> componentDescriptionDTOs) {

		Map<ComponentDescriptionKey, ComponentDescriptionDTO>
			componentDescriptionDTOIndexMap = new HashMap<>();

		for (ComponentDescriptionDTO componentDescriptionDTO :
				componentDescriptionDTOs) {

			componentDescriptionDTOIndexMap.put(
				new ComponentDescriptionKey(componentDescriptionDTO),
				componentDescriptionDTO);
		}

		return componentDescriptionDTOIndexMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SoftCircularDependencyUtil.class);

	private static final Comparator<ComponentDescriptionDTO> _comparator =
		(componentDescriptionDTO1, componentDescriptionDTO2) -> {
			String name = componentDescriptionDTO1.name;

			return name.compareTo(componentDescriptionDTO2.name);
		};

	private static class ComponentDescriptionKey {

		@Override
		public boolean equals(Object object) {
			ComponentDescriptionKey componentDescriptionKey =
				(ComponentDescriptionKey)object;

			if ((_bundleId == componentDescriptionKey._bundleId) &&
				Objects.equals(
					_implementationClass,
					componentDescriptionKey._implementationClass)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = HashUtil.hash(0, _bundleId);

			return HashUtil.hash(hash, _implementationClass);
		}

		private ComponentDescriptionKey(
			ComponentDescriptionDTO componentDescriptionDTO) {

			BundleDTO bundleDTO = componentDescriptionDTO.bundle;

			_bundleId = bundleDTO.id;

			_implementationClass = componentDescriptionDTO.implementationClass;
		}

		private ComponentDescriptionKey(
			long bundleId, String implementationClass) {

			_bundleId = bundleId;
			_implementationClass = implementationClass;
		}

		private final long _bundleId;
		private final String _implementationClass;

	}

	private static class Dependency implements Comparable<Dependency> {

		public void appendToChain(StringBundler sb) {
			sb.append(_fromComponentDescriptionDTO.implementationClass);

			sb.append(StringPool.OPEN_BRACKET);

			if (_referenceDTO.bind != null) {
				sb.append(_referenceDTO.bind);
			}

			if (_referenceDTO.field != null) {
				sb.append(_referenceDTO.field);
			}

			sb.append("] -> ");
		}

		@Override
		public int compareTo(Dependency dependency) {
			return _comparator.compare(
				_toComponentDescriptionDTO,
				dependency._toComponentDescriptionDTO);
		}

		@Override
		public boolean equals(Object object) {
			Dependency dependency = (Dependency)object;

			if (_toComponentDescriptionDTO ==
					dependency._toComponentDescriptionDTO) {

				return true;
			}

			return false;
		}

		public ComponentDescriptionDTO getFromComponentDescriptionDTO() {
			return _fromComponentDescriptionDTO;
		}

		public ReferenceDTO getReferenceDTO() {
			return _referenceDTO;
		}

		public ComponentDescriptionDTO getToComponentDescriptionDTO() {
			return _toComponentDescriptionDTO;
		}

		@Override
		public int hashCode() {
			return _toComponentDescriptionDTO.hashCode();
		}

		@Override
		public String toString() {
			StringBundler sb = new StringBundler(6);

			if (_fromComponentDescriptionDTO != null) {
				sb.append(_fromComponentDescriptionDTO.implementationClass);
			}

			if (_referenceDTO != null) {
				sb.append(StringPool.OPEN_BRACKET);

				if (_referenceDTO.bind != null) {
					sb.append(_referenceDTO.bind);
				}

				if (_referenceDTO.field != null) {
					sb.append(_referenceDTO.field);
				}

				sb.append("] -> ");
			}

			sb.append(_toComponentDescriptionDTO.implementationClass);

			return sb.toString();
		}

		private Dependency(
			ComponentDescriptionDTO fromComponentDescriptionDTO,
			ComponentDescriptionDTO toComponentDescriptionDTO,
			ReferenceDTO referenceDTO) {

			_fromComponentDescriptionDTO = fromComponentDescriptionDTO;
			_toComponentDescriptionDTO = toComponentDescriptionDTO;
			_referenceDTO = referenceDTO;
		}

		private final ComponentDescriptionDTO _fromComponentDescriptionDTO;
		private final ReferenceDTO _referenceDTO;
		private final ComponentDescriptionDTO _toComponentDescriptionDTO;

	}

}