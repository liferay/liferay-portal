/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.constants;

import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Shinn Lok
 */
public class SegmentConstants {

	public static Map<String, String> getSegmentStates() {
		return _segmentStates;
	}

	public static Map<String, String> getSegmentTypes() {
		return _segmentTypes;
	}

	private static final Map<String, String> _segmentStates =
		HashMapBuilder.put(
			"disabled", IndividualSegment.State.DISABLED.name()
		).put(
			"inProgress", IndividualSegment.State.IN_PROGRESS.name()
		).put(
			"ready", IndividualSegment.State.READY.name()
		).build();
	private static final Map<String, String> _segmentTypes = HashMapBuilder.put(
		"batch", IndividualSegment.Type.BATCH.name()
	).put(
		"realTime", IndividualSegment.Type.REAL_TIME.name()
	).build();

}