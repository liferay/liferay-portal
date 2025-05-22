<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/controllers/init.jspf" %>

<%!
public static class AlloyControllerImpl extends PatcherAlloyControllerImpl {

	public AlloyControllerImpl() {
		setAlloyServiceInvokerClass(PatcherFix.class);
		setPermissioned(true);
	}

	public void add() throws Exception {
		_validateAdd();

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.createPatcherFix(0);

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		patcherFix.setPatcherProductVersionId(patcherProductVersionId);

		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		patcherFix.setPatcherProjectVersionId(patcherProjectVersionId);

		String patcherFixName = PatcherUtil.preparePatcherName(ParamUtil.getString(request, "patcherFixName"));

		patcherFix.setKey(PatcherFixUtil.generateKey(patcherProjectVersionId, patcherFixName));

		List<String> patcherFixNames = PatcherUtil.sortTokens(patcherFixName);

		patcherFix.setName(StringUtil.merge(patcherFixNames));

		patcherFix.setKeyVersion(PatcherFixConstants.KEY_VERSION_DEFAULT);

		_savePatcherFix(patcherFix);

		if (isRespondingTo()) {
			respondWith(patcherFix);

			return;
		}

		_redirectOrClose();
	}

	public void builds() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateBuilds(patcherFix);

		renderRequest.setAttribute("patcherFix", patcherFix);

		List<PatcherBuild> patcherBuilds = PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(patcherFix.getPatcherFixId());

		if (isRespondingTo()) {
			List<Document> patcherBuildDocuments = new ArrayList<Document>();

			for (PatcherBuild patcherBuild : patcherBuilds) {
				patcherBuildDocuments.add(indexer.getDocument(patcherBuild));
			}

			respondWith(toJSONArray(patcherBuildDocuments.toArray()));

			return;
		}

		renderRequest.setAttribute("patcherBuilds", patcherBuilds);
	}

	public void create() throws Exception {
		PatcherFix patcherFix = PatcherFixLocalServiceUtil.createPatcherFix(0);

		patcherFix.setKeyVersion(PatcherFixConstants.KEY_VERSION_DEFAULT);

		renderRequest.setAttribute("patcherFix", patcherFix);

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		Map<Long, List<PatcherProjectVersion>> patcherProjectVersions = PatcherProjectVersionUtil.getPatcherProductVersionIdPatcherProjectVersions();

		renderRequest.setAttribute("patcherProjectVersionsJSON", JSONFactoryUtil.createJSONObject(JSONFactoryUtil.looseSerializeDeep(patcherProjectVersions)));

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	public void delete() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateDelete(patcherFix);

		PatcherFixUtil.deletePatcherFix(this, patcherFix);

		if (isRespondingTo()) {
			respondWith(patcherFix);

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void edit() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateEdit(patcherFix);

		if (patcherFix.isNew()) {
			patcherFix.setKeyVersion(PatcherFixConstants.KEY_VERSION_DEFAULT);
		}

		renderRequest.setAttribute("patcherFix", patcherFix);

		renderRequest.setAttribute("gitHubURL", PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()));
		renderRequest.setAttribute("patcherFixStatus", translate(WorkflowConstants.getStatusLabel(patcherFix.getStatus())));

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	public void editCommentsField() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateEditCommentsField(patcherFix);

		renderRequest.setAttribute("patcherFix", patcherFix);

		render("edit_comments_field");
	}

	public void editFixPackFields() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateEditFixPackFields(patcherFix);

		portletRequest.setAttribute("patcherFix", patcherFix);

		boolean disabled = false;

		List<PatcherFixPack> patcherFixPacks = PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(patcherFix.getPatcherFixId());

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			if ((patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_FROZEN) || (patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {
				disabled = true;
			}
		}

		portletRequest.setAttribute("patcherFixPacks", patcherFixPacks);

		portletRequest.setAttribute("disabled", disabled);

		_setAvailableCurrentPatcherFixPacks(patcherFix);

		render("edit_fix_pack_fields");
	}

	public void exclude() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateExclude(patcherFix);

		updateModel(patcherFix, "type", PatcherFixConstants.TYPE_EXCLUDED);

		PatcherFixUtil.updateObsolete(this, patcherFix, true);

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void fixes() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateFixes(patcherFix);

		renderRequest.setAttribute("childPatcherFix", patcherFix);

		List<PatcherFix> patcherFixes = PatcherFixUtil.getParentPatcherFixes(patcherFix);

		if (isRespondingTo()) {
			respondWith(patcherFixes);

			return;
		}

		renderRequest.setAttribute("patcherFixes", patcherFixes);
	}

	public void index() throws Exception {
		Sort sort = new Sort();

		String keywords = ParamUtil.getString(request, "keywords");

		String patcherFixName = ParamUtil.getString(request, "patcherFixName");

		if ((!PatcherUtil.isPatcherTickets(keywords) || PatcherUtil.isPatcherProjectVersionName(keywords)) && !PatcherUtil.isPatcherTickets(patcherFixName)) {
			sort = new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true);
		}

		AlloySearchResult alloySearchResult = search(PatcherUtil.prepareKeywords(keywords), sort);

		if (isRespondingTo()) {
			respondWith(alloySearchResult);

			return;
		}

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		renderRequest.setAttribute("displayTerms", new DisplayTerms(request));

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.ACTION_PHASE, parameterNames = {"committish", "gitRemoteURL", "patcherFixName", "patcherProjectVersionName", "workaround"}, parameterTypes = {String.class, String.class, String.class, String.class, Boolean.class})
	public void saveByName() throws Exception {
		_validateSaveByName();

		String patcherProjectVersionName = ParamUtil.getString(request, "patcherProjectVersionName");

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(patcherProjectVersionName);

		setParameters("patcherProjectVersionId", patcherProjectVersion.getPatcherProjectVersionId(), "patcherProductVersionId", patcherProjectVersion.getPatcherProductVersionId());

		String patcherFixName = PatcherUtil.preparePatcherName(ParamUtil.getString(request, "patcherFixName"));

		List<PatcherFix> patcherFixes = PatcherFixUtil.getFilteredPatcherFixes(patcherProjectVersion.getPatcherProjectVersionId(), patcherFixName, true);

		if (!patcherFixes.isEmpty()) {
			PatcherFix patcherFix = patcherFixes.get(0);

			setParameters("id", patcherFix.getPatcherFixId());

			_update(patcherFix);
		}
		else {
			add();
		}
	}

	public void setFixPackFields() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateSetFixPackFields(patcherFix);

		PatcherFixPackLocalServiceUtil.clearPatcherFixPatcherFixPacks(patcherFix.getPatcherFixId());

		Set<Long> patcherFixPackIds = SetUtil.fromArray(ParamUtil.getLongValues(request, "patcherFixPackIds"));

		for (long patcherFixPackId : patcherFixPackIds) {
			PatcherFixPackLocalServiceUtil.addPatcherFixPatcherFixPack(patcherFix.getPatcherFixId(), patcherFixPackId);
		}

		String dependencies = ParamUtil.getString(request, "dependencies");

		patcherFix.setDependencies(dependencies);

		patcherFix.setFixPackStatus(ParamUtil.getInteger(request, "fixPackStatus"));

		patcherFix.setRequirements(ParamUtil.getString(request, "requirements"));

		updateModelIgnoreRequest(patcherFix);

		_redirectOrClose();
	}

	public void update() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_update(patcherFix);
	}

	public void updateCommentsField() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateUpdateCommentsField(patcherFix);

		String comments = ParamUtil.getString(request, "comments");

		updateModelIgnoreRequest(patcherFix, "comments", comments);

		PatcherUtil.pollIndexState(this, PatcherFix.class.getName(), patcherFix.getPatcherFixId(), "comments", comments);

		if (isRespondingTo()) {
			respondWith(patcherFix);

			return;
		}

		_redirectOrClose();
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"id"}, parameterTypes = {Long.class})
	public void view() throws Exception {
		PatcherFix patcherFix = _fetchPatcherFix();

		_validateView(patcherFix);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherFix));

			return;
		}

		renderRequest.setAttribute("patcherFix", patcherFix);

		Map<String, Serializable> attributes = new HashMap<String, Serializable>();

		attributes.put("key", patcherFix.getKey());
		attributes.put("viewSearch", true);

		AlloySearchResult alloySearchResult = search(attributes, StringPool.BLANK, new Sort("keyVersion", true));

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		renderRequest.setAttribute("gitHubURL", PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()));

		renderRequest.setAttribute("jenkinsResults", JenkinsUtil.getJenkinsResults(patcherFix));

		if (!patcherFix.getLatestFix()) {
			renderRequest.setAttribute("latestPatcherFix", PatcherFixUtil.fetchPatcherFixByLatestFix(patcherFix.getKey()));
		}

		renderRequest.setAttribute("patcherFixStatus", translate(WorkflowConstants.getStatusLabel(patcherFix.getStatus())));

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		List<PatcherProjectVersion> patcherProjectVersions = PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		renderRequest.setAttribute("patcherProjectVersions", patcherProjectVersions);

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	@Override
	protected Indexer buildIndexer() {
		return PatcherFixIndexer.getInstance();
	}

	@Override
	protected MessageListener buildSchedulerMessageListener() {
		return PatcherFixSchedulerMessageListener.getInstance(this);
	}

	@Override
	protected Trigger getSchedulerTrigger() {
		return new CronTrigger(getSchedulerJobName(), getMessageListenerGroupName(), "*/4 * * * * ? *");
	}

	private PatcherFix _fetchPatcherFix() throws Exception {
		long patcherFixId = ParamUtil.getLong(request, "id");

		return PatcherFixLocalServiceUtil.fetchPatcherFix(patcherFixId);
	}

	private void _redirectOrClose() {
		if (themeDisplay.isStatePopUp()) {
			setOpenerSuccessMessage();

			render("../close");
		}
		else {
			addSuccessMessage();

			String redirect = ParamUtil.getString(request, "redirect");

			redirectTo(redirect);
		}
	}

	private void _savePatcherFix(PatcherFix patcherFix) throws Exception {
		String committish = ParamUtil.getString(request, "committish");
		String gitRemoteURL = ParamUtil.getString(request, "gitRemoteURL");
		boolean workaround = ParamUtil.getBoolean(request, "workaround");

		int status = WorkflowConstants.STATUS_FIX_ADDING;
		int type = PatcherFixConstants.TYPE_PATCH;

		if (patcherFix.getType() == PatcherFixConstants.TYPE_REBASE) {
			if (Validator.isNull(committish) || Validator.isNull(gitRemoteURL)) {
				status = WorkflowConstants.STATUS_FIX_REBASING;
			}

			type = PatcherFixConstants.TYPE_REBASE;
		}
		else if (workaround) {
			type = PatcherFixConstants.TYPE_WORKAROUND;
		}

		updateModelIgnoreRequest(patcherFix, "committish", committish, "gitRemoteURL", gitRemoteURL, "latestFix", true, "obsolete", false, "status", status, "type", type);

		List<PatcherBuild> patcherBuilds = PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(patcherFix.getPatcherFixId());

		for (PatcherBuild patcherBuild : patcherBuilds) {
			List<PatcherFix> incompletePatcherFixes = PatcherBuildUtil.getIncompletePatcherFixes(patcherBuild);

			if (incompletePatcherFixes.size() > 2) {
				continue;
			}

			int patcherBuildStatus = PatcherBuildUtil.getNextPatcherBuildWorkflowStatus(patcherBuild, PatcherBuildUtil.isMergeOnly(patcherBuild));

			PatcherBuildUtil.setStatus(this, user, patcherBuild, patcherBuildStatus);

			updateModelIgnoreRequest(patcherBuild);
		}

		JenkinsUtil.sendAgentJenkinsRequest(this, user, patcherFix);

		PatcherUtil.pollIndexState(this, PatcherFix.class.getName(), patcherFix.getPatcherFixId());
	}

	private void _setAvailableCurrentPatcherFixPacks(PatcherFix patcherFix) throws Exception {
		List<KeyValuePair> availablePatcherFixPacks = new ArrayList<KeyValuePair>();
		List<KeyValuePair> currentPatcherFixPacks = new ArrayList<KeyValuePair>();

		List<PatcherFixPack> availablePatcherFixPatcherFixPacks = new ArrayList<PatcherFixPack>();

		AlloyServiceInvoker patcherFixPackAlloyServiceInvoker = new AlloyServiceInvoker(PatcherFixPack.class.getName());

		DynamicQuery patcherFixPackDynamicQuery = patcherFixPackAlloyServiceInvoker.buildDynamicQuery();

		Property patcherProjectVersionIdProperty = PropertyFactoryUtil.forName("patcherProjectVersionId");

		patcherFixPackDynamicQuery.add(patcherProjectVersionIdProperty.eq(patcherFix.getPatcherProjectVersionId()));

		Property statusProperty = PropertyFactoryUtil.forName("status");

		patcherFixPackDynamicQuery.add(statusProperty.eq(WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT));

		availablePatcherFixPatcherFixPacks = patcherFixPackAlloyServiceInvoker.executeDynamicQuery(patcherFixPackDynamicQuery);

		List<PatcherFixPack> patcherFixPacks = PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(patcherFix.getPatcherFixId());

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherFixPackModelImpl.TABLE_NAME, "patcherProjectVersionId", true, "patcherFixComponentId", true, "version", true);

		patcherFixPacks = ListUtil.sort(patcherFixPacks, obc);

		for (PatcherFixPack currentPatcherFixPack : patcherFixPacks) {
			currentPatcherFixPacks.add(new KeyValuePair(String.valueOf(currentPatcherFixPack.getPatcherFixPackId()), translate(currentPatcherFixPack.getName())));
		}

		currentPatcherFixPacks = ListUtil.sort(currentPatcherFixPacks, new KeyValuePairComparator(false, true));

		portletRequest.setAttribute("currentPatcherFixPacks", currentPatcherFixPacks);

		availablePatcherFixPatcherFixPacks = ListUtil.sort(availablePatcherFixPatcherFixPacks, obc);

		availablePatcherFixPatcherFixPacks.removeAll(patcherFixPacks);

		for (PatcherFixPack availablePatcherFixPack : availablePatcherFixPatcherFixPacks) {
			availablePatcherFixPacks.add(new KeyValuePair(String.valueOf(availablePatcherFixPack.getPatcherFixPackId()), translate(availablePatcherFixPack.getName())));
		}

		availablePatcherFixPacks = ListUtil.sort(availablePatcherFixPacks, new KeyValuePairComparator(false, true));

		portletRequest.setAttribute("availablePatcherFixPacks", availablePatcherFixPacks);
	}

	private void _update(PatcherFix patcherFix) throws Exception {
		_validateUpdate(patcherFix);

		List<PatcherFix> parentPatcherFixes = PatcherFixRelUtil.getParentPatcherFixes(patcherFix);

		if (patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_COMPLETE) {
			patcherFix.setLatestFix(false);

			updateModelIgnoreRequest(patcherFix);

			PatcherFixUtil.updateObsolete(this, patcherFix, true);

			PatcherFix newPatcherFix = PatcherFixLocalServiceUtil.createPatcherFix(0);

			newPatcherFix.setKey(patcherFix.getKey());
			newPatcherFix.setKeyVersion(BigDecimalUtil.add(patcherFix.getKeyVersion(), 0.1));
			newPatcherFix.setName(patcherFix.getName());
			newPatcherFix.setPatcherProductVersionId(patcherFix.getPatcherProductVersionId());
			newPatcherFix.setPatcherProjectVersionId(patcherFix.getPatcherProjectVersionId());
			newPatcherFix.setType(patcherFix.getType());

			updateModelIgnoreRequest(newPatcherFix);

			patcherFix = newPatcherFix;
		}
		else if (PatcherFixUtil.isIncomplete(patcherFix)) {
			patcherFix.setGitHash(StringPool.BLANK);
			patcherFix.setJenkinsResults(StringPool.BLANK);

			PatcherFixPackLocalServiceUtil.clearPatcherFixPatcherFixPacks(patcherFix.getPatcherFixId());
		}

		if (patcherFix.getType() == PatcherFixConstants.TYPE_REBASE) {
			PatcherFixRelUtil.deletePatcherFixRelsByChildPatcherFixId(patcherFix.getPatcherFixId());

			String committish = ParamUtil.getString(request, "committish");
			String gitRemoteURL = ParamUtil.getString(request, "gitRemoteURL");

			if (Validator.isNotNull(committish) && Validator.isNotNull(gitRemoteURL)) {
				patcherFix.setType(PatcherFixConstants.TYPE_PATCH);
			}
			else {
				PatcherFix rebaseFromPatcherFix = parentPatcherFixes.get(0);

				if (!rebaseFromPatcherFix.isLatestFix()) {
					rebaseFromPatcherFix = PatcherFixUtil.fetchPatcherFixByLatestFix(rebaseFromPatcherFix.getKey());
				}

				PatcherFixRelUtil.addPatcherFixRel(this, patcherFix.getPatcherFixId(), ListUtil.toList(new long[] {rebaseFromPatcherFix.getPatcherFixId()}));
			}
		}

		_savePatcherFix(patcherFix);

		if (isRespondingTo()) {
			respondWith(patcherFix);

			return;
		}

		_redirectOrClose();
	}

	private void _validateAdd() throws Exception {
		_validateCommittish();
		_validateGitRemoteURL();
		_validateProductVersion();
		_validatePatcherProjectVersionId();

		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		_validateName(patcherProjectVersionId);

		if (!PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(patcherProjectVersionId)) {
			_validateSiblingProjectVersionFixes();
		}

		_validateKey();
	}

	private void _validateBuilds(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

	private void _validateCommittish() throws Exception {
		String committish = ParamUtil.getString(request, "committish");

		if (Validator.isNull(committish)) {
			throw new Exception("the-fix-branch-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByCommittish(committish);

		if (patcherProjectVersion != null) {
			throw new Exception(translate("the-branch-name-cannot-be-the-same-as-the-project-version-tag-name-x", patcherProjectVersion.getCommittish()));
		}
	}

	private void _validateDelete(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);

		PatcherFixUtil.validateDelete(patcherFix);
	}

	private void _validateEdit(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

	private void _validateEditCommentsField(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

	private void _validateEditFixPackFields(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

	private void _validateExclude(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

	private void _validateFixes(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

	private void _validateGitRemoteURL() throws Exception {
		String gitRemoteURL = ParamUtil.getString(request, "gitRemoteURL");

		Pattern pattern = Pattern.compile(PatcherConstants.GIT_REMOTE_URL_REGEX);

		Matcher matcher = pattern.matcher(gitRemoteURL);

		if (!matcher.find()) {
			throw new Exception("the-fix-github-url-is-invalid");
		}
	}

	private void _validateKey() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		String patcherFixName = PatcherUtil.preparePatcherName(ParamUtil.getString(request, "patcherFixName"));

		String key = PatcherFixUtil.generateKey(patcherProjectVersionId, patcherFixName);

		List<PatcherFix> patcherFixes = PatcherFixUtil.getFilteredPatcherFixes(key, true);

		if (!patcherFixes.isEmpty()) {
			throw new Exception("the-fix-already-exists");
		}
	}

	private void _validateName(long patcherProjectVersionId) throws Exception {
		String patcherFixName = PatcherUtil.preparePatcherName(ParamUtil.getString(request, "patcherFixName"));

		if (Validator.isNull(patcherFixName)) {
			throw new Exception("the-fix-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherProjectVersionId);

		if (!PatcherUtil.isPatcherTickets(patcherFixName, patcherProjectVersion.getPatcherProductVersionId())) {
			throw new Exception("the-fix-name-cannot-be-evaluated");
		}

		if (patcherProjectVersion.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			List<String> fixedIssues = PatcherUtil.getTokens(patcherProjectVersion.getFixedIssues());

			fixedIssues.retainAll(PatcherUtil.getTokens(patcherFixName));

			if (!fixedIssues.isEmpty()) {
				throw new Exception(translate("the-tickets-x-is-already-included-in-project-version-x", StringUtil.merge(fixedIssues), patcherProjectVersion.getName()));
			}
		}
	}

	private void _validateParentPatcherBuildMainFix(PatcherFix patcherFix) throws Exception {
		AlloyServiceInvoker patcherBuildAlloyServiceInvoker = new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildDynamicQuery = patcherBuildAlloyServiceInvoker.buildDynamicQuery(new Object[] {"childBuild", false, "patcherFixId", patcherFix.getPatcherFixId()});

		Property productVersionProperty = PropertyFactoryUtil.forName("patcherProductVersionId");

		patcherBuildDynamicQuery.add(productVersionProperty.ne(PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)));

		Property typeProperty = PropertyFactoryUtil.forName("type");

		patcherBuildDynamicQuery.add(typeProperty.ne(PatcherBuildConstants.TYPE_FIX_PACK));

		long count = patcherBuildAlloyServiceInvoker.executeDynamicQueryCount(patcherBuildDynamicQuery);

		if ((count > 0) && (patcherFix.getType() == PatcherFixConstants.TYPE_GENERATED) && (patcherFix.getStatus() == WorkflowConstants.STATUS_ANY)) {
			throw new Exception("the-main-fix-of-a-parent-build-cannot-change");
		}
	}

	private void _validatePatcherFix(PatcherFix patcherFix) throws Exception {
		if (patcherFix == null) {
			throw new Exception("the-fix-does-not-exist");
		}
	}

	private void _validatePatcherFixPackMainFix(PatcherFix patcherFix) throws Exception {
		PatcherFixPack patcherFixPack = PatcherFixPackUtil.fetchPatcherFixPack(patcherFix.getName(), patcherFix.getPatcherProjectVersionId());

		if (patcherFixPack != null) {
			throw new Exception("the-main-fix-of-a-fix-pack-cannot-change");
		}
	}

	private void _validatePatcherFixPackUnderDevelopment(PatcherFixPack patcherFixPack) throws Exception {
		if (patcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {
			throw new Exception(translate("the-fix-pack-x-is-not-under-development-so-its-dependencies-cannot-change", patcherFixPack.getName()));
		}
	}

	private void _validatePatcherProjectVersionId() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		_validatePatcherProjectVersionId(patcherProjectVersionId);
	}

	private void _validatePatcherProjectVersionId(long patcherProjectVersionId) throws Exception {
		if (patcherProjectVersionId == 0) {
			throw new Exception("the-project-version-is-invalid");
		}
	}

	private void _validatePatcherProjectVersionName() throws Exception {
		String patcherProjectVersionName = ParamUtil.getString(request, "patcherProjectVersionName");

		if (Validator.isNull(patcherProjectVersionName)) {
			throw new Exception("the-project-version-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(patcherProjectVersionName);

		if (patcherProjectVersion == null) {
			throw new Exception("the-project-version-name-is-invalid");
		}
	}

	private void _validateProductVersion() throws Exception {
		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(patcherProductVersionId);

		if (patcherProductVersion == null) {
			throw new Exception("the-product-version-id-is-invalid");
		}
	}

	private void _validateSaveByName() throws Exception {
		_validatePatcherProjectVersionName();

		String patcherProjectVersionName = ParamUtil.getString(request, "patcherProjectVersionName");

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(patcherProjectVersionName);

		_validateName(patcherProjectVersion.getPatcherProjectVersionId());
	}

	private void _validateSetFixPackFields(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);

		_validatePatcherFixPackMainFix(patcherFix);

		Set<String> dependenciesComponentNames = new HashSet<String>();

		String dependencies = ParamUtil.getString(request, "dependencies");

		List<String> dependenciesTokens = PatcherUtil.getTokens(dependencies);

		for (String dependenciesToken : dependenciesTokens) {
			String[] names = dependenciesToken.split("->");

			if ((names.length % 2) != 0) {
				throw new Exception("the-fix's-dependencies-are-invalid");
			}

			for (String name : names) {
				AlloyServiceInvoker patcherFixComponentAlloyServiceInvoker = new AlloyServiceInvoker(PatcherFixComponent.class.getName());

				List<PatcherFixComponent> patcherFixComponents = patcherFixComponentAlloyServiceInvoker.executeDynamicQuery(new Object[] {"name", name});

				if (patcherFixComponents.isEmpty()) {
					throw new Exception("the-fix's-dependencies-has-an-invalid-fix-component");
				}

				dependenciesComponentNames.add(name);
			}
		}

		Set<Long> patcherFixComponentIds = new HashSet<Long>();

		Map<String, Set<String>> patcherFixComponentDependencies = PatcherFixUtil.getComponentDependencies(patcherFix.getDependencies());
		Map<String, Set<String>> requestComponentDependencies = PatcherFixUtil.getComponentDependencies(dependencies);

		Set<Long> patcherFixPackIds = SetUtil.fromArray(ParamUtil.getLongValues(request, "patcherFixPackIds"));

		for (long patcherFixPackId : patcherFixPackIds) {
			PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.getPatcherFixPack(patcherFixPackId);

			if (patcherFixComponentIds.contains(patcherFixPack.getPatcherFixComponentId())) {
				throw new Exception("the-fix-cannot-be-in-multiple-fix-packs-with-the-same-component");
			}

			patcherFixComponentIds.add(patcherFixPack.getPatcherFixComponentId());

			PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(patcherFixPack.getPatcherFixComponentId());

			dependenciesComponentNames.remove(patcherFixComponent.getName());

			if (patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {
				continue;
			}

			Set<String> patcherFixComponentNameDependencies = new HashSet<String>();

			if (patcherFixComponentDependencies.containsKey(patcherFixComponent.getName())) {
				patcherFixComponentNameDependencies = patcherFixComponentDependencies.get(patcherFixComponent.getName());
			}

			Set<String> requestComponentNameDependencies = new HashSet<String>();

			if (requestComponentDependencies.containsKey(patcherFixComponent.getName())) {
				requestComponentNameDependencies = requestComponentDependencies.get(patcherFixComponent.getName());
			}

			if (!requestComponentNameDependencies.equals(patcherFixComponentNameDependencies)) {
				throw new Exception(translate("the-fix-pack-x-is-not-under-development-so-its-dependencies-cannot-change", patcherFixPack.getName()));
			}
		}

		if (!dependenciesComponentNames.isEmpty()) {
			throw new Exception("the-fix's-current-fix-packs-must-include-the-fix's-dependency-components");
		}

		Set<Long> changedPatcherFixPackIds = new HashSet<Long>();

		Set<Long> oldPatcherFixPackIds = new HashSet<Long>();

		List<PatcherFixPack> patcherFixPatcherFixPacks = PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(patcherFix.getPatcherFixId());

		for (PatcherFixPack patcherFixPatcherFixPack : patcherFixPatcherFixPacks) {
			oldPatcherFixPackIds.add(patcherFixPatcherFixPack.getPatcherFixPackId());
		}

		Set<Long> newPatcherFixPackIds = new HashSet<Long>(patcherFixPackIds);

		newPatcherFixPackIds.removeAll(oldPatcherFixPackIds);

		for (long newPatcherFixPackId : newPatcherFixPackIds) {
			List<String> patcherFixTokens = PatcherUtil.getTokens(patcherFix.getName());

			List<PatcherFix> patcherFixPackPatcherFixes = PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(newPatcherFixPackId);

			String patcherFixesNames = ListUtil.toString(patcherFixPackPatcherFixes, "name");

			patcherFixTokens.retainAll(PatcherUtil.getTokens(patcherFixesNames));

			if (patcherFixTokens.isEmpty()) {
				continue;
			}

			List<Long> patcherFixIds = new ArrayList<Long>();

			for (PatcherFix patcherFixPackPatcherFix : patcherFixPackPatcherFixes) {
				List<String> patcherFixPackPatcherFixTokens = PatcherUtil.getTokens(patcherFixPackPatcherFix.getName());

				if (!Collections.disjoint(patcherFixTokens, patcherFixPackPatcherFixTokens)) {
					patcherFixIds.add(patcherFixPackPatcherFix.getPatcherFixId());
				}
			}

			PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.getPatcherFixPack(newPatcherFixPackId);

			throw new Exception(translate("the-fix-pack-x-already-has-tickets-x-in-fixes-x", patcherFixPack.getName(), StringUtil.merge(patcherFixTokens), StringUtil.merge(patcherFixIds)));
		}

		changedPatcherFixPackIds.addAll(newPatcherFixPackIds);

		oldPatcherFixPackIds.removeAll(patcherFixPackIds);

		changedPatcherFixPackIds.addAll(oldPatcherFixPackIds);

		for (long changedPatcherFixPackId : changedPatcherFixPackIds) {
			PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.getPatcherFixPack(changedPatcherFixPackId);

			_validatePatcherFixPackUnderDevelopment(patcherFixPack);
		}

		List<String> requirementsTokens = PatcherUtil.getTokens(ParamUtil.getString(request, "requirements"));

		for (String requirementsToken : requirementsTokens) {
			if (!JenkinsUtil.isValidJenkinsRequirement(requirementsToken)) {
				throw new Exception(translate("the-fix's-requirement-x-is-invalid", requirementsToken));
			}
		}
	}

	private void _validateSiblingProjectVersionFixes() throws Exception {
		Set<String> siblingPatcherProjectVersionFixTickets = new HashSet<String>();

		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(patcherProjectVersionId);

		PatcherProjectVersion siblingPatcherProjectVersion = PatcherProjectVersionUtil.getSiblingPatcherProjectVersion(patcherProjectVersion.getCommittish());

		List<PatcherFix> siblingPatcherProjectVersionFixes = PatcherFixUtil.getFilteredPatcherFixes(siblingPatcherProjectVersion.getPatcherProjectVersionId(), WorkflowConstants.STATUS_ANY);

		for (PatcherFix siblingPatcherProjectVersionFix : siblingPatcherProjectVersionFixes) {
			if (siblingPatcherProjectVersionFix.getType() == PatcherFixConstants.TYPE_EXCLUDED) {
				continue;
			}

			siblingPatcherProjectVersionFixTickets.addAll(PatcherUtil.getTickets(siblingPatcherProjectVersionFix.getName()));
		}

		String patcherFixName = PatcherUtil.preparePatcherName(ParamUtil.getString(request, "patcherFixName"));

		List<String> patcherFixTickets = PatcherUtil.getTickets(patcherFixName);

		for (String patcherFixTicket : patcherFixTickets) {
			if (siblingPatcherProjectVersionFixTickets.contains(patcherFixTicket)) {
				throw new Exception(translate("the-fix-cannot-be-added-because-there-are-fixes-containing-x-on-project-version-x", patcherFixTicket, siblingPatcherProjectVersion.getCommittish()));
			}
		}
	}

	private void _validateUpdate(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);

		if ((patcherFix.getType() != PatcherFixConstants.TYPE_REBASE) || (patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_REBASE_CONFLICT)) {
			_validateCommittish();
			_validateGitRemoteURL();
		}

		_validateParentPatcherBuildMainFix(patcherFix);

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");
		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		if ((patcherProductVersionId != 0) && (patcherProductVersionId != patcherFix.getPatcherProductVersionId())) {
			throw new Exception("the-product-version-cannot-be-changed");
		}
		else if ((patcherProjectVersionId != 0L) && (patcherProjectVersionId != patcherFix.getPatcherProjectVersionId())) {
			throw new Exception("the-project-version-cannot-be-changed");
		}

		_validatePatcherFixPackMainFix(patcherFix);

		List<PatcherFixPack> patcherFixPacks = PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(patcherFix.getPatcherFixId());

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			_validatePatcherFixPackUnderDevelopment(patcherFixPack);
		}

		if (!patcherFix.getLatestFix()) {
			throw new Exception("the-fix-cannot-be-versioned-because-the-current-fix-is-not-the-latest");
		}
	}

	private void _validateUpdateCommentsField(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

	private void _validateView(PatcherFix patcherFix) throws Exception {
		_validatePatcherFix(patcherFix);
	}

}
%>