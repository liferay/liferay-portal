/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixPackUtil {

	public static boolean containsPatcherFixPackName(String name)
		throws Exception {

		List<String> patcherFixPackNames = getPatcherFixPackNames(name);

		return !patcherFixPackNames.isEmpty();
	}

	public static PatcherFixPack fetchPatcherFixPack(
		PatcherBuild patcherBuild) {

		return PatcherFixPackLocalServiceUtil.
			fetchPatcherFixPackByPatcherBuildId(
				patcherBuild.getPatcherBuildId());
	}

	public static PatcherFixPack fetchPatcherFixPack(
		String name, long patcherProjectVersionId) {

		return PatcherFixPackLocalServiceUtil.fetchPatcherFixPack(
			patcherProjectVersionId, name);
	}

	public static PatcherFixPack fetchPatcherFixPackByRootPatcherProjectVersion(
			long patcherFixComponentId, int version,
			long rootPatcherProjectVersionId)
		throws Exception {

		List<PatcherFixPack> patcherFixPacks =
			PatcherFixPackLocalServiceUtil.getPatcherFixPacks(
				patcherFixComponentId, version);

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			PatcherProjectVersion patcherProjectVersion =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
					patcherFixPack.getPatcherProjectVersionId());

			if (patcherProjectVersion.getRootPatcherProjectVersionId() ==
					rootPatcherProjectVersionId) {

				return patcherFixPack;
			}
		}

		return null;
	}

	public static PatcherFixPack fetchPatcherFixPackVersion(
			PatcherFixPack patcherFixPack, boolean older)
		throws Exception {

		List<PatcherFixPack> patcherFixPackVersions = getPatcherFixPackVersions(
			patcherFixPack, older);

		if (!patcherFixPackVersions.isEmpty()) {
			return patcherFixPackVersions.get(0);
		}

		return null;
	}

	public static List<PatcherFixPack>
			getFilteredPatcherFixPacksByComponentAndProjectVersion()
		throws Exception {

		List<PatcherFixPack> filteredPatcherFixPacks = new ArrayList<>();

		Map<Long, Set<Long>> patcherFixPackComponentIds = new HashMap<>();

		List<PatcherFixPack> currentPatcherFixPacks =
			PatcherFixPackLocalServiceUtil.getPatcherFixPacks(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherFixPack currentPatcherFixPack : currentPatcherFixPacks) {
			Set<Long> currentComponents = patcherFixPackComponentIds.get(
				currentPatcherFixPack.getPatcherProjectVersionId());

			if (currentComponents == null) {
				currentComponents = new HashSet<>();
			}

			if (!currentComponents.contains(
					currentPatcherFixPack.getPatcherFixComponentId())) {

				filteredPatcherFixPacks.add(currentPatcherFixPack);
			}

			currentComponents.add(
				currentPatcherFixPack.getPatcherFixComponentId());

			patcherFixPackComponentIds.put(
				currentPatcherFixPack.getPatcherProjectVersionId(),
				currentComponents);
		}

		return filteredPatcherFixPacks;
	}

	public static PatcherFixPack getPatcherFixPack(PatcherBuild patcherBuild)
		throws Exception {

		return PatcherFixPackLocalServiceUtil.getPatcherFixPackByPatcherBuildId(
			patcherBuild.getPatcherBuildId());
	}

	public static PatcherFixPack getPatcherFixPack(
			String name, long patcherProjectVersionId)
		throws Exception {

		return PatcherFixPackLocalServiceUtil.getPatcherFixPack(
			patcherProjectVersionId, name);
	}

	public static List<String> getPatcherFixPackNames(String name)
		throws Exception {

		List<String> patcherFixPackNames = new ArrayList<>();

		List<String> tokens = PatcherUtil.getTokens(name);

		for (String token : tokens) {
			Pattern pattern = Pattern.compile(PatcherConstants.FIX_PACKS_REGEX);

			Matcher matcher = pattern.matcher(token);

			if (!matcher.find()) {
				continue;
			}

			patcherFixPackNames.add(matcher.group(1));
		}

		return patcherFixPackNames;
	}

	public static int getPatcherFixPackNamesCount(String name)
		throws Exception {

		List<String> patcherFixPackNames = getPatcherFixPackNames(name);

		return patcherFixPackNames.size();
	}

	public static String getPatcherFixPackRequirements(
			PatcherFixPack patcherFixPack)
		throws Exception {

		List<String> patcherFixPackRequirements = new ArrayList<>();

		Set<String> allRequirements = new HashSet<>(
			getRequirements(patcherFixPack));

		List<PatcherFixPack> patcherFixPackVersions = getPatcherFixPackVersions(
			patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			allRequirements.addAll(getRequirements(patcherFixPackVersion));
		}

		for (String requirement : allRequirements) {
			Pattern pattern = Pattern.compile(
				PatcherConstants.REQUIREMENTS_REGEX);

			Matcher matcher = pattern.matcher(requirement);

			if (!matcher.find()) {
				throw new Exception(
					"The requirement \"" + requirement + "\" is invalid.");
			}

			String component = matcher.group(1);
			long newVersion = GetterUtil.getLong(matcher.group(2));

			for (String patcherFixPackRequirement :
					patcherFixPackRequirements) {

				matcher = pattern.matcher(patcherFixPackRequirement);

				if (!matcher.find() || !component.equals(matcher.group(1))) {
					continue;
				}

				long oldVersion = GetterUtil.getLong(matcher.group(2));

				if (oldVersion >= newVersion) {
					newVersion = oldVersion;
				}

				patcherFixPackRequirements.remove(patcherFixPackRequirement);

				break;
			}

			patcherFixPackRequirements.add(
				component + StringPool.GREATER_THAN_OR_EQUAL + newVersion);
		}

		Collections.sort(patcherFixPackRequirements);

		return StringUtil.merge(patcherFixPackRequirements);
	}

	public static List<PatcherFixPack> getPatcherFixPacks(
			List<String> patcherFixPackNames, long patcherProjectVersionId)
		throws Exception {

		List<PatcherFixPack> patcherFixPacks = new ArrayList<>();

		for (String patcherFixPackName : patcherFixPackNames) {
			patcherFixPacks.add(
				getPatcherFixPack(patcherFixPackName, patcherProjectVersionId));
		}

		return patcherFixPacks;
	}

	public static List<PatcherFixPack> getPatcherFixPackVersions(
		PatcherFixPack patcherFixPack, boolean older) {

		return PatcherFixPackLocalServiceUtil.getPatcherFixPacks(
			patcherFixPack.getPatcherFixComponentId(),
			patcherFixPack.getPatcherProjectVersionId(),
			patcherFixPack.getVersion(), older);
	}

	public static Set<PatcherFixPack> getPrerequisitePatcherFixPacks(
			long patcherFixPackId)
		throws Exception {

		return getPrerequisitePatcherFixPacks(
			PatcherFixPackLocalServiceUtil.getPatcherFixPack(patcherFixPackId));
	}

	public static Set<PatcherFixPack> getPrerequisitePatcherFixPacks(
			PatcherFixPack patcherFixPack)
		throws Exception {

		Set<PatcherFixPack> prerequisitePatcherFixPacks = new HashSet<>();

		PatcherFixComponent dependentPatcherFixComponent =
			PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(
				patcherFixPack.getPatcherFixComponentId());

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFix : patcherFixes) {
			String[] phrases = StringUtil.split(patcherFix.getDependencies());

			for (String phrase : phrases) {
				String[] componentNames = StringUtil.split(phrase, "->");

				String dependentComponentName = componentNames[0];

				if (!dependentComponentName.equals(
						dependentPatcherFixComponent.getName())) {

					continue;
				}

				String prerequisiteComponentName = componentNames[1];

				List<PatcherFixPack> patcherFixPatcherFixPacks =
					PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(
						patcherFix.getPatcherFixId());

				for (PatcherFixPack patcherFixPatcherFixPack :
						patcherFixPatcherFixPacks) {

					if (patcherFixPatcherFixPack.getPatcherFixPackId() ==
							patcherFixPack.getPatcherFixPackId()) {

						continue;
					}

					PatcherFixComponent prerequisitePatcherFixComponent =
						PatcherFixComponentLocalServiceUtil.
							getPatcherFixComponent(
								patcherFixPatcherFixPack.
									getPatcherFixComponentId());

					if (!prerequisiteComponentName.equals(
							prerequisitePatcherFixComponent.getName())) {

						continue;
					}

					prerequisitePatcherFixPacks.add(patcherFixPatcherFixPack);
				}
			}
		}

		return prerequisitePatcherFixPacks;
	}

	protected static Set<String> getRequirementFields(
			PatcherFixPack patcherFixPack)
		throws Exception {

		Set<String> requirements = new HashSet<>(
			SetUtil.fromArray(
				StringUtil.split(patcherFixPack.getRequirements())));

		PatcherFixComponent patcherFixComponent =
			PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(
				patcherFixPack.getPatcherFixComponentId());

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFix : patcherFixes) {
			List<String> tokens = PatcherUtil.getTokens(
				patcherFix.getRequirements());

			for (String token : tokens) {
				String[] split = token.split(">=");

				if (!split[0].equals(patcherFixComponent.getName())) {
					requirements.add(token);
				}
			}
		}

		return requirements;
	}

	protected static Set<String> getRequirements(PatcherFixPack patcherFixPack)
		throws Exception {

		Set<String> requirements = new HashSet<>(
			getRequirementFields(patcherFixPack));

		Set<PatcherFixPack> prerequisitePatcherFixPacks =
			getPrerequisitePatcherFixPacks(patcherFixPack);

		for (PatcherFixPack prerequisitePatcherFixPack :
				prerequisitePatcherFixPacks) {

			PatcherFixComponent prerequisitePatcherFixComponent =
				PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(
					prerequisitePatcherFixPack.getPatcherFixComponentId());

			requirements.add(
				prerequisitePatcherFixComponent.getName() +
					StringPool.GREATER_THAN_OR_EQUAL +
						prerequisitePatcherFixPack.getVersion());
		}

		return requirements;
	}

}