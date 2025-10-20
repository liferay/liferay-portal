/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_3;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Mariano Álvaro Sáiz
 */
public class QuartzDBPartitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (PortalUtil.getDefaultCompanyId() !=
				CompanyThreadLocal.getCompanyId()) {

			return;
		}

		for (String createIndexSQLStatement : _CREATE_INDEX_SQL_STATEMENTS) {
			_addIndex(createIndexSQLStatement);
		}
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		return !PropsValues.DATABASE_PARTITION_ENABLED;
	}

	private void _addIndex(String createIndexSQLStatement) throws Exception {
		String[] parts = StringUtil.split(
			createIndexSQLStatement, CharPool.SPACE);

		if (!hasIndex(parts[4], parts[2])) {
			runSQL(createIndexSQLStatement);
		}
	}

	private static final String[] _CREATE_INDEX_SQL_STATEMENTS = {
		"create index IX_339E078M on QUARTZ_FIRED_TRIGGERS (SCHED_NAME, " +
			"INSTANCE_NAME, REQUESTS_RECOVERY);",
		"create index IX_BC2F03B0 on QUARTZ_FIRED_TRIGGERS (SCHED_NAME, " +
			"JOB_GROUP);",
		"create index IX_5005E3AF on QUARTZ_FIRED_TRIGGERS (SCHED_NAME, " +
			"JOB_NAME, JOB_GROUP);",
		"create index IX_4BD722BM on QUARTZ_FIRED_TRIGGERS (SCHED_NAME, " +
			"TRIGGER_GROUP);",
		"create index IX_BE3835E5 on QUARTZ_FIRED_TRIGGERS (SCHED_NAME, " +
			"TRIGGER_NAME, TRIGGER_GROUP);",
		"create index IX_88328984 on QUARTZ_JOB_DETAILS (SCHED_NAME, " +
			"JOB_GROUP);",
		"create index IX_779BCA37 on QUARTZ_JOB_DETAILS (SCHED_NAME, " +
			"REQUESTS_RECOVERY);",
		"create index IX_CD7132D0 on QUARTZ_TRIGGERS (SCHED_NAME, " +
			"CALENDAR_NAME);",
		"create index IX_8AA50BE1 on QUARTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);",
		"create index IX_A85822A0 on QUARTZ_TRIGGERS (SCHED_NAME, JOB_NAME, " +
			"JOB_GROUP);",
		"create index IX_1F92813C on QUARTZ_TRIGGERS (SCHED_NAME, " +
			"NEXT_FIRE_TIME, MISFIRE_INSTR);",
		"create index IX_F2DD7C7E on QUARTZ_TRIGGERS (SCHED_NAME, " +
			"NEXT_FIRE_TIME, TRIGGER_STATE, MISFIRE_INSTR);",
		"create index IX_91CA7CCE on QUARTZ_TRIGGERS (SCHED_NAME, " +
			"TRIGGER_GROUP, NEXT_FIRE_TIME, TRIGGER_STATE, MISFIRE_INSTR);",
		"create index IX_D219AFDE on QUARTZ_TRIGGERS (SCHED_NAME, " +
			"TRIGGER_GROUP, TRIGGER_STATE);",
		"create index IX_99108B6E on QUARTZ_TRIGGERS (SCHED_NAME, " +
			"TRIGGER_STATE);"
	};

}