/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.util.comparator;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AMDistanceComparator;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.portal.kernel.repository.model.FileVersion;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class AMPropertyDistanceComparator
	implements AMDistanceComparator<AdaptiveMedia<AMProcessor<FileVersion>>> {

	public AMPropertyDistanceComparator(
		Map<AMAttribute<AMProcessor<FileVersion>, ?>, ?> amAttributes) {

		_amAttributes = amAttributes;
	}

	@Override
	public long compare(
		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia1,
		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia2) {

		for (Map.Entry<AMAttribute<AMProcessor<FileVersion>, ?>, ?> entry :
				_amAttributes.entrySet()) {

			AMAttribute<AMProcessor<FileVersion>, Object> amAttribute =
				(AMAttribute<AMProcessor<FileVersion>, Object>)entry.getKey();

			Object value1 = adaptiveMedia1.getValue(amAttribute);
			Object value2 = adaptiveMedia2.getValue(amAttribute);

			if ((value1 == null) || (value2 == null)) {
				continue;
			}

			Object requestedValue = entry.getValue();

			long valueDistance1 = amAttribute.distance(value1, requestedValue);

			long valueDistance2 = amAttribute.distance(value2, requestedValue);

			long result = valueDistance1 - valueDistance2;

			if (result != 0) {
				return result;
			}
		}

		return 0L;
	}

	private final Map<AMAttribute<AMProcessor<FileVersion>, ?>, ?>
		_amAttributes;

}