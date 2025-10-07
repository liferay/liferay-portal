/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.constants;

import com.liferay.petra.string.StringPool;

/**
 * @author Jenny Chen
 */
public interface HeatTagConstants {

	public static final String CUSTOMER_UPGRADE = "customer_upgrade";

	public static final String ESCALATION_OVERRIDE = "escalation_override";

	public static final String EVENT_14_DAYS = "event_14_days";

	public static final String EVENT_30_DAYS = "event_30_days";

	public static final String EVENT_MISSED = "event_missed";

	public static final String GO_LIVE_14_DAYS = "go_live_14_days";

	public static final String GO_LIVE_30_DAYS = "go_live_30_days";

	public static final String GO_LIVE_MISSED = "go_live_missed";

	public static final String SECURITY_EXPERIENCING_ATTACK =
		"security_experiencing_attack";

	public static final String SECURITY_REPORTED_VULNERABILITIES =
		"security_reported_vulnerabilities";

	public static final String SECURITY_SCAN_RESULT_CONCERNS =
		"security_scan_result_concerns";

	public static final String[] SUPPORT_ISSUE_LABELS = {
		CUSTOMER_UPGRADE, ESCALATION_OVERRIDE, EVENT_14_DAYS, EVENT_30_DAYS,
		EVENT_MISSED, GO_LIVE_14_DAYS, GO_LIVE_30_DAYS, GO_LIVE_MISSED,
		SECURITY_EXPERIENCING_ATTACK, SECURITY_REPORTED_VULNERABILITIES,
		SECURITY_SCAN_RESULT_CONCERNS
	};

	public static String getHeatTag(
		String businessEventType, long daysUntilTargetGoLive) {

		if (businessEventType.equals(BusinessEventConstants.TYPE_GO_LIVE)) {
			if (daysUntilTargetGoLive <= 0) {
				return GO_LIVE_MISSED;
			}
			else if ((daysUntilTargetGoLive > 0) &&
					 (daysUntilTargetGoLive <= 14)) {

				return GO_LIVE_14_DAYS;
			}
			else if ((daysUntilTargetGoLive > 14) &&
					 (daysUntilTargetGoLive <= 30)) {

				return GO_LIVE_30_DAYS;
			}
		}
		else if (businessEventType.equals(
					BusinessEventConstants.TYPE_MIGRATION) ||
				 businessEventType.equals(
					 BusinessEventConstants.TYPE_OTHER_EVENT)) {

			if (daysUntilTargetGoLive <= 0) {
				return EVENT_MISSED;
			}
			else if ((daysUntilTargetGoLive > 0) &&
					 (daysUntilTargetGoLive <= 14)) {

				return EVENT_14_DAYS;
			}
			else if ((daysUntilTargetGoLive > 14) &&
					 (daysUntilTargetGoLive <= 30)) {

				return EVENT_30_DAYS;
			}
		}
		else if (businessEventType.equals(
					BusinessEventConstants.TYPE_UPGRADE)) {

			return CUSTOMER_UPGRADE;
		}

		return StringPool.BLANK;
	}

	public static int getScore(String heatTag) {
		if (heatTag.equals(CUSTOMER_UPGRADE)) {
			return 5;
		}
		else if (heatTag.equals(ESCALATION_OVERRIDE)) {
			return 1000;
		}
		else if (heatTag.equals(EVENT_14_DAYS)) {
			return 5;
		}
		else if (heatTag.equals(EVENT_30_DAYS)) {
			return 3;
		}
		else if (heatTag.equals(EVENT_MISSED)) {
			return 8;
		}
		else if (heatTag.equals(GO_LIVE_14_DAYS)) {
			return 13;
		}
		else if (heatTag.equals(GO_LIVE_30_DAYS)) {
			return 8;
		}
		else if (heatTag.equals(GO_LIVE_MISSED)) {
			return 21;
		}
		else if (heatTag.equals(SECURITY_EXPERIENCING_ATTACK)) {
			return 21;
		}
		else if (heatTag.equals(SECURITY_REPORTED_VULNERABILITIES)) {
			return 13;
		}
		else if (heatTag.equals(SECURITY_SCAN_RESULT_CONCERNS)) {
			return 5;
		}

		return 0;
	}

}