/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.model.impl;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.util.WorkflowDefinitionContentUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalServiceUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class KaleoDefinitionVersionImpl extends KaleoDefinitionVersionBaseImpl {

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
	public KaleoDefinition getKaleoDefinition() throws PortalException {
		return KaleoDefinitionLocalServiceUtil.getKaleoDefinition(
			getKaleoDefinitionId());
	}

	@Override
	public KaleoNode getKaleoStartNode() throws PortalException {
		return KaleoNodeLocalServiceUtil.getKaleoNode(getStartKaleoNodeId());
	}

	@Override
	public void setContentAsXML(String contentAsXML) {
		_contentAsXML = contentAsXML;
	}

	protected int getVersion(String version) {
		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		return versionParts[0];
	}

	@CacheField(propagateToInterface = true)
	private String _contentAsXML;

}