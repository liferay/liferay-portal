/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.client.extension.util.spring.boot3.ClientExtensionUtilSpringBootComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Raymond Augé
 * @author Gregory Amerson
 * @author Brian Wing Shun Chan
 */
@ComponentScan(basePackages = {"com.liferay.osb", "com.liferay.customer"})
@EnableCaching
@EnableScheduling
@Import(ClientExtensionUtilSpringBootComponentScan.class)
@SpringBootApplication
public class CustomerSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerSpringBootApplication.class, args);
	}

}