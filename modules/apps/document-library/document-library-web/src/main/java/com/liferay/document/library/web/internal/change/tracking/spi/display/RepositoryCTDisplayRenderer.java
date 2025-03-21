/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.repository.registry.RepositoryClassDefinition;
import com.liferay.portal.repository.registry.RepositoryClassDefinitionCatalogUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class RepositoryCTDisplayRenderer
	extends BaseCTDisplayRenderer<Repository> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Repository repository)
		throws Exception {

		Group group = _groupLocalService.getGroup(repository.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_repository"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"folderId", repository.getDlFolderId()
		).setParameter(
			"repositoryId", repository.getRepositoryId()
		).buildString();
	}

	@Override
	public Class<Repository> getModelClass() {
		return Repository.class;
	}

	@Override
	public String getTitle(Locale locale, Repository repository) {
		return repository.getName();
	}

	@Override
	public boolean isHideable(Repository repository) {
		return StringUtil.equals(
			repository.getName(), TempFileEntryUtil.class.getName());
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Repository> displayBuilder) {
		Repository repository = displayBuilder.getModel();

		displayBuilder.display(
			"name", repository.getName()
		).display(
			"description", repository.getDescription()
		).display(
			"repository-type",
			() -> {
				RepositoryClassDefinition repositoryClassDefinition =
					RepositoryClassDefinitionCatalogUtil.
						getRepositoryClassDefinition(
							repository.getCompanyId(),
							repository.getClassName());

				return repositoryClassDefinition.getRepositoryTypeLabel(
					displayBuilder.getLocale());
			}
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}