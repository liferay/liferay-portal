/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Raymond Augé
 */
@FunctionalInterface
public interface PortalK8sConfigMapModifier {

	public Result modifyConfigMap(
		Consumer<PortalK8sConfigMapModifier.ConfigMapModel>
			configMapModelConsumer,
		String configMapName);

	public interface ConfigMapModel {

		public Map<String, String> annotations();

		public Map<String, String> binaryData();

		public Map<String, String> data();

		public Map<String, String> labels();

	}

	public enum Result {

		BUFFERED, CREATED, DELETED, UNCHANGED, UPDATED

	}

}