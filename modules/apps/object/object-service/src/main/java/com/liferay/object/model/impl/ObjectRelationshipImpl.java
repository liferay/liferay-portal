/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;
import java.util.Set;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectRelationshipImpl extends ObjectRelationshipBaseImpl {

	@Override
	public boolean compareType(String type) {
		return type.equals(getType());
	}

	@Override
	public String getDefaultLanguageId() {
		String xml = getLabel();

		if (xml == null) {
			return "";
		}

		return LocalizationUtil.getDefaultLanguageId(
			xml, LocaleUtil.getDefault());
	}

	@Override
	public boolean hasRelationshipObjectField() {
		if (compareType(ObjectRelationshipConstants.TYPE_ONE_TO_MANY) ||
			compareType(ObjectRelationshipConstants.TYPE_ONE_TO_ONE)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isAllowedObjectRelationshipType(String type) {
		if (StringUtil.equals(
				type, ObjectRelationshipConstants.TYPE_ONE_TO_ONE) &&
			!FeatureFlagManagerUtil.isEnabled(getCompanyId(), "LPD-108620")) {

			return false;
		}

		Set<String> defaultObjectRelationshipTypes =
			ObjectRelationshipUtil.getDefaultObjectRelationshipTypes();

		return defaultObjectRelationshipTypes.contains(type);
	}

	@Override
	public boolean isSelf() {
		return Objects.equals(
			getObjectDefinitionId1(), getObjectDefinitionId2());
	}

}