/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.definition.groovy.script.use;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * @author Feliphe Marinho
 */
public class WorkflowDefinitionGroovyScriptUseDetector {

	public static boolean detect(String content, JSONFactory jsonFactory)
		throws JSONException {

		Queue<Map<String, Object>> queue = new LinkedList<>();

		JSONObject jsonObject = jsonFactory.createJSONObject(content);

		queue.add(jsonObject.toMap());

		while (!queue.isEmpty()) {
			Map<String, Object> map = queue.poll();

			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if (entry.getValue() instanceof List) {
					if (Objects.equals(entry.getKey(), "#cdata-value")) {
						continue;
					}

					queue.addAll((List<Map<String, Object>>)entry.getValue());
				}
				else if (map.size() == 2) {
					if (!Objects.equals(
							map.get("#tag-name"), "script-language")) {

						continue;
					}

					if (Objects.equals(map.get("#value"), "drl") ||
						Objects.equals(map.get("#value"), "groovy") ||
						Objects.equals(map.get("#value"), "java")) {

						return true;
					}
				}
			}
		}

		return false;
	}

}