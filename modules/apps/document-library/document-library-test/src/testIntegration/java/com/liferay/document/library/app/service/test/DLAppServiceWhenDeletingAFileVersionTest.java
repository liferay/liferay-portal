/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.app.service.test.util.DLAppServiceTestUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class DLAppServiceWhenDeletingAFileVersionTest
	extends BaseDLAppTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-107151")
	public void testDeleteFileVersion() throws Exception {
		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());

		FileVersion fileVersion = fileEntry.getFileVersion();

		DLAppServiceTestUtil.updateFileEntry(
			group.getGroupId(), fileEntry.getFileEntryId(),
			RandomTestUtil.randomString(), null, null, null, true);

		UserTestUtil.setUser(UserTestUtil.addUser(group.getGroupId()));

		try {
			dlAppService.deleteFileVersion(fileVersion.getFileVersionId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertNotNull(principalException);
		}

		Assert.assertNotNull(
			_dlFileVersionLocalService.fetchDLFileVersion(
				fileVersion.getFileVersionId()));

		UserTestUtil.setUser(TestPropsValues.getUser());

		dlAppService.deleteFileVersion(fileVersion.getFileVersionId());

		Assert.assertNull(
			_dlFileVersionLocalService.fetchDLFileVersion(
				fileVersion.getFileVersionId()));
	}

	@Inject
	private DLFileVersionLocalService _dlFileVersionLocalService;

}