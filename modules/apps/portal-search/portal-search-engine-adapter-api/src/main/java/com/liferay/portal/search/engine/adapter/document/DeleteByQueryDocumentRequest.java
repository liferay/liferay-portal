/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.engine.adapter.document;

import com.liferay.portal.search.engine.adapter.ccr.CrossClusterRequest;
import com.liferay.portal.search.query.Query;

/**
 * @author Michael C. Han
 */
public class DeleteByQueryDocumentRequest
	extends CrossClusterRequest
	implements DocumentRequest<DeleteByQueryDocumentResponse> {

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by
	 *             DeleteByQueryDocumentRequest.DeleteByQueryDocumentRequest(
	 *             Query, String...)
	 */
	@Deprecated
	public DeleteByQueryDocumentRequest(
		com.liferay.portal.kernel.search.Query query, String... indexNames) {

		_query = query;
		_indexNames = indexNames;

		_portalSearchQuery = null;
	}

	public DeleteByQueryDocumentRequest(
		Query portalSearchQuery, String... indexNames) {

		_portalSearchQuery = portalSearchQuery;
		_indexNames = indexNames;

		_query = null;
	}

	@Override
	public DeleteByQueryDocumentResponse accept(
		DocumentRequestExecutor documentRequestExecutor) {

		return documentRequestExecutor.executeDocumentRequest(this);
	}

	public String[] getIndexNames() {
		return _indexNames;
	}

	public Query getPortalSearchQuery() {
		return _portalSearchQuery;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getPortalSearchQuery()}
	 */
	@Deprecated
	public com.liferay.portal.kernel.search.Query getQuery() {
		return _query;
	}

	public boolean isProceedOnConflicts() {
		return _proceedOnConflicts;
	}

	public boolean isRefresh() {
		return _refresh;
	}

	public boolean isWaitForCompletion() {
		return _waitForCompletion;
	}

	public void setProceedOnConflicts(boolean proceedOnConflicts) {
		_proceedOnConflicts = proceedOnConflicts;
	}

	public void setRefresh(boolean refresh) {
		_refresh = refresh;
	}

	public void setWaitForCompletion(boolean waitForCompletion) {
		_waitForCompletion = waitForCompletion;
	}

	private final String[] _indexNames;
	private final Query _portalSearchQuery;
	private boolean _proceedOnConflicts;
	private final com.liferay.portal.kernel.search.Query _query;
	private boolean _refresh;
	private boolean _waitForCompletion;

}