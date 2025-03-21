/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.servlet;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.taglib.internal.util.SegmentsExperienceUtil;
import com.liferay.layout.util.structure.CommonStylesUtil;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.StyledLayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/layout-common-styles",
		"osgi.http.whiteboard.servlet.name=com.liferay.layout.taglib.internal.servlet.LayoutStructureCommonStylesCSSServlet",
		"osgi.http.whiteboard.servlet.pattern=/layout-common-styles/*"
	},
	service = Servlet.class
)
public class LayoutStructureCommonStylesCSSServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			User user = _portal.getUser(httpServletRequest);

			if (user == null) {
				HttpSession httpSession = httpServletRequest.getSession();

				if (PortalSessionThreadLocal.getHttpSession() == null) {
					PortalSessionThreadLocal.setHttpSession(httpSession);
				}

				String userIdString = (String)httpSession.getAttribute(
					"j_username");
				String password = (String)httpSession.getAttribute(
					"j_password");

				if ((userIdString != null) && (password != null)) {
					long userId = GetterUtil.getLong(userIdString);

					user = _userLocalService.getUser(userId);
				}
			}

			if (user != null) {
				PrincipalThreadLocal.setName(user.getUserId());
				PrincipalThreadLocal.setPassword(
					_portal.getUserPassword(httpServletRequest));

				PermissionThreadLocal.setPermissionChecker(
					_permissionCheckerFactory.create(user));
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		httpServletResponse.setContentType(ContentTypes.TEXT_CSS_UTF8);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		Layout layout = null;

		long plid = ParamUtil.getLong(httpServletRequest, "plid");

		if (plid > 0) {
			layout = _layoutLocalService.fetchLayout(plid);
		}

		if ((layout == null) ||
			(!layout.isTypeAssetDisplay() && !layout.isTypeContent() &&
			 !layout.isTypeUtility() &&
			 ((layout.getMasterLayoutPlid() == 0) ||
			  !layout.isTypePortlet()))) {

			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(),
				SegmentsExperienceUtil.getSegmentsExperienceId(
					httpServletRequest));

		if (layoutStructure == null) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(
			".lfr-layout-structure-item-container {padding: 0;} ");
		printWriter.write(
			".lfr-layout-structure-item-row {overflow: hidden;} ");
		printWriter.write(".portlet-borderless .portlet-content {padding: 0;}");

		JSONObject frontendTokensJSONObject = _getFrontendTokensJSONObject(
			layout.getGroupId(), layout,
			ParamUtil.getBoolean(httpServletRequest, "styleBookEntryPreview"));

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		for (ViewportSize viewportSize : _sortedViewportSizes) {
			StringBundler cssSB = new StringBundler();

			for (LayoutStructureItem layoutStructureItem :
					layoutStructureItems) {

				if (!(layoutStructureItem instanceof
						StyledLayoutStructureItem)) {

					continue;
				}

				StyledLayoutStructureItem styledLayoutStructureItem =
					(StyledLayoutStructureItem)layoutStructureItem;

				cssSB.append(
					_getLayoutStructureItemCSS(
						frontendTokensJSONObject, styledLayoutStructureItem,
						viewportSize));

				String customCSS = _getCustomCSS(
					styledLayoutStructureItem, viewportSize);

				if (Validator.isNotNull(customCSS)) {
					cssSB.append(
						StringUtil.replace(
							customCSS, _FRAGMENT_CLASS_PLACEHOLDER,
							styledLayoutStructureItem.getUniqueCssClass()));
				}
			}

			if (cssSB.length() == 0) {
				continue;
			}

			if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
				printWriter.print(cssSB);
			}
			else {
				printWriter.print("@media screen and (max-width: ");
				printWriter.print(viewportSize.getMaxWidth());
				printWriter.print("px) {");
				printWriter.print(cssSB);
				printWriter.print(StringPool.CLOSE_CURLY_BRACE);
			}
		}
	}

	private JSONObject _createJSONObject(String json) {
		try {
			return _jsonFactory.createJSONObject(json);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return _jsonFactory.createJSONObject();
		}
	}

	private String _getCustomCSS(
		StyledLayoutStructureItem styledLayoutStructureItem,
		ViewportSize viewportSize) {

		if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
			return styledLayoutStructureItem.getCustomCSS();
		}

		Map<String, String> customCSSViewports =
			styledLayoutStructureItem.getCustomCSSViewports();

		return customCSSViewports.get(viewportSize.getViewportSizeId());
	}

	private JSONObject _getFrontendTokensJSONObject(
		long groupId, Layout layout, boolean styleBookEntryPreview) {

		JSONObject frontendTokensJSONObject = _jsonFactory.createJSONObject();

		StyleBookEntry styleBookEntry = null;

		if (!styleBookEntryPreview) {
			styleBookEntry = DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(
				layout);
		}

		JSONObject frontendTokenValuesJSONObject =
			_jsonFactory.createJSONObject();

		if (styleBookEntry != null) {
			frontendTokenValuesJSONObject = _createJSONObject(
				styleBookEntry.getFrontendTokensValues());
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return _jsonFactory.createJSONObject();
		}

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			ServletContextUtil.getFrontendTokenDefinitionRegistry();

		FrontendTokenDefinition frontendTokenDefinition = null;

		if (FeatureFlagManagerUtil.isEnabled(
				layout.getCompanyId(), "LPD-30204")) {

			frontendTokenDefinition =
				frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					layout);
		}
		else {
			frontendTokenDefinition =
				frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					_layoutSetLocalService.fetchLayoutSet(
						group.getGroupId(), group.isLayoutSetPrototype()));
		}

		if (frontendTokenDefinition == null) {
			return _jsonFactory.createJSONObject();
		}

		Collection<FrontendToken> frontendTokens =
			frontendTokenDefinition.getFrontendTokens();

		for (FrontendToken frontendToken : frontendTokens) {
			List<FrontendTokenMapping> frontendTokenMappings = new ArrayList<>(
				frontendToken.getFrontendTokenMappings(
					FrontendTokenMapping.TYPE_CSS_VARIABLE));

			if (ListUtil.isEmpty(frontendTokenMappings)) {
				continue;
			}

			String value = frontendToken.getDefaultValue();

			JSONObject valueJSONObject =
				frontendTokenValuesJSONObject.getJSONObject(
					frontendToken.getName());

			if (valueJSONObject != null) {
				value = valueJSONObject.getString("value");
			}

			frontendTokensJSONObject.put(
				frontendToken.getName(),
				JSONUtil.put(
					FrontendTokenMapping.TYPE_CSS_VARIABLE,
					() -> {
						FrontendTokenMapping frontendTokenMapping =
							frontendTokenMappings.get(0);

						return frontendTokenMapping.getValue();
					}
				).put(
					"value", value
				));
		}

		return frontendTokensJSONObject;
	}

	private String _getLayoutStructureItemCSS(
		JSONObject frontendTokensJSONObject,
		StyledLayoutStructureItem styledLayoutStructureItem,
		ViewportSize viewportSize) {

		JSONObject stylesJSONObject = _getStylesJSONObject(
			styledLayoutStructureItem.getItemConfigJSONObject(), viewportSize);

		if (stylesJSONObject.length() == 0) {
			return StringPool.BLANK;
		}

		List<String> availableStyles = ListUtil.filter(
			CommonStylesUtil.getAvailableStyleNames(),
			styleName -> _includeStyles(
				styledLayoutStructureItem, styleName,
				stylesJSONObject.getString(styleName), viewportSize));

		if (ListUtil.isEmpty(availableStyles)) {
			return StringPool.BLANK;
		}

		StringBundler cssSB = new StringBundler(
			(availableStyles.size() * 2) + 4);

		cssSB.append(".lfr-layout-structure-item-");
		cssSB.append(styledLayoutStructureItem.getItemId());
		cssSB.append(" {\n");

		for (String styleName : availableStyles) {
			String value = stylesJSONObject.getString(styleName);

			cssSB.append(
				StringUtil.replace(
					CommonStylesUtil.getCSSTemplate(styleName), "{value}",
					_getStyleValue(
						frontendTokensJSONObject, styledLayoutStructureItem,
						styleName, value)));

			cssSB.append(StringPool.NEW_LINE);
		}

		cssSB.append("}\n");

		return cssSB.toString();
	}

	private String _getStyleFromStyleBookEntry(
		JSONObject frontendTokensJSONObject, String styleValue) {

		JSONObject styleValueJSONObject =
			frontendTokensJSONObject.getJSONObject(styleValue);

		if (styleValueJSONObject == null) {
			return styleValue;
		}

		String cssVariable = styleValueJSONObject.getString(
			FrontendTokenMapping.TYPE_CSS_VARIABLE);

		return "var(--" + cssVariable + ")";
	}

	private JSONObject _getStylesJSONObject(
		JSONObject itemConfigJSONObject, ViewportSize viewportSize) {

		if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
			return itemConfigJSONObject.getJSONObject("styles");
		}

		JSONObject viewportJSONObject = itemConfigJSONObject.getJSONObject(
			viewportSize.getViewportSizeId());

		if (viewportJSONObject != null) {
			JSONObject jsonObject = viewportJSONObject.getJSONObject("styles");

			if (jsonObject != null) {
				return jsonObject;
			}
		}

		return _jsonFactory.createJSONObject();
	}

	private String _getStyleValue(
		JSONObject frontendTokensJSONObject,
		StyledLayoutStructureItem styledLayoutStructureItem, String styleName,
		String value) {

		if (styleName.startsWith("margin") || styleName.startsWith("padding")) {
			StringBundler sb = new StringBundler(5);

			String spacingValue = _spacings.get(value);

			if (Validator.isNotNull(spacingValue)) {
				sb.append("var(--spacer-");
				sb.append(value);
				sb.append(StringPool.COMMA);
				sb.append(spacingValue);
				sb.append("rem)");
			}
			else {
				sb.append(value);
			}

			return sb.toString();
		}

		if (Objects.equals(styleName, "backgroundImage")) {
			return "var(--lfr-background-image-" +
				styledLayoutStructureItem.getItemId() +
					StringPool.CLOSE_PARENTHESIS;
		}

		if (Objects.equals(styleName, "opacity")) {
			return String.valueOf(GetterUtil.getInteger(value, 100) / 100.0);
		}

		return _getStyleFromStyleBookEntry(frontendTokensJSONObject, value);
	}

	private boolean _includeStyles(
		StyledLayoutStructureItem styledLayoutStructureItem, String styleName,
		String value, ViewportSize viewportSize) {

		if (Validator.isNull(value) ||
			(Objects.equals(
				value, CommonStylesUtil.getDefaultStyleValue(styleName)) &&
			 Objects.equals(viewportSize, ViewportSize.DESKTOP))) {

			return false;
		}

		if (!(styledLayoutStructureItem instanceof
				ContainerStyledLayoutStructureItem)) {

			return true;
		}

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)styledLayoutStructureItem;

		if (!Objects.equals(
				containerStyledLayoutStructureItem.getWidthType(), "fixed")) {

			return true;
		}

		if (Objects.equals(styleName, "marginLeft") ||
			Objects.equals(styleName, "marginRight")) {

			return false;
		}

		return true;
	}

	private static final String _FRAGMENT_CLASS_PLACEHOLDER =
		"[$FRAGMENT_CLASS$]";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStructureCommonStylesCSSServlet.class);

	private static final ViewportSize[] _sortedViewportSizes =
		ViewportSize.values();
	private static final Map<String, String> _spacings = HashMapBuilder.put(
		"0", "0"
	).put(
		"1", "0.25"
	).put(
		"2", "0.5"
	).put(
		"3", "1"
	).put(
		"4", "1.5"
	).put(
		"5", "3"
	).put(
		"6", "4.5"
	).put(
		"7", "6"
	).put(
		"8", "7.5"
	).put(
		"9", "9"
	).put(
		"10", "10"
	).build();

	static {
		Arrays.sort(
			_sortedViewportSizes,
			Comparator.comparingInt(ViewportSize::getOrder));
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutStructureProvider _layoutStructureProvider;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}