/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.link;

import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Mikel Lorza
 */
public class BrokenLinkTarget {

	public static BrokenLinkTarget deleted() {
		return new BrokenLinkTarget(true, 0, WorkflowConstants.STATUS_ANY);
	}

	public BrokenLinkTarget(long objectEntryId, int status) {
		this(false, objectEntryId, status);
	}

	public long getObjectEntryId() {
		return _objectEntryId;
	}

	public int getStatus() {
		return _status;
	}

	public boolean isDeleted() {
		return _deleted;
	}

	private BrokenLinkTarget(boolean deleted, long objectEntryId, int status) {
		_deleted = deleted;
		_objectEntryId = objectEntryId;
		_status = status;
	}

	private final boolean _deleted;
	private final long _objectEntryId;
	private final int _status;

}