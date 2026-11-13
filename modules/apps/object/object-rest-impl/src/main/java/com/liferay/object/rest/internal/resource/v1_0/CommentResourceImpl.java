/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.headless.delivery.dto.v1_0.Comment;
import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.headless.delivery.dto.v1_0.util.CommentUtil;
import com.liferay.headless.delivery.resource.v1_0.util.CommentResourceUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.internal.odata.entity.v1_0.provider.CommentEntityModel;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.comment.DiscussionComment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Jhosseph Gonzalez
 */
public class CommentResourceImpl extends BaseCommentResourceImpl {

	public CommentResourceImpl(
		CommentManager commentManager,
		DiscussionPermission discussionPermission,
		DTOConverterRegistry dtoConverterRegistry,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry) {

		_commentManager = commentManager;
		_discussionPermission = discussionPermission;
		_dtoConverterRegistry = dtoConverterRegistry;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;

		_className = objectDefinition.getClassName();
		_objectDefinitionId = objectDefinition.getObjectDefinitionId();
	}

	@Override
	public void deleteByExternalReferenceCodeComment(
			String externalReferenceCode, String commentExternalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(externalReferenceCode, null);

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_fetchComment(
				_className, objectEntry.getId(), commentExternalReferenceCode,
				_getNonzeroGroupId(objectEntry.getId()));

		if (serviceBuilderComment == null) {
			throw new NotFoundException();
		}

		_deleteComment(serviceBuilderComment.getCommentId());
	}

	@Override
	public void deleteScopeScopeKeyByExternalReferenceCodeComment(
			String scopeKey, String externalReferenceCode,
			String commentExternalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, scopeKey);

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_fetchComment(
				_className, objectEntry.getId(), commentExternalReferenceCode,
				objectEntry.getScopeId());

		if (serviceBuilderComment == null) {
			throw new NotFoundException();
		}

		_deleteComment(serviceBuilderComment.getCommentId());
	}

	@Override
	public Comment getByExternalReferenceCodeComment(
			String externalReferenceCode, String commentExternalReferenceCode)
		throws Exception {

		return _getByExternalReferenceCodeComment(
			commentExternalReferenceCode, externalReferenceCode, null);
	}

	@Override
	public Page<Comment> getByExternalReferenceCodeCommentChildCommentsPage(
			String externalReferenceCode, String commentExternalReferenceCode,
			String search, Aggregation aggregation, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getByExternalReferenceCodeCommentChildCommentsPage(
			aggregation, commentExternalReferenceCode, externalReferenceCode,
			pagination, null, search, sorts);
	}

	@Override
	public Page<Comment> getByExternalReferenceCodeCommentsPage(
			String externalReferenceCode, String search,
			Aggregation aggregation, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getByExternalReferenceCodeCommentsPage(
			aggregation, externalReferenceCode, pagination, null, search,
			sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new CommentEntityModel();
	}

	@Override
	public Comment getScopeScopeKeyByExternalReferenceCodeComment(
			String scopeKey, String externalReferenceCode,
			String commentExternalReferenceCode)
		throws Exception {

		return _getByExternalReferenceCodeComment(
			commentExternalReferenceCode, externalReferenceCode, scopeKey);
	}

	@Override
	public Page<Comment>
			getScopeScopeKeyByExternalReferenceCodeCommentChildCommentsPage(
				String scopeKey, String externalReferenceCode,
				String commentExternalReferenceCode, String search,
				Aggregation aggregation, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getByExternalReferenceCodeCommentChildCommentsPage(
			aggregation, commentExternalReferenceCode, externalReferenceCode,
			pagination, scopeKey, search, sorts);
	}

	@Override
	public Page<Comment> getScopeScopeKeyByExternalReferenceCodeCommentsPage(
			String scopeKey, String externalReferenceCode, String search,
			Aggregation aggregation, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getByExternalReferenceCodeCommentsPage(
			aggregation, externalReferenceCode, pagination, scopeKey, search,
			sorts);
	}

	@Override
	public Comment postByExternalReferenceCodeComment(
			String externalReferenceCode, Comment comment)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(externalReferenceCode, null);

		return _addComment(
			comment.getExternalReferenceCode(),
			_getNonzeroGroupId(objectEntry.getId()), objectEntry.getId(), null,
			comment.getText());
	}

	@Override
	public Comment postByExternalReferenceCodeCommentChildComment(
			String externalReferenceCode, String commentExternalReferenceCode,
			Comment comment)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(externalReferenceCode, null);

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_fetchComment(
				_className, objectEntry.getId(), commentExternalReferenceCode,
				_getNonzeroGroupId(objectEntry.getId()));

		if (serviceBuilderComment == null) {
			throw new NotFoundException();
		}

		return _addComment(
			comment.getExternalReferenceCode(),
			serviceBuilderComment.getGroupId(), objectEntry.getId(),
			serviceBuilderComment.getCommentId(), comment.getText());
	}

	@Override
	public Comment postScopeScopeKeyByExternalReferenceCodeComment(
			String scopeKey, String externalReferenceCode, Comment comment)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, scopeKey);

		return _addComment(
			comment.getExternalReferenceCode(), objectEntry.getScopeId(),
			objectEntry.getId(), null, comment.getText());
	}

	@Override
	public Comment postScopeScopeKeyByExternalReferenceCodeCommentChildComment(
			String scopeKey, String externalReferenceCode,
			String commentExternalReferenceCode, Comment comment)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, scopeKey);

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_fetchComment(
				_className, objectEntry.getId(), commentExternalReferenceCode,
				objectEntry.getScopeId());

		if (serviceBuilderComment == null) {
			throw new NotFoundException();
		}

		return _addComment(
			comment.getExternalReferenceCode(),
			serviceBuilderComment.getGroupId(), objectEntry.getId(),
			serviceBuilderComment.getCommentId(), comment.getText());
	}

	@Override
	public Comment putByExternalReferenceCodeComment(
			String externalReferenceCode, String commentExternalReferenceCode,
			Comment comment)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(externalReferenceCode, null);

		long groupId = _getNonzeroGroupId(objectEntry.getId());

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_fetchComment(
				_className, objectEntry.getId(), commentExternalReferenceCode,
				groupId);

		if (serviceBuilderComment != null) {
			return _updateComment(comment, serviceBuilderComment);
		}

		return _addComment(
			commentExternalReferenceCode, groupId, objectEntry.getId(), null,
			comment.getText());
	}

	@Override
	public Comment putScopeScopeKeyByExternalReferenceCodeComment(
			String scopeKey, String externalReferenceCode,
			String commentExternalReferenceCode, Comment comment)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, scopeKey);

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_fetchComment(
				_className, objectEntry.getId(), commentExternalReferenceCode,
				objectEntry.getScopeId());

		if (serviceBuilderComment != null) {
			return _updateComment(comment, serviceBuilderComment);
		}

		return _addComment(
			commentExternalReferenceCode, objectEntry.getScopeId(),
			objectEntry.getId(), null, comment.getText());
	}

	private Map<String, String> _addAction(
		String actionName, Long id, String[] methodNames, Long ownerId,
		String permissionName, Object scopeKey, Long siteId) {

		if (Validator.isNull(scopeKey)) {
			return addAction(
				actionName, id, methodNames[0], ownerId, permissionName,
				siteId);
		}

		return addAction(
			actionName, id, methodNames[1], ownerId, permissionName, siteId);
	}

	private Comment _addComment(
			String externalReferenceCode, long groupId, long objectEntryId,
			Long parentCommentId, String text)
		throws Exception {

		_discussionPermission.checkAddPermission(
			PermissionThreadLocal.getPermissionChecker(),
			contextCompany.getCompanyId(), groupId, _className, objectEntryId);

		if (parentCommentId != null) {
			return CommentUtil.toComment(
				() -> _commentManager.fetchComment(
					_commentManager.addComment(
						externalReferenceCode, PrincipalThreadLocal.getUserId(),
						_className, objectEntryId, StringPool.BLANK,
						parentCommentId, StringPool.BLANK,
						StringBundler.concat("<p>", text, "</p>"),
						_createServiceContextFunction())),
				_commentManager, PortalUtil.getPortal());
		}

		return CommentUtil.toComment(
			() -> _commentManager.fetchComment(
				_commentManager.addComment(
					externalReferenceCode, PrincipalThreadLocal.getUserId(),
					groupId, _className, objectEntryId, StringPool.BLANK,
					StringPool.BLANK, StringBundler.concat("<p>", text, "</p>"),
					_createServiceContextFunction())),
			_commentManager, PortalUtil.getPortal());
	}

	private Function<String, ServiceContext> _createServiceContextFunction() {
		return className -> {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			return serviceContext;
		};
	}

	private void _deleteComment(Long commentId) throws Exception {
		_discussionPermission.checkDeletePermission(
			PermissionThreadLocal.getPermissionChecker(), commentId);

		_commentManager.deleteComment(commentId);
	}

	private com.liferay.portal.kernel.comment.Comment _fetchComment(
		String className, long classPK, String externalReferenceCode,
		long groupId) {

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_commentManager.fetchComment(groupId, externalReferenceCode);

		if ((serviceBuilderComment != null) &&
			CommentResourceUtil.isAssociated(
				className, classPK, serviceBuilderComment)) {

			return serviceBuilderComment;
		}

		return null;
	}

	private Comment _getByExternalReferenceCodeComment(
			String commentExternalReferenceCode, String externalReferenceCode,
			String scopeKey)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, scopeKey);

		long groupId = objectEntry.getScopeId();

		if (groupId == 0) {
			groupId = _getNonzeroGroupId(objectEntry.getId());
		}

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_getComment(commentExternalReferenceCode, groupId);

		return CommentUtil.toComment(
			serviceBuilderComment, _commentManager, PortalUtil.getPortal());
	}

	private Page<Comment> _getByExternalReferenceCodeCommentChildCommentsPage(
			Aggregation aggregation, String commentExternalReferenceCode,
			String externalReferenceCode, Pagination pagination,
			String scopeKey, String search, Sort[] sorts)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, scopeKey);

		Creator creator = objectEntry.getCreator();

		long groupId = objectEntry.getScopeId();

		if (groupId == 0) {
			groupId = _getNonzeroGroupId(objectEntry.getId());
		}

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_getComment(commentExternalReferenceCode, groupId);

		return CommentResourceUtil.getComments(
			HashMapBuilder.put(
				"add-discussion",
				_addAction(
					ActionKeys.ADD_DISCUSSION, objectEntry.getId(),
					new String[] {
						"postByExternalReferenceCodeCommentChildComment",
						"postScopeScopeKeyByExternalReferenceCodeComment" +
							"ChildComment"
					},
					creator.getId(), _className, scopeKey, groupId)
			).put(
				"get",
				_addAction(
					ActionKeys.VIEW, objectEntry.getId(),
					new String[] {
						"getByExternalReferenceCodeCommentChildCommentsPage",
						"getScopeScopeKeyByExternalReferenceCodeCommentChild" +
							"CommentsPage"
					},
					creator.getId(), _className, scopeKey, groupId)
			).build(),
			serviceBuilderComment.getCommentId(), contextCompany.getCompanyId(),
			_commentManager, search, aggregation, null, pagination,
			PortalUtil.getPortal(), sorts);
	}

	private Page<Comment> _getByExternalReferenceCodeCommentsPage(
			Aggregation aggregation, String externalReferenceCode,
			Pagination pagination, String scopeKey, String search, Sort[] sorts)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, scopeKey);

		Creator creator = objectEntry.getCreator();

		long groupId = objectEntry.getScopeId();

		if (groupId == 0) {
			groupId = _getNonzeroGroupId(objectEntry.getId());
		}

		Discussion discussion = _commentManager.getDiscussion(
			PrincipalThreadLocal.getUserId(), groupId, _className,
			objectEntry.getId(), _createServiceContextFunction());

		DiscussionComment rootDiscussionComment =
			discussion.getRootDiscussionComment();

		return CommentResourceUtil.getComments(
			HashMapBuilder.put(
				"add-discussion",
				_addAction(
					ActionKeys.ADD_DISCUSSION, objectEntry.getId(),
					new String[] {
						"postByExternalReferenceCodeComment",
						"postScopeScopeKeyByExternalReferenceCodeComment"
					},
					creator.getId(), _className, scopeKey, groupId)
			).put(
				"get",
				_addAction(
					ActionKeys.VIEW, objectEntry.getId(),
					new String[] {
						"getByExternalReferenceCodeCommentsPage",
						"getScopeScopeKeyByExternalReferenceCodeCommentsPage"
					},
					creator.getId(), _className, scopeKey, groupId)
			).build(),
			rootDiscussionComment.getCommentId(), contextCompany.getCompanyId(),
			_commentManager, search, aggregation, null, pagination,
			PortalUtil.getPortal(), sorts);
	}

	private com.liferay.portal.kernel.comment.Comment _getComment(
			String externalReferenceCode, long groupId)
		throws Exception {

		com.liferay.portal.kernel.comment.Comment comment =
			_commentManager.getComment(groupId, externalReferenceCode);

		_discussionPermission.checkViewPermission(
			PermissionThreadLocal.getPermissionChecker(),
			contextCompany.getCompanyId(), comment.getGroupId(),
			comment.getClassName(), comment.getClassPK());

		return comment;
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
		Long objectEntryId) {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null,
			_dtoConverterRegistry, contextHttpServletRequest, objectEntryId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private long _getNonzeroGroupId(long objectEntryId) throws Exception {
		com.liferay.object.model.ObjectEntry objectEntry =
			_objectEntryLocalService.getObjectEntry(objectEntryId);

		return objectEntry.getNonzeroGroupId();
	}

	private ObjectEntry _getObjectEntry(
			String objectEntryExternalReferenceCode, String scopeKey)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				_objectDefinitionId);

		if (!_isEnabled(objectDefinition)) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getCompanyId(),
					objectDefinition.getStorageType()));

		ObjectEntry objectEntry = defaultObjectEntryManager.getObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			objectEntryExternalReferenceCode, objectDefinition, scopeKey);

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryLocalService.getObjectEntry(objectEntry.getId());

		if (!serviceBuilderObjectEntry.isHead()) {
			throw new NotFoundException();
		}

		return objectEntry;
	}

	private boolean _isEnabled(ObjectDefinition objectDefinition) {
		if (!objectDefinition.isEnableComments()) {
			return false;
		}

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE)) {

			return true;
		}

		return FeatureFlagManagerUtil.isEnabled(
			objectDefinition.getCompanyId(), "LPD-43996");
	}

	private Comment _updateComment(
			Comment comment,
			com.liferay.portal.kernel.comment.Comment serviceBuilderComment)
		throws Exception {

		_discussionPermission.checkUpdatePermission(
			PermissionThreadLocal.getPermissionChecker(),
			serviceBuilderComment.getCommentId());

		return CommentUtil.toComment(
			() -> _commentManager.fetchComment(
				_commentManager.updateComment(
					PrincipalThreadLocal.getUserId(), _className,
					serviceBuilderComment.getClassPK(),
					serviceBuilderComment.getCommentId(), StringPool.BLANK,
					StringBundler.concat("<p>", comment.getText(), "</p>"),
					_createServiceContextFunction())),
			_commentManager, PortalUtil.getPortal());
	}

	private final String _className;
	private final CommentManager _commentManager;
	private final DiscussionPermission _discussionPermission;
	private final DTOConverterRegistry _dtoConverterRegistry;
	private final long _objectDefinitionId;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;

}