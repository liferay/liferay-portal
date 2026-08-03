/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.document.library.internal.display.context;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.url.SignDSURLProvider;
import com.liferay.document.library.display.context.BaseDLViewFileVersionDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Danny Situ
 */
public class SignatureDLViewFileVersionDisplayContext
	extends BaseDLViewFileVersionDisplayContext {

	public SignatureDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion,
		boolean hasUpdatePermission, String providerRequestId,
		String requestStatus, SignDSURLProvider signDSURLProvider) {

		super(
			UUID.fromString("9b5f3f1a-2d4c-4b7e-9c8a-1e2f3a4b5c6d"),
			parentDLDisplayContext, httpServletRequest, httpServletResponse,
			fileVersion);

		_httpServletRequest = httpServletRequest;
		_fileVersion = fileVersion;
		_hasUpdatePermission = hasUpdatePermission;
		_providerRequestId = providerRequestId;
		_requestStatus = requestStatus;
		_signDSURLProvider = signDSURLProvider;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		List<DropdownItem> dropdownItems = super.getActionDropdownItems();

		if (dropdownItems == null) {
			dropdownItems = new ArrayList<>();
		}

		List<DropdownItem> signatureDropdownItems = new ArrayList<>();

		if (Validator.isNotNull(_providerRequestId)) {
			signatureDropdownItems.add(_getSignDropdownItem());
		}

		if (_hasUpdatePermission) {
			signatureDropdownItems.add(_getViewSignatureStatusDropdownItem());

			if (Objects.equals(_requestStatus, "sent")) {
				signatureDropdownItems.add(_getResendDropdownItem());
				signatureDropdownItems.add(_getVoidDropdownItem());
			}
		}

		signatureDropdownItems.sort(
			Comparator.comparing(
				dropdownItem -> (String)dropdownItem.get("label")));

		dropdownItems.addAll(signatureDropdownItems);

		return dropdownItems;
	}

	private ResourceURL _createResourceURL(String resourceID) {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(portletResponse);

		ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

		resourceURL.setParameter(
			"fileEntryId", String.valueOf(_fileVersion.getFileEntryId()));
		resourceURL.setResourceID(resourceID);

		return resourceURL;
	}

	private DropdownItem _getResendDropdownItem() {
		return DropdownItemBuilder.putData(
			"action", "resendDSRequest"
		).putData(
			"resendDSRequestURL",
			_createResourceURL(
				"/document_library/resend_ds_request"
			).toString()
		).setIcon(
			"envelope-closed"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "resend")
		).build();
	}

	private DropdownItem _getSignDropdownItem() {
		return DropdownItemBuilder.setHref(
			() -> {
				String portletNamespace = PortalUtil.getPortletNamespace(
					DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);

				return HttpComponentsUtil.addParameter(
					_signDSURLProvider.getURL(
						_themeDisplay.getCompanyId(), _providerRequestId),
					portletNamespace + "backURL",
					_themeDisplay.getURLCurrent());
			}
		).setIcon(
			"pencil"
		).setKey(
			"sign"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "sign")
		).build();
	}

	private DropdownItem _getViewSignatureStatusDropdownItem() {
		return DropdownItemBuilder.putData(
			"action", "viewSignatureStatus"
		).putData(
			"fileEntryTitle", _fileVersion.getFileName()
		).putData(
			"signatureDetailsURL",
			_createResourceURL(
				"/document_library/get_signature_details"
			).toString()
		).setIcon(
			"list-ul"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "view-signature-status")
		).build();
	}

	private DropdownItem _getVoidDropdownItem() {
		return DropdownItemBuilder.putData(
			"action", "voidDSRequest"
		).putData(
			"fileEntryTitle", _fileVersion.getFileName()
		).putData(
			"voidDSRequestURL",
			_createResourceURL(
				"/document_library/void_ds_request"
			).toString()
		).setIcon(
			"times-circle"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "void")
		).build();
	}

	private final FileVersion _fileVersion;
	private final boolean _hasUpdatePermission;
	private final HttpServletRequest _httpServletRequest;
	private final String _providerRequestId;
	private final String _requestStatus;
	private final SignDSURLProvider _signDSURLProvider;
	private final ThemeDisplay _themeDisplay;

}