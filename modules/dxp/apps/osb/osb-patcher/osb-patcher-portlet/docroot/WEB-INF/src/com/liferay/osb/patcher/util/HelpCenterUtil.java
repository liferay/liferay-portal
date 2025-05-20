/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;

import java.io.File;
import java.io.FileInputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * @author Zsolt Balogh
 */
public class HelpCenterUtil {

	public static String addAttachmentComment(
			String fileName, PatcherBuild patcherBuild, String path)
		throws Exception {

		File file = new File(path);

		uploadAttachment(file, fileName, patcherBuild.getSupportTicket());

		Http.Options options = new Http.Options();

		String login =
			PortletPropsValues.HELP_CENTER_API_USERNAME + ":" +
				PortletPropsValues.HELP_CENTER_API_PASSWORD;

		options.addHeader(
			"Authorization", "Basic " + Base64.encode(login.getBytes()));

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.getPatcherAccount(
				patcherBuild.getPatcherAccountId());

		options.addPart(
			"accountEntryId",
			String.valueOf(patcherAccount.getAccountEntryId()));

		options.addPart("fileName", fileName);
		options.addPart(
			"fileRepositoryId", PortletPropsValues.HELP_CENTER_FILE_REPO_ID);
		options.addPart("fileSize", String.valueOf(file.length()));
		options.addPart("regionRestricted", "false");
		options.addPart("type", "1");
		options.addPart("zendeskTicketId", patcherBuild.getSupportTicket());

		options.setLocation(
			PortletPropsValues.HELP_CENTER_JSONWS_URL +
				StringPool.FORWARD_SLASH +
					PortletPropsValues.
						HELP_CENTER_TICKET_ATTACHMENT_API_ENDPOINT);
		options.setPost(true);

		return HttpUtil.URLtoString(options);
	}

	public static long fetchAccountEntryId(String accountEntryCode)
		throws Exception {

		Http.Options options = new Http.Options();

		String login =
			PortletPropsValues.HELP_CENTER_API_USERNAME + ":" +
				PortletPropsValues.HELP_CENTER_API_PASSWORD;

		options.addHeader(
			"Authorization", "Basic " + Base64.encode(login.getBytes()));

		options.addPart("code", accountEntryCode);

		options.setLocation(
			PortletPropsValues.HELP_CENTER_JSONWS_URL +
				StringPool.FORWARD_SLASH +
					PortletPropsValues.HELP_CENTER_GET_ACCOUNT_API_ENDPOINT);
		options.setPost(true);

		String response = HttpUtil.URLtoString(options);

		response = response.replace(StringPool.QUOTE, StringPool.BLANK);

		Pattern pattern = Pattern.compile(
			PatcherConstants.HELP_CENTER_ACCOUNT_ID_REGEX);

		Matcher matcher = pattern.matcher(response);

		if (matcher.find()) {
			return GetterUtil.getLong(matcher.group(1));
		}

		return 0;
	}

	protected static String getAttachmentToken(String supportTicket)
		throws Exception {

		Http.Options options = new Http.Options();

		String uploadTokenURL =
			PortletPropsValues.HELP_CENTER_FILE_REPO_URL +
				StringPool.FORWARD_SLASH + "token";

		String dirPath =
			PortletPropsValues.HELP_CENTER_TOKEN_TICKET_DIR +
				StringPool.FORWARD_SLASH + supportTicket;

		uploadTokenURL = HttpUtil.addParameter(
			uploadTokenURL, "dirPath", dirPath);

		options.setLocation(uploadTokenURL);

		return HttpUtil.URLtoString(options);
	}

	protected static void uploadAttachment(
			File file, String fileName, String supportTicket)
		throws Exception {

		String uploadURL =
			PortletPropsValues.HELP_CENTER_FILE_REPO_URL +
				StringPool.FORWARD_SLASH + "upload";

		uploadURL = HttpUtil.addParameter(uploadURL, "resumableChunkNumber", 1);
		uploadURL = HttpUtil.addParameter(
			uploadURL, "resumableChunkSize", 26214400);
		uploadURL = HttpUtil.addParameter(
			uploadURL, "resumableFilename", fileName);
		uploadURL = HttpUtil.addParameter(uploadURL, "resumableTotalChunks", 1);
		uploadURL = HttpUtil.addParameter(
			uploadURL, "resumableTotalSize", file.length());
		uploadURL = HttpUtil.addParameter(
			uploadURL, "token", getAttachmentToken(supportTicket));

		URL url = new URL(uploadURL);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty(
			"Content-Type", "application/octet-stream");

		IOUtils.copy(
			new FileInputStream(file), httpURLConnection.getOutputStream());

		IOUtils.toString(httpURLConnection.getInputStream());
	}

}