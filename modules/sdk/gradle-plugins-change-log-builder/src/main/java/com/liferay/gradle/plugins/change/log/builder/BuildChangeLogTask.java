/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.change.log.builder;

import com.liferay.gradle.plugins.change.log.builder.internal.util.GitUtil;
import com.liferay.gradle.plugins.change.log.builder.internal.util.NaturalOrderStringComparator;
import com.liferay.gradle.util.GUtil;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import java.io.BufferedWriter;
import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Andrea Di Giorgi
 */
public class BuildChangeLogTask extends DefaultTask {

	public BuildChangeLogTask() {
		Project project = getProject();

		setGitDir(project.getRootDir());

		setTicketIdPrefixes("CLDSVCS", "LPS", "SOS", "SYNC");
	}

	@TaskAction
	public void buildChangeLog() throws Exception {
		File changeLogFile = getChangeLogFile();

		Path changeLogPath = changeLogFile.toPath();

		String changeLogContent = null;

		if (changeLogFile.exists()) {
			changeLogContent = new String(
				Files.readAllBytes(changeLogPath), StandardCharsets.UTF_8);
		}

		String rangeEnd = getRangeEnd();
		String rangeStart = getRangeStart();

		Set<String> ticketIds;

		try (Repository repository = GitUtil.openRepository(getGitDir())) {
			if (Validator.isNull(rangeEnd)) {
				rangeEnd = GitUtil.getHashHead(repository);
			}

			if (Validator.isNull(rangeStart)) {
				rangeStart = _getRangeStart(changeLogContent, repository);
			}

			ticketIds = _getTicketIds(rangeStart, rangeEnd, repository);
		}

		String range = rangeStart + ".." + rangeEnd;

		if (ticketIds.isEmpty()) {
			Project project = getProject();

			throw new StopExecutionException(
				project + " does not have changes for range " + range);
		}

		File changeLogDir = changeLogFile.getParentFile();

		changeLogDir.mkdirs();

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				changeLogPath, StandardCharsets.UTF_8,
				StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {

			if (Validator.isNotNull(changeLogContent)) {
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			}

			bufferedWriter.append('#');
			bufferedWriter.newLine();

			bufferedWriter.append("# ");
			bufferedWriter.append(getChangeLogHeader());
			bufferedWriter.newLine();

			bufferedWriter.append('#');
			bufferedWriter.newLine();

			bufferedWriter.append(range);
			bufferedWriter.append('=');

			boolean firstTicket = true;

			for (String ticketId : ticketIds) {
				if (firstTicket) {
					firstTicket = false;
				}
				else {
					bufferedWriter.append(' ');
				}

				bufferedWriter.append(ticketId);
			}
		}
	}

	public BuildChangeLogTask dirs(Iterable<?> dirs) {
		GUtil.addToCollection(_dirs, dirs);

		return this;
	}

	public BuildChangeLogTask dirs(Object... dirs) {
		return dirs(Arrays.asList(dirs));
	}

	@Internal
	public File getChangeLogFile() {
		return GradleUtil.toFile(getProject(), _changeLogFile);
	}

	@Input
	public String getChangeLogHeader() {
		return GradleUtil.toString(_changeLogHeader);
	}

	@Input
	public Iterable<File> getDirs() {
		Project project = getProject();

		return project.files(_dirs);
	}

	@Internal
	public File getGitDir() {
		return GradleUtil.toFile(getProject(), _gitDir);
	}

	@Input
	@Optional
	public String getRangeEnd() {
		return GradleUtil.toString(_rangeEnd);
	}

	@Input
	@Optional
	public String getRangeStart() {
		return GradleUtil.toString(_rangeStart);
	}

	@Input
	public Set<String> getTicketIdPrefixes() {
		return _ticketIdPrefixes;
	}

	public void setChangeLogFile(Object changeLogFile) {
		_changeLogFile = changeLogFile;
	}

	public void setChangeLogHeader(Object changeLogHeader) {
		_changeLogHeader = changeLogHeader;
	}

	public void setDirs(Iterable<?> dirs) {
		_dirs.clear();

		dirs(dirs);
	}

	public void setDirs(Object... dirs) {
		setDirs(Arrays.asList(dirs));
	}

	public void setGitDir(Object gitDir) {
		_gitDir = gitDir;
	}

	public void setRangeEnd(Object rangeEnd) {
		_rangeEnd = rangeEnd;
	}

	public void setRangeStart(Object rangeStart) {
		_rangeStart = rangeStart;
	}

	public void setTicketIdPrefixes(Iterable<String> ticketIdPrefixes) {
		_ticketIdPrefixes.clear();

		ticketIdPrefixes(ticketIdPrefixes);
	}

	public void setTicketIdPrefixes(String... ticketIdPrefixes) {
		setTicketIdPrefixes(Arrays.asList(ticketIdPrefixes));
	}

	public BuildChangeLogTask ticketIdPrefixes(
		Iterable<String> ticketIdPrefixes) {

		GUtil.addToCollection(_ticketIdPrefixes, ticketIdPrefixes);

		return this;
	}

	public BuildChangeLogTask ticketIdPrefixes(String... ticketIdPrefixes) {
		return ticketIdPrefixes(Arrays.asList(ticketIdPrefixes));
	}

	private String _getRangeStart(
			String changeLogContent, Repository repository)
		throws Exception {

		String rangeStart;

		if (Validator.isNotNull(changeLogContent)) {
			Matcher matcher = _lastRangeEndPattern.matcher(changeLogContent);

			if (!matcher.find()) {
				throw new GradleException("Unable to find the range start");
			}

			rangeStart = matcher.group(1);
		}
		else {
			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.YEAR, -2);

			rangeStart = GitUtil.getHashBefore(calendar.getTime(), repository);

			if (Validator.isNull(rangeStart)) {
				return GitUtil.getHashOldest(repository);
			}
		}

		return rangeStart + "^";
	}

	private String _getTicketId(RevCommit revCommit) {
		String message = revCommit.getShortMessage();

		int index = message.indexOf('-');

		if (index == -1) {
			return null;
		}

		String prefix = message.substring(0, index);
		Set<String> ticketIdPrefixes = getTicketIdPrefixes();

		if (!ticketIdPrefixes.contains(prefix)) {
			return null;
		}

		index = message.indexOf(' ');

		if (index == -1) {
			index = message.length();
		}

		return message.substring(0, index);
	}

	private Set<String> _getTicketIds(
			String rangeStart, String rangeEnd, Repository repository)
		throws Exception {

		Set<String> ticketIds = new TreeSet<>(
			new NaturalOrderStringComparator());

		Iterable<RevCommit> revCommits = GitUtil.getCommits(
			getDirs(), rangeStart, rangeEnd, repository);

		for (RevCommit revCommit : revCommits) {
			String ticketId = _getTicketId(revCommit);

			if (Validator.isNull(ticketId)) {
				continue;
			}

			ticketIds.add(ticketId);
		}

		if (rangeStart.equals(GitUtil.getHashOldest(repository))) {
			try (RevWalk revWalk = new RevWalk(repository)) {
				RevCommit revCommit = revWalk.parseCommit(
					repository.resolve(rangeStart));

				String ticketId = _getTicketId(revCommit);

				if (Validator.isNotNull(ticketId)) {
					ticketIds.add(ticketId);
				}
			}
		}

		return ticketIds;
	}

	private static final Pattern _lastRangeEndPattern = Pattern.compile(
		"\\.\\.([0-9a-f]{40})=.*$");

	private Object _changeLogFile;
	private Object _changeLogHeader;
	private final Set<Object> _dirs = new HashSet<>();
	private Object _gitDir;
	private Object _rangeEnd;
	private Object _rangeStart;
	private final Set<String> _ticketIdPrefixes = new HashSet<>();

}