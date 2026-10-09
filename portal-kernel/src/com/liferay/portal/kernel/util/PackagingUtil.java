/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;

import java.io.File;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author Raymond Augé
 */
public class PackagingUtil {

	public static List<String> getPackagesFromPath(File file) {
		Set<String> packages = new HashSet<>();
		Stack<String> packageStack = new Stack<>();

		subPackages(file, packages, packageStack);

		List<String> list = ListUtil.fromCollection(packages);

		Collections.sort(list);

		return list;
	}

	protected static void subPackages(
		File file, Set<String> packages, Stack<String> packageStack) {

		for (File subFile : file.listFiles()) {
			if (!subFile.isDirectory()) {
				continue;
			}

			packageStack.push(subFile.getName());

			String packageName = StringUtil.merge(
				packageStack, StringPool.PERIOD);

			if (packageName.contains(StringPool.PERIOD)) {
				packages.add(packageName);
			}

			subPackages(subFile, packages, packageStack);

			packageStack.pop();
		}
	}

}