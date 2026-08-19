/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.field.type.util;

import com.liferay.info.field.type.AssigneeInfoFieldType;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.field.type.EmailInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.PhoneNumberInfoFieldType;
import com.liferay.info.field.type.PicklistMultiselectInfoFieldType;
import com.liferay.info.field.type.PicklistSelectInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class ObjectFieldInfoFieldTypeUtil {

	public static InfoFieldType getInfoFieldType(ObjectField objectField) {
		if (Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			return AssigneeInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			return FileInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {

			return BooleanInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_DATE)) {

			return DateInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			return DateTimeInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_DECIMAL) ||
				 Objects.equals(
					 objectField.getBusinessType(),
					 ObjectFieldConstants.BUSINESS_TYPE_INTEGER) ||
				 Objects.equals(
					 objectField.getBusinessType(),
					 ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER) ||
				 Objects.equals(
					 objectField.getBusinessType(),
					 ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL)) {

			return NumberInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_EMAIL_ADDRESS)) {

			return EmailInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT)) {

			return LongTextInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			return PicklistMultiselectInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PHONE_NUMBER)) {

			return PhoneNumberInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return PicklistSelectInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			return RelationshipInfoFieldType.INSTANCE;
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			return HTMLInfoFieldType.INSTANCE;
		}

		return TextInfoFieldType.INSTANCE;
	}

}