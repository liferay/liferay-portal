/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.CloudBucketUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface PersistentResource {

	public void download(String artifactName, File destinationDir);

	public List<Artifact> getArtifacts();

	public String getBaseS3ObjectPath();

	public String getControllerBuildURL();

	public String getProducerBuildURL();

	public JenkinsMaster getProducerJenkinsMaster();

	public long getProducerQueueId();

	public Status getStatus();

	public Type getType();

	public void touch();

	public void upload(File baseDir);

	public void waitForUpdate(long waitTime);

	public static class Artifact {

		public JSONObject getJSONObject() {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put(
				"name", getName()
			).put(
				"s3_object_path", getS3ObjectPath()
			);

			return jsonObject;
		}

		public String getName() {
			return _name;
		}

		public String getS3ObjectPath() {
			return JenkinsResultsParserUtil.combine(
				_persistentResource.getBaseS3ObjectPath(), "/", getName());
		}

		public boolean isAvailable() {
			if (_available != null) {
				return _available;
			}

			if (!CloudBucketUtil.isS3ObjectPathAvailable(getS3ObjectPath())) {
				return false;
			}

			_available = true;

			return _available;
		}

		@Override
		public String toString() {
			return String.valueOf(getJSONObject());
		}

		protected Artifact(String name, PersistentResource persistentResource) {
			_name = name;
			_persistentResource = persistentResource;
		}

		private Boolean _available;
		private final String _name;
		private final PersistentResource _persistentResource;

	}

	public static enum Status {

		FAILED, IN_PROGRESS, IN_QUEUE, NOT_STARTED, SUCCESS

	}

	public static enum Type {

		ASAH_BUNDLE("asah-bundle"), FARO_BUNDLE("faro-bundle"),
		PORTAL_BUNDLE("portal-bundle");

		public static Type get(String key) {
			return _types.get(key);
		}

		@Override
		public String toString() {
			return _key;
		}

		private Type(String key) {
			_key = key;
		}

		private static Map<String, Type> _types = new HashMap<>();

		static {
			for (Type type : values()) {
				_types.put(type.toString(), type);
			}
		}

		private final String _key;

	}

}