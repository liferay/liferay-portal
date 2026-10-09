/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.debug.spring.extender.internal;

import com.liferay.petra.string.StringBundler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.ComponentDeclaration;
import org.apache.felix.dm.ComponentDependencyDeclaration;
import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Tina Tian
 */
public class UnavailableComponentUtil {

	public static String scanUnavailableComponents() {
		StringBundler sb = new StringBundler();

		for (DependencyManager dependencyManager :
				DependencyManager.getDependencyManagers()) {

			Map<ComponentDeclaration, List<ComponentDependencyDeclaration>>
				unavailableComponentDeclarations = new HashMap<>();

			for (Component component : dependencyManager.getComponents()) {
				ComponentDeclaration componentDeclaration =
					(ComponentDeclaration)component;

				if (componentDeclaration.getState() !=
						ComponentDeclaration.STATE_UNREGISTERED) {

					continue;
				}

				List<ComponentDependencyDeclaration>
					componentDependencyDeclarations =
						unavailableComponentDeclarations.computeIfAbsent(
							componentDeclaration, key -> new ArrayList<>());

				for (ComponentDependencyDeclaration
						componentDependencyDeclaration :
							componentDeclaration.getComponentDependencies()) {

					if (componentDependencyDeclaration.getState() !=
							ComponentDependencyDeclaration.
								STATE_UNAVAILABLE_REQUIRED) {

						continue;
					}

					componentDependencyDeclarations.add(
						componentDependencyDeclaration);
				}
			}

			if (unavailableComponentDeclarations.isEmpty()) {
				continue;
			}

			BundleContext bundleContext = dependencyManager.getBundleContext();

			Bundle bundle = bundleContext.getBundle();

			sb.append("\nBundle {id: ");
			sb.append(bundle.getBundleId());
			sb.append(", name: ");
			sb.append(bundle.getSymbolicName());
			sb.append(", version: ");
			sb.append(bundle.getVersion());
			sb.append("}.\n");

			for (Map.Entry
					<ComponentDeclaration, List<ComponentDependencyDeclaration>>
						entry : unavailableComponentDeclarations.entrySet()) {

				sb.append("\tComponent with ID ");

				ComponentDeclaration componentDeclaration = entry.getKey();

				sb.append(componentDeclaration.getId());

				sb.append(" is unavailable due to missing required ");
				sb.append("dependencies:\n\t\t");

				for (ComponentDependencyDeclaration
						componentDependencyDeclaration : entry.getValue()) {

					sb.append(componentDependencyDeclaration);
					sb.append("\n\t\t");
				}

				sb.setIndex(sb.index() - 1);
			}
		}

		return sb.toString();
	}

}