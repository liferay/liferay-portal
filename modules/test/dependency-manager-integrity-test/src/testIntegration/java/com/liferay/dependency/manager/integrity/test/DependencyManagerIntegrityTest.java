/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dependency.manager.integrity.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.ComponentDeclaration;
import org.apache.felix.dm.ComponentDependencyDeclaration;
import org.apache.felix.dm.DependencyManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class DependencyManagerIntegrityTest {

	@Test
	public void testDependencyManagerIntegrity() throws IOException {
		List<ComponentDependencyDeclaration>
			missingComponentDependencyDeclarations = new ArrayList<>();

		for (DependencyManager dependencyManager :
				DependencyManager.getDependencyManagers()) {

			for (Component component : dependencyManager.getComponents()) {
				ComponentDeclaration componentDeclaration =
					(ComponentDeclaration)component;

				if (componentDeclaration.getState() !=
						ComponentDeclaration.STATE_UNREGISTERED) {

					continue;
				}

				for (ComponentDependencyDeclaration
						componentDependencyDeclaration :
							componentDeclaration.getComponentDependencies()) {

					if (componentDependencyDeclaration.getState() !=
							ComponentDependencyDeclaration.
								STATE_UNAVAILABLE_REQUIRED) {

						continue;
					}

					missingComponentDependencyDeclarations.add(
						componentDependencyDeclaration);
				}
			}
		}

		Assert.assertTrue(
			"Missing dependencies " + missingComponentDependencyDeclarations,
			missingComponentDependencyDeclarations.isEmpty());
	}

}