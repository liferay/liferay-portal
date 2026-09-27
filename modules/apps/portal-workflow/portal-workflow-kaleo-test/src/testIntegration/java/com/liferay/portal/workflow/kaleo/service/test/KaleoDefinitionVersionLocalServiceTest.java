/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionVersionException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.util.comparator.KaleoDefinitionVersionTitleComparator;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Inácio Nery
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class KaleoDefinitionVersionLocalServiceTest
	extends BaseKaleoLocalServiceTestCase {

	@Test
	public void testAddKaleoDefinitionShouldCreateVersion() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				kaleoDefinition.getCompanyId(), kaleoDefinition.getName(),
				_getVersion(kaleoDefinition.getVersion()));

		Assert.assertEquals("1.0", kaleoDefinitionVersion.getVersion());

		Assert.assertNotNull(kaleoDefinitionVersion.getContentAsXML());

		String content = read("single-approver-workflow-definition.xml");

		kaleoDefinitionVersion.setContent(content);

		Assert.assertEquals(content, kaleoDefinitionVersion.getContentAsXML());
	}

	@Test(expected = NoSuchDefinitionVersionException.class)
	public void testDeleteKaleoDefinitionShouldDeleteVersion()
		throws Exception {

		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		deactivateKaleoDefinition(kaleoDefinition);

		deleteKaleoDefinition(kaleoDefinition);

		kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
			kaleoDefinition.getCompanyId(), kaleoDefinition.getName(),
			_getVersion(kaleoDefinition.getVersion()));
	}

	@Ignore
	@Test
	public void testGetLatestKaleoDefinitionVersions() throws Exception {
		KaleoDefinition kaleoDefinition1 = addKaleoDefinition(null);

		KaleoDefinitionVersion kaleoDefinition1LatestKaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
				kaleoDefinition1.getKaleoDefinitionId(),
				"KaleoDefinitionVersionName1", kaleoDefinition1.getTitle(),
				"KaleoDefinitionVersionDescription1",
				kaleoDefinition1.getContent(), "2.0", serviceContext);

		KaleoDefinition kaleoDefinition2 = addKaleoDefinition(null);

		KaleoDefinitionVersion kaleoDefinition2LatestKaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
				kaleoDefinition2.getKaleoDefinitionId(),
				"KaleoDefinitionVersionName2", "KaleoDefinitionVersionTitle2",
				kaleoDefinition2.getDescription(),
				kaleoDefinition2.getContent(), "2.0", serviceContext);

		KaleoDefinition kaleoDefinition3 = addKaleoDefinition(null);

		KaleoDefinitionVersion kaleoDefinition3LatestKaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
				kaleoDefinition3.getKaleoDefinitionId(),
				kaleoDefinition3.getName(), "KaleoDefinitionVersionTitle3",
				"KaleoDefinitionVersionDescription3",
				kaleoDefinition3.getContent(), "3.0", serviceContext);

		KaleoDefinitionVersion kaleoDefinition3SecondKaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
				kaleoDefinition3.getKaleoDefinitionId(),
				kaleoDefinition3.getName(), kaleoDefinition3.getTitle(),
				kaleoDefinition3.getDescription(),
				kaleoDefinition3.getContent(), "2.0", serviceContext);

		long kaleoDefinition3SecondKaleoDefinitionVersionId =
			kaleoDefinition3SecondKaleoDefinitionVersion.
				getKaleoDefinitionVersionId();

		long kaleoDefinition3LatestKaleoDefinitionVersionId =
			kaleoDefinition3LatestKaleoDefinitionVersion.
				getKaleoDefinitionVersionId();

		Assert.assertTrue(
			kaleoDefinition3SecondKaleoDefinitionVersionId >
				kaleoDefinition3LatestKaleoDefinitionVersionId);

		KaleoDefinitionVersionTitleComparator
			kaleoDefinitionVersionTitleComparator =
				new KaleoDefinitionVersionTitleComparator(true);

		Assert.assertEquals(
			Arrays.asList(
				kaleoDefinition1LatestKaleoDefinitionVersion,
				kaleoDefinition3LatestKaleoDefinitionVersion),
			kaleoDefinitionVersionLocalService.getLatestKaleoDefinitionVersions(
				kaleoDefinition1.getCompanyId(),
				"kaleodefinitionversiondescription",
				WorkflowConstants.STATUS_ANY, LocaleUtil.US, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, kaleoDefinitionVersionTitleComparator));
		Assert.assertEquals(
			Arrays.asList(
				kaleoDefinition1LatestKaleoDefinitionVersion,
				kaleoDefinition2LatestKaleoDefinitionVersion),
			kaleoDefinitionVersionLocalService.getLatestKaleoDefinitionVersions(
				kaleoDefinition1.getCompanyId(), "kaleodefinitionversionname",
				WorkflowConstants.STATUS_ANY, LocaleUtil.US, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, kaleoDefinitionVersionTitleComparator));
		Assert.assertEquals(
			Arrays.asList(
				kaleoDefinition2LatestKaleoDefinitionVersion,
				kaleoDefinition3LatestKaleoDefinitionVersion),
			kaleoDefinitionVersionLocalService.getLatestKaleoDefinitionVersions(
				kaleoDefinition1.getCompanyId(), "kaleodefinitionversiontitle",
				WorkflowConstants.STATUS_ANY, LocaleUtil.US, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, kaleoDefinitionVersionTitleComparator));
	}

	@Test
	public void testUpdateKaleoDefinitionShouldIncrementVersion1()
		throws Exception {

		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		kaleoDefinition = updateKaleoDefinition(kaleoDefinition);

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				kaleoDefinition.getCompanyId(), kaleoDefinition.getName(),
				_getVersion(kaleoDefinition.getVersion()));

		Assert.assertEquals("2.0", kaleoDefinitionVersion.getVersion());
	}

	@Inject
	protected KaleoDefinitionVersionLocalService
		kaleoDefinitionVersionLocalService;

	private String _getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

}