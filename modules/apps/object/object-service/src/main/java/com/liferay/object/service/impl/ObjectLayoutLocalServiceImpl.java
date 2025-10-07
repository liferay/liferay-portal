/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectLayoutBoxConstants;
import com.liferay.object.exception.DefaultObjectLayoutException;
import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.exception.ObjectDefinitionModifiableException;
import com.liferay.object.exception.ObjectLayoutBoxCategorizationTypeException;
import com.liferay.object.exception.ObjectLayoutColumnSizeException;
import com.liferay.object.exception.ObjectRelationshipEdgeException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutBox;
import com.liferay.object.model.ObjectLayoutColumn;
import com.liferay.object.model.ObjectLayoutRow;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutTabLocalService;
import com.liferay.object.service.base.ObjectLayoutLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectLayoutBoxPersistence;
import com.liferay.object.service.persistence.ObjectLayoutColumnPersistence;
import com.liferay.object.service.persistence.ObjectLayoutRowPersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectLayout",
	service = AopService.class
)
public class ObjectLayoutLocalServiceImpl
	extends ObjectLayoutLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectLayout addObjectLayout(
			long userId, long objectDefinitionId, boolean defaultObjectLayout,
			Map<Locale, String> nameMap, List<ObjectLayoutTab> objectLayoutTabs)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		if (objectDefinition.isUnmodifiableSystemObject()) {
			throw new ObjectDefinitionModifiableException.MustBeModifiable();
		}

		_validate(0, objectDefinitionId, defaultObjectLayout, objectLayoutTabs);

		ObjectLayout objectLayout = objectLayoutPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectLayout.setCompanyId(user.getCompanyId());
		objectLayout.setUserId(user.getUserId());
		objectLayout.setUserName(user.getFullName());

		objectLayout.setObjectDefinitionId(
			objectDefinition.getObjectDefinitionId());
		objectLayout.setDefaultObjectLayout(defaultObjectLayout);
		objectLayout.setNameMap(nameMap);

		objectLayout = objectLayoutPersistence.update(objectLayout);

		objectLayout.setObjectLayoutTabs(
			_addObjectLayoutTabs(
				user, objectDefinitionId, objectLayout, objectLayoutTabs));

		return objectLayout;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ObjectLayout deleteObjectLayout(long objectLayoutId)
		throws PortalException {

		ObjectLayout objectLayout = objectLayoutPersistence.findByPrimaryKey(
			objectLayoutId);

		return deleteObjectLayout(objectLayout);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectLayout deleteObjectLayout(ObjectLayout objectLayout)
		throws PortalException {

		objectLayout = objectLayoutPersistence.remove(objectLayout);

		_deleteObjectLayoutTabs(objectLayout.getObjectLayoutId());

		return objectLayout;
	}

	@Override
	public void deleteObjectLayouts(long objectDefinitionId)
		throws PortalException {

		for (ObjectLayout objectLayout :
				objectLayoutPersistence.findByObjectDefinitionId(
					objectDefinitionId)) {

			objectLayoutLocalService.deleteObjectLayout(objectLayout);
		}
	}

	@Override
	public ObjectLayout fetchDefaultObjectLayout(long objectDefinitionId) {
		ObjectLayout objectLayout =
			objectLayoutPersistence.fetchByODI_DOL_First(
				objectDefinitionId, true, null);

		if (objectLayout == null) {
			return null;
		}

		objectLayout.setObjectLayoutTabs(_getObjectLayoutTabs(objectLayout));

		return objectLayout;
	}

	@Override
	public ObjectLayout getDefaultObjectLayout(long objectDefinitionId)
		throws PortalException {

		ObjectLayout objectLayout = objectLayoutPersistence.findByODI_DOL_First(
			objectDefinitionId, true, null);

		objectLayout.setObjectLayoutTabs(_getObjectLayoutTabs(objectLayout));

		return objectLayout;
	}

	@Override
	public ObjectLayout getObjectLayout(long objectLayoutId)
		throws PortalException {

		ObjectLayout objectLayout = objectLayoutPersistence.findByPrimaryKey(
			objectLayoutId);

		objectLayout.setObjectLayoutTabs(_getObjectLayoutTabs(objectLayout));

		return objectLayout;
	}

	@Override
	public List<ObjectLayout> getObjectLayouts(long objectDefinitionId) {
		List<ObjectLayout> objectLayouts =
			objectLayoutPersistence.findByObjectDefinitionId(
				objectDefinitionId);

		for (ObjectLayout objectLayout : objectLayouts) {
			objectLayout.setObjectLayoutTabs(
				_getObjectLayoutTabs(objectLayout));
		}

		return objectLayouts;
	}

	@Override
	public List<ObjectLayout> getObjectLayouts(
		long objectDefinitionId, int start, int end) {

		return objectLayoutPersistence.findByObjectDefinitionId(
			objectDefinitionId, start, end);
	}

	@Override
	public int getObjectLayoutsCount(long objectDefinitionId) {
		return objectLayoutPersistence.countByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public Map<Long, List<ObjectLayout>> getObjectLayoutsMap(long companyId) {
		Map<Long, List<ObjectLayout>> objectLayoutsMap = new HashMap<>();

		for (ObjectLayout objectLayout :
				objectLayoutPersistence.findByC_DOL(companyId, true)) {

			objectLayout.setObjectLayoutTabs(
				_getObjectLayoutTabs(objectLayout));

			List<ObjectLayout> objectLayouts = objectLayoutsMap.computeIfAbsent(
				objectLayout.getObjectDefinitionId(),
				objectDefinitionId -> new ArrayList<>());

			objectLayouts.add(objectLayout);
		}

		return objectLayoutsMap;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectLayout updateObjectLayout(
			long objectLayoutId, boolean defaultObjectLayout,
			Map<Locale, String> nameMap, List<ObjectLayoutTab> objectLayoutTabs)
		throws PortalException {

		ObjectLayout objectLayout = objectLayoutPersistence.findByPrimaryKey(
			objectLayoutId);

		_validate(
			objectLayoutId, objectLayout.getObjectDefinitionId(),
			defaultObjectLayout, objectLayoutTabs);

		_deleteObjectLayoutTabs(objectLayoutId);

		objectLayout.setDefaultObjectLayout(defaultObjectLayout);
		objectLayout.setNameMap(nameMap);

		objectLayout = objectLayoutPersistence.update(objectLayout);

		User user = _userLocalService.getUser(objectLayout.getUserId());

		objectLayout.setObjectLayoutTabs(
			_addObjectLayoutTabs(
				user, objectLayout.getObjectDefinitionId(), objectLayout,
				objectLayoutTabs));

		return objectLayout;
	}

	private ObjectLayoutBox _addObjectLayoutBox(
			User user, long objectDefinitionId, long objectLayoutTabId,
			ObjectLayoutBox objectLayoutBox)
		throws PortalException {

		ObjectLayoutBox newObjectLayoutBox = _objectLayoutBoxPersistence.create(
			counterLocalService.increment());

		newObjectLayoutBox.setCompanyId(user.getCompanyId());
		newObjectLayoutBox.setUserId(user.getUserId());
		newObjectLayoutBox.setUserName(user.getFullName());
		newObjectLayoutBox.setObjectLayoutTabId(objectLayoutTabId);
		newObjectLayoutBox.setCollapsable(objectLayoutBox.isCollapsable());
		newObjectLayoutBox.setNameMap(objectLayoutBox.getNameMap());
		newObjectLayoutBox.setPriority(objectLayoutBox.getPriority());
		newObjectLayoutBox.setType(objectLayoutBox.getType());
		newObjectLayoutBox.setObjectLayoutRows(
			objectLayoutBox.getObjectLayoutRows());

		newObjectLayoutBox = _objectLayoutBoxPersistence.update(
			newObjectLayoutBox);

		newObjectLayoutBox.setObjectLayoutRows(
			_addObjectLayoutRows(
				user, objectDefinitionId,
				newObjectLayoutBox.getObjectLayoutBoxId(),
				newObjectLayoutBox.getObjectLayoutRows()));

		return newObjectLayoutBox;
	}

	private List<ObjectLayoutBox> _addObjectLayoutBoxes(
			User user, long objectDefinitionId, long objectLayoutTabId,
			List<ObjectLayoutBox> objectLayoutBoxes)
		throws PortalException {

		return TransformUtil.unsafeTransform(
			objectLayoutBoxes,
			objectLayoutBox -> _addObjectLayoutBox(
				user, objectDefinitionId, objectLayoutTabId, objectLayoutBox));
	}

	private ObjectLayoutColumn _addObjectLayoutColumn(
			User user, long objectDefinitionId, long objectFieldId,
			long objectLayoutRowId, int priority, int size)
		throws PortalException {

		ObjectField objectField = _objectFieldPersistence.findByPrimaryKey(
			objectFieldId);

		if (objectField.getObjectDefinitionId() != objectDefinitionId) {
			throw new NoSuchObjectFieldException();
		}

		if ((size < 0) || (size > 12)) {
			throw new ObjectLayoutColumnSizeException(
				"Object layout column size must be more than 0 and less than " +
					"12");
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			ObjectRelationship objectRelationship =
				objectField.getObjectRelationship();

			if (objectRelationship.isEdge()) {
				throw new ObjectRelationshipEdgeException(
					"Edge object relationship object fields cannot be " +
						"associated with object layouts",
					"edge-object-relationship-object-fields-cannot-be-" +
						"associated-with-object-layouts");
			}
		}

		ObjectLayoutColumn objectLayoutColumn =
			_objectLayoutColumnPersistence.create(
				counterLocalService.increment());

		objectLayoutColumn.setCompanyId(user.getCompanyId());
		objectLayoutColumn.setUserId(user.getUserId());
		objectLayoutColumn.setUserName(user.getFullName());
		objectLayoutColumn.setObjectFieldId(objectField.getObjectFieldId());
		objectLayoutColumn.setObjectLayoutRowId(objectLayoutRowId);
		objectLayoutColumn.setPriority(priority);
		objectLayoutColumn.setSize(size);

		return _objectLayoutColumnPersistence.update(objectLayoutColumn);
	}

	private List<ObjectLayoutColumn> _addObjectLayoutColumns(
			User user, long objectDefinitionId, long objectLayoutRowId,
			List<ObjectLayoutColumn> objectLayoutColumns)
		throws PortalException {

		return TransformUtil.unsafeTransform(
			objectLayoutColumns,
			objectLayoutColumn -> _addObjectLayoutColumn(
				user, objectDefinitionId, objectLayoutColumn.getObjectFieldId(),
				objectLayoutRowId, objectLayoutColumn.getPriority(),
				objectLayoutColumn.getSize()));
	}

	private ObjectLayoutRow _addObjectLayoutRow(
			User user, long objectDefinitionId, long objectLayoutBoxId,
			int priority, List<ObjectLayoutColumn> objectLayoutColumns)
		throws PortalException {

		ObjectLayoutRow objectLayoutRow = _objectLayoutRowPersistence.create(
			counterLocalService.increment());

		objectLayoutRow.setCompanyId(user.getCompanyId());
		objectLayoutRow.setUserId(user.getUserId());
		objectLayoutRow.setUserName(user.getFullName());
		objectLayoutRow.setObjectLayoutBoxId(objectLayoutBoxId);
		objectLayoutRow.setPriority(priority);

		objectLayoutRow = _objectLayoutRowPersistence.update(objectLayoutRow);

		objectLayoutRow.setObjectLayoutColumns(
			_addObjectLayoutColumns(
				user, objectDefinitionId,
				objectLayoutRow.getObjectLayoutRowId(), objectLayoutColumns));

		return objectLayoutRow;
	}

	private List<ObjectLayoutRow> _addObjectLayoutRows(
			User user, long objectDefinitionId, long objectLayoutBoxId,
			List<ObjectLayoutRow> objectLayoutRows)
		throws PortalException {

		return TransformUtil.unsafeTransform(
			objectLayoutRows,
			objectLayoutRow -> _addObjectLayoutRow(
				user, objectDefinitionId, objectLayoutBoxId,
				objectLayoutRow.getPriority(),
				objectLayoutRow.getObjectLayoutColumns()));
	}

	private ObjectLayoutTab _addObjectLayoutTab(
			User user, long objectDefinitionId, long objectLayoutId,
			long objectRelationshipId, Map<Locale, String> nameMap,
			int priority, List<ObjectLayoutBox> objectLayoutBoxes)
		throws PortalException {

		ObjectLayoutTab objectLayoutTab =
			_objectLayoutTabLocalService.addObjectLayoutTab(
				user.getUserId(), objectLayoutId, objectRelationshipId, nameMap,
				priority);

		objectLayoutTab.setObjectLayoutBoxes(
			_addObjectLayoutBoxes(
				user, objectDefinitionId,
				objectLayoutTab.getObjectLayoutTabId(), objectLayoutBoxes));

		return objectLayoutTab;
	}

	private List<ObjectLayoutTab> _addObjectLayoutTabs(
			User user, long objectDefinitionId, ObjectLayout objectLayout,
			List<ObjectLayoutTab> objectLayoutTabs)
		throws PortalException {

		objectLayoutTabs = TransformUtil.unsafeTransform(
			objectLayoutTabs,
			objectLayoutTab -> _addObjectLayoutTab(
				user, objectDefinitionId, objectLayout.getObjectLayoutId(),
				objectLayoutTab.getObjectRelationshipId(),
				objectLayoutTab.getNameMap(), objectLayoutTab.getPriority(),
				objectLayoutTab.getObjectLayoutBoxes()));

		if (objectLayout.isDefaultObjectLayout()) {
			_objectLayoutTabLocalService.
				registerObjectLayoutTabScreenNavigationCategories(
					_objectDefinitionPersistence.fetchByPrimaryKey(
						objectDefinitionId),
					objectLayoutTabs);
		}

		return objectLayoutTabs;
	}

	private void _deleteObjectLayoutBoxes(
		List<ObjectLayoutTab> objectLayoutTabs) {

		for (ObjectLayoutTab objectLayoutTab : objectLayoutTabs) {
			List<ObjectLayoutBox> objectLayoutBoxes =
				_objectLayoutBoxPersistence.findByObjectLayoutTabId(
					objectLayoutTab.getObjectLayoutTabId());

			_objectLayoutBoxPersistence.removeByObjectLayoutTabId(
				objectLayoutTab.getObjectLayoutTabId());

			_deleteObjectLayoutRows(objectLayoutBoxes);
		}
	}

	private void _deleteObjectLayoutColumns(
		List<ObjectLayoutRow> objectLayoutRows) {

		for (ObjectLayoutRow objectLayoutRow : objectLayoutRows) {
			_objectLayoutColumnPersistence.removeByObjectLayoutRowId(
				objectLayoutRow.getObjectLayoutRowId());
		}
	}

	private void _deleteObjectLayoutRows(
		List<ObjectLayoutBox> objectLayoutBoxes) {

		for (ObjectLayoutBox objectLayoutBox : objectLayoutBoxes) {
			List<ObjectLayoutRow> objectLayoutRows =
				_objectLayoutRowPersistence.findByObjectLayoutBoxId(
					objectLayoutBox.getObjectLayoutBoxId());

			_objectLayoutRowPersistence.removeByObjectLayoutBoxId(
				objectLayoutBox.getObjectLayoutBoxId());

			_deleteObjectLayoutColumns(objectLayoutRows);
		}
	}

	private void _deleteObjectLayoutTabs(long objectLayoutId)
		throws PortalException {

		List<ObjectLayoutTab> objectLayoutTabs =
			_objectLayoutTabLocalService.getObjectLayoutObjectLayoutTabs(
				objectLayoutId);

		_deleteObjectLayoutBoxes(objectLayoutTabs);

		_objectLayoutTabLocalService.deleteObjectLayoutObjectLayoutTabs(
			objectLayoutId);
	}

	private List<ObjectLayoutBox> _getObjectLayoutBoxes(
		ObjectLayoutTab objectLayoutTab) {

		List<ObjectLayoutBox> objectLayoutBoxes =
			_objectLayoutBoxPersistence.findByObjectLayoutTabId(
				objectLayoutTab.getObjectLayoutTabId());

		for (ObjectLayoutBox objectLayoutBox : objectLayoutBoxes) {
			objectLayoutBox.setObjectLayoutRows(
				_getObjectLayoutRows(objectLayoutBox));
		}

		return objectLayoutBoxes;
	}

	private List<ObjectLayoutRow> _getObjectLayoutRows(
		ObjectLayoutBox objectLayoutBox) {

		List<ObjectLayoutRow> objectLayoutRows =
			_objectLayoutRowPersistence.findByObjectLayoutBoxId(
				objectLayoutBox.getObjectLayoutBoxId());

		for (ObjectLayoutRow objectLayoutRow : objectLayoutRows) {
			objectLayoutRow.setObjectLayoutColumns(
				_objectLayoutColumnPersistence.findByObjectLayoutRowId(
					objectLayoutRow.getObjectLayoutRowId()));
		}

		return objectLayoutRows;
	}

	private List<ObjectLayoutTab> _getObjectLayoutTabs(
		ObjectLayout objectLayout) {

		List<ObjectLayoutTab> objectLayoutTabs =
			_objectLayoutTabLocalService.getObjectLayoutObjectLayoutTabs(
				objectLayout.getObjectLayoutId());

		for (ObjectLayoutTab objectLayoutTab : objectLayoutTabs) {
			objectLayoutTab.setObjectLayoutBoxes(
				_getObjectLayoutBoxes(objectLayoutTab));
		}

		return objectLayoutTabs;
	}

	private void _validate(
			long objectLayoutId, long objectDefinitionId,
			boolean defaultObjectLayout, List<ObjectLayoutTab> objectLayoutTabs)
		throws PortalException {

		if (defaultObjectLayout) {
			Set<Long> objectFieldIds = new HashSet<>();

			ObjectLayoutTab objectLayoutTab = objectLayoutTabs.get(0);

			List<ObjectLayoutBox> objectLayoutBoxes =
				objectLayoutTab.getObjectLayoutBoxes();

			for (ObjectLayoutBox objectLayoutBox : objectLayoutBoxes) {
				List<ObjectLayoutRow> objectLayoutRows =
					objectLayoutBox.getObjectLayoutRows();

				for (ObjectLayoutRow objectLayoutRow : objectLayoutRows) {
					List<ObjectLayoutColumn> objectLayoutColumns =
						objectLayoutRow.getObjectLayoutColumns();

					for (ObjectLayoutColumn objectLayoutColumn :
							objectLayoutColumns) {

						objectFieldIds.add(
							objectLayoutColumn.getObjectFieldId());
					}
				}
			}

			List<ObjectField> objectFields =
				_objectFieldLocalService.getObjectFields(objectDefinitionId);

			for (ObjectField objectField : objectFields) {
				if (!objectField.isRequired()) {
					continue;
				}

				if (!objectFieldIds.contains(objectField.getObjectFieldId())) {
					throw new DefaultObjectLayoutException(
						"All required object fields must be associated to " +
							"the first tab of a default object layout");
				}
			}

			ObjectLayout objectLayout =
				objectLayoutPersistence.fetchByODI_DOL_First(
					objectDefinitionId, true, null);

			if ((objectLayout != null) &&
				(objectLayout.getObjectLayoutId() != objectLayoutId)) {

				throw new DefaultObjectLayoutException(
					"There can only be one default object layout");
			}
		}

		int countObjectLayoutBoxCategorizationType = 0;
		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId);

		for (ObjectLayoutTab objectLayoutTab : objectLayoutTabs) {
			if (objectLayoutTab.getObjectRelationshipId() != 0) {
				ObjectRelationship objectRelationship =
					objectLayoutTab.getObjectRelationship();

				if (objectRelationship.isEdge()) {
					throw new ObjectRelationshipEdgeException(
						"Edge object relationships cannot be associated with " +
							"object layout tabs",
						"edge-object-relationships-cannot-be-associated-with-" +
							"object-layout-tabs");
				}
			}

			List<ObjectLayoutBox> objectLayoutBoxes =
				objectLayoutTab.getObjectLayoutBoxes();

			for (ObjectLayoutBox objectLayoutBox : objectLayoutBoxes) {
				if (Validator.isNull(objectLayoutBox.getType())) {
					throw new ObjectLayoutBoxCategorizationTypeException(
						"Object layout box must have a type");
				}

				if (!StringUtil.equals(
						objectLayoutBox.getType(),
						ObjectLayoutBoxConstants.TYPE_CATEGORIZATION)) {

					continue;
				}

				if (!objectDefinition.isEnableCategorization()) {
					throw new ObjectLayoutBoxCategorizationTypeException(
						"Categorization layout box must be enabled to be used");
				}

				if (!objectDefinition.isDefaultStorageType()) {
					throw new ObjectLayoutBoxCategorizationTypeException(
						"Categorization layout box can only be used in " +
							"object definitions with a default storage type");
				}

				countObjectLayoutBoxCategorizationType++;

				if (countObjectLayoutBoxCategorizationType > 1) {
					throw new ObjectLayoutBoxCategorizationTypeException(
						"There can only be one categorization layout box per " +
							"layout");
				}

				if (ListUtil.isNotEmpty(
						objectLayoutBox.getObjectLayoutRows())) {

					throw new ObjectLayoutBoxCategorizationTypeException(
						"Categorization layout box must not have layout rows");
				}
			}
		}
	}

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldPersistence _objectFieldPersistence;

	@Reference
	private ObjectLayoutBoxPersistence _objectLayoutBoxPersistence;

	@Reference
	private ObjectLayoutColumnPersistence _objectLayoutColumnPersistence;

	@Reference
	private ObjectLayoutRowPersistence _objectLayoutRowPersistence;

	@Reference
	private ObjectLayoutTabLocalService _objectLayoutTabLocalService;

	@Reference
	private UserLocalService _userLocalService;

}