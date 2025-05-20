<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/controllers/init.jspf" %>

<%!
public static class AlloyControllerImpl extends PatcherAlloyControllerImpl {

	public AlloyControllerImpl() {
		setAlloyServiceInvokerClass(PatcherBuild.class);
		setPermissioned(true);
	}

	public void add() throws Exception {
		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.createPatcherBuild(increment());

		_validateAdd();

		String accountEntryCode = StringUtil.toUpperCase(ParamUtil.getString(request, "patcherBuildAccountEntryCode"));

		PatcherAccount patcherAccount = PatcherAccountUtil.fetchPatcherAccount(accountEntryCode);

		if (Validator.isNotNull(patcherAccount)) {
			patcherBuild.setPatcherAccountId(patcherAccount.getPatcherAccountId());
		}

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		patcherBuild.setPatcherProductVersionId(patcherProductVersionId);

		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		patcherBuild.setPatcherProjectVersionId(patcherProjectVersionId);

		String patcherBuildName = PatcherUtil.preparePatcherName(ParamUtil.getString(request, "patcherBuildName"));

		List<String> patcherBuildTokens = PatcherUtil.sortTokens(patcherBuildName);

		if (patcherProductVersionId != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			patcherBuild.setOriginalName(StringUtil.merge(patcherBuildTokens));

			List<String> cumulativePatcherProjectVersionFixedIssues = PatcherProjectVersionUtil.getCumulativePatcherProjectVersionFixedIssues(patcherProjectVersionId);

			patcherBuildTokens.removeAll(cumulativePatcherProjectVersionFixedIssues);
		}

		patcherBuild.setName(StringUtil.merge(patcherBuildTokens));

		patcherBuild.setKey(PatcherBuildUtil.generateKey(patcherProjectVersionId, patcherBuildName, accountEntryCode));

		int type = ParamUtil.getInteger(request, "type");

		patcherBuild.setType(type);

		boolean useExistingHotfix = ParamUtil.getBoolean(request, "useExistingHotfix");

		if (useExistingHotfix) {
			_linkExistingHotfix(patcherBuild);
		}

		_savePatcherBuild(user, patcherBuild);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		Layout layout = themeDisplay.getLayout();

		PortletURL portletURL = PortletURLFactoryUtil.create(request, PortletKeys.OSB_PATCHER, layout.getPlid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter("action", "view");
		portletURL.setParameter("controller", "builds");
		portletURL.setParameter("id", String.valueOf(patcherBuild.getPatcherBuildId()));

		setParameters("redirect", portletURL.toString());

		_redirectOrClose();
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.ACTION_PHASE, parameterNames = {"mergeOnly", "patcherBuildAccountEntryCode", "patcherBuildName", "supportTicket", "patcherProjectVersionName", "smokeTestOnly", "typeLabel"}, parameterTypes = {Boolean.class, String.class, String.class, String.class, String.class, Boolean.class, String.class})
	public void addByName() throws Exception {
		_validateAddByName();

		String patcherProjectVersionName = ParamUtil.getString(request, "patcherProjectVersionName");

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.getPatcherProjectVersion(patcherProjectVersionName);

		String typeLabel = ParamUtil.getString(request, "typeLabel");

		setParameters("patcherProjectVersionId", patcherProjectVersion.getPatcherProjectVersionId(), "patcherProductVersionId", patcherProjectVersion.getPatcherProductVersionId(), "type", PatcherBuildConstants.getLabelType(typeLabel));

		add();
	}

	public void build() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateBuild(patcherBuild);

		patcherBuild = _versionPatcherBuild(patcherBuild);

		if (patcherBuild.isNew()) {
			PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(patcherBuild.getPatcherAccountId());

			setParameters("mergeOnly", PatcherBuildUtil.isMergeOnly(patcherBuild), "patcherBuildAccountEntryCode", patcherAccount.getAccountEntryCode(), "smokeTestOnly", PatcherBuildUtil.isSmokeTestOnly(patcherBuild), "supportTicket", patcherBuild.getSupportTicket());

			_savePatcherBuild(user, patcherBuild);
		}
		else {
			JenkinsUtil.sendDistJenkinsRequest(this, user, patcherBuild);

			PatcherBuildUtil.setStatus(this, user, patcherBuild, WorkflowConstants.STATUS_BUILD_COMPILING);

			updateModelIgnoreRequest(patcherBuild);

			PatcherUtil.pollIndexState(this, PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(), "status", WorkflowConstants.STATUS_BUILD_COMPILING);
		}

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void childBuilds() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateViewBuilds(patcherBuild);

		if (PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId())) {
			return;
		}

		renderRequest.setAttribute("parentPatcherBuild", patcherBuild);

		List<PatcherBuild> childPatcherBuilds = PatcherBuildRelUtil.getChildPatcherBuilds(patcherBuild);

		if (isRespondingTo()) {
			respondWith(childPatcherBuilds);

			return;
		}

		renderRequest.setAttribute("childPatcherBuilds", childPatcherBuilds);

		render("child_builds");
	}

	public void content() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateContent(patcherBuild);

		renderRequest.setAttribute("patcherProjectVersionId", patcherBuild.getPatcherProjectVersionId());

		renderRequest.setAttribute("tickets", patcherBuild.getName());

		render("../view_tickets");
	}

	public void create() throws Exception {
		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.createPatcherBuild(0);

		patcherBuild.setKeyVersion(PatcherBuildConstants.KEY_VERSION_DEFAULT);

		long templatePatcherBuildId = ParamUtil.getLong(request, "templatePatcherBuildId");

		if (templatePatcherBuildId > 0) {
			PatcherBuild templatePatcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(templatePatcherBuildId);

			if (templatePatcherBuild == null) {
				return;
			}

			patcherBuild.setName(templatePatcherBuild.getName());
			patcherBuild.setPatcherAccountId(templatePatcherBuild.getPatcherAccountId());
			patcherBuild.setPatcherProductVersionId(templatePatcherBuild.getPatcherProductVersionId());
			patcherBuild.setPatcherProjectVersionId(templatePatcherBuild.getPatcherProjectVersionId());
			patcherBuild.setType(templatePatcherBuild.getType());

			renderRequest.setAttribute("mergeOnly", PatcherBuildUtil.isMergeOnly(templatePatcherBuild));

			PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(templatePatcherBuild.getPatcherAccountId());

			renderRequest.setAttribute("patcherBuildAccountEntryCode", patcherAccount.getAccountEntryCode());

			renderRequest.setAttribute("patcherProjectVersionId", templatePatcherBuild.getPatcherProjectVersionId());

			renderRequest.setAttribute("smokeTestOnly", PatcherBuildUtil.isSmokeTestOnly(templatePatcherBuild));
		}

		if (patcherBuild.getType() != PatcherBuildConstants.TYPE_DEBUG) {
			patcherBuild.setType(PatcherBuildConstants.TYPE_OFFICIAL);
		}

		renderRequest.setAttribute("patcherBuild", patcherBuild);

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		Map<Long, List<PatcherProjectVersion>> patcherProjectVersions = PatcherProjectVersionUtil.getPatcherProductVersionIdPatcherProjectVersions();

		renderRequest.setAttribute("patcherProjectVersionsJSON", JSONFactoryUtil.createJSONObject(JSONFactoryUtil.looseSerializeDeep(patcherProjectVersions)));

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	public void delete() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateDelete(patcherBuild);

		PatcherBuildUtil.deletePatcherBuildAndChildBuilds(this, patcherBuild);

		if (isRespondingTo()) {
			respondWith(patcherBuild);

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void edit() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateEdit(patcherBuild);

		renderRequest.setAttribute("patcherBuild", patcherBuild);

		List<String> patcherBuildTickets = PatcherUtil.getTickets(patcherBuild.getName());

		List<String> cumulativeFixedIssues = PatcherProjectVersionUtil.getCumulativePatcherProjectVersionFixedIssues(patcherBuild.getPatcherProjectVersionId());

		patcherBuildTickets.retainAll(cumulativeFixedIssues);

		renderRequest.setAttribute("overlappingProjectVersionFixedIssues", StringUtil.merge(patcherBuildTickets));

		PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(patcherBuild.getPatcherAccountId());

		renderRequest.setAttribute("patcherBuildAccountEntryCode", patcherAccount.getAccountEntryCode());

		renderRequest.setAttribute("patcherBuildMergeOnly", PatcherBuildUtil.isMergeOnly(patcherBuild));

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	public void editCommentsField() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateEditCommentsField(patcherBuild);

		renderRequest.setAttribute("patcherBuild", patcherBuild);

		render("edit_comments_field");
	}

	public void editQAFields() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateEditQAFields(patcherBuild);

		renderRequest.setAttribute("patcherBuild", patcherBuild);

		render("edit_qa_fields");
	}

	public void fixes() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateViewFixes(patcherBuild);

		renderRequest.setAttribute("patcherBuild", patcherBuild);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherFixModelImpl.TABLE_NAME, "status", false);

		List<PatcherFix> patcherFixes = PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(patcherBuild.getPatcherBuildId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);

		patcherFixes = ListUtil.copy(patcherFixes);

		PatcherFix mainPatcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherBuild.getPatcherFixId());

		if ((mainPatcherFix != null) && (mainPatcherFix.getStatus() == WorkflowConstants.STATUS_FIX_CONFLICT)) {
			if (PatcherFixPackUtil.containsPatcherFixPackName(mainPatcherFix.getName())) {
				patcherFixes.remove(mainPatcherFix);
			}
			else {
				for (PatcherFix patcherFix : patcherFixes) {
					if (!patcherFix.equals(mainPatcherFix) && ((patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_CONFLICT) || (patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_ADDING))) {
						patcherFixes.remove(mainPatcherFix);

						break;
					}
				}
			}
		}
		else if (patcherFixes.size() > 1) {
			patcherFixes.remove(mainPatcherFix);
		}

		if (isRespondingTo()) {
			respondWith(patcherFixes);

			return;
		}

		renderRequest.setAttribute("patcherFixes", patcherFixes);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.ACTION_PHASE, parameterNames = {"limit", "tickets", "productVersionId", "projectVersionId"}, parameterTypes = {Integer.class, String.class, Long.class, Long.class})
	public void getTicketSuggestionFields() throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		JSONArray regressionTicketJSONArray = JSONFactoryUtil.createJSONArray();

		regressionTicketJSONArray.put("");

		jsonObject.put("regression", regressionTicketJSONArray);

		JSONArray securityTicketJSONArray = JSONFactoryUtil.createJSONArray();

		securityTicketJSONArray.put("");

		jsonObject.put("security", securityTicketJSONArray);

		JSONArray troubleshootTicketJSONArray = JSONFactoryUtil.createJSONArray();

		Long productVersionId = ParamUtil.getLong(request, "productVersionId");
		String tickets = ParamUtil.getString(request, "tickets");
		Long projectVersionId = ParamUtil.getLong(request, "projectVersionId");

		String troubleshootingHint = PatcherTicketHintUtil.getPatcherTicketHintList(productVersionId, tickets, projectVersionId);

		troubleshootTicketJSONArray.put(troubleshootingHint);

		jsonObject.put("troubleshooting", troubleshootTicketJSONArray);

		respondWith(jsonObject);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.ACTION_PHASE, parameterNames = {"projectVersionId", "tickets"}, parameterTypes = {Long.class, String.class})
	public void hotfixExists() throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		JSONArray hotfixExistsJSONArray = JSONFactoryUtil.createJSONArray();

		long projectVersionId = ParamUtil.getLong(request, "projectVersionId");
		String tickets = ParamUtil.getString(request, "tickets");

		boolean hotfixExists = PatcherBuildUtil.hasEquivalentPatcherBuild(projectVersionId, tickets);

		hotfixExistsJSONArray.put(hotfixExists);

		jsonObject.put("hotfixExists", hotfixExistsJSONArray);

		respondWith(jsonObject);
	}

	public void index() throws Exception {
		AlloySearchResult alloySearchResult = null;

		Sort sort = new Sort();

		String keywords = ParamUtil.getString(request, "keywords");

		String patcherBuildName = ParamUtil.getString(request, "patcherBuildName");

		if ((!PatcherUtil.isPatcherTickets(keywords) || PatcherUtil.isPatcherProjectVersionName(keywords)) && !PatcherUtil.isPatcherTickets(patcherBuildName)) {
			sort = new Sort("statusDate", Sort.LONG_TYPE, true);
		}

		boolean advanceSearch = ParamUtil.getBoolean(request, "advancedSearch", false);

		if (!advanceSearch) {
			Map<String, Serializable> attributes = new HashMap<String, Serializable>();

			attributes.put("buildsIndexSearch", true);

			alloySearchResult = search(attributes, PatcherUtil.prepareKeywords(keywords), sort);
		}
		else {
			alloySearchResult = search(PatcherUtil.prepareKeywords(keywords), sort);
		}

		if (isRespondingTo()) {
			respondWith(alloySearchResult);

			return;
		}

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		renderRequest.setAttribute("displayTerms", new DisplayTerms(request));

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));
	}

	public void readyForRelease() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateRelease(patcherBuild);

		patcherBuild.setStatus(WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE);

		updateModelIgnoreRequest(patcherBuild);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void releaseManually() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateRelease(patcherBuild);

		patcherBuild.setStatus(WorkflowConstants.STATUS_BUILD_RELEASED);

		updateModelIgnoreRequest(patcherBuild);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void releaseToHelpCenter() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateRelease(patcherBuild);

		PatcherBuildUtil.releasePatcherBuild(patcherBuild);

		patcherBuild.setStatus(WorkflowConstants.STATUS_BUILD_RELEASED);

		updateModelIgnoreRequest(patcherBuild);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void smokeTest() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateSmokeTest(patcherBuild);

		JenkinsUtil.sendTestJenkinsRequest(this, user, patcherBuild);

		patcherBuild.setQaStatus(WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY);

		updateModelIgnoreRequest(patcherBuild);

		PatcherUtil.pollIndexState(this, PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(), "status", WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void test() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateTest(patcherBuild);

		JenkinsUtil.sendTestJenkinsRequest(this, user, patcherBuild);

		patcherBuild.setQaStatus(WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED);

		updateModelIgnoreRequest(patcherBuild);

		PatcherUtil.pollIndexState(this, PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(), "status", WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void update() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateUpdate(patcherBuild);

		if (_changedMetaDataFields(patcherBuild)) {
			updateMetaDataFields(patcherBuild);

			return;
		}

		patcherBuild = _versionPatcherBuild(patcherBuild);

		_savePatcherBuild(user, patcherBuild);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		_redirectOrClose();
	}

	public void updateCommentsField() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateUpdateCommentsField(patcherBuild);

		String comments = ParamUtil.getString(request, "comments");

		updateModelIgnoreRequest(patcherBuild, "comments", comments);

		PatcherUtil.pollIndexState(this, PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(), "comments", comments);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		_redirectOrClose();
	}

	public void updateMetaDataFields(PatcherBuild patcherBuild) throws Exception {
		String supportTicket = ParamUtil.getString(request, "supportTicket");

		int type = ParamUtil.getInteger(request, "type");

		patcherBuild.setType(type);

		PatcherBuildUtil.workflowCompletedPatcherBuildQAStatus(this, user, patcherBuild);

		updateModelIgnoreRequest(patcherBuild, "supportTicket", supportTicket);

		PatcherBuildUtil.sendTestJenkinsRequest(this, user, patcherBuild);

		PatcherUtil.pollIndexState(this, PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(), "supportTicket", supportTicket, "type", type);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		_redirectOrClose();
	}

	public void updateQAFields() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateUpdateQAFields(patcherBuild);

		String qaComments = ParamUtil.getString(request, "qaComments");

		int qaStatus = ParamUtil.getInteger(request, "qaStatus");

		updateModelIgnoreRequest(patcherBuild, "qaComments", qaComments, "qaStatus", qaStatus);

		PatcherUtil.pollIndexState(this, PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId(), "qaStatus", qaStatus, "qaComments", qaComments);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		_redirectOrClose();
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"id"}, parameterTypes = {Long.class})
	public void view() throws Exception {
		PatcherBuild patcherBuild = _fetchPatcherBuild();

		_validateView(patcherBuild);

		if (isRespondingTo()) {
			respondWith(indexer.getDocument(patcherBuild));

			return;
		}

		renderRequest.setAttribute("patcherBuild", patcherBuild);

		Map<String, Serializable> attributes = new HashMap<String, Serializable>();

		attributes.put("buildsViewSearch", true);
		attributes.put("key", patcherBuild.getKey());

		AlloySearchResult alloySearchResult = search(attributes, StringPool.BLANK, new Sort("keyVersion", true));

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		renderRequest.setAttribute("buildTypeLabel", PatcherBuildConstants.getTypeLabel(patcherBuild.getType()));

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherBuild.getPatcherFixId());

		if (patcherFix != null) {
			renderRequest.setAttribute("gitHash", patcherFix.getGitHash());

			renderRequest.setAttribute("gitHubURL", PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()));
		}

		renderRequest.setAttribute("jenkinsResults", JenkinsUtil.getJenkinsResults(patcherBuild));

		if (PortletPropsValues.OSB_PATCHER_SCANNING_ENABLED && !patcherBuild.getLatestSupportTicketBuild()) {
			renderRequest.setAttribute("latestPatcherBuild", PatcherBuildUtil.fetchPatcherBuildByLatestSupportTicketBuild(patcherBuild.getSupportTicket()));
		}
		else if (!patcherBuild.getLatestKeyBuild()) {
			renderRequest.setAttribute("latestPatcherBuild", PatcherBuildUtil.fetchPatcherBuildByLatestKeyBuild(patcherBuild.getKey()));
		}

		PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(patcherBuild.getPatcherAccountId());

		renderRequest.setAttribute("patcherBuildAccountEntryCode", patcherAccount.getAccountEntryCode());

		renderRequest.setAttribute("patcherBuildMergeOnly", PatcherBuildUtil.isMergeOnly(patcherBuild));
		renderRequest.setAttribute("patcherBuildStatus", translate(WorkflowConstants.getStatusLabel(patcherBuild.getStatus())));

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		List<PatcherProjectVersion> patcherProjectVersions = PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		renderRequest.setAttribute("patcherProjectVersions", patcherProjectVersions);

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"limit"}, parameterTypes = {Integer.class})
	public void viewMostRecent() throws Exception {
		_validateViewMostRecent();

		if (!isRespondingTo()) {
			return;
		}

		Map<String, Serializable> attributes = new HashMap<String, Serializable>();

		attributes.put("viewMostRecent", true);

		Sort sort = new Sort("statusDate", Sort.LONG_TYPE, true);
		int limit = ParamUtil.getInteger(request, "limit");

		AlloySearchResult alloySearchResult = search(attributes, null, new Sort[] {sort}, 0, limit);

		respondWith(alloySearchResult);
	}

	@Override
	protected Indexer buildIndexer() {
		return PatcherBuildIndexer.getInstance();
	}

	@Override
	protected MessageListener buildSchedulerMessageListener() {
		return PatcherBuildSchedulerMessageListener.getInstance(this);
	}

	@Override
	protected Trigger getSchedulerTrigger() {
		return new CronTrigger(getSchedulerJobName(), getMessageListenerGroupName(), "*/4 * * * * ? *");
	}

	private boolean _changedMetaDataFields(PatcherBuild patcherBuild) {
		int type = ParamUtil.getInteger(request, "type");

		if (type != patcherBuild.getType()) {
			return true;
		}

		return false;
	}

	private PatcherBuild _fetchPatcherBuild() throws Exception {
		long patcherBuildId = ParamUtil.getLong(request, "id");

		return PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);
	}

	private void _linkExistingHotfix(PatcherBuild patcherBuild) throws Exception {
		PatcherBuild existingPatcherBuild = PatcherBuildUtil.getLatestEquivalentPatcherBuild(patcherBuild.getPatcherProjectVersionId(), patcherBuild.getName());

		if (existingPatcherBuild != null) {
			patcherBuild.setFileName(existingPatcherBuild.getFileName());
			patcherBuild.setQaComments(translate("the-build-process-was-skipped-because-a-pre-existing-hotfix-was-used-original-build-id-x", existingPatcherBuild.getPatcherBuildId()));
		}
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

	private void _savePatcherBuild(User user, PatcherBuild patcherBuild) throws Exception {
		String accountEntryCode = StringUtil.toUpperCase(ParamUtil.getString(request, "patcherBuildAccountEntryCode"));

		String supportTicket = ParamUtil.getString(request, "supportTicket");

		patcherBuild.setSupportTicket(supportTicket);

		long newHotfixId = PatcherBuildUtil.generateHotfixId(accountEntryCode, supportTicket, patcherBuild.getPatcherProjectVersionId());

		patcherBuild.setHotfixId(newHotfixId);

		patcherBuild.setQaStatus(WorkflowConstants.STATUS_PENDING);

		boolean smokeTestOnly = ParamUtil.getBoolean(request, "smokeTestOnly", true);

		if (smokeTestOnly) {
			patcherBuild.setQaStatus(WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY);
		}

		Map<Long, List<Long>> patcherProjectVersionIdPatcherFixIdsMap = new HashMap<Long, List<Long>>();

		if (patcherBuild.getPatcherProductVersionId() == PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			List<Long> patcherFixIds = PatcherScanUtil.scanPatcherFixIdsByProjectVersionId(this, patcherBuild.getName(), patcherBuild.getPatcherProjectVersionId(), PatcherFixUtil.getPatcherFixesSelection(patcherBuild.getPatcherProjectVersionId(), false));

			patcherProjectVersionIdPatcherFixIdsMap.put(patcherBuild.getPatcherProjectVersionId(), patcherFixIds);
		}
		else {
			patcherProjectVersionIdPatcherFixIdsMap = PatcherScanUtil.scanPatcherFixIdsBy7xProjectVersions(this, patcherBuild);
		}

		boolean mergeOnly = ParamUtil.getBoolean(request, "mergeOnly");

		Class<?> patcherBuildUtilClass = PatcherBuildUtil.class;

		Method savePatcherBuildMethod = patcherBuildUtilClass.getDeclaredMethod("savePatcherBuild", new Class<?>[] {AlloyController.class, User.class, PatcherBuild.class, Map.class, boolean.class, String.class});

		ServiceBeanMethodInvocationFactoryUtil.proceed(null, PatcherBuildUtil.class, savePatcherBuildMethod, new Object[] {this, user, patcherBuild, patcherProjectVersionIdPatcherFixIdsMap, mergeOnly, accountEntryCode}, new String[] {"transactionAdvice"});

		PatcherBuildUtil.reindexRelatedModels(this, patcherBuild);

		PatcherUtil.pollIndexState(this, PatcherBuild.class.getName(), patcherBuild.getPatcherBuildId());
	}

	private void _validateAccount() throws Exception {
		String accountEntryCode = StringUtil.toUpperCase(ParamUtil.getString(request, "patcherBuildAccountEntryCode"));

		if (Validator.isNull(accountEntryCode)) {
			throw new AlloyException("the-account-code-is-invalid");
		}

		for (int i = 0; i < accountEntryCode.length(); i++) {
			if (!Validator.isAscii(accountEntryCode.charAt(i))) {
				throw new AlloyException("the-account-code-contains-non-ascii-characters");
			}
		}

		List<String> accountWhitelist = ListUtil.fromArray(PortletPropsValues.OSB_PATCHER_ACCOUNT_WHITELIST);

		if (!accountWhitelist.contains(StringUtil.toLowerCase(accountEntryCode))) {
			long accountEntryId = HelpCenterUtil.fetchAccountEntryId(accountEntryCode);

			if (accountEntryId <= 0) {
				_log.error("The account does not exist in OSB: " + accountEntryCode);

				throw new AlloyException("the-account-does-not-exist-in-osb", false);
			}
		}
	}

	private void _validateAdd() throws Exception {
		_validateAccount();
		_validateProductVersion();
		_validatePatcherProjectVersionId();

		PatcherBuild patcherBuild = new PatcherBuildImpl();

		patcherBuild.setKeyVersion(PatcherBuildConstants.KEY_VERSION_DEFAULT);

		String patcherBuildName = ParamUtil.getString(request, "patcherBuildName");

		patcherBuild.setName(patcherBuildName);

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		patcherBuild.setPatcherProductVersionId(patcherProductVersionId);

		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		patcherBuild.setPatcherProjectVersionId(patcherProjectVersionId);

		_validateKey(patcherBuild);
		_validateName(patcherBuild);

		_validateSupportTicket();
		_validateType();
	}

	private void _validateAddByName() throws Exception {
		_validatePatcherBuildTypeLabel();
		_validatePatcherProjectVersionName();
	}

	private void _validateBuild(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		_validateChildPatcherBuild(patcherBuild);

		_validatePatcherFixPack(patcherBuild);

		String message = JenkinsUtil.validateJenkinsSetup();

		if (Validator.isNotNull(message)) {
			throw new AlloyException(message);
		}

		if (PatcherBuildUtil.isMergeOnly(patcherBuild)) {
			throw new AlloyException("the-build-cannot-be-built-because-the-build-is-merge-only");
		}

		List<PatcherFix> patcherFixes = PatcherBuildRelUtil.getChildPatcherBuildsMainFixes(patcherBuild);

		for (PatcherFix patcherFix : patcherFixes) {
			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new AlloyException("the-build-cannot-be-built-because-its-fix-git-hash-is-not-set");
			}
		}
	}

	private void _validateChildPatcherBuild(PatcherBuild patcherBuild) throws Exception {
		if (PatcherBuildRelUtil.hasParentPatcherBuilds(patcherBuild)) {
			throw new AlloyException("the-action-cannot-be-performed-on-child-builds");
		}
	}

	private void _validateContent(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		_validateChildPatcherBuild(patcherBuild);
	}

	private void _validateDelete(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		if (!patcherBuild.isLatestKeyBuild()) {
			throw new AlloyException("the-build-cannot-be-deleted-because-the-current-build-is-not-the-latest");
		}

		AlloyServiceInvoker patcherFixPackAlloyServiceInvoker = new AlloyServiceInvoker(PatcherFixPack.class.getName());

		List<PatcherFixPack> patcherFixPacks = patcherFixPackAlloyServiceInvoker.executeDynamicQuery(new Object[] {"patcherBuildId", patcherBuild.getPatcherBuildId()});

		if (!patcherFixPacks.isEmpty()) {
			PatcherFixPack patcherFixPack = patcherFixPacks.get(0);

			throw new AlloyException(translate("the-build-cannot-be-deleted-because-fix-pack-x-depends-on-it", patcherFixPack.getName()));
		}
	}

	private void _validateEdit(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		_validateChildPatcherBuild(patcherBuild);
	}

	private void _validateEditCommentsField(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		_validateChildPatcherBuild(patcherBuild);
	}

	private void _validateEditQAFields(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		_validateChildPatcherBuild(patcherBuild);
	}

	private void _validateKey(PatcherBuild patcherBuild) throws Exception {
		if (PortletPropsValues.OSB_PATCHER_SCANNING_ENABLED) {
			return;
		}

		String accountEntryCode = StringUtil.toUpperCase(ParamUtil.getString(request, "patcherBuildAccountEntryCode"));

		String key = PatcherBuildUtil.generateKey(patcherBuild.getPatcherProjectVersionId(), patcherBuild.getName(), accountEntryCode);

		List<PatcherBuild> patcherBuilds = alloyServiceInvoker.executeDynamicQuery(new Object[] {"key", key, "keyVersion", patcherBuild.getKeyVersion()});

		if (!patcherBuilds.isEmpty()) {
			PatcherBuild oldPatcherBuild = patcherBuilds.get(0);

			if (oldPatcherBuild.getPatcherBuildId() != patcherBuild.getPatcherBuildId()) {
				throw new AlloyException("the-build-name-already-exists");
			}
		}
	}

	private void _validateName(PatcherBuild patcherBuild) throws Exception {
		if (Validator.isNull(patcherBuild.getName())) {
			throw new AlloyException("the-build-name-is-invalid");
		}

		String patcherBuildName = PatcherUtil.preparePatcherName(patcherBuild.getName());

		List<String> tokens = PatcherUtil.sortTokens(patcherBuildName);

		for (String token : tokens) {
			Matcher matcher = null;

			if (patcherBuild.getPatcherProductVersionId() == PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
				matcher = _patcherTicketName6xPattern.matcher(token);
			}
			else {
				matcher = _patcherTicketNameAllPattern.matcher(token);
			}

			if (matcher.find()) {
				continue;
			}

			matcher = _patcherFixPackNamePattern.matcher(token);

			if (!matcher.find()) {
				throw new AlloyException(translate("the-build-name-has-invalid-token-x", token));
			}
			else if (patcherBuild.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
				throw new AlloyException("the-build-name-cannot-contain-fix-packs");
			}

			PatcherFixPack patcherFixPack = PatcherFixPackUtil.fetchPatcherFixPack(token, patcherBuild.getPatcherProjectVersionId());

			if (patcherFixPack == null) {
				throw new AlloyException(translate("the-fix-pack-name-x-is-invalid", token));
			}

			if (patcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_RELEASED) {
				throw new AlloyException(translate("the-fix-pack-name-x-is-not-released", token));
			}
		}

		if (patcherBuild.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			List<String> cumulativePatcherProjectVersionFixedIssues = PatcherProjectVersionUtil.getCumulativePatcherProjectVersionFixedIssues(patcherBuild.getPatcherProjectVersionId());

			if (cumulativePatcherProjectVersionFixedIssues.containsAll(tokens)) {
				PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId());

				throw new AlloyException(translate("all-the-tickets-in-the-ticket-list-are-included-in-x", patcherProjectVersion.getName()));
			}
		}
	}

	private void _validatePatcherBuild(PatcherBuild patcherBuild) throws Exception {
		if (patcherBuild == null) {
			throw new AlloyException("the-build-does-not-exist");
		}
	}

	private void _validatePatcherBuildTypeLabel() throws Exception {
		String typeLabel = ParamUtil.getString(request, "typeLabel");

		if (Validator.isNull(typeLabel)) {
			throw new AlloyException("the-type-label-is-invalid");
		}
	}

	private void _validatePatcherFixPack(PatcherBuild patcherBuild) throws Exception {
		if (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) {
			return;
		}

		PatcherFixPack patcherFixPack = PatcherFixPackUtil.getPatcherFixPack(patcherBuild);

		if (patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_RELEASED) {
			throw new AlloyException("the-main-build-of-a-released-fix-pack-cannot-change");
		}
	}

	private void _validatePatcherProjectVersionId() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		if (patcherProjectVersionId == 0) {
			throw new AlloyException("the-build-project-version-is-invalid");
		}

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		if (patcherProductVersionId != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(patcherProjectVersionId);

			if (patcherProjectVersion.getPatcherProductVersionId() == PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
				throw new AlloyException("the-project-version-is-invalid-because-its-product-version-is-6x");
			}

			Pattern pattern = Pattern.compile(PatcherConstants.LIFERAY_PORTAL_REPOSITORY_REGEX);

			Matcher matcher = pattern.matcher(patcherProjectVersion.getRepositoryName());

			if (!matcher.find() || (patcherProjectVersion.getPatcherProductVersionId() == PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X))) {
				throw new AlloyException("the-project-version-is-invalid-because-its-repository-is-not-portal");
			}
		}
	}

	private void _validatePatcherProjectVersionName() throws Exception {
		String patcherProjectVersionName = ParamUtil.getString(request, "patcherProjectVersionName");

		if (Validator.isNull(patcherProjectVersionName)) {
			throw new AlloyException("the-project-version-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(patcherProjectVersionName);

		if (patcherProjectVersion == null) {
			throw new AlloyException("the-project-version-name-is-invalid");
		}
	}

	private void _validateProductVersion() throws Exception {
		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		if (Validator.isNull(PatcherProductVersionLocalServiceUtil.getPatcherProductVersion(patcherProductVersionId))) {
			throw new AlloyException("the-product-version-id-is-invalid");
		}
	}

	private void _validateRelease(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		_validateChildPatcherBuild(patcherBuild);

		if (!PatcherBuildUtil.isCompleteOrReady(patcherBuild)) {
			throw new AlloyException("the-build-cannot-be-released-before-completion");
		}

		String supportTicket = patcherBuild.getSupportTicket();

		if (!Validator.isNumber(supportTicket)) {
			throw new AlloyException("the-build-cannot-be-released-because-the-support-ticket-does-not-point-to-zendesk");
		}
	}

	private void _validateSmokeTest(PatcherBuild patcherBuild) throws Exception {
		if (patcherBuild.getStatus() != WorkflowConstants.STATUS_BUILD_COMPLETE) {
			throw new AlloyException("the-build-cannot-be-tested-because-its-status-is-not-complete");
		}

		if (Validator.isNull(patcherBuild.getFileName())) {
			throw new AlloyException("the-build-cannot-be-tested-because-its-filename-is-not-set");
		}
	}

	private void _validateSupportTicket() throws AlloyException {
		String supportTicket = ParamUtil.getString(request, "supportTicket");

		if (Validator.isNull(supportTicket)) {
			throw new AlloyException("the-support-ticket-is-invalid");
		}

		if (supportTicket.contains(StringPool.SPACE)) {
			throw new AlloyException("the-support-ticket-cannot-contain-spaces");
		}

		for (int i = 0; i < supportTicket.length(); i++) {
			if (!Validator.isAscii(supportTicket.charAt(i))) {
				throw new AlloyException("the-support-ticket-contains-non-ascii-characters");
			}
		}
	}

	private void _validateTest(PatcherBuild patcherBuild) throws Exception {
		if (patcherBuild.getStatus() != WorkflowConstants.STATUS_BUILD_COMPLETE) {
			throw new AlloyException("the-build-cannot-be-tested-because-its-status-is-not-complete");
		}

		if (Validator.isNull(patcherBuild.getFileName())) {
			throw new AlloyException("the-build-cannot-be-tested-because-its-filename-is-not-set");
		}
	}

	private void _validateType() throws Exception {
		int type = ParamUtil.getInteger(request, "type");

		if ((type != PatcherBuildConstants.TYPE_DEBUG) && (type != PatcherBuildConstants.TYPE_IGNORE) && (type != PatcherBuildConstants.TYPE_OFFICIAL)) {
			throw new AlloyException("the-type-is-invalid");
		}
	}

	private void _validateUpdate(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);

		if (!PortletPropsValues.OSB_PATCHER_SCANNING_ENABLED && !PatcherBuildUtil.isLatestPatcherBuild(patcherBuild)) {
			throw new AlloyException("the-build-cannot-be-versioned-because-the-current-build-is-not-the-latest");
		}

		_validateKey(patcherBuild);
		_validateName(patcherBuild);
		_validatePatcherFixPack(patcherBuild);

		_validateSupportTicket();
		_validateType();
	}

	private void _validateUpdateCommentsField(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);
	}

	private void _validateUpdateQAFields(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);
	}

	private void _validateView(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);
	}

	private void _validateViewBuilds(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);
	}

	private void _validateViewFixes(PatcherBuild patcherBuild) throws Exception {
		_validatePatcherBuild(patcherBuild);
	}

	private void _validateViewMostRecent() throws Exception {
		int limit = ParamUtil.getInteger(request, "limit");

		if (limit < 1) {
			throw new AlloyException("the-limit-is-invalid");
		}
	}

	private PatcherBuild _versionPatcherBuild(PatcherBuild patcherBuild) throws Exception {
		if (!PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild)) {
			return patcherBuild;
		}

		PatcherBuild newPatcherBuild = (PatcherBuild)patcherBuild.clone();

		newPatcherBuild.setFileName(StringPool.BLANK);
		newPatcherBuild.setHotfixId(0L);
		newPatcherBuild.setQaComments(StringPool.BLANK);

		if (newPatcherBuild.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
			newPatcherBuild.setOriginalName(newPatcherBuild.getName());

			List<String> patcherBuildTokens = PatcherUtil.getTokens(newPatcherBuild.getName());

			List<String> cumulativePatcherProjectVersionFixedIssues = PatcherProjectVersionUtil.getCumulativePatcherProjectVersionFixedIssues(newPatcherBuild.getPatcherProjectVersionId());

			patcherBuildTokens.removeAll(cumulativePatcherProjectVersionFixedIssues);

			newPatcherBuild.setName(StringUtil.merge(patcherBuildTokens));
		}

		newPatcherBuild.setNew(true);

		newPatcherBuild.setPatcherBuildId(increment());

		return newPatcherBuild;
	}

	private static Log _log = LogFactoryUtil.getLog("jsp.osb.patcher.controllers.BuildsController");

	private Pattern _patcherFixPackNamePattern = Pattern.compile(PatcherConstants.FIX_PACKS_REGEX);
	private Pattern _patcherTicketName6xPattern = Pattern.compile(PatcherConstants.TICKET_NAME_6X_REGEX);
	private Pattern _patcherTicketNameAllPattern = Pattern.compile(PatcherConstants.TICKET_NAME_ALL_REGEX);

}
%>