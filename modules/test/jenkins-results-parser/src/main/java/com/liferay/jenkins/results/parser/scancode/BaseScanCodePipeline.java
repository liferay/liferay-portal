/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.scancode;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.NotificationUtil;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public abstract class BaseScanCodePipeline implements ScanCodePipeline {

	public static String getBuildNumber(String url) {
		Matcher matcher = _buildNumberPattern.matcher(url);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return "";
	}

	public void addAdditionalPipeline(String pipelineName)
		throws IOException, TimeoutException {

		_pipelineNames.add(pipelineName);

		StringBuilder sb = new StringBuilder();

		sb.append("curl ");
		sb.append(_projectAPIURL);
		sb.append("add_pipeline/");
		sb.append(" --data ");

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"execute_now", true
		).put(
			"pipeline", pipelineName
		);

		sb.append("'");
		sb.append(jsonObject);
		sb.append("'");
		sb.append(" --header ");
		sb.append(_CONTENT_TYPE);
		sb.append(" --header ");
		sb.append("\"Authorization:Token ");
		sb.append(_API_KEY);
		sb.append("\"");
		sb.append(" --request POST ");

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			sb.toString());

		try {
			JenkinsResultsParserUtil.readInputStream(process.getInputStream());
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void addFileInput(String filePath)
		throws IOException, TimeoutException {

		StringBuilder sb = new StringBuilder();

		sb.append("curl ");
		sb.append(_projectAPIURL);
		sb.append("add_input/ --form \"upload_file=@");
		sb.append(filePath);
		sb.append("\" --header \"Authorization:Token ");
		sb.append(_API_KEY);
		sb.append("\" --request POST ");

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			sb.toString());

		try {
			JenkinsResultsParserUtil.readInputStream(process.getInputStream());
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void checkComplianceAlerts(ComplianceAlertType complianceAlertType)
		throws IOException, TimeoutException {

		StringBuilder sb = new StringBuilder();

		sb.append("curl ");
		sb.append(_projectAPIURL);
		sb.append("compliance/?fail_level=");
		sb.append(complianceAlertType);
		sb.append(" --header ");
		sb.append(_CONTENT_TYPE);
		sb.append(" --header ");
		sb.append("\"Authorization:Token ");
		sb.append(_API_KEY);
		sb.append("\"");
		sb.append(" --request GET ");

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			new String[] {sb.toString()});

		String output = JenkinsResultsParserUtil.readInputStream(
			process.getInputStream());

		output = output.trim();

		JSONObject outputJSONObject = new JSONObject(output);

		JSONObject complianceAlertsJSONObject = outputJSONObject.getJSONObject(
			"compliance_alerts");

		if (complianceAlertsJSONObject.isEmpty()) {
			return;
		}

		for (String key : complianceAlertsJSONObject.keySet()) {
			JSONObject complianceAlertJSONObject =
				complianceAlertsJSONObject.getJSONObject(key);

			if (!complianceAlertJSONObject.has(
					complianceAlertType.toString())) {

				continue;
			}

			JSONArray complianceAlertTypeJSONArray =
				complianceAlertJSONObject.getJSONArray(
					complianceAlertType.toString());

			_complianceAlertCountsMap.put(
				key + "-" + complianceAlertType,
				complianceAlertTypeJSONArray.length());
		}
	}

	public void checkComplianceAlerts(String complianceAlertTypeString)
		throws IOException, TimeoutException {

		checkComplianceAlerts(
			ComplianceAlertType.valueOf(complianceAlertTypeString));
	}

	public void downloadResultFiles() throws IOException, TimeoutException {
		String scanCodeResultsDir = JenkinsResultsParserUtil.getBuildProperty(
			"scancode.results.dir");

		for (Map.Entry<String, String> resultTypeEntry :
				_resultTypeExtensionsMap.entrySet()) {

			StringBuilder sb = new StringBuilder();

			sb.append("curl ");
			sb.append("--output ");
			sb.append(scanCodeResultsDir);
			sb.append(_projectNameFromURL);
			sb.append(".");
			sb.append(resultTypeEntry.getValue());
			sb.append(" \"");
			sb.append(_projectAPIURL);
			sb.append("results_download/?output_format=");
			sb.append(resultTypeEntry.getKey());
			sb.append("\" --header \"Authorization:Token ");
			sb.append(_API_KEY);
			sb.append("\" --request GET ");

			Process process = JenkinsResultsParserUtil.executeBashCommands(
				sb.toString());

			try {
				JenkinsResultsParserUtil.readInputStream(
					process.getInputStream());
			}
			catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		String tarGzName = _projectNameFromURL + ".tar.gz";

		File resultsTarGzFile = new File(tarGzName);

		JenkinsResultsParserUtil.tarGzip(
			new File(scanCodeResultsDir), resultsTarGzFile);

		uploadResultsToBucket(resultsTarGzFile.toString());

		JenkinsResultsParserUtil.delete(resultsTarGzFile);
	}

	public abstract void execute() throws IOException, TimeoutException;

	public String getBuildURL() {
		return _buildURL;
	}

	public String getCloudBucketURL() {
		return _cloudBucketURL;
	}

	public String getComplianceAlertMessage(
		ComplianceAlertType complianceAlertType) {

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Integer> entry :
				_complianceAlertCountsMap.entrySet()) {

			String key = entry.getKey();

			if (!key.endsWith(complianceAlertType.toString())) {
				continue;
			}

			sb.append(complianceAlertType.getSlackEmoji());
			sb.append(" ");
			sb.append(
				key.replaceAll(
					"(.*)-" + Pattern.quote(complianceAlertType.toString()) +
						"$",
					"$1"));
			sb.append(":");
			sb.append(entry.getValue());
			sb.append(" ");
		}

		return sb.toString();
	}

	public String getComplianceAlertMessage(String complianceAlertTypeString) {
		return getComplianceAlertMessage(
			ComplianceAlertType.valueOf(complianceAlertTypeString));
	}

	public List<String> getLabels(String... labelsArray) {
		List<String> labels = new ArrayList<>();

		labels.add("automated");

		for (String label : labelsArray) {
			labels.add(label);
		}

		return labels;
	}

	public String getPipelineRunURL(String pipelineName) {
		try {
			StringBuilder sb = new StringBuilder();

			sb.append("curl ");
			sb.append(_projectAPIURL);
			sb.append(" --header ");
			sb.append(_CONTENT_TYPE);
			sb.append(" --header \"Authorization:Token ");
			sb.append(_API_KEY);
			sb.append("\" --request GET ");

			Process process = JenkinsResultsParserUtil.executeBashCommands(
				new String[] {sb.toString()});

			String output = JenkinsResultsParserUtil.readInputStream(
				process.getInputStream());

			output = output.trim();

			JSONObject outputJSONObject = new JSONObject(output);

			JSONArray runsJSONArray = outputJSONObject.getJSONArray("runs");

			for (int i = 0; i < runsJSONArray.length(); i++) {
				JSONObject runJSONObject = runsJSONArray.getJSONObject(i);

				if (!Objects.equals(
						runJSONObject.get("pipeline_name"), pipelineName)) {

					continue;
				}

				return runJSONObject.get(
					"url"
				).toString();
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	public SimpleDateFormat getSimpleDateFormat() {
		return _simpleDateFormat;
	}

	public void invokeScan(JSONObject jsonObject)
		throws IOException, TimeoutException {

		StringBuilder sb = new StringBuilder();

		sb.append("curl ");
		sb.append(_API_URL);

		sb.append(" --data ");
		sb.append("'");
		sb.append(jsonObject);
		sb.append("'");
		sb.append(" --header ");
		sb.append(_CONTENT_TYPE);
		sb.append(" --header ");
		sb.append("\"Authorization:Token ");
		sb.append(_API_KEY);
		sb.append("\"");
		sb.append(" --request POST ");

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			sb.toString());

		String output = null;

		try {
			output = JenkinsResultsParserUtil.readInputStream(
				process.getInputStream());

			output = output.trim();

			JSONObject outputJSONObject = new JSONObject(output);

			_projectAPIURL = outputJSONObject.getString("url");
			_projectID = outputJSONObject.getString("uuid");
			_projectName = outputJSONObject.getString("name");
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void sendSlackNotification(String cloudBucketURL) {
		StringBuilder sb = new StringBuilder();

		String subject = "ScanCode pipeline is complete";

		if (_hasErrors()) {
			subject = ":red-alert: Release blocker :red-alert:";
		}

		String complianceAlertErrorMessage = getComplianceAlertMessage(
			ComplianceAlertType.ERROR);
		String complianceAlertWarningMessage = getComplianceAlertMessage(
			ComplianceAlertType.WARNING);

		String complianceAlertMessages =
			complianceAlertErrorMessage + complianceAlertWarningMessage;

		if (!JenkinsResultsParserUtil.isNullOrEmpty(complianceAlertMessages)) {
			sb.append("*Compliance Alerts:* ");
			sb.append(complianceAlertMessages);
			sb.append("\n");
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_releaseBuildURL)) {
			sb.append("*Release Jenkins Build:* ");
			sb.append("<");
			sb.append(_releaseBuildURL);
			sb.append("|test-portal-release#");
			sb.append(getBuildNumber(_releaseBuildURL));
			sb.append(">\n");
		}

		sb.append("*ScanCode Pipelines Jenkins Build:* ");
		sb.append("<");
		sb.append(_buildURL);
		sb.append("|test-scancode-pipelines#");
		sb.append(getBuildNumber(_buildURL));
		sb.append(">\n");
		sb.append("*Project Link:* ");
		sb.append("<");
		sb.append(_projectURL);
		sb.append("|");
		sb.append(_projectName);
		sb.append(">\n");
		sb.append(
			JenkinsResultsParserUtil.getNounForm(
				_pipelineNames.size(), "*Pipelines:* ", "*Pipeline:* "));
		sb.append(String.join(", ", _pipelineNames));
		sb.append("\n");
		sb.append("*Status:* ");
		sb.append(
			_projectStatuses.toString(
			).replaceAll(
				"(^\\[|\\]$)", ""
			));

		if (_cloudBucketURL != null) {
			sb.append("\n*Cloud Bucket tar.gz:* ");
			sb.append("<");
			sb.append(_cloudBucketURL);
			sb.append("|");
			sb.append(_projectNameFromURL + ".tar.gz");
			sb.append(">");
		}

		NotificationUtil.sendSlackNotification(
			sb.toString(), "#scancode-io", ":liferay-ci:", subject,
			"Liferay CI");
	}

	public void setProjectURL(String uid, String name) {
		name = name.replaceAll(
			"[.:]", ""
		).toLowerCase();

		name = name.replace(" ", "-");

		uid = uid.substring(0, uid.indexOf("-"));

		_projectNameFromURL = name + "-" + uid;

		_projectURL =
			"https://liferay1.scancode.io/project/" + name + "-" + uid + "/";
	}

	public void startPipeline(String pipelineName) throws Exception {
		String pipelineRunURL = getPipelineRunURL(pipelineName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(pipelineRunURL)) {
			throw new IOException("Unable to start " + pipelineName);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("curl ");
		sb.append(pipelineRunURL);
		sb.append("start_pipeline/ --header \"Authorization:Token ");
		sb.append(_API_KEY);
		sb.append("\" --request POST ");

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			sb.toString());

		try {
			JenkinsResultsParserUtil.readInputStream(process.getInputStream());
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void uploadResultsToBucket(String tarGzFilePath) {
		File tarGzFile = new File(tarGzFilePath);

		try {
			ScanCodeCloudBucket scanCodeCloudBucket =
				ScanCodeCloudBucket.getInstance();

			scanCodeCloudBucket.createScanCodeCloudObject(
				"inbox/" + tarGzFile.getName(), tarGzFile);

			_cloudBucketURL = scanCodeCloudBucket.getCloudBucketURL();
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void waitForScan(String pipelineName) {
		StringBuilder sb = new StringBuilder();

		sb.append("curl ");
		sb.append(_projectAPIURL);
		sb.append(" --header ");
		sb.append(_CONTENT_TYPE);
		sb.append(" --header ");
		sb.append("\"Authorization:Token ");
		sb.append(_API_KEY);
		sb.append("\"");
		sb.append(" --request GET ");

		while (true) {
			try {
				Process process = JenkinsResultsParserUtil.executeBashCommands(
					new String[] {sb.toString()});

				String output = JenkinsResultsParserUtil.readInputStream(
					process.getInputStream());

				output = output.trim();

				JSONObject outputJSONObject = new JSONObject(output);

				JSONArray jsonArray = outputJSONObject.getJSONArray("runs");

				Object run = jsonArray.get(0);

				if (_pipelineNames.size() > 1) {
					run = jsonArray.get(1);
				}

				JSONObject runJSONObject = new JSONObject(run.toString());

				String projectStatus = runJSONObject.getString("status");

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Project status for ", pipelineName, ": ",
						projectStatus));

				if (!projectStatus.equals("running") &&
					!projectStatus.equals("queued")) {

					_projectStatuses.add(projectStatus);

					break;
				}

				Thread.sleep(10 * 60 * 1000);
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		setProjectURL(_projectID, _projectName);
	}

	public static enum ComplianceAlertType {

		ERROR("error", ":red_circle:"),
		WARNING("warning", ":large_yellow_circle:");

		public String getSlackEmoji() {
			return _slackEmoji;
		}

		public String toString() {
			return _name;
		}

		private ComplianceAlertType(String name, String slackEmoji) {
			_name = name;
			_slackEmoji = slackEmoji;
		}

		private final String _name;
		private final String _slackEmoji;

	}

	protected BaseScanCodePipeline(String buildURL, String pipelineName) {
		_buildURL = buildURL;

		_pipelineNames.add(pipelineName);
		_releaseBuildURL = null;
	}

	protected BaseScanCodePipeline(
		String buildURL, String pipelineName, String releaseBuildURL) {

		_buildURL = buildURL;

		_pipelineNames.add(pipelineName);
		_releaseBuildURL = releaseBuildURL;
	}

	private boolean _hasErrors() {
		for (String key : _complianceAlertCountsMap.keySet()) {
			if (key.endsWith(ComplianceAlertType.ERROR.toString())) {
				return true;
			}
		}

		return false;
	}

	private static final String _API_KEY;

	private static final String _API_URL =
		"https://liferay1.scancode.io/api/projects/";

	private static final String _CONTENT_TYPE =
		"'Content-Type: application/json;'";

	private static final Pattern _buildNumberPattern = Pattern.compile(
		"job/.*/(\\d+)/?$");
	private static final Map<String, Integer> _complianceAlertCountsMap =
		new HashMap<>();
	private static final Map<String, String> _resultTypeExtensionsMap =
		new HashMap<String, String>() {
			{
				put("attribution", "attribution.html");
				put("cyclonedx", "cdx.json");
				put("spdx", "spdx.json");
				put("xlsx", "xlsx");
			}
		};

	static {
		try {
			_API_KEY = JenkinsResultsParserUtil.getBuildProperty(
				"scancode.api.key");
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get ScanCode API key", ioException);
		}
	}

	private final String _buildURL;
	private String _cloudBucketURL;
	private final List<String> _pipelineNames = new ArrayList<>();
	private String _projectAPIURL;
	private String _projectID;
	private String _projectName;
	private String _projectNameFromURL;
	private final List<String> _projectStatuses = new ArrayList<>();
	private String _projectURL;
	private final String _releaseBuildURL;
	private final SimpleDateFormat _simpleDateFormat = new SimpleDateFormat(
		"MMM d yy HH:mm:ss");

}