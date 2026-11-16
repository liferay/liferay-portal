/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.result.contributor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

import java.util.Locale;

/**
 * @author Matyas Wollner
 */
public class CommerceAvailabilityEstimateModelSummaryContributor
	implements ModelSummaryContributor {

	@Override
	public Summary getSummary(
		Document document, Locale locale, String snippet) {

		String prefix = Field.SNIPPET + StringPool.UNDERLINE;

		Summary summary = new Summary(
			document.get(prefix + Field.TITLE, Field.TITLE), StringPool.BLANK);

		summary.setMaxContentLength(200);

		return summary;
	}

}