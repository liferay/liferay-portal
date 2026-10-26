/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.input;

import com.liferay.petra.string.StringBundler;

import graphql.kickstart.execution.input.GraphQLBatchedInvocationInput;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import graphql.kickstart.servlet.input.BatchInputPreProcessResult;
import graphql.kickstart.servlet.input.BatchInputPreProcessor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author Magdalena Jedraszak
 */
public class BatchOperationLimitInputPreProcessor
	implements BatchInputPreProcessor {

	public BatchOperationLimitInputPreProcessor(int batchOperationLimit) {
		_batchOperationLimit = batchOperationLimit;
	}

	@Override
	public BatchInputPreProcessResult preProcessBatch(
		GraphQLBatchedInvocationInput graphQLBatchedInvocationInput,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		List<GraphQLSingleInvocationInput> invocationInputs =
			graphQLBatchedInvocationInput.getInvocationInputs();

		if ((_batchOperationLimit > 0) &&
			(invocationInputs.size() > _batchOperationLimit)) {

			return new BatchInputPreProcessResult(
				HttpServletResponse.SC_BAD_REQUEST,
				StringBundler.concat(
					"Batch size ", invocationInputs.size(),
					" is greater than the batch operation limit of ",
					_batchOperationLimit));
		}

		return new BatchInputPreProcessResult(graphQLBatchedInvocationInput);
	}

	private final int _batchOperationLimit;

}