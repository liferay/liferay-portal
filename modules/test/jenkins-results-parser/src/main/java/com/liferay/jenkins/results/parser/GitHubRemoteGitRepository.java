/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.google.common.collect.Lists;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HttpRequestMethod;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public class GitHubRemoteGitRepository extends BaseRemoteGitRepository {

	public boolean addLabel(String color, String description, String name) {
		if (hasLabel(name)) {
			return true;
		}

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"color", color
		).put(
			"name", name
		);

		if ((description != null) && !description.isEmpty()) {
			jsonObject.put("description", description);
		}

		String labelRequestURL = getLabelRequestURL();

		try {
			JenkinsResultsParserUtil.toString(
				labelRequestURL, jsonObject.toString());

			_labelsLists.remove(labelRequestURL);
		}
		catch (IOException ioException) {
			System.out.println("Unable to add label " + name);

			ioException.printStackTrace();

			return false;
		}

		return true;
	}

	public void deleteLabel(Label oldLabel) {
		updateLabel(null, null, null, oldLabel);
	}

	public List<String> getCollaboratorUsernames() {
		if (_collaboratorUsernames != null) {
			return _collaboratorUsernames;
		}

		String url = JenkinsResultsParserUtil.getGitHubAPIURL(
			getName(), getUsername(), "collaborators");

		try {
			JSONArray collaboratorsJSONArray =
				JenkinsResultsParserUtil.toJSONArray(url);

			_collaboratorUsernames = new ArrayList<>(
				collaboratorsJSONArray.length());

			for (int i = 0; i < collaboratorsJSONArray.length(); i++) {
				JSONObject collaboratorUserJSONObject =
					collaboratorsJSONArray.getJSONObject(i);

				_collaboratorUsernames.add(
					collaboratorUserJSONObject.getString("login"));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get collaborators", ioException);
		}

		return _collaboratorUsernames;
	}

	public String getHtmlURL() {
		return JenkinsResultsParserUtil.combine(
			"https://github.com/", getUsername(), "/", getName());
	}

	public Label getLabel(String name) {
		for (Label label : getLabels()) {
			if (name.equals(label.getName())) {
				return label;
			}
		}

		return null;
	}

	public List<Label> getLabels() {
		String labelRequestURL = getLabelRequestURL();

		List<Label> labelsList = _labelsLists.get(labelRequestURL);

		if (labelsList != null) {
			return labelsList;
		}

		JSONArray labelsJSONArray;

		Set<Label> labels = new HashSet<>();

		for (int pageNumber = 1;
			 pageNumber <=
				 JenkinsResultsParserUtil.PAGES_GITHUB_API_PAGES_SIZE_MAX;
			 pageNumber++) {

			try {
				labelsJSONArray = JenkinsResultsParserUtil.toJSONArray(
					JenkinsResultsParserUtil.combine(
						labelRequestURL, "?per_page=",
						String.valueOf(
							JenkinsResultsParserUtil.
								PER_PAGE_GITHUB_API_PAGES_SIZE_MAX),
						"&page=", String.valueOf(pageNumber)),
					false);
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to get labels for ", getName(),
						" Git repository"),
					ioException);
			}

			if (labelsJSONArray.length() == 0) {
				break;
			}

			for (int i = 0; i < labelsJSONArray.length(); i++) {
				labels.add(new Label((JSONObject)labelsJSONArray.get(i), this));
			}

			if (labelsJSONArray.length() <
					JenkinsResultsParserUtil.PAGES_GITHUB_API_PAGES_SIZE_MAX) {

				break;
			}

			if (pageNumber ==
					JenkinsResultsParserUtil.PAGES_GITHUB_API_PAGES_SIZE_MAX) {

				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Too many GitHub labels (>",
						String.valueOf(labels.size()), ") found for ",
						"GitHub repository ", getRemoteURL()));
			}
		}

		_labelsLists.put(labelRequestURL, Lists.newArrayList(labels));

		return Lists.newArrayList(labels);
	}

	public boolean hasLabel(String name) {
		if (getLabel(name) == null) {
			return false;
		}

		return true;
	}

	public void updateLabel(
		String color, String description, String name, Label oldLabel) {

		if (!hasLabel(oldLabel.getName())) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to update or delete label ", oldLabel.getName(),
					" because it does not exist in the ", getName(),
					" Git repository"));
		}

		JSONObject jsonObject = null;

		if (name != null) {
			jsonObject = new JSONObject();

			jsonObject.put(
				"color", color
			).put(
				"name", name
			);

			if ((description != null) && !description.isEmpty()) {
				jsonObject.put("description", description);
			}
		}

		String labelRequestURL = JenkinsResultsParserUtil.combine(
			getLabelRequestURL(), "/", oldLabel.getName());

		try {
			if (jsonObject == null) {
				JenkinsResultsParserUtil.toString(
					labelRequestURL, false, HttpRequestMethod.DELETE);
			}
			else {
				JenkinsResultsParserUtil.toString(
					labelRequestURL, HttpRequestMethod.PATCH,
					jsonObject.toString());
			}

			_labelsLists.remove(getLabelRequestURL());
		}
		catch (IOException ioException) {
			if (jsonObject == null) {
				System.out.println(
					"Unable to delete label " + oldLabel.getName());
			}
			else {
				System.out.println(
					"Unable to update label " + oldLabel.getName());
			}

			ioException.printStackTrace();
		}
	}

	public static class Label {

		public Label(
			JSONObject jsonObject,
			GitHubRemoteGitRepository gitHubRemoteGitRepository) {

			_jsonObject = jsonObject;
			_gitHubRemoteGitRepository = gitHubRemoteGitRepository;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof Label)) {
				return false;
			}

			Label label = (Label)object;

			if (Objects.equals(getColor(), label.getColor()) &&
				Objects.equals(getName(), label.getName())) {

				return true;
			}

			return false;
		}

		public String getColor() {
			return _jsonObject.getString("color");
		}

		public String getDescription() {
			return _jsonObject.optString("description");
		}

		public GitHubRemoteGitRepository getGitHubRemoteGitRepository() {
			return _gitHubRemoteGitRepository;
		}

		public String getName() {
			return _jsonObject.getString("name");
		}

		@Override
		public int hashCode() {
			String name = getName();

			return name.hashCode();
		}

		@Override
		public String toString() {
			return _jsonObject.toString(4);
		}

		private final GitHubRemoteGitRepository _gitHubRemoteGitRepository;
		private final JSONObject _jsonObject;

	}

	protected GitHubRemoteGitRepository(GitRemote gitRemote) {
		super(gitRemote);

		String hostname = getHostname();

		if (!hostname.equals("github.com")) {
			throw new IllegalArgumentException(
				getName() + " is not a GitHub repository");
		}
	}

	protected GitHubRemoteGitRepository(
		String gitHubRemoteGitRepositoryName, String username) {

		super("github.com", gitHubRemoteGitRepositoryName, username);
	}

	protected String getLabelRequestURL() {
		if (_labelRequestURL != null) {
			return _labelRequestURL;
		}

		_labelRequestURL = JenkinsResultsParserUtil.getGitHubAPIURL(
			getName(), getUsername(), "/labels");

		return _labelRequestURL;
	}

	protected void setLabelRequestURL(String labelRequestURL) {
		_labelRequestURL = labelRequestURL;
	}

	private static final Map<String, List<Label>> _labelsLists =
		new ConcurrentHashMap<>();

	private List<String> _collaboratorUsernames;
	private String _labelRequestURL;

}