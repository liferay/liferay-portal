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
		setAlloyServiceInvokerClass(PatcherProjectVersion.class);
		setPermissioned(true);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"depth", "id"}, parameterTypes = {Integer.class, Long.class})
	public void accountBuilds() throws Exception {
		PatcherProjectVersion patcherProjectVersion = _fetchPatcherProjectVersion();

		_validateAccountBuilds(patcherProjectVersion);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		int depth = ParamUtil.getInteger(request, "depth");

		List<Long> patcherAccountIds = _getPatcherAccountIds(patcherProjectVersion.getPatcherProjectVersionId());

		for (long patcherAccountId : patcherAccountIds) {
			JSONArray latestHotfixesJSONArray = JSONFactoryUtil.createJSONArray();

			List<Long> latestHotfixes = _getLatestHotfixes(patcherProjectVersion.getPatcherProjectVersionId(), patcherAccountId, depth);

			for (long latestHotfix : latestHotfixes) {
				latestHotfixesJSONArray.put(latestHotfix);
			}

			PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(patcherAccountId);

			jsonObject.put(patcherAccount.getAccountEntryCode(), latestHotfixesJSONArray);
		}

		respondWith(jsonObject);
	}

	public void add() throws Exception {
		_validateAdd();

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.createPatcherProjectVersion(0);

		updateModel(patcherProjectVersion);

		PatcherUtil.pollIndexState(this, PatcherProjectVersion.class.getName(), patcherProjectVersion.getPatcherProjectVersionId());

		if (isRespondingTo()) {
			respondWith(patcherProjectVersion);

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.ACTION_PHASE, parameterNames = {"combinedBranch", "committish", "fixedIssues", "name", "productVersionLabel", "repositoryName", "rootPatcherProjectVersionName"}, parameterTypes = {Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class})
	public void addByName() throws Exception {
		_validateAddByName();

		String productVersionLabel = ParamUtil.getString(request, "productVersionLabel");

		PatcherProductVersion patcherProductVersion = PatcherProductVersionUtil.fetchPatcherProductVersion(productVersionLabel);

		setParameters("patcherProductVersionId", patcherProductVersion.getPatcherProductVersionId());

		String rootPatcherProjectVersionName = ParamUtil.getString(request, "rootPatcherProjectVersionName");

		if (Validator.isNotNull(rootPatcherProjectVersionName)) {
			PatcherProjectVersion rootPatcherProjectVersion = PatcherProjectVersionUtil.getPatcherProjectVersion(rootPatcherProjectVersionName);

			setParameters("rootPatcherProjectVersionId", rootPatcherProjectVersion.getPatcherProjectVersionId());
		}

		add();
	}

	public void create() throws Exception {
		JSONArray dxp70AndNewerPatcherProductVersionIdsJSONArray = _getPatcherProductVersions(PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30);

		dxp70AndNewerPatcherProductVersionIdsJSONArray.put(PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_QUARTERLY_RELEASES));

		renderRequest.setAttribute("dxp70AndNewerPatcherProductVersionIdsJSONArray", dxp70AndNewerPatcherProductVersionIdsJSONArray);

		List<Long> marketplaceReleasePatcherProductVersionIds = PatcherProductVersionUtil.getMarketplaceReleasePatcherProductVersionIds();

		renderRequest.setAttribute("marketplaceReleasePatcherProductVersionIds", JSONFactoryUtil.createJSONArray(JSONFactoryUtil.looseSerializeDeep(marketplaceReleasePatcherProductVersionIds)));

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.createPatcherProjectVersion(0);

		renderRequest.setAttribute("patcherProjectVersion", patcherProjectVersion);

		renderRequest.setAttribute("rootPatcherProjectVersions", PatcherProjectVersionUtil.getRootPatcherProjectVersions());

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	public void delete() throws Exception {
		PatcherProjectVersion patcherProjectVersion = _fetchPatcherProjectVersion();

		_validateDelete(patcherProjectVersion);

		PatcherProjectVersionLocalServiceUtil.deletePatcherProjectVersion(patcherProjectVersion);

		if (isRespondingTo()) {
			respondWith(patcherProjectVersion);

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void edit() throws Exception {
		PatcherProjectVersion patcherProjectVersion = _fetchPatcherProjectVersion();

		_validateEdit(patcherProjectVersion);

		JSONArray dxp70AndNewerPatcherProductVersionIdsJSONArray = _getPatcherProductVersions(PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30);

		dxp70AndNewerPatcherProductVersionIdsJSONArray.put(PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_QUARTERLY_RELEASES));

		renderRequest.setAttribute("dxp70AndNewerPatcherProductVersionIdsJSONArray", dxp70AndNewerPatcherProductVersionIdsJSONArray);

		List<Long> marketplaceReleasePatcherProductVersionIds = PatcherProductVersionUtil.getMarketplaceReleasePatcherProductVersionIds();

		renderRequest.setAttribute("marketplaceReleasePatcherProductVersionIds", JSONFactoryUtil.createJSONArray(JSONFactoryUtil.looseSerializeDeep(marketplaceReleasePatcherProductVersionIds)));

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());
		renderRequest.setAttribute("patcherProjectVersion", patcherProjectVersion);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherProjectVersionModelImpl.TABLE_NAME, "name", true);

		List<PatcherProjectVersion> patcherProjectVersions = alloyServiceInvoker.executeDynamicQuery(new Object[] {"rootPatcherProjectVersionId", 0L}, QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);

		renderRequest.setAttribute("patcherProjectVersions", patcherProjectVersions);
	}

	public void fixedIssues() throws Exception {
		PatcherProjectVersion patcherProjectVersion = _fetchPatcherProjectVersion();

		_validateViewFixedIssues(patcherProjectVersion);

		renderRequest.setAttribute("patcherProjectVersionId", patcherProjectVersion.getPatcherProjectVersionId());

		renderRequest.setAttribute("tickets", patcherProjectVersion.getFixedIssues());

		render("../view_tickets");
	}

	public void index() throws Exception {
		AlloySearchResult alloySearchResult = search(null, new Sort("name_sortable", false));

		if (isRespondingTo()) {
			respondWith(alloySearchResult);

			return;
		}

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());
	}

	public void update() throws Exception {
		PatcherProjectVersion patcherProjectVersion = _fetchPatcherProjectVersion();

		_validateUpdate(patcherProjectVersion);

		updateModel(patcherProjectVersion);

		addSuccessMessage();

		PatcherUtil.pollIndexState(this, PatcherProjectVersion.class.getName(), patcherProjectVersion.getPatcherProjectVersionId());

		if (isRespondingTo()) {
			respondWith(patcherProjectVersion);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.ACTION_PHASE, parameterNames = {"patcherProjectVersionId", "fixedIssues"}, parameterTypes = {Long.class, String.class})
	public void updateFixedIssues() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherProjectVersionId);

		_validateUpdateFixedIssues(patcherProjectVersion);

		String upcomingFixedIssues = ParamUtil.getString(request, "fixedIssues");

		String fixedIssues = patcherProjectVersion.getFixedIssues();

		if (!fixedIssues.isEmpty()) {
			fixedIssues += "," + upcomingFixedIssues;
		}
		else {
			fixedIssues = upcomingFixedIssues;
		}

		updateModel(patcherProjectVersion, "fixedIssues", fixedIssues);

		PatcherUtil.pollIndexState(this, PatcherProjectVersion.class.getName(), patcherProjectVersion.getPatcherProjectVersionId(), "fixedIssues", fixedIssues);

		if (isRespondingTo()) {
			respondWith(patcherProjectVersion);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void view() throws Exception {
		PatcherProjectVersion patcherProjectVersion = _fetchPatcherProjectVersion();

		_validateView(patcherProjectVersion);

		if (isRespondingTo()) {
			respondWith(patcherProjectVersion);

			return;
		}

		renderRequest.setAttribute("patcherProjectVersion", patcherProjectVersion);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherProjectVersionModelImpl.TABLE_NAME, "name", true);

		List<PatcherProjectVersion> patcherProjectVersions = alloyServiceInvoker.executeDynamicQuery(new Object[] {"rootPatcherProjectVersionId", 0L}, QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);

		renderRequest.setAttribute("patcherProjectVersions", patcherProjectVersions);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"patcherProductVersionId"}, parameterTypes = {Long.class})
	public void viewRootPatcherProjectVersions() throws Exception {
		_validateViewRootPatcherProjectVersions();

		if (!isRespondingTo()) {
			return;
		}

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherProjectVersionModelImpl.TABLE_NAME, "name", true);

		List<PatcherProjectVersion> patcherProjectVersions = alloyServiceInvoker.executeDynamicQuery(new Object[] {"rootPatcherProjectVersionId", 0L, "patcherProductVersionId", patcherProductVersionId}, QueryUtil.ALL_POS, QueryUtil.ALL_POS, obc);

		respondWith(patcherProjectVersions);
	}

	@Override
	protected Indexer buildIndexer() {
		return PatcherProjectVersionIndexer.getInstance();
	}

	private PatcherProjectVersion _fetchPatcherProjectVersion() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(request, "id");

		return PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherProjectVersionId);
	}

	private List<Long> _getLatestHotfixes(long patcherProjectVersionId, long patcherAccountId, int depth) throws Exception {
		AlloyServiceInvoker patcherBuildAlloyServiceInvoker = new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildDynamicQuery = patcherBuildAlloyServiceInvoker.buildDynamicQuery(new Object[] {"patcherProjectVersionId", patcherProjectVersionId, "patcherAccountId", patcherAccountId});

		Projection patcherBuildIdProjection = ProjectionFactoryUtil.property("patcherBuildId");

		patcherBuildDynamicQuery.setProjection(patcherBuildIdProjection);

		OrderByComparator obc = OrderByComparatorFactoryUtil.create(PatcherBuildModelImpl.TABLE_NAME, "createDate", false);

		return patcherBuildAlloyServiceInvoker.executeDynamicQuery(patcherBuildDynamicQuery, 0, depth, obc);
	}

	private List<Long> _getPatcherAccountIds(long patcherProjectVersionId) throws Exception {
		AlloyServiceInvoker patcherBuildAlloyServiceInvoker = new AlloyServiceInvoker(PatcherBuild.class.getName());

		DynamicQuery patcherBuildDynamicQuery = patcherBuildAlloyServiceInvoker.buildDynamicQuery(new Object[] {"patcherProjectVersionId", patcherProjectVersionId});

		Property patcherAccountIdProperty = PropertyFactoryUtil.forName("patcherAccountId");

		patcherBuildDynamicQuery.add(patcherAccountIdProperty.ne(0L));

		Projection patcherAccountIdProjection = ProjectionFactoryUtil.property("patcherAccountId");

		patcherBuildDynamicQuery.setProjection(ProjectionFactoryUtil.distinct(patcherAccountIdProjection));

		return patcherBuildAlloyServiceInvoker.executeDynamicQuery(patcherBuildDynamicQuery);
	}

	private JSONArray _getPatcherProductVersions(int fixDeliveryMethod) throws Exception {
		JSONArray patcherProductVersionIdsJSONArray = JSONFactoryUtil.createJSONArray();

		List<PatcherProductVersion> patcherProductVersions = PatcherProductVersionUtil.getPatcherProductVersions(fixDeliveryMethod);

		for (PatcherProductVersion patcherProductVersion : patcherProductVersions) {
			patcherProductVersionIdsJSONArray.put(patcherProductVersion.getPatcherProductVersionId());
		}

		return patcherProductVersionIdsJSONArray;
	}

	private void _validateAccountBuilds(PatcherProjectVersion patcherProjectVersion) throws Exception {
		if (!isRespondingTo("json")) {
			throw new Exception("this-method-only-responds-with-json");
		}

		_validatePatcherProjectVersion(patcherProjectVersion);

		int depth = ParamUtil.getInteger(request, "depth");

		if (depth < 1) {
			throw new Exception("the-depth-is-invalid");
		}
	}

	private void _validateAdd() throws Exception {
		_validateProductVersion();

		_validateName();

		_validateCombinedBranch();

		_validateCommittish();

		_validateHide();

		_validateRepositoryName();
	}

	private void _validateAddByName() throws Exception {
		_validateProductVersionLabel();

		_validateRootPatcherProjectVersionName();
	}

	private void _validateAssociatedPatcherBuilds(PatcherProjectVersion patcherProjectVersion) throws Exception {
		AlloyServiceInvoker patcherBuildAlloyServiceInvoker = new AlloyServiceInvoker(PatcherBuild.class.getName());

		long patcherBuildCount = patcherBuildAlloyServiceInvoker.executeDynamicQueryCount(new Object[] {"patcherProjectVersionId", patcherProjectVersion.getPatcherProjectVersionId()});

		if (patcherBuildCount > 0) {
			throw new Exception("the-project-version-cannot-be-deleted-because-it-has-associated-builds");
		}
	}

	private void _validateAssociatedPatcherFixes(PatcherProjectVersion patcherProjectVersion) throws Exception {
		AlloyServiceInvoker patcherFixAlloyServiceInvoker = new AlloyServiceInvoker(PatcherFix.class.getName());

		long patcherFixCount = patcherFixAlloyServiceInvoker.executeDynamicQueryCount(new Object[] {"patcherProjectVersionId", patcherProjectVersion.getPatcherProjectVersionId()});

		if (patcherFixCount > 0) {
			throw new Exception("the-project-version-cannot-be-deleted-because-it-has-associated-fixes");
		}
	}

	private void _validateCombinedBranch() throws Exception {
		PermissionChecker permissionChecker = PatcherPermission.getPermissionChecker(themeDisplay);

		Map<String, String[]> parameterMap = request.getParameterMap();

		if (!permissionChecker.isCompanyAdmin() && parameterMap.containsKey("combinedBranch")) {
			throw new Exception("only-admin-can-update-combined-branch-field");
		}
	}

	private void _validateCommittish() throws Exception {
		String committish = ParamUtil.getString(request, "committish");

		if (Validator.isNull(committish)) {
			throw new Exception("the-tag-name-is-invalid");
		}
	}

	private void _validateDelete(PatcherProjectVersion patcherProjectVersion) throws Exception {
		validateRequestPostMethod();

		_validatePatcherProjectVersion(patcherProjectVersion);

		_validateAssociatedPatcherBuilds(patcherProjectVersion);

		_validateAssociatedPatcherFixes(patcherProjectVersion);
	}

	private void _validateEdit(PatcherProjectVersion patcherProjectVersion) throws Exception {
		_validatePatcherProjectVersion(patcherProjectVersion);
	}

	private void _validateFixedIssues() throws Exception {
		String repositoryName = ParamUtil.getString(request, "repositoryName");

		if (!StringUtil.equalsIgnoreCase(repositoryName, PortletPropsValues.OSB_PATCHER_LIFERAY_PORTAL_REPOSITORY)) {
			return;
		}

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.getPatcherProductVersion(patcherProductVersionId);

		if (patcherProductVersion.getFixDeliveryMethod() != PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30) {
			return;
		}

		String committish = ParamUtil.getString(request, "committish");

		if (committish.contains("fix-pack-base-")) {
			return;
		}

		String fixedIssues = ParamUtil.getString(request, "fixedIssues");

		if (Validator.isNull(fixedIssues)) {
			throw new Exception("the-fixed-issues-are-required");
		}

		fixedIssues = PatcherUtil.preparePatcherName(fixedIssues);

		List<String> tokens = PatcherUtil.sortTokens(fixedIssues);

		for (String token : tokens) {
			Matcher matcher = _patcherTicketNameAllPattern.matcher(token);

			if (matcher.find()) {
				continue;
			}

			if (!matcher.find()) {
				throw new Exception(translate("the-fixed-issues-has-invalid-token-x", token));
			}
		}
	}

	private void _validateHide() throws Exception {
		PermissionChecker permissionChecker = PatcherPermission.getPermissionChecker(themeDisplay);

		Map<String, String[]> parameterMap = request.getParameterMap();

		if (!permissionChecker.isCompanyAdmin() && parameterMap.containsKey("hide")) {
			throw new Exception("only-admin-can-hide-a-project-version");
		}
	}

	private void _validateName() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(request, "id");
		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		if ((patcherProjectVersionId > 0) && (patcherProductVersionId != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X))) {
			Map<String, String[]> parameterMap = request.getParameterMap();

			if (parameterMap.containsKey("name")) {
				throw new Exception("the-project-version-name-cannot-be-updated");
			}

			return;
		}

		String name = ParamUtil.getString(request, "name");

		if (Validator.isNull(name)) {
			throw new Exception("the-project-version-name-is-invalid");
		}

		List<PatcherProjectVersion> patcherProjectVersions = alloyServiceInvoker.executeDynamicQuery(new Object[] {"name", name});

		if (!patcherProjectVersions.isEmpty()) {
			PatcherProjectVersion patcherProjectVersion = patcherProjectVersions.get(0);

			if (patcherProjectVersion.getPatcherProjectVersionId() != patcherProjectVersionId) {
				throw new Exception("the-project-version-name-already-exists");
			}
		}
	}

	private void _validatePatcherProjectVersion(PatcherProjectVersion patcherProjectVersion) throws Exception {
		if (patcherProjectVersion == null) {
			throw new Exception("the-project-version-does-not-exist");
		}
	}

	private void _validateProductVersion() throws Exception {
		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.getPatcherProductVersion(patcherProductVersionId);

		if (Validator.isNull(patcherProductVersion)) {
			throw new Exception("the-product-version-id-is-invalid");
		}
	}

	private void _validateProductVersionLabel() throws Exception {
		String productVersionLabel = ParamUtil.getString(request, "productVersionLabel");

		if (Validator.isNull(productVersionLabel)) {
			throw new Exception("the-product-version-label-is-invalid");
		}

		PatcherProductVersion patcherProductVersion = PatcherProductVersionUtil.fetchPatcherProductVersion(productVersionLabel);

		if (patcherProductVersion == null) {
			throw new Exception("the-product-version-label-is-invalid");
		}
	}

	private void _validateRepositoryName() throws Exception {
		String repositoryName = ParamUtil.getString(request, "repositoryName");

		if (Validator.isNull(repositoryName)) {
			throw new Exception("the-repository-name-is-invalid");
		}
	}

	private void _validateRootPatcherProjectVersionName() throws Exception {
		String rootPatcherProjectVersionName = ParamUtil.getString(request, "rootPatcherProjectVersionName");

		if (Validator.isNotNull(rootPatcherProjectVersionName)) {
			PatcherProjectVersion rootPatcherProjectVersion = PatcherProjectVersionUtil.fetchPatcherProjectVersionByName(rootPatcherProjectVersionName);

			if (rootPatcherProjectVersion == null) {
				throw new Exception("the-root-patcher-project-version-name-does-not-exist");
			}

			if (rootPatcherProjectVersion.getRootPatcherProjectVersionId() != 0) {
				throw new Exception("the-root-patcher-project-version-name-is-not-a-root-project-version");
			}
		}
	}

	private void _validateUpdate(PatcherProjectVersion patcherProjectVersion) throws Exception {
		_validatePatcherProjectVersion(patcherProjectVersion);

		_validateProductVersion();

		_validateName();

		_validateCombinedBranch();

		_validateCommittish();

		_validateHide();

		_validateRepositoryName();
	}

	private void _validateUpdateFixedIssues(PatcherProjectVersion patcherProjectVersion) throws Exception {
		_validatePatcherProjectVersion(patcherProjectVersion);

		String fixedIssues = ParamUtil.getString(request, "fixedIssues");

		if (Validator.isNull(fixedIssues)) {
			throw new Exception("the-fixed-issues-are-required");
		}

		fixedIssues = PatcherUtil.preparePatcherName(fixedIssues);

		List<String> tokens = PatcherUtil.sortTokens(fixedIssues);

		tokens.remove(PatcherConstants.INVALID_TICKET_KEY);

		for (String token : tokens) {
			Matcher matcher = _patcherTicketNameAllPattern.matcher(token);

			if (matcher.find()) {
				continue;
			}

			if (!matcher.find()) {
				throw new Exception(translate("the-fixed-issues-has-invalid-token-x", token));
			}
		}
	}

	private void _validateView(PatcherProjectVersion patcherProjectVersion) throws Exception {
		_validatePatcherProjectVersion(patcherProjectVersion);
	}

	private void _validateViewFixedIssues(PatcherProjectVersion patcherProjectVersion) throws Exception {
		_validatePatcherProjectVersion(patcherProjectVersion);
	}

	private void _validateViewRootPatcherProjectVersions() throws Exception {
		_validateProductVersion();
	}

	private Pattern _patcherTicketNameAllPattern = Pattern.compile(PatcherConstants.TICKET_NAME_ALL_REGEX);

}
%>