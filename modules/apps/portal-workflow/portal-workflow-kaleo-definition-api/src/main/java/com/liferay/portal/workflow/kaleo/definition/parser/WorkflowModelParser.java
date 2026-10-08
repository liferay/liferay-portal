/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.parser;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.Definition;

import java.io.InputStream;

/**
 * @author Michael C. Han
 */
public interface WorkflowModelParser {

	public Definition parse(InputStream inputStream) throws WorkflowException;

	public default Definition parse(long companyId, InputStream inputStream)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default Definition parse(String content) throws WorkflowException {
		return parse(new UnsyncByteArrayInputStream(content.getBytes()));
	}

	public default void setValidate(boolean validate) {
		throw new UnsupportedOperationException();
	}

}