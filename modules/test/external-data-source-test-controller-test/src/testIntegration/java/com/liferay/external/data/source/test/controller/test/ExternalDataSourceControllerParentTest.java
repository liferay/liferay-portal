/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.data.source.test.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class ExternalDataSourceControllerParentTest
	extends ExternalDataSourceControllerTest {

	@Override
	protected String getResourceDestination() {
		return "META-INF/spring/parent/ext-spring.xml";
	}

}