/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.conflict;

import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Preston Crary
 */
public class MissingRequirementConflictInfo extends BaseConflictInfo {

	public MissingRequirementConflictInfo(
		long modelClassPK, String requirementTypeName) {

		_modelClassPK = modelClassPK;
		_requirementTypeName = requirementTypeName;

		_className = null;
		_requirementCTDisplayRenderer = null;
	}

	public MissingRequirementConflictInfo(
		String className, long modelClassPK,
		CTDisplayRenderer<?> requirementCTDisplayRenderer) {

		_className = className;
		_modelClassPK = modelClassPK;
		_requirementCTDisplayRenderer = requirementCTDisplayRenderer;

		_requirementTypeName = null;
	}

	@Override
	public String getConflictDescription(ResourceBundle resourceBundle) {
		return LanguageUtil.get(resourceBundle, "missing-requirement-conflict");
	}

	@Override
	public String getResolutionDescription(ResourceBundle resourceBundle) {
		return LanguageUtil.format(
			resourceBundle,
			"cannot-be-added-because-a-required-x-has-been-deleted",
			_getRequirementTypeName(resourceBundle.getLocale()), false);
	}

	@Override
	public long getSourcePrimaryKey() {
		return _modelClassPK;
	}

	@Override
	public long getTargetPrimaryKey() {
		return 0;
	}

	private String _getRequirementTypeName(Locale locale) {
		if (Validator.isNotNull(_requirementTypeName)) {
			return LanguageUtil.get(locale, _requirementTypeName);
		}

		if (_requirementCTDisplayRenderer == null) {
			String name = ResourceActionsUtil.getModelResource(
				locale, _className);

			if (name.startsWith("model.resource.")) {
				return _className;
			}

			return name;
		}

		return _requirementCTDisplayRenderer.getTypeName(locale);
	}

	private final String _className;
	private final long _modelClassPK;
	private final CTDisplayRenderer<?> _requirementCTDisplayRenderer;
	private final String _requirementTypeName;

}