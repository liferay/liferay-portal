/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.google.api.gax.core.CredentialsProvider;
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

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.web.internal.constants.PatcherConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
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
import com.liferay.portal.kernel.util.ServiceBeanMethodInvocationFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.lock.service.LockLocalServiceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public static List<String> getCurrentTickets(PatcherFixPack patcherFixPack)
		throws Exception {

		List<String> currentTickets = new ArrayList<>();

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFix : patcherFixes) {
			currentTickets.addAll(getTokens(patcherFix.getName()));
		}

		return currentTickets;
	}

	public static List<String> getNewTickets(PatcherFixPack patcherFixPack)
		throws Exception {

		List<String> newTickets = getCurrentTickets(patcherFixPack);

		List<String> oldTickets = getOldTickets(patcherFixPack);

		newTickets.removeAll(oldTickets);

		return PatcherUtil.sortTokens(newTickets);
	}

	public static String getNextPatcherBuilderStatusMsg() throws IOException {
		if (Validator.isNull(
				PortletPropsValues.OSB_PATCHER_PUBSUB_CREDENTIAL_FILE_PATH)) {

			return null;
		}

		ServiceAccountCredentials serviceAccountCredentials =
			ServiceAccountCredentials.fromStream(
				new FileInputStream(
					PortletPropsValues.
						OSB_PATCHER_PUBSUB_CREDENTIAL_FILE_PATH));

		CredentialsProvider credentialsProvider =
			FixedCredentialsProvider.create(serviceAccountCredentials);

		SubscriberStubSettings.Builder subscriberStubSettingsBuilder =
			SubscriberStubSettings.newBuilder();

		subscriberStubSettingsBuilder.setCredentialsProvider(
			credentialsProvider);
		subscriberStubSettingsBuilder.setTransportChannelProvider(
			SubscriberStubSettings.defaultGrpcTransportProviderBuilder(
			).setMaxInboundMessageSize(
				20 * 1024 * 1024
			).build());

		SubscriberStubSettings subscriberStubSettings =
			subscriberStubSettingsBuilder.build();

		SubscriberStub subscriber = null;

		try {
			subscriber = GrpcSubscriberStub.create(subscriberStubSettings);

			String subscriptionName = ProjectSubscriptionName.format(
				PortletPropsValues.OSB_PATCHER_PUBSUB_PROJECT_ID,
				PortletPropsValues.OSB_PATCHER_PUBSUB_SUBSCRIPTION_ID);

			PullRequest.Builder pullRequestBuilder = PullRequest.newBuilder();

			pullRequestBuilder.setMaxMessages(1);
			pullRequestBuilder.setSubscription(subscriptionName);

			PullRequest pullRequest = pullRequestBuilder.build();

			UnaryCallable<PullRequest, PullResponse> pullUnaryCallable =
				subscriber.pullCallable();

			PullResponse pullResponse = pullUnaryCallable.call(pullRequest);

			List<ReceivedMessage> receivedMessageList =
				pullResponse.getReceivedMessagesList();

			ReceivedMessage receivedMessage = receivedMessageList.get(0);

			List<String> acknowledgeIds = new ArrayList<>();

			acknowledgeIds.add(receivedMessage.getAckId());

			AcknowledgeRequest.Builder acknowledgeRequestBuilder =
				AcknowledgeRequest.newBuilder();

			acknowledgeRequestBuilder.setSubscription(subscriptionName);
			acknowledgeRequestBuilder.addAllAckIds(acknowledgeIds);

			AcknowledgeRequest acknowledgeRequest =
				acknowledgeRequestBuilder.build();

			UnaryCallable<AcknowledgeRequest, Empty> acknowledgeUnaryCallable =
				subscriber.acknowledgeCallable();

			acknowledgeUnaryCallable.call(acknowledgeRequest);

			subscriber.close();

			PubsubMessage pubsubMessage = receivedMessage.getMessage();

			ByteString pubsubMessageData = pubsubMessage.getData();

			return pubsubMessageData.toStringUtf8();
		}
		catch (Exception e) {
			subscriber.close();
		}

		return null;
	}

	public static List<String> getOldTickets(PatcherFixPack patcherFixPack)
		throws Exception {

		List<String> oldTickets = new ArrayList<>();

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			oldTickets.addAll(getCurrentTickets(patcherFixPackVersion));
		}

		return oldTickets;
	}

	public static List<String> getOverriddenTickets(
			PatcherFixPack patcherFixPack)
		throws Exception {

		List<String> overriddenTickets = getOldTickets(patcherFixPack);

		overriddenTickets.retainAll(getCurrentTickets(patcherFixPack));

		return PatcherUtil.sortTokens(overriddenTickets);
	}

	public static Map<String, Object> getPropertiesMap(Object... properties)
		throws Exception {

		Map<String, Object> propertiesMap = new HashMap<>();

		for (int i = 0; i < properties.length; i += 2) {
			String propertyName = String.valueOf(properties[i]);
			Object propertyValue = properties[i + 1];

			propertiesMap.put(propertyName, propertyValue);
		}

		return propertiesMap;
	}

	public static List<String> getTickets(String name) throws Exception {
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

	public static int getTicketsCount(String name) throws Exception {
		List<String> tickets = getTickets(name);

		return tickets.size();
	}

	public static List<String> getTokens(String name) throws Exception {
		return ListUtil.fromArray(StringUtil.split(name));
	}

	public static String getUserDisplayURL(
			ThemeDisplay themeDisplay, long userId)
		throws Exception {

		User user = UserLocalServiceUtil.fetchUser(userId);

		if (user == null) {
			return StringPool.BLANK;
		}

		if (Validator.isNull(PortletPropsValues.LIFERAY_USERS_PROFILE_URL)) {
			return user.getDisplayURL(themeDisplay);
		}

		return StringUtil.replace(
			PortletPropsValues.LIFERAY_USERS_PROFILE_URL,
			"${liferay:screenName}", user.getScreenName());
	}

	public static boolean isPatcherProjectVersionName(String name)
		throws Exception {

		AlloyServiceInvoker PatcherProjectVersionAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherProjectVersion.class.getName());

		List<PatcherProjectVersion> patcherProjectVersions =
			PatcherProjectVersionAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"name", name});

		return !patcherProjectVersions.isEmpty();
	}

	public static boolean isPatcherTickets(String name) throws Exception {
		return isPatcherTickets(name, PatcherConstants.TICKET_NAME_ALL_REGEX);
	}

	public static boolean isPatcherTickets(
			String name, long patcherProductVersionId)
		throws Exception {

		if (patcherProductVersionId ==
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			return isPatcherTickets(
				name, PatcherConstants.TICKET_NAME_6X_REGEX);
		}

		return isPatcherTickets(name);
	}

	public static boolean isPatcherTickets(String name, String ticketNameRegex)
		throws Exception {

		Pattern pattern = Pattern.compile(ticketNameRegex);

		name = unprepareKeywords(name);

		if (Validator.isNull(name)) {
			return false;
		}

		String[] tokens = StringUtil.split(name);

		for (String token : tokens) {
			Matcher matcher = pattern.matcher(StringUtil.trim(token));

			if (!matcher.find()) {
				return false;
			}
		}

		return true;
	}

	public static void notifyUsersInactivePatcherBaseModels(
			AlloyController alloyController)
		throws Exception {

		PatcherBuildUtil.notifyUsersInactivePatcherBuilds(alloyController);

		PatcherFixUtil.notifyUsersInactivePatcherFixes(alloyController);
	}

	public static void pollIndexState(
			AlloyController alloyController, String className, long classPK,
			Object... attributes)
		throws Exception {

		Map<String, Serializable> attributesMap = getSearchAttributes(
			attributes);

		attributesMap.put(Field.ENTRY_CLASS_PK, classPK);

		for (int i = 0; i < _POLL_INDEX_MAX_COUNT; i++) {
			Hits hits = search(alloyController, className, attributesMap, null);

			Document[] documents = hits.getDocs();

			if (documents.length > 0) {
				return;
			}

			Thread.sleep(_POLL_INDEX_WAIT_INTERVAL);
		}
	}

	public static String prepareKeywords(String keywords) throws Exception {
		if (Validator.isNull(keywords)) {
			return StringPool.BLANK;
		}

		String[] keywordsArray = keywords.split("\\s*(,|\\s)\\s*");

		return StringPool.QUOTE + StringUtil.merge(keywordsArray, "\" \"") +
			StringPool.QUOTE;
	}

	public static String preparePatcherName(String name) throws Exception {
		if (Validator.isNull(name)) {
			return StringPool.BLANK;
		}

		return StringUtil.replace(
			name, new String[] {StringPool.NEW_LINE, StringPool.SPACE},
			new String[] {StringPool.BLANK, StringPool.BLANK});
	}

	public static void processOSBPatcherMessageQueue(
			AlloyController alloyController)
		throws Exception {

		ThemeDisplay themeDisplay = alloyController.getThemeDisplay();

		long defaultUserId = UserLocalServiceUtil.getDefaultUserId(
			themeDisplay.getCompanyId());

		Class<?> clazz = PatcherBuildUtil.class;

		String lockClassName = PatcherBuild.class.getName() + "_Jenkins";

		String methodName = "processOSBPatcherBuildCompileJenkinsStatus";

		if (LockLocalServiceUtil.hasLock(
				defaultUserId, lockClassName, themeDisplay.getCompanyId())) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping " + lockClassName +
						" file processing for company " +
							themeDisplay.getCompanyId() +
								"because it is currently running");
			}

			return;
		}

		try {
			LockLocalServiceUtil.lock(
				defaultUserId, lockClassName, themeDisplay.getCompanyId(),
				lockClassName, false, Time.HOUR);

			String jenkinsStatusJSONString = getNextPatcherBuilderStatusMsg();

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

			long userId = jenkinsStatusJSONObject.getLong("patcherUserId");

			User user = UserLocalServiceUtil.fetchUser(userId);

			if (!Validator.isNumber(patcherId)) {
				_log.error("Patcher ID is not a number: " + patcherId);

				return;
			}

			if (user != null) {
				alloyController.setUser(user);
			}

			try {
				Method processOSBPatcherStatusMessageMethod =
					clazz.getDeclaredMethod(
						methodName,
						new Class<?>[] {
							AlloyController.class, User.class, long.class,
							String.class
						});

				ServiceBeanMethodInvocationFactoryUtil.proceed(
					null, clazz, processOSBPatcherStatusMessageMethod,
					new Object[] {
						alloyController, user, GetterUtil.getLong(patcherId),
						jenkinsStatusJSONString
					},
					new String[] {"transactionAdvice"});
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			LockLocalServiceUtil.unlock(
				lockClassName, themeDisplay.getCompanyId());
		}
	}

	public static void processOSBPatcherStatusFiles(
			AlloyController alloyController, String path)
		throws Exception {

		ThemeDisplay themeDisplay = alloyController.getThemeDisplay();

		long defaultUserId = UserLocalServiceUtil.getDefaultUserId(
			themeDisplay.getCompanyId());

		Class<?> clazz = null;

		String lockClassName = null;

		String methodName = null;

		if (path.equals(
				PortletPropsValues.OSB_PATCHER_STATUS_BUILD_JENKINS_PATH)) {

			clazz = PatcherBuildUtil.class;

			lockClassName = PatcherBuild.class.getName() + "_Jenkins";

			methodName = "processOSBPatcherBuildCompileJenkinsStatus";
		}
		else if (path.equals(
					PortletPropsValues.
						OSB_PATCHER_STATUS_BUILD_JENKINS_TEST_PATH)) {

			clazz = PatcherBuildUtil.class;

			lockClassName = PatcherBuild.class.getName() + "_Jenkins_Test";

			methodName = "processOSBPatcherBuildTestJenkinsStatus";
		}
		else if (path.equals(
					PortletPropsValues.OSB_PATCHER_STATUS_BUILD_PATH)) {

			clazz = PatcherBuildUtil.class;

			lockClassName = PatcherBuild.class.getName() + "_Build";

			methodName = "processOSBPatcherBuildMergeJenkinsStatus";
		}
		else {
			clazz = PatcherFixUtil.class;

			lockClassName = PatcherFix.class.getName();

			methodName = "processOSBPatcherFixAddJenkinsStatus";
		}

		if (LockLocalServiceUtil.hasLock(
				defaultUserId, lockClassName, themeDisplay.getCompanyId())) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping " + lockClassName +
						" file processing for company " +
							themeDisplay.getCompanyId() +
								"because it is currently running");
			}

			return;
		}

		try {
			LockLocalServiceUtil.lock(
				defaultUserId, lockClassName, themeDisplay.getCompanyId(),
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

				if (user != null) {
					alloyController.setUser(user);
				}

				try {
					if (methodName.equals(
							"processOSBPatcherFixAddJenkinsStatus")) {

						Method processOSBPatcherStatusFileMethod =
							clazz.getDeclaredMethod(
								methodName,
								new Class<?>[] {
									AlloyController.class, long.class,
									String.class
								});

						ServiceBeanMethodInvocationFactoryUtil.proceed(
							null, clazz, processOSBPatcherStatusFileMethod,
							new Object[] {
								alloyController, GetterUtil.getLong(patcherId),
								jenkinsStatusJSONString
							},
							new String[] {"transactionAdvice"});
					}
					else {
						Method processOSBPatcherStatusFileMethod =
							clazz.getDeclaredMethod(
								methodName,
								new Class<?>[] {
									AlloyController.class, User.class,
									long.class, String.class
								});

						ServiceBeanMethodInvocationFactoryUtil.proceed(
							null, clazz, processOSBPatcherStatusFileMethod,
							new Object[] {
								alloyController, user,
								GetterUtil.getLong(patcherId),
								jenkinsStatusJSONString
							},
							new String[] {"transactionAdvice"});
					}
				}
				catch (Exception e) {
					_log.error(e);
				}
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			LockLocalServiceUtil.unlock(
				lockClassName, themeDisplay.getCompanyId());
		}
	}

	public static Hits search(
			AlloyController alloyController, String className,
			Map<String, Serializable> attributes, String keywords)
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

		ThemeDisplay themeDisplay = alloyController.getThemeDisplay();

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
		catch (Exception e) {
			searchContext.setGroupIds(null);
		}

		return indexer.search(searchContext);
	}

	public static List<String> sortTokens(List<String> tokens)
		throws Exception {

		return sortTokens(StringUtil.merge(tokens));
	}

	public static List<String> sortTokens(String name) throws Exception {
		if (Validator.isNull(name)) {
			return Collections.emptyList();
		}

		List<String> tokens = ListUtil.fromArray(name.split("\\s*,\\s*"));

		ListUtil.distinct(tokens);

		Collections.sort(tokens);

		return tokens;
	}

	public static String unprepareKeywords(String keywords) throws Exception {
		if (Validator.isNull(keywords)) {
			return StringPool.BLANK;
		}

		keywords = keywords.replaceFirst("^\"", StringPool.BLANK);
		keywords = keywords.replaceFirst("\"$", StringPool.BLANK);

		String[] keywordsArray = keywords.split("\" \"");

		return StringUtil.merge(keywordsArray, StringPool.COMMA_AND_SPACE);
	}

	protected static Map<String, Serializable> getSearchAttributes(
			Object... attributes)
		throws Exception {

		Map<String, Serializable> attributesMap = new HashMap<>();

		if ((attributes.length != 0) && ((attributes.length % 2) != 0)) {
			throw new Exception("Arguments length is not an even number");
		}

		for (int i = 0; i < attributes.length; i += 2) {
			String name = String.valueOf(attributes[i]);

			Serializable value = (Serializable)attributes[i + 1];

			attributesMap.put(name, value);
		}

		return attributesMap;
	}

	private static final long _POLL_INDEX_MAX_COUNT = 4;

	private static final long _POLL_INDEX_WAIT_INTERVAL = 250;

	private static final Log _log = LogFactoryUtil.getLog(
		"jsp.osb.patcher.util.PatcherUtil");

}