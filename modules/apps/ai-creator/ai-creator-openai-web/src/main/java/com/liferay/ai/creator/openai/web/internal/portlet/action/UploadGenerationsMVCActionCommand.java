/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.portlet.action;

import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIPortletKeys;
import com.liferay.ai.creator.openai.web.internal.upload.AICreatorOpenAIUploadFileEntryHandler;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI,
		"mvc.command.name=/ai_creator_openai/upload_generations"
	},
	service = MVCActionCommand.class
)
public class UploadGenerationsMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_aiCreatorOpenAIFileEntryHandler =
			new AICreatorOpenAIUploadFileEntryHandler(_dlAppService);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_aiCreatorOpenAIFileEntryHandler, _defaultUploadResponseHandler,
			actionRequest, actionResponse);
	}

	private volatile AICreatorOpenAIUploadFileEntryHandler
		_aiCreatorOpenAIFileEntryHandler;

	@Reference(target = "(upload.response.handler.system.default=true)")
	private UploadResponseHandler _defaultUploadResponseHandler;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private UploadHandler _uploadHandler;

}