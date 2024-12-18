/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.exception;

/**
 * @author Brian Wing Shun Chan
 */
public class DuplicateWorkflowDefinitionLinkExternalReferenceCodeException
	extends DuplicateExternalReferenceCodeException {

	public DuplicateWorkflowDefinitionLinkExternalReferenceCodeException() {
	}

	public DuplicateWorkflowDefinitionLinkExternalReferenceCodeException(
		String msg) {

		super(msg);
	}

	public DuplicateWorkflowDefinitionLinkExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateWorkflowDefinitionLinkExternalReferenceCodeException(
		Throwable throwable) {

		super(throwable);
	}

}