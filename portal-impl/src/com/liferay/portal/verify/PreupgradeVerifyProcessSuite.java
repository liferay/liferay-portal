/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author István András Dézsi
 */
public class PreupgradeVerifyProcessSuite extends PreupgradeVerifyProcess {

	@Override
	public void doVerify() throws Exception {
		_verify(new PreupgradeVerifyCompanyUsers());
		_verify(new PreupgradeVerifyDatabaseCharacterSet());
		_verify(new PreupgradeVerifyDatabaseState());
		_verify(new PreupgradeVerifyProperties());

		if (ListUtil.isNotEmpty(_exceptionMessages)) {
			throw new VerifyException(
				StringUtil.merge(_exceptionMessages, StringPool.NEW_LINE));
		}
	}

	@Override
	protected boolean isSkipDBPartitions() {
		return true;
	}

	private void _verify(VerifyProcess verifyProcess) {
		try {
			verify(verifyProcess);
		}
		catch (VerifyException verifyException) {
			if (_log.isDebugEnabled()) {
				_log.debug(verifyException);
			}

			_exceptionMessages.add(verifyException.getMessage());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PreupgradeVerifyProcessSuite.class);

	private final List<String> _exceptionMessages = new ArrayList<>();

}