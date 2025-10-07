/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.lock.service.LockLocalServiceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zsolt Balogh
 */
public class PatcherUtil {

	public static void addMessage(String message, List<String> messages) {
		messages.add(message);

		if (_log.isDebugEnabled()) {
			_log.debug(message);
		}
	}

	public static boolean equals(
			List<Long> patcherFixIds1, List<Long> patcherFixIds2)
		throws Exception {

		patcherFixIds1 = ListUtil.copy(patcherFixIds1);

		Collections.sort(patcherFixIds1);

		patcherFixIds2 = ListUtil.copy(patcherFixIds2);

		Collections.sort(patcherFixIds2);

		return patcherFixIds1.equals(patcherFixIds2);
	}

	public static String generatePatcherKey(Object... arguments)
		throws Exception {

		if (arguments.length <= 0) {
			throw new Exception("Arguments are empty");
		}

		String key = StringUtil.merge(arguments, StringPool.BLANK);

		return DigesterUtil.digestHex(StringUtil.toUpperCase(key));
	}

	public static List<String> getCurrentTickets(
		PatcherFixPack patcherFixPack) {

		List<String> currentTickets = new ArrayList<>();

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFix : patcherFixes) {
			currentTickets.addAll(getTokens(patcherFix.getName()));
		}

		return currentTickets;
	}

	public static List<String> getNewTickets(PatcherFixPack patcherFixPack)
		throws Exception {

		List<String> newTickets = getCurrentTickets(patcherFixPack);

		newTickets.removeAll(getOldTickets(patcherFixPack));

		return sortTokens(newTickets);
	}

	public static String getNextPatcherBuilderStatusMsg(long companyId)
		throws Exception {

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, companyId);

		if (Validator.isNull(
				patcherConfiguration.patcherPubsubCredentialFilePath())) {

			return null;
		}

		ServiceAccountCredentials serviceAccountCredentials =
			ServiceAccountCredentials.fromStream(
				new FileInputStream(
					patcherConfiguration.patcherPubsubCredentialFilePath()));

		SubscriberStubSettings subscriberStubSettings =
			SubscriberStubSettings.newBuilder(
			).setCredentialsProvider(
				FixedCredentialsProvider.create(serviceAccountCredentials)
			).setTransportChannelProvider(
				SubscriberStubSettings.defaultGrpcTransportProviderBuilder(
				).setMaxInboundMessageSize(
					20 * 1024 * 1024
				).build()
			).build();

		SubscriberStub subscriber = null;

		try {
			subscriber = GrpcSubscriberStub.create(subscriberStubSettings);

			String subscriptionName = ProjectSubscriptionName.format(
				patcherConfiguration.patcherPubsubProjectId(),
				patcherConfiguration.patcherPubsubSubscriptionId());

			PullRequest pullRequest = PullRequest.newBuilder(
			).setMaxMessages(
				1
			).setSubscription(
				subscriptionName
			).build();

			UnaryCallable<PullRequest, PullResponse> pullUnaryCallable =
				subscriber.pullCallable();

			PullResponse pullResponse = pullUnaryCallable.call(pullRequest);

			List<ReceivedMessage> receivedMessageList =
				pullResponse.getReceivedMessagesList();

			ReceivedMessage receivedMessage = receivedMessageList.get(0);

			AcknowledgeRequest acknowledgeRequest =
				AcknowledgeRequest.newBuilder(
				).setSubscription(
					subscriptionName
				).addAllAckIds(
					Collections.singleton(receivedMessage.getAckId())
				).build();

			UnaryCallable<AcknowledgeRequest, Empty> acknowledgeUnaryCallable =
				subscriber.acknowledgeCallable();

			acknowledgeUnaryCallable.call(acknowledgeRequest);

			subscriber.close();

			PubsubMessage pubsubMessage = receivedMessage.getMessage();

			ByteString pubsubMessageData = pubsubMessage.getData();

			return pubsubMessageData.toStringUtf8();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			subscriber.close();
		}

		return null;
	}

	public static List<String> getOldTickets(PatcherFixPack patcherFixPack) {
		List<String> oldTickets = new ArrayList<>();

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			oldTickets.addAll(getCurrentTickets(patcherFixPackVersion));
		}

		return oldTickets;
	}

	public static List<String> getOverriddenTickets(
		PatcherFixPack patcherFixPack) {

		List<String> overriddenTickets = getOldTickets(patcherFixPack);

		overriddenTickets.retainAll(getCurrentTickets(patcherFixPack));

		return sortTokens(overriddenTickets);
	}

	public static List<String> getTickets(String name) {
		List<String> tickets = new ArrayList<>();

		List<String> tokens = getTokens(name);

		for (String token : tokens) {
			Pattern pattern = Pattern.compile(
				PatcherConstants.TICKET_NAME_ALL_REGEX);

			Matcher matcher = pattern.matcher(token);

			if (!matcher.find()) {
				continue;
			}

			tickets.add(matcher.group(1));
		}

		return tickets;
	}

	public static int getTicketsCount(String name) {
		List<String> tickets = getTickets(name);

		return tickets.size();
	}

	public static List<String> getTokens(String name) {
		return ListUtil.fromArray(StringUtil.split(name));
	}

	public static boolean isPatcherProjectVersionName(String name) {
		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.
				fetchPatcherProjectVersionByName(name);

		if (patcherProjectVersion == null) {
			return false;
		}

		return true;
	}

	public static boolean isPatcherTickets(String name) {
		return isPatcherTickets(name, PatcherConstants.TICKET_NAME_ALL_REGEX);
	}

	public static boolean isPatcherTickets(
		String name, long patcherProductVersionId) {

		if (patcherProductVersionId ==
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			return isPatcherTickets(
				name, PatcherConstants.TICKET_NAME_6X_REGEX);
		}

		return isPatcherTickets(name);
	}

	public static boolean isPatcherTickets(
		String name, String ticketNameRegex) {

		name = unprepareKeywords(name);

		if (Validator.isNull(name)) {
			return false;
		}

		Pattern pattern = Pattern.compile(ticketNameRegex);

		for (String token : StringUtil.split(name)) {
			Matcher matcher = pattern.matcher(StringUtil.trim(token));

			if (!matcher.find()) {
				return false;
			}
		}

		return true;
	}

	public static void notifyUsersInactivePatcherBaseModels() throws Exception {
		PatcherBuildUtil.notifyUsersInactivePatcherBuilds();

		PatcherFixUtil.notifyUsersInactivePatcherFixes();
	}

	public static String preparePatcherName(String name) {
		if (Validator.isNull(name)) {
			return StringPool.BLANK;
		}

		return StringUtil.replace(
			name, new String[] {StringPool.NEW_LINE, StringPool.SPACE},
			new String[] {StringPool.BLANK, StringPool.BLANK});
	}

	public static void processOSBPatcherMessageQueue(long companyId)
		throws Exception {

		long defaultUserId = UserLocalServiceUtil.getDefaultUserId(companyId);

		String lockClassName = PatcherBuild.class.getName() + "_Jenkins";

		if (LockLocalServiceUtil.hasLock(
				defaultUserId, lockClassName, companyId)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping ", lockClassName,
						" file processing for company ", companyId,
						"because it is currently running"));
			}

			return;
		}

		try {
			LockLocalServiceUtil.lock(
				defaultUserId, lockClassName, companyId, lockClassName, false,
				Time.HOUR);

			String jenkinsStatusJSONString = getNextPatcherBuilderStatusMsg(
				companyId);

			if (Validator.isNotNull(jenkinsStatusJSONString) &&
				_log.isInfoEnabled()) {

				_log.info(
					"Received PubSub message: " + jenkinsStatusJSONString);
			}
			else {
				return;
			}

			JSONObject jenkinsStatusJSONObject =
				JSONFactoryUtil.createJSONObject(jenkinsStatusJSONString);

			String patcherId = jenkinsStatusJSONObject.getString(
				"patcherBuildId");

			if (!Validator.isNumber(patcherId)) {
				_log.error("Patcher ID is not a number: " + patcherId);

				return;
			}

			PatcherBuildUtil.processOSBPatcherBuildCompileJenkinsStatus(
				UserLocalServiceUtil.fetchUser(
					jenkinsStatusJSONObject.getLong("patcherUserId")),
				GetterUtil.getLong(patcherId), jenkinsStatusJSONString);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			LockLocalServiceUtil.unlock(lockClassName, companyId);
		}
	}

	public static void processOSBPatcherStatusFiles(long companyId, String path)
		throws Exception {

		User defaultUser = UserLocalServiceUtil.getDefaultUser(companyId);

		String lockClassName = PatcherFix.class.getName();

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, companyId);

		String patcherStatusPath = patcherConfiguration.patcherStatusPath();

		String patcherStatusBuildJenkinsPath =
			patcherStatusPath +
				patcherConfiguration.patcherStatusBuildJenkinsPath();
		String patcherStatusBuildJenkinsTestPath =
			patcherStatusPath +
				patcherConfiguration.patcherStatusBuildJenkinsTestPath();
		String patcherStatusBuildPath =
			patcherStatusPath + patcherConfiguration.patcherStatusBuildPath();

		if (Objects.equals(path, patcherStatusBuildJenkinsPath)) {
			lockClassName = PatcherBuild.class.getName() + "_Jenkins";
		}
		else if (Objects.equals(path, patcherStatusBuildJenkinsTestPath)) {
			lockClassName = PatcherBuild.class.getName() + "_Jenkins_Test";
		}
		else if (Objects.equals(path, patcherStatusBuildPath)) {
			lockClassName = PatcherBuild.class.getName() + "_Build";
		}

		if (LockLocalServiceUtil.hasLock(
				defaultUser.getUserId(), lockClassName, companyId)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping ", lockClassName,
						" file processing for company ", companyId,
						"because it is currently running"));
			}

			return;
		}

		try {
			LockLocalServiceUtil.lock(
				defaultUser.getUserId(), lockClassName, companyId,
				lockClassName, false, Time.HOUR);

			String[] patcherFileNames = FileUtil.listFiles(path);

			for (String patcherFileName : patcherFileNames) {
				String[] splits = patcherFileName.split("-");

				String patcherId = StringPool.BLANK;

				if (splits.length > 1) {
					patcherId = splits[1];
				}
				else {
					patcherId = splits[0];
				}

				if (!Validator.isNumber(patcherId)) {
					_log.error(
						"Patcher ID is not a number for file " +
							patcherFileName);

					continue;
				}

				File patcherFile = new File(
					path + StringPool.SLASH + patcherFileName);

				String jenkinsStatusJSONString = FileUtil.read(patcherFile);

				FileUtil.delete(patcherFile);

				if (Validator.isNull(jenkinsStatusJSONString)) {
					continue;
				}

				JSONObject jenkinsStatusJSONObject =
					JSONFactoryUtil.createJSONObject(jenkinsStatusJSONString);

				long userId = jenkinsStatusJSONObject.getLong("patcherUserId");

				User user = UserLocalServiceUtil.fetchUser(userId);

				try {
					if (Objects.equals(path, patcherStatusBuildJenkinsPath)) {
						PatcherBuildUtil.
							processOSBPatcherBuildCompileJenkinsStatus(
								user, GetterUtil.getLong(patcherId),
								jenkinsStatusJSONString);
					}
					else if (Objects.equals(
								path, patcherStatusBuildJenkinsTestPath)) {

						PatcherBuildUtil.
							processOSBPatcherBuildTestJenkinsStatus(
								user, GetterUtil.getLong(patcherId),
								jenkinsStatusJSONString);
					}
					else if (Objects.equals(path, patcherStatusBuildPath)) {
						PatcherBuildUtil.
							processOSBPatcherBuildMergeJenkinsStatus(
								user, GetterUtil.getLong(patcherId),
								jenkinsStatusJSONString);
					}
					else {
						PatcherFixUtil.processOSBPatcherFixAddJenkinsStatus(
							GetterUtil.getLong(patcherId),
							jenkinsStatusJSONString, defaultUser);
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			LockLocalServiceUtil.unlock(lockClassName, companyId);
		}
	}

	public static Hits search(
			String className, Map<String, Serializable> attributes,
			String keywords, ThemeDisplay themeDisplay)
		throws Exception {

		Class<?> clazz = Class.forName(className);

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(clazz);

		if (indexer == null) {
			throw new Exception("No indexer found for class " + className);
		}

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(true);

		if ((attributes != null) && !attributes.isEmpty()) {
			searchContext.setAttributes(attributes);
		}

		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setGroupIds(new long[] {themeDisplay.getScopeGroupId()});
		searchContext.setUserId(themeDisplay.getUserId());

		searchContext.setEnd(QueryUtil.ALL_POS);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		searchContext.setSorts(new Sort[] {new Sort()});
		searchContext.setStart(QueryUtil.ALL_POS);

		String indexerClassName = indexer.getSearchClassNames()[0];

		Class<?> indexerClass = Class.forName(indexerClassName);

		try {
			indexerClass.getField(Field.GROUP_ID);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			searchContext.setGroupIds(null);
		}

		return indexer.search(searchContext);
	}

	public static List<String> sortTokens(List<String> tokens) {
		return sortTokens(StringUtil.merge(tokens));
	}

	public static List<String> sortTokens(String name) {
		if (Validator.isNull(name)) {
			return Collections.emptyList();
		}

		List<String> tokens = ListUtil.fromArray(name.split("\\s*,\\s*"));

		ListUtil.distinct(tokens);

		Collections.sort(tokens);

		return tokens;
	}

	public static String unprepareKeywords(String keywords) {
		if (Validator.isNull(keywords)) {
			return StringPool.BLANK;
		}

		keywords = keywords.replaceFirst("^\"", StringPool.BLANK);
		keywords = keywords.replaceFirst("\"$", StringPool.BLANK);

		String[] keywordsArray = keywords.split("\" \"");

		return StringUtil.merge(keywordsArray, StringPool.COMMA_AND_SPACE);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		"jsp.osb.patcher.util.PatcherUtil");

}