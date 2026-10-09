/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.debug.internal.osgi.commands;

import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.portal.kernel.util.SystemCheckerUtil;
import com.liferay.portal.kernel.util.URLUtil;

import java.io.IOException;

import java.net.URL;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(
	property = {
		"osgi.command.function=check", "osgi.command.function=dc",
		"osgi.command.function=idc", "osgi.command.function=listCapabilities",
		"osgi.command.function=listSPIProviders", "osgi.command.scope=system"
	},
	service = OSGiCommands.class
)
public class SystemOSGiCommands implements OSGiCommands {

	public void check() {
		SystemCheckerUtil.runSystemCheckers(
			System.out::println, System.out::println);
	}

	public void dc(long bundleId, long... additionalBundleIds) {
		List<Bundle> bundles = new ArrayList<>();

		bundles.add(_bundleContext.getBundle(bundleId));

		for (long additionalBundleId : additionalBundleIds) {
			bundles.add(_bundleContext.getBundle(additionalBundleId));
		}

		System.out.println(_frameworkWiring.getDependencyClosure(bundles));
	}

	public void idc(
		boolean runtime, long bundleId, long... additionalBundleIds) {

		Queue<Bundle> queue = new LinkedList<>();

		queue.add(_bundleContext.getBundle(bundleId));

		for (long additionalBundleId : additionalBundleIds) {
			queue.add(_bundleContext.getBundle(additionalBundleId));
		}

		Set<Bundle> invertDependencyClosureBundles = Collections.newSetFromMap(
			new TreeMap<>());

		Bundle currentBundle = null;

		while ((currentBundle = queue.poll()) != null) {
			BundleWiring bundleWiring = currentBundle.adapt(BundleWiring.class);

			for (BundleWire bundleWire : bundleWiring.getRequiredWires(null)) {
				BundleWiring providerBundleWiring =
					bundleWire.getProviderWiring();

				Bundle providerBundle = providerBundleWiring.getBundle();

				if (!invertDependencyClosureBundles.add(providerBundle)) {
					continue;
				}

				queue.add(providerBundle);
			}
		}

		if (runtime) {
			invertDependencyClosureBundles.addAll(
				_frameworkWiring.getDependencyClosure(
					invertDependencyClosureBundles));
		}

		System.out.println(invertDependencyClosureBundles);
	}

	public void listCapabilities(long bundleId, String... namespaces) {
		Bundle bundle = _bundleContext.getBundle(bundleId);

		if (bundle == null) {
			System.out.println("Invalid bundle ID: " + bundleId);

			return;
		}

		if (namespaces.length == 0) {
			namespaces = new String[] {null};
		}

		Map<String, Map.Entry<Set<BundleCapability>, Set<BundleRequirement>>>
			map = new TreeMap<>();

		for (String namespace : namespaces) {
			_collectCapabilities(bundle, map, namespace);
		}

		_listCapabilities(map);
	}

	public void listCapabilities(String... namespaces) {
		if (namespaces.length == 0) {
			namespaces = new String[] {null};
		}

		Map<String, Map.Entry<Set<BundleCapability>, Set<BundleRequirement>>>
			map = new TreeMap<>();

		Bundle[] bundles = _bundleContext.getBundles();

		for (String namespace : namespaces) {
			for (Bundle bundle : bundles) {
				_collectCapabilities(bundle, map, namespace);
			}
		}

		_listCapabilities(map);
	}

	public void listSPIProviders(long bundleId, String... spiTypes)
		throws IOException {

		Bundle bundle = _bundleContext.getBundle(bundleId);

		if (bundle == null) {
			System.out.println("Invalid bundle ID: " + bundleId);

			return;
		}

		_listSPIProviders(
			bundle, bundle.findEntries("/META-INF/services/", null, true),
			new HashSet<>(Arrays.asList(spiTypes)));
	}

	public void listSPIProviders(String... spiTypes) throws IOException {
		Set<String> spiTypesSet = new HashSet<>(Arrays.asList(spiTypes));

		for (Bundle bundle : _bundleContext.getBundles()) {
			Enumeration<URL> enumeration = bundle.findEntries(
				"/META-INF/services/", null, true);

			if (enumeration == null) {
				continue;
			}

			_listSPIProviders(bundle, enumeration, spiTypesSet);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		Bundle bundle = bundleContext.getBundle(0);

		_frameworkWiring = bundle.adapt(FrameworkWiring.class);
	}

	private void _collectCapabilities(
		Bundle bundle,
		Map<String, Map.Entry<Set<BundleCapability>, Set<BundleRequirement>>>
			map,
		String namespace) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		for (BundleCapability bundleCapability :
				bundleWiring.getCapabilities(namespace)) {

			Map.Entry<Set<BundleCapability>, Set<BundleRequirement>> entry =
				map.computeIfAbsent(
					bundleCapability.getNamespace(),
					key -> new AbstractMap.SimpleImmutableEntry<>(
						new HashSet<>(), new HashSet<>()));

			Set<BundleCapability> bundleCapabilities = entry.getKey();

			bundleCapabilities.add(bundleCapability);
		}

		for (BundleRequirement bundleRequirement :
				bundleWiring.getRequirements(namespace)) {

			Map.Entry<Set<BundleCapability>, Set<BundleRequirement>> entry =
				map.computeIfAbsent(
					bundleRequirement.getNamespace(),
					key -> new AbstractMap.SimpleImmutableEntry<>(
						new HashSet<>(), new HashSet<>()));

			Set<BundleRequirement> bundleRequirements = entry.getValue();

			bundleRequirements.add(bundleRequirement);
		}
	}

	private void _listCapabilities(
		Map<String, Map.Entry<Set<BundleCapability>, Set<BundleRequirement>>>
			map) {

		for (Map.Entry
				<String,
				 Map.Entry<Set<BundleCapability>, Set<BundleRequirement>>>
					entry1 : map.entrySet()) {

			System.out.println("\nNamespace: " + entry1.getKey() + "\n");

			Map.Entry<Set<BundleCapability>, Set<BundleRequirement>> entry2 =
				entry1.getValue();

			Map<Bundle, List<BundleCapability>> bundleCapabilities =
				new TreeMap<>();

			for (BundleCapability bundleCapability : entry2.getKey()) {
				BundleRevision bundleRevision = bundleCapability.getRevision();

				List<BundleCapability> currentBundleCapabilities =
					bundleCapabilities.computeIfAbsent(
						bundleRevision.getBundle(), key -> new ArrayList<>());

				currentBundleCapabilities.add(bundleCapability);
			}

			System.out.println("Providers: ");

			for (Map.Entry<Bundle, List<BundleCapability>> entry3 :
					bundleCapabilities.entrySet()) {

				System.out.println("\t" + entry3.getKey());

				for (BundleCapability bundleCapability : entry3.getValue()) {
					System.out.println("\t\t" + bundleCapability);
				}
			}

			Map<Bundle, List<BundleRequirement>> bundleRequirements =
				new TreeMap<>();

			for (BundleRequirement bundleRequirement : entry2.getValue()) {
				BundleRevision bundleRevision = bundleRequirement.getRevision();

				List<BundleRequirement> currentBundleRequirements =
					bundleRequirements.computeIfAbsent(
						bundleRevision.getBundle(), key -> new ArrayList<>());

				currentBundleRequirements.add(bundleRequirement);
			}

			System.out.println("Consumers: ");

			for (Map.Entry<Bundle, List<BundleRequirement>> entry4 :
					bundleRequirements.entrySet()) {

				System.out.println("\t" + entry4.getKey());

				for (BundleRequirement bundleRequirement : entry4.getValue()) {
					System.out.println("\t\t" + bundleRequirement);
				}
			}
		}
	}

	private void _listSPIProviders(
			Bundle bundle, Enumeration<URL> enumeration,
			Set<String> spiTypesSet)
		throws IOException {

		List<URL> urls = new ArrayList<>();

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				if (spiTypesSet.isEmpty()) {
					urls.add(url);

					continue;
				}

				String path = url.getPath();

				int index = path.lastIndexOf('/');

				if (!spiTypesSet.contains(path.substring(index + 1))) {
					continue;
				}

				urls.add(url);
			}
		}

		if (!urls.isEmpty()) {
			System.out.println(bundle + ":");

			for (URL url : urls) {
				System.out.println("\t" + url);
				System.out.println("\t\t" + URLUtil.toString(url));
			}
		}
	}

	private BundleContext _bundleContext;
	private FrameworkWiring _frameworkWiring;

}