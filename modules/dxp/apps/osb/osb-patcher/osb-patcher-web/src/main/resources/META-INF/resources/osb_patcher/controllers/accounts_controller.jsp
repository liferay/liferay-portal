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
		setAlloyServiceInvokerClass(PatcherAccount.class);
		setPermissioned(true);
	}

	public void index() throws Exception {
		String accountEntryCode = ParamUtil.getString(request, "accountEntryCode");

		renderRequest.setAttribute("accountEntryCode", accountEntryCode);

		String keywords = ParamUtil.getString(request, "keywords");

		Sort[] sorts = {
			new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true)
		};

		if (isRespondingTo()) {
			int limit = ParamUtil.getInteger(request, "limit");

			AlloySearchResult alloySearchResult = search(indexer, alloyServiceInvoker, request, portletRequest, null, keywords, sorts, 0, limit);

			respondWith(alloySearchResult);

			return;
		}

		AlloySearchResult alloySearchResult = search(keywords, sorts);

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		renderRequest.setAttribute("displayTerms", new DisplayTerms(request));

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionLocalServiceUtil.getPatcherProductVersions(QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"limit", "patcherBuildAccountEntryCode"}, parameterTypes = {Integer.class, String.class})
	public void view() throws Exception {
		PatcherAccount patcherAccount = _fetchPatcherAccount();

		_validateView(patcherAccount);

		PatcherBuildIndexer patcherBuildIndexer = new PatcherBuildIndexer();

		AlloyServiceInvoker patcherBuildAlloyServiceInvoker = new AlloyServiceInvoker(PatcherBuild.class.getName());

		Sort sort = new Sort();

		String keywords = ParamUtil.getString(request, "keywords");

		String patcherBuildName = ParamUtil.getString(request, "patcherBuildName");

		if ((!PatcherUtil.isPatcherTickets(keywords) || PatcherUtil.isPatcherProjectVersionName(keywords)) && !PatcherUtil.isPatcherTickets(patcherBuildName)) {
			sort = new Sort("statusDate", Sort.LONG_TYPE, true);
		}

		if (isRespondingTo()) {
			int limit = ParamUtil.getInteger(request, "limit");

			AlloySearchResult alloySearchResult = search(patcherBuildIndexer, patcherBuildAlloyServiceInvoker, request, portletRequest, null, keywords, new Sort[] {sort}, 0, limit);

			respondWith(alloySearchResult);

			return;
		}

		AlloySearchResult alloySearchResult = search(patcherBuildIndexer, patcherBuildAlloyServiceInvoker, request, portletRequest, null, keywords, new Sort[] {sort});

		renderRequest.setAttribute("alloySearchResult", alloySearchResult);

		renderRequest.setAttribute("displayTerms", new DisplayTerms(request));

		String patcherBuildAccountEntryCode = ParamUtil.getString(request, "patcherBuildAccountEntryCode");

		renderRequest.setAttribute("patcherBuildAccountEntryCode", patcherBuildAccountEntryCode);

		long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

		renderRequest.setAttribute("patcherProductVersionId", patcherProductVersionId);

		renderRequest.setAttribute("patcherProductVersions", PatcherProductVersionUtil.getPatcherProductVersions());

		renderRequest.setAttribute("patcherProjectVersions", PatcherProjectVersionUtil.getPatcherProjectVersions("name", true));
	}

	@Override
	protected Indexer buildIndexer() {
		return PatcherAccountIndexer.getInstance();
	}

	private PatcherAccount _fetchPatcherAccount() throws Exception {
		String patcherBuildAccountEntryCode = ParamUtil.getString(request, "patcherBuildAccountEntryCode");

		List<PatcherAccount> patcherAccounts = alloyServiceInvoker.executeDynamicQuery(new Object[] {"accountEntryCode", patcherBuildAccountEntryCode});

		if (patcherAccounts.isEmpty()) {
			return null;
		}

		return patcherAccounts.get(0);
	}

	private void _validateLimit() throws Exception {
		int limit = ParamUtil.getInteger(request, "limit");

		if (limit < 1) {
			throw new Exception("the-limit-is-invalid");
		}
	}

	private void _validatePatcherAccount(PatcherAccount patcherAccount) throws Exception {
		if (patcherAccount == null) {
			throw new Exception("the-account-does-not-exist");
		}
	}

	private void _validatePatcherBuildAccountEntryCode() throws Exception {
		String patcherBuildAccountEntryCode = ParamUtil.getString(request, "patcherBuildAccountEntryCode");

		if (Validator.isNull(patcherBuildAccountEntryCode)) {
			throw new Exception("the-account-code-is-invalid");
		}
	}

	private void _validateView(PatcherAccount patcherAccount) throws Exception {
		_validatePatcherAccount(patcherAccount);

		if (isRespondingTo()) {
			_validateLimit();

			_validatePatcherBuildAccountEntryCode();
		}
	}

}
%>