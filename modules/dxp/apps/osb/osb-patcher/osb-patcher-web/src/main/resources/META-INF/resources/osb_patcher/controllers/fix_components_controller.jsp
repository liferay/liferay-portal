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
		setAlloyServiceInvokerClass(PatcherFixComponent.class);
		setPermissioned(true);
	}

	public void add() throws Exception {
		_validateAdd();

		PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.createPatcherFixComponent(0);

		String patcherFixComponentName = StringUtil.toLowerCase(ParamUtil.getString(request, "patcherFixComponentName"));

		patcherFixComponent.setName(patcherFixComponentName);

		updateModel(patcherFixComponent);

		addSuccessMessage();

		PatcherUtil.pollIndexState(this, PatcherFixComponent.class.getName(), patcherFixComponent.getPatcherFixComponentId());

		if (isRespondingTo()) {
			respondWith(patcherFixComponent);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void create() throws Exception {
		PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.createPatcherFixComponent(0);

		renderRequest.setAttribute("patcherFixComponent", patcherFixComponent);
	}

	public void delete() throws Exception {
		PatcherFixComponent patcherFixComponent = _fetchPatcherFixComponent();

		_validateDelete(patcherFixComponent);

		PatcherFixComponentLocalServiceUtil.deletePatcherFixComponent(patcherFixComponent);

		if (isRespondingTo()) {
			respondWith(patcherFixComponent);

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	public void edit() throws Exception {
		PatcherFixComponent patcherFixComponent = _fetchPatcherFixComponent();

		_validateEdit(patcherFixComponent);

		renderRequest.setAttribute("patcherFixComponent", patcherFixComponent);
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
	}

	public void update() throws Exception {
		PatcherFixComponent patcherFixComponent = _fetchPatcherFixComponent();

		_validateUpdate(patcherFixComponent);

		String patcherFixComponentName = StringUtil.toLowerCase(ParamUtil.getString(request, "patcherFixComponentName"));

		patcherFixComponent.setName(patcherFixComponentName);

		updateModel(patcherFixComponent);

		addSuccessMessage();

		PatcherUtil.pollIndexState(this, PatcherFixComponent.class.getName(), patcherFixComponent.getPatcherFixComponentId());

		if (isRespondingTo()) {
			respondWith(patcherFixComponent);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	@Override
	protected Indexer buildIndexer() {
		return PatcherFixComponentIndexer.getInstance();
	}

	private PatcherFixComponent _fetchPatcherFixComponent() throws Exception {
		long patcherFixComponentId = ParamUtil.getLong(request, "id");

		if (patcherFixComponentId > 0) {
			return PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixComponentId);
		}

		return PatcherFixComponentLocalServiceUtil.createPatcherFixComponent(0);
	}

	private void _validateAdd() throws Exception {
		_validateName();
	}

	private void _validateDelete(PatcherFixComponent patcherFixComponent) throws Exception {
		validateRequestPostMethod();

		_validatePatcherFixComponent(patcherFixComponent);

		AlloyServiceInvoker patcherFixPackAlloyServiceInvoker = new AlloyServiceInvoker(PatcherFixPack.class.getName());

		List<PatcherFixPack> patcherFixPacks = patcherFixPackAlloyServiceInvoker.executeDynamicQuery(new Object[] {"patcherFixComponentId", patcherFixComponent.getPatcherFixComponentId()});

		if (!patcherFixPacks.isEmpty()) {
			throw new Exception("the-component-cannot-be-deleted-because-it-has-an-associated-fix-pack");
		}
	}

	private void _validateEdit(PatcherFixComponent patcherFixComponent) throws Exception {
		_validatePatcherFixComponent(patcherFixComponent);
	}

	private void _validateFixPacks(PatcherFixComponent patcherFixComponent) throws Exception {
		AlloyServiceInvoker patcherFixPackAlloyServiceInvoker = new AlloyServiceInvoker(PatcherFixPack.class.getName());

		List<PatcherFixPack> patcherFixPacks = patcherFixPackAlloyServiceInvoker.executeDynamicQuery(new Object[] {"patcherFixComponentId", patcherFixComponent.getPatcherFixComponentId()});

		if (!patcherFixPacks.isEmpty()) {
			throw new Exception("the-component's-name-cannot-change-when-the-component-is-used-in-a-fix-pack");
		}

		List<PatcherFix> patcherFixes = PatcherFixLocalServiceUtil.getPatcherFixs(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherFix patcherFix : patcherFixes) {
			String dependencies = patcherFix.getDependencies();

			String[] componentNames = dependencies.split("(,|->)");

			for (String componentName : componentNames) {
				if (componentName.equals(patcherFixComponent.getName())) {
					throw new Exception("the-component's-name-cannot-be-changed-while-it-is-in-a-fix-dependency");
				}
			}
		}
	}

	private void _validateName() throws Exception {
		String patcherFixComponentName = ParamUtil.getString(request, "patcherFixComponentName");

		if (Validator.isNull(patcherFixComponentName)) {
			throw new Exception("the-name-is-invalid");
		}

		Pattern pattern = Pattern.compile(PatcherConstants.FIX_COMPONENT_REGEX);

		Matcher matcher = pattern.matcher(patcherFixComponentName);

		if (!matcher.find()) {
			throw new Exception("the-name-is-invalid");
		}

		List<PatcherFixComponent> patcherFixComponents = alloyServiceInvoker.executeDynamicQuery(new Object[] {"name", patcherFixComponentName});

		if (!patcherFixComponents.isEmpty()) {
			throw new Exception("the-component-name-already-exists");
		}
	}

	private void _validatePatcherFixComponent(PatcherFixComponent patcherFixComponent) throws Exception {
		if (patcherFixComponent == null) {
			throw new Exception("the-fix-component-does-not-exist");
		}
	}

	private void _validateUpdate(PatcherFixComponent patcherFixComponent) throws Exception {
		_validatePatcherFixComponent(patcherFixComponent);

		_validateName();

		String patcherFixComponentName = ParamUtil.getString(request, "patcherFixComponentName");

		if (StringUtil.equalsIgnoreCase(patcherFixComponentName, patcherFixComponent.getName())) {
			return;
		}

		_validateFixPacks(patcherFixComponent);
	}

}
%>