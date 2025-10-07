/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarity.solution;

import com.liferay.client.extension.util.spring.boot3.ClientExtensionUtilSpringBootComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author Mumen Tayyem
 * @author Brian Wing Shun Chan
 */
@Import(ClientExtensionUtilSpringBootComponentScan.class)
@SpringBootApplication
public class ClaritySolutionSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaritySolutionSpringBootApplication.class, args);
	}

}