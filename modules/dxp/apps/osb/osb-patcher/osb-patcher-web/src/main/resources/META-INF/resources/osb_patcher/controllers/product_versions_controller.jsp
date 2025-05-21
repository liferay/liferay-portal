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
		setAlloyServiceInvokerClass(PatcherProductVersion.class);
		setPermissioned(true);
	}

	public void add() throws Exception {
		_validateAdd();

		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.createPatcherProductVersion(0);

		updateModel(patcherProductVersion);

		PatcherUtil.pollIndexState(this, PatcherProductVersion.class.getName(), patcherProductVersion.getPatcherProductVersionId());

		if (isRespondingTo()) {
			respondWith(patcherProductVersion);

			return;
		}

		addSuccessMessage();

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.ACTION_PHASE, parameterNames = {"fixDeliveryMethodLabel", "moduleFolderName", "name"}, parameterTypes = {String.class, String.class, String.class})
	public void addByName() throws Exception {
		_validateAddByName();

		String fixDeliveryMethodLabel = ParamUtil.getString(request, "fixDeliveryMethodLabel");

		int fixDeliveryMethodType = PatcherProductVersionConstants.getLabelType(fixDeliveryMethodLabel);

		setParameters("fixDeliveryMethod", fixDeliveryMethodType);

		add();
	}

	public void create() throws Exception {
		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.createPatcherProductVersion(0);

		renderRequest.setAttribute("patcherProductVersion", patcherProductVersion);

		String redirect = ParamUtil.getString(request, "redirect");

		renderRequest.setAttribute("redirect", redirect);
	}

	public void edit() throws Exception {
		PatcherProductVersion patcherProductVersion = _fetchPatcherProductVersion();

		_validateEdit(patcherProductVersion);

		renderRequest.setAttribute("patcherProductVersion", patcherProductVersion);
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
		PatcherProductVersion patcherProductVersion = _fetchPatcherProductVersion();

		_validateUpdate(patcherProductVersion);

		updateModel(patcherProductVersion);

		addSuccessMessage();

		PatcherUtil.pollIndexState(this, PatcherProductVersion.class.getName(), patcherProductVersion.getPatcherProductVersionId());

		if (isRespondingTo()) {
			respondWith(patcherProductVersion);

			return;
		}

		String redirect = ParamUtil.getString(request, "redirect");

		redirectTo(redirect);
	}

	@JSONWebServiceMethod(lifecycle = PortletRequest.RENDER_PHASE, parameterNames = {"name"}, parameterTypes = {String.class})
	public void viewByName() throws Exception {
		_validateViewByName();

		String name = ParamUtil.getString(request, "name");

		PatcherProductVersion patcherProductVersion = PatcherProductVersionUtil.fetchPatcherProductVersion(name);

		if (isRespondingTo()) {
			respondWith(patcherProductVersion);

			return;
		}
	}

	@Override
	protected Indexer buildIndexer() {
		return PatcherProductVersionIndexer.getInstance();
	}

	private PatcherProductVersion _fetchPatcherProductVersion() throws Exception {
		long patcherProductVersionId = ParamUtil.getLong(request, "id");

		return PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(patcherProductVersionId);
	}

	private void _validateAdd() throws Exception {
		_validateDuplicateName();
		_validateFixDeliveryMethod();
	}

	private void _validateAddByName() throws Exception {
		String fixDeliveryMethodLabel = ParamUtil.getString(request, "fixDeliveryMethodLabel");

		if (Validator.isNull(fixDeliveryMethodLabel)) {
			throw new AlloyException("fixDeliveryMethodLabel-is-not-valid");
		}

		int fixDeliveryMethodType = PatcherProductVersionConstants.getLabelType(fixDeliveryMethodLabel);

		if (fixDeliveryMethodType < 0) {
			throw new AlloyException("fixDeliveryMethodLabel-is-not-valid");
		}
	}

	private void _validateDuplicateName() throws Exception {
		_validateName();

		String name = ParamUtil.getString(request, "name");

		PatcherProductVersion patcherProductVersion = PatcherProductVersionUtil.fetchPatcherProductVersion(name);

		if (patcherProductVersion != null) {
			long patcherProductVersionId = ParamUtil.getLong(request, "id");

			if (patcherProductVersion.getPatcherProductVersionId() != patcherProductVersionId) {
				throw new AlloyException("the-product-version-name-already-exists");
			}
		}
	}

	private void _validateEdit(PatcherProductVersion patcherProductVersion) throws Exception {
		_validatePatcherProductVersion(patcherProductVersion);
	}

	private void _validateFixDeliveryMethod() throws Exception {
		int fixDeliveryMethod = ParamUtil.getInteger(request, "fixDeliveryMethod");
		String moduleFolderName = ParamUtil.getString(request, "moduleFolderName");

		if (Validator.isNull(fixDeliveryMethod)) {
			throw new AlloyException("the-fix-delivery-method-is-invalid");
		}
		else if ((fixDeliveryMethod == PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE) && Validator.isNull(moduleFolderName)) {
			throw new AlloyException("the-module-folder-name-is-required-for-marketplace-apps");
		}
	}

	private void _validateName() throws Exception {
		String name = ParamUtil.getString(request, "name");

		if (Validator.isNull(name)) {
			throw new AlloyException("the-product-version-name-is-invalid");
		}
	}

	private void _validatePatcherProductVersion(PatcherProductVersion patcherProductVersion) throws Exception {
		if (patcherProductVersion == null) {
			throw new AlloyException("the-product-version-does-not-exist");
		}
	}

	private void _validateUpdate(PatcherProductVersion patcherProductVersion) throws Exception {
		_validatePatcherProductVersion(patcherProductVersion);

		_validateDuplicateName();
		_validateFixDeliveryMethod();
	}

	private void _validateViewByName() throws Exception {
		_validateName();

		String name = ParamUtil.getString(request, "name");

		PatcherProductVersion patcherProductVersion = PatcherProductVersionUtil.fetchPatcherProductVersion(name);

		_validatePatcherProductVersion(patcherProductVersion);
	}

}
%>