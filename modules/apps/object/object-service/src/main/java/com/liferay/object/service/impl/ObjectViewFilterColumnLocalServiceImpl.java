/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.exception.ObjectViewFilterColumnException;
import com.liferay.object.field.filter.parser.ObjectFieldFilterContext;
import com.liferay.object.field.filter.parser.ObjectFieldFilterContributor;
import com.liferay.object.field.filter.parser.ObjectFieldFilterContributorRegistry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectView;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.service.base.ObjectViewFilterColumnLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectViewFilterColumn",
	service = AopService.class
)
public class ObjectViewFilterColumnLocalServiceImpl
	extends ObjectViewFilterColumnLocalServiceBaseImpl {

	@Override
	public List<ObjectViewFilterColumn> addObjectViewFilterColumns(
			User user, ObjectView objectView,
			List<ObjectViewFilterColumn> objectViewFilterColumns)
		throws PortalException {

		_validate(objectView.getObjectDefinitionId(), objectViewFilterColumns);

		return TransformUtil.transform(
			objectViewFilterColumns,
			objectViewFilterColumn -> {
				ObjectViewFilterColumn newObjectViewFilterColumn =
					objectViewFilterColumnPersistence.create(
						counterLocalService.increment());

				newObjectViewFilterColumn.setCompanyId(user.getCompanyId());
				newObjectViewFilterColumn.setUserId(user.getUserId());
				newObjectViewFilterColumn.setUserName(user.getFullName());
				newObjectViewFilterColumn.setObjectViewId(
					objectView.getObjectViewId());
				newObjectViewFilterColumn.setFilterType(
					objectViewFilterColumn.getFilterType());
				newObjectViewFilterColumn.setJSON(
					objectViewFilterColumn.getJSON());
				newObjectViewFilterColumn.setObjectFieldName(
					objectViewFilterColumn.getObjectFieldName());

				return objectViewFilterColumnPersistence.update(
					newObjectViewFilterColumn);
			});
	}

	private boolean _isEmpty(ObjectViewFilterColumn objectViewFilterColumn)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			objectViewFilterColumn.getJSON());

		JSONArray jsonArray = jsonObject.getJSONArray(
			objectViewFilterColumn.getFilterType());

		if (JSONUtil.isEmpty(jsonArray)) {
			return true;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			if (Validator.isNotNull(jsonArray.getString(i))) {
				return false;
			}
		}

		return true;
	}

	private void _validate(
			long objectDefinitionId,
			List<ObjectViewFilterColumn> objectViewFilterColumns)
		throws PortalException {

		for (ObjectViewFilterColumn objectViewFilterColumn :
				objectViewFilterColumns) {

			if (Validator.isNull(objectViewFilterColumn.getObjectFieldName())) {
				throw new ObjectViewFilterColumnException(
					"Object field name is null");
			}

			ObjectField objectField = _objectFieldPersistence.findByODI_N(
				objectDefinitionId,
				objectViewFilterColumn.getObjectFieldName());

			if (!(_filterableObjectFieldBusinessTypes.contains(
					objectField.getBusinessType()) ||
				  _filterableObjectFieldNames.contains(
					  objectField.getName()))) {

				throw new ObjectViewFilterColumnException(
					StringBundler.concat(
						"Object field name \"",
						objectViewFilterColumn.getObjectFieldName(),
						"\" is not filterable"));
			}

			if (Validator.isNull(objectViewFilterColumn.getFilterType()) &&
				Validator.isNull(objectViewFilterColumn.getJSON())) {

				continue;
			}

			if ((Validator.isNull(objectViewFilterColumn.getFilterType()) &&
				 Validator.isNotNull(objectViewFilterColumn.getJSON())) ||
				(Validator.isNotNull(objectViewFilterColumn.getFilterType()) &&
				 Validator.isNull(objectViewFilterColumn.getJSON())) ||
				_isEmpty(objectViewFilterColumn)) {

				throw new ObjectViewFilterColumnException(
					StringBundler.concat(
						"Object field name \"",
						objectViewFilterColumn.getObjectFieldName(),
						"\" needs to have the filter type and JSON specified"));
			}

			ObjectFieldFilterContributor objectFieldFilterContributor =
				_objectFieldFilterContributorRegistry.
					getObjectFieldFilterContributor(
						new ObjectFieldFilterContext(
							0, null, objectField.getObjectDefinitionId(),
							objectViewFilterColumn));

			objectFieldFilterContributor.validate();
		}
	}

	private static final Set<String> _filterableObjectFieldBusinessTypes =
		Collections.unmodifiableSet(
			SetUtil.fromArray(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
				ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP));
	private static final Set<String> _filterableObjectFieldNames =
		Collections.unmodifiableSet(
			SetUtil.fromArray("status", "createDate", "modifiedDate"));

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectFieldFilterContributorRegistry
		_objectFieldFilterContributorRegistry;

	@Reference
	private ObjectFieldPersistence _objectFieldPersistence;

}