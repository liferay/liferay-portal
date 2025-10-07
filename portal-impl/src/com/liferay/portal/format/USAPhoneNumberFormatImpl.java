/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.format;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.format.PhoneNumberFormat;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 * @author Manuel de la Peña
 */
public class USAPhoneNumberFormatImpl implements PhoneNumberFormat {

	@Override
	public String format(String phoneNumber) {
		if (Validator.isNull(phoneNumber)) {
			return StringPool.BLANK;
		}

		if (phoneNumber.length() > 10) {
			return StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, phoneNumber.substring(0, 3), ") ",
				phoneNumber.substring(3, 6), StringPool.DASH,
				phoneNumber.substring(6, 10), " x", phoneNumber.substring(10));
		}
		else if (phoneNumber.length() == 10) {
			return StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, phoneNumber.substring(0, 3), ") ",
				phoneNumber.substring(3, 6), StringPool.DASH,
				phoneNumber.substring(6));
		}
		else if (phoneNumber.length() == 7) {
			return StringBundler.concat(
				phoneNumber.substring(0, 3), StringPool.DASH,
				phoneNumber.substring(3));
		}

		return phoneNumber;
	}

	@Override
	public String strip(String phoneNumber) {
		return StringUtil.extractDigits(phoneNumber);
	}

	@Override
	public boolean validate(String phoneNumber) {
		if (Validator.isNull(phoneNumber)) {
			return false;
		}

		return phoneNumber.matches(PropsValues.PHONE_NUMBER_FORMAT_USA_REGEXP);
	}

}