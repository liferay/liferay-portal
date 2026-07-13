/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio;

import com.liferay.client.extension.util.spring.boot3.ClientExtensionUtilSpringBootComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Kiana Suetani
 */
@EnableScheduling
@Import(ClientExtensionUtilSpringBootComponentScan.class)
@SpringBootApplication
public class SEOStudioSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SEOStudioSpringBootApplication.class, args);
	}

}