/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Random;
import java.util.UUID;

/**
 * @author Kenji Heigel
 */
public class RandomTestUtil {

	public static double randomDouble() {
		return _random.nextDouble();
	}

	public static long randomLong() {
		return _random.nextLong();
	}

	public static String randomString() {
		UUID uuid = UUID.randomUUID();

		return uuid.toString();
	}

	private static final Random _random = new Random();

}