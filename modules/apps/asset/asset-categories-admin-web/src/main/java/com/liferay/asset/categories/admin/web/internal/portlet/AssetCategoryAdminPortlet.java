/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.portlet;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.categories.admin.web.internal.configuration.AssetCategoriesAdminWebConfiguration;
import com.liferay.asset.categories.admin.web.internal.constants.AssetCategoriesAdminWebKeys;
import com.liferay.asset.category.property.exception.CategoryPropertyKeyException;
import com.liferay.asset.category.property.exception.CategoryPropertyValueException;
import com.liferay.asset.category.property.exception.DuplicateCategoryPropertyException;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.NoSuchClassTypeException;
import com.liferay.asset.kernel.exception.AssetCategoryLimitException;
import com.liferay.asset.kernel.exception.AssetCategoryNameException;
import com.liferay.asset.kernel.exception.DuplicateCategoryException;
import com.liferay.asset.kernel.exception.DuplicateVocabularyException;
import com.liferay.asset.kernel.exception.InvalidAssetCategoryException;
import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.exception.NoSuchEntryException;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.exception.VocabularyNameException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.asset.categories.admin.web.internal.configuration.AssetCategoriesAdminWebConfiguration",
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-asset-category-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/asset_category_admin.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Asset Category Admin",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AssetCategoryAdminPortlet extends MVCPortlet {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_assetCategoriesAdminWebConfiguration =
			ConfigurableUtil.createConfigurable(
				AssetCategoriesAdminWebConfiguration.class, properties);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, NoSuchCategoryException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchVocabularyException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);

			return;
		}

		renderRequest.setAttribute(
			AssetCategoriesAdminWebKeys.ASSET_CATEGORIES_ADMIN_CONFIGURATION,
			_assetCategoriesAdminWebConfiguration);
		renderRequest.setAttribute(
			AssetCategoriesAdminWebKeys.
				ASSET_DISPLAY_PAGE_FRIENDLY_URL_PROVIDER,
			_assetDisplayPageFriendlyURLProvider);
		renderRequest.setAttribute(ItemSelector.class.getName(), _itemSelector);

		CTTimelineUtil.setClassName(renderRequest, AssetCategory.class);

		super.doDispatch(renderRequest, renderResponse);
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof AssetCategoryLimitException ||
			throwable instanceof AssetCategoryNameException ||
			throwable instanceof CategoryPropertyKeyException ||
			throwable instanceof CategoryPropertyValueException ||
			throwable instanceof DuplicateCategoryException ||
			throwable instanceof DuplicateCategoryPropertyException ||
			throwable instanceof DuplicateVocabularyException ||
			throwable instanceof InvalidAssetCategoryException ||
			throwable instanceof NoSuchCategoryException ||
			throwable instanceof NoSuchClassTypeException ||
			throwable instanceof NoSuchEntryException ||
			throwable instanceof NoSuchVocabularyException ||
			throwable instanceof PrincipalException ||
			throwable instanceof VocabularyNameException) {

			return true;
		}

		return false;
	}

	private volatile AssetCategoriesAdminWebConfiguration
		_assetCategoriesAdminWebConfiguration;

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private ItemSelector _itemSelector;

}