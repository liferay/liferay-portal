/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.baseline;

import aQute.bnd.differ.Baseline.BundleInfo;
import aQute.bnd.differ.Baseline.Info;
import aQute.bnd.differ.DiffPluginImpl;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.version.Version;

import aQute.lib.io.IO;

import aQute.service.reporter.Reporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Raymond Augé
 * @author Andrea Di Giorgi
 */
public class Baseline {

	public boolean execute() throws Exception {
		_headerPrinted = false;
		_printWriter = null;

		File logDir = _logFile.getParentFile();

		logDir.mkdirs();

		BaselineProcessor baselineProcessor = new BaselineProcessor();

		baselineProcessor.setProperties(_properties);

		Jar newJar = new Jar(_newJarFile);

		if (_newCompatJarFile != null) {
			newJar.addAll(new Jar(_newCompatJarFile));
		}

		Jar oldJar = null;

		if (_oldJarFile != null) {
			if (!_oldJarFile.exists() || _oldJarFile.isDirectory() ||
				!_oldJarFile.canRead()) {

				baselineProcessor.warning(
					"Baseline file %s is invalid. Check if it exists, is " +
						"readable, and is not a directory.",
					_oldJarFile);
			}
			else {
				oldJar = new Jar(_oldJarFile);
			}
		}
		else {
			oldJar = baselineProcessor.getBaselineJar();
		}

		try {
			if (oldJar == null) {
				return true;
			}

			aQute.bnd.differ.Baseline baseline = new aQute.bnd.differ.Baseline(
				baselineProcessor, new DiffPluginImpl());

			List<String> packageFilter = new ArrayList<>();

			for (String movedPackage : getMovedPackages()) {
				packageFilter.add("!".concat(movedPackage));
			}

			packageFilter.add("*");

			Set<Info> infos = baseline.baseline(
				newJar, oldJar, new Instructions(packageFilter));

			if (infos.isEmpty()) {
				return true;
			}

			BundleInfo bundleInfo = baseline.getBundleInfo();

			if (_forceCalculatedVersion) {
				bundleInfo.suggestedVersion = calculateVersion(
					bundleInfo.olderVersion, infos);
			}
			else if (hasPackageRemoved(infos)) {
				bundleInfo.suggestedVersion = new Version(
					bundleInfo.olderVersion.getMajor() + 1, 0, 0);
			}

			int compare = bundleInfo.suggestedVersion.compareTo(
				bundleInfo.newerVersion.getWithoutQualifier());

			if ((compare > 0) || (_forceCalculatedVersion && (compare != 0))) {
				bundleInfo.mismatch = true;
			}

			Info[] infosArray = infos.toArray(new Info[0]);

			Arrays.sort(
				infosArray,
				new Comparator<Info>() {

					@Override
					public int compare(Info info1, Info info2) {
						return info1.packageName.compareTo(info2.packageName);
					}

				});

			Set<String> ignoredPackageNames = new HashSet<>();
			Set<String> mismatchPackageNames = new HashSet<>();
			boolean warning = false;

			for (Info info : infosArray) {
				if (info.mismatch) {
					mismatchPackageNames.add(info.packageName);
				}

				Diff packageDiff = info.packageDiff;

				Delta delta = packageDiff.getDelta();

				if (_forceVersionOneOnAddedPackages && (delta == Delta.ADDED) &&
					bundleInfo.newerVersion.equals(info.newerVersion)) {

					info.suggestedVersion = Version.ONE;
				}

				String warnings = "-";

				Version newerVersion = info.newerVersion;

				if (_ignoreExcessiveVersionIncreases &&
					(info.suggestedVersion != null) &&
					(newerVersion.compareTo(info.suggestedVersion) > 0)) {

					info.suggestedVersion = newerVersion;
				}

				Version suggestedVersion = info.suggestedVersion;

				if (suggestedVersion != null) {
					if (newerVersion.compareTo(suggestedVersion) > 0) {
						mismatchPackageNames.add(info.packageName);

						warnings = _WARNING_EXCESSIVE_VERSION_INCREASE;
					}
					else if (newerVersion.compareTo(suggestedVersion) < 0) {
						warnings = _WARNING_VERSION_INCREASE_REQUIRED;
					}
				}

				if (delta == Delta.REMOVED) {
					warnings = _WARNING_PACKAGE_REMOVED;
				}
				else if (delta == Delta.UNCHANGED) {
					boolean newVersionSuggested = false;

					if (suggestedVersion.compareTo(newerVersion) > 0) {
						warnings = _WARNING_VERSION_INCREASE_SUGGESTED;

						newVersionSuggested = true;
					}
					else if (suggestedVersion.compareTo(newerVersion) < 0) {
						warnings = _WARNING_EXCESSIVE_VERSION_INCREASE;

						newVersionSuggested = true;
					}

					if (!newVersionSuggested && !info.mismatch) {
						continue;
					}
				}

				if (isIgnoredWarnings(newJar, info, delta, warnings)) {
					ignoredPackageNames.add(info.packageName);

					continue;
				}

				boolean correctPackageInfo = generatePackageInfo(
					newJar, info, delta);

				if (!correctPackageInfo) {
					if (delta == Delta.ADDED) {
						warnings = _WARNING_PACKAGE_ADDED_MISSING_PACKAGEINFO;
					}
					else if (delta == Delta.REMOVED) {
						warnings =
							_WARNING_PACKAGE_REMOVED_UNNECESSARY_PACKAGEINFO;
					}
				}

				if (((!_reportDiff || _reportOnlyDirtyPackages) &&
					 warnings.equals("-")) ||
					(_reportOnlyDirtyPackages && correctPackageInfo &&
					 (delta == Delta.REMOVED))) {

					continue;
				}

				doInfo(bundleInfo, info, warnings);

				if (_reportDiff && (delta != Delta.REMOVED)) {
					doPackageDiff(packageDiff);
				}

				warning = true;
			}

			if (!warning) {
				if (!mismatchPackageNames.isEmpty() &&
					ignoredPackageNames.containsAll(mismatchPackageNames)) {

					return true;
				}

				if (mismatchPackageNames.isEmpty() && !bundleInfo.mismatch) {
					return true;
				}
			}

			if (bundleInfo.mismatch) {
				Version newVersion = bundleInfo.suggestedVersion;
				Version oldVersion = bundleInfo.newerVersion;

				updateBundleVersion(oldVersion, newVersion);

				reportBundleVersion(bundleInfo);
			}

			reportMode();

			return false;
		}
		finally {
			log(baselineProcessor);

			baselineProcessor.close();
			newJar.close();

			if (oldJar != null) {
				oldJar.close();
			}

			if (_printWriter != null) {
				_printWriter.close();
			}
		}
	}

	public Properties getProperties() {
		return _properties;
	}

	public void setBndFile(File bndFile) {
		_bndFile = bndFile;
	}

	public void setForceCalculatedVersion(boolean forceCalculatedVersion) {
		_forceCalculatedVersion = forceCalculatedVersion;
	}

	public void setForcePackageInfo(boolean forcePackageInfo) {
		_forcePackageInfo = forcePackageInfo;
	}

	public void setForceVersionOneOnAddedPackages(
		boolean forceVersionOneOnAddedPackages) {

		_forceVersionOneOnAddedPackages = forceVersionOneOnAddedPackages;
	}

	public void setIgnoreExcessiveVersionIncreases(
		boolean ignoreExcessiveVersionIncreases) {

		_ignoreExcessiveVersionIncreases = ignoreExcessiveVersionIncreases;
	}

	public void setLogFile(File logFile) {
		_logFile = logFile;
	}

	public void setNewCompatJarFile(File newCompatJarFile) {
		_newCompatJarFile = newCompatJarFile;
	}

	public void setNewJarFile(File newJarFile) {
		_newJarFile = newJarFile;
	}

	public void setOldJarFile(File oldJarFile) {
		_oldJarFile = oldJarFile;
	}

	public void setReportDiff(boolean reportDiff) {
		_reportDiff = reportDiff;
	}

	public void setReportOnlyDirtyPackages(boolean reportOnlyDirtyPackages) {
		_reportOnlyDirtyPackages = reportOnlyDirtyPackages;
	}

	public void setSourceDir(File sourceDir) {
		_sourceDir = sourceDir;
	}

	protected Version calculateVersion(Version version, Set<Info> infos)
		throws IOException {

		Delta highestDelta = Delta.UNCHANGED;

		Set<String> movedPackages = getMovedPackages();

		for (Info info : infos) {
			Delta delta = info.packageDiff.getDelta();

			if ((delta == Delta.ADDED) || (delta == Delta.CHANGED)) {
				delta = Delta.MICRO;
			}
			else if (delta == Delta.REMOVED) {
				if (movedPackages.contains(info.packageName)) {
					delta = Delta.MICRO;
				}
				else {
					delta = Delta.MAJOR;
				}
			}

			if (delta.compareTo(highestDelta) <= 0) {
				continue;
			}

			highestDelta = delta;
		}

		if (highestDelta == Delta.MAJOR) {
			version = new Version(version.getMajor() + 1, 0, 0);
		}
		else if (highestDelta == Delta.MINOR) {
			version = new Version(
				version.getMajor(), version.getMinor() + 1, 0);
		}
		else {
			version = new Version(
				version.getMajor(), version.getMinor(), version.getMicro() + 1);
		}

		return version;
	}

	protected void doDiff(Diff diff, StringBuilder sb) {
		String type = String.valueOf(diff.getType());

		String output = String.format(
			"%s%-3s %-10s %s", sb, getShortDelta(diff.getDelta()),
			type.toLowerCase(), diff.getName());

		log(output);

		if (_printWriter != null) {
			_printWriter.println(output);
		}

		sb.append("\t");

		for (Diff curDiff : diff.getChildren()) {
			if (curDiff.getDelta() == Delta.UNCHANGED) {
				continue;
			}

			doDiff(curDiff, sb);
		}

		sb.deleteCharAt(sb.length() - 1);
	}

	protected void doInfo(BundleInfo bundleInfo, Info info, String warnings)
		throws IOException {

		doPackagesHeader(bundleInfo);

		reportLog(
			String.valueOf(info.mismatch ? '*' : ' '), info.packageName,
			String.valueOf(info.packageDiff.getDelta()),
			String.valueOf(info.newerVersion),
			String.valueOf(info.olderVersion),
			String.valueOf(
				(info.suggestedVersion == null) ? "-" : info.suggestedVersion),
			warnings, String.valueOf(info.attributes));
	}

	protected void doPackageDiff(Diff diff) {
		StringBuilder sb = new StringBuilder();

		sb.append("\t");

		for (Diff curDiff : diff.getChildren()) {
			if (curDiff.getDelta() == Delta.UNCHANGED) {
				continue;
			}

			doDiff(curDiff, sb);
		}
	}

	protected void doPackagesHeader(BundleInfo bundleInfo) throws IOException {
		if (_headerPrinted) {
			return;
		}

		_headerPrinted = true;

		reportLog(
			" ", "PACKAGE_NAME", "DELTA", "CUR_VER", "BASE_VER", "REC_VER",
			"WARNINGS", "ATTRIBUTES");

		reportLog(
			"=", "==================================================",
			"==========", "==========", "==========", "==========",
			"==========", "==========");
	}

	protected String encodeWarnings(String warnings) {
		String encodedWarnings = warnings.replace(",", "");

		return encodedWarnings.replace(' ', '-');
	}

	protected boolean generatePackageInfo(Jar jar, Info info, Delta delta)
		throws Exception {

		boolean correct = true;

		File packageDir = new File(
			_sourceDir, info.packageName.replace('.', File.separatorChar));

		if (!_forcePackageInfo && !packageDir.exists()) {
			return correct;
		}

		File packageInfoFile = new File(packageDir, "packageinfo");

		if (delta == Delta.REMOVED) {
			if (packageInfoFile.exists()) {
				correct = false;

				packageInfoFile.delete();
			}
		}
		else {
			boolean writePackageInfoFile = true;

			if (!packageInfoFile.exists()) {
				correct = false;

				Resource resource = jar.getResource(
					info.packageName.replace('.', '/') + "/packageinfo");

				if (resource != null) {
					writePackageInfoFile = false;

					String content = IO.collect(resource.openInputStream());

					if (content.startsWith("version ")) {
						Version version = Version.parseVersion(
							content.substring(8));

						if (version.equals(info.suggestedVersion)) {
							correct = true;
						}
					}
				}
			}

			Version newerVersion = info.newerVersion;

			if ((newerVersion != null) &&
				(newerVersion.compareTo(info.suggestedVersion) == 0)) {

				writePackageInfoFile = false;
			}

			if (writePackageInfoFile) {
				packageDir.mkdirs();

				try (FileOutputStream fileOutputStream = new FileOutputStream(
						packageInfoFile)) {

					String content = "version " + info.suggestedVersion;

					fileOutputStream.write(content.getBytes());
				}
			}
		}

		return correct;
	}

	protected Set<String> getMovedPackages() throws IOException {
		File movedPackagesFile = new File(
			_bndFile.getParentFile(), "moved-packages.txt");

		if (!movedPackagesFile.exists()) {
			return Collections.emptySet();
		}

		Set<String> movedPackages = new LinkedHashSet<>();

		try (BufferedReader bufferedReader = new BufferedReader(
				new FileReader(movedPackagesFile))) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				movedPackages.add(line);
			}
		}

		return movedPackages;
	}

	protected String getShortDelta(Delta delta) {
		if (delta == Delta.ADDED) {
			return "+";
		}
		else if (delta == Delta.CHANGED) {
			return "~";
		}
		else if (delta == Delta.MAJOR) {
			return ">";
		}
		else if (delta == Delta.MICRO) {
			return "0xB5";
		}
		else if (delta == Delta.MINOR) {
			return "<";
		}
		else if (delta == Delta.REMOVED) {
			return "-";
		}

		String deltaString = delta.toString();

		return String.valueOf(deltaString.charAt(0));
	}

	protected boolean hasPackageRemoved(Iterable<Info> infos)
		throws IOException {

		Set<String> movedPackages = getMovedPackages();

		for (Info info : infos) {
			if ((info.packageDiff.getDelta() == Delta.REMOVED) &&
				!movedPackages.contains(info.packageName)) {

				return true;
			}
		}

		return false;
	}

	protected boolean isIgnoredWarnings(
			Jar jar, Info info, Delta delta, String warnings)
		throws Exception {

		boolean allWarnings = true;
		String dir = info.packageName.replace('.', '/');
		Set<String> lines = new HashSet<>();

		while (true) {
			Resource resource = jar.getResource(dir + "/.lfrbuild-packageinfo");

			if (resource != null) {
				String content = IO.collect(resource.openInputStream());

				try (BufferedReader bufferedReader = new BufferedReader(
						new StringReader(content.trim()))) {

					String line = null;

					while ((line = bufferedReader.readLine()) != null) {
						String s = line.trim();

						if (s.endsWith("-RECURSIVE")) {
							int endIndex = s.length() - 10;

							lines.add(s.substring(0, endIndex));
						}
						else if (allWarnings) {
							lines.add(s);
						}
					}
				}
			}

			allWarnings = false;

			int index = dir.lastIndexOf('/');

			if ((index == -1) && dir.isEmpty()) {
				break;
			}

			if (index == -1) {
				dir = "";
			}
			else {
				dir = dir.substring(0, index);
			}
		}

		if (lines.contains(encodeWarnings(warnings))) {
			return true;
		}

		if (delta == Delta.ADDED) {
			String s = _WARNING_PACKAGE_ADDED_MISSING_PACKAGEINFO;

			if (lines.contains(encodeWarnings(s))) {
				return true;
			}
		}

		if (delta == Delta.REMOVED) {
			String s = _WARNING_PACKAGE_REMOVED_UNNECESSARY_PACKAGEINFO;

			if (lines.contains(encodeWarnings(s))) {
				return true;
			}
		}

		Resource resource = jar.getResource(
			info.packageName.replace('.', '/') + "/packageinfo");

		if (resource != null) {
			String content = IO.collect(resource.openInputStream());

			if (content.startsWith("version ")) {
				Version version = Version.parseVersion(content.substring(8));

				if (version.getMajor() == 0) {
					return true;
				}
			}
		}

		return false;
	}

	protected void log(Reporter reporter) {
		for (String message : reporter.getErrors()) {
			System.out.println(message);
		}

		for (String message : reporter.getWarnings()) {
			System.out.println(message);
		}
	}

	protected void log(String output) {
		System.out.println(output);
	}

	protected void persistLog(String output) throws IOException {
		if (_logFile == null) {
			return;
		}

		if (_printWriter == null) {
			_logFile.createNewFile();

			_printWriter = new PrintWriter(_logFile);
		}

		_printWriter.println(output);
	}

	protected void reportBundleVersion(BundleInfo bundleInfo)
		throws IOException {

		String output =
			"[Baseline Warning] Bundle Version Change Recommended: " +
				bundleInfo.suggestedVersion;

		log(output);

		persistLog(output);
	}

	protected void reportLog(
			String string1, String string2, String string3, String string4,
			String string5, String string6, String string7, String string8)
		throws IOException {

		String output = String.format(
			"%s %-50s %-10s %-10s %-10s %-10s %-10s", string1, string2, string3,
			string4, string5, string6, string7);

		log(output);

		persistLog(output);
	}

	protected void reportMode() {
		String output = "[Baseline Report] Mode: ";

		if (_reportDiff) {
			output += "diff";
		}
		else {
			output += "standard";
		}

		if (_logFile != null) {
			output += " (persisted)";
		}

		log(output);
	}

	protected void updateBundleVersion(Version oldVersion, Version newVersion)
		throws IOException {

		if (_bndFile == null) {
			return;
		}

		String content = IO.collect(_bndFile);

		content = content.replace(
			Constants.BUNDLE_VERSION + ": " + oldVersion,
			Constants.BUNDLE_VERSION + ": " + newVersion);

		IO.store(content, _bndFile);
	}

	private static final String _WARNING_EXCESSIVE_VERSION_INCREASE =
		"EXCESSIVE VERSION INCREASE";

	private static final String _WARNING_PACKAGE_ADDED_MISSING_PACKAGEINFO =
		"PACKAGE ADDED, MISSING PACKAGEINFO";

	private static final String _WARNING_PACKAGE_REMOVED = "PACKAGE REMOVED";

	private static final String
		_WARNING_PACKAGE_REMOVED_UNNECESSARY_PACKAGEINFO =
			"PACKAGE REMOVED, UNNECESSARY PACKAGEINFO";

	private static final String _WARNING_VERSION_INCREASE_REQUIRED =
		"VERSION INCREASE REQUIRED";

	private static final String _WARNING_VERSION_INCREASE_SUGGESTED =
		"VERSION INCREASE SUGGESTED";

	private File _bndFile;
	private boolean _forceCalculatedVersion;
	private boolean _forcePackageInfo;
	private boolean _forceVersionOneOnAddedPackages = true;
	private boolean _headerPrinted;
	private boolean _ignoreExcessiveVersionIncreases;
	private File _logFile;
	private File _newCompatJarFile;
	private File _newJarFile;
	private File _oldJarFile;
	private PrintWriter _printWriter;
	private final Properties _properties = new Properties();
	private boolean _reportDiff;
	private boolean _reportOnlyDirtyPackages;
	private File _sourceDir;

}