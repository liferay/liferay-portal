/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.helper;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.helper.CPCompareHelper;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = CPCompareHelper.class)
public class CPCompareHelperImpl implements CPCompareHelper {

	@Override
	public List<CPCatalogEntry> getCPCatalogEntries(
			long groupId, long commerceAccountId,
			String cpDefinitionIdsCookieValue)
		throws PortalException {

		return TransformUtil.transform(
			_getCpDefinitionIds(cpDefinitionIdsCookieValue),
			cpDefinitionId -> {
				CPCatalogEntry cpCatalogEntry = null;

				try {
					cpCatalogEntry = _cpDefinitionHelper.getCPCatalogEntry(
						commerceAccountId, groupId, cpDefinitionId,
						LocaleUtil.getDefault());
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}

					return null;
				}

				if (cpCatalogEntry != null) {
					return cpCatalogEntry;
				}

				return null;
			});
	}

	@Override
	public List<Long> getCPDefinitionIds(
		long groupId, long commerceAccountId,
		String cpDefinitionIdsCookieValue) {

		return getCPDefinitionIds(
			groupId, commerceAccountId, cpDefinitionIdsCookieValue, true);
	}

	@Override
	public List<Long> getCPDefinitionIds(
		long groupId, long commerceAccountId, String cpDefinitionIdsCookieValue,
		boolean secure) {

		return TransformUtil.transform(
			_getCpDefinitionIds(cpDefinitionIdsCookieValue),
			cpDefinitionId -> {
				CPCatalogEntry cpCatalogEntry = null;

				try {
					cpCatalogEntry = _cpDefinitionHelper.getCPCatalogEntry(
						commerceAccountId, groupId, cpDefinitionId,
						LocaleUtil.getDefault(), secure);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}

					return null;
				}

				if (cpCatalogEntry != null) {
					return cpDefinitionId;
				}

				return null;
			});
	}

	@Override
	public String getCPDefinitionIdsCookieKey(long commerceChannelGroupId) {
		return "COMMERCE_COMPARE_cpDefinitionIds_" + commerceChannelGroupId;
	}

	private List<Long> _getCpDefinitionIds(String cookieValue) {
		return ListUtil.fromArray(
			StringUtil.split(cookieValue, StringPool.COLON, -1L));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPCompareHelperImpl.class);

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

}