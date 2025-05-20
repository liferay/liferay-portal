<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/controllers/init.jspf" %>

<%!
public static class AlloyControllerImpl extends PatcherAlloyControllerImpl {

	public AlloyControllerImpl() {
		setAlloyServiceInvokerClass(PatcherFixPack.class);
		setPermissioned(true);
	}

	public void add() throws Exception {
		_validateAdd();

		PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.createPatcherFixPack(0);

		int patcherFixPackVersion = ParamUtil.getInteger(request, "patcherFixPackVersion", PatcherFixPackConstants.PATCHER_FIX_PACK_VERSION_DEFAULT);

		long patcherFixComponentId = ParamUtil.getLong(request, "patcherFixComponentId");

		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherFixPackModelImpl.TABLE_NAME, "version", false);

		List<PatcherFixPack> patcherFixPacks = alloyServiceInvoker.executeDynamicQuery(new Object[] {"patcherFixComponentId", patcherFixComponentId, "patcherProjectVersionId", patcherProjectVersionId}, QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);

		if (!patcherFixPacks.isEmpty()) {
			PatcherFixPack oldPatcherFixPack = patcherFixPacks.get(0);

			patcherFixPackVersion = oldPatcherFixPack.getVersion() + 1;
		}

		patcherFixPack.setVersion(patcherFixPackVersion);

		PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(patcherFixComponentId);

		patcherFixPack.setName(patcherFixComponent.getName() + StringPool.DASH + String.valueOf(patcherFixPackVersion));

		patcherFixPack.setStatus(WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT);

		updateModel(patcherFixPack);

		addSuccessMessage();

		PatcherUtil.pollIndexState(this, PatcherFixPack.class.getName(), patcherFixPack.getPatcherFixPackId());

		if (isRespondingTo()) {
			respondWith(patcherFixPack);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void build() throws Exception {
		PatcherFixPack patcherFixPack = _fetchPatcherFixPack();

		_validateBuild(patcherFixPack);

		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.getPatcherBuild(patcherFixPack.getPatcherBuildId());

		JenkinsUtil.sendDistJenkinsRequest(this, user, patcherBuild);

		addSuccessMessage();

		if (isRespondingTo()) {
			respondWith(patcherFixPack);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void create() throws Exception {
		PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.createPatcherFixPack(0);

		renderRequest.setAttribute("patcherFixPack", patcherFixPack);

		List<PatcherFixPack> filteredPatcherFixPacks = PatcherFixPackUtil.getFilteredPatcherFixPacksByComponentAndProjectVersion();

		String filteredPatcherFixPacksJSON = JSONFactoryUtil.looseSerialize(filteredPatcherFixPacks);

		renderRequest.setAttribute("filteredPatcherFixPacksJSON", HtmlUtil.escapeJS(filteredPatcherFixPacksJSON));

		renderRequest.setAttribute("patcherFixComponents", PatcherFixComponentUtil.getPatcherFixComponents("name", true));

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X), true));
	}

	public void delete() throws Exception {
		PatcherFixPack patcherFixPack = _fetchPatcherFixPack();

		_validateDelete(patcherFixPack);

		PatcherFixPackLocalServiceUtil.deletePatcherFixPack(patcherFixPack.getPatcherFixPackId());

		PatcherFixLocalServiceUtil.clearPatcherFixPackPatcherFixs(patcherFixPack.getPatcherFixPackId());

		PatcherBuild patcherFixPackBuild = PatcherBuildLocalServiceUtil.getPatcherBuild(patcherFixPack.getPatcherBuildId());

		List<PatcherBuild> patcherBuilds = PatcherBuildUtil.fetchPatcherBuildsByKey(patcherFixPackBuild.getKey());

		for (PatcherBuild patcherBuild : patcherBuilds) {
			PatcherBuildUtil.deletePatcherBuildAndChildBuilds(this, patcherBuild);
		}

		if (isRespondingTo()) {
			respondWith(patcherFixPack);

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void edit() throws Exception {
		PatcherFixPack patcherFixPack = _fetchPatcherFixPack();

		_validateEdit(patcherFixPack);

		renderRequest.setAttribute("patcherFixPack", patcherFixPack);

		List<PatcherFixPack> filteredPatcherFixPacks = PatcherFixPackUtil.getFilteredPatcherFixPacksByComponentAndProjectVersion();

		String filteredPatcherFixPacksJSON = JSONFactoryUtil.looseSerialize(filteredPatcherFixPacks);

		renderRequest.setAttribute("filteredPatcherFixPacksJSON", HtmlUtil.escapeJS(filteredPatcherFixPacksJSON));

		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());

		if (patcherBuild != null) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

			renderRequest.setAttribute("gitHubURL", PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()));

			Map<String, String> distJenkinsRequestParameters = JenkinsUtil.getDistJenkinsRequestParameters(patcherBuild);

			if (patcherBuild.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
				distJenkinsRequestParameters.put("git.revision", patcherFix.getGitHash());
			}

			renderRequest.setAttribute("jenkinsRequestParameters", distJenkinsRequestParameters);

			renderRequest.setAttribute("patcherFix", patcherFix);
		}

		renderRequest.setAttribute("patcherFixComponents", PatcherFixComponentUtil.getPatcherFixComponents("name", true));

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	public void index() throws Exception {
		AlloySearchResult alloySearchResult = search(null, new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true));

		if (isRespondingTo()) {
			respondWith(alloySearchResult);

			return;
		}

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		renderRequest.setAttribute("displayTerms", new DisplayTerms(request));

		renderRequest.setAttribute("patcherFixComponents", PatcherFixComponentUtil.getPatcherFixComponents("name", true));

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));
	}

	public void setBuild() throws Exception {
		PatcherFixPack patcherFixPack = _fetchPatcherFixPack();

		_validateSetBuild(patcherFixPack);

		List<Long> patcherFixPackPatcherFixIds = new ArrayList<Long>();

		List<PatcherFix> patcherFixPackPatcherFixes = PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFixPackPatcherFix : patcherFixPackPatcherFixes) {
			patcherFixPackPatcherFixIds.add(patcherFixPackPatcherFix.getPatcherFixId());
		}

		List<Long> patcherFixIds = new ArrayList<Long>();

		patcherFixIds.addAll(patcherFixPackPatcherFixIds);

		List<PatcherFix> previousFixPackBuildFixes = PatcherFixUtil.getPreviousFixPackBuildFixes(patcherFixPack);

		for (PatcherFix previousFixPackBuildFix : previousFixPackBuildFixes) {
			if (PatcherFixUtil.isCoveredPatcherFixTickets(previousFixPackBuildFix, patcherFixPackPatcherFixes)) {
				continue;
			}

			patcherFixIds.add(previousFixPackBuildFix.getPatcherFixId());
		}

		PatcherScanUtil.refinePatcherFixIds(patcherFixIds);

		PatcherBuild patcherBuild = PatcherBuildUtil.addPatcherFixPackMainBuild(this, user, patcherFixPack.getPatcherProjectVersionId(), patcherFixPack.getName(), PatcherBuildConstants.PATCHER_BUILD_ACCOUNT_ENTRY_NAME_LIFERAY, WorkflowConstants.STATUS_BUILD_MERGING_ONLY, patcherFixIds);

		patcherFixPack.setPatcherBuildId(patcherBuild.getPatcherBuildId());

		updateModelIgnoreRequest(patcherFixPack);

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

		patcherFix.setObsolete(false);

		updateModelIgnoreRequest(patcherFix);

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void update() throws Exception {
		PatcherFixPack patcherFixPack = _fetchPatcherFixPack();

		_validateUpdate(patcherFixPack);

		int status = ParamUtil.getInteger(request, "status");

		if ((patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_FROZEN) && (status == WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT)) {
			patcherFixPack.setPatcherBuildId(0);
		}
		else if ((patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_FROZEN) && (status == WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {
			patcherFixPack.setReleasedDate(new Date());
		}

		updateModel(patcherFixPack);

		addSuccessMessage();

		PatcherUtil.pollIndexState(this, PatcherFixPack.class.getName(), patcherFixPack.getPatcherFixPackId());

		if (isRespondingTo()) {
			respondWith(patcherFixPack);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void view() throws Exception {
		PatcherFixPack patcherFixPack = _fetchPatcherFixPack();

		_validateView(patcherFixPack);

		if (isRespondingTo()) {
			respondWith(patcherFixPack);

			return;
		}

		renderRequest.setAttribute("patcherFixPack", patcherFixPack);

		renderRequest.setAttribute("newTickets", StringUtil.merge(PatcherUtil.getNewTickets(patcherFixPack)));

		renderRequest.setAttribute("oldestPatcherFixDate", PatcherFixUtil.getOldestPatcherFixCreateDate(patcherFixPack.getPatcherFixPackId()));

		renderRequest.setAttribute("overriddenTickets", StringUtil.merge(PatcherUtil.getOverriddenTickets(patcherFixPack)));

		PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixPack.getPatcherFixComponentId());

		renderRequest.setAttribute("patcherFixComponent", patcherFixComponent);

		List<PatcherFix> patcherFixes = PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(patcherFixPack.getPatcherFixPackId());

		renderRequest.setAttribute("patcherFixes", patcherFixes);

		renderRequest.setAttribute("patcherFixPackStatus", translate(WorkflowConstants.getStatusLabel(patcherFixPack.getStatus())));

		Set<PatcherFixPack> prerequisitePatcherFixPacks = PatcherFixPackUtil.getPrerequisitePatcherFixPacks(patcherFixPack.getPatcherFixPackId());

		renderRequest.setAttribute("prerequisitePatcherFixPacks", ListUtil.fromCollection(prerequisitePatcherFixPacks));

		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());

		if (patcherBuild != null) {
			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

			renderRequest.setAttribute("gitHubURL", PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()));
			renderRequest.setAttribute("mainPatcherFix", patcherFix);

			renderRequest.setAttribute("mainPatcherBuild", patcherBuild);

			renderRequest.setAttribute("patcherBuildQAStatus", translate(PatcherBuildUtil.getQAStatusLabel(patcherFixPack.getPatcherBuildId())));

			renderRequest.setAttribute("patcherBuildStatus", translate(WorkflowConstants.getStatusLabel(patcherBuild.getStatus())));
		}

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"rootPatcherProjectVersionName", "patcherFixComponentName", "fixPackVersion"}, parameterTypes = {String.class, String.class, Integer.class})
	public void viewIssues() throws Exception {
		_validateViewIssues();

		if (!isRespondingTo()) {
			return;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		int fixPackVersion = ParamUtil.getInteger(request, "fixPackVersion");

		String rootPatcherProjectVersionName = ParamUtil.getString(request, "rootPatcherProjectVersionName");

		PatcherProjectVersion rootPatcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(rootPatcherProjectVersionName);

		if (rootPatcherProjectVersion.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			String patcherProjectVersionName = _getPatcherProjectVersionName(rootPatcherProjectVersion, fixPackVersion);

			PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(patcherProjectVersionName);

			jsonObject.put("fixedIssues", patcherProjectVersion.getFixedIssues());

			respondWith(jsonObject);

			return;
		}

		String patcherFixComponentName = ParamUtil.getString(request, "patcherFixComponentName");

		PatcherFixComponent patcherFixComponent = PatcherFixComponentUtil.fetchPatcherFixComponent(patcherFixComponentName);

		PatcherFixPack patcherFixPack = PatcherFixPackUtil.fetchPatcherFixPackByRootPatcherProjectVersion(patcherFixComponent.getPatcherFixComponentId(), fixPackVersion, rootPatcherProjectVersion.getPatcherProjectVersionId());

		jsonObject.put("newIssues", StringUtil.merge(PatcherUtil.getNewTickets(patcherFixPack)));

		respondWith(jsonObject);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE)
	public void viewLatestPortalFixPack() throws Exception {
		if (!isRespondingTo()) {
			return;
		}

		PatcherFixComponent portalPatcherFixComponent = PatcherFixComponentUtil.fetchPatcherFixComponent("portal");

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherFixPackModelImpl.TABLE_NAME, "version", false);

		List<PatcherFixPack> patcherFixPacks = alloyServiceInvoker.executeDynamicQuery(new Object[] {"patcherFixComponentId", portalPatcherFixComponent.getPatcherFixComponentId()}, QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);

		respondWith(patcherFixPacks.get(0));
	}

	@Override
	protected Indexer buildIndexer() {
		return PatcherFixPackIndexer.getInstance();
	}

	private PatcherFixPack _fetchPatcherFixPack() throws Exception {
		long patcherFixPackId = ParamUtil.getLong(request, "id");

		return PatcherFixPackLocalServiceUtil.fetchPatcherFixPack(patcherFixPackId);
	}

	private String _getPatcherProjectVersionName(PatcherProjectVersion rootPatcherProjectVersion, int patcherFixPackVersion) throws Exception {
		if (rootPatcherProjectVersion.getPatcherProductVersionId() == PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_70)) {
			return StringUtil.replace(rootPatcherProjectVersion.getName(), PatcherFixPackConstants.FIX_PACK_COMPONENT_BASE, PatcherFixPackConstants.FIX_PACK_COMPONENT_DE + StringPool.DASH + patcherFixPackVersion);
		}
		else if (rootPatcherProjectVersion.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			return StringUtil.replace(rootPatcherProjectVersion.getName(), PatcherFixPackConstants.FIX_PACK_COMPONENT_BASE, PatcherFixPackConstants.FIX_PACK_COMPONENT_DXP + StringPool.DASH + patcherFixPackVersion);
		}

		return rootPatcherProjectVersion.getName();
	}

	private void _validateAdd() throws Exception {
		long patcherFixComponentId = ParamUtil.getLong(request, "patcherFixComponentId");

		PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixComponentId);

		if (patcherFixComponent == null) {
			throw new AlloyException("the-fix-component-is-invalid");
		}

		int patcherFixPackVersion = ParamUtil.getInteger(request, "patcherFixPackVersion", PatcherFixPackConstants.PATCHER_FIX_PACK_VERSION_DEFAULT);

		if (patcherFixPackVersion < 1) {
			throw new AlloyException("the-fix-pack-version-must-be-greater-than-zero");
		}

		_validateRequirements();
	}

	private void _validateBuild(PatcherFixPack patcherFixPack) throws Exception {
		String message = JenkinsUtil.validateJenkinsSetup();

		if (Validator.isNotNull(message)) {
			throw new AlloyException(message);
		}

		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());

		if (patcherBuild == null) {
			throw new AlloyException("the-fix-pack-cannot-be-built-because-it-is-not-merged");
		}

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

		if (Validator.isNull(patcherFix.getGitHash())) {
			throw new AlloyException("the-fix-pack-cannot-be-built-because-it-is-not-merged");
		}
	}

	private void _validateDelete(PatcherFixPack patcherFixPack) throws Exception {
		_validatePatcherFixPack(patcherFixPack);

		List<PatcherFixPack> newerPatcherFixPacks = PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, false);

		if (!newerPatcherFixPacks.isEmpty()) {
			throw new AlloyException("the-fix-pack-cannot-be-deleted-because-the-current-fix-pack-is-not-the-latest");
		}
	}

	private void _validateEdit(PatcherFixPack patcherFixPack) throws Exception {
		_validatePatcherFixPack(patcherFixPack);
	}

	private void _validateFrozenFixPack(PatcherFixPack patcherFixPack) throws Exception {
		_validatePreviousPatcherFixPacks(patcherFixPack);

		List<String> pendingPatcherFixPackNames = new ArrayList<String>();

		Set<PatcherFixPack> prerequisitePatcherFixPacks = PatcherFixPackUtil.getPrerequisitePatcherFixPacks(patcherFixPack.getPatcherFixPackId());

		for (PatcherFixPack prerequisitePatcherFixPack : prerequisitePatcherFixPacks) {
			if ((prerequisitePatcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_FROZEN) && (prerequisitePatcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {
				pendingPatcherFixPackNames.add(prerequisitePatcherFixPack.getName());
			}
		}

		if (!pendingPatcherFixPackNames.isEmpty()) {
			String patcherFixPacks = StringUtil.merge(PatcherUtil.sortTokens(pendingPatcherFixPackNames));

			throw new AlloyException(translate("the-fix-pack-cannot-be-frozen-as-it-depends-on-the-following-fix-packs-that-need-to-be-frozen-first-x", patcherFixPacks));
		}
	}

	private void _validatePatcherFixPack(PatcherFixPack patcherFixPack) throws Exception {
		if (patcherFixPack == null) {
			throw new AlloyException("the-fix-pack-does-not-exist");
		}
	}

	private void _validatePreviousPatcherFixPacks(PatcherFixPack patcherFixPack) throws Exception {
		List<PatcherFixPack> patcherFixPackVersions = PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() == WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {
				throw new AlloyException("all-previous-fix-packs-of-the-same-component-must-no-longer-be-under-development");
			}

			long patcherBuildId = patcherFixPackVersion.getPatcherBuildId();

			PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

			if (patcherBuild == null) {
				throw new AlloyException("all-previous-fix-packs-of-the-same-component-must-complete-merging");
			}

			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new AlloyException("all-previous-fix-packs-of-the-same-component-must-complete-merging");
			}
		}
	}

	private void _validateReleaseFixPack(PatcherFixPack patcherFixPack) throws Exception {
		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());

		if (patcherBuild == null) {
			throw new AlloyException("the-fix-pack-must-complete-merging-before-release");
		}

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

		if (Validator.isNull(patcherFix.getGitHash())) {
			throw new AlloyException("the-fix-pack-must-complete-merging-before-release");
		}

		List<PatcherFixPack> patcherFixPackVersions = PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() != WorkflowConstants.STATUS_FIX_PACK_RELEASED) {
				throw new AlloyException("all-previous-fix-packs-of-the-same-component-must-already-be-released");
			}

			long patcherBuildId = patcherFixPackVersion.getPatcherBuildId();

			patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

			if (patcherBuild == null) {
				throw new AlloyException("all-previous-fix-packs-of-the-same-component-must-complete-merging");
			}

			patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new AlloyException("all-previous-fix-packs-of-the-same-component-must-complete-merging");
			}
		}

		List<String> pendingPatcherFixPackNames = new ArrayList<String>();

		Set<PatcherFixPack> prerequisitePatcherFixPacks = PatcherFixPackUtil.getPrerequisitePatcherFixPacks(patcherFixPack.getPatcherFixPackId());

		for (PatcherFixPack prerequisitePatcherFixPack : prerequisitePatcherFixPacks) {
			if (prerequisitePatcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_RELEASED) {
				pendingPatcherFixPackNames.add(prerequisitePatcherFixPack.getName());
			}
		}

		if (!pendingPatcherFixPackNames.isEmpty()) {
			String patcherFixPacks = StringUtil.merge(PatcherUtil.sortTokens(pendingPatcherFixPackNames));

			throw new AlloyException(translate("the-fix-pack-cannot-be-released-as-it-depends-on-the-following-fix-packs-that-need-to-be-released-first-x", patcherFixPacks));
		}
	}

	private void _validateRequirements() throws AlloyException {
		List<String> requirements = ListUtil.fromArray(StringUtil.split(ParamUtil.getString(request, "requirements")));

		for (String requirement : requirements) {
			if (!JenkinsUtil.isValidJenkinsRequirement(requirement)) {
				throw new AlloyException("the-fix-pack's-requirement-is-invalid");
			}
		}
	}

	private void _validateSetBuild(PatcherFixPack patcherFixPack) throws Exception {
		Set<Long> addPatcherFixIds = new HashSet<Long>();

		Set<Long> removePatcherFixIds = new HashSet<Long>();

		List<Long> patcherFixPackPatcherFixIds = new ArrayList<Long>();

		List<PatcherFix> patcherFixPackPatcherFixes = PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFixPackPatcherFix : patcherFixPackPatcherFixes) {
			patcherFixPackPatcherFixIds.add(patcherFixPackPatcherFix.getPatcherFixId());

			if (!patcherFixPackPatcherFix.isLatestFix()) {
				removePatcherFixIds.add(patcherFixPackPatcherFix.getPatcherFixId());

				PatcherFix latestPatcherFix = PatcherFixUtil.fetchPatcherFixByLatestFix(patcherFixPackPatcherFix.getKey());

				addPatcherFixIds.add(latestPatcherFix.getPatcherFixId());
			}
		}

		List<Long> allPatcherFixIds = PatcherFixUtil.getPatcherFixIds(patcherFixPack);

		List<Long> copiedAllPatcherFixIds = ListUtil.copy(allPatcherFixIds);

		PatcherScanUtil.refinePatcherFixIds(allPatcherFixIds);

		List<Long> parentPatcherFixIds = PatcherFixRelUtil.getParentPatcherFixIds(allPatcherFixIds, copiedAllPatcherFixIds);

		for (long parentPatcherFixId : parentPatcherFixIds) {
			if (!patcherFixPackPatcherFixIds.contains(parentPatcherFixId)) {
				parentPatcherFixIds.remove(parentPatcherFixId);
			}
		}

		removePatcherFixIds.addAll(parentPatcherFixIds);

		addPatcherFixIds.addAll(PatcherFixRelUtil.getChildPatcherFixIds(allPatcherFixIds, copiedAllPatcherFixIds));

		if (!addPatcherFixIds.isEmpty() || !removePatcherFixIds.isEmpty()) {
			throw new AlloyException(translate("the-fix-pack-cannot-be-merged-as-x-needs-to-be-removed-and-x-needs-to-be-added", removePatcherFixIds, addPatcherFixIds));
		}

		_validatePreviousPatcherFixPacks(patcherFixPack);
	}

	private void _validateUnderDevelopmentFixPack(PatcherFixPack patcherFixPack) throws Exception {
		List<PatcherFixPack> patcherFixPackVersions = PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, false);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() != WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {
				throw new AlloyException("this-fix-pack-cannot-be-under-development-because-a-newer-fix-pack-version-is-frozen");
			}
		}
	}

	private void _validateUpdate(PatcherFixPack patcherFixPack) throws Exception {
		_validatePatcherFixPack(patcherFixPack);
		_validateRequirements();

		int status = ParamUtil.getInteger(request, "status");

		if ((patcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_FROZEN) && (status == WorkflowConstants.STATUS_FIX_PACK_FROZEN)) {
			_validateFrozenFixPack(patcherFixPack);
		}

		if ((patcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_RELEASED) && (status == WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {
			_validateReleaseFixPack(patcherFixPack);
		}

		if ((patcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) && (status == WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT)) {
			_validateUnderDevelopmentFixPack(patcherFixPack);
		}
	}

	private void _validateView(PatcherFixPack patcherFixPack) throws Exception {
		_validatePatcherFixPack(patcherFixPack);
	}

	private void _validateViewIssues() throws Exception {
		int fixPackVersion = ParamUtil.getInteger(request, "fixPackVersion");

		if (fixPackVersion <= 0) {
			throw new AlloyException("the-patcher-fix-pack-version-is-invalid");
		}

		String rootPatcherProjectVersionName = ParamUtil.getString(request, "rootPatcherProjectVersionName");

		if (Validator.isNull(rootPatcherProjectVersionName)) {
			throw new AlloyException("the-root-patcher-project-version-name-is-invalid");
		}

		PatcherProjectVersion rootPatcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(rootPatcherProjectVersionName);

		if ((rootPatcherProjectVersion == null) || (rootPatcherProjectVersion.getRootPatcherProjectVersionId() != 0)) {
			throw new AlloyException("the-root-patcher-project-version-name-does-not-exist");
		}

		String patcherProjectVersionName = _getPatcherProjectVersionName(rootPatcherProjectVersion, fixPackVersion);

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(patcherProjectVersionName);

		if (patcherProjectVersion == null) {
			throw new AlloyException("the-patcher-project-version-name-x-does-not-exist", new Object[] {patcherProjectVersionName});
		}

		if (rootPatcherProjectVersion.getPatcherProductVersionId() == PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			String patcherFixComponentName = ParamUtil.getString(request, "patcherFixComponentName");

			if (Validator.isNull(patcherFixComponentName)) {
				throw new AlloyException("the-patcher-fix-component-name-is-invalid");
			}

			PatcherFixComponent patcherFixComponent = PatcherFixComponentUtil.fetchPatcherFixComponent(patcherFixComponentName);

			if (patcherFixComponent == null) {
				throw new AlloyException("the-patcher-fix-component-name-does-not-exist");
			}

			PatcherFixPack patcherFixPack = PatcherFixPackUtil.fetchPatcherFixPackByRootPatcherProjectVersion(patcherFixComponent.getPatcherFixComponentId(), fixPackVersion, rootPatcherProjectVersion.getPatcherProjectVersionId());

			if (patcherFixPack == null) {
				throw new AlloyException("the-fix-pack-does-not-exist");
			}
		}
	}

}
%>