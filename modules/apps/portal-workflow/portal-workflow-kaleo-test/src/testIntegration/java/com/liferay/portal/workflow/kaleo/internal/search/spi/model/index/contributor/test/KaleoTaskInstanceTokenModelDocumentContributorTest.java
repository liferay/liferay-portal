/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.test.BaseKaleoLocalServiceTestCase;

import java.io.Serializable;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Selena Aungst
 */
@RunWith(Arquillian.class)
public class KaleoTaskInstanceTokenModelDocumentContributorTest
	extends BaseKaleoLocalServiceTestCase {

	@Test
	public void testContributeWhenWorkflowHandlerIsNotRegistered()
		throws Exception {

		KaleoDefinition kaleoDefinition = addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_ALL);

		KaleoDefinitionVersion kaleoDefinitionVersion =
			getLatestKaleoDefinitionVersion(kaleoDefinition);

		String className = RandomTestUtil.randomString();

		KaleoInstance kaleoInstance =
			_kaleoInstanceLocalService.addKaleoInstance(
				kaleoDefinition.getKaleoDefinitionId(),
				kaleoDefinitionVersion.getKaleoDefinitionVersionId(),
				kaleoDefinition.getName(), 1,
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME, className
				).put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_PK, "1"
				).build(),
				serviceContext);

		KaleoInstanceToken kaleoInstanceToken = addKaleoInstanceToken(
			kaleoInstance);

		String taskName = RandomTestUtil.randomString();

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
				kaleoInstanceToken.getKaleoInstanceTokenId(), 1, taskName,
				Collections.emptyList(), null,
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME, className
				).put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_PK, "1"
				).build(),
				serviceContext);

		Document document = new DocumentImpl();

		_modelDocumentContributor.contribute(document, kaleoTaskInstanceToken);

		Assert.assertEquals(className, document.get("className"));
		Assert.assertEquals(
			String.valueOf(kaleoDefinition.getKaleoDefinitionId()),
			document.get("kaleoDefinitionId"));
		Assert.assertEquals(taskName, document.get("taskName"));
	}

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.internal.search.spi.model.index.contributor.KaleoTaskInstanceTokenModelDocumentContributor"
	)
	private ModelDocumentContributor<KaleoTaskInstanceToken>
		_modelDocumentContributor;

}