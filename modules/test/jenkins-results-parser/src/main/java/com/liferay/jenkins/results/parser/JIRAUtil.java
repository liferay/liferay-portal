/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import io.atlassian.util.concurrent.Promise;

import java.io.IOException;

import java.net.URI;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author Charlotte Wong
 */
public class JIRAUtil {

	public static void executeTransition(
		String comment, Issue issue, Transition transition) {

		if (_issueRestClient == null) {
			return;
		}

		TransitionInput transitionInput = new TransitionInput(
			transition.getId(), Comment.valueOf(comment));

		try {
			Promise<Void> promise = _issueRestClient.transition(
				issue, transitionInput);

			promise.get();

			_uncacheIssue(issue.getKey());
		}
		catch (ExecutionException | InterruptedException | RestClientException
					exception) {

			System.err.println(
				"Unable to execute transition " + transition.getName());

			exception.printStackTrace();
		}
	}

	public static Issue getIssue(String issueKey) {
		if (_issueRestClient == null) {
			return null;
		}

		CachedIssue cachedIssue = _cachedIssues.get(issueKey);

		if (cachedIssue != null) {
			if (!cachedIssue.isExpired()) {
				return cachedIssue.issue;
			}

			_uncacheIssue(issueKey);
		}

		try {
			Promise<Issue> promise = _issueRestClient.getIssue(issueKey);

			Issue issue = promise.claim();

			_cachedIssues.put(issueKey, new CachedIssue(issue));

			return issue;
		}
		catch (Exception exception) {
			System.err.println("Unable to get issue " + issueKey);

			exception.printStackTrace();

			return null;
		}
	}

	public static Transition getTransition(Issue issue, String transitionName) {
		Map<String, Transition> transitionMap = getTransitions(issue);

		if (transitionMap == null) {
			return null;
		}

		return transitionMap.get(transitionName);
	}

	public static Map<String, Transition> getTransitions(Issue issue) {
		if (!_transitionsMap.containsKey(issue.getKey())) {
			_initTransitions(issue);
		}

		return _transitionsMap.get(issue.getKey());
	}

	private static IssueRestClient _initIssueRestClient() {
		try {
			Properties buildProperties = null;

			try {
				buildProperties = JenkinsResultsParserUtil.getBuildProperties();
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to get build properties", ioException);
			}

			JiraRestClientFactory jiraRestClientFactory =
				new AsynchronousJiraRestClientFactory();

			JiraRestClient jiraRestClient =
				jiraRestClientFactory.createWithBasicHttpAuthentication(
					URI.create(buildProperties.getProperty("jira.url")),
					buildProperties.getProperty("jira.username"),
					buildProperties.getProperty("jira.password"));

			return jiraRestClient.getIssueClient();
		}
		catch (Exception exception) {
			System.err.println("Unable to create JIRA rest client object");

			exception.printStackTrace();

			return null;
		}
	}

	private static void _initTransitions(Issue issue) {
		if (_issueRestClient == null) {
			return;
		}

		Map<String, Transition> transitions = new ConcurrentHashMap<>();

		Promise<Iterable<Transition>> promise = _issueRestClient.getTransitions(
			issue);

		Iterable<Transition> iterable = promise.claim();

		for (Transition transition : iterable) {
			transitions.put(transition.getName(), transition);
		}

		_transitionsMap.put(issue.getKey(), transitions);
	}

	private static void _uncacheIssue(String issueKey) {
		_cachedIssues.remove(issueKey);
		_transitionsMap.remove(issueKey);
	}

	private static final Map<String, CachedIssue> _cachedIssues =
		new ConcurrentHashMap<>();
	private static final IssueRestClient _issueRestClient =
		_initIssueRestClient();
	private static final Map<String, Map<String, Transition>> _transitionsMap =
		new ConcurrentHashMap<>();

	private static class CachedIssue {

		public CachedIssue(Issue issue) {
			this.issue = issue;

			timestamp = System.currentTimeMillis();
		}

		public boolean isExpired() {
			if ((System.currentTimeMillis() - timestamp) > _MAX_ISSUE_AGE) {
				return true;
			}

			return false;
		}

		public final Issue issue;
		public final Long timestamp;

		private static final long _MAX_ISSUE_AGE = 1000 * 60 * 5;

	}

}