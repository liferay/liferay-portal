/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.baseline;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.repository.InfoRepository;
import aQute.bnd.service.repository.Phase;
import aQute.bnd.service.repository.SearchableRepository.ResourceDescriptor;
import aQute.bnd.version.Version;

import aQute.lib.collections.SortedList;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;

/**
 * @generated
 * @see https://github.com/bndtools/bnd/blob/2.4.1.REL/biz.aQute.bndlib/src/aQute/bnd/build/ProjectBuilder.java
 */
public class BaselineProcessor extends Analyzer {

	/**
	 * This method attempts to find the baseline jar for the current project. It
	 * reads the -baseline property and treats it as instructions. These
	 * instructions are matched against the bsns of the jars (think sub
	 * builders!). If they match, the sub builder is selected.
	 * <p>
	 * The instruction can then specify the following options:
	 *
	 * <pre>
	 * 	version : baseline version from repository
	 * 	file    : a file path
	 * </pre>
	 *
	 * If neither is specified, the current version is used to find the highest
	 * version (without qualifier) that is below the current version. If a
	 * version is specified, we take the highest version with the same base
	 * version.
	 * <p>
	 * Since baselining is expensive and easily generates errors you must enable
	 * it. The easiest solution is to {@code -baseline: *}. This will match all
	 * sub builders and will calculate the version.
	 *
	 * @return a Jar or null
	 */
	public Jar getBaselineJar() throws Exception {
		String bl = getProperty(Constants.BASELINE);
		if (bl == null || Constants.NONE.equals(bl)) {
			return null;
		}

		Instructions baselines = new Instructions(getProperty(Constants.BASELINE));
		if (baselines.isEmpty())
		{
			return null; // no baselining
		}

		RepositoryPlugin repo = getBaselineRepo();
		if (repo == null)
		{
			return null; // errors reported already
		}

		String bsn = getBsn();
		Version version = new Version(getVersion());

		//
		// Loop over the instructions, first match commits.
		//

		for (Entry<Instruction,Attrs> e : baselines.entrySet()) {
			if (e.getKey().matches(bsn)) {
				Attrs attrs = e.getValue();
				Version target;

				if (attrs.containsKey("version")) {
					SortedSet<Version> versions = removeStagedAndFilter(repo.versions(bsn), repo, bsn);

					if (versions.isEmpty()) {
						// We have a repo
						Version v = new Version(getVersion());
						if (v.getWithoutQualifier().compareTo(Version.ONE) > 0) {
							warning("There is no baseline for %s in the baseline repo %s. The build is for version %s, which is <= 1.0.0 which suggests that there should be a prior version.",
									getBsn(), repo, v);
						}
						return null;
					}

					// Specified version!

					String v = attrs.get("version");
					if (!Verifier.isVersion(v)) {
						error("Not a valid version in %s %s", Constants.BASELINE, v);
						return null;
					}

					Version base = new Version(v);
					SortedSet<Version> later = versions.tailSet(base);
					if (later.isEmpty()) {
						error("For baselineing %s-%s, specified version %s not found", bsn, version, base);
						return null;
					}

					// First element is equal or next to the base we desire

					target = later.first();

					// Now, we could end up with a higher version than our
					// current
					// project

				} else {
					String file = attrs.get("file");

					if (file == null) {
						throw new IllegalArgumentException("Instruction must contain the version or file attribute!");
					}

					// Can be useful to specify a file
					// for example when copying a bundle with a public api

					File f = getFile(file);
					if (f != null && f.isFile()) {
						Jar jar = new Jar(f);
						addClose(jar);
						return jar;
					}
					error("Specified file for baseline but could not find it %s", f);
					return null;
				}

				// Fetch the revision

				if (target.getWithoutQualifier().compareTo(version.getWithoutQualifier()) > 0) {
					error("The baseline version %s is higher than the current version %s for %s in %s", target,
							version, bsn, repo);
					return null;
				}
				if (target.getWithoutQualifier().compareTo(version.getWithoutQualifier()) == 0) {
					if (isPedantic()) {
						warning("Baselining against jar");
					}
				}
				File file = repo.get(bsn, target, attrs);
				if (file == null || !file.isFile()) {
					error("Decided on version %s-%s but cannot get file from repo %s", bsn, version, repo);
					return null;
				}
				Jar jar = new Jar(file);
				addClose(jar);
				return jar;
			}
		}

		// Ignore, nothing matched
		return null;
	}

	private RepositoryPlugin getBaselineRepo() {
		String repoName = getProperty(Constants.BASELINEREPO);
		if (repoName == null) {
			return getReleaseRepo();
		}

		List<RepositoryPlugin> repos = getPlugins(RepositoryPlugin.class);
		for (RepositoryPlugin r : repos) {
			if (r.getName().equals(repoName)) {
				return r;
			}
		}
		error("Could not find -baselinerepo %s", repoName);
		return null;
	}

	private RepositoryPlugin getReleaseRepo() {
		String repoName = getProperty(Constants.RELEASEREPO);

		List<RepositoryPlugin> repos = getPlugins(RepositoryPlugin.class);
		for (RepositoryPlugin r : repos) {
			if (r.canWrite()) {
				if (repoName == null || r.getName().equals(repoName)) {
					return r;
				}
			}
		}
		if (repoName == null) {
			error("Could not find a writable repo for the release repo (-releaserepo is not set)");
		}
		else {
			error("No such -releaserepo %s found", repoName);
		}

		return null;
	}

	/**
	 * Check if we have a master phase.
	 *
	 * @param repo
	 * @param bsn
	 * @param v
	 * @return
	 * @throws Exception
	 */
	private boolean isMaster(InfoRepository repo, String bsn, Version v) throws Exception {
		ResourceDescriptor descriptor = repo.getDescriptor(bsn, v);
		if (descriptor == null) {
			return false;
		}

		return descriptor.phase == Phase.MASTER;
	}

	/**
	 * Remove any staging versions that have a variant with a higher qualifier.
	 *
	 * @param versions
	 * @param repo
	 * @return
	 * @throws Exception
	 */
	private SortedSet<Version> removeStagedAndFilter(SortedSet<Version> versions, RepositoryPlugin repo, String bsn)
			throws Exception {
		List<Version> filtered = new ArrayList<Version>(versions);
		Collections.reverse(filtered);

		InfoRepository ir = (repo instanceof InfoRepository) ? (InfoRepository) repo : null;

		//
		// Filter any versions that only differ in qualifier
		// The last variable is the last one added. Since we are
		// sorted from high to low, we skip any earlier base versions
		//
		Version last = null;
		for (Iterator<Version> i = filtered.iterator(); i.hasNext();) {
			Version v = i.next();

			// Check if same base version as last
			Version current = v.getWithoutQualifier();
			if (last != null && current.equals(last)) {
				i.remove();
				continue;
			}

			//
			// Check if this is not a master if the repo
			// has a state for each resource
			// /
			if (ir != null && !isMaster(ir, bsn, v)) {
				i.remove();
			}

			last = current;
		}
		SortedList<Version> set = new SortedList<Version>(filtered);
		trace("filtered for only latest staged: %s from %s in range ", set, versions);
		return set;
	}

}