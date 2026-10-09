/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Hashimoto
 */
public class LegacyDataArchiveGroup {

	public LegacyDataArchiveGroup(
		LegacyDataArchivePortalVersion legacyDataArchivePortalVersion,
		String dataArchiveType) {

		_legacyDataArchivePortalVersion = legacyDataArchivePortalVersion;
		_dataArchiveType = dataArchiveType;

		_legacyDataArchiveHelper =
			legacyDataArchivePortalVersion.getLegacyDataArchiveHelper();

		_legacyGitWorkingDirectory =
			_legacyDataArchiveHelper.getLegacyGitWorkingDirectory();

		_legacyDataArchives = _getLegacyDataArchives();
	}

	public void commitLegacyDataArchives() throws IOException {
		for (LegacyDataArchive legacyDataArchive : _legacyDataArchives) {
			if (legacyDataArchive.isUpdated()) {
				continue;
			}

			legacyDataArchive.stageLegacyDataArchive();
		}

		String status = _legacyGitWorkingDirectory.status();

		if (!status.contains("nothing to commit") &&
			!status.contains("nothing added to commit")) {

			LocalGitCommit latestTestLocalGitCommit =
				_legacyDataArchivePortalVersion.getLatestTestLocalGitCommit();

			_legacyGitWorkingDirectory.commitStagedFilesToCurrentBranch(
				JenkinsResultsParserUtil.combine(
					"archive:ignore Update '", _dataArchiveType, "' for '",
					_legacyDataArchivePortalVersion.getPortalVersion(), "' at ",
					latestTestLocalGitCommit.getAbbreviatedSHA(), "."));
		}
	}

	public String getDataArchiveType() {
		return _dataArchiveType;
	}

	public LegacyDataArchivePortalVersion getLegacyDataArchivePortalVersion() {
		return _legacyDataArchivePortalVersion;
	}

	public List<LegacyDataArchive> getLegacyDataArchives() {
		return _legacyDataArchives;
	}

	private List<LegacyDataArchive> _getLegacyDataArchives() {
		List<LegacyDataArchive> legacyDataArchives = new ArrayList<>();

		List<String> databaseNames =
			_legacyDataArchivePortalVersion.getDatabaseNames();

		for (String databaseName : databaseNames) {
			legacyDataArchives.add(new LegacyDataArchive(this, databaseName));
		}

		return legacyDataArchives;
	}

	private final String _dataArchiveType;
	private final LegacyDataArchiveHelper _legacyDataArchiveHelper;
	private final LegacyDataArchivePortalVersion
		_legacyDataArchivePortalVersion;
	private final List<LegacyDataArchive> _legacyDataArchives;
	private final GitWorkingDirectory _legacyGitWorkingDirectory;

}