/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseHost implements Host {

	@Override
	public void cleanUpServices() {
		for (HostService hostService : _hostServices) {
			try {
				hostService.cleanUpService();
			}
			catch (Exception exception) {
				System.out.println(exception.getMessage());
			}
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	protected BaseHost(String name) {
		_name = name;

		try {
			Properties jenkinsProperties =
				JenkinsResultsParserUtil.getJenkinsProperties();

			if (!jenkinsProperties.containsKey(name)) {
				return;
			}

			String serviceNames = jenkinsProperties.getProperty(name);

			Properties buildProperties =
				JenkinsResultsParserUtil.getBuildProperties(false);

			for (String serviceId : serviceNames.split(",")) {
				String serviceCleanCommand = buildProperties.getProperty(
					"service.clean.command[" + serviceId + "]", "");
				String serviceName = buildProperties.getProperty(
					"service.name[" + serviceId + "]", "");

				if (serviceCleanCommand.isEmpty() || serviceName.isEmpty()) {
					continue;
				}

				_hostServices.add(
					new HostService(serviceName, serviceCleanCommand));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final List<HostService> _hostServices = new ArrayList<>();
	private final String _name;

}