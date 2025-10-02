/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.channels.Channels;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * @author Zsolt Balogh
 */
public class HelpCenterUtil {

	public static String addAttachmentComment(
			String fileName, PatcherBuild patcherBuild)
		throws Exception {

		StorageOptions storageOptions = StorageOptions.getDefaultInstance();

		Storage storage = storageOptions.getService();

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, patcherBuild.getCompanyId());

		BlobId blobId = BlobId.of(
			patcherConfiguration.googleCloudHotfixBucket(),
			patcherBuild.getFileName());

		Blob blob = storage.get(blobId);

		if (blob == null) {
			throw new PortalException(
				LanguageUtil.format(
					LocaleUtil.getMostRelevantLocale(),
					"file-x-not-found-in-the-x-gcs-bucket",
					new Object[] {
						fileName, patcherConfiguration.googleCloudHotfixBucket()
					}));
		}

		long fileSize = blob.getSize();

		try {
			InputStream fileInputStream = Channels.newInputStream(
				blob.reader());

			uploadAttachment(
				patcherBuild.getCompanyId(), fileInputStream, fileName,
				fileSize, patcherBuild.getSupportTicket());
		}
		catch (Exception e) {
			throw new Exception("Error processing GCS file.", e);
		}

		Http.Options options = new Http.Options();

		String login =
			patcherConfiguration.helpCenterApiUserName() + ":" +
				patcherConfiguration.helpCenterApiPassword();

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
			"fileRepositoryId", patcherConfiguration.helpCenterFileRepoId());
		options.addPart("fileSize", String.valueOf(fileSize));
		options.addPart("regionRestricted", "false");
		options.addPart("type", "1");
		options.addPart("zendeskTicketId", patcherBuild.getSupportTicket());

		String helpCenterTicketAttachmentApiEndpoint =
			patcherConfiguration.helpCenterTicketAttachmentApiEndpoint();

		options.setLocation(
			patcherConfiguration.helpCenterJsonwsURL() +
				StringPool.FORWARD_SLASH +
					helpCenterTicketAttachmentApiEndpoint);

		options.setPost(true);

		return HttpUtil.URLtoString(options);
	}

	public static long fetchAccountEntryId(
			String accountEntryCode, long companyId)
		throws Exception {

		Http.Options options = new Http.Options();

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, companyId);

		String login =
			patcherConfiguration.helpCenterApiUserName() + ":" +
				patcherConfiguration.helpCenterApiPassword();

		options.addHeader(
			"Authorization", "Basic " + Base64.encode(login.getBytes()));

		options.addPart("code", accountEntryCode);

		options.setLocation(
			patcherConfiguration.helpCenterJsonwsURL() +
				StringPool.FORWARD_SLASH +
					patcherConfiguration.helpCenterGetAccountApiEndpoint());
		options.setPost(true);

		String response = StringUtil.removeSubstring(
			HttpUtil.URLtoString(options), StringPool.QUOTE);

		Pattern pattern = Pattern.compile(
			PatcherConstants.HELP_CENTER_ACCOUNT_ID_REGEX);

		Matcher matcher = pattern.matcher(response);

		if (matcher.find()) {
			return GetterUtil.getLong(matcher.group(1));
		}

		return 0;
	}

	protected static String getAttachmentToken(
			long companyId, String supportTicket)
		throws Exception {

		Http.Options options = new Http.Options();

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, companyId);

		String uploadTokenURL =
			patcherConfiguration.helpCenterFileRepoURL() + "/token";

		String dirPath =
			patcherConfiguration.helpCenterTokenTicketDir() +
				StringPool.FORWARD_SLASH + supportTicket;

		uploadTokenURL = HttpComponentsUtil.addParameter(
			uploadTokenURL, "dirPath", dirPath);

		options.setLocation(uploadTokenURL);

		return HttpUtil.URLtoString(options);
	}

	protected static void uploadAttachment(
			long companyId, InputStream fileInputStream, String fileName,
			long fileSize, String supportTicket)
		throws Exception {

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, companyId);

		String uploadURL =
			patcherConfiguration.helpCenterFileRepoURL() + "/upload";

		uploadURL = HttpComponentsUtil.addParameter(
			uploadURL, "resumableChunkNumber", 1);
		uploadURL = HttpComponentsUtil.addParameter(
			uploadURL, "resumableChunkSize", 26214400);
		uploadURL = HttpComponentsUtil.addParameter(
			uploadURL, "resumableFilename", fileName);
		uploadURL = HttpComponentsUtil.addParameter(
			uploadURL, "resumableTotalChunks", 1);
		uploadURL = HttpComponentsUtil.addParameter(
			uploadURL, "resumableTotalSize", fileSize);
		uploadURL = HttpComponentsUtil.addParameter(
			uploadURL, "token", getAttachmentToken(companyId, supportTicket));

		URL url = new URL(uploadURL);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty(
			"Content-Type", "application/octet-stream");

		IOUtils.copy(fileInputStream, httpURLConnection.getOutputStream());

		IOUtils.toString(httpURLConnection.getInputStream());

		httpURLConnection.disconnect();
	}

}