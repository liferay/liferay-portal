/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.internal.resource.v1_0;

import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.exception.DSEnvelopeException;
import com.liferay.digital.signature.exception.DSRequestException;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.rest.dto.v1_0.SignatureRequest;
import com.liferay.digital.signature.rest.dto.v1_0.SignatureRequestRecipient;
import com.liferay.digital.signature.rest.resource.v1_0.SignatureRequestResource;
import com.liferay.digital.signature.url.DSRequestURLProvider;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.definition.security.permission.resource.ObjectDefinitionPortletResourcePermissionRegistryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Danny Situ
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/signature-request.properties",
	scope = ServiceScope.PROTOTYPE, service = SignatureRequestResource.class
)
public class SignatureRequestResourceImpl
	extends BaseSignatureRequestResourceImpl {

	@Override
	public SignatureRequest getSignatureRequest(Long signatureRequestId)
		throws Exception {

		return _toSignatureRequest(_getDSRequest(signatureRequestId));
	}

	@Override
	public Page<SignatureRequest> getSiteSignatureRequestsPage(
			Long siteId, String search, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_dsRequestManager.getGroupDSRequests(
					contextCompany.getCompanyId(), siteId, search,
					pagination.getStartPosition(), pagination.getEndPosition()),
				this::_toSignatureRequest),
			pagination,
			_dsRequestManager.getGroupDSRequestsCount(
				contextCompany.getCompanyId(), siteId, search));
	}

	@Override
	public SignatureRequest postSignatureRequestNotification(
			Long signatureRequestId)
		throws Exception {

		DSRequest dsRequest = _getDSRequest(signatureRequestId);

		_checkPermission(ActionKeys.UPDATE, dsRequest);

		if (dsRequest.isTerminal()) {
			throw new BadRequestException(
				StringBundler.concat(
					"Unable to notify recipients of signature request ",
					signatureRequestId, " with status \"",
					dsRequest.getStatus(), "\""));
		}

		_dsRequestManager.sendDSRequestNotifications(
			dsRequest.getDSRequestId());

		return _toSignatureRequest(dsRequest);
	}

	@Override
	public SignatureRequest postSignatureRequestVoid(
			Long signatureRequestId, String voidedReason)
		throws Exception {

		DSRequest dsRequest = _getDSRequest(signatureRequestId);

		_checkPermission(ActionKeys.UPDATE, dsRequest);

		if (dsRequest.isTerminal()) {
			throw new BadRequestException(
				StringBundler.concat(
					"Unable to void signature request ", signatureRequestId,
					" with status \"", dsRequest.getStatus(), "\""));
		}

		if (Validator.isNull(voidedReason)) {
			throw new BadRequestException(
				"A reason is required to void a signature request");
		}

		try {
			_dsRequestManager.voidDSRequest(
				dsRequest.getDSRequestId(), voidedReason);
		}
		catch (DSEnvelopeException.MustBeVoidable dsEnvelopeException) {
			throw new BadRequestException(
				dsEnvelopeException.getMessage(), dsEnvelopeException);
		}
		catch (DSRequestException dsRequestException) {
			throw new BadRequestException(
				dsRequestException.getMessage(), dsRequestException);
		}

		return _toSignatureRequest(
			_dsRequestManager.fetchDSRequest(
				contextCompany.getCompanyId(), dsRequest.getDSRequestId()));
	}

	@Override
	public SignatureRequest postSiteSignatureRequest(
			Long siteId, SignatureRequest signatureRequest)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					contextCompany.getCompanyId());

		PortletResourcePermission portletResourcePermission =
			ObjectDefinitionPortletResourcePermissionRegistryUtil.getService(
				objectDefinition.getResourceName());

		portletResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), 0,
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		Long[] fileEntryIds = signatureRequest.getFileEntryIds();

		if (ArrayUtil.isEmpty(fileEntryIds)) {
			throw new BadRequestException(
				"A signature request must have at least one file entry");
		}

		if (ArrayUtil.contains(fileEntryIds, null)) {
			throw new BadRequestException("A file entry ID must not be null");
		}

		GroupPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), siteId,
			ActionKeys.VIEW);

		for (Long fileEntryId : fileEntryIds) {
			_fileEntryModelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(),
				_dlAppService.getFileEntry(fileEntryId), ActionKeys.UPDATE);
		}

		DSRequest dsRequest = null;

		try {
			dsRequest = _dsRequestManager.addDSRequest(
				contextCompany.getCompanyId(), _toDSEnvelope(signatureRequest),
				ArrayUtil.toArray(fileEntryIds), siteId,
				contextUser.getUserId());
		}
		catch (DSRequestException dsRequestException) {
			throw new BadRequestException(
				dsRequestException.getMessage(), dsRequestException);
		}

		if (GetterUtil.getBoolean(
				signatureRequest.getSendNotifications(), true)) {

			_dsRequestManager.sendDSRequestNotifications(
				dsRequest.getDSRequestId());
		}

		return _toSignatureRequest(dsRequest);
	}

	private void _checkPermission(String actionId, DSRequest dsRequest)
		throws Exception {

		if (!_dsRequestManager.hasPermission(
				PermissionThreadLocal.getPermissionChecker(), dsRequest,
				actionId)) {

			throw new PrincipalException.MustHavePermission(
				contextUser.getUserId(), DSRequest.class.getName(),
				dsRequest.getDSRequestId(), actionId);
		}
	}

	private Map<String, Map<String, String>> _getActions(DSRequest dsRequest)
		throws Exception {

		if (!dsRequest.isSignatureRequired(contextUser.getUserId())) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Map<String, String>>put(
			"sign",
			HashMapBuilder.put(
				"href",
				_dsRequestURLProvider.getSignURL(
					contextCompany.getCompanyId(), dsRequest.getDSRequestId(),
					dsRequest.getSiteGroupId())
			).put(
				"method", "GET"
			).build()
		).build();
	}

	private DSRequest _getDSRequest(Long signatureRequestId) throws Exception {
		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			contextCompany.getCompanyId(), signatureRequestId);

		if ((dsRequest == null) ||
			!_dsRequestManager.hasPermission(
				PermissionThreadLocal.getPermissionChecker(), dsRequest,
				ActionKeys.VIEW)) {

			throw new NotFoundException(
				"Unable to find signature request " + signatureRequestId);
		}

		return dsRequest;
	}

	private DSEnvelope _toDSEnvelope(SignatureRequest signatureRequest)
		throws Exception {

		return new DSEnvelope() {
			{
				setDSRecipients(
					_toDSRecipients(
						signatureRequest.getSignatureRequestRecipients()));
				setEmailBlurb(signatureRequest.getEmailMessage());
				setEmailSubject(signatureRequest.getEmailSubject());
				setExpireAfter(
					GetterUtil.getInteger(signatureRequest.getExpireAfter()));
				setName(signatureRequest.getName());
			}
		};
	}

	private List<DSRecipient> _toDSRecipients(
			SignatureRequestRecipient[] signatureRequestRecipients)
		throws Exception {

		if (ArrayUtil.isEmpty(signatureRequestRecipients)) {
			throw new BadRequestException(
				"A signature request must have at least one recipient");
		}

		IntegerWrapper integerWrapper = new IntegerWrapper();

		return unsafeTransformToList(
			signatureRequestRecipients,
			signatureRequestRecipient -> {
				Long userId = signatureRequestRecipient.getUserId();

				if (userId == null) {
					throw new BadRequestException(
						"A recipient must have a user ID");
				}

				User user = _userService.getUserById(userId);

				return new DSRecipient() {
					{
						setDSRecipientId(
							String.valueOf(integerWrapper.increment()));
						setEmailAddress(user.getEmailAddress());
						setName(user.getFullName());
					}
				};
			});
	}

	private SignatureRequest _toSignatureRequest(DSRequest dsRequest)
		throws Exception {

		if (dsRequest == null) {
			return null;
		}

		return _signatureRequestDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(dsRequest), _dtoConverterRegistry,
				dsRequest.getDSRequestId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			dsRequest);
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private DSRequestURLProvider _dsRequestURLProvider;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(
		target = "(component.name=com.liferay.digital.signature.rest.internal.dto.v1_0.converter.SignatureRequestDTOConverter)"
	)
	private DTOConverter<DSRequest, SignatureRequest>
		_signatureRequestDTOConverter;

	@Reference
	private UserService _userService;

}