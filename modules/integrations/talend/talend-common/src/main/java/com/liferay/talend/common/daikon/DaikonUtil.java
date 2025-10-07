/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.daikon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;

/**
 * @author Igor Beslic
 */
public class DaikonUtil {

	public static List<NamedThing> toNamedThings(Map<String, String> names) {
		List<NamedThing> namedThings = new ArrayList<>();

		names.forEach(
			(key, value) -> namedThings.add(new SimpleNamedThing(value, key)));

		return namedThings;
	}

	public static List<NamedThing> toNamedThings(Set<String> names) {
		List<NamedThing> namedThings = new ArrayList<>();

		names.forEach(
			name -> namedThings.add(new SimpleNamedThing(name, name)));

		return namedThings;
	}

}