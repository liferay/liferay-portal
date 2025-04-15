/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.upload.test;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UniqueFileNameProvider;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;

/**
 * @author Manuel de la Peña
 */
public class TestUploadMVCActionCommand extends BaseMVCActionCommand {

	public TestUploadMVCActionCommand(
		TestUploadPortlet testUploadPortlet,
		UniqueFileNameProvider uniqueFileNameProvider) {

		_testUploadHandler = new TestUploadHandler(
			testUploadPortlet, uniqueFileNameProvider);
	}

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		_testUploadHandler.upload(actionRequest, actionResponse);

		Assert.assertNull(actionRequest.getAttribute(WebKeys.UPLOAD_EXCEPTION));
	}

	private final TestUploadHandler _testUploadHandler;

}