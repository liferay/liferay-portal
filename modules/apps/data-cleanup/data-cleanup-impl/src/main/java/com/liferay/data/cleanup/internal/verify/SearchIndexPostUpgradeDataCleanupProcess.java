/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jorge Avalos
 */
public class SearchIndexPostUpgradeDataCleanupProcess
	implements PostUpgradeDataCleanupProcess {

	public SearchIndexPostUpgradeDataCleanupProcess(
		IndexInformation indexInformation, IndexNameBuilder indexNameBuilder) {

		_indexInformation = indexInformation;

		_pattern = Pattern.compile(
			"^" + Pattern.quote(indexNameBuilder.getIndexNamePrefix()) +
				"(\\d+).*");
	}

	@Override
	public void cleanUp() throws Exception {
		long[] companyIds = PortalInstancePool.getCompanyIds();

		Arrays.sort(companyIds);

		String[] indexNames = _indexInformation.getIndexNames();

		for (String indexName : indexNames) {
			if (indexName == null) {
				continue;
			}

			Matcher matcher = _pattern.matcher(indexName);

			if (!matcher.find()) {
				continue;
			}

			long companyId = GetterUtil.getLong(matcher.group(1));

			if ((companyId == CompanyConstants.SYSTEM) ||
				(Arrays.binarySearch(companyIds, companyId) >= 0)) {

				continue;
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Index ", indexName, " belongs to deleted company ",
						companyId, ". Remove it if it is not used anywhere ",
						"else."));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchIndexPostUpgradeDataCleanupProcess.class);

	private final IndexInformation _indexInformation;
	private final Pattern _pattern;

}