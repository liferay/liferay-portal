/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.RequiredWorkflowDefinitionException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.exception.IncompleteWorkflowInstancesException;
import com.liferay.portal.workflow.kaleo.designer.web.constants.KaleoDesignerPortletKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerActionKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.permission.KaleoDefinitionVersionPermission;
import com.liferay.portal.workflow.kaleo.designer.web.internal.permission.KaleoDesignerPermission;
import com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.display.context.helper.KaleoDesignerRequestHelper;
import com.liferay.portal.workflow.kaleo.designer.web.internal.search.KaleoDefinitionVersionSearch;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.runtime.action.ActionExecutorManager;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.util.comparator.KaleoDefinitionVersionModifiedDateComparator;
import com.liferay.portal.workflow.kaleo.util.comparator.KaleoDefinitionVersionTitleComparator;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Rafael Praxedes
 */
public class KaleoDesignerDisplayContext {

	public KaleoDesignerDisplayContext(
		ActionExecutorManager actionExecutorManager,
		RenderRequest renderRequest,
		KaleoDefinitionVersionLocalService kaleoDefinitionVersionLocalService,
		PortletResourcePermission portletResourcePermission,
		ResourceBundleLoader resourceBundleLoader,
		ScriptManagementConfigurationHelper scriptManagementConfigurationHelper,
		UserLocalService userLocalService) {

		_actionExecutorManager = actionExecutorManager;
		_kaleoDefinitionVersionLocalService =
			kaleoDefinitionVersionLocalService;
		_portletResourcePermission = portletResourcePermission;
		_resourceBundleLoader = resourceBundleLoader;
		_scriptManagementConfigurationHelper =
			scriptManagementConfigurationHelper;
		_userLocalService = userLocalService;

		_kaleoDesignerRequestHelper = new KaleoDesignerRequestHelper(
			renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public boolean canPublishWorkflowDefinition() {
		return _portletResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			_themeDisplay.getCompanyGroupId(),
			KaleoDesignerActionKeys.ADD_NEW_WORKFLOW);
	}

	public String getDuplicateTitle(KaleoDefinition kaleoDefinition) {
		if (kaleoDefinition == null) {
			return StringPool.BLANK;
		}

		String defaultLanguageId = LocalizationUtil.getDefaultLanguageId(
			kaleoDefinition.getTitle());

		return LocalizationUtil.updateLocalization(
			kaleoDefinition.getTitle(), "title",
			LanguageUtil.format(
				_getResourceBundle(), "copy-of-x",
				kaleoDefinition.getTitle(defaultLanguageId)),
			defaultLanguageId);
	}

	public JSONArray getFunctionActionExecutorsJSONArray() throws Exception {
		return JSONUtil.putAll(
			_actionExecutorManager.getFunctionActionExecutorKeys());
	}

	public KaleoDefinition getKaleoDefinition(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		try {
			if (kaleoDefinitionVersion != null) {
				return kaleoDefinitionVersion.getKaleoDefinition();
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	public OrderByComparator<KaleoDefinitionVersion>
		getKaleoDefinitionVersionOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<KaleoDefinitionVersion> orderByComparator = null;

		if (orderByCol.equals("title")) {
			orderByComparator = new KaleoDefinitionVersionTitleComparator(
				orderByAsc);
		}
		else if (orderByCol.equals("last-modified")) {
			orderByComparator =
				KaleoDefinitionVersionModifiedDateComparator.getInstance(
					orderByAsc);
		}

		return orderByComparator;
	}

	public List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		return _kaleoDefinitionVersionLocalService.getKaleoDefinitionVersions(
			kaleoDefinitionVersion.getCompanyId(),
			kaleoDefinitionVersion.getName(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			KaleoDefinitionVersionModifiedDateComparator.getInstance(false));
	}

	public KaleoDefinitionVersionSearch getKaleoDefinitionVersionSearch(
		int status) {

		KaleoDefinitionVersionSearch kaleoDefinitionVersionSearch =
			new KaleoDefinitionVersionSearch(
				_kaleoDesignerRequestHelper.getLiferayPortletRequest(),
				getPortletURL());

		kaleoDefinitionVersionSearch.setOrderByCol(getOrderByCol());
		kaleoDefinitionVersionSearch.setOrderByComparator(
			getKaleoDefinitionVersionOrderByComparator(
				getOrderByCol(), getOrderByType()));
		kaleoDefinitionVersionSearch.setOrderByType(getOrderByType());

		kaleoDefinitionVersionSearch.setResultsAndTotal(
			() ->
				_kaleoDefinitionVersionLocalService.
					getLatestKaleoDefinitionVersions(
						_kaleoDesignerRequestHelper.getCompanyId(),
						_getKeywords(), status,
						_kaleoDesignerRequestHelper.getLocale(),
						kaleoDefinitionVersionSearch.getStart(),
						kaleoDefinitionVersionSearch.getEnd(),
						kaleoDefinitionVersionSearch.getOrderByComparator()),
			_kaleoDefinitionVersionLocalService.
				getLatestKaleoDefinitionVersionsCount(
					_kaleoDesignerRequestHelper.getCompanyId(), _getKeywords(),
					status));

		return kaleoDefinitionVersionSearch;
	}

	public JSONArray getKaleoDefinitionVersionsJSONArray(
			KaleoDefinitionVersion currentKaleoDefinitionVersion)
		throws Exception {

		return JSONUtil.toJSONArray(
			getKaleoDefinitionVersions(currentKaleoDefinitionVersion),
			kaleoDefinitionVersion -> JSONUtil.put(
				"creatorName",
				() -> {
					User user = _userLocalService.fetchUser(
						kaleoDefinitionVersion.getUserId());

					if (user != null) {
						return user.getFullName();
					}

					user = _userLocalService.fetchUserByScreenName(
						kaleoDefinitionVersion.getCompanyId(),
						UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT);

					if (user != null) {
						return user.getFullName();
					}

					return StringPool.BLANK;
				}
			).put(
				"dateCreated",
				() -> {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

					return simpleDateFormat.format(
						kaleoDefinitionVersion.getCreateDate());
				}
			).put(
				"version", kaleoDefinitionVersion.getVersion()
			));
	}

	public String getManageSubmissionsLink() {
		return _buildErrorLink(
			"configure-submissions", _getWorkflowInstancesPortletURL());
	}

	public Object[] getMessageArguments(
			IncompleteWorkflowInstancesException
				incompleteWorkflowInstancesException)
		throws PortletException {

		return new Object[] {
			String.valueOf(
				incompleteWorkflowInstancesException.
					getWorkflowInstancesCount()),
			getManageSubmissionsLink()
		};
	}

	public Object[] getMessageArguments(
			RequiredWorkflowDefinitionException
				requiredWorkflowDefinitionException)
		throws PortletException {

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			requiredWorkflowDefinitionException.getWorkflowDefinitionLinks();

		if (workflowDefinitionLinks.isEmpty()) {
			return new Object[0];
		}
		else if (workflowDefinitionLinks.size() == 1) {
			WorkflowDefinitionLink workflowDefinitionLink =
				workflowDefinitionLinks.get(0);

			return new Object[] {
				_getLocalizedAssetName(workflowDefinitionLink.getClassName())
			};
		}
		else if (workflowDefinitionLinks.size() == 2) {
			WorkflowDefinitionLink workflowDefinitionLink1 =
				workflowDefinitionLinks.get(0);
			WorkflowDefinitionLink workflowDefinitionLink2 =
				workflowDefinitionLinks.get(1);

			return new Object[] {
				_getLocalizedAssetName(workflowDefinitionLink1.getClassName()),
				_getLocalizedAssetName(workflowDefinitionLink2.getClassName()),
				_getConfigureAssignementLink()
			};
		}

		WorkflowDefinitionLink workflowDefinitionLink1 =
			workflowDefinitionLinks.get(0);
		WorkflowDefinitionLink workflowDefinitionLink2 =
			workflowDefinitionLinks.get(1);

		return new Object[] {
			_getLocalizedAssetName(workflowDefinitionLink1.getClassName()),
			_getLocalizedAssetName(workflowDefinitionLink2.getClassName()),
			workflowDefinitionLinks.size() - 2, _getConfigureAssignementLink()
		};
	}

	public String getMessageKey(
		IncompleteWorkflowInstancesException
			incompleteWorkflowInstancesException) {

		if (incompleteWorkflowInstancesException.getWorkflowInstancesCount() ==
				1) {

			return "there-is-x-unresolved-workflow-submission-x";
		}

		return "there-are-x-unresolved-workflow-submissions-x";
	}

	public String getMessageKey(
		RequiredWorkflowDefinitionException
			requiredWorkflowDefinitionException) {

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			requiredWorkflowDefinitionException.getWorkflowDefinitionLinks();

		if (workflowDefinitionLinks.isEmpty()) {
			return StringPool.BLANK;
		}
		else if (workflowDefinitionLinks.size() == 1) {
			return "workflow-is-in-use.-remove-its-assignment-to-x";
		}
		else if (workflowDefinitionLinks.size() == 2) {
			return "workflow-is-in-use.-remove-its-assignment-to-x-and-x";
		}

		return "workflow-is-in-use.-remove-its-assignment-to-x-x-and-x-more";
	}

	public Date getModifiedDate(KaleoDefinitionVersion kaleoDefinitionVersion) {
		try {
			KaleoDefinition kaleoDefinition =
				kaleoDefinitionVersion.getKaleoDefinition();

			return kaleoDefinition.getModifiedDate();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return kaleoDefinitionVersion.getModifiedDate();
	}

	public String getOrderByCol() {
		return ParamUtil.getString(
			_kaleoDesignerRequestHelper.getRequest(), "orderByCol",
			"last-modified");
	}

	public String getOrderByType() {
		return ParamUtil.getString(
			_kaleoDesignerRequestHelper.getRequest(), "orderByType", "asc");
	}

	public LiferayPortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_kaleoDesignerRequestHelper.getLiferayPortletResponse();

		LiferayPortletURL portletURL = liferayPortletResponse.createRenderURL(
			KaleoDesignerPortletKeys.CONTROL_PANEL_WORKFLOW);

		portletURL.setParameter("mvcPath", "/view.jsp");
		portletURL.setParameter("navigation", _getDefinitionsNavigation());

		String delta = ParamUtil.getString(
			_kaleoDesignerRequestHelper.getRequest(), "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String keywords = _getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public String getPublishKaleoDefinitionVersionButtonLabel(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		KaleoDefinition kaleoDefinition = getKaleoDefinition(
			kaleoDefinitionVersion);

		if ((kaleoDefinition != null) && kaleoDefinition.isActive()) {
			return "update";
		}

		return "publish";
	}

	public String getScriptManagementConfigurationPortletURL()
		throws PortalException {

		return _scriptManagementConfigurationHelper.
			getScriptManagementConfigurationPortletURL();
	}

	public JSONArray getStatusesJSONArray() {
		return JSONUtil.putAll(
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_APPROVED)
			).put(
				"value", WorkflowConstants.STATUS_APPROVED
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_DENIED)
			).put(
				"value", WorkflowConstants.STATUS_DENIED
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_DRAFT)
			).put(
				"value", WorkflowConstants.STATUS_DRAFT
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_EXPIRED)
			).put(
				"value", WorkflowConstants.STATUS_EXPIRED
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_IN_TRASH)
			).put(
				"value", WorkflowConstants.STATUS_IN_TRASH
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_INACTIVE)
			).put(
				"value", WorkflowConstants.STATUS_INACTIVE
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_INCOMPLETE)
			).put(
				"value", WorkflowConstants.STATUS_INCOMPLETE
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_PENDING)
			).put(
				"value", WorkflowConstants.STATUS_PENDING
			),
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_kaleoDesignerRequestHelper.getRequest(),
					WorkflowConstants.LABEL_SCHEDULED)
			).put(
				"value", WorkflowConstants.STATUS_SCHEDULED
			));
	}

	public String getTimeZoneId() {
		User user = _themeDisplay.getUser();

		if ((user == null) || user.isGuestUser() ||
			Validator.isNull(user.getFullName())) {

			return null;
		}

		return user.getTimeZoneId();
	}

	public String getTitle(KaleoDefinitionVersion kaleoDefinitionVersion) {
		if (kaleoDefinitionVersion == null) {
			return _getLanguage("new-workflow");
		}

		if (Validator.isNull(kaleoDefinitionVersion.getTitle())) {
			return _getLanguage("untitled-workflow");
		}

		ThemeDisplay themeDisplay =
			_kaleoDesignerRequestHelper.getThemeDisplay();

		return HtmlUtil.escape(
			kaleoDefinitionVersion.getTitle(themeDisplay.getLanguageId()));
	}

	public String getUserName(KaleoDefinitionVersion kaleoDefinitionVersion) {
		User user = _userLocalService.fetchUser(
			kaleoDefinitionVersion.getUserId());

		if ((user == null) || user.isGuestUser() ||
			Validator.isNull(user.getFullName())) {

			return null;
		}

		return user.getFullName();
	}

	public String getUserNameOrBlank(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		String userName = getUserName(kaleoDefinitionVersion);

		if (userName == null) {
			userName = StringPool.BLANK;
		}

		return userName;
	}

	public boolean isAllowScriptContentToBeExecutedOrIncluded() {
		return _scriptManagementConfigurationHelper.
			isAllowScriptContentToBeExecutedOrIncluded();
	}

	public boolean isDefinitionInputDisabled(
		boolean previewBeforeRestore,
		KaleoDefinitionVersion kaleoDefinitionVersion,
		PermissionChecker permissionChecker) {

		if (previewBeforeRestore) {
			return true;
		}

		if ((kaleoDefinitionVersion == null) &&
			KaleoDesignerPermission.contains(
				permissionChecker, _themeDisplay.getCompanyGroupId(),
				KaleoDesignerActionKeys.ADD_NEW_WORKFLOW)) {

			return false;
		}

		try {
			if ((kaleoDefinitionVersion != null) &&
				KaleoDefinitionVersionPermission.contains(
					permissionChecker, kaleoDefinitionVersion,
					ActionKeys.UPDATE)) {

				return false;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return true;
	}

	public boolean isPublishKaleoDefinitionVersionButtonVisible(
		PermissionChecker permissionChecker,
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		if (!canPublishWorkflowDefinition()) {
			return false;
		}

		if (kaleoDefinitionVersion != null) {
			try {
				return KaleoDefinitionVersionPermission.contains(
					permissionChecker, kaleoDefinitionVersion,
					ActionKeys.UPDATE);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return KaleoDesignerPermission.contains(
			permissionChecker, _themeDisplay.getCompanyGroupId(),
			KaleoDesignerActionKeys.ADD_NEW_WORKFLOW);
	}

	public boolean isSaveKaleoDefinitionVersionButtonVisible(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		PermissionChecker permissionChecker =
			_kaleoDesignerRequestHelper.getPermissionChecker();

		if (kaleoDefinitionVersion != null) {
			KaleoDefinition kaleoDefinition = getKaleoDefinition(
				kaleoDefinitionVersion);

			if ((kaleoDefinition != null) && !kaleoDefinition.isActive()) {
				try {
					return KaleoDefinitionVersionPermission.contains(
						permissionChecker, kaleoDefinitionVersion,
						ActionKeys.UPDATE);
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}
			}

			return false;
		}

		return KaleoDesignerPermission.contains(
			permissionChecker, _themeDisplay.getCompanyGroupId(),
			KaleoDesignerActionKeys.ADD_NEW_WORKFLOW);
	}

	public void setKaleoDesignerRequestHelper(RenderRequest renderRequest) {
		_kaleoDesignerRequestHelper = new KaleoDesignerRequestHelper(
			renderRequest);
	}

	private String _buildErrorLink(String messageKey, PortletURL portletURL) {
		return StringUtil.replace(
			_HTML, new String[] {"[$RENDER_URL$]", "[$MESSAGE$]"},
			new String[] {
				portletURL.toString(),
				LanguageUtil.get(_getResourceBundle(), messageKey)
			});
	}

	private String _getConfigureAssignementLink() {
		return _buildErrorLink(
			"configure-assignments", _getWorkflowDefinitionLinkPortletURL());
	}

	private String _getDefinitionsNavigation() {
		return ParamUtil.getString(
			_kaleoDesignerRequestHelper.getRequest(), "navigation", "all");
	}

	private String _getKeywords() {
		return ParamUtil.getString(
			_kaleoDesignerRequestHelper.getRequest(), "keywords");
	}

	private String _getLanguage(String key) {
		return LanguageUtil.get(_getResourceBundle(), key);
	}

	private String _getLocalizedAssetName(String className) {
		return ResourceActionsUtil.getModelResource(
			_kaleoDesignerRequestHelper.getLocale(), className);
	}

	private ResourceBundle _getResourceBundle() {
		return _resourceBundleLoader.loadResourceBundle(
			_kaleoDesignerRequestHelper.getLocale());
	}

	private PortletURL _getWorkflowDefinitionLinkPortletURL() {
		return PortletURLBuilder.createLiferayPortletURL(
			_kaleoDesignerRequestHelper.getLiferayPortletResponse(),
			KaleoDesignerPortletKeys.CONTROL_PANEL_WORKFLOW,
			PortletRequest.RENDER_PHASE
		).setMVCPath(
			"/view.jsp"
		).setParameter(
			"tab", WorkflowWebKeys.WORKFLOW_TAB_DEFINITION_LINK
		).buildPortletURL();
	}

	private PortletURL _getWorkflowInstancesPortletURL() {
		return PortletURLBuilder.createLiferayPortletURL(
			_kaleoDesignerRequestHelper.getLiferayPortletResponse(),
			KaleoDesignerPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE,
			PortletRequest.RENDER_PHASE
		).setMVCPath(
			"/view.jsp"
		).buildPortletURL();
	}

	private static final String _HTML =
		"<a class='alert-link' href='[$RENDER_URL$]'>[$MESSAGE$]</a>";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoDesignerDisplayContext.class);

	private final ActionExecutorManager _actionExecutorManager;
	private final KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;
	private KaleoDesignerRequestHelper _kaleoDesignerRequestHelper;
	private final PortletResourcePermission _portletResourcePermission;
	private final ResourceBundleLoader _resourceBundleLoader;
	private final ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;
	private final ThemeDisplay _themeDisplay;
	private final UserLocalService _userLocalService;

}