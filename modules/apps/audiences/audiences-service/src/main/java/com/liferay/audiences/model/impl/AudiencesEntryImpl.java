/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.model.impl;

import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.audiences.service.AudiencesEntryGroupRelLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class AudiencesEntryImpl extends AudiencesEntryBaseImpl {

	@Override
	public List<String> getGroupERCs() {
		return TransformUtil.transform(
			AudiencesEntryGroupRelLocalServiceUtil.
				getAudiencesEntryGroupRelsByAudienceEntryERC(
					getCompanyId(), getExternalReferenceCode()),
			AudiencesEntryGroupRel::getGroupERC);
	}

}