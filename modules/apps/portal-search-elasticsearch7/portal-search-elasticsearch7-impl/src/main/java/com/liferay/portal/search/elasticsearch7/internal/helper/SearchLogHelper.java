/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.helper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.search.elasticsearch7.configuration.RESTClientLoggerLevel;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkResponse;

/**
 * @author Adam Brandizzi
 */
public interface SearchLogHelper {

	public void logActionResponse(Log log, ActionResponse actionResponse);

	public void logActionResponse(Log log, BulkResponse bulkResponse);

	public void setRESTClientLoggerLevel(
		RESTClientLoggerLevel restClientLoggerLevel);

}