/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesCompileAxisTestClassGroup extends AxisTestClassGroup {

	protected WorkspacesCompileAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		super(jsonObject, segmentTestClassGroup);
	}

	protected WorkspacesCompileAxisTestClassGroup(
		WorkspacesCompileBatchTestClassGroup
			workspacesCompileBatchTestClassGroup) {

		super(workspacesCompileBatchTestClassGroup);
	}

}