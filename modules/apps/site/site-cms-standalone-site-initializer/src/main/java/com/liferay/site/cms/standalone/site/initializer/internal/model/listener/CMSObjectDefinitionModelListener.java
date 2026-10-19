/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.model.listener;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.exception.ObjectDefinitionObjectFolderIdException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 */
@Component(service = ModelListener.class)
public class CMSObjectDefinitionModelListener
	extends BaseModelListener<ObjectDefinition> {

	@Override
	public void onBeforeCreate(ObjectDefinition objectDefinition)
		throws ModelListenerException {

		if (_isSystem(objectDefinition)) {
			return;
		}

		_validateScope(objectDefinition);

		if (!_isEmpty(objectDefinition)) {
			_validateObjectFolder(objectDefinition);
		}
	}

	@Override
	public void onBeforeUpdate(
			ObjectDefinition originalObjectDefinition,
			ObjectDefinition objectDefinition)
		throws ModelListenerException {

		if (_isSystem(objectDefinition)) {
			return;
		}

		if (!StringUtil.equals(
				originalObjectDefinition.getScope(),
				objectDefinition.getScope())) {

			_validateScope(objectDefinition);
		}

		if (!_isEmpty(objectDefinition) &&
			(_isEmpty(originalObjectDefinition) ||
			 (originalObjectDefinition.getObjectFolderId() !=
				 objectDefinition.getObjectFolderId()))) {

			_validateObjectFolder(objectDefinition);
		}
	}

	private boolean _isEmpty(ObjectDefinition objectDefinition) {
		if (objectDefinition.getStatus() == WorkflowConstants.STATUS_EMPTY) {
			return true;
		}

		return false;
	}

	private boolean _isSystem(ObjectDefinition objectDefinition) {
		if (objectDefinition.isSystem() ||
			StringUtil.startsWith(
				objectDefinition.getExternalReferenceCode(),
				ObjectDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_OBJECT_DEFINITION)) {

			return true;
		}

		return false;
	}

	private void _validateObjectFolder(ObjectDefinition objectDefinition) {
		if (!_objectFolderExternalReferenceCodes.contains(
				objectDefinition.getObjectFolderExternalReferenceCode())) {

			throw new ModelListenerException(
				new ObjectDefinitionObjectFolderIdException(
					"Custom object definitions must be in a CMS object " +
						"folder"));
		}
	}

	private void _validateScope(ObjectDefinition objectDefinition) {
		if (!StringUtil.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT)) {

			throw new ModelListenerException(
				new ObjectDefinitionScopeException(
					"Custom object definitions must have scope \"depot\""));
		}
	}

	private final Set<String> _objectFolderExternalReferenceCodes =
		SetUtil.fromArray(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
			ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS);

}