/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend;

import com.liferay.talend.tliferayconnection.TLiferayConnectionDefinition;
import com.liferay.talend.tliferayinput.TLiferayInputDefinition;
import com.liferay.talend.tliferayoutput.TLiferayOutputDefinition;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.talend.components.service.spring.SpringTestApp;

/**
 * @author Zoltán Takács
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestApp.class)
public class SpringTLiferayInputTest extends LiferayAbstractComponentTestCase {

	@Test
	public void testComponentHasBeenRegistered() {
		assertComponentIsRegistered(
			TLiferayConnectionDefinition.class,
			TLiferayConnectionDefinition.COMPONENT_NAME);
		assertComponentIsRegistered(
			TLiferayInputDefinition.class,
			TLiferayInputDefinition.COMPONENT_NAME);
		assertComponentIsRegistered(
			TLiferayOutputDefinition.class,
			TLiferayOutputDefinition.COMPONENT_NAME);
	}

}