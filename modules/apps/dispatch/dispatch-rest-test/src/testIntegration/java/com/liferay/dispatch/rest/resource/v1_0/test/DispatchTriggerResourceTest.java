/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.rest.client.dto.v1_0.DispatchTrigger;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nilton Vieira
 */
@RunWith(Arquillian.class)
public class DispatchTriggerResourceTest
	extends BaseDispatchTriggerResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDispatchTriggersPage() throws Exception {
		super.testGraphQLGetDispatchTriggersPage();
	}

	@Override
	@Test
	public void testPostDispatchTrigger() throws Exception {
		_addDispatchTrigger(randomDispatchTrigger());
	}

	@Override
	@Test
	public void testPostDispatchTriggerRun() throws Exception {
		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			randomDispatchTrigger());

		dispatchTriggerResource.postDispatchTriggerRun(dispatchTrigger.getId());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"dispatchTaskExecutorType", "name"};
	}

	@Override
	protected DispatchTrigger randomDispatchTrigger() throws Exception {
		return new DispatchTrigger() {
			{
				active = RandomTestUtil.randomBoolean();
				companyId = RandomTestUtil.randomLong();
				cronExpression = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dispatchTaskClusterMode = RandomTestUtil.randomInt();
				dispatchTaskExecutorType = SetUtil.randomElement(
					_dispatchTaskExecutorRegistry.
						getDispatchTaskExecutorTypes());
				dispatchTaskSettings = LocalizedMapUtil.getI18nMap(
					RandomTestUtil.randomLocaleStringMap());
				endDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				overlapAllowed = RandomTestUtil.randomBoolean();
				startDate = RandomTestUtil.nextDate();
				system = RandomTestUtil.randomBoolean();
				timeZoneId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userId = RandomTestUtil.randomLong();
			}
		};
	}

	@Override
	protected DispatchTrigger testGetDispatchTriggersPage_addDispatchTrigger(
			DispatchTrigger dispatchTrigger)
		throws Exception {

		return _addDispatchTrigger(dispatchTrigger);
	}

	private DispatchTrigger _addDispatchTrigger(DispatchTrigger dispatchTrigger)
		throws Exception {

		return dispatchTriggerResource.postDispatchTrigger(dispatchTrigger);
	}

	@Inject
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

}