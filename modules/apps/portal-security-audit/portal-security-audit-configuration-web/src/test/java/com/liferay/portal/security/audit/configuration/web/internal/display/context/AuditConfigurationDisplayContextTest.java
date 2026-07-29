/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Christian Moura
 */
public class AuditConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsAuditMessageMaxQueueSizeOverridden() {
		_testIsAuditMessageMaxQueueSizeOverridden(false);
		_testIsAuditMessageMaxQueueSizeOverridden(true);
	}

	private void _testIsAuditMessageMaxQueueSizeOverridden(boolean overridden) {
		try (MockedStatic<AuditConfigurationOverrideUtil> mockedStatic =
				Mockito.mockStatic(AuditConfigurationOverrideUtil.class)) {

			mockedStatic.when(
				() -> AuditConfigurationOverrideUtil.isOverridden(
					"auditMessageMaxQueueSize")
			).thenReturn(
				overridden
			);

			AuditConfigurationDisplayContext auditConfigurationDisplayContext =
				new AuditConfigurationDisplayContext(
					Mockito.mock(AuditConfiguration.class), true);

			Assert.assertEquals(
				overridden,
				auditConfigurationDisplayContext.
					isAuditMessageMaxQueueSizeOverridden());

			if (overridden) {
				Assert.assertEquals(
					"this-field-has-been-set-by-a-portal-property-and-cannot-" +
						"be-changed-here",
					auditConfigurationDisplayContext.
						getAuditMessageMaxQueueSizeHelpMessage());
			}
			else {
				Assert.assertEquals(
					StringPool.BLANK,
					auditConfigurationDisplayContext.
						getAuditMessageMaxQueueSizeHelpMessage());
			}
		}
	}

}