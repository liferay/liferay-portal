/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.headless.form.client.dto.v1_0.FormDocument;
import com.liferay.headless.form.dto.v1_0.util.FormDocumentUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class FormDocumentResourceTest extends BaseFormDocumentResourceTestCase {

	@Override
	protected FormDocument testDeleteFormDocument_addFormDocument()
		throws Exception {

		return _addFormDocument(testGroup.getGroupId());
	}

	@Override
	protected FormDocument testGetFormDocument_addFormDocument()
		throws Exception {

		return _addFormDocument(testGroup.getGroupId());
	}

	@Override
	protected FormDocument testGraphQLFormDocument_addFormDocument()
		throws Exception {

		return _addFormDocument(testGroup.getGroupId());
	}

	private FormDocument _addFormDocument(Long groupId) throws Exception {
		com.liferay.headless.form.dto.v1_0.FormDocument formDocument =
			FormDocumentUtil.toFormDocument(
				_dlurlHelper,
				DLAppLocalServiceUtil.addFileEntry(
					null, TestPropsValues.getUserId(), groupId,
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
					RandomTestUtil.randomString(), StringUtil.randomString(),
					StringUtil.randomString(), StringUtil.randomString(),
					new byte[0], null, null, null, new ServiceContext()));

		return FormDocument.toDTO(formDocument.toString());
	}

	@Inject(type = DLURLHelper.class)
	private DLURLHelper _dlurlHelper;

}