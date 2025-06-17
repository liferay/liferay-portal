/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.model.impl;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.util.WorkflowDefinitionContentUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalServiceUtil;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class KaleoDefinitionImpl extends KaleoDefinitionBaseImpl {

	@Override
	public String getContentAsXML() {
		if (_contentAsXML != null) {
			return _contentAsXML;
		}

		try {
			_contentAsXML = WorkflowDefinitionContentUtil.toXML(getContent());

			contentAsXMLUpdateEntityCacheConsumer.accept(_contentAsXML);
		}
		catch (WorkflowException workflowException) {
			ReflectionUtil.throwException(workflowException);
		}

		return _contentAsXML;
	}

	@Override
	public List<KaleoDefinitionVersion> getKaleoDefinitionVersions()
		throws PortalException {

		return KaleoDefinitionVersionLocalServiceUtil.
			getKaleoDefinitionVersions(getCompanyId(), getName());
	}

	@Override
	public void setContentAsXML(String contentAsXML) {
		_contentAsXML = contentAsXML;
	}

	@CacheField(propagateToInterface = true)
	private String _contentAsXML;

}