/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hugo Huijser
 * @author Andrea Di Giorgi
 */
public class GitUtil {

	public static List<String> getCurrentBranchAddedFileNames(
			String baseDirName, String gitWorkingBranchName)
		throws Exception {

		String gitWorkingBranchLatestCommitId = _getLatestCommitId(
			gitWorkingBranchName, "origin/" + gitWorkingBranchName,
			"upstream/" + gitWorkingBranchName);

		return _getAddedFileNames(baseDirName, gitWorkingBranchLatestCommitId);
	}

	public static List<String> getCurrentBranchCommitMessages(
			String baseDirName, String gitWorkingBranchName)
		throws Exception {

		String gitWorkingBranchLatestCommitId = _getLatestCommitId(
			gitWorkingBranchName, "origin/" + gitWorkingBranchName,
			"upstream/" + gitWorkingBranchName);

		List<String> commitMessages = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			"git log --pretty=format:%H:%B--END_OF_COMMIT_MESSAGE-- " +
				gitWorkingBranchLatestCommitId + "..HEAD");

		String line = null;

		StringBundler sb = new StringBundler();

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (!line.equals("--END_OF_COMMIT_MESSAGE--")) {
				sb.append(line);
				sb.append(StringPool.NEW_LINE);

				continue;
			}

			if (sb.index() > 1) {
				sb.setIndex(sb.index() - 1);

				commitMessages.add(sb.toString());

				sb.setIndex(0);
			}
		}

		return commitMessages;
	}

	public static List<String> getCurrentBranchDeletedFileNames(
			String baseDirName, String gitWorkingBranchName)
		throws Exception {

		String gitWorkingBranchLatestCommitId = _getLatestCommitId(
			gitWorkingBranchName, "origin/" + gitWorkingBranchName,
			"upstream/" + gitWorkingBranchName);

		List<String> deleteFileNames = new ArrayList<>();

		deleteFileNames.addAll(
			getDeletedFileNames(baseDirName, gitWorkingBranchLatestCommitId));

		return deleteFileNames;
	}

	public static String getCurrentBranchDiff(
			String baseDirName, String gitWorkingBranchName)
		throws Exception {

		return getCurrentBranchFileDiff(baseDirName, gitWorkingBranchName, "");
	}

	public static String getCurrentBranchFileDiff(
			String baseDirName, String gitWorkingBranchName, String fileName)
		throws Exception {

		String gitWorkingBranchLatestCommitId = _getLatestCommitId(
			gitWorkingBranchName, "origin/" + gitWorkingBranchName,
			"upstream/" + gitWorkingBranchName);

		StringBundler sb = new StringBundler();

		String gitCommand =
			"git diff " + gitWorkingBranchLatestCommitId + "..HEAD";

		if (Validator.isNotNull(fileName)) {
			gitCommand = gitCommand + " -- " + fileName;
		}

		try (UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
				gitCommand)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				sb.append(line);

				sb.append("\n");
			}
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	public static List<String> getCurrentBranchFileNames(
			String baseDirName, String gitWorkingBranchName)
		throws Exception {

		return getCurrentBranchFileNames(
			baseDirName, gitWorkingBranchName, false);
	}

	public static List<String> getCurrentBranchFileNames(
			String baseDirName, String gitWorkingBranchName,
			boolean includeDeletedFileNames)
		throws Exception {

		String gitWorkingBranchLatestCommitId = _getLatestCommitId(
			gitWorkingBranchName, "origin/" + gitWorkingBranchName,
			"upstream/" + gitWorkingBranchName);

		List<String> fileNames = getFileNames(
			baseDirName, gitWorkingBranchLatestCommitId);

		if (includeDeletedFileNames) {
			fileNames.addAll(
				getDeletedFileNames(
					baseDirName, gitWorkingBranchLatestCommitId));
		}

		return fileNames;
	}

	public static List<String> getCurrentBranchRenamedFileNames(
			String baseDirName, String gitWorkingBranchName)
		throws Exception {

		String gitWorkingBranchLatestCommitId = _getLatestCommitId(
			gitWorkingBranchName, "origin/" + gitWorkingBranchName,
			"upstream/" + gitWorkingBranchName);

		return _getRenamedFileNames(
			baseDirName, gitWorkingBranchLatestCommitId);
	}

	public static String getFileContent(String fileName) throws Exception {
		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
				"git show HEAD:" + fileName)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				sb.append(line);

				sb.append("\n");
			}
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	public static List<String> getLatestAuthorFileNames(String baseDirName)
		throws Exception {

		return getLatestAuthorFileNames(baseDirName, false);
	}

	public static List<String> getLatestAuthorFileNames(
			String baseDirName, boolean includeDeletedFileNames)
		throws Exception {

		String commitId = getLatestAuthorCommitId();

		List<String> fileNames = getFileNames(baseDirName, commitId);

		if (includeDeletedFileNames) {
			fileNames.addAll(getDeletedFileNames(baseDirName, commitId));
		}

		return fileNames;
	}

	public static String getLatestCommitId() throws Exception {
		return _getLatestCommitId("HEAD");
	}

	public static List<String> getLocalChangesFileNames(String baseDirName)
		throws Exception {

		return getLocalChangesFileNames(baseDirName, false);
	}

	public static List<String> getLocalChangesFileNames(
			String baseDirName, boolean includeDeletedFileNames)
		throws Exception {

		List<String> fileNames = getLocalChangesFileNames(baseDirName, "add");

		if (includeDeletedFileNames) {
			fileNames.addAll(getLocalChangesFileNames(baseDirName, "remove"));
		}

		return fileNames;
	}

	public static List<String> getModifiedFileNames(
			String baseDirName, int commitCount)
		throws Exception {

		return getModifiedFileNames(baseDirName, commitCount, false);
	}

	public static List<String> getModifiedFileNames(
			String baseDirName, int commitCount,
			boolean includeDeletedFileNames)
		throws Exception {

		if (commitCount <= 0) {
			return Collections.emptyList();
		}

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			"git log --pretty=format:%h");

		String line = null;

		int count = 0;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			count++;

			if (count != commitCount) {
				continue;
			}

			List<String> fileNames = getFileNames(baseDirName, line);

			if (includeDeletedFileNames) {
				fileNames.addAll(getDeletedFileNames(baseDirName, line));
			}

			return fileNames;
		}

		return null;
	}

	public static List<String> getModifiedLastDayFileNames(String baseDirName)
		throws Exception {

		List<String> fileNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			"git diff --diff-filter=AMR --name-only --stat @{last.day}");

		int gitLevel = getGitLevel(baseDirName);

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (StringUtil.count(line, CharPool.SLASH) >= gitLevel) {
				fileNames.add(getFileName(line, gitLevel));
			}
		}

		return fileNames;
	}

	public static void main(String[] args) throws Exception {
		String quiet = System.getProperty(
			SystemProperties.SYSTEM_PROPERTIES_QUIET);

		System.setProperty(
			SystemProperties.SYSTEM_PROPERTIES_QUIET, StringPool.TRUE);

		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String baseDirName = ArgumentsUtil.getString(
			arguments, "git.base.dir", "./");
		String type = ArgumentsUtil.getString(
			arguments, "git.type", "current-branch");

		try {
			Iterable<String> fileNames = null;

			if (type.equals("current-branch")) {
				String gitWorkingBranchName = ArgumentsUtil.getString(
					arguments, "git.working.branch.name", "master");

				fileNames = getCurrentBranchFileNames(
					baseDirName, gitWorkingBranchName, false);
			}
			else if (type.equals("latest-author")) {
				fileNames = getLatestAuthorFileNames(baseDirName, false);
			}
			else if (type.equals("local-changes")) {
				fileNames = getLocalChangesFileNames(baseDirName, false);
			}
			else {
				throw new IllegalArgumentException();
			}

			String markerFileName = ArgumentsUtil.getString(
				arguments, "git.marker.file", null);

			if (Validator.isNotNull(markerFileName)) {
				fileNames = getDirNames(baseDirName, fileNames, markerFileName);
			}

			for (String fileName : fileNames) {
				System.out.println(fileName);
			}
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
		finally {
			if (quiet == null) {
				System.clearProperty(SystemProperties.SYSTEM_PROPERTIES_QUIET);
			}
			else {
				System.setProperty(
					SystemProperties.SYSTEM_PROPERTIES_QUIET, quiet);
			}
		}
	}

	protected static List<String> getDeletedFileNames(
			String baseDirName, String commitId)
		throws Exception {

		List<String> fileNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			StringBundler.concat(
				"git diff --diff-filter=RD --name-status ", commitId, " ",
				getLatestCommitId()));

		int gitLevel = getGitLevel(baseDirName);

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			String[] array = line.split("\\s+");

			if (array.length < 2) {
				continue;
			}

			if (StringUtil.count(array[1], CharPool.SLASH) >= gitLevel) {
				fileNames.add(getFileName(array[1], gitLevel));
			}
		}

		return fileNames;
	}

	protected static Set<String> getDirNames(
		String baseDirName, Iterable<String> fileNames, String markerFileName) {

		Set<String> dirNames = new HashSet<>();

		File baseDir = new File(baseDirName);

		Path baseDirPath = baseDir.toPath();

		for (String fileName : fileNames) {
			File file = new File(baseDir, fileName);

			File dir = getRootDir(
				file.getParentFile(), baseDir, markerFileName);

			if (dir != null) {
				String dirName = String.valueOf(
					baseDirPath.relativize(dir.toPath()));

				dirName = StringUtil.replace(
					dirName, File.separatorChar, CharPool.SLASH);

				dirNames.add(dirName);
			}
		}

		return dirNames;
	}

	protected static String getFileName(String fileName, int gitLevel) {
		for (int i = 0; i < gitLevel; i++) {
			int x = fileName.indexOf(StringPool.SLASH);

			fileName = fileName.substring(x + 1);
		}

		return fileName;
	}

	protected static List<String> getFileNames(
			String baseDirName, String commitId)
		throws Exception {

		List<String> fileNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			StringBundler.concat(
				"git diff --diff-filter=AMR --name-only ", commitId, " ",
				getLatestCommitId()));

		int gitLevel = getGitLevel(baseDirName);

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (StringUtil.count(line, CharPool.SLASH) >= gitLevel) {
				fileNames.add(getFileName(line, gitLevel));
			}
		}

		return fileNames;
	}

	protected static UnsyncBufferedReader getGitCommandReader(String gitCommand)
		throws Exception {

		Runtime runtime = Runtime.getRuntime();

		Process process = null;

		try {
			process = runtime.exec(gitCommand);
		}
		catch (IOException ioException) {
			String errorMessage = ioException.getMessage();

			if (errorMessage.contains("Cannot run program")) {
				throw new GitException(
					"Add Git to your PATH system variable first");
			}

			throw ioException;
		}

		return new UnsyncBufferedReader(
			new InputStreamReader(process.getInputStream()));
	}

	protected static int getGitLevel(String baseDirName) throws GitException {
		Path path = Paths.get(baseDirName);

		path = path.toAbsolutePath();

		path = path.normalize();

		File dir = path.toFile();

		for (int i = 0; i <= ToolsUtil.PORTAL_MAX_DIR_LEVEL; i++) {
			if ((dir == null) || !dir.exists()) {
				continue;
			}

			File gitFile = new File(dir, ".git");

			if (gitFile.exists()) {
				return i;
			}

			dir = dir.getParentFile();
		}

		throw new GitException(
			"Unable to retrieve files because .git directory is missing");
	}

	protected static String getLatestAuthorCommitId() throws Exception {
		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			"git log --pretty=format:%H:%an");

		String latestAuthor = null;

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			String[] parts = line.split(StringPool.COLON);

			String author = parts[1];

			if (latestAuthor == null) {
				latestAuthor = author;

				continue;
			}

			if (!latestAuthor.equals(author)) {
				return parts[0];
			}
		}

		return null;
	}

	protected static List<String> getLocalChangesFileNames(
			String baseDirName, String command)
		throws Exception {

		List<String> localChangesFileNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			"git add . --dry-run");

		int gitLevel = getGitLevel(baseDirName);

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if ((StringUtil.count(line, CharPool.SLASH) < gitLevel) ||
				Validator.isNull(command) || !line.startsWith(command + " '")) {

				continue;
			}

			String fileName = line.substring(
				command.length() + 2, line.length() - 1);

			localChangesFileNames.add(getFileName(fileName, gitLevel));
		}

		return localChangesFileNames;
	}

	protected static File getRootDir(
		File dir, File baseDir, String markerFileName) {

		while (true) {
			File markerFile = new File(dir, markerFileName);

			if (markerFile.exists()) {
				return dir;
			}

			dir = dir.getParentFile();

			if ((dir == null) || baseDir.equals(dir)) {
				return null;
			}
		}
	}

	private static List<String> _getAddedFileNames(
			String baseDirName, String commitId)
		throws Exception {

		List<String> fileNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			StringBundler.concat(
				"git diff --diff-filter=A --name-only ", commitId, " ",
				getLatestCommitId()));

		int gitLevel = getGitLevel(baseDirName);

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (StringUtil.count(line, CharPool.SLASH) >= gitLevel) {
				fileNames.add(getFileName(line, gitLevel));
			}
		}

		return fileNames;
	}

	private static String _getLatestCommitId(String... branchNames)
		throws Exception {

		if (branchNames.length == 1) {
			UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
				"git rev-parse " + branchNames[0]);

			return unsyncBufferedReader.readLine();
		}

		String latestCommitId = null;
		long latestTimestamp = 0;

		for (String branchName : branchNames) {
			UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
				"git log -n 1 " + branchName +
					" --pretty=format:%H:%cd --date=unix");

			String line = unsyncBufferedReader.readLine();

			if (line == null) {
				continue;
			}

			String[] parts = line.split(StringPool.COLON);

			if (parts.length != 2) {
				continue;
			}

			long timestamp = GetterUtil.getLong(parts[1]);

			if (timestamp > latestTimestamp) {
				latestCommitId = parts[0];
				latestTimestamp = timestamp;
			}
		}

		return latestCommitId;
	}

	private static List<String> _getRenamedFileNames(
			String baseDirName, String commitId)
		throws Exception {

		List<String> fileNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = getGitCommandReader(
			StringBundler.concat(
				"git diff --diff-filter=R --name-only ", commitId, " ",
				getLatestCommitId()));

		int gitLevel = getGitLevel(baseDirName);

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (StringUtil.count(line, CharPool.SLASH) >= gitLevel) {
				fileNames.add(getFileName(line, gitLevel));
			}
		}

		return fileNames;
	}

}